/* Copyright (c) 2015 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.seasar.framework.util.StringUtil;

import jp.ecom_plat.saigaitask.service.TrackdataidAttrService;
import jp.ecom_plat.saigaitask.util.SaigaiTaskDBLang;
import net.sf.json.JSONArray;

@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class SummaryListDto extends ListDto {

	/** 集計項目 */
	public List<Calc> calcs = new ArrayList<SummaryListDto.Calc>();

	/** 集計対象のリスト */
	private List<ListDtoItem> listDtoItems = new ArrayList<SummaryListDto.ListDtoItem>();

	public String LOCALGOVNAME_ROW_NAME() { return lang.__("Local gov. name"); }
	public String DISASTERNUM_ROW_NAME() { return lang.__("Number of occurrence disaster"); }

	/**
	 * 自治体名列を追加する.
	 * @return 自治体名列インデックス
	 */
	public int addLocalgovnameRow() {
		SummaryListDto summaryListDto = this;
		summaryListDto.columnNames.add(LOCALGOVNAME_ROW_NAME());
		return summaryListDto.columnNames.size() - 1;
	}

	/**
	 * 自治体名 列があれば削除する
	 * @return 成功フラグ
	 */
	public SummaryListDto deleteLocalgovNameRowIfExists() {
		deleteRowByName(LOCALGOVNAME_ROW_NAME());
		return this;
	}

	/**
	 * 発生災害数列を追加する.
	 * @return 発生災害数列インデックス
	 */
	public int addDisasternumRow() {
		SummaryListDto summaryListDto = this;
		summaryListDto.columnNames.add(DISASTERNUM_ROW_NAME()); //2番目の列は発生災害数
		summaryListDto.calcs.add(SummaryListDto.Calc.distinct(TrackdataidAttrService.TRACKDATA_ATTR_ID));
		return summaryListDto.columnNames.size() - 1;
	}

	/**
	 * 発生災害数列を削除する.
	 * @return 成功フラグ
	 */
	public SummaryListDto deleteDisasternumRow() {
		deleteRowByName(DISASTERNUM_ROW_NAME());
		return this;
	}

	public int getDisasternumRowIndex() {
		return columnNames.indexOf(DISASTERNUM_ROW_NAME());
	}

	/**
	 * 複数同時災害のリストを１つでも持っているかどうか
	 * 集計後に使うこと.
	 * @return 複数同時災害のリストを１つでも持っているかどうか
	 */
	public boolean hasMultiDisasterRow() {
		int disasternumRowIndex = getDisasternumRowIndex();
		if(-1<disasternumRowIndex) {
			for(List<String> value : columnValues) {
				String str = value.get(disasternumRowIndex);
				if(StringUtil.isNotEmpty(str)) {
					if(2<=Long.parseLong(str)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 集計対象リストに追加
	 * @param metadataid メタデータID
	 * @param listName リスト名称
	 * @param listDto
	 */
	public void addList(String metadataid, String listName, ListDto listDto) {
		ListDtoItem listDtoItem = new ListDtoItem();
		listDtoItem.metadataid = metadataid;
		listDtoItem.listName = listName;
		listDtoItem.listDto = listDto;
		listDtoItems.add(listDtoItem);
	}

	/**
	 * 各リストを集計して集計リストを生成する.
	 */
	public void summary() {
		//System.out.println("calcs: "+calcs.size());

		int disasternumRowIndex = getDisasternumRowIndex();

		for(ListDtoItem listDtoItem : listDtoItems) {
			ListDto list = listDtoItem.listDto;
			String metadataid = listDtoItem.metadataid;
			String listName = listDtoItem.listName;

			// 外部リストの集計
			calc(list, calcs);

			// 集計結果の取り出し
			List<String> values = new ArrayList<String>();
			values.add(getLocalgovAnchor(metadataid, listName));
			for(SummaryListDto.Calc calc : calcs) {
				// WFSで取得失敗した場合
				if(list.columnNames==null) {
					values.add(VALUE_UNDEFINED);
				}
				else {
					if(values.size()==disasternumRowIndex) {
						int count = 0;
						if(!StringUtil.isEmpty(calc.result)) {
							JSONArray disasterids = JSONArray.fromObject(calc.result);
							count = disasterids.size();
							// 0 は災害数としてカウントしない
							if(disasterids.contains("0")) count--;
						}
						values.add(String.valueOf(count));
					}
					else {
						values.add(calc.result==null ? VALUE_UNDEFINED : calc.result);
					}
				}
				// 結果をクリア
				calc.clear();
			}

			// 結果を集計リストの行に追加
			this.columnValues.add(values);
		}
	}

	/**
	 * クリックしたら selectを変更してページ遷移する aタグを返す
	 * @param metadataid
	 * @param listName
	 * @return
	 */
	protected String getLocalgovAnchor(String metadataid, String listName) {
		return "<a href='#' style='color: blue; text-decoration: underline;' onclick='var select=$(\"#externallistselect-container select\"); select.val(\""+metadataid+"\"); select.change();'>"+listName+"</a>";
	}

	public static void calc(ListDto list, List<Calc> calcs) {
		if(list.columnNames!=null) {
			// 一行ずつ演算
			for(List<String> values : list.columnValues) {
				for(int idx=0; idx<list.columnNames.size(); idx++) {
					String column = list.columnNames.get(idx);
					String value = values.get(idx);
					// 演算
					for(Calc calc : calcs) {
						calc.execute(column, value);
					}
				}
			}
		}
	}

	public static class ListDtoItem {
		public ListDto listDto;
		public String metadataid;
		public String listName;
	}

	public static class Calc {

		/**
		 * 計算結果.
		 * 再利用する場合は clearを呼ぶこと
		 */
		public String result;

		/** 演算の種類 */
		private String func;

		/** 属性IDまたは属性名称 */
		private String attrId;

		/** 条件 */
		private Condition condition;
		public Calc condition(String condition) {
			if(StringUtil.isEmpty(condition)) this.condition = null;
			else this.condition = new Condition(condition);
			return this;
		}

		public static List<String> functions = new ArrayList<String>(){{
			add("sum");
			add("count");
		}};
		public static String getFunctionName(String function, SaigaiTaskDBLang lang) {
			switch(function) {
			case "sum": return lang.__("Total<!--2-->");
			case "count": return lang.__("Count");
			}
			return function;
		}

		private Calc(String func, String attrId) {
			this.func = func;
			this.attrId = attrId;
			clear();
		}

		public static Calc max(String attrId) {
			return new Calc("max", attrId);
		}
		public static Calc sum(String attrId) {
			return new Calc("sum", attrId);
		}
		public static Calc count(String attrId) {
			return new Calc("count", attrId);
		}
		/**
		 * 重複を排除したJSONArray文字列を求める.
		 */
		public static Calc distinct(String attrId) {
			return new Calc("distinct", attrId);
		}

		/**
		 * 演算文字列をパースして取得します.
		 * @param str 演算文字列
		 * @param attrNameMap 属性IDから属性名称が得られるMap
		 * @return
		 */
		public static Calc parse(String str, Map<String, String> attrNameMap) {
			for(String func : functions) {
				String regex = func+"\\("+"(\\w+)"+"\\)";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(str);
				if(matcher.find()) {
					String attr = matcher.group(1);
					// 属性名称があれば、属性IDではなく、属性名称を設定する
					if(attrNameMap!=null && 0<attrNameMap.size()) {
						if(attrNameMap.containsKey(attr)) attr = attrNameMap.get(attr);
					}
					return new Calc(func, attr);
				}
			}
			return null;
		}

		public void execute(String column, String value) {
			if(!attrId.equals(column)) return;

			// 条件があれば判定
			Boolean cond = null;
			if(condition!=null) {
				cond = condition.eval(value);
				if(cond==false) return;
			}

			// TODO: 型を判定する

			//
			if("max".equals(func)) {
				if(StringUtil.isNotEmpty(value)) {
					try{
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.JAPANESE);
						Date val = sdf.parse(value);
						Date cur = result!=null ? sdf.parse(result) : null;
						if(cur==null || val.after(cur)) result = sdf.format(val);
					} catch(IllegalArgumentException e) {
						// do nothing
					} catch (ParseException e) {
						// do nothing
					}
				}
			}
			else if("sum".equals(func)) {
				if(result==null) result = String.valueOf(0);
				if(StringUtil.isNotEmpty(value)) {
					try {
						result = String.valueOf(new BigDecimal(result).add(new BigDecimal(value)));
					} catch(NumberFormatException e) {
						// do nothing
					}
				}
			}
			else if("count".equals(func)) {
				if(result==null) result = String.valueOf(0);
				// 空でなければカウントする
				// 空をカウントする条件だったらカウントする
				if(StringUtil.isNotEmpty(value) || (cond!=null&&cond==true)) {
					try {
						result = String.valueOf(Long.parseLong(result) + 1L);
					} catch(NumberFormatException e) {
						// do nothing
					}
				}
			}
			else if("distinct".equals(func)) {
				JSONArray array = new JSONArray();
				if(result!=null) {
					array = JSONArray.fromObject(result);
				}
				if(StringUtil.isNotEmpty(value)) {
					if(!array.contains(value)) {
						array.add(value);
					}
				}
				result = array.toString();
			}
		}

		/** 演算結果をクリアする */
		public void clear() {
			result = null;
		}

		public static class Condition {
			Logger logger = Logger.getLogger(Condition.class);

			/** 条件式 */
			public String condition;

			/** 次の条件 */
			public Condition next;

			/**
			 * 次の条件の論理演算
			 * OR: |
			 * AND: &
			 */
			public String nextLogical;

			public Condition(String condition) {
				condition = condition.trim();
				// 引用符で始まったら、終わりの方の引用符を探す
				if(condition.startsWith("'")) {
					int fromIndex = 1;
					int endQuoteIndex = -1;
					do {
						endQuoteIndex = condition.indexOf("'", fromIndex);
						System.out.println("endQuoteIndex: "+endQuoteIndex);
						if(endQuoteIndex==-1) break;
						else {
							// 1つ前の文字列がエスケープ文字かどうか
							char pref = condition.charAt(endQuoteIndex-1);
							if(pref=='\\') {
								// エスケープ文字を削除
								condition = condition.substring(0, endQuoteIndex-1) + condition.substring(endQuoteIndex);

								// 次を探す
								fromIndex = endQuoteIndex;
								continue;
							}
							else break;
						}
					} while(true);
					boolean existEndQuote = endQuoteIndex!=-1;
					System.out.println("existEndQuote: "+existEndQuote);
					// 引用符の終わりが見つかったら文字列として扱う
					if(existEndQuote) {
						this.condition = condition.substring(1, endQuoteIndex);
						System.out.println("condition: "+this.condition);
						String nextCondition = condition.substring(endQuoteIndex+1);
						if(StringUtil.isNotEmpty(nextCondition)) {
							// 論理演算
							String operator = "OR";
							int index = nextCondition.indexOf(" "+operator+" ");
							if(index!=-1) {
								this.nextLogical = operator;
								this.next = new Condition(nextCondition.substring(index+3));
								return;
							}
						}
						return;
					}
				}
				// TODO: 括弧は階層

				// 論理演算
				String operator = "OR";
				int index = condition.indexOf(" "+operator+" ");
				if(index!=-1) {
					this.condition = condition.substring(0, index);
					this.nextLogical = operator;
					this.next = new Condition(condition.substring(index+3));
					return;
				}

				// TODO: 比較演算子
				this.condition = condition;
				System.out.println("condition: "+condition);
			}

			/** 条件を評価する */
			public boolean eval(String value) {
				// 等しいか判定
				boolean result = condition.equals(value);
				// 条件がまだある場合
				if(next!=null) {
					if("OR".equals(nextLogical)) {
						if(result==false) {
							result = next.eval(value);
						}
					}
				}
				return result;
			}
		}
	}

}
