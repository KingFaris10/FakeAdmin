package com.faris.fakeadmin;

import com.faris.fakeadmin.api.FakeAdminAPI;
import com.faris.fakeadmin.api.event.PlayerAdminEvent;
import com.faris.fakeadmin.api.event.PlayerLoseAdminEvent;
import com.faris.fakeadmin.command.AdminCommand;
import com.faris.fakeadmin.command.AdminCommands;
import com.faris.fakeadmin.helper.Lang;
import com.faris.fakeadmin.helper.Utilities;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			if (this.getPlugin().getManager().getAdminManager().isFakeAdmin(event.getPlayer().getUniqueId())) {
				List<UUID> bannedPlayers = this.getPlugin().getManager().getBanManager().getBannedUUIDs();
				for (UUID bannedPlayerUUID : bannedPlayers) {
					Player bannedPlayer = event.getPlayer().getServer().getPlayer(bannedPlayerUUID);
					if (Utilities.PlayerUtilities.isOnline(bannedPlayer)) event.getPlayer().hidePlayer(bannedPlayer);
				}
				List<UUID> kickedPlayers = this.getPlugin().getManager().getKickManager().getKickedUUIDs();
				for (UUID kickedPlayerUUID : kickedPlayers) {
					Player kickedPlayer = event.getPlayer().getServer().getPlayer(kickedPlayerUUID);
					if (Utilities.PlayerUtilities.isOnline(kickedPlayer) && event.getPlayer().canSee(kickedPlayer))
						event.getPlayer().hidePlayer(kickedPlayer);
				}
			} else {
				boolean isBanned = this.getPlugin().getManager().getBanManager().isBanned(event.getPlayer().getUniqueId());
				if (isBanned) {
					for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
						Player fakeAdmin = event.getPlayer().getServer().getPlayer(fakeAdminUUID);
						if (Utilities.PlayerUtilities.isOnline(fakeAdmin)) fakeAdmin.hidePlayer(event.getPlayer());
					}
				}
				boolean isKicked = this.getPlugin().getManager().getKickManager().isKicked(event.getPlayer().getUniqueId());
				if (isKicked) {
					for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
						Player fakeAdmin = event.getPlayer().getServer().getPlayer(fakeAdminUUID);
						if (Utilities.PlayerUtilities.isOnline(fakeAdmin) && fakeAdmin.canSee(event.getPlayer()))
							fakeAdmin.hidePlayer(event.getPlayer());
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLeave(PlayerQuitEvent event) {
		try {
			if (this.getPlugin().getManager().getAdminManager().isFakeAdmin(event.getPlayer().getUniqueId())) {
				List<UUID> bannedPlayers = this.getPlugin().getManager().getBanManager().getBannedUUIDs();
				for (UUID bannedPlayerUUID : bannedPlayers) {
					Player bannedPlayer = event.getPlayer().getServer().getPlayer(bannedPlayerUUID);
					if (Utilities.PlayerUtilities.isOnline(bannedPlayer) && !Utilities.PlayerUtilities.isVanished(event.getPlayer(), bannedPlayer))
						event.getPlayer().showPlayer(bannedPlayer);
				}
				List<UUID> kickedPlayers = this.getPlugin().getManager().getKickManager().getKickedUUIDs();
				for (UUID kickedPlayerUUID : kickedPlayers) {
					Player kickedPlayer = event.getPlayer().getServer().getPlayer(kickedPlayerUUID);
					if (Utilities.PlayerUtilities.isOnline(kickedPlayer) && !event.getPlayer().canSee(kickedPlayer) && !Utilities.PlayerUtilities.isVanished(event.getPlayer(), kickedPlayer))
						event.getPlayer().showPlayer(kickedPlayer);
				}
			} else {
				boolean isBanned = this.getPlugin().getManager().getBanManager().isBanned(event.getPlayer().getUniqueId());
				if (isBanned) {
					for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
						Player fakeAdmin = event.getPlayer().getServer().getPlayer(fakeAdminUUID);
						if (Utilities.PlayerUtilities.isOnline(fakeAdmin) && !Utilities.PlayerUtilities.isVanished(fakeAdmin, event.getPlayer()))
							fakeAdmin.showPlayer(event.getPlayer());
					}
				}
				boolean isKicked = this.getPlugin().getManager().getKickManager().isKicked(event.getPlayer().getUniqueId());
				if (isKicked) {
					for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
						Player fakeAdmin = event.getPlayer().getServer().getPlayer(fakeAdminUUID);
						if (Utilities.PlayerUtilities.isOnline(fakeAdmin) && !fakeAdmin.canSee(event.getPlayer()) && !Utilities.PlayerUtilities.isVanished(fakeAdmin, event.getPlayer()))
							fakeAdmin.showPlayer(event.getPlayer());
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerGainAdmin(PlayerAdminEvent event) {
		try {
			this.getPlugin().getManager().getSpyManager().removeSpy(event.getPlayer().getUniqueId());

			if (this.getPlugin().getManager().getBanManager().isBanned(event.getPlayer().getUniqueId())) {
				this.getPlugin().getManager().getBanManager().unbanPlayers(event.getPlayer().getUniqueId());
				this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Banned players." + event.getPlayer().getUniqueId().toString(), null);
				if (this.getPlugin().getManager().getConfigManager().getPlayersConfig().isConfigurationSection("Banned players") && this.getPlugin().getManager().getConfigManager().getPlayersConfig().getConfigurationSection("Banned players").getValues(false).isEmpty())
					this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Banned players", null);
				this.getPlugin().getManager().getConfigManager().savePlayersConfig();

				for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
					if (fakeAdminUUID != null && !fakeAdminUUID.equals(event.getPlayer().getUniqueId())) {
						Utilities.showPlayer(fakeAdminUUID, event.getPlayer().getUniqueId());
					}
				}
			}
			if (this.getPlugin().getManager().getKickManager().isKicked(event.getPlayer().getUniqueId())) {
				this.getPlugin().getManager().getKickManager().removeKick(event.getPlayer().getUniqueId());

				for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
					if (fakeAdminUUID != null && !fakeAdminUUID.equals(event.getPlayer().getUniqueId())) {
						Utilities.showPlayer(fakeAdminUUID, event.getPlayer().getUniqueId());
					}
				}
			}
			if (this.getPlugin().getManager().getMuteManager().isMuted(event.getPlayer().getUniqueId())) {
				this.getPlugin().getManager().getMuteManager().removeMute(event.getPlayer().getUniqueId());
				this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Muted players." + event.getPlayer().getUniqueId().toString(), null);
				if (this.getPlugin().getManager().getConfigManager().getPlayersConfig().isConfigurationSection("Muted players") && this.getPlugin().getManager().getConfigManager().getPlayersConfig().getConfigurationSection("Muted players").getValues(false).isEmpty())
					this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Muted players", null);
				this.getPlugin().getManager().getConfigManager().savePlayersConfig();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler
	public void onPlayerLoseAdmin(PlayerLoseAdminEvent event) {
		try {
			List<UUID> bannedUUIDs = this.getPlugin().getManager().getBanManager().getBannedUUIDs();
			for (UUID bannedUUID : bannedUUIDs) Utilities.showPlayer(event.getPlayer().getUniqueId(), bannedUUID);
			List<UUID> kickedUUIDs = this.getPlugin().getManager().getKickManager().getKickedUUIDs();
			for (UUID kickedUUID : kickedUUIDs) Utilities.showPlayer(event.getPlayer().getUniqueId(), kickedUUID);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerTypeCommand(PlayerCommandPreprocessEvent event) {
		try {
			if (event.isCancelled()) return;
			if (this.getPlugin().getManager().getAdminManager().isFakeAdmin(event.getPlayer().getUniqueId())) {
				String[] commandSplit = event.getMessage().contains(" ") ? event.getMessage().split(" ") : new String[]{event.getMessage()};
				if (commandSplit.length > 0) {
					commandSplit[0] = commandSplit[0].replaceFirst("/", "");
					String strCommand = commandSplit[0].toLowerCase();
					Class<? extends AdminCommand> commandClass = AdminCommands.getCommandClass(strCommand);
					if (commandClass != null) {
						boolean cancelEvent = true;
						try {
							Constructor<? extends AdminCommand> commandConstructor = commandClass.getConstructor();
							if (commandConstructor != null) {
								String[] commandArgs = new String[commandSplit.length - 1];
								for (int i = 1; i < commandSplit.length; i++) commandArgs[i - 1] = commandSplit[i];
								AdminCommand adminCommand = commandConstructor.newInstance();
								try {
									AdminCommand.CommandReturnType returnType = adminCommand.onCommand(event.getPlayer(), strCommand, commandArgs);
									switch (returnType) {
										case DISABLED:
											cancelEvent = false;
											break;
										case INVALID_USAGE:
											List<String> cmdUsage = adminCommand.getUsage();
											for (String usage : cmdUsage)
												event.getPlayer().sendMessage(Utilities.replaceChatColoursAndFormats(usage));
											break;
										default:
											break;
									}
								} catch (Exception ex) {
									ex.printStackTrace();
									Lang.sendMessage(event.getPlayer(), Lang.GENERAL_ERROR);
								} finally {
									this.getPlugin().getManager().getSpyManager().showCommand(event.getPlayer().getName(), strCommand.toLowerCase() + " " + Utilities.toString(commandArgs));
								}
							}
						} catch (Exception ex) {
						}
						event.setCancelled(cancelEvent);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		try {
			if (event.isCancelled()) return;
			if (!Utilities.PlayerUtilities.isMuted(event.getPlayer())) {
				if (this.getPlugin().getManager().getAdminManager().isFakeAdmin(event.getPlayer().getUniqueId())) {
					List<Player> chatRecipients = new ArrayList<>();
					List<Player> rawRecipients = new ArrayList<>(event.getRecipients());
					for (Player rawRecipient : rawRecipients) {
						if (rawRecipient != null && !this.getPlugin().getManager().getAdminManager().isFakeAdmin(rawRecipient.getUniqueId()))
							chatRecipients.add(rawRecipient);
					}

					event.getRecipients().clear();
					if (!chatRecipients.isEmpty()) event.getRecipients().addAll(chatRecipients);

					String strMessage = Lang.CHAT_ADMIN.getRawMessage();
					if (this.getPlugin().getManager().getNicknameManager().hasNickname(event.getPlayer().getUniqueId()))
						strMessage = strMessage.replace("<player>", this.getPlugin().getManager().getNicknameManager().getNickname(event.getPlayer().getUniqueId()));
					else strMessage = strMessage.replace("<player>", event.getPlayer().getDisplayName());
					strMessage = strMessage.replace("<name>", event.getPlayer().getName());
					strMessage = strMessage.replace("<message>", event.getMessage());
					strMessage = Utilities.replaceChatColoursAndFormats(strMessage);

					for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
						Player fakeAdmin = event.getPlayer().getServer().getPlayer(fakeAdminUUID);
						if (fakeAdmin != null) fakeAdmin.sendMessage(strMessage);
					}
				} else {
					List<Player> removeRecipients = new ArrayList<>();
					List<Player> adminRecipients = new ArrayList<>();
					List<Player> rawRecipients = new ArrayList<>(event.getRecipients());
					if (this.getPlugin().getManager().getBanManager().isBanned(event.getPlayer().getUniqueId()) || this.getPlugin().getManager().getKickManager().isKicked(event.getPlayer().getUniqueId()) || this.getPlugin().getManager().getMuteManager().isMuted(event.getPlayer().getUniqueId())) {
						for (Player rawRecipient : rawRecipients) {
							if (rawRecipient != null && this.getPlugin().getManager().getAdminManager().isFakeAdmin(rawRecipient.getUniqueId()))
								removeRecipients.add(rawRecipient);
						}
					} else if (this.getPlugin().getManager().getNicknameManager().hasNickname(event.getPlayer().getUniqueId())) {
						for (Player rawRecipient : rawRecipients) {
							if (rawRecipient != null && this.getPlugin().getManager().getAdminManager().isFakeAdmin(rawRecipient.getUniqueId())) {
								adminRecipients.add(rawRecipient);
								removeRecipients.add(rawRecipient);
							}
						}
					}
					if (!removeRecipients.isEmpty()) event.getRecipients().removeAll(removeRecipients);

					String strMessage = Lang.CHAT_ADMIN.getRawMessage();
					if (this.getPlugin().getManager().getNicknameManager().hasNickname(event.getPlayer().getUniqueId()))
						strMessage = strMessage.replace("<player>", this.getPlugin().getManager().getNicknameManager().getNickname(event.getPlayer().getUniqueId()));
					else strMessage = strMessage.replace("<player>", event.getPlayer().getDisplayName());
					strMessage = strMessage.replace("<name>", event.getPlayer().getName());
					strMessage = Utilities.replaceChatColoursAndFormats(strMessage);
					strMessage = strMessage.replace("<message>", (String) FakeAdminAPI.getChatHandler().getValue(event.getPlayer(), event.getMessage()));

					for (Player adminRecipient : adminRecipients) adminRecipient.sendMessage(strMessage);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public FakeAdmin getPlugin() {
		return FakeAdmin.getInstance();
	}

}
