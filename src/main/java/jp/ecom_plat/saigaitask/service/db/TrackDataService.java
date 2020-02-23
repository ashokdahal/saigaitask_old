/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service.db;

import static jp.ecom_plat.saigaitask.entity.Names.trackData;
import static jp.ecom_plat.saigaitask.util.StringUtil.toWareki;
import static org.seasar.extension.jdbc.operation.Operations.and;
import static org.seasar.extension.jdbc.operation.Operations.asc;
import static org.seasar.extension.jdbc.operation.Operations.desc;
import static org.seasar.extension.jdbc.operation.Operations.eq;
import static org.seasar.extension.jdbc.operation.Operations.isNotNull;
import static org.seasar.extension.jdbc.operation.Operations.isNull;
import static org.seasar.extension.jdbc.operation.Operations.ne;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.transaction.UserTransaction;

import org.seasar.extension.jdbc.AutoSelect;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.map.db.MapInfo;
import jp.ecom_plat.map.db.MapLayerInfo;
import jp.ecom_plat.map.file.LegendFile;
import jp.ecom_plat.map.file.SLDFile;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.Names;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.AlertcontentData;
import jp.ecom_plat.saigaitask.entity.db.AssemblestateData;
import jp.ecom_plat.saigaitask.entity.db.DisasterbuildData;
import jp.ecom_plat.saigaitask.entity.db.DisastercasualtiesData;
import jp.ecom_plat.saigaitask.entity.db.DisasterfarmData;
import jp.ecom_plat.saigaitask.entity.db.DisasterfireData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhospitalData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhouseData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhouseholdData;
import jp.ecom_plat.saigaitask.entity.db.DisasterhouseregidentData;
import jp.ecom_plat.saigaitask.entity.db.DisasterlifelineData;
import jp.ecom_plat.saigaitask.entity.db.DisasterroadData;
import jp.ecom_plat.saigaitask.entity.db.DisasterschoolData;
import jp.ecom_plat.saigaitask.entity.db.DisastersummaryhistoryData;
import jp.ecom_plat.saigaitask.entity.db.DisasterwelfareData;
import jp.ecom_plat.saigaitask.entity.db.GeneralizationhistoryData;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.HeadofficeData;
import jp.ecom_plat.saigaitask.entity.db.HistorytableInfo;
import jp.ecom_plat.saigaitask.entity.db.ImporttrackInfo;
import jp.ecom_plat.saigaitask.entity.db.JalerttriggerData;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.LoginData;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteotriggerData;
import jp.ecom_plat.saigaitask.entity.db.NoticemailData;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportData;
import jp.ecom_plat.saigaitask.entity.db.ReportData;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.ThreadData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackgroupData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanData;
import jp.ecom_plat.saigaitask.service.AbstractService;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.DeleteCascadeResult;
import jp.ecom_plat.saigaitask.service.EdituserAttrService;
import jp.ecom_plat.saigaitask.service.HistoryTableService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.TrackdataidAttrService;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.service.ecommap.MapService;

/**
 * 記録データサービス.
 * 災害を記録する.
 */
@org.springframework.stereotype.Repository
public class TrackDataService extends AbstractService<TrackData> {

	//Logger logger = Logger.getLogger(TrackDataService.class);

	@Resource
	protected ServletContext application;
	@Resource
	protected UserTransaction userTransaction;

	@Resource
	protected MapService mapService;
	@Resource
	protected LayerService layerService;
	@Resource
	protected TableService tableService;
	@Resource
	protected TrackdataidAttrService trackdataidAttrService;
	@Resource
	protected EdituserAttrService edituserAttrService;

	@Resource
	protected LocalgovInfoService localgovInfoService;
	@Resource
	protected MapmasterInfoService mapmasterInfoService;
	@Resource
	protected TrackmapInfoService trackmapInfoService;
	@Resource
	protected TablemasterInfoService tablemasterInfoService;
	@Resource
	protected TracktableInfoService tracktableInfoService;
	// クリアリングハウス関連のサービス
	@Resource
	protected ClearinghouseService clearinghouseService;
	@Resource
	protected HistoryTableService historyTableService;

	/**
	 * IDで検索
	 * @param id
	 * @return 記録データ
	 */
    public TrackData findById(Long id) {
    	//return select().leftOuterJoin(trackData().disasterMaster()).leftOuterJoin(trackData().trainingplanData()).id(id).getSingleResult();
    	return select().leftOuterJoin(trackData().trainingplanData()).leftOuterJoin(trackData().trackmapInfo()).id(id).getSingleResult();
    }

    /**
     * 現在進行中の災害を検索
     * @deprecated 同時複数災害の対応により、対応中の記録データは複数になったため {@link #findByCurrentTrackDatas(Long)}を利用してください。
     * @param govid 自治体ID
     * @return 記録データ
     */
    @Deprecated
    public TrackData findByCurrentTrackData(Long govid) {
    	return select().where(
    		and(
    			eq(trackData().localgovinfoid(), govid),
    			isNull(trackData().endtime())
    		)
    	).orderBy(desc(trackData().starttime())).limit(1).getSingleResult();
    }

    /**
     * 現在進行中の災害を検索
     * @param govid 自治体ID
     * @return 記録データ
     */
    public List<TrackData> findByCurrentTrackDatas(Long govid) {
    	return findByCurrentTrackDatas(govid, false);
    }
    /**
     * 現在進行中の災害を検索
     * @param govid 自治体ID
     * @param training 訓練情報取得ならtrue
     * @return 記録データ
     */
    public List<TrackData> findByCurrentTrackDatas(Long govid, boolean training) {
    	if(training){
    		return select().where(
    			and(
    				eq(trackData().localgovinfoid(), govid),
    				isNotNull(trackData().trainingplandataid()),
    				isNull(trackData().endtime())
    			)
    		).orderBy(desc(trackData().starttime())).getResultList();
    	}
    	return select().where(
    		and(
    			eq(trackData().localgovinfoid(), govid),
    			isNull(trackData().trainingplandataid()),
    			isNull(trackData().endtime())
    		)
    	).orderBy(desc(trackData().starttime())).getResultList();
    }

