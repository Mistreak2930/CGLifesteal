# CGLifesteal 1.5.3

CGLifesteal is a Paper 26.3 plugin built with Java 25. It provides configurable lifesteal hearts, teams and team wars, orbits, daily bundles, a heart shop, lottery, PlaceholderAPI expansion, and player-facing GUIs.

## Build

```bash
mvn clean package
```

The release artifact is `target/CGLifesteal-1.5.3.jar`.

## Optional Integrations

- PlayerPoints enables points-based features.
- Vault plus an economy provider enables money features.
- PlaceholderAPI enables the plugin's placeholders.

## Configuration

The default config lives in `src/main/resources/config.yml` and is copied to `plugins/Lifesteal/config.yml` on startup. It includes configurable heart/team limits and GUI titles, themes, icons, layouts, and button slots. Use `/lifesteal:lifestealreload` to apply config changes.

Commands are available in both their normal form and the namespaced `/lifesteal:<command>` form. Existing `CGLifesteal` data files are copied when missing from the new `Lifesteal` data folder.

See [README.md](README.md), [BUILD.md](BUILD.md), [INSTALL.md](INSTALL.md), and [CHANGELOG.md](CHANGELOG.md) for user and release documentation.
