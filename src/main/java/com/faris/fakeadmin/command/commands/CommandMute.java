package com.faris.fakeadmin.command.commands;

import com.faris.fakeadmin.ConfigCommand;
import com.faris.fakeadmin.command.AdminCommand;
import com.faris.fakeadmin.helper.TemporaryPlayer;
import com.faris.fakeadmin.helper.Utilities;
import com.faris.fakeadmin.hook.EssentialsAPI;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.Date;
import java.util.List;

public class CommandMute extends AdminCommand {

	@Override
	public CommandReturnType onCommand(Player player, String command, String[] args) throws Exception {
		if (!ConfigCommand.MUTE.isEnabled()) return CommandReturnType.DISABLED;
		if (args.length > 0) {
			String strTarget = args[0];
			Player target = Bukkit.getServer().getPlayer(strTarget);
			if (target == null || !target.isOnline() || (!target.isOp() && !(EssentialsAPI.hasEssentials() && target.hasPermission("essentials.mute.exempt")) && !this.getPlugin().getManager().getAdminManager().isFakeAdmin(target.getUniqueId()))) {
				if (args.length > 1) {
					if (!Utilities.isAlphabetical(args[1])) {
						long muteDuration = Utilities.DateUtilities.getTimeAsMilliSeconds(args[1]);
						Date muteDate = new Date(muteDuration);
						String strFormat = Utilities.DateUtilities.formatDateDiff(System.currentTimeMillis() + muteDuration);
						if (target != null && target.isOnline() && muteDuration != 0L) {
							this.getPlugin().getManager().getMuteManager().removeMute(target.getUniqueId());
							TemporaryPlayer temporaryPlayer = this.getPlugin().getManager().getMuteManager().mutePlayer(target.getUniqueId(), muteDate.getTime());
							this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Muted players." + target.getUniqueId().toString(), temporaryPlayer.serialize());
							this.getPlugin().getManager().getConfigManager().savePlayersConfig();
						}
						if (ConfigCommand.MUTE.hasSpecialAttribute("Tempmute private message")) {
							List<String> muteMessages = ConfigCommand.MUTE.getSpecialAttribute("Tempmute private message").castValue(List.class);
							for (String muteMsg : muteMessages)
								this.getPlugin().getManager().getAdminManager().broadcastMessage(Utilities.replaceChatColoursAndFormats(muteMsg.replace("<muter>", player.getName()).replace("<muted>", (target != null ? target.getName() : strTarget)).replace("<duration>", strFormat)));
						}
						if (ConfigCommand.MUTE.hasSpecialAttribute("Mute message")) {
							List<String> muteMessages = ConfigCommand.MUTE.getSpecialAttribute("Mute message").castValue(List.class);
							for (String muteMsg : muteMessages)
								this.getPlugin().getManager().getAdminManager().broadcastMessage(Utilities.replaceChatColoursAndFormats(muteMsg.replace("<muter>", player.getName()).replace("<muted>", (target != null ? target.getName() : strTarget))));
						}
					} else {
						if (ConfigCommand.MUTE.hasSpecialAttribute("Illegal date format"))
							player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.MUTE.getSpecialAttribute("Illegal date format").castValue(String.class)));
					}
				} else {
					boolean sendMessages = true;
					if (target != null && target.isOnline()) {
						if (this.getPlugin().getManager().getMuteManager().isMuted(target.getUniqueId())) {
							this.getPlugin().getManager().getMuteManager().removeMute(target.getUniqueId());
							this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Muted players." + target.getUniqueId().toString(), null);
							if (!this.getPlugin().getManager().getConfigManager().getPlayersConfig().isConfigurationSection("Muted players") && this.getPlugin().getManager().getConfigManager().getPlayersConfig().getConfigurationSection("Muted players").getValues(false).isEmpty())
								this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Muted players", null);
							this.getPlugin().getManager().getConfigManager().savePlayersConfig();

							if (ConfigCommand.MUTE.hasSpecialAttribute("Unmute private message")) {
								List<String> unmuteMessages = ConfigCommand.MUTE.getSpecialAttribute("Unmute private message").castValue(List.class);
								for (String unmuteMsg : unmuteMessages)
									this.getPlugin().getManager().getAdminManager().broadcastMessage(Utilities.replaceChatColoursAndFormats(unmuteMsg.replace("<muter>", player.getName()).replace("<muted>", (target.getName()))));
							}

							sendMessages = false;
						} else {
							TemporaryPlayer mutedPlayer = this.getPlugin().getManager().getMuteManager().mutePlayer(target.getUniqueId());
							this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Muted players." + target.getUniqueId().toString(), mutedPlayer.serialize());
							this.getPlugin().getManager().getConfigManager().savePlayersConfig();
						}
					}
					if (sendMessages) {
						if (ConfigCommand.MUTE.hasSpecialAttribute("Mute private message")) {
							List<String> muteMessages = ConfigCommand.MUTE.getSpecialAttribute("Mute private message").castValue(List.class);
							for (String muteMsg : muteMessages)
								this.getPlugin().getManager().getAdminManager().broadcastMessage(Utilities.replaceChatColoursAndFormats(muteMsg.replace("<muter>", player.getName()).replace("<muted>", (target != null ? target.getName() : strTarget))));
						}
						if (ConfigCommand.MUTE.hasSpecialAttribute("Mute message")) {
							List<String> muteMessages = ConfigCommand.MUTE.getSpecialAttribute("Mute message").castValue(List.class);
							for (String muteMsg : muteMessages)
								this.getPlugin().getManager().getAdminManager().broadcastMessage(Utilities.replaceChatColoursAndFormats(muteMsg.replace("<muter>", player.getName()).replace("<muted>", (target != null ? target.getName() : strTarget))));
						}
					}
				}
			} else {
				if (ConfigCommand.MUTE.hasSpecialAttribute("Exempt player"))
					player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.MUTE.getSpecialAttribute("Exempt player").castValue(String.class).replace("<muted>", target.getName())));
			}
			return CommandReturnType.VALID;
		}
		return CommandReturnType.INVALID_USAGE;
	}

	@Override
	public List<String> getUsage() {
		return ConfigCommand.MUTE.getUsageMessage("mute <player> [datediff]");
	}

}
