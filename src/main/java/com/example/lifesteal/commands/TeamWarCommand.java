package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.TeamManager;
import com.example.lifesteal.system.TeamWarManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Command to manage team wars
 * Usage: /teamwar <declare|accept|deny|info> [teamName]
 */
public class TeamWarCommand implements CommandExecutor {
    
    private LifestealPlugin plugin;
    
    public TeamWarCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§8[§cWars§8] §cOnly players can use this command!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!plugin.hasTeamWarManager()) {
            player.sendMessage("§8[§cWars§8] §cTeam wars are not enabled!");
            return true;
        }
        
        TeamManager.Team playerTeam = plugin.getTeamManager().getPlayerTeam(player);
        
        if (playerTeam == null) {
            player.sendMessage("§8[§cWars§8] §cYou must be in a team to use this command!");
            return true;
        }
        
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        
        String subcommand = args[0].toLowerCase();
        
        switch (subcommand) {
            case "declare":
                if (args.length < 2) {
                    player.sendMessage("§8[§cWars§8] §cUsage: /teamwar declare <teamName>");
                    return true;
                }
                handleDeclare(player, playerTeam, args[1]);
                break;
                
            case "accept":
                handleAccept(player, playerTeam);
                break;
                
            case "deny":
                handleDeny(player, playerTeam);
                break;
                
            case "info":
                handleInfo(player, playerTeam);
                break;
                
            case "list":
                handleList(player);
                break;
                
            default:
                sendHelp(player);
                break;
        }
        
        return true;
    }
    
    /**
     * Handle war declaration
     */
    private void handleDeclare(Player player, TeamManager.Team team, String targetTeamName) {
        boolean success = plugin.getTeamWarManager().declareWar(team.getName(), targetTeamName, player);
        
        if (!success) {
            // Error messages are handled in TeamWarManager
            return;
        }
        
        player.sendMessage("§c§l[WAR] §aWar declaration sent to §e" + targetTeamName + "§a!");
    }
    
    /**
     * Handle war acceptance
     */
    private void handleAccept(Player player, TeamManager.Team team) {
        boolean success = plugin.getTeamWarManager().acceptWar(team.getName(), player);
        
        if (!success) {
            // Error messages are handled in TeamWarManager
            return;
        }
        
        player.sendMessage("§c§l[WAR] §aWar accepted! The battle begins!");
    }
    
    /**
     * Handle war denial
     */
    private void handleDeny(Player player, TeamManager.Team team) {
        boolean success = plugin.getTeamWarManager().denyWar(team.getName(), player);
        
        if (!success) {
            // Error messages are handled in TeamWarManager
            return;
        }
        
        player.sendMessage("§c[Wars] §7War declaration denied!");
    }
    
    /**
     * Handle war info
     */
    private void handleInfo(Player player, TeamManager.Team team) {
        TeamWarManager.War war = plugin.getTeamWarManager().getActiveWar(team.getName());
        
        if (war == null) {
            player.sendMessage("§8[§cWars§8] §7Your team is not currently at war.");
            player.sendMessage("§8[§cWars§8] §7Use §e/teamwar declare <team>§7 to start a war!");
            return;
        }
        
        String enemy = war.getTeam1().equals(team.getName()) ? war.getTeam2() : war.getTeam1();
        long duration = (System.currentTimeMillis() - war.getStartTime()) / 1000 / 60; // minutes
        
        player.sendMessage("§c§l⚔ WAR INFO ⚔");
        player.sendMessage("§7━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§eEnemy Team: §c" + enemy);
        player.sendMessage("§eDuration: §f" + duration + " minutes");
        player.sendMessage("§7");
        player.sendMessage("§eYour Team Deaths:");
        
        TeamManager.Team enemyTeam = plugin.getTeamManager().getTeam(enemy);
        java.util.Map<java.util.UUID, Integer> deaths = war.getTeamDeaths(team.getName());
        
        for (java.util.UUID memberUUID : team.getMembers()) {
            org.bukkit.OfflinePlayer member = org.bukkit.Bukkit.getOfflinePlayer(memberUUID);
            String memberName = member.getName() != null ? member.getName() : "Unknown";
            int deathCount = deaths.getOrDefault(memberUUID, 0);
            
            String status = deathCount > 0 ? "§c✖ Eliminated" : "§a✓ Alive";
            player.sendMessage("  §f" + memberName + ": " + status + " §7(" + deathCount + " deaths)");
        }
        
        player.sendMessage("§7━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Handle war list
     */
    private void handleList(Player player) {
        java.util.Collection<TeamWarManager.War> wars = plugin.getTeamWarManager().getActiveWars();
        
        if (wars.isEmpty()) {
            player.sendMessage("§8[§cWars§8] §7There are no active wars.");
            return;
        }
        
        player.sendMessage("§c§l⚔ ACTIVE WARS ⚔");
        player.sendMessage("§7━━━━━━━━━━━━━━━━━━━━━━");
        
        for (TeamWarManager.War war : wars) {
            long duration = (System.currentTimeMillis() - war.getStartTime()) / 1000 / 60;
            player.sendMessage("§e" + war.getTeam1() + " §cvs §e" + war.getTeam2() + " §7(" + duration + "m)");
        }
        
        player.sendMessage("§7━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Send help message
     */
    private void sendHelp(Player player) {
        player.sendMessage("§c§l⚔ TEAM WARS ⚔");
        player.sendMessage("§7━━━━━━━━━━━━━━━━━━━━━━");
        player.sendMessage("§e/teamwar declare <team> §7- Declare war on a team");
        player.sendMessage("§e/teamwar accept §7- Accept war declaration");
        player.sendMessage("§e/teamwar deny §7- Deny war declaration");
        player.sendMessage("§e/teamwar info §7- View current war info");
        player.sendMessage("§e/teamwar list §7- List all active wars");
        player.sendMessage("§7━━━━━━━━━━━━━━━━━━━━━━");
    }
}
