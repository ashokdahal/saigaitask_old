/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import static jp.ecom_plat.saigaitask.util.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.exception.SQLRuntimeException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.form.db.MenutableInfoForm;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.EcommapDataGetService;
import jp.ecom_plat.saigaitask.util.DatabaseUtil;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.JqGridUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import net.sf.json.JSONObject;

/**
 * メニューテーブル情報のアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/menutableInfo")
public class MenutableInfoAction extends AbstractAdminAction<MenutableInfo> {
	protected MenutableInfoForm menutableInfoForm;

	/** サービスクラス */
	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected TablemasterInfoService tablemasterInfoService;
	@Resource
	protected EcommapDataGetService ecommapDataGetService;
	@Resource
	protected JdbcManager jdbcManager;


	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index() {
		return "/admin/menutableInfo/index";
	}

	/**
	 * 一覧情報を取得してJSON形式で返却（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value="/jqgridindex/{parentgrid_refkey}/{parentId}")
	@ResponseBody
	public String jqgridindex(
				@PathVariable("parentgrid_refkey") String parentgridRefkey,
				@PathVariable String parentId) throws ServiceException {
		try{
			//検索条件マップ
			BeanMap conditions = new BeanMap();

			//親のIDの取得
//			String parentgridRefkey = (String)request.getParameter("parentgrid_refkey");
//			String parentId = (String)request.getParameter("parentId");
			if(StringUtils.isNotEmpty(parentId)){
				//検索条件をセット
				conditions.put(parentgridRefkey, parentId);
			}


			//検索フォーム関連パラメータ取得
			boolean isSearch = Boolean.valueOf((String) request.getParameter(JQG_DO_SEARCH)); //検索実行時か否か
			String searchField = (String) request.getParameter(JQG_SEARCH_FIELD); //検索対象
			String searchOper = (String) request.getParameter(JQG_SEARCH_OPER);   //比較演算子
			String searchString =  (String) request.getParameter(JQG_SEARCH_STRING);   //検索文字列
			if(isSearch && StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchOper) && StringUtils.isNotEmpty(searchString)){
				//検索条件をセット
				conditions.put(JqGridUtil.getCoditionStr(searchField, searchOper), searchString);
			}

			//ページャー関連パラメータ取得
			int page = Integer.parseInt((String)request.getParameter(JQG_PAGER_PAGE));  //現在ページ番号
			int rows =  Integer.parseInt((String)request.getParameter(JQG_PAGER_ROWS)); //1ページの件数
			String sidx = (String) request.getParameter(JQG_PAGER_SIDX);    //ソート項目名
			String sord = (String) request.getParameter(JQG_PAGER_SORD);    //ソート順（昇順 or 降順）

			//postDataパラメータ取得
			//loadonce
			boolean loadonce = Boolean.valueOf((String)request.getParameter(JQG_LOADONCE));
			boolean editing = Boolean.valueOf((String)request.getParameter("editing"));

			//取得件数の判定
			//loadonce=true：グリッドデータを1回ロードし、その後はある程度クライアント側だけで済ませるため、データは全件取得。
			//loadonce=false：グリッドデータは必要な時に必要（1ページ）な分だけ取得するため、データは1ページ分取得。
			int limit = rows;
			if(loadonce){
				limit = NON_LIMIT;
				if (!editing) {
					page = JQG_FIRST_PAGE;
				}
			}

			// 一覧件数取得
			int count = menutableInfoService.getCount(conditions);

			// 一覧内容取得
			List<MenutableInfo> list = new ArrayList<MenutableInfo>();
			if(count > 0){
				list = menutableInfoService.findByCondition(conditions, sidx, sord, limit, (page-1)*limit);
			}

			//ユーザーデータ取得
			Map<String, Object> userdata = new HashMap<String, Object>();

			//表示順取得
			int maxDisporder = 0;
			BeanMap maxDisporderConditions = new BeanMap();
			//親IDが存在する場合、親IDに紐付くデータの内、最大の表示順を取得する。
			maxDisporderConditions.put(parentgridRefkey, Integer.parseInt(parentId));
			maxDisporder = menutableInfoService.getLargestDisporder(maxDisporderConditions);
			userdata.put("maxDisporder", maxDisporder);


			//ページャー関連パラメータ再計算
			int totalPages = 0;
			if( count >0 ) {
				totalPages = (int)Math.ceil((double)count/rows);
			}
			if (page > totalPages){
				page = totalPages;
			}

			//返却値セット
			JSONObject json = new JSONObject();
			json.put(JQG_PAGER_PAGE, page); //現在ページ番号
			json.put(JQG_PAGER_RECORDS, count); //総結果件数
			json.put(JQG_PAGER_TOTAL, totalPages);  //総ページ数
			json.put(JQG_USERDATA, userdata); //ユーザーデータ
			json.put(JQG_PAGER_ROWS, jsonForCyclicalReferences(list));   //結果一覧

			//レスポンスへ出力
			responseService.responseJson(json);

		}catch(Exception e){
			e.printStackTrace();
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed initial display processing. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}

		return null;
	}

	/**
	 * Jqgrid新規登録、編集、削除実行（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value="/jqgridedit", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String jqgridedit(@ModelAttribute MenutableInfoForm menutableInfoForm) throws ServiceException {

		try{
			//操作種別取得
			String oper = (String)request.getParameter(JQG_OPER);
			if(StringUtils.isEmpty(oper)){
				throw new IllegalStateException(lang.__("There is no operation type."));
			}

			//操作種別に応じたアクションを実行
			MenutableInfo entity;
			//登録
			if(JQG_OPER_ADD.equals(oper)) {
				entity = Beans.createAndCopy(MenutableInfo.class, menutableInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
				//登録実行
				menutableInfoService.insert(entity);
				//採番したキー値を返却
				JSONObject json = new JSONObject();
				json.put(JQG_NEW_ENTITY, StringUtil.jsonForCyclicalReferences(entity));
				responseService.responseJson(json);
			//編集
			}else if(JQG_OPER_EDIT.equals(oper)) {
				//編集前データのシリアライズ文字列を取得
				String serializedPreEditData = (String)request.getParameter(JQG_SERIALIZED_PRE_EDIT_DATA);
				if(StringUtils.isEmpty(serializedPreEditData)){
					throw new IllegalStateException(lang.__("There is no serialization string of pre-edit data."));
				}else{
					//現時点の編集対象データを取得
					MenutableInfo menutableInfo = menutableInfoService.findById(Long.decode(menutableInfoForm.id));

					//結果返却用JSON
					JSONObject json = new JSONObject();
					if(menutableInfo != null){
						//編集対象が存在する場合、実行。
						//現時点の編集対象データが他者により変更されているかを判定
						if(JqGridUtil.existsNoChangeByOther(serializedPreEditData, MenutableInfo.class, menutableInfo)){
							//変更されていない場合、更新
							entity = Beans.createAndCopy(MenutableInfo.class, menutableInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
							PropertyName<?>[] updateExcludesAry = null;
							List<PropertyName<?>> updateExcludesList = new ArrayList<PropertyName<?>>();
							//更新実行
							menutableInfoService.update(entity, updateExcludesAry);
							json.put(JQG_EDIT_ENTITY, StringUtil.jsonForCyclicalReferences(entity));
							responseService.responseJson(json);
						}else{
							//変更されている場合、更新せずにリフレッシュをユーザーに促すメッセージを返却。
							json.put(JQG_MESSAGE, lang.__("It may have been changed by another user.\n You need modify the data on edit form."));
							responseService.responseJson(json);
						}
					}else{
						//編集対象が存在しない場合、他者により削除された可能性があるため更新は実行実行せず、
						//リフレッシュをユーザーに促すメッセージを返却。
						json.put(JQG_MESSAGE, lang.__("It may have been deleted by another user."));
						responseService.responseJson(json);
					}
				}
			//削除
			}else if(JQG_OPER_DEL.equals(oper)) {
				//削除対象データの有無を取得
				//検索条件マップ
				BeanMap conditions = new BeanMap();
				conditions.put("id", Integer.parseInt(menutableInfoForm.id));
				int count = menutableInfoService.getCount(conditions);
				if(count==1){
					//削除対象が存在する場合、削除実行。
					entity = menutableInfoService.findById(Long.decode(menutableInfoForm.id));
					//削除実行
					try{
						menutableInfoService.deleteLogically(entity);
					}catch(SQLRuntimeException se){
						//子レコードが存在する場合、外部キーエラーを検知し、メッセージを返却。
						if(TableService.isForeignKeyViolation(se)){
							JSONObject json = new JSONObject();
							json.put(JQG_MESSAGE, lang.__("You can't delete the data because of its child data."));
							responseService.responseJson(json);
						}else{
							throw se;
						}
					}
				}else{
					//削除対象が存在しない場合、他者により削除された可能性があるため削除は実行実行せず、
					//リフレッシュをユーザーに促すメッセージを返却。
					JSONObject json = new JSONObject();
					json.put(JQG_MESSAGE, lang.__("It may have been deleted by another user."));
					responseService.responseJson(json);
				}
			}else{
				throw new IllegalStateException(lang.__("Not proper operation type."));
			}
		}catch(Exception e){
			e.printStackTrace();
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed to update. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}

		return null;
	}

	/**
	 * Jqgrid selectボックスタグの構成要素を生成（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value="/createSelectTag/{selectTagColumn}/{tableName}/{codeColumn}/{decodeColumn}")
	@ResponseBody
	public String createSelectTag(
				@PathVariable String selectTagColumn,
				@PathVariable String tableName,
				@PathVariable String codeColumn,
				@PathVariable String decodeColumn) throws ServiceException {
		try{
			//パラメータ取得
//			String selectTagColumn = (String) request.getParameter(JQG_SELECT_TAG_COLUMN); //selectタグとするグリッドカラム名
//			String tableName = (String) request.getParameter(JQG_TABLE_NAME); //デコード値を保持しているテーブル名
//			String codeColumn = (String) request.getParameter(JQG_CODE_COLUMN); //デコード値を保持しているテーブルのコードカラム
//			String decodeColumn = (String) request.getParameter(JQG_DECODE_COLUMN); //デコード値を保持しているテーブルのデコードカラム

			//返却値
			JSONObject json = new JSONObject();

			if(StringUtils.isEmpty(selectTagColumn)
					|| StringUtils.isEmpty(tableName)
					|| StringUtils.isEmpty(codeColumn)
					|| StringUtils.isEmpty(decodeColumn)){
				throw new IllegalStateException(lang.__("Pull-down creation parameters is invalid."));
			}else{
				//デコード値を保持しているテーブルのデータを取得し、コードをデコードしたselectボックスタグの構成要素を生成。
				String dbServiceName = DB_SERVICE_PKG+"."+StringUtil.snakeToCamelCapitalize(tableName)+DB_SERVICE_SUFFIX;
				long localgovinfoid = loginDataDto.getLocalgovinfoid();
				String dbServiceMethodName = "findByLocalgovinfoid";
				Class<?>[] methodArgType = {Long.class};
				Object[] methodArg = {localgovinfoid};
				Object result = executeDbServiceMethod(dbServiceName, dbServiceMethodName, methodArgType, methodArg);
				List<?> parentList = (ArrayList<?>)result;

				//返却値セット
				if("localgov_info".equals(tableName)){
					json.put(JQG_SELECT_TAG, StringUtil.json(JqGridUtil.createSelectTagList(parentList, codeColumn, Names.localgovInfo().pref().toString(), Names.localgovInfo().city().toString())));   //結果一覧
				}else{
					json.put(JQG_SELECT_TAG, StringUtil.json(JqGridUtil.createSelectTagList(parentList, codeColumn, decodeColumn)));   //結果一覧
				}
			}

			//レスポンスへ出力
			responseService.responseJson(json);

		}catch(Exception e){
			e.printStackTrace();
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed to create pull down. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}

		return null;
	}

	/**
	 * eコミマップからのデータ取得
	 * @return null
	 * @throws Exception
	 */
	@RequestMapping(value="/jqgridecommap/{tableid}")
	@ResponseBody
	public String jqgridecommap(
				@PathVariable String tableid) throws ServiceException {
		try{
			String j = "";
			if (tableid != null) {
				TablemasterInfo tablemasterInfo = tablemasterInfoService.findById(new Long(tableid));
				Long mapMasterInfoId = tablemasterInfo.mapmasterinfoid;
				String layerId = tablemasterInfo.layerid;
				String layerName = tablemasterInfo.name;
				String tableName = tablemasterInfo.tablename;

		        //ページャー関連パラメータ取得
				int page;
				int rows;
		        if(request.getParameter(JQG_PAGER_PAGE) == null){
		        	page = 1;
		        }else{
		    		page = Integer.parseInt((String)request.getParameter(JQG_PAGER_PAGE));  //現在ページ番号
		        }
		        if(request.getParameter(JQG_PAGER_ROWS) == null){
		        	rows = 5;
		        }else{
		    		rows =  Integer.parseInt((String)request.getParameter(JQG_PAGER_ROWS)); //1ページの件数
		        }

				//postDataパラメータ取得
				//loadonce
				boolean loadonce = Boolean.valueOf((String)request.getParameter(JQG_LOADONCE));
		        if(request.getParameter(JQG_LOADONCE) == null){
		        	loadonce = true;
		        }else{
		    		loadonce = Boolean.valueOf((String)request.getParameter(JQG_LOADONCE));
		        }

		        //取得件数の判定
				//loadonce=true：グリッドデータを1回ロードし、その後はある程度クライアント側だけで済ませるため、データは全件取得。
				//loadonce=false：グリッドデータは必要な時に必要（1ページ）な分だけ取得するため、データは1ページ分取得。
				int limit = rows;
				if(loadonce){
					limit = NON_LIMIT;
					page = JQG_FIRST_PAGE;
				}
				int count = 0;
		  	    int totalPages = 0;

				// テーブルのカラム名のコメントマップを取得
				if (mapMasterInfoId.longValue() == 0 && tableName != null && !tableName.equals("")) {
					j = getSystemTableAttributeList(tableName);
				}

				//eコミマップからレイヤの属性情報取得し、レスポンスへ出力。
				if (mapMasterInfoId.longValue() != 0) {
					j = ecommapDataGetService.getAttributeList(loginDataDto.getGroupInfo().ecomuser, layerId);
				}

				//レイヤ名追加
				j = j.replaceAll("\"attrId\":", "\"layerName\":\""+layerName+"\",\"attrId\":");
				JSONObject json = JSONObject.fromObject(j);
				net.sf.json.JSONArray jsonArray = json.getJSONArray("items");

		  	    // 一覧件数取得
		  	    count = jsonArray.size();

		  	    //ページャー関連パラメータ再計算
		  	    if( count >0 ) {
		  	        totalPages = (int)Math.ceil((double)count/rows);
		  	    }
		  	    if (page > totalPages){
		  	        page = totalPages;
		  	    }
		  	    if (limit != NON_LIMIT)
		  	    	jsonArray = net.sf.json.JSONArray.fromObject(jsonArray.subList((page - 1) * limit, Math.min(page * limit, count)));

			      //返却値セット
			      json = new JSONObject();
			      json.put(JQG_PAGER_PAGE, page); //現在ページ番号
			      json.put(JQG_PAGER_RECORDS, count); //総結果件数
			      json.put(JQG_PAGER_TOTAL, totalPages);  //総ページ数
			      json.put("rows", jsonArray);   //結果一覧

			      //レスポンスへ出力
			      j = json.toString();
			}
			responseService.response(j);

		}catch(Exception e){
			e.printStackTrace();
			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
			logger.error("", e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed initial display processing. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}
		return null;
	}

	/**
	 * システムテーブルからのデータ取得
	 * @return 属性情報一覧のJSON形式の文字列
	 * @throws Exception
	 */
    protected String getSystemTableAttributeList(String tableName) {

    	try {
    		// 指定テーブルのカラム属性を取得
    		Map<String, String> alterMap = DatabaseUtil.getAlterFieldNameMap(jdbcManager, tableName);
	    	JSONArray items = new JSONArray();
			for (Map.Entry<String, String> e : alterMap.entrySet()) {
	    		JSONObject item = new JSONObject();
	    		// カラムの物理名、日本語名称を取得
	    		item.put("dataType", "0");
	    		item.put("name", e.getValue());
	    		item.put("attrId", e.getKey());
	    		item.put("dataExp", "");
	    		items.put(item);
	    	}
			// 属性情報の配列をJSON形式として作成
    		JSONObject jsonAt = new JSONObject();
    		jsonAt.put("items", items.toString());
    		// タイトルとしてシステムテーブルを設定する。（システムテーブル取得のみ"title"を設定されるものとする）
    		jsonAt.put("title", lang.__("System table"));

			return jsonAt.toString();

    	} catch (Exception e) {

    	}

    	return null;
    }
}