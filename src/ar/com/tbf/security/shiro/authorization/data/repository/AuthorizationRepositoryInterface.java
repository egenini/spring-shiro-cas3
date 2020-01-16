package ar.com.tbf.security.shiro.authorization.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ar.com.tbf.security.shiro.authorization.model.RecursiveGroupPermission;

public interface AuthorizationRepositoryInterface {

	static String GROUP_PERMISSION_FROM_SUBJECT = 
			" WITH RECURSIVE all_group_cte(id, name, description, parent_id, level) AS ( "
					+ " SELECT g.id, g.name, g.description, g.parent_id, 0 AS level "
	                		 + " FROM \"authorization\".\"group\" g "
	                		   + " where parent_id in (select group_id from \"authorization\".subject_group where upper(subject) = ?1) "
	                		  + " UNION ALL "
	        		+ " SELECT sg.id, sg.name, sg.description, sg.parent_id, ag.level + 1 "
	                		 + " FROM \"authorization\".\"group\" sg "
	                		   + " JOIN all_group_cte ag ON ag.id = sg.parent_id "
	            		  + ") "
	        + " SELECT all_group_cte.id, all_group_cte.name, all_group_cte.description, all_group_cte.parent_id, all_group_cte.level, p.permission "  
	        + " FROM all_group_cte "
	        + " left join \"authorization\".permission p on p.group_id = all_group_cte.id "
			+ " union all "
			+ "SELECT g.id, g.name, g.description, g.parent_id, 0 AS level, p.permission FROM \"authorization\".\"group\" g "
			+ "join \"authorization\".subject_group sj on g.id = sj.group_id "
			+ "left join \"authorization\".permission p on p.group_id = g.id "
			+ "where upper(sj.subject) = ?1";
	
	public List<RecursiveGroupPermission> allFromSubject( String subject );
}
