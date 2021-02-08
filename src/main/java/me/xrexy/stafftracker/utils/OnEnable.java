package me.xrexy.stafftracker.utils;

import com.earth2me.essentials.Essentials;
import me.xrexy.stafftracker.StaffTracker;
import me.xrexy.stafftracker.commands.CommandHandler;
import me.xrexy.stafftracker.commands.MainCommand;
import me.xrexy.stafftracker.commands.STTabCompleter;
import me.xrexy.stafftracker.commands.subcommands.Check;
import me.xrexy.stafftracker.commands.subcommands.Date;
import me.xrexy.stafftracker.commands.subcommands.Today;
import me.xrexy.stafftracker.commands.subcommands.Yesterday;
import me.xrexy.stafftracker.files.FileManager;
import me.xrexy.stafftracker.players.AFKHandler;
import me.xrexy.stafftracker.players.PlayerManager;
import me.xrexy.stafftracker.players.PlaytimeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class OnEnable {
    private final StaffTracker staffTracker = StaffTracker.getInstance();
    private final PluginManager pluginManager = Bukkit.getPluginManager();
    private final PlaytimeManager playtimeManager = new PlaytimeManager();

    public void execute() {
        if (!setupEssentials()) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", staffTracker.getDescription().getName()));
            pluginManager.disablePlugin(staffTracker);
            Bukkit.getScheduler().cancelTasks(staffTracker);
            return;
        }

        staffTracker.afkHandler = new AFKHandler();

        FileManager fileManager = new FileManager();
        fileManager.loadDailyConfig();
        fileManager.loadDefaultConfig();
        registerEvents();
        registerCommands();

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (Utils.isCheckable(player))
                playtimeManager.addPlayer(player);
        });
    }

    private boolean setupEssentials() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Essentials");
        if (plugin == null)
            return false;

        if (plugin instanceof Essentials)
            staffTracker.essentials = (Essentials) plugin;

        return staffTracker.essentials != null;
    }

    private void registerCommands() {
        final PluginCommand mainPluginCommand = staffTracker.getCommand(staffTracker.MAIN_COMMAND);
        final CommandHandler commandHandler = new CommandHandler(staffTracker.MAIN_COMMAND);

        if (mainPluginCommand == null) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled, couldn't load main command!", staffTracker.getDescription().getName()));
            staffTracker.getServer().getPluginManager().disablePlugin(staffTracker);
            return;
        }

        mainPluginCommand.setExecutor(commandHandler);
        mainPluginCommand.setTabCompleter(new STTabCompleter());

        // MAIN COMMAND
        MainCommand mainCommand = new MainCommand();
        commandHandler.register(mainCommand.getCommand(), mainCommand);

        // SUBCOMMANDS
        Check check = new Check();
        commandHandler.register(check.getCommand(), check);

        Yesterday yesterday = new Yesterday();
        commandHandler.register(yesterday.getCommand(), yesterday);

        Date date = new Date();
        commandHandler.register(date.getCommand(), date);

        Today today = new Today();
        commandHandler.register(today.getCommand(), today);
    }

    private void registerEvents() {
        pluginManager.registerEvents(new PlayerManager(), staffTracker);
        pluginManager.registerEvents(staffTracker.afkHandler, staffTracker);
    }
}
