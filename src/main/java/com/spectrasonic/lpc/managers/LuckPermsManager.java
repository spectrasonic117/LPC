package com.spectrasonic.lpc.managers;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class LuckPermsManager {

    private final LuckPerms luckPerms;

    public CachedMetaData getPlayerMetaData(Player player) {
        return luckPerms.getPlayerAdapter(Player.class).getMetaData(player);
    }

    public String getPrimaryGroup(Player player) {
        return getPlayerMetaData(player).getPrimaryGroup();
    }

    public String getPrefix(Player player) {
        return getPlayerMetaData(player).getPrefix();
    }

    public String getSuffix(Player player) {
        return getPlayerMetaData(player).getSuffix();
    }

    public Map<Integer, String> getPrefixes(Player player) {
        return getPlayerMetaData(player).getPrefixes();
    }

    public Map<Integer, String> getSuffixes(Player player) {
        return getPlayerMetaData(player).getSuffixes();
    }

    public String getMetaValue(Player player, String key) {
        return getPlayerMetaData(player).getMetaValue(key);
    }
}
