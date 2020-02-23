/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import static jp.ecom_plat.saigaitask.entity.Names.maplayerInfo;
import static jp.ecom_plat.saigaitask.entity.Names.menutableInfo;
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
import jp.ecom_plat.saigaitask.entity.db.MaplayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.form.db.InnermapdisplayerInfoForm;
import jp.ecom_plat.saigaitask.service.db.MaplayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.JqGridUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import net.sf.json.JSONObject;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/innermapInfo")
public class InnermapInfoAction extends AbstractAdminAction<MenuInfo> {
	protected InnermapdisplayerInfoForm innermapdisplayerInfoForm;

	/** サービスクラス */
	@Resource
	protected MaplayerInfoService maplayerInfoService;
	@Resource
	protected MenutableInfoService menutableInfoService;
	@Resource
	protected TablemasterInfoService tablemasterInfoService;


	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index() {
		return "/admin/innermapInfo/index";
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
			int count = maplayerInfoService.getCount(conditions);

			// 一覧内容取得
			List<MaplayerInfo> list = new ArrayList<MaplayerInfo>();
			if(count > 0){
				list = maplayerInfoService.findByCondition(conditions, sidx, sord, limit, (page-1)*limit);
			}

			// 返却リスト作成
			ArrayList<InnermapdisplayerInfo> results= new ArrayList<InnermapdisplayerInfo>();
			for(MaplayerInfo maplayerInfo : list){
				InnermapdisplayerInfo result = new InnermapdisplayerInfo();
				result.setId(maplayerInfo.id.toString());
				result.setMenuinfoid(maplayerInfo.menuinfoid.toString());
				result.setTablemasterinfoid(maplayerInfo.tablemasterinfoid.toString());
				result.setVisible(maplayerInfo.visible!=null?maplayerInfo.visible.toString():"");
				result.setClosed(maplayerInfo.closed!=null?maplayerInfo.closed.toString():"");
				result.setEditable(maplayerInfo.editable!=null?maplayerInfo.editable.toString():"");
				result.setAddable(maplayerInfo.addable!=null?maplayerInfo.addable.toString():"");
				result.setSnapable(maplayerInfo.snapable!=null?maplayerInfo.snapable.toString():"");
				result.setValid(maplayerInfo.valid!=null?maplayerInfo.valid.toString():"");
				result.setDisporder(maplayerInfo.disporder!=null?maplayerInfo.disporder.toString():"");
				result.setMenutableinfoid(maplayerInfo.menutableInfo!=null?maplayerInfo.menutableInfo.id.toString():"");
				result.setMenutableinfoaddable(maplayerInfo.menutableInfo!=null?maplayerInfo.menutableInfo.addable.toString():"");
				result.setMenutableinfodeletable(maplayerInfo.menutableInfo!=null?maplayerInfo.menutableInfo.deletable.toString():"");
				result.setName(maplayerInfo.tablemasterInfo!=null?maplayerInfo.tablemasterInfo.name:"");
				result.setList(maplayerInfo.menutableInfo!=null?"true":"false");
				results.add(result);
			}

			//ユーザーデータ取得
			Map<String, Object> userdata = new HashMap<String, Object>();

			//表示順取得
			int maxDisporder = 0;
			BeanMap maxDisporderConditions = new BeanMap();
			//親IDが存在する場合、親IDに紐付くデータの内、最大の表示順を取得する。
			maxDisporderConditions.put(parentgridRefkey, Integer.parseInt(parentId));
			maxDisporder = maplayerInfoService.getLargestDisporder(maxDisporderConditions);
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
	public String jqgridedit(@ModelAttribute InnermapdisplayerInfoForm innermapdisplayerInfoForm) throws ServiceException {

		try{
			//操作種別取得
			String oper = (String)request.getParameter(JQG_OPER);
			if(StringUtils.isEmpty(oper)){
				throw new IllegalStateException(lang.__("There is no operation type."));
			}

			//操作種別に応じたアクションを実行
			MaplayerInfo maplayerInfoEntity;
			MenutableInfo menutableInfoEntity;
			//登録
			if(JQG_OPER_ADD.equals(oper)) {
				//検索条件マップ
				BeanMap conditions = new BeanMap();
				Long menuinfoid = Long.parseLong(innermapdisplayerInfoForm.menuinfoid);
				//登録実行
				//地図レイヤ情報テーブルは『編集』のチェックの有無により以下のように処理。
				//『編集』のチェックあり⇒登録対象のメニューIDを持つ地図レイヤテーブルレコードのeditableをfalseで更新し、登録対象を登録。
				//『編集』のチェックなし⇒登録対象を登録。
				if(Boolean.parseBoolean(innermapdisplayerInfoForm.editable)){
					conditions.put(maplayerInfo().menuinfoid().toString(), menuinfoid);
					int count = maplayerInfoService.getCount(conditions);
					if(count>0){
						//同一メニューIDを持つレコードが存在する場合、editableの更新実行。
						List<MaplayerInfo> maplayerInfoList = maplayerInfoService.findByCondition(conditions);
						//更新実行
						for(MaplayerInfo entity : maplayerInfoList){
							entity.editable = false;
							maplayerInfoService.update(entity);
						}
					}
				}else{
				}
				maplayerInfoEntity = Beans.createAndCopy(MaplayerInfo.class, innermapdisplayerInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
				maplayerInfoService.insert(maplayerInfoEntity);

				//メニューテーブル情報テーブルは『リスト』のチェック有無により以下のように処理。
				//『リスト』にチェックあり⇒同一メニューIDのレコードを削除し、登録対象を登録する。
				//『リスト』にチェックない⇒なにもしない。
				if(Boolean.parseBoolean(innermapdisplayerInfoForm.list)){
					//削除対象データの有無を取得
					conditions = new BeanMap();
					conditions.put(menutableInfo().menuinfoid().toString(), menuinfoid);
					int count = menutableInfoService.getCount(conditions);
					if(count>0){
						//削除対象が存在する場合、削除実行。
						List<MenutableInfo> menutableInfoList = menutableInfoService.findByCondition(conditions);
						//削除実行
						for(MenutableInfo entity : menutableInfoList){
							menutableInfoService.deleteLogically(entity);
						}
					}

					//登録実行
					menutableInfoEntity = populateMenutableInfo(innermapdisplayerInfoForm);
					menutableInfoService.insert(menutableInfoEntity);
				}

				//採番したキー値を返却
				JSONObject json = new JSONObject();
				json.put(JQG_NEW_ENTITY, StringUtil.jsonForCyclicalReferences(maplayerInfoEntity));
				responseService.responseJson(json);
			//編集
			}else if(JQG_OPER_EDIT.equals(oper)) {
				//編集前データのシリアライズ文字列を取得
				String serializedPreEditData = (String)request.getParameter(JQG_SERIALIZED_PRE_EDIT_DATA);
				if(StringUtils.isEmpty(serializedPreEditData)){
					throw new IllegalStateException(lang.__("There is no serialization string of pre-edit data."));
				}else{
					//現時点の編集対象データを取得
					Long id = Long.parseLong(innermapdisplayerInfoForm.id);
					maplayerInfoEntity = maplayerInfoService.findById(id);
					//結果返却用JSON
					JSONObject json = new JSONObject();
					if(maplayerInfoEntity != null){
						//編集対象が存在する場合、実行。
						//現時点の編集対象データが他者により変更されているかを判定
						//2エンティティあるが、楽観排他は地図レイヤ情報で判断する。
						if(JqGridUtil.existsNoChangeByOther(serializedPreEditData, MaplayerInfo.class, maplayerInfoEntity)){
							//変更されていない場合、更新
							//地図レイヤ情報テーブルは『編集』のチェックの有無により以下のように処理。
							//『編集』のチェックあり⇒登録対象のメニューIDを持つ地図レイヤテーブルレコードのeditableをfalseで更新し、更新対象を更新。
							//『編集』のチェックなし⇒更新対象を更新。
							Long menuinfoid = Long.parseLong(innermapdisplayerInfoForm.menuinfoid);
							//検索条件マップ
							BeanMap conditions = new BeanMap();
							if(Boolean.parseBoolean(innermapdisplayerInfoForm.editable)){
								conditions.put(maplayerInfo().menuinfoid().toString(), menuinfoid);
								int count = maplayerInfoService.getCount(conditions);
								if(count>0){
									//同一メニューIDを持つレコードが存在する場合、editableの更新実行。
									List<MaplayerInfo> maplayerInfoList = maplayerInfoService.findByCondition(conditions);
									//更新実行
									for(MaplayerInfo entity : maplayerInfoList){
										entity.editable = false;
										maplayerInfoService.update(entity);
									}
								}
							}else{
							}

							//更新実行
							maplayerInfoEntity = Beans.createAndCopy(MaplayerInfo.class, innermapdisplayerInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
							maplayerInfoService.update(maplayerInfoEntity);
							json.put(JQG_EDIT_ENTITY, StringUtil.jsonForCyclicalReferences(maplayerInfoEntity));
							responseService.responseJson(json);

							//メニューテーブル情報テーブルは『リスト』にチェックの状態に応じ、以下のように処理する。
							//チェックあり⇒編集対象と同一メニューIDのレコードを削除し、登録。
							//チェックなし⇒編集対象のidのレコードを削除。
							if(Boolean.parseBoolean(innermapdisplayerInfoForm.list)){
								//リストにチェックあり
								conditions = new BeanMap();
								conditions.put(menutableInfo().menuinfoid().toString(), menuinfoid);
								//削除対象が存在する場合、削除実行。
								List<MenutableInfo> menutableInfoList = menutableInfoService.findByCondition(conditions);
								//削除実行
								for(MenutableInfo entity : menutableInfoList){
									menutableInfoService.deleteLogically(entity);
								}
								//登録実行
								menutableInfoEntity = populateMenutableInfo(innermapdisplayerInfoForm);
								menutableInfoService.insert(menutableInfoEntity);
							}else{
								//リストにチェックなし
								menutableInfoEntity = populateMenutableInfo(innermapdisplayerInfoForm);
								menutableInfoService.deleteLogically(menutableInfoEntity);
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
				//地図レイヤ情報テーブルは無条件に削除。
				//削除対象データの有無を取得
				//検索条件マップ
				BeanMap conditions = new BeanMap();
				Long id = Long.parseLong(innermapdisplayerInfoForm.id);
				conditions.put(maplayerInfo().id().toString(), id);
				int count = maplayerInfoService.getCount(conditions);
				List<MaplayerInfo> maplayerInfoList = new ArrayList<MaplayerInfo>();
				if(count==1){
					//削除対象が存在する場合、削除実行。
					maplayerInfoList = maplayerInfoService.findByCondition(conditions, null, null, null, null);
					//削除実行
					maplayerInfoService.deleteLogically(maplayerInfoList.get(0));
				}else{
					//削除対象が存在しない場合、他者により削除された可能性があるため削除は実行実行せず、
					//リフレッシュをユーザーに促すメッセージを返却。
					JSONObject json = new JSONObject();
					json.put(JQG_MESSAGE, lang.__("It may have been deleted by another user.\n Refresh and state latest."));
					responseService.responseJson(json);
				}

				//メニューテーブル情報テーブルは削除対象に紐付くレコードが存在している（『リスト』にチェックがあった）場合に削除。
				if(maplayerInfoList.get(0).menutableInfo != null){
					//削除実行
					menutableInfoService.deleteLogically(maplayerInfoList.get(0).menutableInfo);
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

	private MenutableInfo populateMenutableInfo(InnermapdisplayerInfoForm innermapdisplayerInfoForm){
		MenutableInfo menutableInfoEntity = Beans.createAndCopy(MenutableInfo.class, innermapdisplayerInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
		if(StringUtils.isNotEmpty(innermapdisplayerInfoForm.menutableinfoid)){
			menutableInfoEntity.id = Long.parseLong(innermapdisplayerInfoForm.menutableinfoid);
		}else{
			menutableInfoEntity.id = null;
		}
		menutableInfoEntity.addable = Boolean.parseBoolean(innermapdisplayerInfoForm.menutableinfoaddable);
		menutableInfoEntity.deletable = Boolean.parseBoolean(innermapdisplayerInfoForm.menutableinfodeletable);

		return menutableInfoEntity;
	}

	/**
     * 地図レイヤ情報とメニューテーブル情報データ格納クラス
     */
    private class InnermapdisplayerInfo{
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
		public String getTablemasterinfoid() {
			return tablemasterinfoid;
		}
		public void setTablemasterinfoid(String tablemasterinfoid) {
			this.tablemasterinfoid = tablemasterinfoid;
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
		public String getEditable() {
			return editable;
		}
		public void setEditable(String editable) {
			this.editable = editable;
		}
		public String getAddable() {
			return addable;
		}
		public void setAddable(String addable) {
			this.addable = addable;
		}
		public String getSnapable() {
			return snapable;
		}
		public void setSnapable(String snapable) {
			this.snapable = snapable;
		}
		public String getValid() {
			return valid;
		}
		public void setValid(String valid) {
			this.valid = valid;
		}
		public String getDisporder() {
			return disporder;
		}
		public void setDisporder(String disporder) {
			this.disporder = disporder;
		}
		public String getMenutableinfoid() {
			return menutableinfoid;
		}
		public void setMenutableinfoid(String menutableid) {
			this.menutableinfoid = menutableid;
		}
		public String getMenutableinfoaddable() {
			return menutableinfoaddable;
		}
		public void setMenutableinfoaddable(String menutableinfoaddable) {
			this.menutableinfoaddable = menutableinfoaddable;
		}
		public String getMenutableinfodeletable() {
			return menutableinfodeletable;
		}
		public void setMenutableinfodeletable(String menutableinfodeletable) {
			this.menutableinfodeletable = menutableinfodeletable;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getList() {
			return list;
		}
		public void setList(String list) {
			this.list = list;
		}
		private String id = "";
    	private String menuinfoid = "";
    	private String tablemasterinfoid = "";
    	private String visible = "true";
    	private String closed = "false";
    	private String editable = "false";
    	private String addable = "false";
    	private String snapable = "false";
    	private String valid = "true";
    	private String disporder = "";
    	private String menutableinfoid = "";
    	private String menutableinfoaddable = "false";
    	private String menutableinfodeletable = "false";
    	private String name = "";
    	private String list = "false";
    }
}
