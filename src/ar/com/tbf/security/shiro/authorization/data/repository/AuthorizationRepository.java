package ar.com.tbf.security.shiro.authorization.data.repository;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;

import ar.com.tbf.security.shiro.authorization.model.RecursiveGroupPermission;

public class AuthorizationRepository implements AuthorizationRepositoryInterface {

	@Autowired
    private EntityManager entityManager;
	
	@Override
	public List<RecursiveGroupPermission> allFromSubject( String subject ){
		
		return (List<RecursiveGroupPermission>) entityManager.createNativeQuery(GROUP_PERMISSION_FROM_SUBJECT).setParameter(1, subject).getResultList();
		
	}
	

}
