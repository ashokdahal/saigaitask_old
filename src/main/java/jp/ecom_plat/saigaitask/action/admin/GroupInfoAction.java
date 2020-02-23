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
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.exception.SQLRuntimeException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.security.UserAuthorization;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.form.db.GroupInfoForm;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.JqGridUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import net.sf.json.JSONObject;

/**
 * 班情報のアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/groupInfo")
public class GroupInfoAction extends AbstractAdminAction<GroupInfo> {
	public GroupInfoForm groupInfoForm;

	/** サービスクラス */
	@Resource
	protected GroupInfoService groupInfoService;
	@Resource
	protected UnitInfoService unitInfoService;

	/** 管理者権限 */
	public boolean admin;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("admin", admin);
	}

	/**  APIキー再発行 */
	public static final String JQG_OPER_MODIFYAPIKEY = "apikeymodify";
	public static final String APIKEY_REISSUE = "reissue";
	public static final String APIKEY_CLEAR = "clear";

	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model, @ModelAttribute GroupInfoForm groupInfoForm) {
		this.groupInfoForm = groupInfoForm;
		admin = loginDataDto.isAdmin();

		setupModel(model);

		return "/admin/groupInfo/index";
	}

	/**
	 * 一覧情報を取得してJSON形式で返却（Ajax）
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value="/jqgridindex")
	@ResponseBody
	public String jqgridindex() throws ServiceException {
		try{
			//検索条件マップ
			BeanMap conditions = new BeanMap();


			//参照できるデータをログインした自治体(ID)で絞り込む
			Long localgovinfoid = loginDataDto.getLocalgovinfoid();
			if(localgovinfoid != ADMIN_LOCALGOVINFOID){
				//管理者(localgovinfoid == 0)は全件参照可能
				conditions.put(Names.groupInfo().localgovinfoid().toString(), localgovinfoid);
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

			//管理権限がない場合はログインユーザに関するレコードの表示する
			if (!loginDataDto.isAdmin()) {
				//検索条件をセット
				conditions.put(Names.groupInfo().id().toString(), loginDataDto.getGroupid());
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
			int count = groupInfoService.getCount(conditions);

			// 一覧内容取得
			List<GroupInfo> list = new ArrayList<GroupInfo>();
			if(count > 0){
				list = groupInfoService.findByCondition(conditions, sidx, sord, limit, (page-1)*limit);
			}

			//ユーザーデータ取得
			Map<String, Object> userdata = new HashMap<String, Object>();

			//表示順取得
			int maxDisporder = 0;
			BeanMap maxDisporderConditions = new BeanMap();
			//親IDが存在しない場合、全データの内、最大の表示順を取得する。
			maxDisporder = groupInfoService.getLargestDisporder(maxDisporderConditions);
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
	public String jqgridedit(@ModelAttribute GroupInfoForm groupInfoForm) throws ServiceException {

		try{
			//操作種別取得
			String oper = (String)request.getParameter(JQG_OPER);
			if(StringUtils.isEmpty(oper)){
				throw new IllegalStateException(lang.__("There is no operation type."));
			}

			//操作種別に応じたアクションを実行
			GroupInfo entity;
			//登録
			if(JQG_OPER_ADD.equals(oper)) {
				entity = Beans.createAndCopy(GroupInfo.class, groupInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
					if(StringUtils.isNotEmpty(entity.password)){
						//暗号化対象を暗号化
						entity.password = UserAuthorization.getEncryptedPass(entity.password);
					}
					if(StringUtils.isNotEmpty(entity.ecompass)){
						//暗号化対象を暗号化
						entity.ecompass = StringUtil.encrypt(entity.ecompass);
					}

				// APIキー発行
				entity.apikey = apikeyIssue(entity.localgovinfoid);	
					
				//登録実行
				groupInfoService.insert(entity);
				//全コピー実施時
				boolean isAllCopy = Boolean.valueOf((String)request.getParameter(JQG_ALLCOPY)).booleanValue();
				if(isAllCopy){
					long oldId = Long.parseLong((String)request.getParameter(JQG_ALLCOPY_ID));
					doAllCopy("group_info", oldId, entity.id);
				}
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
					GroupInfo groupInfo = groupInfoService.findById(Long.decode(groupInfoForm.id));

					//結果返却用JSON
					JSONObject json = new JSONObject();
					if(groupInfo != null){
						//編集対象が存在する場合、実行。
						//現時点の編集対象データが他者により変更されているかを判定
						if(JqGridUtil.existsNoChangeByOther(serializedPreEditData, GroupInfo.class, groupInfo)){
							//変更されていない場合、更新
							entity = Beans.createAndCopy(GroupInfo.class, groupInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
							PropertyName<?>[] updateExcludesAry = null;
							List<PropertyName<?>> updateExcludesList = new ArrayList<PropertyName<?>>();

							if(StringUtils.isNotEmpty(entity.password)){
								//暗号化対象のデータが送信されてきた場合、暗号化
								entity.password = UserAuthorization.getEncryptedPass(entity.password);
							}else{
								//暗号化対象が送信されてこなければ暗号化せず、更新もしない。
								updateExcludesList.add(Names.groupInfo().password());
							}
							if(StringUtils.isNotEmpty(entity.ecompass)){
								//暗号化対象のデータが送信されてきた場合、暗号化
								entity.ecompass = StringUtil.encrypt(entity.ecompass);
							}else{
								//暗号化対象が送信されてこなければ暗号化せず、更新もしない。
								updateExcludesList.add(Names.groupInfo().ecompass());
							}
							
							// APIキー編集
							String apikeyModify = (String)request.getParameter(JQG_OPER_MODIFYAPIKEY);
							if(! StringUtils.isEmpty(apikeyModify)) {
								switch (apikeyModify) {
								case APIKEY_REISSUE:
									entity.apikey = apikeyIssue(entity.localgovinfoid);	
									break;
								case APIKEY_CLEAR:
									entity.apikey = "";	
									break;
								default:
									updateExcludesList.add(Names.groupInfo().apikey());
									break;
								}
							}else {
								updateExcludesList.add(Names.groupInfo().apikey());
							}
							
							updateExcludesAry = updateExcludesList.toArray(new PropertyName[updateExcludesList.size()]);
							//更新実行
							groupInfoService.update(entity, updateExcludesAry);
							json.put(JQG_EDIT_ENTITY, StringUtil.jsonForCyclicalReferences(entity));
							responseService.responseJson(json);
							//ログイン情報を更新
							// 班でログインしている場合のみ
							if(loginDataDto.getGroupInfo() != null){
								if(loginDataDto.getGroupInfo().id.equals(entity.id)){
									loginDataDto.setGroupInfo(entity);
								}
							}
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
				conditions.put("id", Integer.parseInt(groupInfoForm.id));
				int count = groupInfoService.getCount(conditions);
				if(count==1){
					//削除対象が存在する場合、削除実行。
					entity = groupInfoService.findById(Long.decode(groupInfoForm.id));
					//削除実行
					try{
						groupInfoService.deleteLogically(entity);
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
				String dbServiceMethodName = "findByCondition";
				Class<?>[] methodArgType = {Map.class, String.class, String.class, Integer.class, Integer.class};
				//検索条件追加。
				BeanMap conditions = new BeanMap();
				long localgovinfoid = loginDataDto.getLocalgovinfoid();
				if("localgov_info".equals(tableName)){
					//作成するテーブルが地方自治体情報の場合、管理者以外であればログインした自治体で絞り込む。
					if(localgovinfoid != ADMIN_LOCALGOVINFOID){
						//管理者(localgovinfoid == 0)は全件参照可能
						conditions.put(Names.localgovInfo().id().toString(), localgovinfoid);
					}
				}else{
					//作成する対象テーブルに自治体idカラムが存在する場合、管理者以外はログイン時の自治体idで絞り込む。
					if(localgovinfoid != ADMIN_LOCALGOVINFOID){
						String entityName = ENTITY_PKG+"."+StringUtil.snakeToCamelCapitalize(tableName);
						if(JqGridUtil.hasEntityProperty(entityName, "localgovinfoid")){
							conditions.put("localgovinfoid", localgovinfoid);
						}
					}
				}
				Object[] methodArg = {conditions, codeColumn, ASC, NON_LIMIT, NON_SELECT_OFFSET};
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
	 * 自治体IDに紐づく班情報データ(id, name)を取得する関数
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value="/createIdName")
	@ResponseBody
	public String createIdName() throws ServiceException {
		try{
			String codeColumn = "id";
			List<?> parentList;
			long lgov_id = Integer.parseInt((String)request.getParameter("localgovinfoid"));
			String tableName = "group_info";
			//String dbServiceMethodName = "findByLocalgovinfoid";
			String dbServiceMethodName = "findByCondition";
			//Class<?>[] methodArgType = {Long.class};
			Class<?>[] methodArgType = {Map.class, String.class, String.class, Integer.class, Integer.class};

			BeanMap conditions = new BeanMap();
			conditions.put("localgovinfoid", lgov_id);

			//Object[] methodArg = {lgov_id};
			Object[] methodArg = {conditions, codeColumn, ASC, NON_LIMIT, NON_SELECT_OFFSET};

			String dbServiceName = DB_SERVICE_PKG+"."+StringUtil.snakeToCamelCapitalize(tableName)+DB_SERVICE_SUFFIX;

			Object result = executeDbServiceMethod(dbServiceName, dbServiceMethodName, methodArgType, methodArg);
			parentList = (ArrayList<?>)result;

			//返却値
			JSONObject json = new JSONObject();

	        String code = "id";
			String decode = "name";
	        List<Map<String, Object>> selectTagList = new ArrayList<Map<String, Object>>();
	        for(Object entity : parentList){

	            Map<String, Object> map = new HashMap<String, Object>();
	            Class<?> clazz = entity.getClass();
	            Object codeValue = clazz.getField(code).get(entity);
	            Object decodeValue = clazz.getField(decode).get(entity);
	            Object deleted = clazz.getField("deleted").get(entity);
	            // 削除フラグfalseのデータのみ取得
	            if(deleted.equals(false)){
	            	map.put("key", codeValue);
	            	map.put("value", decodeValue);
	            	selectTagList.add(map);
	            }
	        }

	        json.put("selectTag", selectTagList);

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
	 * APIキーの発行を行う
	 *
	 * @return APIキー
	 * @throws Exception
	 */
	private String apikeyIssue(Long localgovinfoid) {

		String apikey = "";
		boolean isValidApikey = false;
		while( !isValidApikey ) {
			apikey = UUID.randomUUID().toString();
			GroupInfo dbGroupInfo = groupInfoService.findByLocalgovInfoIdAndApikey(localgovinfoid, apikey);
			UnitInfo dbUnitInfo = unitInfoService.findByLocalgovInfoIdAndApikey(localgovinfoid, apikey);
			if(dbGroupInfo == null && dbUnitInfo == null) {
				isValidApikey = true;
			}
		}
		return apikey;
	}
}
