package ar.com.tbf.security.shiro;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cas.CasRealm;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.com.tbf.web.generic.filter.RequestResponseAccessibility;


@SuppressWarnings("deprecation")
public class CasLoginUrlResolver {

	private static final Logger LOG = LoggerFactory.getLogger(CasLoginUrlResolver.class);

	private CasRealm casRealm;
	private String mainAction;
	private String casServerScheme             = null;
	private String casServerName               = null;
	private String casServerPort               = null;
	private String casServerContextPath        = null;	
	private String casServiceScheme            = null;
	private String casServiceName              = null;
	private String casServicePort              = null;
	private String casServiceContextPath       = null;
	private String casServiceContextPathAppend = "shiro-cas";
	private String customViewParam             = null;
		
	private String     mapping;
	private JSONObject mappingJSONObjetct;
	private JSONArray  mappingJSONArray;
	private String     originalRequest;
	
	CasLoginUrlResolverProperties properties;

	public CasLoginUrlResolver() {
	}

	public CasLoginUrlResolver(CasLoginUrlResolverProperties properties) {
		
		this.properties           = properties; 
		this.mapping              = this.properties.getMapping();
		this.casServerName        = this.properties.getServerName();
		this.casServerPort        = this.properties.getServerPort();
		this.casServerScheme      = this.properties.getServerScheme();
		this.casServerContextPath = this.properties.getServerContextPath();
	}

	public void setResolve(CasRealm casRealm){

		this.casRealm = casRealm;

		this.casRealm.setCasServerUrlPrefix( this.properties.getServerUrlPrefix() );
		this.casRealm.setCasService(         this.properties.getService()         );
		
	}
	
	public CasRealm getResolve(){
		
		return this.casRealm;
	}
	
	public String getLoginUrl(){
		
		/*
		 * roles.loginUrl = http://localhost:8081/cas/login?
		 * service=http://localhost:8080/enlanube/shiro-cas
		 * &main=/common/main.action
		 * &customViewParam=securityManager
		 */
		HttpServletRequest request = RequestResponseAccessibility.getRequest();

		if( this.getCasServerScheme() == null ){
			
			setCasServerScheme(RequestResponseAccessibility.getScheme());
		}

		if( this.getCasServerName() == null ){
			
			setCasServerName(request.getServerName());
		}
		if( this.getCasServerPort() == null ){
			
			setCasServerPort(String.valueOf( request.getServerPort()) );
		}
		
		// service
		if( this.getCasServiceScheme() == null ){
			
			setCasServiceScheme( request.getScheme());
		}
		if( this.getCasServiceName() == null ){
			
			setCasServiceName(request.getServerName());
		}
		if( this.getCasServicePort() == null ){
			
			setCasServicePort(String.valueOf( request.getServerPort()) );
		}
		if( this.getCasServiceContextPath() == null ){
			
			setCasServiceContextPath(request.getContextPath());
		}
		
		originalRequest = (String) SecurityUtils.getSubject().getSession().getAttribute( "original_path" );
		
		originalRequest = originalRequest == null ? "" : originalRequest;
		
		String casPort     = this.getCasServerPort().equals("443")  || this.getCasServerPort().equals("80")  ? "" : ":"+ this.getCasServerPort();
		String servicePort = this.getCasServicePort().equals("443") || this.getCasServicePort().equals("80") ? "" : ":"+ this.getCasServicePort();
		
		casRealm.setCasServerUrlPrefix( this.getCasServerScheme() +"://"+ this.getCasServerName()  + casPort +"/"+ this.getCasServerContextPath());
		casRealm.setCasService( this.getCasServiceScheme() +"://"+ this.getCasServiceName() + servicePort  + this.getCasServiceContextPath() +"/"+ this.getCasServiceContextPathAppend());
		
		this.getMainActionAndCustomViewParam();
		
		if( ! originalRequest.isEmpty() && ! this.getMainAction().equals(originalRequest) ) {
			
			if( LOG.isDebugEnabled() ) LOG.debug( "Reemplazando el orginal request ", originalRequest, " por el definido en shiro.ini ", this.getMainAction() );
			
			SecurityUtils.getSubject().getSession().setAttribute( "original_path", this.getMainAction() );
		}
		
		return casRealm.getCasServerUrlPrefix() + "/login?service=" + this.casRealm.getCasService()
				+"&main=" + this.getMainAction()
				+"&customViewParam=" + this.getCustomViewParam();
	}
	
