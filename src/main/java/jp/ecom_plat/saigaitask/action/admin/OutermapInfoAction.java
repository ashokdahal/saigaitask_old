/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import static jp.ecom_plat.saigaitask.entity.Names.externalmapdataInfo;
import static jp.ecom_plat.saigaitask.entity.Names.externaltabledataInfo;
import static jp.ecom_plat.saigaitask.util.Constants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AuthorizationInfo;
import jp.ecom_plat.saigaitask.entity.db.ExternalmapdataInfo;
import jp.ecom_plat.saigaitask.entity.db.ExternaltabledataInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.form.db.OutermapInfoForm;
import jp.ecom_plat.saigaitask.service.db.ExternalmapdataInfoService;
import jp.ecom_plat.saigaitask.service.db.ExternaltabledataInfoService;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.JqGridUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import net.sf.json.JSONObject;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/outermapInfo")
public class OutermapInfoAction extends AbstractAdminAction<MenuInfo> {
	protected OutermapInfoForm outermapInfoForm;

	/** サービスクラス */
	@Resource
	protected ExternalmapdataInfoService externalmapdataInfoService;
	@Resource
	protected ExternaltabledataInfoService externaltabledataInfoService;


	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index() {
		return "/admin/outermapInfo/index";
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
			conditions.put("layerparent", 0);

			//親のIDの取得
//			String parentgridRefkey = (String)request.getParameter("parentgrid_refkey");
//			String parentId = (String)request.getParameter("parentId");
			if(StringUtils.isNotEmpty(parentId)){
				//検索条件をセット
				logger.debug("--- parentgridRefkey:" + parentgridRefkey + ", parentId:" + parentId);
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
			String sidx = (String) request.getParameter(JQG_PAGER_SIDX);    //ソート項目名
			String sord = (String) request.getParameter(JQG_PAGER_SORD);    //ソート順（昇順 or 降順）

			//postDataパラメータ取得
			//loadonce
			boolean loadonce = Boolean.parseBoolean((String)request.getParameter(JQG_LOADONCE));
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

			// 一覧件数取得
			int count = externalmapdataInfoService.getCount(conditions);

			// 一覧内容取得
			List<ExternalmapdataInfo> list = new ArrayList<ExternalmapdataInfo>();
			if(count > 0){
				list = externalmapdataInfoService.findByCondition(conditions, sidx, sord, limit, (page-1)*limit);
			}

			// 返却リスト作成
			ArrayList<OutermapdisplayerInfo> results= new ArrayList<OutermapdisplayerInfo>();
			for(ExternalmapdataInfo externalmapdataInfo : list){
				OutermapdisplayerInfo result = new OutermapdisplayerInfo();
				result.setId(externalmapdataInfo.id.toString());
				result.setMenuinfoid(externalmapdataInfo.menuinfoid.toString());
				result.setName(externalmapdataInfo.name!=null?externalmapdataInfo.name:"");
				result.setMetadataid(externalmapdataInfo.metadataid!=null?externalmapdataInfo.metadataid:"");
				result.setWmscapsurl(externalmapdataInfo.wmscapsurl!=null?externalmapdataInfo.wmscapsurl:"");
				result.setFilterid(externalmapdataInfo.filterid!=null?externalmapdataInfo.filterid.toString():"");
				result.setLayeropacity(externalmapdataInfo.layeropacity!=null?externalmapdataInfo.layeropacity.toString():"");
				result.setVisible(externalmapdataInfo.visible!=null?externalmapdataInfo.visible.toString():"");
				result.setList(externalmapdataInfo.externaltabledataInfo!=null?"true":"false");
				result.setClosed(externalmapdataInfo.closed!=null?externalmapdataInfo.closed.toString():"");
				result.setDisporder(externalmapdataInfo.disporder!=null?externalmapdataInfo.disporder.toString():"");
				if(externalmapdataInfo.externaltabledataInfo != null){
					result.setExternaltabledatainfoid(externalmapdataInfo.externaltabledataInfo.id!=null?externalmapdataInfo.externaltabledataInfo.id.toString():"");
					result.setExternaltabledatainfofilterid(externalmapdataInfo.externaltabledataInfo.filterid!=null?externalmapdataInfo.externaltabledataInfo.filterid.toString():"");
				}else{
					result.setExternaltabledatainfoid("");
					result.setExternaltabledatainfofilterid("");
				}
				result.setAuthorizationInfo(externalmapdataInfo.authorizationInfo);
				result.setAuthorizationinfoid(externalmapdataInfo.authorizationinfoid!=null?externalmapdataInfo.authorizationinfoid.toString():"");
				results.add(result);
			}

			//ユーザーデータ取得
			Map<String, Object> userdata = new HashMap<String, Object>();

			//表示順取得
			int maxDisporder = 0;
			BeanMap maxDisporderConditions = new BeanMap();
			//親IDが存在する場合、親IDに紐付くデータの内、最大の表示順を取得する。
			maxDisporderConditions.put(parentgridRefkey, Integer.parseInt(parentId));
			maxDisporder = externalmapdataInfoService.getLargestDisporder(maxDisporderConditions);
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
	      json.put(JQG_PAGER_ROWS, jsonForCyclicalReferences(results));   //結果一覧

	      logger.debug("--- JSON ---:" + json.toString());

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
	 * Jqgrid新規登録、編集、削除実行（Ajax）<br>
	 * 地図レイヤ情報およびメニューテーブル情報に登録、編集、削除する。<br>
	 * 地図レイヤ情報テーブルにおいて、同一メニューIDのレコードでeditableがtrueのものは最大１件のみ存在可とする。
	 * メニューテーブル情報テーブルにおいて、同一メニューIDに紐付くレコードは最大１件のみ存在可とする。
	 * @return null
	 * @throws ServiceException
	 */
	@RequestMapping(value="/jqgridedit", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String jqgridedit(@ModelAttribute OutermapInfoForm outermapInfoForm) throws ServiceException {

		int i;

		//WMSのlegendheightを処理
		//if (outermapInfoForm.metadataid.equals("WMSCapabilities") && outermapInfoForm.legendheight != "") {
		if (!outermapInfoForm.wmscapsurl.equals("") && outermapInfoForm.legendheight != "") {
			int legendheight = Integer.parseInt(outermapInfoForm.legendheight);
			if (legendheight > 0 && 10 <= legendheight && legendheight <= 64)
				if (outermapInfoForm.wmslegendurl != "")
					outermapInfoForm.wmslegendurl += "HEIGHT=" + outermapInfoForm.legendheight;
		}

		try{
			//操作種別取得
			String oper = (String)request.getParameter(JQG_OPER);
			if(StringUtils.isEmpty(oper)){
				throw new IllegalStateException(lang.__("There is no operation type."));
			}

			//操作種別に応じたアクションを実行
			ExternalmapdataInfo externalmapdataInfoEntity;
			ExternaltabledataInfo externaltabledataInfoEntity;

			logger.debug("--- oper:" + oper);

			//登録
			if(JQG_OPER_ADD.equals(oper)) {
				//検索条件マップ
				BeanMap conditions = new BeanMap();
				Long menuinfoid = Long.parseLong(outermapInfoForm.menuinfoid);
				//登録実行
				//外部地図データ情報テーブルは無条件に登録。
				externalmapdataInfoEntity = Beans.createAndCopy(ExternalmapdataInfo.class, outermapInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
				externalmapdataInfoService.insert(externalmapdataInfoEntity);

				//外部リストデータ情報テーブルは『リスト』のチェック有無により以下のように処理。
				//『リスト』にチェックあり⇒登録対象を登録する。
				//『リスト』にチェックない⇒なにもしない。
				if(Boolean.parseBoolean(outermapInfoForm.list)){
					//登録実行
					externaltabledataInfoEntity = populateExternaltabledataInfo(outermapInfoForm);
					externaltabledataInfoService.insert(externaltabledataInfoEntity);
				}

				//WMSのサブレイヤを追加
				//if (outermapInfoForm.metadataid.equals("WMSCapabilities")) {
				if (!outermapInfoForm.wmscapsurl.equals("")) {
					if (outermapInfoForm.selectedwmslayerids != "" && outermapInfoForm.selectedwmslayernames != "") {
						long id = externalmapdataInfoService.getLargestId(Long.parseLong(outermapInfoForm.menuinfoid));
						logger.debug("--- INSERTEDID:" + id);
						//選択されたレイヤリストを分析して１つずつのサブレイヤを登録
						String[] layerids = outermapInfoForm.selectedwmslayerids.split("\\$,\\$");
						String[] layernames = outermapInfoForm.selectedwmslayernames.split("\\$,\\$");
						if (layerids.length > 0 && layerids.length == layernames.length) {
							for (i = 0; i < layerids.length; i++) {
								externalmapdataInfoEntity.id = null;
								externalmapdataInfoEntity.layerparent = id;
								externalmapdataInfoEntity.name = layernames[i];
								externalmapdataInfoEntity.featuretypeid = layerids[i];
								externalmapdataInfoService.insert(externalmapdataInfoEntity);
							}
						}
					}
				}

				//採番したキー値を返却
				JSONObject json = new JSONObject();
				json.put(JQG_NEW_ENTITY, StringUtil.jsonForCyclicalReferences(externalmapdataInfoEntity));
				responseService.responseJson(json);
			//編集
			}else if(JQG_OPER_EDIT.equals(oper)) {
				//編集前データのシリアライズ文字列を取得
				String serializedPreEditData = (String)request.getParameter(JQG_SERIALIZED_PRE_EDIT_DATA);
				if(StringUtils.isEmpty(serializedPreEditData)){
					throw new IllegalStateException(lang.__("There is no serialization string of pre-edit data."));
				}else{
					//現時点の編集対象データを取得
					Long id = Long.parseLong(outermapInfoForm.id);
					logger.debug("--- id:" + id);
					ExternalmapdataInfo currExternalmapdataInfoEntity = externalmapdataInfoService.findById(id);
					//結果返却用JSON
					JSONObject json = new JSONObject();

					if(currExternalmapdataInfoEntity != null){
						//編集対象が存在する場合、実行。
						//現時点の編集対象データが他者により変更されているかを判定
						//2エンティティあるが、楽観排他は外部地図データ情報で判断する。
						if(JqGridUtil.existsNoChangeByOther(serializedPreEditData, ExternalmapdataInfo.class, currExternalmapdataInfoEntity)){
							//現時点の編集対象のメタデータID
							String nowMetadataid = currExternalmapdataInfoEntity.metadataid;
							//検索条件マップ
							BeanMap conditions = new BeanMap();
							//変更されていない場合、更新
							//外部地図データ情報テーブルは無条件に更新する。
							//更新実行
							externalmapdataInfoEntity = Beans.createAndCopy(ExternalmapdataInfo.class, outermapInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();


							//WMSレイヤ処理
							//if (outermapInfoForm.metadataid.equals("WMSCapabilities")) {
							if (outermapInfoForm.wmscapsurl.equals("")) {
								//WMS設定フォームを開かなかった場合、WMS関係の項目を維持
								if (outermapInfoForm.selectedwmslayerids == "" && outermapInfoForm.selectedwmslayernames == "") {
									externalmapdataInfoEntity.attribution = currExternalmapdataInfoEntity.attribution;
									// 透過度はメタデータ指定でも使うようになったためこの処理は不要
									//externalmapdataInfoEntity.layeropacity = currExternalmapdataInfoEntity.layeropacity;
									externalmapdataInfoEntity.wmsformat = currExternalmapdataInfoEntity.wmsformat;
									externalmapdataInfoEntity.layerdescription = currExternalmapdataInfoEntity.layerdescription;
								}
							}

							externalmapdataInfoService.update(externalmapdataInfoEntity);
							json.put(JQG_EDIT_ENTITY, StringUtil.jsonForCyclicalReferences(externalmapdataInfoEntity));
							responseService.responseJson(json);

							//外部リストデータ情報テーブルは『リスト』にチェックの状態に応じ、以下のように処理する。
							//チェックあり⇒編集対象のレコードがあれば更新。なければ登録。
							//チェックなし⇒編集対象のレコードがあれば削除。
							externaltabledataInfoEntity = populateExternaltabledataInfo(outermapInfoForm);
							conditions.put(externaltabledataInfo().menuinfoid().toString(), externaltabledataInfoEntity.menuinfoid);
							conditions.put(externaltabledataInfo().metadataid().toString(), nowMetadataid);
							int count = externaltabledataInfoService.getCount(conditions);
							if(Boolean.parseBoolean(outermapInfoForm.list)){
								//リストにチェックあり
								if(count > 0){
									//更新実行
									logger.debug("--- UPdate ---");
									externaltabledataInfoService.update(externaltabledataInfoEntity);
								}else{
									//登録実行
									externaltabledataInfoService.insert(externaltabledataInfoEntity);
								}
							}else{
								//リストにチェックなし
								if(count > 0){
									externaltabledataInfoService.delete(externaltabledataInfoEntity);
								}
							}

							//WMSのサブレイヤを追加
							//if (outermapInfoForm.metadataid.equals("WMSCapabilities")) {
							if (!outermapInfoForm.wmscapsurl.equals("")) {
								if (outermapInfoForm.selectedwmslayerids != "" && outermapInfoForm.selectedwmslayernames != "") {
									externalmapdataInfoService.deleteChildren(id);	//古いサブレイヤを削除

									//選択されたレイヤリストを分析して１つずつのサブレイヤを登録
									String[] layerids = outermapInfoForm.selectedwmslayerids.split("\\$,\\$");
									String[] layernames = outermapInfoForm.selectedwmslayernames.split("\\$,\\$");
									if (layerids.length > 0 && layerids.length == layernames.length) {

										for (i = 0; i < layerids.length; i++) {
											logger.debug("--- Register layer, id:" + layerids[i] + ", name:" + layernames[i]);
											externalmapdataInfoEntity.id = null;
											externalmapdataInfoEntity.layerparent = id;
											externalmapdataInfoEntity.name = layernames[i];
											externalmapdataInfoEntity.featuretypeid = layerids[i];
											externalmapdataInfoService.insert(externalmapdataInfoEntity);
										}
									}
								}
							}
							else {
								externalmapdataInfoService.deleteChildren(externalmapdataInfoEntity.id);	//WMSから切り替えた可能性があるので全てのサブレイヤを削除
							}
						}else{
							//変更されている場合、更新せずにリフレッシュをユーザーに促すメッセージを返却。
							json.put(JQG_MESSAGE, lang.__("It may have been changed by another user.\n Refresh and state latest."));
							responseService.responseJson(json);
						}
					}else{
						//編集対象が存在しない場合、他者により削除された可能性があるため更新は実行実行せず、
						//リフレッシュをユーザーに促すメッセージを返却。
						json.put(JQG_MESSAGE, lang.__("It may have been deleted by another user.\n Refresh and state latest.<!--2-->"));
						responseService.responseJson(json);
					}
				}
			//削除
			}else if(JQG_OPER_DEL.equals(oper)) {
				//外部地図データ情報テーブルは無条件に削除。
				//削除対象データの有無を取得
				//検索条件マップ
				BeanMap conditions = new BeanMap();
				Long id = Long.parseLong(outermapInfoForm.id);
				conditions.put(externalmapdataInfo().id().toString(), id);
				int count = externalmapdataInfoService.getCount(conditions);
				List<ExternalmapdataInfo> externalmapdataInfoList = new ArrayList<ExternalmapdataInfo>();
				if(count==1){
					//削除対象が存在する場合、削除実行。
					externalmapdataInfoList = externalmapdataInfoService.findByCondition(conditions, null, null, null, null);
					//削除実行
					externalmapdataInfoService.delete(externalmapdataInfoList.get(0));
				}else{
					//削除対象が存在しない場合、他者により削除された可能性があるため削除は実行実行せず、
					//リフレッシュをユーザーに促すメッセージを返却。
					JSONObject json = new JSONObject();
					json.put(JQG_MESSAGE, lang.__("It may have been deleted by another user.\n Refresh and state latest."));
					responseService.responseJson(json);
				}

				//外部リストデータ情報テーブルは削除対象に紐付くレコードが存在している（『リスト』にチェックがあった）場合に削除。
				if(externalmapdataInfoList.get(0).externaltabledataInfo != null){
					//削除実行
					externaltabledataInfoService.delete(externalmapdataInfoList.get(0).externaltabledataInfo);
				}

				//WMSのサブレイヤを削除
				externalmapdataInfoService.deleteChildren(id);
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

	private ExternaltabledataInfo populateExternaltabledataInfo(OutermapInfoForm outermapdisplayerInfoForm){
		ExternaltabledataInfo externaltabledataInfoEntity = Beans.createAndCopy(ExternaltabledataInfo.class, outermapdisplayerInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
		if(StringUtils.isNotEmpty(outermapdisplayerInfoForm.externaltabledatainfoid)){
			externaltabledataInfoEntity.id = Long.parseLong(outermapdisplayerInfoForm.externaltabledatainfoid);
		}else{
			externaltabledataInfoEntity.id = null;
		}

		return externaltabledataInfoEntity;
	}

	/**
     * 地図レイヤ情報とメニューテーブル情報データ格納クラス
     */
    private class OutermapdisplayerInfo{
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getMenuinfoid() {
			return menuinfoid;
		}
		public void setMenuinfoid(String menuinfoid) {
			this.menuinfoid = menuinfoid;
		}
    	public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMetadataid() {
			return metadataid;
		}
		public void setMetadataid(String metadataid) {
			this.metadataid = metadataid;
		}


		public String getLayerparent() {
			return layerparent;
		}
		public void setLayerparent(String layerparent) {
			this.layerparent = layerparent;
		}

		public String getAttribution() {
			return attribution;
		}
		public void setAttribution(String attribution) {
			this.attribution = attribution;
		}

		public String getLayeropacity() {
			return layeropacity;
		}
		public void setLayeropacity(String layeropacity) {
			this.layeropacity = layeropacity;
		}

		public String getWmscapsurl() {
			return wmscapsurl;
		}
		public void setWmscapsurl(String wmscapsurl) {
			this.wmscapsurl = wmscapsurl;
		}

		public String getWmsurl() {
			return wmsurl;
		}
		public void setWmsurl(String wmsurl) {
			this.wmsurl = wmsurl;
		}

		public String getWmsformat() {
			return wmsformat;
		}
		public void setWmsformat(String wmsformat) {
			this.wmsformat = wmsformat;
		}

		public String getWmslegendurl() {
			return wmslegendurl;
		}
		public void setWmslegendurl(String wmslegendurl) {
			this.wmslegendurl = wmslegendurl;
		}

		public String getWmsfeatureurl() {
			return wmsfeatureurl;
		}
		public void setWmsfeatureurl(String wmsfeatureurl) {
			this.wmsfeatureurl = wmsfeatureurl;
		}

		public String getLayerdescription() {
			return layerdescription;
		}
		public void setLayerdescription(String layerdescription) {
			this.layerdescription = layerdescription;
		}



		public String getList() {
			return list;
		}
		public void setList(String list) {
			this.list = list;
		}
		public String getFilterid() {
			return filterid;
		}
		public void setFilterid(String filterid) {
			this.filterid = filterid;
		}
		public String getVisible() {
			return visible;
		}
		public void setVisible(String visible) {
			this.visible = visible;
		}
		public String getClosed() {
			return closed;
		}
		public void setClosed(String closed) {
			this.closed = closed;
		}
		public String getDisporder() {
			return disporder;
		}
		public void setDisporder(String disporder) {
			this.disporder = disporder;
		}
		public String getExternaltabledatainfoid() {
			return externaltabledatainfoid;
		}
		public void setExternaltabledatainfoid(String externaltabledatainfoid) {
			this.externaltabledatainfoid = externaltabledatainfoid;
		}
		public String getExternaltabledatainfofilterid() {
			return externaltabledatainfofilterid;
		}
		public void setExternaltabledatainfofilterid(
				String externaltabledatainfofilterid) {
			this.externaltabledatainfofilterid = externaltabledatainfofilterid;
		}
		public AuthorizationInfo getAuthorizationInfo() {
			return authorizationInfo;
		}
		public void setAuthorizationInfo(
				AuthorizationInfo authorizationinfo) {
			this.authorizationInfo = authorizationinfo;
		}
		public String getAuthorizationinfoid() {
			return authorizationinfoid;
		}
		public void setAuthorizationinfoid(
				String authorizationinfoid) {
			this.authorizationinfoid = authorizationinfoid;
		}
		private String id = "";
    	private String menuinfoid = "";
		private String name = "";
    	private String metadataid = "";

    	private String layerparent = "";
    	private String attribution = "";
    	private String layeropacity = "";
    	private String wmscapsurl = "";
    	private String wmsurl = "";
    	private String wmsformat = "";
    	private String wmslegendurl = "";
    	private String wmsfeatureurl = "";
    	private String layerdescription = "";

    	private String list = "false";
    	private String filterid = "";
    	private String visible = "true";
    	private String closed = "false";
    	private String disporder = "";
    	private String externaltabledatainfoid = "";
    	private String externaltabledatainfofilterid = "";
    	private AuthorizationInfo authorizationInfo = null;
    	private String authorizationinfoid = "";
    }
}