    /**
     * 現在進行中の災害を検索
     * @deprecated 同時複数災害の対応により、対応中の記録データは複数になりました。
     * @param govid 自治体ID
     * @param did 災害情報ID
     * @return 記録データ
     */
    @Deprecated
    public TrackData findByCurrentTrackData(Long govid, Integer did) {
    	return select().where(
    		and(
    			eq(trackData().localgovinfoid(), govid),
    			//eq(trackData().disasterid(), did),
    			isNull(trackData().endtime())
    		)
    	).orderBy(desc(trackData().starttime())).limit(1).getSingleResult();
    }

    /**
     * 終了した災害情報を検索
     * @param did
     * @return
     */
    /*public List<TrackData> findByOldTrackData(Long govid, Integer did) {
    	return findByOldTrackData(govid, did, false);
    }*/
    /**
     * 終了した災害情報を検索
     * @param did
     * @param training 訓練情報取得ならtrue
     * @return
     */
    /*public List<TrackData> findByOldTrackData(Long govid, Integer did, boolean training) {
    	if(training){
    		return select().where(
    			and(
    				eq(trackData().localgovinfoid(), govid),
    				//eq(trackData().disasterid(), did),
    				isNotNull(trackData().trainingplandataid()),
    				isNotNull(trackData().endtime())
    			)
    		).orderBy(asc(trackData().id())).getResultList();
    	}
    	return select().where(
    		and(
    			eq(trackData().localgovinfoid(), govid),
    			//eq(trackData().disasterid(), did),
    			isNull(trackData().trainingplandataid()),
    			isNotNull(trackData().endtime())
    		)
    	).orderBy(asc(trackData().id())).getResultList();
    }*/

    /**
     * 終了した災害情報を検索
     * @param govid 自治体ID
     * @return 記録データリスト
     */
    public List<TrackData> findByOldTrackData(Long govid) {
    	return findByOldTrackData(govid, false);
    }
    /**
     * 終了した災害情報を検索
     * @param govid 自治体ID
     * @param training 訓練情報取得ならtrue
     * @return 記録データリスト
     */
    public List<TrackData> findByOldTrackData(Long govid, boolean training) {
    	if(training){
    		return select().where(
    			and(
    				eq(trackData().localgovinfoid(), govid),
    				//eq(trackData().disasterid(), did),
    				isNotNull(trackData().trainingplandataid()),
    				isNotNull(trackData().endtime())
    			)
    		).orderBy(asc(trackData().id())).getResultList();
    	}
    	return select().where(
    		and(
    			eq(trackData().localgovinfoid(), govid),
    			//eq(trackData().disasterid(), did),
    			isNull(trackData().trainingplandataid()),
    			isNotNull(trackData().endtime())
    		)
    	).orderBy(asc(trackData().id())).getResultList();
    }

    /**
     * 自治体IDと災害種別で検索
     * @param govid 自治体ID
     * @param did 災害種別
     * @return 記録IDリスト
     */
    /*public List<TrackData> findByLocalgovInfoIdAndDisasterId(Long govid, Integer did) {
    	return findByLocalgovInfoIdAndDisasterId(govid, did, false);
    }*/
    /**
     * 自治体IDと災害種別で検索
     * @param govid 自治体ID
     * @param did 災害種別
     * @param training 訓練情報取得ならtrue
     * @return 記録IDリスト
     */
    /*public List<TrackData> findByLocalgovInfoIdAndDisasterId(Long govid, Integer did, boolean training) {
    	if(training){
    		return select().where(
    			and(
    				eq(trackData().localgovinfoid(), govid),
    				//eq(trackData().disasterid(), did),
    				isNotNull(trackData().trainingplandataid())
    			)
    		).orderBy(asc(trackData().id())).getResultList();
    	}
    	return select().where(
    		and(
    			eq(trackData().localgovinfoid(), govid),
    			//eq(trackData().disasterid(), did),
    			isNull(trackData().trainingplandataid())
    		)
    	).orderBy(asc(trackData().id())).getResultList();
    }*/

    /**
     * 自治体IDと災害種別で削除してないものを検索
     * @param govid
     * @param did
     * @return
     */
    /*public List<TrackData> findByLocalgovInfoIdAndDisasterIdAndNotDelete(Long govid, Integer did) {
    	return findByLocalgovInfoIdAndDisasterIdAndNotDelete(govid, did, false);
    }*/
    /**
     * 自治体IDと災害種別で削除してないものを検索
     * @param govid
     * @param did
     * @param training 訓練情報取得ならtrue
     * @return
     */
    /*public List<TrackData> findByLocalgovInfoIdAndDisasterIdAndNotDelete(Long govid, Integer did, boolean training) {
    	if(training){
    		return select().where(
    			and(
    				eq(trackData().localgovinfoid(), govid),
    				eq(trackData().disasterid(), did),
    				isNotNull(trackData().trainingplandataid()),
    				ne(trackData().deleted(), true)
    			)
    		).orderBy(asc(trackData().id())).getResultList();

    	}
    	return select().where(
    		and(
    			eq(trackData().localgovinfoid(), govid),
    			eq(trackData().disasterid(), did),
    			isNull(trackData().trainingplandataid()),
    			ne(trackData().deleted(), true)
    		)
    	).orderBy(asc(trackData().id())).getResultList();
    }*/

