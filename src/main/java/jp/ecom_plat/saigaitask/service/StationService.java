/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.UserInfo;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.dto.AlerttriggerInfoDto;
import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.dto.PageDto;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageInfo;
import jp.ecom_plat.saigaitask.entity.db.HistorytableInfo;
import jp.ecom_plat.saigaitask.entity.db.StationalarmInfo;
import jp.ecom_plat.saigaitask.entity.db.StationclassInfo;
import jp.ecom_plat.saigaitask.entity.db.StationlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageInfoService;
import jp.ecom_plat.saigaitask.service.db.StationalarmInfoService;
import jp.ecom_plat.saigaitask.service.db.StationclassInfoService;
import jp.ecom_plat.saigaitask.service.db.StationlayerInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;

/**
 * 体制のサービスクラス
 */
@org.springframework.stereotype.Service
public class StationService extends BaseService {

	Logger logger = Logger.getLogger(StationService.class);

	// サービス
	/** テーブルサービス */
	@Resource protected TableService tableService;
	/** 記録データサービス */
	@Resource protected TrackDataService trackDataService;
	/** 記録テーブル情報サービス */
	@Resource protected TracktableInfoService tracktableInfoService;
	/** 体制区分情報サービス */
	@Resource protected StationclassInfoService stationclassInfoService;
	/** 体制レイヤ情報サービス */
	@Resource protected StationlayerInfoService stationlayerInfoService;
	/** 体制アラート情報サービス */
	@Resource protected StationalarmInfoService stationalarmInfoService;
	/** アラームメッセージ情報サービス */
	@Resource protected AlarmmessageInfoService alarmmessageInfoService;
	/** アラームメッセージデータサービス */
	@Resource protected AlarmmessageDataService alarmmessageDataService;
	/** 履歴テーブルサービス */
	@Resource protected HistoryTableService historyTableService;

	// Dto
	/** ログイン情報 */
	@Resource protected LoginDataDto loginDataDto;

	/**
	 * ログイン中の自治体の体制レイヤ情報を取得する.
	 * @return 体制レイヤ情報
	 */
	public StationlayerInfo getLoginStationlayerInfo() {
		// 体制レイヤ情報を取得する
		StationlayerInfo stationlayerInfo = stationlayerInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		return stationlayerInfo;
	}

	/**
	 * ログイン中の災害の「現在の体制」を取得します.
	 * @return 「現在の体制」を返す
	 *         体制がなければ、デフォルトの「体制なし」を返す
	 *         体制レイヤ情報が設定されていなければ、 null を返す
	 */
	public String getLoginCurrentSationName() {
		// 「現在の体制」の初期化
		StationService stationService = this;

		// 体制レイヤ情報を取得する
		StationlayerInfo stationlayerInfo = stationlayerInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		if(stationlayerInfo==null) {
			logger.debug(lang.__("For local gov. ID ({0}) has not been set system layer info, [current system] does not displayed.", loginDataDto.getLocalgovinfoid()));
			return null;
		}
		else {
			// 体制区分の属性ID が設定されているかどうか
			if(StringUtil.isEmpty(stationlayerInfo.stationclassattrid)) {
				throw new ServiceException(lang.__("\"Attribute ID of system type\" of system layer has not been set."));
			}
			else {
				String currentStationName = null;

				// 平時モード
				if(loginDataDto.getTrackdataid() == 0) {
					// マスタテーブルから、現在の体制を取得
					TablemasterInfo tablemasterInfo = stationlayerInfo.tablemasterInfo;
					currentStationName = stationService.getCurrentStationName(stationlayerInfo, loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid(), tablemasterInfo.tablename, tablemasterInfo.layerid);
				}
				// 災害モード
				else {
					// 記録テーブルを検索
					TracktableInfo tracktableInfo = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(stationlayerInfo.tablemasterinfoid, loginDataDto.getTrackdataid());
					if(tracktableInfo==null) {
						throw new ServiceException(lang.__("Recording table of system of this disaster can not be found."));
					}
					else {
						// 記録テーブルから、現在の体制を取得
						currentStationName = stationService.getCurrentStationName(stationlayerInfo, loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid(), tracktableInfo.tablename, tracktableInfo.layerid);
					}
				}

				// テーブルから体制を取得できた場合
				if(currentStationName!=null) {
					return currentStationName;
				}
				// 取得できなかった場合はデフォルトを返す
				else {
					return new PageDto().getStationName();
				}
			}
		}
	}

