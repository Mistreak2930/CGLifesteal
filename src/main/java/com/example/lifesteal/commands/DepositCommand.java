package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        if (item == null || !isHeartItem(item)) {
            player.sendMessage("§c[Lifesteal] You must hold a heart item (redstone block)!");
            return true;
        }

        int amount = getHeartAmount(item);
        if (amount <= 0) {
            player.sendMessage("§c[Lifesteal] Invalid heart item!");
            return true;
        }

        int beforeHearts = plugin.getHeartManager().getHearts(player);
        
        // Add hearts
        plugin.getHeartManager().addHearts(player, amount);
        
        int afterHearts = plugin.getHeartManager().getHearts(player);
        
        // Remove item from player
        player.getInventory().setItemInMainHand(null);

        player.sendMessage("§6[Lifesteal] §aDeposited §e" + amount + "§a hearts! (§e" + beforeHearts + "§a -> §e" + afterHearts + "§a)");

        return true;
    }

    private boolean isHeartItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        String displayName = meta.getDisplayName();
        return displayName != null && displayName.contains("Heart Item");
    }

    private int getHeartAmount(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return 0;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasLore()) return 0;
        
        java.util.List<String> lore = meta.getLore();
        for (String line : lore) {
            if (line.contains("Amount:")) {
                try {
                    String amount = line.replaceAll("[^0-9]", "");
                    return Integer.parseInt(amount);
                } catch (Exception e) {
                    return 0;
                }
            }
        }
        return 0;
    }
}
