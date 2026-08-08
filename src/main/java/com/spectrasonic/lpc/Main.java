package com.spectrasonic.lpc;

import com.spectrasonic.lpc.managers.ChatManager;
import com.spectrasonic.lpc.managers.CommandManager;
import com.spectrasonic.lpc.managers.ListenerManager;
import com.spectrasonic.lpc.managers.LuckPermsManager;
import java.util.List;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static final List<String> CHAT_PLUGINS = List.of(
            "EssentialsChat", "VentureChat", "HeroChat", "DeluxeChat",
            "ChatManager", "ChatEx", "UltraChat", "TownyChat");

    @Getter
    private LuckPermsManager luckPermsManager;
    @Getter
    private ChatManager chatManager;

    private CommandManager commandManager;
    private ListenerManager listenerManager;

    @Override
    public void onEnable() {
        LuckPerms luckPerms = getServer().getServicesManager().load(LuckPerms.class);
        if (luckPerms == null) {
            getLogger().severe("LuckPerms not found! LPC requires LuckPerms to function.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        luckPermsManager = new LuckPermsManager(luckPerms);
        chatManager = new ChatManager(this, luckPermsManager);
        commandManager = new CommandManager(this);
        listenerManager = new ListenerManager(this, chatManager);

        saveDefaultConfig();
        commandManager.registerCommands();
        listenerManager.registerListeners();

        warnAboutChatPlugins();
    }

    private void warnAboutChatPlugins() {
        CHAT_PLUGINS.forEach(pluginName -> {
            if (getServer().getPluginManager().isPluginEnabled(pluginName)) {
                getLogger().warning("Detected " + pluginName
                        + " which may also format chat. To avoid message duplication, disable chat formatting in "
                        + pluginName + ".");
            }
        });
    }
}
