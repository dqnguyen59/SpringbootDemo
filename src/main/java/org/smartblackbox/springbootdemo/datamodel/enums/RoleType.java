package org.smartblackbox.springbootdemo.datamodel.enums;

public enum RoleType {
	ROLE_ROOT(10, "ROLE_ROOT"),
	ROLE_ADMIN(9, "ROLE_ADMIN"),
	ROLE_USER(0, "ROLE_USER");

	int roleLevel;
	String roleName;

	RoleType(int roleLevel, String roleName) {
		this.roleLevel = roleLevel;
		this.roleName = roleName;
	}

	public int getLevel() {
		return roleLevel;
	}
	
	public String getName() {
		return roleName;
	}
	
	public static RoleType byOrdinal(int ord) {
		for (RoleType role : RoleType.values()) {
			if (role.roleLevel == ord) {
				return role;
			}
		}
		return null;
	}

}
