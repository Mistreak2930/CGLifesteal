package com.example.lifesteal.system;

import com.example.lifesteal.LifestealPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manages team wars and war declarations
 * Team leaders can declare war on each other
 * If entire team dies, winning team gets money
 */
public class TeamWarManager {
    
    private LifestealPlugin plugin;
    private File warsFile;
    private FileConfiguration warsData;
    
    // Active wars: team1_vs_team2 -> War object
    private Map<String, War> activeWars;
    
    // Pending war declarations: targetTeam -> War object
    private Map<String, War> pendingWars;
    
    // Configuration
    private double warRewardPerMember = 1000.0;
    private boolean requireBothLeaders = true;
    private int warCooldownMinutes = 60;
    
    public TeamWarManager(LifestealPlugin plugin) {
        this.plugin = plugin;
        this.activeWars = new HashMap<>();
        this.pendingWars = new HashMap<>();
        this.warsFile = new File(plugin.getDataFolder(), "wars.yml");
        loadWarData();
        
        // Load configuration
        this.warRewardPerMember = plugin.getConfig().getDouble("team-wars.reward-per-member", 1000.0);
        this.requireBothLeaders = plugin.getConfig().getBoolean("team-wars.require-both-leaders", true);
        this.warCooldownMinutes = plugin.getConfig().getInt("team-wars.cooldown-minutes", 60);
    }
    
    /**
     * Load war data from file
     */
    private void loadWarData() {
        if (!warsFile.exists()) {
            try {
                warsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to create wars.yml: " + e.getMessage());
            }
        }
        
        warsData = YamlConfiguration.loadConfiguration(warsFile);
        
        // Load active wars
        if (warsData.contains("active-wars")) {
            for (String key : warsData.getConfigurationSection("active-wars").getKeys(false)) {
                String path = "active-wars." + key;
                String team1 = warsData.getString(path + ".team1");
                String team2 = warsData.getString(path + ".team2");
                long startTime = warsData.getLong(path + ".start-time");
                Map<UUID, Integer> team1Deaths = loadDeathMap(path + ".team1-deaths");
                Map<UUID, Integer> team2Deaths = loadDeathMap(path + ".team2-deaths");
                
                War war = new War(team1, team2, startTime);
                war.setTeam1Deaths(team1Deaths);
                war.setTeam2Deaths(team2Deaths);
                activeWars.put(key, war);
            }
        }
    }
    
