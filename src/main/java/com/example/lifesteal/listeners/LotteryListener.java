package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.LotteryGUI;
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
        if (!event.getView().getTitle().contains("Lottery")) {
            return;
        }
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        
        // Slot 13 = Buy ticket button
        if (slot == 13) {
            lotteryManager.buyTicket(player);
            // Refresh GUI
            new LotteryGUI(plugin, lotteryManager, player).open();
        }
        // Slot 11 = Refresh button
        else if (slot == 11) {
            new LotteryGUI(plugin, lotteryManager, player).open();
        }
    }
}
