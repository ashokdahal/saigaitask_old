/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.dto.ListEditDto;

/**
 * 一括更新フォーム
 */
@lombok.Getter @lombok.Setter
public class SlimerForm {

	/** 変更対象テーブル名 */
	public String tablename;
	/** 主キー */
	public String key;
	/** 変更対象IDリスト(空または0ならばすべて対象) */
	public List<String> targetIds = new ArrayList<String>();

	/**  一括変更項目 */
	public List<ListEditDto> slimerDatas = new ArrayList<ListEditDto>();

	/** 一括編集のグルーピング */
	public List<ListEditDto> groupingDatas = new ArrayList<ListEditDto>();

}
