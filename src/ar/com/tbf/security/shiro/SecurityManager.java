package ar.com.tbf.security.shiro;

import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import ar.com.tbf.security.shiro.CasLoginUrlResolver;

public class SecurityManager extends DefaultWebSecurityManager{

	private CasLoginUrlResolver urlResolver = null;

	public CasLoginUrlResolver getUrlResolver() {
		return urlResolver;
	}

	public void setUrlResolver(CasLoginUrlResolver urlResolver) {
		this.urlResolver = urlResolver;
	}
	
}