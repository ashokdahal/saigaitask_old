/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;




/**
 * 公共情報コモンズ避難勧告レイヤ情報フォーム
 *
 */
@lombok.Getter @lombok.Setter
public class PubliccommonsReportRefugeInfoForm {
	/** ID */
	public String id = "";
	/** 自治体ID */
	public String localgovinfoid = "";
	/** テーブルマスタ情報ID */
	public String tablemasterinfoid = "";
	/** 地区 */
	public String attrarea = "";
	/** 発令状況 */
	public String attrorderstatus = "";
	/** 対象世帯数 */
	public String attrhouseholds = "";
	/** 人数 */
	public String attrpeople = "";
	/** 発令日時 */
	public String attrordertime = "";
}
