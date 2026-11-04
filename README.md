# CGLifesteal 1.5.3

A configurable Lifesteal plugin for Paper 26.3 servers, featuring heart management, teams and wars, a heart shop, lottery, daily bundles, and interactive GUIs. Requires Java 25.

## 📖 Quick Start

### Installation
1. Download the compiled JAR from releases
2. Place JAR in your `plugins/` folder
3. Install required plugins:
  - **PlayerPoints** for points-based shops and rewards (optional)
  - **Vault** plus an economy provider for money features (optional)
4. Restart your server
5. Configure `plugins/Lifesteal/config.yml` as needed
6. Restart again or use `/lifestealreload`

### Basic Commands
- `/hearts` - Check your hearts
- `/menu` - Open main menu (players)
- `/team create [name]` - Create a team
- `/lottery` - Buy lottery tickets
- `/dailyreward`, `/bundle`, or `/bundles` - Open your daily bundles

All commands and aliases also support the namespaced form, such as `/lifesteal:menu` or `/lifesteal:bundle`.

## 🎮 Features Overview

### 1. **Hearts System**
- Players have dynamically managed hearts with configurable minimum and maximum values
- Hearts are persistent across sessions
- Default hearts: 10 (set on first join)
- Hearts are tracked per player in `hearts.yml`
- Ban players automatically when they reach 0 hearts
- **Configurable** death mechanics in `config.yml`

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
  - **Daily Bundles** - Open your automatically replenished bundles
  - **Heart Shop** - Purchase hearts with PlayerPoints
  - **Lottery** - Try your luck
  - **Teams** - Manage your team
- View your personal stats (hearts, orbits, bundles claimed)
- Beautiful, organized interface

### 5. **Daily Bundles System** ✨ NEW
- Automatically receive **3 bundles per day** (resets every 24 hours)
- Open one bundle at a time from a single bundle button
- Each opening randomly selects and grants one reward
- Watch the reward animation and see the result announced in chat
- The dedicated spin screen closes when the opening ends
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
- See the countdown to the next draw in the lottery menu
- Current pool and ticket count display
- Multiple players can participate

### 7. **Daily Bundle Access**
- `/dailyreward`, `/bundle`, or `/bundles` - Open the daily bundles screen
- Bundles replenish automatically every 24 hours; no separate reward claim is needed
- The daily reward flow grants no additional money or points

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
- `/lifestealreload` or `/lifesteal:lifestealreload` - Reload plugin configuration without restarting
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
  ├─ lifesteal.daily-reward - Access daily bundles (default: true)
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

**Required:**
- **Paper 26.3** - Core server software
- **Java 25** - Server runtime

**Optional:**
- **PlayerPoints** - Points currency for the heart shop and optional bundle rewards
- **Vault** - Economy integration for money features
- **PlaceholderAPI** - For advanced chat placeholder integration

## 📦 Installation & Setup

### Step-by-Step Installation

1. **Download & Install**
   ```
   1. Download latest JAR from releases
   2. Place file in plugins/ folder
   3. Run /stop or restart server
   ```

2. **Install Dependencies**
   - Download and install:
    - PlayerPoints (optional; enables points features)
    - Vault and an economy provider (optional; enables money features)
     - PlaceholderAPI (optional but recommended)
   - Place these JARs in plugins/ folder and restart

3. **Configure** (Optional)
  - Edit `plugins/Lifesteal/config.yml` with your preferences
   - All settings have helpful comments explaining each option
   - Default values work for most servers

4. **Reload Plugin**
   - Use `/lifestealreload` to apply config changes without restarting
   - No server restart needed after configuration edits

### Verify Installation

Check the console on startup. You should see:
```
✓ Lifesteal Plugin ENABLED successfully!
✓ Vault Economy integration enabled
✓ PlayerPoints integration enabled
```

If you see warnings, check the Troubleshooting section below.

## ✨ Verified Features

✅ **Hearts System** - Complete player hearts management
✅ **Main Menu System** - Centralized GUI hub for all features
✅ **Daily Bundles** - 3 bundles per day with random rewards
✅ **Team System** - Create teams, manage members, track stats
✅ **Team Wars** - Elimination-based team combat
✅ **Lottery System** - Hourly draws with prize pools
✅ **Daily Bundle Rewards** - Random rewards with an animated opening screen
✅ **Orbits System** - Secondary currency system
✅ **Physical Heart Trading** - Withdraw/deposit hearts as items
✅ **Team Chat** - Private team-only communication
✅ **PlaceholderAPI Support** - Advanced chat integration
✅ **Persistent Data** - All progress saved across restarts
✅ **Admin Tools** - Full management commands
✅ **Performance Optimized** - Lightweight & efficient

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
2. Three bundles become available automatically every 24 hours
3. Use the single bundle button to open one bundle
4. Watch the randomized reward spin; the menu closes when it finishes

