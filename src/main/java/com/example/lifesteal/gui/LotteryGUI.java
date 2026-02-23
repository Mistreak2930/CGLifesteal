package com.example.lifesteal.gui;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.LotteryManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LotteryGUI {
    
    private LifestealPlugin plugin;
    private LotteryManager lotteryManager;
    private Player player;
    
    public LotteryGUI(LifestealPlugin plugin, LotteryManager lotteryManager, Player player) {
        this.plugin = plugin;
        this.lotteryManager = lotteryManager;
        this.player = player;
    }
    
    public void open() {
        Inventory inventory = Bukkit.createInventory(null, 27, "§6§l★ Lottery System ★");
        
        // Background
        ItemStack background = createPane((short) 15, " ");
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, background);
        }
        
        // Top border
        ItemStack topBorder = createPane((short) 14, " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, topBorder);
        }
        
        // Bottom border  
        ItemStack bottomBorder = createPane((short) 1, " ");
        for (int i = 18; i < 27; i++) {
            inventory.setItem(i, bottomBorder);
        }
        
        // Left border
        for (int i = 9; i < 18; i += 9) {
            inventory.setItem(i, createPane((short) 7, " "));
        }
        
        // Right border
        for (int i = 17; i < 18; i += 9) {
            inventory.setItem(i, createPane((short) 7, " "));
        }
        
        // Title and Info
        ItemStack lotteryInfo = createInfoItem();
        inventory.setItem(4, lotteryInfo);
        
        // Pool amount display
        ItemStack poolDisplay = createPoolDisplay();
        inventory.setItem(11, poolDisplay);
        
        // Buy ticket button
        ItemStack buyTicket = createBuyTicketButton();
        inventory.setItem(13, buyTicket);
        
        // Ticket info
        ItemStack ticketInfo = createTicketInfoButton();
        inventory.setItem(15, ticketInfo);
        
        // Stats section
        ItemStack statsDisplay = createStatsDisplay();
        inventory.setItem(20, statsDisplay);
        
        // Help section
        ItemStack helpDisplay = createHelpDisplay();
        inventory.setItem(24, helpDisplay);
        
        player.openInventory(inventory);
    }
    
    private ItemStack createInfoItem() {
        ItemStack item = new ItemStack(Material.EMERALD);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§l★ LOTTERY ★");
            List<String> lore = new ArrayList<>();
            lore.add("§e═══════════════════");
            lore.add("§aWin a chance to earn");
            lore.add("§agreat rewards!");
            lore.add(" ");
            lore.add("§eTicket Price: §c" + lotteryManager.getTicketPrice() + " points");
            lore.add("§eDraw: §cEvery Hour");
            lore.add("§e═══════════════════");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createPoolDisplay() {
        ItemStack item = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§e§l💰 Current Pool");
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§eCurrent Prize Pool:");
            lore.add("§a$" + formatMoney(lotteryManager.getLotteryPool()));
            lore.add(" ");
            lore.add("§7Click to refresh info");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createBuyTicketButton() {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§a§l✓ BUY TICKET");
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            
            if (lotteryManager.hasTicket(player.getUniqueId())) {
                lore.add("§c✗ Already have ticket");
                lore.add("§cWait for next draw!");
                meta.setDisplayName("§c§l✗ TICKET OWNED");
            } else {
                lore.add("§ePrice: §c$" + lotteryManager.getTicketPrice());
                lore.add("§aClick to purchase");
                lore.add(" ");
                lore.add("§7Your Balance: §e$" + String.format("%.2f", lotteryManager.getPlayerBalance(player)));
            }
            
            lore.add(" ");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createTicketInfoButton() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b§l📊 Draw Info");
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§eTickets Sold:");
            lore.add("§a" + lotteryManager.getTicketCount() + " tickets");
            lore.add(" ");
            lore.add("§eYour Status:");
            if (lotteryManager.hasTicket(player.getUniqueId())) {
                lore.add("§a✓ In this draw");
            } else {
                lore.add("§c✗ Not in draw");
            }
            lore.add(" ");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createStatsDisplay() {
        ItemStack item = new ItemStack(Material.GOLD_BLOCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§b§l★ Your Stats ★");
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§ePlayer: §a" + player.getName());
            lore.add("§eMoney: §a$" + String.format("%.2f", lotteryManager.getPlayerBalance(player)));
            lore.add("§eTickets: §a" + (lotteryManager.hasTicket(player.getUniqueId()) ? "1 (Active)" : "0"));
            lore.add(" ");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createHelpDisplay() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§l? HOW IT WORKS");
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add("§71. Buy a ticket");
            lore.add("§72. Lottery draws every hour");
            lore.add("§73. Winner gets the pool!");
            lore.add(" ");
            lore.add("§eGood Luck! 🍀");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }
    
    private ItemStack createPane(short data, String name) {
        ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, data);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            pane.setItemMeta(meta);
        }
        return pane;
    }
    
    private String formatMoney(double amount) {
        if (amount >= 1_000_000) {
            return String.format("%.1fM", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.1fK", amount / 1_000);
        } else {
            return String.format("%.0f", amount);
        }
    }
}
