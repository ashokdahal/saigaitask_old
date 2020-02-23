/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * リストページのアクションフォームクラスです.
 * リストページ固有のプロパティを定義します.
 */
@lombok.Getter @lombok.Setter
public class GeneralizationHistoryForm extends AbstractPageForm {

	Logger logger = Logger.getLogger(GeneralizationHistoryForm.class);

	/** 総括表履歴ID */
	public long id;

	/** 表ID */
	public String listid;

	/** 拡張子 */
	public String suffix;

	/** リストのJSON形式のデータ */
	public JSONArray dataList;

	/** 合計行の表示 */
	public boolean totalable;

	/**
	 * CSV文字列を dataList から生成する.
	 * @return
	 */
	public String createCsvStringBydataList() {
		//二重配列に格納したデータをカンマ区切りで整形、改行は「<br>」で代用して、パラメータ文字列に整形
		StringBuffer csvStr = new StringBuffer();
		try {
			JSONArray datas = dataList;
			for(int i=0; i<datas.length(); i++) {
				JSONArray data = datas.getJSONArray(i);
				for(int j=0; j<data.length(); j++){
					csvStr.append(data.getString(j));
					if(j<data.length()-1) {
						csvStr.append(",");
					}
				}
				csvStr.append("<br>");
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		return csvStr.toString();
	}
}
