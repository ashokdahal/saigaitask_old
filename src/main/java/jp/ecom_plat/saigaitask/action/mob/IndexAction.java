/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.mob;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import jp.ecom_plat.saigaitask.action.AbstractAction;
import jp.ecom_plat.saigaitask.constant.Menutype;
import jp.ecom_plat.saigaitask.entity.db.MenuInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuloginInfo;
import jp.ecom_plat.saigaitask.entity.db.MenuprocessInfo;
import jp.ecom_plat.saigaitask.entity.db.MenutaskInfo;
import jp.ecom_plat.saigaitask.form.mob.MobForm;
import jp.ecom_plat.saigaitask.service.db.MenuInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuloginInfoService;
import jp.ecom_plat.saigaitask.service.db.MenuprocessInfoService;
import jp.ecom_plat.saigaitask.service.db.MenutaskInfoService;

import org.springframework.web.bind.annotation.ModelAttribute;

@jp.ecom_plat.saigaitask.action.RequestScopeController("/mob")
public class IndexAction extends AbstractAction {

	/** アクションフォーム */
	protected MobForm mobForm;

    @Resource
	protected MenuloginInfoService menuloginInfoService;
	@Resource
	protected MenuprocessInfoService menuprocessInfoService;
	public List<MenuprocessInfo> menuprocessInfoItems;

	@Resource
	protected MenutaskInfoService menutaskInfoService;
	public List<MenutaskInfo> menutaskInfoItems;

	@Resource
	protected MenuInfoService menuInfoService;
	public List<MenuInfo> menuInfoItems;
	
	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		model.put("menuprocessInfoItems", menuprocessInfoItems);
		model.put("menutaskInfoItems", menutaskInfoItems);
		model.put("menuInfoItems", menuInfoItems);
		model.put("mobForm", mobForm);
	}

    @org.springframework.web.bind.annotation.RequestMapping(value="/mob/process")
    public String process(Map<String,Object>model) {
		MenuloginInfo login = null;
		if (!loginDataDto.isUsual())
			login = menuloginInfoService.findByGroupInfoIdAndNotDeleted(loginDataDto.getGroupid());
		else
			login = menuloginInfoService.findByUnitInfoIdAndNotDeleted(loginDataDto.getUnitid());
		if (login != null)
			menuprocessInfoItems = menuprocessInfoService.findByMenuloginInfoIdAndValidAndNotDeleted(login.id);

		setupModel(model);
		
    	return "/mob/process";
    }
    
    @org.springframework.web.bind.annotation.RequestMapping(value="/mob/task/{processid}")
	public String task(Map<String,Object>model, @Valid @ModelAttribute MobForm mobForm) {
    	this.mobForm = mobForm;
    	long processid = mobForm.processid;
    	//MenuprocessInfo menuprocessInfo = menuprocessInfoService.findById(processid);
    	menutaskInfoItems = menutaskInfoService.findByMenuprocessInfoIdAndValidAndNotDeleted(processid);

		setupModel(model);

    	return "/mob/task";
    }

    @org.springframework.web.bind.annotation.RequestMapping(value="/mob/menu/{taskid}")
	public String menu(Map<String,Object>model, @Valid @ModelAttribute MobForm mobForm) {
    	this.mobForm = mobForm;
    	long taskid = mobForm.taskid;
    	//MenuprocessInfo menuprocessInfo = menuprocessInfoService.findById(processid);
    	List<MenuInfo> menulist = menuInfoService.findByMenutaskinfoidOrderbyDisporder(taskid);
    	menuInfoItems = new ArrayList<MenuInfo>();

    	for (MenuInfo menuInfo : menulist) {
    		if (menuInfo.menutypeid.equals(Menutype.LIST) ||
    				menuInfo.menutypeid.equals(Menutype.LIST_WITH_MAP) ||
    				menuInfo.menutypeid.equals(Menutype.MAP_WITH_LIST)) {
    			menuInfoItems.add(menuInfo);
    		}
    	}

		setupModel(model);

    	return "/mob/menu";
    }

}
