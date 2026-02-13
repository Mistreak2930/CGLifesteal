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
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (!plugin.hasPlayerPoints()) {
            player.sendMessage("§c[Lifesteal] PlayerPoints plugin is not installed!");
            return true;
        }

        new HeartShopGUI(plugin, player).open();
        return true;
    }
}

