package jp.ecom_plat.saigaitask.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    public static ApplicationContext getApplicationContext() {
        return context;
    }
    
    /**
     * リクエストを返します。
     * 
     * @return リクエスト
     */
    public static HttpServletRequest getRequest() {
    	
    	ServletRequestAttributes servletRequestAttributes = 
    			(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    	if(servletRequestAttributes!=null) {
    		return servletRequestAttributes.getRequest();
    	}
    		
    	return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext ac)
            throws BeansException {
        context = ac;
    }
}
