/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Status;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.AlertrequestInfoDto;
import jp.ecom_plat.saigaitask.dto.AlerttriggerInfoDto;
import jp.ecom_plat.saigaitask.entity.db.MeteoData;
import jp.ecom_plat.saigaitask.entity.db.MeteorequestInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteoxsltInfo;
import jp.ecom_plat.saigaitask.service.db.MeteoDataService;
import jp.ecom_plat.saigaitask.service.db.MeteorequestInfoService;
import jp.ecom_plat.saigaitask.service.db.MeteotriggerInfoService;
import jp.ecom_plat.saigaitask.util.FileUtil;

/**
 * 気象情報を取得してアラームを出すサービス
 */
@org.springframework.stereotype.Service
public class MeteoricAlarmService extends AbstractAlarmService {

	/** 気象情報取得サービス */
	@Resource
	protected MeteorequestInfoService meteorequestInfoService;
	@Resource
	protected MeteoDataService meteoDataService;
	@Resource
	protected MeteotriggerInfoService meteotriggerInfoService;

	protected Map<Long, Long> lastIdMap = new HashMap<Long, Long>();


	/**
	 * アラームチェック実行
	 */
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void checkAlarm() {

		long starttime = System.currentTimeMillis();

		List<MeteorequestInfo> requestList = meteorequestInfoService.findByValid();

		String surl = null;
		try {
			userTransaction.begin();

			for (MeteorequestInfo req : requestList) {
//震度速報
//if (!req.id.equals(2L)) continue;
//警報注意報
//if (!req.id.equals(1L)) continue;
//津波警報
//if (!req.meteotypeid.equals(3)) continue;
//震源震度に関する情報
//if (!req.id.equals(4L)) continue;
//土砂災害警戒情報
//if (!req.meteotypeid.equals(6)) continue;
//竜巻注意情報
//if (!req.meteotypeid.equals(8)) continue;

				//前回取得したXMLの最新のIDを取得
				Long lastid = lastIdMap.get(req.id);
				logger.trace("req.id, lastid : "+req.id +", "+lastid);
				if (lastid == null){
					MeteoData latestMeteoData = meteoDataService.findLatest(req.id);
					if(latestMeteoData!=null){
						lastid = latestMeteoData.meteoid;
					}else{
						lastid=0L;
					}
				}

				if (StringUtil.isEmpty(req.meteoareaid)) continue;

				//XML検索リクエスト用URL組立
				surl = meteoUrl + "jmaxml/find/" + lastid + "/" + req.meteoareaid + "/" + meteotypeMasterService.getMeteotypeName(req.meteotypeid);

				//System.out.println("surl : "+surl);
				//ファイルの読み込み
				readXMLFileUrl(req, surl, false, 0l);

//			}
			}
		} catch (Exception e) {
			//logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error(surl);
			logger.error("",e);
			try {
				userTransaction.setRollbackOnly();
			} catch (Exception e1) {
				logger.error(e1);
			}
			throw new ServiceException(e);
		} finally {
			try {
				if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
					userTransaction.commit();
				} else {
					userTransaction.rollback();
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}

			long endtime = System.currentTimeMillis();
			double elapsed = (double)(endtime-starttime)/1000;
			logger.info("[MethodDuration] MeteoricAlarmService.checkAlarm elapsed: "+String.format("%.2f", elapsed)+"s"
					+" request: "+requestList.size()
					+" elapsed per request: "+String.format("%.2f", elapsed/requestList.size())+"s/request"
					+" (start at "+new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(starttime))+")");
		}
	}

	/**
	 * ファイルのURLを読み込む
	 * @param req 気象庁リクエスト
	 * @param xmlfileurl XMLファイル
	 * @param fileOnly ファイル取得のみの場合
	 * @param demoid デモID
	 * @throws Exception
	 */
	public void readXMLFileUrl(MeteorequestInfo req, String xmlfileurl, boolean fileOnly, long demoid) throws Exception
	{
		URL url = new URL(xmlfileurl);
		URLConnection urlcon = url.openConnection();
		//FIXME! 長時間取得していないと、60秒でも取得しきれない可能性大。DBのレコードを定期的に削除するしかないか。
		urlcon.setReadTimeout(60000);
		InputStream in = urlcon.getInputStream();

		String str = convertString(in);
		in.close();
		if (str.isEmpty()) return;

		/** 受け取った結果文字列の解析 */
		//「ID,XML本文」のセット。複数あればその数だけカンマ区切りで「ID,XML本文」のセットが返ってくる
		String[] resData = str.split(",");
		if (fileOnly && resData.length > 0) {//ファイル取得のみの場合はIDがないのでセット
			String[] rdata = new String[2];
			rdata[0] = "0";
			rdata[1] = resData[0];
			resData = rdata;
		}
		//System.out.println(str);
		//System.out.println("resData length : "+resData.length);

		/** 取得XML文字列をファイル化して格納 */
		//自治体IDごとにディレクトリを作成
		String meteoXmlPath =  fileService.getXmlRoot() + mxmlPath+req.localgovinfoid;
		//ディレクトリが無ければ作成する
		File dateDir = new File(meteoXmlPath);
		if(!dateDir.exists()) {
			boolean created = dateDir.mkdirs();
			if(!created) {
				logger.error("failed mkdirs "+dateDir.getAbsolutePath());
			}
		}

		//取得したXMLファイルの数だけ登録
		for(int i=0; i< resData.length; i=i+2){
			//XMLファイル「xmlID_ファイル数連番」とする
			String fileNum = FileUtil.getFileNum(meteoXmlPath, "%06d");
			//警報注意報
			//File jmaxml = new File("D:\\NIED\\戦略推進\\災害対応システム２\\気象\\xsl_test\\70_15_08_130412_01VPWW53.xml");
			//File jmaxml = new File("D:\\NIED\\戦略推進\\災害対応システム２\\気象\\xsl_test\\70_15_08_130412_01VPWW50.xml");
			//File jmaxml = new File("D:\\NIED\\戦略推進\\災害対応システム２\\気象\\xsl_test\\119202_000108.xml");
			//震度速報
			//File jmaxml = new File("C:\\work\\NIED\\戦略推進\\災害対応システム2\\data\\kisyou\\jmaxml_20130826_Samples\\70_32-39_11_120615_01shindosokuhou3.xml");
			//津波警報
			//File jmaxml = new File("D:\\NIED\\戦略推進\\災害対応システム２\\気象\\xsl_test\\70_32-39_11_120615_11tsunamiyohou11.xml");
			//震源・震度に関する情報
			//File jmaxml = new File("D:\\NIED\\戦略推進\\災害対応システム２\\気象\\xsl_test\\70_32-39_11_120615_05zenkokusaisumo11.xml");
			//土砂災害警戒情報
			//File jmaxml = new File("D:\\NIED\\戦略推進\\災害対応システム２\\気象\\xsl_test\\70_17_03_091210_VXWW40_0724-0810.xml");
			//竜巻注意情報
			//File jmaxml = new File("D:\\NIED\\戦略推進\\災害対応システム２\\気象\\xsl_test\\70_19_01_091210_tatsumakijyohou1.xml");



			File jmaxml = new File(meteoXmlPath+"/"+resData[i]+"_"+fileNum+".xml");
			//ファイル出力
			PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(jmaxml)));
			pw.println(resData[i+1]);
			pw.close();

			/** 取得したXMLファイルをXSLTでparse */
			//xsltファイルの取得
			MeteoxsltInfo xsltInfo = meteoxsltInfoService.findByLocalgovinfoidOrZeroAndMeteotypeid(req.localgovinfoid, req.meteotypeid);
			File xsltFile = new File(fileService.getXmlRoot() + mxsltPath + xsltInfo.filepath);
			//parse準備
			TransformerFactory trfactory = TransformerFactory.newInstance();
			StringWriter swriter = new StringWriter();
			Transformer t = trfactory.newTransformer(new StreamSource(xsltFile));
			//parse
			t.setParameter("code", req.meteoareaid);
			t.transform(new StreamSource(jmaxml), new StreamResult(swriter));
			//結果をStringに出力
			String parseStr = swriter.toString();
			String[] datas = parseStr.split("\\\\n");

			//メッセージ用StringBuffer定義
			StringBuffer msg = new StringBuffer();
			//発表日時は全種類共通なのでここでセット
			//xsltの解析で、機械的に時刻を+9しているだけなので、24時間を超える時間になっている場合があるので、ここでTimestampに直して発表時刻を修正
			Timestamp reportdatetime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(datas[1]).getTime());
			String reportdatetimeStr = reportdatetime.toString();
			//末尾の「.0」を削除する
			reportdatetimeStr = reportdatetimeStr.substring(0, reportdatetimeStr.length()-2);

			msg.append(lang.__("【{0} 気象庁発表】", reportdatetimeStr));
			String title = lang.__("【{0} 気象庁発表】", reportdatetimeStr);

			//meteo_dataテーブルへの登録
			MeteoData meteoData = new MeteoData();
			meteoData.meteorequestinfoid = req.id;
			meteoData.meteoid = Long.parseLong(resData[i]);
			meteoData.reporttime = new Timestamp(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(datas[1]).getTime());
			meteoData.filepath = meteoXmlPath+"/"+resData[i]+"_"+fileNum+".xml";
			meteoDataService.insert(meteoData);
			//最新IDの格納
			lastIdMap.put(req.id, Long.parseLong(resData[i]));
			logger.info("receive jmaxml("+(req.meteotypeMaster!=null?req.meteotypeMaster.name:req.meteotypeid)+") meteorequestinfoid="+req.id+", meteodataid="+meteoData.id+" localgovinfoid="+req.localgovinfoid);

			//すでに取得した情報の場合
			List<MeteoData> meteolist = meteoDataService.findByMeteoIdAndLocalgovInfoId(meteoData.meteoid, req.localgovinfoid);
			if (meteolist.size() > 1 && demoid <= 0) continue;//すでに取得済み

			if (demoid > 0 || reportdatetime.getTime() > System.currentTimeMillis()-expire) {//2日前までしか取得しない
				AlertrequestInfoDto requestInfo = Beans.createAndCopy(AlertrequestInfoDto.class, req).execute();
				MeteotriggerInfo trigger = meteotriggerInfoService.findByMeteorequestInfoId(req.id);
				AlerttriggerInfoDto triggerInfo = null;
				if (trigger != null) triggerInfo = Beans.createAndCopy(AlerttriggerInfoDto.class, trigger).execute();

				execAlarmData(requestInfo, triggerInfo, jmaxml, datas, title, msg, meteoData.id, demoid);
			}
		}
	}

	/**
	 * 入力ストリームを文字列に変換
	 * @param is 入力ストリーム
	 * @return 文字列
	 * @throws IOException
	 */
	public String convertString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		StringBuilder sb = new StringBuilder();
		String line;
		while (null != (line = reader.readLine())) {
			sb.append(line);
		}
		return sb.toString();
	}
}
