# CLAUDE.md — Trackpad Camera Plugin

This file contains project-specific instructions for Claude Code. Follow these conventions in every session.

## Project Goal

A RuneLite plugin for Old School RuneScape (OSRS) that lets macOS users control the in-game camera using their MacBook trackpad gestures (swipe, ctrl+scroll, etc.).

## Architecture

- **Language:** Java 11, Gradle build system
- **Framework:** RuneLite Plugin API (`net.runelite.client`)
- **Platform target:** macOS only (trackpad gesture input)
- **Entry point:** [src/main/java/com/trackpadcamera/TrackpadCameraPlugin.java](src/main/java/com/trackpadcamera/TrackpadCameraPlugin.java)
- **Config:** [src/main/java/com/trackpadcamera/TrackpadCameraConfig.java](src/main/java/com/trackpadcamera/TrackpadCameraConfig.java)
- **RuneLite version pin:** See [gradle.properties](gradle.properties)

## Key Files

| File | Purpose |
|------|---------|
| `src/main/java/com/trackpadcamera/TrackpadCameraPlugin.java` | Main plugin — gesture detection, camera manipulation |
| `src/main/java/com/trackpadcamera/TrackpadCameraConfig.java` | RuneLite config panel definitions |
| `build.gradle` | Gradle build config, RuneLite dependency |
| `gradle.properties` | Pinned RuneLite version |
| `docs/` | Design docs and feature catalog |

## Gesture Model

| Gesture | macOS Event | Camera Action |
|---------|------------|---------------|
| Two-finger swipe left/right | `SHIFT + scroll` | Rotate yaw |
| Ctrl + two-finger scroll up/down | `CTRL + scroll` | Tilt pitch |
| Two-finger swipe up/down | Vertical scroll | Zoom (OSRS native) |

Trackpad events are distinguished from scroll wheel events by checking `getPreciseWheelRotation()` vs `getWheelRotation()` divergence.

## RuneLite Camera API

- `client.getCameraYaw()` / `client.setCameraYawTarget(int)` — yaw range 0–2047
- `client.getCameraPitch()` / `client.setCameraPitchTarget(int)` — pitch range 128–512
- `client.get3dZoom()` / `client.runScript(ScriptID.CAMERA_DO_ZOOM, zoom, zoom)` — zoom range ~128–1500

## Development Conventions

- Keep the plugin lightweight — no heavy UI or overlays unless explicitly added
- All new gestures or behaviors must have a corresponding config toggle in `TrackpadCameraConfig`
- Do not break compatibility with standard mouse scroll wheel users
- Sensitivity values exposed in config should be integers in range 1–20 unless there is a strong reason otherwise
- Always check `client.getGameState() == GameState.LOGGED_IN` before acting on events
- Consume (`e.consume()`) trackpad events so RuneLite doesn't double-process them

## Documentation Format

Two tiers:

**Reference / living docs** → `/docs/FILENAME.md` (docs root)
- Maintained across the project lifetime; always current
- Examples: `PROJECT_OVERVIEW.md`, `GESTURE_DETECTION.md`, `CAMERA_CONTROL_API.md`, `CONFIGURATION.md`, `FEATURE_CATALOG.md`, `MANUAL_TESTING.md`

**Feature requirement specs** → `/docs/YYYY-MM/##_FEATURE_TITLE.md`
- Time-scoped; written when a feature is being designed, not updated afterward
- Cover: **Summary**, **Motivation**, **Requirements**, **Design**, **Config Options**, **Implementation Notes**
- Examples: a new gesture type, a momentum system, a platform port

All docs should link to relevant source files using relative markdown paths.

## Validation Procedure

Run these in order before every commit:

```bash
./gradlew checkstyleMain   # 1. lint — fast, catches style issues early
./gradlew build            # 2. compile + checkstyle + package
./gradlew test             # 3. unit tests
```

Or as a single command:

```bash
./gradlew checkstyleMain build test
```

All three must pass before pushing. If checkstyle fails, fix formatting before attempting a build.

## Build & Test

```bash
./gradlew build          # compile and run checkstyle
./gradlew test           # run unit tests
```

Load as a local plugin via RuneLite's "Load unpacked plugin" developer mode.
