package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.gui.TeamGUI;
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
        String inventoryTitle = event.getInventory().getName();

        // Check if this is a team GUI inventory
        if (!inventoryTitle.startsWith("§6Team:") && !inventoryTitle.startsWith("§6Team Members")) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot < 0 || slot >= event.getInventory().getSize()) {
            return;
        }

        // Handle main team GUI
        if (inventoryTitle.startsWith("§6Team:")) {
            handleMainTeamGUI(player, slot, inventoryTitle);
            return;
        }

        // Handle members GUI
        if (inventoryTitle.startsWith("§6Team Members")) {
            handleMembersGUI(player, slot);
        }
    }

    /**
     * Handle clicks in the main team GUI
     * @param player the player
     * @param slot the clicked slot
     * @param inventoryTitle the inventory title
     */
    private void handleMainTeamGUI(Player player, int slot, String inventoryTitle) {
        TeamManager.Team team = teamManager.getPlayerTeam(player);

        if (team == null) {
            player.sendMessage("§c[Team] §cYou are not in a team!");
            player.closeInventory();
            return;
        }

        switch (slot) {
            case 0:
                // Team Info button
                openTeamInfo(player, team);
                break;
            case 1:
                // View Members button
                teamGUI.openMembersGUI(player);
                break;
            case 2:
                // Invite Player button
                player.closeInventory();
                player.sendMessage("§6[Team] §aType the player name in chat to invite:");
                // Store invitation state (simplified - could use a tracker class)
                break;
            case 3:
                // Team Chat Toggle button
                toggleTeamChat(player);
                break;
            case 4:
                // Leave Team button
                handleLeaveTeam(player, team);
                break;
            case 5:
                // Delete Team button (owner only)
                if (team.getLeader().equals(player.getUniqueId())) {
                    handleDeleteTeam(player, team);
                } else {
                    player.sendMessage("§c[Team] §cOnly the owner can delete the team!");
                }
                break;
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
        if (slot == inventorySize - 1) {
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
