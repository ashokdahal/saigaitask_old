/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form;

/**
 * 発表組織／作成組織アクションフォーム
 */
@lombok.Getter @lombok.Setter
public class OrganizationForm {

	/** 組織タイプ(1:発表組織 2:作成組織)*/
	public String organizationType;

	/** 行番号*/
	public int rownum;

	/** 組織名*/
	public String organizationName = "";

	/** 地方公共団体コード*/
	public String organizationCode = "";

	/** 組織ドメイン*/
	public String organizationDomainName = "";

	/** 部署名*/
	public String officeName = "";

	/** 部署名(カナ)*/
	public String officeNameKana = "";

	/** 部署住所*/
	public String officeLocationArea = "";

	/** 部署電話番号*/
	public String phone = "";

	/** 部署FAX番号*/
	public String fax = "";

	/** 部署メールアドレス*/
	public String email = "";

	/** 部署ドメイン*/
	public String officeDomainName = "";
}
