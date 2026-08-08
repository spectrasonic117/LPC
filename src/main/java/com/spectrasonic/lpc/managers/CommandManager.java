package com.spectrasonic.lpc.managers;

import com.spectrasonic.lpc.Main;
import com.spectrasonic.lpc.command.LPCCommand;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CommandManager {

    private final Main plugin;

    public void registerCommands() {
        new LPCCommand(plugin, plugin.getLuckPermsManager()).register();
    }
}
