package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
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
        if (event.getInventory().getName() == null || !event.getInventory().getName().contains("Heart Shop")) {
            return;
        }

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();

        // Handle close button
        if (slot == 49) {
            player.closeInventory();
            player.sendMessage("§6[Lifesteal] §aShop closed!");
            return;
        }

        int hearts = 0;
        int price = 0;

        switch (slot) {
            case 11: // 1 Heart
                hearts = 1;
                price = 10;
                break;
            case 20: // 5 Hearts
                hearts = 5;
                price = 45;
                break;
            case 29: // 10 Hearts
                hearts = 10;
                price = 80;
                break;
            case 38: // 20 Hearts (Max)
                // Check if player has VIP permission
                if (!player.hasPermission("lifesteal.vip")) {
                    player.sendMessage("§c[Lifesteal] You need VIP to buy max hearts!");
                    return;
                }
                hearts = 20;
                price = 150;
                break;
            default:
                return;
        }

        // Check if player has enough points
        int playerPoints = getPlayerPoints(player.getUniqueId());
        if (playerPoints < price) {
            java.util.Map<String, String> replacements = new java.util.HashMap<>();
            replacements.put("price", String.valueOf(price));
            replacements.put("points", String.valueOf(playerPoints));
            player.sendMessage(plugin.getMessageManager().getMessage("shop.not-enough-points", replacements));
            return;
        }

        // Check if adding hearts would exceed max
        int currentHearts = plugin.getHeartManager().getHearts(player);
        if (currentHearts + hearts > plugin.getHeartManager().getMaxHearts()) {
            java.util.Map<String, String> replacements = new java.util.HashMap<>();
            replacements.put("max", String.valueOf(plugin.getHeartManager().getMaxHearts()));
            player.sendMessage(plugin.getMessageManager().getMessage("shop.exceeds-max", replacements));
            return;
        }

        // Process transaction
        if (takePoints(player.getUniqueId(), price)) {
            plugin.getHeartManager().addHearts(player, hearts);

            java.util.Map<String, String> replacements = new java.util.HashMap<>();
            replacements.put("hearts", String.valueOf(hearts));
            replacements.put("price", String.valueOf(price));
            replacements.put("total", String.valueOf(plugin.getHeartManager().getHearts(player)));
            
            player.sendMessage(plugin.getMessageManager().getMessage("shop.success", replacements));
            
            // Refresh GUI to show updated stats
            new com.example.lifesteal.gui.HeartShopGUI(plugin, player).open();
        } else {
            player.sendMessage("§c[Error] Failed to process transaction!");
        }
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

    private boolean takePoints(UUID uuid, int amount) {
        try {
            Object api = plugin.getPlayerPointsAPI();
            if (api != null) {
                java.lang.reflect.Method method = api.getClass().getMethod("take", java.util.UUID.class, int.class);
                method.invoke(api, uuid, amount);
                return true;
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to take points: " + e.getMessage());
        }
        return false;
    }
}
