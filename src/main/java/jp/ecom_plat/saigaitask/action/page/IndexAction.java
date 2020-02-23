/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.ecom_plat.saigaitask.action.page;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import jp.ecom_plat.map.db.FeatureDB;
import jp.ecom_plat.map.db.FeatureResult;
import jp.ecom_plat.map.db.LayerInfo;
import jp.ecom_plat.map.db.MapDB;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.constant.Localgovtype;
import jp.ecom_plat.saigaitask.constant.Menutype;
import jp.ecom_plat.saigaitask.entity.db.AlarmmessageData;
import jp.ecom_plat.saigaitask.entity.db.AlarmshowData;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.TablemasterInfo;
import jp.ecom_plat.saigaitask.entity.db.TelopData;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.entity.db.TracktableInfo;
import jp.ecom_plat.saigaitask.form.page.PageForm;
import jp.ecom_plat.saigaitask.service.JsonImportService;
import jp.ecom_plat.saigaitask.service.MetadataService;
import jp.ecom_plat.saigaitask.service.db.AlarmmessageDataService;
import jp.ecom_plat.saigaitask.service.db.AlarmshowDataService;
import jp.ecom_plat.saigaitask.service.db.AutocompleteDataService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MapmasterInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskmenuInfoService;
import jp.ecom_plat.saigaitask.service.db.TelopDataService;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.FileUtil;
import jp.ecom_plat.saigaitask.util.StringUtil;
import jp.ecom_plat.saigaitask.util.TimeUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.seasar.framework.util.tiger.Pair;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * パラメータに応じてフォワード先を振り分けるアクションクラスです.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/page")
public class IndexAction extends AbstractPageAction {

	protected PageForm pageForm;
	//@Resource protected MenuInfoService menuInfoService;

	/** テロップデータサービス */
	@Resource protected TelopDataService telopDataService;
	/** アラームメッセージデータサービス */
	@Resource protected AlarmmessageDataService alarmmessageDataService;
	/** アラーム表示データサービス */
	@Resource protected AlarmshowDataService alarmshowDataService;
	/** 班情報サービス */
	@Resource protected GroupInfoService groupInfoService;
	/** メタデータサービス */
	@Resource protected MetadataService metadataService;
	@Resource
	protected AutocompleteDataService autocompleteDataService;
	@Resource
	protected MapmasterInfoService mapmasterInfoService;
	@Resource
	protected MenutaskmenuInfoService menutaskmenuInfoService;
	@Resource
	protected JsonImportService jsonImportService;

	/** オートコンプリート */
	public Map<String, String[]> autocompleteDataMap = new HashMap<String, String[]>();

