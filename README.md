# Lifesteal Plugin 1.3.0

A comprehensive Lifesteal plugin for Minecraft 1.12.2 servers featuring hearts management, team systems with wars, lottery, daily rewards, bundles system, and dynamic GUI interfaces.

## 🎮 Features Overview

### 1. **Hearts System**
- Players have dynamically managed hearts (0-20)
- Hearts are persistent across sessions
- Default hearts: 10 (set on first join)
- Hearts are tracked per player in `hearts.yml`
- Ban players automatically when they reach 0 hearts

### 2. **Player Hearts Commands**
- `/hearts [player]` - View your or another player's hearts
- `/giveheart <player> <amount>` - Give hearts to a player (Admin only)
- `/takeheart <player> <amount>` - Remove hearts from a player (Admin only)

### 3. **Withdraw/Deposit System**
- `/withdraw <amount>` - Convert hearts into physical heart items
- `/deposit` - Convert heart items back to hearts
- Right-click heart items to gain max hearts
- Maximum hearts limit enforced
- Heart items can be dropped and traded with other players

### 4. **Main Menu System** ✨ NEW
- `/menu` - Opens the main hub GUI
- Quick access to all plugin features:
  - **Daily Bundles** - Claim daily rewards
  - **Warps** - Quick teleportation (placeholder for future)
  - **Heart Shop** - Purchase hearts (placeholder for future)
  - **Lottery** - Try your luck
  - **Daily Rewards** - Claim money and points
  - **Teams** - Manage your team
- View your personal stats (hearts, orbits, bundles claimed)
- Beautiful, organized interface

### 5. **Daily Bundles System** ✨ NEW
- Get **3 bundles per day** (resets every 24 hours)
- Each bundle contains random rewards
- Rewards refresh every **12 hours** or when opened
- Preview your next reward before opening
- View all possible rewards in the bundle
- Track total bundles claimed

**Possible Rewards:**
- 💰 Money ($100-500)
- ⭐ PlayerPoints (50-250)
- ❤️ Hearts (1-3)
- ✨ Orbits (10-50)
- 💎 Diamonds (1-5)
- 🟢 Emeralds (1-10)
- ⚪ Iron Ingots (5-15)
- 🟡 Gold Ingots (3-12)

### 6. **Lottery System**
- `/lottery` - Open the lottery interface
- Buy lottery tickets with money
- Win the entire prize pool
- Lottery draws automatically every hour
- Current pool and ticket count display
- Multiple players can participate

### 7. **Daily Reward System**
- `/dailyreward` - Claim your daily rewards
- Earn money and PlayerPoints daily
- 24-hour cooldown between claims
- Track total rewards claimed
- Beautiful GUI with reward information
- Automatic cooldown timer display

### 8. **Orbits System** ✨
- `/orbits see` - View your orbits
- `/orbits give <player> <amount>` - Give orbits (Admin)
- `/orbits take <player> <amount>` - Take orbits (Admin)
- Secondary currency system
- Tracked in `orbits.yml`
- Integrated with PlaceholderAPI

### 9. **Team System**
- Create and manage teams with other players
- Maximum team size: 10 players
- Team GUI interface (`/team gui`)
- **Team Statistics:**
  - Total kills
  - Total deaths
  - Wars won
  - Wars lost
  - K/D ratio
- Team-specific features:
  - `/team create <name>` - Create a new team
  - `/team invite <player>` - Invite a player to your team
  - `/team accept <team>` - Accept a team invite
  - `/team deny <team>` - Deny a team invite
  - `/team list` - View all teams on the server
  - `/team info` - View your team information
  - `/team leave` - Leave your current team
  - `/team gui` - Open team management GUI
  - Leadership transfer when leader leaves (auto to next member)

### 10. **Team Wars System** ⚔️ NEW
- Team leaders can declare war on each other
- Elimination-based combat system
- **Win Condition:** Eliminate all enemy team members
- **Rewards:** Winning team receives money ($1000 per enemy member by default)
- 60-minute cooldown after wars
- Real-time war tracking and notifications

