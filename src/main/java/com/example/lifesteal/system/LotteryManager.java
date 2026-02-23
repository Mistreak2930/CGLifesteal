package com.example.lifesteal.system;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class LotteryManager {
    
    private LifestealPlugin plugin;
    private File lotteryFile;
    private FileConfiguration lotteryData;
    private double lotteryPool = 0;
    private Set<UUID> lotteryTickets = new HashSet<>();
    private BukkitTask lotteryTask;
    private int ticketPrice = 100;
    
    public LotteryManager(LifestealPlugin plugin) {
        this.plugin = plugin;
        this.lotteryFile = new File(plugin.getDataFolder(), "lottery.yml");
        loadLotteryData();
        setupLotteryTask();
    }
    
    /**
     * Load lottery data from file
     */
    private void loadLotteryData() {
        if (!lotteryFile.exists()) {
            try {
                lotteryFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create lottery.yml: " + e.getMessage());
            }
        }
        
        lotteryData = YamlConfiguration.loadConfiguration(lotteryFile);
        lotteryPool = lotteryData.getDouble("pool", 0);
        ticketPrice = plugin.getConfig().getInt("lottery.ticket-price", 100);
        
        // Load tickets
        List<String> ticketUUIDs = lotteryData.getStringList("tickets");
        for (String uuid : ticketUUIDs) {
            try {
                lotteryTickets.add(UUID.fromString(uuid));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in lottery tickets: " + uuid);
            }
        }
    }
    
    /**
     * Save lottery data to file
     */
    public void saveLotteryData() {
        try {
            lotteryData.set("pool", lotteryPool);
            
            List<String> ticketUUIDs = new ArrayList<>();
            for (UUID uuid : lotteryTickets) {
                ticketUUIDs.add(uuid.toString());
            }
            lotteryData.set("tickets", ticketUUIDs);
            
            lotteryData.save(lotteryFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save lottery data: " + e.getMessage());
        }
    }
    
    /**
     * Setup the hourly lottery task
     */
    private void setupLotteryTask() {
        // Run lottery every hour (3600 seconds = 72000 ticks)
        lotteryTask = Bukkit.getScheduler().runTaskTimer(plugin, this::drawLottery, 72000L, 72000L);
    }
    
    /**
     * Buy a lottery ticket
     */
    public boolean buyTicket(Player player) {
        if (lotteryTickets.contains(player.getUniqueId())) {
            player.sendMessage("§8[§cLifesteal§8] §cYou already have a ticket in this draw!");
            return false;
        }
        
        // Check if player has enough money
        double playerBalance = getPlayerBalance(player);
        if (playerBalance < ticketPrice) {
            player.sendMessage("§8[§cLifesteal§8] §cYou don't have enough money! §eYou have: $" + String.format("%.2f", playerBalance));
            player.sendMessage("§8[§cLifesteal§8] §ePrice: $" + ticketPrice);
            return false;
        }
        
        // Withdraw money from player
        if (!withdrawMoney(player, ticketPrice)) {
            player.sendMessage("§8[§cLifesteal§8] §cFailed to process payment!");
            return false;
        }
        
        lotteryTickets.add(player.getUniqueId());
        lotteryPool += ticketPrice;
        saveLotteryData();
        
        player.sendMessage("§8[§cLifesteal§8] §a✓ Lottery ticket purchased!");
        player.sendMessage("§8[§cLifesteal§8] §ePool: §a$" + formatMoney(lotteryPool));
        player.sendMessage("§8[§cLifesteal§8] §eTickets in draw: §a" + lotteryTickets.size());
        
        return true;
    }
    
    /**
     * Draw the lottery and pick a winner
     */
    private void drawLottery() {
        if (lotteryTickets.isEmpty()) {
            Bukkit.broadcastMessage("§8[§cLifesteal§8] §c⚠ No lottery tickets sold this hour!");
            return;
        }
        
        // Pick random winner
        List<UUID> ticketList = new ArrayList<>(lotteryTickets);
        UUID winnerUUID = ticketList.get(new Random().nextInt(ticketList.size()));
        Player winner = Bukkit.getPlayer(winnerUUID);
        
        String winnerName = "Unknown";
        if (winner != null) {
            winnerName = winner.getName();
        } else {
            // Try to get name from offline player data
            winnerName = Bukkit.getOfflinePlayer(winnerUUID).getName();
        }
        
        // Award winner
        if (winner != null) {
            depositMoney(winner, (int) lotteryPool);
            winner.sendMessage("§8[§cLifesteal§8] §6§l★ YOU WON THE LOTTERY! ★");
            winner.sendMessage("§8[§cLifesteal§8] §aYou received §e$" + formatMoney(lotteryPool) + "§a!");
        }
        
        // Broadcast result
        Bukkit.broadcastMessage("§8[§cLifesteal§8] §6§l★ LOTTERY DRAW ★");
        Bukkit.broadcastMessage("§8[§cLifesteal§8] §aWinner: §6" + winnerName);
        Bukkit.broadcastMessage("§8[§cLifesteal§8] §aPrize: §e$" + formatMoney(lotteryPool));
        Bukkit.broadcastMessage("§8[§cLifesteal§8] §aNext draw in §e1 hour§a!");
        
        // Reset lottery
        lotteryTickets.clear();
        lotteryPool = 0;
        saveLotteryData();
    }
    
    /**
     * Get the current lottery pool amount
     */
    public double getLotteryPool() {
        return lotteryPool;
    }
    
    /**
     * Get number of tickets sold
     */
    public int getTicketCount() {
        return lotteryTickets.size();
    }
    
    /**
     * Check if player has a ticket
     */
    public boolean hasTicket(UUID uuid) {
        return lotteryTickets.contains(uuid);
    }
    
    /**
     * Get ticket price
     */
    public int getTicketPrice() {
        return ticketPrice;
    }
    
    /**
     * Charge player money (using Vault)
     */
    private boolean withdrawMoney(Player player, int amount) {
        try {
            if (plugin.getEconomy() == null) {
                plugin.getLogger().warning("Vault Economy is null!");
                return false;
            }
            
            double balance = plugin.getEconomy().getBalance(player);
            if (balance < amount) {
                return false;
            }
            
            return plugin.getEconomy().withdrawPlayer(player, amount).transactionSuccess();
        } catch (Exception e) {
            plugin.getLogger().warning("Error withdrawing money from " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Award player money (using Vault)
     */
    private void depositMoney(Player player, int amount) {
        try {
            if (plugin.getEconomy() == null) {
                plugin.getLogger().warning("Vault Economy is null when trying to deposit!");
                return;
            }
            
            plugin.getEconomy().depositPlayer(player, amount);
        } catch (Exception e) {
            plugin.getLogger().warning("Error depositing money to " + player.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get player's current money balance (using Vault)
     */
    public double getPlayerBalance(Player player) {
        try {
            if (plugin.getEconomy() == null) return 0.0;
            
            return plugin.getEconomy().getBalance(player);
        } catch (Exception e) {
            plugin.getLogger().warning("Error getting balance for " + player.getName() + ": " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Charge player points (old method - for reference)
     */
    private boolean chargePlayerPoints(Player player, int amount) {
        try {
            if (plugin.getPlayerPointsAPI() == null) {
                player.sendMessage("§8[§cLifesteal§8] §cPlayerPoints not available!");
                return false;
            }
            
            Object api = plugin.getPlayerPointsAPI();
            Class<?> apiClass = api.getClass();
            
            // Check balance first
            Integer balance = (Integer) apiClass.getMethod("look", UUID.class)
                .invoke(api, player.getUniqueId());
            
            if (balance == null || balance < amount) {
                return false;
            }
            
            // Take the points
            Boolean result = (Boolean) apiClass.getMethod("take", UUID.class, int.class)
                .invoke(api, player.getUniqueId(), amount);
            
            return result != null && result;
        } catch (Exception e) {
            plugin.getLogger().warning("Error charging points: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Award player points (old method - for reference)
     */
    private void awardPlayerPoints(Player player, int amount) {
        try {
            if (plugin.getPlayerPointsAPI() == null) return;
            
            Object api = plugin.getPlayerPointsAPI();
            Class<?> apiClass = api.getClass();
            
            // Give points
            apiClass.getMethod("give", UUID.class, int.class)
                .invoke(api, player.getUniqueId(), amount);
        } catch (Exception e) {
            plugin.getLogger().warning("Error awarding points: " + e.getMessage());
        }
    }
    
    /**
     * Get player's current points balance (old method - for reference)
     */
    public int getPlayerPoints(Player player) {
        try {
            if (plugin.getPlayerPointsAPI() == null) return 0;
            
            Object api = plugin.getPlayerPointsAPI();
            Class<?> apiClass = api.getClass();
            
            Integer balance = (Integer) apiClass.getMethod("look", UUID.class)
                .invoke(api, player.getUniqueId());
            
            return balance != null ? balance : 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Format money value with proper abbreviations
     */
    private String formatMoney(double amount) {
        if (amount >= 1_000_000) {
            return String.format("%.1fM", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.1fK", amount / 1_000);
        } else {
            return String.format("%.0f", amount);
        }
    }
    
    /**
     * Cleanup and disable
     */
    public void disable() {
        saveLotteryData();
        if (lotteryTask != null) {
            lotteryTask.cancel();
        }
    }
}
