package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.BundleShopGUI;
import com.example.lifesteal.gui.GuiUtil;
import com.example.lifesteal.gui.HeartShopGUI;
import com.example.lifesteal.gui.LotteryGUI;
import com.example.lifesteal.gui.MenuGUI;
import com.example.lifesteal.gui.TeamGUI;
import com.example.lifesteal.system.BundleManager;
import com.example.lifesteal.system.BundleManager.BundleReward;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Listener for handling clicks in the Menu and Bundle GUIs
 */
public class BundleGUIListener implements Listener {
    
    private LifestealPlugin plugin;
    private BundleManager bundleManager;
    private final Set<UUID> openingBundles = new HashSet<>();
    
    public BundleGUIListener(LifestealPlugin plugin, BundleManager bundleManager) {
        this.plugin = plugin;
        this.bundleManager = bundleManager;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        String guiId;
        if (GuiUtil.isGui(event.getView().getTopInventory(), "menu")) {
            guiId = "menu";
        } else if (GuiUtil.isGui(event.getView().getTopInventory(), "bundles")) {
            guiId = "bundles";
        } else if (GuiUtil.isGui(event.getView().getTopInventory(), "bundle-rewards")) {
            guiId = "bundle-rewards";
        } else {
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
        
        if (guiId.equals("menu")) {
            handleMainMenuClick(player, slot);
        } else if (guiId.equals("bundles")) {
            handleBundleShopClick(player, slot);
        } else {
            handleBundleDetailClick(player, slot);
        }
    }
    
    /**
     * Handle clicks in the Main Menu GUI
     */
    private void handleMainMenuClick(Player player, int slot) {
        if (slot == GuiUtil.slot(plugin, "menu", "bundles", 20, 45)) {
            player.closeInventory();
            new BundleShopGUI(plugin, player).open();
            player.sendMessage("§8[§dBundles§8] §eOpening bundle shop...");
        } else if (slot == GuiUtil.slot(plugin, "menu", "heartshop", 22, 45)) {
            if (plugin.getConfig().getBoolean("heartshop.enabled", true) && plugin.hasPlayerPoints()) {
                player.closeInventory();
                new HeartShopGUI(plugin, player).open();
            } else {
                player.sendMessage(plugin.getMessageManager().getMessage("shop.disabled"));
            }
        } else if (slot == GuiUtil.slot(plugin, "menu", "lottery", 24, 45)) {
            if (plugin.hasLotteryManager()) {
                player.closeInventory();
                new LotteryGUI(plugin, plugin.getLotteryManager(), player).open();
            } else {
                player.sendMessage("§8[§cCGLifesteal§8] §cLottery system not available!");
            }
        } else if (slot == GuiUtil.slot(plugin, "menu", "teams", 29, 45)) {
            player.closeInventory();
            new TeamGUI(plugin).openTeamGUI(player);
        } else if (slot == GuiUtil.slot(plugin, "menu", "close", 40, 45)) {
            player.closeInventory();
        }
    }
    
    /**
     * Handle clicks in the Bundle Shop GUI
     */
    private void handleBundleShopClick(Player player, int slot) {
        int[] bundleSlots = {
            GuiUtil.slot(plugin, "bundles", "bundle-1", 20, 54),
            GuiUtil.slot(plugin, "bundles", "bundle-2", 22, 54),
            GuiUtil.slot(plugin, "bundles", "bundle-3", 24, 54)
        };
        for (int i = 0; i < bundleSlots.length; i++) {
            if (slot == bundleSlots[i]) {
                int bundleNumber = i + 1;
                int availableBundles = bundleManager.getAvailableBundles(player);
                if (bundleNumber <= availableBundles) {
                    player.closeInventory();
                    new BundleShopGUI(plugin, player).openBundleDetail();
                    player.sendMessage("§8[§dBundles§8] §eViewing bundle contents...");
                } else {
                    player.sendMessage("§8[§dBundles§8] §cThis bundle has already been claimed!");
                    player.sendMessage("§8[§dBundles§8] §7Next reset in: §e"
                        + formatTime(bundleManager.getTimeUntilReset(player)));
                }
                return;
            }
        }

        if (slot == GuiUtil.slot(plugin, "bundles", "back", 48, 54)) {
            player.closeInventory();
            new MenuGUI(plugin, player).open();
        } else if (slot == GuiUtil.slot(plugin, "bundles", "close", 49, 54)) {
            player.closeInventory();
        }
    }
    
    /**
     * Handle clicks in the Bundle Detail GUI
     */
    private void handleBundleDetailClick(Player player, int slot) {
        if (slot == GuiUtil.slot(plugin, "bundle-rewards", "next-reward", 13, 54)
            || slot == GuiUtil.slot(plugin, "bundle-rewards", "open", 40, 54)) {
            if (!bundleManager.canClaimBundle(player)) {
                player.sendMessage("§8[§dBundles§8] §cYou don't have any bundles available!");
                long timeUntil = bundleManager.getTimeUntilReset(player);
                player.sendMessage("§8[§dBundles§8] §7Next reset in: §e" + formatTime(timeUntil));
                return;
            }
            animateBundleOpening(player);
        } else if (slot == GuiUtil.slot(plugin, "bundle-rewards", "back", 48, 54)) {
            player.closeInventory();
            new BundleShopGUI(plugin, player).open();
        } else if (slot == GuiUtil.slot(plugin, "bundle-rewards", "close", 49, 54)) {
            player.closeInventory();
        }
    }

    private void animateBundleOpening(Player player) {
        UUID playerId = player.getUniqueId();
        if (!openingBundles.add(playerId)) {
            return;
        }

        Inventory inventory = player.getOpenInventory().getTopInventory();
        Optional<BundleReward> availableReward = bundleManager.getCurrentReward(player);
        if (availableReward.isEmpty()) {
            openingBundles.remove(playerId);
            player.sendMessage(plugin.getMessageManager().getMessage("bundles.no-rewards"));
            return;
        }
        int rewardSlot = GuiUtil.slot(plugin, "bundle-rewards", "next-reward", 13, inventory.getSize());
        BundleReward selectedReward = availableReward.get();
        List<BundleReward> possibleRewards = bundleManager.getAllPossibleRewards();

        int durationTicks = Math.max(1, plugin.getConfig().getInt("bundles.animation.duration-ticks", 40));
        int intervalTicks = Math.max(1, plugin.getConfig().getInt("bundles.animation.cycle-ticks", 3));
        int[] elapsedTicks = {0};
        List<BundleReward> animationRewards = possibleRewards;

        Bukkit.getScheduler().runTaskTimer(plugin, task -> {
            if (!player.isOnline() || player.getOpenInventory().getTopInventory() != inventory) {
                openingBundles.remove(playerId);
                task.cancel();
                return;
            }

            elapsedTicks[0] += intervalTicks;
            if (elapsedTicks[0] >= durationTicks) {
                Optional<BundleReward> reward = bundleManager.openBundle(player, selectedReward);
                openingBundles.remove(playerId);
                task.cancel();

                if (reward.isEmpty()) {
                    player.sendMessage(plugin.getMessageManager().getMessage("bundles.open-failed"));
                    return;
                }

                player.sendMessage(plugin.getMessageManager().getMessage("bundles.opened")
                    .replace("{reward}", reward.get().getDisplayName()));
                player.closeInventory();
                if (bundleManager.canClaimBundle(player)) {
                    new BundleShopGUI(plugin, player).openBundleDetail();
                } else {
                    new BundleShopGUI(plugin, player).open();
                }
                return;
            }

            BundleReward preview = animationRewards.get(
                (int) (Math.random() * animationRewards.size()));
            inventory.setItem(rewardSlot, createRewardDisplay(preview));
        }, 0L, intervalTicks);
    }

    private ItemStack createRewardDisplay(BundleReward reward) {
        ItemStack item = new ItemStack(reward.getDisplayMaterial());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(reward.getDisplayName());
            item.setItemMeta(meta);
        }
        return item;
    }
    
    /**
     * Format time in seconds to readable format
     */
    private String formatTime(long seconds) {
        if (seconds <= 0) {
            return "Ready!";
        }
        
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }
}
