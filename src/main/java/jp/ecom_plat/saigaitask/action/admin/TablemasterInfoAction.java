/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;

import static jp.ecom_plat.saigaitask.util.Constants.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.postgis.PGgeometry;
import org.seasar.extension.jdbc.name.PropertyName;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.exception.SQLRuntimeException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;

import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.MGRSCoord;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.util.GeometryUtils;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.db.TablemasterInfoForm;
import jp.ecom_plat.saigaitask.service.EdituserAttrService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.TrackdataidAttrService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.EcommapDataGetService;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.JqGridUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * テーブルマスター情報のアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/tablemasterInfo")
public class TablemasterInfoAction extends AbstractAdminAction<TablemasterInfo> {
	protected TablemasterInfoForm tablemasterInfoForm;

	/** サービスクラス */
	@Resource
	protected TablemasterInfoService tablemasterInfoService;

	/** サービスクラス */
	@Resource
	protected MapmasterInfoService mapmasterInfoService;

	/** サービスクラス */
	@Resource
	protected EcommapDataGetService ecommapDataGetService;

	/** サービスクラス */
	@Resource
	protected TableService tableService;

	@Resource protected TrackDataService trackDataService;
	@Resource protected TracktableInfoService tracktableInfoService;
	@Resource protected TrackdataidAttrService trackdataidAttrService;
	@Resource protected EdituserAttrService edituserAttrService;
	@Resource protected TrackmapInfoService trackmapInfoService;

	/**  最適化 */
	public static final String JQG_OPER_OPTIMIZE = "optimize";

