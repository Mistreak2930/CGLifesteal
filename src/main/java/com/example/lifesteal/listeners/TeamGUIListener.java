package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.TeamGUI;
import com.example.lifesteal.gui.GuiUtil;
import com.example.lifesteal.system.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Handles team GUI interactions and button clicks
 */
public class TeamGUIListener implements Listener {

    private LifestealPlugin plugin;
    private TeamGUI teamGUI;
    private TeamManager teamManager;

    public TeamGUIListener(LifestealPlugin plugin) {
        this.plugin = plugin;
        this.teamGUI = new TeamGUI(plugin);
        this.teamManager = plugin.getTeamManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        boolean mainGui = GuiUtil.isGui(event.getView().getTopInventory(), "team-main");
        boolean membersGui = GuiUtil.isGui(event.getView().getTopInventory(), "team-members");
        if (!mainGui && !membersGui) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getView().getTopInventory().getSize()) {
            return;
        }

        if (mainGui) {
            handleMainTeamGUI(player, slot, event.getView().getTopInventory().getSize());
            return;
        }

        handleMembersGUI(player, slot);
    }

    /**
     * Handle clicks in the main team GUI
     * @param player the player
     * @param slot the clicked slot
     * @param inventoryTitle the inventory title
     */
    private void handleMainTeamGUI(Player player, int slot, int inventorySize) {
        TeamManager.Team team = teamManager.getPlayerTeam(player);

        if (team == null) {
            player.sendMessage("§c[Team] §cYou are not in a team!");
            player.closeInventory();
            return;
        }

        if (slot == GuiUtil.slot(plugin, "team-main", "info", 0, inventorySize)) {
            openTeamInfo(player, team);
        } else if (slot == GuiUtil.slot(plugin, "team-main", "members", 1, inventorySize)) {
            teamGUI.openMembersGUI(player);
        } else if (slot == GuiUtil.slot(plugin, "team-main", "invite", 2, inventorySize)) {
            player.closeInventory();
            player.sendMessage("§6[Team] §aType the player name in chat to invite:");
        } else if (slot == GuiUtil.slot(plugin, "team-main", "chat", 3, inventorySize)) {
            toggleTeamChat(player);
        } else if (slot == GuiUtil.slot(plugin, "team-main", "leave", 4, inventorySize)) {
            handleLeaveTeam(player, team);
        } else if (slot == GuiUtil.slot(plugin, "team-main", "delete", 5, inventorySize)) {
            if (team.getLeader().equals(player.getUniqueId())) {
                handleDeleteTeam(player, team);
            } else {
                player.sendMessage("§c[Team] §cOnly the owner can delete the team!");
            }
        }
    }

    /**
     * Handle clicks in the members GUI
     * @param player the player
     * @param slot the clicked slot
     */
    private void handleMembersGUI(Player player, int slot) {
        int inventorySize = player.getOpenInventory().getTopInventory().getSize();

        // Back button is at the last slot
        if (slot == GuiUtil.slot(plugin, "team-members", "back", inventorySize - 1, inventorySize)) {
            teamGUI.openTeamGUI(player);
            return;
        }

        // Member items are not clickable for now (just information)
        player.sendMessage("§7Members list is for information only.");
    }

    /**
     * Open team info message
     * @param player the player
     * @param team the team
     */
    private void openTeamInfo(Player player, TeamManager.Team team) {
        player.sendMessage("§6╔════════════════════════╗");
        player.sendMessage("§6║  §aTeam Information");
        player.sendMessage("§6╠════════════════════════╣");
        player.sendMessage("§6║  §aName: §e" + team.getName());
        
        org.bukkit.OfflinePlayer owner = Bukkit.getOfflinePlayer(team.getLeader());
        String ownerName = owner.getName() != null ? owner.getName() : "Unknown";
        player.sendMessage("§6║  §aOwner: §e" + ownerName);
        
        player.sendMessage("§6║  §aMembers: §e" + team.getMembers().size() + "/" + teamManager.getMaxTeamSize());
        player.sendMessage("§6╚════════════════════════╝");
    }

    /**
     * Toggle team chat for a player
     * @param player the player
     */
    private void toggleTeamChat(Player player) {
        player.closeInventory();
        player.sendMessage("§6[Team] §aTeam chat toggled!");
        // This would be better with a proper team chat state tracker
    }

    /**
     * Handle leaving the team
     * @param player the player
     * @param team the team
     */
    private void handleLeaveTeam(Player player, TeamManager.Team team) {
        player.closeInventory();
        
        if (teamManager.leaveTeam(player)) {
            player.sendMessage("§6[Team] §aYou have left the team!");
            
            /// Notify other team members
            for (java.util.UUID memberUUID : team.getMembers()) {
                Player member = Bukkit.getPlayer(memberUUID);
                if (member != null && member.isOnline()) {
                    member.sendMessage("§6[Team] §e" + player.getName() + "§a has left the team!");
                }
            }
        } else {
            player.sendMessage("§c[Team] §cFailed to leave team!");
        }
    }

    /**
     * Handle deleting the team (owner only)
     * @param player the player
     * @param team the team
     */
    private void handleDeleteTeam(Player player, TeamManager.Team team) {
        player.closeInventory();

        // Notify all members
        for (java.util.UUID memberUUID : team.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.sendMessage("§6[Team] §cThe team has been disbanded!");
            }
        }

        // Remove all members from the team
        for (java.util.UUID memberUUID : new java.util.ArrayList<>(team.getMembers())) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null) {
                teamManager.leaveTeam(member);
            }
        }

        player.sendMessage("§6[Team] §aTeam deleted successfully!");
        plugin.getLogger().info("Team " + team.getName() + " was deleted by " + player.getName());
    }
}
