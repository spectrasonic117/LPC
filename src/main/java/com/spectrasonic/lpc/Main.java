package com.spectrasonic.lpc;

import com.spectrasonic.lpc.managers.ChatManager;
import com.spectrasonic.lpc.managers.CommandManager;
import com.spectrasonic.lpc.managers.ConfigManager;
import com.spectrasonic.lpc.managers.ListenerManager;
import com.spectrasonic.lpc.managers.LuckPermsManager;
import com.spectrasonic.lpc.managers.MessageManager;
import com.spectrasonic.lpc.util.MessageUtils;

import java.util.List;
import lombok.Getter;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static final List<String> CHAT_PLUGINS = List.of(
            "EssentialsChat", "VentureChat", "HeroChat", "DeluxeChat",
            "ChatManager", "ChatEx", "UltraChat", "TownyChat");

    @Getter
    private LuckPermsManager luckPermsManager;
    @Getter
    private ChatManager chatManager;
    @Getter
    private ConfigManager configManager;
    @Getter
    private MessageManager messageManager;

    private CommandManager commandManager;
    private ListenerManager listenerManager;

    @Override
    public void onEnable() {
        // La configuración y los mensajes se cargan primero para tenerlos disponibles
        // siempre
        configManager = new ConfigManager(this);
        messageManager = MessageManager.getInstance();

        // Se usa getRegistration en lugar de load: load() es @Nullable pero
        // algunos analizadores lo infieren como @NotNull y marcan el chequeo
        // de null como Dead Code. getRegistration es @Nullable explícito.
        RegisteredServiceProvider<LuckPerms> luckPermsProvider = getServer().getServicesManager()
                .getRegistration(LuckPerms.class);
        if (luckPermsProvider == null) {
            getLogger().severe("LuckPerms not found! LPC requires LuckPerms to function.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        luckPermsManager = new LuckPermsManager(luckPermsProvider.getProvider());
        chatManager = new ChatManager(this, luckPermsManager);
        commandManager = new CommandManager(this);
        listenerManager = new ListenerManager(this, chatManager);

        commandManager.registerCommands();
        listenerManager.registerListeners();

        warnAboutChatPlugins();

        MessageUtils.sendStartupMessage(this);
    }

    public void onDisable() {
        MessageUtils.sendShutdownMessage(this);
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
