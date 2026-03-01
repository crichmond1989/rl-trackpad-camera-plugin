# Gesture Detection

## Summary

How the plugin distinguishes MacBook trackpad gestures from standard mouse scroll wheel events, and how it maps those gestures to camera actions.

## Motivation

Java AWT does not natively expose a "trackpad gesture" event type. All trackpad input (swipes, ctrl+scroll) arrives as `MouseWheelEvent` — the same type used by a physical scroll wheel. We must infer the input source from event properties.

## Trackpad vs Scroll Wheel Detection

macOS translates trackpad gestures into `MouseWheelEvent` with these characteristics:

| Property | Trackpad | Scroll Wheel |
|----------|----------|-------------|
| `getPreciseWheelRotation()` | Fractional (e.g., `0.12`) | Matches `getWheelRotation()` |
| `getWheelRotation()` | Small integer or 0 | Integer (1, 3, etc.) |
| Magnitude per event | Very small | Larger step increments |

**Detection logic** (see [TrackpadCameraPlugin.java:70](../src/main/java/com/trackpadcamera/TrackpadCameraPlugin.java#L70)):

```java
private boolean isTrackpadEvent(MouseWheelEvent e) {
    double precise = e.getPreciseWheelRotation();
    double coarse  = e.getWheelRotation();
    return Math.abs(precise - coarse) > 0.01
        || Math.abs(precise) < TRACKPAD_PRECISION_THRESHOLD;
}
```

`TRACKPAD_PRECISION_THRESHOLD = 1.5` — events with magnitude below this are assumed to come from a trackpad.

## Gesture-to-Event Mapping

macOS encodes different gesture axes using AWT event modifiers:

| Gesture | AWT Encoding | Modifier Check |
|---------|-------------|----------------|
| Two-finger swipe left/right | Scroll + `SHIFT` | `e.getModifiersEx() & SHIFT_DOWN_MASK` |
| Ctrl + two-finger scroll up/down | Scroll + `CTRL` | `e.isControlDown()` |
| Two-finger swipe up/down | Vertical scroll | No modifier |

### Horizontal Swipe (Yaw)

macOS remaps horizontal scroll as `SHIFT + vertical scroll` at the OS level. The `SHIFT_DOWN_MASK` flag distinguishes it.

### Ctrl + Two-Finger Scroll (Pitch)

Holding Ctrl while scrolling two fingers up or down sends a `CTRL + scroll` event. The plugin maps this to camera pitch (tilt). Note: this overlaps with the system accessibility zoom shortcut — disable "Use scroll gesture with modifier keys to zoom" in System Settings → Accessibility → Zoom if there is interference.

### Vertical Swipe (Zoom — OSRS native)

Plain vertical scroll with no modifier. The plugin passes this through unmodified to OSRS, which handles zoom natively via the "Use Scroll Wheel to Zoom" setting. When zoom invert is enabled, the plugin intercepts these events and applies an inverted zoom via the OSRS `CAMERA_DO_ZOOM` script.

## Event Consumption

Trackpad events that are handled by the plugin are consumed (`e.consume()`) to prevent RuneLite from also acting on them (e.g., triggering the default zoom behavior on a vertical scroll).

Non-trackpad events (scroll wheel) are returned unmodified so standard scroll-to-zoom continues to work.

## Known Limitations

- The `SHIFT` horizontal scroll encoding is OS-level; some apps or drivers may not use this convention.
- `CTRL + scroll` for tilt can conflict with system accessibility zoom.
- Very fast swipes may produce events that exceed `TRACKPAD_PRECISION_THRESHOLD`, potentially being misidentified as scroll wheel events.

## Implementation Notes

- Detection threshold (`TRACKPAD_PRECISION_THRESHOLD`) is a compile-time constant for now. A future improvement could expose it as a hidden/advanced config option.
- On non-macOS systems, horizontal scroll via `SHIFT` is uncommon; the plugin will silently do nothing useful there (by design — it targets macOS only).
