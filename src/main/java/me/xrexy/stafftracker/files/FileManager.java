package me.xrexy.stafftracker.files;

import me.xrexy.stafftracker.StaffTracker;
import me.xrexy.stafftracker.players.AFKHandler;
import me.xrexy.stafftracker.players.PlaytimeManager;
import me.xrexy.stafftracker.utils.CustomDate;
import me.xrexy.stafftracker.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public class FileManager {
    private final StaffTracker staffTracker = StaffTracker.getInstance();
    private final FileConfiguration defaultConfig = staffTracker.getConfig();
    private DailyConfig dailyConfig = null;
    private final PlaytimeManager playtimeManager = new PlaytimeManager();
    private final AFKHandler afkHandler = staffTracker.afkHandler;

    public void loadDefaultConfig() {
        defaultConfig.options().copyDefaults(true);
        staffTracker.saveDefaultConfig();
    }

    public void loadDailyConfig() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(staffTracker, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (Utils.isCheckable(player))
                    if (afkHandler.contains(player)) {
                        long afkStart = afkHandler.getAfkStart(player);
                        playtimeManager.loadPlayer(player).setTotalPlaytime(afkStart).setPlaytime(afkStart).newTimestamp(System.currentTimeMillis());
                    } else playtimeManager.loadPlayer(player).update();
            });
            dailyConfig = new DailyConfig(new CustomDate()); // creates a new file only if current one doesn't exist
        }, 0L, defaultConfig.getInt("day-check-interval") * 20L);
    }

    public DailyConfig getCurrentDayConfig() {
        if (dailyConfig == null)
            dailyConfig = new DailyConfig(new CustomDate());
        return dailyConfig;
    }

    public FileConfiguration getDefaultConfig() {
        return defaultConfig;
    }
}
