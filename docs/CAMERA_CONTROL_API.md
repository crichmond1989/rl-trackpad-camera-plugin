# Camera Control API

## Summary

How the plugin manipulates the OSRS camera using the RuneLite Client API, and the constraints imposed by the game engine's camera model.

## RuneLite Camera API

The RuneLite `Client` interface exposes camera control through these methods:

| Method | Description | Range |
|--------|------------|-------|
| `client.getCameraYaw()` | Get current horizontal camera angle | 0–2047 |
| `client.setCameraYawTarget(int)` | Set target yaw (engine lerps to it) | 0–2047 |
| `client.getCameraPitch()` | Get current vertical tilt angle | 128–512 |
| `client.setCameraPitchTarget(int)` | Set target pitch | 128–512 |
| `client.get3dZoom()` | Get current zoom level | ~128–1500 |
| `client.setZoom(int)` | Set zoom directly | ~128–1500 |

### Yaw

Yaw represents horizontal camera rotation. The value wraps around (0 and 2047 are adjacent). When setting yaw, the engine smoothly interpolates to the target value on the next tick.

```java
int newYaw = (currentYaw + delta) % 2048;
if (newYaw < 0) newYaw += 2048;
client.setCameraYawTarget(newYaw);
```

### Pitch

Pitch controls vertical tilt. Lower values look more overhead; higher values look more horizontal toward the horizon.

- Minimum (128): Near top-down view
- Maximum (512): Near-horizontal view
- Default: ~384 (angled view)

Values are clamped to `[128, 512]` to avoid engine issues outside this range.

### Zoom

Zoom is the 3D render distance / field-of-view scale. Smaller values zoom in; larger values zoom out.

- Minimum (~128): Very close up
- Maximum (~1500): Very zoomed out
- Default: ~600

## Sensitivity Scaling

Raw trackpad deltas are very small (fractional rotations of 0.01–0.5 per event). They must be multiplied by a sensitivity constant before being applied to the coarser camera angle ranges.

Current internal constants (not user-exposed):

```java
private static final double ROTATE_SENSITIVITY = 3.0;
private static final double ZOOM_SENSITIVITY   = 80.0;
```

User-facing sensitivity config values (1–20) further multiply these:

```java
rotateCameraYaw(preciseRotation * config.rotationSensitivity());
```

So the effective delta applied to yaw is approximately:
```
yaw_delta = preciseRotation × configSensitivity × ROTATE_SENSITIVITY
```

## Thread Safety

RuneLite event callbacks (`mouseWheelMoved`) are called on the AWT Event Dispatch Thread (EDT). The RuneLite Client API camera setters are generally safe to call from the EDT in the context of a `MouseWheelListener`. No additional synchronization is needed for this plugin's current usage.

## Known Constraints

- `setCameraYawTarget` and `setCameraPitchTarget` set a *target* — the engine may lag behind by up to one game tick (600 ms) when moving. This creates a natural smoothing effect.
- `setZoom` takes effect immediately without smoothing. Very large zoom deltas per event may feel jerky; sensitivity should be kept conservative.
- The RuneLite API may change the camera methods across client versions. The `runelite.version` in [gradle.properties](../gradle.properties) should be pinned to a tested version.

## Future Considerations

- Momentum / inertia: accumulate velocity from swipe events and decay over time for a more natural feel.
- Smooth zoom: accumulate zoom deltas and apply gradually over several frames rather than immediately.
