package com.spectrasonic.lpc.listener;

import com.spectrasonic.lpc.managers.ChatManager;
import com.spectrasonic.lpc.util.ColorUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class ChatListener implements Listener {

    private final ChatManager chatManager;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        Component originalMessage = event.message();
        String messageString = ColorUtils.serializeToLegacy(originalMessage);
        String processedMessage = chatManager.processMessage(player, messageString);
        String format = chatManager.buildFormat(player);
        String finalFormat = format.replace("{message}", processedMessage).replace("%", "%%");
        Component finalComponent = ColorUtils.deserialize(finalFormat);
        event.message(finalComponent);
    }
}
