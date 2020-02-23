/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.sql.Timestamp;
import java.util.List;

/**
 * コモンズ送信情報
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class PCommonsSendDto {

	/** 自治体ID */
	public Long localgovinfoid;

	/** 運用種別 */
	public String statusValue;

	/** 更新種別(normalSend:新規or更新 correctionSend:訂正 cancelSend:取消) */
	public String distributiontype;

	/** 前回更新種別(normalSend:新規or更新 correctionSend:訂正 cancelSend:取消) */
	public String distributiontypelast;

	/** 取消/訂正理由 */
	public String description;

	/** 作成日時 */
	public Timestamp createdatetime;

	/** 初版作成日時 */
	public Timestamp firstcreatedatetime;

	/** 公式発表日時 */
	public Timestamp reporttime;

	/** 希望公開開始日時 */
	public Timestamp targetdatetime;

	/** 希望公開終了日時 */
	public String validdatetime;

	/** 発表組織 担当者名*/
	public String personResponsible = "";

	/** 発表組織 組織名*/
	public String organizationName = "";

	/** 発表組織 地方公共団体コード*/
	public String organizationCode = "";

	/** 発表組織 組織ドメイン*/
	public String organizationDomainName = "";

	/** 発表組織 部署名*/
	public String officeName = "";

	/** 発表組織 部署名(カナ)*/
	public String officeNameKana = "";

	/** 発表組織 部署住所*/
	public String officeLocationArea = "";

	/** 発表組織 部署電話番号*/
	public String phone = "";

	/** 発表組織 部署FAX番号*/
	public String fax = "";

	/** 発表組織 部署メールアドレス*/
	public String email = "";

	/** 発表組織 部署ドメイン*/
	public String officeDomainName = "";

	/** 作成組織 組織名*/
	public String organizationNameEditorial = "";

	/** 作成組織 地方公共団体コード*/
	public String organizationCodeEditorial = "";

	/** 作成組織 組織ドメイン*/
	public String organizationDomainNameEditorial = "";

	/** 作成組織 部署名*/
	public String officeNameEditorial = "";

	/** 作成組織 部署名(カナ)*/
	public String officeNameKanaEditorial = "";

	/** 作成組織 部署住所*/
	public String officeLocationAreaEditorial = "";

	/** 作成組織 部署電話番号*/
	public String phoneEditorial = "";

	/** 作成組織 部署FAX番号*/
	public String faxEditorial = "";

	/** 作成組織 部署メールアドレス*/
	public String emailEditorial = "";

	/** 作成組織 部署ドメイン*/
	public String officeDomainNameEditorial = "";

	/** 要旨 */
	public String contentDescription;

	/** タイトル*/
	public String title = "";

	/** 本文*/
	public String text = "";

	/** 開催場所*/
	public String area = "";

	/** 開催開始日*/
	public String eventFrom = "";

	/** 開催終了日*/
	public String eventTo = "";

	/** 参加料金*/
	public String eventFee = "";

	/** 告知URI*/
	public String eventNotificationUri = "";

	/** ファイルURI*/
	public String eventFileUri = "";

	/** カテゴリータイプ*/
	public String categoryType = "";

	/** ドキュメントID*/
	public String documentId;

	/** 送信システムID */
	public String senderId;

	/** ディストリビューションID */
	public String distributionId;

	/** 版数*/
	public String documentRevision;

	/** 補足情報*/
	public String complementaryInfo;

	/** 災害名称(複数災害対応) */
	public String trackdataname;

	/** 記録データID */
	public Long trackdataid;

	/** 避難所リスト */
	public List<ShelterInformationDto> shelterInformationList;
	/** 避難勧告・避難指示リスト */
	public List<RefugeInformationDto> refugeInformationList;
	/** 被害情報 */
	public DamageInformationDto damageInformationDto;
	/** イベント */
	public EventInformationDto eventInformationDto;
	/** お知らせ */
	public GeneralInformationDto generalInformationDto;
	/** 災害対策本部設置状況 */
	public AntidisasterInformationDto antidisasterInformationDto;
}
