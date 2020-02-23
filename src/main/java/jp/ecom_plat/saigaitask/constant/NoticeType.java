/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.constant;

/**
 * 通知先種別
 */
public class NoticeType {

	/** メール */
	public static final Integer MAIL = 1;
	/** アラート */
	public static final Integer ALERT = 2;
	/** テロップ */
	public static final Integer TELOP = 3;
	/** "公共情報ｺﾓﾝｽﾞ（緊急速報ﾒｰﾙ）発信" */
	public static final Integer COMMONSMAIL = 4;
	/** "公共情報ｺﾓﾝｽﾞ（ﾒﾃﾞｨｱ）発信" */
	public static final Integer COMMONSMADIA = 5;
	/** "facebook" */
	public static final Integer FACEBOOK = 6;
	/** "twitter" */
	public static final Integer TWITTER = 7;
	/** "Ecomグループウェア" */
	public static final Integer ECOMGW = 8;
}
