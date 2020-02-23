package jp.ecom_plat.saigaitask.form.admin.setupper;

import jp.ecom_plat.saigaitask.validator.Required;

@lombok.Getter @lombok.Setter
public class ExportForm {
	/** エクスポート自治体ID */
	@Required
	public Long localgovinfoid;

}
