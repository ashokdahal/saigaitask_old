/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.disconnect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.seasar.framework.beans.util.BeanMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.MapbaselayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.form.admin.disconnect.RasterDataUploadForm;
import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.service.db.MapbaselayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.util.FileUtil;
import net.sf.json.JSONObject;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/disconnect/rasterDataUpload")
public class RasterDataUploadAction extends AbstractDisconnectAction {

	public RasterDataUploadForm rasterDataUploadForm;

	@Resource
	private ResponseService responseService;
	@Resource
	private MapmasterInfoService mapmasterInfoService;
	@Resource
	private TrackmapInfoService trackmapInfoService;
	@Resource
	private MenuInfoService menuInfoService;
	@Resource
	private MapbaselayerInfoService mapbaselayerInfoService;

	static final String ZIPFILE = "tiles.zip";
	static final String LAYERID = "XYZ_LOCAL";
	String LAYERNAME() { return lang.__("Local Geographical Survey Institute tile"); }
	static final String WORKDIR = "uploadwork";

	/**
	 * ラスタデータアップロード画面を表示する
	 */
	@RequestMapping(value="/content")
	public String content(Map<String,Object>model, @ModelAttribute RasterDataUploadForm rasterDataUploadForm) {
		this.rasterDataUploadForm = rasterDataUploadForm;
		initDisconnect();

		rasterDataUploadForm.ecomUser = loginDataDto.getEcomUser();

		// 自治体セレクトボックスの作成
		if (loginDataDto.getLocalgovinfoid() == 0) {
			createLocalgovSelectOptions();
		}

		setupModel(model);

		return "/admin/disconnect/rasterDataUpload/content";
	}

