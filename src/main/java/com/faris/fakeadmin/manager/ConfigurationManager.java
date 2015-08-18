package com.faris.fakeadmin.manager;

import com.faris.fakeadmin.ConfigCommand;
import com.faris.fakeadmin.FakeAdmin;
import com.faris.fakeadmin.helper.TemporaryPlayer;
import com.faris.fakeadmin.helper.Utilities;
import com.faris.fakeadmin.helper.custom.CustomMap;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.configuration.file.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;

public class ConfigurationManager implements Manager {

	private boolean loading = false;

	private boolean[] updater = new boolean[2];

	@Override
	public void onDisable() {
		if (!this.loading) {
			this.commandsConfig = null;
			this.commandsFile = null;
		}
		this.loading = false;
	}

	public void loadConfiguration() {
		this.getConfig().options().header("FakeAdmin configuration");
		this.getConfig().addDefault("Updater.Check", true);
		this.getConfig().addDefault("Updater.Update", false);
		this.getConfig().addDefault("Fake admins", new ArrayList<String>());
		this.getConfig().addDefault("Command spies", new ArrayList<String>());
		this.getConfig().options().copyDefaults(true);
		this.getConfig().options().copyHeader(true);
		this.saveConfig();

		this.loading = true;
		FakeAdmin.getInstance().getManager().onDisable(false);
		this.loading = false;

		this.updater[0] = this.getConfig().getBoolean("Updater.Check");
		this.updater[1] = this.getConfig().getBoolean("Updater.Update");

		List<String> listAdmins = this.getConfig().getStringList("Fake admins");
		for (String strFakeAdminUUID : listAdmins) {
			if (Utilities.isUUID(strFakeAdminUUID))
				FakeAdmin.getInstance().getManager().getAdminManager().addFakeAdmin(UUID.fromString(strFakeAdminUUID));
		}
		List<String> listSpies = this.getConfig().getStringList("Command spies");
		for (String strSpyUUID : listSpies) {
			if (Utilities.isUUID(strSpyUUID))
				FakeAdmin.getInstance().getManager().getSpyManager().addSpy(UUID.fromString(strSpyUUID));
		}

		this.loadCommands();
		this.loadUsers();
	}

	private void loadUsers() {
		if (ConfigCommand.NICK.isEnabled()) {
			int maxNickLength = ConfigCommand.NICK.hasSpecialAttribute("Max nickname length") ? ConfigCommand.NICK.getSpecialAttribute("Max nickname length").castValue(Integer.class) : 15;
			Map<String, Object> configNicknames = Utilities.getMap(this.getPlayersConfig().get("Nicknames"));
			for (Map.Entry<String, Object> userEntry : configNicknames.entrySet()) {
				String strUUID = userEntry.getKey();
				if (userEntry.getValue() != null) {
					if (Utilities.isUUID(strUUID)) {
						UUID userUUID = UUID.fromString(userEntry.getKey());
						if (FakeAdmin.getInstance().getManager().getAdminManager().isFakeAdmin(userUUID)) {
							String strNickname = userEntry.getValue().toString();
							if (strNickname != null && strNickname.length() > 0 && strNickname.length() <= maxNickLength)
								FakeAdmin.getInstance().getManager().getNicknameManager().setNickname(userUUID, strNickname);
							else this.getPlayersConfig().set("Nicknames." + userEntry.getKey(), null);
						} else {
							this.getPlayersConfig().set("Nicknames." + userEntry.getKey(), null);
						}
					} else {
						this.getPlayersConfig().set("Nicknames." + userEntry.getKey(), null);
					}
				} else {
					this.getPlayersConfig().set("Nicknames." + userEntry.getKey(), null);
				}
			}
		} else {
			FakeAdmin.getInstance().getManager().getNicknameManager().clearNicknames();
			this.getPlayersConfig().set("Nicknames", null);
		}

		if (ConfigCommand.BAN.isEnabled()) {
			Map<String, Object> configBannedPlayers = Utilities.getMap(this.getPlayersConfig().get("Banned players"));
			for (Map.Entry<String, Object> bannedPlayerEntry : configBannedPlayers.entrySet()) {
				TemporaryPlayer bannedPlayer = TemporaryPlayer.deserialize(bannedPlayerEntry.getValue());
				if (bannedPlayer != null && bannedPlayer.getUniqueId() != null)
					FakeAdmin.getInstance().getManager().getBanManager().banPlayer(bannedPlayer);
			}
		} else {
			this.getPlayersConfig().set("Banned players", null);
		}
		if (ConfigCommand.MUTE.isEnabled()) {
			Map<String, Object> configMutedPlayers = Utilities.getMap(this.getPlayersConfig().get("Muted players"));
			for (Map.Entry<String, Object> mutedPlayerEntry : configMutedPlayers.entrySet()) {
				TemporaryPlayer mutedPlayer = TemporaryPlayer.deserialize(mutedPlayerEntry.getValue());
				if (mutedPlayer != null && mutedPlayer.getUniqueId() != null)
					FakeAdmin.getInstance().getManager().getMuteManager().mutePlayer(mutedPlayer);
			}
		} else {
			this.getPlayersConfig().set("Muted players", null);
		}

		this.savePlayersConfig();
	}

