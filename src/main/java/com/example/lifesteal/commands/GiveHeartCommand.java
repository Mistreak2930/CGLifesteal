package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveHeartCommand implements CommandExecutor {

    private LifestealPlugin plugin;

    public GiveHeartCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("lifesteal.admin")) {
            sender.sendMessage("§8[§cLifesteal§8] §cYou don't have permission!");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§8[§cLifesteal§8] §cUsage: /giveheart <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§8[§cLifesteal§8] §cPlayer not found!");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§8[§cLifesteal§8] §cInvalid amount!");
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage("§8[§cLifesteal§8] §cAmount must be positive!");
            return true;
        }

        int oldHearts = plugin.getHeartManager().getHearts(target);
        plugin.getHeartManager().addHearts(target, amount);
        int newHearts = plugin.getHeartManager().getHearts(target);

        sender.sendMessage("§8[§cLifesteal§8] §aGave §e" + amount + "§a hearts to §e" + target.getName() + 
                          "§a (§e" + oldHearts + "§a -> §e" + newHearts + "§a)");
        target.sendMessage("§8[§cLifesteal§8] §e" + sender.getName() + "§a gave you §e" + amount + "§a hearts!");

        return true;
    }
}
