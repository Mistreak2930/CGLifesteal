package com.example.lifesteal.placeholders;

import com.example.lifesteal.LifestealPlugin;
import com.example.lifesteal.system.TeamManager;
import com.example.lifesteal.system.TeamWarManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class LifestealPlaceholderExpansion extends PlaceholderExpansion {

    private LifestealPlugin plugin;

    public LifestealPlaceholderExpansion(LifestealPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "lifesteal";
    }

    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null || params == null) {
            return "";
        }

        String key = params.toLowerCase();

        if (key.equals("orbits")) {
            long orbits = plugin.getOrbitsManager().getOrbits(player.getUniqueId());
            return String.valueOf(orbits);
        }

        if (key.equals("orbits_formatted")) {
            long orbits = plugin.getOrbitsManager().getOrbits(player.getUniqueId());
            return plugin.getOrbitsManager().formatOrbits(orbits);
        }

        if (key.startsWith("team_")) {
            TeamManager.Team team = plugin.getTeamManager().getPlayerTeam(player.getUniqueId());
            if (team == null) {
                return "None";
            }

            switch (key) {
                case "team_name":
                    return team.getName();
                    
                case "team_name_hover":
                    // Returns JSON format with hover text for EssentialsXChat
                    return getTeamNameWithHover(player, team);
                    
                case "team_leader":
                    OfflinePlayer leader = Bukkit.getOfflinePlayer(team.getLeader());
                    return leader.getName() != null ? leader.getName() : "Unknown";
                    
                case "team_size":
                    return String.valueOf(team.getMembers().size());
                    
                case "team_max":
                    return String.valueOf(plugin.getTeamManager().getMaxTeamSize());
                    
                case "team_kills":
                    return String.valueOf(team.getKills());
                    
                case "team_deaths":
                    return String.valueOf(team.getDeaths());
                    
                case "team_war_status":
                    if (plugin.hasTeamWarManager()) {
                        TeamWarManager.War war = plugin.getTeamWarManager().getActiveWar(team.getName());
                        if (war != null) {
                            return "§c⚔ AT WAR";
                        }
                    }
                    return "§aPeaceful";
                    
                default:
                    return "";
            }
        }

        return "";
    }
    
    /**
     * Get team name with hover text showing team stats
     * This is for EssentialsXChat integration
     */
    private String getTeamNameWithHover(Player player, TeamManager.Team team) {
        // Get team stats
        int kills = team.getKills();
        int deaths = team.getDeaths();
        double kdr = deaths > 0 ? (double) kills / deaths : kills;
        
        OfflinePlayer leader = Bukkit.getOfflinePlayer(team.getLeader());
        String leaderName = leader.getName() != null ? leader.getName() : "Unknown";
        
        // Check if in war
        String warStatus = "§aPeaceful";
        if (plugin.hasTeamWarManager()) {
            TeamWarManager.War war = plugin.getTeamWarManager().getActiveWar(team.getName());
            if (war != null) {
                String enemyTeam = war.getTeam1().equals(team.getName()) ? war.getTeam2() : war.getTeam1();
                warStatus = "§c⚔ War vs " + enemyTeam;
            }
        }
        
        // Create JSON hover text (for 1.12.2)
        // Format: {"text":"[TeamName]","color":"gold","hoverEvent":{"action":"show_text","value":"Stats here"}}
        String hoverText = String.format(
            "{\"text\":\"[%s]\",\"color\":\"gold\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" +
            "§6§l%s\\n" +
            "§7━━━━━━━━━━━━━━━\\n" +
            "§eLeader: §f%s\\n" +
            "§eMembers: §f%d/%d\\n" +
            "§eKills: §a%d\\n" +
            "§eDeaths: §c%d\\n" +
            "§eK/D: §f%.2f\\n" +
            "§eStatus: %s\"}}",
            team.getName(),
            team.getName(),
            leaderName,
            team.getMembers().size(),
            plugin.getTeamManager().getMaxTeamSize(),
            kills,
            deaths,
            kdr,
            warStatus
        );
        
        return hoverText;
    }
}

