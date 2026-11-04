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

/**
 * Main menu GUI that provides access to various features
 * - Warps
 * - Bundles Shop
 * - Heart Shop
 * - Lottery
 * - Daily Rewards
 * - Teams
 */
public class MenuGUI {
    
    private LifestealPlugin plugin;
    private Player player;
    
    public MenuGUI(LifestealPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }
    
    public void open() {
        Inventory inventory = GuiUtil.createInventory(plugin, "menu", 45, "<dark_red><bold>CGLifesteal</bold></dark_red>");

        ItemStack background = createPane(GuiUtil.material(plugin, "gui.theme.background", Material.BLACK_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < 45; i++) {
            inventory.setItem(i, background);
        }

        ItemStack borderPane = createPane(GuiUtil.material(plugin, "gui.theme.accent", Material.RED_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, borderPane);
            inventory.setItem(i + 36, borderPane);
        }
        for (int i = 9; i < 36; i += 9) {
            inventory.setItem(i, borderPane);
            inventory.setItem(i + 8, borderPane);
        }

        ItemStack title = createItem(Material.NETHER_STAR, "§6§l✦ Lifesteal Menu ✦",
            "§7Welcome " + player.getName() + "!",
            "§7Hearts: §c" + plugin.getHeartManager().getHearts(player));
        inventory.setItem(4, title);

        ItemStack bundlesShop = createItem(GuiUtil.material(plugin, "gui.materials.menu.bundles", Material.CHEST), "§d§lDaily Bundles",
            "§7Open daily bundles to receive rewards!",
            "",
            "§eAvailable Bundles: §a" + plugin.getBundleManager().getAvailableBundles(player) + "/" + plugin.getConfig().getInt("bundles.max-per-day", 3),
            "§7Claim bundles every 24 hours",
            "",
            "§e» Click to open!");
        inventory.setItem(GuiUtil.slot(plugin, "menu", "bundles", 20, 45), bundlesShop);

        ItemStack heartShop = createItem(GuiUtil.material(plugin, "gui.materials.menu.heartshop", Material.RED_DYE), "§c§lHeart Shop",
            "§7Purchase hearts with points", "", "§e» Click to open!");
        inventory.setItem(GuiUtil.slot(plugin, "menu", "heartshop", 22, 45), heartShop);

        if (plugin.hasLotteryManager()) {
            ItemStack lottery = createItem(GuiUtil.material(plugin, "gui.materials.menu.lottery", Material.EMERALD), "§a§lLottery",
                "§7Try your luck!",
                "",
                "§7Buy tickets and win big prizes",
                "",
                "§e» Click to open!");
            inventory.setItem(GuiUtil.slot(plugin, "menu", "lottery", 24, 45), lottery);
        }

        if (plugin.hasDailyRewardManager()) {
            boolean canClaim = plugin.getDailyRewardManager().canClaimReward(player);
            ItemStack dailyRewards = createItem(GuiUtil.material(plugin,
                canClaim ? "gui.materials.menu.daily-rewards-ready" : "gui.materials.menu.daily-rewards-claimed",
                canClaim ? Material.LIME_DYE : Material.GRAY_DYE),
                "§6§lDaily Rewards", 
                "§7Claim your daily rewards!",
                "",
                canClaim ? "§a✔ Ready to claim!" : "§c✖ Already claimed today",
                "",
                "§e» Click to open!");
            inventory.setItem(GuiUtil.slot(plugin, "menu", "daily-rewards", 29, 45), dailyRewards);
        }

        ItemStack teams = createItem(GuiUtil.material(plugin, "gui.materials.menu.teams", Material.PLAYER_HEAD), "§e§lTeams",
            "§7Manage your team",
            "",
            "§7Create or join a team",
            "§7Work together with friends",
            "",
            "§e» Click to open!");
        inventory.setItem(GuiUtil.slot(plugin, "menu", "teams", 31, 45), teams);

        int hearts = plugin.getHeartManager().getHearts(player);
        long orbits = plugin.getOrbitsManager().getOrbits(player.getUniqueId());
        ItemStack stats = createItem(GuiUtil.material(plugin, "gui.materials.menu.stats", Material.BOOK), "§b§lYour Stats",
            "§7Current Statistics",
            "",
            "§c❤ Hearts: §f" + hearts,
            "§d✦ Orbits: §f" + orbits,
            "§6⭐ Bundles Claimed: §f" + plugin.getBundleManager().getTotalBundlesClaimed(player));
        inventory.setItem(GuiUtil.slot(plugin, "menu", "stats", 33, 45), stats);

        ItemStack close = createItem(GuiUtil.material(plugin, "gui.materials.menu.close", Material.BARRIER), "§c§lClose",
            "§7Click to close this menu");
        inventory.setItem(GuiUtil.slot(plugin, "menu", "close", 40, 45), close);

        player.openInventory(inventory);
    }
    
    /**
     * Create a glass pane item
     */
    private ItemStack createPane(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Create a custom item with name and lore
     */
    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            meta.setDisplayName(name);
            
            if (lore.length > 0) {
                List<String> loreList = new ArrayList<>();
                for (String line : lore) {
                    loreList.add(line);
                }
                meta.setLore(loreList);
            }
            
            item.setItemMeta(meta);
        }
        
        return item;
    }
}
