package jp.ecom_plat.saigaitask.form.admin.setupper;

@lombok.Getter @lombok.Setter

public class DeleteLocalgovInfoForm {
	/** 削除成功フラグ */
	public boolean success = false;
	/** 削除失敗フラグ */
	public boolean fail = false;
	/** requestid */
	public String requestid;
	/** 削除対象自治体ID */
	public Long localgovinfoid;
}
