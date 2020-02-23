/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.setupper;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.postgis.PGgeometry;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKTWriter;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.ExMapDB;
import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.FeatureResult.AttrResult;
import jp.ecom_plat.map.db.FeatureResult.FileResult;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.LayerInfo.TimeSeriesType;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.map.exception.EcommapConflictException;
import jp.ecom_plat.map.file.ContentsFile;
import jp.ecom_plat.map.util.FileUploadUtil;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.ListDto;
import jp.ecom_plat.saigaitask.dto.PageDto;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.HistorytableInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.admin.setupper.TimeDimensionForm;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.EdituserAttrService;
import jp.ecom_plat.saigaitask.service.HistoryTableService;
import jp.ecom_plat.saigaitask.service.MeteoricEarthQuakeService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.TrackdataidAttrService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.HistorytableInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.FileUploadService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.TimeUtil;

/**
 * 自治体セットアッパーの時系列化アクション
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class TimeDimensionAction extends AbstractSetupperAction {

	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected TablemasterInfoService tablemasterInfoService;
	@Resource protected TrackmapInfoService trackmapInfoService;
	@Resource protected TracktableInfoService tracktableInfoService;
	@Resource protected HistoryTableService historyTableService;
	@Resource protected HistorytableInfoService historytableInfoService;
	@Resource protected MapService mapService;
	@Resource protected TableService tableService;
	@Resource protected FileUploadService fileUploadService;
	@Resource protected ClearinghouseService clearinghouseService;
	@Resource protected GroupInfoService groupInfoService;
	@Resource protected TrackdataidAttrService trackdataidAttrService;
	@Resource protected EdituserAttrService edituserAttrService;
	@Resource protected MeteoricEarthQuakeService meteoricEarthQuakeService;

	// ActionForm
	TimeDimensionForm timeDimensionForm;

	// for JSP
	/** 災害情報リスト */
	//public List<DisasterMaster> disasterItems;
	/** 災害地図情報リスト */
	public List<TrackmapInfo> trackmapInfos;

	/** 変換した時系列マップ */
	public MapInfo timemapInfo;
	/** 変換した時系列マップのeコミ地図画面 */
	public String timemapLink;
	/** 地図リスト */
	public ListDto mapListDto;
	/** 履歴レイヤリスト */
	public EcommapHistoryContentsLayerListDto historyLayerListDto;
	/** PageDto */
	public PageDto pageDto;

	// for convert
	/** レイヤID変換マップ<マスタレイヤID, 時系列レイヤID> */
	public Map<String, String> layerIdMap = null;

	DateFormat dfYYYY_MM_DD_HH_MM_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("trackmapInfos", trackmapInfos);
		model.put("timemapInfo", timemapInfo);
		model.put("timemapLink", timemapLink);
		model.put("mapListDto", mapListDto);
		model.put("historyLayerListDto", historyLayerListDto);
		model.put("pageDto", pageDto);
		model.put("layerIdMap", layerIdMap);

	}
	/**
	 * 時系列化画面
	 *
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/setupper/timeDimension","/admin/setupper/timeDimension/index"})
	public String index(Map<String,Object>model,
			@Valid @ModelAttribute TimeDimensionForm timeDimensionForm, BindingResult bindingResult) {
		this.timeDimensionForm = timeDimensionForm;
		// セットアッパーの初期化
		initSetupper();

		// 表示内容の取得
		content(model, timeDimensionForm, bindingResult);

		setupModel(model);
		return "/admin/setupper/timeDimension/index";
	}

	/**
	 * 時系列化画面.
	 *
	 * @return 表示ページ
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/timeDimension/content")
	public String content(Map<String,Object>model,
			@Valid @ModelAttribute TimeDimensionForm timeDimensionForm, BindingResult bindingResult) {
		this.timeDimensionForm = timeDimensionForm;
		try {
			if(loginDataDto.isAdmin()==false) {
				throw new ServiceException(lang.__("Admin privilege is required."));
			}

			MapDB mapDB = MapDB.getMapDB();
			/*
			// 時系列機能が有効になっているかチェック
			try {
				if("HISTORY".equals(mapDB.getOption("TIME_SERIES_TYPE"))==false) {
					throw new ServiceException("eコミマップの時系列機能が有効になっていません。");
				}
			} catch(Exception e) {
				if(e instanceof ServiceException) throw (ServiceException) e;
				throw new ServiceException("eコミマップの時系列機能の設定状況を取得できませんでした。", e);
			}
			*/

			long localgovinfoid = loginDataDto.getLocalgovinfoid();
			if(localgovinfoid==0) throw new ServiceException(lang.__("Select local gov.<!--2-->"));

			// マスタマップ情報
			MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
			if(mapmasterInfo==null) throw new ServiceException(lang.__("No master map is configured."));
			MapInfo masterMapInfo = mapDB.getMapInfo(mapmasterInfo.mapid);

			// 震度レイヤ情報
			List<String> earthquakeLayerList = meteoricEarthQuakeService.earthquakeLayerDataList();

			// マスタマップレイヤー一覧
			historyLayerListDto = new EcommapHistoryContentsLayerListDto(lang, mapmasterInfo.mapid, earthquakeLayerList);
			for(String msg : historyLayerListDto.msgs) {
				addRequestErrorMessage(bindingResult, msg);
			}

			// マップ一覧
			{
				ListDto listDto = mapListDto = new ListDto();
				listDto.styleId = "ecommap_map_list";
				listDto.title = lang.__("Map list");

				//
				// 列情報の設定
				//
				listDto.columnNames.add(lang.__("Type<!--2-->"));
				listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "text");
				listDto.columnNames.add(lang.__("Map title<!--2-->"));
				listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "text");
				listDto.columnNames.add(lang.__("History layer transformation"));
				listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "number");
				//listDto.columnNames.add(lang.__("Integration"));
				//listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "text");

				String anchorFormat = "<a target='ecommap' href='"+Config.getEcommapURL()+"map/map/?cid=%d&amp;gid=%d&amp;mid=%d'>%s</a>";
				List<Long> addedMapIds = new ArrayList<Long>();
				// マスターマップ
				{
					// リストへ一行追加
					List<String> values = new ArrayList<String>();
					values.add(lang.__("Master map<!--2-->"));
					//
					values.add(String.format(anchorFormat, mapmasterInfo.communityid, mapmasterInfo.mapgroupid, mapmasterInfo.mapid, masterMapInfo.title).toString());
					// 列：時系列レイヤ変換
					values.add(historyLayerListDto.containsNONETimeSeriesType?lang.__("Not yet transformation"):lang.__("Conversion completed"));
					//// 列：統合
					//values.add("-");
					listDto.columnValues.add(values);
					addedMapIds.add(mapmasterInfo.mapid);
				}

				// 災害マップ
				trackmapInfos = trackmapInfoService.findByLocalgovInfoIdAndTrackDataNotDelete(loginDataDto.getLocalgovinfoid());
				for(TrackmapInfo trackmapInfo : trackmapInfos) {
					if(addedMapIds.contains(trackmapInfo.mapid)) continue;
					boolean isTraining = trackmapInfo.trackDatas.get(0).isTraining();

					MapInfo tmapInfo = mapDB.getMapInfo(trackmapInfo.mapid);

					// リストへ一行追加
					List<String> values = new ArrayList<String>();
					values.add(isTraining ? lang.__("Drill map") : lang.__("Disaster map")+"<input type='hidden' name='trackmapinfoIds' value='"+trackmapInfo.id+"' checked></input>");
					values.add(String.format(anchorFormat, mapmasterInfo.communityid, mapmasterInfo.mapgroupid, trackmapInfo.mapid, tmapInfo.title).toString());

					// 列：時系列レイヤ変換
					boolean notupgradeyet = historyTableService.checkIfHistorytableExists(trackmapInfo.id, mapmasterInfo.id);
					if(notupgradeyet) {
						values.add(lang.__("Not yet transformation"));
					}
					else {
						values.add(lang.__("Conversion completed"));
					}

					// 列：統合
					//if(isTraining) values.add("-");
					//else values.add(notupgradeyet ? "<input type='checkbox' name='trackmapinfoIds' value='"+trackmapInfo.id+"' checked></input>" : "-");

					listDto.columnValues.add(values);
					addedMapIds.add(trackmapInfo.mapid);
				}
			}


		} catch(Exception e) {
			// エラーをキャッチしたら、ログ出力、ユーザへのエラーメッセージを追加
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Can't run history transformation."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			//ActionMessagesUtil.addErrors(SpringContext.getRequest(), errors);
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}
		setupModel(model);
		return "/admin/setupper/timeDimension/content";
	}

	protected MapInfo convertTimeDimensionMap(Long srcMapId) {
		try {
			MapDB mapDB = MapDB.getMapDB();

			MapInfo srcMapInfo = mapDB.getMapInfo(srcMapId);
			long dstMapId = 0;
			MapInfo dstMapInfo = null;

			// MapInfoを複製する（登録情報レイヤはすべて複製してレイヤを作成する）
			{
				//eコミマップ地図作成
				UserInfo userInfo = mapDB.getUserInfo(srcMapInfo.userId);
				dstMapId = mapService.createMap(srcMapInfo.mapId, srcMapInfo.groupId, timeDimensionForm.mapname, srcMapInfo.communityId, userInfo.authId, null);
				dstMapInfo = mapDB.getMapInfo(dstMapId);

				int size = srcMapInfo.countMapLayerInfo();
				for(int idx=0; idx<size; idx++) {
					MapLayerInfo mapLayerInfo = srcMapInfo.getMapLayerInfo(idx);
					LayerInfo srcLayerInfo = mapDB.getLayerInfo(mapLayerInfo.layerId, true);
					// すでに削除済みならスキップ
					if(srcLayerInfo==null) continue;

					// 登録情報レイヤなら複製
					if(srcLayerInfo.getGroupType()==LayerInfo.GROUPTYPE_LOCAL) {
						boolean cloneFeatures = true;
						switch (srcLayerInfo.type) {
						case LayerInfo.TYPE_LOCAL_GROUP:
						case LayerInfo.TYPE_REFERENCE_GROUP:
						case LayerInfo.TYPE_OVERLAY_GROUP:
						case LayerInfo.TYPE_BASE_GROUP:
							cloneFeatures = false;
						case LayerInfo.TYPE_LOCAL:
							// レイヤの複製
							LayerInfo dstLayerInfo = null;
							try {
								String srcLayerId = srcLayerInfo.layerId;
								int communityId = srcMapInfo.communityId;
								int dstGroupId = srcMapInfo.groupId;
								int srcCommunityId = srcMapInfo.communityId;
								int newOwnerId = userInfo.userId;
								String dstLayerId = mapDB.cloneLayer(communityId, dstGroupId, dstMapInfo, srcCommunityId, srcMapId, srcLayerId, newOwnerId, cloneFeatures);
								dstLayerInfo = mapDB.getLayerInfo(dstLayerId);
								//mapDB.updateLayerInfo(dstLayerInfo);
								logger.info(lang.__("Create \"{0}\"layer({1}) after copying from \"{2}\"layer({3})", dstLayerInfo.name, dstLayerInfo.layerId, srcLayerInfo.name, srcLayerInfo.layerId));

								// 親レイヤＩＤを変換
								MapLayerInfo dstMapLayerInfo = mapDB.getMapLayerInfo(dstMapInfo.mapId, dstLayerId);
								dstMapLayerInfo.parent = layerIdMap.get(dstMapLayerInfo.parent);
								mapDB.updateMapLayerInfo(dstMapInfo, dstMapLayerInfo);

								// レイヤID変換マップを更新
								layerIdMap.put(srcLayerInfo.layerId, dstLayerInfo.layerId);

							} catch(Exception e) {
								throw new ServiceException(lang.__("Failed to copy layer."), e);
							}

							if(LayerInfo.TYPE_LOCAL==srcLayerInfo.type) {
								// 登録情報レイヤを履歴レイヤに全て変換する
								dstLayerInfo.timeSeriesType = TimeSeriesType.HISTORY;
								mapDB.setTimeSeriesType(dstLayerInfo);
								//DBの_layerを更新 属性情報は更新しない
								mapDB.updateLayerInfo( dstLayerInfo );
							}

							break;
						}
					}
					// 登録情報レイヤ以外は引用
					else {
						mapDB.shareLayer(dstMapInfo, srcMapInfo.communityId, srcMapInfo.mapId, srcLayerInfo.layerId);
					}
				}

				// 地図の更新日時変更
				mapDB.updateMapInfoModified(dstMapInfo, new Timestamp(System.currentTimeMillis()));

			}

			return dstMapInfo;
		} catch(Exception e) {
			throw new ServiceException(lang.__("Failed to transform into map with history."), e);
		}
	}

	/**
	 * @param timemapInfo 時系列マップ（インポート先）
	 * @param trackmapInfo インポート対象災害マップ
	 * @param deleteHistory インポートした災害データの削除実行フラグ
	 * @param upgradeMasterMap 地図マスタの更新実行フラグ
	 * @param resetTimeParam 災害終了時にリセットデータとして用いる時刻
	 * @return インポート結果フラグ
	 */
	protected boolean importHistoryToTimeDimensionMap(MapInfo timemapInfo, TrackmapInfo trackmapInfo, boolean deleteHistory, boolean upgradeMasterMap, Date[] resetTimeParam) {
		boolean success = false;

		try {
			MapDB mapDB = MapDB.getMapDB();
			//MapInfo mapInfo = mapDB.getMapInfo(trackmapInfo.mapid);
			UserInfo userInfo = mapDB.getAuthIdUserInfo("admin");
			Map<String, GroupInfo> groupInfoCache = new HashMap<>();

			long localgovinfoid = loginDataDto.getLocalgovinfoid();

			WKBReader wkbReader = new WKBReader();
			WKTWriter wktWriter = new WKTWriter();

			// 災害終了フラグ
			boolean isCompleted = true;
			Timestamp endtime = null;
			for(TrackData trackData : trackmapInfo.trackDatas) {
				if(trackData.endtime==null) {
					isCompleted = false;
					break;
				}
				else {
					if(endtime==null || endtime.before(trackData.endtime)) {
						endtime = trackData.endtime;
					}
				}
			}

			// テーブルマスタのレイヤ
			List<TablemasterInfo> tablemasterInfos = tablemasterInfoService.findByLocalgovinfoid(localgovinfoid);
			for(TablemasterInfo tablemasterInfo : tablemasterInfos) {
				// レイヤかつコピーフラグを対象とする
				if(StringUtil.isEmpty(tablemasterInfo.layerid) || TablemasterInfo.COPY_SHARE.equals(tablemasterInfo.copy)) continue;

				// テーブルマスタのレイヤIDから時系列マップのレイヤIDを取得
				String dstLayerId = layerIdMap!=null ? layerIdMap.get(tablemasterInfo.layerid) : tablemasterInfo.layerid;
				LayerInfo layerInfo = mapDB.getLayerInfo(dstLayerId);

				// 必須属性がなければ追加
				trackdataidAttrService.alterTableAddTrackdataidColumnIfNotExists(layerInfo.layerId);
				edituserAttrService.alterTableAddEdituserColumnIfNotExists(layerInfo.layerId);

				// 地物のorgId マップ
				Map<Long, Long> orgIdMap = new HashMap<Long, Long>();
				// マスタマップのレイヤにすでに登録されている地物は、災害レイヤの方の同gid と同じものとみなす
				LayerInfo masterLayerInfo = mapDB.getLayerInfo(tablemasterInfo.layerid);
				List<String> mastergidList = tableService.selectDistinct(tablemasterInfo.layerid, TimeSeriesType.HISTORY.equals(masterLayerInfo.timeSeriesType) ? "_orgid" : "gid");
				for(String mastergidStr : mastergidList) {
					if(StringUtil.isEmpty(mastergidStr)) continue;
					Long mastergid = Long.valueOf(mastergidStr);
					orgIdMap.put(mastergid, mastergid);
				}

				TracktableInfo tracktableInfo = tracktableInfoService.findByTrackmapInfoIdAndTablemasterinfoid(trackmapInfo.id, tablemasterInfo.id);
				// 後から追加したテーブルマスタ情報の場合は、過去の災害の tracktable_info がない場合がある
				if(tracktableInfo==null) {
					logger.info(lang.__("Skip history data import of history map due to history table {1} of {0} not found.", tablemasterInfo.name, "tracktable_info"));
					continue;
				}
				HistorytableInfo historytableInfo = historytableInfoService.findByTracktableInfo(tracktableInfo);
				if(historytableInfo==null || tableService.isExists(historytableInfo.historytablename)==false) {
					logger.info(lang.__("Skip history data import of history map due to history table {1} of {0} not found.", tablemasterInfo.name, (historytableInfo!=null?"\""+historytableInfo.historytablename+"\"":"")));
					continue;
				}

				LayerInfo srcLayerInfo = mapDB.getLayerInfo(tracktableInfo.layerid);

				// 地物ＩＤを取得する
				List<String> gidList = tableService.selectDistinct(historytableInfo.historytablename, "gid");
				for(String gidStr : gidList) {
					if(StringUtil.isEmpty(gidStr)) continue;

					Long gid = Long.valueOf(gidStr);

					// 添付ファイル
					Vector<FileResult> fileResults = FeatureDB.getFeatureFileList(srcLayerInfo, gid, null);
					Map<String, String> filePathMap = new HashMap<String, String>();
					// 添付ファイルをすべてコピー
					for(FileResult fileResult : fileResults) {
						try {
							// 添付ファイルのファイル自体をコピーする
							String fileUrl = fileResult.url;
							if(fileUrl.startsWith("/")) {
								File file = new File(fileUploadService.getEcommapRealPath("/.."+fileUrl));
								String path = file.getPath();

								// <div lang="ja">PATH名を除くファイル名のみを取得</div>
								//<div lang="en">Get filename without PATH</div>
								String fileName = file.getName();
								logger.info("Uploaded File : "+fileName);

								//<div lang="ja">拡張子取得</div>
								//<div lang="en">Get file extension</div>
								String fileBaseName = "";
								String ext = "";
								if (!path.endsWith("/")) path += "/";
								//<div lang="ja">.付き</div>
								//<div lang="en">. position</div>
								int dotIdx = fileName.lastIndexOf('.');
								if (dotIdx > 0) {
									fileBaseName = fileName.substring(0,dotIdx);
									ext = fileName.substring(dotIdx+1);
								}

								// コピー先の取得
								String dstPath = ContentsFile.getContentsDirUrl(userInfo.userId, dstLayerId);
								String uploadPath = fileUploadService.getEcommapRealPath(dstPath);
								File dstFile = FileUploadUtil.getUniqFile(uploadPath, fileBaseName, ext);
								File dstDir = dstFile.getParentFile();
								if(dstDir.exists()==false) {
									dstDir.mkdirs();
								}
								if(dstFile.exists()==false) {
									dstFile.createNewFile();
								}

								// コピー実施
								FileCopyUtils.copy(file, dstFile);

								// URL 変更
								fileUrl = "/map"+dstPath+dstFile.getName();

								filePathMap.put(fileResult.url, fileUrl);
							}
						} catch(Exception e) {
							logger.warn(lang.__("Failed to copy attached file.")+" "+srcLayerInfo.layerId+"."+gid+" "+fileResult.title, e);
						}
					}

					// 履歴のインポート
					List<BeanMap> logs = historyTableService.getLogByIDSorted(historytableInfo, gid);
					if(logs.size()==0) {
						logger.info(lang.__("Skip history data import of history map due to no log recorded in {0} history table \"{1}\".", tablemasterInfo.name, historytableInfo.historytablename));
						continue;
					}
					// 過去からログを取得するために逆順で走査
					for(int logsIdx=logs.size()-1; 0<=logsIdx; logsIdx--) {
						 BeanMap log = logs.get(logsIdx);

						// 属性の構築
						HashMap<String, String> attributes = new HashMap<String, String>();
						for(AttrInfo attrInfo : layerInfo.getAttrIterable()) {
							if(log.containsKey(attrInfo.attrId)) {
								attributes.put(attrInfo.attrId, (String) log.get(attrInfo.attrId));
							}
						}
						// timeFrom をログ時刻に設定
						Timestamp logTime = (Timestamp) log.get("logTime");
						// UTC対応eコミなら UTC でフォーマット
						dfYYYY_MM_DD_HH_MM_SS.setTimeZone(Config.isAvailableUTCTimeZone() ? TimeZone.getTimeZone("UTC") : TimeZone.getDefault());
						attributes.put("time_from", dfYYYY_MM_DD_HH_MM_SS.format(logTime));

						// 編集者 st_edituser
						String logGroupIdStr = (String) log.get("groupid");
						GroupInfo groupInfo = groupInfoCache.get(logGroupIdStr);
						if(groupInfo==null && StringUtil.isNotEmpty(logGroupIdStr)) {
							try {
								groupInfo = groupInfoService.findById(Long.parseLong(logGroupIdStr));
								groupInfoCache.put(logGroupIdStr, groupInfo);
							} catch(Exception e) {
								logger.error("history table groupid column parse fail", e);
							}
						}
						// 編集者を設定
						attributes.put(EdituserAttrService.EDITUSER_ATTR_ID, groupInfo!=null ? groupInfo.name : ""/*不明な場合*/);

						// ジオメトリWKT の取得
						String wkt = null;
						Object theGeom = log.get("theGeom");
						if(theGeom instanceof PGgeometry) {
							PGgeometry pggeom = (PGgeometry) theGeom;
							wkt = pggeom.toString();
						}
						else if(theGeom!=null) {
							try {
								Geometry geom = wkbReader.read(WKBReader.hexToBytes(theGeom.toString()));
								wkt = wktWriter.write(geom);
							}catch(Exception e) {
								String msg = "WKBReader error: the_geom=\""+theGeom+"\"";
								logger.error(msg, e);
								throw new ServiceException(msg, e);
							}
						}

						// orgId の確認
						Long orgId = orgIdMap.get(gid);
						long newFeatureId = 0;

						// まだ追加されていなければ新規追加
						try {
							if(orgId==null) {
								newFeatureId = FeatureDB.insertFeature(userInfo, layerInfo.layerId, wkt, attributes);
								orgIdMap.put(gid, newFeatureId);
								logger.info(lang.__("Add {0} into {1} by history data import of history map. gid={2}", historytableInfo.historytablename+"."+gid, layerInfo.layerId+":"+layerInfo.name, newFeatureId));
							}
							// 履歴の挿入
							else {
								newFeatureId = FeatureDB.insertFeatureHistory(layerInfo, orgId, wkt, attributes, userInfo);
								logger.info(lang.__("Insert {0} into {1} by history data import of history map. gid={2}", historytableInfo.historytablename+"."+gid, layerInfo.layerId+":"+layerInfo.name, newFeatureId));
							}
						} catch(EcommapConflictException e) {
							logger.warn(lang.__("Skip history import by importing {0} into {1} due to conflict.", historytableInfo.historytablename+"."+gid, layerInfo.layerId+":"+layerInfo.name));
						}

						// 添付ファイル
						for(FileResult fileResult : fileResults) {
							try {
								// アップロード時間があればチェックする
								// アップロード時間以前には添付しない
								if(StringUtil.isNotEmpty(fileResult.time_upload)) {
									// ローカルタイムでパース
									dfYYYY_MM_DD_HH_MM_SS.setTimeZone(TimeZone.getDefault());
									Date uploadTime = dfYYYY_MM_DD_HH_MM_SS.parse(fileResult.time_upload);
									if(uploadTime.after(logTime)) continue;
								}

								// コピーした場合はコピー後のパスを取得する
								String fileUrl = fileResult.url;
								if(fileUrl.startsWith("/")) {
									fileUrl = filePathMap.get(fileUrl);
								}

								// 添付ファイルの設定
								String fileTitle = fileResult.title;
								String serverRootUrl = fileUploadService.getEcommapServerRootUrl();
								mapDB.insertFeatureFile(dstLayerId, newFeatureId, timemapInfo.mapId, userInfo.userId, fileUrl, fileTitle, serverRootUrl);
							} catch(Exception e) {
								logger.warn(lang.__("Failed history transformation of attached file.", srcLayerInfo.layerId+"."+gid, fileResult.title), e);
							}
						}
					}

					// 災害終了なら地物を削除する
					if(isCompleted) {
						try {
							// マスタにあるならマスタの地物でリセット
							if(mastergidList.contains(String.valueOf(gid))) {
								boolean attrGrouped = false;
								int resultGeomType = FeatureDB.GEOM_TYPE_GEOM;
								FeatureResult feature = FeatureDB.getFeatureContent(masterLayerInfo, gid, attrGrouped, resultGeomType, /*bbox*/null, resetTimeParam);

								// 属性の構築
								HashMap<String, String> attributes = new HashMap<String, String>();
								for(AttrInfo attrInfo : layerInfo.getAttrIterable()) {
									if(feature==null) {
										logger.debug("feature is null");
									}
									AttrResult attrResult = feature.getAttrResult(attrInfo.attrId);
									attributes.put(attrInfo.attrId, attrResult!=null ? attrResult.getAttrValue() : null);
								}
								// UTC対応eコミなら UTC でフォーマット
								dfYYYY_MM_DD_HH_MM_SS.setTimeZone(Config.isAvailableUTCTimeZone() ? TimeZone.getTimeZone("UTC") : TimeZone.getDefault());
								attributes.put("time_from", dfYYYY_MM_DD_HH_MM_SS.format(new Date(endtime.getTime()+1000L))); // リセット履歴の time_from は 災害終了時刻に 1s 足す(ミリ秒切捨てされるため)
								FeatureDB.insertFeatureHistory(layerInfo, gid, feature.getWKT(), attributes, userInfo);
							}
							// マスタにない地物なら災害終了日で削除
							// 災害中に削除した地物は削除ログがないため削除日時が不明なので、災害終了日を削除日時とする。
							else {
								Long orgId = orgIdMap.get(gid);
								// UTC対応版なら削除日時もUTC時刻をセットする
								FeatureDB.deleteFeature(userInfo, dstLayerId, orgId, null, new Date((Config.isAvailableUTCTimeZone() ? TimeUtil.newUTCDate(endtime.getTime()) : endtime).getTime()+1000L)); // 災害終了時刻に 1s 足す(ミリ秒切捨てされるため)
							}
						} catch(EcommapConflictException e) {
							logger.warn(lang.__("Skip history import after disaster completion due to conflict.", historytableInfo.historytablename+"."+gid, layerInfo.layerId+":"+layerInfo.name));
						}
					}
				}

				// マスタマップの時系列アップグレードならば
				if(upgradeMasterMap) {
					// tracktable_info の更新
					tracktableInfo.layerid = tablemasterInfo.layerid;
					tracktableInfo.tablename = tablemasterInfo.tablename;
					tracktableInfoService.update(tracktableInfo);
				}
				// 履歴テーブルの削除
				if(deleteHistory) {
					historyTableService.dropTable(historytableInfo);
					historytableInfoService.delete(historytableInfo);
					// 誤ってマスタレイヤを削除してしまわないように、レイヤIDが違う場合のみレイヤ削除する。
					if(!srcLayerInfo.layerId.equals(tablemasterInfo.layerid) && !!srcLayerInfo.layerId.equals(dstLayerId)) {
						// レイヤの削除
						ExMapDB.deleteLayerInfo(srcLayerInfo);
					}
				}
			}

			// マスタマップの時系列アップグレードならば
			if(upgradeMasterMap) {
				// 震度レイヤは先に実施（後で実施すると災害マップが消えてしまうので）
				meteoricEarthQuakeService.earthquakeTimeDimension(mapDB, localgovinfoid);
				// インポートした地図は削除する。
				MapInfo mapInfo = mapDB.getMapInfo(trackmapInfo.mapid);
				mapInfo.status = MapInfo.STATUS_DELETED;
				mapDB.updateMapInfo(mapInfo);
				// 記録地図情報をマスタマップを参照するように変更する
				MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
				trackmapInfo.mapid = mapmasterInfo.mapid;
				trackmapInfoService.update(trackmapInfo);
			}

			success = true;
		} catch(Exception e) {
			throw new ServiceException(lang.__("History data can't be imported into history map: {0}", e.getMessage()), e);
		}

		return success;
	}

	/**
	 * 時系列化変換する
	 * @return 結果ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/timeDimension/upgrade")
	public String upgrade(Map<String,Object>model,
			@Valid @ModelAttribute TimeDimensionForm timeDimensionForm, BindingResult bindingResult) {
		this.timeDimensionForm = timeDimensionForm;

		try {
			long localgovinfoid = loginDataDto.getLocalgovinfoid();
			if(localgovinfoid==0) throw new ServiceException(lang.__("Select local gov.<!--2-->"));

			MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
			if(mapmasterInfo==null) throw new ServiceException(lang.__("No master map is configured."));

			// 災害マップのコピーをしないように設定
			mapmasterInfo.copy = false;
			mapmasterInfoService.update(mapmasterInfo);

			MapDB mapDB = MapDB.getMapDB();

			// eコミマップの時系列履歴管理を有効化する（サイト単位で有効かはできない）
			mapDB.setOption("TIME_SERIES_TYPE", TimeSeriesType.HISTORY.toString());

			// マスターマップの時系列化
			{
				// MapLayerInfo
				MapInfo mapInfo = mapDB.getMapInfo(mapmasterInfo.mapid);
				upgradeHistoryMap(mapInfo);

				// 指定した災害マップをマスターマップに統合
				Date[] resetTimeParam = new Date[]{new Date(0L)}; // マスタマップは -infinity で保持されているのでその時間帯でリセットさせる
				String msg = null;
				if(timeDimensionForm.trackmapinfoIds!=null) {
					for(String trackmapinfoidStr : timeDimensionForm.trackmapinfoIds) {
						try {
							Long trackmapinfoid = Long.valueOf(trackmapinfoidStr);
							TrackmapInfo trackmapInfo = trackmapInfoService.findByIdLeftJoinNotDeletedTrackDatas(trackmapinfoid);

							MapInfo tmapInfo = mapDB.getMapInfo(trackmapInfo.mapid);

							boolean success = importHistoryToTimeDimensionMap(mapInfo, trackmapInfo, /*deleteHistory*/ true, /*upgradeMasterMap*/ true, resetTimeParam);
							if(success) msg = lang.__("Get disaster history data from \"{0}\"", tmapInfo.title);
							else        msg = lang.__("Failed to get disaster history data from \"{0}\"", tmapInfo.title);
							logger.info(msg);
							addRequestMessage(msg);
						} catch (Exception e) {
							msg = "error importHistoryToTimeDimensionMap trackmapinfoid=\""+trackmapinfoidStr+"\": "+e.getMessage();
							logger.error(msg, e);
							addRequestErrorMessage(bindingResult, msg);
						}
					}
				}
			}

			// そのほかのマップの時系列化
			{
				// 災害マップ
				trackmapInfos = trackmapInfoService.findByLocalgovInfoIdAndTrackDataNotDelete(loginDataDto.getLocalgovinfoid());
				for(TrackmapInfo trackmapInfo : trackmapInfos) {
					// マスターマップに統合済みのものはスキップ
					if(mapmasterInfo.mapid.equals(trackmapInfo.mapid)) continue;

					MapInfo tmapInfo = mapDB.getMapInfo(trackmapInfo.mapid);
					upgradeHistoryMap(tmapInfo);

					// _history をインポートして削除
					importHistoryToTimeDimensionMap(tmapInfo, trackmapInfo, /*deleteHistory*/ true, /*upgradeMasterMap*/ false, /*resetTimeParam*/null);
				}
			}

			// クリアリングハウス、メタデータ更新
			clearinghouseService.updatemetadataAll();

		} catch(Exception e) {
			// エラーをキャッチしたら、ログ出力、ユーザへのエラーメッセージを追加
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Can't run history transformation."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		setupModel(model);
		return "forward:/admin/setupper/timeDimension/";
	}

	protected void upgradeHistoryMap(MapInfo mapInfo) {
		try {
			MapDB mapDB = MapDB.getMapDB();
			// 登録情報レイヤを履歴レイヤに変換する
			for(MapLayerInfo mapLayerInfo : mapInfo.getMapLayerIterable()) {
				if(mapLayerInfo.getLayerType()!=LayerInfo.TYPE_LOCAL) continue;
				// 震度レイヤは何もしない（後で纏めて時系列化するので）
				if(meteoricEarthQuakeService.isExistLayerId(mapLayerInfo.layerId)) continue;

				LayerInfo layerInfo = mapDB.getLayerInfo(mapLayerInfo.layerId);
				// すでにレイヤ情報が削除されていて地図レイヤ情報に残っている場合は飛ばす。
				if(layerInfo==null) continue;

				// 履歴レイヤに変換
				if(layerInfo.timeSeriesType==null || layerInfo.timeSeriesType.equals(TimeSeriesType.NONE)) {
					// 登録情報レイヤを履歴レイヤに全て変換する
					layerInfo.timeSeriesType = TimeSeriesType.HISTORY;
					mapDB.setTimeSeriesType(layerInfo);
					//DBの_layerを更新 属性情報は更新しない
					mapDB.updateLayerInfo( layerInfo );
				}
			}
		} catch(Exception e) {
			throw new ServiceException("faild to upgradeHistoryMap _mapid="+mapInfo.mapId+", title=\""+mapInfo.title+"\"", e);
		}
	}

	/**
	 * 履歴登録情報レイヤリスト
	 */
	public static class EcommapHistoryContentsLayerListDto extends ListDto {

		/** アラームメッセージリスト */
		public List<String> msgs = new ArrayList<String>();

		/** 履歴レイヤでないものを含んでいるか */
		public Boolean containsNONETimeSeriesType = false;

		/**
		 * 指定したeコミマップの地図の履歴登録情報レイヤリストを作成する
		 * @param mapId 地図ID
		 */
		public EcommapHistoryContentsLayerListDto(SaigaiTaskDBLang lang, Long mapId, List<String> earthquakeLayerList) {
			MapDB mapDB = MapDB.getMapDB();

			// リストの作成
			EcommapHistoryContentsLayerListDto listDto = this; //new ListDto();
			listDto.styleId = "ecommap_mapid"+mapId+"_list";
			listDto.title = lang.__("Master map layer list");

			//
			// 列情報の設定
			//
			listDto.columnNames.add(lang.__("Type<!--2-->"));
			listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "text");
			listDto.columnNames.add(lang.__("Layer ID"));
			listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "text");
			listDto.columnNames.add(lang.__("Layer name<!--2-->"));
			listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "text");
			listDto.columnNames.add(lang.__("History layer transformation"));
			listDto.typeItems.put(listDto.columnNames.get(listDto.columnNames.size()-1), "number");
			// MapLayerInfo
			MapInfo mapInfo = mapDB.getMapInfo(mapId);
			if(mapInfo==null) {
				msgs.add(lang.__("mapID={0} not found.", mapId));
			}
			else {
				for(MapLayerInfo mapLayerInfo : mapInfo.getMapLayerIterable()) {
					if(mapLayerInfo.getLayerType()!=LayerInfo.TYPE_LOCAL) continue;
					// 震度レイヤはリストに出さない
					if(earthquakeLayerList.contains(mapLayerInfo.layerId)) continue;

					// リストへ一行追加
					List<String> values = new ArrayList<String>();
					values.add(lang.__("Layer info"));
					values.add(mapLayerInfo.layerId);
					values.add(mapLayerInfo.layerName);

					LayerInfo layerInfo = mapDB.getLayerInfo(mapLayerInfo.layerId);
					// すでにレイヤ情報が削除されていて地図レイヤ情報に残っている場合は飛ばす。
					if(layerInfo==null) continue;
					if(layerInfo.timeSeriesType==null || layerInfo.timeSeriesType.equals(TimeSeriesType.NONE)) {
						values.add(lang.__("Not yet transformation"));
						listDto.containsNONETimeSeriesType = true;
					}
					else if(layerInfo.timeSeriesType.equals(TimeSeriesType.HISTORY)) {
						values.add(lang.__("Conversion completed"));
					}
					// その他の場合は無視？
					else {
						values.add(String.valueOf(layerInfo.timeSeriesType));
					}

					listDto.columnValues.add(values);
				}
			}

			// 履歴レイヤでないものがあるので、エラーメッセージを表示
			if(listDto.containsNONETimeSeriesType) {
				msgs.add(lang.__("Not yet transformed history layer exists. Run history transformation."));
			}
		}
	}
}
