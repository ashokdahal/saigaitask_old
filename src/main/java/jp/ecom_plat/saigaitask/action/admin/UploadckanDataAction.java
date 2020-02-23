package jp.ecom_plat.saigaitask.action.admin;

import static jp.ecom_plat.saigaitask.entity.Names.ckanmetadatadefaultInfo;
import static jp.ecom_plat.saigaitask.entity.Names.tablemasterInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ADMIN_LOCALGOVINFOID;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.LayerGroupInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapGroupInfo;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.CkanmetadatadefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.CkanmetadataInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.admin.UploadckanDataForm;
import jp.ecom_plat.saigaitask.service.CkanService;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.db.CkanmetadatadefaultInfoService;
import jp.ecom_plat.saigaitask.service.db.CkanmetadataInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.util.CKANUtil;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * クリアリングハウスにメタデータを登録するアクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/uploadckanData")
public class UploadckanDataAction extends AbstractAdminAction<LocalgovInfo> {

	public UploadckanDataForm uploadckanDataForm;

	/** 地方自治体情報リスト */
	public List<LocalgovInfo> localgovInfos;

	/** テーブルマスター情報リスト */
	public List<TablemasterInfo> tablemasterInfos;
	
	/** 組織の一覧 */
	public List<JSONObject> organizationList;
	
	/** ライセンス情報の一覧 */
	public List<JSONObject> licenseList;

	/** メタデータデフォルト設定情報サービス */
	@Resource
	protected CkanmetadatadefaultInfoService ckanmetadatadefaultInfoService;

	/** クリアリングハウス事前データ情報サービス */
	@Resource
	protected CkanmetadataInfoService ckanmetadataInfoService;

	/** テーブルマスタ情報サービス */
	@Resource
	protected TablemasterInfoService tablemasterInfoService;

	/** 地図マスタ情報サービス */
	@Resource
	protected MapmasterInfoService mapmasterInfoService;

	/** 地図情報サービス */
	@Resource
	protected TracktableInfoService tracktableInfoService;

