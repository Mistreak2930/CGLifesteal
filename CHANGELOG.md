# Changelog

All notable changes to the Lifesteal plugin will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.5.2] - 2026-11-04

### Added
- Added `/lifesteal:<command>` namespaced forms for all plugin commands and aliases.
- Added `/bundle` and `/bundles` aliases for opening daily bundles.
- Added a dedicated bundle reward spin screen.

### Changed
- Consolidated daily bundles into one opening button that shows the available count.
- Randomized each bundle reward at opening; removed the next-reward preview.
- Close the menu as soon as the bundle spin ends.
- Use the `Lifesteal` plugin namespace while preserving existing `CGLifesteal` data through migration.

## [1.5.1] - 2026-11-04

### Changed
- Daily rewards now provide three automatically replenished bundles; the old separate cash and points claim has been removed.
- Bundle openings now animate the possible rewards and announce the reward actually granted.
- Lottery menus show the countdown to the next draw.
- Improved the main-menu layout and organized bundle animation settings in the config.
- Existing messages files are supplemented with new default message keys without replacing custom messages.

### Fixed
- Team leaders in older team data are recognized as team members after loading.
- Heart shop insufficient-points messages fall back to newly added defaults on existing installations.

## [1.5.0] - 2026-11-04

### Added
- Added MiniMessage-configurable titles, theme materials, and button slots for plugin GUIs.
- Added configurable main-menu icons and a compact five-row menu layout.
- Added persistent item data for withdrawn hearts so amounts no longer rely on item durability.

### Changed
- Updated the server API target to Paper 26.3 and the runtime/compiler baseline to Java 25.
- Renamed the plugin to CGLifesteal and updated the author to Coolguy1353.
- Updated all GUIs to current Minecraft materials and semantic inventory holders.
- Connected the main-menu Heart Shop button to the functional shop GUI.
- Applied configured heart limits, team limits, and zero-heart ban behavior at runtime.
- Made PlayerPoints an optional integration; features that require it remain unavailable without it.

### Fixed
- Fixed custom GUI titles breaking inventory click handling.
- Fixed documented heart and team configuration values being ignored.

## [1.4.0] - 2026-04-23

### Added
- **Heart Shop**: Re-enabled the heart shop feature allowing players to purchase hearts using PlayerPoints
  - Added `/heartshop` command with alias `/hshop`
  - Added HeartShopGUI with three purchase tiers (1 heart, 5 hearts, 10 hearts)
  - Added HeartShopListener to handle GUI interactions
  - Added heartshop configuration section in config.yml
  - Added shop-related messages in messages.yml
  - Integrated with PlayerPoints API for point transactions

### Changed
- Updated plugin version from 1.3.0 to 1.4.0
- Improved command registration and listener management

### Fixed
- Restored heart shop functionality that was previously disabled

## [1.3.0] - 2024-XX-XX

### Added
- Team Wars system
- Bundle reward system
- Orbits currency system
- Enhanced GUI system
- Daily rewards
- Lottery system

### Changed
- Improved heart management
- Enhanced team system

### Fixed
- Various bug fixes and improvements

## [1.2.0] - 2024-XX-XX

### Added
- Basic team system
- Heart withdrawal/deposit system
- GUI interfaces

### Changed
- Improved configuration options

## [1.1.0] - 2024-XX-XX

### Added
- Basic lifesteal mechanics
- Heart management commands
- Player death handling

## [1.0.0] - 2024-XX-XX

### Added
- Initial release
- Core lifesteal functionality
- Basic commands and permissions