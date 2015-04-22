package com.faris.fakeadmin.manager;

import com.faris.fakeadmin.helper.TemporaryPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MuteManager implements Manager {

	private List<TemporaryPlayer> mutedPlayers = null;

	public MuteManager() {
		this.mutedPlayers = new ArrayList<>();
	}

	@Override
	public void onDisable() {
		this.clearMutes();
	}

	public void clearMutes() {
		this.mutedPlayers.clear();
	}

	public boolean isMuted(UUID mutedUUID) {
		if (mutedUUID != null) {
			for (TemporaryPlayer mutedPlayer : this.mutedPlayers) {
				if (mutedPlayer != null && mutedUUID.equals(mutedPlayer.getUniqueId())) return true;
			}
		}
		return false;
	}

	public TemporaryPlayer mutePlayer(UUID mutedUUID) {
		return this.mutePlayer(mutedUUID, -1000L);
	}

	public TemporaryPlayer mutePlayer(UUID mutedUUID, long duration) {
		return mutedUUID != null ? this.mutePlayer(new TemporaryPlayer(mutedUUID, duration)) : null;
	}

	public TemporaryPlayer mutePlayer(TemporaryPlayer mutedPlayer) {
		if (mutedPlayer != null) this.mutedPlayers.add(mutedPlayer);
		return mutedPlayer;
	}

	public void removeMute(UUID mutedUUID) {
		if (mutedUUID != null) {
			List<TemporaryPlayer> removePlayers = new ArrayList<>();
			for (TemporaryPlayer temporaryPlayer : this.mutedPlayers) {
				if (mutedUUID.equals(temporaryPlayer.getUniqueId())) removePlayers.add(temporaryPlayer);
			}
			if (!removePlayers.isEmpty()) this.mutedPlayers.removeAll(removePlayers);
		}
	}

	public void removeMutes(List<UUID> mutedUUIDs) {
		if (mutedUUIDs != null && !mutedUUIDs.isEmpty()) {
			List<TemporaryPlayer> removePlayers = new ArrayList<>();
			for (TemporaryPlayer temporaryPlayer : this.mutedPlayers) {
				if (mutedUUIDs.contains(temporaryPlayer.getUniqueId())) removePlayers.add(temporaryPlayer);
			}
			if (!this.mutedPlayers.isEmpty()) this.mutedPlayers.removeAll(removePlayers);
		}
	}

}
