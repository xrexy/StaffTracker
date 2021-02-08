package me.xrexy.stafftracker.commands.subcommands;

import de.leonhard.storage.Json;
import me.xrexy.stafftracker.StaffTracker;
import me.xrexy.stafftracker.commands.CommandInterface;
import me.xrexy.stafftracker.files.FileManager;
import me.xrexy.stafftracker.players.AFKHandler;
import me.xrexy.stafftracker.players.PlaytimeManager;
import me.xrexy.stafftracker.players.StaffPlayer;
import me.xrexy.stafftracker.utils.CustomDate;
import me.xrexy.stafftracker.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class Today implements CommandInterface {
    private final FileManager fileManager = new FileManager();
    private final StaffTracker staffTracker = StaffTracker.getInstance();
    private final PlaytimeManager playtimeManager = new PlaytimeManager();
    private final AFKHandler afkHandler = staffTracker.afkHandler;

    @Override
    public String getCommand() {
        return "today";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;
        try {
            CustomDate current = fileManager.getCurrentDayConfig().getDate();
            Json wantedJson = fileManager.getCurrentDayConfig().getJson();

            StringBuilder message = new StringBuilder();
            for (String s : staffTracker.getConfig().getStringList("messages.files.messages"))
                message.append(
                        s.replace("%date%", current.getFormatted())
                                .replace("%wanted%", current.getFormatted()));

            StringBuilder players = new StringBuilder();
            String format = staffTracker.getConfig().getString("messages.files.player-format");

            long playtime;
            ArrayList<String> loaded = new ArrayList<>();
            for (String key : wantedJson.keySet()) {
                boolean isAfk = false;

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(key.split("\\.")[0]));

                if (offlinePlayer == null || loaded.contains(offlinePlayer.getName()))
                    continue;

                loaded.add(offlinePlayer.getName());
                StaffPlayer staffPlayer = playtimeManager.loadPlayer(offlinePlayer);

                if (afkHandler.contains(offlinePlayer)) {
                    playtime = afkHandler.getAfkStart(offlinePlayer);
                    isAfk = true;
                } else {
                    if (offlinePlayer.isOnline())
                        playtime = staffPlayer.update().getPlaytime();
                    else
                        playtime = staffPlayer.getPlaytime();
                }

                if (playtime <= 0 && !staffTracker.getConfig().getBoolean("messages.show-0s"))
                    continue;

                players.append(format.replace("%player%", offlinePlayer.getName())
                        .replace("%playtime%", Utils.format(playtime))
                        .replace("%afk%", Utils.colorize(Utils.getString("messages.afk." + isAfk))))
                        .append("\n");
            }
            player.sendMessage(Utils.colorize(message.toString()));
            if (loaded.isEmpty())
                Utils.sendMessage(player, "messages.files.no-information");
            else
                player.sendMessage(Utils.colorize(players.toString()));
        } catch (Exception exception) {
            Utils.debug(exception, Level.WARNING, "Something went wrong while parsing information for date");
        }
        return true;
    }
}
