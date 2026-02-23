package com.example.lifesteal.gui;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI system for team management.
 * Provides a chest-based interface for all team operations.
 */
public class TeamGUI {

    private TeamManager teamManager;

    public TeamGUI(LifestealPlugin plugin) {
        this.teamManager = plugin.getTeamManager();
    }

    /**
     * Open the main team GUI for a player
     * @param player the player to open the GUI for
     */
    public void openTeamGUI(Player player) {
        TeamManager.Team team = teamManager.getPlayerTeam(player);

        if (team == null) {
            player.sendMessage("§c[Team] §aYou are not in a team!");
            return;
        }

        // Create inventory (size 27 = 3 rows, 9 columns)
        Inventory gui = Bukkit.createInventory(null, 27, "§6Team: " + team.getName());

        // Get member names for info
        StringBuilder memberList = new StringBuilder();
        for (int i = 0; i < team.getMembers().size(); i++) {
            org.bukkit.OfflinePlayer member = Bukkit.getOfflinePlayer(team.getMembers().get(i));
            memberList.append(member.getName());
            if (i < team.getMembers().size() - 1) {
                memberList.append(", ");
            }
        }

        // Button 1: Team Info (Slot 0)
        ItemStack infoButton = createButton(Material.PAPER, "§6Team Info", new String[]{
            "§7Name: §e" + team.getName(),
            "§7Owner: §e" + (Bukkit.getOfflinePlayer(team.getLeader()).getName() != null ? 
                            Bukkit.getOfflinePlayer(team.getLeader()).getName() : "Unknown"),
            "§7Members: §e" + team.getMembers().size() + "/" + teamManager.getMaxTeamSize()
        });
        gui.setItem(0, infoButton);

        // Button 2: View Members (Slot 1)
        ItemStack membersButton = createButton(Material.SKULL_ITEM, "§aView Members", new String[]{
            "§7Click to see team members",
            "§e" + team.getMembers().size() + " members"
        });
        membersButton.setDurability((short) 3);
        gui.setItem(1, membersButton);

        // Button 3: Invite Player (Slot 2)
        ItemStack inviteButton = createButton(Material.EMERALD, "§aInvite Player", new String[]{
            "§7Click to invite a player",
            "§7to your team"
        });
        gui.setItem(2, inviteButton);

        // Button 4: Team Chat Toggle (Slot 3)
        ItemStack chatButton = createButton(Material.BOOK, "§aToggle Team Chat", new String[]{
            "§7Click to toggle team chat",
            "§7mode on/off"
        });
        gui.setItem(3, chatButton);

        // Button 5: Leave Team (Slot 4)
        ItemStack leaveButton = createButton(Material.REDSTONE, "§cLeave Team", new String[]{
            "§7Click to leave the team"
        });
        gui.setItem(4, leaveButton);

        // Button 6: Delete Team (Slot 5) - Only for owner
        if (team.getLeader().equals(player.getUniqueId())) {
            ItemStack deleteButton = createButton(Material.BARRIER, "§cDelete Team", new String[]{
                "§cOwner only!",
                "§7Click to permanently",
                "§7delete this team"
            });
            gui.setItem(5, deleteButton);
        }

        // Filler items (wool) to make it look nice
        ItemStack filler = createButton(Material.STAINED_GLASS_PANE, "§7", new String[0]);
        for (int i = 9; i < 27; i++) {
            if (gui.getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }

        player.openInventory(gui);
    }

    /**
     * Open the members list GUI
     * @param player the player opening the GUI
     */
    public void openMembersGUI(Player player) {
        TeamManager.Team team = teamManager.getPlayerTeam(player);

        if (team == null) {
            player.sendMessage("§c[Team] §aYou are not in a team!");
            return;
        }

        // Calculate inventory size (27 per page)
        int memberCount = team.getMembers().size();
        int invSize = Math.min(27, ((memberCount / 9) + 1) * 9);
        if (invSize < 27) invSize = 27; // Minimum size

        Inventory gui = Bukkit.createInventory(null, invSize, "§6Team Members (" + memberCount + ")");

        // Add member names as items
        int slot = 0;
        for (java.util.UUID memberUUID : team.getMembers()) {
            org.bukkit.OfflinePlayer member = Bukkit.getOfflinePlayer(memberUUID);
            String memberName = member.getName() != null ? member.getName() : "Unknown";
            
            ItemStack memberItem = createButton(Material.SKULL_ITEM, "§a" + memberName, new String[]{
                "§7UUID: §e" + memberUUID
            });
            memberItem.setDurability((short) 3);
            gui.setItem(slot++, memberItem);
        }

        // Back button
        ItemStack backButton = createButton(Material.ARROW, "§6Back", new String[]{
            "§7Click to go back"
        });
        gui.setItem(invSize - 1, backButton);

        player.openInventory(gui);
    }

    /**
     * Creates a button item with custom name and lore
     * @param material the material of the button
     * @param name the display name
     * @param lore the lore lines
     * @return the created ItemStack
     */
    private ItemStack createButton(Material material, String name, String[] lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            
            List<String> loreList = new ArrayList<>();
            for (String loreLine : lore) {
                loreList.add(loreLine);
            }
            meta.setLore(loreList);
            
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Get the team from a player
     * @param player the player
     * @return the team or null
     */
    public TeamManager.Team getTeam(Player player) {
        return teamManager.getPlayerTeam(player);
    }
}
