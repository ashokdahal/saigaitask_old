/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.setupper;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.operation.Operations;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.names.LocalgovInfoNames;
import jp.ecom_plat.saigaitask.service.db.DisasterMasterService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;

/**
 * 自治体セットアッパーの抽象アクションクラスです.
 */
@Transactional(propagation=Propagation.REQUIRED, noRollbackFor=ServiceException.class)
public abstract class AbstractSetupperAction extends AbstractAction {

	/** 自治体切り替えSELECTオプション */
	public Map<Long, String> localgovSelectOptions;

	@Resource protected DisasterMasterService disasterMasterService;
	@Resource protected MenuloginInfoService menuloginInfoService;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("localgovSelectOptions", localgovSelectOptions);
	}

	/**
	 * セットアッパー画面の初期化
	 */
	public void initSetupper() {
		// アクションの初期化
		initAction();

		// 自治体切り替えSELECTオプション
		localgovSelectOptions = new LinkedHashMap<Long, String>();
		// システム管理者でログイン中
		if(loginDataDto.getGroupid()==0) {
			localgovSelectOptions = getLocalgovSelectOptionsAll();
		}
		else {
			LocalgovInfo localgovInfo = loginDataDto.getLocalgovInfo();
			localgovSelectOptions.put(localgovInfo.id, localgovInfoService.getLocalgovNameFull(localgovInfo));
		}
	}
	
	public LinkedHashMap<Long, String> getLocalgovSelectOptionsAll() {
		LinkedHashMap<Long, String> localgovSelectOptions = new LinkedHashMap<Long, String>();
		
		List<LocalgovInfo> localgovInfos = localgovInfoService.findAll(
				//Operations.asc(LocalgovInfoNames.prefcode()),
				//Operations.asc(LocalgovInfoNames.citycode()),
				Operations.asc(LocalgovInfoNames.id())
		);
		localgovSelectOptions.put(0L, lang.__("<New>"));
		for(LocalgovInfo localgovInfo : localgovInfos) {
			if(localgovInfo.id.equals(0L)) continue;
			/*
			// ログイン中の自治体以外で無効になっているものは表示しない
			if(localgovInfo.id.equals(loginDataDto.getLocalgovinfoid())==false
					&& localgovInfo.valid==false) continue;
			*/
			// 自治体名称の取得
			String localgovName = localgovInfoService.getLocalgovNameFull(localgovInfo);
			if(localgovInfo.valid==false) localgovName = lang.__("<Invalid>") + localgovName; // 自治体名称に無効状態表示
			// Select オプション設定
			localgovSelectOptions.put(localgovInfo.id, localgovName);
		}
		
		return localgovSelectOptions;
	}

	/**
	 * 指定した自治体にログインする.
	 * @param localgovinfoid 自治体ID
	 * @return ログイン成功フラグ
	 */
	public boolean loginLocalgovInfo(long localgovinfoid) {
		LocalgovInfo localgovInfo = localgovInfoService.findById(localgovinfoid);
		if(localgovInfo==null) throw new ServiceException(lang.__("Local gov. ID = {0} not found.", localgovinfoid));
		// 自治体情報のログイン
		loginDataDto.setLocalgovinfoid(localgovInfo.id);
		loginDataDto.setLocalgovInfo(localgovInfo);
		// 使っている災害種別を取得
		HashSet<Integer> usingDisasterids = menuloginInfoService.getUsingDisasterIds(loginDataDto.getLocalgovinfoid());
		// 使っている災害種別があれば、先頭のものでとりあえずログイン
		if(0<usingDisasterids.size()) {
			//loginDataDto.getDisasterid() = usingDisasterids.iterator().next();
		}
		return true;
	}
}