	private void loadCommands() {
		this.getCommandsConfig().options().header("FakeAdmin commands configuration");
		for (ConfigCommand command : ConfigCommand.values()) {
			String commandKey = command.getConfigKey();
			this.getCommandsConfig().addDefault(commandKey + ".Enabled", command.isEnabled());
			this.getCommandsConfig().addDefault(commandKey + ".Usage messages", command.getUsageMessages());
			List<ConfigCommand.SpecialAttribute> specialAttributes = command.getSpecialAttributes();
			for (ConfigCommand.SpecialAttribute specialAttribute : specialAttributes)
				this.getCommandsConfig().addDefault(commandKey + "." + specialAttribute.getKey(), specialAttribute.getValue());
		}
		this.getCommandsConfig().options().copyDefaults(true);
		this.getCommandsConfig().options().copyHeader(true);
		this.saveCommandsConfig();

		for (ConfigCommand command : ConfigCommand.values()) {
			try {
				String commandKey = command.getConfigKey();
				CustomMap<String, Object> commandMap = Utilities.getMap(this.getCommandsConfig().get(commandKey));
				command.setEnabled(Boolean.parseBoolean(commandMap.remove("Enabled", Boolean.TRUE).toString()));
				command.setUsageMessages((List<String>) commandMap.remove("Usage messages", command.getUsageMessages()));
				command.clearSpecialAttributes();
				for (Map.Entry<String, Object> configEntry : commandMap.entrySet()) {
					command.addSpecialAttribute(configEntry.getKey(), configEntry.getValue());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				Bukkit.getServer().getLogger().log(Level.INFO, "Could not load the command '" + command.getConfigKey() + "' from the configuration.", ex);
			}
		}
	}

	public boolean shouldAutoUpdate() {
		return this.updater[1];
	}

	public boolean shouldCheckForUpdates() {
		return this.updater[0];
	}

	private File commandsFile = null;
	private YamlConfiguration commandsConfig = null;
	private File playersFile = null;
	private YamlConfiguration playersConfig = null;

	public FileConfiguration getConfig() {
		return FakeAdmin.getInstance().getConfig();
	}

	public void reloadConfig() {
		FakeAdmin.getInstance().reloadConfig();
	}

	public void saveConfig() {
		FakeAdmin.getInstance().saveConfig();
	}

	public YamlConfiguration getCommandsConfig() {
		if (this.commandsConfig == null || this.commandsFile == null) this.reloadCommandsConfig();
		return this.commandsConfig;
	}

	public void reloadCommandsConfig() {
		if (this.commandsFile == null)
			this.commandsFile = new File(FakeAdmin.getInstance().getDataFolder(), "commands.yml");
		try {
			List<Object> loadedConfig = loadConfigSafely(this.commandsFile);
			this.commandsConfig = (YamlConfiguration) loadedConfig.get(1);
			this.commandsFile = (File) loadedConfig.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void saveCommandsConfig() {
		if (this.commandsConfig == null || this.commandsFile == null) return;
		try {
			this.commandsConfig.save(this.commandsFile);
		} catch (Exception ignored) {
		}
	}

	public YamlConfiguration getPlayersConfig() {
		if (this.playersConfig == null || this.playersFile == null) this.reloadPlayersConfig();
		return this.playersConfig;
	}

	public void reloadPlayersConfig() {
		if (this.playersFile == null)
			this.playersFile = new File(FakeAdmin.getInstance().getDataFolder(), "players.yml");
		try {
			List<Object> loadedConfig = loadConfigSafely(this.playersFile);
			this.playersConfig = (YamlConfiguration) loadedConfig.get(1);
			this.playersFile = (File) loadedConfig.get(0);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void savePlayersConfig() {
		if (this.playersConfig == null || this.playersFile == null) return;
		try {
			this.playersConfig.save(this.playersFile);
		} catch (Exception ignored) {
		}
	}

	public void reloadConfigs() {
		this.reloadConfig();
		this.reloadCommandsConfig();
		this.reloadPlayersConfig();
	}

	private static List<Object> loadConfigSafely(File configFile) {
		try {
			return new ArrayList<>(Arrays.asList(configFile, customLoadConfiguration(configFile)));
		} catch (Exception ex) {
			if (ex.getClass() != FileNotFoundException.class)
				Bukkit.getServer().getLogger().log(Level.SEVERE, "Cannot load " + configFile, ex);
			if (configFile.exists()) {
				String filePath = configFile.getAbsolutePath();
				String brokenFilePath = filePath.substring(0, filePath.indexOf(".yml")) + "-" + System.currentTimeMillis() + ".yml.broken";
				File configDestination = new File(brokenFilePath);
				try {
					FileInputStream configFileInputStream = new FileInputStream(configFile);
					Files.copy(configFileInputStream, configDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
					configFileInputStream.close();
					configFile.delete();
				} catch (Exception ignored) {
				}
			}
			try {
				configFile.createNewFile();
				return new ArrayList<>(Arrays.asList(configFile, customLoadConfiguration(configFile)));
			} catch (Exception ignored) {
			}
		}
		File tempFile = new File(FakeAdmin.getInstance().getDataFolder(), "temp" + System.currentTimeMillis() + ".yml");
		return new ArrayList<>(Arrays.asList(tempFile, YamlConfiguration.loadConfiguration(tempFile)));
	}

	private static YamlConfiguration customLoadConfiguration(File file) throws Exception {
		Validate.notNull(file, "File cannot be null");
		YamlConfiguration config = new YamlConfiguration();
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (Exception ignored) {
		}
		config.load(file);
		return config;
	}

}
