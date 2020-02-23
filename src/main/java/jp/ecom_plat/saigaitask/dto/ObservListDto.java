/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.dto;

@lombok.Getter @lombok.Setter @org.springframework.stereotype.Component @org.springframework.context.annotation.Scope(value = org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST, proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS)
public class ObservListDto {

	public Long id = 1l;
	
	public Integer no = 1;
	
	public String typename = "";
	
	public Integer typecol = 1;

	public String name = "";
	
	public Integer namecol = 1;
	
	public String itemname = "";
	
	public Integer observid = 1;
}