    /**
     * Load death map from config
     */
    private Map<UUID, Integer> loadDeathMap(String path) {
        Map<UUID, Integer> deaths = new HashMap<>();
        if (warsData.contains(path)) {
            for (String uuidStr : warsData.getConfigurationSection(path).getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    int deathCount = warsData.getInt(path + "." + uuidStr);
                    deaths.put(uuid, deathCount);
                } catch (IllegalArgumentException e) {
                    // Invalid UUID, skip
                }
            }
        }
        return deaths;
    }
    
    /**
     * Save war data to file
     */
    public void saveWarData() {
        warsData = new YamlConfiguration();
        
        // Save active wars
        int index = 0;
        for (Map.Entry<String, War> entry : activeWars.entrySet()) {
            War war = entry.getValue();
            String path = "active-wars." + entry.getKey();
            
            warsData.set(path + ".team1", war.getTeam1());
            warsData.set(path + ".team2", war.getTeam2());
            warsData.set(path + ".start-time", war.getStartTime());
            
            // Save death counts
            for (Map.Entry<UUID, Integer> death : war.getTeam1Deaths().entrySet()) {
                warsData.set(path + ".team1-deaths." + death.getKey().toString(), death.getValue());
            }
            for (Map.Entry<UUID, Integer> death : war.getTeam2Deaths().entrySet()) {
                warsData.set(path + ".team2-deaths." + death.getKey().toString(), death.getValue());
            }
        }
        
        try {
            warsData.save(warsFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save wars data: " + e.getMessage());
        }
    }
    
    /**
     * Declare war on another team
     */
    public boolean declareWar(String declaringTeam, String targetTeam, Player declarer) {
        TeamManager.Team team1 = plugin.getTeamManager().getTeam(declaringTeam);
        TeamManager.Team team2 = plugin.getTeamManager().getTeam(targetTeam);
        
        if (team1 == null || team2 == null) {
            return false;
        }
        
        // Check if declarer is the leader
        if (!team1.getLeader().equals(declarer.getUniqueId())) {
            declarer.sendMessage("§c[Wars] §7Only the team leader can declare war!");
            return false;
        }
        
        // Check if already at war
        if (getActiveWar(declaringTeam) != null) {
            declarer.sendMessage("§c[Wars] §7Your team is already at war!");
            return false;
        }
        
        if (getActiveWar(targetTeam) != null) {
            declarer.sendMessage("§c[Wars] §7That team is already at war!");
            return false;
        }
        
        // Check if same team
        if (declaringTeam.equals(targetTeam)) {
            declarer.sendMessage("§c[Wars] §7You cannot declare war on your own team!");
            return false;
        }
        
        // Check cooldown
        if (isOnCooldown(declaringTeam)) {
            long remaining = getCooldownRemaining(declaringTeam);
            declarer.sendMessage("§c[Wars] §7War cooldown: §e" + formatTime(remaining) + " §7remaining!");
            return false;
        }
        
        // Create war declaration
        War war = new War(declaringTeam, targetTeam, System.currentTimeMillis());
        
        if (requireBothLeaders) {
            // Add to pending wars
            pendingWars.put(targetTeam, war);
            
            // Notify both teams
            broadcastToTeam(team1, "§c§l[WAR] §eYour team has declared war on §c" + targetTeam + "§e!");
            broadcastToTeam(team2, "§c§l[WAR] §eTeam §c" + declaringTeam + " §ehas declared war on you!");
            broadcastToTeam(team2, "§c§l[WAR] §7Your leader must §a/teamwar accept §7to begin!");
            
            return true;
        } else {
            // Start war immediately
            return startWar(war);
        }
    }
    
    /**
     * Accept a war declaration
     */
    public boolean acceptWar(String teamName, Player accepter) {
        War war = pendingWars.get(teamName);
        
        if (war == null) {
            accepter.sendMessage("§c[Wars] §7No pending war declaration!");
            return false;
        }
        
        TeamManager.Team team = plugin.getTeamManager().getTeam(teamName);
        
        if (!team.getLeader().equals(accepter.getUniqueId())) {
            accepter.sendMessage("§c[Wars] §7Only the team leader can accept wars!");
            return false;
        }
        
        // Remove from pending and start war
        pendingWars.remove(teamName);
        return startWar(war);
    }
    
    /**
     * Deny a war declaration
     */
    public boolean denyWar(String teamName, Player denier) {
        War war = pendingWars.get(teamName);
        
        if (war == null) {
            denier.sendMessage("§c[Wars] §7No pending war declaration!");
            return false;
        }
        
        TeamManager.Team team = plugin.getTeamManager().getTeam(teamName);
        
        if (!team.getLeader().equals(denier.getUniqueId())) {
            denier.sendMessage("§c[Wars] §7Only the team leader can deny wars!");
            return false;
        }
        
        // Remove from pending
        pendingWars.remove(teamName);
        
        // Notify both teams
        String declaringTeam = war.getTeam1();
        TeamManager.Team team1 = plugin.getTeamManager().getTeam(declaringTeam);
        
        broadcastToTeam(team, "§c[Wars] §7Your team has §cdenied §7the war declaration!");
        broadcastToTeam(team1, "§c[Wars] §eTeam §c" + teamName + " §ehas declined your war!");
        
        return true;
    }
    
    /**
     * Start a war
     */
    private boolean startWar(War war) {
        String key = war.getTeam1() + "_vs_" + war.getTeam2();
        activeWars.put(key, war);
        
        TeamManager.Team team1 = plugin.getTeamManager().getTeam(war.getTeam1());
        TeamManager.Team team2 = plugin.getTeamManager().getTeam(war.getTeam2());
        
        broadcastToTeam(team1, "§c§l⚔ WAR HAS BEGUN! ⚔");
        broadcastToTeam(team1, "§eYou are now at war with §c" + war.getTeam2() + "§e!");
        broadcastToTeam(team1, "§7Eliminate all enemy team members to win!");
        
        broadcastToTeam(team2, "§c§l⚔ WAR HAS BEGUN! ⚔");
        broadcastToTeam(team2, "§eYou are now at war with §c" + war.getTeam1() + "§e!");
        broadcastToTeam(team2, "§7Eliminate all enemy team members to win!");
        
        saveWarData();
        return true;
    }
    
    /**
     * Record a player death in war
     */
    public void recordDeath(Player victim) {
        TeamManager.Team victimTeam = plugin.getTeamManager().getPlayerTeam(victim);
        if (victimTeam == null) return;
        
        War war = getActiveWar(victimTeam.getName());
        if (war == null) return;
        
        // Record death
        war.recordDeath(victim.getUniqueId(), victimTeam.getName());
        
        // Check if entire team is dead
        if (isTeamEliminated(victimTeam, war)) {
            String winningTeamName = war.getTeam1().equals(victimTeam.getName()) ? war.getTeam2() : war.getTeam1();
            endWar(war, winningTeamName);
        } else {
            saveWarData();
        }
    }
    
    /**
     * Check if entire team is eliminated
     */
    private boolean isTeamEliminated(TeamManager.Team team, War war) {
        Map<UUID, Integer> deaths = war.getTeamDeaths(team.getName());
        
        // Check if all team members have died at least once
        for (UUID member : team.getMembers()) {
            if (!deaths.containsKey(member) || deaths.get(member) == 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * End a war
     */
    private void endWar(War war, String winningTeamName) {
        TeamManager.Team winningTeam = plugin.getTeamManager().getTeam(winningTeamName);
        String losingTeamName = war.getTeam1().equals(winningTeamName) ? war.getTeam2() : war.getTeam1();
        TeamManager.Team losingTeam = plugin.getTeamManager().getTeam(losingTeamName);
        
        // Calculate reward
        double reward = warRewardPerMember * losingTeam.getMembers().size();
        
        // Give rewards to winning team
        for (UUID memberUUID : winningTeam.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && plugin.hasEconomy()) {
                plugin.getEconomy().depositPlayer(member, reward);
            }
        }
        
        // Broadcast results
        broadcastToTeam(winningTeam, "§a§l⚔ VICTORY! ⚔");
        broadcastToTeam(winningTeam, "§eYour team has won the war against §c" + losingTeamName + "§e!");
        broadcastToTeam(winningTeam, "§aEach member received: §e$" + String.format("%.2f", reward));
        
        broadcastToTeam(losingTeam, "§c§l⚔ DEFEAT ⚔");
        broadcastToTeam(losingTeam, "§eYour team has lost the war against §a" + winningTeamName + "§e!");
        broadcastToTeam(losingTeam, "§7Your team was eliminated!");
        
        // Update team stats
        winningTeam.addWins(1);
        losingTeam.addLosses(1);
        
        // Set cooldown
        setWarCooldown(winningTeamName);
        setWarCooldown(losingTeamName);
        
        // Remove war
        String key = war.getTeam1() + "_vs_" + war.getTeam2();
        activeWars.remove(key);
        
        saveWarData();
        plugin.getTeamManager().saveTeams();
    }
    
    /**
     * Get active war for a team
     */
    public War getActiveWar(String teamName) {
        for (War war : activeWars.values()) {
            if (war.getTeam1().equals(teamName) || war.getTeam2().equals(teamName)) {
                return war;
            }
        }
        return null;
    }
    
    /**
     * Check if team is on war cooldown
     */
    private boolean isOnCooldown(String teamName) {
        if (!warsData.contains("cooldowns." + teamName)) {
            return false;
        }
        
        long cooldownEnd = warsData.getLong("cooldowns." + teamName);
        return System.currentTimeMillis() < cooldownEnd;
    }
    
    /**
     * Get remaining cooldown time
     */
    private long getCooldownRemaining(String teamName) {
        if (!warsData.contains("cooldowns." + teamName)) {
            return 0;
        }
        
        long cooldownEnd = warsData.getLong("cooldowns." + teamName);
        long remaining = cooldownEnd - System.currentTimeMillis();
        return Math.max(0, remaining / 1000); // Return seconds
    }
    
    /**
     * Set war cooldown for a team
     */
    private void setWarCooldown(String teamName) {
        long cooldownEnd = System.currentTimeMillis() + (warCooldownMinutes * 60 * 1000L);
        warsData.set("cooldowns." + teamName, cooldownEnd);
        saveWarData();
    }
    
    /**
     * Broadcast message to all online team members
     */
    private void broadcastToTeam(TeamManager.Team team, String message) {
        for (UUID memberUUID : team.getMembers()) {
            Player member = Bukkit.getPlayer(memberUUID);
            if (member != null && member.isOnline()) {
                member.sendMessage(message);
            }
        }
    }
    
    /**
     * Format time in seconds to readable format
     */
    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%dh %dm", hours, minutes);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }
    
    /**
     * Get all active wars
     */
    public Collection<War> getActiveWars() {
        return activeWars.values();
    }
    
    /**
     * Inner class representing a war between two teams
     */
    public static class War {
        private String team1;
        private String team2;
        private long startTime;
        private Map<UUID, Integer> team1Deaths;
        private Map<UUID, Integer> team2Deaths;
        
        public War(String team1, String team2, long startTime) {
            this.team1 = team1;
            this.team2 = team2;
            this.startTime = startTime;
            this.team1Deaths = new HashMap<>();
            this.team2Deaths = new HashMap<>();
        }
        
        public String getTeam1() {
            return team1;
        }
        
        public String getTeam2() {
            return team2;
        }
        
        public long getStartTime() {
            return startTime;
        }
        
        public Map<UUID, Integer> getTeam1Deaths() {
            return team1Deaths;
        }
        
        public Map<UUID, Integer> getTeam2Deaths() {
            return team2Deaths;
        }
        
        public void setTeam1Deaths(Map<UUID, Integer> deaths) {
            this.team1Deaths = deaths;
        }
        
        public void setTeam2Deaths(Map<UUID, Integer> deaths) {
            this.team2Deaths = deaths;
        }
        
        public void recordDeath(UUID player, String teamName) {
            Map<UUID, Integer> deaths = teamName.equals(team1) ? team1Deaths : team2Deaths;
            deaths.put(player, deaths.getOrDefault(player, 0) + 1);
        }
        
        public Map<UUID, Integer> getTeamDeaths(String teamName) {
            return teamName.equals(team1) ? team1Deaths : team2Deaths;
        }
    }
}
