/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;



/**
 * 公共情報コモンズ避難所レイヤ情報フォーム
 *
 */
@lombok.Getter @lombok.Setter
public class PubliccommonsReportShelterInfoForm {
	/** ID */
	public String id = "";
	/** 自治体ID */
	public String localgovinfoid = "";
	/** テーブルマスタ情報ID */
	public String tablemasterinfoid = "";
	/** 避難所名 */
	public String attrshelter = "";
	/** 地区 */
	public String attrarea = "";
	/** 住所 */
	public String attraddress = "";
	/** 電話番号 */
	public String attrphone = "";
	/** FAX */
	public String attrfax = "";
	/** 代表者氏名 */
	public String attrstaff = "";
	/** 開設状況 */
	public String attrstatus = "";
	/** 収容定員数 */
	public String attrcapacity = "";
	/** 開設日時 */
	public String attrsetuptime = "";
	/** 閉鎖日時 */
	public String attrclosetime = "";
	/** 種別 */
	public String attrtype = "";
	/** 座標 */
	public String attrcircle = "";
	/** 避難人数 */
	public String attrheadcount = "";
	/** 避難人数(うち自主避難) */
	public String attrheadcountvoluntary = "";
	/** 避難世帯数 */
	public String attrhouseholds = "";
	/** 避難世帯数(うち自主避難) */
	public String attrhouseholdsvoluntary = "";
}
