package me.xrexy.stafftracker.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class STTabCompleter implements TabCompleter {
    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("st")
                || command.getName().equalsIgnoreCase("tracker")
                || command.getName().equalsIgnoreCase("stafftracker")) {
            if (args.length == 1)
                return new ArrayList<>(Arrays.asList("date", "check", "yesterday", "today"));

            String arg = args[0].toLowerCase();
            if (args.length > 2 || arg.equals("today") || arg.equals("yesterday"))
                return new ArrayList<>();
        }
        return null;
    }
}
