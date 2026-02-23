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
        Inventory inventory = Bukkit.createInventory(null, 54, "§6§l✦ Main Menu ✦");
        
        // Fill background with black stained glass pane
        ItemStack background = createPane(Material.STAINED_GLASS_PANE, " ");
        background.setDurability((short) 15); // Black
        for (int i = 0; i < 54; i++) {
            inventory.setItem(i, background);
        }
        
        // Create decorative border with colored panes
        ItemStack borderPane = createPane(Material.STAINED_GLASS_PANE, "§e§l▬");
        borderPane.setDurability((short) 4); // Yellow
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, borderPane);
            inventory.setItem(i + 45, borderPane);
        }
        for (int i = 9; i < 45; i += 9) {
            inventory.setItem(i, borderPane);
            inventory.setItem(i + 8, borderPane);
        }
        
        // Title item in center top
        ItemStack title = createItem(Material.NETHER_STAR, "§6§l✦ Lifesteal Menu ✦", 
            "§7Welcome " + player.getName() + "!",
            "§7Select an option below");
        inventory.setItem(4, title);
        
        // Bundles Shop (slot 20)
        ItemStack bundlesShop = createItem(Material.CHEST, "§d§lDaily Bundles", 
            "§7Open daily bundles to receive rewards!",
            "",
            "§eAvailable Bundles: §a" + plugin.getBundleManager().getAvailableBundles(player) + "/3",
            "§7Claim bundles every 24 hours",
            "",
            "§e» Click to open!");
        inventory.setItem(20, bundlesShop);
        
        // Warps (slot 22)
        ItemStack warps = createItem(Material.ENDER_PEARL, "§b§lWarps", 
            "§7Teleport to various locations",
            "",
            "§7Coming Soon!",
            "",
            "§c» Currently Disabled");
        inventory.setItem(22, warps);
        
        // Heart Shop (slot 24)
        ItemStack heartShop = createItem(Material.INK_SACK, "§c§lHeart Shop", 
            "§7Purchase hearts and items",
            "",
            "§7Coming Soon!",
            "",
            "§c» Currently Disabled");
        inventory.setItem(24, heartShop);
        
        // Lottery (slot 29)
        if (plugin.hasLotteryManager()) {
            ItemStack lottery = createItem(Material.EMERALD, "§a§lLottery", 
                "§7Try your luck!",
                "",
                "§7Buy tickets and win big prizes",
                "",
                "§e» Click to open!");
            inventory.setItem(29, lottery);
        }
        
        // Daily Rewards (slot 31)
        if (plugin.hasDailyRewardManager()) {
            boolean canClaim = plugin.getDailyRewardManager().canClaimReward(player);
            ItemStack dailyRewards = createItem(
                canClaim ? Material.INK_SACK : Material.INK_SACK, 
                "§6§lDaily Rewards", 
                "§7Claim your daily rewards!",
                "",
                canClaim ? "§a✔ Ready to claim!" : "§c✖ Already claimed today",
                "",
                "§e» Click to open!");
            dailyRewards.setDurability(canClaim ? (short) 10 : (short) 8); // Lime or Gray
            inventory.setItem(31, dailyRewards);
        }
        
        // Teams (slot 33)
        ItemStack teams = createItem(Material.SKULL_ITEM, "§e§lTeams", 
            "§7Manage your team",
            "",
            "§7Create or join a team",
            "§7Work together with friends",
            "",
            "§e» Click to open!");
        inventory.setItem(33, teams);
        
        // Player Stats (slot 40)
        int hearts = plugin.getHeartManager().getHearts(player);
        long orbits = plugin.getOrbitsManager().getOrbits(player.getUniqueId());
        ItemStack stats = createItem(Material.BOOK, "§b§lYour Stats", 
            "§7Current Statistics",
            "",
            "§c❤ Hearts: §f" + hearts,
            "§d✦ Orbits: §f" + orbits,
            "§6⭐ Bundles Claimed: §f" + plugin.getBundleManager().getTotalBundlesClaimed(player));
        inventory.setItem(40, stats);
        
        // Close button (slot 49)
        ItemStack close = createItem(Material.BARRIER, "§c§lClose", 
            "§7Click to close this menu");
        inventory.setItem(49, close);
        
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
