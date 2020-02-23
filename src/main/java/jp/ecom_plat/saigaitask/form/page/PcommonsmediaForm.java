/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.ecom_plat.saigaitask.validator.Required;
/**
 * 公共情報コモンズ(メディア発信)フォーム
 */
@lombok.Getter @lombok.Setter
public class PcommonsmediaForm extends AbstractPageForm {

	/** email 件名 */
	//@Required
	public String emailTitle = "";

	/** 通知グループID */
	public List<String> noticegroupinfoid = new ArrayList<String>();

	/** 追加送付先 */
	public String additionalReceiver = "";

	/**  避難所フラグ */
	public boolean isShelter;

	/** 避難勧告フラグ */
	public boolean isRefuge;

	/** イベントフラグ */
	public boolean isEvent;

	/** おしらせフラグ */
	public boolean isGeneral;

	/** 被害情報フラグ */
	public boolean isDamage;

	/**災害対策本部設置状況フラグ*/
	public boolean isAntidisaster;

	/** 更新種別 */
	public String distributiontype = "";

	/** 前回更新種別 */
	public String distributiontypelast = "";

	/** 取消・訂正理由 */
	public String description = "";

	/** 希望公開終了日時 */
	public String validdatetime = "";

	/** 希望公開終了日時有効期間From */
	public String validdatetimetermfrom = "";

	/** 希望公開終了日時有効期間To */
	public String validdatetimetermto = "";

	/** 公式発表日時 */
	public String reporttime = "";

	/** 公共情報コモンズ発信種別 */
	public String sendType = "";

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
	public String notificationUri = "";

	/** ファイルURI*/
	public String fileUri = "";

	/** 分類*/
	public String division = "";

	/** ドキュメントID*/
	public String documentId = "";

	/** 版数*/
	public String documentRevision;

	/** ファイルタイトル*/
	public String fileCaption;

	/** 情報識別区分*/
	public String disasterInformationType;

	/** テンプレート番号 */
	public String templateid = "";

	/** 補足情報 */
	public String complementaryInfo = "";

	/** 発表組織 担当者名 */
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

	/** バリデーションチェックエラーフラグ */
	public boolean validationCheckflg = false;

	/** 都道府県フラグ */
	public boolean isPrefecture;

	/** 災害名称(複数災害対応) */
	public String trackdataname;

	/** 運用種別 */
	public String statusValue;

	/** 発表組織名リスト */
	public String organizationNameList;

	/** 本部名称*/
	public String name;

	/** テーブル情報 */
	public Map<String, String> lasthatureiKbnMap = new HashMap<String, String>();
}