	public void getMainActionAndCustomViewParam(){

		if( this.mappingJSONObjetct == null && this.mappingJSONArray == null ){
			
			try{
				
				this.mappingJSONObjetct = (JSONObject) JSONValue.parse( mapping );
				
			}catch( Exception e ) {
				
				this.mappingJSONArray   = (JSONArray) JSONValue.parse( mapping );
			}
		}
		
		if( this.mappingJSONObjetct != null ) {
			
			fromJsonObject();
		}
		
		if( this.mappingJSONArray != null ) {
			
			fromJsonArray();
		}
	}
	
	private void fromJsonArray() {
		
		JSONObject       jsonObject;
		String           key;
		String           keyLowerCase;
		Iterator<String> keySetIterator;
		JSONObject       params          = null;
		
		for( Object obj : this.mappingJSONArray ) {
			
			jsonObject     = (JSONObject) obj;
			keySetIterator = jsonObject.keySet().iterator();
			
			while( keySetIterator.hasNext() ) {
				
				key          = keySetIterator.next();
				keyLowerCase = key.toLowerCase();
				
				if( 
						   this.originalRequest.toLowerCase().contains( keyLowerCase ) 
						|| this.getCasServerName().toLowerCase().contains(         keyLowerCase )
					){
				
					params = (JSONObject) jsonObject.get( key );
					break;
				}				
			}
			
			if( params != null ) {
				break;
			}
		}
		
		this.setMainAction(      (String) params.get("main") );
		this.setCustomViewParam( (String) params.get("view") );
	}
	
	private void fromJsonObject() {
		
		JSONObject       params = null;
		Iterator<String> mappingIterator = this.mappingJSONObjetct.keySet().iterator();
		String           key;
		String           keyLowerCase;
		
		while( mappingIterator.hasNext() ){
			
			key          = mappingIterator.next();
			keyLowerCase = key.toLowerCase();
			
			if( 
					   this.originalRequest.toLowerCase().contains( keyLowerCase ) 
					|| this.getCasServerName().toLowerCase().contains(         keyLowerCase )
				){
			
				params = (JSONObject) this.mappingJSONObjetct.get(key);
				break;
			}
		}
		
		this.setMainAction(      (String) params.get("main") );
		this.setCustomViewParam( (String) params.get("view") );
	}
	
	public String getMainAction() {
		return mainAction;
	}

	public void setMainAction(String mainAction) {
		this.mainAction = mainAction;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}
	/*
	 * Metodo falso
	 */
	public String getMapping(String mapping) {
		
		return this.mapping;
	}

	public String getCasServerScheme() {
		return casServerScheme;
	}

	public void setCasServerScheme(String casServerScheme) {
		this.casServerScheme = casServerScheme;
	}

	public String getCasServerName() {
		return casServerName;
	}

	public void setCasServerName(String casServerName) {
		this.casServerName = casServerName;
	}

	public String getCasServerPort() {
		return casServerPort;
	}

	public void setCasServerPort(String casServerPort) {
		this.casServerPort = casServerPort;
	}

	public String getCasServerContextPath() {
		return casServerContextPath;
	}

	public void setCasServerContextPath(String casServerContextPath) {
		this.casServerContextPath = casServerContextPath;
	}

	public String getCasServiceScheme() {
		return casServiceScheme;
	}

	public void setCasServiceScheme(String casServiceScheme) {
		this.casServiceScheme = casServiceScheme;
	}

	public String getCasServiceName() {
		return casServiceName;
	}

	public void setCasServiceName(String casServiceName) {
		this.casServiceName = casServiceName;
	}

	public String getCasServicePort() {
		return casServicePort;
	}

	public void setCasServicePort(String casServicePort) {
		this.casServicePort = casServicePort;
	}

	public String getCasServiceContextPathAppend() {
		return casServiceContextPathAppend;
	}

	public void setCasServiceContextPathAppend(String casServiceContextPathAppend) {
		this.casServiceContextPathAppend = casServiceContextPathAppend;
	}

	public String getCasServiceContextPath() {
		return casServiceContextPath;
	}

	public void setCasServiceContextPath(String casServiceContextPath) {
		this.casServiceContextPath = casServiceContextPath;
	}

	public String getCustomViewParam() {
		return customViewParam;
	}

	public void setCustomViewParam(String customViewParam) {
		this.customViewParam = customViewParam;
	}

	public static void main(String[] args ){
		
		CasLoginUrlResolver resolver = new CasLoginUrlResolver();
		
		resolver.setMapping("{\"localhost\":{\"main\":\"/common/main.action,view:securityManager\"}, \"intranetfgb\":{\"main\":\"/common/main.action,view:securityManager\"}, \"extranetfgb\":{\"main\":\"/common/main.action,view:securityManager\"}}");
		resolver.getLoginUrl();
	}

	public CasLoginUrlResolverProperties getProperties() {
		return properties;
	}

	public void setProperties(CasLoginUrlResolverProperties properties) {
		this.properties = properties;
	}
}
