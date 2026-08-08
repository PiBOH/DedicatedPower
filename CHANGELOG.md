# Changelog

## [26.2-1.2.8-rc6] - 2026-08-08

### Fixed
- Fixed the server menu bar staying light/white in Dark mode: the native Windows menu bar skin is replaced with the themed renderer (issue [#3](https://github.com/PiBOH/DedicatedPower/issues/3))
- Submenu arrows, keyboard accelerator text, and disabled menu entries now use theme colors in Dark mode
- Checkbox and radio menu items now use the same theme-aware icons as the rest of the GUI

### Added
- Added `Tools > Export Mod List...` to save the installed mods as a plain text file or as a Markdown table

## [26.2-1.2.7-rc5] - 2026-08-08

### Fixed
- MOTD formatting buttons are never clipped anymore: the toolbar wraps onto additional rows (WrapLayout) instead of scrolling horizontally, so every button is always fully visible

## [26.2-1.2.7-rc4] - 2026-08-08

### Fixed
- Fixed section titles (e.g. `Message`, `Saved MOTDs`, `Theme`, `Log colors`) rendering black in Dark mode: titled borders now use the theme foreground color
- Themed scrollbar thumbs and tracks so they follow Light/Dark mode instead of the native light gray

## [26.2-1.2.7-rc3] - 2026-08-08

### Changed
- Checkbox and radio button glyphs now use custom theme-aware icons that stay readable in both Light and Dark mode instead of the native Look & Feel icons

## [26.2-1.2.7-rc2] - 2026-08-08

### Changed
- The MOTD editor now enforces Minecraft's two-line limit: extra lines are trimmed when typing, pasting, loading history entries, or resetting
- The editor status bar now shows the current line count (`1/2` or `2/2`) next to the character counter

## [26.2-1.2.7-rc1] - 2026-08-08

### Fixed
- Fixed the MOTD editor formatting controls being clipped at narrow window sizes: the button strip no longer wraps into cut-off rows and instead scrolls horizontally
- Applied explicit Light/Dark styling to every MOTD editor control, including the formatting buttons, toolbar strip, editor caret and selection colors, secondary labels, scrollbars, and history checkbox

## [26.2-1.2.6-beta] - 2026-08-07

### Added
- Redesigned `Tools > MOTD Editor...` with a professional server-hosting-style workspace
- Added one-click Minecraft formatting controls for common colors, bold, italic, underline, strikethrough, and reset codes
- Added a live character counter, MOTD status indicator, copy action, reset action, and clearer `Save & Apply` workflow
- Added a server-list-style preview card and richer saved-MOTD history presentation

### Changed
- Improved MOTD editor spacing, hierarchy, preview readability, and Light/Dark theme integration

## [26.2-1.2.5-beta] - 2026-08-07

### Fixed
- Console output now follows the newest log automatically until the user scrolls upward
- Returning the scrollbar to the bottom automatically re-enables follow-bottom mode

## [26.2-1.2.4-beta] - 2026-08-07

### Fixed
- Fixed command input caret positioning so the first typed character no longer moves to the end of the command
- Deferred autocomplete until after Swing finishes the text mutation and ignored stale asynchronous suggestions

## [26.2-1.2.3-beta] - 2026-08-07

### Fixed
- Preserved custom log palette colors when switching to Dark mode or when Swing refreshes the Appearance dialog controls

## [26.2-1.2.2-beta] - 2026-08-07

### Fixed
- `/opengui` now reuses and brings forward an existing GUI instead of opening duplicate windows
- `Close GUI Only` now hides the existing window so `/opengui` can reopen it later, including when the server was started with `--nogui`

## [26.2-1.2.1-beta] - 2026-08-07

### Added
- Added the DedicatedPower mod version below the Minecraft version in `Help > System Information`

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [26.2-1.2.0-beta] - 2026-08-07

### Added
- Added `/opengui`, which explicitly opens the DedicatedPower GUI even when the server was started with `--nogui` (when a graphical environment is available)
- Added a `Network` menu showing the Java server port and detecting the Bedrock port from common Geyser-Fabric configuration paths
- Added `Tools > MOTD Editor...` with live preview, Minecraft legacy color-code support, persistent MOTD history, history enable/disable, selected-entry deletion, and clear-history controls

### Fixed
- Applied explicit Basic Swing menu and popup renderers, including menus opened after the initial GUI theme pass, so menu entries and submenus follow Light/Dark mode more reliably

## [26.2-1.1.2-beta] - 2026-08-07

### Fixed
- Fixed ordinary Swing buttons still using the native light renderer in dark mode by adding explicit themed rendering for normal, hover, and pressed button states

## [26.2-1.1.1-beta] - 2026-08-07

### Fixed
- Completed theme switching for existing buttons and controls, including checkboxes, radio buttons, menu items, spinners, lists, tables, popups, and scrollbars in the GUI and open dialogs

## [26.2-1.1.0-beta] - 2026-08-07

### Added
- Added a persistent Light/Dark appearance switch under `Tools > Appearance...`
- Added a configurable log color palette for INFO, WARN, ERROR, DEBUG, and CHAT messages using native Swing color pickers
- Appearance settings are stored in `config/dedicatedpower-gui.properties` and restored on the next server start

### Changed
- Integrated the appearance controls from issue [#1](https://github.com/PiBOH/DedicatedPower/issues/1) and issue [#2](https://github.com/PiBOH/DedicatedPower/issues/2), with the theme applied to the log panel, player list, statistics panel, menus, and dialogs
- The appearance and GUI configuration work is documented alongside the related [PiBOH/jarock](https://github.com/PiBOH/jarock) project

## [26.2-1.0.9] - 2026-08-07

### Added
- Added a `Done!` ASCII ready banner when the server finishes starting, working both with and without Geyser
- Inspired by Jarock's `server-ready-banner.txt`: [PiBOH/jarock](https://github.com/PiBOH/jarock)

## [26.2-1.0.8] - 2026-08-07

### Fixed
- Moved shared GUI state and logging outside the mixin package and split the GUI mixins into standalone classes to prevent Fabric mixin class-loading crashes during server startup

## [26.2-1.0.7] - 2026-08-07

### Fixed
- The enhanced server GUI is no longer forced open when the server is launched with `--nogui` (or in a headless environment), matching vanilla behaviour

## [26.2-1.0.6] - 2026-08-07

### Fixed
- Fixed the top menu bar not appearing: menus are now built synchronously and the frame is always revalidated/repainted after install
- Fixed very slow shutdown with "Close GUI and Server": the server now stops on a background thread so the GUI stays responsive and the process exits reliably once worlds are saved

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
