# Configuration

## Summary

The plugin exposes per-gesture toggles and sensitivity sliders through the standard RuneLite config panel. All settings are persisted by RuneLite's `ConfigManager`.

## Config Group

Config group key: `trackpadcamera`

Defined in [TrackpadCameraConfig.java](../src/main/java/com/trackpadcamera/TrackpadCameraConfig.java).

## Current Config Options

### Section: Zoom

> Enable "Use Scroll Wheel to Zoom" in OSRS Settings > Controls > Camera to allow vertical scroll to zoom natively.

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `zoomNote` | boolean | `false` | Informational reminder — not read by the plugin |
| `invertZoom` | boolean | `false` | Reverse the scroll direction for zooming |

### Section: Rotation

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enableRotation` | boolean | `true` | Two-finger horizontal swipe rotates camera yaw |
| `invertRotation` | boolean | `false` | Reverse the rotation direction |
| `rotationSensitivity` | int (1–20) | `5` | Multiplier applied to horizontal swipe delta for yaw |

### Section: Tilt

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| `enableTilt` | boolean | `true` | Ctrl + two-finger scroll tilts camera pitch |
| `invertTilt` | boolean | `false` | Reverse the tilt direction |
| `tiltSensitivity` | int (1–20) | `5` | Multiplier applied to ctrl+scroll delta for pitch |

## Design Conventions

- Every gesture axis has a toggle and a sensitivity control. Never add a gesture without both.
- Sensitivity ranges are 1–20 (integer). Avoid floats in config; convert internally.
- Defaults should feel reasonable out of the box on a MacBook with standard trackpad settings.
- Group related options together in the config panel using `@ConfigSection`.

## Planned Config Options (Future)

| Key | Type | Purpose |
|-----|------|---------|
| `smoothingEnabled` | boolean | Enable momentum/inertia on swipes |
| `smoothingDecay` | int (1–10) | How quickly swipe momentum decays |
| `trackpadThreshold` | int (1–10) | Advanced: adjust trackpad detection sensitivity |
