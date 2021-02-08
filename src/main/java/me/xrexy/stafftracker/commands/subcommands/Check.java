package me.xrexy.stafftracker.commands.subcommands;

import me.xrexy.stafftracker.StaffTracker;
import me.xrexy.stafftracker.commands.CommandInterface;
import me.xrexy.stafftracker.players.AFKHandler;
import me.xrexy.stafftracker.players.PlaytimeManager;
import me.xrexy.stafftracker.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class Check implements CommandInterface {
    private final PlaytimeManager playtimeManager = new PlaytimeManager();
    private final AFKHandler afkHandler = StaffTracker.getInstance().afkHandler;

    @Override
    public String getCommand() {
        return "check";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        Player player = (Player) sender;
        try {
            if (args.length <= 1) {
                Utils.sendMessage(player, "messages.no-player");
                return true;
            }

            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                OfflinePlayer offlineTarget = Bukkit.getOfflinePlayer(args[1]);

                if (!offlineTarget.hasPlayedBefore()) {
                    Utils.sendMessage(player, "messages.invalid-player");
                    return true;
                }

                Utils.sendCheckMessage(player, offlineTarget, playtimeManager.loadPlayer(offlineTarget).getPlaytime(), "messages.check");
                return true;
            }

            if (!Utils.isCheckable(target)) {
                Utils.sendMessage(player, "messages.missing-permission");
                return true;
            }

            if (afkHandler.contains(target)) {
                Utils.sendCheckMessage(player, target, afkHandler.getAfkStart(target), "messages.check");
                return true;
            }

            Utils.sendCheckMessage(player, target, playtimeManager.loadPlayer(target).update().getPlaytime(), "messages.check");
            return true;
        } catch (Exception exception) {
            Utils.debug(exception, Level.WARNING, "Something went wrong while parsing player information");
        }
        return false;
    }
}
