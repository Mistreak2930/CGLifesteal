package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.HeartShopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HeartShopCommand implements CommandExecutor {

    private LifestealPlugin plugin;

    public HeartShopCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§8[§cLifesteal§8] §cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // Check if PlayerPoints is available
        if (plugin.getPlayerPointsAPI() == null) {
            player.sendMessage("§8[§cLifesteal§8] §c✗ PlayerPoints plugin not found!");
            player.sendMessage("§8[§cLifesteal§8] §eThe heart shop requires PlayerPoints to work.");
            return true;
        }

        // Check if heartshop is enabled in config
        if (!plugin.getConfig().getBoolean("heartshop.enabled", true)) {
            player.sendMessage(plugin.getMessageManager().getMessage("shop.disabled"));
            return true;
        }

        // Open heart shop GUI
        new HeartShopGUI(plugin, player).open();
        return true;
    }
}