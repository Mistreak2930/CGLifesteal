package com.example.lifesteal.gui;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.BundleManager;
import com.example.lifesteal.system.BundleManager.BundleReward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * GUI for the daily bundles shop
 * Shows available bundles and allows players to open them
 */
public class BundleShopGUI {
    
    private LifestealPlugin plugin;
    private BundleManager bundleManager;
    private Player player;
    
    public BundleShopGUI(LifestealPlugin plugin, Player player) {
        this.plugin = plugin;
        this.bundleManager = plugin.getBundleManager();
        this.player = player;
    }
    
    public void open() {
        Inventory inventory = GuiUtil.createInventory(plugin, "bundles", 54, "<light_purple><bold>Daily Bundles</bold></light_purple>");
        
        // Fill background
        ItemStack background = createPane(GuiUtil.material(plugin, "gui.theme.background", Material.BLACK_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, background);
        }
        
        // Create decorative border
        ItemStack borderPane = createPane(GuiUtil.material(plugin, "gui.theme.accent", Material.RED_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, borderPane);
            inventory.setItem(i + 45, borderPane);
        }
        for (int i = 9; i < 45; i += 9) {
            inventory.setItem(i, borderPane);
            inventory.setItem(i + 8, borderPane);
        }
        
        // Title
        ItemStack title = createItem(Material.CHEST, "§d§l✦ Daily Bundles ✦", 
            "§7Open bundles to receive rewards!",
            "",
            "§eRewards refresh every 12 hours");
        inventory.setItem(4, title);
        
        // Get player's bundle info
        int availableBundles = bundleManager.getAvailableBundles(player);
        long timeUntilReset = bundleManager.getTimeUntilReset(player);
        long timeUntilRefresh = bundleManager.getTimeUntilRewardRefresh(player);
        
        // Bundle slots: 20, 22, 24
        int[] bundleSlots = {
            GuiUtil.slot(plugin, "bundles", "bundle-1", 20, 54),
            GuiUtil.slot(plugin, "bundles", "bundle-2", 22, 54),
            GuiUtil.slot(plugin, "bundles", "bundle-3", 24, 54)
        };
        
        for (int i = 0; i < 3; i++) {
            ItemStack bundle;
            if (i < availableBundles) {
                // Available bundle
                bundle = createAvailableBundle(i + 1);
            } else {
                // Claimed bundle
                bundle = createClaimedBundle(i + 1);
            }
            inventory.setItem(bundleSlots[i], bundle);
        }
        
        // Info panel (slot 31)
        ItemStack info = createItem(Material.PAPER, "§e§lBundle Information", 
            "§7Information about bundles",
            "",
            "§eAvailable Bundles: §a" + availableBundles + "§7/§23",
            "§eTotal Claimed: §a" + bundleManager.getTotalBundlesClaimed(player),
            "",
            "§7Time until reset:",
            "§e" + formatTime(timeUntilReset),
            "",
            "§7Next reward refresh:",
            "§e" + formatTime(timeUntilRefresh));
        inventory.setItem(GuiUtil.slot(plugin, "bundles", "info", 31, 54), info);
        
        // Current reward preview (slot 40)
        Optional<BundleReward> currentReward = bundleManager.getCurrentReward(player);
        ItemStack rewardPreview = currentReward
            .map(reward -> createItem(reward.getDisplayMaterial(),
                "§6§lNext Reward",
                "§7Your next bundle will contain:",
                "",
                "§e" + reward.getDisplayName(),
                "",
                "§7Refreshes: §e" + formatTime(timeUntilRefresh)))
            .orElseGet(() -> createItem(Material.BARRIER, "§c§lNo Rewards Available",
                "§7No configured bundle rewards are",
                "§7available with the current integrations."));
        inventory.setItem(GuiUtil.slot(plugin, "bundles", "preview", 40, 54), rewardPreview);
        
        // Back button (slot 48)
        ItemStack back = createItem(Material.ARROW, "§e§lBack", 
            "§7Return to main menu");
        inventory.setItem(GuiUtil.slot(plugin, "bundles", "back", 48, 54), back);
        
        // Close button (slot 49)
        ItemStack close = createItem(Material.BARRIER, "§c§lClose", 
            "§7Click to close");
        inventory.setItem(GuiUtil.slot(plugin, "bundles", "close", 49, 54), close);
        
        player.openInventory(inventory);
    }
    
