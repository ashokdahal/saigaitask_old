/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.admin.setupper;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;

import jp.ecom_plat.map.security.UserAuthorization;
import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskmenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutasktypeInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.form.admin.setupper.GroupMenuForm;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuprocessInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskmenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutasktypeInfoService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.util.SpringContext;

/**
 * 自治体セットアッパーの「ユーザ・メニュー階層」のメニューを表示するアクションクラス.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class GroupMenuAction extends AbstractSetupperAction {

	protected GroupMenuForm groupMenuForm;

	@Resource protected GroupInfoService groupInfoService;
	@Resource protected MenuprocessInfoService menuprocessInfoService;
	@Resource protected MenutaskInfoService menutaskInfoService;
	@Resource protected MenutasktypeInfoService menutasktypeInfoService;
	@Resource protected MenuInfoService menuInfoService;
	@Resource protected MenutaskmenuInfoService menutaskmenuInfoService;
	@Resource protected UnitInfoService unitInfoService;

    /** 災害情報リスト */
	public List<DisasterMaster> disasterItems;
	/** 班情報リスト */
	public List groupInfoItems;
	/** メニュー階層リスト */
	public List<MenuloginInfo> menuloginInfoItems = new ArrayList<MenuloginInfo>();

	//public Map<Long, List<MenuprocessInfo>> menuprocessInfoMap = new HashMap<Long, List<MenuprocessInfo>>();
	/** タスク名称リスト */
	public List<String> menuprocessNames = new ArrayList<String>();
	/** サブタスク名称マップ */
	public Map<String, List<String>> menutaskNames = new HashMap<String, List<String>>();
	/** メニュータスク種別名称マップ */
	public Map<String, String> menutasktypeNames = new HashMap<String, String>();
	/** メニュー名称マップ */
	public Map<String, List<MenuInfo>> menuNames = new HashMap<String, List<MenuInfo>>();
	/** 班IDとメニュータスクメニュー情報のマップ */
	public Map<Long, Map<Long, MenutaskmenuInfo>> groupMenuMap = new HashMap<Long, Map<Long, MenutaskmenuInfo>>();

	public boolean disasterCombined = false;

	public boolean usual = false;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("disasterItems", disasterItems);
		model.put("groupInfoItems", groupInfoItems);
		model.put("menuloginInfoItems", menuloginInfoItems);
		model.put("menuprocessNames", menuprocessNames);
		model.put("menutaskNames", menutaskNames);
		model.put("menutasktypeNames", menutasktypeNames);
		model.put("menuNames", menuNames);
		model.put("groupMenuMap", groupMenuMap);
		model.put("disasterCombined", disasterCombined);
		model.put("usual", usual);

	}

    /**
	 * ページを表示する.
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/admin/setupper/groupMenu","/admin/setupper/groupMenu/index"})
	public String index(Map<String,Object>model,
			@Valid @ModelAttribute GroupMenuForm groupMenuForm, BindingResult bindingResult) {
		this.groupMenuForm = groupMenuForm;
		initSetupper();

		content(model, groupMenuForm, bindingResult);

		setupModel(model);
		return "/admin/setupper/groupMenu/index";
	}

	/**
	 * ページを表示する.
	 * @return 表示ページ
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/groupMenu/content")
	public String content(Map<String,Object>model,
			@Valid @ModelAttribute GroupMenuForm groupMenuForm, BindingResult bindingResult) {
		this.groupMenuForm = groupMenuForm;
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
			disasterCombined = loginDataDto.getLocalgovInfo().disastercombined;
			usual = loginDataDto.isUsual();
			// 災害種別の指定がなければ、ログイン中の災害種別を指定
			if(groupMenuForm.disasterid==null || groupMenuForm.disasterid==0) groupMenuForm.disasterid = loginDataDto.getDisasterid();
			// 選択された災害種別をログイン情報に設定
			if(0<groupMenuForm.disasterid) loginDataDto.setDisasterid(groupMenuForm.disasterid);

			// 班情報
			if (!loginDataDto.isUsual()) {
				groupInfoItems = groupInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());
				for (int i = 0; i < groupInfoItems.size(); i++) {
					GroupInfo group = (GroupInfo)groupInfoItems.get(i);
					MenuloginInfo menulogin = null;
					List<MenuloginInfo> menulogins = menuloginInfoService.findByDisasterIdAndGroupInfoId(groupMenuForm.disasterid, group.id);
					if (1<menulogins.size()) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Menu hierarchy of group info\"{0}\" are multiply registered. Reduce registry to one.", group.name), false));
					}
					if (0<menulogins.size()) menulogin = menulogins.get(0);
					if (menulogin == null) continue;
					menuloginInfoItems.add(menulogin);

					contentMenu(menulogin, group.id);
				}
			}
			else {
				groupInfoItems = unitInfoService.findByLocalgovInfoIdAndValid(loginDataDto.getLocalgovinfoid());
				for (int i = 0; i < groupInfoItems.size(); i++) {
					UnitInfo unit = (UnitInfo)groupInfoItems.get(i);
					MenuloginInfo menulogin = null;
					List<MenuloginInfo> menulogins = menuloginInfoService.findByUnitInfoId(unit.id);
					if (1<menulogins.size()) {
						errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Menu hierarchy of group info\"{0}\" are multiply registered. Reduce registry to one.", unit.name), false));
					}
					if (0<menulogins.size()) menulogin = menulogins.get(0);
					if (menulogin == null) continue;
					menuloginInfoItems.add(menulogin);

					contentMenu(menulogin, unit.id);
				}
			}

			// メニュータスク種別から、まだ出現していないメニュー情報を追加する
			List<MenutasktypeInfo> tasktypeList = menutasktypeInfoService.findByLocalgovinfoid(loginDataDto.getLocalgovinfoid(), false);
			if(tasktypeList.size()==0) throw new ServiceException(lang.__("There is no setting of menu task type. <br/>Please set on the admin window."));
			for (MenutasktypeInfo type : tasktypeList) {
				// JSPで設定対象のメニュー情報リスト
				List<MenuInfo> mlist = menuNames.get(type.name);

				// 未追加なら追加する
				if (mlist != null) {
					// メニュータスク種別からメニュー情報リストを取得
					List<MenuInfo> menuList = menuInfoService.findByMenutasktypeinfoid(type.id);
					for (MenuInfo menuInfo : menuList) {
						boolean cont = false;
						for (MenuInfo minfo : mlist) {
							if (menuInfo.id.equals(minfo.id)) {
								cont = true;
								break;
							}
						}
						if (!cont) mlist.add(menuInfo);
					}
				}
				else {
					// TODO: まだメニュータスク種別がどの班にも設定されていない場合
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Task type {0}: \"{1}\" is not set in any group's menu hierarchy.", type.id, type.name), false));
				}
			}

			// エラーメッセージがあれば表示する
			if(0<errors.size()) {
				ActionMessagesUtil.addErrors(bindingResult, errors);
			}
		} catch (ServiceException e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Unable to display GroupMenu of city configuration."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		setupModel(model);
		return "/admin/setupper/groupMenu/content";
	}

	protected void contentMenu(MenuloginInfo menulogin, Long groupid) {
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
				Map<Long, MenutaskmenuInfo> menuidMap = groupMenuMap.get(groupid);
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
				groupMenuMap.put(groupid, menuidMap);
			}
			menutaskNames.put(process.name, taskNames);
		}

	}

	/**
	 * 保存処理
	 * @return リダイレクト先
	 */
	@org.springframework.web.bind.annotation.RequestMapping("/admin/setupper/groupMenu/update")
	public String update(Map<String,Object>model,
		@ModelAttribute GroupMenuForm groupMenuForm, BindingResult bindingResult) {
		this.groupMenuForm = groupMenuForm;

		try {
			// 新規登録の場合、DB登録後に発行されたIDからリクエストパラメータのIDに変換する
			DualHashBidiMap requestId2dbId = new DualHashBidiMap();
			// リクエストパラメータから班のデータを取得
			if(groupMenuForm.groupid!=null) {
				int maxdisp = 0;
				if (!loginDataDto.isUsual())
					maxdisp = groupInfoService.getLargestDisporderByLocalGovInfoID(loginDataDto.getLocalgovinfoid());
				else
					maxdisp = unitInfoService.getLargestDisporderByLocalGovInfoID(loginDataDto.getLocalgovinfoid());
				for(int idx=0; idx<groupMenuForm.groupid.size(); idx++) {
					Long requestGroupId = Long.valueOf(groupMenuForm.groupid.get(idx));
					String name = groupMenuForm.groupname.get(idx);
					// id がマイナスのときは新規作成
					if (!loginDataDto.isUsual()) {
						if(requestGroupId<0) {
							GroupInfo groupInfo = new GroupInfo();
							groupInfo.name = name;
							groupInfo.localgovinfoid = loginDataDto.getLocalgovinfoid();
							groupInfo.admin = false;      // 管理権限
							groupInfo.headoffice = false; // 本部権限
							groupInfo.ecomuser = null;
							try {
								groupInfo.password = UserAuthorization.getEncryptedPass("password");
							} catch (NoSuchAlgorithmException e) {
								throw new ServiceException(lang.__("Unable to encrypt password."));
							}
							groupInfo.disporder = ++maxdisp;
							groupInfo.valid = true;
							groupInfo.deleted = false;
							groupInfoService.insert(groupInfo);
							requestId2dbId.put(requestGroupId, groupInfo.id);
						}
						// 存在する班
						else {
							GroupInfo groupInfo = groupInfoService.findById(requestGroupId);
							groupInfo.name = name;
							groupInfoService.update(groupInfo);
						}
					}
					else {
						if(requestGroupId<0) {
							UnitInfo unitInfo = new UnitInfo();
							unitInfo.name = name;
							unitInfo.localgovinfoid = loginDataDto.getLocalgovinfoid();
							unitInfo.admin = false;      // 管理権限
							unitInfo.ecomuser = null;
							try {
								unitInfo.password = UserAuthorization.getEncryptedPass("password");
							} catch (NoSuchAlgorithmException e) {
								throw new ServiceException(lang.__("Unable to encrypt password."));
							}
							unitInfo.disporder = ++maxdisp;
							unitInfo.valid = true;
							unitInfo.deleted = false;
							unitInfoService.insert(unitInfo);
							requestId2dbId.put(requestGroupId, unitInfo.id);
						}
						// 存在する班
						else {
							UnitInfo unitInfo = unitInfoService.findById(requestGroupId);
							unitInfo.name = name;
							unitInfoService.update(unitInfo);
						}
					}
				}
			}
			// 班情報の削除
			if(groupMenuForm.deletegroupid!=null) {
				for(String deletegroupidStr : groupMenuForm.deletegroupid) {
					long deleteGroupId = Long.parseLong(deletegroupidStr);
					if (!loginDataDto.isUsual()) {
						GroupInfo groupInfo =  groupInfoService.findById(deleteGroupId);
						groupInfo.deleted = true;
						groupInfoService.update(groupInfo);
					}
					else {
						UnitInfo unitInfo =  unitInfoService.findById(deleteGroupId);
						unitInfo.deleted = true;
						unitInfoService.update(unitInfo);
					}
				}
			}

			// リクエストパラメータから班で使用するメニューを取得
			Map<Long, List<Long>> groupMenu = new HashMap<Long, List<Long>>();
			if(groupMenuForm.menu!=null) {
				for (String val : groupMenuForm.menu) {
					int idx = val.indexOf(':');
					int idx2 = val.indexOf(':', idx+1);
					long requestGroupId = Long.parseLong(val.substring(idx+1, idx2));
					long groupid = requestGroupId<0 ? (Long) requestId2dbId.get(requestGroupId) : requestGroupId; // マイナスの場合は、新規登録で仮IDになっているので変換する
					Long menuid = Long.parseLong(val.substring(idx2+1));

					List<Long> menuidList = groupMenu.get(groupid);
					if (menuidList == null) menuidList = new ArrayList<Long>();
					groupMenu.put(groupid, menuidList);
					menuidList.add(menuid);
				}
			}

			// 班ごとにメニュー階層を更新
			if (!loginDataDto.isUsual())
				groupInfoItems = groupInfoService.findByLocalgovInfoIdAll(loginDataDto.getLocalgovinfoid());
			else
				groupInfoItems = unitInfoService.findByLocalgovInfoIdAll(loginDataDto.getLocalgovinfoid());
			//for (GroupInfo group : groupInfoItems) {
			for (int i = 0; i < groupInfoItems.size(); i++) {
				long groupid = 0;
				if (!loginDataDto.isUsual()) {
					GroupInfo info = ((GroupInfo)groupInfoItems.get(i));
					if(info.deleted==true) continue;
					groupid = ((GroupInfo)groupInfoItems.get(i)).id;
				}
				else {
					UnitInfo info = ((UnitInfo)groupInfoItems.get(i));
					if(info.deleted==true) continue;
					groupid = info.id;
				}
				List<Long> menuidList = groupMenu.get(groupid);

				// メニューログイン情報の取得
				MenuloginInfo menulogin = null;
				List<MenuloginInfo> menulogins = null;
				if (!loginDataDto.isUsual())
					menulogins = menuloginInfoService.findByDisasterIdAndGroupInfoId(groupMenuForm.disasterid, groupid);
				else
					menulogins = menuloginInfoService.findByUnitInfoId(groupid);
				if(0<menulogins.size()) menulogin = menulogins.get(0);
				if (menuidList == null && menulogin == null) continue;//何もない
				if (menulogin == null) { // なければ作成
					menulogin = new MenuloginInfo();
					menulogin.disasterid = groupMenuForm.disasterid;
					if (!loginDataDto.isUsual())
						menulogin.groupid = groupid;
					else
						menulogin.unitid = groupid;
					menulogin.valid = true;
					menulogin.deleted = false;
					menuloginInfoService.insert(menulogin);
				}

				// 現在登録中のタスクリスト
				List<MenuprocessInfo> processList = menuprocessInfoService.findAllJoinMenuInfoOrderbyDisporder(menulogin.id);
				// 削除から
				if (menuidList == null) {
					//メニュー削除
					deleteAll(menulogin, processList);
				}
				else {
					//タスクへメニューの追加
					Map<Long, Integer> disporders = new HashMap<Long, Integer>();
					for(Long menuid : menuidList) {
						Long grpid = menulogin.groupid;
						if (loginDataDto.isUsual()) grpid = menulogin.unitid;
						long requestGroupId = requestId2dbId.containsValue(grpid) ? (Long)requestId2dbId.getKey(grpid) : grpid;
						String name = "d:"+requestGroupId+":"+menuid;
						String dorder = request.getParameter(name);
						if (StringUtil.isNotEmpty(dorder)) {
							disporders.put(menuid, Integer.valueOf(dorder));
						}
					}

					//更新、削除
					updateMenu(menulogin, processList, menuidList, disporders);

					//追加
					addMenu(menulogin, processList, menuidList, disporders);
				}
			}
		} catch (ServiceException e) {
			logger.error(loginDataDto.logInfo()+ "\n" + lang.__("Unable to update."), e);
			ActionMessages errors = new ActionMessages();
			errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(e.getMessage(), false));
			ActionMessagesUtil.addErrors(bindingResult, errors);
		}

		setupModel(model);
		return "forward:index";
	}

	protected void addMenu(MenuloginInfo menulogin, List<MenuprocessInfo> processList, List<Long> menuidList, Map<Long, Integer> disporders) {
		for (Long menuid : menuidList) {
			MenuInfo menu = menuInfoService.findById(menuid);

			//存在チェック
			boolean exist = false;
			MenutaskInfo mytask = null;
			for (MenuprocessInfo process : processList) {
				for (MenutaskInfo task : process.menutaskInfos) {
					if (!task.menutasktypeinfoid.equals(menu.menutasktypeinfoid))
						continue;

					mytask = task;
					for (MenutaskmenuInfo tmenu : task.menutaskmenuInfos) {
						if (menu.id.equals(tmenu.menuinfoid)) {
							exist = true;
							break;
						}
					}
					if (exist) break;
				}
				if (exist) break;
			}

			//新規
			if (!exist) {
				//プロセス、タスクがない
				if (mytask == null) {
					//プロセス有無確認
					MenutaskInfo task2 = menutaskInfoService.findBymenutasktypeinfoid(menu.menutasktypeinfoid);
					MenuprocessInfo process2 = menuprocessInfoService.findById(task2.menuprocessinfoid);

					MenuprocessInfo pros = null;
					for (MenuprocessInfo process : processList) {
						if (process.name.equals(process2.name)) {
							pros = process;
							break;
						}
					}

					//プロセスがなければ追加
					if (pros == null) {
						pros = new MenuprocessInfo();
						pros.disporder = process2.disporder;
						pros.menulogininfoid = menulogin.id;
						pros.name = process2.name;
						pros.valid = true;
						pros.visible = true;
						pros.deleted = false;
						menuprocessInfoService.insert(pros);
						processList.add(pros);
					}

					//タスク追加
					mytask = new MenutaskInfo();
					mytask.disporder = task2.disporder;
					mytask.menuprocessinfoid = pros.id;
					mytask.menutasktypeinfoid = task2.menutasktypeinfoid;
					mytask.name = task2.name;
					mytask.valid = true;
					mytask.visible = true;
					mytask.deleted = false;
					menutaskInfoService.insert(mytask);
					if (pros.menutaskInfos == null) pros.menutaskInfos = new ArrayList<MenutaskInfo>();
					pros.menutaskInfos.add(mytask);
				}

				//タスクへメニューの追加
				MenutaskmenuInfo tmenu = new MenutaskmenuInfo();
				tmenu.menuinfoid = menuid;
				tmenu.menutaskinfoid = mytask.id;
				tmenu.disporder = 1;
				if(disporders.get(menuid)!=null) tmenu.disporder = disporders.get(menuid);
				menutaskmenuInfoService.insert(tmenu);
				if (mytask.menutaskmenuInfos == null) mytask.menutaskmenuInfos = new ArrayList<MenutaskmenuInfo>();
				mytask.menutaskmenuInfos.add(tmenu);
			}
		}
	}

	protected void updateMenu(MenuloginInfo menulogin, List<MenuprocessInfo> processList, List<Long> menuidList, Map<Long, Integer> disporders) {
		for (MenuprocessInfo process : processList) {
			for (MenutaskInfo task : process.menutaskInfos) {
				for (MenutaskmenuInfo tmenu : task.menutaskmenuInfos) {
					if (!menuidList.contains(tmenu.menuinfoid))
						menutaskmenuInfoService.delete(tmenu);
					else {
						if(disporders.get(tmenu.menuinfoid)!=null) {
							int disporder = disporders.get(tmenu.menuinfoid);
							//表示順が変わった
							if (tmenu.disporder != disporder) {
								tmenu.disporder = disporder;
								menutaskmenuInfoService.update(tmenu);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * タスクメニューをすべて削除する.
	 * @param menulogin
	 * @param processList
	 */
	protected void deleteAll(MenuloginInfo menulogin, List<MenuprocessInfo> processList) {
		for (MenuprocessInfo process : processList) {
			for (MenutaskInfo task : process.menutaskInfos) {
				for (MenutaskmenuInfo tmenu : task.menutaskmenuInfos) {
					menutaskmenuInfoService.delete(tmenu);
				}
			}
		}
	}
}
