package com.example.lifesteal.system;

import com.example.lifesteal.LifestealPlugin;
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
    private static final int DEFAULT_HEARTS = 10;

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
        Map<String, Integer> nameHearts = new HashMap<>();
        
        for (String key : heartsConfig.getKeys(false)) {
            int hearts = heartsConfig.getInt(key, DEFAULT_HEARTS);
            hearts = Math.max(MIN_HEARTS, Math.min(MAX_HEARTS, hearts));
            
            try {
                // Try to parse as UUID (for backwards compatibility)
                UUID uuid = UUID.fromString(key);
                heartsCache.put(uuid, hearts);
            } catch (IllegalArgumentException e) {
                // Not a UUID, treat as player name
                nameHearts.put(key, hearts);
            }
        }
        
        // Convert name entries to UUID entries
        for (Map.Entry<String, Integer> entry : nameHearts.entrySet()) {
            try {
                UUID uuid = Bukkit.getOfflinePlayer(entry.getKey()).getUniqueId();
                heartsCache.put(uuid, entry.getValue());
                plugin.getLogger().info("Migrated " + entry.getKey() + " to UUID in hearts cache");
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to migrate player " + entry.getKey() + ": " + e.getMessage());
            }
        }
    }

    public void saveHearts() {
        heartsConfig = new YamlConfiguration();
        for (Map.Entry<UUID, Integer> entry : heartsCache.entrySet()) {
            try {
                String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                if (playerName != null && !playerName.isEmpty()) {
                    // Save by player name for readability
                    heartsConfig.set(playerName, entry.getValue());
                } else {
                    // Fallback to UUID if name not available
                    heartsConfig.set(entry.getKey().toString(), entry.getValue());
                }
            } catch (Exception e) {
                // Fallback to UUID
                heartsConfig.set(entry.getKey().toString(), entry.getValue());
            }
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
            player.sendMessage("§8[§cLifesteal§8] §aYour hearts: §e" + clamped);
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
        
        // Check if player ran out of hearts and ban them
        if (newAmount <= 0) {
            Player player = Bukkit.getPlayer(uuid);
            LifestealPlugin lifestealPlugin = LifestealPlugin.getInstance();
            if (player != null) {
                String banReason = lifestealPlugin.getMessageManager().getMessage("ban.ran-out-of-hearts");
                player.kickPlayer("§c" + banReason);
                Bukkit.getOnlinePlayers().forEach(p -> {
                    if (p.hasPermission("lifesteal.admin")) {
                        p.sendMessage(lifestealPlugin.getMessageManager().getMessage("ban.notification", 
                            java.util.Map.of("player", player.getName())));
                    }
                });
            }
            // Ban the player
            try {
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(
                    Bukkit.getOfflinePlayer(uuid).getName(),
                    lifestealPlugin.getMessageManager().getMessage("ban.ran-out-of-hearts"),
                    null,
                    null
                );
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to ban player: " + e.getMessage());
            }
        }
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

    public void resetAllHearts(int heartsAmount) {
        heartsCache.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            heartsCache.put(player.getUniqueId(), heartsAmount);
            updatePlayerHealth(player, heartsAmount);
            player.sendMessage("§8[§cLifesteal§8] §aAll hearts have been reset to §e" + heartsAmount);
        }
        saveHearts();
        plugin.getLogger().info("Reset all players' hearts to " + heartsAmount);
    }
}
