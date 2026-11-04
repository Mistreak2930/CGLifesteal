# CGLifesteal 1.5.2 Installation

## Requirements

- Paper 26.3
- Java 25
- PlayerPoints for points-based features (optional)
- Vault and an economy provider for money features (optional)
- PlaceholderAPI for placeholders (optional)

## Install

1. Download `CGLifesteal-1.5.2.jar` from the public GitHub release.
2. Put the JAR in the server's `plugins/` directory.
3. Start the server. Configuration and data files are created in `plugins/Lifesteal/`.
4. Install optional integrations and restart to enable their features.

On upgrade, stop the server and back up the existing plugin data folder first. On startup, Lifesteal migrates any files missing from `plugins/Lifesteal/` out of the old `plugins/CGLifesteal/` folder without overwriting existing files.

## Configuration

Edit `plugins/Lifesteal/config.yml`, then run `/lifesteal:lifestealreload` to apply changes. Unprefixed command forms remain available.

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
- `/lifesteal:menu` is the namespaced form; all Lifesteal commands and aliases support this prefix.
- `/hearts [player]` views heart counts.
- `/heartshop` opens the points-based heart shop.
- `/team` manages teams.
- `/lottery` opens the money-based lottery; `/dailyreward`, `/bundle`, or `/bundles` opens your daily bundles.
- `/lifestealreload` reloads the configuration and managers (admin permission required).
