package me.xrexy.stafftracker.commands.subcommands;

import de.leonhard.storage.Json;
import de.leonhard.storage.LightningBuilder;
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

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

public class Date implements CommandInterface {
    private final FileManager fileManager = new FileManager();
    private final StaffTracker staffTracker = StaffTracker.getInstance();
    private final PlaytimeManager playtimeManager = new PlaytimeManager();
    private final AFKHandler afkHandler = new AFKHandler();

    @Override
    public String getCommand() {
        return "date";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;

        if (args.length <= 1) {
            Utils.sendMessage(player, "messages.missing-args");
            return true;
        }

        CustomDate current = fileManager.getCurrentDayConfig().getDate();

        try {
            String target = args[1];

            File file = new File("plugins/StaffTracker/data/" + target + ".json");
            if (!file.exists()) {
                Utils.processAndSendFileMessage(player, target, current, "messages.files.no-file");
                return true;
            }

            Json wantedJson = LightningBuilder.fromFile(file).createJson();

            StringBuilder message = new StringBuilder();
            for (String s : staffTracker.getConfig().getStringList("messages.files.messages"))
                message.append(
                        s.replace("%date%", current.getFormatted())
                                .replace("%wanted%", target));

            StringBuilder players = new StringBuilder();
            String format = staffTracker.getConfig().getString("messages.files.player-format");

            ArrayList<String> loaded = new ArrayList<>();
            for (String key : wantedJson.keySet()) {
                String uuid = key.split("\\.")[0];
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

                if (offlinePlayer == null || loaded.contains(offlinePlayer.getName()))
                    continue;

                long playtime = wantedJson.getLong(uuid + ".total");
                
                if (playtime <= 0 && !staffTracker.getConfig().getBoolean("messages.show-0s"))
                    continue;

                loaded.add(offlinePlayer.getName());

                players.append(format.replace("%player%", offlinePlayer.getName())
                        .replace("%playtime%", Utils.format(playtime))
                        .replace("%afk%", Utils.colorize(Utils.getString("messages.afk.false"))))
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
