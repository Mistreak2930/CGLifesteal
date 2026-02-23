package com.example.lifesteal.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

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
            config.set("prefix.main", "§8[§cLifesteal§8]");
            config.set("prefix.team", "§6[Team]");
            config.set("prefix.shop", "§6[Shop]");
            
            // General Messages
            config.set("general.no-permission", "§cYou don't have permission!");
            config.set("general.player-not-found", "§cPlayer not found!");
            config.set("general.invalid-amount", "§cInvalid amount!");
            config.set("general.only-players", "§cOnly players can use this command!");
            config.set("general.command-success", "§aCommand executed successfully!");
            config.set("general.command-failed", "§cCommand failed! Check console for errors.");
            config.set("general.kick-message", "§cYou have been removed from the server!");
            
            // Ban Messages
            config.set("ban.ran-out-of-hearts", "You ran out of hearts");
            config.set("ban.notification", "§c{player} ran out of hearts and has been banned!");
            
            // Hearts Messages
            config.set("hearts.view-own", "§aYou have §e{hearts}§a hearts");
            config.set("hearts.view-other", "§a{player} has §e{hearts}§a hearts");
            config.set("hearts.give-success", "§aGave §e{amount}§a hearts to §e{player}§a (§e{old}§a -> §e{new}§a)");
            config.set("hearts.give-notify", "§e{admin}§a gave you §e{amount}§a hearts!");
            config.set("hearts.take-success", "§aRemoved §e{amount}§a hearts from §e{player}§a (§e{old}§a -> §e{new}§a)");
            config.set("hearts.take-notify", "§e{admin}§a removed §c{amount}§a hearts from you!");
            config.set("hearts.set-success", "§aSet hearts for §e{player}§a to §e{amount}");
            config.set("hearts.set-notify", "§aYour hearts were set to §e{amount}§a!");
            config.set("hearts.reset-all", "§aReset all players' hearts to §e10");
            config.set("hearts.reset-notify", "§aAll players' hearts have been reset to §e10");
            config.set("hearts.max-hearts", "§cCannot exceed maximum of §e{max}§c hearts!");
            config.set("hearts.min-hearts", "§cHearts cannot go below §e0");
            
            // Withdraw Messages
            config.set("withdraw.success", "§aWithdrew §e{amount}§a hearts! You now have §e{remaining}§a hearts");
            config.set("withdraw.success-dropped", "§aWithdrew §e{amount}§a hearts! (dropped due to full inventory)");
            config.set("withdraw.not-enough", "§cYou don't have enough hearts! You have §e{hearts}§c hearts");
            config.set("withdraw.invalid-amount", "§cAmount must be positive!");
            
            // Deposit Messages
            config.set("deposit.success", "§aDeposited §e{amount}§a hearts! You now have §e{total}§a hearts");
            config.set("deposit.invalid-item", "§cYou must hold a heart item!");

            // Orbits Messages
            config.set("orbits.view-self", "§aYou have §e{orbits}§a orbits");
            config.set("orbits.view-other", "§a{player} has §e{orbits}§a orbits");
            config.set("orbits.give-success", "§aGave §e{amount}§a orbits to §e{player}§a (Total: §e{total}§a)");
            config.set("orbits.give-notify", "§e{admin}§a gave you §e{amount}§a orbits! (Total: §e{total}§a)");
            config.set("orbits.take-success", "§aRemoved §e{amount}§a orbits from §e{player}§a (Total: §e{total}§a)");
            config.set("orbits.take-notify", "§e{admin}§a removed §c{amount}§a orbits from you! (Total: §e{total}§a)");
            
            // Shop Messages
            config.set("shop.opened", "§aOpened Heart Shop!");
            config.set("shop.no-permission", "§cYou don't have permission to buy this tier!");
            config.set("shop.not-enough-points", "§cYou don't have enough points! You need §e{price}§c points but only have §e{points}§c");
            config.set("shop.exceeds-max", "§cAdding that many hearts would exceed the maximum of {max} hearts!");
            config.set("shop.success", "§aYou bought §e{hearts}§a hearts for §e{price}§a points! You now have §e{total}§a hearts");
            
            // Death Messages
            config.set("death.no-killer", Arrays.asList(
                "§e{player}§c lost a heart to the void!",
                "§e{player}§c couldn't escape fate!",
                "§e{player}§c slipped away... and a heart too!",
                "§e{player}§c met an untimely end!",
                "§e{player}§c's adventure came to an end!",
                "§e{player}§c was taken by the unknown!",
                "§c❤ §e{player}§c lost a heart!",
                "§e{player}§c fell to the void!",
                "§e{player}§c respects nature's power!"
            ));
            config.set("death.with-killer", Arrays.asList(
                "§e{victim}§c was defeated by §e{killer}§c!",
                "§e{killer}§c claimed a heart from §e{victim}§c!",
                "§e{victim}§c fell to §e{killer}§c's blade!",
                "§e{victim}§c was eliminated by §e{killer}§c!",
                "§e{killer}§c struck down §e{victim}§c!",
                "§e{victim}§c couldn't match §e{killer}§c!",
                "§e{killer}§c overpowered §e{victim}§c!",
                "§c❤ §e{killer}§c defeated §e{victim}§c!",
                "§e{victim}§c lost to §e{killer}§c's combat!",
                "§e{victim}§c was outmatched by §e{killer}§c!",
                "§e{killer}§c proved superior to §e{victim}§c!"
            ));
            
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

    public String getRandomMessage(String key, Map<String, String> replacements) {
        String message = getRandomMessage(key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return message;
    }

    public String getRandomMessage(String key) {
        if (messagesConfig != null && messagesConfig.isList(key)) {
            List<String> options = messagesConfig.getStringList(key);
            if (!options.isEmpty()) {
                int index = ThreadLocalRandom.current().nextInt(options.size());
                return options.get(index);
            }
        }
        return getMessage(key);
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
