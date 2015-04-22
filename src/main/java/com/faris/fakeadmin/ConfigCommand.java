package com.faris.fakeadmin;

import com.faris.fakeadmin.helper.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ConfigCommand {
	BAN(Arrays.asList("Bans a player.", "/<usage>"),
			new SpecialAttribute("Ban message", new ArrayList<>(Arrays.asList("&6Player &4<banner> &6banned &c<banned> &6for: &c<reason>&6."))),
			new SpecialAttribute("Exempt player", "&cError: &4You cannot ban that player.")),
	TEMPBAN(Arrays.asList("Temporary ban a user", "/<usage>"),
			new SpecialAttribute("Tempban message", new ArrayList<>(Arrays.asList("&6Player &c<banner> &6temporarily banned &c<banned> &6for &c<duration>&6: &c<reason>&6."))),
			new SpecialAttribute("Exempt player", "&4You may not tempban that player.")),
	UNBAN(Arrays.asList("Unbans the specified player.", "/<usage>"),
			new SpecialAttribute("Player not found", "&cError: &4Player not found."),
			new SpecialAttribute("Unban message", new ArrayList<>(Arrays.asList("&6Player &4<banner> &6unbanned &c<banned>.")))),
	KICK(Arrays.asList("Kicks a specified player with a reason.", "/<usage>"),
			new SpecialAttribute("Kick message", new ArrayList<>(Arrays.asList("&6Player &4<kicker> &6kicked <kicked> for <reason>."))),
			new SpecialAttribute("Exempt player", "&cError: &4You cannot kick that person."),
			new SpecialAttribute("Player not found", "&cError: &4Player not found.")),
	MUTE(Arrays.asList("Mutes or unmutes a player.", "/<usage>"),
			new SpecialAttribute("Mute message", new ArrayList<>(Arrays.asList("&c<muter> &6has muted player &c<muted>&6."))),
			new SpecialAttribute("Mute private message", new ArrayList<>(Arrays.asList("&6Player &c<muted> &6muted."))),
			new SpecialAttribute("Exempt player", "&cError: &4You may not mute that player."),
			new SpecialAttribute("Illegal date format", "&cError: &4Illegal date format."),
			new SpecialAttribute("Tempmute private message", new ArrayList<>(Arrays.asList("&6Player &c<muted> &6muted for &c<duration>&6."))),
			new SpecialAttribute("Unmute private message", new ArrayList<>(Arrays.asList("&6Player &c<muted> &6unmuted.")))),
	NICK(Arrays.asList("Change your nickname or that of another player.", "/<usage>"),
			new SpecialAttribute("Invalid nickname", "&cError: &4Nicknames must be alphanumeric."),
			new SpecialAttribute("Max nickname length", 15),
			new SpecialAttribute("Nick in use  message", "&4That name is already in use."),
			new SpecialAttribute("Nick message", "&6Your nickname is now &4<nick>&6."),
			new SpecialAttribute("Nick off message", "&6You no longer have a nickname."),
			new SpecialAttribute("Nick other message", "&6Nickname changed."),
			new SpecialAttribute("Nick too long message", "&cError: &4That nickname is too long."),
			new SpecialAttribute("Player not found", "&cError: &4Player not found."));

	private boolean enabled = true;
	private String configKey = null;
	private List<String> usageMessages = Collections.singletonList("&cUsage: &4/<usage>");
	private List<SpecialAttribute> specialAttributes = new ArrayList<>();

	private ConfigCommand(List<String> usageMessages, SpecialAttribute... specialAttributes) {
		this.usageMessages = usageMessages;
		if (specialAttributes != null && specialAttributes.length > 0)
			this.specialAttributes = new ArrayList<>(Arrays.asList(specialAttributes));
	}

	private ConfigCommand(String configKey, String usageMessage) {
		this.configKey = configKey;
		this.usageMessages = new ArrayList<>(Collections.singletonList(usageMessage));
	}

	private ConfigCommand(boolean enabled, List<String> usageMessages, SpecialAttribute... specialAttributes) {
		this.enabled = enabled;
		this.usageMessages = usageMessages;
		if (specialAttributes != null && specialAttributes.length > 0)
			this.specialAttributes = new ArrayList<>(Arrays.asList(specialAttributes));
	}

	private ConfigCommand(boolean enabled, String configKey, String usageMessage) {
		this.enabled = enabled;
		this.configKey = configKey;
		this.usageMessages = new ArrayList<>(Collections.singletonList(usageMessage));
	}

	private ConfigCommand(boolean enabled, String configKey, List<String> usageMessages, SpecialAttribute... specialAttributes) {
		this.enabled = enabled;
		this.configKey = configKey;
		this.usageMessages = usageMessages;
		if (specialAttributes != null && specialAttributes.length > 0)
			this.specialAttributes = new ArrayList<>(Arrays.asList(specialAttributes));
	}

	public void addSpecialAttribute(String key, Object value) {
		if (key != null && !key.trim().isEmpty()) {
			SpecialAttribute existingAttribute = this.getSpecialAttribute(key);
			if (existingAttribute != null) this.specialAttributes.remove(existingAttribute);
			this.specialAttributes.add(new SpecialAttribute(key, value));
		}
	}

	public void clearSpecialAttributes() {
		try {
			this.specialAttributes.clear();
		} catch (Exception ex) {
			this.specialAttributes = new ArrayList<>();
		}
	}

	public String getConfigKey() {
		return this.configKey != null ? this.configKey : Utilities.capitalise(this.name());
	}

	/**
	 * Get the usage messages.
	 *
	 * @return The usage messages.
	 */
	public List<String> getUsageMessages() {
		return this.usageMessages != null ? this.usageMessages : new ArrayList<>(Collections.singletonList("/<usage>"));
	}

	/**
	 * Get the usage messages, replacing the '<usage>' with the 'usage' parameter.
	 *
	 * @param usage - The usage
	 * @return The usage messages.
	 */
	public List<String> getUsageMessage(String usage) {
		List<String> usageMsgs = this.getUsageMessages();
		for (int i = 0; i < usageMsgs.size(); i++) {
			usageMsgs.set(i, usageMsgs.get(i).replace("<usage>", usage));
		}
		return usageMsgs;
	}

	public SpecialAttribute getSpecialAttribute(String key) {
		for (SpecialAttribute specialAttribute : this.specialAttributes) {
			if (specialAttribute.getKey().equals(key)) return specialAttribute;
		}
		return null;
	}

	public List<SpecialAttribute> getSpecialAttributes() {
		return Collections.unmodifiableList(this.specialAttributes);
	}

	public boolean hasSpecialAttribute(String key) {
		for (SpecialAttribute specialAttribute : this.specialAttributes) {
			if (specialAttribute.getKey().equals(key) && specialAttribute.hasValue()) {
				if (!(specialAttribute.getValue() instanceof String && ((String) specialAttribute.getValue()).isEmpty())) {
					if (!(specialAttribute.getValue() instanceof List && ((List) specialAttribute.getValue()).isEmpty()))
						return true;
				}
				return false;
			}
		}
		return false;
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void setUsageMessages(String usageMessage) {
		this.usageMessages = new ArrayList<>(Collections.singletonList(usageMessage));
	}

	public void setUsageMessages(List<String> usageMessages) {
		this.usageMessages = new ArrayList<>(usageMessages);
	}

	public static class SpecialAttribute {
		private String key = null;
		private Object value = null;

		public SpecialAttribute(String key, Object value) {
			this.key = key != null ? key : "";
			this.value = value;
		}

		public <T> T castValue(Class<? extends T> unusedClass) {
			return (T) this.value;
		}

		public String getKey() {
			return this.key;
		}

		public Object getValue() {
			return this.value;
		}

		public boolean hasValue() {
			return this.value != null;
		}
	}
}
