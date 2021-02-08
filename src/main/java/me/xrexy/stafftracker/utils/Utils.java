package me.xrexy.stafftracker.utils;

import me.xrexy.stafftracker.StaffTracker;
import me.xrexy.stafftracker.files.FileManager;
import me.xrexy.stafftracker.players.AFKHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class Utils {
    private static class Placeholder {

        private final String placeholder, replacement;

        public Placeholder(String placeholder, String replacement) {
            this.placeholder = placeholder;
            this.replacement = replacement;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public String getReplacement() {
            return replacement;
        }

        public static String processPlaceholders(String message, Placeholder... placeholders) {
            for (Placeholder p : placeholders)
                message = message.replace(p.getPlaceholder(), p.getReplacement());

            return colorize(message);
        }

        public static ArrayList<String> processPlaceholders(List<String> messages, Placeholder... placeholders) {
            ArrayList<String> output = new ArrayList<>();
            for (String line : messages) {
                for (Placeholder p : placeholders)
                    line = line.replace(p.getPlaceholder(), p.getReplacement());

                output.add(colorize(line));
            }
            return output;
        }
    }

    private static final StaffTracker staffTracker = StaffTracker.getInstance();
    private static final FileManager fileManager = new FileManager();
    private static final FileConfiguration defaultConfig = fileManager.getDefaultConfig();
    private static final AFKHandler afkHandler = new AFKHandler();

    public static void debug(Exception exception, Level level, String message) {
        if (staffTracker.getConfig().getBoolean("debug"))
            exception.printStackTrace();
        Bukkit.getLogger().log(level, colorize(process(message)));
    }

    public static boolean isCheckable(Player player) {
        return player.hasPermission("stafftracker.checkable");
    }

    public static void processAndSendFileMessage(Player toReceive, CustomDate wanted, CustomDate current, String path) {
        toReceive.sendMessage(Placeholder.processPlaceholders(getString(path),
                new Placeholder("%prefix%", defaultConfig.getString("prefix")),
                new Placeholder("%current%", current.getFormatted()),
                new Placeholder("%wanted%", wanted.getFormatted())
        ));
    }

    public static void processAndSendFileMessage(Player toReceive, String wanted, CustomDate current, String path) {
        toReceive.sendMessage(Placeholder.processPlaceholders(getString(path),
                new Placeholder("%prefix%", defaultConfig.getString("prefix")),
                new Placeholder("%current%", current.getFormatted()),
                new Placeholder("%wanted%", wanted)
        ));
    }

    public static String process(String toProcess) {
        return Placeholder.processPlaceholders(toProcess,
                new Placeholder("%prefix%", defaultConfig.getString("prefix")));
    }

    public static String getString(String path) {
        return defaultConfig.getString(path);
    }

    public static void sendCheckMessage(Player player, OfflinePlayer target, long playtime, String path) {
        StringBuilder output = new StringBuilder();
        long minutes = TimeUnit.MINUTES.convert(playtime, TimeUnit.SECONDS);
        long hours = TimeUnit.HOURS.convert(playtime, TimeUnit.SECONDS);
        long days = TimeUnit.DAYS.convert(playtime, TimeUnit.SECONDS);

        for (String s : defaultConfig.getStringList(path))
            output.append(s.replace("%player%", target.getName())
                    .replace("%playtime%", format(playtime, minutes, hours, days))
                    .replace("%seconds_total%", playtime + "")
                    .replace("%minutes_total%", minutes + "")
                    .replace("%hours_total%", hours + "")
                    .replace("%days_total%", days + "")
                    .replace("%afk%", getString("messages.afk." + afkHandler.contains(target)))).append("\n");
        player.sendMessage(colorize(output.toString()));
    }

    public static String format(long seconds) {
        StringBuilder output = new StringBuilder();
        long minutes = TimeUnit.MINUTES.convert(seconds, TimeUnit.SECONDS);
        long hours = TimeUnit.HOURS.convert(seconds, TimeUnit.SECONDS);
        long days = TimeUnit.DAYS.convert(seconds, TimeUnit.SECONDS);

        int shaded_seconds = (int) (seconds % 60);
        int shaded_minutes = (int) (minutes % 60);

        // 2d 1h 23m 32sec

        if (days > 0)
            output.append(days).append(getString("units.day")).append(getString("units.split"));
        if (hours > 0)
            output.append(hours).append(getString("units.hour")).append(getString("units.split"));
        if (minutes > 0)
            output.append(shaded_minutes).append(getString("units.minute")).append(getString("units.split"));
        output.append(shaded_seconds).append(getString("units.second"));

        return output.toString();
    }

    public static String format(long seconds, long minutes, long hours, long days) {
        StringBuilder output = new StringBuilder();

        int shaded_seconds = (int) (seconds % 60);
        int shaded_minutes = (int) (minutes % 60);

        // 2d 1h 23m 32sec

        if (days > 0)
            output.append(days).append(getString("units.day")).append(getString("units.split"));
        if (hours > 0)
            output.append(hours).append(getString("units.hour")).append(getString("units.split"));
        if (minutes > 0)
            output.append(shaded_minutes).append(getString("units.minute")).append(getString("units.split"));
        output.append(shaded_seconds).append(getString("units.second"));

        return output.toString();
    }

    public static void sendMultilineMessage(Player p, String path) {
        for (String s : defaultConfig.getStringList(path))
            p.sendMessage(colorize(s));
    }

    public static void sendMessage(Player p, String path) {
        p.sendMessage(colorize(process(getString(path))));
    }

    public static String colorize(String toColorize) {
        return ChatColor.translateAlternateColorCodes('&', toColorize);
    }
}
