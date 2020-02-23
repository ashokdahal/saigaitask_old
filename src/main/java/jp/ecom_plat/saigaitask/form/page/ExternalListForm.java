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
public class ExternalListForm extends AbstractPageForm {

	/** 画面の値 */
	public String value = "";

	/** リストのJSON形式のデータ */
	public JSONArray dataList;

	/** 外部地図のメタデータID */
	public String metaid;

	/** 災害グループでリストの行をフィルタするフラグ */
	public boolean filterTrackgroup;

	/** ソート項目 */
	public String sort = "";

	/** 合計行表示 */
	public boolean totalable = false;

	/** 合計行のみの表示 */
	public boolean onlyTotale = false;

	/** 表ID */
	public String listid;

	/** ページ番号 */
	public Integer npage = 0;

	/** ページ数 */
	public Integer pagesize = 0;

	/** 全ページ */
	public boolean pageall = false;

	/** 履歴表示するgid */
	public Long gid = null;
	/** 履歴表示する_orgid */
	public Long _orgid = null;

	/**
	 * CSV文字列を dataList から生成する.
	 * @return
	 */
	public String createCsvStringBydataList() {
		Logger logger = Logger.getLogger(ExternalListForm.class);

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
