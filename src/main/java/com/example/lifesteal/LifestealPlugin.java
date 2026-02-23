package com.example.lifesteal;

import com.example.lifesteal.commands.*;
import com.example.lifesteal.config.MessageManager;
import com.example.lifesteal.listeners.*;
import com.example.lifesteal.system.HeartManager;
import com.example.lifesteal.system.TeamManager;
import com.example.lifesteal.system.LotteryManager;
import com.example.lifesteal.system.DailyRewardManager;
import com.example.lifesteal.system.OrbitsManager;
import com.example.lifesteal.system.BundleManager;
import com.example.lifesteal.system.TeamWarManager;
import com.example.lifesteal.placeholders.LifestealPlaceholderExpansion;
import com.example.lifesteal.util.MessageUtil;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

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
    private DailyRewardManager dailyRewardManager;
    private OrbitsManager orbitsManager;
    private BundleManager bundleManager;
    private TeamWarManager teamWarManager;
    private Object playerPointsAPI;
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        
        // Load configuration
        saveDefaultConfig();
        
        // Initialize MessageUtil first
        MessageUtil.setPlugin(this);
        
        // Initialize managers
        this.heartManager = new HeartManager(this);
        this.teamManager = new TeamManager(this);
        this.messageManager = new MessageManager(this);
        this.orbitsManager = new OrbitsManager(this);
        this.bundleManager = new BundleManager(this);
        this.teamWarManager = new TeamWarManager(this);
        
        // Setup Vault economy integration
        if (setupEconomy()) {
            getLogger().info("Vault Economy integration enabled!");
        } else {
            getLogger().warning("Vault Economy not found! Money features will be disabled.");
        }
        
        // Setup PlayerPoints integration
        if (setupPlayerPoints()) {
            getLogger().info("PlayerPoints integration enabled!");
            
            // Initialize economy-dependent managers
            this.lotteryManager = new LotteryManager(this);
            this.dailyRewardManager = new DailyRewardManager(this);
            getLogger().info("Lottery and Daily Reward systems enabled!");
        } else {
            getLogger().warning("PlayerPoints not found! Lottery and Daily Reward features will be disabled.");
            this.lotteryManager = null;
            this.dailyRewardManager = null;
        }
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LifestealPlaceholderExpansion(this).register();
            getLogger().info("PlaceholderAPI integration enabled!");
        } else {
            getLogger().info("PlaceholderAPI not found. Placeholders disabled.");
        }
        
        getLogger().info("╔═════════════════════════╗");
        getLogger().info("║ Lifesteal Plugin Enabled ║");
        getLogger().info("║ Version: " + getDescription().getVersion() + "         ║");
        getLogger().info("║ Features:                ║");
        getLogger().info("║ • Hearts System          ║");
        getLogger().info("║ • Teams Management       ║");
        getLogger().info("║ • Team GUI               ║");
        if (hasPlayerPoints()) {
            getLogger().info("║ • Lottery System         ║");
            getLogger().info("║ • Daily Rewards          ║");
        }
        getLogger().info("╚═════════════════════════╝");
    }

    @Override
    public void onDisable() {
        if (heartManager != null) {
            heartManager.saveHearts();
        }
        if (teamManager != null) {
            teamManager.saveTeams();
        }
        if (orbitsManager != null) {
            orbitsManager.saveOrbits();
        }
        if (dailyRewardManager != null) {
            dailyRewardManager.saveDailyRewardData();
        }
        if (bundleManager != null) {
            bundleManager.saveBundleData();
        }
        if (teamWarManager != null) {
            teamWarManager.saveWarData();
        }
        if (lotteryManager != null) {
            lotteryManager.disable();
        }
        getLogger().info("Lifesteal plugin disabled!");
    }

    /**
     * Setup Vault Economy integration
     */
    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        
        economy = rsp.getProvider();
        return economy != null;
    }

    /**
     * Setup PlayerPoints API integration
     */
    private boolean setupPlayerPoints() {
        try {
            // Check if PlayerPoints plugin is loaded
            if (Bukkit.getPluginManager().getPlugin("PlayerPoints") == null) {
                getLogger().warning("PlayerPoints plugin not found!");
                return false;
            }
            
            // Try to get the PlayerPoints instance
            Class<?> playerPointsClass = Class.forName("org.black_ixx.playerpoints.PlayerPoints");
            Object ppInstance = playerPointsClass.getMethod("getInstance").invoke(null);
            
            if (ppInstance == null) {
                getLogger().warning("Could not get PlayerPoints instance!");
                return false;
            }
            
            // Get the API from the instance
            this.playerPointsAPI = playerPointsClass.getMethod("getAPI").invoke(ppInstance);
            
            if (this.playerPointsAPI == null) {
                getLogger().warning("Could not get PlayerPoints API!");
                return false;
            }
            
            getLogger().info("Successfully hooked into PlayerPoints!");
            return true;
        } catch (ClassNotFoundException e) {
            getLogger().warning("PlayerPoints classes not found: " + e.getMessage());
            return false;
        } catch (Exception e) {
            getLogger().warning("Error setting up PlayerPoints: " + e.getMessage());
            e.printStackTrace();
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
        
        // Only register if managers are available (requires PlayerPoints)
        if (lotteryManager != null && dailyRewardManager != null) {
            getServer().getPluginManager().registerEvents(new LotteryListener(this, lotteryManager), this);
            getServer().getPluginManager().registerEvents(new DailyRewardListener(this, dailyRewardManager), this);
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
        return lotteryManager != null && hasPlayerPoints();
    }

    /**
     * Get the daily reward manager
     */
    public DailyRewardManager getDailyRewardManager() {
        return dailyRewardManager;
    }

    /**
     * Check if daily reward manager is available
     */
    public boolean hasDailyRewardManager() {
        return dailyRewardManager != null && hasPlayerPoints();
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
