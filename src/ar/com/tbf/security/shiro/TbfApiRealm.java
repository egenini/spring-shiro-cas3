package ar.com.tbf.security.shiro;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;


@SuppressWarnings("deprecation")
public class TbfApiRealm extends CasRealm {

	private Logger LOG = LoggerFactory.getLogger(TbfApiRealm.class);

	private String api;
	
	public TbfApiRealm(){
		super();
		this.setCasServerUrlPrefix("");
		this.setCasService("cas");
	}
	
	@Override
    protected void onInit() {
        super.onInit();
    }
	
	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

		Cache<Object, AuthorizationInfo> cache             = getAuthorizationCache();
		AuthorizationInfo                authorizationInfo = cache.get(principals.getPrimaryPrincipal());
		
		// buscar los grupos a los que pertenece este usuario.
		// los permisos se heredan por lo tanto hay que buscar los grupos hijos
		if( authorizationInfo == null){
			
			LOG.info("buscando permisos");
			
			authorizationInfo = new SimpleAuthorizationInfo();

			this.load( principals, (SimpleAuthorizationInfo) authorizationInfo );

			cache.put(principals.getPrimaryPrincipal(), authorizationInfo);
			
			LOG.info("permisos configurados y en cache");
		}else{
			
			if( LOG.isDebugEnabled() ) LOG.debug("retornando permisos desde el cache");
		}
		return authorizationInfo;
	}

	private void load( PrincipalCollection principals, SimpleAuthorizationInfo authorizationInfo ){
		
		Set<String> permissions = new LinkedHashSet<String>();
		Set<String> roles = new LinkedHashSet<String>();

		String subject = (String) principals.getPrimaryPrincipal();
		
		HttpRequest request = null;
		
		request = HttpRequest.get( this.getApi() + subject );

		HttpResponse response = request.acceptEncoding("Windows-1252").send();

		if( response.statusCode() == 200 ){
			
			 JsonParser parser = new JsonParser();
			 JsonArray array = parser.parse(response.body()).getAsJsonArray();
		
			 JsonObject currentJo;
				
			    for( JsonElement je : array ){
			    	
			    	if( je.isJsonObject() ){
			    		
			    		currentJo = (JsonObject)je;
			    		
			    		roles.add(currentJo.get("name").getAsString() );
			    		
			    		permissions.add( currentJo.get("permission").getAsString() );
			    	}
			    }

			    authorizationInfo.setStringPermissions(permissions);
			    authorizationInfo.setRoles(roles);
		}
		else{
			
			LOG.error( response.bodyText() );
		}
	}
	
	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}
}

