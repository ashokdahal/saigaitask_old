package jp.ecom_plat.saigaitask.action.admin;

import javax.annotation.Resource;

import org.seasar.framework.util.StringUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.LocalgovInfo;
import jp.ecom_plat.saigaitask.service.CkanService;
import jp.ecom_plat.saigaitask.util.CKANUtil;
import jp.ecom_plat.saigaitask.util.Config;

@jp.ecom_plat.saigaitask.action.RequestScopeController
@RequestMapping("/admin/ckan")
public class CkanAction extends AbstractAdminAction<LocalgovInfo> {
	
	@Resource
	protected CkanService ckanService;

	/**
	 * CKANで地図を検索するAJAXサービス。
	 *
	 * @throws ServiceException
	 */
	@RequestMapping(value="/search", method=org.springframework.web.bind.annotation.RequestMethod.POST)
	@ResponseBody
	public String search() throws ServiceException {
   	try {
		String keyword = (String)request.getParameter("keyword");
		String orderby = (String)request.getParameter("orderby");
		String startPosition = (String)request.getParameter("startPosition");
		String maxRecords = (String)request.getParameter("maxRecords");
		Boolean isTraining= "true".equalsIgnoreCase(request.getParameter("training"));

		if (orderby == null) orderby = "5";
		if (StringUtil.isEmpty(startPosition)) startPosition = "1";

    	logger.info("[search clearinghouse] keyword:" + keyword + ", orderby:" + orderby + ", startPosition:" + startPosition + ", maxRecords:" + maxRecords);

    	try {
			// 災害モードと訓練モードでクリアリングハウスの登録先を変更する
			String ckanurl = isTraining ? Config.getString("CKAN_URL_TRAINING") : Config.getString("CKAN_URL");

    		String json =  CKANUtil.getRecords(ckanurl, keyword, null, Integer.parseInt(startPosition), Integer.parseInt(maxRecords), Integer.parseInt(orderby));
    		json = json.replace("\n", "<br/>");
    		responseService.response(json);
    	}
		catch (Exception ex) {
			logger.error(ex.getMessage());
		}

	} catch(Exception ex){
		logger.error(ex.getMessage());
	}

	return null;
}

}
