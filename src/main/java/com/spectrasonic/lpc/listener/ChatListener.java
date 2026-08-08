package com.spectrasonic.lpc.listener;

import com.spectrasonic.lpc.managers.ChatManager;
import com.spectrasonic.lpc.util.ColorUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

@RequiredArgsConstructor
public final class ChatListener implements Listener {

    private final ChatManager chatManager;

    // Fallback para servidores Spigot puros; la clase está deprecada en Paper pero sigue siendo la única vía en Spigot
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String processedMessage = chatManager.processMessage(player, event.getMessage());
        event.setMessage(processedMessage);

        String format = chatManager.buildFormat(player)
                .replace("{message}", "%2$s");
        event.setFormat(ColorUtils.serializeToLegacy(ColorUtils.deserialize(format)));
    }
}
