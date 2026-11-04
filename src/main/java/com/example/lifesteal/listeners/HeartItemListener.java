package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.util.HeartItemUtil;
import com.example.lifesteal.gui.GuiUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles heart item usage - right-clicking a heart item to gain max health
 */
public class HeartItemListener implements Listener {

    private LifestealPlugin plugin;

    public HeartItemListener(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if this is a right-click
        if (!event.getAction().toString().contains("RIGHT")) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Check if item is valid
        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (HeartItemUtil.isDepositHeartItem(item)) {
            event.setCancelled(true);

            int amount = HeartItemUtil.getDepositHeartAmount(item);
            if (amount <= 0) {
                player.sendMessage(plugin.getMessageManager().getMessage("deposit.invalid-item"));
                return;
            }

            plugin.getHeartManager().addHearts(player, amount);
            int totalHearts = plugin.getHeartManager().getHearts(player);

            if (item.getAmount() > 1) {
                item.setAmount(item.getAmount() - 1);
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            }

            Map<String, String> replacements = new HashMap<>();
            replacements.put("amount", String.valueOf(amount));
            replacements.put("total", String.valueOf(totalHearts));
            player.sendMessage(plugin.getMessageManager().getMessage("deposit.success", replacements));
            return;
        }
        
        if (item.getType() != GuiUtil.material(plugin, "hearts.item.material", Material.RED_DYE)) {
            return;
        }

        // Check if this is a heart item (RED_DYE with proper metadata)
        if (!isHeartItem(item)) {
            return;
        }

        // Prevent default action
        event.setCancelled(true);

        // Get the heart manager
        int currentHearts = plugin.getHeartManager().getHearts(player);
        int maxHearts = plugin.getHeartManager().getMaxHearts();

        // Check if player is at max hearts
        if (currentHearts >= maxHearts) {
            player.sendMessage("§8[§cLifesteal§8] §cYou already have the maximum hearts!");
            return;
        }

        // Add one heart
        plugin.getHeartManager().addHearts(player, 1);

        // Remove one item from player's hand
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
        } else {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        }

        // Notify player
        int newHearts = currentHearts + 1;
        player.sendMessage("§8[§cLifesteal§8] §aYou used a heart! Now you have §e" + newHearts + "§a hearts.");
    }

    /**
     * Check if an item is a heart item
     * @param item the item to check
     * @return true if it's a heart item
     */
    private boolean isHeartItem(ItemStack item) {
        if (item.getType() != GuiUtil.material(plugin, "hearts.item.material", Material.RED_DYE)) {
            return false;
        }

        // Check if it has custom name or lore
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        // Check for "Heart" display name
        if (meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();
            // Remove color codes for comparison
            String cleanName = displayName.replaceAll("§[0-9a-fk-or]", "");
            String configuredName = plugin.getConfig().getString("hearts.item.name", "§cHeart")
                .replaceAll("§[0-9a-fk-or]", "");
            if (cleanName.equalsIgnoreCase(configuredName)) {
                return true;
            }
        }

        return false;
    }
}