    /**
     * 自治体IDで削除してないものを検索
     * @param govid
     * @return
     */
    public List<TrackData> findByLocalgovInfoIdAndNotDelete(Long govid){
    	return select().where(
    		and(
    			eq(trackData().localgovinfoid(), govid),
    			ne(trackData().deleted(), true)
    		)
    	).orderBy(asc(trackData().id())).getResultList();
    }

    /**
     * trackmapinfoid から検索(開始日昇順)
     * @param trackmapinfoid
     * @return 記録データリスト
     */
    public List<TrackData> findByTrackmapinfoidAndNotDeleted(Long trackmapinfoid) {
    	return findByTrackmapinfoidAndNotDeleted(trackmapinfoid, false);
    }
    /**
     * trackmapinfoid から検索(開始日昇順)
     * @param trackmapinfoid
     * @param training 訓練情報取得ならtrue
     * @return 記録データリスト
     */
    public List<TrackData> findByTrackmapinfoidAndNotDeleted(Long trackmapinfoid, boolean training) {
    	if(training){
    		return select().where(
    			and(
    				eq(trackData().trackmapinfoid(), trackmapinfoid),
    				isNotNull(trackData().trainingplandataid()),
    				ne(trackData().deleted(), true)
    			)
    		).orderBy(asc(trackData().starttime())).getResultList();
    	}
    	return select().where(
    		and(
    			eq(trackData().trackmapinfoid(), trackmapinfoid),
    			isNull(trackData().trainingplandataid()),
    			ne(trackData().deleted(), true)
    		)
    	).orderBy(asc(trackData().starttime())).getResultList();
    }

	/**
	 * 都道府県に紐づく、まだ未割り当ての市町村の記録データを検索(未削除のみ)
	 * @param preflocalgovinfoid 都道府県の自治体ID
	 * @param citylocalgovinfoid 市区町村の自治体ID
	 * @return 記録データリスト
	 */
    public List<TrackData> findNotAssociateCityTrackDatas(Long preflocalgovinfoid, Long citylocalgovinfoid) {
    	return findNotAssociateCityTrackDatas(preflocalgovinfoid, citylocalgovinfoid, false);
    }
	/**
	 * 都道府県に紐づく、まだ未割り当ての市町村の記録データを検索
	 * @param preflocalgovinfoid 都道府県の自治体ID
	 * @param citylocalgovinfoid 市町村の自治体ID
     * @param training 訓練情報取得ならtrue
	 * @return 記録データリスト
	 */
    public List<TrackData> findNotAssociateCityTrackDatas(Long preflocalgovinfoid, Long citylocalgovinfoid, boolean training) {
		if(training){
			return select()
					.leftOuterJoin(trackData().localgovInfo())
					//.leftOuterJoin(trackData().disasterMaster())
					.leftOuterJoin(trackData().cityTrackgroupDatas())
					.where(
							ne(trackData().deleted(), true), // 市町村の記録データが削除されていない
							eq(trackData().localgovinfoid(), citylocalgovinfoid),  // 自治体ID指定
							eq(trackData().localgovInfo().preflocalgovinfoid(), preflocalgovinfoid),  // 都道府県に紐づく自治体
							isNotNull(trackData().trainingplandataid()), // 訓練モード
							isNull(trackData().cityTrackgroupDatas().id()) // まだ記録グループに関連付けられていない
					)
					.orderBy(desc(trackData().starttime()), desc(trackData().endtime()))
					.getResultList();

		}
		return select()
				.leftOuterJoin(trackData().localgovInfo())
				//.leftOuterJoin(trackData().disasterMaster())
				.leftOuterJoin(trackData().cityTrackgroupDatas())
				.where(
						ne(trackData().deleted(), true), // 市町村の記録データが削除されていない
						eq(trackData().localgovinfoid(), citylocalgovinfoid),  // 自治体ID指定
						eq(trackData().localgovInfo().preflocalgovinfoid(), preflocalgovinfoid),  // 都道府県に紐づく自治体
						isNull(trackData().trainingplandataid()), // 訓練モードでない
						isNull(trackData().cityTrackgroupDatas().id()) // まだ記録グループに関連付けられていない
				)
				.orderBy(desc(trackData().starttime()), desc(trackData().endtime()))
				.getResultList();
	}

