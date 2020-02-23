/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.setupper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.json.JSONArray;
import org.json.JSONObject;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.admin.MapRestore;
import jp.ecom_plat.map.db.CommunityInfo;
import jp.ecom_plat.map.db.ListSearchConditionInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.security.UserAuthorization;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.CkanmetadataInfo;
import jp.ecom_plat.saigaitask.entity.db.ClearinghousemetadataInfo;
import jp.ecom_plat.saigaitask.entity.db.FilterInfo;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapbaselayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MapkmllayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MaplayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MapreferencelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutasktypeInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.db.FilterInfoService;
import jp.ecom_plat.saigaitask.service.db.MapbaselayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MapkmllayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MaplayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MapreferencelayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.SummarylistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.service.db.UserInfoService;
import jp.ecom_plat.saigaitask.service.excellist.ExcellistService;
import jp.ecom_plat.saigaitask.service.setupper.ExportService.ExportFileSet;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.TableSheetReader;

/**
 * 自治体設定をインポートするサービスクラスです.
 */
@org.springframework.stereotype.Service
public class ImportInfoService extends AbstractImportService {

	/** Logger */
	protected Logger logger = Logger.getLogger(ImportInfoService.class);

	// service
	@Resource protected ExcelImportInfoService excelImportInfoService;
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected MaplayerInfoService maplayerInfoService;
	@Resource protected MapbaselayerInfoService mapbaselayerInfoService;
	@Resource protected MapreferencelayerInfoService mapreferencelayerInfoService;
	@Resource protected MapkmllayerInfoService mapkmllayerInfoService;
	@Resource protected TablemasterInfoService tablemasterInfoService;
	@Resource protected MenuInfoService menuInfoService;
	@Resource protected FilterInfoService filterInfoService;
	@Resource protected UnitInfoService unitInfoService;
	@Resource protected UserInfoService userInfoService;
	@Resource protected SummarylistcolumnInfoService summarylistcolumnInfoService;
	@Resource protected ExcellistService excellistService;

	/**
	 * テーブルマスタ情報のうち、地図マスタ情報ID=0の、システムテーブルのマッピング
	 * Excel のシステムマスタと、DBとのマッピング情報が入っています.
	 */
	protected Map<Object, Object> systemtableMap = null;

	protected static List<Class<?>> orderByImport;
	protected static Comparator<Class<?>> orderByImportComparator = new Comparator<Class<?>>() {
		@Override
		public int compare(Class<?> o1, Class<?> o2) {
			int o1Index = orderByImport.indexOf(o1);
			int o2Index = orderByImport.indexOf(o2);
			// 両方とも orderByImport にある場合
			if(o1Index!=-1 && o2Index!=-1) return o1Index - o2Index;
			// o1 だけ orderByImport にある場合
			else if(o1Index!=-1) return -1;
			// o2 だけ orderByImport にある場合
			else if(o2Index!=-1) return 1;
			// 両方とも orderByImport にない場合
			else return 0;
		}
	};
	static {
		// 外部参照制約を考慮しインポート順をここで定義する
		orderByImport = new ArrayList<Class<?>>();

		// 自治体設定
		orderByImport.add(GroupInfo.class); // AlarmmessageInfo より先
		orderByImport.add(UnitInfo.class);
		orderByImport.add(UserInfo.class);

		// 地図マスタ情報
		orderByImport.add(MapmasterInfo.class);
		orderByImport.add(TablemasterInfo.class);

		// メニュー情報
		orderByImport.add(MenutasktypeInfo.class);
		orderByImport.add(MenuInfo.class);
		orderByImport.add(FilterInfo.class); // MenuInfo より後
	}

