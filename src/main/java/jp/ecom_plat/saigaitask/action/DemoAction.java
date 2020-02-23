/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.seasar.framework.util.StringUtil;
import org.seasar.struts.util.ActionMessagesUtil;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jp.ecom_plat.map.util.FormUtils;
import jp.ecom_plat.saigaitask.entity.db.DemoInfo;
import jp.ecom_plat.saigaitask.entity.db.DisasterMaster;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.entity.db.MeteorequestInfo;
import jp.ecom_plat.saigaitask.form.DemoForm;
import jp.ecom_plat.saigaitask.service.MeteoricAlarmService;
import jp.ecom_plat.saigaitask.service.db.DemoInfoService;
import jp.ecom_plat.saigaitask.service.db.DisasterMasterService;
import jp.ecom_plat.saigaitask.service.db.MeteorequestInfoService;

/**
 * 気象庁のデモデータを流すアクションクラス.
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class DemoAction extends AbstractAction {

    /** 災害情報リスト */
	public List<DisasterMaster> disasterItems;
	/** 災害情報マスターサービス */
    @Resource
    protected DisasterMasterService disasterMasterService;

	/** デモ情報 */
	@Resource
	protected DemoInfoService demoInfoService;
	/** デモ情報リスト */
	public List<DemoInfo> demoInfoItems;
	/** 災害種別マスタ */
	public Map<Integer, String> disasterMap = new HashMap<Integer, String>();

	@Resource
	protected MeteoricAlarmService meteoricAlarmService;
	@Resource
	protected MeteorequestInfoService meteorequestInfoService;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);

		model.put("disasterItems", disasterItems);
		model.put("demoInfoItems", demoInfoItems);
		model.put("disasterMap", disasterMap);
	}

	/**
     * デモ開始画面
     * @param mode モード
     * @return 次ページ
     */
	@RequestMapping(value={"/demo", "/demo/index"})
    public String index(Map<String,Object>model,
			@ModelAttribute DemoForm demoForm, BindingResult bindingResult) {

		ActionMessages errors = new ActionMessages();

		//StringBuffer buff = request.getRequestURL();
    	//String url2 = buff.toString();
    	String url2 = request.getServerName();
    	LocalgovInfo gov = localgovInfoService.findByURLAndValid(url2);
    	// 自治体IDが指定してあれば、切り替える
    	if(StringUtil.isNotEmpty(demoForm.localgovinfoid)) {
    		LocalgovInfo localgovInfo = localgovInfoService.findById(Long.parseLong(demoForm.localgovinfoid));
    		if(localgovInfo!=null) {
    			gov = localgovInfo;
    		}
    	}

    	if (gov == null) {
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Unable to get local gov. info."), false));
        	return "/demo/index";
    	}

    	if (!gov.autostart)
        	errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage(lang.__("Auto alarm activation is invalid."), false));

    	demoInfoItems = demoInfoService.findByLocalgovInfoId(gov.id);

		disasterItems = disasterMasterService.findAllOrderBy();
		for (DisasterMaster dis : disasterItems)
			disasterMap.put(dis.id, dis.name);

		ActionMessagesUtil.addErrors(bindingResult, errors);

		setupModel(model);
    	return "/demo/index";
    }

    /**
     * デモ開始実行
     * @return 成功した場合、ログイン画面。失敗した場合はデモ画面へ遷移
     */
	@RequestMapping("/demo/start")
    public String start(Map<String,Object>model,
			@ModelAttribute DemoForm demoForm, BindingResult bindingResult) {
    	
    	
    	// CSRF対策
    	if (!FormUtils.checkToken(request)) {
			throw new InvalidAccessException(lang.__("Invalid session."));
    	}

    	long did = Long.parseLong(demoForm.id);

    	DemoInfo demoInfo = demoInfoService.findById(did);
    	meteoricAlarmService.init(false);
    	MeteorequestInfo reqInfo = meteorequestInfoService.findById(demoInfo.meteorequestinfoid);

    	LocalgovInfo gov = localgovInfoService.findById(demoInfo.localgovinfoid);
    	if (!gov.autostart) {
    		return "forward:/demo/";
    	}

    	try {
        	meteoricAlarmService.readXMLFileUrl(reqInfo, demoInfo.triggerurl, true, demoInfo.id);
		} catch (Exception e) {
			logger.error("DemoAction.start",e);
		}

    	return "redirect:/";
    }
}
