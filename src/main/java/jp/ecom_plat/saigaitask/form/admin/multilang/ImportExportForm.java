/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin.multilang;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

@lombok.Getter @lombok.Setter
public class ImportExportForm implements Serializable{

	/** 言語コード */
	public String langCode;

	/** 言語名 */
	public String langName;

	/** インポート方式 */
	public String importType = "1";

	/** 言語データファイル */
	public MultipartFile langDataFile;
}
