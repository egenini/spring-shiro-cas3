package ar.com.tbf.security.shiro;

import org.apache.shiro.SecurityUtils;

public class LogoutUrl {

	public static String get() {
		
		ar.com.tbf.security.shiro.SecurityManager sm = (ar.com.tbf.security.shiro.SecurityManager) SecurityUtils.getSecurityManager();

		return sm.getUrlResolver().getLogoutUrl();
	}
}
