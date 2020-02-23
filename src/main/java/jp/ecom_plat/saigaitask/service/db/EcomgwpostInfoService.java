/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.ecomgwpostInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DESC;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.eq;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.OrderByItem;
import org.seasar.extension.jdbc.OrderByItem.OrderingSpec;
import org.seasar.extension.jdbc.name.PropertyName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.EcomgwpostInfo;
import jp.ecom_plat.saigaitask.entity.db.EcomgwpostdefaultInfo;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;

@org.springframework.stereotype.Repository
public class EcomgwpostInfoService extends AbstractService<EcomgwpostInfo> {

	public final int RESPONSE_OK = 200;

	/**
	 * IDで検索
	 * @param id ID
	 * @return IDに対応唯一のレコード
	 */
	public EcomgwpostInfo findById(Long id) {
		return select().where(eq(Names.ecomgwpostInfo().valid(), true)).id(id).getSingleResult();
	}

	/**
	 * 自治体IDで検索
	 * @param govid 自治体ID
	 * @return 検索結果のリスト
	 */
	public List<EcomgwpostInfo> findByLocalgovInfoId(Long govid) {
		return select().where(
				and(
						eq(Names.ecomgwpostInfo().localgovinfoid(), govid)
						, eq(Names.ecomgwpostInfo().valid(), true)
						)
				).orderBy(asc(Names.ecomgwpostInfo().disporder())).getResultList();
	}

	/**
	 * 送信
	 * @param ecomgwpostinfoid	送信先のID
	 * @param ecomgwpostUrgent	緊急度
	 * @param ecomgwpostTitle	タイトル
	 * @param ecomgwpostContent	本文（html）
	 * @return 送信結果。成功の場合true。
	 */
	public boolean send(
			List<String> ecomgwpostinfoid	// 送信先のID
			, String ecomgwpostUrgent	// 緊急度
			, String ecomgwpostTitle		// タイトル
			, String ecomgwpostContent	// 本文（html）
			) {

		// 送信対象でループ
		for (String id : ecomgwpostinfoid) {

			// 送信対象情報検索
			EcomgwpostInfo postInfo = findById(new Long(id));
			if (postInfo == null) throw new IllegalArgumentException(lang.__("Invalid ecomgwpostinfoid. [{0}]", id));

			// XML文字列作成（ファイル作成では無くオンメモリ）
			String data = null;
			try {
				data = buildXmlData(postInfo, ecomgwpostUrgent, ecomgwpostTitle, ecomgwpostContent);
			} catch (Exception e) {
				logger.error("Failed to XML Build", e);
				return false;
			}

			// リトライ処理
			int retryCount = 0;
			int resultCode = -1;
			while (retryCount<getRetryCount()) {
				try {
					// 送信
					resultCode = postData(postInfo.posturl, data);
					break;
				} catch (Exception e) {
					// この例外キャッチはhttpステータスを検証しているものでは無く、純粋な通信エラー時などにのみ発生する。
					// サーバ側の500エラーなどについてはキャッチされずに続行する。
					logger.error("Failed to send EcomGroupware Request", e);
					retryCount++;
				}
			}

			// ここでhttpステータスを検証。リトライ失敗している場合（純粋な通信エラーの場合）はresultCodeは-1でログに出力される。
			if (RESPONSE_OK != resultCode) {
				// 失敗
				logger.error("Failed to send EcomGroupware Request HTTP-Response-Status[" +resultCode + "]");
				// 継続せずに終了する。
				return false;
			}
		}	// end for (String id : ecomgwpostinfoid)

		// 全件送信成功
		return true;
	}

	/**
	 * XML文字列作成
	 * @param ecomgwpostinfoid
	 * @param ecomgwpostUrgent
	 * @param ecomgwpostTitle
	 * @param ecomgwpostContent
	 * @return
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 */
	private String buildXmlData(
			EcomgwpostInfo postInfo	// 送信先情報
			, String ecomgwpostUrgent	// 緊急度
			, String ecomgwpostTitle		// タイトル
			, String ecomgwpostContent	// 本文（html）
			) throws TransformerException, ParserConfigurationException {

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = builder.newDocument();
        document.setXmlVersion("1.0");

        Element data = document.createElement("data");

        // blockidを作成（カンマ区切りで複数指定されている可能性がある）
        Element blockids = document.createElement("blockids");
        for (String b : StringUtils.split(postInfo.blockid, ",")) {
            blockids.appendChild(createTextElement(document, "blockid", StringUtils.trim(b)));
        }
        data.appendChild(blockids);

        data.appendChild(createTextElement(document, "groupid", postInfo.groupid));
        data.appendChild(createTextElement(document, "partsid", postInfo.partsid));
        data.appendChild(createTextElement(document, "date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())));
        data.appendChild(createTextElement(document, "title", ecomgwpostTitle));
        data.appendChild(createTextElement(document, "body", ecomgwpostContent));
        data.appendChild(createTextElement(document, "urgent", ecomgwpostUrgent));

        document.appendChild(data);

//        Element datas = document.createElement("datas");
//        datas.appendChild(data);
//
//        document.appendChild(datas);


         // DOMオブジェクトを文字列として出力
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer transformer = tfactory.newTransformer();

        // encoding="UTF-8"を指定
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        // Stringへ書きだす
        StringWriter out = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(out));
        return out.toString();
	}

	private Element createTextElement(Document document, String tagName, String text) {
		Element elm = document.createElement(tagName);
        elm.appendChild(document.createTextNode(text));
        return elm;

	}

	private int postData(String targetUrl, String content) throws IOException {

		HttpURLConnection con = null;
		DataOutputStream out = null;

		// boundary を作成。本文中に利用されている文字列は避ける。
		String boundary = null;
		do {
			boundary = RandomStringUtils.randomAlphanumeric(10);
		} while (content.indexOf(boundary) >= 0);

		try {

			URL url = new URL(targetUrl);
			con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
			con.setDoOutput(true);
            con.setRequestProperty("User-Agent", "SaigaiTask2 java post");
            con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			con.setRequestProperty("Accept-Language", "jp");
			con.connect();

			// body部作成(小さいデータなので小刻み送信する必要は無いため、デバッグしやすいように一度StringBuilderに入れてしまう)
			StringBuilder sb = new StringBuilder();
			sb.append("--" + boundary + "\r\n");
			sb.append("Content-Disposition: form-data; name=\"datas\"; filename=\"datas.xml\"\r\n");
			sb.append("Content-type: text/xml; charset=\"UTF-8\"\r\n");
			sb.append("\r\n");
			sb.append(content);
			sb.append("\r\n");
			sb.append("--" + boundary + "--");

			// 送信
			out = new DataOutputStream(con.getOutputStream());
			out.write(sb.toString().getBytes());
			out.flush();
			out.close();

			int reponseCode =  con.getResponseCode();
			return reponseCode;
		} finally {
			try { out.close(); } catch (Exception e) {}
			try { con.disconnect(); }  catch (Exception e) {}
		}
	}



	/**
	 * Eコミグループウェアのリトライ回数を取得
	 * @return リトライ回数
	 */
	private int getRetryCount() {
		final String DEFAULT_VALUE = "3";

		InputStream is = getClass().getResourceAsStream("/SaigaiTask.properties");
		Properties prop = new Properties();
		try {
			prop.load(is);

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
					logger.error("", e);
				}
			}
		}

