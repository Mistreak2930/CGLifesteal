package com.example.lifesteal.gui;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.BundleManager.BundleReward;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/**
 * Minimal display used while a bundle reward is being randomized.
 */
public class BundleSpinGUI {

    private final LifestealPlugin plugin;

    public BundleSpinGUI(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    public Inventory open(Player player) {
        Inventory inventory = GuiUtil.createInventory(plugin, "bundle-spin", 27,
            "<light_purple><bold>Opening Bundle</bold></light_purple>");
        ItemStack background = new ItemStack(
            GuiUtil.material(plugin, "gui.theme.background", Material.BLACK_STAINED_GLASS_PANE));
        ItemMeta backgroundMeta = background.getItemMeta();
        if (backgroundMeta != null) {
            backgroundMeta.setDisplayName(" ");
            background.setItemMeta(backgroundMeta);
        }
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, background);
        }

        ItemStack border = new ItemStack(
            GuiUtil.material(plugin, "gui.theme.accent", Material.RED_STAINED_GLASS_PANE));
        ItemMeta borderMeta = border.getItemMeta();
        if (borderMeta != null) {
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);
        }
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, border);
            inventory.setItem(i + 18, border);
        }

        inventory.setItem(GuiUtil.slot(plugin, "bundle-spin", "reward", 13, inventory.getSize()),
            createDisplay(Material.CHEST, "§d§lOpening...", "§7Your reward is being randomized."));
        player.openInventory(inventory);
        return inventory;
    }

    public ItemStack createDisplay(BundleReward reward) {
        return createDisplay(reward.getDisplayMaterial(), reward.getDisplayName(),
            "§7Your reward is being randomized.");
    }

    private ItemStack createDisplay(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(List.of(lore));
            item.setItemMeta(meta);
        }
        return item;
    }
}
