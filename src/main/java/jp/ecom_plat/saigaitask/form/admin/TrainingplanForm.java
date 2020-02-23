/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin;

import java.util.List;

import jp.ecom_plat.saigaitask.entity.db.MeteotypeMaster;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanData;
import jp.ecom_plat.saigaitask.entity.db.TrainingplanlinkData;

import org.springframework.web.multipart.MultipartFile;
import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;

/**
 * 訓練プランの外部設定ファイル受け渡し用Form
 */

@lombok.Getter @lombok.Setter
public class TrainingplanForm {

	/** 訓練プランリスト */
	public TrainingplanData trainingplanData;
	/** 訓練プラン連携自治体リスト */
	public List<TrainingplanlinkData> trainingplanlinkDataList;
	/** 過去訓練情報リスト */
	public List<MeteotypeMaster> meteoTypeMasterList;

	public String meteoid = "";
	public String trainingplandataid = "";
	public String meteotypeid = "";
	public String meteoName = "";
	public String meteoNote = "";
	public String meteoUrl = "";
	public String meteoUpdate = "";
	public String meteoDeleted = "";
	/** アップロードファイル */
    @Binding(bindingType = BindingType.NONE)
    public MultipartFile formFile_external_xml = null;

    /** 複製対象プランID */
    public String copyTrainingplanId = "0";
    /** サーバ上の防災情報XMLファイル種別 */
    public String bousaixmlfile_selected_type = "";
    /** サーバ上の防災情報XMLファイル名 */
    public String bousaixmlfile_selected_filename = "";
}