		// デフォルト 3回
		try {
			return Integer.parseInt(prop.getProperty("ECOMGW_RETRY_COUNT", DEFAULT_VALUE));

		} catch(NumberFormatException e) {
			e.printStackTrace();
			logger.error(lang.__("SaigaiTask.properties : ECOMGW_RETRY_COUNT value is invalid."));
			return Integer.parseInt(DEFAULT_VALUE);
		}
	}

	/**
	 * 検索条件に従い検索し、検索結果件数を取得する。
	 * @param conditions 検索条件マップ
	 * @return 検索結果件数
	 */
	public int getCount(Map<String, Object> conditions) {
		return (int)select()
			.innerJoin(ecomgwpostInfo().localgovInfo())
			.where(conditions)
			.getCount();
	}

	/**
	 * 検索条件に従い検索し、結果一覧を取得する。ソート、ページング対応版。
	 * @param conditions 検索条件マップ
	 * @param sortName ソート項目名
	 * @param sortOrder ソート順（昇順 or 降順）
	 * @param limit 取得件数
	 * @param offset 取得開始位置
	 * @return 検索結果
	 */
	public List<EcomgwpostInfo> findByCondition(Map<String, Object> conditions, String sortName, String sortOrder, Integer limit, Integer offset) {
		//ページャー条件
		if(limit == null) {limit = NON_LIMIT;}
		if(offset == null) {offset = NON_SELECT_OFFSET;}

		//ソート条件
		//第１ソートキー：画面での指定。指定のない場合、表示順カラムがあれば表示順、なければid。
		//第２ソートキー：第１ソートキーがidならばなし、idでなければid。
		if(StringUtils.isEmpty(sortName)) {
			sortName = ecomgwpostInfo().disporder().toString();
		}
		if(StringUtils.isEmpty(sortOrder)) {
			sortOrder = ASC;
		}
		List<OrderByItem> orderByItemList = new ArrayList<OrderByItem>();
		orderByItemList.add(new OrderByItem(sortName, DESC.equals(sortOrder.toUpperCase())?OrderingSpec.DESC:OrderingSpec.ASC));
		if(!ecomgwpostInfo().id().toString().equals(sortName)){
			orderByItemList.add(new OrderByItem(ecomgwpostInfo().id(), OrderingSpec.ASC));
		}
		OrderByItem[] orderByItems = orderByItemList.toArray(new OrderByItem[0]);

		return select()
			.innerJoin(ecomgwpostInfo().localgovInfo())
			.where(conditions)
			.orderBy(orderByItems)
			.limit(limit)
			.offset(offset)
			.getResultList();
	}

	/**
	 * 表示順最大値を取得
	 * @param conditions 検索条件マップ
	 * @return 表示順の最大値
	 */
	public int getLargestDisporder(Map<String, Object> conditions) {
		conditions.put(ecomgwpostInfo().disporder().toString()+"_IS_NOT_NULL", Boolean.TRUE);
		List<EcomgwpostInfo> list = findByCondition(conditions, ecomgwpostInfo().disporder().toString(), DESC, NON_LIMIT, NON_SELECT_OFFSET);

		if (list.size() == 0){
			return 0;
		}else{
			return list.get(0).disporder;
		}
	}

	/**
	 * 更新対象外の項目を指定して更新する。
	 * @param entity 更新対象データ
	 * @param excludes 更新対象外プロパティ配列
	 * @return
	*/
	public int update(EcomgwpostInfo entity, PropertyName<?>[] excludes) {
		if(excludes != null){
			return jdbcManager
				.update(entity)
				.excludes(excludes)
				.execute();
		}else{
			return jdbcManager
				.update(entity)
				.execute();
		}
	}

	@Override
	public DeleteCascadeResult deleteCascade(EcomgwpostInfo entity, DeleteCascadeResult result) {

		result.cascade(EcomgwpostdefaultInfo.class, Names.ecomgwpostdefaultInfo().ecomgwpostinfoid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
