package me.xrexy.stafftracker.players;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaytimeManager {
    protected final ConcurrentHashMap<UUID, StaffPlayer> players = new ConcurrentHashMap<>();

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }

    @NotNull
    public StaffPlayer addPlayer(Player player) {
        StaffPlayer sp = new StaffPlayer(player);
        players.put(player.getUniqueId(), sp);
        return sp;
    }

    @NotNull
    public StaffPlayer loadPlayer(OfflinePlayer player) {
        if (players.containsKey(player.getUniqueId()))
            return players.get(player.getUniqueId());
        return new StaffPlayer(player);
    }
}
