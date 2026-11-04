package com.example.lifesteal;

import com.example.lifesteal.commands.*;
import com.example.lifesteal.config.MessageManager;
import com.example.lifesteal.listeners.*;
import com.example.lifesteal.system.HeartManager;
import com.example.lifesteal.system.TeamManager;
import com.example.lifesteal.system.LotteryManager;
import com.example.lifesteal.system.OrbitsManager;
import com.example.lifesteal.system.BundleManager;
import com.example.lifesteal.system.TeamWarManager;
import com.example.lifesteal.placeholders.LifestealPlaceholderExpansion;
import com.example.lifesteal.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Main plugin class for Lifesteal system
 * Manages initialization, command registration, and event listeners
 */
public class LifestealPlugin extends JavaPlugin {

    private static LifestealPlugin instance;
    private HeartManager heartManager;
    private TeamManager teamManager;
    private MessageManager messageManager;
    private LotteryManager lotteryManager;
    private OrbitsManager orbitsManager;
    private BundleManager bundleManager;
    private TeamWarManager teamWarManager;
    private Object playerPointsAPI;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        
        getLogger().info("════════════════════════════════════════════════════════");
        getLogger().info("Lifesteal Plugin v" + getDescription().getVersion() + " - Initializing...");
        getLogger().info("════════════════════════════════════════════════════════");
        
        // Load configuration
        long startTime = System.currentTimeMillis();
        migrateLegacyDataFolder();
        saveDefaultConfig();
        getLogger().info("✓ Configuration loaded");
        
        // Initialize MessageUtil first
        MessageUtil.setPlugin(this);
        getLogger().info("✓ Message system initialized");
        
        // Initialize managers
        this.heartManager = new HeartManager(this);
        this.teamManager = new TeamManager(this);
        this.messageManager = new MessageManager(this);
        this.orbitsManager = new OrbitsManager(this);
        this.bundleManager = new BundleManager(this);
        this.teamWarManager = new TeamWarManager(this);
        getLogger().info("✓ Core managers loaded (Hearts, Teams, Orbits, Bundles, Wars)");
        
        // Setup Vault economy integration
        if (setupEconomy()) {
            getLogger().info("✓ Vault Economy integration enabled");
            if (getConfig().getBoolean("lottery.enabled", true)) {
                this.lotteryManager = new LotteryManager(this);
            }
        } else {
            getLogger().warning("✗ Vault Economy not found - money features disabled");
        }
        
        // Setup PlayerPoints integration
        if (setupPlayerPoints()) {
            getLogger().info("✓ PlayerPoints integration enabled");
        } else {
            getLogger().warning("✗ PlayerPoints not found - points-based features are disabled (optional plugin)");
        }
        
        // Register commands
        registerCommands();
        getLogger().info("✓ All commands registered (14 commands)");
        
        // Register listeners
        registerListeners();
        getLogger().info("✓ Event listeners registered");

