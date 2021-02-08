package me.xrexy.stafftracker;

import com.earth2me.essentials.Essentials;
import me.xrexy.stafftracker.players.AFKHandler;
import me.xrexy.stafftracker.utils.OnEnable;
import org.bukkit.plugin.java.JavaPlugin;

public final class StaffTracker extends JavaPlugin {
    private static StaffTracker instance;
    public final String MAIN_COMMAND = "stafftracker";
    public Essentials essentials = null;
    public AFKHandler afkHandler = null;

    @Override
    public void onEnable() {
        instance = this;
        new OnEnable().execute();
    }

    public static StaffTracker getInstance() {
        return instance;
    }
}
