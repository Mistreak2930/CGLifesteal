package com.example.lifesteal.gui;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.BundleManager;
import com.example.lifesteal.system.BundleManager.BundleReward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI for opening daily bundles and viewing their possible rewards.
 */
public class BundleShopGUI {

    private final LifestealPlugin plugin;
    private final BundleManager bundleManager;
    private final Player player;

    public BundleShopGUI(LifestealPlugin plugin, Player player) {
        this.plugin = plugin;
        this.bundleManager = plugin.getBundleManager();
        this.player = player;
    }

    public void open() {
        Inventory inventory = GuiUtil.createInventory(plugin, "bundles", 54,
            "<light_purple><bold>Daily Bundles</bold></light_purple>");
        ItemStack background = createPane(
            GuiUtil.material(plugin, "gui.theme.background", Material.BLACK_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, background);
        }

        ItemStack border = createPane(
            GuiUtil.material(plugin, "gui.theme.accent", Material.RED_STAINED_GLASS_PANE), " ");
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, border);
            inventory.setItem(i + 45, border);
        }
        for (int i = 9; i < 45; i += 9) {
            inventory.setItem(i, border);
            inventory.setItem(i + 8, border);
        }

        int availableBundles = bundleManager.getAvailableBundles(player);
        int maxBundles = plugin.getConfig().getInt("bundles.max-per-day", 3);
        long timeUntilReset = bundleManager.getTimeUntilReset(player);
        ItemStack bundle = createBundleButton(availableBundles, timeUntilReset);
        inventory.setItem(GuiUtil.slot(plugin, "bundles", "open", 22, inventory.getSize()), bundle);

        List<BundleReward> rewards = bundleManager.getAllPossibleRewards();
        int[] rewardSlots = {19, 20, 21, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        for (int i = 0; i < Math.min(rewards.size(), rewardSlots.length); i++) {
            BundleReward reward = rewards.get(i);
            inventory.setItem(rewardSlots[i], createItem(reward.getDisplayMaterial(),
                "§e" + reward.getType().replace("_", " ").replace("ITEM ", ""),
                "§7Possible bundle reward",
                "§7Amount: §f" + reward.getMinAmount() + " - " + reward.getMaxAmount()));
        }

        inventory.setItem(GuiUtil.slot(plugin, "bundles", "info", 40, inventory.getSize()),
            createItem(Material.PAPER, "§e§lBundle Information",
                "§7Available: §a" + availableBundles + "§7/§f" + maxBundles,
                "§7Total opened: §f" + bundleManager.getTotalBundlesClaimed(player),
                "§7Next reset: §e" + formatTime(timeUntilReset)));
        inventory.setItem(GuiUtil.slot(plugin, "bundles", "back", 48, inventory.getSize()),
            createItem(Material.ARROW, "§e§lBack", "§7Return to the main menu"));
        inventory.setItem(GuiUtil.slot(plugin, "bundles", "close", 49, inventory.getSize()),
            createItem(Material.BARRIER, "§c§lClose", "§7Close this menu"));

        player.openInventory(inventory);
    }

    private ItemStack createBundleButton(int availableBundles, long timeUntilReset) {
        if (availableBundles > 0) {
            ItemStack item = new ItemStack(Material.CHEST, Math.min(availableBundles, 64));
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§d§lDaily Bundle");
                meta.setLore(List.of(
                    "§7Open one bundle at a time.",
                    "",
                    "§aAvailable: §f" + availableBundles,
                    "§e» Click to open a bundle!"
                ));
                item.setItemMeta(meta);
            }
            return item;
        }
        return createItem(Material.GRAY_DYE, "§7§lNo Bundles Available",
            "§7You have opened all your bundles for today.",
            "§7Next reset: §e" + formatTime(timeUntilReset));
    }

    private String formatTime(long seconds) {
        if (seconds <= 0) {
            return "Ready!";
        }
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes, secs);
        }
        return minutes > 0 ? String.format("%dm %ds", minutes, secs) : String.format("%ds", secs);
    }

    private ItemStack createPane(Material material, String name) {
        return createItem(material, name);
    }

    private ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(new ArrayList<>(List.of(lore)));
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
