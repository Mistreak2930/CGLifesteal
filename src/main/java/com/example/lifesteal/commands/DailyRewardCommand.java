package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.BundleShopGUI;
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
        
        int availableBundles = plugin.getBundleManager().getAvailableBundles(player);
        player.sendMessage(plugin.getMessageManager().getMessage("daily-bundles.available")
            .replace("{available}", String.valueOf(availableBundles))
            .replace("{total}", String.valueOf(plugin.getConfig().getInt("bundles.max-per-day", 3))));
        new BundleShopGUI(plugin, player).open();
        return true;
    }
}