	/**
	 * ログイン中の「現在の体制」の体制区分情報を取得する.
	 * @return 体制区分
	 */
	public StationclassInfo getLoginCurrentStationclassInfo() {
		String currentStationName = getLoginCurrentSationName();
		return stationclassInfoService.findByLocalgovInfoIdAndName(loginDataDto.getLocalgovinfoid(), currentStationName);
	}

	/**
	 * 指定した記録データの「現在の体制」を取得します.
	 * @param stationlayerInfo 体制レイヤ情報
	 * @param trackdataid 記録データID
	 * @param localgovinfoid 自治体ID
	 * @param table テーブル名
	 * @param layerid レイヤID
	 * @return 「現在の体制」
	 * @throws ServiceException 
	 */
	public String getCurrentStationName(StationlayerInfo stationlayerInfo, long localgovinfoid, long trackdataid, String table, String layerid) throws ServiceException {

		// レイヤの存在チェック
		LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerid);
		if(layerInfo==null) return null;

		// データを取得
		List<BeanMap> result = null;
		// レイヤの場合
		if(StringUtil.isNotEmpty(layerid)) {
			result = tableService.selectAll(layerid, "desc", "gid", /*timeParam*/null/*常に最新を表示*/);
		}
		// システムテーブルの場合(stationorder_data の互換性を保つ？)
		else if(StringUtil.isNotEmpty(table)) {
			result = tableService.selectByTrackdataid(table, trackdataid, "desc", "id");
		}

		// 「現在の体制」を取得
		String currentStationName = null;
		if(result!=null && 0<result.size()) {
			// 指定された体制区分の属性IDが記録テーブルに存在しない
			if(! result.get(0).containsKey(stationlayerInfo.stationclassattrid)) {
				throw new ServiceException(lang.__("System layer  attributes ({0}) specified in the attributes ID settings of system type not found.", stationlayerInfo.stationclassattrid));
			}

			// 体制区分情報を表示順＋ID順のリストで取得する
			List<StationclassInfo> stationclassInfos = stationclassInfoService.findByLocalgovinfoid(localgovinfoid);
			final List<String> nameOrders = new ArrayList<String>();
			for(StationclassInfo stationclassInfo : stationclassInfos) {
				nameOrders.add(stationclassInfo.name);
			}

			// 体制のデータが複数ある場合は、体制区分情報の表示順により表示する体制を選ぶ
			for(BeanMap beanMap : result) {
				Object value = beanMap.get(stationlayerInfo.stationclassattrid);
				boolean isBlank = " ".equals(value); // 空欄可の場合は、半角スペースが入っている
				if(StringUtil.isNotEmpty((String) value) && !isBlank) {
					if(currentStationName==null) currentStationName = (String) value;
					// 複数の体制が登録されている場合
					else {
						// 順序を取得
						int index = nameOrders.indexOf(value);
						if(index==-1) {
							throw new ServiceException(lang.__("System data \"{0}\" is not set to system type, so it can not determine the \"current system\".", value));
						}
						// 体制解除でなければ
						if(stationclassInfos.get(index).stationid!=0) {
							int currentIndex = nameOrders.indexOf(currentStationName);
							if(currentIndex==-1) {
								throw new ServiceException(lang.__("System data \"{0}\" is not set to system type, so it can not determine the \"current system\".", currentStationName));
							}
							// 体制解除が保持されていた場合は上書きする
							if(stationclassInfos.get(currentIndex).stationid==0) {
								currentStationName = (String) value;
							}
							// 体制解除でないものが保持されていた場合は、順序を比較して大きいほうを選ぶ
							else if(currentIndex<index) {
								currentStationName = (String) value;
							}
						}
					}
				}
			}
		}

