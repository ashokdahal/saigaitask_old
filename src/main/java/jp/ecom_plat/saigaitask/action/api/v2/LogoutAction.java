/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.service.LoginService;
import jp.ecom_plat.saigaitask.service.db.LoginDataService;
import net.sf.json.JSONObject;

@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/logout")
public class LogoutAction extends AbstractApiAction{
	/** ログアウトデータサービス */
	@Resource
	protected LoginDataService loginDataService;
	public Long trackdatid;
	@Resource
	protected LoginService loginService;

	@RequestMapping(value={"/api/v2/logout"})
	@ResponseBody
	public String index(){
		apiDto.authorize();
		
		if(isPostMethod()){
			//if(request.getContentType().startsWith("application/json")){
				loginService.logout(loginDataDto.getLogindataid());
				
				try {
					request.logout();
				} catch (ServletException e) {
					logger.error("failed to logout", e);
				}
				
				JSONObject result = new JSONObject();
				result.put("login", false);
	    		result.put("pagelogin",false);
	    		result.put("adminlogin", false);
				return responseJSONObject(result);
			//}
		}
		else{
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		return null;
	}
}