    /**
     * 都道府県の記録データに紐づく市町村の記録データを、市町村を指定で検索
     * @param cityLocalgovInfo 市区町村の自治体情報
     * @param preftrackdataid 都道府県の自治体ID
     * @return 記録データリスト
     */
    public List<TrackData> findCityTrackDatas(LocalgovInfo cityLocalgovInfo, long preftrackdataid) {
    	return findCityTrackDatas(cityLocalgovInfo, preftrackdataid, false);
    }
    /**
     * 都道府県の記録データに紐づく市町村の記録データを、市町村を指定で検索
     * @param cityLocalgovInfo
     * @param preftrackdataid
     * @param training 訓練情報取得ならtrue
     * @return 記録データリスト
     */
    @SuppressWarnings("unchecked")
    public List<TrackData> findCityTrackDatas(LocalgovInfo cityLocalgovInfo, long preftrackdataid, boolean training) {
		Long preflocalgovinfoid = cityLocalgovInfo.preflocalgovinfoid;

		AutoSelect<?> select = select();
		select.leftOuterJoin(trackData().localgovInfo());
		select.leftOuterJoin(trackData().cityTrackgroupDatas());
		select.leftOuterJoin(trackData().cityTrackgroupDatas().cityTrackData());
		if(training){
			select.where(
					eq(trackData().localgovinfoid(), cityLocalgovInfo.id), // 自治体ID指定
					eq(trackData().localgovInfo().preflocalgovinfoid(), preflocalgovinfoid), // 都道府県に紐づく自治体
					eq(trackData().cityTrackgroupDatas().preftrackdataid(), preftrackdataid), // 都道府県の記録データ
					isNotNull(trackData().trainingplandataid()) // 訓練モードでない
					,ne(trackData().cityTrackgroupDatas().cityTrackData().deleted(), true) // 市町村の記録データが削除されていない
			);
		}else{
			select.where(
					eq(trackData().localgovinfoid(), cityLocalgovInfo.id), // 自治体ID指定
					eq(trackData().localgovInfo().preflocalgovinfoid(), preflocalgovinfoid), // 都道府県に紐づく自治体
					eq(trackData().cityTrackgroupDatas().preftrackdataid(), preftrackdataid), // 都道府県の記録データ
					isNull(trackData().trainingplandataid()) // 訓練モードでない
					,ne(trackData().cityTrackgroupDatas().cityTrackData().deleted(), true) // 市町村の記録データが削除されていない
			);

		}
		select.orderBy(desc(trackData().starttime()), desc(trackData().endtime()));
		return (List<TrackData>) select.getResultList();
	}

    /**
     * 訓練プランに紐付く訓練用記録データを検索する
     * @param trainingplandataid
     * @return 記録データリスト
     */
    public List<TrackData> findBytrainingplandataid(Long trainingplandataid) {
    	return select().where(
    		and(
    			eq(trackData().trainingplandataid(), trainingplandataid),
    			isNull(trackData().endtime()),
    			ne(trackData().deleted(), true)
    		)
    	).getResultList();
    }

    /**
     * 訓練プランに紐付く訓練用記録データを検索する
     * @param trainingplandataid
     * @return
     */
    public List<TrackData> findBytrainingplandataidAndLocalgovinfoid(Long trainingplandataid, Long localgovinfoid) {
    	return select().where(
    		and(
    			eq(trackData().trainingplandataid(), trainingplandataid),
    			eq(trackData().localgovinfoid(), localgovinfoid),
    			ne(trackData().deleted(), true)
    		)
    	).getResultList();
    }

	/**
	 * ログイン中の記録データを切り替える
	 * @param tid
	 */
	public void loginTrackData(long tid) {
		TrackData track = findById(tid);
		loginTrackData(track);
	}

	/**
	 * ログイン中の記録データを切り替える
	 * @param track
	 */
	public void loginTrackData(TrackData track) {
		if (track != null) {
			loginDataDto.setTrackdataid(track.id);
			loginDataDto.setTrackmapinfoid(0);
			// 複合災害対応
			if(track.trackmapinfoid!=null) {
				loginDataDto.setTrackmapinfoid(track.trackmapinfoid);
			}
			// 複合災害対応前の処理
			else {
				// 記録IDから地図IDを取得する
				TrackmapInfo trackmapInfo = trackmapInfoService.findByTrackDataId(loginDataDto.getTrackdataid());
				if(trackmapInfo!=null) {
					loginDataDto.setTrackmapinfoid(trackmapInfo.id);
				}
			}
			//loginDataDto.getDisasterid() = track.disasterid;
			// 過去の災害は編集不可とする
			loginDataDto.setEdiable(track.endtime == null);
			loginDataDto.setTime(track.endtime);
		}
		else { // trackdataid=0 or not found
			boolean isUSUAL = loginDataDto.getDisasterid()==0;
			loginDataDto.setEdiable(isUSUAL);
			loginDataDto.setTime(null);
		}
		// マスター確認フラグはログイン時のみ設定する？
		//loginDataDto.isMaster() = loginDataDto.isEdiable();
	}

