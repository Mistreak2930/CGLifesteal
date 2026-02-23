package com.example.lifesteal.system;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manages the daily bundle system
 * Players get 3 daily bundles per day by claiming every 24 hours
 * Each bundle contains multiple rewards that refresh every 12 hours
 */
public class BundleManager {
    
    private LifestealPlugin plugin;
    private File bundleFile;
    private FileConfiguration bundleData;
    
    // Bundle configuration
    private int maxBundlesPerDay = 3;
    private long bundleResetTime = 24 * 60 * 60 * 1000L; // 24 hours in milliseconds
    private long rewardRefreshTime = 12 * 60 * 60 * 1000L; // 12 hours in milliseconds
    
    public BundleManager(LifestealPlugin plugin) {
        this.plugin = plugin;
        this.bundleFile = new File(plugin.getDataFolder(), "bundles.yml");
        loadBundleData();
        
        // Load configuration
        this.maxBundlesPerDay = plugin.getConfig().getInt("bundles.max-per-day", 3);
        this.bundleResetTime = plugin.getConfig().getLong("bundles.reset-time-hours", 24) * 60 * 60 * 1000L;
        this.rewardRefreshTime = plugin.getConfig().getLong("bundles.reward-refresh-hours", 12) * 60 * 60 * 1000L;
    }
    
    /**
     * Load bundle data from file
     */
    private void loadBundleData() {
        if (!bundleFile.exists()) {
            try {
                bundleFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create bundles.yml: " + e.getMessage());
            }
        }
        
        bundleData = YamlConfiguration.loadConfiguration(bundleFile);
    }
    
