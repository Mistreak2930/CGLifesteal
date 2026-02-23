package com.example.lifesteal.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public final class HeartItemUtil {

    private HeartItemUtil() {
    }

    public static boolean isDepositHeartItem(ItemStack item) {
        if (item == null || item.getType() != Material.REDSTONE_BLOCK) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
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
        if (item == null || item.getType() != Material.REDSTONE_BLOCK) {
            return 0;
        }

        ItemMeta meta = item.getItemMeta();
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

        if (item.getDurability() > 0) {
            return item.getDurability();
        }

        return 0;
    }
}
