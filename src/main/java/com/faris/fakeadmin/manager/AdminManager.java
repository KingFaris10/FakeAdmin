package com.faris.fakeadmin.manager;

import com.faris.fakeadmin.helper.Utilities;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AdminManager implements Manager {

	private List<UUID> fakeAdminList = null;

	public AdminManager() {
		this.fakeAdminList = new ArrayList<>();
	}

	@Override
	public void onDisable() {
		this.clearFakeAdmins();
	}

	public void addFakeAdmin(UUID playerUUID) {
		if (!this.isFakeAdmin(playerUUID)) this.fakeAdminList.add(playerUUID);
	}

	public void broadcastMessage(String message) {
		for (UUID fakeAdminUUID : this.fakeAdminList) {
			Player fakeAdmin = Bukkit.getServer().getPlayer(fakeAdminUUID);
			if (fakeAdmin != null && fakeAdmin.isOnline())
				fakeAdmin.sendMessage(Utilities.replaceChatColoursAndFormats(message));
		}
	}

	public void clearFakeAdmins() {
		this.fakeAdminList.clear();
	}

	public List<UUID> getFakeAdmins() {
		return Collections.unmodifiableList(this.fakeAdminList);
	}

	public boolean isFakeAdmin(UUID playerUUID) {
		return playerUUID != null && this.fakeAdminList.contains(playerUUID);
	}

	public void removeFakeAdmin(UUID playerUUID) {
		if (this.isFakeAdmin(playerUUID)) this.fakeAdminList.remove(playerUUID);
	}

}