        // Setup PlaceholderAPI integration
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LifestealPlaceholderExpansion(this).register();
            getLogger().info("✓ PlaceholderAPI integration enabled");
        } else {
            getLogger().info("ℹ PlaceholderAPI not found - placeholders disabled (optional)");
        }
        
        long loadTime = System.currentTimeMillis() - startTime;
        getLogger().info("════════════════════════════════════════════════════════");
        getLogger().info("✓ Lifesteal Plugin ENABLED successfully!");
        getLogger().info("  • Version: " + getDescription().getVersion());
        getLogger().info("  • Load time: " + loadTime + "ms");
        getLogger().info("  • Status: Ready for play");
        getLogger().info("════════════════════════════════════════════════════════");
    }

    private void migrateLegacyDataFolder() {
        File currentFolder = getDataFolder();
        File legacyFolder = new File(currentFolder.getParentFile(), "CGLifesteal");
        if (!legacyFolder.isDirectory()) {
            return;
        }

        try {
            if (!currentFolder.exists()) {
                Files.move(legacyFolder.toPath(), currentFolder.toPath());
            } else {
                Path legacyPath = legacyFolder.toPath();
                try (Stream<Path> files = Files.walk(legacyPath)) {
                    for (Path source : files.toList()) {
                        Path destination = currentFolder.toPath().resolve(legacyPath.relativize(source));
                        if (Files.isDirectory(source)) {
                            Files.createDirectories(destination);
                        } else if (!Files.exists(destination)) {
                            Files.copy(source, destination);
                        }
                    }
                }
            }
            getLogger().info("Migrated Lifesteal data from CGLifesteal where needed.");
        } catch (IOException exception) {
            getLogger().severe("Could not migrate the existing CGLifesteal data folder: " + exception.getMessage());
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("════════════════════════════════════════════════════════");
        getLogger().info("Lifesteal Plugin - Shutting down...");
        getLogger().info("════════════════════════════════════════════════════════");
        
        int savedCount = 0;
        
        if (heartManager != null) {
            heartManager.saveHearts();
            savedCount++;
        }
        if (teamManager != null) {
            teamManager.saveTeams();
            savedCount++;
        }
        if (orbitsManager != null) {
            orbitsManager.saveOrbits();
            savedCount++;
        }
        if (bundleManager != null) {
            bundleManager.saveBundleData();
            savedCount++;
        }
        if (teamWarManager != null) {
            teamWarManager.saveWarData();
            savedCount++;
        }
        if (lotteryManager != null) {
            lotteryManager.disable();
        }
        
        getLogger().info("✓ Data saved (" + savedCount + " managers flushed)");
        getLogger().info("════════════════════════════════════════════════════════");
        getLogger().info("✓ Lifesteal Plugin DISABLED");
        getLogger().info("════════════════════════════════════════════════════════");
    }

    /**
     * Setup Vault Economy integration
     * Attempts to hook into the Vault economy provider
     * 
     * @return true if Vault economy is available and set up, false otherwise
     */
    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getLogger().fine("Vault plugin not found - economy features will be unavailable");
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().fine("No economy service provider found - coins/money features disabled");
            return false;
        }
        
        economy = rsp.getProvider();
        return economy != null;
    }

    /**
     * Setup PlayerPoints API integration
     * Attempts to hook into the PlayerPoints plugin via reflection
     * Required for: Lottery, Daily Rewards, Bundle System
     * 
     * @return true if PlayerPoints API is successfully initialized, false otherwise
     */
    private boolean setupPlayerPoints() {
        try {
            // Check if PlayerPoints plugin is loaded
            if (Bukkit.getPluginManager().getPlugin("PlayerPoints") == null) {
                getLogger().fine("PlayerPoints plugin not installed - lottery/rewards disabled");
                return false;
            }
            
            // Try to get the PlayerPoints instance
            Class<?> playerPointsClass = Class.forName("org.black_ixx.playerpoints.PlayerPoints");
            Object ppInstance = playerPointsClass.getMethod("getInstance").invoke(null);
            
            if (ppInstance == null) {
                getLogger().warning("ERROR: PlayerPoints instance is null - failed to initialize");
                return false;
            }
            
            // Get the API from the instance
            this.playerPointsAPI = playerPointsClass.getMethod("getAPI").invoke(ppInstance);
            
            if (this.playerPointsAPI == null) {
                getLogger().warning("ERROR: PlayerPoints API is null - failed to hook");
                return false;
            }
            
            getLogger().fine("PlayerPoints API successfully hooked via reflection");
            return true;
        } catch (ClassNotFoundException e) {
            getLogger().fine("PlayerPoints class not found in classpath");
            return false;
        } catch (NoSuchMethodException e) {
            getLogger().warning("ERROR: PlayerPoints API method not found - wrong version?");
            return false;
        } catch (Exception e) {
            getLogger().warning("ERROR: Failed to hook PlayerPoints - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Register all plugin commands
     */
    private void registerCommands() {
        getCommand("hearts").setExecutor(new HeartsCommand(this));
        getCommand("giveheart").setExecutor(new GiveHeartCommand(this));
        getCommand("takeheart").setExecutor(new TakeHeartCommand(this));
        getCommand("lottery").setExecutor(new LotteryCommand(this));
        getCommand("dailyreward").setExecutor(new DailyRewardCommand(this));
        getCommand("heartshop").setExecutor(new HeartShopCommand(this));
        getCommand("withdraw").setExecutor(new WithdrawCommand(this));
        getCommand("deposit").setExecutor(new DepositCommand(this));
        getCommand("team").setExecutor(new TeamCommand(this));
        getCommand("teamchat").setExecutor(new TeamChatCommand(this));
        getCommand("lifestealreload").setExecutor(new ReloadCommand(this));
        getCommand("orbits").setExecutor(new OrbitsCommand(this));
        getCommand("menu").setExecutor(new MenuCommand(this));
        getCommand("teamwar").setExecutor(new TeamWarCommand(this));
    }

    /**
     * Register all event listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new TeamChatListener(this), this);
        getServer().getPluginManager().registerEvents(new HeartItemListener(this), this);
        getServer().getPluginManager().registerEvents(new TeamGUIListener(this), this);
        getServer().getPluginManager().registerEvents(new BundleGUIListener(this, bundleManager), this);
        getServer().getPluginManager().registerEvents(new HeartShopListener(this), this);
        
        // Only register if managers are available (requires PlayerPoints)
        if (lotteryManager != null) {
            getServer().getPluginManager().registerEvents(new LotteryListener(this, lotteryManager), this);
        }
    }

    /**
     * Get the plugin instance
     */
    public static LifestealPlugin getInstance() {
        return instance;
    }

    /**
     * Get the heart manager
     */
    public HeartManager getHeartManager() {
        return heartManager;
    }

    /**
     * Get the team manager
     */
    public TeamManager getTeamManager() {
        return teamManager;
    }

    /**
     * Get the message manager
     */
    public MessageManager getMessageManager() {
        return messageManager;
    }

    /**
     * Get the orbits manager
     */
    public OrbitsManager getOrbitsManager() {
        return orbitsManager;
    }

    /**
     * Get the PlayerPoints API
     */
    public Object getPlayerPointsAPI() {
        return playerPointsAPI;
    }

    /**
     * Get the Vault Economy
     */
    public Economy getEconomy() {
        return economy;
    }

    /**
     * Check if Vault Economy is available
     */
    public boolean hasEconomy() {
        return economy != null;
    }

    /**
     * Check if PlayerPoints is available
     */
    public boolean hasPlayerPoints() {
        return playerPointsAPI != null;
    }

    /**
     * Get the lottery manager
     */
    public LotteryManager getLotteryManager() {
        return lotteryManager;
    }

    /**
     * Check if lottery manager is available
     */
    public boolean hasLotteryManager() {
        return lotteryManager != null;
    }

    /**
     * Get the bundle manager
     */
    public BundleManager getBundleManager() {
        return bundleManager;
    }

    /**
     * Check if bundle manager is available
     */
    public boolean hasBundleManager() {
        return bundleManager != null;
    }

    /**
     * Get the team war manager
     */
    public TeamWarManager getTeamWarManager() {
        return teamWarManager;
    }

    /**
     * Check if team war manager is available
     */
    public boolean hasTeamWarManager() {
        return teamWarManager != null;
    }

    /**
     * Reload all managers and configuration
     */
    public void reloadManagers() {
        // Save current data
        if (heartManager != null) {
            heartManager.saveHearts();
        }
        if (teamManager != null) {
            teamManager.saveTeams();
        }

        // Reinitialize managers with new config
        this.heartManager = new HeartManager(this);
        this.teamManager = new TeamManager(this);
        this.messageManager = new MessageManager(this);

        getLogger().info("All managers reloaded successfully!");
    }
}
