# Trackpad Camera Plugin for RuneLite

Control the Old School RuneScape camera using trackpad gestures. Developed for MacBook trackpads, but compatible with any input device that encodes gestures the same way.

## Gestures

| Gesture | Action |
|---|---|
| Two-finger swipe left/right | Rotate camera (yaw) |
| Ctrl + two-finger scroll up/down | Tilt camera (pitch) |
| Two-finger swipe up/down | Zoom camera (OSRS native) |

## Configuration

Each gesture can be individually enabled, inverted, and tuned:

| Section | Options |
|---|---|
| Zoom | Invert scroll direction |
| Rotation | Enable/disable, invert, sensitivity (1–20) |
| Tilt | Enable/disable, invert, sensitivity (1–20) |

The plugin intercepts scroll events by modifier key — it does not detect the input device. Mouse users who scroll while holding SHIFT or CTRL will also trigger rotation and tilt. Zoom inversion applies to all plain vertical scroll input.

## Requirements

- Java 11
- RuneLite client
- A trackpad that encodes horizontal swipes as `SHIFT+scroll` and ctrl+scroll as `CTRL+scroll` (standard on macOS)

## Development

```bash
./gradlew build    # compile and lint
./gradlew test     # run unit tests
./gradlew run      # launch RuneLite with the plugin loaded
```
