package jp.ecom_plat.saigaitask.security.sample;

import java.io.Serializable;

import jp.ecom_plat.saigaitask.entity.db.GroupInfo;
import jp.ecom_plat.saigaitask.entity.db.UnitInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors
@Getter
@Setter
public class Account implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long logindataid;
	
	private String username;
	private String password;
	private String name;
	private String authorities;
	
	/** 班ログイン */
	private GroupInfo groupInfo;
	/** 課ログイン */
	private UnitInfo unitInfo;
}
