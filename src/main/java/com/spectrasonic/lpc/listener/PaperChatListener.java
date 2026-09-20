package com.spectrasonic.lpc.listener;

import com.spectrasonic.lpc.managers.ChatManager;
import com.spectrasonic.lpc.util.ColorUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class PaperChatListener implements Listener {

    private static final String MESSAGE_PLACEHOLDER = "{message}";

    private final ChatManager chatManager;

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        try {
            String format = chatManager.buildFormat(player);
            // Texto crudo del jugador sin formato legacy para procesar permisos de colores
            String plain = PlainTextComponentSerializer.plainText().serialize(event.message());
            String processed = chatManager.processMessage(player, plain);
            Component messageComponent = ColorUtils.deserializeSafe(processed, plain);
            // Se deserializan las partes por separado para que el texto del jugador nunca rompa la plantilla
            int index = format.indexOf(MESSAGE_PLACEHOLDER);
            String before = index >= 0 ? format.substring(0, index) : format;
            String after = index >= 0 ? format.substring(index + MESSAGE_PLACEHOLDER.length()) : "";
            Component finalMessage = ColorUtils.deserializeSafe(before, "")
                    .append(messageComponent)
                    .append(ColorUtils.deserializeSafe(after, ""));
            event.renderer((source, sourceDisplayName, msg, audience) -> finalMessage);
        } catch (Exception e) {
            // Sin formato personalizado Paper usa el vanilla; se registra para no fallar en silencio
            Bukkit.getLogger().warning("[LPC] No se pudo formatear el chat de " + player.getName() + ": " + e.getMessage());
        }
    }
}