	/**
	 * 各メニューのActionにフォワードする
	 * @return フォワード先、各メニューへ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/page","/page/index"})
	public String index(Map<String,Object>model, @Valid @ModelAttribute PageForm pageForm, BindingResult result) {
		this.pageForm = pageForm;
		return forward();
	}

	/**
	 * リクエストパラメータのpagetypeに対応するActionにフォワードする．
	 * @return フォワード先、各メニュー
	 */
	private String forward() {
		MenuInfo menuInfo = null;
		MenutaskInfo menutaskInfo = null;

		// 地図表示／非表示対応
		// セッションから地図表示／非表示フラグを取得
		String mapVisible = (String)request.getSession().getAttribute(Constants.SESSIONPARAM_MAPVISIBLE);
		if(StringUtils.isEmpty(mapVisible)){
			mapVisible = Constants.MAPVISIBLE_VISIBLE;
			request.getSession().setAttribute(Constants.SESSIONPARAM_MAPVISIBLE, mapVisible);
		}
		pageDto.setMapVisible(mapVisible);

		// 災害情報のチェック
		checkTrackData();

		// プロセスリストを初期化
		initMenuprocessInfos();

		// 各リクエストからメニューを取得
		// メニュー指定のリクエスト
		if(pageForm.menutaskid!=null && pageForm.menuid!=null) {
			menuInfo = menuInfoService.findByNotDeletedId(pageForm.menuid);
			menutaskInfo = menutaskInfoService.findByNotDeletedId(pageForm.menutaskid);
		}
		// タスク指定のリクエスト
		else if(pageForm.menutaskid!=null) {
			// デフォルトのメニューを取得
			List<MenuInfo> menuInfos =
					menuInfoService.findByMenutaskinfoidOrderbyDisporder(pageForm.menutaskid);
			//menuInfoService.applyMenuInfo(menuMasters, loginDataDto.getGroupid(), false);
			menutaskInfo = menutaskInfoService.findById(pageForm.menutaskid);
			if(0<menuInfos.size()) {
				menuInfo = menuInfos.get(0);
			}
		}
		// なにも指定なしのリクエスト
		else {
			// 一番最初のメニューを表示する
			if(0<pageDto.getMenuprocessInfos().size()) {
				MenuprocessInfo menuprocessInfo = pageDto.getMenuprocessInfos().get(0);
				if(0<menuprocessInfo.menutaskInfos.size()) {
					menutaskInfo = menuprocessInfo.menutaskInfos.get(0);
					if(0<menutaskInfo.menutaskmenuInfos.size()) {
						MenutaskmenuInfo taskmenu = menutaskInfo.menutaskmenuInfos.get(0);
						menuInfo = taskmenu.menuInfo;
					}
				}
			}
		}

		// メニューが存在するかチェック
		if(menuInfo==null || menutaskInfo==null) {
			throw new ServiceException(lang.__("Menu has not been set."));
		}

		// フォワードするページ種別を取得
		// リクエストパラメータを設定してフォワード
		pageForm.menuid = menuInfo.id;
		pageForm.menutaskid = menutaskInfo.id;

		// フォワード先ページをチェック
		List<Pair<String, String>> buttons = Menutype.buttonsMap.get(menuInfo.menutypeid);
		if(buttons!=null && 0<buttons.size()) {
			// ページの指定がない場合は、最初のメニューを表示する
			if(StringUtils.isEmpty(pageForm.pagetype)) {
				Pair<String, String> button = buttons.get(0);
				pageForm.pagetype = button.getFirst();

				// 地図表示／非表示対応
				// 地図表示／非表示フラグが非表示かつ、地図（リスト有）ページが指定された場合は
				// リストページに遷移させる
				if(Constants.MAPVISIBLE_UNVISIBLE.equals(mapVisible)){
					if(menuInfo.menutypeid.equals(Menutype.MAP_WITH_LIST)){
						if(pageForm.pagetype.equals("map")){
							pageForm.pagetype = "list";
						}
					}
				}
			}
			// ページの指定があれば、正しいかチェックする
			else {
				// どちらかのボタンに一致しなければ不正とする
				boolean invalid = true;
				for(Pair<String, String> button : buttons) {
					// 一致すれば正しい
					if((button.getFirst().equals(pageForm.pagetype))) {
						invalid = false;
						break;
					}
				}
				// buttons にない場合は、AbstractPageAction を継承しているかチェックする(externallistを許可)
				if(invalid) {
					try {
						Class<?> clazz = Class.forName(AbstractPageAction.class.getPackage().getName()+"."+org.seasar.framework.util.StringUtil.capitalize(pageForm.pagetype)+"Action");
						if(clazz.getSuperclass()==AbstractPageAction.class) {
							invalid = false;
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
				// pagetype 不正処理
				if(invalid) {
					String invalidPagetype = pageForm.pagetype;
					pageForm.pagetype = null;
					throw new ServiceException(lang.__("Page type (pagetype = \"{0}\") is invalid.", invalidPagetype));
				}
			}
			request.setAttribute("page_pageForm", pageForm);
			return "forward:/page/"+pageForm.pagetype;
		}
		throw new ServiceException(lang.__("Displayable page type is not found."));
	}

	/**
	 * アラームを開く処理
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/alarmopen")
	@ResponseBody
	public String alarmopen() {
		loginDataDto.setAlarmOpen(!loginDataDto.isAlarmOpen());
		pageDto.setAlarmOpen(loginDataDto.isAlarmOpen());

		return null;
	}

	/**
	 * アラーム一覧を表示する処理
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/alarm")
	@ResponseBody
	public String alarm() {

		StringBuffer msg = new StringBuffer("<ul>");
		List<AlarmmessageData> list = null;
		if (!loginDataDto.isUsual())
			list = alarmmessageDataService.findByLocalgovInfoIdOrTrackdataIdAndGroupId(loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid(), loginDataDto.getGroupid());
		else
			list = alarmmessageDataService.findByLocalgovInfoIdAndUnitId(loginDataDto.getLocalgovinfoid(), loginDataDto.getUnitid());
		for (AlarmmessageData alarm : list) {
			String message = alarm.message;
			if(message!=null) message = StringUtil.convURLLink(message, "target=\"_blank\"");
			msg.append("<li><a href=\"#\" id=\"showAlarmDetail_"+alarm.id+"\" >" + StringUtil.h(message) + "</a></li>");
		}
		msg.append("</ul>");
		//ResponseUtil.write(msg.toString(), "text/html");
		// TODO:どこかにリンクする？
		// ResponseUtil.write("<ul>"+
		// "<li><a href=\"#\">渡瀬橋の水位が第三次配備体制の基準に達しました。</a></li>"+
		// "<li><a href=\"#\">渡瀬橋の水位が第三次配備体制の基準に達しました。</a></li>"+
		// "<li><a href=\"#\">渡瀬橋の水位が第三次配備体制の基準に達しました。</a></li>"+
		// "</ul>");
		return msg.toString();
		//return null;
	}

	/**
	 * 新しいアラームがあれば返す処理
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/newalert", produces="application/json")
	@ResponseBody
	public String newalert() {
		if (request == null || request.getSession() == null)
			return null;

		String sessionid = request.getSession().getId();

		StringBuffer msg = new StringBuffer();
		msg.append("{\"item\":[");

		List<AlarmmessageData> alarmlist = null;
		if (!loginDataDto.isUsual())
			alarmlist = alarmmessageDataService.findByRecentShowData(loginDataDto.getLocalgovinfoid(), loginDataDto.getTrackdataid(), loginDataDto.getGroupid());
		else
			alarmlist = alarmmessageDataService.findByRecentShowData(loginDataDto.getLocalgovinfoid(), loginDataDto.getUnitid());
		int size = alarmlist.size();
		int cnt = 0;
		for (AlarmmessageData alarm : alarmlist) {
			if (alarm != null) {
				// 複数タブを開いている場合は複数件登録される場合がある.
				List<AlarmshowData> alarmshowDatas = alarmshowDataService.findByAlarmmessageDataIdAndSesssionId(alarm.id, sessionid);
				if (0<alarmshowDatas.size())// 表示済み
					alarm = null;
			}

			if (alarm != null) {
				msg.append("{");
				msg.append("\"id\": \"");
				msg.append(alarm.id);
				msg.append("\",\"message\": \"");
				// 改行コードが入ったJSONをJavaScriptでは受け取れないため、
				// メッセージ本文の改行コード(\n\r, \n)をエスケープした"\\\\n"に変換する
				msg.append(StringUtil.h(alarm.message.replaceAll("\n", "\\\\n").replaceAll("\r", "")));
				msg.append("\",\"type\": \"");
				msg.append(alarm.messagetype);
				msg.append("\"");
				if (alarm.duration != null && alarm.duration > 0) {
					msg.append(",\"autoClose\": \"true\"");
					msg.append(",\"duration\": \"");
					msg.append(alarm.duration);
					msg.append("\"");

				}
				Timestamp time = alarm.registtime;
				String timeStr = TimeUtil.convertTimestamp2(time.toString());
				msg.append(",\"time\": \"");
				msg.append(timeStr);
				msg.append("\"");

				if(cnt==size-1){
					msg.append("}");
				}else{
					msg.append("},");
				}

				AlarmshowData show = new AlarmshowData();
				show.alarmmessagedataid = alarm.id;
				show.sessionid = sessionid;
				// TODO:本当は手動で消したかどうか判断出来ると良い
				show.stop = true;
				alarmshowDataService.insert(show);

				//ResponseUtil.write(msg.toString());
				//break;// メッセージは一つずつ
			}
			cnt++;
		}
		msg.append("]}");
		//ResponseUtil.write(msg.toString(), "application/json");
		return msg.toString();

		//return null;
	}

	/**
	 * アラームの詳細を表示する処理
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/alarmdetail")
	@ResponseBody
	public String alarmdetail(Map<String,Object>model, @Valid @ModelAttribute PageForm pageForm) {

		logger.info("alarmdetail : "+pageForm.id);

		//IDに該当するAlarmmessageDataの取得
		AlarmmessageData alarm = alarmmessageDataService.findById(Long.parseLong(pageForm.id));

		//送信班名の取得
		String gName = lang.__("(Auto)<!--2-->");
		if(alarm.sendgroupid!=null && alarm.sendgroupid>0){
			GroupInfo gInfo = groupInfoService.findById(alarm.sendgroupid);
			gName = gInfo.name;
			// 別の自治体の班の場合は、自治体名をカッコ書きで表示する
			if(gInfo.localgovinfoid.equals(loginDataDto.getLocalgovinfoid())==false) {
				StringBuilder sb = new StringBuilder();
				LocalgovInfo localgovInfo = gInfo.localgovInfo;
				// 都道府県名を追加
				if(org.seasar.framework.util.StringUtil.isNotEmpty(localgovInfo.pref)) {
					sb.append(localgovInfo.pref);
				}
				// 都道府県ではない場合、市町村名があれば追加
				if(Localgovtype.PREF.equals(localgovInfo.localgovtypeid)==false || org.seasar.framework.util.StringUtil.isNotEmpty(localgovInfo.city)) {
					sb.append(localgovInfo.city);
				}
				// 自治体名称があればカッコ書きで追加
				if(0<sb.length()) {
					gName += "("+sb.toString()+")";
				}
			}
		}
		//通知先
		String noticeto = StringUtils.isEmpty(alarm.noticeto) ? lang.__("(All)") : alarm.noticeto;
		//再現URL
		String noticeurl = StringUtils.isEmpty(alarm.noticeurl) ? lang.__("(None)") : alarm.noticeurl;

		//表示文字列の整形
		StringBuffer msg = new StringBuffer("<table border=\"0\" cellpadding=\"3\" cellspacing=\"2\" id=\"alarmTable\" class=\"tablesorter\" style=\"margin-left:10px;\">");
		msg.append("<tr><th width=\"100px\">" + lang.__("Alarm date and time") + "</th><td>"+alarm.registtime+"</td></tr>");
		msg.append("<tr><th width=\"100px\">" + lang.__("Send group") + "</th><td>"+gName+"</td></tr>");
		msg.append("<tr><th width=\"100px\">" + lang.__("Notice destination") + "</th><td>"+ noticeto +"</td></tr>");
		msg.append("<tr><th width=\"100px\">" + lang.__("Re-appear URL") + "</th><td>"+ StringUtil.convURLLink(noticeurl, "target=\"_blank\"") +"</td></tr>");
		msg.append("<tr><th width=\"100px\">" + lang.__("Alarm body text") + "</th><td>"+StringUtil.convURLLink(StringUtil.h(alarm.message), "target=\"_blank\"")+"</td></tr>");
		msg.append("</table>");

		//出力
		//ResponseUtil.write(msg.toString());
		return msg.toString();
		//return null;
	}

	/**
	 * テロップの文字列を返す。
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/telopdata")
	@ResponseBody
	public String telopdata() {

		List<TelopData> telopList = telopDataService.findValidData(loginDataDto.getLocalgovinfoid());
		StringBuffer buff = new StringBuffer();
		for (TelopData data : telopList)
			buff.append(data.message).append("    ");

		//ResponseUtil.write(buff.toString());
		return buff.toString();

		//return null;
	}

	/**
	 * 自動発報の切り替え
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/autostart")
	@ResponseBody
	public String autostart() {

		LocalgovInfo gov = localgovInfoService.findById(loginDataDto.getLocalgovinfoid());
		gov.autostart = !gov.autostart;
		localgovInfoService.update(gov);
		if (gov.autostart) {
			//ResponseUtil.write(Constants.ISSUE_AUTO_VALID());
			return Constants.ISSUE_AUTO_VALID();
		}
		else {
			//ResponseUtil.write(Constants.ISSUE_AUTO_INVALID());
			return Constants.ISSUE_AUTO_INVALID();
		}

		//return null;
	}

	/**
	 * URL送信
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/seturl")
	public String seturl(@Valid @ModelAttribute PageForm pageForm) {
		if (loginDataDto != null)
			loginDataDto.setPreMenuUrl(pageForm.url);

		return null;
	}

	/**
	 * 新たに登録・更新されたメタデータを取得
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/newmetadata", produces="application/json")
	@ResponseBody
	public String newmetadata() {

		boolean isTraining = false;
		if(loginDataDto!=null && loginDataDto.getTrackdataid()!=0L) {
			TrackData trackData = trackDataService.findById(loginDataDto.getTrackdataid());
			isTraining = trackData!=null&&trackData.trainingplandataid!=null && trackData.trainingplandataid > 0;
		}

		String json = metadataService.getLatestMetadata(isTraining);
		// 改行を改行コードに変換
		//ResponseUtil.write(json.replaceAll("\n", "\\n"), "application/json","UTF-8");
		return json.replaceAll("\n", "\\n");//
		//return null;
	}

	/**
	 * オートコンプリートを表示する処理
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/tags/{layerid}/{attrid}")
	@ResponseBody
	public String tags(Map<String,Object>model, @Valid @ModelAttribute PageForm pageForm) {

		String value = request.getParameter("term");

		//オートコンプリート
		TracktableInfo ttbl = tracktableInfoService.findByTrackDataIdAndTablename(loginDataDto.getTrackdataid(), pageForm.layerid);
		long masterid = 0;
		if (ttbl == null) {
			MapmasterInfo mastermap = mapmasterInfoService.findByLocalgovInfoId(loginDataDto.getLocalgovinfoid());
			TablemasterInfo master = tablemasterInfoService.findByMapmasterInfoIdAndLayerId(mastermap.id, pageForm.layerid);
			masterid = master.id;
		}
		else
			masterid = ttbl.tablemasterinfoid;

		String[] values = autocompleteDataService.findArray(loginDataDto.getLocalgovinfoid(), loginDataDto.getGroupid(), masterid, pageForm.attrid);
		//autocompleteDataMap.put(attr.attrId, values);

		JSONArray ary = new JSONArray();
		for (String val : values) {
			if (!StringUtils.isEmpty(value)) {
				if (val.indexOf(value) == 0)
					ary.put(val);
			}
			else
				ary.put(val);
		}
		//ResponseUtil.write(ary.toString());
		return ary.toString();

		//return null;
	}

	/**
	 * 西暦から和暦に変換する.
	 * @return null(フォワードしない)
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/getUpdateTime", produces="application/json")
	@ResponseBody
	public String getUpdateTime(Map<String,Object>model, @Valid @ModelAttribute PageForm pageForm) {
		Timestamp timestamp = null;
		if(StringUtils.isNotEmpty(pageForm.timestamp)) {
			timestamp = Timestamp.valueOf(pageForm.timestamp.replace('/', '-'));
		}
		String updateTime = getUpdateTimeBy(timestamp);

		try{
			// 結果JSONを生成
			JSONObject result = new JSONObject();
			result.put("text", updateTime);

			// 出力の準備
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print( result.toString() );
			return result.toString();

		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * ファイルリストを返す。
	 * @return
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/filelist/{layerid}/{id}")
	@ResponseBody
	public String filelist(Map<String,Object>model, @Valid @ModelAttribute PageForm pageForm) {
		try{
			long fid = Long.parseLong(pageForm.id);
			//写真情報
			//JSONArray jary = MapDB.getMapDB().getFeatureFileList(pageForm.layerid, fid);
			LayerInfo layerInfo = MapDB.getMapDB().getLayerInfo(pageForm.layerid);
			Date[] timeParam = pageForm.timeParams();
			if(timeParam==null) timeParam = new Date[]{new Date()};
			Vector<FeatureResult.FileResult> results = FeatureDB.getFeatureFileList(layerInfo, fid, timeParam);
			JSONArray jary = new JSONArray();
			for(FeatureResult.FileResult result : results) {

				JSONArray values = new JSONArray();
				String url = result.url;
				if(StringUtils.isNotEmpty(url) && url.startsWith("http")==false) {
					url = Config.getEcommapURL().replaceAll("/$", "") + url;
				}
				if (url != null) url = url.replaceAll("\\+", "%20");
				values.put(url);
				values.put(result.title);
				// 画像以外のファイルの場合はアイコン表示
				String imgurl = url;
				boolean bimg = true;
				String ext = FileUtil.getFileExt(url);
				if (! ext.matches("(?i)(jpg|gif|png|jpeg)")) {
					imgurl = Config.getEcommapURL()+"map/map/fileicons/"+ext+".png";
					bimg = false;
				}

				values.put(imgurl);
				values.put(bimg);
				values.put(/*result.type*/"");
				//values.put(result.featureId); // for debug
				jary.put(values);
			}
			JSONObject jobj = new JSONObject();
			jobj.append("data", jary);

			// 出力の準備
			//response.setContentType("application/json");
			//response.setCharacterEncoding("UTF-8");
			//PrintWriter out = response.getWriter();
			//out.print( jobj.toString() );
			return jobj.toString();

		}catch(Exception e){
			e.printStackTrace();
			logger.error(e);
		}
		return null;
	}

	/**
	 * 地図表示／非表示切り替え
	 * @return null
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/switchMapVisible")
	@ResponseBody
	public String switchMapVisible(HttpServletRequest request) {
		if (request == null || request.getSession() == null){
			//ResponseUtil.write("failed".toString());
			return "failed";
//			/return null;
		}

		String mapVisible = (String)request.getSession().getAttribute(Constants.SESSIONPARAM_MAPVISIBLE);
		String newMapVisible;
		if(mapVisible == null){
			newMapVisible = Constants.MAPVISIBLE_VISIBLE;
		}else{
			if( Constants.MAPVISIBLE_VISIBLE.equals(mapVisible)){
				newMapVisible = Constants.MAPVISIBLE_UNVISIBLE;
			}else{
				newMapVisible = Constants.MAPVISIBLE_VISIBLE;
			}
		}
		request.getSession().setAttribute(Constants.SESSIONPARAM_MAPVISIBLE, newMapVisible);
		pageDto.setMapVisible(newMapVisible);

		//ResponseUtil.write("success".toString());
		return "success";
		//return null;
	}

	/**
	 * JSON取り込み機能のエラーを取得
	 * @return 
	 */
	private static final String JSONIMPORT_SESSION = "JSONIMPORT_ERROR";
	@org.springframework.web.bind.annotation.RequestMapping(value="/page/jsonimportstatus", produces="application/json")
	@ResponseBody
	public String jsonimportstatus() {
		String action = (String)request.getParameter("action");
		String error;
		switch (action) {
		
		// エラー情報を取得する
		case "get":
			error = jsonImportService.getError(loginDataDto.getLocalgovinfoid());
			JSONObject json = new JSONObject();
			try {
				// エラーがあった場合
				if (error != null) {
					// session に保存されているエラーと同一の場合は「エラーなし」の応答を返す
					if (!error.equals(session.getAttribute(JSONIMPORT_SESSION)))
						json.put("error", error);
				}
				// エラーがなかった場合
				else {
					session.removeAttribute(JSONIMPORT_SESSION);
				}
			} catch (JSONException e) {
			}
			return json.toString();

		// エラー情報を session に保存する
		case "done":
			error = (String)request.getParameter("error");
			session.setAttribute("JSONIMPORT_ERROR", error);
			return "{}";
			
		default:
			return "{}";
		}			
	}

}
