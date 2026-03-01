# Feature Catalog

This document is the canonical list of all features: shipped, in-progress, and planned. Update this file when a feature changes status.

---

## Status Legend

| Status | Meaning |
|--------|---------|
| ✅ Shipped | Implemented and in the current build |
| 🔨 In Progress | Actively being developed |
| 📋 Planned | Designed but not yet started |
| 💡 Idea | Under consideration, not yet designed |
| ❌ Rejected | Explicitly decided against |

---

## Core Gesture Controls

### ✅ Yaw Rotation — Two-Finger Horizontal Swipe

Swipe two fingers left/right on the trackpad to rotate the camera horizontally.

- **Config:** `enableRotation` toggle, `rotationSensitivity` slider
- **Doc:** [02_GESTURE_DETECTION.md](GESTURE_DETECTION.md), [03_CAMERA_CONTROL_API.md](CAMERA_CONTROL_API.md)

---

### ✅ Pitch Tilt — Ctrl + Two-Finger Scroll

Hold Ctrl and scroll two fingers up/down to tilt the camera vertically.

- **Config:** `enableTilt` toggle, `invertTilt` invert, `tiltSensitivity` slider
- **Doc:** [GESTURE_DETECTION.md](GESTURE_DETECTION.md), [CAMERA_CONTROL_API.md](CAMERA_CONTROL_API.md)

---

### ✅ Zoom — OSRS Native (with optional invert)

Two-finger vertical scroll is passed through to OSRS, which handles zoom natively via "Use Scroll Wheel to Zoom" in Settings → Controls → Camera. When `invertZoom` is enabled, the plugin intercepts the scroll and applies the zoom in the opposite direction via `CAMERA_DO_ZOOM`.

- **Config:** `invertZoom` toggle
- **Doc:** [GESTURE_DETECTION.md](GESTURE_DETECTION.md)

---

## Configuration & Usability

### ✅ Per-Gesture Enable/Disable Toggles

Each gesture can be independently disabled from the RuneLite plugin config panel.

---

### ✅ Per-Gesture Sensitivity Sliders

Each gesture has a sensitivity multiplier (1–20) in the config panel.

---

### ✅ Scroll Wheel Pass-Through

Non-trackpad scroll events (physical scroll wheel) are not consumed by the plugin and continue to work normally.

---

## Planned Features

### ✅ Gesture Direction Inversion

Each gesture axis can be inverted independently.

- **Config options:** `invertRotation`, `invertTilt`, `invertZoom` (booleans) — all shipped
- **Motivation:** Some users prefer "natural" vs "standard" scroll directions; matches macOS System Settings scroll direction preference

---

### 📋 Swipe Momentum / Inertia

After a swipe gesture ends, camera continues rotating and decelerates naturally — matching the feel of macOS scroll momentum.

- **Config options:** `smoothingEnabled` (boolean), `smoothingDecay` (int 1–10)
- **Implementation approach:** Accumulate velocity vector from recent swipe events; apply decay per game tick via a `GameTick` subscriber; stop when velocity falls below threshold
- **Motivation:** Improves feel for fast camera rotations; matches trackpad physics users are accustomed to from other apps

---

### 📋 Smooth Zoom

Apply zoom deltas gradually over multiple frames instead of instantly, to reduce jerkiness on fast scroll gestures.

- **Config options:** Share `smoothingEnabled` or add separate `smoothZoom` boolean
- **Implementation approach:** Accumulate zoom target; lerp toward it each tick

---

### 📋 Advanced Trackpad Detection Threshold

Expose the internal `TRACKPAD_PRECISION_THRESHOLD` constant as a hidden/advanced config option for users with unusual trackpad drivers or mice.

- **Config:** `trackpadThreshold` (int 1–10, maps to 0.5–2.5 internal range)

---

## Ideas (Not Yet Designed)

### 💡 Three-Finger Gestures

Use three-finger swipe for an alternate action (e.g., snap to compass north, reset pitch to default).

- **Blocker:** Java AWT does not distinguish two-finger from three-finger scroll events natively. May require native macOS JNI bridge or accessibility API.

---

### 💡 Gesture Remapping

Let users assign any camera action to any gesture slot.

- **Complexity:** Medium. Requires a more flexible event routing layer.

---

### 💡 Windows / Linux Trackpad Support

Detect precision touchpad scroll events on Windows (Windows Precision Touchpad API) or Linux (libinput).

- **Complexity:** High. Requires platform-specific native code or JNI.
- **Note:** RuneLite is cross-platform; macOS-only limits reach.

---

## Rejected Features

### ❌ Custom Gesture Recording

Record arbitrary swipe patterns and map them to actions. Too complex for the scope of this plugin and duplicates OS-level gesture tooling.
