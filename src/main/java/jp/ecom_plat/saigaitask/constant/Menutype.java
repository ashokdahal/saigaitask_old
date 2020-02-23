/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ecom_plat.saigaitask.action.page.DisastersummaryGeneralizationAction;
import jp.ecom_plat.saigaitask.action.page.Report4formGeneralizationAction;
import jp.ecom_plat.saigaitask.action.page.StationGeneralizationAction;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

import org.seasar.framework.util.tiger.Pair;

/**
 * メニュータイプ
 */
@SuppressWarnings("serial")
public class Menutype {

	/**
	 * ページ切替ボタンリストのマップ.
	 * ページ切替ボタンリストをページタイプにマッピング.
	 * key ページタイプ
	 * value ページ切替ボタンリスト List<Pair<String, String>>>
	 */
	public static final Map<Integer, List<Pair<String, String>>> buttonsMap = new HashMap<Integer, List<Pair<String, String>>>();

	/** リスト（地図有） */
	public static Integer LIST_WITH_MAP = 1;
	/** リスト（地図無） */
	public static Integer LIST = 2;
	/** 地図（リスト有） */
	public static Integer MAP_WITH_LIST = 3;
	/** 地図（リスト無） */
	public static Integer MAP = 4;
	/** 要請 */
	public static Integer REQUEST = 5;
	/** 4号様式 */
	public static Integer REPORT_4FORM = 6;
	/** 公共情報コモンズ（緊急速報メール） */
	public static Integer PUBLICCOMMONS_MAIL = 7;
	/** 公共情報コモンズ（メディア）：避難勧告 */
	public static Integer PUBLICCOMMONS_MEDIA_REFUGE = 8;
	/** facebook */
	public static Integer FACEBOOK = 9;
	/** twitter */
	public static Integer TWITTER = 10;
	/** 職員参集 */
	public static Integer ASSEMBLE = 11;
	/** 公共情報コモンズ（メディア）：避難所 */
	public static Integer PUBLICCOMMONS_MEDIA_SHELTER = 12;
	/** 対応履歴
	 * @deprecated
	 **/
	public static Integer HISTORY = 13;
	/** 監視観測 */
	public static Integer OBSERVE = 14;
	/** 被災集計 */
	public static Integer DISASTER_SUMMARY = 15;
	/** Eコミグループウェア周知 */
	public static Integer ECOMGWPOST = 16;
	/** 通知履歴 **/
	public static Integer MAILHISTORY = 17;
	/** 4号様式の集計・総括 */
	public static Integer REPORT_4FORM_GENERALIZATION = 18;
	/** 体制の集計・総括 */
	public static Integer STATION_GENERALIZATION = 19;
	/** 公共情報コモンズ（メディア）：おしらせ */
	public static Integer PUBLICCOMMONS_MEDIA_GENERAL_INFORMATION = 20;
	/** 公共情報コモンズ（メディア）：イベント情報 */
	public static Integer PUBLICCOMMONS_MEDIA_EVENT = 21;
	/** 公共情報コモンズ（メディア）：被害情報 */
	public static Integer PUBLICCOMMONS_MEDIA_DAMAGE_INFORMATION = 22;
	/** 公共情報コモンズ（メディア）：本部設置状況 */
	public static Integer PUBLICCOMMONS_MEDIA_ANTIDISASTER_HEADQUARTER = 23;
	/** 被災集計の総括表 */
	public static Integer DISASTER_SUMMARY_GENERALIZATION = 24;
	/** 投稿写真の振り分け */
	public static Integer POSTING_PHOTO = 25;
	/** エクセル帳票 */
	public static Integer EXCELLIST = 26;

	private static SaigaiTaskDBLang lang = new SaigaiTaskDBLang("");