	/** クリアリングハウスサービス */
	@Resource
	protected CkanService ckanService;
	@Resource
	protected TrackDataService trackDataService;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("localgovInfos", localgovInfos);
		model.put("tablemasterInfos", tablemasterInfos);
		model.put("organizationList", organizationList);
		model.put("licenseList", licenseList);
	}	

	/**
	 * 初期セットアップ
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model,
			@ModelAttribute UploadckanDataForm uploadckanDataForm) {
		Long localgovinfoid = loginDataDto.getLocalgovinfoid();
		boolean isTraining = !trackDataService.findByCurrentTrackDatas(localgovinfoid, true).isEmpty();

		// レイヤ情報を取得する
		//検索条件マップ
		BeanMap conditions = new BeanMap();
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者でなければ参照できるデータをログインした自治体(ID)で絞り込む
			conditions.put(tablemasterInfo().mapmasterInfo().localgovinfoid().toString(), localgovinfoid);
		}
		tablemasterInfos = tablemasterInfoService.findByCondition(conditions, tablemasterInfo().id().toString(), ASC, null, null);
		organizationList = CKANUtil.getOrganizetionList(ckanService.getApiKey(isTraining), isTraining);
		licenseList = CKANUtil.getLicenseList(isTraining);

		//自治体IDでメタデータデフォルト設定情報を取得する
		CkanmetadatadefaultInfo defaultInfo = getDefault(localgovinfoid);

		// メタデータに関する情報
		{

			// システム名
			uploadckanDataForm.systemName = lang.__("NIED disaster information sharing system");
			if (loginDataDto.getLocalgovInfo() != null && StringUtil.isNotEmpty(loginDataDto.getLocalgovInfo().systemname))
				uploadckanDataForm.systemName = loginDataDto.getLocalgovInfo().systemname;

			// システムURL
			uploadckanDataForm.systemUrl = request.getRequestURL().toString().replaceAll("admin/uploadckanData.*", "");

			// 言語
			uploadckanDataForm.language = lang.getLangCode();
		}

		// メタデータ/地図データに関する問い合わせ先情報
		{
			if (defaultInfo != null) {

				// 作成者
				if (StringUtil.isEmpty(uploadckanDataForm.author)) {
					uploadckanDataForm.author = defaultInfo.author;
				}

				// 作成者のメールアドレス
				if (StringUtil.isEmpty(uploadckanDataForm.authorEmail)) {
					uploadckanDataForm.authorEmail = defaultInfo.authoremail;
				}

				// メンテナー
				if (StringUtil.isEmpty(uploadckanDataForm.maintainer)) {
					uploadckanDataForm.maintainer = defaultInfo.maintainer;
				}

				// メンテナーのメールアドレス
				if (StringUtil.isEmpty(uploadckanDataForm.maintainerEmail)) {
					uploadckanDataForm.maintainerEmail = defaultInfo.maintaineremail;
				}
			}
		}
		
		uploadckanDataForm.setAuthorized(ckanService.getApiKey(isTraining) != null && organizationList != null && !organizationList.isEmpty());

		setupModel(model);
		return "/admin/uploadckanData/index";
	}

	/**
	 * 登録実行
	 *
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"}, params="insert")
	public String insert(Map<String,Object>model,
			@ModelAttribute UploadckanDataForm uploadckanDataForm, BindingResult bindingResult) {
		try {
			Long localgovinfoid = loginDataDto.getLocalgovinfoid();
			List<TrackData> trainingTrackDatas = trackDataService.findByCurrentTrackDatas(localgovinfoid, true);
			boolean isTraining = !trainingTrackDatas.isEmpty();
			String ckanurl = isTraining ? Config.getString("CKAN_URL_TRAINING") : Config.getString("CKAN_URL");

			// デフォルト情報の更新の場合
			if(uploadckanDataForm.updateDefault) {
				CkanmetadatadefaultInfo defaultInfo = getDefault(localgovinfoid);
				boolean insert = defaultInfo == null;
				if (insert) {
					defaultInfo = new CkanmetadatadefaultInfo();
					defaultInfo.localgovinfoid = localgovinfoid;
				}

				// 入力値をセット
				{
					// 作成者
					defaultInfo.author = uploadckanDataForm.author;
					// 作成者のメールアドレス
					defaultInfo.authoremail = uploadckanDataForm.authorEmail;
					// メンテナー
					defaultInfo.maintainer =uploadckanDataForm.maintainer;
					// メンテナーのメールアドレス
					defaultInfo.maintaineremail = uploadckanDataForm.maintainerEmail;
				}

				if (insert) {
					ckanmetadatadefaultInfoService.insert(defaultInfo);
				}
				else {
					ckanmetadatadefaultInfoService.update(defaultInfo);
				}
			}

			// レイヤ未選択かつ、デフォルト更新もしなかった場合.
			if (StringUtil.isEmpty(uploadckanDataForm.layerid)) {
				if (uploadckanDataForm.updateDefault == false) {
					uploadckanDataForm.error = lang.__("Target layer is required.");
					return "/admin/uploadckanData/finish";
					//throw new ServiceException(lang.__("Target layer is required."));
				}
				else {
					ActionMessages messages = new ActionMessages();
					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Meta data default info updated."), false));
					ActionMessagesUtil.addMessages(SpringContext.getRequest(), messages);
					return "/admin/uploadckanData/finish";
				}
			}

	        //テーブルマスト情報検索 --> 名称を特定
	        //optionのvalueは、「地図マスター情報．地図ID:テーブルマスター情報．レイヤID:テーブルマスター情報．ID」のレイアウト。
	        String[] ids = (uploadckanDataForm.layerid).split(":");
	        String layerId = ids[1];
	        String tablemasterId = ids[2];
	        Long tableMasterInfoId = Long.valueOf(tablemasterId);
	      	TablemasterInfo tablemasterInfo = tablemasterInfoService.findById(tableMasterInfoId);

			MapmasterInfo mapmasterInfo = mapmasterInfoService.findById(tablemasterInfo.mapmasterinfoid);
			if(mapmasterInfo==null) { throw new ServiceException(lang.__("Metadata registration error: map master info of table master info ID {0} not found.", tableMasterInfoId)); }
			int cid = mapmasterInfo.communityid;
			Long mapId = mapmasterInfo.mapid;

			// 訓練モード
			if (isTraining) {
				TrackData trackData = trainingTrackDatas.get(0);
				TracktableInfo tracktableInfo = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tableMasterInfoId, trackData.id);
				mapId = tracktableInfo.trackmapInfo.mapid;
				layerId = tracktableInfo.layerid;
			}

			try {
				// 地図をインターネットに公開する
				MapGroupInfo.insert(cid, MapGroupInfo.TARGET_INTERNET, mapId, MapGroupInfo.TYPE_OPEN);
				// レイヤをインターネットに公開する
				LayerGroupInfo.insert(cid, LayerGroupInfo.TARGET_INTERNET, layerId, LayerGroupInfo.SHARE_READ);
			}
			catch(Exception e) {
				throw new ServiceException(lang.__("Unable to set publishing settings of map or layer."), e);
			}

	      	//ファイル識別子生成
	        String mdFileID = layerId + "_" + uploadckanDataForm.infoType;

			// DBに保存されたメタデータIDがあるか確認
	        boolean update = false;
			List<CkanmetadataInfo> infos = ckanmetadataInfoService.findByLocalgovinfoid(localgovinfoid);
			if (0 < infos.size()) {
				long tablemasterinfoid = tablemasterInfo.id;
				for(CkanmetadataInfo info : infos) {
					if (info.layerid == null)
						info.layerid = tablemasterInfo.layerid;
					if (info.tablemasterinfoid.equals(tablemasterinfoid) && info.layerid.equals(layerId)) {
						mdFileID = info.name;
						update = true;
						break;
					}
				}
			}
			
			if (!update) {
				//既にクリアリングハウスに登録済かどうか確認する
				String resourceUrl = uploadckanDataForm.wms;
				//最大の（最新の）ファイル識別子を求める
				String maxId = "";
				for (int start = 1; start < 10000; start += 10) {
					String json = CKANUtil.getRecords(ckanurl, null, resourceUrl, start, 10, 1);
					JSONObject jsonObj = new JSONObject(json.replaceAll("\n", "\\n"));
					JSONObject result = jsonObj.getJSONObject("result");
					int recCnt = result.getJSONArray("results").length();
					if (recCnt == 0)
						break;
					for (int rec = 0; rec < recCnt; rec++) {
						String id = result.getJSONArray("results").getJSONObject(rec).getString("name");
						if (id.compareTo(maxId) > 0)
							maxId = id;
						update = true;
					}
				}
				//既存のメタデータを更新する
				if (update)
					mdFileID = maxId;
			}

	  		// サムネイル URL
	  		String thumbnailUrl = null;
	  		if (uploadckanDataForm.wms != null) {
	  			try {
		  			double[] bbox = new double[] {
		  				Double.parseDouble(uploadckanDataForm.minx),
		  				Double.parseDouble(uploadckanDataForm.miny),
		  				Double.parseDouble(uploadckanDataForm.maxx),
		  				Double.parseDouble(uploadckanDataForm.maxy)
		  			};
		  			thumbnailUrl = CKANUtil.thumbnailUrl(uploadckanDataForm.wms, cid, mapId, layerId, bbox, 900913);
	  			} catch (Exception e) {
	  			}
	  		}

			// 災害ID、災害名
//			List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(localgovinfoid);
//			String disasterId = null;
//			String disasterName = null;
//			if (trackDatas.size() > 0) {
//				disasterId = trackDatas.get(0).id + "";
//				disasterName = trackDatas.get(0).name;
//			}

			HashMap<String,Object> items = new HashMap<String, Object>();
	        items.put("name",			mdFileID);
	        items.put("title",			uploadckanDataForm.title);
	        items.put("isOpen",			uploadckanDataForm.isOpen);
	        items.put("infoType",		uploadckanDataForm.infoType);
	        items.put("depInfoType",	uploadckanDataForm.depInfoType);
	        items.put("systemName",		uploadckanDataForm.systemName);
	        items.put("systemUrl",		uploadckanDataForm.systemUrl);
	        items.put("language",		uploadckanDataForm.language);
	        items.put("maxx",			uploadckanDataForm.maxx);
	        items.put("maxy",			uploadckanDataForm.maxy);
	        items.put("minx",			uploadckanDataForm.minx);
	        items.put("miny",			uploadckanDataForm.miny);
	        items.put("spatial",		uploadckanDataForm.spatial);
	        items.put("abstr",			uploadckanDataForm.abstr);
	        items.put("tags",			uploadckanDataForm.tags);
	        items.put("licenseInfo",	uploadckanDataForm.licenseInfo);
	        items.put("organization",	uploadckanDataForm.organization);
	        items.put("author",			uploadckanDataForm.author);
	        items.put("authorEmail",	uploadckanDataForm.authorEmail);
	        items.put("metadataCreated",uploadckanDataForm.metadataCreated);
	        items.put("maintainer",		uploadckanDataForm.maintainer);
	        items.put("maintainerEmail",uploadckanDataForm.maintainerEmail);
	        items.put("version",		uploadckanDataForm.version);
	        items.put("exTemp",			uploadckanDataForm.exTemp);
	        items.put("wms",			uploadckanDataForm.wms);
	        items.put("wfs",			uploadckanDataForm.wfs);
	       	items.put("thumnailUrl",	thumbnailUrl);
//	       	items.put("disasterId",		disasterId);
//	       	items.put("disasterName",	disasterName);

			//完了画面に表示するためのデータ
	       	uploadckanDataForm.mdFileID 	= mdFileID;
			uploadckanDataForm.update		= update;

			boolean ret = false;
			try {
				ret = CKANUtil.uploadMetadata(ckanurl, ckanService.getApiKey(isTraining), items, update, isTraining);
				if (ret == false) {
					if (items.get("error") != null) {
						uploadckanDataForm.error = (String)items.get("error");
						return "/admin/uploadckanData/finish";
					}
					throw new ServiceException("Upload metadata failed.");
				}
			} catch(Exception e) {
				throw new ServiceException(lang.__("Failed to register meta data into clearing house for production."+"\n"+e.getMessage()), e);
			}

			//外部クリアリングハウスへ正常に登録できた場合、クリアリングハウス事前データ情報（clearinghousemetadata_info）へレコード挿入する。
			if (ret) {
				CkanmetadataInfo entity = new CkanmetadataInfo();
				if (update) {
					BeanMap conds = new BeanMap();
					conds.put(Names.ckanmetadataInfo().name().toString(), mdFileID);
					List<CkanmetadataInfo> results = ckanmetadataInfoService.findByCondition(conds);
					if (results.size() > 0)
						entity = results.get(0);
					else
						update = false;
				}
				entity.name					= mdFileID;
				entity.localgovinfoid 		= localgovinfoid;
				entity.tablemasterinfoid 	= tableMasterInfoId;
				entity.infotype 			= (String)items.get("infoType");
				entity.title				= (String)items.get("title");
				entity.note					= (String)items.get("abstr");
				entity.ownerorg				= (String)items.get("organization");
				entity.ownerorgtitle		= uploadckanDataForm.organizationTitle;
				entity.tags					= (String)items.get("tags");
				entity.layerid				= layerId;

				if (update)
					ckanmetadataInfoService.update(entity);
				else
					ckanmetadataInfoService.insert(entity);

				// メタデータ登録時にすでに災害が起動している場合はもう一度メタデータ更新する ???
			}

		}
		catch(Exception e) {
			logger.error(loginDataDto.logInfo()+ "\n" + e.getMessage(), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
			logger.error("UploadCkan failed: "+e.getMessage(), e);
			setupModel(model);
			return "/admin/uploadckanData/index";
		}
		return "/admin/uploadckanData/finish";
	}

	protected CkanmetadatadefaultInfo getDefault(Long localgovinfoid) {
		BeanMap conditions = new BeanMap();
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者でなければ参照できるデータをログインした自治体(ID)で絞り込む
			conditions.put(ckanmetadatadefaultInfo().localgovinfoid().toString(), localgovinfoid);
		}
		List<CkanmetadatadefaultInfo> defaults = ckanmetadatadefaultInfoService.findByCondition(conditions, ckanmetadatadefaultInfo().id().toString(), ASC, null, null);

		return 0<defaults.size() ? defaults.get(0) : null;
	}

	/**
	 * 対象レイヤプルダウンの値変更
	 *
	 * @return 表示ページ
	 * @throws IOException
	 */
	@RequestMapping(value="/changevalue")
	@ResponseBody
	public ResponseEntity<String> changevalue() throws IOException {

		final HttpHeaders httpHeaders= new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		MapDB mapDB = MapDB.getMapDB();
		// マップIDをパラメータから取得
		Long mapId = Long.valueOf(request.getParameter("mapId"));
		MapInfo mapInfo = mapDB.getMapInfo(mapId);

		int cid = mapInfo.communityId;

		// レイヤIDをパラメータから取得
		String layerId = (String) request.getParameter("layerId");
		TablemasterInfo tablemasterInfo = tablemasterInfoService.findByLayerId(layerId);

		// 訓練モード
		boolean isTraining = false;
		List<TrackData> trainingTrackDatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), true);
		if (0 < trainingTrackDatas.size()) {
			long tablemasterInfoId = Long.valueOf(request.getParameter("id"));
			TrackData trackData = trainingTrackDatas.get(0);
			TracktableInfo tracktableInfo = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tablemasterInfoId, trackData.id);
			mapId = tracktableInfo.trackmapInfo.mapid;
			layerId = tracktableInfo.layerid;
			isTraining = true;
		}

		String ckanurl = isTraining ? Config.getString("CKAN_URL_TRAINING") : Config.getString("CKAN_URL");
		String wms = ckanService.getWMSURL(request, mapId, layerId);
		String wfs = ckanService.getWFSURL(request, cid, mapId, layerId);

		// ユーザには infinity 形式で表示
		String[] exTemp = ckanService.getExTemp(layerId);
		if(ExMapDB.isNegativeInfinity(exTemp[0])) exTemp[0] = "-infinity";
		if(ExMapDB.isPositiveInfinity(exTemp[0])) exTemp[0] = "infinity";
		if(ExMapDB.isNegativeInfinity(exTemp[1])) exTemp[1] = "-infinity";
		if(ExMapDB.isPositiveInfinity(exTemp[1])) exTemp[1] = "infinity";
		if(exTemp[0]==null && exTemp[1]!=null) exTemp[0] = "-infinity";
		if(exTemp[1]==null && exTemp[0]!=null) exTemp[1] = "infinity";

		Long localgovinfoid = loginDataDto.getLocalgovinfoid();

		// 既にクリアリングハウスに登録済かどうか確認する
		boolean isRegistered = false;
		String metadataid = null;

		// DBに保存されたメタデータIDがあるか確認
		List<CkanmetadataInfo> infos = ckanmetadataInfoService.findByLocalgovinfoid(localgovinfoid);
		if (0 < infos.size()) {
			if (tablemasterInfo != null) {
				long tablemasterinfoid = tablemasterInfo.id;
				for (CkanmetadataInfo info : infos) {
					if (info.layerid == null)
						info.layerid = tablemasterInfo.layerid;
					if (info.tablemasterinfoid.equals(tablemasterinfoid) && info.layerid.equals(layerId)) {
						isRegistered = true;
						metadataid = info.name;
						break;
					}
				}
			}
		}
		
		// DBに保存されたメタデータIDがなければ、クリアリングハウスから同じWMSURLがあるか確認
		if (isRegistered == false) {
			try {
				String json = CKANUtil.getRecords(ckanurl, null, wms, 1, 1, 1);
				JSONObject jsonObj;
				jsonObj = new JSONObject(json.replaceAll("\n", "\\n"));
				JSONObject result = jsonObj.getJSONObject("result");
				isRegistered = result.getInt("count") > 0;
				if (isRegistered) {
					JSONArray record = result.getJSONArray("results");
					if (0 < record.length()) {
						metadataid = record.getJSONObject(0).getString("name");
					}
				}
			} catch (JSONException e) {
				logger.error(e.getMessage(), e);
			}
		}

		JSONObject obj = new JSONObject();
		try {
			if (isRegistered && StringUtil.isNotEmpty(metadataid)) {
				obj = ckanService.getRecordById(metadataid, isTraining);

				// 作成日／修正日を整形する
				SimpleDateFormat df = new SimpleDateFormat(lang.__("yyyy/M/d H:m（zzz）"));
				String created = obj.getString("metadataCreated");
				if (StringUtil.isNotEmpty(created)) {
					obj.put("createdFormatted", df.format(DatatypeConverter.parseDateTime(created + "Z").getTime()));
				}
				String modified = obj.getString("metadataModified");
				if (StringUtil.isNotEmpty(modified)) {
					obj.put("modifiedFormatted", df.format(DatatypeConverter.parseDateTime(modified + "Z").getTime()));
				}
			}
			else {
				isRegistered = false;
				CkanmetadatadefaultInfo defaultInfo = getDefault(localgovinfoid);

				// メタデータに関する情報
				{
					// 公開・非公開
					obj.put("isOpen", "1");

					// 情報種別（UUID）
					obj.put("infoType", UUID.randomUUID().toString());

					// システム名
					obj.put("systemName", lang.__("NIED disaster information sharing system"));
					if (loginDataDto.getLocalgovInfo() != null && StringUtil.isNotEmpty(loginDataDto.getLocalgovInfo().systemname))
						obj.put("systemName", loginDataDto.getLocalgovInfo().systemname);

					// システムURL
					obj.put("systemUrl", request.getRequestURL().toString().replaceAll("admin/uploadckanData.*", ""));

					// 言語
					obj.put("language", lang.getLangCode());
					
					// 地図情報
					CKANUtil.getSpatial(mapId, layerId, obj);
				}

				// 地図データに関する情報
				{
					// タイトル
					obj.put("title", (isTraining ? lang.__("[Training]") : "") + tablemasterInfo.name);
				}

				// メタデータ/地図データに関する問い合わせ先情報
				{
					if (defaultInfo != null) {

						// 作成者
						obj.put("author", defaultInfo.author);
						// 作成者のメールアドレス
						obj.put("authorEmail", defaultInfo.authoremail);
						// メンテナー
						obj.put("maintainer", defaultInfo.maintainer);
						// メンテナーのメールアドレス
						obj.put("maintainerEmail", defaultInfo.maintaineremail);
					}
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		
		// プロパティを登録
		try {
			obj.put("mapId", mapId);
			obj.put("layerId", layerId);
			obj.put("wms", wms);
			obj.put("wfs", wfs);
			obj.put("exTemp", exTemp);
			obj.put("registered", isRegistered);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

		// 出力の準備
		return new ResponseEntity<String>(obj.toString(), httpHeaders, HttpStatus.OK);
	}

}
