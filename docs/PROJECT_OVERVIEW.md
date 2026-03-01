# Project Overview

## Summary

The Trackpad Camera Plugin is a RuneLite plugin for Old School RuneScape (OSRS) that maps macOS trackpad gestures to in-game camera controls. It enables a more natural, fluid camera experience for MacBook users without requiring mouse buttons or keyboard modifiers.

## Motivation

OSRS camera control traditionally requires holding the middle mouse button or right mouse button to rotate, and scrolling to zoom. On a MacBook trackpad this is awkward — there is no dedicated middle button and right-click requires a two-finger tap or modifier key. Trackpad users have access to rich gesture input (two-finger swipe, ctrl+scroll) that maps naturally to yaw, pitch, and zoom.

## Scope

### In Scope
- Two-finger swipe left/right → camera yaw rotation
- Ctrl + two-finger scroll up/down → camera pitch tilt
- Two-finger swipe up/down → zoom (OSRS native, with optional invert)
- Per-gesture enable/disable toggles in RuneLite config panel
- Per-gesture sensitivity sliders in config panel
- Graceful pass-through for non-trackpad scroll events

### Out of Scope (v1)
- Three-finger gestures
- Gesture customization (remapping gestures to different actions)
- Windows / Linux trackpad support
- Overlay UI or HUD elements

## Architecture Overview

```
macOS trackpad input
       │
       ▼
Java AWT MouseWheelEvent
  (via RuneLite MouseWheelListener)
       │
       ▼
TrackpadCameraPlugin.mouseWheelMoved()
  ├── isTrackpadEvent()   ← distinguish trackpad vs scroll wheel
  ├── SHIFT modifier      ← horizontal swipe → yaw
  ├── CTRL modifier       ← ctrl+scroll → pitch
  └── no modifier         ← vertical swipe → zoom (OSRS native / inverted)
       │
       ▼
RuneLite Client API
  client.setCameraYawTarget()
  client.setCameraPitchTarget()
  client.runScript(ScriptID.CAMERA_DO_ZOOM, zoom, zoom)  ← only when invertZoom enabled
```

## Key Source Files

- [TrackpadCameraPlugin.java](../src/main/java/com/trackpadcamera/TrackpadCameraPlugin.java) — core plugin logic
- [TrackpadCameraConfig.java](../src/main/java/com/trackpadcamera/TrackpadCameraConfig.java) — config panel interface

## Related Docs

- [GESTURE_DETECTION.md](GESTURE_DETECTION.md)
- [CAMERA_CONTROL_API.md](CAMERA_CONTROL_API.md)
- [CONFIGURATION.md](CONFIGURATION.md)
- [MANUAL_TESTING.md](MANUAL_TESTING.md)
- [FEATURE_CATALOG.md](FEATURE_CATALOG.md)