	/**
	 * 自治体設定のインポート
	 * @param exportFileSet エクスポートファイル
	 * @param localgovinfoid 自治体ID
	 * @param password ログインパスワード
	 * @param ecomuser eコミユーザ
	 * @param communityInfo eコミサイト情報
	 * @param groupInfo eコミグループ情報
	 * @return 自治体情報インポート結果
	 * @throws NoSuchAlgorithmException
	 */
	public ImportInfoResult execute(ExportFileSet exportFileSet, Long localgovinfoid, String password, String ecomuser, CommunityInfo communityInfo, jp.ecom_plat.map.db.GroupInfo groupInfo) throws NoSuchAlgorithmException {

		// 別スレッドで実行の場合は ThreadLocal の lang を使用する
		SaigaiTaskDBLang lang = SaigaiTaskDBLang.langThreadLocal.get();

		// 地図バックアップファイルの存在チェック
		if(!exportFileSet.mapbackupZip.exists()) {
			throw new ServiceException(lang.__("Map backup is not found."));
		}

		// エクセルファイルの存在チェック
		if(!exportFileSet.infoExportXsl.exists()) {
			throw new ServiceException(lang.__("Local gov. settings data is not found."));
		}

		// ユーザID変換テーブル生成
		MapDB mapDB = MapDB.getMapDB();
		UserInfo userInfo = mapDB.getAuthIdUserInfo(ecomuser);
		if(userInfo==null) throw new ServiceException(lang.__("e-Com map user \"{0}\" not exist.", ecomuser));
		HashMap<Integer, UserInfo> userInfoTable = new HashMap<Integer, UserInfo>();
		userInfoTable.put(0, userInfo);

		// 地図の復元
		Writer out = new PrintWriter(new ByteArrayOutputStream());
		MapRestore.Result mapRestoreResult = restoreMap(out, exportFileSet.mapbackupZip, communityInfo, groupInfo, userInfoTable);
		// 地図タイトルの更新
		try {
			// 自治体名
			LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
			String localgovName = localgovInfo.pref;
			if(StringUtil.isNotEmpty(localgovInfo.city)) localgovName += localgovInfo.city;
			// リストアした地図のタイトルを更新
			int mapIdTableIdx = 0;
			for(Map.Entry<Long, Long> entry : mapRestoreResult.mapIdTable.entrySet()) {
				MapInfo mapInfo = mapDB.getMapInfo(entry.getValue());
				mapInfo.title = localgovName+lang.__("Master map");
				if(0<mapIdTableIdx++) mapInfo.title += mapIdTableIdx; // 最初は添え字なし、次からは２となる
				// DB 更新
				mapDB.updateMapInfo(mapInfo);
			}
		} catch(Exception e) {
			logger.error(lang.__("Failed to change map name."), e);
		}

		// フィルタ条件のインポート
		Map<Long, Long> filterIdTable = importListSearchConditionInfo(exportFileSet.infoExportXsl, mapRestoreResult);

		// システムテーブルのマッピング読込
		setSystemtableMap(exportFileSet.masterExportXsl);

		// 自治体設定のインポート
		ImportInfoResult importInfoResult = importInfo(exportFileSet.infoExportXsl, localgovinfoid);

		// ログイン班のパスワードを設定
		updateGroupInfoAndUnitInfo(importInfoResult, password, ecomuser);

		// 復元した地図情報の地図ID, レイヤIDを更新
		updateRestoreMapIdLayerId(mapRestoreResult, importInfoResult.localgovinfoidDest);

		// 復元したフィルタIDを更新
		updateRestoreFilterId(filterIdTable, importInfoResult.localgovinfoidDest);

		// エクセル帳票テンプレートファイルをインポート
		importExceltemplate(importInfoResult, mapRestoreResult, filterIdTable, exportFileSet.exceltenplateZip);

		return importInfoResult;
	}

