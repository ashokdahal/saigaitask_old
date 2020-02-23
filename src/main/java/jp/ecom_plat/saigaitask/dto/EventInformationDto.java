/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.io.Serializable;

/**
 * イベント情報
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class EventInformationDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/** タイトル */
	public String title;

	/** 本文 */
	public String text;

	/** 開催場所 */
	public String eventArea;

	/** 開催開始日 */
	public String eventFrom;

	/** 開催終了日 */
	public String eventTo;

	/** 参加料金 */
	public String eventFee;

	/** 告知URI */
	public String eventNotificationUri;

	/** ファイルURI */
	public String eventFileUri;

	/** MIMEタイプ */
	public String mimeType;

	/** メディアタイプ */
	public String mediaType;

	/** ファイルタイトル */
	public String eventFileCaption;

	/** 情報識別区分*/
	public String disasterInformationType;

	/** 希望公開終了日時  */
	public String validDateTime;

	/** 更新種別  */
	public String distributiontype;

	/** ドキュメントID  */
	public String documentid;

	/** 版数  */
	public Long documentRevision;

	/** メールタイトル */
	public String emailtitle;

	/** 送信本文 */
	public String content;
}
