# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Chat is a BentoBox addon for Minecraft (Paper/Spigot) that provides Team Chat and Island Chat for island-type game modes (BSkyBlock, AcidIsland, CaveBlock, SkyGrid). It requires Java 21 and targets Paper 1.21.x.

## Build Commands

```bash
mvn clean package          # Build (default goal)
mvn test                   # Run all tests
mvn -Dtest=ChatListenerTest test   # Run a single test class
mvn -Dtest=ChatListenerTest#onChat test  # Run a single test method
```

The build produces `target/Chat-{version}-SNAPSHOT-LOCAL.jar`.

## Architecture

This is a **BentoBox Pladdon** (plugin-addon):

- `ChatPladdon` — Entry point loaded by BentoBox's Pladdon system, creates the `Chat` addon instance
- `Chat` — Main addon class. On enable: loads `Settings` from config.yml, registers commands per game mode, creates `ChatListener`, and registers the `IsTeamChatHandler` request handler
- `ChatListener` — Core logic. Implements both `Listener` and `EventExecutor`. Intercepts `AsyncPlayerChatEvent` (registered manually with configurable priority from Settings). Maintains in-memory sets for team chat users, island chatters, team spies, and island spies. All toggle state is held in memory (not persisted)
- `Settings` — BentoBox `ConfigObject` stored at `addons/Chat/config.yml`. Controls which game modes have team/island chat, logging, event priority, and default chat game mode
- `IsTeamChatHandler` — BentoBox request handler that lets other addons query if a player has team chat enabled

Commands follow BentoBox's `CompositeCommand` pattern and are registered dynamically onto each game mode's player/admin command trees:
- Player: `IslandChatCommand` (chat), `IslandTeamChatCommand` (teamchat)
- Admin: `AdminIslandChatSpyCommand` (chatspy), `AdminTeamChatSpyCommand` (teamchatspy)

## Testing

Tests use JUnit 5 + Mockito + MockBukkit. The `CommonTestSetup` base class provides standard mocks for BentoBox, Bukkit, Player, World, Island, etc. New test classes should extend `CommonTestSetup` and call `super.setUp()`/`super.tearDown()`. The `WhiteBox` utility sets private/static fields via reflection for test setup.

Localization strings are defined in `src/main/resources/locales/` (en-US.yml is the primary locale).
