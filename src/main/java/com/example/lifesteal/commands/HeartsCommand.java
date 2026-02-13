package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HeartsCommand implements CommandExecutor {

    private LifestealPlugin plugin;

    public HeartsCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Show own hearts
            int hearts = plugin.getHeartManager().getHearts(player);
            player.sendMessage("§6[Lifesteal] §aYou have §e" + hearts + "§a hearts");
            return true;
        }

        if (args.length == 1) {
            // Show another player's hearts
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§c[Lifesteal] Player not found!");
                return true;
            }
            int hearts = plugin.getHeartManager().getHearts(target);
            player.sendMessage("§6[Lifesteal] §a" + target.getName() + " has §e" + hearts + "§a hearts");
            return true;
        }

        player.sendMessage("§c[Lifesteal] Usage: /hearts [player]");
        return true;
    }
}
