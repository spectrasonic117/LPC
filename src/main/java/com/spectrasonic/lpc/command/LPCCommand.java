package com.spectrasonic.lpc.command;

import com.spectrasonic.lpc.Main;
import com.spectrasonic.lpc.managers.LuckPermsManager;
import com.spectrasonic.lpc.util.ColorUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class LPCCommand {

    private final Main plugin;
    private final LuckPermsManager luckPermsManager;

    public void register() {
        new CommandAPICommand("lpc")
                .withPermission(CommandPermission.OP)
                .withSubcommands(
                        new CommandAPICommand("reload")
                                .executes((CommandSender sender,
                                        CommandArguments args) -> handleReload(
                                                sender)),
                        new CommandAPICommand("clear")
                                .executes((CommandSender sender,
                                        CommandArguments args) -> handleClearChat()),
                        new CommandAPICommand("debug")
                                .withArguments(new EntitySelectorArgument.OnePlayer(
                                        "target"))
                                .executes((CommandSender sender,
                                        CommandArguments args) -> handleDebug(
                                                sender,
                                                (Player) args.get(
                                                        "target"))))
                .register(plugin);
    }

    private void handleReload(CommandSender sender) {
        plugin.reloadConfig();
        sender.sendMessage(ColorUtils.colorize("&aLPC has been reloaded."));
    }

    private void handleClearChat() {
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            for (int i = 0; i < 100; i++) {
                player.sendMessage("");
            }
        });

        String clearMessage = plugin.getConfig().getString("clear-chat-message",
                "&7Chat has been cleared by a staff member.");
        Component message = LegacyComponentSerializer.legacySection()
                .deserialize(ColorUtils.colorize(clearMessage));
        plugin.getServer().broadcast(message);
    }

    private void handleDebug(CommandSender sender, Player target) {
        CachedMetaData debugMeta = luckPermsManager.getPlayerMetaData(target);
        sender.sendMessage(ColorUtils.colorize("&6&lLPC Debug: &f" + target.getName()));
        sender.sendMessage(ColorUtils.colorize("&7Primary Group: &f" + debugMeta.getPrimaryGroup()));
        sender.sendMessage(ColorUtils.colorize(
                "&7Prefix: &f" + (debugMeta.getPrefix() != null ? debugMeta.getPrefix() : "&cnone")));
        sender.sendMessage(ColorUtils.colorize(
                "&7Suffix: &f" + (debugMeta.getSuffix() != null ? debugMeta.getSuffix() : "&cnone")));

        sender.sendMessage(ColorUtils.colorize("&7All Prefixes (by weight):"));
        debugMeta.getPrefixes().forEach((weight, prefix) -> sender
                .sendMessage(ColorUtils.colorize("  &7[" + weight + "] &f" + prefix)));

        sender.sendMessage(ColorUtils.colorize("&7All Suffixes (by weight):"));
        debugMeta.getSuffixes().forEach((weight, suffix) -> sender
                .sendMessage(ColorUtils.colorize("  &7[" + weight + "] &f" + suffix)));

        String usernameColor = debugMeta.getMetaValue("username-color");
        String messageColor = debugMeta.getMetaValue("message-color");
        sender.sendMessage(ColorUtils.colorize(
                "&7Username-color: &f" + (usernameColor != null ? usernameColor : "&cnone")));
        sender.sendMessage(ColorUtils.colorize(
                "&7Message-color: &f" + (messageColor != null ? messageColor : "&cnone")));

        String groupFormat = plugin.getConfig()
                .getString("group-formats." + debugMeta.getPrimaryGroup()) != null
                        ? "group-formats." + debugMeta.getPrimaryGroup()
                        : "chat-format (default)";
        sender.sendMessage(ColorUtils.colorize("&7Group format: &f" + groupFormat));

        boolean hasPapi = plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        sender.sendMessage(ColorUtils.colorize("&7PAPI: &f" + (hasPapi ? "&ahooked" : "&cnot found")));
        sender.sendMessage(ColorUtils
                .colorize("&7Has lpc.colorcodes: &f" + target.hasPermission("lpc.colorcodes")));
        sender.sendMessage(
                ColorUtils.colorize("&7Has lpc.rgbcodes: &f" + target.hasPermission("lpc.rgbcodes")));
        sender.sendMessage(ColorUtils
                .colorize("&7Has lpc.minimessage: &f" + target.hasPermission("lpc.minimessage")));
    }
}
