package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.util.HeartItemUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class WithdrawCommand implements CommandExecutor {

    private LifestealPlugin plugin;

    public WithdrawCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.only-players"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage("§8[§cLifesteal§8] §cUsage: /withdraw <amount>");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(plugin.getMessageManager().getMessage("general.invalid-amount"));
            return true;
        }

        if (amount <= 0) {
            Map<String, String> replacements = new HashMap<>();
            player.sendMessage(plugin.getMessageManager().getMessage("withdraw.invalid-amount", replacements));
            return true;
        }

        int hearts = plugin.getHeartManager().getHearts(player);
        if (hearts < amount) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("hearts", String.valueOf(hearts));
            player.sendMessage(plugin.getMessageManager().getMessage("withdraw.not-enough", replacements));
            return true;
        }

        // Create heart item
        ItemStack heartItem = HeartItemUtil.createDepositHeartItem(plugin, amount);
        
        // Remove hearts from player
        plugin.getHeartManager().removeHearts(player, amount);
        int remaining = plugin.getHeartManager().getHearts(player);

        // Give item to player
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(heartItem);
            Map<String, String> replacements = new HashMap<>();
            replacements.put("amount", String.valueOf(amount));
            replacements.put("remaining", String.valueOf(remaining));
            player.sendMessage(plugin.getMessageManager().getMessage("withdraw.success", replacements));
        } else {
            // Drop item if inventory is full
            player.getWorld().dropItemNaturally(player.getLocation(), heartItem);
            Map<String, String> replacements = new HashMap<>();
            replacements.put("amount", String.valueOf(amount));
            player.sendMessage(plugin.getMessageManager().getMessage("withdraw.success-dropped", replacements));
        }

        return true;
    }

}

