/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form;

import java.util.List;

/**
 * 記録情報フォーム
 */
@lombok.Getter @lombok.Setter
public class TrackForm {

	/** 災害ID */
	public String id = "";
	/** 自治体ID */
	public String localgovinfoid = "0";
	/** 地図情報ID */
	public String trackmapinfoid = "0";
	/** デモID */
	public String demoinfoid = "0";
	/** 災害種別 */
	//public String disasterid = "";
	/** 訓練プラン */
	public String trainingplandataid = "0";
	/** 災害名 */
	public String name = "";
	/** 備考 */
	public String note = "";
	/** 開始時刻 */
	public String starttime = "";
	/** 完了時刻 */
	public String endtime = "";
	/** 削除フラグ */
	public String deleted = "false";

	public String ref = "";
	/** 災害選択の災害ID */
	public String oldtrackid = "";

	/** 市町村の記録データID */
	public List<String> citytrackdataids;
	/** 市町村の記録データ名称統一フラグ */
	public String overrideTrackDataName = "false";

	/** 災害登録時のマップコピーフラグ */
	public String copyMap = "false";
}
