package com.spectrasonic.lpc.listener;

import com.spectrasonic.lpc.managers.ChatManager;
import com.spectrasonic.lpc.util.ColorUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class PaperChatListener implements Listener {

    private final ChatManager chatManager;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        String format = chatManager.buildFormat(player);
        String messageContent = LegacyComponentSerializer.legacySection().serialize(event.message());
        String processedMessage = chatManager.processMessage(player, messageContent);
        String finalFormat = format.replace("{message}", processedMessage);
        Component finalMessage = ColorUtils.deserialize(finalFormat);
        event.renderer((source, sourceDisplayName, msg, audience) -> finalMessage);
    }
}