    /**
     * 災害用地図を作成、訓練の場合は地図を複製する。
     * @param govid 自治体ID
     * @param grpInfo 班
     * @param trackData 記録データ
     * @param copyMap 地図複製フラグ（指定しない場合はDB設定値を利用する）
     * @return 成功：TRUE、失敗：FALSE
     */
	public boolean createDisasterMap(Long govid, GroupInfo grpInfo, TrackData trackData, Boolean copyMap)
    {
		MapDB mapDB = MapDB.getMapDB();
    	try {
        	//LocalgovInfo govInfo = localgovInfoService.findById(govid);
        	MapmasterInfo pmasterInfo = mapmasterInfoService.findByLocalgovInfoId(govid);
        	if(pmasterInfo==null) throw new ServiceException(lang.__("Disaster map not created because no map master info specified."));

			boolean isTraining = trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
			if(copyMap==null) copyMap = isTraining | pmasterInfo.copy;

			//eコミマップ地図作成
        	long mapid = pmasterInfo.mapid;
        	if(copyMap) {
            	String mapname = localgovInfoService.getLocalgovName(govid)+" "+trackData.name;
        		mapid = mapService.createMap(pmasterInfo.mapid, pmasterInfo.mapgroupid, mapname, pmasterInfo.communityid, grpInfo.ecomuser, trackData.note);
            	logger.info(lang.__("Map m{1} of e-Com map is created by copying m{0}.", pmasterInfo.mapid, mapid) + ": localgovinfoid="+govid+", mapmasterinfoid="+pmasterInfo.id+", trackdataid="+trackData.id);
        	}
        	else {
            	logger.info(lang.__("Not copy disaster map, use master map.", pmasterInfo.mapid, mapid) + ": localgovinfoid="+govid+", mapmasterinfoid="+pmasterInfo.id+", trackdataid="+trackData.id);
        	}

        	// 記録地図情報
        	TrackmapInfo tmapInfo = new TrackmapInfo();
        	tmapInfo.communityid = pmasterInfo.communityid;
        	tmapInfo.mapgroupid = pmasterInfo.mapgroupid;
        	//tmapInfo.trackdataid = trackData.id;
        	tmapInfo.mapid = mapid;

        	//地図設定
        	trackmapInfoService.insert(tmapInfo);
        	trackData.trackmapinfoid = tmapInfo.id;
        	update(trackData);

        	logger.info("trackmapInfoService.insert "+tmapInfo.id);

			List<TablemasterInfo> tablelist = tablemasterInfoService.findByMapmasterInfoId(pmasterInfo.id);
			// マスタ用レイヤID(key)と災害用レイヤID(value)のマップ
			Map<String, String> layerIdMap = new HashMap<String, String>();
			if(copyMap) {

				List<MapLayerInfo> mapLayerInfos = null;

				//基本地図項目を共有
				mapLayerInfos = layerService.shareMapLayer(pmasterInfo.mapid, tmapInfo.mapid, LayerInfo.GROUPTYPE_BASE);
				for(MapLayerInfo mapLayerInfo : mapLayerInfos) {
					// レイヤIDは変わらない
					layerIdMap.put(mapLayerInfo.layerId, mapLayerInfo.layerId);
				}

				logger.info("layerService.shareMapLayer GROUPTYPE_BASE");

				//主題図（画像）項目を共有
				mapLayerInfos = layerService.shareMapLayer(pmasterInfo.mapid, tmapInfo.mapid, LayerInfo.GROUPTYPE_OVERLAY);
				for(MapLayerInfo mapLayerInfo : mapLayerInfos) {
					// レイヤIDは変わらない
					layerIdMap.put(mapLayerInfo.layerId, mapLayerInfo.layerId);
				}

				logger.info("layerService.shareMapLayer GROUPTYPE_OVERLAY");

				//主題図項目を共有
				mapLayerInfos = layerService.shareMapLayer(pmasterInfo.mapid, tmapInfo.mapid, LayerInfo.GROUPTYPE_REFERENCE);
				for(MapLayerInfo mapLayerInfo : mapLayerInfos) {
					// レイヤIDは変わらない
					layerIdMap.put(mapLayerInfo.layerId, mapLayerInfo.layerId);
				}

				logger.info("layerService.shareMapLayer GROUPTYPE_REFERENCE");

				// KML項目をコピー
				mapLayerInfos = layerService.shareMapLayer(pmasterInfo.mapid, tmapInfo.mapid, LayerInfo.TYPE_KML);
				for(MapLayerInfo mapLayerInfo : mapLayerInfos) {
					// レイヤIDは変わらない
					layerIdMap.put(mapLayerInfo.layerId, mapLayerInfo.layerId);
				}

				//登録情報を共有
				for (TablemasterInfo table : tablelist) {
					if (TablemasterInfo.COPY_SHARE.equals(table.copy) && !StringUtil.isEmpty(table.layerid)) {
						// レイヤがあるかどうか
						LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(table.layerid);
						if(layerInfo==null) {
							logger.warn(table.layerid+" not found. fail shareMapLayer");
						}
						// 属性ID trackdataid が未追加なら追加する
						trackdataidAttrService.alterTableAddTrackdataidColumnIfNotExists(table.layerid);
						// 属性ID st_edituser が未追加なら追加する
						edituserAttrService.alterTableAddEdituserColumnIfNotExists(table.layerid);
						layerService.shareMapLayer(pmasterInfo.mapid, tmapInfo.mapid, table.layerid);
						// レイヤIDは変わらない
						layerIdMap.put(table.layerid, table.layerid);

						// テーブル情報の保存
						TracktableInfo ttable = new TracktableInfo();
						ttable.layerid = table.layerid;
						ttable.tablemasterinfoid = table.id;
						ttable.tablename = table.layerid;
						ttable.trackmapinfoid = tmapInfo.id;
						tracktableInfoService.insert(ttable);
					}
				}

				logger.info("layerService.shareMapLayer");

				// 登録情報をコピー
				for (TablemasterInfo table : tablelist) {
					if (!TablemasterInfo.COPY_SHARE.equals(table.copy) && !StringUtil.isEmpty(table.layerid)) {
						// レイヤがあるかどうか
						LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(table.layerid);
						if(layerInfo==null) {
							logger.warn(table.layerid+" not found. fail cloneMapLayer");
						}

						// 属性ID trackdataid が未追加なら追加する
						trackdataidAttrService.alterTableAddTrackdataidColumnIfNotExists(table.layerid);
						// 属性ID st_edituser が未追加なら追加する
						edituserAttrService.alterTableAddEdituserColumnIfNotExists(table.layerid);

						String layerId = null;
						try {
							// FIXME: テーブルマスタ情報のコピーフラグに応じたレイヤ複製
							if(TablemasterInfo.COPY_NOFEATURE.equals(table.copy)
									|| TablemasterInfo.COPY_ALL.equals(table.copy)
									|| TablemasterInfo.COPY_LATEST.equals(table.copy))
							{
								// レイヤのコピー
								layerId = layerService.cloneMapLayer(pmasterInfo.mapid, tmapInfo.mapid, table.layerid, grpInfo.ecomuser, table.copy);
							}
						} catch (Exception e) {
							logger.error("fail clone layer "+table.name+"("+table.layerid+")", e);
						}

						if (layerId == null) continue;// レイヤがない

						// 複製したレイヤ情報
						LayerInfo cloneLayerInfo = mapDB.getLayerInfo(layerId);

						try {
							// SLDのコピー
							SLDFile.copyMapToCommunitySldFile(pmasterInfo.communityid, pmasterInfo.mapid, table.layerid, tmapInfo.communityid, layerId, true);
						} catch (Exception e) { logger.error(e.getMessage(), e); }

						try {
							// LEGENDのコピー
							LegendFile.copyLegendFile(0, pmasterInfo.mapid, table.layerid, tmapInfo.mapid, layerId);
						} catch (Exception e) { logger.error(e.getMessage(), e); }

						// テーブル情報の保存
						TracktableInfo ttable = new TracktableInfo();
						ttable.layerid = layerId;
						ttable.tablemasterinfoid = table.id;
						ttable.tablename = layerId;
						ttable.trackmapinfoid = tmapInfo.id;
						tracktableInfoService.insert(ttable);

						//履歴テーブルの作成
						HistorytableInfo htable = historyTableService.createTable(ttable, cloneLayerInfo);
						historyTableService.logAll(ttable,htable,layerInfo);

						// レイヤIDは変わる
						layerIdMap.put(table.layerid, layerId);
					}
				}

				logger.info("layerService.cloneMapLayer");

				// マスタ地図情報
				MapInfo mastermapInfo = mapDB.getMapInfo(pmasterInfo.mapid);
				if (mastermapInfo == null) mastermapInfo = mapDB.getMapInfo(pmasterInfo.mapid);
				if (mastermapInfo == null) throw new ServiceException(lang.__("Map info not found. (mid = {0})", pmasterInfo.mapid));

				// 災害用地図情報
				MapInfo disastermapInfo = mapDB.getMapInfo(mapid);
				if (disastermapInfo == null) disastermapInfo = mapDB.getMapInfo(mapid);
				if (disastermapInfo == null) throw new ServiceException(lang.__("Map info not found. (mid = {0})", mapid));

				// 震度グループ情報が登録されていれば、震度グループを新設して、複製された震度レイヤを紐付ける
				//meteoricEarthQuakeService.mapCopyUpdateEarthquakeLayer(tmapInfo.id, pmasterInfo.id, trackData.id);

				// == ここから地図設定のコピー ==
				for (MapLayerInfo mastermapLayerInfo : mastermapInfo.getMapLayerIterable()) {
					LayerInfo masterlayerInfo = mapDB.getLayerInfo(mastermapLayerInfo.layerId);
					// レイヤが削除されていたら出力しない
					if (masterlayerInfo == null || masterlayerInfo.status == LayerInfo.STATUS_DELETED) continue;

					// 項目グループレイヤをコピー
					if (masterlayerInfo.type==LayerInfo.TYPE_LOCAL_GROUP) {
						String newLayerId = layerService.cloneMapLayer(pmasterInfo.mapid, tmapInfo.mapid, masterlayerInfo.layerId, grpInfo.ecomuser, TablemasterInfo.COPY_NOFEATURE);
						layerIdMap.put(masterlayerInfo.layerId, newLayerId);
					}
				}

				logger.debug("layerIdMap(mastermap to trackmap): "+layerIdMap);

				for (MapLayerInfo mastermapLayerInfo : mastermapInfo.getMapLayerIterable()) {
					LayerInfo masterlayerInfo = mapDB.getLayerInfo(mastermapLayerInfo.layerId);
					// レイヤが削除されていたら出力しない
					if (masterlayerInfo == null || masterlayerInfo.status == LayerInfo.STATUS_DELETED) continue;

					// 親グループがあれば災害用レイヤにも反映
					if(StringUtil.isNotEmpty(mastermapLayerInfo.parent)) {
						String disasterParent = layerIdMap.get(mastermapLayerInfo.parent);
						if(StringUtil.isNotEmpty(disasterParent)) {
							// 災害用レイヤを取得
							String disasterLayerId = layerIdMap.get(masterlayerInfo.layerId);
							if (StringUtil.isNotEmpty(disasterLayerId)) {
								MapLayerInfo mapLayerInfo = mapDB.getMapLayerInfo(mapid, disasterLayerId);
								if (mapLayerInfo != null) {
									mapLayerInfo.parent = disasterParent;
									mapDB.updateMapLayerInfo(disastermapInfo, mapLayerInfo);
								}
							}
						}
					}
				}
			}
			// 地図をコピーしない
			else {
				//登録情報を記録だけする
				insertTracktableInfo(tmapInfo.id, tablelist);
			}

			//4号様式など、固定テーブル情報のコピー
			tablelist = tablemasterInfoService.findByMapmasterInfoId(0L);
			for (TablemasterInfo table : tablelist) {
				// テーブル情報の保存
				TracktableInfo ttable = new TracktableInfo();
				ttable.layerid = "";
				ttable.tablemasterinfoid = table.id;
				ttable.tablename = table.tablename;
				ttable.trackmapinfoid = tmapInfo.id;
				tracktableInfoService.insert(ttable);
			}
			logger.info("tracktableInfoService.insert");

			try {
				// クリアリングハウスのメタデータを更新
				if(isTraining) {
					clearinghouseService.onCreateTrainingMap(trackData.trackmapinfoid);
				}
				else {
					clearinghouseService.onCreateDisasterMap(trackData.trackmapinfoid);
				}
			} catch (Exception e) { logger.error(e.getMessage(), e); }
			logger.info("clearinghouseService.onCreateDisasterMap");

    	} catch (Exception e) {
    		e.printStackTrace();
			logger.error(e.getMessage(), e);
			// ServiceException ならそのままなげる
			if(e instanceof ServiceException) throw (ServiceException) e;
			return false;
		}

    	return true;
    }

