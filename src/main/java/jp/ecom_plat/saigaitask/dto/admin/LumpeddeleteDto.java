/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto.admin;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 管理画面一括削除 画面表示内容
 */
@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class LumpeddeleteDto implements Serializable{
	private static final long serialVersionUID = 1L;


	public LumpeddeleteDto(){
		currentLocalgovinfoid = "";
		localgovInfos = new LinkedHashMap<Long,String>();

		menuloginInfos = new LinkedHashMap<Long,Map<Long,String>>();
		menuprocessInfos = new LinkedHashMap<Long,Map<Long,String>>();
		menutaskInfos = new LinkedHashMap<Long,Map<Long,String>>();
		menutaskmenuInfos = new LinkedHashMap<Long,Map<Long,String>>();

		localgovInfoMenuloginInfosMenuCount = new LinkedHashMap<Long,Map<Long,Integer>>();
		localgovInfoMenuprocessInfosMenuCount = new LinkedHashMap<Long,Map<Long,Integer>>();
		localgovInfoMenutaskInfosMenuCount = new LinkedHashMap<Long,Map<Long,Integer>>();

		viewDataCount = 0;
	}

	/** */
	public String currentLocalgovinfoid;

	/** 地方自治体情報 */
	public Map<Long, String> localgovInfos;

	/** 地方自治体情報画面表示フラグ */
	public Boolean viewLocalgovInfoLabel;

	/** 画面表示件数 */
	public int viewDataCount;

	/** メニュー設定情報 */
	public Map<Long,Map<Long,String>> menuloginInfos;
	public Map<Long,Map<Long,Integer>> localgovInfoMenuloginInfosMenuCount;

	/** メニュープロセス情報リスト */
	public Map<Long,Map<Long,String>> menuprocessInfos;
	public Map<Long,Map<Long,Integer>> localgovInfoMenuprocessInfosMenuCount;

	/** メニュータスク情報リスト */
	public Map<Long,Map<Long,String>> menutaskInfos;
	public Map<Long,Map<Long,Integer>> localgovInfoMenutaskInfosMenuCount;

	/** タスクメニュー情報リスト */
	public Map<Long,Map<Long,String>> menutaskmenuInfos;
}
