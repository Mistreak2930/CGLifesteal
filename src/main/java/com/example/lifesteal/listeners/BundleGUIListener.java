package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.BundleShopGUI;
import com.example.lifesteal.gui.DailyRewardGUI;
import com.example.lifesteal.gui.LotteryGUI;
import com.example.lifesteal.gui.MenuGUI;
import com.example.lifesteal.gui.TeamGUI;
import com.example.lifesteal.system.BundleManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Listener for handling clicks in the Menu and Bundle GUIs
 */
public class BundleGUIListener implements Listener {
    
    private LifestealPlugin plugin;
    private BundleManager bundleManager;
    
    public BundleGUIListener(LifestealPlugin plugin, BundleManager bundleManager) {
        this.plugin = plugin;
        this.bundleManager = bundleManager;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        
        // Check if it's one of our custom GUIs
        if (!title.contains("Main Menu") && 
            !title.contains("Daily Bundles") && 
            !title.contains("Bundle Rewards")) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        
        // Handle Main Menu clicks
        if (title.contains("Main Menu")) {
            handleMainMenuClick(player, slot);
        }
        // Handle Daily Bundles clicks
        else if (title.contains("Daily Bundles") && !title.contains("Bundle Rewards")) {
            handleBundleShopClick(player, slot);
        }
        // Handle Bundle Rewards (detail) clicks
        else if (title.contains("Bundle Rewards")) {
            handleBundleDetailClick(player, slot);
        }
    }
    
    /**
     * Handle clicks in the Main Menu GUI
     */
    private void handleMainMenuClick(Player player, int slot) {
        switch (slot) {
            case 20: // Bundles Shop
                player.closeInventory();
                new BundleShopGUI(plugin, player).open();
                player.sendMessage("§8[§dBundles§8] §eOpening bundle shop...");
                break;
                
            case 22: // Warps
                player.sendMessage("§8[§bWarps§8] §cWarps feature coming soon!");
                break;
                
            case 24: // Heart Shop
                player.sendMessage("§8[§cHeart Shop§8] §cHeart shop feature coming soon!");
                break;
                
            case 29: // Lottery
                if (plugin.hasLotteryManager()) {
                    player.closeInventory();
                    new LotteryGUI(plugin, plugin.getLotteryManager(), player).open();
                    player.sendMessage("§8[§aLottery§8] §eOpening lottery...");
                } else {
                    player.sendMessage("§8[§cLifesteal§8] §cLottery system not available!");
                }
                break;
                
            case 31: // Daily Rewards
                if (plugin.hasDailyRewardManager()) {
                    player.closeInventory();
                    new DailyRewardGUI(plugin, plugin.getDailyRewardManager(), player).open();
                    player.sendMessage("§8[§6Daily Rewards§8] §eOpening daily rewards...");
                } else {
                    player.sendMessage("§8[§cLifesteal§8] §cDaily rewards system not available!");
                }
                break;
                
            case 33: // Teams
                player.closeInventory();
                new TeamGUI(plugin).openTeamGUI(player);
                player.sendMessage("§8[§eTeams§8] §eOpening team GUI...");
                break;
                
            case 49: // Close
                player.closeInventory();
                break;
        }
    }
    
    /**
     * Handle clicks in the Bundle Shop GUI
     */
    private void handleBundleShopClick(Player player, int slot) {
        switch (slot) {
            case 20: // Bundle #1
            case 22: // Bundle #2
            case 24: // Bundle #3
                int bundleNumber = (slot == 20) ? 1 : (slot == 22) ? 2 : 3;
                int availableBundles = bundleManager.getAvailableBundles(player);
                
                if (bundleNumber <= availableBundles) {
                    // Open bundle detail view
                    player.closeInventory();
                    new BundleShopGUI(plugin, player).openBundleDetail();
                    player.sendMessage("§8[§dBundles§8] §eViewing bundle contents...");
                } else {
                    player.sendMessage("§8[§dBundles§8] §cThis bundle has already been claimed!");
                    long timeUntil = bundleManager.getTimeUntilReset(player);
                    player.sendMessage("§8[§dBundles§8] §7Next reset in: §e" + formatTime(timeUntil));
                }
                break;
                
            case 48: // Back
                player.closeInventory();
                new MenuGUI(plugin, player).open();
                break;
                
            case 49: // Close
                player.closeInventory();
                break;
        }
    }
    
    /**
     * Handle clicks in the Bundle Detail GUI
     */
    private void handleBundleDetailClick(Player player, int slot) {
        switch (slot) {
            case 13: // Next reward (claim)
            case 40: // Open bundle button
                if (bundleManager.canClaimBundle(player)) {
                    // Open the bundle and give reward
                    boolean success = bundleManager.openBundle(player);
                    
                    if (success) {
                        player.sendMessage("§8[§dBundles§8] §a§l✔ Bundle opened successfully!");
                        player.sendMessage("§8[§dBundles§8] §eYou received your reward!");
                        
                        // Refresh the GUI
                        player.closeInventory();
                        
                        // Check if they still have bundles available
                        if (bundleManager.canClaimBundle(player)) {
                            new BundleShopGUI(plugin, player).openBundleDetail();
                        } else {
                            new BundleShopGUI(plugin, player).open();
                        }
                    } else {
                        player.sendMessage("§8[§dBundles§8] §cFailed to open bundle!");
                    }
                } else {
                    player.sendMessage("§8[§dBundles§8] §cYou don't have any bundles available!");
                    long timeUntil = bundleManager.getTimeUntilReset(player);
                    player.sendMessage("§8[§dBundles§8] §7Next reset in: §e" + formatTime(timeUntil));
                }
                break;
                
            case 48: // Back
                player.closeInventory();
                new BundleShopGUI(plugin, player).open();
                break;
                
            case 49: // Close
                player.closeInventory();
                break;
        }
    }
    
    /**
     * Format time in seconds to readable format
     */
    private String formatTime(long seconds) {
        if (seconds <= 0) {
            return "Ready!";
        }
        
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }
}
