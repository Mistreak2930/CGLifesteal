package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Command to reload plugin configuration and managers
 */
public class ReloadCommand implements CommandExecutor {

    private LifestealPlugin plugin;

    public ReloadCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("lifesteal.admin.reload")) {
            sender.sendMessage("§8[§cLifesteal§8] §cYou don't have permission to reload!");
            return true;
        }

        sender.sendMessage("§8[§cLifesteal§8] §aReloading configuration and managers...");

        try {
            // Reload config
            plugin.reloadConfig();
            sender.sendMessage("§8[§cLifesteal§8] §a✓ Configuration reloaded");

            // Reload all managers
            plugin.reloadManagers();
            sender.sendMessage("§8[§cLifesteal§8] §a✓ All managers reloaded");

            sender.sendMessage("§8[§cLifesteal§8] §a✓ Plugin configuration successfully reloaded!");
        } catch (Exception e) {
            sender.sendMessage("§8[§cLifesteal§8] §cError reloading plugin: " + e.getMessage());
            plugin.getLogger().severe("Error during reload: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
