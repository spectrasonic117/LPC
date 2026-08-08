package com.spectrasonic.lpc.managers;

import com.spectrasonic.lpc.Main;
import com.spectrasonic.lpc.listener.ChatListener;
import com.spectrasonic.lpc.listener.PaperChatListener;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.PluginManager;

@RequiredArgsConstructor
public final class ListenerManager {

    private final Main plugin;
    private final ChatManager chatManager;

    public void registerListeners() {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        boolean paperChat = false;

        try {
            Class.forName("io.papermc.paper.event.player.AsyncChatEvent");
            paperChat = true;
        } catch (ClassNotFoundException ignored) {
            // Servidor no Paper, se usa el listener estándar de Spigot
        }

        if (paperChat) {
            pluginManager.registerEvents(new PaperChatListener(chatManager), plugin);
        } else {
            pluginManager.registerEvents(new ChatListener(chatManager), plugin);
        }
    }
}
