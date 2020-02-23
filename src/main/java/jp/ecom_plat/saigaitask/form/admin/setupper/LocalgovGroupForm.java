/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.form.admin.setupper;

import java.util.ArrayList;
import java.util.List;

import org.seasar.struts.annotation.LongType;

import jp.ecom_plat.saigaitask.form.db.LocalgovgroupmemberInfoForm;

/**
 * 自治体セットアッパーのアクションフォーム
 */
@lombok.Getter @lombok.Setter
public class LocalgovGroupForm {

	// class LocalgovgroupInfoForm

	/** 自治体グループID */
	@LongType
    public String id = "";
    public String name = "";
    public String localgovinfoid = "";
    public String disporder = "";

    /** 有効・無効 */
	public Boolean valid = true;
	
	public List<LocalgovgroupmemberInfoForm> members = new ArrayList<>();
	
	/** 一覧画面で利用する自治体IDフィルタ */
	public String filterlocalgovinfoid = "0";
}
