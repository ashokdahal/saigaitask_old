/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.DisastersituationhistoryData;
import jp.ecom_plat.saigaitask.entity.db.DisastersummaryhistoryData;




@lombok.Getter @lombok.Setter
public class DisastersummaryForm extends AbstractPageForm {
	/** 表示対象の被災集計履歴ID */
	public Long selectedDisastersummaryhistoryid;
	
	/** 被災集計履歴データ */
	public DisastersummaryhistoryData disastersummaryhistoryData;
	
	/** 被災状況履歴データリスト */
	public List<DisastersituationhistoryData> disastersituationhistoryDataList;
}