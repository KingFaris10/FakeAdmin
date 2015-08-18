package com.faris.fakeadmin;

import org.bukkit.permissions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Permissions {

	private static List<Permission> permissionList = null;

	public static Permission COMMAND_FAKE_ADMIN = null;
	public static Permission COMMAND_ADMIN = null;
	public static Permission COMMAND_CLEAR = null;
	public static Permission COMMAND_LIST = null;
	public static Permission COMMAND_RELOAD = null;
	public static Permission COMMAND_SPY = null;
	public static Permission COMMAND_WHOIS = null;

	public static Permission FAKE_ADMIN_EXEMPT = null;

	public static void deinitialisePermissions() {
		permissionList.clear();
		permissionList = null;

		COMMAND_FAKE_ADMIN = null;
		COMMAND_ADMIN = null;
		COMMAND_CLEAR = null;
		COMMAND_LIST = null;
		COMMAND_RELOAD = null;
		COMMAND_SPY = null;
		COMMAND_WHOIS = null;
		FAKE_ADMIN_EXEMPT = null;
	}

	public static List<Permission> getPermissions() {
		return Collections.unmodifiableList(permissionList);
	}

	public static void initialisePermissions() {
		permissionList = new ArrayList<>();

		COMMAND_FAKE_ADMIN = registerPermission("fakeadmin.command.fakeadmin");
		COMMAND_ADMIN = registerPermission("fakeadmin.command.admin");
		COMMAND_CLEAR = registerPermission("fakeadmin.command.clear");
		COMMAND_LIST = registerPermission("fakeadmin.command.list");
		COMMAND_RELOAD = registerPermission("fakeadmin.command.reload");
		COMMAND_SPY = registerPermission("fakeadmin.command.spy");
		COMMAND_WHOIS = registerPermission("fakeadmin.command.whois");
		FAKE_ADMIN_EXEMPT = registerPermission("fakeadmin.exempt");
	}

	public static Permission registerPermission(String permissionNode) {
		return permissionNode != null ? registerPermission(new Permission(permissionNode)) : null;
	}

	public static Permission registerPermission(Permission permission) {
		if (permission != null && !permissionList.contains(permission)) permissionList.add(permission);
		return permission;
	}

}
