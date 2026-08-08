package com.spectrasonic.lpc.managers;

import com.spectrasonic.lpc.Main;
import com.spectrasonic.lpc.util.ColorUtils;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public final class ChatManager {

    private final Main plugin;
    private final LuckPermsManager luckPermsManager;

    public String buildFormat(Player player) {
        String group = luckPermsManager.getPrimaryGroup(player);
        String format = plugin.getConfig().getString(
                plugin.getConfig().getString("group-formats." + group) != null ? "group-formats." + group
                        : "chat-format");
        if (format == null) {
            format = "{prefix}{name}&r: {message}";
        }

        String prefix = luckPermsManager.getPrefix(player);
        String suffix = luckPermsManager.getSuffix(player);
        String usernameColor = luckPermsManager.getMetaValue(player, "username-color");
        String messageColor = luckPermsManager.getMetaValue(player, "message-color");

        String allPrefixes = luckPermsManager.getPrefixes(player).keySet().stream()
                .map(key -> luckPermsManager.getPrefixes(player).get(key))
                .collect(Collectors.joining());
        String allSuffixes = luckPermsManager.getSuffixes(player).keySet().stream()
                .map(key -> luckPermsManager.getSuffixes(player).get(key))
                .collect(Collectors.joining());

        format = format.replace("{prefix}", prefix != null ? prefix : "")
                .replace("{suffix}", suffix != null ? suffix : "")
                .replace("{prefixes}", allPrefixes)
                .replace("{suffixes}", allSuffixes)
                .replace("{world}", player.getWorld().getName())
                .replace("{name}", player.getName())
                .replace("{displayname}", ColorUtils.serializeToLegacy(player.displayName()))
                .replace("{username-color}", usernameColor != null ? usernameColor : "")
                .replace("{message-color}", messageColor != null ? messageColor : "");

        format = ColorUtils.translateHexColorCodes(format);
        if (plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            format = PlaceholderAPI.setPlaceholders(player, format);
        }

        return ColorUtils.colorize(ColorUtils.translateHexColorCodes(format));
    }

    public String processMessage(Player player, String message) {
        boolean hasMiniMessage = player.hasPermission("lpc.minimessage");
        boolean hasColorCodes = player.hasPermission("lpc.colorcodes");
        boolean hasRgbCodes = player.hasPermission("lpc.rgbcodes");
        if (hasMiniMessage && ColorUtils.containsMiniMessage(message)) {
            return message;
        } else if (hasColorCodes && hasRgbCodes) {
            return ColorUtils.colorize(ColorUtils.translateHexColorCodes(message));
        } else if (hasColorCodes) {
            return ColorUtils.colorize(ColorUtils.stripHexCodes(message));
        } else {
            return hasRgbCodes ? ColorUtils.stripColorCodes(ColorUtils.translateHexColorCodes(message))
                    : ColorUtils.stripColorCodes(ColorUtils.stripHexCodes(message));
        }
    }

    public String colorize(String message) {
        return ColorUtils.colorize(message);
    }

    public String translateHexColorCodes(String message) {
        return ColorUtils.translateHexColorCodes(message);
    }

    public String stripColorCodes(String message) {
        return ColorUtils.stripColorCodes(message);
    }

    public String stripHexCodes(String message) {
        return ColorUtils.stripHexCodes(message);
    }

    public Component deserializeMessage(String message) {
        return ColorUtils.deserialize(message);
    }

    public String serializeToLegacy(Component component) {
        return ColorUtils.serializeToLegacy(component);
    }

    public String serializeToMiniMessage(Component component) {
        return ColorUtils.serializeToMiniMessage(component);
    }
}
