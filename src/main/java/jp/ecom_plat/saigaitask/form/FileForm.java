/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form;

import org.springframework.web.multipart.MultipartFile;

/**
 * ファイルアップロードフォーム
 */
@lombok.Getter @lombok.Setter
public class FileForm {

	/** アップロードしたファイル */
	public MultipartFile formFile;

	/** タイトル（ファイル名） */
	public String title;

	/** URL(外部リンクの場合) */
	public String url;
}
