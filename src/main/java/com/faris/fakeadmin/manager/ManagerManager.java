package com.faris.fakeadmin.manager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ManagerManager implements Manager {

	private AdminManager adminManager = null;
	private BanManager banManager = null;
	private ConfigurationManager configManager = null;
	private KickManager kickManager = null;
	private MuteManager muteManager = null;
	private NicknameManager nickManager = null;
	private SpyManager spyManager = null;

	public ManagerManager() {
		this.onEnable();
	}

	public AdminManager getAdminManager() {
		return this.adminManager;
	}

	public BanManager getBanManager() {
		return this.banManager;
	}

	public ConfigurationManager getConfigManager() {
		return this.configManager;
	}

	public KickManager getKickManager() {
		return this.kickManager;
	}

	public MuteManager getMuteManager() {
		return this.muteManager;
	}

	public NicknameManager getNicknameManager() {
		return this.nickManager;
	}

	public SpyManager getSpyManager() {
		return this.spyManager;
	}

	public void onEnable() {
		this.adminManager = new AdminManager();
		this.banManager = new BanManager();
		this.configManager = new ConfigurationManager();
		this.kickManager = new KickManager();
		this.muteManager = new MuteManager();
		this.nickManager = new NicknameManager();
		this.spyManager = new SpyManager();
	}

	@Override
	public void onDisable() {
		this.onDisable(true);
	}

	public void onDisable(boolean clearInstance) {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				Object objManager = field.get(this);
				if (objManager != null) {
					Method methodOnDisable = objManager.getClass().getMethod("onDisable");
					if (methodOnDisable != null) methodOnDisable.invoke(objManager);
				}
				if (clearInstance) field.set(this, null);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
