package com.faris.fakeadmin.api.event;

import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

/**
 * Called when a player is about to become a fake admin.
 */
public class PlayerPreAdminEvent extends PlayerEvent implements Cancellable {
	private static HandlerList handlerList = new HandlerList();

	private boolean isCancelled = false;

	public PlayerPreAdminEvent(Player who) {
		super(who);
	}

	@Override
	public boolean isCancelled() {
		return this.isCancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.isCancelled = cancelled;
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList;
	}
}
