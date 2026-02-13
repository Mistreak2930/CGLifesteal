package com.example.lifesteal.gui;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HeartShopGUI {

    private LifestealPlugin plugin;
    private Player player;

    public HeartShopGUI(LifestealPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public void open() {
        Inventory inventory = Bukkit.createInventory(null, 54, "§c❤ Heart Shop - Select Your Package");

        // Fill borders with decorative items
        ItemStack border = createBorder();
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, border); // Top row
            inventory.setItem(45 + i, border); // Bottom row
        }
        for (int i = 9; i < 45; i += 9) {
            inventory.setItem(i, border); // Left column
            inventory.setItem(i + 8, border); // Right column
        }

        // Header info section
        ItemStack playerStats = createPlayerStats();
        inventory.setItem(4, playerStats); // Center top

        // Value tier items - Economy
        inventory.setItem(11, createShopItem(Material.REDSTONE_BLOCK, 1, "§c❤ Single Heart", 
            "§eAmount: §a1 Heart",
            "§ePrice: §610 Points",
            "§eRatio: §a0.1 H/P",
            " ",
            "§7Click to purchase"));

        // Value tier items - Standard
        inventory.setItem(20, createShopItem(Material.REDSTONE_BLOCK, 5, "§c❤ Heart Bundle", 
            "§eAmount: §a5 Hearts",
            "§ePrice: §645 Points",
            "§eRatio: §a0.11 H/P §7(+10% value)",
            " ",
            "§7Click to purchase"));

        // Value tier items - bulk
        inventory.setItem(29, createShopItem(Material.REDSTONE_BLOCK, 10, "§c❤ Heart Pack", 
            "§eAmount: §a10 Hearts",
            "§ePrice: §680 Points",
            "§eRatio: §a0.125 H/P §7(+25% value)",
            "§6Best Value!",
            "§7Click to purchase"));

        // Premium tier
        inventory.setItem(38, createShopItem(Material.REDSTONE_BLOCK, 20, "§c❤ Max Hearts §6[VIP]", 
            "§eAmount: §a20 Hearts",
            "§ePrice: §6150 Points",
            "§eRatio: §a0.133 H/P §7(+33% value)",
            "§c⭐ MAXIMUM HEARTS ⭐",
            "§cVIP Permission Required",
            "§7Click to purchase"));

        // Special offers section header
        ItemStack specialHeader = createCategoryItem(Material.SIGN, "§e⭐ Special Offers ⭐");
        inventory.setItem(22, specialHeader);

        // Combo deal info
        ItemStack comboInfo = createInfoItem();
        inventory.setItem(13, comboInfo);

        // Help/Info item
        ItemStack helpItem = createHelpItem();
        inventory.setItem(31, helpItem);

        // Close button
        ItemStack closeItem = createCloseItem();
        inventory.setItem(49, closeItem);

        player.openInventory(inventory);
        player.sendMessage(plugin.getMessageManager().getMessage("shop.opened"));
    }

    private ItemStack createBorder() {
        ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createCategoryItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(new ArrayList<>());
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createShopItem(Material material, int amount, String name, String... lore) {
        ItemStack item = new ItemStack(material, Math.min(amount, 64));
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            List<String> loreList = new ArrayList<>();
            for (String line : lore) {
                loreList.add(line);
            }
            meta.setLore(loreList);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createPlayerStats() {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            int points = getPlayerPoints(player.getUniqueId());
            int hearts = plugin.getHeartManager().getHearts(player);
            int maxHearts = plugin.getHeartManager().getMaxHearts();

            meta.setDisplayName("§6✦ Your Stats ✦");
            List<String> lore = new ArrayList<>();
            lore.add("§7─────────────────────────");
            lore.add("§ePoints Balance: §a" + points);
            lore.add("§eYour Hearts: §a" + hearts + "§7/§a" + maxHearts);
            
            int canBuy = points / 10; // Cheapest item is 10 points
            lore.add("§eYou can buy up to: §a" + canBuy + " single hearts");
            
            lore.add("§7─────────────────────────");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createInfoItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§e📚 Value Comparison");
            List<String> lore = new ArrayList<>();
            lore.add("§7─────────────────────────");
            lore.add("§e1 Heart: §a10 pts §7(0.1)");
            lore.add("§e5 Hearts: §a45 pts §7(0.11)");
            lore.add("§e10 Hearts: §a80 pts §7(0.125)");
            lore.add("§e20 Hearts: §a150 pts §7(0.133)");
            lore.add("§7─────────────────────────");
            lore.add("§7The bigger the bundle,");
            lore.add("§7the better your value!");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createHelpItem() {
        ItemStack item = new ItemStack(Material.BOOK_AND_QUILL);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§9❓ How to Use");
            List<String> lore = new ArrayList<>();
            lore.add("§7─────────────────────────");
            lore.add("§eClick on any package:");
            lore.add(" ");
            lore.add("§a✓ §7Your points are deducted");
            lore.add("§a✓ §7Hearts are added");
            lore.add("§a✓ §7Inventory updates instantly");
            lore.add(" ");
            lore.add("§cIf you don't have enough");
            lore.add("§cpoints, you'll be notified.");
            lore.add("§7─────────────────────────");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§c✖ Close Shop");
            List<String> lore = new ArrayList<>();
            lore.add("§7Click to close this menu");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private int getPlayerPoints(UUID uuid) {
        try {
            Object api = plugin.getPlayerPointsAPI();
            if (api != null) {
                java.lang.reflect.Method method = api.getClass().getMethod("look", java.util.UUID.class);
                Object result = method.invoke(api, uuid);
                return ((Number) result).intValue();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get player points: " + e.getMessage());
        }
        return 0;
    }
}

