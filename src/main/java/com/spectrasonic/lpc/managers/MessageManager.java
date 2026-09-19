package com.spectrasonic.lpc.managers;

import com.spectrasonic.lpc.Main;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

// Gestor central de mensajes: carga messages.yml y expone las claves del plugin
public final class MessageManager {

    private static MessageManager instance;

    private final Main plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    // Constructor privado: la instanciación pasa únicamente por init()
    private MessageManager(Main plugin) {
        this.plugin = plugin;
        loadMessages();
    }

    // Inicializa la instancia única; solo debe invocarse una vez desde ConfigManager
    public static void init(Main plugin) {
        if (instance == null) {
            new MessageManager(plugin);
        }
    }

    // Instancia única inicializada desde ConfigManager durante onEnable
    public static MessageManager getInstance() {
        return instance;
    }

    // Devuelve el string MiniMessage de la clave o un fallback visible si no existe
    public static String getMessage(String key) {
        return instance.messagesConfig.getString(
                key,
                "<red>Message not found: " + key + "</red>");
    }

    // Resuelve placeholders <placeholder> en pares clave/valor antes de enviar
    public static String getMessage(String key, String... replacements) {
        String message = getMessage(key);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace("<" + replacements[i] + ">", replacements[i + 1]);
        }
        return message;
    }

    // Devuelve la clave ya deserializada como Component de Adventure
    public static Component component(String key) {
        return MiniMessage.miniMessage().deserialize(getMessage(key));
    }

    // Variante con placeholders que devuelve Component directamente
    public static Component component(String key, String... replacements) {
        return MiniMessage.miniMessage().deserialize(getMessage(key, replacements));
    }

    // Copia messages.yml desde el jar si no existe y carga la configuración
    public void loadMessages() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        try (InputStream inputStream = plugin.getResource("messages.yml")) {
            if (inputStream != null) {
                Files.copy(inputStream, messagesFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save messages.yml: " + e.getMessage());
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        instance = this;
    }

    // Recarga solo la configuración en memoria, nunca recrea el archivo
    public void reloadMessages() {
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
}