	/**
	 * 記録テーブル情報を登録する
	 * @param trackmapinfoId
	 * @param tablelist
	 * @return 登録結果
	 */
	public List<TracktableInfo> insertTracktableInfo(long trackmapinfoId, List<TablemasterInfo> tablelist) {
		List<TracktableInfo> results = new ArrayList<>();
		for (TablemasterInfo table : tablelist) {
			TracktableInfo ttable = null;
			if (!StringUtil.isEmpty(table.layerid)) {
				// レイヤがあるかどうか
				LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(table.layerid);
				if(layerInfo==null) {
					logger.warn(table.layerid+" not found. fail shareMapLayer");
				}
				try {
					// 属性ID trackdataid が未追加なら追加する
					trackdataidAttrService.alterTableAddTrackdataidColumnIfNotExists(table.layerid);
					// 属性ID st_edituser が未追加なら追加する
					edituserAttrService.alterTableAddEdituserColumnIfNotExists(table.layerid);

					// テーブル情報の保存
					ttable = new TracktableInfo();
					ttable.layerid = table.layerid;
					ttable.tablemasterinfoid = table.id;
					ttable.tablename = table.layerid;
					ttable.trackmapinfoid = trackmapinfoId;
					tracktableInfoService.insert(ttable);
				} catch(Exception e) {
					throw new ServiceException(lang.__("Failed to create record table info. layerId={0} tablemasterinfoid={1}", table.layerid, table.id), e);
				}
			}
			results.add(ttable);
		}
		return results;
	}

