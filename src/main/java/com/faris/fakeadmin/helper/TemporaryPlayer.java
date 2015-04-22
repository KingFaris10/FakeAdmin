package com.faris.fakeadmin.helper;

import com.faris.fakeadmin.helper.custom.CustomMap;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.serialization.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TemporaryPlayer implements ConfigurationSerializable {

	private UUID playerUUID = null;
	private long startTime = 0L;
	private long duration = -1L;

	public TemporaryPlayer(UUID playerUUID) {
		this(playerUUID, System.currentTimeMillis(), -1000L);
	}

	public TemporaryPlayer(UUID playerUUID, long duration) {
		this(playerUUID, System.currentTimeMillis(), duration);
	}

	public TemporaryPlayer(UUID playerUUID, long startTime, long duration) {
		Validate.notNull(playerUUID);
		this.playerUUID = playerUUID;
		this.startTime = startTime;
		this.duration = duration;
	}

	public long getDuration() {
		return this.duration;
	}

	public long getStartTime() {
		return this.startTime;
	}

	public UUID getUniqueId() {
		return this.playerUUID;
	}

	public boolean hasDuration() {
		return this.duration >= 0L;
	}

	public boolean hasExpired() {
		return this.hasDuration() ? System.currentTimeMillis() - this.getStartTime() >= this.getDuration() : false;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> serializedPlayer = new HashMap<>();
		serializedPlayer.put("UUID", this.playerUUID.toString());
		serializedPlayer.put("Start time", this.startTime);
		serializedPlayer.put("Duration", this.duration >= 0L ? this.duration : -1000L);
		return serializedPlayer;
	}

	public static TemporaryPlayer deserialize(Object objSerializedPlayer) {
		if (objSerializedPlayer != null) {
			CustomMap<String, Object> serializedPlayer = Utilities.getMap(objSerializedPlayer);
			if (serializedPlayer.containsKey("UUID")) {
				String strPlayerUUID = serializedPlayer.get("UUID").toString();
				if (Utilities.isUUID(strPlayerUUID)) {
					UUID playerUUID = UUID.fromString(strPlayerUUID);
					long startTime = Utilities.isLong(serializedPlayer.get("Start time")) ? Long.parseLong(serializedPlayer.get("Start time").toString()) : System.currentTimeMillis();
					long duration = Utilities.isLong(serializedPlayer.get("Duration")) ? Long.parseLong(serializedPlayer.get("Duration").toString()) : -1000L;
					return new TemporaryPlayer(playerUUID, startTime, duration);
				}
			}
		}
		return null;
	}

}
