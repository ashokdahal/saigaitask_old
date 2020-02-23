/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form;

/**
 * 定型文アクションフォーム
 */
@lombok.Getter @lombok.Setter
public class TemplateForm {

	/** 定型文ID */
	public String templateid = "";
	/** メニューID */
	public String menuinfoid = "";
	/** */
	public String name = "";
	/** */
	public String value = "";
	/** テンプレート種別 */
	public String noticetemplatetypeid = "";
	/** 区分 */
	public String templateclass = "";
	/** 通知種別 */
	public String noticetypeid = "";

}
