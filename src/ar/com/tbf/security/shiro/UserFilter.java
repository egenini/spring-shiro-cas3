package ar.com.tbf.security.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.SecurityUtils;

public class UserFilter extends org.apache.shiro.web.filter.authc.UserFilter{
	
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue){
		
		boolean isAccessAllowed = super.isAccessAllowed(request, response, mappedValue);
		
		return isAccessAllowed;
	}

	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception{
		
		SecurityManager securityManager = (SecurityManager) SecurityUtils.getSecurityManager();
		
		CasLoginUrlResolver urlResolver = securityManager.getUrlResolver();
		
		this.setLoginUrl(urlResolver.getLoginUrl());
		
		return super.onAccessDenied(request, response);				
	}
}
