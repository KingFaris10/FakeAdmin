package com.faris.fakeadmin.manager;

import com.faris.fakeadmin.helper.TemporaryPlayer;
import com.faris.fakeadmin.helper.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class KickManager implements Manager {

	private List<TemporaryPlayer> kickedPlayers = null;

	public KickManager() {
		this.kickedPlayers = new ArrayList<>();
	}

	@Override
	public void onDisable() {
		this.clearKicks();
	}

	public void clearKicks() {
		this.kickedPlayers.clear();
	}

	public List<TemporaryPlayer> getKicked() {
		return Collections.unmodifiableList(this.kickedPlayers);
	}

	public List<UUID> getKickedUUIDs() {
		List<UUID> kickerList = new ArrayList<>();
		for (TemporaryPlayer temporaryPlayer : this.kickedPlayers) {
			if (temporaryPlayer != null) kickerList.add(temporaryPlayer.getUniqueId());
		}
		return kickerList;
	}

	public boolean isKicked(UUID kickedUUID) {
		if (kickedUUID != null) {
			for (TemporaryPlayer temporaryPlayer : this.kickedPlayers) {
				if (kickedUUID.equals(temporaryPlayer.getUniqueId())) return true;
			}
		}
		return false;
	}

	public TemporaryPlayer kickPlayer(UUID kickedUUID) {
		if (kickedUUID != null) {
			TemporaryPlayer temporaryPlayer = new TemporaryPlayer(kickedUUID, (long) (Utilities.getRandom().nextInt(20) + 5));
			this.kickPlayer(temporaryPlayer);
			return temporaryPlayer;
		}
		return null;
	}

	public void kickPlayer(TemporaryPlayer kickedPlayer) {
		if (kickedPlayer != null && !this.kickedPlayers.contains(kickedPlayer)) this.kickedPlayers.add(kickedPlayer);
	}

	public void removeKick(UUID kickedUUID) {
		if (kickedUUID != null) {
			List<TemporaryPlayer> removePlayers = new ArrayList<>();
			for (TemporaryPlayer temporaryPlayer : this.kickedPlayers) {
				if (kickedUUID.equals(temporaryPlayer.getUniqueId())) removePlayers.add(temporaryPlayer);
			}
			if (!removePlayers.isEmpty()) this.kickedPlayers.removeAll(removePlayers);
		}
	}

	public void removeKicks(List<UUID> kickedUUIDs) {
		if (kickedUUIDs != null && !kickedUUIDs.isEmpty()) {
			List<TemporaryPlayer> removePlayers = new ArrayList<>();
			for (TemporaryPlayer temporaryPlayer : this.kickedPlayers) {
				if (kickedUUIDs.contains(temporaryPlayer.getUniqueId())) removePlayers.add(temporaryPlayer);
			}
			if (!removePlayers.isEmpty()) this.kickedPlayers.removeAll(removePlayers);
		}
	}

}
