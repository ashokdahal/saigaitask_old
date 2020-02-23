package jp.ecom_plat.saigaitask.security.sample;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/**
 * @see http://qiita.com/kazuki43zoo/items/e925f134e65d7595aa3c#%E7%8B%AC%E8%87%AA%E3%81%AEdb%E8%AA%8D%E8%A8%BC%E7%94%A8%E3%81%AEuserdetailsservice%E3%82%92spring-security%E3%81%AB%E9%81%A9%E7%94%A8%E3%81%99%E3%82%8B 
 * @author oku
 *
 */

import jp.ecom_plat.saigaitask.action.ServiceException;
import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import jp.ecom_plat.saigaitask.form.LoginForm;
import jp.ecom_plat.saigaitask.service.LoginService;
import jp.ecom_plat.saigaitask.service.db.GroupInfoService;
import jp.ecom_plat.saigaitask.service.db.UnitInfoService;
import jp.ecom_plat.saigaitask.util.StringUtil;
@Service
public class MyUserDetailsService implements UserDetailsService {

	@Resource NamedParameterJdbcOperations namedParameterJdbcOperations;
	@Resource GroupInfoService groupInfoService;
	@Resource UnitInfoService unitInfoService;

	@Override
	public UserDetails loadUserByUsername(String loginFormString) throws UsernameNotFoundException {
        try {
        	LoginForm loginForm;
			try {
				loginForm = (LoginForm) StringUtil.deserialize(loginFormString);
				return loadUserByUsername(loginForm);
			} catch (ClassNotFoundException | IOException e) {
				throw new ServiceException("ERROR: cannot deserialize loginFormString", e);
			}
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new UsernameNotFoundException("A specified user does not exist.", e);
        }
	}
        
	public UserDetails loadUserByUsername(LoginForm loginForm) throws UsernameNotFoundException {
    	Long localgovinfoid = Long.parseLong(loginForm.localgovinfoid);

    	// 班名で検索
    	if(loginForm.mode==LoginService.MODE_TASK) {
    	//if(StringUtils.isNotEmpty(loginForm.groupid)) {
        	GroupInfo groupInfo = groupInfoService.findByName(loginForm.groupid, localgovinfoid);
        	if(groupInfo==null) {
                throw new UsernameNotFoundException("A specified user does not exist.");
        	}
        	Account account = new Account();
        	account.setUsername(groupInfo.name);
        	account.setPassword(groupInfo.password);
        	account.setAuthorities(LoginService.AUTHORITY_USER);
        	// 班ID:0 は管理者
        	if(groupInfo.id==0L) {
            	account.setAuthorities(LoginService.AUTHORITY_ADMIN);
        	}
        	account.setGroupInfo(groupInfo);
            return new MyUserDetails(account);
    	}

    	// 課名で検索
   		if(loginForm.mode==LoginService.MODE_USUAL) {
    	//if(StringUtils.isNotEmpty(loginForm.unitid)) {
        	UnitInfo unitInfo = unitInfoService.findByName(loginForm.unitid, localgovinfoid);
        	if(unitInfo==null) {
                throw new UsernameNotFoundException("A specified user does not exist.");
        	}
        	Account account = new Account();
        	account.setUsername(unitInfo.name);
        	account.setPassword(unitInfo.password);
        	account.setAuthorities(LoginService.AUTHORITY_USER);
        	account.setUnitInfo(unitInfo);
            return new MyUserDetails(account);
    	}

    	throw new UsernameNotFoundException("A specified user does not exist.");
	}

}
