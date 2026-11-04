# CGLifesteal 1.5.1

CGLifesteal is a Paper 26.3 plugin built with Java 25. It provides configurable lifesteal hearts, teams and team wars, orbits, daily bundles, a heart shop, lottery, PlaceholderAPI expansion, and player-facing GUIs.

## Build

```bash
mvn clean package
```

The release artifact is `target/CGLifesteal-1.5.1.jar`.

## Optional Integrations

- PlayerPoints enables points-based features.
- Vault plus an economy provider enables money features.
- PlaceholderAPI enables the plugin's placeholders.

## Configuration

The default config lives in `src/main/resources/config.yml` and is copied to `plugins/CGLifesteal/config.yml` on startup. It includes configurable heart/team limits and GUI titles, themes, icons, layouts, and button slots. Use `/lifestealreload` to apply config changes.

On upgrade from the previous `Lifesteal` plugin name, the data folder is migrated automatically to `CGLifesteal`; back up server data before upgrading.

See [README.md](README.md), [BUILD.md](BUILD.md), [INSTALL.md](INSTALL.md), and [CHANGELOG.md](CHANGELOG.md) for user and release documentation.
