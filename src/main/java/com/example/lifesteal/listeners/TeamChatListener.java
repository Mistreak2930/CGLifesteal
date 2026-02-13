package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TeamChatListener implements Listener {

    private LifestealPlugin plugin;

    public TeamChatListener(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Check if message starts with !
        if (!message.startsWith("!")) {
            return;
        }

        TeamManager.Team team = plugin.getTeamManager().getPlayerTeam(player);
        if (team == null) {
            player.sendMessage("§c[Team] You're not in a team!");
            return;
        }

        // Cancel global chat and send to team only
        event.setCancelled(true);

        // Remove the ! prefix
        String actualMessage = message.substring(1);

        String finalMessage = "§7[§6" + team.getName() + "§7] §e" + player.getName() + "§a: " + actualMessage;

        // Send to all team members
        for (java.util.UUID uuid : team.getMembers()) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(finalMessage);
            }
        }
    }
}
