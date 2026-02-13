package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private LifestealPlugin plugin;

    public PlayerJoinListener(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Ensure player has hearts data, loading from file or creating new
        int hearts = plugin.getHeartManager().getHearts(player);
        
        // Update player's health display
        plugin.getHeartManager().setHearts(player.getUniqueId(), hearts);
        
        plugin.getLogger().info(player.getName() + " joined with " + hearts + " hearts.");
        
        // Welcome message
        player.sendMessage("§6[Lifesteal] §aWelcome! You have §e" + hearts + "§a hearts.");
        player.sendMessage("§6[Lifesteal] §aUse §e/heartshop§a to buy hearts!");
    }
}
