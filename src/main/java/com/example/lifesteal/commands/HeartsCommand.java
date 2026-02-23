package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command handler for /hearts command
 * Supports both player and admin subcommands
 */
public class HeartsCommand implements CommandExecutor {

    private LifestealPlugin plugin;

    public HeartsCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            // Console can use admin commands
            if (sender instanceof org.bukkit.command.ConsoleCommandSender) {
                return handleAdminCommand(sender, args);
            }
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        // If first argument is an admin subcommand and player has permission
        if (args.length > 0 && player.hasPermission("lifesteal.admin")) {
            String subcommand = args[0].toLowerCase();
            switch (subcommand) {
                case "give":
                    return handleGive(player, args);
                case "take":
                    return handleTake(player, args);
                case "set":
                    return handleSet(player, args);
                case "get":
                    return handleGet(player, args);
                case "reset":
                    return handleReset(player, args);
            }
        }

        // Regular player commands
        if (args.length == 0) {
            // Show own hearts
            int hearts = plugin.getHeartManager().getHearts(player);
            player.sendMessage("§8[§cLifesteal§8] §aYou have §e" + hearts + "§a hearts");
            return true;
        }

        if (args.length == 1) {
            // Show another player's hearts
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§8[§cLifesteal§8] §cPlayer not found!");
                return true;
            }
            int hearts = plugin.getHeartManager().getHearts(target);
            player.sendMessage("§8[§cLifesteal§8] §a" + target.getName() + " has §e" + hearts + "§a hearts");
            return true;
        }

        player.sendMessage("§8[§cLifesteal§8] §cUsage: /hearts [player]");
        return true;
    }

    /**
     * Handle console admin commands
     */
    private boolean handleAdminCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§8[§cLifesteal§8] §cUsage: /hearts <give|take|set|get> <player> [amount]");
            return true;
        }

        String subcommand = args[0].toLowerCase();
        String playerName = args[1];

        switch (subcommand) {
            case "give": {
                if (args.length < 3) {
                    sender.sendMessage("§8[§cLifesteal§8] §cUsage: /hearts give <player> <amount>");
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[2]);
                    java.util.UUID playerUUID = Bukkit.getOfflinePlayer(playerName).getUniqueId();
                    plugin.getHeartManager().addHearts(playerUUID, amount);
                    sender.sendMessage("§8[§cLifesteal§8] §aGave §e" + amount + "§a hearts to §e" + playerName);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§8[§cLifesteal§8] §cInvalid amount!");
                }
                return true;
            }
            case "take": {
                if (args.length < 3) {
                    sender.sendMessage("§8[§cLifesteal§8] §cUsage: /hearts take <player> <amount>");
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[2]);
                    java.util.UUID playerUUID = Bukkit.getOfflinePlayer(playerName).getUniqueId();
                    plugin.getHeartManager().removeHearts(playerUUID, amount);
                    sender.sendMessage("§8[§cLifesteal§8] §aTook §e" + amount + "§a hearts from §e" + playerName);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§8[§cLifesteal§8] §cInvalid amount!");
                }
                return true;
            }
            case "set": {
                if (args.length < 3) {
                    sender.sendMessage("§8[§cLifesteal§8] §cUsage: /hearts set <player> <amount>");
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[2]);
                    java.util.UUID playerUUID = Bukkit.getOfflinePlayer(playerName).getUniqueId();
                    plugin.getHeartManager().setHearts(playerUUID, amount);
                    sender.sendMessage("§8[§cLifesteal§8] §aSet hearts for §e" + playerName + "§a to §e" + amount);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§8[§cLifesteal§8] §cInvalid amount!");
                }
                return true;
            }
            case "get": {
                java.util.UUID playerUUID = Bukkit.getOfflinePlayer(playerName).getUniqueId();
                int hearts = plugin.getHeartManager().getHearts(playerUUID);
                sender.sendMessage("§8[§cLifesteal§8] §e" + playerName + "§a has §e" + hearts + "§a hearts");
                return true;
            }
            case "reset": {
                int hearts = 10; // Default to 10 hearts
                if (args.length > 1) {
                    try {
                        hearts = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage("§8[§cLifesteal§8] §cInvalid amount! Using default of 10.");
                    }
                }
                plugin.getHeartManager().resetAllHearts(hearts);
                sender.sendMessage("§8[§cLifesteal§8] §aReset all players' hearts to §e" + hearts);
                return true;
            }
        }

        sender.sendMessage("§8[§cLifesteal§8] §cUnknown subcommand!");
        return true;
    }

    /**
     * Handle /hearts give <player> <amount>
     */
    private boolean handleGive(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§8[§cLifesteal§8] §cUsage: /hearts give <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§8[§cLifesteal§8] §cPlayer not found!");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[2]);
            plugin.getHeartManager().addHearts(target, amount);
            player.sendMessage("§8[§cLifesteal§8] §aGave §e" + amount + "§a hearts to §e" + target.getName());
            target.sendMessage("§8[§cLifesteal§8] §aYou received §e" + amount + "§a hearts!");
        } catch (NumberFormatException e) {
            player.sendMessage("§8[§cLifesteal§8] §cInvalid amount!");
        }

        return true;
    }

    /**
     * Handle /hearts take <player> <amount>
     */
    private boolean handleTake(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§8[§cLifesteal§8] §cUsage: /hearts take <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§8[§cLifesteal§8] §cPlayer not found!");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[2]);
            plugin.getHeartManager().removeHearts(target, amount);
            player.sendMessage("§8[§cLifesteal§8] §aTook §e" + amount + "§a hearts from §e" + target.getName());
            target.sendMessage("§8[§cLifesteal§8] §cYou lost §e" + amount + "§c hearts!");
        } catch (NumberFormatException e) {
            player.sendMessage("§8[§cLifesteal§8] §cInvalid amount!");
        }

        return true;
    }

    /**
     * Handle /hearts set <player> <amount>
     */
    private boolean handleSet(Player player, String[] args) {
        if (args.length < 3) {
            player.sendMessage("§8[§cLifesteal§8] §cUsage: /hearts set <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§8[§cLifesteal§8] §cPlayer not found!");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[2]);
            plugin.getHeartManager().setHearts(target.getUniqueId(), amount);
            player.sendMessage("§8[§cLifesteal§8] §aSet §e" + target.getName() + "§a's hearts to §e" + amount);
            target.sendMessage("§8[§cLifesteal§8] §aYour hearts set to §e" + amount);
        } catch (NumberFormatException e) {
            player.sendMessage("§8[§cLifesteal§8] §cInvalid amount!");
        }

        return true;
    }

    /**
     * Handle /hearts get <player>
     */
    private boolean handleGet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§8[§cLifesteal§8] §cUsage: /hearts get <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§8[§cLifesteal§8] §cPlayer not found!");
            return true;
        }

        int hearts = plugin.getHeartManager().getHearts(target);
        player.sendMessage("§8[§cLifesteal§8] §e" + target.getName() + "§a has §e" + hearts + "§a hearts");

        return true;
    }
    /**
     * Handle /hearts reset [amount]
     */
    private boolean handleReset(Player player, String[] args) {
        int hearts = 10; // Default to 10 hearts
        if (args.length > 1) {
            try {
                hearts = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("§8[§cLifesteal§8] §cInvalid amount! Using default of 10.");
            }
        }
        
        plugin.getHeartManager().resetAllHearts(hearts);
        player.sendMessage("§8[§cLifesteal§8] §aReset all players' hearts to §e" + hearts);
        return true;
    }
}
