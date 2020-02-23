/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;

/**
 * テレメータの定数を定義
 */
public class TMObsrvtn {

	/** 雨量 */
	public static final Integer ITEMKIND_RAIN = 1;
	/** 水位 */
	public static final Integer ITEMKIND_RIVER = 4;
	/** ダム */
	public static final Integer ITEMKIND_DAM = 7;

	private static SaigaiTaskDBLang lang = new SaigaiTaskDBLang("");
	private static SaigaiTaskDBLang lang2 = new SaigaiTaskDBLang();

	/**
	 * 翻訳処理を含む Map クラス
	 */
	public static class TMOMap extends HashMap<Integer, String> {
		private static final long serialVersionUID = 1L;
		public TMOMap(Map<Integer, String> map) {
			super(map);
		}
		public String get(Integer key) {
			return lang2.__(super.get(key));
		}
	}

	/**
	 * データ種別コードのラベルのマップ
	 */
	public static final TMOMap ITEMKIND_CODE_MAP;
	static {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(ITEMKIND_RAIN, lang.__("Rainfall"));
		map.put(ITEMKIND_RIVER, lang.__("Water level"));
		map.put(ITEMKIND_DAM, lang.__("Dam data"));
		ITEMKIND_CODE_MAP = new TMOMap(Collections.unmodifiableMap(map));
	}

	/**
	 * コードから監視観測データ名称を取得
	 */
	public static final Map<Integer, TMOMap> ITEM_CODE_MAP;
	static {
		Map<Integer, TMOMap> mapmap = new HashMap<Integer, TMOMap>();
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(10, lang.__("10-min rainfall"));
		map.put(20, lang.__("30-min rainfall"));
		map.put(30, lang.__("Hourly rainfall"));
		map.put(40, lang.__("3-hr rainfall "));
		map.put(50, lang.__("6-hr rainfall "));
		map.put(60, lang.__("24-hr rainfall "));
		map.put(70, lang.__("Cumulative rainfall"));
		map.put(80, lang.__("Rainfall detection time"));
		map.put(100, lang.__("60-min rainfall"));
		mapmap.put(ITEMKIND_RAIN, new TMOMap(Collections.unmodifiableMap(map)));
		map = new HashMap<Integer, String>();
		map.put(10, lang.__("River water level"));
		map.put(20, lang.__("River flow rate"));
		map.put(50, lang.__("10-min changes in water level amount"));
		map.put(60, lang.__("30-min changes in water level amount"));
		map.put(70, lang.__("Time changes in water level amount"));
		map.put(80, lang.__("10 min flow rate variation"));
		map.put(90, lang.__("30 min flow rate variation"));
		map.put(100, lang.__("Time flow rate variation amount"));
		mapmap.put(ITEMKIND_RIVER, new TMOMap(Collections.unmodifiableMap(map)));
		map = new HashMap<Integer, String>();
		map.put(10, lang.__("Reservoir water level"));
		map.put(20, lang.__("Reservoir capacity"));
		map.put(30, lang.__("Free space"));
		map.put(40, lang.__("Water storage rate (water utilization capacity)"));
		map.put(50, lang.__("Total inflow amount"));
		map.put(60, lang.__("Adjusted flow rate"));
		map.put(70, lang.__("Total discharge amount"));
		map.put(80, lang.__("Dam discharge amounts"));
		map.put(90, lang.__("Gate discharge amount"));
		map.put(100, lang.__("Valve discharge amount"));
		map.put(110, lang.__("Water utilization discharge amount"));
		map.put(120, lang.__("The amount of water for power"));
		map.put(130, lang.__("Pumping amount"));
		map.put(150, lang.__("Water storage rate (effective capacity)"));
		mapmap.put(ITEMKIND_DAM, new TMOMap(Collections.unmodifiableMap(map)));
		ITEM_CODE_MAP = Collections.unmodifiableMap(mapmap);
	}

	/**
	 * コンテンツコードのラベルのマップ
	 */
	public static final TMOMap CONTENTS_CODE_MAP;
	static {
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		//データ有効（0～127）
		map.put(10, lang.__("Range abnormality"));
		map.put(20, lang.__("Over the variation amount"));
		map.put(30, lang.__("Constant value"));
		map.put(40, lang.__("Now maintaining"));
		map.put(50, lang.__("Modified (manual entry)"));
		map.put(60, lang.__("Calculation value include missing values"));
		map.put(70, lang.__("Recalculation"));
		//データ無効（128～255）
		map.put(130, lang.__("Plan (old)"));
		map.put(131, lang.__("Plan (new)"));
		map.put(140, lang.__("Closing (dormant)"));
		map.put(150, lang.__("Now maintaining"));
		map.put(160, lang.__("Uncollected"));
		map.put(170, lang.__("No observation data"));
		map.put(180, lang.__("Data file uncreated"));
		map.put(190, lang.__("Missing value"));
		map.put(200, lang.__("Range abnormality"));
		map.put(255, lang.__("Training"));
		CONTENTS_CODE_MAP = new TMOMap(Collections.unmodifiableMap(map));
	}

}
