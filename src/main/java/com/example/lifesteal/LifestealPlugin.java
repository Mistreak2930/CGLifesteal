package com.example.lifesteal;

import com.example.lifesteal.commands.*;
import com.example.lifesteal.config.MessageManager;
import com.example.lifesteal.listeners.HeartShopListener;
import com.example.lifesteal.listeners.PlayerDeathListener;
import com.example.lifesteal.listeners.PlayerJoinListener;
import com.example.lifesteal.listeners.TeamChatListener;
import com.example.lifesteal.system.HeartManager;
import com.example.lifesteal.system.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LifestealPlugin extends JavaPlugin {

    private static LifestealPlugin instance;
    private HeartManager heartManager;
    private TeamManager teamManager;
    private MessageManager messageManager;
    private Object playerPointsAPI;

    @Override
    public void onEnable() {
        instance = this;
        
        // Load configuration
        saveDefaultConfig();
        
        // Initialize managers
        this.heartManager = new HeartManager(this);
        this.teamManager = new TeamManager(this);
        this.messageManager = new MessageManager(this);
        
        // Setup PlayerPoints integration
        if (setupPlayerPoints()) {
            getLogger().info("PlayerPoints integration enabled!");
        } else {
            getLogger().warning("PlayerPoints not found! Shop features will be disabled.");
        }
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        getLogger().info("Lifesteal plugin enabled! Version: " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        if (heartManager != null) {
            heartManager.saveHearts();
        }
        if (teamManager != null) {
            teamManager.saveTeams();
        }
        getLogger().info("Lifesteal plugin disabled!");
    }

    private boolean setupPlayerPoints() {
        try {
            if (Bukkit.getPluginManager().getPlugin("PlayerPoints") != null) {
                // Use reflection to safely load PlayerPoints
                Class<?> playerPointsClass = Class.forName("org.black_ixx.playerpoints.PlayerPoints");
                Object instance = playerPointsClass.getMethod("getInstance").invoke(null);
                this.playerPointsAPI = playerPointsClass.getMethod("getAPI").invoke(instance);
                return true;
            }
        } catch (Exception e) {
            getLogger().info("PlayerPoints API not available: " + e.getMessage());
        }
        return false;
    }

    private void registerCommands() {
        getCommand("hearts").setExecutor(new HeartsCommand(this));
        getCommand("giveheart").setExecutor(new GiveHeartCommand(this));
        getCommand("takeheart").setExecutor(new TakeHeartCommand(this));
        getCommand("withdraw").setExecutor(new WithdrawCommand(this));
        getCommand("deposit").setExecutor(new DepositCommand(this));
        getCommand("heartshop").setExecutor(new HeartShopCommand(this));
        getCommand("team").setExecutor(new TeamCommand(this));
        getCommand("teamchat").setExecutor(new TeamChatCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new TeamChatListener(this), this);
        getServer().getPluginManager().registerEvents(new HeartShopListener(this), this);
    }

    public static LifestealPlugin getInstance() {
        return instance;
    }

    public HeartManager getHeartManager() {
        return heartManager;
    }

    public TeamManager getTeamManager() {
        return teamManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public Object getPlayerPointsAPI() {
        return playerPointsAPI;
    }

    public boolean hasPlayerPoints() {
        return playerPointsAPI != null;
    }
}
