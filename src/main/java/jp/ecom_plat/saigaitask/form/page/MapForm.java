/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import org.json.JSONObject;

/**
 * 地図ページのアクションフォームクラスです.
 * 地図ページ固有のプロパティを定義します.
 */
@lombok.Getter @lombok.Setter
public class MapForm extends AbstractPageForm {

	/** 地図ID */
	public Long mapid;

	/** 追加するレイヤID(CSV) この順序でレイヤを重ねます. */
	public String layers;

	/** 非表示レイヤID(CSV) */
	public String hidden;

	/** 凡例を折りたたまないレイヤID(CSV) */
	public String expand;

	/** ポップアップするフィーチャID "レイヤID.フィーチャID" */
	public String popup;

	/** 中心位置(WGS84) "lon,lat" */
	public String center;

	/** ズームレベル */
	public Integer zoom;

	/** グレー表示 */
	public boolean filtergray = true;//デフォルトはグレー表示

	/** 空間検索範囲表示 */
	public boolean filterspatial = false;//デフォルトは非表示(17/06/13要望により変更)

	/** 検索条件JSON形式 */
	public JSONObject conditionValue;

	/**
	 * true: 地物登録フォームを表示
	 */
	public boolean insertFeature = false;
	/**
	 * 地物登録フォームのプリセットのJSON
	 */
	public JSONObject insertFeatureData;
	/**
	 * 地物登録成功時の動作を指定する
	 * "close": 画面を閉じる
	 * "redirect:http://example.com": 指定URLにリダイレクトする
	 */
	public String oninsertFeature;

	/** ジオメトリ登録モード */
	public boolean drawGeometry = false;

	/** ジオメトリ登録モードのWKT */
	public String drawGeometrywkt;

	/** ジオメトリ登録モードのfid */
	public String drawGeometryfid;

	/** 凡例パネル */
	public boolean legendCollapsed = false;

	/** 住所 */
	public String address;

	/** 凡例表示切替のSLDルール */
	public String rule;

	/** リスト画面、アコーディオンの開閉状態 */
	public String accordion = "";

	/** レイヤID */
	public String layerId;
}
