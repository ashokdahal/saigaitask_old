/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.UserTransaction;
import javax.validation.Valid;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TrackmapInfo;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.TrackForm;
import jp.ecom_plat.saigaitask.security.TokenProcessor;
import jp.ecom_plat.saigaitask.service.ClearinghouseService;
import jp.ecom_plat.saigaitask.service.TabledataResetService;
import jp.ecom_plat.saigaitask.service.TrackService;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.DisasterMasterService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackgroupDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.publiccommons.PublicCommonsService;
import jp.ecom_plat.saigaitask.util.HttpUtil;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * 災害を起動/完了するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class TrackAction extends AbstractAction {
	protected TrackForm trackForm;

	@Resource
	protected TrackDataService trackDataService;

	@Resource
	protected TrackgroupDataService trackgroupDataService;

	@Resource
	protected TrackmapInfoService trackmapInfoService;

	@Resource
	protected ClearinghouseService clearinghouseService;

	@Resource
	protected PublicCommonsService publicCommonsService;

	@Resource
	protected TrackService trackService;

	/** 災害情報リスト */
	public List<DisasterMaster> disasterItems;
	/** 災害情報マスターサービス */
	@Resource
	protected DisasterMasterService disasterMasterService;

	/** アラームメッセージデータサービス */
	@Resource
	protected AlarmmessageDataService alarmmessageDataService;

	/** テーブルリセットサービス */
	@Resource
	protected TabledataResetService tabledataResetService;

	@Resource
	protected MapmasterInfoService mapmasterInfoService;

	/** 対応中の記録リスト */
	public List<TrackData> currentTrackItems;
	/** 連携自治体情報 */
	public List<LocalgovInfo> cityLocalgovInfos;
	/** 災害地図情報リスト */
	public List<TrackmapInfo> trackmapInfos;
	/** ログイン班 */
	public GroupInfo groupInfo;
	/** リセット対象テーブルリスト */
	public List<TracktableInfo> resetTablemasterInfos;
	/** リセット対象カラムリスト(JSON) */
	public String resetAttrJSON;
	/** 体制リセット済ならtrue */
	public String isStationOFF = "false";

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("disasterItems", disasterItems);
		model.put("currentTrackItems", currentTrackItems);
		model.put("cityLocalgovInfos", cityLocalgovInfos);
		model.put("trackmapInfos", trackmapInfos);
		model.put("groupInfo", groupInfo);
		model.put("resetTablemasterInfos", resetTablemasterInfos);
		model.put("resetAttrJSON", resetAttrJSON);
		model.put("isStationOFF", isStationOFF);
		model.put("trackForm", trackForm);
	}

	@Resource
	protected UserTransaction userTransaction;

	/** 公共情報コモンズ関連メッセージ */
	public ArrayList<String> pcommonsMessages = new ArrayList<String>();

	/**
	 * ページを表示する.
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/track", "/track/index"})
	public String index(Map<String,Object>model,
			@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult) {
		this.trackForm = trackForm;

		// 災害情報リストを取得
		disasterItems = disasterMasterService.findGtZeroOrderBy();

		TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
		if (trackData != null)
			Beans.copy(trackData, trackForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
		else {
			// ログイン中の災害種別を初期値に選んでおく
			//trackForm.disasterid = String.valueOf(loginDataDto.getDisasterid());
		}
		currentTrackItems = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid());
		// 災害が0件の場合、訓練モードも調査する
		if(currentTrackItems.size() == 0)
			currentTrackItems = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), true);
		trackmapInfos = trackmapInfoService.findByLocalgovInfoIdAndTrackDataNotDelete(loginDataDto.getLocalgovinfoid());
		Collections.sort(trackmapInfos, TrackmapInfo.orderbyTrackDataStarttimeDesc);
		groupInfo = loginDataDto.getGroupInfo();

		// 県の場合は、連携する自治体を検索する
		LocalgovInfo localgovInfo = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
		long preflocalgovinfoid = localgovInfoService.havePrefLocalgovinfoid(localgovInfo);
		if(0<preflocalgovinfoid
				// ログイン中の自治体が連携県自治体ならば
				&& loginDataDto.getLocalgovinfoid()==preflocalgovinfoid) {
			
			long preftrackdataid = loginDataDto.getTrackdataid();
			// 連携自治体 を記録データ付きで検索
			cityLocalgovInfos = localgovInfoService.findCityLocalgovInfo(preflocalgovinfoid);
			for(LocalgovInfo cityLocalgovInfo : cityLocalgovInfos) {
				cityLocalgovInfo.trackDatas = trackDataService.findCityTrackDatas(cityLocalgovInfo, preftrackdataid);
			}
			// 過去の災害に、市町村の災害を設定
			for(TrackmapInfo trackmapInfo : trackmapInfos) {
				for(TrackData oldTrackData : trackmapInfo.trackDatas) {
					oldTrackData.cityTrackgroupDatas = trackgroupDataService.findByPreftrackdataid(oldTrackData.id);
				}
			}
		}

		// 避難勧告・避難指示の全解除対象チェック
		if (publicCommonsService.checkAllCloseEvacuationOrder(loginDataDto.getTrackdataid())) {
			pcommonsMessages.add(lang.__("Evacuation advisory/order not released exists.<br />In case of disaster request completed, send release message to L-Alert."));
		}

		// 避難所の全閉鎖対象チェック
		if (publicCommonsService.checkAllCloseShelter(loginDataDto.getTrackdataid())) {
			pcommonsMessages.add(lang.__("Shelter not closed exists.<br />In case of disaster request completed, send close message to L-Alert."));
		}

		// 災害対策本部設置状況の全解散対象チェック
		if (publicCommonsService.checkAllCloseAntidisaster(loginDataDto.getTrackdataid())) {
			pcommonsMessages.add(lang.__("System not released exists.<br />In case of disaster request completed, send release message to L-Alert."));
		}

		// 地図コピーフラグ
		MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		// 訓練モードまたは地図マスタ情報のコピーフラグが有効な場合に地図コピーする
		trackForm.copyMap = String.valueOf(mapmasterInfo.copy);

		setupModel(model);
		return "/track/index";
	}


	/**
	 * 記録の追加。災害地図の作成
	 * @return 次ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/track", "/track/index"}, params="insert", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@Transactional(propagation=Propagation.NEVER)
	public String insert(Map<String,Object>model,
			@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult) {
		TrackData trackData = trackService.insertDisaster(loginDataDto.getLocalgovinfoid(), trackForm, loginDataDto.getGroupInfo());
		// 災害モード
		trackDataService.loginTrackData(trackData);

		return "/track/success";
	}

	/**
	 * 都道府県に関連付けられた市町村の、まだ災害グループに割り当てられていない記録データをJSONでレスポンスします.
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/track/selectCitytrackDialogContent")
	public ResponseEntity<String> selectCitytrackDialogContent(Map<String,Object>model,
			@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult) {
		this.trackForm = trackForm;
		try{
			long citylocalgovinfoid = Long.parseLong(trackForm.localgovinfoid);
			long preflocalgovinfoid = loginDataDto.getLocalgovinfoid();
			List<TrackData> trackDatas = new ArrayList<TrackData>();
			if(trackForm.citytrackdataids!=null) {
				for(String idStr : trackForm.citytrackdataids) {
					Long trackdataid = Long.parseLong(idStr);
					TrackData trackData = trackDataService.findById(trackdataid);
					if(trackData!=null) trackDatas.add(trackData);
				}
			}
			// 未割り当ての記録データを取得
			List<TrackData> selectCityTrackItems = null;
			TrackData loginTrackData = trackDataService.findById(loginDataDto.getTrackdataid());
			// 現在の災害or訓練モードで割り当て対象を切り替える
			// 平常時でログイン中の場合はloginTrackDataがnullになるので、その場合は災害モード扱い
			if(loginTrackData==null || loginTrackData.trainingplandataid == null){
				// 災害のみで表示する
				selectCityTrackItems = trackDataService.findNotAssociateCityTrackDatas(preflocalgovinfoid, citylocalgovinfoid, false);
			}else{
				// 訓練のみで表示する
				selectCityTrackItems = trackDataService.findNotAssociateCityTrackDatas(preflocalgovinfoid, citylocalgovinfoid, true);
			}
			trackDatas.addAll(selectCityTrackItems);
			JSONArray array = new JSONArray();
			for(final TrackData trackData : trackDatas) {
				JSONObject json = new JSONObject();
				json.put("id", trackData.id);
				//json.put("disasterid", trackData.disasterid);
				/*json.put("disasterMaster", new JSONObject() {{
					put("name", trackData.disasterMaster.name);
				}});*/
				json.put("starttime", trackData.starttime!=null ? new SimpleDateFormat(lang.__("MMM.d,yyyy 'at' HH:MM<!--3-->"), lang.getLocale()).format(trackData.starttime) : null);
				json.put("endtime", trackData.endtime!=null ? new SimpleDateFormat(lang.__("MMM.d,yyyy 'at' HH:MM<!--3-->"), lang.getLocale()).format(trackData.endtime) : null);
				json.put("localgovinfoid", trackData.localgovinfoid);
				json.put("name", trackData.name);
				json.put("note", trackData.note);
				array.put(json);
			}

			// 出力の準備
			/*response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.print( array.toString() );*/

			final HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
			return new ResponseEntity<String>(array.toString(), httpHeaders, HttpStatus.OK);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 記録情報の更新
	 * @return 次ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/track", "/track/index"}, params="update")
	public String update(Map<String,Object>model,
			@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult) {
		this.trackForm = trackForm;
		if(validateUpdate(this.trackForm).equals("")){
			setupModel(model);
			return null;
		}
		setupModel(model);
		return trackService.updateDisaster(loginDataDto.getLocalgovinfoid(), loginDataDto.getGroupid(), trackForm);
	}

	/**
	 * 記録情報の完了
	 * @return 次ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/track/complete")
	public String complete(Map<String,Object>model,
		@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult) {
		this.trackForm = trackForm;
		if(validateComplete(trackForm).equals("")){
			setupModel(model);
			return null;
		}
		setupModel(model);
		return trackService.completeDisaster(loginDataDto.getLocalgovinfoid(), trackForm);
	}

	/**
	 * 記録情報の選択
	 * @return 次ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/track", "/track/index"}, params="select")
	public String select(Map<String,Object>model,
			@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult) {
		this.trackForm = trackForm;
		if(validateSelect(trackForm).equals("")){
			setupModel(model);
			return null;
		}
		long tid = Long.parseLong(trackForm.oldtrackid);
		// ログイン中の記録データを切り替え
		trackDataService.loginTrackData(tid);
		return "forward:success";
	}

	/**
	 * 記録情報の削除
	 * @return 次ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/track", "/track/index"}, params="delete")
	public String delete(Map<String,Object>model,
			@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult) {
		this.trackForm = trackForm;
		if(validateDelete(this.trackForm).equals("")){
			setupModel(model);
			return null;
		}
		long tid = Long.parseLong(trackForm.oldtrackid);

		setupModel(model);
		return trackService.deleteDisaster(tid);
	}

	/**
	 * 成功ページ
	 * @return 次ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/track/success")
	public String success(Map<String,Object>model,
			@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult) {
		this.trackForm = trackForm;
		return "/track/success";
	}

	/**
	 * 登録のバリデータ
	 * @return 検証エラーメッセージ
	 */
	public ActionMessages validateInsert(TrackForm trackForm)
	{
		ActionMessages errors = new ActionMessages();

		// CSRF対策
		/*if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}*/

		if (StringUtil.isEmpty(trackForm.name)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please enter disaster name."), false));
		}
		//if (StringUtil.isEmpty(trackForm.disasterid)) {
		//	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please select a disaster type."), false));
		//}
		/* 複数同時災害の対応
		TrackData track = trackDataService.findByCurrentTrackData(loginDataDto.getLocalgovinfoid(), loginDataDto.getDisasterid());
        if (track != null) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("すでに災害は登録されています。", false));
        }
		 */

		return errors;
	}

	/**
	 * 登録のバリデータ
	 * @return 検証エラーメッセージ
	 */
	public ActionMessages validateUpdate(TrackForm trackForm)
	{
		ActionMessages errors = new ActionMessages();

		// CSRF対策
		/*if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}*/

		if (StringUtil.isEmpty(trackForm.name)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please enter disaster name."), false));
		}

		return errors;
	}

	/**
	 * 削除のバリデータ
	 * @return 検証エラーメッセージ
	 */
	public ActionMessages validateDelete(TrackForm trackForm)
	{
		ActionMessages errors = new ActionMessages();

		// CSRF対策
		/*if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}*/

		HttpUtil.throwBadRequestExceptionIfIsNull(trackForm.oldtrackid);
		long tid = Long.parseLong(trackForm.oldtrackid);
		TrackData track = trackDataService.findById(tid);
		if (track != null && track.endtime == null) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Disaster in the correspondence can not be deleted."), false));
		}
		if (tid == 0) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please select the disaster that you want to delete."), false));
		}

		return errors;
	}

	/**
	 * 完了のバリデータ
	 * @return 検証エラーメッセージ
	 */
	public ActionMessages validateComplete(TrackForm trackForm)
	{
		ActionMessages errors = new ActionMessages();

		// CSRF対策
		/*if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}*/

		if (StringUtil.isEmpty(trackForm.name)) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please enter disaster name."), false));
		}
		long tid = Long.parseLong(trackForm.id);
		TrackData track = trackDataService.findById(tid);
		if (track != null && track.endtime != null) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("It has been completed already."), false));
		}

		return errors;
	}

	/**
	 * 選択のバリデータ
	 * @return 検証エラーメッセージ
	 */
	public ActionMessages validateSelect(TrackForm trackForm)
	{
		ActionMessages errors = new ActionMessages();

		// CSRF対策
		/*if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}*/

		HttpUtil.throwBadRequestExceptionIfIsNull(trackForm.oldtrackid);
		long tid = Long.parseLong(trackForm.oldtrackid);
		if (tid == 0) {
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Please select disaster"), false));
		}

		return errors;
	}

	/**
	 * データリセット対象のレイヤをチェック
	 * @return : null
	 */
    @org.springframework.web.bind.annotation.RequestMapping(value="/track/outputResetLayers", produces="application/json", method=org.springframework.web.bind.annotation.RequestMethod.POST)
    @ResponseBody
	public String outputResetLayers(Map<String,Object>model,
			@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult){
		this.trackForm = trackForm;

		// 二度押し防止トークン
		TokenProcessor.getInstance().saveToken(SpringContext.getRequest());

		JSONObject json = new JSONObject();
		// 出力の準備
		/*response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");*/

		try{
			// リセット対象属性の一覧をJSのObjectに渡す為に、JSONを生成する
			if(loginDataDto.getTrackmapinfoid() == 0){
				TrackmapInfo trackmapInfo = trackmapInfoService.findByTrackDataId(loginDataDto.getTrackdataid());
				if(trackmapInfo != null) loginDataDto.setTrackmapinfoid(trackmapInfo.id);
			}
			resetTablemasterInfos = tabledataResetService.resetTableList(loginDataDto.getTrackmapinfoid());
			JSONArray arr = new JSONArray();

			for(TracktableInfo info : resetTablemasterInfos){
				JSONObject obj = new JSONObject();
				obj.put("layerid", info.layerid);
				obj.put("layername", info.tablemasterInfo.name);

				// リセット対象属性情報を取得し、設定されている属性は初期でONの状態で返却する
				JSONArray resetArr = tabledataResetService.layerInfoAttrName(info.layerid, info.tablemasterinfoid);
				obj.put("attrs", resetArr);
				arr.put(obj);
			}

			json.put("resetlayers", arr);
			List<TrackData> trackDataList = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), false);
			// 一番古い災害のstarttimeで設定する
			String oldTrackStartTime = null;
			// Null対策用
			Timestamp time = new Timestamp(System.currentTimeMillis());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			// ここに入る事はありえないがNullチェック
			if(trackDataList == null){
				String now = sdf.format(time);
				oldTrackStartTime = now;
			}else{
				// 一番古い災害起動のstarttimeを取得する
				int len = trackDataList.size();
				oldTrackStartTime = sdf.format( trackDataList.get(len-1).starttime );
			}
			json.put("restoretime", oldTrackStartTime);

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
	 * データリセット対象のレイヤをチェック
	 * 災害モードかつリセットフラグがtrueのレイヤが１つでもあればtrueを返す
	 * @return : null
	 */
    @org.springframework.web.bind.annotation.RequestMapping(value="/track/checkResetLayers", produces="application/json")
	public ResponseEntity<String> checkResetLayers(Map<String,Object>model,
			@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult){
		JSONObject json = new JSONObject();
		// 出力の準備
		/*response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");*/

		try{
			// Tablemaster_infoのresetフラグがtrueのレイヤを検索する
			List<TracktableInfo> tracktableInfoList = tabledataResetService.resetTableList(loginDataDto.getTrackmapinfoid());
			// 複数同時災害の時は実施しない(size > 1以上の時は対象としない)
			List<TrackData> trackDatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), false);
			// 訓練モードも考慮する
			List<TrackData> trainingDatas = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid(), true);
			if(tracktableInfoList == null || tracktableInfoList.size() == 0 || trainingDatas.size() > 0 || trackDatas.size() > 1){
				json.put("resetbool", "false");
			}else{
				json.put("resetbool", "true");
			}
			// 出力
			/*PrintWriter out = response.getWriter();
			out.print( json.toString() );*/

			final HttpHeaders httpHeaders = new HttpHeaders();

			httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
			return new ResponseEntity<String>(json.toString(), httpHeaders, HttpStatus.OK);

		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		}
		return null;
	}

	/**
	 * リセット対象一覧ページへフォワードする
	 * @return フォワード先、各メニューへ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/track/tablereset")
	public String tablereset(Map<String,Object>model,
			@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult) {
		this.trackForm = trackForm;
		if(loginDataDto.getTrackmapinfoid() == 0){
			TrackmapInfo trackmapInfo = trackmapInfoService.findByTrackDataId(loginDataDto.getTrackdataid());
			if(trackmapInfo != null) loginDataDto.setTrackmapinfoid(trackmapInfo.id);
		}
		resetTablemasterInfos = tabledataResetService.resetTableList(loginDataDto.getTrackmapinfoid());
		// 体制名が体制なしか確認(体制なしならtrue)
		isStationOFF = tabledataResetService.isStationOFF();
		setupModel(model);
		return "/track/tablereset";
	}

	/**
	 * 完了処理前のリセット処理
	 * @return : null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/track/resetExecute")
	public String resetExecute(Map<String,Object>model,
			@Valid @ModelAttribute TrackForm trackForm, BindingResult bindingResult) {
		this.trackForm = trackForm;
		try{
			// JSON文字列
			String resetReq = (String)request.getParameter("resetParam");
			Timestamp now = new Timestamp(System.currentTimeMillis());

			if(resetReq != null){
				// データリセット
				tabledataResetService.dataReset(loginDataDto.getEcomUser(), resetReq, now);
			}
			// 災害対応を完了する
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			if (trackData != null){
				// リセット時刻の1秒前にセットする
				trackData.endtime = new Timestamp(now.getTime()-1000L);
				Beans.copy(trackData, trackForm).dateConverter("yyyy-MM-dd HH:mm:ss").execute();
			}

		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
		} catch (IOException e) {
			logger.error(loginDataDto.logInfo(), e);
		} catch (ParseException e) {
			logger.error(loginDataDto.logInfo(), e);
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
		}
		setupModel(model);
		//return complete(model, trackForm, bindingResult);
		//return "forward:complete";
		// foward:complete を使うとリセット実行後に下記のエラーが発生するので、関数呼び出し形式にする 
		// java.lang.NumberFormatException: For input string: ""
        // at java.lang.NumberFormatException.forInputString(NumberFormatException.java:65) ~[na:1.8.0_131]
        // at java.lang.Long.parseLong(Long.java:601) ~[na:1.8.0_131]
        // at java.lang.Long.parseLong(Long.java:631) ~[na:1.8.0_131]
        // at jp.ecom_plat.saigaitask.action.TrackAction.validateComplete(TrackAction.java:456) ~[classes/:2.1.1-SNAPSHOT]

		return complete(model, trackForm, bindingResult);
	}
}
