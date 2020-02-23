/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin;


import static jp.ecom_plat.saigaitask.entity.Names.clearinghousemetadatadefaultInfo;
import static jp.ecom_plat.saigaitask.entity.Names.tablemasterInfo;
import static jp.ecom_plat.saigaitask.util.Constants.ADMIN_LOCALGOVINFOID;
import static jp.ecom_plat.saigaitask.util.Constants.ASC;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
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
import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadataInfo;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadatadefaultInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.form.admin.UploadclearinghouseDataForm;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.db.ClearinghousemetadataInfoService;
import jp.ecom_plat.saigaitask.service.db.ClearinghousemetadatadefaultInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.util.CSWUtil;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.SpringContext;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * クリアリングハウスにメタデータを登録するアクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/uploadclearinghouseData")
public class UploadclearinghouseDataAction extends AbstractAdminAction<LocalgovInfo> {

	public UploadclearinghouseDataForm uploadclearinghouseDataForm;

	/** 地方自治体情報リスト */
	public List<LocalgovInfo> localgovInfos;

	/** テーブルマスター情報リスト */
	public List<TablemasterInfo> tablemasterInfos;

	/** メタデータデフォルト設定情報サービス */
	@Resource
	protected ClearinghousemetadatadefaultInfoService clearinghousemetadatadefaultInfoService;

	/** クリアリングハウス事前データ情報サービス */
	@Resource
	protected ClearinghousemetadataInfoService clearinghousemetadataInfoService;

	/** テーブルマスタ情報サービス */
	@Resource
	protected TablemasterInfoService tablemasterInfoService;

	/** 地図マスタ情報サービス */
	@Resource
	protected MapmasterInfoService mapmasterInfoService;

	/** 地図情報サービス */
	@Resource
	protected TrackmapInfoService trackmapInfoService;

	/** クリアリングハウスサービス */
	@Resource
	protected ClearinghouseService clearinghouseService;

	@Resource protected TrackDataService trackDataService;
	
