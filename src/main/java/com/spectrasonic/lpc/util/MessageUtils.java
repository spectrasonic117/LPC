package com.spectrasonic.lpc.util;

import com.spectrasonic.lpc.managers.MessageManager;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.time.Duration;

// Única clase autorizada para enviar texto visible a jugadores y consola
@UtilityClass
public final class MessageUtils {

    private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(MessageUtils.class);

    public static final String DIVIDER = "<gray>----------------------------------------</gray>";
    public static final String PREFIX = "<gray>[<gold>" + plugin.getPluginMeta().getName()
            + "</gold>]</gray> <gold>»</gold> ";

    public static final String CLOSE_PREFIX = "</#9e9893>";
    public static final String SUCCESS_PREFIX = "<green><b>[✔]</b></green> <#9e9893>";
    public static final String ALERT_PREFIX = "<yellow><b>[!]</b></yellow> <#9e9893>";
    public static final String DENY_PREFIX = "<red><b>[✖]</b></red> <#9e9893>";
    public static final String WARNING_PREFIX = "<red><b>[⚠]</b></red> <#9e9893>";
    public static final String INFO_PREFIX = "<aqua><b>[i]</b></aqua> <#9e9893>";
    public static final String DEBUG_PREFIX = "<blue><b>[d]</b></blue> <#9e9893>";

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    // Mensaje con prefijo estándar del plugin
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(MINI_MESSAGE.deserialize(PREFIX + message));
    }

    // Mensaje sin prefijo, útil para textos ya compuestos por el llamador
    public static void rawMessage(CommandSender sender, String message) {
        sender.sendMessage(MINI_MESSAGE.deserialize(message));
    }

    // Envío de un Component ya construido (labels + valores legacy concatenados)
    public static void sendComponent(CommandSender sender, Component component) {
        sender.sendMessage(component);
    }

    // Mensaje hacia la consola con prefijo del plugin
    public static void sendConsoleMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(MINI_MESSAGE.deserialize(PREFIX + message));
    }

    // Mensajes predefinidos que leen su texto desde messages.yml
    public static void sendPermissionMessage(CommandSender sender) {
        denyMessage(sender, MessageManager.getMessage("messages.permission.no_permission"));
    }

    public static void onlyPlayerMessage(CommandSender sender) {
        denyMessage(sender, MessageManager.getMessage("messages.command.only_player"));
    }

    public static void configReloadedMessage(CommandSender sender) {
        successMessage(sender, MessageManager.getMessage("messages.reload.success"));
    }

    public static void successMessage(CommandSender sender, String message) {
        sender.sendMessage(MINI_MESSAGE.deserialize(SUCCESS_PREFIX + message + CLOSE_PREFIX));
    }

    public static void alertMessage(CommandSender sender, String message) {
        sender.sendMessage(MINI_MESSAGE.deserialize(ALERT_PREFIX + message + CLOSE_PREFIX));
    }

    public static void denyMessage(CommandSender sender, String message) {
        sender.sendMessage(MINI_MESSAGE.deserialize(DENY_PREFIX + message + CLOSE_PREFIX));
    }

    public static void warningMessage(CommandSender sender, String message) {
        sender.sendMessage(MINI_MESSAGE.deserialize(WARNING_PREFIX + message + CLOSE_PREFIX));
    }

    public static void infoMessage(CommandSender sender, String message) {
        sender.sendMessage(MINI_MESSAGE.deserialize(INFO_PREFIX + message + CLOSE_PREFIX));
    }

    public static void debugMessage(CommandSender sender, String message) {
        sender.sendMessage(MINI_MESSAGE.deserialize(DEBUG_PREFIX + message + CLOSE_PREFIX));
    }

    // Mensajes de arranque y apagado hacia la consola
    public static void sendStartupMessage(JavaPlugin plugin) {
        String[] messages = {
                DIVIDER,
                "",
                "<green>██      ██████   ██████</green>",
                "<green>██      ██   ██ ██     </green>",
                "<green>██      ██████  ██     </green>",
                "<green>██      ██      ██     </green>",
                "<green>███████ ██       ██████</green>",
                "",
                "<aqua>Version:</aqua>" + plugin.getPluginMeta().getVersion(),
                "<aqua>Developed by:</aqua> <red>" + plugin.getPluginMeta().getAuthors(),
                "<green>Plugin Enabled!</green>",
                "",
                DIVIDER
        };

        for (String message : messages) {
            Bukkit.getConsoleSender().sendMessage(MINI_MESSAGE.deserialize(message));
        }
    }

    public static void sendShutdownMessage(JavaPlugin plugin) {
        String[] messages = {
                DIVIDER,
                PREFIX + "<red>" + plugin.getPluginMeta().getName() + " plugin Disabled!</red>",
                DIVIDER
        };

        for (String message : messages) {
            Bukkit.getConsoleSender().sendMessage(MINI_MESSAGE.deserialize(message));
        }
    }

    // Broadcast de chat a todos los jugadores conectados
    public static void broadcastMessage(String message) {
        Component component = MINI_MESSAGE.deserialize(message);
        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(component));
    }

    // Título y subtítulo con MiniMessage; tiempos en segundos
    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Component titleComponent = MINI_MESSAGE.deserialize(title);
        Component subtitleComponent = MINI_MESSAGE.deserialize(subtitle);
        player.showTitle(Title.title(titleComponent, subtitleComponent, Title.Times.times(
                Duration.ofSeconds(fadeIn),
                Duration.ofSeconds(stay),
                Duration.ofSeconds(fadeOut))));
    }

    // Action bar individual con MiniMessage
    public static void sendActionBar(Player player, String message) {
        player.sendActionBar(MINI_MESSAGE.deserialize(message));
    }

    // Título enviado a todos los jugadores conectados
    public static void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Component titleComponent = MINI_MESSAGE.deserialize(title);
        Component subtitleComponent = MINI_MESSAGE.deserialize(subtitle);
        Title formattedTitle = Title.title(titleComponent, subtitleComponent, Title.Times.times(
                Duration.ofSeconds(fadeIn),
                Duration.ofSeconds(stay),
                Duration.ofSeconds(fadeOut)));

        Bukkit.getOnlinePlayers().forEach(player -> player.showTitle(formattedTitle));
    }

    // Action bar enviado a todos los jugadores conectados
    public static void broadcastActionBar(String message) {
        Component component = MINI_MESSAGE.deserialize(message);
        Bukkit.getOnlinePlayers().forEach(player -> player.sendActionBar(component));
    }
}
