package ar.com.tbf.security.shiro.authorization.model;

public class RecursiveGroupPermission {

	private Integer id;
	private String name;
	private String description;
	private Integer parentId;
	private Integer level;
	private String permission;
	
	public RecursiveGroupPermission( Integer id, String name, String description, Integer parentId, Integer level, String permission ){
		
		this.id = id;
		this.name = name;
		this.description = description;
		this.parentId = parentId;
		this.level = level;
		this.permission = permission;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public String getPermission() {
		return permission;
	}
	public void setPermission(String permission) {
		this.permission = permission;
	}
}