	// クリアリングハウス検索で CKAN を利用する
	protected final boolean useCkan = !Config.getString("CKAN_URL", "").isEmpty();

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("localgovInfos", localgovInfos);
		model.put("tablemasterInfos", tablemasterInfos);
	}	

	/**
	 * 初期セットアップ
	 *
	 * @return 表示ページ
	 */
	@RequestMapping(value={"","/index"})
	public String index(Map<String,Object>model,
			@ModelAttribute UploadclearinghouseDataForm uploadclearinghouseDataForm) {
		
		// CKAN を利用する
		if (loginDataDto.isUseCkan()) {
			return "forward:/admin/uploadckanData";
		}

		Long localgovinfoid = loginDataDto.getLocalgovinfoid();

		// レイヤ情報を取得する
		//検索条件マップ
		BeanMap conditions = new BeanMap();
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者でなければ参照できるデータをログインした自治体(ID)で絞り込む
			conditions.put(tablemasterInfo().mapmasterInfo().localgovinfoid().toString(), localgovinfoid);
		}
		tablemasterInfos = tablemasterInfoService.findByCondition(conditions, tablemasterInfo().id().toString(), ASC, null, null);

		//自治体IDでメタデータデフォルト設定情報を取得する
		ClearinghousemetadatadefaultInfo defaultInfo = getDefault(localgovinfoid);
		if (defaultInfo!=null) {
			// 地図データに関する情報
			{
				// 接頭語
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.prefix)) {
					uploadclearinghouseDataForm.prefix = defaultInfo.prefix;
				}
				// 接尾語
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.suffix)) {
					uploadclearinghouseDataForm.suffix = defaultInfo.suffix;
				}
			}

			// メタデータ/地図データに関する問い合わせ先情報
			{
				// 問合せ先
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.organizationName)) {
					uploadclearinghouseDataForm.organizationName = defaultInfo.reference;
				}
				// 郵便番号
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.postCode)) {
					uploadclearinghouseDataForm.postCode= defaultInfo.postcode;
				}
				// 都道府県名
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.adminArea)) {
					uploadclearinghouseDataForm.adminArea = defaultInfo.adminarea;
				}
				// 市区町村
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.city)) {
					uploadclearinghouseDataForm.city = defaultInfo.city;
				}
				// 都道府県コード
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.adminAreaCode)) {
					uploadclearinghouseDataForm.adminAreaCode = defaultInfo.adminareacode;
				}
				// 市区町村コード
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.cityCode)) {
					uploadclearinghouseDataForm.cityCode = defaultInfo.citycode;
				}
				// 町名、番地、ビル名等
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.delPoint)) {
					uploadclearinghouseDataForm.delPoint = defaultInfo.deliverypoint;
				}
				// 電話番号
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.voice)) {
					uploadclearinghouseDataForm.voice = defaultInfo.telno;
				}
				// 電子メールアドレス
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.eMailAdd)) {
					uploadclearinghouseDataForm.eMailAdd = defaultInfo.email;
				}
				// 問い合わせ先のHP等のURL
				if(StringUtil.isEmpty(uploadclearinghouseDataForm.linkage)) {
					uploadclearinghouseDataForm.linkage = defaultInfo.linkage;
				}
			}
		}

		if(StringUtil.isEmpty(uploadclearinghouseDataForm.adminAreaCode)) {
			uploadclearinghouseDataForm.adminAreaCode = loginDataDto.getLocalgovInfo().prefcode;
		}
		if(StringUtil.isEmpty(uploadclearinghouseDataForm.cityCode)) {
			uploadclearinghouseDataForm.cityCode = loginDataDto.getLocalgovInfo().citycode;
		}

		//WMS
		uploadclearinghouseDataForm.wms = "";
		//WFS
		uploadclearinghouseDataForm.wfs = "";

		setupModel(model);
		return "/admin/uploadclearinghouseData/index";
	}


