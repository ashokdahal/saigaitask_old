/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jp.ecom_plat.saigaitask.constant.Localgovtype;
import jp.ecom_plat.saigaitask.dto.ThreadListDto;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.ThreadData;
import jp.ecom_plat.saigaitask.entity.db.ThreadresponseData;
import jp.ecom_plat.saigaitask.entity.db.ThreadsendtoData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackgroupData;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanlinkData;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.ThreadDataService;
import jp.ecom_plat.saigaitask.service.db.ThreadresponseDataService;
import jp.ecom_plat.saigaitask.service.db.ThreadsendtoDataService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackgroupDataService;
import jp.ecom_plat.saigaitask.service.db.TrainingplanDataService;
import jp.ecom_plat.saigaitask.service.db.TrainingplanlinkDataService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * 掲示板のアクションクラス
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class BbsAction extends AbstractAction {

	/** Priorityの時間設定 */
	public static final int[] priorityCheckTime = new int[]{-1, 1, 3, 0};

	/** アップロードファイル */
	//@Binding(bindingType = BindingType.NONE)
	//public MultipartFile formFile_bbs = null;
	/** スレッドID(HTTPリクエストパラメータ) */
	//public String form_threaddataid = null;
	/** エラーメッセージ */
	public String errorMsg = "";

	/** 班情報リスト */
	public List<GroupInfo> groupInfoItems;
	/** 課情報リスト */
    public List<UnitInfo> unitInfoItems;
	/** 連携自治体情報県) */
	public LocalgovInfo prefLocalgovInfo;
	/** 連携自治体情報 */
	public List<LocalgovInfo> cityLocalgovInfos;
	/** 班情報 */
	protected GroupInfo groupInfo;
	/** 班情報 */
	public Long groupID;
    /** 課情報 */
	//public Long unitID;
	/** 班情報サービス */
	@Resource
	protected GroupInfoService groupInfoService;
	/** 課情報サービス */
	@Resource
	protected UnitInfoService unitInfoService;
	/** スレッド情報 */
	@Resource
	protected ThreadDataService threadDataService;
	/** スレッド応答メッセージ情報 */
	@Resource
	protected ThreadresponseDataService threadresponseDataService;
	/** スレッド送信先情報 */
	@Resource
	protected ThreadsendtoDataService threadsendtoDataService;
	@Resource
	protected TrackDataService trackDataService;
	@Resource
	protected AlarmmessageDataService alarmmessageDataService;
	/** 訓練プラン情報 */
	@Resource protected TrainingplanDataService trainingplanDataService;
	/** 訓練プラン連携自治体情報 */
	@Resource protected TrainingplanlinkDataService trainingplanlinkDataService;
	/** 災害グループ情報 */
	@Resource protected TrackgroupDataService trackgroupDataService;

	/** スレッド一覧List */
	public List<ThreadListDto> threadListData = new ArrayList<ThreadListDto>();
	/** 選択されたスレッドのデータList */
	public Map<String, String> threadData = new HashMap<String, String>();
	// スレッドデフォルトタイトル
	public String thread_default_title = "";


	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("errorMsg", errorMsg);
		model.put("groupInfoItems", groupInfoItems);
		model.put("unitInfoItems", unitInfoItems);
		model.put("prefLocalgovInfo", prefLocalgovInfo);
		model.put("cityLocalgovInfos", cityLocalgovInfos);
		model.put("groupInfo", groupInfo);
		model.put("groupID", groupID);
		model.put("threadListData", threadListData);
		model.put("threadData", threadData);
		model.put("thread_default_title", thread_default_title);
	}

	/**
	 * 掲示板の初期ページを表示する
	 * @return フォワード先
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/bbs","/bbs/index"})
	public String index(Map<String,Object>model) {
    	// ログインユーザのGroupIDを格納
    	groupID = loginDataDto.getGroupid();
    	if (loginDataDto.isUsual())
    		groupID = loginDataDto.getUnitid();
    	// 自治体情報を取得
    	LocalgovInfo localgovInfo = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
    	// 自自治体が訓練モードかどうか確認する
    	boolean trainingMode = false;
		long trainingdataid = 0L;
    	if(loginDataDto.getTrackdataid() > 0){
    		TrackData nowTrackData = trackDataService.findById(loginDataDto.getTrackdataid());
    		// 訓練モード
    		if(nowTrackData.trainingplandataid != null){
    			trainingMode = true;
				trainingdataid = nowTrackData.trainingplandataid;
    			thread_default_title = lang.__("[Training]");
    		}
    	}
    	// 連携する県の自治体IDを取得
    	long preflocalgovinfoid = localgovInfoService.havePrefLocalgovinfoid(localgovInfo);
    	// 自治体連携有
    	if(preflocalgovinfoid > 0){
    		prefLocalgovInfo = localgovInfoService.findById(preflocalgovinfoid);
    		if(trainingMode){
    			// 初期化
    			cityLocalgovInfos = new ArrayList<LocalgovInfo>();
    			// 現在の訓練情報(track_data)を取得する
    			//List<TrackData> myTrackDataList = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), true);
    			// 災害グループが複数あると自治体を重複して取得する可能性があるので、一旦リストに収める
    			//List<Long> chainCityLocalgovList = new ArrayList<Long>();
				List<TrainingplanlinkData> trainingplanlinkDataList = trainingplanlinkDataService.findByTrainingplandataId(trainingdataid);
				for(TrainingplanlinkData links : trainingplanlinkDataList){
					List<TrackData> linksTrackDataList = trackDataService.findByCurrentTrackDatas(links.localgovinfoid, true);
					// 訓練中なら登録する
					if(linksTrackDataList.size() > 0){
						if(links.localgovinfoid.equals(preflocalgovinfoid)){
							groupInfoItems = groupInfoService.findByLocalgovInfoIdAndValid(preflocalgovinfoid);
						}else{
							LocalgovInfo chainCityInfo = localgovInfoService.findById(links.localgovinfoid);
							chainCityInfo.groupInfos = groupInfoService.findByLocalgovInfoIdAndValid(links.localgovinfoid);
							cityLocalgovInfos.add(chainCityInfo);
						}

					}
				}


    		}else{
    			// 平時または災害モード
    			// 県の班を検索
				if (!loginDataDto.isUsual())
					groupInfoItems = groupInfoService.findByLocalgovInfoIdAndValid(preflocalgovinfoid);
				else
					unitInfoItems = unitInfoService.findByLocalgovInfoIdAndValid(preflocalgovinfoid);
    			// 連携自治体 を検索
    			cityLocalgovInfos = localgovInfoService.findCityLocalgovInfo(preflocalgovinfoid);
    			for(LocalgovInfo cityLocalgovinfo : cityLocalgovInfos) {
    				if (!loginDataDto.isUsual())
    					cityLocalgovinfo.groupInfos = groupInfoService.findByLocalgovInfoIdAndValid(cityLocalgovinfo.id);
    				else
    					cityLocalgovinfo.unitInfos = unitInfoService.findByLocalgovInfoIdAndValid(cityLocalgovinfo.id);
    			}
			}
		}
		// 県が設定されていない自治体は自班を探す
		else{
			// 初期化
			groupInfoItems = new ArrayList<GroupInfo>();
			// ログイン班と同一の自治体班を探す
			cityLocalgovInfos = localgovInfoService.findByLocalgovinfoid(loginDataDto.getLocalgovinfoid(), false);
			// 取得した自治体が持つ班(group)を取得
			if(cityLocalgovInfos != null){
				for(LocalgovInfo cityLocalgovinfo : cityLocalgovInfos) {
    				if (!loginDataDto.isUsual())
    					cityLocalgovinfo.groupInfos = groupInfoService.findByLocalgovInfoIdAndValid(cityLocalgovinfo.id);
    				else
    					cityLocalgovinfo.unitInfos = unitInfoService.findByLocalgovInfoIdAndValid(cityLocalgovinfo.id);
				}
			}
		}

    	setupModel(model);

		return "/bbs/index";
	}

	/**
	 * ファイルアップロード処理
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/bbs/upload")
	public String upload(Map<String,Object>model, @RequestParam("formFile_bbs") MultipartFile formFile_bbs, @RequestParam("form_threaddataid") String form_threaddataid) {
		if(formFile_bbs != null) {
			String path = FileUtil.upload(application, loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid(), formFile_bbs);
			if(path.indexOf("/upload") != -1){
				path = path.substring(path.indexOf("/upload") + 7, path.length());
			}
			// コメントを作成
			ThreadresponseData responseData = new ThreadresponseData();
			responseData.threaddataid = Long.parseLong(form_threaddataid);
			if (!loginDataDto.isUsual())
				responseData.groupid = loginDataDto.getGroupid();
			else
				responseData.unitid = loginDataDto.getUnitid();
			responseData.message = path;
			responseData.fileflag = true;
			responseData.registtime = new Timestamp(System.currentTimeMillis());
			int cnt = threadresponseDataService.insert(responseData);
			if(cnt > 0){
				// 更新時間を更新
				ThreadsendtoData threadsendtoData = threadsendtoDataService.findBythreaddataIdGroupId(responseData.threaddataid, loginDataDto.isUsual(), responseData.groupid, responseData.unitid);
				threadsendtoData.updatetime = new Timestamp(System.currentTimeMillis());
				threadsendtoDataService.update(threadsendtoData);
			}
			logger.info("bbs file upload : " + path);
		}

		setupModel(model);

    	return "/bbs/upload";
    }

	/**
	 * ファイル削除処理
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/bbs/fileDelete", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String fileDelete() {

		JSONObject json = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		/* パラメータを取得 */
		String deleteFileID = request.getParameter("deleteFile");
		// スレッドID
		String threadid = request.getParameter("threaddataid");
		if(threadid == null) return null;

		try{
			/* キャストする */
			Long columnID = Long.parseLong(deleteFileID);
			ThreadresponseData threadresponseData = threadresponseDataService.findById(columnID);
			// fileflagがtrueのときのみ
			if(threadresponseData.fileflag){
				String filename = threadresponseData.message;
				// ファイルの存在チェック
				String filepath = application.getRealPath("/upload/") + filename;
				File files = new File(filepath);
				// rename用 (重複は後ほど考慮する)
				String filepath_rename = application.getRealPath("/upload/") + filename + "_del";
				File files_rename = new File(filepath_rename);
				// ファイルが存在していたらリネームして対応
				if(files.exists()){
					int i = 0;
					// 重複対応
					while(!files.renameTo(files_rename)){
						i++;
						filepath_rename = application.getRealPath("/upload/") + filename + "_del" + i;
						files_rename = new File(filepath_rename);
					}
					// レコードの削除
					threadresponseDataService.delete(threadresponseData);

					// 自班のスレッドの更新時間を更新
					ThreadsendtoData threadsendtoData = threadsendtoDataService.findBythreaddataIdGroupId(Long.parseLong(threadid), loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid());
					threadsendtoData.updatetime = new Timestamp(System.currentTimeMillis());
					threadsendtoDataService.update(threadsendtoData);

					json.put("message", lang.__("Deletion processing has completed<!--2-->"));
					// 出力
					//PrintWriter out = response.getWriter();
					//out.print( json.toString() );
					return json.toString();
				}
			}
		}catch(NumberFormatException e){
			// 数字じゃないのでそのまま終了
			return null;
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 * ログイン班に関係するスレッドで未読がついているスレッドの数を取得
	 * @return 未読スレッドの数
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/bbs/getUnReadThread")
	@ResponseBody
	public String getUnReadThread(){
		JSONObject json = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		// スレッド一覧のIDを格納する配列
		Long[] ownThreadListID = threadsendtoDataService.findByOwnThreadList(loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid());
		// カウント
		int cnt = 0;
		try {
			for(int i = 0; i < ownThreadListID.length; i++){
				// 自身が関係するスレッドデータを取得
				ThreadData threadDataSQL = threadDataService.findById(ownThreadListID[i]);
				if(threadDataSQL == null) continue;
				// 該当スレッドの自身のグループが最終確認した日時
				Timestamp threadOwnLatestTime = threadsendtoDataService.findByLatestUpdateTime(ownThreadListID[i], loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid());
				// 該当スレッドの未読数をカウントしてClassに格納する
				//int unRead = threadresponseDataService.getThreadUnReadCount(ownThreadListID[i], threadOwnLatestTime);
				int unRead = threadresponseDataService.getThreadUnReadCount(ownThreadListID[i], threadOwnLatestTime, loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid());
				if(unRead > 0){
					cnt++;
				}
			}
			json.put("unReadThread", cnt);
			json.put("message", lang.__("Sent completed."));
			// 出力
			//PrintWriter out = response.getWriter();
			//out.print( json.toString() );
			return json.toString();
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 * ログイン班に関係するスレッドで未読がついているメッセージの数を取得
	 * @return 未読メッセージの数
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/bbs/haveUnReadMessage")
	@ResponseBody
	public String haveUnReadMessage(){
		JSONObject json = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		// スレッド一覧のIDを格納する配列
		//Long[] ownThreadListID = threadsendtoDataService.findByOwnThreadList(loginDataDto.getGroupid());
		// 自身が関係するスレッド一覧
		List<ThreadData> ownThreadList = ownThreadList();

		// カウント
		int cnt = 0;
		try {
			for(ThreadData threaddata : ownThreadList){
				// 該当スレッドの自身のグループが最終確認した日時
				Timestamp threadOwnLatestTime = threadsendtoDataService.findByLatestUpdateTime(threaddata.id, loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid());
				// 該当スレッドの未読数をカウントしてClassに格納する
				int unRead = threadresponseDataService.getThreadUnReadCount(threaddata.id, threadOwnLatestTime, loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid());
				if(unRead > 0){
					cnt += unRead;
				}
			}
			json.put("unReadThread", cnt);
			json.put("message", lang.__("Sent completed."));
			// 出力
			//PrintWriter out = response.getWriter();
			//out.print( json.toString() );
			return json.toString();
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}


	/**
	 * 自班が配信先に含まれるスレッドを取得する
	 * 平時の場合は平時のみ、災害モードの時は時間指定で取得
	 */
	public List<ThreadData> ownThreadList(){
		// 自自治体が訓練モードかどうか確認する
		boolean isTraining = false;
		long trainingplandataid = 0;
		// 災害モードフラグ
		boolean nowTrackFlag = false;
		// 災害復元
		boolean isReproduceTrackFlag = false;
		// 災害データ、訓練データの確認
		if(loginDataDto!=null && loginDataDto.getTrackdataid()!=0L) {
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			isTraining = trackData!=null&&trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
			trainingplandataid = isTraining ? trackData.trainingplandataid : 0;
			nowTrackFlag = trackData != null && trackData.endtime == null ? true : false;
			isReproduceTrackFlag = !nowTrackFlag && loginDataDto.getTrackdataid() > 0L ? true : false;
		}

		// 返却用List
		List<ThreadData> returnThreadDataList = new ArrayList<ThreadData>();
		// 自治体情報を取得
		LocalgovInfo localgovInfo = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
		// 連携する県のlocalgovinfoidを取得
		long preflocalgovinfoid = localgovInfoService.havePrefLocalgovinfoid(localgovInfo);

		// 災害再現中かどうか
		if(isReproduceTrackFlag && !isTraining){
			// 過去の災害を再現中の為、該当災害の開始時間と終了時間の間に生成されたスレッドのみを対象とする
			TrackData oldTrackData = trackDataService.findById(loginDataDto.getTrackdataid());

			// エラーチェック
			if(oldTrackData == null){
				logger.warn(lang.__("Past disaster data now in use is not found."));
			}
			else{
				// 同時複数災害だと時刻がずれるので、trackmapinfoidで再検索
				List<TrackData> oldTrackDataList = trackDataService.findByTrackmapinfoidAndNotDeleted(oldTrackData.trackmapinfoid);
				// 一旦、現在再現中の災害の時刻でセット
				Timestamp startTime = oldTrackData.starttime;
				Timestamp endTime = oldTrackData.endtime;
				if(oldTrackDataList.size() > 1){
					// starttimeで昇順
					Collections.sort(oldTrackDataList, new SortTrackDataComparator(SortTrackDataComparator.ASC, SortTrackDataComparator.STARTTIME));
					// 1つ目を設定する
					startTime = oldTrackDataList.get(0).starttime;
					// endtimeで降順
					Collections.sort(oldTrackDataList, new SortTrackDataComparator(SortTrackDataComparator.DESC, SortTrackDataComparator.ENDTIME));
					// 1つ目を設定する
					endTime = oldTrackDataList.get(0).endtime;
				}
				List<ThreadData> oldThreadData = threadDataService.rangeSearchThreadData(startTime, endTime);

				/*
				// threadsendto_dataのupdatetime順に降順としたいので、ThreadDataのregisttimeにupdatetimeを格納し、ソートを行う
				for(ThreadData td : oldThreadData){
					// 全グループの最終更新時間を取得
					td.registtime = threadsendtoDataService.findByLatestUpdateTime(td.id,null);
				}
				// 降順ソート
				Collections.sort(oldThreadData, new DESCComparator());
				*/

				for(ThreadData thread : oldThreadData){
					// 自班が該当スレッドの送信先に設定されていなければ無視する
					if(!threadsendtoDataService.checkThreadsendtoMe(thread.id, loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid())){ continue; }
					returnThreadDataList.add(thread);
				}
			}
		}
		// 訓練モード(復元も動作は同じ)
		else if(isTraining){
			// 平時に作成されたスレッドは必ず取得する
			List<ThreadsendtoData> sendtoThreadDataList = threadsendtoDataService.findByOwnNormalThreadList(loginDataDto.getGroupid());
			// 訓練時の連携テーブルを参照する
			List<TrainingplanlinkData> trainingplanlinkDataList = trainingplanlinkDataService.findByTrainingplandataId(trainingplandataid);
			if(trainingplanlinkDataList != null){
				for(TrainingplanlinkData linkData : trainingplanlinkDataList){
					if(linkData == null) continue;
					// 連携先の同一訓練データを取得する
					List<TrackData> trainingDataList = trackDataService.findBytrainingplandataidAndLocalgovinfoid(trainingplandataid, linkData.localgovinfoid);
					// 同一訓練データが無ければ何もしない
					if(trainingDataList == null || trainingDataList.size() == 0) continue;
					// 複数同時訓練モードは想定していないので、基本的にはsize=1
					for(TrackData trackData : trainingDataList){
						List<ThreadsendtoData> sendtoTrackThreadDataList = threadsendtoDataService.findByOwnTrackThreadList(loginDataDto.getGroupid(), trackData.id);
						for(ThreadsendtoData sends : sendtoTrackThreadDataList){
							sendtoThreadDataList.add(sends);
						}
					}
				}
			}
			for(ThreadsendtoData sends : sendtoThreadDataList){
				if(sends == null) continue;
				if(sends.threadData == null) continue;
				returnThreadDataList.add(sends.threadData);
			}
		}else{
			// 平時モード or 災害モード中
			// 平時に作成されたスレッドは必ず取得する
			List<ThreadsendtoData> sendtoThreadDataList = threadsendtoDataService.findByOwnNormalThreadList(loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid());
			// 連携する自治体を取得
			List<LocalgovInfo> chainLocalgovList = localgovInfoService.findCityLocalgovInfo(preflocalgovinfoid);

			// 自自治体が平時の場合は、各連携先の現在の災害モードスレッドを取得する
			if(!nowTrackFlag && !loginDataDto.isUsual()){
				for(LocalgovInfo gov : chainLocalgovList){
					// 連携先自治体の現在のモードが災害モードであれば取得する
					List<TrackData> currentTrackDataList = trackDataService.findByCurrentTrackDatas(gov.id, false);
					for(TrackData currentTrack : currentTrackDataList){
						List<ThreadsendtoData> sendtoTrackThreadDataList = threadsendtoDataService.findByOwnTrackThreadList(loginDataDto.getGroupid(), currentTrack.id);
						for(ThreadsendtoData sends : sendtoTrackThreadDataList){
							sendtoThreadDataList.add(sends);
						}
					}
				}
			}
			// 災害モードの場合は、現在の災害モードとグループ化されている災害用スレッドのみを取得する
			else if (nowTrackFlag){
				// 災害グループ検索の為、自自治体の災害を取得する
				List<TrackData> myTrackDataList = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), false);
				// 災害グループ班のスレッドを収集する
				for(TrackData td : myTrackDataList){
					List<TrackgroupData> trackgroupDatas = null;
					Long preftrackdataid = null;
					// 県
					if(preflocalgovinfoid == loginDataDto.getLocalgovinfoid()){
						preftrackdataid = td.id;
					}else{
						// 市町村
						// 市町村の災害IDから災害グループを取得する場合、自分の災害グループレコードの１件分しか入っていない
						List<TrackgroupData> cityTrackgroupDatas = trackgroupDataService.findByPreflocalgovinfoAndCitytrackdataid(preflocalgovinfoid, td.id);
						if(0<cityTrackgroupDatas.size()) {
							// 災害グループの全市町村を取得するには、県の災害ID を使って災害グループを検索する必要があるので preftrackdataid を取得
							preftrackdataid = cityTrackgroupDatas.get(0).preftrackdataid;
						}
					}

					// 県の災害に紐づく、市町村の trackgroup_data を取得
					trackgroupDatas = trackgroupDataService.findByPreftrackdataid(preftrackdataid);

					// 1件以上存在すれば県の災害スレッドを取得する
					if(trackgroupDatas.size() > 0){
						List<ThreadsendtoData> sendtoTrackThreadDataList = threadsendtoDataService.findByOwnTrackThreadList(loginDataDto.getGroupid(), trackgroupDatas.get(0).preftrackdataid);
						for(ThreadsendtoData sends : sendtoTrackThreadDataList){
							sendtoThreadDataList.add(sends);
						}
						// 市町村の災害スレッド取得
						for(TrackgroupData trackgroupData : trackgroupDatas) {
							sendtoTrackThreadDataList = threadsendtoDataService.findByOwnTrackThreadList(loginDataDto.getGroupid(), trackgroupData.citytrackdataid);
							for(ThreadsendtoData sends : sendtoTrackThreadDataList){
								sendtoThreadDataList.add(sends);
							}
						}
					}
					// 災害グループが存在しないので、該当災害のスレッドを取得
					else{
						List<ThreadsendtoData> sendtoTrackThreadDataList = threadsendtoDataService.findByOwnTrackThreadList(loginDataDto.getGroupid(), td.id);
						for(ThreadsendtoData sends : sendtoTrackThreadDataList){
							sendtoThreadDataList.add(sends);
						}
					}
				}
			}
			//平常時業務の場合
			else {
				/*for(LocalgovInfo gov : chainLocalgovList){
					// 連携先自治体の平常時スレッドを取得する
					List<ThreadsendtoData> sendtoTrackThreadDataList = threadsendtoDataService.findByOwnNormalUnitThreadList(loginDataDto.getUnitid());
					for(ThreadsendtoData sends : sendtoTrackThreadDataList){
						sendtoThreadDataList.add(sends);
					}
				}*/
			}
			for(ThreadsendtoData sends : sendtoThreadDataList){
				if(sends == null) continue;
				if(sends.threadData == null) continue;
				returnThreadDataList.add(sends.threadData);
			}
		}
		return returnThreadDataList;
	}

	/**
	 * 連携する自治体リストを取得する
	 * 連携していない場合は自自治体班を取得する
	 */
	public List<GroupInfo> chainLocalgovGroupList(){
		// 自治体情報を取得
		LocalgovInfo localgovInfo = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
		// 連携する自治体を取得
		long preflocalgovinfoid = localgovInfoService.havePrefLocalgovinfoid(localgovInfo);
		// 自治体連携有の場合は県＋連携市町村の班情報を取得
		List<GroupInfo> groupList = null;
		if(preflocalgovinfoid > 0){
			// 県の班を検索
			groupList = groupInfoService.findByLocalgovInfoIdAndValid(preflocalgovinfoid);
			// 連携自治体 を検索
			cityLocalgovInfos = localgovInfoService.findCityLocalgovInfo(preflocalgovinfoid);
			// 取得した自治体が持つ班(group)を取得
			if(cityLocalgovInfos != null){
				for(LocalgovInfo cityLocalgovinfo : cityLocalgovInfos) {
					cityLocalgovinfo.groupInfos = groupInfoService.findByLocalgovInfoIdAndValid(cityLocalgovinfo.id);
					if(cityLocalgovinfo.groupInfos != null){
						groupList.addAll(cityLocalgovinfo.groupInfos);
					}
				}
			}
		// 連携設定されていない自治体は自自治体班を探す
    	}else{
			// ログイン班と同一の自治体班を探す
    		groupList = groupInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());
		}
		return groupList;
	}

	/**
	 * 連携する自治体リストを取得する
	 * 連携していない場合は自自治体班を取得する
	 */
	public List<UnitInfo> chainLocalgovUnitList(){
		// 自治体情報を取得
		LocalgovInfo localgovInfo = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
		// 連携する自治体を取得
		long preflocalgovinfoid = localgovInfoService.havePrefLocalgovinfoid(localgovInfo);
		// 自治体連携有の場合は県＋連携市町村の班情報を取得
		List<UnitInfo> unitList = null;
		if(preflocalgovinfoid > 0){
			// 県の班を検索
			unitList = unitInfoService.findByLocalgovInfoIdAndValid(preflocalgovinfoid);
			// 連携自治体 を検索
			cityLocalgovInfos = localgovInfoService.findCityLocalgovInfo(preflocalgovinfoid);
			// 取得した自治体が持つ課(unit)を取得
			if(cityLocalgovInfos != null){
				for(LocalgovInfo cityLocalgovinfo : cityLocalgovInfos) {
					cityLocalgovinfo.unitInfos = unitInfoService.findByLocalgovInfoIdAndValid(cityLocalgovinfo.id);
					if(cityLocalgovinfo.unitInfos != null){
						unitList.addAll(cityLocalgovinfo.unitInfos);
					}
				}
			}
		// 連携設定されていない自治体は自自治体班を探す
    	}else{
			// ログイン班と同一の自治体班を探す
    		unitList = unitInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());
		}
		return unitList;
	}

	/**
	 * 班IDに関係するスレッド一覧を取得
	 * @return スレッド情報
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/bbs/nowThreadListJSON2", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String nowThreadListJSON2(){
		JSONObject json = new JSONObject();
		JSONArray json_array = new JSONArray();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// スレッド一覧のIDを格納する配列 (responseテーブルの時間ソートの結果を取る為のSQL都合で、一旦全て取得する)
		List<Long> ownThreadListID = new ArrayList<Long>();

		// 自治体連携有の場合は県＋連携市町村の班情報を取得
		List<GroupInfo> groupList = null;
		List<UnitInfo> unitList = null;
		if (!loginDataDto.isUsual()) groupList = chainLocalgovGroupList();
		else unitList = chainLocalgovUnitList();


		/* パラメータを取得 */
		// 全スレッド取得用flag
		String allThreadflag = request.getParameter("all_thread_flag");

		// 全スレッド表示なら全て取得する
		if(allThreadflag != null && allThreadflag.equals("true")){
			Long[] ownThreadArray = null;
			if (!loginDataDto.isUsual()) ownThreadArray = threadsendtoDataService.findByThreadList(groupList);
			else ownThreadArray = threadsendtoDataService.findByUnitThreadList(unitList);
			ownThreadListID = Arrays.asList(ownThreadArray);
		}else{
			// 自身が関係するスレッド一覧
			List<ThreadData> ownThreadList = ownThreadList();
			for(ThreadData thd : ownThreadList){
				ownThreadListID.add(thd.id);
			}
		}

		try {
			List<ThreadData> threadDataList = new ArrayList<ThreadData>();
			// 最終確認時刻を取得
			for(Long threadid : ownThreadListID){
				// 自身が関係するスレッドデータを取得
				ThreadData threadDataSQL = threadDataService.findById(threadid);
				if(threadDataSQL == null) continue;
				// 該当スレッドの全送信先の最終確認した日時
				// ソートする為一時的に、ThreadDataのregisttimeに入れておく
				threadDataSQL.registtime = threadsendtoDataService.findByLatestUpdateTime(threadid, loginDataDto.isUsual(), null, null);
				threadDataList.add(threadDataSQL);
			}
			// 降順ソート
			Collections.sort(threadDataList, new DESCComparator());

			for(ThreadData th : threadDataList){
				JSONObject json_thread = new JSONObject();
				// 該当スレッドの自身のグループが最終確認した日時
				Timestamp threadOwnLatestTime = threadsendtoDataService.findByLatestUpdateTime(th.id, loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid());
				// 該当スレッドの最終書込み時間
				Timestamp threadLatestResponseTime = threadresponseDataService.findByLatestWriteTime(th.id);
				// フォーマットを変更して登録(最終投稿時間だとユーザが勘違いするので最終確認日時に変更)
				json_thread.put("latestTime", new SimpleDateFormat("yyyy/MM/dd HH:mm").format(th.registtime));
				// 2016/07/21 最終書込み時間も表示するように要望対応
				json_thread.put("latestResponseTime", new SimpleDateFormat("yyyy/MM/dd HH:mm").format(threadLatestResponseTime));
				// 該当スレッドの未読数をカウントしてClassに格納する
				// 全スレッド検索の際、自班が含まれていないスレッドの未読数は0にする
				if(th.registtime == null || !threadsendtoDataService.checkThreadsendtoMe(th.id, loginDataDto.getGroupid())){
					json_thread.put("unRead", 0);
				}else{
					json_thread.put("unRead", threadresponseDataService.getThreadUnReadCount(th.id, threadOwnLatestTime, loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid()));
				}
				// ThreadDataから必要なデータをClassに格納する
				json_thread.put("id", th.id);
				json_thread.put("title", th.title);
				json_thread.put("priority", th.priority);

				// Priorityや終了有無によって、背景色を設定
				//if(th.deleted){
				if(th.closetime != null){
					// 終了したスレッドは黒文字＋グレー背景
					json_thread.put("fontColor", ThreadListDto.COLOR_BLACK);
					json_thread.put("bgColor", ThreadListDto.COLOR_GRAY);
					json_thread.put("threadStyle", "endThread");
				}
				else{
					if(isPriorityOver(th.priority - 1, th.registtime)){
						if(th.priority == 2){
							// オレンジ -> 緊急と一緒
							json_thread.put("fontColor", ThreadListDto.COLOR_WHITE); // COLOR_BLACK -> COLOR_WHITE
							json_thread.put("bgColor", ThreadListDto.COLOR_RED);  // COLOR_ORANGE -> COLOR_RED
							json_thread.put("threadStyle", "priorityThread");  // priorityThread_2 -> priorityThread
							json_thread.put("test", th.registtime);
						}else if(th.priority == 3){
							// 黄色 -> 緊急と一緒
							json_thread.put("fontColor", ThreadListDto.COLOR_WHITE); // COLOR_BLACK -> COLOR_WHITE
							json_thread.put("bgColor", ThreadListDto.COLOR_RED);  // COLOR_YERROW -> COLOR_RED
							json_thread.put("threadStyle", "priorityThread"); // priorityThread_3 -> priorityThread
							json_thread.put("test", th.registtime);
						}else{
							// 白文字＋赤背景
							json_thread.put("fontColor", ThreadListDto.COLOR_WHITE);
							json_thread.put("bgColor", ThreadListDto.COLOR_RED);
							json_thread.put("threadStyle", "priorityThread");
							json_thread.put("test", th.registtime);
						}
					}else{
						if(th.priority == 2){
							// オレンジ
							json_thread.put("fontColor", ThreadListDto.COLOR_BLACK);
							json_thread.put("bgColor", ThreadListDto.COLOR_ORANGE);
							json_thread.put("threadStyle", "priorityThread_2");
							json_thread.put("test", th.registtime);
						}else if(th.priority == 3){
							json_thread.put("fontColor", ThreadListDto.COLOR_BLACK);
							json_thread.put("bgColor", ThreadListDto.COLOR_YERROW);
							json_thread.put("threadStyle", "priorityThread_3");
							json_thread.put("test", th.registtime);
						}else {
							// 黒文字＋白背景
							json_thread.put("fontColor", ThreadListDto.COLOR_BLACK);
							json_thread.put("bgColor", ThreadListDto.COLOR_WHITE);
							json_thread.put("threadStyle", "normalThread");
							json_thread.put("test", th.registtime);
						}
					}
				}
				json_array.put(json_thread);
			}
			json.put("threaddata", json_array);
			json.put("message", lang.__("Sent completed."));

			// 出力
			//PrintWriter out = response.getWriter();
			//out.print( json.toString() );
			return json.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			logger.error(loginDataDto.logInfo(), e);
		//} catch (IOException e) {
		//	e.printStackTrace();
		}
		return null;
	}

	/**
	 * 優先度による、スレッド確認リミット時間を超過しているかチェック
	 * @param priority  優先度
	 * @param threadLatestTime スレッド確認リミット時間
	 * @return true : 超過 or 緊急    false : 未超過 or 設定なし
	 * @throws ParseException
	 */
	public boolean isPriorityOver(int priority, Timestamp threadLatestTime){
		ResourceBundle bundle2 = ResourceBundle.getBundle("SaigaiTask", Locale.getDefault());
		// 優先度高いに設定された際のリミット時間を設定ファイルから読む
		String priorityHighLimit = bundle2.getString("PRIORITY_HIGH");
		String priorityLowLimit = bundle2.getString("PRIORITY_LOW");
		try{
			priorityCheckTime[1] = Integer.parseInt(priorityHighLimit);
			priorityCheckTime[2] = Integer.parseInt(priorityLowLimit);
		}catch(NumberFormatException e){
			// 失敗したらそのまま実行
		}

		if(priority < 0 || priority >= priorityCheckTime.length) return false;
		// 緊急時
		if(priorityCheckTime[priority] == -1){
			return true;
		}
		// 設定無し
		else if(priorityCheckTime[priority] == 0){
			return false;
		}
		// 設定有＋超過している
		else{
			// 現在時刻からPriorityによって設定された時間分を減算した時刻を取得
			Calendar nowDate = Calendar.getInstance();
			nowDate.add(Calendar.HOUR_OF_DAY, priorityCheckTime[priority]*-1);
			// Timestamp型をCalendar型に変換
			Calendar threadLatestTimeCal = Calendar.getInstance();
			threadLatestTimeCal.setTimeInMillis(threadLatestTime.getTime());
			if(threadLatestTimeCal.before(nowDate)){
				return true;
			}else{
				return false;
			}
		}
	}

	/**
	 * 新規スレッドの作成
	 * @return JSON Object
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/bbs/newThreadJson", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String newThreadJson() {
		// 返却用JSON
		JSONObject returnJSON = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		/* パラメータを取得 */
		// スレッドタイトル
		String title = request.getParameter("thread_title_val");
		// スレッド優先度
		String priority = request.getParameter("thread_priority_val");
		// 送信先
		String sendto = request.getParameter("sendto");
		// 送信先を格納するList
		List<String> sendToList = new ArrayList<String>();
		// 新規スレッド作成画面に自身の班を表示するように要望があったので修正 2016/02/10
		/*
		// 送信先に自身の班を追加する
		sendToList.add(String.valueOf(loginDataDto.getGroupid()));
		*/
		// 自身以外の班があれば追加する
		if(sendto != null){
			String[] sendtoArray = request.getParameterValues("sendto");
			for(int i = 0; i < sendtoArray.length; i++){
				sendToList.add(sendtoArray[i]);
			}
		}
		// スレッドメッセージ
		String message = request.getParameter("thread_message_val");

		try {
			/* パラメータチェック */
			// タイトル未設定の時
			if(title == null || title.length() == 0){
				returnJSON.put("message", lang.__("Title is not set."));
				returnJSON.put("threadid", "0");
				//PrintWriter out = response.getWriter();
				//out.print( returnJSON.toString() );
				return returnJSON.toString();
			}
			// 優先度未設定の時
			if(priority == null || priority.equals("0")){
				returnJSON.put("message", lang.__("Priority has not been set."));
				returnJSON.put("threadid", "0");
				//PrintWriter out = response.getWriter();
				//out.print( returnJSON.toString() );
				return returnJSON.toString();
			}
			// メッセージ未設定の時
			if(message == null || message.length() == 0){
				returnJSON.put("message", lang.__("Message has not been set."));
				returnJSON.put("threadid", "0");
				//PrintWriter out = response.getWriter();
				//out.print( returnJSON.toString() );
				return returnJSON.toString();
			}
			// 現在のtrackdataidを取得する
			Long trackdataID = loginDataDto.getTrackdataid() == 0 ? null : loginDataDto.getTrackdataid();
			if(trackdataID != null){
				TrackData trackdata = trackDataService.findById(trackdataID);
				// 終了時間が入っていたら終了している災害なのでnullにする
				if(trackdata.endtime != null) trackdataID = null;
			}

			// スレッドを作成
			ThreadData threadData = new ThreadData();
			if (!loginDataDto.isUsual())
				threadData.groupid = loginDataDto.getGroupid();
			else
				threadData.unitid= loginDataDto.getUnitid();
			threadData.title = title;
			threadData.priority = Integer.parseInt(priority);
			threadData.registtime = new Timestamp(System.currentTimeMillis());
			threadData.closetime = null;
			threadData.deleted = false;
			threadData.trackdataid = trackdataID;
			int cnt = threadDataService.insert(threadData);
			if(cnt!=1) throw new ServiceException(lang.__("Unable to create thread."));

			// 送信先を設定(updatetimeは現在時刻で設定)
			for(int i = 0; i < sendToList.size(); i++){
				ThreadsendtoData threadsendtoData = new ThreadsendtoData();
				threadsendtoData.threaddataid = threadData.id;
				if (!loginDataDto.isUsual())
					threadsendtoData.groupid = Long.parseLong(sendToList.get(i));
				else
					threadsendtoData.unitid = Long.parseLong(sendToList.get(i));
				threadsendtoData.updatetime = new Timestamp(System.currentTimeMillis());
				threadsendtoDataService.insert(threadsendtoData);

				// 緊急で作成されたスレッドはAlarmMessageの通知も出す
				if(threadData.priority == 1 && !loginDataDto.isUsual()){
					addAlarmData(threadData.title, threadsendtoData.groupid, threadData.registtime);
				}
			}

			// 1つ目のメッセージを設定する
			if (!loginDataDto.isUsual()) {
				Long responseDataid = addMessage(message, loginDataDto.getGroupid(), threadData.id);
				logger.info(lang.__("Message registration successfully completed. responseid =") + responseDataid);
			}
			else {
				Long responseDataid = addMessage(message, loginDataDto.getUnitid(), threadData.id);
				logger.info(lang.__("Message registration successfully completed. responseid =") + responseDataid);
			}

			// 正常終了
			returnJSON.put("message", lang.__("Created notice."));
			returnJSON.put("threadid", threadData.id);
			// 出力
			//PrintWriter out = response.getWriter();
			//out.print( returnJSON.toString() );
			return returnJSON.toString();

		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 * 新規コメントの作成
	 * @return JSON Object
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/bbs/sendMessage", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String sendMessage() {

		JSONObject json = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		/* パラメータを取得 */
		// コメント
		String message = request.getParameter("message");
		// スレッドのID
		String threaddataid = request.getParameter("threaddataid");

		try {
			/* パラメータチェック */
			// コメント未設定の時
			if(message == null || message.length() == 0){
				json.put("message", lang.__("Message has not been set."));
				json.put("threadid", "-1");
				PrintWriter out = response.getWriter();
				out.print( json.toString() );
				return null;
			}
			// コメントを作成
			long groupid = loginDataDto.getGroupid();
			if (loginDataDto.isUsual()) groupid = loginDataDto.getUnitid();
			Long responseDataid = addMessage(message, groupid, Long.parseLong(threaddataid));
			if(responseDataid != null){
				json.put("message", lang.__("Sent completed."));
				json.put("threadid", responseDataid);
			}else{
				json.put("message", lang.__("Failed to send."));
				json.put("threadid", 0);
			}
			// 出力
			//PrintWriter out = response.getWriter();
			//out.print( json.toString() );
			return json.toString();

		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		} catch (IOException e) {
			logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 * 選択されたスレッドのレスポンス情報取得
	 * @param updatetime この更新日時以降に投稿されたコメントを取得
	 * @param threaddataid スレッドID
	 * @return JSONArray
	 * @throws JSONException
	 * @throws ParseException
	 */
	public JSONArray haveThreadResponseData(Timestamp updatetime, Long threaddataid) throws JSONException {
		// スレッドの中でupdatetime以降に投稿されたコメントを取得
		List<ThreadresponseData> responseDatas = threadresponseDataService.getUnReadResponse(threaddataid, updatetime);
		// スレッド自身の情報を取得
		ThreadData threaddata = threadDataService.findById(threaddataid);
		// 該当スレッドの確認時間を取得
		ThreadsendtoData threadsendtoData = threadsendtoDataService.findBythreaddataIdGroupId(threaddataid, loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid());
		JSONArray json_array = new JSONArray();
		for(int j = 0; j < responseDatas.size(); j++){
			JSONObject res = new JSONObject();

			if(responseDatas.get(j).fileflag){
				//res.put("message", SpringContext.getRequest().getContextPath()+("/bbs/upload") + responseDatas.get(j).message);
				res.put("message", SpringContext.getRequest().getContextPath()+"/upload" + responseDatas.get(j).message);
				//res.put("message", "/bbs/upload" + responseDatas.get(j).message);
			}else{
				res.put("message", responseDatas.get(j).message);
			}

			// ID
			res.put("id", responseDatas.get(j).id);
			res.put("registtime", responseDatas.get(j).registtime);
			// 県もしくは市の名前を取得
			long localgovinfoid = 0;
			if (!loginDataDto.isUsual()) {
				GroupInfo groupinfo = groupInfoService.findById(responseDatas.get(j).groupid);
				localgovinfoid = groupinfo.localgovinfoid;
			}
			else {
				UnitInfo unitinfo = unitInfoService.findById(responseDatas.get(j).unitid);
				localgovinfoid = unitinfo.localgovinfoid;
			}
			LocalgovInfo localgovinfo = localgovInfoService.findById(localgovinfoid);
			String pref_city = localgovinfo.city == null || localgovinfo.city.equals("") ? localgovinfo.pref : localgovinfo.city;
			// bbs/index.jsp の送信先タブの表記に合わせる
			if(localgovinfo.localgovtypeid==1) {
				pref_city = localgovinfo.pref;
			}
			else if(localgovinfo.localgovtypeid==2) {
				pref_city = localgovinfo.city;
			}
			else if(localgovinfo.localgovtypeid==3) {
				pref_city = localgovinfo.section;
			}
			// 班の名前を取得
			if (!loginDataDto.isUsual()) {
				res.put("groupname", pref_city + groupInfoService.getNameByID(responseDatas.get(j).groupid));
				// 投稿コメントが作成した班のものか否か
				res.put("ownThread", threaddata.groupid.equals(responseDatas.get(j).groupid) ? true : false);
				// ファイル送信メッセージか否か
				res.put("fileflag", responseDatas.get(j).fileflag ? true : false);
				// 自班のコメントかどうか
				res.put("ownMessage", responseDatas.get(j).groupid.equals(loginDataDto.getGroupid()) ? true : false);
			}
			else {
				res.put("groupname", pref_city + unitInfoService.getNameByID(responseDatas.get(j).unitid));
				// 投稿コメントが作成した班のものか否か
				res.put("ownThread", threaddata.unitid.equals(responseDatas.get(j).unitid) ? true : false);
				// ファイル送信メッセージか否か
				res.put("fileflag", responseDatas.get(j).fileflag ? true : false);
				// 自班のコメントかどうか
				res.put("ownMessage", responseDatas.get(j).unitid.equals(loginDataDto.getUnitid()) ? true : false);
			}
			// 未読コメントかどうか
			if(threadsendtoData == null || threadsendtoData.updatetime == null){
				res.put("unRead", false);
			}else{
				res.put("unRead", responseDatas.get(j).registtime.after(threadsendtoData.updatetime) ? true : false);
			}
			// 送信時間
			res.put("registtime", new SimpleDateFormat("MM/dd HH:mm").format(responseDatas.get(j).registtime));
			json_array.put(res);
		}
		return json_array;
	}

	/**
	 * 選択されたスレッドの情報取得
	 * @return null(フォワードしない)
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/bbs/haveThreadDatajson", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String haveThreadDatajson() {

		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		/* パラメータを取得 */
		// スレッドID
		String threadID = request.getParameter("threadid");
		if(threadID == null) return null;
		if(threadID.equals("0") || threadID.equals("")) return null;

		try {
			JSONObject json = new JSONObject();
			// スレッドの取得
			ThreadData threadData = threadDataService.findById(Long.parseLong(threadID));
			json.put("id", threadData.id);
			// スレッド情報から作成グループ名称(県名＋市町村名も取得)を取得する
			if (!loginDataDto.isUsual())
				json.put("registGroupName", haveGroupName_pref_city(threadData.groupid));
			else
				json.put("registGroupName", haveUnitName_pref_city(threadData.unitid));
			// 作成日時の取得
			json.put("registTime", new SimpleDateFormat("yyyy/MM/dd HH:mm").format(threadData.registtime));
			// 閉鎖有無
			json.put("closed", threadData.closetime != null ? true : false);
			// 自身が作成したスレッドか否か
			if (!loginDataDto.isUsual())
				json.put("ownCreateThread", threadData.groupid.equals(loginDataDto.getGroupid()) ? true : false);
			else
				json.put("ownCreateThread", threadData.unitid.equals(loginDataDto.getUnitid()) ? true : false);
			json.put("sendtoMe", threadsendtoDataService.checkThreadsendtoMe(Long.parseLong(threadID), loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid()) ? true : false);
			// 優先度
			json.put("priority", threadData.priority);
			// タイトル
			json.put("title", threadData.title);
			// 管理者権限
			json.put("admin", loginDataDto.isAdmin());
			// 該当スレッドの全送信先の最終投稿時間
			Timestamp latestTime = threadresponseDataService.getLatestResponse(Long.parseLong(threadID));
			if(latestTime != null)
				json.put("latestTime", latestTime.toString());
			else
				json.put("latestTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(threadData.registtime));
			// 送信先リストを取得して、送信先グループ名を取得する
			List<ThreadsendtoData> threadsendtoDataList = threadsendtoDataService.findBythreaddataId(threadData.id);
			StringBuilder sendtoMember = new StringBuilder();
			StringBuilder sendtoMemberID = new StringBuilder();
			for(int i = 0; i < threadsendtoDataList.size(); i++){
				// 自身の班も表示するように要望があったので修正 2016/02/10
				/*
				// 作成グループと同一なら何もしない
				if(threadsendtoDataList.get(i).groupid.equals(threadData.groupid)) continue;
				*/
				// 区切り文字
				if(sendtoMember.length() != 0) sendtoMember.append(",");
				if(sendtoMemberID.length() != 0) sendtoMemberID.append(",");
				if (!loginDataDto.isUsual()) {
					sendtoMember.append(haveGroupName_pref_city(threadsendtoDataList.get(i).groupid));
					// 送信先グループのIDを格納
					sendtoMemberID.append(threadsendtoDataList.get(i).groupid);
				}
				else {
					sendtoMember.append(haveUnitName_pref_city(threadsendtoDataList.get(i).unitid));
					// 送信先グループのIDを格納
					sendtoMemberID.append(threadsendtoDataList.get(i).unitid);
				}
			}
			// 送信先グループ名称
			json.put("sendtoMember", new String(sendtoMember));
			// 送信先グループID
			json.put("sendtoMemberID", new String(sendtoMemberID));
			// レスポンスの内容
			//json.put("responseData", getThreadResponseData(latestTime, threadData.id));
			json.put("responseData", haveThreadResponseData(null, threadData.id));
			// 正常終了
			json.put("message", lang.__("Get the thread info."));
			// 出力
			//PrintWriter out = response.getWriter();
			//out.print( json.toString() );
			return json.toString();

		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 * Groupidから県名＋市町村名＋班名称を取得する。
	 * @param groupid 班ID
	 * @return String : 県名＋市町村名＋班名称
	 */
	public String haveGroupName_pref_city(Long groupid){
		// スレッド情報から作成グループを取得する
		GroupInfo groupInfo = groupInfoService.findById(groupid);
		// 作成グループ名以外にもlocalgovinfoから県名、市町村名を取得する
		LocalgovInfo localgovInfo = localgovInfoService.findById(groupInfo.localgovinfoid);
		if(localgovInfo != null) {
			// bbs/index.jsp の送信先タブの表記に合わせる
			String pref_city = null;
			if(localgovInfo.localgovtypeid==1) {
				pref_city = localgovInfo.pref;
			}
			else if(localgovInfo.localgovtypeid==2) {
				pref_city = localgovInfo.city;
			}
			else if(localgovInfo.localgovtypeid==3) {
				pref_city = localgovInfo.section;
			}
			return pref_city + groupInfo.name;
		}
		else
			return groupInfo.name;
	}

	/**
	 * Unitidから県名＋市町村名＋課名称を取得する。
	 * @param unitid 班ID
	 * @return String : 県名＋市町村名＋課名称
	 */
	public String haveUnitName_pref_city(Long unitid){
		// スレッド情報から作成グループを取得する
		UnitInfo unitInfo = unitInfoService.findById(unitid);
		// 作成グループ名以外にもlocalgovinfoから県名、市町村名を取得する
		LocalgovInfo localgovInfo = localgovInfoService.findById(unitInfo.localgovinfoid);
		if(localgovInfo != null) {
			// bbs/index.jsp の送信先タブの表記に合わせる
			String pref_city = null;
			if(localgovInfo.localgovtypeid==1) {
				pref_city = localgovInfo.pref;
			}
			else if(localgovInfo.localgovtypeid==2) {
				pref_city = localgovInfo.city;
			}
			else if(localgovInfo.localgovtypeid==3) {
				pref_city = localgovInfo.section;
			}
			return pref_city + unitInfo.name;
		}
		else
			return unitInfo.name;
	}

	/**
	 * 選択されたスレッドのレスポンスを更新する
	 * @return JSON Object
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/bbs/nowSendResponseAjax", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String nowSendResponseAjax() {

		// 返却用JSON
		JSONObject json = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		/* パラメータを取得 */
		// スレッドID
		String threadID = request.getParameter("threaddataid");
		String updatetime = request.getParameter("latesttime");
		if(threadID == null || updatetime == null) return null;
		if(threadID.equals("0")) return null;

		try {
			// updatetimeをTimestamp型に変換
			Timestamp beforeUpdateTime = Timestamp.valueOf(updatetime);
			json.put("responseData", haveThreadResponseData(beforeUpdateTime, Long.parseLong(threadID)));
			// 現在の時刻を送信して、index.jsp側で持つupdatetimeを更新
			json.put("updateTime", new Timestamp(System.currentTimeMillis()).toString());
			// 正常終了
			json.put("message", lang.__("Get the thread info."));
			json.put("before", beforeUpdateTime.toString());
			// 出力
			//PrintWriter out = response.getWriter();
			//out.print( json.toString() );
			return json.toString();
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 * 選択されたスレッドの情報を更新する
	 * @return JSON Object
	 * @throws ParseException
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/bbs/editThreadData", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String editThreadData() {

		// 返却用JSON
		JSONObject json = new JSONObject();
		// 出力の準備
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		/* パラメータを取得 */
		// スレッドID
		String threadid = request.getParameter("threaddataid");
		if(threadid == null) return null;
		String checkRead = request.getParameter("checkRead");
		String priority = request.getParameter("priorityEdit");
		String sendMember = request.getParameter("sendMemberEdit");
		String closed = request.getParameter("closed");
		String deleted = request.getParameter("deleted");
		// 共にNull,Nullでないパターンは存在しないはずなので返却
		if(checkRead == null && priority == null && sendMember == null && closed == null && deleted == null) return null;
		if(checkRead != null && priority != null && sendMember != null && closed != null && deleted != null) return null;
		if(checkRead != null && checkRead.equals("true")){
			// 自班のスレッドの更新時間を更新
			ThreadsendtoData threadsendtoData = threadsendtoDataService.findBythreaddataIdGroupId(Long.parseLong(threadid), loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid());
			threadsendtoData.updatetime = new Timestamp(System.currentTimeMillis());
			threadsendtoDataService.update(threadsendtoData);
		}else if(sendMember != null){
			String[] sendToList = request.getParameterValues("sendMemberEdit");
			// 念の為、自身が作成したスレッドかどうかチェック
			if(threadDataService.checkOwnThread(Long.parseLong(threadid), loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid())){
				// 新規で追加する場合は、スレッド作成時間でupdatetimeを設定する
				ThreadData td = threadDataService.findById(Long.parseLong(threadid));
				// 追加された送信先を登録
				for(int i = 0; i < sendToList.length; i++){
					ThreadsendtoData threadsendtoData = new ThreadsendtoData();
					threadsendtoData.threaddataid = Long.parseLong(threadid);
					if (!loginDataDto.isUsual())
						threadsendtoData.groupid = Long.parseLong(sendToList[i]);
					else
						threadsendtoData.unitid = Long.parseLong(sendToList[i]);
					threadsendtoData.updatetime = td.registtime;
					threadsendtoDataService.insert(threadsendtoData);
				}
			}
		}else if(priority != null){
			// スレッドの取得
			// 念の為、自身が作成したスレッドかどうかチェック
			if(threadDataService.checkOwnThread(Long.parseLong(threadid), loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid())){
				ThreadData threadData = threadDataService.findById(Long.parseLong(threadid));
				threadData.priority = Integer.parseInt(priority);
				threadDataService.update(threadData);

				// 緊急で作成されたスレッドはAlarmMessageの通知も出す
				if(threadData.priority == 1){
					// 送信先リストの取得
					List<ThreadsendtoData> threadsendtoDataList = threadsendtoDataService.findBythreaddataId(threadData.id);

					if(threadsendtoDataList != null && !loginDataDto.isUsual()){
						for(ThreadsendtoData sendData : threadsendtoDataList){
							addAlarmData(threadData.title, sendData.groupid, threadData.registtime);
						}
					}
				}
			}
		}else if(closed != null && closed.equals("true")){
			// スレッドの取得
			ThreadData threadData = threadDataService.findById(Long.parseLong(threadid));
			threadData.closetime = new Timestamp(System.currentTimeMillis());
			//threadData.deleted = true;
			// 念の為、自身が作成したスレッドかどうかチェック
			if((!loginDataDto.isUsual() && threadData.groupid.equals(loginDataDto.getGroupid())) ||
					(loginDataDto.isUsual() && threadData.unitid.equals(loginDataDto.getUnitid()))){
				threadDataService.update(threadData);
				// スレッドの更新時間も更新
				ThreadsendtoData threadsendtoData = threadsendtoDataService.findBythreaddataIdGroupId(Long.parseLong(threadid), loginDataDto.isUsual(), loginDataDto.getGroupid(), loginDataDto.getUnitid());
				threadsendtoData.updatetime = new Timestamp(System.currentTimeMillis());
				threadsendtoDataService.update(threadsendtoData);
			}
		}else if(deleted != null && deleted.equals("true")){
			// 自身が管理者権限を持っているか確認
			if(loginDataDto.isAdmin()){
				// スレッドの論理削除 (closetimeもつける)
				ThreadData threadData = threadDataService.findById(Long.parseLong(threadid));
				threadData.closetime = new Timestamp(System.currentTimeMillis());
				threadData.deleted = true;
				threadDataService.update(threadData);
			}else{
				// 管理者権限がない
				logger.warn("Don't have Administrator Permission");
			}
		}
		// 正常終了
		try {
			json.put("message", lang.__("Get the thread info."));
			// 出力
			//PrintWriter out = response.getWriter();
			//out.print( json.toString() );
			return json.toString();
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		//} catch (IOException e) {
		//	logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 *
	 * @param title アラームタイトル
	 * @param sendgroupid アラーム先の班ID
	 * @param registTime スレッド作成日時
	 */
	public void addAlarmData(String title, Long sendgroupid, Timestamp registTime){
		// 班情報から自治体IDを取得
		GroupInfo groupinfo = groupInfoService.findByNotDeletedId(sendgroupid);
		// 削除されているグループであれば何もしない
		if(groupinfo == null) return;

		// 訓練モードフラグ
		boolean isTraining = false;
		if(loginDataDto!=null && loginDataDto.getTrackdataid()!=0L) {
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			isTraining = trackData!=null&&trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
		}

		// 複数同時災害対応
		List<Long> trackids = new ArrayList<Long>();
		List<TrackData> tracks = null;
		if(isTraining){
			tracks = trackDataService.findByCurrentTrackDatas(groupinfo.localgovinfoid, true);
		}else{
			tracks = trackDataService.findByCurrentTrackDatas(groupinfo.localgovinfoid, false);
		}
		// 平時は0でセット（-1は自治体全体で使うもの限定）
		if(tracks.size()==0) trackids.add(0L);
		else {
			for(TrackData track : tracks) {
				if (track != null)
					trackids.add(track.id);
			}
		}
		// 作成日時をアラームに載せるので、整形する
		// Nullは仕様上有り得ないけど、念の為確認
		String registTimeStr = "";
		if(registTime != null){
			registTimeStr = registTime.toString();
			// 秒以下を外す
			String[] timeStr2 = registTimeStr.split(":");
			// 配列の２番目までを取得する
			if(timeStr2.length == 3){
				registTimeStr = timeStr2[0] + ":" + timeStr2[1];
				// ハイフンをスラッシュに変更する
				registTimeStr = registTimeStr.replaceAll("-", "/");
			}
		}

		for(Long trackid : trackids) {
			AlarmmessageData alarm = new AlarmmessageData();
			alarm.localgovinfoid = groupinfo.localgovinfoid;
			alarm.duration = 0;
			alarm.message = lang.__("[Emergency notice] Check {0} urgently.", title) + " " + lang.__("Confirm urgently, created time:{0}", registTimeStr);
			alarm.messagetype = "error";
			alarm.showmessage = true;
			alarm.groupid = sendgroupid;
			alarm.registtime = new Timestamp(System.currentTimeMillis());
			alarm.trackdataid = trackid;
			alarm.deleted = false;

			alarmmessageDataService.insert(alarm);
		}
	}

	/**
	 * レスポンス登録用関数
	 * @param message : 登録するメッセージ
	 * @param groupid : 書き込んだ班id もしくは 課ID
	 * @param threaddataid スレッドID
	 * @return レスポンスid
	 */
	public Long addMessage(String message, Long groupid, Long threaddataid){
		ThreadresponseData responseData = new ThreadresponseData();
		responseData.threaddataid = threaddataid;
		if (!loginDataDto.isUsual())
			responseData.groupid = groupid;
		else
			responseData.unitid = groupid;
		responseData.message = message;
		responseData.fileflag = false;
		responseData.registtime = new Timestamp(System.currentTimeMillis());
		int cnt = threadresponseDataService.insert(responseData);
		if(cnt > 0){
			// 更新時間を更新
			ThreadsendtoData threadsendtoData = threadsendtoDataService.findBythreaddataIdGroupId(responseData.threaddataid, loginDataDto.isUsual(), responseData.groupid, responseData.unitid);
			threadsendtoData.updatetime = new Timestamp(System.currentTimeMillis() + 1000);
			threadsendtoDataService.update(threadsendtoData);
			return responseData.id;
		}else{
			return null;
		}
	}

	class DESCComparator implements Comparator<ThreadData>{
		public int compare(ThreadData arg1, ThreadData arg2){
			if(arg1 == null && arg2 == null){ return 0;}
			else if(arg1 == null){ return -1; }
			else if(arg2 == null){ return 1; }
			//return ((Comparable)arg1).compareTo((Comparable)arg2) * -1;
			return arg1.registtime.before(arg2.registtime) ? 1 : -1;
		}
	}

	/**
	 * Trackdataのソートを実行する
	 */
	class SortTrackDataComparator implements Comparator<TrackData>{
		// 昇順
		public static final int ASC = 1;
		// 降順
		public static final int DESC = -1;
		// ソート対象 = starttime
		public static final int STARTTIME = 1;
		// ソート対象 = endtime
		public static final int ENDTIME = 2;
		// ソートパターン
		private int sort = ASC;
		// ソート対象
		private int target = 1;

		public SortTrackDataComparator(int pat, int target){
			this.sort = pat;
			this.target = target;
		}

		public int compare(TrackData arg1, TrackData arg2){
			if(arg1 == null && arg2 == null){ return 0;}
			else if(arg1 == null){ return 1 * this.sort; }
			else if(arg2 == null){ return -1 * this.sort; }
			if(STARTTIME == this.target) return arg1.starttime.before(arg2.starttime) ? -1 * this.sort : 1 * this.sort;
			else if(arg1.endtime == null && arg2.endtime != null) return 1 * this.sort;
			else if(arg1.endtime != null && arg2.endtime == null) return -1 * this.sort;
			else if(arg1.endtime == null && arg2.endtime == null) return -1 * this.sort;
			else return arg1.endtime.before(arg2.endtime) ? -1 * this.sort : 1 * this.sort;
		}
	}

    /**
     * 現在のログイン班を除外する
     */
    public void cutLoginGroup(){
    	// 自身が県なら、県の現在ログインしている班を除く
    	if(loginDataDto.getLocalgovInfo().localgovtypeid.equals(Localgovtype.PREF)){
    		for(int i = 0; i < groupInfoItems.size(); i++){
    			if(groupInfoItems.get(i).id == loginDataDto.getGroupid()){
    				groupInfoItems.remove(i);
    			}
    		}
    	}else{
    		boolean loopFlag = false;
    		for(LocalgovInfo localgov : cityLocalgovInfos){
    			if(loopFlag) break;
    			for(int i = 0; i < localgov.groupInfos.size(); i++){
    				if(localgov.groupInfos.get(i).id == loginDataDto.getGroupid()){
    					localgov.groupInfos.remove(i);
    					loopFlag = true;
    					break;
    				}
    			}
    		}
    	}
    }

}
