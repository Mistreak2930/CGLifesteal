package com.example.lifesteal.system;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeamManager {

    private JavaPlugin plugin;
    private File teamsFile;
    private FileConfiguration teamsConfig;
    private Map<String, Team> teamsCache;
    private Map<UUID, String> playerTeamMap; // UUID -> Team Name
    private final int maxTeamSize;
    private final int minTeamNameLength;
    private final int maxTeamNameLength;

    public TeamManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.minTeamNameLength = Math.max(1, plugin.getConfig().getInt("teams.min-team-name-length", 3));
        this.maxTeamNameLength = Math.max(minTeamNameLength,
            plugin.getConfig().getInt("teams.max-team-name-length", 16));
        this.maxTeamSize = Math.max(1, plugin.getConfig().getInt("teams.max-team-size",
            plugin.getConfig().getInt("settings.max-team-size", 10)));
        this.teamsCache = new HashMap<>();
        this.playerTeamMap = new HashMap<>();
        initializeTeamsFile();
        loadTeams();
    }

    private void initializeTeamsFile() {
        teamsFile = new File(plugin.getDataFolder(), "teams.yml");
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        if (!teamsFile.exists()) {
            try {
                teamsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create teams.yml: " + e.getMessage());
            }
        }
        teamsConfig = YamlConfiguration.loadConfiguration(teamsFile);
    }

    public void loadTeams() {
        teamsCache.clear();
        playerTeamMap.clear();
        
        for (String teamName : teamsConfig.getKeys(false)) {
            String leaderUUID = teamsConfig.getString(teamName + ".leader");
            List<String> members = teamsConfig.getStringList(teamName + ".members");
            List<String> invites = teamsConfig.getStringList(teamName + ".invites");
            
            // Load team stats
            int kills = teamsConfig.getInt(teamName + ".stats.kills", 0);
            int deaths = teamsConfig.getInt(teamName + ".stats.deaths", 0);
            int wins = teamsConfig.getInt(teamName + ".stats.wins", 0);
            int losses = teamsConfig.getInt(teamName + ".stats.losses", 0);
            
            List<UUID> memberUUIDs = new ArrayList<>();
            List<UUID> inviteUUIDs = new ArrayList<>();
            
            try {
                for (String member : members) {
                    UUID uuid = UUID.fromString(member);
                    memberUUIDs.add(uuid);
                    playerTeamMap.put(uuid, teamName);
                }
                for (String invite : invites) {
                    inviteUUIDs.add(UUID.fromString(invite));
                }
                
                Team team = new Team(teamName, UUID.fromString(leaderUUID), memberUUIDs, inviteUUIDs);
                team.setKills(kills);
                team.setDeaths(deaths);
                team.setWins(wins);
                team.setLosses(losses);
                teamsCache.put(teamName, team);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in teams.yml for team: " + teamName);
            }
        }
    }

    public void saveTeams() {
        teamsConfig = new YamlConfiguration();
        for (Map.Entry<String, Team> entry : teamsCache.entrySet()) {
            Team team = entry.getValue();
            String teamName = entry.getKey();
            
            teamsConfig.set(teamName + ".leader", team.getLeader().toString());
            
            List<String> memberStrings = new ArrayList<>();
            for (UUID uuid : team.getMembers()) {
                memberStrings.add(uuid.toString());
            }
            teamsConfig.set(teamName + ".members", memberStrings);
            
            List<String> inviteStrings = new ArrayList<>();
            for (UUID uuid : team.getPendingInvites()) {
                inviteStrings.add(uuid.toString());
            }
            teamsConfig.set(teamName + ".invites", inviteStrings);
            
            // Save team stats
            teamsConfig.set(teamName + ".stats.kills", team.getKills());
            teamsConfig.set(teamName + ".stats.deaths", team.getDeaths());
            teamsConfig.set(teamName + ".stats.wins", team.getWins());
            teamsConfig.set(teamName + ".stats.losses", team.getLosses());
        }
        
        try {
            teamsConfig.save(teamsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save teams.yml: " + e.getMessage());
        }
    }

    public boolean createTeam(String teamName, Player leader) {
        if (getTeam(teamName) != null) {
            return false; // Team already exists
        }
        if (teamName.length() > maxTeamNameLength || teamName.length() < minTeamNameLength) {
            return false; // Invalid team name length
        }
        if (playerTeamMap.containsKey(leader.getUniqueId())) {
            return false; // Player already in a team
        }

        List<UUID> members = new ArrayList<>();
        members.add(leader.getUniqueId());
        
        Team team = new Team(teamName, leader.getUniqueId(), members, new ArrayList<>());
        teamsCache.put(teamName, team);
        playerTeamMap.put(leader.getUniqueId(), teamName);
        saveTeams();
        return true;
    }

    public Team getTeam(String teamName) {
        return teamsCache.get(teamName);
    }

    public Team getPlayerTeam(UUID uuid) {
        String teamName = playerTeamMap.get(uuid);
        if (teamName == null) return null;
        return teamsCache.get(teamName);
    }

    public Team getPlayerTeam(Player player) {
        return getPlayerTeam(player.getUniqueId());
    }

    public boolean invitePlayer(String teamName, Player inviter, Player target) {
        Team team = getTeam(teamName);
        if (team == null) return false;
        if (!team.getLeader().equals(inviter.getUniqueId()) && !team.isMember(inviter.getUniqueId())) {
            return false; // Only team members can invite
        }
        if (team.getMembers().size() >= maxTeamSize) {
            return false; // Team is full
        }
        if (playerTeamMap.containsKey(target.getUniqueId())) {
            return false; // Player already in a team
        }

        team.addInvite(target.getUniqueId());
        saveTeams();
        return true;
    }

    public boolean acceptInvite(Player player, String teamName) {
        Team team = getTeam(teamName);
        if (team == null) return false;
        if (!team.hasInvite(player.getUniqueId())) return false;

        team.removeInvite(player.getUniqueId());
        team.addMember(player.getUniqueId());
        playerTeamMap.put(player.getUniqueId(), teamName);
        saveTeams();
        return true;
    }

    public boolean denyInvite(Player player, String teamName) {
        Team team = getTeam(teamName);
        if (team == null) return false;
        if (!team.hasInvite(player.getUniqueId())) return false;

        team.removeInvite(player.getUniqueId());
        saveTeams();
        return true;
    }

    public boolean leaveTeam(Player player) {
        Team team = getPlayerTeam(player);
        if (team == null) return false;

        if (team.getLeader().equals(player.getUniqueId())) {
            // Leader leaving - check if there are other members
            if (team.getMembers().size() > 1) {
                // Transfer leadership to next member
                UUID newLeader = null;
                for (UUID mem : team.getMembers()) {
                    if (!mem.equals(player.getUniqueId())) {
                        newLeader = mem;
                        break;
                    }
                }
                team.setLeader(newLeader);
            } else {
                // Disband team
                teamsCache.remove(team.getName());
            }
        }

        team.removeMember(player.getUniqueId());
        playerTeamMap.remove(player.getUniqueId());
        saveTeams();
        return true;
    }

    public List<Team> getAllTeams() {
        return new ArrayList<>(teamsCache.values());
    }

    public int getMaxTeamSize() {
        return maxTeamSize;
    }

    // Inner Team class
    public static class Team {
        private String name;
        private UUID leader;
        private List<UUID> members;
        private List<UUID> pendingInvites;
        private int kills;
        private int deaths;
        private int wins;
        private int losses;

        public Team(String name, UUID leader, List<UUID> members, List<UUID> pendingInvites) {
            this.name = name;
            this.leader = leader;
            this.members = members;
            this.pendingInvites = pendingInvites;
            this.kills = 0;
            this.deaths = 0;
            this.wins = 0;
            this.losses = 0;
        }

        public String getName() {
            return name;
        }

        public UUID getLeader() {
            return leader;
        }

        public void setLeader(UUID newLeader) {
            this.leader = newLeader;
        }

        public List<UUID> getMembers() {
            return members;
        }

        public boolean isMember(UUID uuid) {
            return members.contains(uuid);
        }

        public void addMember(UUID uuid) {
            if (!members.contains(uuid)) {
                members.add(uuid);
            }
        }

        public void removeMember(UUID uuid) {
            members.remove(uuid);
        }

        public List<UUID> getPendingInvites() {
            return pendingInvites;
        }

        public boolean hasInvite(UUID uuid) {
            return pendingInvites.contains(uuid);
        }

        public void addInvite(UUID uuid) {
            if (!pendingInvites.contains(uuid)) {
                pendingInvites.add(uuid);
            }
        }

        public void removeInvite(UUID uuid) {
            pendingInvites.remove(uuid);
        }
        
        // Stats methods
        public int getKills() {
            return kills;
        }
        
        public void setKills(int kills) {
            this.kills = kills;
        }
        
        public void addKills(int amount) {
            this.kills += amount;
        }
        
        public int getDeaths() {
            return deaths;
        }
        
        public void setDeaths(int deaths) {
            this.deaths = deaths;
        }
        
        public void addDeaths(int amount) {
            this.deaths += amount;
        }
        
        public int getWins() {
            return wins;
        }
        
        public void setWins(int wins) {
            this.wins = wins;
        }
        
        public void addWins(int amount) {
            this.wins += amount;
        }
        
        public int getLosses() {
            return losses;
        }
        
        public void setLosses(int losses) {
            this.losses = losses;
        }
        
        public void addLosses(int amount) {
            this.losses += amount;
        }
    }
}
