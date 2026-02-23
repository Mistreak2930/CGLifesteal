package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.example.lifesteal.util.HeartItemUtil;

import java.util.HashMap;
import java.util.Map;

public class DepositCommand implements CommandExecutor {

    private LifestealPlugin plugin;

    public DepositCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.only-players"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || !HeartItemUtil.isDepositHeartItem(item)) {
            player.sendMessage(plugin.getMessageManager().getMessage("deposit.invalid-item"));
            return true;
        }

        int amountPerItem = HeartItemUtil.getDepositHeartAmount(item);
        if (amountPerItem <= 0) {
            player.sendMessage(plugin.getMessageManager().getMessage("deposit.invalid-item"));
            return true;
        }

        int stackAmount = item.getAmount();
        int totalAmount = amountPerItem * stackAmount;

        int beforeHearts = plugin.getHeartManager().getHearts(player);
        
        // Add hearts
        plugin.getHeartManager().addHearts(player, totalAmount);
        
        int afterHearts = plugin.getHeartManager().getHearts(player);
        
        // Remove item from player
        player.getInventory().setItemInMainHand(null);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("amount", String.valueOf(totalAmount));
        replacements.put("total", String.valueOf(afterHearts));
        player.sendMessage(plugin.getMessageManager().getMessage("deposit.success", replacements));

        return true;
    }

}
