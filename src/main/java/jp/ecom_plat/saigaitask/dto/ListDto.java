/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.entity.db.TrackData;
import jp.ecom_plat.saigaitask.service.TrackdataidAttrService;
import jp.ecom_plat.saigaitask.service.db.TrackDataService;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * リスト画面を表示するための Dto クラスです.
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class ListDto {

	Logger logger = Logger.getLogger(ListDto.class);
	protected SaigaiTaskDBLang lang = new SaigaiTaskDBLang();

	/** 値が未定の場合の表示 */
	public static final String VALUE_UNDEFINED = "-";

	/** 表のタイトル */
	public String title;

	/** 項目名リスト */
	public List<String> columnNames = new ArrayList<String>();

	/** 項目値のリスト */
	public List<List<String>> columnValues = new ArrayList<List<String>>();
	/** 項目値のMap（リスト表示しない値も含む）*/
	public List<Map<String, String>> columnValueMaps = new ArrayList<>();

	/** 合計フラグ */
	public Boolean totalable = false;
	/** 合計値用 */
	public Map<String,String> sumItems = new HashMap<String,String>();
	/** デフォルトソート */
	public String defsort = null;

	/** このリストのtrackdataidの値(重複なし) */
	public Set<Long> distinctTrackdataids;

	/** 要素のID */
	public String styleId = "editTable";

	/** 文字のインデント設定用 */
	public Map<String, String> typeItems = new HashMap<String, String>();

	/** 全件数 */
	public int count;

	/** そのページの表示件数 */
	public int index;

	/** 履歴ボタンフラグ */
	public Boolean historybtn = false;
	public List<String> historybtnUrlParams = new ArrayList<String>();

	/**
	 * 複数同時災害のリストかどうか判定する.
	 * @return 複数同時災害のリストかどうか
	 */
	public boolean isMultiDisaster() {
		if(distinctTrackdataids!=null) {
			return 1<distinctTrackdataids.size();
		}
		return false;
	}

	/**
	 * 合計行を計算する
	 */
	public void total() {
		// 合計行の表示
		totalable = false;
		// まずは数値であれば、合計値を計算する
		if (columnNames!=null && columnValues!=null) {
			int num = columnNames.size();
			//合計値算出
			BigDecimal[] sums = new BigDecimal[num];
			for(List<String> values : columnValues) {
				for(int valuesIdx=0; valuesIdx<num; valuesIdx++) {
					try {
						String val = values.get(valuesIdx);
						if(val!=null) {
							BigDecimal bd = new BigDecimal(val);
							BigDecimal sum =  sums[valuesIdx];
							if(sum==null) sum = new BigDecimal(0);
							sums[valuesIdx] = sum.add(bd);
						}
					} catch (NumberFormatException e) {
						// do nothing
					}
				}
			}

			for(int idx=0; idx<num; idx++) {
				BigDecimal sum = sums[idx];
				String sumValue = VALUE_UNDEFINED;
				if(sum!=null) {
					sumValue = String.valueOf(sum);
					totalable = true;
					typeItems.put(columnNames.get(idx), "number");
				}
				else
					typeItems.put(columnNames.get(idx), "text");
				
				sumItems.put(columnNames.get(idx), sumValue);
			}
		}
	}

	/**
	 * 合計行のみを表示する
	 * @return
	 */
	public ListDto onlyTotalList() {
		ListDto listDto = this;

		// 合計行が未計算の場合
		if(sumItems.size()==0) {
			listDto.total();
		}

		// 表示行をクリア
		listDto.columnValues.clear();

		// 合計値の行を追加
		List<String> values = new ArrayList<String>();
		for(String columnName : listDto.columnNames) {
			String sum = listDto.sumItems.get(columnName);
			values.add(sum);
		}
		listDto.columnValues.add(values);

		// 合計行表示フラグを無効
		listDto.totalable = false;

		return this;
	}

	/**
	 * trackdataid列のインデックスを取得する.
	 * @return
	 */
	public int getTrackdataidRowIdx() {
		return columnNames.indexOf(TrackdataidAttrService.TRACKDATA_ATTR_ID);
	}

	/**
	 * trackdataid属性があるなら、災害名称へ変換する
	 * 
	 * @param trackDataService 記録データサービス
	 */
	public void replaceTrackdataid2NameIfExists(TrackDataService trackDataService) {
		ListDto listDto = this;
		int replaceIdx = getTrackdataidRowIdx();

		// trackdataid属性 があれば、災害列として表示するため、IDから名称へ変換する
		if(replaceIdx!=-1) {
			// 項目名を置換
			listDto.columnNames.set(replaceIdx, SpringContext.getApplicationContext().getBean(TrackdataidAttrService.class).TRACKDATA_ATTR_DISPLAY());

			// 災害名称キャッシュ用Map
			Map<Long, String> disasterNameMap = new HashMap<Long, String>();
			for(List<String> values : listDto.columnValues) {
				// 行の記録IDを取得
				String str = values.get(replaceIdx);
				try {
					if(StringUtil.isNotEmpty(str)) {
						Long columnTrackdataid = Long.valueOf(str);
						// 記録データID から災害名称を取得する
						if(!disasterNameMap.containsKey(columnTrackdataid)) {
							TrackData trackData = trackDataService.findById(columnTrackdataid);
							if(trackData!=null) {
								disasterNameMap.put(columnTrackdataid, trackData.name);
							}
						}

						// 災害名称へ変換する
						String disasterName = disasterNameMap.get(columnTrackdataid);
						if(StringUtil.isNotEmpty(disasterName)) {
							values.set(replaceIdx, disasterName);
						}
					}
				} catch(Exception e) {
					logger.warn(lang.__("Unable to get trackdataid(\"{1}\") of external list.", str), e);
				}
			}
		}
	}

	/**
	 * trackdataid 列があれば削除する
	 * @return 成功フラグ
	 */
	public boolean deleteTrackdataidRowIfExists() {
		int deleteIdx = getTrackdataidRowIdx();
		return deleteRow(deleteIdx);
	}

	/**
	 * 列名を指定して、列を削除します.
	 * @param name 列名
	 * @return 成功フラグ
	 */
	public boolean deleteRowByName(String name) {
		int index = columnNames.indexOf(name);
		return deleteRow(index);
	}

	/**
	 * 列インデックスを指定して列を削除します.
	 * @param idx 削除列のインデックス
	 * @return 成功フラグ
	 */
	public boolean deleteRow(int index) {
		boolean deleted = false;
		if(0<=index && index<columnNames.size()) {
			columnNames.remove(index);
			for(List<String> value : columnValues) {
				value.remove(index);
			}
			deleted = true;
		}
		return deleted;
	}

	/**
	 * 変更のない履歴は省く処理
	 * @param history
	 * @param attr
	 * @return 変更のない履歴を省いた結果リスト
	 */
	public void mergeHistory()
	{
		if(columnValues.size()<=1) return;

		List<List<String>> merged = new ArrayList<>();
		int last = columnValues.size()-1;

		// 最後のレコード
		List<String> columnValue = columnValues.get(last);
		merged.add(columnValue);

		// 最後から順にチェックする
		for(int i=last-1; 0<=i; i--) {
			List<String> lastmerged = merged.get(merged.size()-1);
			List<String> checktarget = columnValues.get(i);
			// 0番目、1番目は 最終更新日(time_from) と 編集者(st_edituser)のため、
			// 2番目以降が同じかどうかチェックする
			if(lastmerged.subList(2, lastmerged.size()-1)
					.equals(checktarget.subList(2, checktarget.size()-1))) {
				// 同じだった場合は次をチェック
				continue;
			}
			// 履歴が異なるなら追加する
			merged.add(checktarget);
		}

		// merged が逆順になっているので reverse
		Collections.reverse(merged);
		
		columnValues = merged;
	}
}
