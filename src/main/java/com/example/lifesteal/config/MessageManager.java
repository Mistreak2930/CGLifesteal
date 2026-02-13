package com.example.lifesteal.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private JavaPlugin plugin;
    private File messagesFile;
    private FileConfiguration messagesConfig;
    private Map<String, String> messagesCache;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.messagesCache = new HashMap<>();
        initializeMessagesFile();
        loadMessages();
    }

    private void initializeMessagesFile() {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        if (!messagesFile.exists()) {
            createDefaultMessages();
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    private void createDefaultMessages() {
        try {
            messagesFile.createNewFile();
            FileConfiguration config = new YamlConfiguration();
            
            // Prefixes
            config.set("prefix.main", "§6[Lifesteal]");
            config.set("prefix.team", "§6[Team]");
            config.set("prefix.shop", "§6[Shop]");
            
            // General Messages
            config.set("general.no-permission", "§cYou don't have permission!");
            config.set("general.player-not-found", "§cPlayer not found!");
            config.set("general.invalid-amount", "§cInvalid amount!");
            config.set("general.only-players", "§cOnly players can use this command!");
            
            // Hearts Messages
            config.set("hearts.view-own", "§aYou have §e{hearts}§a hearts");
            config.set("hearts.view-other", "§a{player} has §e{hearts}§a hearts");
            config.set("hearts.give-success", "§aGave §e{amount}§a hearts to §e{player}§a (§e{old}§a -> §e{new}§a)");
            config.set("hearts.give-notify", "§e{admin}§a gave you §e{amount}§a hearts!");
            config.set("hearts.take-success", "§aRemoved §e{amount}§a hearts from §e{player}§a (§e{old}§a -> §e{new}§a)");
            config.set("hearts.take-notify", "§e{admin}§a removed §c{amount}§a hearts from you!");
            
            // Withdraw Messages
            config.set("withdraw.success", "§aWithdrew §e{amount}§a hearts! You now have §e{remaining}§a hearts");
            config.set("withdraw.success-dropped", "§aWithdrew §e{amount}§a hearts! (dropped due to full inventory)");
            config.set("withdraw.not-enough", "§cYou don't have enough hearts! You have §e{hearts}§c hearts");
            config.set("withdraw.invalid-amount", "§cAmount must be positive!");
            
            // Shop Messages
            config.set("shop.opened", "§aOpened Heart Shop!");
            config.set("shop.no-permission", "§cYou don't have permission to buy this tier!");
            config.set("shop.not-enough-points", "§cYou don't have enough points! You need §e{price}§c points but only have §e{points}§c");
            config.set("shop.exceeds-max", "§cAdding that many hearts would exceed the maximum of {max} hearts!");
            config.set("shop.success", "§aYou bought §e{hearts}§a hearts for §e{price}§a points! You now have §e{total}§a hearts");
            
            // Death Messages
            config.set("death.no-killer", "§e{player}§c lost a heart! ❤");
            config.set("death.with-killer", "§e{victim}§c was defeated by §e{killer}§c!");
            
            // Team Messages
            config.set("team.not-in-team", "§cYou're not in a team!");
            config.set("team.create-success", "§aTeam §e{name}§a created successfully!");
            config.set("team.create-broadcast", "§e{player}§a created a new team: §e{name}");
            config.set("team.invite-success", "§aInvited §e{player}§a to team §e{team}");
            config.set("team.invite-notify", "§e{inviter}§a invited you to join team §e{team}§a! Use §e/team accept {team}§a or §e/team deny {team}");
            config.set("team.accept-success", "§aYou joined team §e{team}§a!");
            config.set("team.accept-notify", "§e{player}§a joined the team!");
            config.set("team.deny-success", "§aInvite from team §e{team}§a denied!");
            config.set("team.leave-success", "§aYou left team §e{team}§a!");
            config.set("team.leave-notify", "§e{player}§a left the team!");
            config.set("team.no-teams", "§cNo teams exist yet!");
            config.set("team.death-notify", "§c{player} was killed by {killer}!");
            config.set("team.kill-notify", "§a{player} killed {victim}!");
            
            config.save(messagesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to create messages.yml: " + e.getMessage());
        }
    }

    public void loadMessages() {
        messagesCache.clear();
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        
        for (String key : messagesConfig.getKeys(true)) {
            String value = messagesConfig.getString(key);
            if (value != null) {
                messagesCache.put(key, value);
            }
        }
    }

    public String getMessage(String key) {
        return messagesCache.getOrDefault(key, "§cMessage not found: " + key);
    }

    public String getMessage(String key, Map<String, String> replacements) {
        String message = getMessage(key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public String format(String key, String... args) {
        String message = getMessage(key);
        // Support for simple indexed replacements like {0}, {1}, etc.
        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i]);
        }
        return message;
    }
}
