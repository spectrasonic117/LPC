package com.spectrasonic.lpc.managers;

import com.spectrasonic.lpc.Main;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

// Punto único de entrada para la configuración: posee config.yml y delega messages.yml
@Getter
public final class ConfigManager {

    private final Main plugin;
    private FileConfiguration config;

    // Carga la configuración y los mensajes al construirse
    public ConfigManager(Main plugin) {
        this.plugin = plugin;
        loadConfig();
        loadMessages();
    }

    // Carga config.yml creándolo si no existe
    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    // Inicializa el MessageManager una única vez
    public void loadMessages() {
        MessageManager.init(plugin);
    }

    // Recarga solo config.yml
    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }

    // Recarga solo messages.yml sin recrear la instancia del manager
    public void reloadMessages() {
        MessageManager.getInstance().reloadMessages();
    }

    // Recarga completa: configuración + mensajes
    public void reload() {
        reloadConfig();
        reloadMessages();
    }
}
