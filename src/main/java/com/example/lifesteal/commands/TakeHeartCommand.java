package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TakeHeartCommand implements CommandExecutor {

    private LifestealPlugin plugin;

    public TakeHeartCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("lifesteal.admin")) {
            sender.sendMessage("§c[Lifesteal] You don't have permission!");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§c[Lifesteal] Usage: /takeheart <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§c[Lifesteal] Player not found!");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§c[Lifesteal] Invalid amount!");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage("§c[Lifesteal] Amount must be positive!");
            return true;
        }

        int oldHearts = plugin.getHeartManager().getHearts(target);
        plugin.getHeartManager().removeHearts(target, amount);
        int newHearts = plugin.getHeartManager().getHearts(target);

        sender.sendMessage("§6[Lifesteal] §aRemoved §e" + amount + "§a hearts from §e" + target.getName() + 
                          "§a (§e" + oldHearts + "§a -> §e" + newHearts + "§a)");
        target.sendMessage("§6[Lifesteal] §e" + sender.getName() + "§a removed §c" + amount + "§a hearts from you!");

        return true;
    }
}
