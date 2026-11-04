package com.example.lifesteal.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Map;

public final class GuiUtil {

    private GuiUtil() {
    }

    public static Inventory createInventory(JavaPlugin plugin, String id, int size, String defaultTitle) {
        return createInventory(plugin, id, size, defaultTitle, java.util.Collections.emptyMap());
    }

    public static Inventory createInventory(JavaPlugin plugin, String id, int size, String defaultTitle,
                                            Map<String, String> placeholders) {
        String title = plugin.getConfig().getString("gui.titles." + id, defaultTitle);
        for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
            title = title.replace("{" + placeholder.getKey() + "}", placeholder.getValue());
        }
        GuiHolder holder = new GuiHolder(id);
        Inventory inventory = Bukkit.createInventory(holder, size, MiniMessage.miniMessage().deserialize(title));
        holder.setInventory(inventory);
        return inventory;
    }

    public static boolean isGui(Inventory inventory, String id) {
        return inventory != null && inventory.getHolder() instanceof GuiHolder
            && ((GuiHolder) inventory.getHolder()).getId().equals(id);
    }

    public static int slot(JavaPlugin plugin, String gui, String key, int fallback, int inventorySize) {
        int configured = plugin.getConfig().getInt("gui.slots." + gui + "." + key, fallback);
        return configured >= 0 && configured < inventorySize ? configured : fallback;
    }

    public static int inventorySize(JavaPlugin plugin, String path, int fallback, int minimum, int maximum) {
        int configured = plugin.getConfig().getInt(path, fallback);
        return configured >= minimum && configured <= maximum && configured % 9 == 0 ? configured : fallback;
    }

    public static Material material(JavaPlugin plugin, String path, Material fallback) {
        String configured = plugin.getConfig().getString(path);
        if (configured == null) {
            return fallback;
        }

        Material material = Material.matchMaterial(configured.trim().toUpperCase(Locale.ROOT));
        return material != null && material.isItem() ? material : fallback;
    }

    public static final class GuiHolder implements InventoryHolder {
        private final String id;
        private Inventory inventory;

        private GuiHolder(String id) {
            this.id = id;
        }

        private void setInventory(Inventory inventory) {
            this.inventory = inventory;
        }

        private String getId() {
            return id;
        }

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}