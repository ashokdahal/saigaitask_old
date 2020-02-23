/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import static jp.ecom_plat.saigaitask.util.Constants.ADMIN_LOCALGOVINFOID;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;
import static jp.ecom_plat.saigaitask.util.Constants.DATE_FORMAT_PATTERN;
import static jp.ecom_plat.saigaitask.util.Constants.DB_SERVICE_PKG;
import static jp.ecom_plat.saigaitask.util.Constants.DB_SERVICE_SUFFIX;
import static jp.ecom_plat.saigaitask.util.Constants.ENTITY_PKG;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_DO_SEARCH;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_EDIT_ENTITY;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_FIRST_PAGE;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_LOADONCE;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_MESSAGE;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_NEW_ENTITY;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_OPER;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_OPER_ADD;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_OPER_DEL;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_OPER_EDIT;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_PAGER_PAGE;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_PAGER_RECORDS;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_PAGER_ROWS;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_PAGER_SIDX;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_PAGER_SORD;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_PAGER_TOTAL;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_SEARCH_FIELD;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_SEARCH_OPER;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_SEARCH_STRING;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_SELECT_TAG;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_SERIALIZED_PRE_EDIT_DATA;
import static jp.ecom_plat.saigaitask.util.Constants.JQG_USERDATA;
import static jp.ecom_plat.saigaitask.util.Constants.NON_LIMIT;
import static jp.ecom_plat.saigaitask.util.Constants.NON_SELECT_OFFSET;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import jp.ecom_plat.map.db.StoredConsumer;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.DisasteritemInfo;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MobileqrcodeInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.form.db.MobileqrcodeInfoForm;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MobileqrcodeInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.JqGridUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import net.sf.json.JSONObject;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/mobileqrcodeInfo")
public class MobileqrcodeInfoAction  extends AbstractAdminAction<MobileqrcodeInfo> {
	/** アクションフォーム */
	protected MobileqrcodeInfoForm mobileqrcodeInfoForm;

	/** サービスクラス */
	@Resource
	protected MobileqrcodeInfoService mobileqrcodeInfoService;
	@Resource
	protected TablemasterInfoService tablemasterInfoService;
	@Resource
	protected GroupInfoService groupInfoService;
	@Resource
	protected UnitInfoService unitInfoService;

