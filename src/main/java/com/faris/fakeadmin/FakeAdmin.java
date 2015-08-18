package com.faris.fakeadmin;

import com.faris.fakeadmin.command.AdminCommands;
import com.faris.fakeadmin.command.CommandListener;
import com.faris.fakeadmin.helper.Lang;
import com.faris.fakeadmin.helper.TemporaryPlayer;
import com.faris.fakeadmin.helper.Utilities;
import com.faris.fakeadmin.manager.GlobalManager;
import net.gravitydevelopment.updater.Updater;
import org.bukkit.configuration.serialization.*;
import org.bukkit.entity.*;
import org.bukkit.permissions.*;
import org.bukkit.plugin.java.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FakeAdmin extends JavaPlugin {

	private static FakeAdmin pluginInstance = null;

	private GlobalManager manager = null;

	@Override
	public void onEnable() {
		pluginInstance = this;
		Lang.initialiseMessages();
		AdminCommands.initialiseCommands();
		Permissions.initialisePermissions();
		this.manager = new GlobalManager();
		this.manager.getConfigManager().loadConfiguration();

		this.getCommand("fakeadmin").setExecutor(new CommandListener());
		this.getServer().getPluginManager().registerEvents(new EventListener(), this);
		for (Permission registeredPermission : Permissions.getPermissions()) {
			try {
				this.getServer().getPluginManager().addPermission(registeredPermission);
			} catch (Exception ignored) {
			}
		}

		ConfigurationSerialization.registerClass(TemporaryPlayer.class);

		List<TemporaryPlayer> bannedList = this.manager.getBanManager().getBanned();
		for (UUID fakeAdminUUID : this.manager.getAdminManager().getFakeAdmins()) {
			Player fakeAdmin = this.getServer().getPlayer(fakeAdminUUID);
			if (fakeAdmin != null && fakeAdmin.isOnline()) {
				for (TemporaryPlayer temporaryBannedPlayer : bannedList) {
					if (temporaryBannedPlayer != null) {
						Player bannedPlayer = this.getServer().getPlayer(temporaryBannedPlayer.getUniqueId());
						if (bannedPlayer != null && bannedPlayer.isOnline()) fakeAdmin.hidePlayer(bannedPlayer);
					}
				}
			}
		}

		this.getServer().getScheduler().runTaskTimer(this, new Runnable() {
			@Override
			public void run() {
				List<TemporaryPlayer> bannedPlayers = getManager().getBanManager().getBanned();
				List<UUID> expiredBannedPlayers = new ArrayList<>();
				boolean configChanged = false;
				for (TemporaryPlayer bannedPlayer : bannedPlayers) {
					if (bannedPlayer != null && bannedPlayer.hasExpired()) {
						expiredBannedPlayers.add(bannedPlayer.getUniqueId());
						getManager().getConfigManager().getPlayersConfig().set("Banned players." + bannedPlayer.getUniqueId().toString(), null);
						if (getManager().getConfigManager().getPlayersConfig().isConfigurationSection("Banned players") && getManager().getConfigManager().getPlayersConfig().getConfigurationSection("Banned players").getValues(false).isEmpty())
							getManager().getConfigManager().getPlayersConfig().set("Banned players", null);
						configChanged = true;

						Player expiredPlayer = getServer().getPlayer(bannedPlayer.getUniqueId());
						if (expiredPlayer != null && expiredPlayer.isOnline()) {
							for (UUID fakeAdminUUID : getManager().getAdminManager().getFakeAdmins()) {
								Player fakeAdmin = getServer().getPlayer(fakeAdminUUID);
								if (fakeAdmin != null && fakeAdmin.isOnline() && !Utilities.PlayerUtilities.isVanished(fakeAdmin, expiredPlayer))
									fakeAdmin.showPlayer(expiredPlayer);
							}
						}
					}
				}
				getManager().getBanManager().unbanPlayers(expiredBannedPlayers);

				List<TemporaryPlayer> kickedPlayers = getManager().getKickManager().getKicked();
				List<UUID> expiredKickedPlayers = new ArrayList<>();
				for (TemporaryPlayer kickedPlayer : kickedPlayers) {
					if (kickedPlayer != null && kickedPlayer.hasExpired()) {
						expiredKickedPlayers.add(kickedPlayer.getUniqueId());

						Player expiredPlayer = getServer().getPlayer(kickedPlayer.getUniqueId());
						if (expiredPlayer != null && expiredPlayer.isOnline()) {
							for (UUID fakeAdminUUID : getManager().getAdminManager().getFakeAdmins()) {
								Player fakeAdmin = getServer().getPlayer(fakeAdminUUID);
								if (fakeAdmin != null && fakeAdmin.isOnline() && !Utilities.PlayerUtilities.isVanished(fakeAdmin, expiredPlayer))
									fakeAdmin.showPlayer(expiredPlayer);
							}
						}
					}
				}
				getManager().getKickManager().removeKicks(expiredKickedPlayers);

				if (configChanged) getManager().getConfigManager().savePlayersConfig();
			}
		}, 20L, 20L);

		if (this.getManager().getConfigManager().shouldCheckForUpdates()) {
			this.getLogger().info("Checking for updates...");
			new Updater(this, 91333, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, new Updater.UpdateCallback() {
				@Override
				public void onFinish(Updater updater) {
					switch (updater.getResult()) {
						case UPDATE_AVAILABLE:
							getLogger().info("An update was found!");
							if (getManager().getConfigManager().shouldAutoUpdate()) {
								new Updater(FakeAdmin.this, 91333, getFile(), Updater.UpdateType.NO_VERSION_CHECK, true);
							} else {
								getLogger().info("Current version: " + getDescription().getFullName());
								getLogger().info("Latest version: " + updater.getLatestName());
								getLogger().info("Download link: " + updater.getLatestFileLink());
							}
							break;
						case DISABLED:
							getLogger().info("No update was found, you have disabled update checking in the Updater configuration.");
							break;
						default:
							getLogger().info("No update was found, you have the latest version.");
							break;
					}
				}
			}, false);
		}
	}

	@Override
	public void onDisable() {
		this.getServer().getScheduler().cancelTasks(this);

		if (this.manager != null) {
			for (UUID fakeAdminUUID : this.manager.getAdminManager().getFakeAdmins()) {
				Player fakeAdmin = this.getServer().getPlayer(fakeAdminUUID);
				if (fakeAdmin != null && fakeAdmin.isOnline()) {
					List<TemporaryPlayer> bannedList = this.manager.getBanManager().getBanned();
					for (TemporaryPlayer temporaryBannedPlayer : bannedList) {
						if (temporaryBannedPlayer != null) {
							Player banned = this.getServer().getPlayer(temporaryBannedPlayer.getUniqueId());
							if (banned != null && banned.isOnline() && !Utilities.PlayerUtilities.isVanished(fakeAdmin, banned))
								fakeAdmin.showPlayer(banned);
						}
					}

					List<TemporaryPlayer> kickedList = this.manager.getKickManager().getKicked();
					for (TemporaryPlayer kickedPlayer : kickedList) {
						if (kickedPlayer != null) {
							Player kicked = this.getServer().getPlayer(kickedPlayer.getUniqueId());
							if (kicked != null && kicked.isOnline() && !Utilities.PlayerUtilities.isVanished(fakeAdmin, kicked))
								fakeAdmin.showPlayer(kicked);
						}
					}
				}
			}
		}

		for (Permission registeredPermission : Permissions.getPermissions()) {
			try {
				this.getServer().getPluginManager().removePermission(registeredPermission);
			} catch (Exception ignored) {
			}
		}

		AdminCommands.unregisterCommands();
		Permissions.deinitialisePermissions();

		ConfigurationSerialization.unregisterClass(TemporaryPlayer.class);

		if (this.manager != null) this.manager.onDisable();
		this.manager = null;
		pluginInstance = null;
	}

	public GlobalManager getManager() {
		return this.manager;
	}

	public static FakeAdmin getInstance() {
		return pluginInstance;
	}

}
