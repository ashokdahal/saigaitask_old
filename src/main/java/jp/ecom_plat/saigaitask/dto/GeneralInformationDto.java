/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.io.Serializable;


/**
 * おしらせ情報
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class GeneralInformationDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 分類 */
	public String division;

	/** 場所 */
	public String area;

	/** 本文 */
	public String text;

	/** タイトル */
	public String title;

	/** ファイルURI */
	public String fileUri;

	/** 告知URI */
	public String notificationUri;

	/** MIMEタイプ */
	public String mimeType;

	/** メディアタイプ */
	public String mediaType;

	/** documentID */
	public String documentid;

	/** 版数 */
	public Long documentRevision;

	/** ファイルタイトル */
	public String fileCaption;

	/** 情報識別区分 */
	public String disasterInformationType;

	/** 希望公開終了日時 */
	public String validDateTime;

	/** 更新種別 */
	public String distributiontype;

	/** メールタイトル */
	public String emailtitle;

	/** 送信本文 */
	public String content;
}
