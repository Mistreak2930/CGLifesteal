package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.LotteryGUI;
import com.example.lifesteal.gui.GuiUtil;
import com.example.lifesteal.system.LotteryManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class LotteryListener implements Listener {
    
    private LifestealPlugin plugin;
    private LotteryManager lotteryManager;
    
    public LotteryListener(LifestealPlugin plugin, LotteryManager lotteryManager) {
        this.plugin = plugin;
        this.lotteryManager = lotteryManager;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!GuiUtil.isGui(event.getView().getTopInventory(), "lottery")) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getView().getTopInventory().getSize()) {
            return;
        }
        
        if (slot == GuiUtil.slot(plugin, "lottery", "buy", 13, 27)) {
            lotteryManager.buyTicket(player);
            // Refresh GUI
            new LotteryGUI(plugin, lotteryManager, player).open();
        }
        // Slot 11 = Refresh button
        else if (slot == GuiUtil.slot(plugin, "lottery", "pool", 11, 27)) {
            new LotteryGUI(plugin, lotteryManager, player).open();
        }
    }
}
