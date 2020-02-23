package jp.ecom_plat.saigaitask.form.admin.setupper;

import java.util.List;

@lombok.Getter @lombok.Setter

public class TimeDimensionForm {
	/** 変換対象災害マップ情報IDリスト */
	public List<String> trackmapinfoIds;
	/** 作成するマップ名称 */
	public String mapname;
}
