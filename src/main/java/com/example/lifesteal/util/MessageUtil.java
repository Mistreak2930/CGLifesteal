package com.example.lifesteal.util;

import com.example.lifesteal.LifestealPlugin;

/**
 * Utility class for consistent message formatting across the plugin
 */
public class MessageUtil {

    private static LifestealPlugin plugin;

    public static void setPlugin(LifestealPlugin p) {
        plugin = p;
    }

    /**
     * Get the main Lifesteal prefix
     */
    public static String getPrefix() {
        if (plugin != null) {
            return plugin.getMessageManager().getMessage("prefix.main");
        }
        return "§8[§cLifesteal§8]";
    }

    /**
     * Get the team prefix
     */
    public static String getTeamPrefix() {
        if (plugin != null) {
            return plugin.getMessageManager().getMessage("prefix.team");
        }
        return "§6[Team]";
    }

    /**
     * Get the shop prefix
     */
    public static String getShopPrefix() {
        if (plugin != null) {
            return plugin.getMessageManager().getMessage("prefix.shop");
        }
        return "§6[Shop]";
    }

    /**
     * Format a message with the plugin prefix
     */
    public static String format(String message) {
        return getPrefix() + " " + message;
    }

    /**
     * Format a success message
     */
    public static String success(String message) {
        return format("§a" + message);
    }

    /**
     * Format an error message
     */
    public static String error(String message) {
        return format("§c" + message);
    }

    /**
     * Format an info message
     */
    public static String info(String message) {
        return format("§e" + message);
    }

    /**
     * Color codes helper
     */
    public static String colorize(String text) {
        return text.replace("&", "§");
    }
}
