package com.example.lifesteal.system;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeartManager {

    private JavaPlugin plugin;
    private File heartsFile;
    private FileConfiguration heartsConfig;
    private Map<UUID, Integer> heartsCache;
    private static final int MAX_HEARTS = 20;
    private static final int MIN_HEARTS = 0;
    private static final int DEFAULT_HEARTS = 20;

    public HeartManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.heartsCache = new HashMap<>();
        initializeHeartsFile();
        loadHearts();
    }

    private void initializeHeartsFile() {
        heartsFile = new File(plugin.getDataFolder(), "hearts.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        if (!heartsFile.exists()) {
            try {
                heartsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create hearts.yml: " + e.getMessage());
            }
        }
        heartsConfig = YamlConfiguration.loadConfiguration(heartsFile);
    }

    public void loadHearts() {
        heartsCache.clear();
        for (String key : heartsConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int hearts = heartsConfig.getInt(key, DEFAULT_HEARTS);
                heartsCache.put(uuid, Math.max(MIN_HEARTS, Math.min(MAX_HEARTS, hearts)));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in hearts.yml: " + key);
            }
        }
    }

    public void saveHearts() {
        heartsConfig = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : heartsCache.entrySet()) {
            heartsConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            heartsConfig.save(heartsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save hearts.yml: " + e.getMessage());
        }
    }

    public int getHearts(UUID uuid) {
        return heartsCache.getOrDefault(uuid, DEFAULT_HEARTS);
    }

    public int getHearts(Player player) {
        return getHearts(player.getUniqueId());
    }

    public void setHearts(UUID uuid, int hearts) {
        int clamped = Math.max(MIN_HEARTS, Math.min(MAX_HEARTS, hearts));
        heartsCache.put(uuid, clamped);
        
        plugin.getLogger().info("Set hearts for " + uuid + " to " + clamped);
        
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            updatePlayerHealth(player, clamped);
            player.sendMessage("§6[Lifesteal] §aYour hearts: §e" + clamped);
        }
        
        heartsConfig.set(uuid.toString(), clamped);
        saveHearts();
    }

    public void addHearts(UUID uuid, int amount) {
        int current = getHearts(uuid);
        int newAmount = current + amount;
        setHearts(uuid, newAmount);
        plugin.getLogger().info("Added " + amount + " hearts to " + uuid + ". New: " + newAmount);
    }

    public void removeHearts(UUID uuid, int amount) {
        int current = getHearts(uuid);
        int newAmount = current - amount;
        setHearts(uuid, newAmount);
        plugin.getLogger().info("Removed " + amount + " hearts from " + uuid + ". New: " + newAmount);
    }

    public void addHearts(Player player, int amount) {
        addHearts(player.getUniqueId(), amount);
    }

    public void removeHearts(Player player, int amount) {
        removeHearts(player.getUniqueId(), amount);
    }

    private void updatePlayerHealth(Player player, int hearts) {
        try {
            // If player has zero or less hearts, set health to 0 (dead)
            if (hearts <= 0) {
                player.setHealth(0.0);
                plugin.getLogger().info(player.getName() + " has 0 hearts and was set to 0 health.");
                return;
            }

            // Minecraft uses half-hearts: 1 heart = 2.0 health points
            double calculatedHealth = hearts * 2.0;
            // Clamp to allowed range
            calculatedHealth = Math.max(2.0, Math.min(40.0, calculatedHealth));

            boolean adjustMax = true;
            try {
                adjustMax = plugin.getConfig().getBoolean("settings.adjust-max-health", true);
            } catch (Exception ignored) {
            }

            if (adjustMax) {
                // Update player's max health, then current health
                player.setMaxHealth(calculatedHealth);
                if (player.getHealth() > calculatedHealth) {
                    player.setHealth(calculatedHealth);
                }
                plugin.getLogger().info("Updated " + player.getName() + " maxHealth to " + calculatedHealth + " (" + hearts + " hearts)");
            } else {
                // Only set current health without changing maxHealth
                double setHealth = Math.min(player.getMaxHealth(), calculatedHealth);
                if (setHealth <= 0) setHealth = 0.5; // avoid invalid values
                player.setHealth(setHealth);
                plugin.getLogger().info("Updated " + player.getName() + " health to " + setHealth + " (" + hearts + " hearts) without changing maxHealth");
            }
        } catch (Throwable ex) {
            plugin.getLogger().warning("Failed to update player health for " + player.getName() + ": " + ex.getMessage());
            // Write full stacktrace to debug log if enabled
            try {
                boolean dbg = plugin.getConfig().getBoolean("debug.log-stacktraces", false);
                if (dbg) {
                    java.io.File debugFile = new java.io.File(plugin.getDataFolder(), "debug.log");
                    debugFile.getParentFile().mkdirs();
                    try (java.io.FileWriter fw = new java.io.FileWriter(debugFile, true);
                         java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {
                        pw.println("--- Exception updating health for " + player.getName() + " at " + java.time.Instant.now() + " ---");
                        ex.printStackTrace(pw);
                        pw.println();
                    }
                }
            } catch (Exception e2) {
                plugin.getLogger().warning("Failed to write debug log: " + e2.getMessage());
            }
        }
    }

    public int getMaxHearts() {
        return MAX_HEARTS;
    }

    public int getMinHearts() {
        return MIN_HEARTS;
    }

    public int getDefaultHearts() {
        return DEFAULT_HEARTS;
    }
}
