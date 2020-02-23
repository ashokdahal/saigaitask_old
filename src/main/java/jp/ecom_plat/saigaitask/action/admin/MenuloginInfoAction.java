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
import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.form.db.MenuloginInfoForm;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.JqGridUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import net.sf.json.JSONObject;

/**
 * メニュー設定情報のアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/menuloginInfo")
public class MenuloginInfoAction extends AbstractAdminAction<MenuloginInfo> {
	protected MenuloginInfoForm menuloginInfoForm;

	/** サービスクラス */
	@Resource
	protected MenuloginInfoService menuloginInfoService;
	@Resource
	protected GroupInfoService groupInfoService;
	@Resource
	protected UnitInfoService unitInfoService;

	/** 災害種別統合化済みフラグ */
	public Boolean disasterCombined;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("disasterCombined", disasterCombined);
	}

	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model) {
		disasterCombined = loginDataDto.getLocalgovInfo().disastercombined;

		setupModel(model);

		return "/admin/menuloginInfo/index";
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
			BeanMap conditions2 = new BeanMap();

			//班ID,課IDいずれか空欄でも一覧表示は空欄を含めて表示する
			//  boolean isConditions trueは従来通りconditionsを利用、falseは今回対応の実装を利用
			//    検索時：true
			//    システム管理者でログイン時 : true
			//    それ以外：false
			boolean isConditions = false;

			//参照できるデータをログインした自治体(ID)で絞り込む
			Long localgovinfoid = loginDataDto.getLocalgovinfoid();
			if(localgovinfoid != ADMIN_LOCALGOVINFOID){
				//管理者(localgovinfoid == 0)は全件参照可能
				conditions.put(Names.menuloginInfo().groupInfo().localgovinfoid().toString(), localgovinfoid);
				//conditions.put(Names.menuloginInfo().unitInfo().localgovinfoid().toString(), localgovinfoid);
				//conditions2.put(Names.menuloginInfo().unitInfo().localgovinfoid().toString(), localgovinfoid);
			}else{
				// システム管理者でログイン時
				isConditions = true;
			}

			//検索フォーム関連パラメータ取得
			boolean isSearch = Boolean.valueOf((String) request.getParameter(JQG_DO_SEARCH)); //検索実行時か否か
			String searchField = (String) request.getParameter(JQG_SEARCH_FIELD); //検索対象
			String searchOper = (String) request.getParameter(JQG_SEARCH_OPER);   //比較演算子
			String searchString =  (String) request.getParameter(JQG_SEARCH_STRING);   //検索文字列
			if(isSearch && StringUtils.isNotEmpty(searchField) && StringUtils.isNotEmpty(searchOper) && StringUtils.isNotEmpty(searchString)){
				//検索時
				isConditions = true;
				//検索条件をセット
				conditions.put(JqGridUtil.getCoditionStr(searchField, searchOper), searchString);
				//conditions2.put(JqGridUtil.getCoditionStr(searchField, searchOper), searchString);
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
			int count;

			count = menuloginInfoService.getCountWithGroupUnit(conditions,localgovinfoid, isConditions);
			//if(loginDataDto.isUsual()){
			//	count = menuloginInfoService.getCountForUnit(conditions2);
			//}else{
			//	count = menuloginInfoService.getCount(conditions);
			//}

			// 一覧内容取得
			List<MenuloginInfo> list = new ArrayList<MenuloginInfo>();
			if(count > 0){
				list = menuloginInfoService.findByConditionWithGroupUnit(conditions, sidx, sord, limit, (page-1)*limit,localgovinfoid, isConditions);
				//if(loginDataDto.isUsual()){
				//	list = menuloginInfoService.findByConditionForUnit(conditions2, sidx, sord, limit, (page-1)*limit);
				//}else{
				//	list = menuloginInfoService.findByCondition(conditions, sidx, sord, limit, (page-1)*limit);
				//}
			}
			// 班または課の情報を追加取得
			for(MenuloginInfo menuloginInfo : list){
				if(menuloginInfo.groupid != null &&
						menuloginInfo.groupid > 0 &&
						menuloginInfo.groupInfo == null){
					menuloginInfo.groupInfo = groupInfoService.findById(menuloginInfo.groupid);
				}
				if(menuloginInfo.unitid != null &&
						menuloginInfo.unitid > 0 &&
						menuloginInfo.unitInfo == null){
					menuloginInfo.unitInfo = unitInfoService.findById(menuloginInfo.unitid);
				}
			}

			//ユーザーデータ取得
			Map<String, Object> userdata = new HashMap<String, Object>();


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
	public String jqgridedit(@ModelAttribute MenuloginInfoForm menuloginInfoForm) throws ServiceException {

		try{
			//操作種別取得
			String oper = (String)request.getParameter(JQG_OPER);
			if(StringUtils.isEmpty(oper)){
				throw new IllegalStateException(lang.__("There is no operation type."));
			}

			//操作種別に応じたアクションを実行
			MenuloginInfo entity;
			//登録
			if(JQG_OPER_ADD.equals(oper)) {
				entity = Beans.createAndCopy(MenuloginInfo.class, menuloginInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
				//登録実行
				menuloginInfoService.insert(entity);
				//全コピー実施時
				boolean isAllCopy = Boolean.valueOf((String)request.getParameter(JQG_ALLCOPY)).booleanValue();
				if(isAllCopy){
					long oldId = Long.parseLong((String)request.getParameter(JQG_ALLCOPY_ID));
					doAllCopy("menulogin_info", oldId, entity.id);
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
					MenuloginInfo menuloginInfo = menuloginInfoService.findById(Long.decode(menuloginInfoForm.id));

					//結果返却用JSON
					JSONObject json = new JSONObject();
					if(menuloginInfo != null){
						//編集対象が存在する場合、実行。
						//現時点の編集対象データが他者により変更されているかを判定
						if(JqGridUtil.existsNoChangeByOther(serializedPreEditData, MenuloginInfo.class, menuloginInfo)){
							//変更されていない場合、更新
							entity = Beans.createAndCopy(MenuloginInfo.class, menuloginInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
							PropertyName<?>[] updateExcludesAry = null;
							List<PropertyName<?>> updateExcludesList = new ArrayList<PropertyName<?>>();
							//更新実行
							menuloginInfoService.update(entity, updateExcludesAry);
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
				conditions.put("id", Integer.parseInt(menuloginInfoForm.id));
				int count = menuloginInfoService.getCountById(Long.decode(menuloginInfoForm.id));
				if(count==1){
					//削除対象が存在する場合、削除実行。
					entity = menuloginInfoService.findById(Long.decode(menuloginInfoForm.id));
					//削除実行
					try{
						menuloginInfoService.deleteLogically(entity);
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
					/*if("disaster_master".equals(tableName)){
						//災害種別マスタプルダウンの場合、disasterid =0 は表示しない。
						List<DisasterMaster> newParentList = new ArrayList<DisasterMaster>();
						for(Object obj : parentList){
							DisasterMaster disastermaster = (DisasterMaster)obj;
							if(disastermaster.id != 0){
								newParentList.add(disastermaster);
							}
						}
						parentList = newParentList;
					}*/
					//0:選択なし　は「空欄」選択とするため削除
					//if ("group_info".equals(tableName)) {
					//	GroupInfo group = new GroupInfo();
					//	group.id = 0l;
					//	group.name = lang.__("No selected");
					//	List<GroupInfo> newParentList = new ArrayList<GroupInfo>();
					//	newParentList.add(group);
					//	for(Object obj : parentList)
					//		newParentList.add((GroupInfo)obj);
					//	parentList = newParentList;
					//}
					//0:選択なし　は「空欄」選択とするため削除
					//if ("unit_info".equals(tableName)) {
					//	UnitInfo unit = new UnitInfo();
					//	unit.id = 0l;
					//	unit.name = lang.__("No selected");
					//	List<UnitInfo> newParentList = new ArrayList<UnitInfo>();
					//	newParentList.add(unit);
					//	for(Object obj : parentList)
					//		newParentList.add((UnitInfo)obj);
					//	parentList = newParentList;
					//}
					if ("disaster_master".equals(tableName) && loginDataDto.getLocalgovInfo().disastercombined) {
						List<DisasterMaster> newParentList = new ArrayList<DisasterMaster>();
						DisasterMaster disaster = new DisasterMaster();
						disaster.id = 0;
						disaster.name = lang.__("Normal");
						newParentList.add(disaster);
						disaster = new DisasterMaster();
						disaster.id = 1;
						disaster.name = lang.__("Disaster");
						newParentList.add(disaster);
						parentList = newParentList;
					}
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
}
