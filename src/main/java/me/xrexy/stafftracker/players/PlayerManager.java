package me.xrexy.stafftracker.players;

import me.xrexy.stafftracker.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerManager implements Listener {
    private final PlaytimeManager playtimeManager = new PlaytimeManager();

    @EventHandler
    void join(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!Utils.isCheckable(player))
            return;

        playtimeManager.addPlayer(player).newTimestamp(System.currentTimeMillis());
    }

    @EventHandler
    void quit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        if (!Utils.isCheckable(player))
            return;

        StaffPlayer staffPlayer = playtimeManager.loadPlayer(player);

        staffPlayer.update().setTotalPlaytime(staffPlayer.getPlaytime());
        playtimeManager.removePlayer(player);
    }
}
