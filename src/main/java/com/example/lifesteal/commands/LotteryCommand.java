package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.LotteryGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LotteryCommand implements CommandExecutor {
    
    private LifestealPlugin plugin;
    
    public LotteryCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§8[§cLifesteal§8] §cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check if Vault Economy is available
        if (!plugin.hasEconomy()) {
            player.sendMessage("§8[§cLifesteal§8] §c✗ Vault Economy plugin not found!");
            player.sendMessage("§8[§cLifesteal§8] §eThe lottery system requires Vault to work.");
            return true;
        }
        
        if (!plugin.hasLotteryManager()) {
            player.sendMessage("§8[§cLifesteal§8] §cLottery system not available!");
            return true;
        }
        
        // Open lottery GUI
        new LotteryGUI(plugin, plugin.getLotteryManager(), player).open();
        return true;
    }
}
