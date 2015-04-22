package com.faris.fakeadmin.api.event;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

/**
 * Called when a player is no longer a fake admin.
 */
public class PlayerLoseAdminEvent extends PlayerEvent {
	private static HandlerList handlerList = new HandlerList();

	public PlayerLoseAdminEvent(Player who) {
		super(who);
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}
}
