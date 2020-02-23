/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.validator.Required;

/**
 * 公共情報コモンズ(緊急速報メール)フォーム
 */
@lombok.Getter @lombok.Setter
public class PcommonsmailForm extends AbstractPageForm {

	/** pcommonsmail タイトル */
	//@Required
	public String pcommonsmailTitle = "";

	/** pcommonsmail 本文 */
	//@Required
	public String pcommonsmailContent = "";

	/** email タイトル */
	//@Required
	public String emailTitle = "";

	/** 通知グループID */
	public List<String> noticegroupinfoid = new ArrayList<String>();

	/** 追加送付先 */
	public String additionalReceiver = "";

	/** テンプレート番号 */
	public String templateid = "";

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

	/** 公式発表日時 */
	public String reporttime = "";

	/** 都道府県フラグ */
	public boolean isPrefecture;

	/** 運用種別 */
	public String statusValue;
}