    /**
     * 災害地図を削除
     * @param trackData
     * @return 成功：TRUE、失敗：FALSE
     */
    public boolean deleteDisasterMap(TrackData trackData)
    {
		MapDB mapDB = MapDB.getMapDB();
    	try {
    		TrackmapInfo tmapInfo = trackmapInfoService.findByTrackDataId(trackData.id);
    		MapInfo mapInfo = mapDB.getMapInfo(tmapInfo.mapid);
    		for (int i = 0; i < mapInfo.countMapLayerInfo(); i++) {
    			MapLayerInfo mlay = mapInfo.getMapLayerInfo(i);
        		Vector<MapInfo> minfos = mapDB.getLayerUsedMapInfo(mlay.layerId);
        		if (minfos != null && minfos.size() == 1) {
        			LayerInfo layer = mapDB.getLayerInfo(mlay.layerId);
        			if (layer != null) {
	        			layer.status = LayerInfo.STATUS_DELETED;
	        			mapDB.updateLayerInfo(layer);
        			}
        		}
    		}
    		return mapDB.updateMapStatus(mapInfo, MapInfo.STATUS_DELETED);
    	} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			return false;
		}
    }

	/**
	 * 記録データの更新。
	 * 災害名称に応じて、eコミの地図名称も更新する。
	 */
	@Override
	public int update(TrackData entity) {
		int result = super.update(entity);
		if(entity.trackmapinfoid!=null) {
			updateMapInfo(entity.trackmapinfoid);
		}
		else logger.warn("cannot update _map.\nTrackData("+entity.id+") trackmapinfoid is null");
    	return result;
    }

	/**
	 * eコミマップの地図情報を更新します.
	 * @param trackmapinfoid
	 */
	public void updateMapInfo(long trackmapinfoid) {
		// eコミの地図情報を更新
		//TrackmapInfo trackmapInfo = trackmapInfoService.findById(trackmapinfoid);
		TrackmapInfo trackmapInfo = trackmapInfoService.findByIdLeftJoinTrackDatas(trackmapinfoid);
		if(trackmapInfo!=null && trackmapInfo.mapid!=null) {
			MapDB mapDB = MapDB.getMapDB();
			MapInfo mapInfo = mapDB.getMapInfo(trackmapInfo.mapid);
			if(mapInfo!=null) {
				// 地図マスター情報を取得
				TrackData track = trackmapInfo.getTrackDatas().get(0); // track_data は論理削除なので最低１件あるはず
				long localgovinfoid = track.localgovinfoid;
				MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(localgovinfoid);
				List<TrackData> trackDatas = null;

				boolean isTraining = track.isTraining();
				boolean isCopy = false;

				// 訓練マップの場合
				if(isTraining) {
					// 訓練データがすべて削除されていれば、マップを削除
					trackDatas = findByTrackmapinfoidAndNotDeleted(trackmapinfoid, /*training*/true);
					// 官民の場合、訓練マップは訓練のたびにマップをコピーする。
					isCopy = true;
				}
				// 災害マップの場合
				else {
					// 災害データがすべて削除されていれば、マップを削除
					trackDatas = findByTrackmapinfoidAndNotDeleted(trackmapinfoid);
					// 地図コピーフラグの設定
					isCopy = mapmasterInfo.copy;
				}

				// マップをコピーしていない場合はなにもしない。
				if(isCopy==false) return;

				// すべて削除の場合は論理削除地図削除
				boolean isDeleted = trackDatas.size()==0;
				if(isDeleted) {
					mapInfo.status = MapInfo.STATUS_DELETED;
				}
				// 更新処理の場合
				else {
					// すべての災害が完了しているか
					boolean isComplete = true;
					for(TrackData trackData : trackDatas) if(trackData.endtime==null) isComplete=false;

					// マップタイトル
					{
						// 災害終了時なら自治体名のマスターマップに戻す
						// ただし、訓練マップは訓練のまま
						if(isTraining==false && isComplete) {
							// 自治体名
							LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
							String localgovName = localgovInfo.pref;
							if(StringUtil.isNotEmpty(localgovInfo.city)) localgovName += localgovInfo.city;
							mapInfo.title = localgovName+lang.__("Master map");
						}
						// 災害起動時の場合
						else if(0<trackDatas.size()) {
							// 最初の記録データを使用する
							TrackData trackData = trackDatas.get(0);
							mapInfo.title = trackData.name + (1<trackDatas.size()?lang.__("(Simultaneous disaster {0} items)", trackDatas.size()):"");
						}
					}

					// マップの説明
					{
						String signature = "=====\n";
						StringBuffer description = new StringBuffer();
						boolean first = true;
						for(TrackData trackData : trackDatas) {
							// 改行の挿入
							if(first) first = false;
							else description.append("\n");
							// 災害名称の挿入
							description.append(trackData.name);
							if(trackData.starttime!=null) {
								description.append(lang.__("(Time period: "));
								description.append(toWareki(trackData.starttime));
								description.append(lang.__("to"));
								if(trackData.endtime!=null) {
									description.append(toWareki(trackData.endtime));
								}
								description.append(")");
							}
						}
						description.insert(0, lang.__("[Disaster list]") + "\n");
						// 前後にシグネチャを追加
						description = description.insert(0, signature).append("\n"+signature);
						// すでにあれば、書き換え
						String regex = (signature+"(.|\\n)*"+signature); // エスケープ
						// 部分一致するか
						if(mapInfo.description.matches("(.|\\n)*"+regex+"(.|\\n)*")) {
							mapInfo.description = mapInfo.description.replaceAll(regex, description.toString());
						}
						// なければ、追記
						else {
							if(StringUtil.isNotEmpty(mapInfo.description)) {
								description.insert(0, "\n");
								description.insert(0, mapInfo.description);
							}
							mapInfo.description = description.toString();
						}
					}
				}

				// DB 更新
				try {
					mapDB.updateMapInfo(mapInfo);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public DeleteCascadeResult deleteCascade(TrackData entity, DeleteCascadeResult result) {

		result.cascade(AlarmmessageData.class, Names.alarmmessageData().trackdataid(), entity.id);
		result.cascade(AlertcontentData.class, Names.alertcontentData().trackdataid(), entity.id);
		result.cascade(AssemblestateData.class, Names.assemblestateData().trackdataid(), entity.id);
		result.cascade(DisasterbuildData.class, Names.disasterbuildData().trackdataid(), entity.id);
		result.cascade(DisastercasualtiesData.class, Names.disastercasualtiesData().trackdataid(), entity.id);
		result.cascade(DisasterfarmData.class, Names.disasterfarmData().trackdataid(), entity.id);
		result.cascade(DisasterfireData.class, Names.disasterfireData().trackdataid(), entity.id);
		result.cascade(DisasterhospitalData.class, Names.disasterhospitalData().trackdataid(), entity.id);
		result.cascade(DisasterhouseData.class, Names.disasterhouseData().trackdataid(), entity.id);
		result.cascade(DisasterhouseholdData.class, Names.disasterhouseholdData().trackdataid(), entity.id);
		result.cascade(DisasterhouseregidentData.class, Names.disasterhouseregidentData().trackdataid(), entity.id);
		result.cascade(DisasterlifelineData.class, Names.disasterlifelineData().trackdataid(), entity.id);
		result.cascade(DisasterroadData.class, Names.disasterroadData().trackdataid(), entity.id);
		result.cascade(DisasterschoolData.class, Names.disasterschoolData().trackdataid(), entity.id);
		result.cascade(DisastersummaryhistoryData.class, Names.disastersummaryhistoryData().trackdataid(), entity.id);
		result.cascade(DisasterwelfareData.class, Names.disasterwelfareData().trackdataid(), entity.id);
		result.cascade(GeneralizationhistoryData.class, Names.generalizationhistoryData().trackdataid(), entity.id);
		result.cascade(HeadofficeData.class, Names.headofficeData().trackdataid(), entity.id);
		result.cascade(JalerttriggerData.class, Names.jalerttriggerData().trackdataid(), entity.id);
		result.cascade(LoginData.class, Names.loginData().trackdataid(), entity.id);
		result.cascade(MeteotriggerData.class, Names.meteotriggerData().trackdataid(), entity.id);
		result.cascade(NoticemailData.class, Names.noticemailData().trackdataid(), entity.id);
		result.cascade(PubliccommonsReportData.class, Names.publiccommonsReportData().trackdataid(), entity.id);
		result.cascade(ReportData.class, Names.reportData().trackdataid(), entity.id);
		result.cascade(ThreadData.class, Names.threadData().trackdataid(), entity.id);
		result.cascade(TrackgroupData.class, Names.trackgroupData().citytrackdataid(), entity.id);
		result.cascade(TrackgroupData.class, Names.trackgroupData().preftrackdataid(), entity.id);
		if(entity.trackmapinfoid!=null){
			long trackmapinfoid = entity.trackmapinfoid;
			entity.trackmapinfoid = null;
			update(entity);
			result.cascade(TrackmapInfo.class, Names.trackmapInfo().id(), trackmapinfoid);
		}
		if(entity.trainingplandataid!=null){
			long trainingplandataid = entity.trainingplandataid;
			entity.trainingplandataid = null;
			update(entity);
			result.cascade(TrainingplanData.class, Names.trainingplanData().id(), trainingplandataid);
		}
		result.cascade(ImporttrackInfo.class, Names.importtrackInfo().trackdataid(), entity.id);

		// 子テーブルのレコード削除が正常に完了後、自テーブルの対象レコードを削除
		return super.deleteCascade(entity, result);
	}
}
