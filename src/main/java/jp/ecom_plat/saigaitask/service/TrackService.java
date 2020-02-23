/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.service;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.Localgovtype;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackgroupData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.form.TrackForm;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.LocalgovInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackgroupDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.util.ListUtil;

import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;

@org.springframework.stereotype.Service
public class TrackService extends AbstractService{

	@Resource
	protected TrackDataService trackDataService;
	@Resource
	protected ClearinghouseService clearinghouseService;
	@Resource
	protected TrackgroupDataService trackgroupDataService;
	@Resource
	protected UserTransaction userTransaction;
	@Resource
    protected AlarmmessageDataService alarmmessageDataService;
	@Resource
	protected LocalgovInfoService localgovInfoService;
	@Resource protected MapmasterInfoService mapmasterInfoService;
	@Resource protected TrackmapInfoService trackmapInfoService;

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public TrackData insertDisaster(long localgovinfoid, TrackForm trackForm, GroupInfo groupInfo){
		// 訓練中であれば完了処理を行う
		List<TrackData> trainingDatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), true);
		if(trainingDatas.size() > 0){
			for(TrackData  trainingData : trainingDatas) {
				Timestamp now = new Timestamp(System.currentTimeMillis());
				trainingData.endtime = now;
				trackDataService.update(trainingData);
			}
			// クリアリングハウスのメタデータを更新
			clearinghouseService.onCompleteTrainingMap();
		}
		// すでに記録データがあるかどうか
		List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(localgovinfoid);
		Collections.sort(trackDatas, TrackData.orderbyStarttimeDesc);
		TrackData trackData = 0<trackDatas.size() ? trackDatas.get(0) : null;
		boolean existTrackData = trackData != null;

		TrackData entity = null;
		//災害の登録のみ別トランザクション
		try {
	        userTransaction.begin();
	        Timestamp now = new Timestamp(System.currentTimeMillis());
			entity = Beans.createAndCopy(TrackData.class, trackForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
			entity.demoinfoid = 0l;
			entity.starttime = now;
			entity.endtime = null;
			entity.localgovinfoid = localgovinfoid;
			entity.trackmapinfoid = existTrackData ? trackData.trackmapinfoid : null; // すでに記録データがあれば、同じtrackmapを使う
			entity.deleted = false;
			entity.trainingplandataid = null;
			trackDataService.insert(entity);

			// 災害グループがあれば登録
			trackgroupDataService.update(entity.id, ListUtil.toLongList(trackForm.citytrackdataids));
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			try {
				userTransaction.setRollbackOnly();
			} catch (Exception e1) {logger.error(loginDataDto.logInfo(), e1);}
		} finally {
			try {
				if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
					userTransaction.commit();
				} else {
					userTransaction.rollback();
				}
			} catch (Exception e) {logger.error(loginDataDto.logInfo(), e);}
		}

		//地図作成
		if(existTrackData==false) {
			boolean create = true;
			try {
		        userTransaction.begin();
		        //地図、レイヤの作成
				boolean copyMap = Boolean.valueOf(trackForm.copyMap);
				if (!trackDataService.createDisasterMap(localgovinfoid, groupInfo, entity, copyMap)) {
					create = false;
					userTransaction.setRollbackOnly();
				}

			} catch (Exception e) {
				logger.error(loginDataDto.logInfo(), e);
				try {
					userTransaction.setRollbackOnly();
				} catch (Exception e1) {logger.error(loginDataDto.logInfo(), e1);}
				// ServiceException ならそのままなげる
				if(e instanceof ServiceException) throw (ServiceException) e;
			} finally {
				try {
					if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
						userTransaction.commit();
					} else {
						userTransaction.rollback();
					}
				} catch (Exception e) {logger.error(loginDataDto.logInfo(), e);}
			}

			if (!create) {//作成失敗で災害を削除
				try {
					userTransaction.begin();
					trackDataService.delete(entity);
				} catch (Exception e) {
					logger.error(loginDataDto.logInfo(), e);
					try {
						userTransaction.setRollbackOnly();
					} catch (Exception e1) {logger.error(loginDataDto.logInfo(), e1);}
				} finally {
					try {
						if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
							userTransaction.commit();
						} else {
							userTransaction.rollback();
						}
					} catch (Exception e) {logger.error(loginDataDto.logInfo(), e);}
				}
				throw new ServiceException(lang.__("Unable to register disaster because failed to create map or layer."));
			}
		}
		// 地図作成済み
		else {
			// 地図情報の更新
			trackDataService.updateMapInfo(entity.trackmapinfoid);
		}
		return entity;
	}

	public String updateDisaster(long localgovinfoid, long groupid, TrackForm trackForm) {
		// 記録データの更新
		TrackData entity = Beans.createAndCopy(TrackData.class, trackForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
		trackDataService.update(entity);

		// ログイン中の記録データを切り替え
		trackDataService.loginTrackData(entity);

		// 県の場合
		LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
		if(Localgovtype.PREF.equals(localgovInfo.localgovtypeid)) {
			// 災害グループがあれば更新
			trackgroupDataService.update(entity.id, ListUtil.toLongList(trackForm.citytrackdataids));
			// 災害グループの名称を統一する
			if("true".equals(trackForm.overrideTrackDataName)) {
				// 災害名称を上書き
				List<TrackgroupData> trackgroupDatas = trackgroupDataService.findByPreftrackdataid(entity.id);
				for(TrackgroupData trackgroupData : trackgroupDatas) {
					TrackData trackData = trackgroupData.cityTrackData;
					if(trackData!=null) {
						String oldTrackDataName = trackData.name;
						// 災害か訓練かで表示する文章を切り替える
						String alarmMessage = "";
						if(trackData.trainingplandataid == null){
							alarmMessage = lang.__("Disaster");
						}else{
							alarmMessage = lang.__("Training");
						}
						if(entity.name.equals(oldTrackDataName)==false) {
							logger.info(lang.__("Change {1} name of {0} from {2} to {3}", trackData.localgovInfo.city, alarmMessage, oldTrackDataName, entity.name));

							trackData.name = entity.name;
							trackDataService.update(trackData);
							// 通知する
							AlarmmessageData alarm = new AlarmmessageData();
							alarm.localgovinfoid = trackData.localgovinfoid;
							alarm.duration = 0;
							alarm.message = lang.__("{0} changed {1} name {2} to {3}. ", localgovInfo.pref, alarmMessage, oldTrackDataName, entity.name);
							alarm.messagetype = "information";
							alarm.showmessage = true; // ポップアップ有り？
							alarm.sendgroupid = groupid; // ログイン中の班が送信班
							alarm.groupid = 0L; // すべての班
							alarm.registtime = new Timestamp(System.currentTimeMillis());
							alarm.trackdataid = trackData.id;
							alarm.deleted = false;
							alarmmessageDataService.insert(alarm);
						}
						else {
							logger.info(lang.__("Not update and notice for {1} name of {0} is already {2}", trackData.localgovInfo.city, alarmMessage, entity.name));
						}
					}
				}
			}
		}

		return "forward:/track/success";
	}

	public String completeDisaster(long localgovinfoid, TrackForm trackForm) {
		TrackData entity = Beans.createAndCopy(TrackData.class, trackForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
		// 未指定の場合は、サーバの現在時刻で保存
		if(StringUtil.isEmpty(trackForm.endtime)) {
			Timestamp now = new Timestamp(System.currentTimeMillis());
			entity.endtime = now;
		}
		trackDataService.update(entity);

		// マップの名称を更新
		if(entity.trackmapinfoid!=null) {
			trackDataService.updateMapInfo(entity.trackmapinfoid);
		}

		// 同時複数災害がなければ、平常時に戻る
		List<TrackData> currents = trackDataService.findByCurrentTrackDatas(localgovinfoid, entity.isTraining());
		if(currents.size()==0) {
			// 避難勧告・避難指示全解除
			//publicCommonsService.sendAllCloseEvacuationOrder(entity.id);
			// 避難所全閉鎖
			//publicCommonsService.sendAllCloseShelter(entity.id);

			// クリアリングハウスのメタデータを更新
			if(entity.isTraining()) {
				clearinghouseService.onCompleteTrainingMap();
			}
			else {
				clearinghouseService.onCompleteDisasterMap();
			}
		}

		// ログイン中の記録データを切り替え
		trackDataService.loginTrackData(entity);

		return "forward:/track/success";
	}

	public String deleteDisaster(long tid){
		TrackData track = trackDataService.findById(tid);
        if (track != null) {
        	track.deleted = true;
    		trackDataService.update(track);

			// 同時複数災害がなければ、eコミの地図を削除する
			List<TrackData> notdeletedTrackDatas = trackDataService.findByTrackmapinfoidAndNotDeleted(track.trackmapinfoid);
			if(notdeletedTrackDatas.size()==0) {
	    		TrackmapInfo tmapInfo = trackmapInfoService.findByTrackDataId(track.id);
	    		if(tmapInfo!=null && tmapInfo.mapid!=null) {
					// マスタマップの地図かチェックする
					boolean isMasterMap = false;
					List<MapmasterInfo> mapmasterInfos = mapmasterInfoService.findByLocalgovinfoid(track.localgovinfoid);
					for(MapmasterInfo mapmasterInfo : mapmasterInfos) {
						if(mapmasterInfo.mapid!=null && tmapInfo.mapid.equals(mapmasterInfo.mapid)) {
							isMasterMap = true;
							break;
						}
					}

					// マスターマップでない場合に地図を削除
					if(isMasterMap==false) {
						if (!trackDataService.deleteDisasterMap(track)) {
							throw new ServiceException(lang.__("Unable to delete disaster due to failure of map deletion."));
						}
					}
	    		}
			}
		}

		List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid());
		// 災害モード
		if (0 < trackDatas.size()) {
			TrackData trackData = trackDatas.get(0);
			trackDataService.loginTrackData(trackData);
		}
		else
			loginDataDto.setTrackdataid(0l);

		return "forward:/track/success";
	}
}
