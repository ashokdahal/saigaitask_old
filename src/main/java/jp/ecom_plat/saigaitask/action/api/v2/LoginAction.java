/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.action.api.v2;

import static jp.ecom_plat.saigaitask.service.LoginService.MODE_TASK;
import static jp.ecom_plat.saigaitask.service.LoginService.MODE_USUAL;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ecom_plat.saigaitask.action.api.AbstractApiAction;
import jp.ecom_plat.saigaitask.entity.db.MultilangInfo;
import jp.ecom_plat.saigaitask.form.LoginForm;
import jp.ecom_plat.saigaitask.security.AuthenticationToken;
import jp.ecom_plat.saigaitask.security.sample.MyUserDetailsService;
import jp.ecom_plat.saigaitask.service.LoginService;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

/**
 * 認証API：ログイン
 */
@jp.ecom_plat.saigaitask.action.RequestScopeController("/api/v2/login")
public class LoginAction extends AbstractApiAction {
	@Resource
	public LoginService loginService;
	@Resource
	public MyUserDetailsService myUserDetailsService;

	@RequestMapping(value={"/api/v2/login"})
	@ResponseBody
    public String index() {
		apiDto.authorize();
		
    	if (isGetMethod()) {
    		// results
    		JSONObject result = new JSONObject();
    		result.put("login", loginService.isLoggedIn());
    		result.put("pagelogin",loginService.isPageLoggedIn());
    		result.put("adminlogin", loginService.isAdminLoggedIn());
    		result.put("jsessionid",session.getId());
    		Long accessTime = session.getLastAccessedTime();
			TimeZone tz = TimeZone.getTimeZone("UTC");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
			df.setTimeZone(tz);
			String timeout = df.format(new Date(accessTime));
			result.put("timeout", timeout);
			result.put("timeoutMaxInactiveInterval", session.getMaxInactiveInterval());
    		result.put("localgovinfoid",loginDataDto.getLocalgovinfoid());
    		result.put("groupid", loginDataDto.getGroupid());
    		result.put("unitid", loginDataDto.getUnitid());
    		result.put("disasterid", loginDataDto.getDisasterid());
    		result.put("trackdataid", loginDataDto.getTrackdataid());
    		result.put("trackmapinfoid", loginDataDto.getTrackmapinfoid());
    		result.put("admin", loginDataDto.isAdmin());
    		return responseJSONObject(result);
    	} else if (isPostMethod()) {
    		if(request.getContentType().startsWith("application/json")){
    			// results
    			JSONObject result = new JSONObject();
    			LoginForm loginForm = new LoginForm();
    			// リクエストデータを取得
    			JSONObject jsonObject = toJSONObject(apiDto.getRequestData());
				// 必須パラメータチェック
    			if(apiDto.isGroupLogin()) {
    				if(jsonObject.containsKey("trackdataid") == false || JSONNull.getInstance().equals(jsonObject.get("trackdataid"))){
    					return Response.sendJSONError("invalid parameter: trackdataid", HttpServletResponse.SC_BAD_REQUEST).execute(response);
    				}
    				else {
    	    			loginForm.trackdataid = jsonObject.getString("trackdataid");
    				}
    			}
				if(jsonObject.has("langCode")){
					loginForm.langCode = jsonObject.getString("langCode");
				}
				else{
					MultilangInfo langCode = null;
					if(apiDto.isGroupLogin()) {
						langCode = multilangInfoService.findById(apiDto.getGroupInfo().localgovInfo.multilanginfoid);
					}
					if(apiDto.isUnitLogin()) {
						langCode = multilangInfoService.findById(apiDto.getUnitInfo().localgovInfo.multilanginfoid);
					}
					if(langCode != null){
						loginForm.langCode = langCode.code;
					}
				}
				// リクエストデータにdisasteridがない場合はLoginService内で自動設定
    			if(jsonObject.has("disasterid")){
    				loginForm.disasterid = jsonObject.getString("disasterid");
    			}
    			loginForm.returnpath = "/page/";
    			if(apiDto.isGroupLogin()) {
    				loginForm.mode = MODE_TASK;
        			loginService.login(MODE_TASK, loginForm, apiDto.getGroupInfo());
    			}
    			if(apiDto.isUnitLogin()) {
    				loginForm.mode = MODE_USUAL;
        			loginService.login(MODE_USUAL, loginForm, apiDto.getUnitInfo());
    			}
    			// Spring Security にログイン
    			if(loginService.isLoggedIn()) {
    				loginForm.localgovinfoid = String.valueOf(loginDataDto.getLocalgovinfoid());
    				if(apiDto.isGroupLogin()) loginForm.groupid = apiDto.getGroupInfo().getName();
    				if(apiDto.isUnitLogin()) loginForm.unitid = apiDto.getUnitInfo().getName();
    				UserDetails  principal = myUserDetailsService.loadUserByUsername(loginForm);
    				Authentication authentication = new AuthenticationToken(principal, null, principal.getAuthorities());
    				SecurityContextHolder.getContext().setAuthentication(authentication);
    			}
    			result.put("login", loginService.isLoggedIn());	
    			result.put("pagelogin",loginService.isPageLoggedIn());
    			result.put("adminlogin", loginService.isAdminLoggedIn());
    			result.put("jsessionid",session.getId());
    			Long accessTime = session.getLastAccessedTime();
    			TimeZone tz = TimeZone.getTimeZone("UTC");
    			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
    			df.setTimeZone(tz);
    			String timeout = df.format(new Date(accessTime));
    			result.put("timeout", timeout);
    			result.put("timeoutMaxInactiveInterval", session.getMaxInactiveInterval());
    			result.put("localgovinfoid",loginDataDto.getLocalgovinfoid());
    			result.put("groupid", loginDataDto.getGroupid());
    			result.put("unitid", loginDataDto.getUnitid());
    			result.put("disasterid", loginDataDto.getDisasterid());
    			result.put("trackdataid", loginDataDto.getTrackdataid());
    			result.put("trackmapinfoid", loginDataDto.getTrackmapinfoid());
    			result.put("admin", loginDataDto.isAdmin());
    			return responseJSONObject(result);
    		}
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
		} else {
			response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}
		return null;
	}
}
