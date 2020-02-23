/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.db;

@lombok.Getter @lombok.Setter
public class NoticemailDataForm {

	public String id = "";

	public String trackdataid = "";

	public String noticetypeid = "";

	public String mailto = "";

	public String title = "";

	public String content = "";

	public String sendtime = "";

	public String send = "";


}