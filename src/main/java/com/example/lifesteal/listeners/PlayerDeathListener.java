package com.example.lifesteal.listeners;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.TeamManager;
import com.example.lifesteal.system.TeamWarManager;
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

        // Get teams
        TeamManager.Team victimTeam = plugin.getTeamManager().getPlayerTeam(victim);
        TeamManager.Team killerTeam = killer != null ? plugin.getTeamManager().getPlayerTeam(killer) : null;

        // Update team death stats
        if (victimTeam != null) {
            victimTeam.addDeaths(1);
        }

        // Check if this is a war death
        boolean isWarDeath = false;
        if (plugin.hasTeamWarManager() && victimTeam != null) {
            TeamWarManager.War war = plugin.getTeamWarManager().getActiveWar(victimTeam.getName());
            if (war != null) {
                // This is a war death
                plugin.getTeamWarManager().recordDeath(victim);
                isWarDeath = true;
            }
        }

        // Remove heart from victim
        victimHearts = plugin.getHeartManager().getHearts(victim);
        plugin.getHeartManager().removeHearts(victim, 1);
        victimHearts = plugin.getHeartManager().getHearts(victim);

        if (killer == null) {
            // No killer
            java.util.Map<String, String> replacements = new java.util.HashMap<>();
            replacements.put("player", victim.getName());
            event.setDeathMessage(plugin.getMessageManager().getRandomMessage("death.no-killer", replacements));
            
            // Notify team
            if (victimTeam != null) {
                notifyTeamDeath(victimTeam, victim, null, isWarDeath);
            }
            
            return;
        }

        // Give heart to killer
        victorHearts = plugin.getHeartManager().getHearts(killer);
        plugin.getHeartManager().addHearts(killer, 1);
        victorHearts = plugin.getHeartManager().getHearts(killer);

        // Update team kill stats
        if (killerTeam != null) {
            killerTeam.addKills(1);
        }

        // Custom death message
        java.util.Map<String, String> replacements = new java.util.HashMap<>();
        replacements.put("victim", victim.getName());
        replacements.put("killer", killer.getName());
        
        String deathMessage;
        if (isWarDeath) {
            deathMessage = "§c⚔ [WAR] §e" + killer.getName() + " §cslayed §e" + victim.getName() + 
                          " §7(§e" + victimHearts + "§7 -> §e" + victorHearts + "§7)";
        } else {
            deathMessage = plugin.getMessageManager().getRandomMessage("death.with-killer", replacements) + 
                          " §7(§e" + victimHearts + "§7 -> §e" + victorHearts + "§7)";
        }
        event.setDeathMessage(deathMessage);

        // Team notifications
        if (victimTeam != null) {
            notifyTeamDeath(victimTeam, victim, killer, isWarDeath);
        }

        if (killerTeam != null) {
            notifyTeamKill(killerTeam, killer, victim, isWarDeath);
        }
        
        // Save team stats
        plugin.getTeamManager().saveTeams();
    }
    
    /**
     * Notify team about member death
     */
    private void notifyTeamDeath(TeamManager.Team team, Player victim, Player killer, boolean isWar) {
        for (java.util.UUID uuid : team.getMembers()) {
            Player member = org.bukkit.Bukkit.getPlayer(uuid);
            if (member != null && !member.equals(victim)) {
                if (isWar) {
                    if (killer != null) {
                        member.sendMessage("§7[§6" + team.getName() + "§7] §c⚔ " + victim.getName() + 
                                         " §cwas eliminated by §e" + killer.getName() + " §cin the war!");
                    } else {
                        member.sendMessage("§7[§6" + team.getName() + "§7] §c⚔ " + victim.getName() + 
                                         " §cwas eliminated in the war!");
                    }
                } else {
                    java.util.Map<String, String> notif = new java.util.HashMap<>();
                    notif.put("player", victim.getName());
                    if (killer != null) {
                        notif.put("killer", killer.getName());
                        member.sendMessage("§7[§6" + team.getName() + "§7] " + 
                                         plugin.getMessageManager().getMessage("team.death-notify", notif));
                    } else {
                        member.sendMessage("§7[§6" + team.getName() + "§7] §e" + victim.getName() + " §7died!");
                    }
                }
            }
        }
    }
    
    /**
     * Notify team about member kill
     */
    private void notifyTeamKill(TeamManager.Team team, Player killer, Player victim, boolean isWar) {
        for (java.util.UUID uuid : team.getMembers()) {
            Player member = org.bukkit.Bukkit.getPlayer(uuid);
            if (member != null && !member.equals(killer)) {
                if (isWar) {
                    member.sendMessage("§7[§6" + team.getName() + "§7] §a⚔ " + killer.getName() + 
                                     " §aeliminated §e" + victim.getName() + " §ain the war!");
                } else {
                    java.util.Map<String, String> notif = new java.util.HashMap<>();
                    notif.put("player", killer.getName());
                    notif.put("victim", victim.getName());
                    member.sendMessage("§7[§6" + team.getName() + "§7] " + 
                                     plugin.getMessageManager().getMessage("team.kill-notify", notif));
                }
            }
        }
    }
}


