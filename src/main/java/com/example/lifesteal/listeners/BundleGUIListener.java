package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.BundleShopGUI;
import com.example.lifesteal.gui.DailyRewardGUI;
import com.example.lifesteal.gui.GuiUtil;
import com.example.lifesteal.gui.HeartShopGUI;
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
        String guiId;
        if (GuiUtil.isGui(event.getView().getTopInventory(), "menu")) {
            guiId = "menu";
        } else if (GuiUtil.isGui(event.getView().getTopInventory(), "bundles")) {
            guiId = "bundles";
        } else if (GuiUtil.isGui(event.getView().getTopInventory(), "bundle-rewards")) {
            guiId = "bundle-rewards";
        } else {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getView().getTopInventory().getSize()) {
            return;
        }
        
        if (guiId.equals("menu")) {
            handleMainMenuClick(player, slot);
        } else if (guiId.equals("bundles")) {
            handleBundleShopClick(player, slot);
        } else {
            handleBundleDetailClick(player, slot);
        }
    }
    
    /**
     * Handle clicks in the Main Menu GUI
     */
    private void handleMainMenuClick(Player player, int slot) {
        if (slot == GuiUtil.slot(plugin, "menu", "bundles", 20, 45)) {
            player.closeInventory();
            new BundleShopGUI(plugin, player).open();
            player.sendMessage("§8[§dBundles§8] §eOpening bundle shop...");
        } else if (slot == GuiUtil.slot(plugin, "menu", "heartshop", 22, 45)) {
            if (plugin.getConfig().getBoolean("heartshop.enabled", true) && plugin.hasPlayerPoints()) {
                player.closeInventory();
                new HeartShopGUI(plugin, player).open();
            } else {
                player.sendMessage(plugin.getMessageManager().getMessage("shop.disabled"));
            }
        } else if (slot == GuiUtil.slot(plugin, "menu", "lottery", 24, 45)) {
            if (plugin.hasLotteryManager()) {
                player.closeInventory();
                new LotteryGUI(plugin, plugin.getLotteryManager(), player).open();
            } else {
                player.sendMessage("§8[§cCGLifesteal§8] §cLottery system not available!");
            }
        } else if (slot == GuiUtil.slot(plugin, "menu", "daily-rewards", 29, 45)) {
            if (plugin.hasDailyRewardManager()) {
                player.closeInventory();
                new DailyRewardGUI(plugin, plugin.getDailyRewardManager(), player).open();
            } else {
                player.sendMessage("§8[§cCGLifesteal§8] §cDaily rewards system not available!");
            }
        } else if (slot == GuiUtil.slot(plugin, "menu", "teams", 31, 45)) {
            player.closeInventory();
            new TeamGUI(plugin).openTeamGUI(player);
        } else if (slot == GuiUtil.slot(plugin, "menu", "close", 40, 45)) {
            player.closeInventory();
        }
    }
    
    /**
     * Handle clicks in the Bundle Shop GUI
     */
    private void handleBundleShopClick(Player player, int slot) {
        int[] bundleSlots = {
            GuiUtil.slot(plugin, "bundles", "bundle-1", 20, 54),
            GuiUtil.slot(plugin, "bundles", "bundle-2", 22, 54),
            GuiUtil.slot(plugin, "bundles", "bundle-3", 24, 54)
        };
        for (int i = 0; i < bundleSlots.length; i++) {
            if (slot == bundleSlots[i]) {
                int bundleNumber = i + 1;
                int availableBundles = bundleManager.getAvailableBundles(player);
                if (bundleNumber <= availableBundles) {
                    player.closeInventory();
                    new BundleShopGUI(plugin, player).openBundleDetail();
                    player.sendMessage("§8[§dBundles§8] §eViewing bundle contents...");
                } else {
                    player.sendMessage("§8[§dBundles§8] §cThis bundle has already been claimed!");
                    player.sendMessage("§8[§dBundles§8] §7Next reset in: §e"
                        + formatTime(bundleManager.getTimeUntilReset(player)));
                }
                return;
            }
        }

        if (slot == GuiUtil.slot(plugin, "bundles", "back", 48, 54)) {
            player.closeInventory();
            new MenuGUI(plugin, player).open();
        } else if (slot == GuiUtil.slot(plugin, "bundles", "close", 49, 54)) {
            player.closeInventory();
        }
    }
    
    /**
     * Handle clicks in the Bundle Detail GUI
     */
    private void handleBundleDetailClick(Player player, int slot) {
        if (slot == GuiUtil.slot(plugin, "bundle-rewards", "next-reward", 13, 54)
            || slot == GuiUtil.slot(plugin, "bundle-rewards", "open", 40, 54)) {
            if (bundleManager.canClaimBundle(player)) {
                boolean success = bundleManager.openBundle(player);
                if (success) {
                    player.sendMessage("§8[§dBundles§8] §a§l✔ Bundle opened successfully!");
                    player.sendMessage("§8[§dBundles§8] §eYou received your reward!");
                    player.closeInventory();
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
        } else if (slot == GuiUtil.slot(plugin, "bundle-rewards", "back", 48, 54)) {
            player.closeInventory();
            new BundleShopGUI(plugin, player).open();
        } else if (slot == GuiUtil.slot(plugin, "bundle-rewards", "close", 49, 54)) {
            player.closeInventory();
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
