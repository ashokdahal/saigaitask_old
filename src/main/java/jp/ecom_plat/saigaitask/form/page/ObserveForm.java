/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.page;

@lombok.Getter @lombok.Setter
public class ObserveForm extends AbstractPageForm{

	public String id = "";

	public Integer interval = 10;
}