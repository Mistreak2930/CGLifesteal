package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.HeartShopGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.UUID;

public class HeartShopListener implements Listener {

    private LifestealPlugin plugin;

    public HeartShopListener(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().contains("Heart Emporium")) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        // Get prices from config
        int price1Heart = plugin.getConfig().getInt("heartshop.prices.1-heart", 1000);
        int price5Hearts = plugin.getConfig().getInt("heartshop.prices.5-hearts", 4500);
        int price10Hearts = plugin.getConfig().getInt("heartshop.prices.10-hearts", 8000);

        // Handle purchases
        if (slot == 19) { // 1 Heart
            purchaseHearts(player, 1, price1Heart);
        } else if (slot == 22) { // 5 Hearts
            purchaseHearts(player, 5, price5Hearts);
        } else if (slot == 25) { // 10 Hearts
            purchaseHearts(player, 10, price10Hearts);
        } else if (slot == 49) { // Close button
            player.closeInventory();
            player.sendMessage(plugin.getMessageManager().getMessage("shop.closed"));
        }
    }

    private void purchaseHearts(Player player, int amount, int price) {
        UUID uuid = player.getUniqueId();

        // Check if player has enough points
        int currentPoints = getPlayerPoints(uuid);
        if (currentPoints < price) {
            player.sendMessage(plugin.getMessageManager().getMessage("shop.insufficient-points")
                .replace("%price%", String.valueOf(price))
                .replace("%current%", String.valueOf(currentPoints)));
            return;
        }

        // Deduct points
        if (!takePlayerPoints(uuid, price)) {
            player.sendMessage("§8[§cLifesteal§8] §cFailed to process payment!");
            return;
        }

        // Add hearts
        plugin.getHeartManager().addHearts(player, amount);

        // Success message
        player.sendMessage(plugin.getMessageManager().getMessage("shop.purchase-success")
            .replace("%amount%", String.valueOf(amount))
            .replace("%price%", String.valueOf(price)));

        // Refresh GUI
        new HeartShopGUI(plugin, player).open();
    }

    private int getPlayerPoints(UUID uuid) {
        try {
            Object api = plugin.getPlayerPointsAPI();
            if (api != null) {
                java.lang.reflect.Method method = api.getClass().getMethod("look", java.util.UUID.class);
                Object result = method.invoke(api, uuid);
                return ((Number) result).intValue();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get player points: " + e.getMessage());
        }
        return 0;
    }

    private boolean takePlayerPoints(UUID uuid, int amount) {
        try {
            Object api = plugin.getPlayerPointsAPI();
            if (api != null) {
                java.lang.reflect.Method method = api.getClass().getMethod("take", java.util.UUID.class, int.class);
                Object result = method.invoke(api, uuid, amount);
                return (Boolean) result;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to take player points: " + e.getMessage());
        }
        return false;
    }
}