package jp.ecom_plat.saigaitask.form.admin.setupper;

import java.util.HashMap;
import java.util.Map;

import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;


@lombok.Getter @lombok.Setter
public class DisasterCombineForm {
	/** 班IDとメニュータスクメニュー情報のマップ */
	public Map<Long, Map<Long, MenutaskmenuInfo>> groupMenuMap = new HashMap<Long, Map<Long, MenutaskmenuInfo>>();
}