	/**
	 * フィルタリング条件をインポートします.
	 * エクスポート->{@link ExportService#exportListSearchConditionInfo(HSSFWorkbook, Long)}
	 * @param infoExportXsl 自治体設定エクセルファイル
	 * @param mapRestoreResult 地図リストア結果
	 * @return フィルタID変換テーブル
	 */
	public Map<Long, Long> importListSearchConditionInfo(File infoExportXsl, MapRestore.Result mapRestoreResult) {

		// 別スレッドで実行の場合は ThreadLocal の lang を使用する
		SaigaiTaskDBLang lang = SaigaiTaskDBLang.langThreadLocal.get();

		// フィルタID変換テーブル
		Map<Long, Long> filterIdTable = new HashMap<Long, Long>();

		try {
			// エクセルを開く
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(infoExportXsl));
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			UserInfo userInfo = mapRestoreResult.userInfoTable.get(0);

			TableSheetReader reader = new TableSheetReader(wb, "_list_condition");
			Map<String, Object> record = null;
			while((record=reader.readLine())!=null) {
				try {
					// 元ID
					Long idDest = Long.valueOf((String) record.get("id"));

					// 地図ID変換
					long mapIdSrc = Long.parseLong(((String) record.get("map_id")));
					Long mapId = mapRestoreResult.mapIdTable.get(mapIdSrc);
					// 変換できない場合、有効な地図IDに自動でセットする
					if(mapId==null) {
						for(Long val : mapRestoreResult.mapIdTable.values()) {
							if(val!=null) {
								mapId = val;
								break;
							}
						}
					}
					if(mapId==null) throw new ServiceException("invalid map_id. _list_condition: "+record.toString());

					// レイヤID変換
					String layerIdSrc = (String) record.get("layer_id");
					String layerId = mapRestoreResult.layerIdTable.get(layerIdSrc);

					// 検索条件の名称
					String name = (String) record.get("name");

					// 検索条件
					String conditionStr = (String) record.get("condition");

					// フィルタ条件
					JSONObject conditionValue = new JSONObject(conditionStr);
					// 入力範囲の空間検索範囲のバッファ（単位はm）
					double buffer = conditionValue.getDouble("buffer");
					// 入力範囲の空間検索範囲の重なり条件
					int spatialType = conditionValue.getInt("spatial");
					// 条件を反転するならtrue
					boolean isNot = false;
					if(conditionValue.has("isnot")) {
						isNot = conditionValue.getBoolean("isnot");
					}

					// 空間検索範囲レイヤ条件
					JSONArray spatialLayerSrc = null;
					if(conditionValue.has("spatiallayer")) spatialLayerSrc = conditionValue.getJSONArray("spatiallayer");
					JSONArray spatialLayer = null;
					if(spatialLayerSrc!=null && 0<spatialLayerSrc.length()) {
						spatialLayer = new JSONArray();
						// 各レイヤIDを変換
						for(int idx=0; idx<spatialLayerSrc.length(); idx++) {
							JSONObject spatialLayerJson = spatialLayerSrc.getJSONObject(idx);
							String layer = null;
							if(spatialLayerJson.has("layer")) {
								layer = spatialLayerJson.getString("layer");
							}
							spatialLayerJson.put("layer", mapRestoreResult.layerIdTable.get(layer));
							spatialLayer.put(spatialLayerJson);
						}
					}

					// 属性検索条件
					JSONArray condition = null;
					if(conditionValue.has("condition") && !conditionValue.isNull("condition")) {
						condition = conditionValue.getJSONArray("condition");
					}

					// インポート実行
					long id = ListSearchConditionInfo.insertCondition(userInfo, mapId, layerId, name, condition, buffer, spatialType, spatialLayer, isNot);
					filterIdTable.put(idDest, id);
				} catch(Exception e) {
					throw new ServiceException(lang.__("An error occurred during importing filter conditions.")
							+" record="+record.toString(), e);
				}
			}

			return filterIdTable;

		} catch(Exception e) {
			throw new ServiceException(lang.__("Import was interrupted."), e);
		}
	}

	/**
	 * システムテーブルのIDマッピングを設定します.
	 * @param masterExportXsl
	 */
	public void setSystemtableMap(File masterExportXsl) {
		// 別スレッドで実行の場合は ThreadLocal の lang を使用する
		SaigaiTaskDBLang lang = SaigaiTaskDBLang.langThreadLocal.get();

		Map<Object, Object> systemtableMap = new HashMap<Object, Object>();
		try {
			// エクセルを開く
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(masterExportXsl));
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			@SuppressWarnings("unchecked")
			List<TablemasterInfo> tablemasterInfos = (List<TablemasterInfo>) excelImportInfoService.readRecords(wb, TablemasterInfo.class);
			for(TablemasterInfo tablemasterInfoSrc : tablemasterInfos) {
				// 地図マスタ情報ID=0 は、システムテーブル
				if(Long.valueOf(0).equals(tablemasterInfoSrc.mapmasterinfoid)) {
					TablemasterInfo tablemasterInfoDest = tablemasterInfoService.findByMapmasterInfoIdAndTablename(0L, tablemasterInfoSrc.tablename);
					// DBにない地図マスタ情報の場合は登録する
					if(tablemasterInfoDest==null) {
						tablemasterInfoDest = Beans.createAndCopy(TablemasterInfo.class, tablemasterInfoSrc).execute();
						tablemasterInfoService.insert(tablemasterInfoDest);
					}

					// マッピングを作成
					systemtableMap.put(tablemasterInfoSrc.id, tablemasterInfoDest.id);
				}
			}
			// 設定
			this.systemtableMap = systemtableMap;
		} catch(Exception e) {
			throw new ServiceException(lang.__("Failed to create system table mapping."), e);
		}
	}

	/**
	 * 班情報の更新
	 * @param importInfoResult 自治体情報インポート結果
	 * @param password ログイン班パスワード
	 * @param ecomuser eコミユーザID
	 * @throws NoSuchAlgorithmException
	 */
	public void updateGroupInfoAndUnitInfo(ImportInfoResult importInfoResult, String password, String ecomuser) throws NoSuchAlgorithmException {
		String encryptedPass = UserAuthorization.getEncryptedPass(password);
		List<GroupInfo> groupInfos = groupInfoService.findByLocalgovinfoid(importInfoResult.localgovinfoidDest);
		for(GroupInfo groupInfo : groupInfos) {
			groupInfo.password = encryptedPass;
			groupInfo.ecomuser = ecomuser;
			groupInfoService.update(groupInfo);
		}
		List<UnitInfo> unitInfos = unitInfoService.findByLocalgovinfoid(importInfoResult.localgovinfoidDest);
		for(UnitInfo unitInfo : unitInfos) {
			unitInfo.password = encryptedPass;
			unitInfo.ecomuser = ecomuser;
			unitInfoService.update(unitInfo);
		}
	}

	/**
	 * 地図のリストアでIDが変わっているため、自治体設定の方もIDを更新する.
	 * @param mapRestoreResult 地図復元結果
	 * @param localgovinfoidDest 復元先自治体ID
	 */
	public void updateRestoreMapIdLayerId(MapRestore.Result mapRestoreResult, Long localgovinfoidDest) {

		// 別スレッドで実行の場合は ThreadLocal の lang を使用する
		SaigaiTaskDBLang lang = SaigaiTaskDBLang.langThreadLocal.get();

		// 仕様上、1件しかないはずだが、設定ミスにより複数登録される場合がある。
		// 一件しかID変換しないと、レコード順序次第で未変換レコードが使用される場合があるので
		// 複数のレコードがある場合は全てのレコードでID変換する。
		BeanMap conditions = new BeanMap();
		conditions.put("localgovinfoid", localgovinfoidDest);
		List<MapmasterInfo> mapmasterInfos = mapmasterInfoService.findByCondition(conditions);

		for(MapmasterInfo mapmasterInfo : mapmasterInfos) {
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// マスタマップ情報の変換
			//MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoidDest);
			if(mapmasterInfo==null) throw new ServiceException(lang.__("Master map info is not found."));

			// communityid の変換
			logger.info("mapmaster_info.communityid: "+mapmasterInfo.communityid+" -> "+mapRestoreResult.communityInfo.communityId);
			mapmasterInfo.communityid = mapRestoreResult.communityInfo.communityId;

			// mapgroupid の変換
			logger.info("mapmaster_info.mapgroupid: "+mapmasterInfo.mapgroupid+" -> "+mapRestoreResult.groupInfo.groupId);
			mapmasterInfo.mapgroupid = mapRestoreResult.groupInfo.groupId;

			// mapid の変換
			Long destMapId = null;
			// 論理削除の場合はmapid変換をスキップする
			if(!mapmasterInfo.deleted) {
				destMapId = mapRestoreResult.mapIdTable.get(mapmasterInfo.mapid);
				if(destMapId==null) throw new ServiceException(lang.__("Unable to get {0} map ID after restoration.", "mapid="+mapmasterInfo.mapid));
				logger.info("mapmaster_info.mapid: "+mapmasterInfo.mapid+" -> "+destMapId);
			}
			mapmasterInfo.mapid = destMapId;

			// DB 更新
			mapmasterInfoService.update(mapmasterInfo);

			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// テーブルマスタ情報の変換
			List<TablemasterInfo> tablemasterInfos = tablemasterInfoService.findByMapmasterInfoId(mapmasterInfo.id);
			for(TablemasterInfo tablemasterInfo : tablemasterInfos) {
				// layerid が 空のものは、システムテーブルとみなす
				if(StringUtil.isEmpty(tablemasterInfo.layerid)) continue;

				// 変換後のレイヤIDを取得
				String dstLayerId = mapRestoreResult.layerIdTable.get(tablemasterInfo.layerid);
				if(dstLayerId==null) {
					logger.warn(lang.__("Unable to get {0} layer ID after restoration.", "layerid="+tablemasterInfo.layerid));
				}
				logger.debug("tablemaster_info id="+tablemasterInfo.id+" layerid="+tablemasterInfo.layerid+"->"+dstLayerId+" tablename="+tablemasterInfo.tablename+"->"+dstLayerId);
				tablemasterInfo.layerid = dstLayerId;
				tablemasterInfo.tablename = dstLayerId;

				// DB 更新
				tablemasterInfoService.update(tablemasterInfo);
			}
		}

			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// 地図レイヤ情報の切り出しレイヤID変換
			List<MaplayerInfo> maplayerInfos = maplayerInfoService.findByLocalgovinfoid(localgovinfoidDest, true);
			for(MaplayerInfo maplayerInfo : maplayerInfos) {
				String log = "maplayer_info.id="+maplayerInfo.id+" menuid="+maplayerInfo.menuinfoid;
				if(StringUtil.isEmpty( maplayerInfo.intersectionlayerid)) {
					logger.debug(log+" intersectionlayerid is empty.");
				}
				else {
					String dstLayerId = mapRestoreResult.layerIdTable.get(maplayerInfo.intersectionlayerid);
					if(dstLayerId==null) {
						logger.warn(lang.__("Can't get layer ID after restoration of layerid={0}.", maplayerInfo.intersectionlayerid));
					}
					logger.debug(log+" intersectionlayerid="+maplayerInfo.intersectionlayerid+"->"+dstLayerId);
					maplayerInfo.intersectionlayerid = dstLayerId;
					maplayerInfoService.update(maplayerInfo);
				}

			}

			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// 地図KMLレイヤ情報のレイヤID変換
			List<MapkmllayerInfo> mapkmllayerInfos = mapkmllayerInfoService.findByLocalgovinfoid(localgovinfoidDest, true);
			for(MapkmllayerInfo mapkmllayerInfo : mapkmllayerInfos) {
				String log = "mapkmllayer_info.id="+mapkmllayerInfo.id+" menuid="+mapkmllayerInfo.menuinfoid;
				if(StringUtil.isEmpty( mapkmllayerInfo.layerid)) {
					logger.debug(log+" layerid is empty.");
				}
				// KMLグループの場合はレイヤID変換しない
				else if(mapkmllayerInfo.layerid.startsWith("group:")) {
					continue;
				}
				else {
					String dstLayerId = mapRestoreResult.layerIdTable.get(mapkmllayerInfo.layerid);
					if(dstLayerId==null) {
						logger.warn(lang.__("Unable to get {0} layer ID after restoration.", "layerid="+mapkmllayerInfo.layerid));
					}
					logger.debug(log+" layerid="+mapkmllayerInfo.layerid+"->"+dstLayerId);
					mapkmllayerInfo.layerid = dstLayerId;
					mapkmllayerInfoService.update(mapkmllayerInfo);
				}

			}

			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// 地図参照レイヤ情報のレイヤID変換
			List<MapreferencelayerInfo> mapreferencelayerInfos = mapreferencelayerInfoService.findByLocalgovinfoid(localgovinfoidDest, true);
			for(MapreferencelayerInfo mapreferencelayerInfo : mapreferencelayerInfos) {
				String log = "mapreferencelayer_info.id="+mapreferencelayerInfo.id+" menuid="+mapreferencelayerInfo.menuinfoid;
				if(StringUtil.isEmpty( mapreferencelayerInfo.layerid)) {
					logger.debug(log+" layerid is empty.");
				}
				else {
					String dstLayerId = mapRestoreResult.layerIdTable.get(mapreferencelayerInfo.layerid);
					if(dstLayerId==null) {
						logger.warn(lang.__("Unable to get {0} layer ID after restoration.", "layerid="+mapreferencelayerInfo.layerid));
					}
					logger.debug(log+" layerid="+mapreferencelayerInfo.layerid+"->"+dstLayerId);
					mapreferencelayerInfo.layerid = dstLayerId;
					mapreferencelayerInfoService.update(mapreferencelayerInfo);
				}

			}

			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// 地図ベースレイヤ情報のレイヤID変換
			List<MapbaselayerInfo> mapbaselayerInfos = mapbaselayerInfoService.findByLocalgovinfoid(localgovinfoidDest, true);
			for(MapbaselayerInfo mapbaselayerInfo : mapbaselayerInfos) {
				String log = "mapbaselayer_info.id="+mapbaselayerInfo.id+" menuid="+mapbaselayerInfo.menuinfoid;
				if(StringUtil.isEmpty( mapbaselayerInfo.layerid)) {
					logger.debug(log+" layerid is empty.");
				}
				else {
					String dstLayerId = mapRestoreResult.layerIdTable.get(mapbaselayerInfo.layerid);
					if(dstLayerId==null) {
						logger.warn(lang.__("Unable to get {0} layer ID after restoration.", "layerid="+mapbaselayerInfo.layerid));
					}
					logger.debug(log+" layerid="+mapbaselayerInfo.layerid+"->"+dstLayerId);
					mapbaselayerInfo.layerid = dstLayerId;
					mapbaselayerInfoService.update(mapbaselayerInfo);
				}

			}
	}

	/**
	 * フィルタ条件のリストアでIDが変わっているため、自治体設定の方もIDを更新する.
	 * @param filterIdTable フィルタID変換テーブル
	 * @param localgovinfoidDest 復元先自治体ID
	 */
	@SuppressWarnings("deprecation")
	public void updateRestoreFilterId(Map<Long, Long> filterIdTable, Long localgovinfoidDest) {
		if(filterIdTable!=null) {
			// Ver 1.3
			List<FilterInfo> filterInfos = filterInfoService.findByLocalgovinfoid(localgovinfoidDest, true);
			for(FilterInfo filterInfo : filterInfos) {
				// フィルタIDを変換
				if(filterInfo.filterid!=null) {
					filterInfo.filterid = filterIdTable.get(filterInfo.filterid);
					filterInfoService.update(filterInfo);
				}
			}
			// Ver 1.1, 1.2
			List<MenuInfo> menuInfos = menuInfoService.findByLocalgovinfoid(localgovinfoidDest, true);
			for(MenuInfo menuInfo : menuInfos) {
				// フィルタIDを変換
				if(menuInfo.filterid!=null) {
					// フィルタIDを変換してDBに登録
					long srcFilterid = menuInfo.filterid;
					menuInfo.filterid = filterIdTable.get(menuInfo.filterid);
					menuInfoService.update(menuInfo);

					// 変換後、nullならば次へ進む
					if(menuInfo.filterid==null) {
						logger.warn("convert filterid is null. menuinfoid="+menuInfo.id+" filterid="+srcFilterid);
						continue;
					}

					// フィルタ情報が登録されているか確認
					boolean exists = false;
					filterInfos = filterInfoService.findByMenuid(menuInfo.id);
					for(FilterInfo filterInfo : filterInfos) {
						if(menuInfo.filterid.equals(filterInfo.filterid)) {
							exists = true;
							break;
						}
					}

					// フィルタ情報が登録されていなければ追加登録
					if(exists==false) {
						// フィルタ名称
						String name = null;
						List<BeanMap> listConditions = tableService.selectById("_list_condition", "id", menuInfo.filterid);
						for(BeanMap listCondition : listConditions) {
							name = (String) listCondition.get("name");
							break;
						}

						//表示順取得
						int maxDisporder = 0;
						BeanMap maxDisporderConditions = new BeanMap();
						//親IDが存在する場合、親IDに紐付くデータの内、最大の表示順を取得する。
						maxDisporderConditions.put("menuinfoid", menuInfo.id);
						maxDisporder = filterInfoService.getLargestDisporder(maxDisporderConditions);

						// 追加登録
						FilterInfo filterInfo = new FilterInfo();
						filterInfo.menuinfoid = menuInfo.id;
						filterInfo.filterid = menuInfo.filterid;
						filterInfo.name = name;
						filterInfo.disporder = maxDisporder;
						filterInfo.valid = true;
						filterInfoService.insert(filterInfo);
					}
				}
			}
		}
	}

	/**
	 * 地図のリストア
	 * @param out
	 * @param zipFile
	 * @param communityInfo 復元先サイト
	 * @param groupInfo 復元先グループ
	 * @param userInfoTable ユーザID変換テーブル
	 * @return 地図復元結果
	 */
	public MapRestore.Result restoreMap(Writer out, File zipFile, CommunityInfo communityInfo, jp.ecom_plat.map.db.GroupInfo groupInfo, HashMap<Integer, UserInfo> userInfoTable) {
		// 別スレッドで実行の場合は ThreadLocal の lang を使用する
		SaigaiTaskDBLang lang = SaigaiTaskDBLang.langThreadLocal.get();

		try {
			// 復元ファイルの読み込み
			MapRestore mapRestore = new MapRestore(zipFile, /*regist*/ true);
			mapRestore.setWriter(out);
			return mapRestore.restore(communityInfo, groupInfo, userInfoTable);
		} catch(Exception e) {
			throw new ServiceException(lang.__("Unable to restore map."), e);
		}
	}

	/**
	 * 自治体設定のインポート結果
	 */
	public static class ImportInfoResult extends ExcelImportInfoService.ImportResult{
		/** インポート先の自治体ID */
		public Long localgovinfoidDest;
	}

	/**
	 * 自治体の設定をインポートします.
	 * @param infoExportXsl 自治体設定をエクスポートしたエクセルファイル
	 * @param localgovinfoidDest インポート先自治体ID
	 * @return インポートされたクラスのリスト
	 */
	public ImportInfoResult importInfo(File infoExportXsl, Long localgovinfoidDest) {

		// 別スレッドで実行の場合は ThreadLocal の lang を使用する
		SaigaiTaskDBLang lang = SaigaiTaskDBLang.langThreadLocal.get();

		// インポート結果
		ImportInfoResult importInfoResult = new ImportInfoResult();
		// 設定をインポートするクラスを外部キー依存関係順で並び替え
		importInfoResult.importClasses = getClassesOrderbyDependency(ExportService.EntityType.info);
		// インポートしないエンティティを取り除く
		importInfoResult.importClasses.remove(LocalgovInfo.class);
		importInfoResult.importClasses.remove(ClearinghousemetadataInfo.class);
		importInfoResult.importClasses.remove(CkanmetadataInfo.class);

		// インポート先自治体IDを指定した場合は存在チェック
		LocalgovInfo localgovInfoDest = null;
		if(localgovinfoidDest!=null) {
			localgovInfoDest = localgovInfoService.findById(localgovinfoidDest);
			if(localgovInfoDest==null) {
				throw new ServiceException(lang.__("Local gov. ID:{0} of import destination not exist.", localgovinfoidDest));
			}
		}

		try {
			// エクセルを開く
			POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(infoExportXsl));
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			// 自治体情報の読み込み
			@SuppressWarnings("unchecked")
			List<LocalgovInfo> localgovInfos = (List<LocalgovInfo>) excelImportInfoService.readRecords(wb, LocalgovInfo.class);
			// 1つしか入ってないことを確認
			if(localgovInfos.size()==0) {
				throw new ServiceException(lang.__("Local gov. info is not found."));
			}
			else if(1<localgovInfos.size()) {
				throw new ServiceException(lang.__("Unable to import due to more than one local gov. data exist."));
			}

			// インポート元の自治体情報の読み込み
			LocalgovInfo localgovInfoSrc = localgovInfos.get(0);
			// インポート先の自治体情報の読み込み(なければ作成)
			if(localgovInfoDest==null) {
				localgovInfoDest = Beans.createAndCopy(LocalgovInfo.class, localgovInfoSrc).execute();
				tableService.insertByEntity(localgovInfoDest);
			}
			// 自治体セットアッパーからのインポートの場合
			else {
				// 言語コードが未設定ならインポート
				if(localgovInfoDest.multilanginfoid==null && localgovInfoSrc.multilanginfoid!=null) {
					localgovInfoDest.multilanginfoid = localgovInfoSrc.multilanginfoid;
				}
				// 災害類型統合フラグをインポート
				if(localgovInfoSrc.disastercombined!=null) {
					localgovInfoDest.disastercombined = localgovInfoSrc.disastercombined;
				}
				localgovInfoService.update(localgovInfoDest);
			}
			importInfoResult.localgovinfoidDest = localgovInfoDest.id;
			// 自治体IDのマッピング
			{
				Map<Object, Object> map = new HashMap<Object, Object>();
				map.put(localgovInfoSrc.id, localgovInfoDest.id);
				logger.debug(lang.__("Local gov. ID : {0} import to {1}.", localgovInfoSrc.id, localgovInfoDest.id));
				importInfoResult.idConverter.put("localgovinfoid", map);
			}

			// 自治体IDが0ならば、mapmaster_info も ID=0 でインポートする
			if(localgovinfoidDest!=null && localgovinfoidDest.longValue()==0) {
				@SuppressWarnings("unchecked")
				List<MapmasterInfo> mapmasterInfos = (List<MapmasterInfo>) excelImportInfoService.readRecords(wb, MapmasterInfo.class);
				if(mapmasterInfos.size()==1) {
					MapmasterInfo mapmasterInfoSrc = mapmasterInfos.get(0);
					if(mapmasterInfoSrc.id.longValue()!=0) {
						throw new ServiceException(lang.__("Map master info (ID = 0) of local gov. info (ID = 0) not found."));
					}
					MapmasterInfo mapmasterInfoDest = Beans.createAndCopy(MapmasterInfo.class, mapmasterInfoSrc).execute();
					tableService.insertKeepId(mapmasterInfoDest);
					// 地図マスタ情報IDのマッピング
					{
						Map<Object, Object> map = new HashMap<Object, Object>();
						map.put(mapmasterInfoSrc.id, mapmasterInfoDest.id);
						importInfoResult.idConverter.put("mapmasterinfoid", map);
					}
					// 地図マスタ情報をインポート対象から除く
					importInfoResult.importClasses.remove(MapmasterInfo.class);
				}
				// 班情報はシステムメニューのために必要なので、 ID=0 として一旦インポートする（あとで上書きする）
				@SuppressWarnings("unchecked")
				List<GroupInfo> groupInfos = (List<GroupInfo>) excelImportInfoService.readRecords(wb, GroupInfo.class);
				if(groupInfos.size()==1) {
					GroupInfo groupInfoSrc = groupInfos.get(0);
					if(groupInfoSrc.id.longValue()!=0) {
						throw new ServiceException(lang.__("Menu setting info (ID=0) of municipality info ID=0 not found."));
					}
					GroupInfo groupInfoDest = Beans.createAndCopy(GroupInfo.class, groupInfoSrc).execute();
					tableService.insertKeepId(groupInfoDest);
					// メニュー設定情報IDのマッピング
					{
						Map<Object, Object> map = new HashMap<Object, Object>();
						map.put(groupInfoSrc.id, groupInfoDest.id);
						importInfoResult.idConverter.put("groupid", map);
						// メニュー設定情報をインポート対象から除く
						importInfoResult.importClasses.remove(GroupInfo.class);
					}
				}
				// システムメニュー menulogin_info.id=0 も、ID=0でインポートする
				@SuppressWarnings("unchecked")
				List<MenuloginInfo> menuloginInfos = (List<MenuloginInfo>) excelImportInfoService.readRecords(wb, MenuloginInfo.class);
				if(menuloginInfos.size()==1) {
					MenuloginInfo menuloginInfoSrc = menuloginInfos.get(0);
					if(menuloginInfoSrc.id.longValue()!=0) {
						throw new ServiceException(lang.__("Menu setting info (ID=0) of municipality info ID=0 not found."));
					}
					MenuloginInfo menuloginInfoDest = Beans.createAndCopy(MenuloginInfo.class, menuloginInfoSrc).execute();
					tableService.insertKeepId(menuloginInfoDest);
					// メニュー設定情報IDのマッピング
					{
						Map<Object, Object> map = new HashMap<Object, Object>();
						map.put(menuloginInfoSrc.id, menuloginInfoDest.id);
						importInfoResult.idConverter.put("menulogininfoid", map);
						// メニュー設定情報をインポート対象から除く
						importInfoResult.importClasses.remove(MenuloginInfo.class);
					}
				}
			}
			// 自治体IDが0以外の場合
			else {
				// システムテーブルマッピングがあれば読込
				if(systemtableMap!=null) {
					String key = "tablemasterinfoid";
					// idConverter 取得
					Map<Object, Object> tablemasterinfoidMap = importInfoResult.idConverter.get(key);
					if(tablemasterinfoidMap==null) tablemasterinfoidMap = new HashMap<Object, Object>();
					// ID変換マッピングをロード
					tablemasterinfoidMap.putAll(systemtableMap);
					// idConverter 設定しなおす
					importInfoResult.idConverter.put(key, tablemasterinfoidMap);
				}
			}

			// インポート実行
			try {
				// インポート順をソート
				Collections.sort(importInfoResult.importClasses, orderByImportComparator);
				excelImportInfoService.importBy(wb, importInfoResult);

				// Ver2.0 で UnitInfo.localgovinfoid が追加されたため、
				// しかし、旧バージョンのインポートの場合、localgovinfoid の指定なしの状態となるため、自動付与する
				@SuppressWarnings("unchecked")
				List<UnitInfo> unitInfos = (List<UnitInfo>) importInfoResult.importResult.get(UnitInfo.class);
				for(UnitInfo unitInfo : unitInfos) {
					if(unitInfo.localgovinfoid==null) {
						unitInfo.localgovinfoid = localgovInfoDest.id;
						unitInfoService.update(unitInfo);
					}
				}
				// Ver2.0 では UnitInfo は UnitInfo だけでなく GroupInfo にも紐づくため groupid が必須となる。
				// しかし、旧バージョンのインポートの場合、groupid の指定なしの状態となるため、自動付与する
				@SuppressWarnings("unchecked")
				List<jp.ecom_plat.saigaitask.entity.db.UserInfo> userInfos = (List<jp.ecom_plat.saigaitask.entity.db.UserInfo>) importInfoResult.importResult.get(jp.ecom_plat.saigaitask.entity.db.UserInfo.class);
				for(jp.ecom_plat.saigaitask.entity.db.UserInfo userInfo : userInfos) {
					if(userInfo.groupid==null) {
						userInfo.groupid = importInfoResult.unitid2groupid.get(userInfo.unitid);
						userInfoService.update(userInfo);
					}
				}

				// V1.4形式の設定を V2.0 形式の設定にアップグレードする
				summarylistcolumnInfoService.upgradeToVer2();
			} catch(Exception e) {
				throw new ServiceException(lang.__("An error occurred while import local gov. settings."), e);
			}

			return importInfoResult;
		} catch(Exception e) {
			throw new ServiceException(lang.__("Import was interrupted."), e);
		}
	}

	/**
	 * エクセル帳票テンプレートファイルのインポートを行う
	 * @param importInfoResult
	 * @param mapRestoreResult
	 * @param exceltenplateZip
	 */
	private void importExceltemplate(ImportInfoResult importInfoResult, MapRestore.Result mapRestoreResult, Map<Long, Long> filterIdTable,  File exceltenplateZip){

		// 別スレッドで実行の場合は ThreadLocal の lang を使用する
		SaigaiTaskDBLang lang = SaigaiTaskDBLang.langThreadLocal.get();

		// エクセル帳票テンプレートファイルZIPアーカイブファイルがない場合は何もしない
		if(exceltenplateZip == null || ! exceltenplateZip.exists()){
			return;
		}

		try{
			// エクセル帳票テンプレートファイルZIPアーカイブファイルが空の場合は何もしない
			List<File> files = FileUtil.unzip(exceltenplateZip, "Windows-31J");
			if(files == null || files.size() <= 0){
				return;
			}

			// インポート先ディレクトリの存在確認をし、なければ作成しておく
			String destDirPath = application.getRealPath(Constants.EXCELLIST_BASEDIR + importInfoResult.localgovinfoidDest);
			File destDir = new File(destDirPath);
			if(!destDir.exists()){
				destDir.mkdirs();
			}

			// メニュー情報のマッピング情報を取得
			Map<Object,Object> menuInfoMap = importInfoResult.idConverter.get("menuinfoid");
			// エクセル帳票テンプレートファイル名パターン定義
			Pattern pattern = Pattern.compile(Constants.MENUINFOID_EXCELLIST_FILENAMEPATTERN);
			// 1ファイルずつ処理する
			for(File exceltemplateFile : files){
				// パターンに一致すればコピー
				Matcher matcher = pattern.matcher(exceltemplateFile.getName());
				if(matcher.matches()){
					// メニュー情報IDを変更して移行先ディレクトリへコピー
					String [] tempArray = exceltemplateFile.getName().split("_");
					String oldMenuinfoidByExceltemplatefile = tempArray[2];
					String newMenuinfoidStr = menuInfoMap.get(Long.parseLong(oldMenuinfoidByExceltemplatefile)).toString();
					String newExceltemplateFilePath = destDirPath + "/" + tempArray[0] + "_" + tempArray[1] + "_" + newMenuinfoidStr + "_" + tempArray[3];
					FileUtil.fileCopyByPath(exceltemplateFile.getAbsolutePath(), newExceltemplateFilePath);
					// 移行先エクセル帳票テンプレートファイルのレイヤIDを書き換える
					File newExceltemplatefile = new File(newExceltemplateFilePath);
					excellistService.convertLayerId(newExceltemplatefile, mapRestoreResult.layerIdTable, filterIdTable);
				}
			}
		}catch(Exception e){
			throw new ServiceException(lang.__("Import was interrupted."), e);
		}
	}
}