	static {
		// @param first ページタイプ
		// @param second ボタン名称
		final Pair<String, String> listButton = new Pair<String, String>("list", lang.__("List<!--2-->"));
		final Pair<String, String> mapButton = new Pair<String, String>("map", lang.__("Map<!--2-->"));
		final Pair<String, String> requestButton = new Pair<String, String>("request", lang.__("Request"));
		final Pair<String, String> reportButton = new Pair<String, String>("report4form", lang.__("Report No. 4"));
		final Pair<String, String> pcommonsmailButton = new Pair<String, String>("pcommonsmail", lang.__("L-Alert e-mail"));
		final Pair<String, String> pcommonsmediaRefugeButton = new Pair<String, String>("pcommonsmedia", lang.__("L-Alert media"));
		final Pair<String, String> facebookButton = new Pair<String, String>("facebook", "FACEBOOK");
		final Pair<String, String> twitterButton = new Pair<String, String>("twitter", "TWITTER");
		final Pair<String, String> assembleButton = new Pair<String, String>("assemble", lang.__("Assemble staff"));
		final Pair<String, String> pcommonsmediaShelterButton = new Pair<String, String>("pcommonsmedia", lang.__("L-Alert media"));
		final Pair<String, String> historyButton = new Pair<String, String>("history", lang.__("Response history"));
		final Pair<String, String> observButton = new Pair<String, String>("observe", lang.__("Monitoring and observation"));
		final Pair<String, String> disastersummaryButton = new Pair<String, String>("disastersummary", lang.__("Disaster summary"));
		final Pair<String, String> ecomgwpostButton = new Pair<String, String>("ecomgwpost", "ECOMGWPOST");
		final Pair<String, String> mailhistoryButton = new Pair<String, String>("mailhistory", lang.__("Notice history"));
		final Pair<String, String> reportGeneralizationButton = new Pair<String, String>(Report4formGeneralizationAction.PAGE_TYPE, lang.__("Summary of No.4 format"));
		final Pair<String, String> stationGeneralizationButton = new Pair<String, String>(StationGeneralizationAction.PAGE_TYPE, lang.__("Summary of system"));
		final Pair<String, String> pcommonsmediaEventButton = new Pair<String, String>("pcommonsmedia", lang.__("L-Alert media"));
//		final Pair<String, String> pcommonsmediacivilProtectionButton = new Pair<String, String>("pcommonsmedia", "公共コモンズメディア");
		final Pair<String, String> pcommonsmediaDamageInformationButton = new Pair<String, String>("pcommonsmedia", lang.__("L-Alert media"));
		final Pair<String, String> pcommonsmediaGeneralInformationButton = new Pair<String, String>("pcommonsmedia", lang.__("L-Alert media"));
		final Pair<String, String> disastersummaryGeneralizationButton = new Pair<String, String>(DisastersummaryGeneralizationAction.PAGE_TYPE, lang.__("Summary table of disaster"));
		final Pair<String, String> pcommonsmediaAntidisasterHeadquarterButton = new Pair<String, String>("pcommonsmedia", lang.__("L-Alert media"));
		final Pair<String, String> postingphotoButton = new Pair<String, String>("postingphoto", lang.__("Posted photo assortment"));
		final Pair<String, String> excellistButton = new Pair<String, String>("excellist", lang.__("Excel list"));

		// メニュータイプに対応するページ切替ボタンを初期化
		buttonsMap.put(LIST_WITH_MAP, new ArrayList<Pair<String, String>>(){{
			add(listButton);
			add(mapButton);
		}});
		buttonsMap.put(LIST, new ArrayList<Pair<String, String>>(){{
			add(listButton);
		}});
		buttonsMap.put(MAP_WITH_LIST, new ArrayList<Pair<String, String>>(){{
			add(mapButton);
			add(listButton);
		}});
		buttonsMap.put(MAP, new ArrayList<Pair<String, String>>(){{
			add(mapButton);
		}});
		buttonsMap.put(REQUEST, new ArrayList<Pair<String, String>>(){{
			add(requestButton);
		}});
		buttonsMap.put(REPORT_4FORM, new ArrayList<Pair<String, String>>(){{
			add(reportButton);
		}});
		buttonsMap.put(PUBLICCOMMONS_MAIL, new ArrayList<Pair<String, String>>(){{
			add(pcommonsmailButton);
		}});
		buttonsMap.put(PUBLICCOMMONS_MEDIA_REFUGE, new ArrayList<Pair<String, String>>(){{
			add(pcommonsmediaRefugeButton);
		}});
		buttonsMap.put(FACEBOOK, new ArrayList<Pair<String, String>>(){{
			add(facebookButton);
		}});
		buttonsMap.put(TWITTER, new ArrayList<Pair<String, String>>(){{
			add(twitterButton);
		}});
		buttonsMap.put(ASSEMBLE, new ArrayList<Pair<String, String>>(){{
			add(assembleButton);
		}});
		buttonsMap.put(PUBLICCOMMONS_MEDIA_SHELTER, new ArrayList<Pair<String, String>>(){{
			add(pcommonsmediaShelterButton);
		}});
		buttonsMap.put(HISTORY, new ArrayList<Pair<String, String>>(){{
			add(historyButton);
		}});
		buttonsMap.put(OBSERVE, new ArrayList<Pair<String, String>>(){{
			add(observButton);
		}});
		buttonsMap.put(DISASTER_SUMMARY, new ArrayList<Pair<String, String>>(){{
			add(disastersummaryButton);
		}});
		buttonsMap.put(ECOMGWPOST, new ArrayList<Pair<String, String>>(){{
			add(ecomgwpostButton);
		}});
		buttonsMap.put(MAILHISTORY, new ArrayList<Pair<String, String>>(){{
			add(mailhistoryButton);
		}});
		buttonsMap.put(REPORT_4FORM_GENERALIZATION, new ArrayList<Pair<String, String>>(){{
			add(reportGeneralizationButton);
		}});
		buttonsMap.put(STATION_GENERALIZATION, new ArrayList<Pair<String, String>>(){{
			add(stationGeneralizationButton);
		}});
		buttonsMap.put(PUBLICCOMMONS_MEDIA_EVENT, new ArrayList<Pair<String, String>>(){{
			add(pcommonsmediaEventButton);
		}});
		buttonsMap.put(PUBLICCOMMONS_MEDIA_DAMAGE_INFORMATION, new ArrayList<Pair<String, String>>(){{
			add(pcommonsmediaDamageInformationButton);
		}});
		buttonsMap.put(PUBLICCOMMONS_MEDIA_GENERAL_INFORMATION, new ArrayList<Pair<String, String>>(){{
			add(pcommonsmediaGeneralInformationButton);
		}});
		buttonsMap.put(DISASTER_SUMMARY_GENERALIZATION, new ArrayList<Pair<String, String>>(){{
			add(disastersummaryGeneralizationButton);
		}});
		buttonsMap.put(PUBLICCOMMONS_MEDIA_ANTIDISASTER_HEADQUARTER, new ArrayList<Pair<String, String>>(){{
			add(pcommonsmediaAntidisasterHeadquarterButton);
		}});
		buttonsMap.put(POSTING_PHOTO, new ArrayList<Pair<String, String>>(){{
			add(postingphotoButton);
		}});
		buttonsMap.put(EXCELLIST, new ArrayList<Pair<String, String>>(){{
			add(excellistButton);
		}});
	}
}
