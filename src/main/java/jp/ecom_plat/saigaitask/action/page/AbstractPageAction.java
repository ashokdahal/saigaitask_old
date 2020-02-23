/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.geoserver.util.ISO8601Formatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.beans.util.BeanMap;
import org.seasar.framework.beans.util.Beans;
import org.seasar.framework.util.StringUtil;
import org.seasar.framework.util.tiger.Pair;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.html.WebColors;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import jp.ecom_plat.map.db.AttrInfo;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.InvalidAccessException;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.Menutype;
import jp.ecom_plat.saigaitask.dto.ListEditDto;
import jp.ecom_plat.saigaitask.dto.PageDto;
import jp.ecom_plat.saigaitask.entity.db.IssuelayerInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutableInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.PagebuttonMaster;
import jp.ecom_plat.saigaitask.entity.db.PagemenubuttonInfo;
import jp.ecom_plat.saigaitask.entity.db.StationlayerInfo;
import jp.ecom_plat.saigaitask.entity.db.TablelistcolumnInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.page.AbstractPageForm;
import jp.ecom_plat.saigaitask.service.StationService;
import jp.ecom_plat.saigaitask.service.TableService;
import jp.ecom_plat.saigaitask.service.db.FilterInfoService;
import jp.ecom_plat.saigaitask.service.db.IssuelayerInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuprocessInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutableInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskInfoService;
import jp.ecom_plat.saigaitask.service.db.PagebuttonMasterService;
import jp.ecom_plat.saigaitask.service.db.PagemenubuttonInfoService;
import jp.ecom_plat.saigaitask.service.db.StationclassInfoService;
import jp.ecom_plat.saigaitask.service.db.StationlayerInfoService;
import jp.ecom_plat.saigaitask.service.db.TablelistcolumnInfoService;
import jp.ecom_plat.saigaitask.service.db.TablemasterInfoService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.service.db.TrackmapInfoService;
import jp.ecom_plat.saigaitask.service.db.TracktableInfoService;
import jp.ecom_plat.saigaitask.service.ecommap.LayerService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.HttpUtil;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * ページアクションの抽象クラスです.
 * 基本機能はここで実装します.
 */
@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=ServiceException.class)
public abstract class AbstractPageAction extends AbstractAction {

	@Resource protected MenuloginInfoService menuloginInfoService;
	@Resource protected MenuInfoService menuInfoService;
	@Resource protected MenutaskInfoService menutaskInfoService;
	@Resource protected MenuprocessInfoService menuprocessInfoService;
	@Resource protected PagemenubuttonInfoService pagemenubuttonInfoService;
	@Resource protected PagebuttonMasterService pagebuttonMasterService;
	@Resource protected TrackDataService trackDataService;
	@Resource protected StationlayerInfoService stationlayerInfoService;
	@Resource protected StationclassInfoService stationclassInfoService;
	@Resource protected StationService stationService;
	@Resource protected TrackmapInfoService trackmapInfoService;
	@Resource protected TablemasterInfoService tablemasterInfoService;
	@Resource protected TracktableInfoService tracktableInfoService;
	@Resource protected IssuelayerInfoService issuelayerInfoService;
	@Resource protected TableService tableService;
	@Resource protected FilterInfoService filterInfoService;

	/** 下ボタンリスト */
	public List<PagemenubuttonInfo> bbuttonItems;
	/** ボタンマスタ */
	public Map<Integer, PagebuttonMaster> buttonMap = new HashMap<Integer, PagebuttonMaster>();
	/** レイヤサービス */
	@Resource protected LayerService layerService;

	public String pagetype = "";

