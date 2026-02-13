# Lifesteal Plugin 1.12.2

A comprehensive Lifesteal plugin for Minecraft 1.12.2 servers featuring hearts management, team systems, and dynamic shop interface.

## Features

### 1. **Hearts System**
- Players have dynamically managed hearts (0-20)
- Hearts are persistent across sessions
- Default hearts: 20
- Hearts are tracked per player in `hearts.yml`

### 2. **Admin Commands**
- `/hearts [player]` - View your or another player's hearts
- `/giveheart <player> <amount>` - Give hearts to a player (Admin only)
- `/takeheart <player> <amount>` - Remove hearts from a player (Admin only)

### 3. **Withdraw System**
- `/withdraw <amount>` - Convert hearts into physical items
- Physical Heart Items can be dropped, traded, or stored
- Right-click to deposit hearts back (requires listener setup for deposit)
- Useful for trading hearts between players

### 4. **Heart Shop GUI**
- `/heartshop` - Opens an interactive heart shop
- Buy hearts using PlayerPoints
- Shop options:
  - 1 Heart for 10 Points
  - 5 Hearts for 45 Points
  - 10 Hearts for 80 Points
  - 20 Hearts for 150 Points (VIP only)
- Real-time point and heart balance display

### 5. **Team System**
- Create and manage teams with other players
- Max team size: 10 players
- Team-specific features:
  - `/team create <name>` - Create a new team
  - `/team invite <player>` - Invite a player to your team
  - `/team accept <team>` - Accept a team invite
  - `/team deny <team>` - Deny a team invite
  - `/team list` - View all teams on the server
  - `/team info` - View your team information
  - `/team leave` - Leave your current team
  - Leadership transfer when leader leaves (auto to next member)

### 6. **Team Chat**
- `/teamchat <message>` or `/tc <message>` - Send message to team chat
- Use `!<message>` in global chat to send to team chat
- Team chat messages are color-coded with team name
- Only team members can see team messages
- 

### 7. **Death Mechanics**
- Players lose 1 heart on death
- Killer gains 1 heart (only if there's a killer)
- Custom death messages showing heart count
- Team notifications when members die or kill players

## Permission Nodes

```
lifesteal.user - Basic user permissions (default: true)
lifesteal.hearts - View hearts (default: true)
lifesteal.admin - Admin commands (default: op)
lifesteal.vip - VIP features like max hearts shop (default: false)
```

## Dependencies

- **Spigot/Bukkit 1.12.2** - Core server software
- **PlayerPoints** - For heart shop currency system

## Installation

1. Download the compiled JAR file
2. Place it in your server's `plugins` folder
3. Ensure PlayerPoints plugin is installed
4. Restart your server
5. Plugin will auto-generate configuration files in `plugins/Lifesteal/`

## Data Files

- `hearts.yml` - Stores player heart counts (auto-generated)
- `teams.yml` - Stores team information (auto-generated)
- `config.yml` - Configuration settings

## Configuration (config.yml)

```yaml
settings:
  max-hearts: 20         # Maximum hearts a player can have
  min-hearts: 0          # Minimum hearts (0 = dead)
  default-hearts: 20     # Starting hearts for new players
  max-team-size: 10      # Max players per team

heartshop:
  prices:
    1-heart: 10          # 1 Heart costs 10 PlayerPoints
    5-hearts: 45         # 5 Hearts cost 45 PlayerPoints
    10-hearts: 80        # 10 Hearts cost 80 PlayerPoints
    20-hearts: 150       # Max hearts (VIP only) cost 150 PlayerPoints

death:
  hearts-lost-on-death: 1    # Hearts lost per death
  hearts-gained-on-kill: 1   # Hearts gained per kill
  killer-required: false     # If true, hearts only lost if killed by player
```

## Usage Examples

### Basic Commands
```
/hearts                    # Check your hearts
/hearts PlayerName        # Check someone's hearts
/withdraw 5               # Withdraw 5 hearts as items
/heartshop               # Open shop GUI
```

### Team Operations
```
/team create MyTeam      # Create team "MyTeam"
/team invite Steve       # Invite Steve to your team
/team list              # See all teams on server
/team info              # View your team details
/team leave             # Leave your team
!Hello team!            # Send message only to team members
```

### Admin Commands
```
/giveheart Player 5      # Give Player 5 hearts
/takeheart Player 3      # Take 3 hearts from Player
/hearts Player           # Check Player's hearts
```

## Features

✅ Fully compatible with other plugins (non-invasive)
✅ Persistent data storage
✅ GUI-based shop system
✅ Team communication system
✅ Admin tools for management
✅ Physical heart item trading
✅ Integration with PlayerPoints economy
✅ Custom death messages
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

- **Plugin Version**: 1.0.0
- **Minecraft Version**: 1.12.2
- **API**: Spigot/Bukkit
- **Java**: 8+

## License

Free to use and modify for your server.

---

Enjoy your Lifesteal experience!
