package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.TeamManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    private LifestealPlugin plugin;

    public PlayerDeathListener(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        int victorHearts = 0;
        int victimHearts = 0;

        // Remove heart from victim
        victimHearts = plugin.getHeartManager().getHearts(victim);
        plugin.getHeartManager().removeHearts(victim, 1);
        victimHearts = plugin.getHeartManager().getHearts(victim);

        if (killer == null) {
            // No killer
            java.util.Map<String, String> replacements = new java.util.HashMap<>();
            replacements.put("player", victim.getName());
            event.setDeathMessage(plugin.getMessageManager().getMessage("death.no-killer", replacements));
            return;
        }

        // Give heart to killer
        victorHearts = plugin.getHeartManager().getHearts(killer);
        plugin.getHeartManager().addHearts(killer, 1);
        victorHearts = plugin.getHeartManager().getHearts(killer);

        // Custom death message
        java.util.Map<String, String> replacements = new java.util.HashMap<>();
        replacements.put("victim", victim.getName());
        replacements.put("killer", killer.getName());
        event.setDeathMessage(plugin.getMessageManager().getMessage("death.with-killer", replacements) + 
                             " §7(§e" + victimHearts + "§7 -> §e" + victorHearts + "§7)");

        // Team notifications
        TeamManager.Team victimTeam = plugin.getTeamManager().getPlayerTeam(victim);
        TeamManager.Team killerTeam = plugin.getTeamManager().getPlayerTeam(killer);

        if (victimTeam != null) {
            for (java.util.UUID uuid : victimTeam.getMembers()) {
                Player member = org.bukkit.Bukkit.getPlayer(uuid);
                if (member != null && !member.equals(victim)) {
                    java.util.Map<String, String> notif = new java.util.HashMap<>();
                    notif.put("player", victim.getName());
                    notif.put("killer", killer.getName());
                    member.sendMessage("§7[§6" + victimTeam.getName() + "§7] " + 
                                      plugin.getMessageManager().getMessage("team.death-notify", notif));
                }
            }
        }

        if (killerTeam != null) {
            for (java.util.UUID uuid : killerTeam.getMembers()) {
                Player member = org.bukkit.Bukkit.getPlayer(uuid);
                if (member != null && !member.equals(killer)) {
                    java.util.Map<String, String> notif = new java.util.HashMap<>();
                    notif.put("player", killer.getName());
                    notif.put("victim", victim.getName());
                    member.sendMessage("§7[§6" + killerTeam.getName() + "§7] " + 
                                      plugin.getMessageManager().getMessage("team.kill-notify", notif));
                }
            }
        }
    }
}

