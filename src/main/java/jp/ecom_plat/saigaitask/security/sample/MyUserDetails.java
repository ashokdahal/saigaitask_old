package jp.ecom_plat.saigaitask.security.sample;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class MyUserDetails extends User {
    private static final long serialVersionUID = 1L;
    private final Account account;
    public MyUserDetails(Account account) {
        super(account.getUsername(), account.getPassword(),
                AuthorityUtils.commaSeparatedStringToAuthorityList(account.getAuthorities()));
        this.account = account;
    }
    public Account getAccount() {
        return account;
    }
}