### Bundle Tips
- Open bundles daily to maximize rewards
- All possible rewards are visible
- Track total bundles claimed in stats

## 🔧 Troubleshooting & Support

### Plugin Startup Issues

**Warnings in console about missing dependencies**
- Install required plugins: PlayerPoints, Vault
- Ensure plugins are in `plugins/` folder
- Restart server completely (not just `/stop`, but full restart)
- Check that the server runs Paper 26.3 on Java 25

**"PlayerPoints integration failed"**
- Verify PlayerPoints is installed and enabled (`/plugins`)
- Check that PlayerPoints is installed if using points-based features
- Look for red error messages in startup logs
- If still failing, contact PlayerPoints support

### Runtime Issues

**Hearts not saving**
- Check file permissions on `plugins/Lifesteal/` folder
- Ensure folder exists and is readable/writable
- Verify disk space is available
- Check console for permission errors

**Team wars not starting**
- Both leaders must be online
- Verify teams aren't on cooldown (60 min default)
- Check neither team is in another war
- Use `/teamwar info` to check war status

**Placeholders not showing in chat**
- Install PlaceholderAPI plugin
- Run `/papi reload` after install
- Verify format: `%lifesteal_team_name_hover%`
- Check your chat plugin supports PlaceholderAPI

**Daily Bundles not resetting**
- Check server system time is correct
- Verify `reset-time-hours` in config (default: 24)
- Check `bundles.yml` file for reset timestamps
- Use `/lifestealreload` after changing config

### Common Questions

**Q: Can I change the ban time for 0 hearts players?**
A: Currently players are permanently banned. You can unban them manually with `/unban` or use another plugin to manage auto-unbans.

**Q: How do I customize bundle rewards?**
A: Edit `config.yml` under `bundles.possible-rewards`. Add or remove reward types as needed.

**Q: Can I disable the menu or any feature?**
A: Yes, most features can be disabled in `config.yml` by setting `enabled: false`.

**Q: How do I migrate from another lifesteal plugin?**
A: Data is stored in YAML format in the plugin folder. You may need to manually migrate player heart counts.

## 🔨 Building from Source

### Prerequisites
- Java 25 JDK
- Maven 3.6+

### Build Steps

```bash
# Clone or extract the source
cd Lifesteal

# Build the plugin
mvn clean package

# JAR will be in target/ folder
```

## ⚙️ Configuration Guide

### config.yml - Key Settings

```yaml
# Number of hearts players start with
default-hearts: 10

# Maximum hearts a player can reach
max-hearts: 20

# Hearts lost when player dies (0 = disabled)
hearts-lost-on-death: 1

# Hearts gained when killing (0 = disabled)  
hearts-gained-on-kill: 1
```

For detailed configuration, see comments in `plugins/Lifesteal/config.yml` after first run. GUI titles accept MiniMessage formatting; theme materials, button slots, heart bounds, and team limits can also be customized there.

## 💡 Tips & Tricks

1. **Balanced Economy** - Adjust lottery prices and bundle rewards to match your server economy
2. **Team Size** - Set `max-team-size` based on server player count
3. **War Cooldown** - Increase `cooldown-minutes` if wars happen too frequently
4. **Bundle Rewards** - Customize rewards to match your server themes
5. **Custom Messages** - Edit `messages.yml` to match your server's style

## 📊 Version Info

- **Plugin Version**: 1.5.3
- **Minecraft Version**: Paper 26.3
- **Java**: 25+
- **Optional integrations**: Vault, PlayerPoints, PlaceholderAPI

## 📝 Changelog

### v1.5.3 (Current)
- Fixed circular admin permission inheritance that caused startup stack overflow.

### v1.5.2
- Added namespaced `/lifesteal:<command>` forms for all commands and aliases.
- Consolidated the bundle opener, randomized rewards for each opening, and added a spin screen that closes when complete.

### v1.5.1
- Three daily bundles are granted automatically; bundle openings animate and announce the reward.
- Lottery menu shows the next-draw countdown.
- Fixed team leader membership and Heart Shop error messages on existing installations.
- Improved menu layout and configuration organization.

### v1.5.0
- Paper 26.3 and Java 25 support
- Configurable GUI titles, themes, icons, and button slots
- Updated layouts and functional main-menu heart-shop navigation
- Configurable heart/team limits and persistent heart-item data

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

---

## 📄 License & Support

This plugin is free to use and modify for your server.

For issues or feature requests, review the console logs and troubleshooting section above, then contact the developer.

## 👥 Credits

**CGLifesteal v1.5.3** - Developed by Coolguy1353 for Paper 26.3

**Enjoy your enhanced Lifesteal experience!**