**War Commands:**
- `/teamwar declare <team>` - Declare war on another team (Leader only)
- `/teamwar accept` - Accept war declaration (Leader only)
- `/teamwar deny` - Deny war declaration (Leader only)
- `/teamwar info` - View current war statistics
- `/teamwar list` - List all active wars

**War Features:**
- Both leaders must accept (configurable)
- Track deaths for each team member
- Special war-themed death messages
- Team notifications during combat
- Automatic victory detection
- Configurable rewards and cooldowns

### 11. **Team Chat**
- `/teamchat <message>` or `/tc <message>` - Send message to team chat
- Use `!<message>` in global chat to send to team chat
- Team chat messages are color-coded
- Only team members can see team messages
- Team notifications for deaths and kills

### 12. **Enhanced Team Placeholders** 💬 NEW
**For EssentialsXChat Integration:**
- `%lifesteal_team_name_hover%` - Team name with hover stats
- When hovering over team name in chat, displays:
  - Team leader
  - Member count
  - Total kills and deaths
  - K/D ratio
  - Current war status

**Other Placeholders:**
- `%lifesteal_team_name%` - Team name
- `%lifesteal_team_leader%` - Team leader name
- `%lifesteal_team_size%` - Team member count
- `%lifesteal_team_kills%` - Total team kills
- `%lifesteal_team_deaths%` - Total team deaths
- `%lifesteal_team_war_status%` - War status
- `%lifesteal_orbits%` - Player's orbits
- `%lifesteal_orbits_formatted%` - Formatted orbits

**EssentialsXChat Example:**
```yaml
format: '{DISPLAYNAME} &7[%lifesteal_team_name_hover%&7] &f{MESSAGE}'
```

