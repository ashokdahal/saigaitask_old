/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.util.ArrayList;
import java.util.List;

import org.seasar.framework.util.ResourceUtil;
import org.seasar.framework.util.tiger.Pair;

import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MapmasterInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.form.page.AbstractPageForm;
import jp.ecom_plat.saigaitask.form.page.ListForm;
import jp.ecom_plat.saigaitask.form.page.MapForm;
import jp.ecom_plat.saigaitask.util.Config;
import jp.ecom_plat.saigaitask.util.Constants;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * 基本機能のDTOクラス.
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class PageDto {
	@javax.annotation.Resource protected SaigaiTaskDBLang lang;

	/** 自分以外のメニューフラグ */
	private boolean othermenu;

	/** 閲覧モード */
	private boolean viewMode;

	/** 全画面表示モード */
	private boolean fullscreen;

	/** 全画面表示モード有効/無効フラグ */ 
	private boolean enableFullscreen;

	/**
	 * html head でインクルードするJSPを指定する.
	 * /WEB-INF/view/page/{pagetype}/head.jsp
	 * があれば自動で設定される.
	 */
	private String includeHeadJSP;

	/** タスクリストに表示するプロセスマスタ */
	private List<MenuprocessInfo> menuprocessInfos;

	/** 現在のプロセス */
	private MenuprocessInfo menuprocessInfo;

	/** 現在のタスク */
	private MenutaskInfo menutaskInfo;

	/** 現在のメニュー */
	private MenuInfo menuInfo;

	/** アラーム間隔(ミリ秒) */
	private int alarmInterval = 120000;

	/** クリアリングハウス検索間隔(ミリ秒) */
	private int metadataInterval = 120000;

	/** アラームオープンフラグ */
	private boolean isAlarmOpen = false;

	/** 自治体情報 */
	private LocalgovInfo localgovInfo;

	/** 班情報 */
	private GroupInfo groupInfo;

	/** ログイン名 */
	private String loginName ;

	/** ログイン中の記録データ */
	private TrackData trackData;

	/** 災害名 */
	private String saigaiName;

	/** 災害中フラグ */
	private boolean isSaigai = false;

	/** 体制名 */
	private String stationName;

	/** 自動発報状態 */
	private String autoName;

	/** 避難勧告状態 */
	private String issueName;

	/** 本部会議のタスクID */
	private long headofficeTaskId = 0;

	/** 意思決定支援フラグ */
	private boolean isDecisionSupport = false;

	/** 表示データ時間(ISO8601) */
	private String time;

	/** timeslider設定 */
	private JSONObject timesliderConf;

	/** 地図マスター情報 */
	private MapmasterInfo mapmasterInfo;

	/** ecommapURL */
	private String ecommapURL = ResourceUtil.getProperties("SaigaiTask.properties").getProperty("ECOMIMAPURL");

	/** JSON取り込みエラーチェック間隔(ミリ秒) */
	private int jsonimportCheckInterval = 0;

	/**
	 * timeslider設定に追加
	 * @param name 名称
	 * @param layerId 追加するレイヤID
	 */
	public void addTimesliderConf(String name, String layerId) {
		if(timesliderConf==null) timesliderConf = new JSONObject();
		timesliderConf.put("name", name);
		timesliderConf.put("layerId", layerId);
	}

	/**
	 * ページ切替ボタン.
	 * 2つのページを切り替える.
	 * 主に リスト/地図 の切り替えボタンを想定。
	 * @param first ページタイプ(listとかmapとか)
	 * @param second ボタン名称
	 */
	private Pair<String, String> pagetoggleButton;

	/**
	 * リクエストパラメータのJSONオブジェクト
	 */
	private JSONObject formJSON;

	/**
	 * リクエストパラメータのJSONオブジェクト変換
	 * @param pageForm
	 */
	public void setFormJSONFromForm(AbstractPageForm pageForm) {
		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnorePublicFields(true);
		formJSON = JSONObject.fromObject(pageForm, jsonConfig);

		// org.json.JSONObject の変換がうまくできないので.
		{
			String conditionValue = null;
			String insertFeatureData = null;
			if(pageForm instanceof MapForm) {
				MapForm mapForm = (MapForm) pageForm;
				if(mapForm.conditionValue!=null) {
					conditionValue = mapForm.conditionValue.toString();
				}
				if(mapForm.insertFeatureData!=null) {
					insertFeatureData = mapForm.insertFeatureData.toString();
				}
			}
			if(pageForm instanceof ListForm) {
				ListForm listForm = (ListForm) pageForm;
				if(listForm.conditionValue!=null) {
					conditionValue = listForm.conditionValue.toString();
				}
			}
			formJSON.put("conditionValue", conditionValue);
			formJSON.put("insertFeatureData", insertFeatureData);
		}
	}

	/**
	 * 最終更新日のフォーマット
	 */
	private String updateTimeFormat;

	/**
	 * 最終更新日
	 */
	private String updateTime;
	/** 平時利用モード */
	private boolean usual = false;

	/** タブリスト */
	private List<PageDto.Tab> tabs = new ArrayList<PageDto.Tab>();

	/**
	 * コンテンツのタブ情報
	 */
	public static class Tab {
		/** タイトル */
		public String title;
		/** リンクURL */
		public String url;
	}

	private String text_align = Config.getString("TEXT_ALIGN");
	private String number_align = Config.getString("NUMBER_ALIGN");
	private int pagerow = Integer.parseInt(Config.getString("PAGEROW", "100"));

	/** 地図表示／非表示 */
	private String mapVisible;

	/** ヘッダーボタンの情報 */
	private String headerbutton = ResourceUtil.getProperties("SaigaiTask.properties").getProperty("HEADER_BUTTON");

	/** 地図画面のみ表示 */
	private Boolean contentOnly = false;
	
	private String earthQuakeGeoServer;

	/**
	 * デフォルト値をセットする。
	 */
	public void setDefault() {
		saigaiName = lang.__("No disaster");
		stationName = lang.__("No system");
		autoName = Constants.ISSUE_AUTO_VALID();
		issueName = Constants.ISSUE_CANCEL_NAME();
		updateTimeFormat = lang.__("MMM.d,yy 'at' H:m");
		earthQuakeGeoServer = Config.getEarthquakeGeoserverWMS();
	}
}
