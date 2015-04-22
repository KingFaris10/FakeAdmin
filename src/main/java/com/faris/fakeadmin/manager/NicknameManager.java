package com.faris.fakeadmin.manager;

import com.faris.fakeadmin.helper.Utilities;
import com.faris.fakeadmin.helper.custom.CustomMap;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.Map;
import java.util.UUID;

public class NicknameManager implements Manager {

	private CustomMap<UUID, String> adminNicknames = null;

	public NicknameManager() {
		this.adminNicknames = new CustomMap<>();
	}

	@Override
	public void onDisable() {
		this.clearNicknames();
	}

	public void clearNicknames() {
		this.adminNicknames.clear();
	}

	public boolean doesConflict(String nickname) {
		if (nickname != null) {
			nickname = ChatColor.stripColor(Utilities.replaceChatColoursAndFormats(nickname));
			for (String adminNickname : this.adminNicknames.values()) {
				if (nickname.equalsIgnoreCase(ChatColor.stripColor(Utilities.replaceChatColoursAndFormats(adminNickname))))
					return true;
			}
			for (Player onlinePlayer : Utilities.getOnlinePlayers()) {
				if (nickname.equalsIgnoreCase(onlinePlayer.getName()) || nickname.equalsIgnoreCase(ChatColor.stripColor(Utilities.replaceChatColoursAndFormats(onlinePlayer.getDisplayName()))))
					return true;
			}
			for (OfflinePlayer offlinePlayer : Bukkit.getServer().getOfflinePlayers()) {
				if (offlinePlayer != null && nickname.equalsIgnoreCase(offlinePlayer.getName()))
					return true;
			}
		}
		return false;
	}

	public String getNickname(UUID playerUUID) {
		return playerUUID != null ? this.adminNicknames.get(playerUUID, "") : "";
	}

	public String getUsername(String nickname) {
		if (nickname != null) {
			nickname = ChatColor.stripColor(Utilities.replaceChatColoursAndFormats(nickname));
			for (Map.Entry<UUID, String> nicknameEntry : this.adminNicknames.entrySet()) {
				if (nickname.equalsIgnoreCase(ChatColor.stripColor(Utilities.replaceChatColoursAndFormats(nicknameEntry.getValue())))) {
					Player nicknameUser = Bukkit.getServer().getPlayer(nicknameEntry.getKey());
					if (nicknameUser != null) return nicknameUser.getName();
				}
			}
			for (Player onlinePlayer : Utilities.getOnlinePlayers()) {
				if (onlinePlayer.getName().equalsIgnoreCase(nickname)) return onlinePlayer.getName();
			}
		}
		return null;
	}

	public boolean hasNickname(UUID playerUUID) {
		return this.adminNicknames.containsKey(playerUUID);
	}

	public void setNickname(UUID playerUUID, String nickname) {
		if (playerUUID != null) {
			if (nickname == null) this.adminNicknames.remove(playerUUID);
			else this.adminNicknames.put(playerUUID, nickname);
		}
	}

}
