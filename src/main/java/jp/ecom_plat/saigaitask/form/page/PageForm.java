/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;


/**
 * 基本機能のリクエストパラメータ.
 * 抽象クラスのAbstractPageFormを具象する.
 * 
 * {@link jp.ecom_plat.saigaitask.action.page.IndexAction} の実行メソッドのリクエストパラメータはここで定義する.
 */
@lombok.Getter @lombok.Setter
public class PageForm extends AbstractPageForm {

	/** アラーム詳細表示に使う、AlarmmessageDataのid */
	public String id;//
	/** URL */
	public String url;
	/** オートコンプリート用属性ID */
	public String attrid;
	/** オートコンプリート用レイヤID */
	public String layerid;

	/**
	 * JDBC タイムスタンプエスケープ形式の String
	 * format must be yyyy-mm-dd hh:mm:ss[.fffffffff]
	 */
	public String timestamp;
}
