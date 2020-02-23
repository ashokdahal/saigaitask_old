/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import jp.ecom_plat.saigaitask.entity.db.AlertcontentData;
import jp.ecom_plat.saigaitask.form.AlertContentForm;
import jp.ecom_plat.saigaitask.service.db.AlertcontentDataService;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 気象情報などのアラート履歴を提供するアクションクラス
 * spring checked take 5/11
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController
public class AlertContentAction extends AbstractAction {

	/** アクションフォーム */
	protected AlertContentForm alertContentForm;

	@Resource
	protected AlertcontentDataService alertcontentDataService;
	
	public List<AlertcontentData> alertcontentItems;
	public static int line = 7;
	public long count = 0;

	public void setupModel(Map<String,Object>model) {
		super.setupModel(model);
		
		model.put("alertcontentItems", alertcontentItems);
		model.put("line", line);
		model.put("count", count);		
		model.put("alertContentForm", alertContentForm);
	}
	
    /**
     * 履歴画面
     * @param mode モード
     * @return 次ページ
     */
	@org.springframework.web.bind.annotation.RequestMapping(value={"/alertContent"})
    public String index(Map<String,Object>model, @Valid @ModelAttribute AlertContentForm alertContentForm) {
    	
    	int offset = alertContentForm.npage*line;
    	int limit = line;
    	
    	alertcontentItems = alertcontentDataService.findByLocalgovinfoid(loginDataDto.getLocalgovinfoid(), offset, limit);
    	
    	count = (long)Math.ceil(alertcontentDataService.getCount(loginDataDto.getLocalgovinfoid())/(double)line);
    	
    	setupModel(model);
    	
    	return "/alertContent/index";
    }

	@org.springframework.web.bind.annotation.RequestMapping(value={"/alertContent/page/{npage}"})
	@ResponseBody
    public String page(Map<String,Object>model, @Valid @ModelAttribute AlertContentForm alertContentForm) {
    	
    	int offset = alertContentForm.npage*line;
    	int limit = line;
    	alertcontentItems = alertcontentDataService.findByLocalgovinfoid(loginDataDto.getLocalgovinfoid(), offset, limit);

    	StringBuffer buff = new StringBuffer();
    	for (AlertcontentData data : alertcontentItems) {
    		buff.append("<tr>");
    		buff.append("<td>").append(data.id).append("</td>");
    		buff.append("<td>").append(data.teloptypeid).append("</td>");
    		buff.append("<td>").append(data.receivetime).append("</td>");
    		buff.append("<td>").append(data.title).append("</td>");
    		buff.append("<td>").append(data.content).append("</td>");
    		buff.append("</tr>");
    	}
    	
    	return buff.toString();
    }
}
