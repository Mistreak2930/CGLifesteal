package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.DailyRewardGUI;
import com.example.lifesteal.gui.GuiUtil;
import com.example.lifesteal.system.DailyRewardManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class DailyRewardListener implements Listener {
    
    private LifestealPlugin plugin;
    private DailyRewardManager dailyRewardManager;
    
    public DailyRewardListener(LifestealPlugin plugin, DailyRewardManager dailyRewardManager) {
        this.plugin = plugin;
        this.dailyRewardManager = dailyRewardManager;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!GuiUtil.isGui(event.getView().getTopInventory(), "daily-rewards")) {
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
        
        if (slot == GuiUtil.slot(plugin, "daily-rewards", "claim", 13, 27)) {
            dailyRewardManager.claimReward(player);
            // Refresh GUI
            new DailyRewardGUI(plugin, dailyRewardManager, player).open();
        }
    }
}
