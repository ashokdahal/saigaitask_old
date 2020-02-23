/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;


/**
 * 掲示板のスレッドリストを表示するための Dto クラスです.
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class ThreadListDto {

	public static final String COLOR_BLACK = "#000000";
	public static final String COLOR_WHITE = "#FFFFFF";
	public static final String COLOR_GRAY = "#A0A0A0";
	public static final String COLOR_RED   = "#FF0000";
	public static final String COLOR_ORANGE = "#FF9900";
	public static final String COLOR_YERROW = "#FFFF00";

	/** スレッドのID */
	public Long id;
	/** スレッドのTitle */
	public String title;
	/** スレッドの優先度 */
	public int priority;
	/** スレッドの背景色 */
	public String bgColor;
	/** スレッドの文字色 */
	public String fontColor;
	/** スレッドの最終投稿日時 */
	public String latestTime;
	/** スレッドの作成日時 */
	//public String registTime;
	/** スレッドの未読数 */
	public int unRead;
	/** スレッドのStyle */
	public String threadStyle;
	/** スレッドの送信先一覧 */
	//public String sendtoMenber;
	/** スレッドの閉鎖 */
	//public boolean closed;
	/** スレッド作成グループ名 */
	//public String registGroupName;
}
