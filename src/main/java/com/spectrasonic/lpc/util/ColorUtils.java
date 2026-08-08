package com.spectrasonic.lpc.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class ColorUtils {
	private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
	private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
	private static final LegacyComponentSerializer LEGACY_AMPERSAND = LegacyComponentSerializer.legacyAmpersand();
	private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
	private static final Pattern BUKKIT_HEX_PATTERN = Pattern.compile("&x(&[A-Fa-f0-9]){6}");
	private static final Pattern MINIMESSAGE_PATTERN = Pattern.compile("<[a-zA-Z#][^>]*>");
	private static final Pattern LEGACY_PATTERN = Pattern.compile("&[0-9a-fA-Fk-orK-OR]");
	private static final Map<Character, String> LEGACY_TO_MINIMESSAGE = new HashMap<>();

	private ColorUtils() {
	}

	public static Component deserialize(String text) {
		if (text != null && !text.isEmpty()) {
			if (containsMiniMessage(text)) {
				return MINI_MESSAGE.deserialize(text);
			} else if (!containsHex(text) && !containsLegacy(text)) {
				return Component.text(text);
			} else {
				String processed = translateHexColorCodes(text);
				return LEGACY_AMPERSAND.deserialize(processed);
			}
		} else {
			return Component.empty();
		}
	}

	public static String serializeToLegacy(Component component) {
		return LEGACY_SERIALIZER.serialize(component);
	}

	public static String serializeToMiniMessage(Component component) {
		return MINI_MESSAGE.serialize(component);
	}

	public static Component legacyToMiniMessage(String legacy) {
		if (legacy != null && !legacy.isEmpty()) {
			return LEGACY_AMPERSAND.deserialize(translateHexColorCodes(legacy));
		} else {
			return Component.empty();
		}
	}

	public static Component miniMessageToLegacy(String miniMessage) {
		return miniMessage != null && !miniMessage.isEmpty() ? MINI_MESSAGE.deserialize(miniMessage)
				: Component.empty();
	}

	public static String colorize(String message) {
		if (message != null && !message.isEmpty()) {
			StringBuilder result = new StringBuilder();

			for (int i = 0; i < message.length(); ++i) {
				char c = message.charAt(i);
				if (c == '&' && i + 1 < message.length()) {
					char next = message.charAt(i + 1);
					String miniMessage = LEGACY_TO_MINIMESSAGE.get(Character.toLowerCase(next));
					if (miniMessage != null) {
						result.append(miniMessage);
						++i;
					} else {
						result.append(c);
					}
				} else {
					result.append(c);
				}
			}

			return result.toString();
		} else {
			return message;
		}
	}

	public static String translateHexColorCodes(String message) {
		if (message != null && !message.isEmpty()) {
			Matcher hexMatcher = HEX_PATTERN.matcher(message);
			StringBuffer buffer = new StringBuffer(message.length());

			while (hexMatcher.find()) {
				String hex = hexMatcher.group(1);
				hexMatcher.appendReplacement(buffer, "<#" + hex + ">");
			}

			String result = hexMatcher.appendTail(buffer).toString();
			Matcher bukkitHexMatcher = BUKKIT_HEX_PATTERN.matcher(result);
			buffer = new StringBuffer(result.length());

			while (bukkitHexMatcher.find()) {
				String hex = bukkitHexMatcher.group().replace("&", "");
				bukkitHexMatcher.appendReplacement(buffer, "<#" + hex.substring(1) + ">");
			}

			result = bukkitHexMatcher.appendTail(buffer).toString();
			return result;
		} else {
			return message;
		}
	}

	public static String stripColorCodes(String message) {
		return message != null && !message.isEmpty() ? message.replaceAll("&[0-9a-fA-Fk-oK-OrR]", "") : message;
	}

	public static String stripHexCodes(String message) {
		if (message != null && !message.isEmpty()) {
			String result = message.replaceAll("&#[0-9a-fA-F]{6}", "");
			result = result.replaceAll("&x(&[0-9a-fA-F]){6}", "");
			return result;
		} else {
			return message;
		}
	}

	public static String stripMiniMessageTags(String message) {
		return message != null && !message.isEmpty() ? message.replaceAll("<[a-zA-Z#][^>]*>", "") : message;
	}

	public static boolean containsMiniMessage(String text) {
		return text != null && MINIMESSAGE_PATTERN.matcher(text).find();
	}

	public static boolean containsHex(String text) {
		return text != null && (HEX_PATTERN.matcher(text).find() || BUKKIT_HEX_PATTERN.matcher(text).find());
	}

	public static boolean containsLegacy(String text) {
		return text != null && LEGACY_PATTERN.matcher(text).find();
	}

	public static boolean containsAnyColor(String text) {
		return containsMiniMessage(text) || containsHex(text) || containsLegacy(text);
	}

	public static Component hexToComponent(String hex) {
		if (hex != null && !hex.isEmpty()) {
			TextColor color = TextColor.fromHexString(hex.startsWith("#") ? hex : "#" + hex);
			return Component.text("").color(color);
		} else {
			return Component.empty();
		}
	}

	public static Component namedColorToComponent(String colorName) {
		if (colorName != null && !colorName.isEmpty()) {
			NamedTextColor color = NamedTextColor.NAMES.value(colorName.toLowerCase());
			return color != null ? Component.text("").color(color) : Component.text("");
		} else {
			return Component.empty();
		}
	}

	static {
		LEGACY_TO_MINIMESSAGE.put('0', "<black>");
		LEGACY_TO_MINIMESSAGE.put('1', "<dark_blue>");
		LEGACY_TO_MINIMESSAGE.put('2', "<dark_green>");
		LEGACY_TO_MINIMESSAGE.put('3', "<dark_aqua>");
		LEGACY_TO_MINIMESSAGE.put('4', "<dark_red>");
		LEGACY_TO_MINIMESSAGE.put('5', "<dark_purple>");
		LEGACY_TO_MINIMESSAGE.put('6', "<gold>");
		LEGACY_TO_MINIMESSAGE.put('7', "<gray>");
		LEGACY_TO_MINIMESSAGE.put('8', "<dark_gray>");
		LEGACY_TO_MINIMESSAGE.put('9', "<blue>");
		LEGACY_TO_MINIMESSAGE.put('a', "<green>");
		LEGACY_TO_MINIMESSAGE.put('b', "<aqua>");
		LEGACY_TO_MINIMESSAGE.put('c', "<red>");
		LEGACY_TO_MINIMESSAGE.put('d', "<light_purple>");
		LEGACY_TO_MINIMESSAGE.put('e', "<yellow>");
		LEGACY_TO_MINIMESSAGE.put('f', "<white>");
		LEGACY_TO_MINIMESSAGE.put('k', "<obfuscated>");
		LEGACY_TO_MINIMESSAGE.put('l', "<bold>");
		LEGACY_TO_MINIMESSAGE.put('m', "<strikethrough>");
		LEGACY_TO_MINIMESSAGE.put('n', "<underline>");
		LEGACY_TO_MINIMESSAGE.put('o', "<italic>");
		LEGACY_TO_MINIMESSAGE.put('r', "<reset>");
	}
}