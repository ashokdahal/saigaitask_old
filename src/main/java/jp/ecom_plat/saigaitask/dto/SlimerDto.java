/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * 一括変更機能のDtoクラスです.
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class SlimerDto implements JSONString{

	private JSONArray slimerInfos = new JSONArray();

	/**
	 * 一括変更の対象項目を追加します.
	 * @param table テーブル名
	 * @param name カラムの名前
	 * @param field カラムのフィールド名
	 * @param dataType カラムのデータ型
	 * @param selectOptions セレクト値(カラムのデータ型がSelectのみ)
	 * @param checkDisplay チェックボックス値(カラムのデータ型がCheckboxのみ)
	 * @param editable 編集可
	 * @param nullable 空値可
	 * @param defaultcheck 一括変更初期チェック
	 * @param groupdefaultcheck 一括変更グループ初期チェック
	 * @param addable 一括追記
	 */
	public void addColumn(String table, String name, String field, String dataType, Map<String, String> selectOptions, String checkDisplay, boolean editable, boolean nullable, boolean defaultcheck, boolean groupdefaultcheck, boolean addable) {
		try {
			// カラム情報を生成
			JSONObject column = new JSONObject();
			column.put("name", name);
			column.put("field", field);
			column.put("dataType", dataType);
			if("Select".equals(dataType)) {
				JSONArray selectOptionsJSON = new JSONArray();
				for(Map.Entry<String, String> entry : selectOptions.entrySet()) {
					JSONObject selectOption = new JSONObject();
					selectOption.put("text", entry.getKey());
					selectOption.put("value", entry.getValue());
					selectOptionsJSON.put(selectOption);
				}

				column.put("selectOptions", selectOptionsJSON);
			}
			if("Checkbox".equals(dataType)) column.put("checkDisplay", checkDisplay);
			column.put("editable", editable);
			column.put("nullable", nullable);
			column.put("defaultcheck", defaultcheck);
			column.put("groupdefaultcheck", groupdefaultcheck);
			column.put("addable", addable);

			// 一括変更情報に追加
			JSONObject slimerInfo = getOrCreateSlimerInfo(table);
			if(slimerInfo.has("columns")==false) slimerInfo.put("columns", new JSONArray());
			JSONArray columns = slimerInfo.getJSONArray("columns");
			columns.put(column);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getColumn(String table, String field) {
		JSONObject slimerInfo = getSlimerInfo(table);
		if(slimerInfo!=null) {
			if(slimerInfo.has("columns")) {
				try {
					JSONArray columns = slimerInfo.getJSONArray("columns");
					for(int idx=0; idx<columns.length(); idx++) {
						JSONObject column = columns.getJSONObject(idx);
						if(column.has("field") && field.equals(column.getString("field"))) {
							return column;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 一括変更グループを追加します.
	 * @param table テーブル名
	 * @param name グループ対象カラムの名前
	 * @param field グループ対象カラムのフィールド名
	 * @param dataType グループ対象カラムのデータ型
	 * @param selectOptions グループのセレクト値(カラムのデータ型がSelectのみ)
	 * @param groupdefaultcheck グループの初期チェック
	 */
	public void addGrouping(String table, String name, String field, String dataType, Map<String, String> selectOptions, boolean groupdefaultcheck) {

		try {
			// 一括変更グループ情報を生成
			JSONObject grouping = new JSONObject();
			grouping.put("name", name);
			grouping.put("field", field);
			grouping.put("dataType", dataType);
			JSONArray selectOptionsJSON = new JSONArray();
			for(Map.Entry<String, String> entry : selectOptions.entrySet()) {
				JSONObject selectOption = new JSONObject();
				selectOption.put("text", entry.getKey());
				selectOption.put("value", entry.getValue());
				selectOptionsJSON.put(selectOption);
			}
			grouping.put("selectOptions", selectOptionsJSON);
			grouping.put("groupdefaultcheck", groupdefaultcheck);

			// 一括変更情報に追加
			JSONObject slimerInfo = getOrCreateSlimerInfo(table);
			if(slimerInfo.has("groupings")==false) slimerInfo.put("groupings", new JSONArray());
			JSONArray groupings = slimerInfo.getJSONArray("groupings");
			groupings.put(grouping);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 絞り込み用IDを追加
	 * @param table
	 * @param targetIdsList
	 */
	public void setTargetIds(String table, List<Long> targetIdsList) {
		try {
			JSONObject slimerInfo = getOrCreateSlimerInfo(table);
			if(slimerInfo.has("targetIds")==false) slimerInfo.put("targetIds", new JSONArray());
			JSONArray targetIds = slimerInfo.getJSONArray("targetIds");
			for(Long targetId : targetIdsList) targetIds.put(targetId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * IDのフィールド名(key)を設定
	 * @param table
	 * @param targetIdsList
	 */
	public void setKey(String table, String key) {
		try {
			JSONObject slimerInfo = getOrCreateSlimerInfo(table);
			slimerInfo.put("key", key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 一括変更情報を取得します.
	 * @param table
	 * @return
	 */
	public JSONObject getSlimerInfo(String table) {
		try {
			// 一括変更情報を検索
			JSONObject slimerInfo = null;
			int index = indexOf(table);
			if(index!=-1) slimerInfo = (JSONObject) slimerInfos.get(index);
			return slimerInfo;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 一括変更情報を取得します.
	 * なければ生成して追加したものが取得されます.
	 * @param table
	 * @return
	 */
	public JSONObject getOrCreateSlimerInfo(String table) {
		try {
			// 一括変更情報を検索
			JSONObject slimerInfo = getSlimerInfo(table);
			// 一括変更情報がなければ作成する
			if(slimerInfo==null) {
				slimerInfo = new JSONObject();
				slimerInfo.put("table", table);
				slimerInfos.put(slimerInfo);
			}

			return slimerInfo;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * テーブル名から一括変更情報を検索します.
	 * @param table テーブル名
	 * @return なければ -1
	 */
	public int indexOf(String table) {
		try {
			// 一括変更情報を検索
			for(int idx=0; idx<slimerInfos.length(); idx++) {
				JSONObject _slimerInfo = (JSONObject) slimerInfos.get(idx);
				if(_slimerInfo.has("table") && table.equals(_slimerInfo.getString("table"))) {
					return idx;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return -1;
	}

	@Override
	public String toJSONString() {
		JSONObject json = new JSONObject();
		try {
			json.put("slimerInfos", slimerInfos);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
}
