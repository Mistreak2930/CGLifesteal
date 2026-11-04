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
        Inventory inventory = GuiUtil.createInventory(plugin, "heartshop", 54, "<red><bold>Heart Shop</bold></red>");

        ItemStack background = createPane(GuiUtil.material(plugin, "gui.theme.background", Material.BLACK_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, background);
        }

        // Top border - accent colors
        ItemStack topAccent = createPane(GuiUtil.material(plugin, "gui.theme.accent", Material.RED_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, topAccent);
        }
        
        // Bottom border - accent colors
        ItemStack bottomAccent = createPane(GuiUtil.material(plugin, "gui.theme.secondary", Material.GRAY_STAINED_GLASS_PANE), " ");
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, bottomAccent);
        }

        // Side dividers
        ItemStack sideDivider = createPane(GuiUtil.material(plugin, "gui.theme.secondary", Material.GRAY_STAINED_GLASS_PANE), " ");
        for (int i = 9; i < 54; i += 9) {
            inventory.setItem(i, sideDivider);
            if (i + 8 < 54) {
                inventory.setItem(i + 8, sideDivider);
            }
        }

        // Get prices from config
        int price1Heart = plugin.getConfig().getInt("heartshop.prices.1-heart", 1000);
        int price5Hearts = plugin.getConfig().getInt("heartshop.prices.5-hearts", 4500);
        int price10Hearts = plugin.getConfig().getInt("heartshop.prices.10-hearts", 8000);

        // Header info section - Player Stats
        ItemStack playerStats = createPlayerStats(price1Heart);
        inventory.setItem(4, playerStats);

        // Heart Shop Items - Arranged horizontally with better spacing
        // Economy Tier - 1 Heart
        inventory.setItem(GuiUtil.slot(plugin, "heartshop", "one-heart", 19, 54), createShopItem(Material.REDSTONE, 1, "§c❤ Single Heart",
            "§eAmount: §a1 Heart",
            "§ePrice: §6" + price1Heart + " Points",
            "§7Cost per heart: §a" + formatPerHeart(price1Heart, 1),
            " ",
            "§aEconomy Choice",
            "§7Left-click to purchase"));

        // Value Tier - 5 Hearts
        inventory.setItem(GuiUtil.slot(plugin, "heartshop", "five-hearts", 22, 54), createShopItem(Material.GOLD_INGOT, 5, "§6❤ Heart Bundle",
            "§eAmount: §a5 Hearts",
            "§ePrice: §6" + price5Hearts + " Points",
            "§7Cost per heart: §a" + formatPerHeart(price5Hearts, 5),
            " ",
            "§6Best Balance",
            "§7Left-click to purchase"));

        // Premium Tier - 10 Hearts
        inventory.setItem(GuiUtil.slot(plugin, "heartshop", "ten-hearts", 25, 54), createShopItem(Material.DIAMOND, 10, "§b❤ Heart Pack",
            "§eAmount: §a10 Hearts",
            "§ePrice: §6" + price10Hearts + " Points",
            "§7Cost per heart: §a" + formatPerHeart(price10Hearts, 10),
            " ",
            "§bMassive Purchase",
            "§7Left-click to purchase"));

        // Middle row info
        ItemStack infoItem = createInfoItem(price1Heart, price5Hearts, price10Hearts);
        inventory.setItem(38, infoItem);

        ItemStack helpItem = createHelpItem();
        inventory.setItem(42, helpItem);

        // Tips and tricks
        ItemStack tipsItem = createTipsItem();
        inventory.setItem(41, tipsItem);

        // Close button
        ItemStack closeItem = createCloseItem();
        inventory.setItem(GuiUtil.slot(plugin, "heartshop", "close", 49, 54), closeItem);

        player.openInventory(inventory);
        player.sendMessage(plugin.getMessageManager().getMessage("shop.opened"));
    }

    private ItemStack createPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
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

    private ItemStack createPlayerStats(int price1Heart) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            int points = getPlayerPoints(player.getUniqueId());
            int hearts = plugin.getHeartManager().getHearts(player);
            int maxHearts = plugin.getHeartManager().getMaxHearts();

            meta.setDisplayName("§6✦ Your Stats ✦");
            List<String> lore = new ArrayList<>();
            lore.add("§7┌─────────────────────────┐");
            lore.add("  §ePoints Balance: §a" + points);
            lore.add("  §eYour Hearts: §a" + hearts + "§7/§a" + maxHearts);
            if (price1Heart > 0) {
                int canBuy = points / price1Heart;
                lore.add("  §eMaximum Purchase: §a" + canBuy + " hearts");
            }
            lore.add("§7└─────────────────────────┘");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createInfoItem(int price1Heart, int price5Hearts, int price10Hearts) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§e📚 Value Comparison");
            List<String> lore = new ArrayList<>();
            lore.add("§7┌─ Price Breakdown ─────┐");
            lore.add("  §e1 Heart: §a" + price1Heart + " pts");
            lore.add("    §7→ §a" + formatPerHeart(price1Heart, 1) + " pts/heart");
            lore.add(" ");
            lore.add("  §e5 Hearts: §a" + price5Hearts + " pts");
            lore.add("    §7→ §a" + formatPerHeart(price5Hearts, 5) + " pts/heart");
            lore.add(" ");
            lore.add("  §e10 Hearts: §a" + price10Hearts + " pts");
            lore.add("    §7→ §a" + formatPerHeart(price10Hearts, 10) + " pts/heart");
            lore.add("§7└───────────────────────┘");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    private ItemStack createHelpItem() {
        ItemStack item = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§9❓ How to Use");
            List<String> lore = new ArrayList<>();
            lore.add("§7┌────────────────────────┐");
            lore.add("  §aHow to Buy Hearts:");
            lore.add(" ");
            lore.add("  §7→ §aClick any package");
            lore.add("  §7→ §aPoints are deducted");
            lore.add("  §7→ §aHearts are added");
            lore.add(" ");
            lore.add("  §cInsufficient Points?");
            lore.add("  §aYou'll get a message!");
            lore.add("§7└────────────────────────┘");
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

    private ItemStack createTipsItem() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§e💡 Pro Tips");
            List<String> lore = new ArrayList<>();
            lore.add("§7─────────────────────────");
            lore.add("§a✓ §7Bundle purchases save money");
            lore.add("§a✓ §7Don't fall below 1 heart!");
            lore.add("§a✓ §7Earn more points in PvP");
            lore.add("§a✓ §7Max hearts is " + plugin.getHeartManager().getMaxHearts());
            lore.add("§7─────────────────────────");
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

    private String formatPerHeart(int price, int hearts) {
        if (hearts <= 0) {
            return "0";
        }
        double perHeart = (double) price / (double) hearts;
        return String.format("%.2f", perHeart);
    }
}

