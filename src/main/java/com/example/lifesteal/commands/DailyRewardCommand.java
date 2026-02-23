package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.DailyRewardGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DailyRewardCommand implements CommandExecutor {
    
    private LifestealPlugin plugin;
    
    public DailyRewardCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§8[§cLifesteal§8] §cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check if both Vault Economy and PlayerPoints are available
        if (!plugin.hasEconomy()) {
            player.sendMessage("§8[§cLifesteal§8] §c✗ Vault Economy plugin not found!");
            player.sendMessage("§8[§cLifesteal§8] §eThe daily reward system requires Vault to work.");
            return true;
        }
        
        if (!plugin.hasPlayerPoints()) {
            player.sendMessage("§8[§cLifesteal§8] §c✗ PlayerPoints plugin not found!");
            player.sendMessage("§8[§cLifesteal§8] §eThe daily reward system requires PlayerPoints to work.");
            return true;
        }
        
        if (!plugin.hasDailyRewardManager()) {
            player.sendMessage("§8[§cLifesteal§8] §cDaily reward system not available!");
            return true;
        }
        
        // Open daily reward GUI
        new DailyRewardGUI(plugin, plugin.getDailyRewardManager(), player).open();
        return true;
    }
}
