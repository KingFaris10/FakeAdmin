package com.faris.fakeadmin.command.commands;

import com.faris.fakeadmin.ConfigCommand;
import com.faris.fakeadmin.command.AdminCommand;
import com.faris.fakeadmin.helper.Utilities;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.List;
import java.util.UUID;

public class CommandUnban extends AdminCommand {

	/*
		Possibly add a player to a scheduled list. Make a repeated scheduler and remove them every second.
		Only do this if the target is offline.
	 */

	@Override
	public CommandReturnType onCommand(final Player player, String command, String[] args) throws Exception {
		if (!ConfigCommand.UNBAN.isEnabled()) return CommandReturnType.DISABLED;
		if (args.length == 1) {
			final String strTarget;
			Player target = Bukkit.getServer().getPlayerExact(args[0]);
			if (target != null) strTarget = target.getName();
			else strTarget = args[0];

			if (target != null) {
				this.unbanPlayer(player, player.getName(), target, target.getName(), target.getUniqueId());
			} else {
				OfflinePlayer offlinePlayer = Utilities.getOfflinePlayer(strTarget);
				if (offlinePlayer != null) {
					this.unbanPlayer(player, player.getName(), null, offlinePlayer.getName(), offlinePlayer.getUniqueId());
				} else {
					if (ConfigCommand.UNBAN.hasSpecialAttribute("Player not found"))
						player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.UNBAN.getSpecialAttribute("Player not found").castValue(String.class)));
				}
			}
			return CommandReturnType.VALID;
		}
		return CommandReturnType.INVALID_USAGE;
	}

	private void unbanPlayer(Player player, String playerName, Player target, String targetName, UUID targetUUID) {
		if (targetUUID == null) return;
		else if (targetName == null && target == null) return;
		else if (targetName == null) targetName = target.getName();
		if (this.getPlugin().getManager().getBanManager().isBanned(targetUUID)) {
			List<UUID> fakeAdmins = this.getPlugin().getManager().getAdminManager().getFakeAdmins();
			for (UUID fakeAdminUUID : fakeAdmins) {
				this.getPlugin().getManager().getBanManager().unbanPlayers(fakeAdminUUID, targetUUID);
				this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Banned players." + targetUUID, null);
				if (this.getPlugin().getManager().getConfigManager().getPlayersConfig().isConfigurationSection("Banned players") && this.getPlugin().getManager().getConfigManager().getPlayersConfig().getConfigurationSection("Banned players").getValues(false).isEmpty())
					this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Banned players", null);
				this.getPlugin().getManager().getConfigManager().savePlayersConfig();

				Player fakeAdmin = (player != null ? player.getServer() : Bukkit.getServer()).getPlayer(fakeAdminUUID);
				if (fakeAdmin != null && fakeAdmin.isOnline() && target != null && target.isOnline() && !Utilities.PlayerUtilities.isVanished(fakeAdmin, target))
					fakeAdmin.showPlayer(target);
			}
		}
		if (ConfigCommand.UNBAN.hasSpecialAttribute("Unban message")) {
			List<String> unbanMessages = ConfigCommand.UNBAN.getSpecialAttribute("Unban message").castValue(List.class);
			for (String unbanMsg : unbanMessages)
				this.getPlugin().getManager().getAdminManager().broadcastMessage(Utilities.replaceChatColoursAndFormats(unbanMsg.replace("<banner>", playerName).replace("<banned>", targetName)));
		}
	}

	@Override
	public List<String> getUsage() {
		return ConfigCommand.UNBAN.getUsageMessage("unban <player>");
	}

}