	/** ページDto */
	@Resource public PageDto pageDto;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("bbuttonItems", bbuttonItems);
		model.put("buttonMap", buttonMap);
		model.put("pageDto", pageDto);
		model.put("pagetype", pagetype);
	}

	/**
	 * ページの表示に必要なデータを取得します.
	 * リストや地図ページの最初に呼び出してください.
	 * @param pagetype ページ種別名称（アクション名）
	 *        TODO: アクションクラスから自動取得できる？
	 * @param pageForm アクションフォーム
	 */
	protected void initPage(String pagetype, AbstractPageForm pageForm) {
		this.pagetype = pagetype;

		// 初期化
		initAction();

		// フォワード前に設定したパラメータがあれば読み込む
		AbstractPageForm forwardPageForm = (AbstractPageForm) request.getAttribute("page_pageForm");
		if(forwardPageForm!=null) {
			// フォワード前に設定したパラメータがあれば読み込む
			Beans.copy(forwardPageForm, pageForm).excludesNull().execute();
			// PageForm を MapForm/ListForm などと同じにする（MapServiceで @Resource pageForm しているため）
			Beans.copy(pageForm, forwardPageForm).execute();
		}

		// 過去の災害閲覧時など、時間パラメータ未指定なら指定する
		if(loginDataDto.getTime()!=null && StringUtil.isEmpty(pageForm.time)) pageForm.time = new ISO8601Formatter().format(loginDataDto.getTime());

		// ページ種別パラメータを初期化
		pageForm.pagetype = pagetype;

		// PageDtoを初期化
		initPageDto(pageForm);

		// 表示可能なメニューではない
		if(pageDto.getMenuInfo()==null) {
			// TODO: 404 と 405 を分ける？
			response.setStatus(404);
			return;
		}

		//表示ボタンを検索
		showBottomButton();
	}

	/**
	 * タスクリストのプロセスリストを
	 * 班が表示可能なものだけに初期化.
	 */
	protected void initMenuprocessInfos() {
		// 災害種別からプロセス、タスク、メニューをdisporder順で取得する
		MenuloginInfo login = null;
		if (loginDataDto.getGroupInfo() != null && !loginDataDto.isUsual())
			//login = menuloginInfoService.findByDisasterIdAndGroupInfoIdAndNotDeleted(loginDataDto.getDisasterid(), loginDataDto.getGroupid());
			login = menuloginInfoService.findByGroupInfoIdAndNotDeleted(loginDataDto.getGroupid());
		else if (loginDataDto.getUnitInfo() != null && loginDataDto.isUsual())
			login = menuloginInfoService.findByUnitInfoIdAndNotDeleted(loginDataDto.getUnitid());

		if (login != null) {
			/*pageDto.setMenuprocessInfos(menuprocessInfoService.findValidJoinMenuInfoOrderbyDisporder2(login.id));
			if (pageDto.getMenuprocessInfos() != null && pageDto.getMenuprocessInfos().size() > 0) {
				for (MenuprocessInfo process : pageDto.getMenuprocessInfos()) {
					for (MenuprocesstaskInfo ptask : process.menuprocesstaskInfos) {
						if (process.menutaskInfos == null)
							process.menutaskInfos = new ArrayList<MenutaskInfo>();
						process.menutaskInfos.add(ptask.menutaskInfo);
						ptask.menutaskInfo.menuprocessInfo = process;
					}
				}
			}
			if (pageDto.getMenuprocessInfos() == null || pageDto.getMenuprocessInfos().size() == 0)*/
				pageDto.setMenuprocessInfos(menuprocessInfoService.findValidJoinMenuInfoOrderbyDisporder(login.id));
		}
		else
			pageDto.setMenuprocessInfos(new ArrayList<MenuprocessInfo>());
		//システムメニューを追加
		List<MenuprocessInfo> processes = menuprocessInfoService.findAllJoinMenuInfoOrderbyDisporder(0L);
		if (processes.size() > 0) {
			MenuprocessInfo process = processes.get(0);
			if (process.menutaskInfos != null && process.menutaskInfos.size() > 0) {
				MenutaskInfo task = process.menutaskInfos.get(0);
				pageDto.setHeadofficeTaskId(task.id);
			}
		}
		pageDto.getMenuprocessInfos().addAll(processes);
	}

	/**
	 * PageDtoを初期化します.
	 */
	protected void initPageDto(AbstractPageForm pageForm) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

		pageDto.setDefault();

		// プロセスリストを初期化
		if(pageDto.getMenuprocessInfos()==null) {
			initMenuprocessInfos();
		}

		// 権限チェック済みメニューから表示するメニュー等を取得
		MenuInfo menuInfo = null;
		MenutaskInfo menutaskInfo = null;
		for(MenuprocessInfo menuprocessInfo : pageDto.getMenuprocessInfos()) {
			for(MenutaskInfo _menutaskInfo : menuprocessInfo.menutaskInfos) {
				// メニュータスクIDが指定されている場合は、タスクもチェックする
				if(pageForm.menutaskid!=null && _menutaskInfo.id.equals(pageForm.menutaskid)==false) continue;
				for(MenutaskmenuInfo _menutaskmenuInfo : _menutaskInfo.menutaskmenuInfos) {
					MenuInfo _menuInfo = _menutaskmenuInfo.menuInfo;
					if(_menuInfo.id.equals(pageForm.menuid)) {
						menuInfo = _menuInfo;
						menutaskInfo = _menutaskInfo;
						break;
					}
				}
				if(menuInfo!=null) break;
			}
			if(menuInfo!=null) break;
		}

		// 見つからなければDBから検索
		if(menuInfo==null) {
			HttpUtil.throwBadRequestExceptionIfIsNull(pageForm.menuid, pageForm.menutaskid);
			menuInfo = menuInfoService.findById(pageForm.menuid);
			menutaskInfo = menutaskInfoService.findById(pageForm.menutaskid);
		}

		pageDto.setViewMode(false);
		if(menuInfo!=null) {
			// フィルター情報を検索
			menuInfo.filterInfoList = filterInfoService.findByMenuid(menuInfo.id);

			// pageDtoに設定
			pageDto.setMenuInfo(menuInfo);
			pageDto.setMenutaskInfo(menutaskInfo);
			pageDto.setMenuprocessInfo(pageDto.getMenutaskInfo().menuprocessInfo);
			if (pageDto.getMenuprocessInfo() == null)
				pageDto.setMenuprocessInfo(menuprocessInfoService.findByNotDeletedId(menutaskInfo.menuprocessinfoid));

			// ページ切替ボタンを初期化
			List<Pair<String, String>> buttons = Menutype.buttonsMap.get(pageDto.getMenuInfo().menutypeid);
			if(buttons!=null && !pageForm.pagetype.equals("externallist")) {
				for(Pair<String, String> button : buttons) {
					if(button.getFirst().equals(pageForm.pagetype)==false) {
						pageDto.setPagetoggleButton(button);
						break;
					}
				}
			}

			// 自分以外のメニューかどうか menulogininfoidが０なら、システムメニュー
			MenuloginInfo menuloginInfo = pageDto.getMenuprocessInfo().menuloginInfo;
			if (menuloginInfo != null && menuloginInfo.id != 0 && !menuloginInfo.deleted) {
				if(!loginDataDto.isUsual() && loginDataDto.getGroupid()!=pageDto.getMenuprocessInfo().menuloginInfo.groupid) {
					pageDto.setOthermenu(true);
					pageDto.setViewMode(true);
				}
				else if(loginDataDto.isUsual() && loginDataDto.getUnitid()!=pageDto.getMenuprocessInfo().menuloginInfo.unitid) {
					pageDto.setOthermenu(true);
					pageDto.setViewMode(true);
				}
			}
			else if (pageDto.getMenuprocessInfo().menulogininfoid != 0 && !pageDto.getMenuprocessInfo().deleted){
				pageDto.setOthermenu(true);
				pageDto.setViewMode(true);
			}
		}

		//災害モードでなければviewモード
		if ((loginDataDto.getTrackdataid() == 0 && !loginDataDto.isMaster() && !loginDataDto.isUsual()) || !loginDataDto.isEdiable())
			pageDto.setViewMode(true);

		// 全画面表示
		pageDto.setFullscreen(pageForm.fullscreen);
		/* v2.0 より、全画面表示でも編集可に変更
		if(pageDto.fullscreen) pageDto.setViewMode(true; // 全画面表示ならviewモード
		*/

		// 意思決定支援
		pageDto.setDecisionSupport(pageForm.decisionsupport);

		// リクエストフォームをJSONオブジェクトに変換
		pageDto.setFormJSONFromForm(pageForm);
		// ページのフォーム以外のリクエストパラメータも渡す
		//Map<String, String> param = (Map<String, String>)session.getAttribute("param");
		Map<String, String> param = new HashMap<>();
		try {
    	    String queryString = request.getQueryString();

    	    if (StringUtil.isNotEmpty(queryString)) {
    	    	queryString = URLDecoder.decode(queryString, "UTF-8");
        	    String[] parameters = queryString.split("&");

        	    for (String parameter : parameters) {
        	        String[] keyValuePair = parameter.split("=");
        	        //String[] values = queryParameters.get(keyValuePair[0]);
        	        //values = ArrayUtils.add(values, keyValuePair.length == 1 ? "" : keyValuePair[1]); //length is one if no value is available.
        	        //queryParameters.put(keyValuePair[0], values);
        	        if(keyValuePair.length > 1){
        	        	
        	        	param.put(keyValuePair[0], keyValuePair[1]);
        	        }
        	    }
    	    }
		} catch(UnsupportedEncodingException e) {
			throw new ServiceException(e);
		}
		if(pageDto.getFormJSON()==null) pageDto.setFormJSON(new net.sf.json.JSONObject());
		pageDto.getFormJSON().putAll(param);

		//アラームの間隔
		pageDto.setAlarmInterval(loginDataDto.getAlarmInterval()*1000);
		//アラームオープンフラグ
		pageDto.setAlarmOpen(loginDataDto.isAlarmOpen());

		pageDto.setGroupInfo(loginDataDto.getGroupInfo());
		pageDto.setLoginName(loginDataDto.getLoginName());
		pageDto.setUsual(loginDataDto.isUsual());

		//災害検索
		TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
		pageDto.setTrackData(trackData);
		if (trackData != null) {
			pageDto.setSaigaiName(trackData.name);
			pageDto.setSaigai(true);
		}

		// 「現在の体制」の初期化
		try {
			StationlayerInfo stationlayerInfo = stationService.getLoginStationlayerInfo();
			// null の場合は、「現在の体制」を表示させない
			if(stationlayerInfo==null) {
				pageDto.setStationName(null);
				logger.debug(lang.__("For local gov. ID ({0}) has not been set system layer info, [current system] does not displayed.", loginDataDto.getLocalgovinfoid()));
			}
			else {
				String currentStationName = stationService.getLoginCurrentSationName();
				pageDto.setStationName(currentStationName);
			}
		} catch (ServiceException e) {
			logger.warn("localgovinfoid: "+loginDataDto.getLocalgovinfoid()+", trackdataid: "+lang.__("An error occurred initializing \"current system\" in {0}.", loginDataDto.getTrackdataid()), e);
			pageDto.setStationName(e.getMessage());
		}

		//自動発報設定
		LocalgovInfo gov = loginDataDto.getLocalgovInfo();
		if(gov!=null)
		if (gov.autostart)
			pageDto.setAutoName(Constants.ISSUE_AUTO_VALID());
		else
			pageDto.setAutoName(Constants.ISSUE_AUTO_INVALID());

		// 自治体設定
		pageDto.setLocalgovInfo(gov);

		//発令状況
		IssuelayerInfo layerInfo = loginDataDto.getIssueLayerInfo();
		if (layerInfo == null) {
			layerInfo = issuelayerInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
			loginDataDto.setIssueLayerInfo(layerInfo);
		}
		if (layerInfo == null) {
			logger.debug(lang.__("For local gov. ID ({0}) has not been set evacuation advisory layer info, [announcement status] does not displayed.", loginDataDto.getLocalgovinfoid()));
			pageDto.setIssueName(null);
		}
		else if (trackData != null) {
			TablemasterInfo master = tablemasterInfoService.findByNotDeletedId(layerInfo.tablemasterinfoid);
			if(master != null){
				TracktableInfo ttable = tracktableInfoService.findByTablemasterInfoIdAndTrackDataId(master.id, trackData.id);
				if (ttable != null) {
					List<String> vals = tableService.selectDistinct(ttable.tablename, layerInfo.attrid, pageForm.timeParams());

					String issue = Constants.ISSUE_CANCEL_NAME();
					for (String val : vals) {
						if(val==null) continue;
						// val=避難準備情報は、避難勧告でないかつ避難指示でないなら上書き
						if (!issue.equals(Constants.ISSUE_ADVISE_NAME()) && !issue.equals(Constants.ISSUE_INDICATE_NAME()) && val.equals(Constants.ISSUE_PREPARE_NAME()))
							issue = Constants.ISSUE_PREPARE_NAME();
						// val=避難勧告情報は、避難指示でないなら上書き
						if (!issue.equals(Constants.ISSUE_INDICATE_NAME()) && val.equals(Constants.ISSUE_ADVISE_NAME()))
							issue = Constants.ISSUE_ADVISE_NAME();
						// val=避難指示なら避難指示
						if (val.equals(Constants.ISSUE_INDICATE_NAME()))
							issue = Constants.ISSUE_INDICATE_NAME();
					}
					pageDto.setIssueName(issue);
				}
			}
		}

		// インクルードページの自動インクルード設定
		// ページ種別のところに head.jsp があればインクルードする.
		String includeHeadJSP = "/WEB-INF/view/page/"+pageForm.pagetype+"/head.jsp";
		File headJSP = new File(application.getRealPath(includeHeadJSP));
		if(headJSP.exists()) {
			pageDto.setIncludeHeadJSP(includeHeadJSP);
		}

		// 地図表示／非表示対応
		// セッションから地図表示／非表示フラグを取得
		String mapVisible = (String)request.getSession().getAttribute(Constants.SESSIONPARAM_MAPVISIBLE);
		if(mapVisible == null){
			pageDto.setMapVisible(Constants.MAPVISIBLE_VISIBLE);
			request.getSession().setAttribute(Constants.SESSIONPARAM_MAPVISIBLE, /*mapVisible*/ null);
		}else{
			pageDto.setMapVisible(mapVisible);
		}

		// 時間パラメータ
		pageDto.setTime(pageForm.time);

		// 地図マスター情報
		MapmasterInfoService mapmasterInfoService = SpringContext.getApplicationContext().getBean(MapmasterInfoService.class);
		MapmasterInfo mapmasterInfo = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
		pageDto.setMapmasterInfo(mapmasterInfo);

		// JSON 連携情報
		String jInterval = Config.getString("JSONIMPORT_CHECK_INTERVAL");
		pageDto.setJsonimportCheckInterval(jInterval == null ? 0 : Integer.parseInt(jInterval) * 1000);
	}

	/**
	 * ページ下部のボタンを表示
	 */
	protected void showBottomButton() {

		bbuttonItems = pagemenubuttonInfoService.findByMenuInfoId(pageDto.getMenuInfo().id);
		// viewモードかつ避難者推計モードでなければ無効化する
		if (pageDto.isViewMode() && !pageDto.isDecisionSupport()) {
			for (PagemenubuttonInfo btn : bbuttonItems) {
				btn.enable = false;
			}
		}
		List<PagebuttonMaster> btnlist = pagebuttonMasterService.findAllOrderBy();
		for (PagebuttonMaster btn : btnlist) {
			// 避難者推計モードなら、ボタン名称を変更して該当ボタンのみを表示させる
			if(pageDto.isDecisionSupport()){
				if(btn.name.equals(lang.__("Refugee estimation"))){
					btn.name = lang.__("Calculation executes");
					//btn.href = "javascript:decisionSupportOperation()";
					buttonMap.put(btn.id, btn);
				}
			}else{
				buttonMap.put(btn.id, btn);
			}
		}
	}

	/**
	 * Timestamp から最終更新日時の文字列を取得(変換)する.
	 * @param lasttime 最終更新日時のTimestamp
	 * @return 最終更新日時文字列、空の場合は null
	 */
	protected String getUpdateTimeBy(Timestamp lasttime) {
		String updateTime = null;
		if (lasttime != null) {
			//Locale.setDefault(new Locale("ja","JP","JP"));
			Calendar cal = Calendar.getInstance();
			cal.setTime(lasttime);
			if (pageDto.getUpdateTimeFormat()==null)
				pageDto.setUpdateTimeFormat(lang.__("MMM.d,yy 'at' H:m"));
			String a = pageDto.getUpdateTimeFormat();
			Locale b = lang.getLocale();
			SimpleDateFormat sdf = new SimpleDateFormat(pageDto.getUpdateTimeFormat(),lang.getLocale());
			updateTime = sdf.format(cal.getTime());
			//Locale.setDefault(Locale.JAPAN);
		}
		return updateTime;
	}

	/**
	 * 最終更新日を設定する.
	 * 和暦年月日形式で表示する.
	 * @param lasttime 最終更新日
	 */
	protected void setUpdateTime(Timestamp lasttime) {
		pageDto.setUpdateTime(getUpdateTimeBy(lasttime));
	}

	/**
	 * 登録災害が変更されていれば、セッションを変更させる。
	 */
	protected void checkTrackData() {
		if (loginDataDto.getTrackdataid() == 0 && !loginDataDto.isUsual()) {//平時にログインしている
			List<TrackData> tracks = trackDataService.findByCurrentTrackDatas(loginDataDto.getLocalgovinfoid());
			for(TrackData track : tracks) {
				if (track != null && track.endtime == null) {//災害が発生
					loginDataDto.setTrackdataid(track.id);
					//loginDataDto.getDisasterid() = track.disasterid;
					loginDataDto.setEdiable(true);
					break;
				}
			}
		}
		else if (loginDataDto.getTrackdataid() > 0 && loginDataDto.isEdiable()) {//災害時にログイン
			TrackData track = trackDataService.findById(loginDataDto.getTrackdataid());
			if (track != null && track.endtime != null) {//災害が終了した。
				loginDataDto.setEdiable(false);
			}
		}
	}

	/**
	 * CSVの出力
	 * @param value
	 * @param timeFilename 表示時刻
	 */
	public void outputCSV(HttpServletResponse httpServletResponse, String value, String timeFilename)
	{
		// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}
		//出力用一時ファイル名を時間で作成
		Timestamp time = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String now = sdf.format(time);
		try {
			//HTTPレスポンスヘッダの定義
			//HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
			httpServletResponse.setContentType("application/octet-stream");
			httpServletResponse.setHeader("Content-disposition","attachment; filename=\""+now+(StringUtil.isNotEmpty(timeFilename)?"_at_"+timeFilename:"")+".csv\"");
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(response.getOutputStream(),"MS932"));
			//PrintWriter pw = new PrintWriter(response.getOutputStream());
			writeCSV(value, pw);
			pw.close();

		}catch(IOException e){
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}
	}

	/**
	 * PrintWriter にCSV文字列を書き出す
	 * @param value
	 * @param pw
	 */
	public void writeCSV(String value, PrintWriter pw) {
		//パラメータで取得したCSVの文字列を行ごとに分割
		String[] csvStr = value.split("<br>");
		for(String oneLine : csvStr){
			if (StringUtil.isEmpty(oneLine)) continue;
			pw.println(oneLine);
		}
		pw.flush();
	}

	/**
	 * リストのPDF出力
	 *
	 * @param printTime 印刷日時やデータ時点情報などの時間に関する文字列
	 * @param menuprocessName プロセス名
	 * @param menutaskName タスク名
	 * @param menuName メニュー名
	 * @param dataList データリスト
	 * @param bsum 合計行フラグ
	 */
	public void outputListPdf(HttpServletResponse httpServletResponse, String printTime, String menuprocessName, String menutaskName, String menuName, JSONArray dataList, boolean bsum)
	{

		//出力用一時ファイル名を時間で作成
		Timestamp time = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String now = sdf.format(time);

		// PDFファイルを作成
		ByteArrayOutputStream bos2 = createListPdf(printTime, menuprocessName, menutaskName, menuName, dataList, bsum);

		//ブラウザへ出力

		//HTTPレスポンスヘッダの定義
		//HttpServletResponse httpServletResponse = ResponseUtil.getResponse();
		httpServletResponse.setContentType("application/pdf");
		httpServletResponse.setHeader("Content-disposition", "attachment; filename=\""+now+".pdf\"");
		//キャッシュを無効化する
		httpServletResponse.addHeader("Cache-Control", "no-cache");
		httpServletResponse.addHeader("Pragma", "no-cache");
		httpServletResponse.addHeader("Expires", "0");

		//出力ストリームの生成
		try {
			OutputStream out = httpServletResponse.getOutputStream();
			out.write(bos2.toByteArray());
			out.close();
		} catch (IOException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		}

	}

	/**
	 * リストのPDF出力
	 *
	 * @param headerStr ヘッダー文字列
	 * @param menuprocessName プロセス名
	 * @param menutaskName タスク名
	 * @param menuName メニュー名
	 * @param dataList データリスト
	 * @param bsum 合計行フラグ
	 * @return 出力ストリーム
	 */
	public ByteArrayOutputStream createListPdf(String headerStr, String menuprocessName, String menutaskName, String menuName, JSONArray dataList, boolean bsum)
	{
		//出力用のストリームをインスタンス化
		ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
		ByteArrayOutputStream bos2 = new ByteArrayOutputStream();

		//step 1:新規ドキュメントを作成
		Document document = new Document();

		try {
			//日本語フォント設定
			BaseFont bfont = BaseFont.createFont(Config.getFontFilePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
			//BaseFont bfont_heiseiKakuGo = BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
			//BaseFont bfont_heiseiMin    = BaseFont.createFont("HeiseiMin-W3", "UniJIS-UCS2-H", BaseFont.NOT_EMBEDDED);
			//BaseFont bfont_ipaMincho   = BaseFont.createFont("C:/IDE/eclipse-4.3-64bit/workspace/JavaStudy/fonts/ipam00303/ipam.ttf", BaseFont.IDENTITY_H, true);

			Font font10 = new Font(bfont, 10);
			Font font10b_white = new Font(bfont, 10, Font.BOLD);
			font10b_white.setColor(WebColors.getRGBColor("#FFFFFF"));


			//ドキュメントの設定
			// Pager Size    : A4
			// Document Body :
			//
			// cf. 1pt = 1/72.27 inch = 0.3514 mm
			//      A4 = (210mm x 297mm) = (595.0pt x842.0pt)
			//
			float margin = 36f;
			//document.setPageSize(PageSize.A4);	//用紙サイズ A4縦
			document.setPageSize(PageSize.A4.rotate());	//用紙サイズ A4横
			document.setMargins(margin, margin, margin, margin);

			//step 2:PdfWriterインスタンスの取得
			PdfWriter.getInstance(document, bos1);
			//PdfWriter writer = PdfWriter.getInstance(document, bos1);


			/**
			//ヘッダーフッター
			HeaderFooter event = new HeaderFooter();
			Rectangle pageSize = document.getPageSize();	//ページサイズ
			Rectangle rectHF = new Rectangle(margin, margin, (pageSize.getWidth()-margin), (pageSize.getHeight()-margin));
			writer.setBoxSize("art", rectHF);
			writer.setPageEvent(event);
			**/

			//step 3:ドキュメントをオープン
			document.open();

			//メタ情報を追加
			//document.addAuthor("");
			document.addTitle(menuprocessName+" > "+menutaskName+" > "+menuName);
			//document.addSubject("sub title");
			document.addCreationDate();
			document.addCreator(loginDataDto.getLocalgovInfo().systemname);


			//step 4:ドキュメントにコンテンツを追加する
			Paragraph p0 = StringUtil.isNotEmpty(headerStr) ? new Paragraph(headerStr, font10) : null;
			Paragraph p1 = new Paragraph(lang.__("Task:")+menuprocessName, font10);
			Paragraph p2 = new Paragraph(lang.__("Sub task:")+menutaskName, font10);
			Paragraph p3 = new Paragraph(lang.__("Menu:")+menuName, font10);

			if(p0!=null) p0.setLeading(0, 2f);
			p1.setLeading(0, 2f);
			p2.setLeading(0, 2f);
			p3.setLeading(0, 2f);

			if(p0!=null) document.add(p0);
			document.add(p1);
			document.add(p2);
			document.add(p3);

			document.add(Chunk.NEWLINE);	//改行

			// データの件数を取得
			int fields = dataList.getJSONArray(0).length();

			//テーブルの作成と設定
			PdfPTable table = new PdfPTable(fields);
			table.getDefaultCell().setBorder(1);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);

			//セルのカラーを定義
			Color color_header = WebColors.getRGBColor("#4F81BD");
			Color color_odd = WebColors.getRGBColor("#F0F0F6");
			Color color_border = WebColors.getRGBColor("#CDCDCD");
			Color color_sum = WebColors.getRGBColor("#1E4B78");
			Color color_grphdr = WebColors.getRGBColor("#F7E5B5");

			//セルの幅
			float[] widths = new float[fields];

			//JSON形式のデータをセルに追加
			PdfPCell cell = null;
			int nr = 0;
			int hidx = 0;
			if (bsum) hidx = 1;
			for (int i = 0; i < dataList.length(); i++) {
				JSONArray jsonArray = null;
				try {
					jsonArray = dataList.getJSONArray(i);
				} catch (JSONException e) {
				}
				if (jsonArray == null) continue;
				for (int j = 0; j < jsonArray.length(); j++) {
					Object obj = jsonArray.get(j);

					//ヘッダ行、合計値行
					if (i <= hidx) {
						//文字列のサイズを取得
						float textWidth = bfont.getWidthPoint(jsonArray.getString(j), 10);
						if (widths[j] < textWidth)
							widths[j] = textWidth;

						//セルにコンテンツを追加
						cell = new PdfPCell(new Phrase(jsonArray.getString(j), font10b_white));
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						if(i==0){cell.setBackgroundColor(color_header);//ヘッダ行
						}else{
							cell.setBackgroundColor(color_sum);//合計値行
						}
						cell.setBorderColor(color_border);
						cell.setPadding(4f);

						//テーブルにセルを追加
						table.addCell(cell);

					// グルーピングヘッダ
					} else if (obj instanceof JSONObject) {
						JSONObject jsonObject = (JSONObject)obj;

						//セルにイメージを追加（開閉状態）
						final byte[] maskOpen = { (byte)0xff, (byte)0xfe, (byte)0xfc, (byte)0xf8, (byte)0xf0, (byte)0xe0, (byte)0xc0, (byte)0x80, (byte)0xff };
						final byte[] maskClose = { (byte)0xdf, (byte)0xcf, (byte)0xc7, (byte)0xc3, (byte)0xc1, (byte)0xc3, (byte)0xc7, (byte)0xcf, (byte)0xdf };
						byte[] maskData = jsonObject.getBoolean("open") ? maskOpen : maskClose;
						Image mask = Image.getInstance(8, 9, 1, 1, maskData);
						mask.makeMask();
						byte[] imageData = new byte[8 * 9];
						Arrays.fill(imageData, (byte)0x88);
						Image image = Image.getInstance(8, 9, 1, 8, imageData);
						image.setImageMask(mask);
						cell = new PdfPCell(image, false);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(color_grphdr);
						cell.setBorderColor(color_border);
						cell.setPadding(4f);

						//テーブルにセルを追加
						table.addCell(cell);

						//セルにコンテンツを追加
						String text = jsonObject.getString("value") + jsonObject.getString("count");
						cell = new PdfPCell(new Phrase(text, font10));
						cell.setColspan(table.getNumberOfColumns() - 1);
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell.setBackgroundColor(color_grphdr);
						cell.setBorderColor(color_border);
						cell.setPadding(4f);

						//テーブルにセルを追加
						table.addCell(cell);
						table.completeRow();
						nr--;

					//データ行
					} else {
						//文字列のサイズを取得
						float textWidth = bfont.getWidthPoint(jsonArray.getString(j), 10);
						if (widths[j] < textWidth)
							widths[j] = textWidth;

						//セルにコンテンツを追加
						cell = new PdfPCell(new Phrase(jsonArray.getString(j), font10));
						cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						if (nr%2!=0)
							cell.setBackgroundColor(color_odd);
						cell.setBorderColor(color_border);
						cell.setPadding(4f);

						//テーブルにセルを追加
						table.addCell(cell);
					}
				}
				table.completeRow();
				nr++;

			}

			//table.setHorizontalAlignment(Element.ALIGN_CENTER);	//テーブルの表示位置
			table.setHorizontalAlignment(Element.ALIGN_LEFT);	//テーブルの表示位置
			table.setWidths(widths);	//セル幅のフィッティング

			//ドキュメントにテーブルを追加
			document.add(table);


			//step 5:ドキュメントをクローズする
			document.close();


			//ページ番号の埋め込み
			PdfReader reader = new PdfReader(bos1.toByteArray());
			PdfStamper stamper = new PdfStamper(reader, bos2);

			int num = reader.getNumberOfPages();
			for (int i = 1; i <= num; i++) {
				String page = String.format("%d/%d",  i, num);
				float strWidth = bfont.getWidthPoint(page, 10);
				ColumnText.showTextAligned(
						stamper.getOverContent(i),
						Element.ALIGN_LEFT,
						new Phrase(page),
						(document.right() / 2) + (strWidth / 2),
						document.bottom() - (document.bottomMargin() / 3 * 2),
						0
				);
			}

			stamper.close();
			reader.close();

			return bos2;

		} catch (DocumentException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		} catch (IOException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		} catch (JSONException e) {
			logger.error(loginDataDto.logInfo(), e);
			throw new ServiceException(e);
		} finally {
			document.close();
		}
	}

	/**
	 * タブを追加する.
	 * @param title タブ名
	 * @param url URL
	 */
	protected void addPageTab(String title, String url) {
		PageDto.Tab tab = new PageDto.Tab();
		tab.title = title;
		tab.url = url;
		pageDto.getTabs().add(tab);
	}

	/**
	 * 検索条件からフィルタリングのJSONを作成する。
	 * @param datas
	 * @param isnot
	 * @return
	 */
	public JSONObject createFileterString(List<ListEditDto> datas, boolean isnot)
	{
		JSONArray condArray = new JSONArray();
		JSONObject conditionValue = new JSONObject();
		try {
			for (ListEditDto data : datas) {
				String value = data.value;
				String ids[] = data.id.split(":");
				String table = ids[0];
				String attrid = ids[1];
				String key = ids[2];
				int item = Integer.parseInt(ids[3]);
				logger.info("datas : "+ids[0]+","+ids[1]+","+ids[2]+","+data.value);
				if (StringUtil.isEmpty(value)) continue;
				if (StringUtil.isEmpty(ids[1])) continue;

				JSONObject json = new JSONObject();
				json.put("attrId", attrid);
				String comp = "=";
				if (isnot) comp = "!=";
				if (key.equals("gid") || key.equals("_orgid")) {//eコミマップ
					LayerInfo linfo = layerService.getLayerInfo(table);
					AttrInfo attrInfo = linfo.getAttrInfo(attrid);
					if (attrInfo.dataType == AttrInfo.DATATYPE_TEXT) {
						comp = " like ";
						value = "%"+value+"%";
					}
					else if (attrInfo.dataType == AttrInfo.DATATYPE_DATETIME || attrInfo.dataType == AttrInfo.DATATYPE_DATE ||
							attrInfo.dataType == AttrInfo.DATATYPE_INTEGER || attrInfo.dataType == AttrInfo.DATATYPE_FLOAT) {
						if (item == 1)
							comp = ">=";
						else if (item == 2)
							comp = "<=";
					}
					else if (attrInfo.dataType == AttrInfo.DATATYPE_CHECKBOX) {
						if (!value.equals(attrInfo.dataExp)) {
							comp = "!=";
							value = attrInfo.dataExp;
						}
					}
				}
				else if (key.equals("id")) {//システムテーブル
					String type = tableService.getAttrType(table, attrid);
					if(type.equals(Constants.DATABASE_COLTYPE_TEXT)){
						comp = " like ";
						value = "'%"+value+"%'";
					}
					else if(type.equals(Constants.DATABASE_COLTYPE_INT) || type.equals(Constants.DATABASE_COLTYPE_BIGINT) ||
							type.equals(Constants.DATABASE_COLTYPE_FLOAT) || type.equals(Constants.DATABASE_COLTYPE_DOUBLE) ||
							type.equals(Constants.DATABASE_COLTYPE_TIMESTAMP)) {
						if (item == 1)
							comp = ">=";
						else if (item == 2)
							comp = "<=";
					}
					if (type.equals(Constants.DATABASE_COLTYPE_TIMESTAMP))
						value = "'"+value+"'";
				}
				json.put("comp", comp);
				json.put("value", value);
				if (condArray.length() > 0) {
					if (!isnot)	json.put("logical", "AND");
					else json.put("logical", "OR");
				}

				condArray.put(json);
			}

			//boolean isnot = false;
			if (condArray.length() == 0) return null;
			conditionValue = createCondJSON(condArray, 0d, 0, null, isnot);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return conditionValue;
	}

	public JSONObject createCondJSON(JSONArray condition, double buffer, int spatialType, JSONArray spatialLayer, boolean isNot) throws JSONException
	{
		JSONObject condJson = new JSONObject();
		if (condition != null) condJson.put("condition", condition);
		condJson.put("buffer", buffer);
		condJson.put("spatial", spatialType);
		if (spatialLayer != null) condJson.put("spatiallayer", spatialLayer);
		if (isNot) condJson.put("isnot", isNot);

		return condJson;
	}

	/**
	 * 検索結果文字列を返す。
	 * @param menuid
	 * @param colinfoItems
	 * @return
	 */
	protected String getSearchText(long taskid, long menuid, List<TablelistcolumnInfo> colinfoItems) {
		MenutableInfoService menutableInfoService = SpringContext.getApplicationContext().getBean(MenutableInfoService.class);
		String searchText = null;
		//検索文字列表示
    	List<ListEditDto> searchData = loginDataDto.getMenuSearchDatas().get(menuid);
    	if (searchData != null) {
    		StringBuffer buff = new StringBuffer(" "+lang.__("attribute in search")+"：[");
    		boolean search = false;
    		for (int i = 0; i < searchData.size(); i++) {
    			ListEditDto dto = searchData.get(i);
    			if (StringUtil.isNotEmpty(dto.value)) {
    				String[] cols = dto.id.split(":");
    				String name = "";
    				String form = "=";
    				int gid = Integer.parseInt(cols[3]);
    				if (gid == 1 || gid == 2 || gid == -1 || gid == -2) form = "<=";
    				for (TablelistcolumnInfo colinfo : colinfoItems) {
    					if (colinfo.attrid.equals(cols[1])) {
    						name = colinfo.name;
    						break;
    					}
    				}
    				if (StringUtil.isEmpty(name) && colinfoItems.size() > 0) {
    					MenutableInfo tableInfo = menutableInfoService.findById(colinfoItems.get(0).menutableinfoid);
    					TablelistcolumnInfoService tablelistcolumnInfoService = SpringContext.getApplicationContext().getBean(TablelistcolumnInfoService.class);
						List<TablelistcolumnInfo> infos = tablelistcolumnInfoService.findByMenutaskidAndTablemasterinfoidAndAttrid(taskid, tableInfo.tablemasterinfoid, cols[1]);
						if (infos.size() > 0)
							name = infos.get(0).name;
    				}
    				String value = dto.value;
    				if (gid == 1 || gid == -2)
    					buff.append(" ").append(value).append(form).append(name);
    				else
    					buff.append(" ").append(name).append(form).append(value);
    				search = true;
    			}
    		}
    		buff.append("]");
    		if (search)
    			searchText = buff.toString();
    	}
		return searchText;
	}

	/**
	 * 履歴ソートクラス
	 */
	public class HistoryComparator implements Comparator<BeanMap> {
		@Override
		public int compare(BeanMap s1, BeanMap s2) {
			Timestamp t1 = (Timestamp)s1.get("timeFrom");
			Timestamp t2 = (Timestamp)s2.get("timeFrom");
			long d1 = 0; if (t1 != null) d1 = t1.getTime();
			long d2 = 0; if (t2 != null) d2 = t2.getTime();
			if (d1 > d2) return -1;
			if (d1 < d2) return 1;

			return 0;
		}
	}

	/**
	 * 10進座標文字列を60進文字列に変更。
	 * @param value
	 * @return 60進文字列
	 */
	public String to60Digree(String value) {
		if (StringUtil.isEmpty(value)) return value;

		try {
			int idx = value.indexOf(',');
			String lon = value.substring(1, idx);
			String lat = value.substring(idx+1, value.length()-1);

			double dlon = Double.parseDouble(lon);
			double dlat = Double.parseDouble(lat);

			DecimalFormat df = new DecimalFormat("0.0");
			int hx = (int)dlon;
			int mx = (int)((dlon-hx)*60);
			String sx = df.format(((dlon-hx)*60-mx)*60);
			int hy = (int)dlat;
			int my = (int)((dlat-hy)*60);
			String sy = df.format(((dlat-hy)*60-my)*60);
			String xy = "("+hx+"°"+mx+"'"+sx+" "+hy+"°"+my+"'"+sy+")";

			return xy;
		} catch (Exception e) {e.printStackTrace();}

		return "";
	}
}
