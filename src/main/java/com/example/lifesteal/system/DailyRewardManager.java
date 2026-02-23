package com.example.lifesteal.system;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DailyRewardManager {
    
    private LifestealPlugin plugin;
    private File dailyRewardFile;
    private FileConfiguration dailyRewardData;
    private int moneyReward = 100;
    private int pointsReward = 100;
    
    public DailyRewardManager(LifestealPlugin plugin) {
        this.plugin = plugin;
        this.dailyRewardFile = new File(plugin.getDataFolder(), "dailyrewards.yml");
        loadDailyRewardData();
        
        // Load amounts from config
        this.moneyReward = plugin.getConfig().getInt("daily-reward.money", 100);
        this.pointsReward = plugin.getConfig().getInt("daily-reward.points", 100);
    }
    
    /**
     * Load daily reward data from file
     */
    private void loadDailyRewardData() {
        if (!dailyRewardFile.exists()) {
            try {
                dailyRewardFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create dailyrewards.yml: " + e.getMessage());
            }
        }
        
        dailyRewardData = YamlConfiguration.loadConfiguration(dailyRewardFile);
    }
    
    /**
     * Save daily reward data to file
     */
    public void saveDailyRewardData() {
        try {
            dailyRewardData.save(dailyRewardFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save daily reward data: " + e.getMessage());
        }
    }
    
    /**
     * Check if player can claim their daily reward
     */
    public boolean canClaimReward(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        
        long lastClaim = dailyRewardData.getLong(path + ".last-claim", 0);
        long now = System.currentTimeMillis();
        long timeDifference = now - lastClaim;
        
        // 24 hours in milliseconds
        long twentyFourHours = 24 * 60 * 60 * 1000L;
        
        return timeDifference >= twentyFourHours;
    }
    
    /**
     * Get time remaining until next reward (in seconds)
     */
    public long getTimeUntilNextReward(Player player) {
        UUID uuid = player.getUniqueId();
        String path = "players." + uuid;
        
        long lastClaim = dailyRewardData.getLong(path + ".last-claim", 0);
        long now = System.currentTimeMillis();
        long timeDifference = now - lastClaim;
        
        long twentyFourHours = 24 * 60 * 60 * 1000L;
        long remaining = twentyFourHours - timeDifference;
        
        return Math.max(0, remaining / 1000); // Convert to seconds
    }
    
    /**
     * Claim daily reward for a player
     */
    public boolean claimReward(Player player) {
        if (!canClaimReward(player)) {
            long timeRemaining = getTimeUntilNextReward(player);
            long hours = timeRemaining / 3600;
            long minutes = (timeRemaining % 3600) / 60;
            
            player.sendMessage("§8[§cLifesteal§8] §cYou've already claimed your daily reward!");
            player.sendMessage("§8[§cLifesteal§8] §eNext reward in: §a" + hours + "h " + minutes + "m");
            return false;
        }
        
        // Award money (through Vault)
        boolean moneyAward = awardMoney(player, moneyReward);
        
        // Award points (through PlayerPoints)
        boolean pointsAward = awardPlayerPoints(player, pointsReward);
        
        if (moneyAward && pointsAward) {
            // Update last claim time
            UUID uuid = player.getUniqueId();
            dailyRewardData.set("players." + uuid + ".last-claim", System.currentTimeMillis());
            dailyRewardData.set("players." + uuid + ".total-claims", 
                dailyRewardData.getInt("players." + uuid + ".total-claims", 0) + 1);
            saveDailyRewardData();
            
            // Send success messages
            player.sendMessage("§8[§cLifesteal§8] §a§l✓ Daily Reward Claimed!");
            player.sendMessage("§8[§cLifesteal§8] §e+ $" + moneyReward + " Money");
            player.sendMessage("§8[§cLifesteal§8] §e+ " + pointsReward + " Player Points");
            player.sendMessage("§8[§cLifesteal§8] §aTotal rewards claimed: §e" + 
                dailyRewardData.getInt("players." + uuid + ".total-claims"));
            
            return true;
        } else if (!moneyAward && !pointsAward) {
            player.sendMessage("§8[§cLifesteal§8] §cReward system unavailable!");
            return false;
        } else if (!moneyAward) {
            player.sendMessage("§8[§cLifesteal§8] §cVault Economy not available!");
            return false;
        } else {
            player.sendMessage("§8[§cLifesteal§8] §cPlayerPoints plugin not available!");
            return false;
        }
    }
    
    /**
     * Award player money (using Vault)
     */
    private boolean awardMoney(Player player, int amount) {
        try {
            if (plugin.getEconomy() == null) {
                plugin.getLogger().warning("Vault Economy is null when trying to award money!");
                return false;
            }
            
            return plugin.getEconomy().depositPlayer(player, amount).transactionSuccess();
        } catch (Exception e) {
            plugin.getLogger().warning("Error awarding money: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Award player points
     */
    private boolean awardPlayerPoints(Player player, int amount) {
        try {
            if (plugin.getPlayerPointsAPI() == null) {
                return false;
            }
            
            Object api = plugin.getPlayerPointsAPI();
            Class<?> apiClass = api.getClass();
            
            // Use the give method: boolean give(UUID uuid, int amount)
            Boolean result = (Boolean) apiClass.getMethod("give", UUID.class, int.class)
                .invoke(api, player.getUniqueId(), amount);
            
            return result != null && result;
        } catch (Exception e) {
            plugin.getLogger().warning("Error awarding points: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get player's current points balance
     */
    public int getPlayerPoints(Player player) {
        try {
            if (plugin.getPlayerPointsAPI() == null) return 0;
            
            Object api = plugin.getPlayerPointsAPI();
            Class<?> apiClass = api.getClass();
            
            // Use the look method: int look(UUID uuid)
            Integer balance = (Integer) apiClass.getMethod("look", UUID.class)
                .invoke(api, player.getUniqueId());
            
            return balance != null ? balance : 0;
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting balance for " + player.getName() + ": " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Get money reward amount
     */
    public int getMoneyReward() {
        return moneyReward;
    }
    
    /**
     * Get points reward amount
     */
    public int getPointsReward() {
        return pointsReward;
    }
    
    /**
     * Get total claims for a player
     */
    public int getTotalClaims(Player player) {
        UUID uuid = player.getUniqueId();
        return dailyRewardData.getInt("players." + uuid + ".total-claims", 0);
    }
}
