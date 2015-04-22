package com.faris.fakeadmin.manager;

import com.faris.fakeadmin.helper.TemporaryPlayer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BanManager implements Manager {

	private List<TemporaryPlayer> bannedPlayers = null;

	public BanManager() {
		this.bannedPlayers = new ArrayList<>();
	}

	@Override
	public void onDisable() {
		this.clearBans();
	}

	public TemporaryPlayer banPlayer(UUID bannedUUID) {
		if (bannedUUID != null) {
			TemporaryPlayer bannedPlayer = new TemporaryPlayer(bannedUUID);
			if (!this.bannedPlayers.contains(bannedPlayer)) this.bannedPlayers.add(bannedPlayer);
			return bannedPlayer;
		}
		return null;
	}

	public TemporaryPlayer banPlayer(UUID bannedUUID, long duration) {
		if (bannedUUID != null && duration != 0L) {
			if (duration < 0L) duration = -1000L;
			TemporaryPlayer bannedPlayer = new TemporaryPlayer(bannedUUID, duration);
			if (!this.bannedPlayers.contains(bannedPlayer)) this.bannedPlayers.add(bannedPlayer);
			return bannedPlayer;
		}
		return null;
	}

	public TemporaryPlayer banPlayer(TemporaryPlayer temporaryPlayer) {
		if (temporaryPlayer != null) {
			if (!this.bannedPlayers.contains(temporaryPlayer)) this.bannedPlayers.add(temporaryPlayer);
		}
		return temporaryPlayer;
	}

	public void clearBans() {
		this.bannedPlayers.clear();
	}

	public List<TemporaryPlayer> getBanned() {
		return Collections.unmodifiableList(this.bannedPlayers);
	}

	public List<UUID> getBannedUUIDs() {
		List<UUID> bannedList = new ArrayList<>();
		for (TemporaryPlayer temporaryPlayer : this.bannedPlayers) {
			if (temporaryPlayer != null) bannedList.add(temporaryPlayer.getUniqueId());
		}
		return bannedList;
	}

	public boolean isBanned(UUID bannedUUID) {
		if (bannedUUID != null) {
			for (TemporaryPlayer bannedPlayer : this.bannedPlayers) {
				if (bannedPlayer != null && bannedUUID.equals(bannedPlayer.getUniqueId())) return true;
			}
		}
		return false;
	}

	public List<TemporaryPlayer> unbanPlayers(UUID... bannedUUIDs) {
		if (bannedUUIDs != null && bannedUUIDs.length > 0) {
			List<TemporaryPlayer> removePlayers = new ArrayList<>();
			for (TemporaryPlayer temporaryBannedPlayer : this.bannedPlayers) {
				for (UUID bannedUUID : bannedUUIDs) {
					if (bannedUUID != null && bannedUUID.equals(temporaryBannedPlayer.getUniqueId()))
						removePlayers.add(temporaryBannedPlayer);
				}
			}
			if (!removePlayers.isEmpty()) this.bannedPlayers.removeAll(removePlayers);
			return removePlayers;
		}
		return new ArrayList<>();
	}

	public List<TemporaryPlayer> unbanPlayers(List<UUID> bannedUUIDs) {
		if (bannedUUIDs != null && !bannedUUIDs.isEmpty()) {
			List<TemporaryPlayer> removePlayers = new ArrayList<>();
			for (TemporaryPlayer temporaryBannedPlayer : this.bannedPlayers) {
				if (bannedUUIDs.contains(temporaryBannedPlayer.getUniqueId())) removePlayers.add(temporaryBannedPlayer);
			}
			if (!removePlayers.isEmpty()) this.bannedPlayers.removeAll(removePlayers);
			return removePlayers;
		}
		return new ArrayList<>();
	}

}
