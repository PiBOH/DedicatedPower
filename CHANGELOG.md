# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [26.2-1.0.5] - 2026-08-07

### Added
- Help menu now links back to the original project repository (GitHub and Modrinth page)

### Changed
- Closing the GUI window (X button) now asks whether to close only the GUI or also shut down the server
- Report Bug now points back to the original repository issue tracker

## [26.2-1.0.4-beta] - 2026-08-07

### Fixed
- Corrected `fabric.mod.json` metadata: homepage now points to Modrinth and sources to the actual GitHub repository

## [26.2-1.0.3-beta] - 2026-08-06

### Added
- `workflow_dispatch` trigger on all GitHub Actions workflows (Auto Release, Build, Test)
- Auto Release tag no longer includes "v" prefix

## [26.2-1.0.2-beta] - 2026-08-06

### Fixed
- GUI blank screen on startup fixed for Minecraft 26.2
- `buildChatPanel` mixin: populate `logAppenderThread` field to prevent NPE that left the window blank

## [26.2-1.0.1-beta] - 2026-08-06

### Changed
- Ported to Minecraft 26.2 (Chaos Cubed Update)
- Updated to Fabric Loader 0.19.3+
- Updated to Java 25

## [26.2-1.0.0-beta] - 2026-08-06

### Added
- Initial release ported to Minecraft 26.2
- Enhanced server GUI with stats panel (TPS, memory, entity/chunk counts)
- Real-time player list with heads, ping, gamemode, and OP indicators
- Advanced log panel with filtering, search, and command auto-completion
- Server menu: properties editor, whitelist manager, difficulty/gamemode controls
- World menu: time/weather controls, game rules editor, world border settings
- Performance menu: entity clearing, garbage collection, thread dump, performance reports
- Tools menu: command palette, datapack manager, server icon changer
- Help menu: command reference, keyboard shortcuts, system information
- Server backup with progress dialog
- Auto-release workflow via GitHub Actions
