package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.MenuGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to open the main menu GUI
 */
public class MenuCommand implements CommandExecutor {
    
    private LifestealPlugin plugin;
    
    public MenuCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§8[§cLifesteal§8] §cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Open the main menu GUI
        new MenuGUI(plugin, player).open();
        player.sendMessage("§8[§6Lifesteal§8] §eOpening main menu...");
        
        return true;
    }
}
