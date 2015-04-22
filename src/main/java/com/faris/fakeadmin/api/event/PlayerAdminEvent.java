package com.faris.fakeadmin.api.event;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

/**
 * Called when a player has become a fake admin.
 */
public class PlayerAdminEvent extends PlayerEvent {
	private static HandlerList handlerList = new HandlerList();

	public PlayerAdminEvent(Player who) {
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