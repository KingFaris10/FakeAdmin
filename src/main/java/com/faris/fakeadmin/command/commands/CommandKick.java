package com.faris.fakeadmin.command.commands;

import com.faris.fakeadmin.ConfigCommand;
import com.faris.fakeadmin.command.AdminCommand;
import com.faris.fakeadmin.helper.TemporaryPlayer;
import com.faris.fakeadmin.helper.Utilities;
import com.faris.fakeadmin.hook.EssentialsAPI;
import org.bukkit.entity.*;

import java.util.List;
import java.util.UUID;

public class CommandKick extends AdminCommand {

	@Override
	public CommandReturnType onCommand(Player player, String command, String[] args) throws Exception {
		if (!ConfigCommand.KICK.isEnabled()) return CommandReturnType.DISABLED;
		if (args.length >= 1) {
			String strTarget = args[0];
			Player target = player.getServer().getPlayer(strTarget);
			if (target != null && target.isOnline()) {
				if (!target.isOp() && !(EssentialsAPI.hasEssentials() && target.hasPermission("essentials.kick.exempt")) && !this.getPlugin().getManager().getAdminManager().isFakeAdmin(target.getUniqueId())) {
					String kickReason = this.getFinalArgs(args, 1);
					if (kickReason.isEmpty()) kickReason = "Kicked from server.";
					TemporaryPlayer kickedPlayer = new TemporaryPlayer(target.getUniqueId(), (long) (Utilities.getRandom().nextInt(20) + 5));
					if (!this.getPlugin().getManager().getKickManager().isKicked(target.getUniqueId()))
						this.getPlugin().getManager().getKickManager().kickPlayer(kickedPlayer);
					for (UUID fakeAdminUUID : this.getPlugin().getManager().getAdminManager().getFakeAdmins()) {
						Player fakeAdmin = player.getServer().getPlayer(fakeAdminUUID);
						if (fakeAdmin != null && fakeAdmin.isOnline() && target.isOnline())
							fakeAdmin.hidePlayer(target);
					}
					if (ConfigCommand.KICK.hasSpecialAttribute("Kick message")) {
						List<String> kickMessages = ConfigCommand.KICK.getSpecialAttribute("Kick message").castValue(List.class);
						for (String kickMsg : kickMessages)
							this.getPlugin().getManager().getAdminManager().broadcastMessage(Utilities.replaceChatColoursAndFormats(kickMsg.replace("<kicker>", player.getName()).replace("<kicked>", target.getName())).replace("<reason>", kickReason));
					}
				} else {
					if (ConfigCommand.KICK.hasSpecialAttribute("Exempt player"))
						player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.KICK.getSpecialAttribute("Exempt player").castValue(String.class).replace("<kicked>", target.getName())));
				}
			} else {
				if (ConfigCommand.KICK.hasSpecialAttribute("Player not found"))
					player.sendMessage(Utilities.replaceChatColoursAndFormats(ConfigCommand.KICK.getSpecialAttribute("Player not found").castValue(String.class)));
			}
			return CommandReturnType.VALID;
		}
		return CommandReturnType.INVALID_USAGE;
	}

	@Override
	public List<String> getUsage() {
		return ConfigCommand.KICK.getUsageMessage("kick <player> [reason]");
	}

}
