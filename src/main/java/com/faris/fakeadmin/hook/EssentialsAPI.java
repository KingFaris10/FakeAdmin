package com.faris.fakeadmin.hook;

import com.faris.fakeadmin.helper.Utilities;
import org.bukkit.entity.*;

public class EssentialsAPI {

	public static boolean hasEssentials() {
		return Utilities.isPluginEnabled("Essentials");
	}

	public static boolean isMuted(Player player) {
		if (hasEssentials()) {
			try {
				com.earth2me.essentials.Essentials essentials = (com.earth2me.essentials.Essentials) Utilities.getPlugin("Essentials");
				com.earth2me.essentials.User user = essentials.getUser(player);
				return user.isMuted();
			} catch (Exception ex) {
			}
		}
		return false;
	}

	public static boolean isVanished(Player player) {
		if (hasEssentials()) {
			try {
				com.earth2me.essentials.Essentials essentials = (com.earth2me.essentials.Essentials) Utilities.getPlugin("Essentials");
				com.earth2me.essentials.User user = essentials.getUser(player);
				return user.isVanished();
			} catch (Exception ex) {
			}
		}
		return false;
	}

}
