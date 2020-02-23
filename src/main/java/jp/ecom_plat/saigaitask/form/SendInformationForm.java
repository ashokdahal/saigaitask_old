/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.seasar.framework.container.annotation.tiger.Binding;
import org.seasar.framework.container.annotation.tiger.BindingType;
import org.springframework.web.multipart.MultipartFile;

import jp.ecom_plat.saigaitask.form.page.AbstractPageForm;

/**
 * 情報発信画面のアクションフォーム
 */
@lombok.Getter @lombok.Setter
public class SendInformationForm extends AbstractPageForm {

	/** email 件名 */
	//メール配信しない。チェックON時の切り分けが必要なためアクションクラス内でバリデーションチェック
	 //@Required
	public String emailTitle = "";	// 画面初期表示値を設定できます

	/** sendInformation 本文 */
	//メール配信しない。チェックON時の切り分けが必要なためアクションクラス内でバリデーションチェック
	//@Required
	public String sendInformationContent = "";

	/** email 宛先リスト */
	//public Map<String, String> emailAddressMap = new LinkedHashMap<String, String>();

	/** alert  宛先リスト */
	//public Map<String, String> alertMap = new LinkedHashMap<String, String>();

	/** 通知グループID */
	public List<String> noticegroupinfoid = new ArrayList<String>();

	/** ポップアップさせる */
	public boolean popup = false;
	/** メールしない */
	public boolean notmail = false;

	/** alert 宛先リスト 選択結果 */
	//public String[] checkedAlertList;
	public List<String> checkedAlertList = new ArrayList<String>();

	/** 追加送付先 */
	public String additionalReceiver = "";

	/** 添付ファイル名 */
	public String attachName = "";

	/** 添付ファイル */
	public File attachFile ;

	/** 添付ファイル */
    @Binding(bindingType = BindingType.NONE)
    public MultipartFile formFile;
    /** 添付ファイル群 */
	@Binding(bindingType = BindingType.NONE)
    public MultipartFile[] formFiles;

	/** 要請メール時のポップアップ処理 */
	public boolean enablePopup = false;

	/** 定型文ID */
	public String templateid = "";
	
	/** メニューID */
	public String menuinfoid = "";
	
	/** PUSH配信 */
	public boolean push = false;
	
	/** PUSH配信内容 */
	public String pushContent = "";
	
	/**
	 * ポップアップ処理のチェックボックスの初期化
	 */
	public void resetEnablePopup() {
		this.enablePopup = false;
	}

}