	/**
	 * 一覧ページ表示
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index() {
		return "/admin/tablemasterInfo/index";
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
			int count = tablemasterInfoService.getCount(conditions);

			// 一覧内容取得
			List<TablemasterInfo> list = new ArrayList<TablemasterInfo>();
			if(count > 0){
				list = tablemasterInfoService.findByCondition(conditions, sidx, sord, limit, (page-1)*limit);
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
	public String jqgridedit(@ModelAttribute TablemasterInfoForm tablemasterInfoForm) throws ServiceException {

		this.tablemasterInfoForm = tablemasterInfoForm;
		
		try{
			//操作種別取得
			String oper = (String)request.getParameter(JQG_OPER);
			if(StringUtils.isEmpty(oper)){
				throw new IllegalStateException(lang.__("There is no operation type."));
			}

			//操作種別に応じたアクションを実行
			TablemasterInfo entity;
			//登録
			if(JQG_OPER_ADD.equals(oper)) {
				entity = Beans.createAndCopy(TablemasterInfo.class, tablemasterInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
				//登録実行
				tablemasterInfoService.insert(entity);

				// 記録テーブル情報の更新
				MapmasterInfo mapmasterInfo = mapmasterInfoService.findById(entity.mapmasterinfoid);
				if(mapmasterInfo!=null) {
					// 対象のテーブルマスタ
					List<TablemasterInfo> tablelist = new ArrayList<TablemasterInfo>();
					tablelist.add(entity);
					// 更新対象の自治体ID
					long localgovinfoid = mapmasterInfo.localgovinfoid;
					List<TrackData> trackDatas = trackDataService.findByLocalgovinfoid(localgovinfoid);
					for(TrackData trackData : trackDatas) {
						long trackmapinfoid = trackData.trackmapinfoid;

						// 登録済みかチェック
						BeanMap conditions = new BeanMap();
						conditions.put("trackmapinfoid", trackmapinfoid);
						conditions.put("tablemasterinfoid", entity.id);
						List<TracktableInfo> tracktableInfos = tracktableInfoService.findByCondition(conditions);
						if(tracktableInfos.size()==0) {
							// 未登録の場合は登録する
							trackDataService.insertTracktableInfo(trackmapinfoid, tablelist);
						}
					}
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
					TablemasterInfo tablemasterInfo = tablemasterInfoService.findById(Long.decode(tablemasterInfoForm.id));

					//結果返却用JSON
					JSONObject json = new JSONObject();
					if(tablemasterInfo != null){
						//編集対象が存在する場合、実行。
						//現時点の編集対象データが他者により変更されているかを判定
						if(JqGridUtil.existsNoChangeByOther(serializedPreEditData, TablemasterInfo.class, tablemasterInfo)){
							//変更されていない場合、更新
							entity = Beans.createAndCopy(TablemasterInfo.class, tablemasterInfoForm).dateConverter(DATE_FORMAT_PATTERN).execute();
							PropertyName<?>[] updateExcludesAry = null;
							List<PropertyName<?>> updateExcludesList = new ArrayList<PropertyName<?>>();
							//更新実行
							tablemasterInfoService.update(entity, updateExcludesAry);
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
				conditions.put("id", Integer.parseInt(tablemasterInfoForm.id));
				int count = tablemasterInfoService.getCount(conditions);
				if(count==1){
					//削除対象が存在する場合、削除実行。
					entity = tablemasterInfoService.findById(Long.decode(tablemasterInfoForm.id));
					//削除実行
					try{
						tablemasterInfoService.deleteLogically(entity);
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
			//最適化
			}else if(JQG_OPER_OPTIMIZE.equals(oper)) {
				int count = setFeatureCoordinates();
				responseService.response("{\"count\":" + count + "}");
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
     * eコミマップからのデータ取得
     * @return null
     * @throws Exception
     */
	@RequestMapping(value="/jqgridecommap/{parentgrid_refkey}/{parentId}")
	@ResponseBody
    public String jqgridecommap(
				@PathVariable("parentgrid_refkey") String parentgridRefkey,
				@PathVariable String parentId,
				@ModelAttribute TablemasterInfoForm tablemasterInfoForm) throws ServiceException {
		try{

			//親のIDの取得
//			String parentgridRefkey = (String)request.getParameter("parentgrid_refkey");
//			String parentId = (String)request.getParameter("parentId");

			MapmasterInfo mapmasterInfo = mapmasterInfoService.findById(Long.parseLong(parentId));


			//検索条件マップ
			BeanMap conditions = new BeanMap();

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

			//ユーザーデータ取得
	        Map<String, String> userdata = new HashMap<String, String>();
	        //表示順取得
	        userdata.put("maxDisporder", "10000");

	        // 返却リスト
	        ArrayList<ResultGetEcommMap> results= new ArrayList<ResultGetEcommMap>();

        	// 地図レイヤ情報取得
            Map<LayerInfo,String []> mapLayerInfos;
            mapLayerInfos = ecommapDataGetService.getMapUsedLayersforTablemasterInfo(mapmasterInfo.communityid, mapmasterInfo.mapgroupid, mapmasterInfo.mapid);

	  	    // 一覧件数取得
	  	    count = mapLayerInfos.size();

	  	    //ページャー関連パラメータ再計算
	  	    if( count >0 ) {
	  	        totalPages = (int)Math.ceil((double)count/rows);
	  	    }
	  	    if (page > totalPages){
	  	        page = totalPages;
	  	    }

	  	    List<LayerInfo> mapLayerInfoList = new ArrayList<LayerInfo>(mapLayerInfos.keySet());
	  	    if (limit != NON_LIMIT)
	  	    	mapLayerInfoList = mapLayerInfoList.subList((page - 1) * limit, Math.min(page * limit, count));
            for(LayerInfo layerInfo : mapLayerInfoList){
            	ResultGetEcommMap result = new ResultGetEcommMap();
            	result.setCommunityInfo(mapmasterInfo.communityid.toString()+":"+ecommapDataGetService.getCommunityName(mapmasterInfo.communityid));
            	result.setGroupInfo(mapmasterInfo.mapgroupid.toString()+":"+ecommapDataGetService.getGroupName(mapmasterInfo.communityid, mapmasterInfo.mapgroupid));
            	result.setMapInfo(mapmasterInfo.mapid.toString()+":"+ecommapDataGetService.getMapName(mapmasterInfo.mapid));
            	result.setLayerInfo(layerInfo.layerId + ":" + layerInfo.name);
            	result.setGeometryType(layerInfo.getGeometryType());
            	String [] attrIds = mapLayerInfos.get(layerInfo);
            	if(attrIds[0] != null){
            		result.setAddressColumn(attrIds[0]);
            	}else{
            		result.setAddressColumn("");
            	}

            	if(attrIds[1] != null){
            		result.setUpdateColumn(attrIds[1]);
            	}else{
            		result.setUpdateColumn("");
            	}

            	if(attrIds[2] != null){
            		result.setCoordinateColumn(attrIds[2]);
            	}else{
            		result.setCoordinateColumn("");
            	}
            	results.add(result);
            }

	      //返却値セット
	      JSONObject json = new JSONObject();
	      json.put(JQG_PAGER_PAGE, page); //現在ページ番号
	      json.put(JQG_PAGER_RECORDS, count); //総結果件数
	      json.put(JQG_PAGER_TOTAL, totalPages);  //総ページ数
	      json.put(JQG_USERDATA, userdata); //ユーザーデータ
	      //json.put(JQG_PAGER_ROWS, StringUtil.jsonForCyclicalReferences(list));   //結果一覧
	      json.put("rows", JSONArray.fromObject(results));   //結果一覧

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
	 * PGobject または PGgeometry オブジェクトより  Geometry オブジェクトを取得する
	 *
	 * @param obj
	 * @return
	 * @throws ParseException
	 */
	private Geometry getGeom(Object obj) throws ParseException {
		if (obj instanceof PGgeometry)
			return GeometryUtils.getGeometryFromWKT(obj.toString().replaceAll("^.*;", ""));
		else
			return GeometryUtils.getGeometryFromWKB(obj.toString());
	}

	/**
	 * 全地物の座標とＵＴＭを設定する
	 * マスタマップと現在の災害マップのレイヤに trackdataid, st_edituser属性を追加する。
	 *
	 * @return 更新件数
	 * @throws Exception
	 */
	private int setFeatureCoordinates() throws Exception {
		int count = 0;

		MapmasterInfo mapmasterInfo = mapmasterInfoService.findById(new Long(tablemasterInfoForm.mapmasterinfoid));
		List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(mapmasterInfo.localgovinfoid);

		List<TablemasterInfo> tableList = tablemasterInfoService.findByMapmasterInfoId(mapmasterInfo.id);
		for (TablemasterInfo master : tableList) {
			if (StringUtils.isNotEmpty(master.layerid) ) {

				// マスタのレイヤIDと現在起動中の災害マップのレイヤIDを追加する。
				List<String> layerIds = new ArrayList<>();
				layerIds.add(master.layerid);
				if(0<trackDatas.size()) {
					Long trackmapinfoid = trackDatas.get(0).trackmapinfoid;
					TracktableInfo tracktableInfo = tracktableInfoService.findByTrackmapInfoIdAndTablemasterinfoid(trackmapinfoid, master.id);
					if(tracktableInfo!=null) {
						if(!layerIds.contains(tracktableInfo.layerid)) {
							layerIds.add(tracktableInfo.layerid);
						}
					}
				}

				// 各レイヤに対しての最適化
				for(String layerId : layerIds) {
					// 属性ID trackdataid が未追加なら追加する
					trackdataidAttrService.alterTableAddTrackdataidColumnIfNotExists(layerId);
					// 属性ID st_edituser が未追加なら追加する
					edituserAttrService.alterTableAddEdituserColumnIfNotExists(layerId);
				}

				if((StringUtils.isNotEmpty(master.coordinatecolumn) || StringUtils.isNotEmpty(master.mgrscolumn))) {
					List<BeanMap> featureList = tableService.selectAll(master.layerid);
					for (Map<String, Object> feature : featureList) {
						Object theGeom = feature.get("theGeom");
						if(theGeom != null){
							// 座標
							if (StringUtils.isNotEmpty(master.coordinatecolumn)) {
								String value = (String)feature.get(master.coordinatecolumn);
								if (StringUtils.isEmpty(value)) {
									Geometry geom = getGeom(theGeom);
									Long gid = (Long)feature.get("gid");
									Point pt = geom.getCentroid();
									DecimalFormat df = new DecimalFormat("0.00000");
									String xy = "("+df.format(pt.getX())+","+ df.format(pt.getY())+")";
									tableService.update(master.layerid, master.coordinatecolumn, "gid", gid, xy);
									count++;
								}
							}

							//　UTM
							if (StringUtils.isNotEmpty(master.mgrscolumn)) {
								String value = (String)feature.get(master.mgrscolumn);
								if (StringUtils.isEmpty(value)) {
									Geometry geom = getGeom(theGeom);
									Long gid = (Long)feature.get("gid");
									Point pt = geom.getCentroid();
									Angle lonAngle = Angle.fromDegreesLongitude(pt.getX());
									Angle latAngle = Angle.fromDegreesLatitude(pt.getY());
									MGRSCoord mgrsCoord = MGRSCoord.fromLatLon(latAngle, lonAngle, master.mgrsdigit);
									tableService.update(master.layerid, master.mgrscolumn, "gid", gid, mgrsCoord.toString());
									count++;
								}
							}
						}
					}
				}
			}
		}
		return count;
	}


	/**
     * eコミマップからのデータ取得結果格納クラス
     */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/tablemasterInfo")
    public class ResultGetEcommMap{
    	private  String  communityInfo;
    	private  String  groupInfo;
    	private  String mapInfo;
    	private  String layerInfo;
    	private  String geometryType;
    	private  String addressColumn;
    	private  String updateColumn;
    	private  String coordinateColumn;

    	public void setCommunityInfo(String communityInfo){
    		this.communityInfo = communityInfo;
    	}
    	public void setGroupInfo(String groupInfo){
    		this.groupInfo = groupInfo;
    	}
    	public void setMapInfo(String mapInfo){
    		this.mapInfo = mapInfo;
    	}
    	public void setLayerInfo(String layerInfo){
    		this.layerInfo = layerInfo;
    	}
    	public void setGeometryType(String geometryType){
    		this.geometryType = geometryType;
    	}
    	public void setAddressColumn(String addressColumn){
    		this.addressColumn = addressColumn;
    	}
    	public void setUpdateColumn(String updateColumn){
    		this.updateColumn = updateColumn;
    	}
    	public void setCoordinateColumn(String coordinateColumn){
    		this.coordinateColumn = coordinateColumn;
    	}


    	public String getCommunityInfo(){
    		return this.communityInfo;
    	}
    	public String getGroupInfo(){
    		return this.groupInfo;
    	}
    	public String getMapInfo(){
    		return this.mapInfo;
    	}
    	public String getLayerInfo(){
    		return this.layerInfo;
    	}
    	public String getGeometryType(){
    		return this.geometryType;
    	}
    	public String getAddressColumn(){
    		return this.addressColumn;
    	}
    	public String getUpdateColumn(){
    		return this.updateColumn;
    	}
    	public String getCoordinateColumn(){
    		return this.coordinateColumn;
    	}
    }
}

