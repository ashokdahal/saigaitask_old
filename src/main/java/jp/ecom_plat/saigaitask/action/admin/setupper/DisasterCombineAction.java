/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.setupper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutasktypeInfo;
import jp.ecom_plat.saigaitask.form.admin.setupper.DisasterCombineForm;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuprocessInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskmenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutasktypeInfoService;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

@jp.ecom_plat.saigaitask.action.RequestScopeController
public class DisasterCombineAction extends AbstractSetupperAction {


	@Resource protected GroupInfoService groupInfoService;
	@Resource protected MenuprocessInfoService menuprocessInfoService;
	@Resource protected MenutaskInfoService menutaskInfoService;
	@Resource protected MenutasktypeInfoService menutasktypeInfoService;
	@Resource protected MenuInfoService menuInfoService;
	@Resource protected MenutaskmenuInfoService menutaskmenuInfoService;

	/* ActionForm */
	DisasterCombineForm disasterCombineForm;

	/** 災害情報リスト */
	public List<DisasterMaster> disasterItems;
	/** 班情報リスト */
	public List<GroupInfo> groupInfoItems;
	/** メニュー階層リスト */
	public List<MenuloginInfo> menuloginInfoItems = new ArrayList<MenuloginInfo>();

	/** タスク名称リスト */
	public Map<Integer, List<String>> disasterprocessNames = new HashMap<Integer, List<String>>();
	/** サブタスク名称マップ */
	public Map<Integer, Map<String, List<String>>> disastertaskNames = new HashMap<Integer, Map<String, List<String>>>();
	/** メニュータスク種別名称マップ */
	public Map<Integer, Map<String, String>> disastertasktypeNames = new HashMap<Integer, Map<String, String>>();
	/** メニュー名称マップ */
	public Map<Integer, Map<String, List<MenuInfo>>> disastermenuNames = new HashMap<Integer, Map<String, List<MenuInfo>>>();


	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("disasterItems", disasterItems);
		model.put("groupInfoItems", groupInfoItems);
		model.put("menuloginInfoItems", menuloginInfoItems);
		model.put("disasterprocessNames", disasterprocessNames);
		model.put("disastertaskNames", disastertaskNames);
		model.put("disastertasktypeNames", disastertasktypeNames);
		model.put("disastermenuNames", disastermenuNames);

	}

	/**
	 * ページを表示する.
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/setupper/disasterCombine","/admin/setupper/disasterCombine/index"})
	public String index(Map<String,Object>model,
			@Valid @ModelAttribute DisasterCombineForm disasterCombineForm, BindingResult bindingResult) {
		this.disasterCombineForm = disasterCombineForm;
		content(model, disasterCombineForm, bindingResult);

		setupModel(model);
		return "/admin/setupper/disasterCombine/index";
	}

	/**
	 * ページを表示する.
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/disasterCombine/content")
	public String content(Map<String,Object>model,
			@Valid @ModelAttribute DisasterCombineForm disasterCombineForm, BindingResult bindingResult) {
		this.disasterCombineForm = disasterCombineForm;
		try {
			ActionMessages errors = new ActionMessages();

			// どの自治体にもログインしていない場合
			if(loginDataDto.getLocalgovinfoid()==0) {
				throw new ServiceException(lang.__("Create local gov. earlier."));
			}

			// 災害種別を取得
			disasterItems = disasterMasterService.findGtZeroOrderBy();
			// 災害種別が未選択の場合、再ログイン
			/*if(0<loginDataDto.getLocalgovinfoid() && loginDataDto.getDisasterid()==0) {
				loginLocalgovInfo(loginDataDto.getLocalgovinfoid());
			}*/

			if (loginDataDto.getLocalgovInfo().disastercombined) {
				errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Already disaster type integration completed."), false));
			}

			// 班情報
			groupInfoItems = groupInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());
			for (DisasterMaster disaster : disasterItems) {
				//タスク名称リスト
				List<String> menuprocessNames = new ArrayList<String>();
				//サブタスク名称マップ
				Map<String, List<String>> menutaskNames = new HashMap<String, List<String>>();
				//メニュータスク種別名称マップ
				Map<String, String> menutasktypeNames = new HashMap<String, String>();
				//メニュー名称マップ
				Map<String, List<MenuInfo>> menuNames = new HashMap<String, List<MenuInfo>>();
				for (GroupInfo group : groupInfoItems) {
					MenuloginInfo menulogin = null;
					List<MenuloginInfo> menulogins = menuloginInfoService.findByDisasterIdAndGroupInfoId(disaster.id, group.id);
					if (1<menulogins.size()) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Menu hierarchy of group info\"{0}\" are multiply registered. Reduce registry to one.", group.name), false));
					}
					if (0<menulogins.size()) menulogin = menulogins.get(0);
					if (menulogin == null) continue;
					menuloginInfoItems.add(menulogin);

					// タスク
					List<MenuprocessInfo> processList = menuprocessInfoService.findAllJoinMenuInfoOrderbyDisporder(menulogin.id);
					for (MenuprocessInfo process : processList) {
						if (!menuprocessNames.contains(process.name)) {
							menuprocessNames.add(process.name);
						}

						// タスク -> サブタスクリスト
						List<String> taskNames = menutaskNames.get(process.name);
						if (taskNames == null) taskNames = new ArrayList<String>();
						for (MenutaskInfo task : process.menutaskInfos) {
							if (!taskNames.contains(task.name)) {
								taskNames.add(task.name);
							}

							// サブタスク -> メニュータスク種別
							if(task.menutasktypeinfoid==null) continue;
							MenutasktypeInfo tasktype = menutasktypeInfoService.findById(task.menutasktypeinfoid);
							menutasktypeNames.put(task.name, tasktype.name);

							// メニュータスク種別 -> メニュー情報リスト
							List<MenuInfo> menuList = menuNames.get(tasktype.name);
							if (menuList == null) {
								menuList = new ArrayList<MenuInfo>();
								menuNames.put(tasktype.name, menuList);
							}

							// メニュー情報 -> メニュータスクメニュー情報
							Map<Long, MenutaskmenuInfo> menuidMap = disasterCombineForm.groupMenuMap.get(group.id);
							if (menuidMap == null) menuidMap = new HashMap<Long, MenutaskmenuInfo>();
							boolean cont = true;
							for (MenutaskmenuInfo tmenu : task.menutaskmenuInfos) {
								menuidMap.put(tmenu.menuinfoid, tmenu);
								MenuInfo menuInfo = menuInfoService.findById(tmenu.menuinfoid);
								for (MenuInfo minfo : menuList) {
									if (minfo.id.equals(menuInfo.id)) {
										cont = false;
										break;
									}
								}
								if (cont)
									menuList.add(menuInfo);
							}
							// 班ID -> (メニュー情報ID -> メニュータスクメニュー情報)
							disasterCombineForm.groupMenuMap.put(group.id, menuidMap);
						}
						menutaskNames.put(process.name, taskNames);
					}
				}
				disasterprocessNames.put(disaster.id, menuprocessNames);
				disastertaskNames.put(disaster.id, menutaskNames);
				disastertasktypeNames.put(disaster.id, menutasktypeNames);
				disastermenuNames.put(disaster.id, menuNames);
			}

			// エラーメッセージがあれば表示する
			if(0<errors.size()) {
				ActionMessagesUtil.addErrors(session, errors);
			}
		} catch (ServiceException e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Unable to display GroupMenu of city configuration."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(session, errors);
		}

		setupModel(model);
		return "/admin/setupper/disasterCombine/content";
	}

	/**
	 * 保存処理
	 * @return リダイレクト先
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/disasterCombine/update")
	public String update(Map<String,Object>model,
			@Valid @ModelAttribute DisasterCombineForm disasterCombineForm, BindingResult bindingResult) {
		this.disasterCombineForm = disasterCombineForm;

		ActionMessages errors = new ActionMessages();

		// 災害種別を取得
		disasterItems = disasterMasterService.findGtZeroOrderBy();
		// 班情報
		//Map<Long, Integer> dispMap = new HashMap<Long, Integer>();
		groupInfoItems = groupInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());

		int minDisp = 0;//災害の中の最小表示順
		int maxDisp = 1;//災害の中の最大表示順
		for (int i = 0; i < disasterItems.size(); i++) {
			DisasterMaster disaster = disasterItems.get(i);
			for (GroupInfo group : groupInfoItems) {
				//グループの表示順
				//Integer disp = dispMap.get(group.id);
				//if (disp == null) {
				//	disp = Integer.valueOf(0);
				//	dispMap.put(group.id, minDisp);
				//}

				MenuloginInfo menulogin = null;
				List<MenuloginInfo> menulogins = menuloginInfoService.findByDisasterIdAndGroupInfoId(disaster.id, group.id);
				if (1<menulogins.size()) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Menu hierarchy of group info\"{0}\" are multiply registered. Reduce registry to one.", group.name), false));
				}
				if (0<menulogins.size()) menulogin = menulogins.get(0);
				if (menulogin == null) continue;

				//コピー先のメニューログイン情報
				menulogins = menuloginInfoService.findByDisasterIdAndGroupInfoId(1, group.id);
				MenuloginInfo disasloginInfo = null;
				if (menulogins.size() > 0)
					disasloginInfo = menulogins.get(0);
				else {
					disasloginInfo = new MenuloginInfo();
					disasloginInfo.deleted = false;
					disasloginInfo.disasterid = 1;
					disasloginInfo.groupid = group.id;
					disasloginInfo.valid = true;
					menuloginInfoService.insert(disasloginInfo);
				}
				List<MenuprocessInfo> processList = menuprocessInfoService.findByMenuloginInfoId(menulogin.id);
				for (MenuprocessInfo process : processList) {
					if (i > 0) {//ずらす
						process.disporder += minDisp;
						process.menulogininfoid = disasloginInfo.id;
						menuprocessInfoService.update(process);
					}
					if (maxDisp < process.disporder) maxDisp = process.disporder;
				}
				if (menulogin.disasterid != 1) {//使用不能にする
					menulogin.deleted = true;
					menuloginInfoService.update(menulogin);
				}
			}
			minDisp = maxDisp;
		}

		//災害類型統合化済みとしてセット
		LocalgovInfo localgovInfo = loginDataDto.getLocalgovInfo();
		localgovInfo.disastercombined = true;
		localgovInfoService.update(localgovInfo);

		setupModel(model);
		return "forward:/admin/setupper/disasterCombine";
	}
}