	/**
	 * @throws Exception
	 *
	 */
	@RequestMapping(value="/upload", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String upload(@ModelAttribute RasterDataUploadForm rasterDataUploadForm) {
		this.rasterDataUploadForm = rasterDataUploadForm;

		try{
			// その他のエラー？
			if (rasterDataUploadForm.rasterDataFile == null) {
				throw new ServiceException(" ");
			}

			ResourceBundle rb = ResourceBundle.getBundle("SaigaiTask");
			String tilePath = rb.getString("DISCONNTCT_TILE_PATH");
			String workPath = tilePath + "/" + WORKDIR;
			String tileUrl = rb.getString("DISCONNTCT_TILE_URL");
			String layerName = LAYERNAME();
			MapDB mapDB = MapDB.getMapDB();
			Long localgovinfoId = loginDataDto.getLocalgovinfoid();
			if (localgovinfoId == 0)
				localgovinfoId = new Long(rasterDataUploadForm.selectLocalgov);
			List<Long> mapIdList = new ArrayList<Long>();

			// マスタマップのマップＩＤを取得する
			MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoId);
			if (mapmasterInfo == null) {
				throw new ServiceException(lang.__("Master map info is not found."));
			}
			mapIdList.add(mapmasterInfo.mapid);

			// 全災害のマップＩＤを追加する
			for (TrackmapInfo trackmapInfo : trackmapInfoService.findByLocalgovInfoIdAndTrackDataNotDelete(localgovinfoId)) {
				mapIdList.add(trackmapInfo.mapid);
			}

			// 作業ディレクトリが残っている場合は削除する
			FileUtils.deleteDirectory(new File(workPath));

			// 作業ディレクトリを作成する
			FileUtil.dirCreate(workPath);

			// 受信データをファイルに保存する
			OutputStream os = new FileOutputStream(workPath + "/" + ZIPFILE);
			InputStream is = rasterDataUploadForm.rasterDataFile.getInputStream();
			IOUtils.copy(is, os);

			// ZIP ファイルを展開する
			File zipFile = new File(workPath + "/" + ZIPFILE);
			List<File> files = FileUtil.unzip(zipFile, null, true);
			if (files.size() <= 0) {
				throw new ServiceException(lang.__("Failed to expand zip file."));
			}

			// ZIP フィアイルは削除する
			zipFile.delete();

			// ディレクトリの移動
			File dir = new File(workPath);
			for (File workDir : dir.listFiles()) {
				// FileUtil.unzip() が作成した作業ディレクトリを探す
				if (workDir.isDirectory() && workDir.listFiles().length > 0) {
					// その下のディレクトリの一覧を取得する（１個でない場合はエラー）
					File[] dirs = workDir.listFiles();
					if (dirs.length != 1) {
						throw new ServiceException(lang.__("Failed to expand zip file."));
					}
					// １つ下のディレクトリを １つ上に、名前を変えてコピーする（ファイルが存在する場合は上書き）
					File topDir = dirs[0];
					File newDir = new File(tilePath + "/" + localgovinfoId);
					FileUtils.copyDirectory(topDir, newDir);
					// 作業ディレクトリは削除する
					FileUtils.deleteDirectory(dir);
					break;
				}
			}

			// layerinfo を追加する
			LayerInfo layerInfo = new LayerInfo();
			layerInfo.layerId = LAYERID;
			layerInfo.name = layerName;
			layerInfo.description = "";
			layerInfo.type = LayerInfo.TYPE_BASE_XYZ;
			layerInfo.status = LayerInfo.STATUS_DEFAULT;
			layerInfo.ownerId = mapDB.getAuthIdUserInfo(rasterDataUploadForm.ecomUser).userId;
			layerInfo.ownerMapId = mapIdList.get(0);
			layerInfo.attribution = "";
			layerInfo.wmsFormat = "image/png";
			layerInfo.wmsURL = tileUrl + "/" + localgovinfoId + "/${z}/${x}/${y}.png";

			// すでにあれば update、なければ insert
			if (!mapDB.updateLayerInfo(layerInfo)) {
				mapDB.insertLayerInfo(layerInfo);
			}

			// 全部のマップに maplayerinfo を追加する
			for (Long mapId : mapIdList) {
				MapInfo mapInfo = mapDB.getMapInfo(mapId);
				MapLayerInfo mapLayerInfo = new MapLayerInfo(LAYERID, layerName, layerInfo.type, null, 0, 0, null);
				mapLayerInfo.visible = true;

				// すでにあれば update、なければ insert
				if (!mapDB.updateMapLayerInfo(mapInfo, mapLayerInfo)) {
					mapDB.insertMapLayerInfo(mapInfo, mapLayerInfo);
				}
			}

			// ベースレイヤが１つ以上ある menuinfo を検索する
			BeanMap conditions = new BeanMap();
			conditions.put(Names.menuInfo().menutasktypeInfo().localgovinfoid().toString(), localgovinfoId);
			List<MenuInfo> menuInfoList = menuInfoService.findByConditionWithBaseLayerInfo(conditions);

			// そのすべてに新規のベースレイヤを追加する
			MapbaselayerInfo newBaseLayer = new MapbaselayerInfo();
			newBaseLayer.layerid = layerInfo.layerId;
			newBaseLayer.visible = true;
			newBaseLayer.valid = true;

			for (MenuInfo menuInfo : menuInfoList) {
				int dispOrder = 0;
				MapbaselayerInfo existingLayer = null;
				for (MapbaselayerInfo baseLayer : menuInfo.mapbaselayerInfoList) {
					// レイヤＩＤ='XYZ_LOCAL' がすでに追加されている場合
					if (LAYERID.equals(baseLayer.layerid)) {
						existingLayer = baseLayer;
					}
					else {
						// disporder の最大値を求める
						if (baseLayer.disporder.intValue() > dispOrder)
							dispOrder = baseLayer.disporder.intValue();
						// 初期表示フラグをクリアする
						if (baseLayer.visible) {
							baseLayer.visible = false;
							mapbaselayerInfoService.update(baseLayer);
						}
					}
				}

				// 新規ベースレイヤを作成する
				newBaseLayer.menuinfoid = menuInfo.id;
				newBaseLayer.disporder = dispOrder + 1;
				// すでにある場合は update
				if (existingLayer != null) {
					newBaseLayer.id = existingLayer.id;
					mapbaselayerInfoService.update(newBaseLayer);
				}
				// ない場合は insert
				else {
					mapbaselayerInfoService.insert(newBaseLayer);
				}
			}

			responseService.response("{}");

		} catch(Exception e) {
			logger.error(loginDataDto.logInfo()+ lang.__("\n Failed to upload raster data upload()."), e);
			JSONObject json = new JSONObject();
			if (e instanceof ServiceException)
				json.put("error", e.getMessage());
			else
				json.put("error", e.toString());
			try {
				responseService.responseJsonText(json);
			} catch (Exception e1) {
			}
		}

		return null;
	}


}
