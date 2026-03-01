# Trackpad Camera Plugin for RuneLite

Control the Old School RuneScape camera using Mac trackpad gestures.

## Gestures

| Gesture | Action |
|---|---|
| Two-finger swipe left/right | Rotate camera (yaw) |
| Ctrl + two-finger scroll up/down | Tilt camera (pitch) |
| Two-finger swipe up/down | Zoom camera (OSRS native) |

## Installation

1. Clone this repo
2. Open in IntelliJ IDEA (recommended) with the RuneLite plugin development setup
3. Run `./gradlew build`
4. Load as a local plugin in the RuneLite client

## Development Setup

Follow the [RuneLite plugin development guide](https://github.com/runelite/plugin-hub#development) to configure your IDE and local RuneLite client for plugin testing.

## Configuration

All gestures and their sensitivities can be toggled and tuned in the RuneLite plugin config panel.

## Requirements

- macOS (trackpad gesture support)
- Java 11+
- RuneLite client
