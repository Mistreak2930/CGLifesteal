package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamCommand implements CommandExecutor {

    private LifestealPlugin plugin;

    public TeamCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        TeamManager teamManager = plugin.getTeamManager();

        if (args.length == 0) {
            showTeamHelp(player);
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "create":
                return handleCreate(player, args, teamManager);
            case "invite":
                return handleInvite(player, args, teamManager);
            case "accept":
                return handleAccept(player, args, teamManager);
            case "deny":
                return handleDeny(player, args, teamManager);
            case "leave":
                return handleLeave(player, teamManager);
            case "list":
                return handleList(player, teamManager);
            case "info":
                return handleInfo(player, args, teamManager);
            default:
                showTeamHelp(player);
                return true;
        }
    }

    private boolean handleCreate(Player player, String[] args, TeamManager teamManager) {
        if (args.length != 2) {
            player.sendMessage("§c[Team] Usage: /team create <name>");
            return true;
        }

        String teamName = args[1];

        if (teamManager.createTeam(teamName, player)) {
            player.sendMessage("§6[Team] §aTeam §e" + teamName + "§a created successfully!");
            Bukkit.broadcastMessage("§6[Team] §e" + player.getName() + "§a created a new team: §e" + teamName);
            return true;
        } else {
            player.sendMessage("§c[Team] Failed to create team! (Already in a team, invalid name, or team exists)");
            return true;
        }
    }

    private boolean handleInvite(Player player, String[] args, TeamManager teamManager) {
        if (args.length != 2) {
            player.sendMessage("§c[Team] Usage: /team invite <player>");
            return true;
        }

        TeamManager.Team team = teamManager.getPlayerTeam(player);
        if (team == null) {
            player.sendMessage("§c[Team] You're not in a team!");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage("§c[Team] Player not found!");
            return true;
        }

        if (teamManager.invitePlayer(team.getName(), player, target)) {
            player.sendMessage("§6[Team] §aInvited §e" + target.getName() + "§a to team §e" + team.getName());
            target.sendMessage("§6[Team] §e" + player.getName() + "§a invited you to join team §e" + team.getName() + 
                              "§a! Use §e/team accept " + team.getName() + "§a or §e/team deny " + team.getName());
            return true;
        } else {
            player.sendMessage("§c[Team] Failed to invite player!");
            return true;
        }
    }

    private boolean handleAccept(Player player, String[] args, TeamManager teamManager) {
        if (args.length != 2) {
            player.sendMessage("§c[Team] Usage: /team accept <team>");
            return true;
        }

        String teamName = args[1];

        if (teamManager.acceptInvite(player, teamName)) {
            player.sendMessage("§6[Team] §aYou joined team §e" + teamName + "§a!");
            TeamManager.Team team = teamManager.getTeam(teamName);
            if (team != null) {
                for (java.util.UUID uuid : team.getMembers()) {
                    Player member = Bukkit.getPlayer(uuid);
                    if (member != null && !member.equals(player)) {
                        member.sendMessage("§6[Team] §e" + player.getName() + "§a joined the team!");
                    }
                }
            }
            return true;
        } else {
            player.sendMessage("§c[Team] No invite to that team or team doesn't exist!");
            return true;
        }
    }

    private boolean handleDeny(Player player, String[] args, TeamManager teamManager) {
        if (args.length != 2) {
            player.sendMessage("§c[Team] Usage: /team deny <team>");
            return true;
        }

        String teamName = args[1];

        if (teamManager.denyInvite(player, teamName)) {
            player.sendMessage("§6[Team] §aInvite from team §e" + teamName + "§a denied!");
            return true;
        } else {
            player.sendMessage("§c[Team] No invite from that team!");
            return true;
        }
    }

    private boolean handleLeave(Player player, TeamManager teamManager) {
        TeamManager.Team team = teamManager.getPlayerTeam(player);
        if (team == null) {
            player.sendMessage("§c[Team] You're not in a team!");
            return true;
        }

        String teamName = team.getName();
        if (teamManager.leaveTeam(player)) {
            player.sendMessage("§6[Team] §aYou left team §e" + teamName + "§a!");
            for (java.util.UUID uuid : team.getMembers()) {
                Player member = Bukkit.getPlayer(uuid);
                if (member != null) {
                    member.sendMessage("§6[Team] §e" + player.getName() + "§a left the team!");
                }
            }
            return true;
        }
        return true;
    }

    private boolean handleList(Player player, TeamManager teamManager) {
        java.util.List<TeamManager.Team> teams = teamManager.getAllTeams();
        if (teams.isEmpty()) {
            player.sendMessage("§c[Team] No teams exist yet!");
            return true;
        }

        player.sendMessage("§6[Team] Available Teams:");
        for (TeamManager.Team team : teams) {
            Player leader = Bukkit.getPlayer(team.getLeader());
            String leaderName = leader == null ? "Unknown" : leader.getName();
            player.sendMessage("§a - §e" + team.getName() + "§a (Leader: §e" + leaderName + 
                              "§a, Members: §e" + team.getMembers().size() + "§a/" + 
                              teamManager.getMaxTeamSize() + "§a)");
        }
        return true;
    }

    private boolean handleInfo(Player player, String[] args, TeamManager teamManager) {
        TeamManager.Team team = teamManager.getPlayerTeam(player);
        if (team == null) {
            player.sendMessage("§c[Team] You're not in a team!");
            return true;
        }

        player.sendMessage("§6[Team] Team: §e" + team.getName());
        Player leader = Bukkit.getPlayer(team.getLeader());
        player.sendMessage("§a  Leader: §e" + (leader == null ? "Unknown" : leader.getName()));
        player.sendMessage("§a  Members: §e" + team.getMembers().size() + "§a/" + teamManager.getMaxTeamSize());
        player.sendMessage("§a  Team Members:");
        for (java.util.UUID uuid : team.getMembers()) {
            Player member = Bukkit.getPlayer(uuid);
            player.sendMessage("§a    - §e" + (member == null ? "Unknown" : member.getName()));
        }
        return true;
    }

    private void showTeamHelp(Player player) {
        player.sendMessage("§6[Team] Available commands:");
        player.sendMessage("§a  /team create <name> §e- Create a new team");
        player.sendMessage("§a  /team invite <player> §e- Invite a player");
        player.sendMessage("§a  /team accept <team> §e- Accept an invite");
        player.sendMessage("§a  /team deny <team> §e- Deny an invite");
        player.sendMessage("§a  /team list §e- List all teams");
        player.sendMessage("§a  /team info §e- View team info");
        player.sendMessage("§a  /team leave §e- Leave your team");
    }
}
