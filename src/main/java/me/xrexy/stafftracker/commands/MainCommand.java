package me.xrexy.stafftracker.commands;

import me.xrexy.stafftracker.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandInterface {
    @Override
    public String getCommand() {
        return "stafftracker";
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String commandLabel, String[] args) {
        Utils.sendMultilineMessage((Player) sender, "messages.help");
        return true;
    }
}