    /**
     * Open the bundle detail GUI showing all rewards
     */
    public void openBundleDetail() {
        Inventory inventory = GuiUtil.createInventory(plugin, "bundle-rewards", 54, "<light_purple><bold>Bundle Rewards</bold></light_purple>");
        
        // Fill background
        ItemStack background = createPane(GuiUtil.material(plugin, "gui.theme.background", Material.BLACK_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, background);
        }
        
        // Create decorative border
        ItemStack borderPane = createPane(GuiUtil.material(plugin, "gui.theme.secondary", Material.LIME_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, borderPane);
            inventory.setItem(i + 45, borderPane);
        }
        for (int i = 9; i < 45; i += 9) {
            inventory.setItem(i, borderPane);
            inventory.setItem(i + 8, borderPane);
        }
        
        // Title
        ItemStack title = createItem(Material.CHEST, "§d§lBundle Preview", 
            "§7All possible rewards in bundles");
        inventory.setItem(4, title);
        
        // Current reward (highlighted)
        Optional<BundleReward> currentReward = bundleManager.getCurrentReward(player);
        long timeUntilRefresh = bundleManager.getTimeUntilRewardRefresh(player);
        ItemStack nextReward = currentReward
            .map(reward -> createItem(reward.getDisplayMaterial(),
                "§6§l⭐ NEXT REWARD ⭐",
                "§7This is your next reward!",
                "",
                "§e" + reward.getDisplayName(),
                "",
                "§7Refreshes: §e" + formatTime(timeUntilRefresh),
                "",
                "§a§l» Click to open a bundle!"))
            .orElseGet(() -> createItem(Material.BARRIER, "§c§lNo Rewards Available",
                "§7No configured bundle rewards are",
                "§7available with the current integrations."));
        inventory.setItem(GuiUtil.slot(plugin, "bundle-rewards", "next-reward", 13, 54), nextReward);
        
        // All possible rewards
        List<BundleReward> allRewards = bundleManager.getAllPossibleRewards();
        int[] rewardSlots = {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        
        for (int i = 0; i < Math.min(allRewards.size(), rewardSlots.length); i++) {
            BundleReward reward = allRewards.get(i);
            ItemStack rewardItem = createItem(reward.getDisplayMaterial(), 
                "§e" + reward.getType().replace("_", " ").replace("ITEM ", ""), 
                "§7Possible reward",
                "",
                "§7Amount: §e" + reward.getMinAmount() + " - " + reward.getMaxAmount());
            inventory.setItem(rewardSlots[i], rewardItem);
        }
        
        // Open bundle button (slot 40)
        int availableBundles = bundleManager.getAvailableBundles(player);
        ItemStack openBundle;
        
        if (availableBundles > 0) {
            openBundle = createItem(Material.LIME_DYE, "§a§l✔ Open Bundle",
                "§7Click to open a bundle!",
                "",
                "§aYou have §e" + availableBundles + "§a bundle(s) available");
        } else {
            long timeUntilReset = bundleManager.getTimeUntilReset(player);
            openBundle = createItem(Material.RED_DYE, "§c§l✖ No Bundles Available",
                "§7You don't have any bundles!",
                "",
                "§7Next reset: §e" + formatTime(timeUntilReset));
        }
        inventory.setItem(GuiUtil.slot(plugin, "bundle-rewards", "open", 40, 54), openBundle);
        
        // Back button (slot 48)
        ItemStack back = createItem(Material.ARROW, "§e§lBack", 
            "§7Return to bundle shop");
        inventory.setItem(GuiUtil.slot(plugin, "bundle-rewards", "back", 48, 54), back);
        
        // Close button (slot 49)
        ItemStack close = createItem(Material.BARRIER, "§c§lClose", 
            "§7Click to close");
        inventory.setItem(GuiUtil.slot(plugin, "bundle-rewards", "close", 49, 54), close);
        
        player.openInventory(inventory);
    }
    
    /**
     * Create an available bundle item
     */
    private ItemStack createAvailableBundle(int bundleNumber) {
        return createItem(Material.CHEST, "§a§lBundle #" + bundleNumber, 
            "§7Click to view rewards!",
            "",
            "§a✔ Available to claim",
            "",
            "§e» Click to open!");
    }
    
    /**
     * Create a claimed bundle item
     */
    private ItemStack createClaimedBundle(int bundleNumber) {
        long timeUntilReset = bundleManager.getTimeUntilReset(player);
        
        ItemStack item = createItem(Material.GRAY_DYE, "§7§lBundle #" + bundleNumber,
            "§7This bundle has been claimed",
            "",
            "§c✖ Already claimed",
            "",
            "§7Next reset: §e" + formatTime(timeUntilReset));
        return item;
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
    
    /**
     * Create a glass pane item
     */
    private ItemStack createPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Create a custom item with name and lore
     */
    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(name);
            
            if (lore.length > 0) {
                List<String> loreList = new ArrayList<>();
                for (String line : lore) {
                    loreList.add(line);
                }
                meta.setLore(loreList);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
}
