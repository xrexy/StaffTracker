package me.xrexy.stafftracker.players;

import de.leonhard.storage.Json;
import de.leonhard.storage.LightningBuilder;
import de.leonhard.storage.internal.settings.DataType;
import de.leonhard.storage.internal.settings.ReloadSettings;
import me.xrexy.stafftracker.utils.Utils;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AFKHandler implements Listener {
    private final PlaytimeManager playtimeManager = new PlaytimeManager();
    private final Json json;

    public AFKHandler() {
        this.json = LightningBuilder.fromPath("afk-players", "plugins/StaffTracker/data")
                .setDataType(DataType.UNSORTED)
                .setReloadSettings(ReloadSettings.INTELLIGENT)
                .createJson();
    }

    @EventHandler
    void afk(AfkStatusChangeEvent event) {
        Player player = event.getAffected().getBase();

        if (!Utils.isCheckable(player))
            return;

        StaffPlayer staffPlayer = playtimeManager.loadPlayer(player);
        String playerName = player.getName();

        if (event.getValue())
            json.set(playerName, staffPlayer.update().getPlaytime());
        else {
            long afkStart = json.getLong(playerName);
            playtimeManager.loadPlayer(player).setTotalPlaytime(afkStart).setPlaytime(afkStart).newTimestamp(System.currentTimeMillis());
            json.remove(playerName);
        }
    }

    public boolean contains(OfflinePlayer player) {
        return json.contains(player.getName());
    }

    public long getAfkStart(OfflinePlayer player) {
        return json.getLong(player.getName());
    }

    public void remove(OfflinePlayer player) {
        json.remove(player.getName());
    }

    public void clear() {
        json.clear();
    }
}