	/**
	 * QRコード一覧ページ
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"qrcodelist"})
	public String qrcodelist(Map<String,Object>model, @ModelAttribute MobileqrcodeInfoForm mobileqrcodeInfoForm) {
		long groupid = loginDataDto.getGroupid();
		long unitid = loginDataDto.getUnitid();
		
		// 班が指定されている場合
		if(StringUtils.isNotEmpty(mobileqrcodeInfoForm.groupid)) {
			groupid = Long.valueOf(mobileqrcodeInfoForm.groupid);
			unitid = 0;
		}

		// 課が指定されている場合
		if(StringUtils.isNotEmpty(mobileqrcodeInfoForm.groupid)) {
			groupid = 0;
			unitid = Long.valueOf(mobileqrcodeInfoForm.unitid);
		}

		// get group,unit
		GroupInfo groupInfo = 0<groupid ? groupInfoService.findById(groupid) : null;
		UnitInfo unitInfo   = 0<unitid ? unitInfoService.findById(unitid) : null;

		// get localgovinfoid
		long localgovinfoid = groupInfo!=null ? groupInfo.localgovinfoid : unitInfo.localgovinfoid;

		// 表示する班、課
		List<Object> infos = new ArrayList<>();
		if(groupInfo!=null) infos.add(groupInfo);
		if(unitInfo!=null) infos.add(unitInfo);

		if(loginDataDto.isAdmin()) {
			infos.clear();
			infos.addAll(groupInfoService.findByLocalgovInfoIdAll(localgovinfoid));
			infos.addAll(unitInfoService.findByLocalgovInfoIdAll(localgovinfoid));
		}
		
		// select
		// @see http://s2container.seasar.org/2.4/ja/s2jdbc_manager_auto.html#Map%E3%81%AB%E3%82%88%E3%82%8B%E6%8C%87%E5%AE%9A
		Map<String, Object> conditions = new HashMap<>();
		conditions.put(Names.mobileqrcodeInfo().valid().toString(), true);
		conditions.put(Names.mobileqrcodeInfo().authenticationdate().toString()+"_GE", new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
		conditions.put(Names.mobileqrcodeInfo().localgovinfoid().toString(), localgovinfoid);
		if(groupInfo!=null) {
			conditions.put(Names.mobileqrcodeInfo().groupid().toString()+"_IN", new Long[]{-1L,groupInfo.id});
		}
		if(unitInfo!=null) {
			conditions.put(Names.mobileqrcodeInfo().unitid().toString()+"_IN", new Long[]{-1L,unitInfo.id});
		}
		String sortName = Names.mobileqrcodeInfo().disporder().toString();
		String sortOrder = Constants.ASC;
		Integer limit = null;
		Integer offset = null;
		List<MobileqrcodeInfo> mobileqrcodeInfos = mobileqrcodeInfoService.findByCondition(conditions, sortName, sortOrder, limit, offset);
		// APIキー用QRコード
		addAPIKeyMobileqrcodeInfo(mobileqrcodeInfos);
		
		setupModel(model);
		model.put("mobileqrcodeInfos", mobileqrcodeInfos);
		if(loginDataDto.isAdmin() || 0<loginDataDto.getGroupid()) {
			infos.add(0, mobileqrcodeInfoService.getNegativeGroupInfo());
		}
		if(loginDataDto.isAdmin() || 0<loginDataDto.getUnitid()) {
			infos.add(1, mobileqrcodeInfoService.getNegativeUnitInfo());
		}
		model.put("infos", infos);
		return "/admin/mobileqrcodeInfo/qrcodelist";
	}

	protected void addAPIKeyMobileqrcodeInfo(List<MobileqrcodeInfo> mobileqrcodeInfos) {
		List<GroupInfo> groupInfos = groupInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());
		List<UnitInfo> unitInfos = unitInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());

		// APIキー指定があれば、id=0で追加
		for(GroupInfo groupInfo : groupInfos) {
			if(StringUtils.isNotEmpty(groupInfo.apikey)) {
				MobileqrcodeInfo mobileqrcodeInfo = new MobileqrcodeInfo();
				mobileqrcodeInfo.id = 0L;
				mobileqrcodeInfo.groupid = groupInfo.id;
				mobileqrcodeInfo.title = lang.__("APIキー");
				mobileqrcodeInfos.add(mobileqrcodeInfo);
			}
		}

		// APIキー指定があれば、id=0で追加
		for(UnitInfo unitInfo : unitInfos) {
			if(StringUtils.isNotEmpty(unitInfo.apikey)) {
				MobileqrcodeInfo mobileqrcodeInfo = new MobileqrcodeInfo();
				mobileqrcodeInfo.id = 0L;
				mobileqrcodeInfo.unitid = unitInfo.id;
				mobileqrcodeInfo.title = lang.__("APIキー");
				mobileqrcodeInfos.add(mobileqrcodeInfo);
			}
		}
	}
	
	
	/**
	 * QRコード表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"qrcode"})
	public String qrcode(Map<String,Object>model, @ModelAttribute MobileqrcodeInfoForm mobileqrcodeInfoForm) {
		this.mobileqrcodeInfoForm = mobileqrcodeInfoForm;

		Long id = Long.valueOf(mobileqrcodeInfoForm.id);
		
		Long groupid = loginDataDto.getUnitid();
		Long unitid = loginDataDto.getUnitid();
		
		// 班が指定されている場合
		if(StringUtils.isNotEmpty(mobileqrcodeInfoForm.groupid)) {
			groupid = Long.valueOf(mobileqrcodeInfoForm.groupid);
			unitid = null;
		}

		// 課が指定されている場合
		if(StringUtils.isNotEmpty(mobileqrcodeInfoForm.unitid)) {
			groupid = null;
			unitid = Long.valueOf(mobileqrcodeInfoForm.unitid);
		}

		boolean outputSecret = false;
		JSONObject json = mobileqrcodeInfoService.getQRCodeJSON(id, groupid, unitid, outputSecret);
		// password の付与
		String password = request.getParameter("password");
		if(StringUtils.isNotEmpty(password)) json.put("password", password);
		
		return "forward:/mob/qrcode?text="+json.toString();
	}

	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model, @ModelAttribute MobileqrcodeInfoForm mobileqrcodeInfoForm) {
		this.mobileqrcodeInfoForm = mobileqrcodeInfoForm;
		return "/admin/mobileqrcodeInfo/index";
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
				conditions.put(Names.mobileqrcodeInfo().localgovinfoid().toString(), localgovinfoid);
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
			int count = mobileqrcodeInfoService.getCount(conditions);

			// 一覧内容取得
			List<MobileqrcodeInfo> list = new ArrayList<MobileqrcodeInfo>();
			if(count > 0){
				list = mobileqrcodeInfoService.findByCondition(conditions, sidx, sord, limit, (page-1)*limit);
			}

			//ユーザーデータ取得
			Map<String, Object> userdata = new HashMap<String, Object>();

			// oauthconsumerid
			userdata.put("oauthconsumer", getStoredConsumerSelectJson());
			
			//表示順取得
			int maxDisporder = 0;
			BeanMap maxDisporderConditions = new BeanMap();
			//親IDが存在する場合、親IDに紐付くデータの内、最大の表示順を取得する。
			//maxDisporderConditions.put(parentgridRefkey, Integer.parseInt(parentId));
			maxDisporder = mobileqrcodeInfoService.getLargestDisporder(maxDisporderConditions);
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
			json.put(JQG_PAGER_ROWS, StringUtil.jsonForCyclicalReferences(list));   //結果一覧

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
	public String jqgridedit(@ModelAttribute MobileqrcodeInfoForm mobileqrcodeInfoForm) throws ServiceException {
		try{
			//操作種別取得
			String oper = (String)request.getParameter(JQG_OPER);
			if(StringUtils.isEmpty(oper)){
				throw new IllegalStateException(lang.__("There is no operation type."));
			}

			//操作種別に応じたアクションを実行
			MobileqrcodeInfo entity;
			//登録
			if(JQG_OPER_ADD.equals(oper)) {
				entity = Beans.createAndCopy(MobileqrcodeInfo.class, mobileqrcodeInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
				//登録実行
				mobileqrcodeInfoService.insert(entity);
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
					MobileqrcodeInfo mobileqrcodeInfo = mobileqrcodeInfoService.findById(Long.decode(mobileqrcodeInfoForm.id));

					//結果返却用JSON
					JSONObject json = new JSONObject();
					if(mobileqrcodeInfo != null){
						//編集対象が存在する場合、実行。
						//現時点の編集対象データが他者により変更されているかを判定
						if(JqGridUtil.existsNoChangeByOther(serializedPreEditData, DisasteritemInfo.class, mobileqrcodeInfo)){
							//変更されていない場合、更新
							entity = Beans.createAndCopy(MobileqrcodeInfo.class, mobileqrcodeInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
							PropertyName<?>[] updateExcludesAry = null;
							List<PropertyName<?>> updateExcludesList = new ArrayList<PropertyName<?>>();
							//更新実行
							mobileqrcodeInfoService.update(entity, updateExcludesAry);
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
				conditions.put("id", Integer.parseInt(mobileqrcodeInfoForm.id));
				int count = mobileqrcodeInfoService.getCount(conditions);
				if(count==1){
					//削除対象が存在する場合、削除実行。
					entity = mobileqrcodeInfoService.findById(Long.decode(mobileqrcodeInfoForm.id));
					//削除実行
					try{
						mobileqrcodeInfoService.delete(entity);
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
			}else if("_oauth_consumer".equals(tableName)){
				json.put(JQG_SELECT_TAG, getStoredConsumerSelectJson());   //結果一覧
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
				Object result;
				if("tablemaster_info".equals(tableName)){
					Class<?>[] methodArgTypet = {Long.class};
					Object[] methodArgt = {localgovinfoid};
					result = executeDbServiceMethod(dbServiceName, "findByLocalgovinfoid", methodArgTypet, methodArgt);
				}else{
					result = executeDbServiceMethod(dbServiceName, dbServiceMethodName, methodArgType, methodArg);
				}
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

	public String getStoredConsumerSelectJson() {
        List<Map<String, Object>> selectTagList = new ArrayList<Map<String, Object>>();
		for(StoredConsumer consumer : StoredConsumer.fetchAll()) {
			Long id = consumer.getId();
			String description = (String) consumer.getSetting("description");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("key", id);
            map.put("value", id + "：" + description);
            selectTagList.add(map);
		}
		return StringUtil.json(selectTagList);   //結果一覧
	}
}