### 13. **Death Mechanics**
- Players lose 1 heart on death
- Killer gains 1 heart (only if there's a killer)
- Custom death messages with multiple variations
- Team notifications when members die or kill players
- **War deaths** tracked separately for war victories
- Ban players at 0 hearts

### 14. **Admin Features**
- `/lifestealreload` - Reload plugin configuration without restarting
- `/giveheart` - Give hearts to players
- `/takeheart` - Remove hearts from players
- `/orbits give/take` - Manage orbits
- Full permission system for all commands

## 📋 Permission Nodes

```
lifesteal.user - Basic user permissions (default: true)
  ├─ lifesteal.hearts - View hearts (default: true)
  ├─ lifesteal.withdraw - Withdraw hearts (default: true)
  ├─ lifesteal.deposit - Deposit heart items (default: true)
  ├─ lifesteal.lottery - Access lottery (default: true)
  ├─ lifesteal.daily-reward - Claim daily rewards (default: true)
  ├─ lifesteal.menu - Open main menu (default: true)
  ├─ lifesteal.bundles - Access bundle system (default: true)
  ├─ lifesteal.teamwar - Use team wars (default: true)
  ├─ lifesteal.orbits - View orbits (default: true)
  └─ lifesteal.team.* - All team permissions (default: true)

lifesteal.admin - Admin permissions (default: op)
  ├─ lifesteal.admin.reload - Reload configuration (default: op)
  ├─ lifesteal.orbits.admin - Manage orbits (default: op)
  └─ All lifesteal.* permissions
```

## ⚙️ Dependencies

- **Spigot/Bukkit 1.12.2** - Core server software
- **PlayerPoints** - For PlayerPoints currency system
- **Vault** - For economy system (supports multiple economy plugins)
- **PlaceholderAPI** (Optional) - For chat placeholder integration

## 📦 Installation

✅ **Main Menu System** - Centralized GUI hub
✅ **Daily Bundles** - 3 bundles per day with random rewards
✅ **Team Wars** - Elimination-based team combat
✅ **Team Statistics** - Track kills, deaths, wins, losses
✅ **Enhanced Placeholders** - Hover stats in chat
✅ **Lottery System** - Hourly draws with prize pools
✅ **Daily Rewards** - Money and points every 24 hours
✅ **Orbits System** - Secondary currency
✅ **Heart Trading** - Physical heart items
✅ **Complete Team Management** - Teams with full GUI
✅ **Team Chat** - Private team communication
✅ **Vault Economy Integration** - Works with any economy plugin
✅ **PlaceholderAPI Support** - Advanced chat integration
✅ **Custom Death Messages** - Multiple variations
✅ **Persistent Data Storage** - All progress saved
✅ **Admin Tools** - Full management system
✅ **Optimized Performance** - Lightweight and efficient

## 🎯 Team Wars Guide

### Starting a War
1. Both teams must have a leader
2. Leader of Team A: `/teamwar declare TeamB`
3. Leader of Team B: `/teamwar accept`
4. War begins immediately!

### During War
- Kill enemy team members
- Each death is tracked
- Special war death messages
- Team notifications for eliminations

### Winning the War
- Eliminate ALL enemy team members
- Each winning team member receives rewards
- Team gets +1 win, enemy team gets +1 loss
- 60-minute cooldown before next war

### War Rewards
- Default: $1000 × (enemy team size) per winner
- If enemy team has 5 members, each winner gets $5000
- Configurable in `config.yml`

## 🎁 Daily Bundles Guide

### How It Works
1. Open `/menu` and click "Daily Bundles"
2. You get 3 bundles every 24 hours
3. Click a bundle to view its contents
4. See your "Next Reward" (refreshes every 12 hours)
5. Click to open and receive the reward

### Bundle Tips
- Open bundles daily to maximize rewards
- Next reward is shown before opening
- All possible rewards are visible
- Track total bundles claimed in stats

## 🔧 Troubleshooting

**"PlayerPoints plugin is not installed"**
- Ensure PlayerPoints plugin is installed and enabled
- Check that the plugin is in the plugins folder and server is restarted

**Hearts not saving**
- Check folder permissions
- Ensure `plugins/Lifesteal/` folder exists
- Check console for errors

**Team wars not starting**
- Verify both leaders have accepted (if required)
- Check that teams aren't on cooldown
- Ensure no team is already in another war

**Placeholders not working**
- Install PlaceholderAPI
- Run `/papi reload`
- Check placeholder format: `%lifesteal_team_name_hover%`

**Bundles not resetting**
- Check system time is correct
- Verify bundle reset time in config
- Check `bundles.yml` for last reset timestamp

## 🔨 Building from Source

```bash
mvn clean package
```

The compiled JAR will be in the `target/` directory.

## 💡 Tips & Tricks

1. **Configure War Rewards** - Adjust `team-wars.reward-per-member` for balanced economy
2. **Customize Bundles** - Edit possible rewards in `config.yml`
3. **Team Chat Integration** - Use EssentialsXChat with hover placeholders
4. **Balance Hearts** - Adjust `hearts-lost-on-death` and `hearts-gained-on-kill`
5. **Economy Balance** - Set appropriate lottery, daily reward, and bundle values

## 📊 Version Info

- **Plugin Version**: 1.3.0
- **Minecraft Version**: 1.12.2
- **API**: Spigot/Bukkit
- **Java**: 8+
- **Dependencies**: Vault, PlayerPoints, PlaceholderAPI (optional)

## 📝 Changelog

### v1.3.0 (Current)
- 📦 Updated version to 1.3.0
- 📝 Documentation improvements

### v1.2.0

### New Features
- ✨ **Main Menu System** - Centralized GUI hub with quick access to all features
- ✨ **Daily Bundles** - 3 bundles per day with 8 different reward types
- ✨ **Team Wars** - Full elimination-based combat system with rewards
- ✨ **Team Statistics** - Track kills, deaths, wins, losses for each team
- ✨ **Enhanced Placeholders** - Hover stats in chat for team names
- ✨ **Orbits System** - Secondary currency with admin commands
- ✨ Lottery system with configurable tickets and hourly draws
- ✨ Daily reward system with money and PlayerPoints
- ✨ Heart items that can be right-clicked to gain hearts
- ✨ Team management GUI interface
- ✨ Reload command for hot configuration reloading

### Improvements
- 🔧 Enhanced team management with war capabilities
- 🔧 PlaceholderAPI integration with hover text support
- 🔧 Team death tracking during wars
- 🔧 Beautiful GUI designs for all menus
- 🔧 Enhanced configuration with bundle and war settings
- 🔧 Vault Economy integration (supports any economy plugin)
- 🔧 Random death message variations
- 🔧 Better error handling and logging
- 🔧 Improved message system with color codes
- 🔧 UUID migration support for hearts storage

### Fixes
- 🐛 Default hearts now correctly set to 10
- 🐛 Fixed message formatting and colors
- 🐛 Better heart cap enforcement
- 🐛 Team stats persistence across restarts
- 🐛 War tracking and victory detection

## 📄 License

Free to use and modify for your server.

---

**Enjoy your enhanced Lifesteal experience with Team Wars and Daily Bundles!**

### Daily Rewards
```
/dailyreward             # Claim daily money and points
                         # 24-hour cooldown between claims
```

### Team Operations
```
/team create MyTeam      # Create team "MyTeam"
/team gui               # Open team management GUI
/team invite Steve      # Invite Steve to your team
/team list              # See all teams on server
/team info              # View your team details
/team leave             # Leave your team
!Hello team!            # Send message only to team members
```

### Team Wars
```
/teamwar declare RedTeam   # Declare war on RedTeam
/teamwar accept           # Accept war declaration (leader only)
/teamwar info             # View current war status
/teamwar list             # List all active wars
```

### Admin Commands
```
/giveheart Player 5      # Give Player 5 hearts
/takeheart Player 3      # Take 3 hearts from Player
/orbits give Player 100  # Give 100 orbits
/lifestealreload         # Reload configuration
/menu                    # Open main menu (also for players)
```

## ✨ Features

### Team Operations
```
/team create MyTeam      # Create team "MyTeam"
/team gui               # Open team management GUI (NEW!)
/team invite Steve      # Invite Steve to your team
/team list              # See all teams on server
/team info              # View your team details
/team leave             # Leave your team
!Hello team!            # Send message only to team members
```

### Admin Commands
```
/giveheart Player 5      # Give Player 5 hearts
/takeheart Player 3      # Take 3 hearts from Player
/lifestealreload         # Reload configuration (NEW!)
/hearts Player           # Check Player's hearts
```

## Features

✅ Lottery system with hourly draws
✅ Daily reward system with money and points
✅ Heart item system for trading
✅ Complete team management GUI
✅ Fully compatible with other plugins (non-invasive)
✅ Persistent data storage (hearts, teams, lottery, rewards)
✅ GUI-based shop system
✅ Team communication system
✅ Admin tools for management
✅ Integration with Vault Economy
✅ Custom death messages with variations
✅ Optimized for server performance

## Troubleshooting

**"PlayerPoints plugin is not installed"**
- Ensure PlayerPoints plugin is installed and enabled
- Check that the plugin is in the plugins folder and server is restarted

**Hearts not saving**
- Check folder permissions
- Ensure `plugins/Lifesteal/` folder exists
- Check console for errors

**Shop not opening**
- Make sure you have the correct permissions
- Verify PlayerPoints is working
- Check that inventory slot 27 is available during click

**Team chat not working**
- Make sure you're in a team (`/team info`)
- Try using `/teamchat` instead of `!` prefix
- Check if you have chat permissions

## Building from Source

```bash
mvn clean package
```

The compiled JAR will be in the `target/` directory.

## Support

For issues or feature requests, please check the configuration and permissions first, then review the console logs for any error messages.

## Version Info

- **Plugin Version**: 1.2.0
- **Minecraft Version**: 1.12.2
- **API**: Spigot/Bukkit
- **Java**: 8+
- **Dependencies**: Vault, PlayerPoints

## Changelog (v1.2.0)

### New Features
- ✨ Lottery system with configurable tickets and hourly draws
- ✨ Daily reward system with money and PlayerPoints
- ✨ Heart items that can be right-clicked to gain hearts
- ✨ Team management GUI interface
- ✨ Reload command for hot configuration reloading

### Improvements
- 🔧 Enhanced configuration with more detailed settings
- 🔧 Vault Economy integration (supports any economy plugin)
- 🔧 Random death message variations
- 🔧 Better error handling and logging
- 🔧 Improved message system with color codes
- 🔧 UUID migration support for hearts storage

### Fixes
- 🐛 Default hearts now correctly set to 10
- 🐛 Fixed message formatting and colors
- 🐛 Better heart cap enforcement

## License

Free to use and modify for your server.

---

Enjoy your Lifesteal experience!
