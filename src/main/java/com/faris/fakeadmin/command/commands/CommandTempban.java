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
import java.util.UUID;

public class CommandTempban extends AdminCommand {

	@Override
	public CommandReturnType onCommand(Player player, String command, String[] args) throws Exception {
		if (!ConfigCommand.TEMPBAN.isEnabled()) return CommandReturnType.DISABLED;
		if (args.length >= 2) {
			String strTarget = args[0];
			Player target = Bukkit.getServer().getPlayer(strTarget);
			if (target == null || !target.isOnline() || (!target.isOp() && !(EssentialsAPI.hasEssentials() && target.hasPermission("essentials.tempban.exempt")) && !this.getPlugin().getManager().getAdminManager().isFakeAdmin(target.getUniqueId()))) {
				long banDuration = Utilities.DateUtilities.getTimeAsMilliSeconds(args[1]);
				Date banDate = new Date(banDuration);
				String strFormat = Utilities.DateUtilities.formatDateDiff(System.currentTimeMillis() + banDuration);

				String tempbanReason = this.getFinalArgs(args, banDuration == -1000L ? 1 : 2);
				if (tempbanReason.isEmpty()) tempbanReason = "The Ban Hammer has spoken!";
				if (target != null && target.isOnline() && banDuration != 0L) {
					if (!this.getPlugin().getManager().getBanManager().isBanned(target.getUniqueId())) {
						TemporaryPlayer bannedPlayer = this.getPlugin().getManager().getBanManager().banPlayer(target.getUniqueId(), banDate.getTime());
						this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Banned players." + target.getUniqueId().toString(), bannedPlayer.serialize());
						this.getPlugin().getManager().getConfigManager().savePlayersConfig();
					}
					for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
						Player fakeAdmin = player.getServer().getPlayer(fakeAdminUUID);
						if (fakeAdmin != null && fakeAdmin.isOnline() && target.isOnline())
							fakeAdmin.hidePlayer(target);
					}
				}
				if (ConfigCommand.TEMPBAN.hasSpecialAttribute("Tempban message")) {
					List<String> tempbanMessages = ConfigCommand.TEMPBAN.getSpecialAttribute("Tempban message").castValue(List.class);
					for (String tempbanMsg : tempbanMessages)
						this.getPlugin().getManager().getAdminManager().broadcastMessage(Utilities.replaceChatColoursAndFormats(tempbanMsg.replace("<banner>", player.getName()).replace("<banned>", (target != null ? target.getName() : strTarget))).replace("<duration>", strFormat).replace("<reason>", tempbanReason));
				}
			} else {
				if (ConfigCommand.TEMPBAN.hasSpecialAttribute("Exempt player"))
					player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.TEMPBAN.getSpecialAttribute("Exempt player").castValue(String.class).replace("<banned>", target.getName())));
			}
			return CommandReturnType.VALID;
		}
		return CommandReturnType.INVALID_USAGE;
	}

	@Override
	public List<String> getUsage() {
		return ConfigCommand.TEMPBAN.getUsageMessage("tempban <playername> <datediff> <reason>");
	}

}
