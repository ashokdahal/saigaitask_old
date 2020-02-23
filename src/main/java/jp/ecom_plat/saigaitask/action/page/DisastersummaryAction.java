/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Status;
import javax.transaction.UserTransaction;
import javax.validation.Valid;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.beans.util.Beans;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.DisasteritemInfo;
import jp.ecom_plat.saigaitask.entity.db.DisastersituationhistoryData;
import jp.ecom_plat.saigaitask.entity.db.DisastersummaryhistoryData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.form.page.DisastersummaryForm;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.service.ResponseService;
import jp.ecom_plat.saigaitask.service.db.DisasteritemInfoService;
import jp.ecom_plat.saigaitask.service.db.DisastersituationhistoryDataService;
import jp.ecom_plat.saigaitask.service.db.DisastersummaryhistoryDataService;
import jp.ecom_plat.saigaitask.util.EnvUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import net.sf.json.JSONObject;

/**
 * リストページを表示するアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class DisastersummaryAction extends AbstractPageAction {

	protected DisastersummaryForm disastersummaryForm;

	@Resource
	protected DisasteritemInfoService disasteritemInfoService;
	@Resource
	protected DisastersummaryhistoryDataService disastersummaryhistoryDataService;
	@Resource
	protected DisastersituationhistoryDataService disastersituationhistoryDataService;
	@Resource
    protected UserTransaction userTransaction;
	@Resource
	protected ResponseService responseService;

	/** 被害項目情報リスト */
	public List<DisasteritemInfo> disasteritemInfoList;
	/** 地区数の最大数 */
	public int MAX_AREA_NUM = 20;
	/** 被害項目数の最大数 */
	public int MAX_DISASTERITEM_NUM = 10;
	/** ユーザー日時のデフォルト設定値 */
	public String defaultUsertime = null;
	/** データ登録済みフラグ */
	public boolean isRegistered = true;
	/** 被害項目未入力時のダミー名称 */
	public String DAMAGE_ITEM_DUMMY_NAME = "DAMAGE_ITEM_DUMMY_NAME";
	/** 地区数値項目最大入力サイズ */
	public int MAX_AREAITEM_INPUT_SIZE = 7;
	/** 手動入力数値項目最大入力サイズ */
	public int MAX_SUMITEM_INPUT_SIZE = 9;
	/** 過去の集計グリッドヘッダー表示名称リスト */
	public List<String> historyGridHeaderDispNameList;
	/** 過去の集計グリッドヘッダーnameリスト */
	public List<String> historyGridHeaderNameList;
	/** 過去の集計グリッドヘッダーの更新日時 */
	private String HISTORY_GRIDHEADER_PERIOD() { return lang.__("Update date and time"); }
	/** 過去の集計グリッドヘッダーのユーザー日時 */
	private String HISTORY_GRIDHEADER_USERTIME() { return lang.__("User date and time"); }

