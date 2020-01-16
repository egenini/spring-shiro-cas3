package ar.com.tbf.security.shiro;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="tbf.cas")
public class CasLoginUrlResolverProperties {

	private String serverScheme;
	private String serverName;
	private String serverPort;
	private String serverContextPath;	
	private String mapping;
	private String serverUrlPrefix;
	private String service;
	private String api;
	
	public String getServerScheme() {
		return serverScheme;
	}
	public void setServerScheme(String serverScheme) {
		this.serverScheme = serverScheme;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public String getServerPort() {
		return serverPort;
	}
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	public String getServerContextPath() {
		return serverContextPath;
	}
	public void setServerContextPath(String serverContextPath) {
		this.serverContextPath = serverContextPath;
	}
	public String getMapping() {
		return mapping;
	}
	public void setMapping(String mapping) {
		this.mapping = mapping;
	}
	public String getServerUrlPrefix() {
		return serverUrlPrefix;
	}
	public void setServerUrlPrefix(String serverUrlPrefix) {
		this.serverUrlPrefix = serverUrlPrefix;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getApi() {
		return api;
	}
	public void setApi(String api) {
		this.api = api;
	}
	
}