//	/**
//	 * 削除実行
//	 *
//	 * @return 遷移ページ
//	 */
//	public String delete() {
//		String mdFileID = "SAIGAITASK_2_20131018174644";
//		boolean ret = CSWUtil.deleteMetadata(mdFileID);
//		logger.error("--- UploadclearinghouseDataAction.delete, mdFileID:" + mdFileID + ", ret:" + ret);
//		return "/admin/uploadclearinghouseData";
//	}

	/**
	 * 登録実行
	 *
	 * @return 遷移ページ
	 */
	@RequestMapping(value={"","/index"}, params="insert")
	public String insert(Map<String,Object>model,
			@ModelAttribute UploadclearinghouseDataForm uploadclearinghouseDataForm, BindingResult bindingResult) {
		try {
			Long localgovinfoid = loginDataDto.getLocalgovinfoid();

			// デフォルト情報の更新の場合
			if(uploadclearinghouseDataForm.updateDefault) {
				ClearinghousemetadatadefaultInfo defaultInfo = getDefault(localgovinfoid);
				boolean insert = defaultInfo==null;
				if(insert) {
					defaultInfo = new ClearinghousemetadatadefaultInfo();
					defaultInfo.localgovinfoid = localgovinfoid;
				}

				// 入力値をセット
				{
					// 地図データに関する情報
					{
						// 接頭語
						defaultInfo.prefix = uploadclearinghouseDataForm.prefix;
						// 接尾語
						defaultInfo.suffix = uploadclearinghouseDataForm.suffix;
					}

					// メタデータ/地図データに関する問い合わせ先情報
					{
						// 問合せ先
						defaultInfo.reference = uploadclearinghouseDataForm.organizationName;
						// 郵便番号
						defaultInfo.postcode = uploadclearinghouseDataForm.postCode;
						// 都道府県名
						defaultInfo.adminarea =uploadclearinghouseDataForm.adminArea;
						// 市区町村
						defaultInfo.city = uploadclearinghouseDataForm.city;
						// 都道府県コード
						defaultInfo.adminareacode = uploadclearinghouseDataForm.adminAreaCode;
						// 市区町村コード
						defaultInfo.citycode = uploadclearinghouseDataForm.cityCode;
						// 町名、番地、ビル名等
						defaultInfo.deliverypoint = uploadclearinghouseDataForm.delPoint;
						// 電話番号
						defaultInfo.telno = uploadclearinghouseDataForm.voice;
						// 電子メールアドレス
						defaultInfo.email = uploadclearinghouseDataForm.eMailAdd;
						// 問い合わせ先のHP等のURL
						defaultInfo.linkage = uploadclearinghouseDataForm.linkage;
					}
				}

				if(insert) {
					clearinghousemetadatadefaultInfoService.insert(defaultInfo);
				}
				else {
					clearinghousemetadatadefaultInfoService.update(defaultInfo);
				}
			}

			// レイヤ未選択かつ、デフォルト更新もしなかった場合.
			if(StringUtil.isEmpty(uploadclearinghouseDataForm.layerid)) {
				if(uploadclearinghouseDataForm.updateDefault==false) {
					throw new ServiceException(lang.__("Target layer is required."));
				}
				else {
					ActionMessages messages = new ActionMessages();
					messages.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Meta data default info updated."), false));
					ActionMessagesUtil.addMessages(SpringContext.getRequest(), messages);
					return "/admin/uploadclearinghouseData/index";
				}
			}

			//現在の日付けを取得
			SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHHmmss");
	        dateFormatGmt.setTimeZone(TimeZone.getDefault());
	        String now = dateFormatGmt.format(new Date());

	        //テーブルマスト情報検索 --> 名称を特定
	        //optionのvalueは、「地図マスター情報．地図ID:テーブルマスター情報．レイヤID:テーブルマスター情報．ID」のレイアウト。
	        String[] ids = (uploadclearinghouseDataForm.layerid).split(":");
	        //String _mapId = ids[0];
	        String _layerId = ids[1];
	        String _tablemasterId = ids[2];
	        Long tableMasterInfoId = Long.valueOf(_tablemasterId);
	        String layerName = "";
	      	TablemasterInfo tablemasterInfo = tablemasterInfoService.findById(tableMasterInfoId);
	  		layerName = tablemasterInfo.name;

			MapmasterInfo mapmasterInfo = mapmasterInfoService.findById(tablemasterInfo.mapmasterinfoid);
			if(mapmasterInfo==null) { throw new ServiceException(lang.__("Metadata registration error: map master info of table master info ID {0} not found.", tableMasterInfoId)); }
			int cid = mapmasterInfo.communityid;
			Long mapId = mapmasterInfo.mapid;
			try {
				// 地図をインターネットに公開する
				MapGroupInfo.insert(cid, MapGroupInfo.TARGET_INTERNET, mapId, MapGroupInfo.TYPE_OPEN);
				// レイヤをインターネットに公開する
				LayerGroupInfo.insert(cid, LayerGroupInfo.TARGET_INTERNET, _layerId, LayerGroupInfo.SHARE_READ);
			}
			catch(Exception e) {
				throw new ServiceException(lang.__("Unable to set publishing settings of map or layer."), e);
			}

	      	//ファイル識別子生成
	        String mdFileID 		= "SAIGAITASK_" + localgovinfoid + "_" + now;


			// DBに保存されたメタデータIDがあるか確認
	        boolean update = false;
			List<ClearinghousemetadataInfo> infos = clearinghousemetadataInfoService.findByLocalgovinfoid(localgovinfoid);
			if(0<infos.size()) {
				long tablemasterinfoid = tablemasterInfo.id;
				for(ClearinghousemetadataInfo info : infos) {
					if(info.tablemasterinfoid.equals(tablemasterinfoid)) {
						mdFileID = info.metadataid;
						update = true;
						break;
					}
				}
			}
			
			if(update==false) {
				//既にクリアリングハウスに登録済かどうか確認する
				HashMap<String, String> conditions = new HashMap<String, String>();
				conditions.put("RESOURCEURL", uploadclearinghouseDataForm.wms.replaceAll("&", "&amp;"));
				//最大の（最新の）ファイル識別子を求める
				String maxId = "";
				//boolean update = false;
				for (int start = 1; start < 10000; start += 10) {
					String xml = CSWUtil.getRecords(conditions, start, 10, 1);
					String json = CSWUtil.GetRecordsResponseToJSON(xml);
					JSONObject jsonObj = JSONObject.fromObject(json.replaceAll("\n", "\\n"));
					if (jsonObj.get("numReturn").equals("0"))
						break;
					int recCnt = Integer.parseInt((String)jsonObj.get("numReturn"));
					for (int rec = 0; rec < recCnt; rec++) {
						String id = (String)((JSONObject)((JSONArray)jsonObj.get("record")).get(rec)).get("fileIdentifier");
						if (id.compareTo(maxId) > 0)
							maxId = id;
						update = true;
					}
					if (jsonObj.get("nextRecord").equals("0"))
						break;
				}
				//既存のメタデータを更新する
				if (update)
					mdFileID = maxId;
			}

	        //タイトル生成
	        StringBuffer _title = new StringBuffer();
	        if(StringUtils.isNotEmpty(uploadclearinghouseDataForm.prefix)){
	        	_title.append(uploadclearinghouseDataForm.prefix).append("/");
	        }
	        _title.append(layerName);
	        if(StringUtils.isNotEmpty(uploadclearinghouseDataForm.suffix)){
	        	_title.append("/").append(uploadclearinghouseDataForm.suffix);
	        }

	        //クリアリングハウス登録データのセット
	        String title 			= _title.toString();
	        String abstr 			= uploadclearinghouseDataForm.abstr;
	        String purpose 			= uploadclearinghouseDataForm.purpose;
	        String useLimit			= uploadclearinghouseDataForm.useLimit;
	        String status			= uploadclearinghouseDataForm.status;
	        String organizationName	= uploadclearinghouseDataForm.organizationName;
	        String voice			= uploadclearinghouseDataForm.voice;
	        String wms				= uploadclearinghouseDataForm.wms;
	        String wfs				= uploadclearinghouseDataForm.wfs;
			String[] horiBndBox = clearinghouseService.getHoriBndBox(wms, _layerId); // 範囲
			String legendUrl = clearinghouseService.getLegendUrl(request, cid, mapId, _layerId); // 凡例画像URL

			HashMap<String,Object> items = new HashMap<String,Object>();
			items.put("mdFileID"		, mdFileID);						//No2
			items.put("resTitle"		, title);							//No28
			items.put("idAbs"			, abstr);							//No32
			items.put("idPurp"			, purpose);							//No33
			items.put("useLimit"		, new String[]{useLimit});			//No64
			items.put("idStatus"		, status);							//No34
			// メタデータ作成者 問い合わせ先
			{
				// 識別情報問い合わせ先（組織名）（contact/organisationName）
				items.put("mdContact_rpOrgName", organizationName);
				// 識別情報問い合わせ先（電話番号）（contact/contactInfo/phone/voice）
				items.put("mdContact_cntPhone", new String[]{voice});
				// 問い合わせ先（住所詳細）（contact/contactInfo/address/deliveryPoint）
				items.put("mdContact_delPoint", new String[]{uploadclearinghouseDataForm.delPoint});
				// 問い合わせ先（市区町村）（contact/contactInfo/address/city）
				items.put("mdContact_city", uploadclearinghouseDataForm.city);
				// 問い合わせ先（市区町村コード）（contact/contactInfo/address/cityCode）
				items.put("mdContact_cityCode", uploadclearinghouseDataForm.cityCode);
				// 問い合わせ先（都道府県名）（contact/contactInfo/address/administrativeArea）
				items.put("mdContact_adminArea", uploadclearinghouseDataForm.adminArea);
				// 問い合わせ先（都道府県コード）（contact/contactInfo/address/administrativeAreaCode）
				items.put("mdContact_adminAreaCode", uploadclearinghouseDataForm.adminAreaCode);
				// 問い合わせ先（郵便番号）（contact/contactInfo/address/postalCode）
				items.put("mdContact_postCode", uploadclearinghouseDataForm.postCode);
				// 問い合わせ先（電子メール）（contact/contactInfo/address/electronicMailAddress）
				items.put("mdContact_eMailAdd", new String[]{uploadclearinghouseDataForm.eMailAdd});
				// 問い合わせ先（リンク）（contact/contactInfo/onlineResource/linkage）
				items.put("mdContact_linkage", uploadclearinghouseDataForm.linkage);
			}
			// データ作成者 問い合わせ先
			{
				// 識別情報問い合わせ先（組織名）（identificationInfo/MD_DataIdentification/pointOfContact/organisationName）
				items.put("Ident_rpOrgName", organizationName);
				// 識別情報問い合わせ先（電話番号）（identificationInfo	/MD_DataIdentification/pointOfContact/contactInfo/phone/voice）
				items.put("Ident_cntPhone", new String[]{voice});
				// 問い合わせ先（住所詳細）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/deliveryPoint）
				items.put("Ident_delPoint", new String[]{uploadclearinghouseDataForm.delPoint});
				// 問い合わせ先（市区町村）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/city）
				items.put("Ident_city", uploadclearinghouseDataForm.city);
				// 問い合わせ先（市区町村コード）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/cityCode）
				items.put("Ident_cityCode", uploadclearinghouseDataForm.cityCode);
				// 問い合わせ先（都道府県名）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/administrativeArea）
				items.put("Ident_adminArea", uploadclearinghouseDataForm.adminArea);
				// 問い合わせ先（都道府県コード）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/administrativeAreaCode）
				items.put("Ident_adminAreaCode", uploadclearinghouseDataForm.adminAreaCode);
				// 問い合わせ先（郵便番号）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/postalCode）
				items.put("Ident_postCode", uploadclearinghouseDataForm.postCode);
		        // 識別情報問い合わせ先（電子メール）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/address/electronicMailAddress）
				items.put("Ident_eMailAdd", new String[]{uploadclearinghouseDataForm.eMailAdd});
				// 識別情報問い合わせ先（リンク）（identificationInfo/MD_DataIdentification/pointOfContact/contactInfo/onlineResource/linkage）
				items.put("Ident_linkage", uploadclearinghouseDataForm.linkage);
			}
			items.put("onlineSrc_WMS"	, new String[]{wms});				//No95: WMS
			items.put("onlineSrc_WFS"	, new String[]{wfs});				//No95: WFS
			items.put("bgFileName", legendUrl); // サムネイル画像
			items.put("HoriBndBox", horiBndBox); // 範囲

			// 時間要素: 履歴レイヤの begin/end
			uploadclearinghouseDataForm.exTemp = clearinghouseService.getExTemp(_layerId);
			items.put("exTemp", uploadclearinghouseDataForm.exTemp);


			//予定メタデータの場合の設定
			final String STATUS_PLAN = "999";
			if(STATUS_PLAN.equals(status)){
				items.put("mdDateSt", "2999-12-31");	//No24
				//items.put("instTemp", "2999-12-31");	//No84
			}

			//完了画面に表示するためのデータ
			uploadclearinghouseDataForm.mdFileID 	= mdFileID;
			uploadclearinghouseDataForm.title 		= title;
			uploadclearinghouseDataForm.update	= update;

			boolean ret = false;
			try {
				String xml = CSWUtil.constructMetadata(items);
				ret = CSWUtil.uploadMetadata(xml,null,null);
				if(ret==false) throw new ServiceException("Upload metadata failed.");
			} catch(Exception e) {
				throw new ServiceException(lang.__("Failed to register meta data into clearing house for production."+"\n"+e.getMessage()), e);
			}

			//外部クリアリングハウスへ正常に登録できた場合、クリアリングハウス事前データ情報（clearinghousemetadata_info）へレコード挿入する。
			if (ret) {
				ClearinghousemetadataInfo entity = new ClearinghousemetadataInfo();
				if (update) {
					BeanMap conds = new BeanMap();
					conds.put(Names.clearinghousemetadataInfo().metadataid().toString(), mdFileID);
					List<ClearinghousemetadataInfo> results = clearinghousemetadataInfoService.findByCondition(conds);
					if (results.size() > 0)
						entity = results.get(0);
					else
						update = false;
				}
				entity.metadataid 			= mdFileID;
				entity.localgovinfoid 		= localgovinfoid;
				entity.tablemasterinfoid 	= tableMasterInfoId;
				entity.name = title;
				entity.note = abstr;

				if (update)
					clearinghousemetadataInfoService.update(entity);
				else
					clearinghousemetadataInfoService.insert(entity);

				// メタデータ登録時にすでに災害が起動している場合はもう一度メタデータ更新する
				{
					List<ClearinghousemetadataInfo> clearinghousemetadataInfos = new ArrayList<>();
					clearinghousemetadataInfos.add(entity);
					// 本番用
					try {
						List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid());
						if(0<trackDatas.size()) {
							TrackmapInfo trackmapInfo = trackmapInfoService.findByIdLeftJoinNotDeletedTrackDatas(trackDatas.get(0).trackmapinfoid);
							clearinghouseService.updatemetadataByMetadataInfos(clearinghousemetadataInfos, trackmapInfo);
						}
					} catch(Exception e) {
						throw new ServiceException(lang.__("Failed to register meta data into clearing house for production."+"\n"+e.getMessage()), e);
					}
					// 訓練用
					try {
						List<TrackData> trainingTrackDatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), true);
						if(0<trainingTrackDatas.size()) {
							clearinghouseService.onCreateTrainingMap(trainingTrackDatas.get(0).trackmapinfoid);
						}
					} catch(Exception e) {
						throw new ServiceException(lang.__("Failed to register meta data into clearing house for drill."+"\n"+e.getMessage()), e);
					}
				}
			}

		} catch (ServiceException e) {
			logger.error(loginDataDto.logInfo()+ "\n" + e.getMessage(), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
			logger.error("UploadClearinghouse failed: "+e.getMessage(), e);
			return "/admin/uploadclearinghouseData/index";
		}

		return "/admin/uploadclearinghouseData/finish";
	}

	protected ClearinghousemetadatadefaultInfo getDefault(Long localgovinfoid) {
		BeanMap conditions = new BeanMap();
		if(localgovinfoid != ADMIN_LOCALGOVINFOID){
			//システム管理者でなければ参照できるデータをログインした自治体(ID)で絞り込む
			conditions.put(clearinghousemetadatadefaultInfo().localgovinfoid().toString(), localgovinfoid);
		}
		List<ClearinghousemetadatadefaultInfo> defaults = clearinghousemetadatadefaultInfoService.findByCondition(conditions, clearinghousemetadatadefaultInfo().id().toString(), ASC, null, null);

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

		String wms = clearinghouseService.getWMSURL(request, mapId, layerId);
		String wfs = clearinghouseService.getWFSURL(request, cid, mapId, layerId);
		String[] exTemp = clearinghouseService.getExTemp(layerId);
		// ユーザには infinity 形式で表示
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
		List<ClearinghousemetadataInfo> infos = clearinghousemetadataInfoService.findByLocalgovinfoid(localgovinfoid);
		if(0<infos.size()) {
			TablemasterInfo tablemasterInfo = tablemasterInfoService.findByLayerId(layerId);
			if(tablemasterInfo!=null) {
				long tablemasterinfoid = tablemasterInfo.id;
				for(ClearinghousemetadataInfo info : infos) {
					if(info.tablemasterinfoid.equals(tablemasterinfoid)) {
						isRegistered = true;
						metadataid = info.metadataid;
						break;
					}
				}
			}
		}
		
		// DBに保存されたメタデータIDがなければ、クリアリングハウスから同じWMSURLがあるか確認
		if(isRegistered==false) {
			HashMap<String, String> conditions = new HashMap<String, String>();
			conditions.put("RESOURCEURL", wms.replaceAll("&", "&amp;"));
			String xml = CSWUtil.getRecords(conditions, 1, 1, 1);
			String json = CSWUtil.GetRecordsResponseToJSON(xml);
			JSONObject jsonObj = JSONObject.fromObject(json.replaceAll("\n", "\\n"));
			isRegistered = !jsonObj.get("numReturn").equals("0");
			
			if(jsonObj.has("record") && jsonObj.getJSONObject("record").isArray()) {
				JSONArray record = jsonObj.getJSONArray("record");
				if(0<record.size()) {
					JSONObject result = record.getJSONObject(0);
					metadataid = result.getString("metadataId");
				}
			}

		}

		JSONObject obj = new JSONObject();
		if(isRegistered && StringUtil.isNotEmpty(metadataid) ) {
			try {
				String recordJSON = clearinghouseService.getRecordById(metadataid, /*isTraining*/false).toString();
				obj = JSONObject.fromObject(recordJSON);
			} catch(Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		else {
			//Long localgovinfoid = loginDataDto.getLocalgovinfoid();
			ClearinghousemetadatadefaultInfo defaultInfo = getDefault(localgovinfoid);
			obj.put("Ident_adminAreaCode", loginDataDto.getLocalgovInfo().prefcode);
			obj.put("Ident_cityCode", loginDataDto.getLocalgovInfo().citycode);
			if (defaultInfo!=null) {
				// 地図データに関する情報
				{
					// 接頭語
					obj.put("prefix", defaultInfo.prefix);
					// 接尾語
					obj.put("suffix", defaultInfo.suffix);
				}

				// メタデータ/地図データに関する問い合わせ先情報
				{
					// 問合せ先
					obj.put("contactname", defaultInfo.reference);
					// 郵便番号
					obj.put("Ident_postCode", defaultInfo.postcode);
					// 都道府県名
					obj.put("Ident_adminArea", defaultInfo.adminarea);
					// 市区町村
					obj.put("Ident_city", defaultInfo.city);
					// 都道府県コード
					String adminareacode = defaultInfo.adminareacode;
					if(StringUtil.isNotEmpty(adminareacode)) {
						obj.put("Ident_adminAreaCode", adminareacode);
					}
					// 市区町村コード
					String citycode = defaultInfo.citycode;
					if(StringUtil.isNotEmpty(citycode)) {
						obj.put("Ident_cityCode", citycode);
					}
					// 町名、番地、ビル名等
					obj.put("Ident_delPoint", defaultInfo.deliverypoint);
					// 電話番号
					obj.put("Ident_cntPhone", defaultInfo.telno);
					// 電子メールアドレス
					obj.put("Ident_eMailAdd", defaultInfo.email);
					// 問い合わせ先のHP等のURL
					obj.put("Ident_linkage", defaultInfo.linkage);
				}
			}
		}
		// プロパティを登録
		obj.put("wms", wms); 	// wms
		obj.put("wfs", wfs); 	// wfs
		obj.put("exTemp", exTemp);
		obj.put("registered", isRegistered);

		// 出力の準備
		return new ResponseEntity<String>(obj.toString(), httpHeaders, HttpStatus.OK);
	}
}
