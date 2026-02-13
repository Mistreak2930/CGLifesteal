package com.example.lifesteal.commands;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamChatCommand implements CommandExecutor {

    private LifestealPlugin plugin;

    public TeamChatCommand(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        TeamManager.Team team = plugin.getTeamManager().getPlayerTeam(player);

        if (team == null) {
            player.sendMessage("§c[Team] You're not in a team!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§c[Team] Usage: /teamchat <message>");
            return true;
        }

        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        String finalMessage = "§7[§6" + team.getName() + "§7] §e" + player.getName() + "§a: " + message.toString();

        for (java.util.UUID uuid : team.getMembers()) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(finalMessage);
            }
        }

        return true;
    }
}
