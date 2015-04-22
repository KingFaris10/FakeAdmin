package com.faris.fakeadmin.manager;

import com.faris.fakeadmin.helper.Lang;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SpyManager implements Manager {

	private List<UUID> spyList = null;

	public SpyManager() {
		this.spyList = new ArrayList<>();
	}

	@Override
	public void onDisable() {
		this.clearSpies();
	}

	public void addSpy(UUID playerUUID) {
		if (!this.isSpy(playerUUID)) this.spyList.add(playerUUID);
	}

	public void clearSpies() {
		this.spyList.clear();
	}

	public List<UUID> getSpies() {
		return Collections.unmodifiableList(this.spyList);
	}

	public boolean isSpy(UUID playerUUID) {
		return playerUUID != null && this.spyList.contains(playerUUID);
	}

	public void removeSpy(UUID playerUUID) {
		if (this.isSpy(playerUUID)) this.spyList.remove(playerUUID);
	}

	public void showCommand(String playerName, String command) {
		if (playerName != null && command != null && !command.isEmpty()) {
			for (UUID spyUUID : this.spyList) {
				Player spy = Bukkit.getServer().getPlayer(spyUUID);
				if (spy != null && spy.isOnline())
					Lang.sendReplacedMessage(spy, Lang.SPY, "<player>", playerName, "<command>", command);
			}
		}
	}

}
