package com.example.lifesteal.util;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.GuiUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public final class HeartItemUtil {

    private HeartItemUtil() {
    }

    private static NamespacedKey amountKey() {
        return new NamespacedKey(LifestealPlugin.getInstance(), "deposit-heart-amount");
    }

    public static ItemStack createDepositHeartItem(LifestealPlugin plugin, int amount) {
        Material material = GuiUtil.material(plugin, "hearts.withdraw.material", Material.REDSTONE_BLOCK);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§c❤ Heart Item x" + amount);
            meta.setLore(java.util.Arrays.asList("§aAmount: §e" + amount,
                "§aRight-click or use /deposit to add"));
            meta.getPersistentDataContainer().set(amountKey(), PersistentDataType.INTEGER, amount);
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isDepositHeartItem(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        if (meta.getPersistentDataContainer().has(amountKey(), PersistentDataType.INTEGER)) {
            return true;
        }

        if (item.getType() != Material.REDSTONE_BLOCK) {
            return false;
        }

        if (meta.hasDisplayName()) {
            String displayName = meta.getDisplayName();
            String cleanName = displayName.replaceAll("§[0-9a-fk-or]", "");
            if (cleanName.toLowerCase().contains("heart item")) {
                return true;
            }
        }

        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore != null) {
                for (String line : lore) {
                    if (line != null && line.contains("Amount:")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static int getDepositHeartAmount(ItemStack item) {
        if (item == null) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Integer storedAmount = meta.getPersistentDataContainer().get(amountKey(), PersistentDataType.INTEGER);
            if (storedAmount != null) {
                return storedAmount;
            }
        }

        if (meta != null && meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore != null) {
                for (String line : lore) {
                    if (line != null && line.contains("Amount:")) {
                        try {
                            String amountText = line.replaceAll("[^0-9]", "");
                            if (!amountText.isEmpty()) {
                                return Integer.parseInt(amountText);
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }

        return 0;
    }
}
