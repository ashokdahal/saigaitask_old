/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

import java.util.ArrayList;
import java.util.List;

import jp.ecom_plat.saigaitask.dto.ListEditDto;

import org.springframework.web.multipart.MultipartFile;
import org.json.JSONArray;
import org.json.JSONObject;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;

/**
 * リストページのアクションフォームクラスです.
 * リストページ固有のプロパティを定義します.
 */
@lombok.Getter @lombok.Setter
public class ListForm extends AbstractPageForm {

	/** ID */
	public String id = "";
	/** 画面の値 */
	public String value = "";

	/** 編集時の値リスト */
	public List<ListEditDto> saveDatas =
			new ArrayList<ListEditDto>();

	/** 一括編集データリスト */
	public List<ListEditDto> slimerDatas =
			new ArrayList<ListEditDto>();

	/** for 一括編集のグルーピング */
	public List<ListEditDto> groupingDatas =
			new ArrayList<ListEditDto>();

	/** 一括追記データリスト */
	public List<ListEditDto> addDatas =
			new ArrayList<ListEditDto>();

	/** アップロードファイル */
	@Binding(bindingType = BindingType.NONE)
	public MultipartFile formFile = null;

	/** for グレー表示 */
	public boolean filtergray = true;

	/** リストのJSON形式のデータ */
	public JSONArray dataList;

	/** フィルタリング行のJSON形式のデータ */
	public JSONArray filterrowList;

	/** 外部地図のメタデータID */
	public String metaid;

	/** 検索条件JSON形式 */
	public JSONObject conditionValue;

	/** ソート項目 */
	public String sort = "";

	/** 合計行表示 */
	public boolean totalable = false;

	/** File Upload 用結果コメント */
	public String responseMessage = "";

	/** ページ番号 */
	public Integer npage = 0;

	/** ページ数 */
	public Integer pagesize = 0;

	/** 全ページ */
	public boolean pageall = false;

	/** ファイル削除用ID*/
	public String deleteid;

	/** レイヤID*/
	public String layerid;

	/** 集計リストフラグ */
	public boolean summarylist;
}
