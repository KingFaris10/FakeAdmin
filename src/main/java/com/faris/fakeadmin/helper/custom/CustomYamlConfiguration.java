package com.faris.fakeadmin.helper.custom;

import org.apache.commons.lang.Validate;
import org.bukkit.configuration.file.*;

import java.io.File;

public class CustomYamlConfiguration extends YamlConfiguration {

	private File configFile = null;

	public CustomYamlConfiguration(File configFile) {
		this.configFile = configFile;
	}

	public File getFile() {
		return this.configFile;
	}

	public void save() {
		try {
			this.save(this.getFile());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void setFile(File file) {
		this.configFile = file;
	}

	public static CustomYamlConfiguration loadConfiguration(File configFile) {
		Validate.notNull(configFile, "File cannot be null");
		CustomYamlConfiguration config = new CustomYamlConfiguration(configFile);
		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
			}
			config.load(configFile);
		} catch (Exception ex) {
		}
		return config;
	}

}
