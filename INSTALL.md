# CGLifesteal 1.5.0 Installation

## Requirements

- Paper 26.3
- Java 25
- PlayerPoints for points-based features (optional)
- Vault and an economy provider for money features (optional)
- PlaceholderAPI for placeholders (optional)

## Install

1. Download `CGLifesteal-1.5.0.jar` from the public GitHub release.
2. Put the JAR in the server's `plugins/` directory.
3. Start the server. Configuration and data files are created in `plugins/CGLifesteal/`.
4. Install optional integrations and restart to enable their features.

For an upgrade from 1.4.0, stop the server and back up `plugins/Lifesteal/` first. On first startup, CGLifesteal moves that folder to `plugins/CGLifesteal/` automatically. If that migration cannot complete, the plugin logs an error and leaves the original files untouched.

## Configuration

Edit `plugins/CGLifesteal/config.yml`, then run `/lifestealreload` to apply changes.

- `settings.default-hearts`, `settings.min-hearts`, and `settings.max-hearts` control heart limits.
- `settings.ban-on-zero-hearts` controls automatic bans at zero hearts.
- `teams.*` controls team-name limits and maximum team size.
- `gui.titles.*` accepts MiniMessage formatting.
- `gui.theme.*` sets the shared inventory pane materials.
- `gui.materials.menu.*` overrides main-menu icons.
- `gui.slots.*` sets zero-based button positions. Keep slots unique and within the screen.
- `gui.team.inventory-size` accepts chest sizes from 27 to 54 in multiples of 9.

The shipped defaults are ready to use. Optional integrations can be absent; their dependent features are disabled gracefully.

## Commands

- `/menu` opens the main feature menu.
- `/hearts [player]` views heart counts.
- `/heartshop` opens the points-based heart shop.
- `/team` manages teams.
- `/lottery` and `/dailyreward` open points-based reward screens.
- `/lifestealreload` reloads the configuration and managers (admin permission required).
