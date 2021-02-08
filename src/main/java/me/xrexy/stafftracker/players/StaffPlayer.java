package me.xrexy.stafftracker.players;

import de.leonhard.storage.Json;
import me.xrexy.stafftracker.files.FileManager;
import org.bukkit.OfflinePlayer;

public class StaffPlayer {
    private final OfflinePlayer player;
    private long timestamp;
    private long totalPlaytime;
    private long playtime;
    private final PlaytimeManager playtimeManager = new PlaytimeManager();
    private final Json json = new FileManager().getCurrentDayConfig().getJson();

    protected StaffPlayer(OfflinePlayer player) {
        this.player = player;
        json.setPathPrefix(player.getUniqueId().toString());
        this.timestamp = json.getOrSetDefault("timestamp", System.currentTimeMillis());
        this.totalPlaytime = json.getOrSetDefault("total", 0);
        this.playtime = json.getOrSetDefault("playtime", 0);

        playtimeManager.players.put(player.getUniqueId(), this);
    }

    public StaffPlayer update() {
        long playedTime = getTimePlayedSinceLastTimestamp() / 1000;
        long totalTime = getTotalPlaytime() + playedTime; // multiplying to convert to milliseconds

        setPlaytime(totalTime); // converting again from milliseconds to seconds
        return this;
    }

    public StaffPlayer setTotalPlaytime(long playtime) {
        json.set("total", playtime);
        this.totalPlaytime = playtime;
        return this;
    }

    public StaffPlayer setPlaytime(long playtime) {
        json.set("playtime", playtime);
        this.playtime = playtime;
        return this;
    }

    public StaffPlayer newTimestamp(long currentTime) {
        json.set("timestamp", currentTime);
        this.timestamp = currentTime;
        return this;
    }

    public long getTimePlayedSinceLastTimestamp() {
        return System.currentTimeMillis() - getTimestamp();
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getTotalPlaytime() {
        return totalPlaytime;
    }

    public long getPlaytime() {
        return playtime;
    }
}
