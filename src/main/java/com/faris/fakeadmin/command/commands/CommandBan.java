package com.faris.fakeadmin.command.commands;

import com.faris.fakeadmin.ConfigCommand;
import com.faris.fakeadmin.command.AdminCommand;
import com.faris.fakeadmin.helper.TemporaryPlayer;
import com.faris.fakeadmin.helper.Utilities;
import com.faris.fakeadmin.hook.EssentialsAPI;
import org.bukkit.*;
import org.bukkit.entity.*;

import java.util.List;
import java.util.UUID;

public class CommandBan extends AdminCommand {

	@Override
	public CommandReturnType onCommand(Player player, String command, String[] args) throws Exception {
		if (!ConfigCommand.BAN.isEnabled()) return CommandReturnType.DISABLED;
		if (args.length > 0) {
			String strTarget = args[0];
			Player target = Bukkit.getServer().getPlayer(strTarget);
			if (target == null || (!target.isOp() && !(EssentialsAPI.hasEssentials() && target.hasPermission("essentials.ban.exempt")) && !this.getPlugin().getManager().getAdminManager().isFakeAdmin(target.getUniqueId()))) {
				String banReason = this.getFinalArgs(args, 1);
				if (banReason.isEmpty()) banReason = "The Ban Hammer has spoken!";
				if (target != null && target.isOnline()) {
					if (!this.getPlugin().getManager().getBanManager().isBanned(target.getUniqueId())) {
						TemporaryPlayer bannedPlayer = this.getPlugin().getManager().getBanManager().banPlayer(target.getUniqueId());
						this.getPlugin().getManager().getConfigManager().getPlayersConfig().set("Banned players." + target.getUniqueId().toString(), bannedPlayer.serialize());
						this.getPlugin().getManager().getConfigManager().savePlayersConfig();
					}
					for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
						Player fakeAdmin = player.getServer().getPlayer(fakeAdminUUID);
						if (fakeAdmin != null && fakeAdmin.isOnline() && target.isOnline())
							fakeAdmin.hidePlayer(target);
					}
				}
				if (ConfigCommand.BAN.hasSpecialAttribute("Ban message")) {
					List<String> banMessages = ConfigCommand.BAN.getSpecialAttribute("Ban message").castValue(List.class);
					for (String banMsg : banMessages)
						this.getPlugin().getManager().getAdminManager().broadcastMessage(Utilities.replaceChatColoursAndFormats(banMsg.replace("<banner>", player.getName()).replace("<banned>", target != null ? target.getName() : strTarget)).replace("<reason>", banReason));
				}
			} else {
				if (ConfigCommand.BAN.hasSpecialAttribute("Exempt player"))
					player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.BAN.getSpecialAttribute("Exempt player").castValue(String.class).replace("<banned>", target.getName())));
			}
			return CommandReturnType.VALID;
		}
		return CommandReturnType.INVALID_USAGE;
	}

	@Override
	public List<String> getUsage() {
		return ConfigCommand.BAN.getUsageMessage("ban <player> [reason]");
	}

}
