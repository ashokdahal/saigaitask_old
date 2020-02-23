/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;


import java.util.List;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.PostingphotolayerInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@jp.ecom_plat.saigaitask.action.RequestScopeController
public class SendlayerInfosAction extends AbstractApiAction{

	/** 班情報サービス */
	@Resource
	protected GroupInfoService groupInfoService;
	/** 課情報サービス */
	@Resource
	protected UnitInfoService unitInfoService;
	/** 災害情報サービス */
	@Resource
	protected TrackDataService trackDataService;
	/** テーブルマスター情報サービス */
	@Resource
	protected TablemasterInfoService tablemasterInfoService;
	/** 地図マスター情報サービス */
	@Resource
	protected MapmasterInfoService mapmasterInfoService;
	/** 災害地図情報サービス */
	@Resource
	protected TrackmapInfoService trackmapInfoService;
	/** 災害記録テーブル情報サービス */
	@Resource
	protected TracktableInfoService tracktableInfoService;
	/** 被災写真投稿レイヤ情報サービス */
	@Resource
	protected PostingphotolayerInfoService postingphotolayerInfoService;

	/**
	 * 投稿先レイヤ情報取得API
	 * 引数付きならpostlayerを利用して情報を返す
	 * @param postlayerid 投稿先LayerID
	 * @return null
	 */
	@RequestMapping(value={"/api/v2/sendlayerInfos/{postlayerid}"})
	@ResponseBody
	public String index(@PathVariable String postlayerid) {

		// 認証
		if(apiDto.authorize()==false) {
			return apiDto.error401NotAuthorized(response);
		}
		
		// 返却用
		JSONObject jsonresult = new JSONObject();
		// results
		JSONArray results = new JSONArray();
		// 自治体ID
		Long localgovinfoid = 0L;
		//Long localgovInfoId = 0L;
		// 班でログイン
		if(apiDto.getGroupInfo()!=null && !apiDto.getGroupInfo().id.equals(0L)) {
			// Get LocalgovInfo
			GroupInfo groupInfo = groupInfoService.findByNotDeletedId(apiDto.getGroupInfo().id);
			localgovinfoid = groupInfo==null ? 0L : groupInfo.localgovinfoid;
		}
		// 課でログイン
		else if(apiDto.getUnitInfo()!=null && !apiDto.getUnitInfo().id.equals(0L)){
			UnitInfo unitInfo = unitInfoService.findByNotDeletedId(apiDto.getUnitInfo().id);
			localgovinfoid = unitInfo==null ? 0L : unitInfo.localgovinfoid;
		}
		// 初期値
		jsonresult.put("total", 0);
		jsonresult.put("results", results);
		jsonresult.put("error", "");
		// 自治体IDで検索
		if(!localgovinfoid.equals(0L) && postlayerid != null){
			long mid = 0;
			int cid = 0;
			String postLayerId = "";
			// 災害マップ用
			/*
			long t_mid = 0;
			int t_cid = 0;
			String t_postLayerId = "";
			*/
			// 現在災害モードかどうか
			List<TrackData> trackDataList = trackDataService.findByCurrentTrackDatas(localgovinfoid, false);
			// 現在訓練モードかどうか
			List<TrackData> trainingDataList = trackDataService.findByCurrentTrackDatas(localgovinfoid, true);

			// 投稿先LayerID無しの場合は、振り分け設定から取得する
			// これは将来的な機能で、現状は利用出来ないようにする
			/*
			if(postlayerid == null){
				// 振り分け設定から取得(ID順)
				List<PostingphotolayerInfo> postingphotolayerInfoList = postingphotolayerInfoService.findByLocalgovInfoId(localgovinfoid);
				// マスタマップを取得する
				List<MapmasterInfo> mapmasterInfoList = mapmasterInfoService.findByLocalgovinfoid(localgovinfoid, false);
				if(postingphotolayerInfoList.size() > 0 && mapmasterInfoList.size() > 0){
					// 先頭のレコードを利用する
					Long tablemasterinfoid = postingphotolayerInfoList.get(0).tablemasterinfoid;
					MapmasterInfo mapmasterInfo = mapmasterInfoList.get(0);
					// レイヤ名を取得する
					TablemasterInfo tablemasterInfo = tablemasterInfoService.findByNotDeletedId(tablemasterinfoid);
					postLayerId = tablemasterInfo.layerid;
					// 平時ならtablemaster_infoのレイヤIDを返却
					if(trackDataList.size() == 0 && trainingDataList.size() == 0){
						mid = mapmasterInfo.mapid;
						cid = mapmasterInfo.communityid;
					}
					// 災害or訓練モードならtracktable_infoから取得する
					else{
						Long tid = 0L;
						// どっちかでIDを取得する
						for(TrackData trackdata : trackDataList){
							tid = trackdata.id;
						}
						for(TrackData trackdata : trainingDataList){
							tid = trackdata.id;
						}
						// 紐付くtracktable_info, trackmap_infoを取得
						TracktableInfo tracktableInfo = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tablemasterInfo.id, tid);
						if(tracktableInfo != null){
							TrackmapInfo trackmapInfo = trackmapInfoService.findById(tracktableInfo.trackmapinfoid);
							mid = trackmapInfo.mapid;
							cid = trackmapInfo.communityid;
							postLayerId = tracktableInfo.layerid;
						}
					}
				}
			}
			*/
			// 投稿先LayerIDから検索
			//else{

			// マスタマップを取得する
			List<MapmasterInfo> mapmasterInfoList = mapmasterInfoService.findByLocalgovinfoid(localgovinfoid, false);
			// レイヤ名を取得する
			List<TablemasterInfo> tablemasterInfoList = tablemasterInfoService.findByLayerId(postlayerid, false);
			if(mapmasterInfoList.size() > 0 && tablemasterInfoList.size() > 0){
				// 先頭のレコードを利用する
				MapmasterInfo mapmasterInfo = mapmasterInfoList.get(0);
				// 平時ならtablemaster_infoのレイヤIDを返却
				if(trackDataList.size() == 0 && trainingDataList.size() == 0){
					mid = mapmasterInfo.mapid;
					cid = mapmasterInfo.communityid;
					postLayerId = postlayerid;
				}
				// 災害or訓練モードならtracktable_infoから取得する
				else{
					Long tid = 0L;
					// どっちかでIDを取得する
					for(TrackData trackdata : trackDataList){
						tid = trackdata.id;
					}
					for(TrackData trackdata : trainingDataList){
						tid = trackdata.id;
					}
					// 紐付くtracktable_info, trackmap_infoを取得
					TracktableInfo tracktableInfo = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(tablemasterInfoList.get(0).id, tid);
					if(tracktableInfo != null){
						TrackmapInfo trackmapInfo = trackmapInfoService.findById(tracktableInfo.trackmapinfoid);
						mid = trackmapInfo.mapid;
						cid = trackmapInfo.communityid;
						postLayerId = tracktableInfo.layerid;
					}
				}
			}else{
				if(mapmasterInfoList.size() == 0){
					jsonresult.put("error", lang.__("Master map belonging to authenticated local gov. not exists."));
				}
				if(tablemasterInfoList.size() == 0){
					jsonresult.put("error", lang.__("Post target layer ID not exists on table master."));
				}
			}

			//}
			JSONObject obj = new JSONObject();
			obj.put("mid", mid);
			obj.put("cid", cid);
			obj.put("layerid", postLayerId);
			/*
			obj.put("t_mid", t_mid);
			obj.put("t_cid", t_cid);
			obj.put("t_layerid", t_postLayerId);
			*/
			results.add(obj);
			jsonresult.put("total", 1);
			jsonresult.put("results", results);

		}else{
			jsonresult.put("error", lang.__("Authenticated local gov. ID not exists."));
		}
		responseJSONObject(jsonresult);

		return null;
	}
}
