package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class OrbitsCommand implements CommandExecutor {

    private LifestealPlugin plugin;

    public OrbitsCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessageManager().getMessage("general.only-players"));
                return true;
            }
            return handleSee(sender, ((Player) sender).getName());
        }

        String subcommand = args[0].toLowerCase();
        switch (subcommand) {
            case "see":
                if (args.length >= 2) {
                    return handleSee(sender, args[1]);
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessageManager().getMessage("general.only-players"));
                    return true;
                }
                return handleSee(sender, ((Player) sender).getName());
            case "give":
                return handleGive(sender, args);
            case "take":
                return handleTake(sender, args);
            default:
                sender.sendMessage("§8[§cLifesteal§8] §cUsage: /orbits <see|give|take> [player] [amount]");
                return true;
        }
    }

    private boolean handleSee(CommandSender sender, String targetName) {
        if (!sender.hasPermission("lifesteal.orbits") && !sender.hasPermission("lifesteal.admin")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.no-permission"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        if (target == null || target.getName() == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.player-not-found"));
            return true;
        }

        long orbits = plugin.getOrbitsManager().getOrbits(target.getUniqueId());
        Map<String, String> replacements = new HashMap<>();
        replacements.put("orbits", String.valueOf(orbits));
        replacements.put("orbits_formatted", plugin.getOrbitsManager().formatOrbits(orbits));

        if (sender instanceof Player && ((Player) sender).getUniqueId().equals(target.getUniqueId())) {
            sender.sendMessage(plugin.getMessageManager().getMessage("orbits.view-self", replacements));
        } else {
            replacements.put("player", target.getName());
            sender.sendMessage(plugin.getMessageManager().getMessage("orbits.view-other", replacements));
        }
        return true;
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lifesteal.orbits.admin") && !sender.hasPermission("lifesteal.admin")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§8[§cLifesteal§8] §cUsage: /orbits give <player> <amount>");
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.invalid-amount"));
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.invalid-amount"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || target.getName() == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.player-not-found"));
            return true;
        }

        plugin.getOrbitsManager().addOrbits(target.getUniqueId(), amount);
        long newTotal = plugin.getOrbitsManager().getOrbits(target.getUniqueId());

        Map<String, String> replacements = new HashMap<>();
        replacements.put("player", target.getName());
        replacements.put("amount", String.valueOf(amount));
        replacements.put("total", String.valueOf(newTotal));
        replacements.put("total_formatted", plugin.getOrbitsManager().formatOrbits(newTotal));
        sender.sendMessage(plugin.getMessageManager().getMessage("orbits.give-success", replacements));

        if (target.isOnline()) {
            Player targetPlayer = target.getPlayer();
            if (targetPlayer != null) {
                Map<String, String> notify = new HashMap<>();
                notify.put("amount", String.valueOf(amount));
                notify.put("total", String.valueOf(newTotal));
                notify.put("total_formatted", plugin.getOrbitsManager().formatOrbits(newTotal));
                notify.put("admin", sender.getName());
                targetPlayer.sendMessage(plugin.getMessageManager().getMessage("orbits.give-notify", notify));
            }
        }

        return true;
    }

    private boolean handleTake(CommandSender sender, String[] args) {
        if (!sender.hasPermission("lifesteal.orbits.admin") && !sender.hasPermission("lifesteal.admin")) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.no-permission"));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage("§8[§cLifesteal§8] §cUsage: /orbits take <player> <amount>");
            return true;
        }

        long amount;
        try {
            amount = Long.parseLong(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.invalid-amount"));
            return true;
        }

        if (amount <= 0) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.invalid-amount"));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target == null || target.getName() == null) {
            sender.sendMessage(plugin.getMessageManager().getMessage("general.player-not-found"));
            return true;
        }

        plugin.getOrbitsManager().removeOrbits(target.getUniqueId(), amount);
        long newTotal = plugin.getOrbitsManager().getOrbits(target.getUniqueId());

        Map<String, String> replacements = new HashMap<>();
        replacements.put("player", target.getName());
        replacements.put("amount", String.valueOf(amount));
        replacements.put("total", String.valueOf(newTotal));
        replacements.put("total_formatted", plugin.getOrbitsManager().formatOrbits(newTotal));
        sender.sendMessage(plugin.getMessageManager().getMessage("orbits.take-success", replacements));

        if (target.isOnline()) {
            Player targetPlayer = target.getPlayer();
            if (targetPlayer != null) {
                Map<String, String> notify = new HashMap<>();
                notify.put("amount", String.valueOf(amount));
                notify.put("total", String.valueOf(newTotal));
                notify.put("total_formatted", plugin.getOrbitsManager().formatOrbits(newTotal));
                notify.put("admin", sender.getName());
                targetPlayer.sendMessage(plugin.getMessageManager().getMessage("orbits.take-notify", notify));
            }
        }

        return true;
    }
}