//	/**
//	 * 被災集計機能ページを表示する.
//	 * @return
//	 */
//	public String index() {
//		initPage("disastersummary", disastersummaryForm);
//		pageDto.setEnableFullscreen(true;
//
//		long menutaskid = pageDto.getMenutaskInfo().id;
//		long menuid = pageDto.getMenuInfo().id;
//		disastersummaryForm.menutaskid = menutaskid;
//		disastersummaryForm.menuid = menuid;
//
//		//デフォルトのユーザー日時
//		defaultUsertime = getUpdateTimeBy(new Timestamp(System.currentTimeMillis()));
//
//		//記録データを取得
//		TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
//
//		//被災集計履歴データテーブル
//		DisastersummaryhistoryData disastersummaryhistoryData = null;
//		//被災状況履歴データリスト
//		List<DisastersituationhistoryData> disastersituationhistoryDataList = null;
//
//		//最新データ取得
//		disastersummaryhistoryData = disastersummaryhistoryDataService.findCurrentByTrackdataid(trackData.id);
//
//		if(disastersummaryhistoryData == null){
//			//新規登録の場合
//			isRegistered = false;
//			disastersummaryhistoryData = new DisastersummaryhistoryData();
//
//			//被害項目情報テーブルからデフォルトで表示する被害項目を取得し、被害項目数の最大数分の行データを作成する。
//			disasteritemInfoList = disasteritemInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
//			int disasteritemInfoSize = disasteritemInfoList.size();
//			disastersituationhistoryDataList = new ArrayList<DisastersituationhistoryData>(MAX_DISASTERITEM_NUM);
//			for(int i=0; i<MAX_DISASTERITEM_NUM; i++){
//				DisastersituationhistoryData disastersituationhistoryData = new DisastersituationhistoryData();
//				if(i < disasteritemInfoSize){
//					String itemName = disasteritemInfoList.get(i).name;
//					disastersituationhistoryData.damageitem = itemName;
//				}
//				disastersituationhistoryDataList.add(disastersituationhistoryData);
//			}
//
//		}else{
//			//追加登録の場合
//			//被災状況履歴データ取得
//			disastersituationhistoryDataList = disastersituationhistoryDataService.findByDisastersummaryhistoryid(disastersummaryhistoryData.id);
//
//			//過去の集計グリッドヘッダー名称リストを最新の履歴の表示する被害項目から作成
//			disastersummaryForm.historyGridHeaderNameList = makeHistoryGridHeaderNameList(disastersituationhistoryDataList);
//
//			//表示用に編集
//			for(DisastersituationhistoryData disastersituationhistoryData : disastersituationhistoryDataList){
//				//表示フラグを反転
//				disastersituationhistoryData.dispflag = disastersituationhistoryData.dispflag?false:true;
//			}
//
//			//更新日時をセット
//			setUpdateTime(disastersummaryhistoryData.period);
//		}
//
//		//フォームにセット
//		disastersummaryForm.disastersummaryhistoryData = disastersummaryhistoryData;
//		disastersummaryForm.disastersituationhistoryDataList = disastersituationhistoryDataList;
//
//		return "index.jsp";
//	}
//
//	/**
//	 * 指定IDの被災集計機能ページを表示する.
//	 * @return
//	 */
//	public String indexById() {
//		initPage("disastersummary", disastersummaryForm);
//		pageDto.setEnableFullscreen(true;
//
//		long menutaskid = pageDto.getMenutaskInfo().id;
//		long menuid = pageDto.getMenuInfo().id;
//		disastersummaryForm.menutaskid = menutaskid;
//		disastersummaryForm.menuid = menuid;
//
//		//リクエストから被災集計履歴IDを取得
//		Long disastersummaryhistoryid = disastersummaryForm.selectedDisastersummaryhistoryid;
//		if(disastersummaryhistoryid == null){
//			logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
//			throw new ServiceException("パラメータ不正です。");
//		}
//
//		//記録データを取得
//		TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
//
//		//被災集計履歴データテーブル
//		DisastersummaryhistoryData disastersummaryhistoryData = null;
//		//被災状況履歴データリスト
//		List<DisastersituationhistoryData> disastersituationhistoryDataList = null;
//
//		//リクエストから取得した被災集計履歴IDのデータ取得
//		disastersummaryhistoryData = disastersummaryhistoryDataService.findById(disastersummaryhistoryid);
//		disastersituationhistoryDataList = disastersituationhistoryDataService.findByDisastersummaryhistoryid(disastersummaryhistoryData.id);
//
//		//過去の集計グリッドヘッダー名称リストを最新の履歴の表示する被害項目から作成
//		//最新データ取得
//		DisastersummaryhistoryData currentDisastersummaryhistoryData = disastersummaryhistoryDataService.findCurrentByTrackdataid(trackData.id);
//		List<DisastersituationhistoryData> currentDisastersituationhistoryDataList = disastersituationhistoryDataService.findByDisastersummaryhistoryid(currentDisastersummaryhistoryData.id);
//		disastersummaryForm.historyGridHeaderNameList = makeHistoryGridHeaderNameList(currentDisastersituationhistoryDataList);
//
//		//表示用に編集
//		for(DisastersituationhistoryData disastersituationhistoryData : disastersituationhistoryDataList){
//			//表示フラグを反転
//			disastersituationhistoryData.dispflag = disastersituationhistoryData.dispflag?false:true;
//		}
//
//		//更新日時をセット
//		setUpdateTime(currentDisastersummaryhistoryData.period);
//
//		//フォームにセット
//		disastersummaryForm.disastersummaryhistoryData = disastersummaryhistoryData;
//		disastersummaryForm.disastersituationhistoryDataList = disastersituationhistoryDataList;
//
//		return "index.jsp";
//	}
	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("MAX_AREA_NUM",MAX_AREA_NUM);
		model.put("MAX_DISASTERITEM_NUM", MAX_DISASTERITEM_NUM);
		model.put("defaultUsertime", defaultUsertime);
		model.put("isRegistered", isRegistered);
		model.put("DAMAGE_ITEM_DUMMY_NAME", DAMAGE_ITEM_DUMMY_NAME);
		model.put("MAX_AREAITEM_INPUT_SIZE", MAX_AREAITEM_INPUT_SIZE);
		model.put("MAX_SUMITEM_INPUT_SIZE", MAX_SUMITEM_INPUT_SIZE);
		model.put("historyGridHeaderDispNameList", historyGridHeaderDispNameList);
		model.put("historyGridHeaderNameList", historyGridHeaderNameList);
		model.put("disastersummaryForm", disastersummaryForm);
	}
	/**
	 * 被災集計機能ページを表示する.
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page/disastersummary","/page/disastersummary/index"})
	public String index(Map<String,Object>model,
			@Valid @ModelAttribute DisastersummaryForm disastersummaryForm, BindingResult bindingResult) {
		this.disastersummaryForm = disastersummaryForm;
		initPage("disastersummary", disastersummaryForm);
		pageDto.setEnableFullscreen(true);

		try {
			long menutaskid = pageDto.getMenutaskInfo().id;
			long menuid = pageDto.getMenuInfo().id;
			disastersummaryForm.menutaskid = menutaskid;
			disastersummaryForm.menuid = menuid;

			//デフォルトのユーザー日時
			defaultUsertime = getUpdateTimeBy(new Timestamp(System.currentTimeMillis()));

			//記録データを取得
			if(loginDataDto.getTrackdataid()==0L) throw new ServiceException(lang.__("Disaster is not run."));
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());

			//被災集計履歴データテーブル
			DisastersummaryhistoryData disastersummaryhistoryData = null;
			//被災状況履歴データリスト
			List<DisastersituationhistoryData> disastersituationhistoryDataList = null;

			//リクエストから被災集計履歴IDを取得
			Long disastersummaryhistoryid = disastersummaryForm.selectedDisastersummaryhistoryid;

			if(disastersummaryhistoryid == null){
				//最新データ取得
				disastersummaryhistoryData = disastersummaryhistoryDataService.findCurrentByTrackdataid(trackData.id);

				if(disastersummaryhistoryData == null){
					//新規登録の場合
					isRegistered = false;
					disastersummaryhistoryData = new DisastersummaryhistoryData();

					//被害項目情報テーブルからデフォルトで表示する被害項目を取得し、被害項目数の最大数分の行データを作成する。
					disasteritemInfoList = disasteritemInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
					disastersituationhistoryDataList = new ArrayList<DisastersituationhistoryData>(MAX_DISASTERITEM_NUM);
					for(int i=1; i<=MAX_DISASTERITEM_NUM; i++){
						DisastersituationhistoryData disastersituationhistoryData = new DisastersituationhistoryData();
						String itemName = null;
						for(DisasteritemInfo disasteritemInfo : disasteritemInfoList){
							if(disasteritemInfo.line == i){
								itemName = disasteritemInfo.name;
							}
						}
						disastersituationhistoryData.damageitem = itemName;
						disastersituationhistoryDataList.add(disastersituationhistoryData);
					}

				}else{
					//追加登録の場合
					//被災状況履歴データ取得
					disastersituationhistoryDataList = disastersituationhistoryDataService.findByDisastersummaryhistoryid(disastersummaryhistoryData.id);

					//過去の集計グリッドヘッダー名称リストを最新の履歴の表示する被害項目から作成
					historyGridHeaderDispNameList = makeHistoryGridHeaderDispNameList(disastersituationhistoryDataList);
					historyGridHeaderNameList = makeHistoryGridHeaderNameList(disastersituationhistoryDataList);

					//表示用に編集
					for(DisastersituationhistoryData disastersituationhistoryData : disastersituationhistoryDataList){
						//表示フラグを反転
						disastersituationhistoryData.dispflag = disastersituationhistoryData.dispflag?false:true;
					}

					//更新日時をセット
					setUpdateTime(disastersummaryhistoryData.period);
				}
			}else{
				//リクエストから取得した被災集計履歴IDのデータ取得
				disastersummaryhistoryData = disastersummaryhistoryDataService.findById(disastersummaryhistoryid);
				disastersituationhistoryDataList = disastersituationhistoryDataService.findByDisastersummaryhistoryid(disastersummaryhistoryData.id);

				//過去の集計グリッドヘッダー名称リストを最新の履歴の表示する被害項目から作成
				//最新データ取得
				DisastersummaryhistoryData currentDisastersummaryhistoryData = disastersummaryhistoryDataService.findCurrentByTrackdataid(trackData.id);
				List<DisastersituationhistoryData> currentDisastersituationhistoryDataList = disastersituationhistoryDataService.findByDisastersummaryhistoryid(currentDisastersummaryhistoryData.id);
				historyGridHeaderDispNameList = makeHistoryGridHeaderDispNameList(currentDisastersituationhistoryDataList);
				historyGridHeaderNameList = makeHistoryGridHeaderNameList(currentDisastersituationhistoryDataList);

				//表示用に編集
				for(DisastersituationhistoryData disastersituationhistoryData : disastersituationhistoryDataList){
					//表示フラグを反転
					disastersituationhistoryData.dispflag = disastersituationhistoryData.dispflag?false:true;
				}

				//更新日時をセット
				setUpdateTime(currentDisastersummaryhistoryData.period);
			}

			//フォームにセット
			disastersummaryForm.disastersummaryhistoryData = disastersummaryhistoryData;
			disastersummaryForm.disastersituationhistoryDataList = disastersituationhistoryDataList;
		} catch (ServiceException e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Unable to display disaster summary."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			//ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		setupModel(model);
		return "page/disastersummary/index";
	}

	/**
	 * 被災集計情報を登録する。
	 * @param noticegroupinfoid
	 * @return フォワード先
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/disastersummary/saveData")
	@Transactional(propagation=Propagation.REQUIRED)
	public String saveData(Map<String,Object>model,
			@Valid @ModelAttribute DisastersummaryForm disastersummaryForm, BindingResult bindingResult){
		this.disastersummaryForm = disastersummaryForm;
		ActionMessages errors = new ActionMessages();

		//実行時刻
		Timestamp now = new Timestamp(System.currentTimeMillis());

		DisastersummaryhistoryData disastersummaryhistoryData = disastersummaryForm.disastersummaryhistoryData;
		List<DisastersituationhistoryData> disastersituationhistoryDataList = disastersummaryForm.disastersituationhistoryDataList;

		try{
			userTransaction.begin();

			//登録実行
			//被災集計履歴データ登録
			disastersummaryhistoryData.id = null;
			disastersummaryhistoryData.trackdataid = loginDataDto.getTrackdataid();
			disastersummaryhistoryData.localgovinfoid = loginDataDto.getLocalgovinfoid();
			disastersummaryhistoryData.period = now;
			disastersummaryhistoryDataService.insert(disastersummaryhistoryData);

			//被災状況履歴データ登録
			for(DisastersituationhistoryData disastersituationhistoryData : disastersituationhistoryDataList){
				disastersituationhistoryData.id = null;
				disastersituationhistoryData.disastersummaryhistoryid = disastersummaryhistoryData.id;
				if(disastersituationhistoryData.dispflag != null){
					disastersituationhistoryData.dispflag = disastersituationhistoryData.dispflag?false:true;
				}else{
					disastersituationhistoryData.dispflag = true;
				}
				disastersituationhistoryDataService.insert(disastersituationhistoryData);
			}

//			//実行結果返却
//			try {
//				HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
//				httpServletResponse.setContentType("application/json");
//				PrintWriter sendPoint = new PrintWriter(httpServletResponse.getOutputStream());
//
//				//Entity→JSON形式に変換して出力します。
//				sendPoint.println("{\"result\":\"OK\"}");
//				sendPoint.flush();
//				sendPoint.close();
//
//			} catch (IOException e) {
//				logger.error("localgovermentid : "+loginDataDto.getLocalgovinfoid()+", groupid : "+loginDataDto.getGroupid());
//				logger.error("", e);
//				throw new ServiceException(e);
//			}
		} catch (Exception e) {
			logger.error(loginDataDto.logInfo(), e);
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Failed to register {0}.", lang.__("Disaster history")), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
			try {
				userTransaction.setRollbackOnly();
			} catch (Exception e1) {
				logger.error(loginDataDto.logInfo(), e1);
			}
		} finally {
			try {
				if (userTransaction.getStatus() == Status.STATUS_ACTIVE) {
					userTransaction.commit();
				} else {
					userTransaction.rollback();
				}
			} catch (Exception e) {
				logger.error(loginDataDto.logInfo(), e);
			}
		}
		setupModel(model);
		return "/page/?menutaskid="+disastersummaryForm.menutaskid+"&menuid=" + disastersummaryForm.menuid+"&redirect=true";
	}

	/**
	 * 過去の被災集計情報を取得する。
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/disastersummary/historyIndex")
	public String historyIndex(Map<String,Object>model,
			@Valid @ModelAttribute DisastersummaryForm disastersummaryForm, BindingResult bindingResult){
		this.disastersummaryForm = disastersummaryForm;
		try{
			//被害集計履歴データ取得
			List<DisastersummaryhistoryData> disastersummaryhistoryDataList = disastersummaryhistoryDataService.findByTrackdataid(loginDataDto.getTrackdataid());
			List<Map<String, String>> results = new ArrayList<Map<String, String>>();
//			//最新の履歴データの表示項目map。行番号：被災項目
//			Map<Integer, String> dispItemLinenoMap = new HashMap<Integer, String>();
//			int i = 0;
			for(DisastersummaryhistoryData disastersummaryhistoryData : disastersummaryhistoryDataList){
				Map<String, String> historyRow = new HashMap<String, String>();
				historyRow.put("disastersummaryhistoryid", disastersummaryhistoryData.id.toString());
				historyRow.put("period", disastersummaryhistoryData.period.toString());
				historyRow.put("usertime", disastersummaryhistoryData.usertime);
				for(DisastersituationhistoryData disastersituationhistoryData : disastersummaryhistoryData.disastersituationhistoryDataList){
					String itemName = "item"+disastersituationhistoryData.lineno;
					Integer total = 0;
//					if(i==0){
//					//最新の履歴
						if(disastersituationhistoryData.dispflag){
							total = (disastersituationhistoryData.manualtotal!=0)?disastersituationhistoryData.manualtotal:disastersituationhistoryData.autototal;
//							dispItemLinenoMap.put(disastersituationhistoryData.lineno, itemName);
						}else{
							total = null;
						}
//					}else{
//					//最新でない履歴は、最新履歴の表示項目の行番号の合計値を表示フラグと関係なく表示する。
//						if(dispItemLinenoMap.containsKey(disastersituationhistoryData.lineno)){
//							total = (disastersituationhistoryData.manualtotal!=0)?disastersituationhistoryData.manualtotal:disastersituationhistoryData.autototal;
//						}
//					}
					historyRow.put(itemName, (total==null)?"":total.toString());
				}

				results.add(historyRow);
//				i++;
			}

			//返却値セット
			JSONObject json = new JSONObject();
			json.put("rows", StringUtil.json(results));   //結果一覧

			setupModel(model);
			//レスポンスへ出力
			responseService.responseJson(json);

		}catch(Exception e){
			logger.error(loginDataDto.logInfo(), e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed initial display processing. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}

		return null;
	}

	/*	過去の集計グリッドヘッダー表示名称リストを最新の履歴の表示する被害項目から作成
	*/
	private List<String> makeHistoryGridHeaderDispNameList(List<DisastersituationhistoryData> disastersituationhistoryDataList){
		List<String> historyGridHeaderDispNameList = new ArrayList<String>();
		historyGridHeaderDispNameList.add("disastersummaryhistoryid");
		historyGridHeaderDispNameList.add(HISTORY_GRIDHEADER_PERIOD());
		historyGridHeaderDispNameList.add(HISTORY_GRIDHEADER_USERTIME());
		for(DisastersituationhistoryData disastersituationhistoryData : disastersituationhistoryDataList){
			String damageitem = disastersituationhistoryData.damageitem;
			historyGridHeaderDispNameList.add(damageitem);
		}

		return historyGridHeaderDispNameList;
	}

	/*	過去の集計グリッドヘッダーnameリストを最新の履歴の表示する被害項目から作成
	*/
	private List<String> makeHistoryGridHeaderNameList(List<DisastersituationhistoryData> disastersituationhistoryDataList){
		List<String> historyGridHeaderNameList = new ArrayList<String>();
		historyGridHeaderNameList.add("disastersummaryhistoryid");
		historyGridHeaderNameList.add("period");
		historyGridHeaderNameList.add("usertime");
		for(DisastersituationhistoryData disastersituationhistoryData : disastersituationhistoryDataList){
			String damageitem = disastersituationhistoryData.damageitem;
			damageitem = "item" + disastersituationhistoryData.lineno;
			historyGridHeaderNameList.add(damageitem);
		}

		return historyGridHeaderNameList;
	}

	/**
	 * 過去の被災集計情報をCSV出力
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/disastersummary/outputcsv")
	public String outputcsv(HttpServletResponse res, Map<String,Object>model,
			@Valid @ModelAttribute DisastersummaryForm disastersummaryForm, BindingResult bindingResult){
		this.disastersummaryForm = disastersummaryForm;
		try{
			final String fs = ",";
			final String rs = "<br>";
			StringBuilder sb = new StringBuilder();

			//被害集計履歴データ取得
			List<DisastersummaryhistoryData> disastersummaryhistoryDataList = disastersummaryhistoryDataService.findByTrackdataid(loginDataDto.getTrackdataid());
			boolean first = true;
			for(DisastersummaryhistoryData disastersummaryhistoryData : disastersummaryhistoryDataList){

				//ヘッダ
				if (first) {
					first = false;
					sb.append(lang.__("Update date and time"));	sb.append(fs);
					sb.append(lang.__("User date and time<!--2-->"));	sb.append(fs);
					for (int i = 1; i <= disastersummaryhistoryData.disastersituationhistoryDataList.size(); i++) {
						sb.append(lang.__("Damage item") + i);							sb.append(fs);
						sb.append(disastersummaryhistoryData.area1);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area2);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area3);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area4);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area5);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area6);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area7);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area8);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area9);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area10);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area11);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area12);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area13);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area14);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area15);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area16);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area17);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area18);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area19);		sb.append(fs);
						sb.append(disastersummaryhistoryData.area20);		sb.append(fs);
						sb.append(lang.__("Total<!--2-->"));									sb.append(fs);
						sb.append(lang.__("Notes"));									sb.append(fs);
					}
					sb.setLength(sb.length() - fs.length());
					sb.append(rs);
				}

				sb.append(disastersummaryhistoryData.period.toString());	sb.append(fs);
				sb.append(disastersummaryhistoryData.usertime);				sb.append(fs);

				for(DisastersituationhistoryData disastersituationhistoryData : disastersummaryhistoryData.disastersituationhistoryDataList){
					sb.append(disastersituationhistoryData.damageitem);		sb.append(fs);
					sb.append(disastersituationhistoryData.area1people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area2people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area3people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area4people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area5people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area6people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area7people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area8people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area9people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area10people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area11people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area12people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area13people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area14people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area15people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area16people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area17people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area18people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area19people);	sb.append(fs);
					sb.append(disastersituationhistoryData.area20people);	sb.append(fs);

					Integer total = 0;
					total = (disastersituationhistoryData.manualtotal!=0)?disastersituationhistoryData.manualtotal:disastersituationhistoryData.autototal;
					sb.append(total == null ? "" : total.toString());	sb.append(fs);
					sb.append("\"");	sb.append(disastersituationhistoryData.note/*.substring(0,17)*/);	sb.append("\"");	sb.append(fs);
				}

				sb.setLength(sb.length() - fs.length());
				sb.append(rs);
			}

			//CSV出力(タイムスライダーは未対応)
			outputCSV(res, sb.toString(), null);

		}catch(Exception e){
			logger.error(loginDataDto.logInfo(), e);
			if(EnvUtil.isProductEnv()){
				throw new ServiceException(lang.__("Failed initial display processing. Contact to system administrator."), e);
			}else{
				//本番環境でなければエラー詳細内容も合わせて返却。
				throw new ServiceException(e);
			}
		}

		return null;
	}

}
