package jp.ecom_plat.saigaitask.interceptor;

import java.net.URLDecoder;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import jp.ecom_plat.saigaitask.dto.LoginDataDto;
import jp.ecom_plat.saigaitask.util.SpringContext;
import net.sf.json.JSONObject;

/**
 * 地図画面の表示状態を保持するインターセプタ
 */
public class RememberLocationInterceptor extends HandlerInterceptorAdapter {

	/** Logger */
	protected Logger logger = Logger.getLogger(RememberLocationInterceptor.class);

    @Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		// 地図画面にて最後に表示していた位置情報を取得
		Boolean centerCookie = (Boolean) request.getAttribute("rememberLocation");
		if(centerCookie==null || !centerCookie) { // すでに処理済みかチェック
			try {
				// parse cookie
				String location = null;
				Cookie[] cookies = request.getCookies();
				if(cookies!=null) {
					for(Cookie cookie : cookies) {
						if("location".equals(cookie.getName())) {
							location = URLDecoder.decode(cookie.getValue(), "UTF-8");
							if(location!=null) {
								logger.debug("location: "+location);
								// 次回の地図表示に利用するため、セッションに保存
								LoginDataDto loginDataDto = SpringContext.getApplicationContext().getBean(LoginDataDto.class);
								if(loginDataDto!=null) {
									loginDataDto.setLocation(JSONObject.fromObject(location));
								}
								// remove cookie
								cookie.setMaxAge(0);
								cookie.setPath("/");
								response.addCookie(cookie);
							}
						}
					}
				}
			} catch(Exception e) {
				logger.error("failed to parse cookie.", e);
			}
			// 処理済フラグ
			request.setAttribute("rememberLocation", true);
		}

	}
}
