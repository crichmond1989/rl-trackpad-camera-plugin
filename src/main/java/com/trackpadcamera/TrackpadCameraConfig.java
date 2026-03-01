package com.trackpadcamera;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("trackpadcamera")
public interface TrackpadCameraConfig extends Config {

    @ConfigSection(
        name = "Zoom",
        description = "Zoom is handled by OSRS natively",
        position = 0,
        closedByDefault = false
    )
    String ZOOM_SECTION = "zoomSection";

    @ConfigSection(
        name = "Rotation",
        description = "Two-finger swipe left/right",
        position = 1,
        closedByDefault = false
    )
    String ROTATION_SECTION = "rotationSection";

    @ConfigSection(
        name = "Tilt",
        description = "Ctrl + two-finger scroll up/down",
        position = 2,
        closedByDefault = false
    )
    String TILT_SECTION = "tiltSection";

    // ---- Zoom (informational) ----

    @ConfigItem(
        keyName = "zoomNote",
        name = "Enable \"Use Scroll Wheel to Zoom\" in OSRS Settings > Controls > Camera",
        description = "Vertical scroll is passed through to OSRS. This plugin does not control zoom.",
        section = TrackpadCameraConfig.ZOOM_SECTION,
        position = 0
    )
    default boolean zoomNote() {
        return false;
    }

    @ConfigItem(
        keyName = "invertZoom",
        name = "Invert Zoom",
        description = "Reverses the scroll direction for zooming — scroll up zooms out instead of in",
        section = TrackpadCameraConfig.ZOOM_SECTION,
        position = 1
    )
    default boolean invertZoom() {
        return false;
    }

    // ---- Rotation ----

    @ConfigItem(
        keyName = "enableRotation",
        name = "Enable Rotation",
        description = "Two-finger horizontal swipe to rotate camera yaw",
        section = TrackpadCameraConfig.ROTATION_SECTION,
        position = 0
    )
    default boolean enableRotation() {
        return true;
    }

    @ConfigItem(
        keyName = "invertRotation",
        name = "Invert",
        description = "Reverse the rotation direction",
        section = TrackpadCameraConfig.ROTATION_SECTION,
        position = 1
    )
    default boolean invertRotation() {
        return false;
    }

    @Range(min = 1, max = 20)
    @ConfigItem(
        keyName = "rotationSensitivity",
        name = "Sensitivity",
        description = "How fast the camera rotates with horizontal swipes",
        section = TrackpadCameraConfig.ROTATION_SECTION,
        position = 2
    )
    default int rotationSensitivity() {
        return 5;
    }

    // ---- Tilt ----

    @ConfigItem(
        keyName = "enableTilt",
        name = "Enable Tilt",
        description = "Ctrl + two-finger scroll up/down to tilt camera pitch",
        section = TrackpadCameraConfig.TILT_SECTION,
        position = 0
    )
    default boolean enableTilt() {
        return true;
    }

    @ConfigItem(
        keyName = "invertTilt",
        name = "Invert",
        description = "Reverse the tilt direction",
        section = TrackpadCameraConfig.TILT_SECTION,
        position = 1
    )
    default boolean invertTilt() {
        return false;
    }

    @Range(min = 1, max = 20)
    @ConfigItem(
        keyName = "tiltSensitivity",
        name = "Sensitivity",
        description = "How fast the camera tilts with ctrl + two-finger scroll",
        section = TrackpadCameraConfig.TILT_SECTION,
        position = 2
    )
    default int tiltSensitivity() {
        return 5;
    }
}
