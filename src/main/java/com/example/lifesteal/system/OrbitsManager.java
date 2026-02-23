package com.example.lifesteal.system;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class OrbitsManager {

    private JavaPlugin plugin;
    private File orbitsFile;
    private FileConfiguration orbitsConfig;
    private Map<UUID, Long> orbitsCache;

    public OrbitsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.orbitsCache = new HashMap<>();
        initializeOrbitsFile();
        loadOrbits();
    }

    private void initializeOrbitsFile() {
        orbitsFile = new File(plugin.getDataFolder(), "orbits.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        if (!orbitsFile.exists()) {
            try {
                orbitsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create orbits.yml: " + e.getMessage());
            }
        }
        orbitsConfig = YamlConfiguration.loadConfiguration(orbitsFile);
    }

    public void loadOrbits() {
        orbitsCache.clear();
        for (String key : orbitsConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                long amount = orbitsConfig.getLong(key, 0L);
                orbitsCache.put(uuid, Math.max(0L, amount));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public void saveOrbits() {
        orbitsConfig = new YamlConfiguration();
        for (Map.Entry<UUID, Long> entry : orbitsCache.entrySet()) {
            orbitsConfig.set(entry.getKey().toString(), Math.max(0L, entry.getValue()));
        }
        try {
            orbitsConfig.save(orbitsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save orbits.yml: " + e.getMessage());
        }
    }

    public long getOrbits(UUID uuid) {
        return orbitsCache.getOrDefault(uuid, 0L);
    }

    public void setOrbits(UUID uuid, long amount) {
        orbitsCache.put(uuid, Math.max(0L, amount));
        saveOrbits();
    }

    public void addOrbits(UUID uuid, long amount) {
        long current = getOrbits(uuid);
        setOrbits(uuid, current + Math.max(0L, amount));
    }

    public void removeOrbits(UUID uuid, long amount) {
        long current = getOrbits(uuid);
        setOrbits(uuid, current - Math.max(0L, amount));
    }

    public String formatOrbits(long amount) {
        NumberFormat format = NumberFormat.getInstance(Locale.US);
        format.setGroupingUsed(true);
        return format.format(Math.max(0L, amount));
    }
}