		return currentStationName;
	}

	/**
	 * 登録/更新する属性値によってアラートを出す.
	 * @param tablemasterinfoid テーブルマスタID
	 * @param attrid 属性ID
	 * @param value 属性値
	 */
	public void triggerAlertmessage(Long tablemasterinfoid, String attrid, String value) {
		try {
			// 体制アラート
			// 対象のテーブルが、体制レイヤかどうか
			StationlayerInfo stationlayerInfo = stationlayerInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
			if(stationlayerInfo!=null && tablemasterinfoid.equals(stationlayerInfo.tablemasterinfoid)) {
				String triggerAttrid = stationlayerInfo.stationclassattrid;

				// アラートトリガーの属性が設定されているか
				if(StringUtil.isEmpty(triggerAttrid)) {
					logger.debug(lang.__("Unable to alert for alert trigger judgment attribute ID not set.", "localgovinfoid: "+loginDataDto.getLocalgovinfoid()+", tableemasterinfoid: "+tablemasterinfoid));
				}
				// アラートトリガーの属性と一致するなら
				else if(triggerAttrid.equals(attrid)){
					// その属性値で出すアラートを取得する
					List<StationalarmInfo> alarmlist = stationalarmInfoService.findByLocalgovInfoIdAndTablemasterInfoId(loginDataDto.getLocalgovinfoid(), tablemasterinfoid);
					for (StationalarmInfo alarminfo : alarmlist) {
						// その体制区分の体制アラートがあるかどうか
						if (alarminfo.stationclassInfo.name.equals(value)) {
							//体制のポップアップ
							AlarmmessageInfo alarm = alarmmessageInfoService.findById(alarminfo.alarmmessageinfoid);
							if (alarm != null) {
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
								String registTime = sdf.format(new Date());
								alarm.message = lang.__("[{0}]", registTime)+alarm.message;

								// ログイン中の災害にアラートを出す
								alarmmessageDataService.insert(loginDataDto.getTrackdataid(), alarm);

								// 対応中の災害すべてにアラートを出す
								List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid());
								for(TrackData trackData : trackDatas) {
									if(trackData.id.longValue()!=loginDataDto.getTrackdataid()) {
										alarmmessageDataService.insert(trackData.id, alarm);
									}
								}
							}
						}
					}
				}
			}
		} catch(ServiceException e) {
			logger.warn(e.getMessage());
		}
	}

	/**
	 * 体制移行トリガーを起動します.
	 *
	 * @param track 記録データ
	 * @param trigger 気象庁トリーガ情報
	 * @return 体制移行されたかどうか
	 */
	public boolean triggerShiftstation(TrackData track, AlerttriggerInfoDto trigger) {

		// トリガー条件の体制区分情報を取得
		StationclassInfo triggerStationclassInfo = stationclassInfoService.findById(trigger.stationclassinfoid);
		if(triggerStationclassInfo==null) {
			throw new ServiceException(lang.__("System type info (ID = {0}) as a trigger condition not found.", trigger.stationclassinfoid));
		}

		// 体制レイヤ情報を取得
		StationlayerInfo stationlayerInfo = stationlayerInfoService.findByLocalgovInfoId(track.localgovinfoid);
		if(stationlayerInfo==null) {
			logger.debug(lang.__("Because system layer info has not been set, system shift trigger can not start by Japan Meteorological Agency XML."));
			return false;
		}
		// 体制区分属性ID が設定されているかチェック
		if(StringUtil.isEmpty(stationlayerInfo.stationclassattrid)) {
			logger.debug(lang.__("Because \"attribute ID of system type\" of system layer info has not been set, system shift trigger can not start by Japan Meteorological Agency XML."));
			return false;
		}

		// 体制レイヤ情報で指定された「テーブルマスタ情報」で、この災害の「記録テーブル情報」を取得する
		TracktableInfo tracktableInfo = tracktableInfoService.findByTrackmapInfoIdAndTablemasterinfoid(track.trackmapinfoid, stationlayerInfo.tablemasterinfoid);
		if(tracktableInfo==null) {
			throw new ServiceException(lang.__("ID={1} is set in record table of system layer: table master info ID={0}, but not exist.", stationlayerInfo.tablemasterinfoid, track.id));
		}

		// 記録テーブルにレイヤIDが指定されているかチェック
		if(StringUtil.isEmpty(tracktableInfo.layerid)) {
			throw new ServiceException(lang.__("Layerid of record table info ID = {0}  has not been set.", tracktableInfo.id));
		}

		// トリガーの結果
		boolean triggered = false;

		// 体制レイヤの地物を取得
		// データが複数あるのは、２つの原因が考えられる
		// 1. 1つの災害に対して複数の体制を持つため
		// 2. 複数同時災害で災害が複数起動されているため
		final String layerId = tracktableInfo.layerid;
		List<BeanMap> beanMaps = tableService.selectAll(layerId);
		if(beanMaps.size()==0) {
			logger.warn(lang.__("Feature is not registered in recording table ({0}) of system layer ", tracktableInfo.id));
		}
		for(BeanMap beanMap : beanMaps) {
			final String stationclassAttrId = stationlayerInfo.stationclassattrid;
			// 体制区分の属性ID が指定されていれば
			if(beanMap.containsKey(stationclassAttrId)) {
				Long featureId = null;
				Object gid = beanMap.get("gid");
				if(gid instanceof Long) featureId = (Long) gid;
				else logger.error("gid: "+gid+" is not Long");

				if(featureId==null) continue;

				// 現在の体制区分の文字列から、体制区分情報を取得する
				String stationclassName = (String) beanMap.get(stationclassAttrId);
				boolean isBlank = " ".equals(stationclassName); // 空欄可の場合は、半角スペースが入っている
				if(StringUtil.isNotEmpty(stationclassName) && !isBlank) {
					StationclassInfo stationclassInfo =  stationclassInfoService.findByLocalgovInfoIdAndName(track.localgovinfoid, stationclassName);
					if(stationclassInfo==null) {
						logger.warn(lang.__("{2}: System type info is not defined at {1}, system type attribute ID, of system layer record table {}.", tracktableInfo.id, stationclassAttrId, stationclassName));
						continue;
					}
					// トリガーで設定された体制区分よりも、現在の体制が同等以上なら体制移行しない
					if(triggerStationclassInfo.disporder<=stationclassInfo.disporder) {
						continue;
					}
				}

				// 体制移行処理
				MapDB mapDB = MapDB.getMapDB();
				LayerInfo layerInfo = mapDB.getLayerInfo(layerId);
				boolean hasTrackdataidAttr = layerInfo.getAttrInfo(TrackdataidAttrService.TRACKDATA_ATTR_ID)!=null;

				// trackdataid 属性をチェックする
				if(hasTrackdataidAttr && beanMap.containsKey(TrackdataidAttrService.TRACKDATA_ATTR_ID)) {
					String trackdataid = (String) beanMap.get(TrackdataidAttrService.TRACKDATA_ATTR_ID);
					if(StringUtil.isNotEmpty(trackdataid) && 0<Long.parseLong(trackdataid)) {
						logger.debug(lang.__("{0} can not be transfer by trigger because it is used in other disaster system.", layerId+"."+featureId));
						continue;
					}
				}

				// 体制変更を実行
				HashMap<String, String> attribute = new HashMap<String, String>();
				attribute.put(stationclassAttrId, triggerStationclassInfo.name);

				// trackdataid属性 があれば設定
				if(hasTrackdataidAttr) {
					attribute.put(TrackdataidAttrService.TRACKDATA_ATTR_ID, String.valueOf(track.id));
				}

				// 編集者 属性の値が設定されていないか
				if(StringUtil.isEmpty(attribute.get(EdituserAttrService.EDITUSER_ATTR_ID))) {
					// edituser 属性の値が入っていなければ、ログイン名を設定
					attribute.put(EdituserAttrService.EDITUSER_ATTR_ID, loginDataDto.getLoginName());
				}

				// 移行時間属性 があれば設定
				if(StringUtil.isNotEmpty(stationlayerInfo.shifttimeattrid)) {
					if(layerInfo.getAttrInfo(stationlayerInfo.shifttimeattrid)!=null) {
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						attribute.put(stationlayerInfo.shifttimeattrid, timestamp.toString());
					}
				}

				UserInfo userInfo =  mapDB.getAuthIdUserInfo("admin");
				try {
					FeatureDB.updateFeatureAttribute(userInfo, layerId, featureId, attribute, new Timestamp(System.currentTimeMillis()));
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}

				// 履歴テーブルに記録
				HistorytableInfo htbl = historyTableService.findOrCreateByTracktableInfo(tracktableInfo, layerInfo);
				historyTableService.log(tracktableInfo, htbl, featureId, layerInfo);

				// 体制変更した
				triggered = true;
			}
		}

		return triggered;
	}

	/**
	 * ログイン中の災害の「現在の体制の発令日時」を取得します.
	 * @return 「現在の体制の発令日時」を返す
	 *         体制レイヤ情報が設定されていなければ、 null を返す
	 */
	public String getLoginCurrentStationShifttime() {
		// 「発令日時」の初期化
		StationService stationService = this;

		// 体制レイヤ情報を取得する
		StationlayerInfo stationlayerInfo = stationlayerInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		if(stationlayerInfo==null) {
			return null;
		}
		else {
			// 発令日時の属性ID が設定されているかどうか
			if(StringUtil.isEmpty(stationlayerInfo.shifttimeattrid)) {
				throw new ServiceException(lang.__("\"attribute ID of official announcement date and time\" of system layer has not been set."));
			}
			else {
				String currentStationShifttime = null;

				// 平時モード
				if(loginDataDto.getTrackdataid() == 0) {
					// マスタテーブルから、現在の体制を取得
					TablemasterInfo tablemasterInfo = stationlayerInfo.tablemasterInfo;
					currentStationShifttime = stationService.getCurrentStationShifttime(stationlayerInfo, loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid(), tablemasterInfo.tablename, tablemasterInfo.layerid);
				}
				// 災害モード
				else {
					// 記録テーブルを検索
					TracktableInfo tracktableInfo = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(stationlayerInfo.tablemasterinfoid, loginDataDto.getTrackdataid());
					if(tracktableInfo==null) {
						throw new ServiceException(lang.__("Recording table of system of this disaster can not be found."));
					}
					else {
						// 記録テーブルから、現在の体制を取得
						currentStationShifttime = stationService.getCurrentStationShifttime(stationlayerInfo, loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid(), tracktableInfo.tablename, tracktableInfo.layerid);
					}
				}

				// テーブルから体制を取得できた場合
				if(currentStationShifttime!=null) {
					return currentStationShifttime;
				}
				// 取得できなかった場合はnullを返す
				else {
					return null;
				}
			}
		}
	}

	/**
	 * 指定した記録データの「現在の体制の発令日時」を取得します.
	 * @param stationlayerInfo 体制レイヤ情報
	 * @param trackdataid 記録データID
	 * @param localgovinfoid 自治体ID
	 * @param table テーブル名
	 * @param layerid レイヤID
	 * @return 「現在の体制の発令日時」
	 * @throws ServiceException 
	 */
	public String getCurrentStationShifttime(StationlayerInfo stationlayerInfo, long localgovinfoid, long trackdataid, String table, String layerid) throws ServiceException {

		// レイヤの存在チェック
		LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(layerid);
		if(layerInfo==null) return null;

		// データを取得
		List<BeanMap> result = null;
		// レイヤの場合
		if(StringUtil.isNotEmpty(layerid)) {
			result = tableService.selectAll(layerid, "desc", "gid", /*timeParam*/null/*常に最新を表示*/);
		}
		// システムテーブルの場合(stationorder_data の互換性を保つ？)
		else if(StringUtil.isNotEmpty(table)) {
			result = tableService.selectByTrackdataid(table, trackdataid, "desc", "id");
		}

		// 「現在の体制」を取得
		String currentStationShifttime = null;
		if(result!=null && 0<result.size()) {
			// 指定された体制区分の属性IDが記録テーブルに存在しない
			if(! result.get(0).containsKey(stationlayerInfo.shifttimeattrid)) {
				throw new ServiceException(lang.__("System layer  attributes ({0}) specified in the attributes ID settings of announcement date and time not found.", stationlayerInfo.shifttimeattrid));
			}

			// 体制区分情報を表示順＋ID順のリストで取得する
			List<StationclassInfo> stationclassInfos = stationclassInfoService.findByLocalgovinfoid(localgovinfoid);
			final List<String> nameOrders = new ArrayList<String>();
			for(StationclassInfo stationclassInfo : stationclassInfos) {
				nameOrders.add(stationclassInfo.name);
			}

			// 体制のデータが複数ある場合は、体制区分情報の表示順により表示する体制を選ぶ
			for(BeanMap beanMap : result) {
				Object value = beanMap.get(stationlayerInfo.shifttimeattrid);
				boolean isBlank = " ".equals(value); // 空欄可の場合は、半角スペースが入っている
				if(StringUtil.isNotEmpty((String) value) && !isBlank) {
					if(currentStationShifttime==null) currentStationShifttime = (String) value;
					// 複数の体制が登録されている場合
					else {
						// 順序を取得
						int index = nameOrders.indexOf(value);
						if(index==-1) {
							throw new ServiceException(lang.__("System data \"{0}\" is not set to system type, so it can not determine the \"current system\".", value));
						}
						// 体制解除でなければ
						if(stationclassInfos.get(index).stationid!=0) {
							int currentIndex = nameOrders.indexOf(currentStationShifttime);
							if(currentIndex==-1) {
								throw new ServiceException(lang.__("System data \"{0}\" is not set to system, so the \"current system\" can not be determined.", currentStationShifttime));
							}
							// 体制解除が保持されていた場合は上書きする
							if(stationclassInfos.get(currentIndex).stationid==0) {
								currentStationShifttime = (String) value;
							}
							// 体制解除でないものが保持されていた場合は、順序を比較して大きいほうを選ぶ
							else if(currentIndex<index) {
								currentStationShifttime = (String) value;
							}
						}
					}
				}
			}
		}

		return currentStationShifttime;
	}
}
