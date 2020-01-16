package ar.com.tbf.security.shiro.authorization;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ar.com.tbf.security.shiro.authorization.data.repository.AuthorizationRepository;
import ar.com.tbf.security.shiro.authorization.model.RecursiveGroupPermission;

@SuppressWarnings("deprecation")
public class TbfAuthorizationRepositoryRealm extends CasRealm {

	private Logger LOG = LoggerFactory.getLogger(TbfAuthorizationRepositoryRealm.class);
	
	AuthorizationRepository repository;
	
	public TbfAuthorizationRepositoryRealm( AuthorizationRepository repository ) {
		
		super();
		this.setCasServerUrlPrefix("");
		this.setCasService("cas");
		this.repository = repository;
		
		LOG.info("Repository is "+ this.repository == null ? "not set" : "set");
	}
	
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
		
		List<RecursiveGroupPermission> permisos = repository.allFromSubject(subject);
		
		for( RecursiveGroupPermission permiso : permisos ) {
			
    		roles.add(       permiso.getName()       );
    		permissions.add( permiso.getPermission() );
		}
		
	    authorizationInfo.setStringPermissions( permissions );
	    authorizationInfo.setRoles(             roles       );

	}
}