    /**
     * Save bundle data to file
     */
    public void saveBundleData() {
        try {
            bundleData.save(bundleFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save bundle data: " + e.getMessage());
        }
    }
    
    /**
     * Get the number of available bundles for a player
     */
    public int getAvailableBundles(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        
        long lastReset = bundleData.getLong(path + ".last-reset", 0);
        long now = System.currentTimeMillis();
        
        // Check if we need to reset daily bundles
        if (now - lastReset >= bundleResetTime) {
            resetDailyBundles(player);
            return maxBundlesPerDay;
        }
        
        int claimedToday = bundleData.getInt(path + ".claimed-today", 0);
        return Math.max(0, maxBundlesPerDay - claimedToday);
    }
    
    /**
     * Get the total number of bundles claimed by a player
     */
    public int getTotalBundlesClaimed(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        return bundleData.getInt(path + ".total-claimed", 0);
    }
    
    /**
     * Reset daily bundles for a player
     */
    private void resetDailyBundles(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        
        bundleData.set(path + ".last-reset", System.currentTimeMillis());
        bundleData.set(path + ".claimed-today", 0);
        saveBundleData();
    }
    
    /**
     * Check if player can claim a bundle
     */
    public boolean canClaimBundle(Player player) {
        return getAvailableBundles(player) > 0;
    }
    
    /**
     * Get time until next bundle reset (in seconds)
     */
    public long getTimeUntilReset(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        
        long lastReset = bundleData.getLong(path + ".last-reset", 0);
        long now = System.currentTimeMillis();
        long timeSinceReset = now - lastReset;
        long timeUntilReset = bundleResetTime - timeSinceReset;
        
        return Math.max(0, timeUntilReset / 1000);
    }
    
    /**
     * Get time until next reward refresh (in seconds)
     */
    public long getTimeUntilRewardRefresh(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        
        long lastRefresh = bundleData.getLong(path + ".last-reward-refresh", 0);
        long now = System.currentTimeMillis();
        long timeSinceRefresh = now - lastRefresh;
        long timeUntilRefresh = rewardRefreshTime - timeSinceRefresh;
        
        return Math.max(0, timeUntilRefresh / 1000);
    }
    
    /**
     * Get the current reward for a player
     * This reward changes every 12 hours or when opened
     */
    public BundleReward getCurrentReward(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        
        long lastRefresh = bundleData.getLong(path + ".last-reward-refresh", 0);
        long now = System.currentTimeMillis();
        
        // Check if we need to refresh the reward
        if (now - lastRefresh >= rewardRefreshTime || lastRefresh == 0) {
            refreshCurrentReward(player);
        }
        
        // Get the stored reward
        String rewardType = bundleData.getString(path + ".current-reward.type", "MONEY");
        int rewardAmount = bundleData.getInt(path + ".current-reward.amount", 100);
        
        return new BundleReward(rewardType, rewardAmount);
    }
    
    /**
     * Refresh the current reward for a player
     */
    private void refreshCurrentReward(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        
        BundleReward newReward = generateRandomReward();
        
        bundleData.set(path + ".current-reward.type", newReward.getType());
        bundleData.set(path + ".current-reward.amount", newReward.getAmount());
        bundleData.set(path + ".last-reward-refresh", System.currentTimeMillis());
        saveBundleData();
    }
    
    /**
     * Get all possible rewards in the bundle
     */
    public List<BundleReward> getAllPossibleRewards() {
        List<BundleReward> rewards = new ArrayList<>();
        
        // Load from config or use defaults
        if (plugin.getConfig().contains("bundles.possible-rewards")) {
            for (String key : plugin.getConfig().getConfigurationSection("bundles.possible-rewards").getKeys(false)) {
                String path = "bundles.possible-rewards." + key;
                String type = plugin.getConfig().getString(path + ".type");
                int minAmount = plugin.getConfig().getInt(path + ".min-amount");
                int maxAmount = plugin.getConfig().getInt(path + ".max-amount");
                
                rewards.add(new BundleReward(type, minAmount, maxAmount));
            }
        } else {
            // Default rewards
            rewards.add(new BundleReward("MONEY", 100, 500));
            rewards.add(new BundleReward("POINTS", 50, 250));
            rewards.add(new BundleReward("HEARTS", 1, 3));
            rewards.add(new BundleReward("ORBITS", 10, 50));
            rewards.add(new BundleReward("ITEM_DIAMOND", 1, 5));
            rewards.add(new BundleReward("ITEM_EMERALD", 1, 10));
        }
        
        return rewards;
    }
    
    /**
     * Generate a random reward from possible rewards
     */
    private BundleReward generateRandomReward() {
        List<BundleReward> possibleRewards = getAllPossibleRewards();
        Random random = new Random();
        
        BundleReward template = possibleRewards.get(random.nextInt(possibleRewards.size()));
        int amount = template.getMinAmount() + random.nextInt(template.getMaxAmount() - template.getMinAmount() + 1);
        
        return new BundleReward(template.getType(), amount);
    }
    
    /**
     * Open a bundle and claim the current reward
     */
    public boolean openBundle(Player player) {
        if (!canClaimBundle(player)) {
            return false;
        }
        
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        
        // Get current reward
        BundleReward reward = getCurrentReward(player);
        
        // Give reward to player
        giveReward(player, reward);
        
        // Update stats
        int claimedToday = bundleData.getInt(path + ".claimed-today", 0);
        int totalClaimed = bundleData.getInt(path + ".total-claimed", 0);
        
        bundleData.set(path + ".claimed-today", claimedToday + 1);
        bundleData.set(path + ".total-claimed", totalClaimed + 1);
        
        // Refresh the reward for next time
        refreshCurrentReward(player);
        
        saveBundleData();
        
        return true;
    }
    
    /**
     * Give a reward to a player
     */
    private void giveReward(Player player, BundleReward reward) {
        String type = reward.getType();
        int amount = reward.getAmount();
        
        switch (type) {
            case "MONEY":
                if (plugin.hasEconomy()) {
                    plugin.getEconomy().depositPlayer(player, amount);
                    player.sendMessage("§a§l✔ §aYou received §e$" + amount + "§a!");
                }
                break;
                
            case "POINTS":
                if (plugin.hasPlayerPoints()) {
                    try {
                        Object api = plugin.getPlayerPointsAPI();
                        api.getClass().getMethod("give", UUID.class, int.class).invoke(api, player.getUniqueId(), amount);
                        player.sendMessage("§a§l✔ §aYou received §e" + amount + " Points§a!");
                    } catch (Exception e) {
                        plugin.getLogger().warning("Failed to give points: " + e.getMessage());
                    }
                }
                break;
                
            case "HEARTS":
                plugin.getHeartManager().addHearts(player, amount);
                player.sendMessage("§a§l✔ §aYou received §c" + amount + " Heart(s)§a!");
                break;
                
            case "ORBITS":
                plugin.getOrbitsManager().addOrbits(player.getUniqueId(), amount);
                player.sendMessage("§a§l✔ §aYou received §d" + amount + " Orbit(s)§a!");
                break;
                
            case "ITEM_DIAMOND":
                player.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.DIAMOND, amount));
                player.sendMessage("§a§l✔ §aYou received §b" + amount + " Diamond(s)§a!");
                break;
                
            case "ITEM_EMERALD":
                player.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.EMERALD, amount));
                player.sendMessage("§a§l✔ §aYou received §2" + amount + " Emerald(s)§a!");
                break;
                
            case "ITEM_IRON_INGOT":
                player.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.IRON_INGOT, amount));
                player.sendMessage("§a§l✔ §aYou received §f" + amount + " Iron Ingot(s)§a!");
                break;
                
            case "ITEM_GOLD_INGOT":
                player.getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.GOLD_INGOT, amount));
                player.sendMessage("§a§l✔ §aYou received §6" + amount + " Gold Ingot(s)§a!");
                break;
        }
    }
    
    /**
     * Inner class to represent a bundle reward
     */
    public static class BundleReward {
        private String type;
        private int amount;
        private int minAmount;
        private int maxAmount;
        
        public BundleReward(String type, int amount) {
            this.type = type;
            this.amount = amount;
            this.minAmount = amount;
            this.maxAmount = amount;
        }
        
        public BundleReward(String type, int minAmount, int maxAmount) {
            this.type = type;
            this.amount = (minAmount + maxAmount) / 2; // Average for display
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
        }
        
        public String type() {
            return type;
        }
        
        public int getAmount() {
            return amount;
        }
        
        public int getMinAmount() {
            return minAmount;
        }
        
        public int getMaxAmount() {
            return maxAmount;
        }
        
        public String getType() {
            return type;
        }
        
        public String getDisplayName() {
            switch (type) {
                case "MONEY": return "§e$" + amount;
                case "POINTS": return "§e" + amount + " Points";
                case "HEARTS": return "§c" + amount + " Heart(s)";
                case "ORBITS": return "§d" + amount + " Orbit(s)";
                case "ITEM_DIAMOND": return "§b" + amount + " Diamond(s)";
                case "ITEM_EMERALD": return "§2" + amount + " Emerald(s)";
                case "ITEM_IRON_INGOT": return "§f" + amount + " Iron Ingot(s)";
                case "ITEM_GOLD_INGOT": return "§6" + amount + " Gold Ingot(s)";
                default: return "§7Unknown Reward";
            }
        }
        
        public Material getDisplayMaterial() {
            switch (type) {
                case "MONEY": return Material.GOLD_INGOT;
                case "POINTS": return Material.EXP_BOTTLE;
                case "HEARTS": return Material.INK_SACK; // Will need durability for red
                case "ORBITS": return Material.ENDER_PEARL;
                case "ITEM_DIAMOND": return Material.DIAMOND;
                case "ITEM_EMERALD": return Material.EMERALD;
                case "ITEM_IRON_INGOT": return Material.IRON_INGOT;
                case "ITEM_GOLD_INGOT": return Material.GOLD_INGOT;
                default: return Material.BARRIER;
            }
        }
    }
}
