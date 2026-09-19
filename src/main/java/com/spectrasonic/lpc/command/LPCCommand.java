package com.spectrasonic.lpc.command;

import com.spectrasonic.lpc.Main;
import com.spectrasonic.lpc.managers.LuckPermsManager;
import com.spectrasonic.lpc.managers.MessageManager;
import com.spectrasonic.lpc.util.ColorUtils;
import com.spectrasonic.lpc.util.MessageUtils;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
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
        // La recarga pasa por ConfigManager para refrescar config.yml y messages.yml
        plugin.getConfigManager().reload();
        MessageUtils.configReloadedMessage(sender);
    }

    private void handleClearChat() {
        // Copia tipada: evita el capture de "? extends Player" que Eclipse marca como
        // posiblemente nulo
        List<Player> onlinePlayers = new ArrayList<>(plugin.getServer().getOnlinePlayers());
        onlinePlayers.forEach(this::clearPlayerChat);

        MessageUtils.broadcastMessage(MessageManager.getMessage("messages.chat.cleared"));
    }

    private void clearPlayerChat(Player player) {
        // Líneas vacías para desplazar el chat visible del cliente
        for (int i = 0; i < 100; i++) {
            player.sendMessage("");
        }
    }

    private void handleDebug(CommandSender sender, Player target) {
        CachedMetaData debugMeta = luckPermsManager.getPlayerMetaData(target);

        MessageUtils.sendComponent(sender,
                MessageManager.component("messages.debug.header", "player", target.getName()));
        MessageUtils.sendComponent(sender, MessageManager.component("messages.debug.primary_group",
                "group", String.valueOf(debugMeta.getPrimaryGroup())));

        sendLegacyValue(sender, "messages.debug.prefix", debugMeta.getPrefix());
        sendLegacyValue(sender, "messages.debug.suffix", debugMeta.getSuffix());

        MessageUtils.sendComponent(sender, MessageManager.component("messages.debug.prefixes_header"));
        debugMeta.getPrefixes().forEach((weight, prefix) -> sendWeightedValue(sender, weight, prefix));

        MessageUtils.sendComponent(sender, MessageManager.component("messages.debug.suffixes_header"));
        debugMeta.getSuffixes().forEach((weight, suffix) -> sendWeightedValue(sender, weight, suffix));

        sendMetaValue(sender, "messages.debug.username_color", debugMeta.getMetaValue("username-color"));
        sendMetaValue(sender, "messages.debug.message_color", debugMeta.getMetaValue("message-color"));
        sendGroupFormat(sender, debugMeta.getPrimaryGroup());

        sendPapiStatus(sender);
        sendPermissionStatus(sender, "lpc.colorcodes", target);
        sendPermissionStatus(sender, "lpc.rgbcodes", target);
        sendPermissionStatus(sender, "lpc.minimessage", target);
    }

    // Etiqueta desde messages.yml + valor legacy/hex de LuckPerms resuelto por ColorUtils
    private void sendLegacyValue(CommandSender sender, String key, String value) {
        Component label = MessageManager.component(key);
        Component resolved = value != null ? ColorUtils.deserialize(value)
                : MessageManager.component("messages.debug.none");
        MessageUtils.sendComponent(sender, label.append(resolved));
    }

    // Entrada con peso para la lista de prefixes/suffixes
    private void sendWeightedValue(CommandSender sender, Integer weight, String value) {
        Component entry = MessageManager.component("messages.debug.weight_entry",
                "weight", String.valueOf(weight));
        MessageUtils.sendComponent(sender, entry.append(ColorUtils.deserialize(value)));
    }

    // Valores de meta que se muestran crudos para diagnóstico
    private void sendMetaValue(CommandSender sender, String key, String value) {
        String resolved = value != null ? value : MessageManager.getMessage("messages.debug.none");
        MessageUtils.sendComponent(sender, MessageManager.component(key, "value", resolved));
    }

    // Muestra qué clave de formato aplica al grupo del jugador
    private void sendGroupFormat(CommandSender sender, String primaryGroup) {
        String groupKey = "group-formats." + primaryGroup;
        String formatKey = plugin.getConfig().getString(groupKey) != null ? groupKey
                : "chat-format (default)";
        MessageUtils.sendComponent(sender,
                MessageManager.component("messages.debug.group_format", "format", formatKey));
    }

    // Estado de conexión con PlaceholderAPI
    private void sendPapiStatus(CommandSender sender) {
        boolean hasPapi = plugin.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
        Component label = MessageManager.component("messages.debug.papi");
        Component status = MessageManager
                .component(hasPapi ? "messages.debug.papi_hooked" : "messages.debug.papi_not_found");
        MessageUtils.sendComponent(sender, label.append(status));
    }

    // Estado con color de un permiso del jugador
    private void sendPermissionStatus(CommandSender sender, String permission, Player target) {
        boolean granted = target.hasPermission(permission);
        Component label = MessageManager.component("messages.debug.permission_line",
                "permission", permission);
        Component status = MessageManager
                .component(granted ? "messages.debug.permission_yes" : "messages.debug.permission_no");
        MessageUtils.sendComponent(sender, label.append(status));
    }
}
