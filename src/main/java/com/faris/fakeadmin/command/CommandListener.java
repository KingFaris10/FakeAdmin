package com.faris.fakeadmin.command;

import com.faris.fakeadmin.ConfigCommand;
import com.faris.fakeadmin.FakeAdmin;
import com.faris.fakeadmin.Permissions;
import com.faris.fakeadmin.api.event.PlayerAdminEvent;
import com.faris.fakeadmin.api.event.PlayerLoseAdminEvent;
import com.faris.fakeadmin.api.event.PlayerPreAdminEvent;
import com.faris.fakeadmin.helper.Lang;
import com.faris.fakeadmin.helper.TemporaryPlayer;
import com.faris.fakeadmin.helper.Utilities;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandListener implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("fakeadmin")) {
			try {
				if (sender.hasPermission(Permissions.COMMAND_FAKE_ADMIN)) {
					if (args.length > 0) {
						String strCommand = args[0];
						if (strCommand.equalsIgnoreCase("reload")) {
							if (sender.hasPermission(Permissions.COMMAND_RELOAD)) {
								for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
									Player fakeAdmin = sender.getServer().getPlayer(fakeAdminUUID);
									if (fakeAdmin != null && fakeAdmin.isOnline()) {
										List<TemporaryPlayer> bannedList = this.getPlugin().getManager().getBanManager().getBanned();
										for (TemporaryPlayer temporaryBannedPlayer : bannedList) {
											if (temporaryBannedPlayer != null) {
												Player banned = sender.getServer().getPlayer(temporaryBannedPlayer.getUniqueId());
												if (banned != null && banned.isOnline() && !Utilities.PlayerUtilities.isVanished(fakeAdmin, banned))
													fakeAdmin.showPlayer(banned);
											}
										}
										List<TemporaryPlayer> kickedList = this.getPlugin().getManager().getKickManager().getKicked();
										for (TemporaryPlayer kickedPlayer : kickedList) {
											if (kickedPlayer != null) {
												Player kicked = sender.getServer().getPlayer(kickedPlayer.getUniqueId());
												if (kicked != null && kicked.isOnline() && !fakeAdmin.canSee(kicked) && !Utilities.PlayerUtilities.isVanished(fakeAdmin, kicked))
													fakeAdmin.showPlayer(kicked);
											}
										}
									}
								}

								this.getPlugin().getManager().getConfigManager().reloadConfigs();
								this.getPlugin().getManager().getConfigManager().loadConfiguration();
								sender.sendMessage(ChatColor.GOLD + "FakeAdmin v" + this.getPlugin().getDescription().getVersion() + "'s configuration has successfully been reloaded.");

								if (ConfigCommand.BAN.isEnabled() || ConfigCommand.TEMPBAN.isEnabled()) {
									List<TemporaryPlayer> bannedList = this.getPlugin().getManager().getBanManager().getBanned();
									for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
										Player fakeAdmin = sender.getServer().getPlayer(fakeAdminUUID);
										if (fakeAdmin != null && fakeAdmin.isOnline()) {
											for (TemporaryPlayer temporaryBannedPlayer : bannedList) {
												if (temporaryBannedPlayer != null) {
													Player bannedPlayer = sender.getServer().getPlayer(temporaryBannedPlayer.getUniqueId());
													if (bannedPlayer != null && bannedPlayer.isOnline())
														fakeAdmin.hidePlayer(bannedPlayer);
												}
											}
										}
									}
								}
							} else {
								Lang.sendReplacedMessage(sender, Lang.GENERAL_NO_PERMISSION, "<command>", strCommand.toLowerCase());
							}
						} else if (strCommand.equalsIgnoreCase("admin")) {
							if (sender.hasPermission(Permissions.COMMAND_ADMIN)) {
								if (args.length == 2) {
									String strPlayer = args[1];
									Player targetPlayer = sender.getServer().getPlayer(strPlayer);
									if (targetPlayer != null && targetPlayer.isOnline()) {
										if (this.getPlugin().getManager().getAdminManager().isFakeAdmin(targetPlayer.getUniqueId())) {
											this.getPlugin().getManager().getAdminManager().removeFakeAdmin(targetPlayer.getUniqueId());
											targetPlayer.getServer().getPluginManager().callEvent(new PlayerLoseAdminEvent(targetPlayer));
											List<String> currentFakeAdmins = this.getPlugin().getManager().getConfigManager().getConfig().getStringList("Fake admins");
											currentFakeAdmins.remove(targetPlayer.getUniqueId().toString());
											this.getPlugin().getManager().getConfigManager().getConfig().set("Fake admins", currentFakeAdmins);
											this.getPlugin().getManager().getConfigManager().saveConfig();

											Lang.sendReplacedMessage(sender, Lang.COMMAND_ADMIN_REMOVE, "<player>", targetPlayer.getName());
											Lang.sendMessage(targetPlayer, Lang.COMMAND_ADMIN_REMOVED);
										} else {
											if (sender instanceof ConsoleCommandSender || !targetPlayer.hasPermission(Permissions.FAKE_ADMIN_EXEMPT)) {
												PlayerPreAdminEvent playerPreAdminEvent = new PlayerPreAdminEvent(targetPlayer);
												if (!playerPreAdminEvent.isCancelled()) {
													this.getPlugin().getManager().getAdminManager().addFakeAdmin(targetPlayer.getUniqueId());
													List<String> currentFakeAdmins = this.getPlugin().getManager().getConfigManager().getConfig().getStringList("Fake admins");
													if (currentFakeAdmins == null)
														currentFakeAdmins = new ArrayList<>();
													if (!currentFakeAdmins.contains(targetPlayer.getUniqueId().toString())) {
														currentFakeAdmins.add(targetPlayer.getUniqueId().toString());
														this.getPlugin().getManager().getConfigManager().getConfig().set("Fake admins", currentFakeAdmins);
														this.getPlugin().getManager().getConfigManager().saveConfig();
													}

													Lang.sendReplacedMessage(sender, Lang.COMMAND_ADMIN_ADD, "<player>", targetPlayer.getName());
													Lang.sendMessage(targetPlayer, Lang.COMMAND_ADMIN_ADDED);

													targetPlayer.getServer().getPluginManager().callEvent(new PlayerAdminEvent(targetPlayer));
												} else {
													Lang.sendMessage(sender, Lang.COMMAND_ADMIN_FAILED);
												}
											} else {
												Lang.sendReplacedMessage(sender, Lang.COMMAND_ADMIN_EXEMPT, "<player>", targetPlayer.getName());
											}
										}
									} else {
										Lang.sendReplacedMessage(sender, Lang.GENERAL_PLAYER_UNKNOWN, "<player>", strPlayer);
									}
								} else {
									Lang.sendReplacedMessage(sender, Lang.COMMAND_USAGE, "<usage>", cmd.getName() + " " + strCommand.toLowerCase() + " <player>");
								}
							} else {
								Lang.sendReplacedMessage(sender, Lang.GENERAL_NO_PERMISSION, "<command>", strCommand.toLowerCase());
							}
						} else if (strCommand.equalsIgnoreCase("clear")) {
							if (sender.hasPermission(Permissions.COMMAND_CLEAR)) {
								if (args.length == 1) {
									List<UUID> fakeAdmins = this.getPlugin().getManager().getAdminManager().getFakeAdmins();
									for (UUID previousFakeAdminUUID : fakeAdmins) {
										List<UUID> bannedPlayers = this.getPlugin().getManager().getBanManager().getBannedUUIDs();
										for (UUID bannedPlayerUUID : bannedPlayers)
											Utilities.showPlayer(previousFakeAdminUUID, bannedPlayerUUID);
										List<UUID> kickedPlayers = this.getPlugin().getManager().getKickManager().getKickedUUIDs();
										for (UUID kickedPlayerUUID : kickedPlayers)
											Utilities.showPlayer(previousFakeAdminUUID, kickedPlayerUUID);
									}

									this.getPlugin().getManager().getAdminManager().clearFakeAdmins();
									this.getPlugin().getManager().getBanManager().clearBans();
									this.getPlugin().getManager().getKickManager().clearKicks();
									this.getPlugin().getManager().getMuteManager().clearMutes();
									this.getPlugin().getManager().getNicknameManager().clearNicknames();

									this.getPlugin().getManager().getConfigManager().getConfig().set("Fake admins", new ArrayList<>());
									this.getPlugin().getManager().getConfigManager().saveConfig();
									Lang.sendMessage(sender, Lang.COMMAND_CLEAR);
								} else {
									Lang.sendReplacedMessage(sender, Lang.COMMAND_USAGE, "<usage>", cmd.getName() + " " + strCommand.toLowerCase());
								}
							} else {
								Lang.sendReplacedMessage(sender, Lang.GENERAL_NO_PERMISSION, "<command>", strCommand.toLowerCase());
							}
						} else if (strCommand.equalsIgnoreCase("list")) {
							if (sender.hasPermission(Permissions.COMMAND_LIST)) {
								if (args.length == 1) {
									List<UUID> fakeAdmins = this.getPlugin().getManager().getAdminManager().getFakeAdmins();
									List<String> onlineFakeAdmins = new ArrayList<>();
									for (UUID fakeAdminUUID : fakeAdmins) {
										Player fakeAdmin = sender.getServer().getPlayer(fakeAdminUUID);
										if (fakeAdmin != null && fakeAdmin.isOnline())
											onlineFakeAdmins.add(fakeAdmin.getName());
									}
									StringBuilder strFakeAdmins = new StringBuilder();
									for (int i = 0; i < onlineFakeAdmins.size(); i++) {
										if (i == onlineFakeAdmins.size() - 1)
											strFakeAdmins.append(onlineFakeAdmins.get(i));
										else strFakeAdmins.append(onlineFakeAdmins.get(i)).append(", ");
									}
									Lang.sendReplacedMessage(sender, Lang.COMMAND_LIST, "<amount>", String.valueOf(onlineFakeAdmins.size()), "<admins>", (onlineFakeAdmins.isEmpty() ? "None" : strFakeAdmins.toString()));
								} else {
									Lang.sendReplacedMessage(sender, Lang.COMMAND_USAGE, "<usage>", cmd.getName() + " " + strCommand.toLowerCase());
								}
							} else {
								Lang.sendReplacedMessage(sender, Lang.GENERAL_NO_PERMISSION, "<command>", strCommand.toLowerCase());
							}
						} else if (strCommand.equalsIgnoreCase("spy")) {
							if (sender.hasPermission(Permissions.COMMAND_SPY)) {
								if (sender instanceof Player) {
									if (args.length == 1) {
										Player player = (Player) sender;
										if (this.getPlugin().getManager().getConfigManager().canFakeAdminsBecomeSpies() || !this.getPlugin().getManager().getSpyManager().isSpy(player.getUniqueId())) {
											if (this.getPlugin().getManager().getSpyManager().isSpy(player.getUniqueId())) {
												this.getPlugin().getManager().getSpyManager().removeSpy(player.getUniqueId());

												List<String> currentSpies = this.getPlugin().getManager().getConfigManager().getConfig().getStringList("Command spies");
												currentSpies.remove(player.getUniqueId().toString());
												this.getPlugin().getManager().getConfigManager().getConfig().set("Command spies", currentSpies);
												this.getPlugin().getManager().getConfigManager().saveConfig();

												Lang.sendMessage(player, Lang.COMMAND_SPY_OFF);
											} else {
												this.getPlugin().getManager().getSpyManager().addSpy(player.getUniqueId());

												List<String> currentSpies = this.getPlugin().getManager().getConfigManager().getConfig().getStringList("Command spies");
												currentSpies.add(player.getUniqueId().toString());
												this.getPlugin().getManager().getConfigManager().getConfig().set("Command spies", currentSpies);
												this.getPlugin().getManager().getConfigManager().saveConfig();

												Lang.sendMessage(player, Lang.COMMAND_SPY_ON);
											}
										} else {
											Lang.sendMessage(player, Lang.COMMAND_SPY_FAKE_ADMIN);
										}
									} else {
										Lang.sendReplacedMessage(sender, Lang.COMMAND_USAGE, "<usage>", cmd.getName() + " " + strCommand.toLowerCase());
									}
								} else {
									Lang.sendReplacedMessage(sender, Lang.GENERAL_PLAYER, "<command>", strCommand.toLowerCase());
								}
							} else {
								Lang.sendReplacedMessage(sender, Lang.GENERAL_NO_PERMISSION, "<command>", strCommand.toLowerCase());
							}
						} else if (strCommand.equalsIgnoreCase("whois")) {
							if (sender.hasPermission(Permissions.COMMAND_WHOIS)) {
								if (args.length == 2) {
									String strNickname = args[1];
									String username = this.getPlugin().getManager().getNicknameManager().getUsername(strNickname);
									if (username != null) {
										Lang.sendReplacedMessage(sender, Lang.COMMAND_WHOIS, "<nickname>", Utilities.replaceChatColoursAndFormats(strNickname), "<player>", username);
									} else {
										Lang.sendReplacedMessage(sender, Lang.COMMAND_WHOIS_UNKNOWN, "<nickname>", Utilities.replaceChatColoursAndFormats(strNickname));
									}
								} else {
									Lang.sendReplacedMessage(sender, Lang.COMMAND_USAGE, "<usage>", cmd.getName() + " " + strCommand.toLowerCase() + " <nickname>");
								}
							} else {
								Lang.sendReplacedMessage(sender, Lang.GENERAL_NO_PERMISSION, "<command>", strCommand.toLowerCase());
							}
						} else {
							Lang.sendReplacedMessage(sender, Lang.COMMAND_UNKNOWN, "<command>", strCommand.toLowerCase());
						}
					} else {
						Lang.sendReplacedMessage(sender, Lang.COMMAND_USAGE, "<usage>", cmd.getUsage().replaceFirst("/", "").replace("<command>", cmd.getName()));
					}
				} else {
					Lang.sendMessage(sender, Lang.COMMAND_FAKE_UNKNOWN);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				Lang.sendMessage(sender, Lang.GENERAL_ERROR);
			}
			return true;
		}
		return false;
	}

	public FakeAdmin getPlugin() {
		return FakeAdmin.getInstance();
	}

}
