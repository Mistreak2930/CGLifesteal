package com.example.lifesteal.gui;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.DailyRewardManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DailyRewardGUI {
    
    private LifestealPlugin plugin;
    private DailyRewardManager dailyRewardManager;
    private Player player;
    
    public DailyRewardGUI(LifestealPlugin plugin, DailyRewardManager dailyRewardManager, Player player) {
        this.plugin = plugin;
        this.dailyRewardManager = dailyRewardManager;
        this.player = player;
    }
    
    public void open() {
        Inventory inventory = GuiUtil.createInventory(plugin, "daily-rewards", 27, "<gold><bold>Daily Rewards</bold></gold>");
        
        // Background
        ItemStack background = createPane(GuiUtil.material(plugin, "gui.theme.background", Material.BLACK_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, background);
        }
        
        // Top border
        ItemStack topBorder = createPane(GuiUtil.material(plugin, "gui.theme.accent", Material.RED_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, topBorder);
        }
        
        // Bottom border
        ItemStack bottomBorder = createPane(GuiUtil.material(plugin, "gui.theme.secondary", Material.GRAY_STAINED_GLASS_PANE), " ");
        for (int i = 18; i < 27; i++) {
            inventory.setItem(i, bottomBorder);
        }
        
        // Left border
        for (int i = 9; i < 18; i += 9) {
            inventory.setItem(i, createPane(GuiUtil.material(plugin, "gui.theme.secondary", Material.GRAY_STAINED_GLASS_PANE), " "));
        }
        
        // Right border
        for (int i = 17; i < 18; i += 9) {
            inventory.setItem(i, createPane(GuiUtil.material(plugin, "gui.theme.secondary", Material.GRAY_STAINED_GLASS_PANE), " "));
        }
        
        // Title
        ItemStack title = createTitleItem();
        inventory.setItem(4, title);
        
        // Money reward display
        ItemStack moneyReward = createMoneyRewardDisplay();
        inventory.setItem(10, moneyReward);
        
        // Claim button or cooldown
        ItemStack claimButton = createClaimButton();
        inventory.setItem(GuiUtil.slot(plugin, "daily-rewards", "claim", 13, 27), claimButton);
        
        // Points reward display
        ItemStack pointsReward = createPointsRewardDisplay();
        inventory.setItem(16, pointsReward);
        
        // Stats section
        ItemStack stayStats = createStats();
        inventory.setItem(20, stayStats);
        
        // Streak/Total rewards
        ItemStack totalRewards = createTotalRewards();
        inventory.setItem(24, totalRewards);
        
        player.openInventory(inventory);
    }
    
    private ItemStack createTitleItem() {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§l✦ DAILY REWARDS ✦");
            List<String> lore = new ArrayList<>();
            lore.add("§e═════════════════════");
            lore.add("§aGet your daily dosage");
            lore.add("§aof rewards!");
            lore.add(" ");
            lore.add("§eAvailable: §cDaily");
            lore.add("§e═════════════════════");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createMoneyRewardDisplay() {
        ItemStack item = new ItemStack(Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§l💰 Money Reward");
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§aDaily Money:");
            lore.add("§e+ $" + dailyRewardManager.getMoneyReward());
            lore.add(" ");
            lore.add("§7Claim for daily bonus!");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createPointsRewardDisplay() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b§l⭐ Points Reward");
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§aDaily Points:");
            lore.add("§e+ " + dailyRewardManager.getPointsReward() + " PlayerPoints");
            lore.add(" ");
            lore.add("§7Claim for daily bonus!");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createClaimButton() {
        ItemStack item;
        ItemMeta meta;
        
        if (dailyRewardManager.canClaimReward(player)) {
            item = new ItemStack(Material.EMERALD_BLOCK);
            meta = item.getItemMeta();
            
            if (meta != null) {
                meta.setDisplayName("§a§l✓ CLAIM REWARD");
                List<String> lore = new ArrayList<>();
                lore.add(" ");
                lore.add("§aAvailable to claim!");
                lore.add(" ");
                lore.add("§eDaily reward is ready!");
                lore.add("§eClick to claim");
                lore.add(" ");
                lore.add("§eReward: §a$" + dailyRewardManager.getMoneyReward() + " §7+ §b" + dailyRewardManager.getPointsReward() + " pts");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        } else {
            item = new ItemStack(Material.REDSTONE_BLOCK);
            meta = item.getItemMeta();
            
            long secondsRemaining = dailyRewardManager.getTimeUntilNextReward(player);
            long hours = secondsRemaining / 3600;
            long minutes = (secondsRemaining % 3600) / 60;
            long seconds = secondsRemaining % 60;
            
            if (meta != null) {
                meta.setDisplayName("§c§l✗ CLAIMED");
                List<String> lore = new ArrayList<>();
                lore.add(" ");
                lore.add("§cAlready claimed today!");
                lore.add(" ");
                lore.add("§eNext reward in:");
                lore.add("§a" + hours + "h " + minutes + "m " + seconds + "s");
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
        
        return item;
    }
    
    private ItemStack createStats() {
        ItemStack item = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b§l★ Your Stats ★");
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§ePlayer: §a" + player.getName());
            lore.add("§ePoints: §a" + dailyRewardManager.getPlayerPoints(player));
            lore.add(" ");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createTotalRewards() {
        ItemStack item = new ItemStack(Material.APPLE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§d§l🎁 Total Rewards");
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§eTotal Claimed:");
            lore.add("§a" + dailyRewardManager.getTotalClaims(player) + " times");
            lore.add(" ");
            lore.add("§eThat's §e" + (dailyRewardManager.getTotalClaims(player) * 
                (dailyRewardManager.getMoneyReward() + dailyRewardManager.getPointsReward())) + " total points!");
            lore.add(" ");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createPane(Material material, String name) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            pane.setItemMeta(meta);
        }
        return pane;
    }
}
