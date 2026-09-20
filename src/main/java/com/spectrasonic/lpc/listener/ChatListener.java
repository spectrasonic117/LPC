package com.spectrasonic.lpc.listener;

import com.spectrasonic.lpc.managers.ChatManager;
import com.spectrasonic.lpc.util.ColorUtils;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public final class ChatListener implements Listener {

    private static final String MESSAGE_PLACEHOLDER = "{message}";

    private final ChatManager chatManager;

    // Fallback para servidores Spigot puros; la clase está deprecada en Paper pero sigue siendo la única vía en Spigot
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String processed = chatManager.processMessage(player, event.getMessage());
        // Spigot solo entiende legacy § en el cliente, se convierte el mensaje procesado
        Component messageComponent = ColorUtils.deserializeSafe(processed, event.getMessage());
        event.setMessage(ColorUtils.serializeToLegacy(messageComponent));

        String format = chatManager.buildFormat(player);
        // Se convierte cada parte a legacy y se inserta %2$s donde iba {message}
        int index = format.indexOf(MESSAGE_PLACEHOLDER);
        String before = index >= 0 ? format.substring(0, index) : format;
        String after = index >= 0 ? format.substring(index + MESSAGE_PLACEHOLDER.length()) : "";
        String legacyFormat = ColorUtils.serializeToLegacy(ColorUtils.deserializeSafe(before, ""))
                + "%2$s"
                + ColorUtils.serializeToLegacy(ColorUtils.deserializeSafe(after, ""));
        event.setFormat(legacyFormat);
    }
}
