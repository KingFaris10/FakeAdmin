package com.faris.fakeadmin.helper;

import com.faris.fakeadmin.api.FakeAdminAPI;
import com.faris.fakeadmin.helper.custom.CustomMap;
import org.bukkit.*;
import org.bukkit.configuration.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilities {

	private static final Pattern alphanumericPattern = Pattern.compile("[^a-zA-Z0-9&]");
	private static final Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?" + "(?:([0-9]+)\\s*(?:s[a-z]*)?)?", Pattern.CASE_INSENSITIVE);
	private static final Random random = new Random();

	public static String capitalise(String aString) {
		return aString != null ? (aString.length() > 1 ? aString.substring(0, 1).toUpperCase() + aString.substring(1).toLowerCase() : aString.toUpperCase()) : "";
	}

	public static CustomMap<String, Object> getMap(Object objMap) {
		return objMap instanceof Map ? new CustomMap<>((Map<String, Object>) objMap) : (objMap instanceof ConfigurationSection ? new CustomMap<>(((ConfigurationSection) objMap).getValues(false)) : new CustomMap<String, Object>());
	}

	public static OfflinePlayer getOfflinePlayer(String playerName) {
		List<OfflinePlayer> offlinePlayers = getOfflinePlayers();
		for (OfflinePlayer offlinePlayer : offlinePlayers) {
			if (offlinePlayer.getName().equalsIgnoreCase(playerName)) return offlinePlayer;
		}
		return null;
	}

	public static List<OfflinePlayer> getOfflinePlayers() {
		List<OfflinePlayer> offlinePlayers = new ArrayList<>();
		for (OfflinePlayer offlinePlayer : Bukkit.getServer().getOfflinePlayers()) {
			if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) offlinePlayers.add(offlinePlayer);
		}
		return offlinePlayers;
	}

	public static List<Player> getOnlinePlayers() {
		List<Player> onlinePlayers = new ArrayList<>();
		for (World world : Bukkit.getServer().getWorlds()) {
			if (world != null) onlinePlayers.addAll(world.getPlayers());
		}
		return onlinePlayers;
	}

	public static Plugin getPlugin(String pluginName) {
		return Bukkit.getServer().getPluginManager().getPlugin(pluginName);
	}

	public static Random getRandom() {
		return random;
	}

	public static boolean isAlphabetical(String aString) {
		if (aString != null) {
			char[] stringChars = aString.toCharArray();
			for (char stringChar : stringChars) {
				if (!Character.isLetter(stringChar)) return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public static boolean isAlphanumerical(String aString) {
		return aString != null && !aString.replace(" ", "").isEmpty() && !alphanumericPattern.matcher(aString).find();
	}

	public static boolean isByte(Object anObject) {
		try {
			if (anObject != null) {
				Byte.parseByte(anObject.toString());
				return true;
			}
		} catch (Exception ex) {
		}
		return false;
	}

	public static boolean isDouble(Object anObject) {
		try {
			if (anObject != null) {
				Double.parseDouble(anObject.toString());
				return true;
			}
		} catch (Exception ex) {
		}
		return false;
	}

	public static boolean isInteger(Object anObject) {
		try {
			if (anObject != null) {
				Integer.parseInt(anObject.toString());
				return true;
			}
		} catch (Exception ex) {
		}
		return false;
	}

	public static boolean isLong(Object anObject) {
		try {
			if (anObject != null) {
				Long.parseLong(anObject.toString());
				return true;
			}
		} catch (Exception ex) {
		}
		return false;
	}

	public static boolean isPluginEnabled(String pluginName) {
		return Bukkit.getServer().getPluginManager().isPluginEnabled(pluginName);
	}

	public static boolean isTool(Material material) {
		String materialName = material != null ? material.name().toLowerCase() : "";
		return materialName.endsWith("_axe") || materialName.endsWith("_hoe") || materialName.equals(Material.FLINT_AND_STEEL.name().toLowerCase()) || materialName.endsWith("_pickaxe") || materialName.endsWith("_spade") || materialName.endsWith("_sword");
	}

	public static boolean isUUID(String aString) {
		try {
			UUID.fromString(aString);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

	private static String replaceChat(String text, String format) {
		if (text != null) {
			char[] b = text.toCharArray();
			for (int i = 0; i < b.length - 1; ++i) {
				if (b[i] == '&' && format.indexOf(b[i + 1]) > -1) {
					b[i] = 167;
					b[i + 1] = Character.toLowerCase(b[i + 1]);
				}
			}
			return new String(b);
		}
		return text != null ? text : "";
	}

	public static String replaceChatColours(String text) {
		return replaceChat(text, "0123456789AaBbCcDdEeFf");
	}

	public static String replaceChatFormats(String text) {
		return replaceChat(text, "LlMmNnOoRr");
	}

	public static String replaceChatMagic(String text) {
		return replaceChat(text, "Kk");
	}

	public static String replaceChatColoursAndFormats(String text) {
		return replaceChat(text, "0123456789AaBbCcDdEeFfKkLlMmNnOoRr");
	}

	public static void showPlayer(UUID playerUUID, UUID hiddenUUID) {
		if (playerUUID != null && hiddenUUID != null) {
			Player player = Bukkit.getServer().getPlayer(playerUUID);
			if (PlayerUtilities.isOnline(player)) {
				Player hidden = Bukkit.getServer().getPlayer(hiddenUUID);
				if (PlayerUtilities.isOnline(hidden)) {
					if (!player.canSee(hidden) && !PlayerUtilities.isVanished(player, hidden))
						player.showPlayer(hidden);
				}
			}
		}
	}

	public static String toString(String[] strings) {
		StringBuilder stringBuilder = new StringBuilder();
		if (strings != null) {
			for (int i = 0; i < strings.length; i++) {
				if (i == strings.length - 1) stringBuilder.append(strings[i]);
				else stringBuilder.append(strings[i]).append(" ");
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Credits go to the authors of the DateUtilities class in the Essentials plugin.
	 */
	public static class DateUtilities {
		private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
			int diff = 0;
			long savedDate = fromDate.getTimeInMillis();
			while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate))) {
				savedDate = fromDate.getTimeInMillis();
				fromDate.add(type, future ? 1 : -1);
				diff++;
			}
			diff--;
			fromDate.setTimeInMillis(savedDate);
			return diff;
		}

		public static String formatDateDiff(long date) {
			Calendar dateCalendar = new GregorianCalendar();
			dateCalendar.setTimeInMillis(date);
			Calendar now = new GregorianCalendar();
			return formatDateDiff(now, dateCalendar);
		}

		private static String formatDateDiff(Calendar fromDate, Calendar toDate) {
			if (toDate.equals(fromDate)) return Lang.TIME_NOW.getRawMessage();

			boolean future = toDate.after(fromDate);
			StringBuilder sb = new StringBuilder();
			int[] types = new int[]{Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND};
			String[] names = new String[]
					{
							Lang.TIME_YEAR.getRawMessage(), Lang.TIME_YEARS.getRawMessage(),
							Lang.TIME_MONTH.getRawMessage(), Lang.TIME_MONTHS.getRawMessage(),
							Lang.TIME_DAY.getRawMessage(), Lang.TIME_DAYS.getRawMessage(),
							Lang.TIME_HOUR.getRawMessage(), Lang.TIME_HOURS.getRawMessage(),
							Lang.TIME_MINUTE.getRawMessage(), Lang.TIME_MINUTES.getRawMessage(),
							Lang.TIME_SECOND.getRawMessage(), Lang.TIME_SECONDS.getRawMessage()
					};
			int accuracy = 0;

			for (int i = 0; i < types.length; i++) {
				if (accuracy > 2) break;
				int dateDiff = dateDiff(types[i], fromDate, toDate, future);
				if (dateDiff > 0) {
					accuracy++;
					sb.append(" ").append(dateDiff).append(" ").append(names[i * 2 + (dateDiff > 1 ? 1 : 0)]);
				}
			}

			if (sb.length() == 0) return Lang.TIME_NOW.getRawMessage();
			else return sb.toString().trim();
		}

		public static long getTimeAsMilliSeconds(String strTime) {
			if (strTime != null) {
				if (!isLong(strTime)) {
					Matcher m = timePattern.matcher(strTime);
					int years = 0, months = 0, weeks = 0, days = 0, hours = 0, minutes = 0, seconds = 0;
					boolean found = false;
					while (m.find()) {
						if (m.group() == null || m.group().isEmpty()) continue;
						for (int i = 0; i < m.groupCount(); i++) {
							if (m.group(i) != null && !m.group(i).isEmpty()) {
								found = true;
								break;
							}
						}
						if (found) {
							if (m.group(1) != null && !m.group(1).isEmpty()) years = Integer.parseInt(m.group(1));
							if (m.group(2) != null && !m.group(2).isEmpty()) months = Integer.parseInt(m.group(2));
							if (m.group(3) != null && !m.group(3).isEmpty()) weeks = Integer.parseInt(m.group(3));
							if (m.group(4) != null && !m.group(4).isEmpty()) days = Integer.parseInt(m.group(4));
							if (m.group(5) != null && !m.group(5).isEmpty()) hours = Integer.parseInt(m.group(5));
							if (m.group(6) != null && !m.group(6).isEmpty()) minutes = Integer.parseInt(m.group(6));
							if (m.group(7) != null && !m.group(7).isEmpty()) seconds = Integer.parseInt(m.group(7));
							break;
						}
					}
					if (found) {
						Calendar calendar = new GregorianCalendar();
						calendar.setTimeInMillis(0L);
						if (years > 0) calendar.add(Calendar.YEAR, years);
						if (months > 0) calendar.add(Calendar.MONTH, months);
						if (weeks > 0) calendar.add(Calendar.WEEK_OF_YEAR, weeks);
						if (days > 0) calendar.add(Calendar.DAY_OF_MONTH, days);
						if (hours > 0) calendar.add(Calendar.HOUR_OF_DAY, hours);
						if (minutes > 0) calendar.add(Calendar.MINUTE, minutes);
						if (seconds > 0) calendar.add(Calendar.SECOND, seconds);
						Calendar max = new GregorianCalendar();
						max.add(Calendar.YEAR, 10);
						if (calendar.after(max)) return max.getTimeInMillis();
						return calendar.getTimeInMillis();
					}
				} else {
					long parsedTime = Long.parseLong(strTime);
					return parsedTime >= 0L ? parsedTime : -1000L;
				}
			}
			return -1000L;
		}
	}

	public static class PlayerUtilities {
		public static boolean isMuted(Player player) {
			return FakeAdminAPI.getMuteHandler() != null ? (Boolean) FakeAdminAPI.getMuteHandler().getValue(player) : false;
		}

		public static boolean isOnline(Player player) {
			return player != null && player.isOnline();
		}

		public static boolean isVanished(Player viewer, Player player) {
			return FakeAdminAPI.getVanishHandler() != null ? (Boolean) FakeAdminAPI.getVanishHandler().getValue(viewer, player) : false;
		}
	}

}
