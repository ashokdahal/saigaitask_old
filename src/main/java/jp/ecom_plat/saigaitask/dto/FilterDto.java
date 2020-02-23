/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import jp.ecom_plat.saigaitask.constant.FilterState;

/**
 * フィルタ処理の結果
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class FilterDto {

	/** レイヤID */
	private String layerId;
	/** フィルターキー */
	private String filterkey;
	/** フィルター名 */
	private String filtername;
	/** 結果IDのリスト(_orgid) */
	private List<Long> filteredFeatureIds;
	/** 合計数 */
	private Long total;
	/** 空間検索条件 */
	private JSONArray spatialLayer;
	/** フィルターID */
	private Long filterInfoId;
	/** フィルター種別 */
	private Integer filterstateid = FilterState.FILTERGRAYON;
	/** 検索文字列 */
	private String searchText;

	/**
	 * 検索条件JSON形式
	 * （マスタ用レイヤIDから災害用レイヤIDに変換する前）
	 */
	private JSONObject conditionValue;

	/**
	 * 検索条件JSON形式
	 * （マスタ用レイヤIDから災害用レイヤIDに変換した後）
	 */
	private JSONObject conditionValueActual;
}
