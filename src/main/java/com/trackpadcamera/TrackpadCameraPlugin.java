package com.trackpadcamera;

import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.input.MouseWheelListener;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

@PluginDescriptor(
    name = "Trackpad Camera",
    description = "Mac trackpad camera controls: swipe left/right to rotate, ctrl+scroll to tilt. Zoom via OSRS scroll setting.",
    tags = {"camera", "trackpad", "mac", "gesture"}
)
public class TrackpadCameraPlugin extends Plugin implements MouseWheelListener, MouseListener {

    @Inject
    private Client client;

    @Inject
    private MouseManager mouseManager;

    @Inject
    private TrackpadCameraConfig config;

    // Sensitivity multiplier applied on top of config sensitivity
    private static final double ROTATE_SENSITIVITY = 3.0;

    // Guards against re-processing the inverted scroll event we dispatch for invertZoom.
    private boolean dispatchingInvertedScroll = false;

    @Override
    protected void startUp() {
        mouseManager.registerMouseWheelListener(this);
        mouseManager.registerMouseListener(this);
    }

    @Override
    protected void shutDown() {
        mouseManager.unregisterMouseWheelListener(this);
        mouseManager.unregisterMouseListener(this);
    }

    @Provides
    TrackpadCameraConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(TrackpadCameraConfig.class);
    }

    @Override
    public MouseWheelEvent mouseWheelMoved(MouseWheelEvent e) {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return e;
        }

        // Skip re-processing the inverted event we dispatched for invertZoom
        if (dispatchingInvertedScroll) {
            dispatchingInvertedScroll = false;
            return e;
        }

        double preciseRotation = e.getPreciseWheelRotation();
        boolean isHorizontal = (e.getModifiersEx() & java.awt.event.InputEvent.SHIFT_DOWN_MASK) != 0;

        if (isHorizontal) {
            // Two-finger horizontal swipe → rotate camera yaw
            if (config.enableRotation()) {
                double delta = preciseRotation * config.rotationSensitivity()
                    * (config.invertRotation() ? -1 : 1);
                rotateCameraYaw(delta);
                e.consume();
            }
        } else if (e.isControlDown()) {
            // Ctrl + two-finger scroll up/down → tilt camera pitch
            if (config.enableTilt()) {
                double delta = preciseRotation * config.tiltSensitivity()
                    * (config.invertTilt() ? -1 : 1);
                rotateCameraPitch(delta);
                e.consume();
            }
        } else if (config.invertZoom()) {
            // Invert scroll direction by consuming the original and re-dispatching with negated values
            e.consume();
            dispatchingInvertedScroll = true;
            e.getComponent().dispatchEvent(new MouseWheelEvent(
                e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(),
                e.getX(), e.getY(), e.getXOnScreen(), e.getYOnScreen(),
                e.getClickCount(), e.isPopupTrigger(),
                e.getScrollType(), e.getScrollAmount(),
                -e.getWheelRotation(), -e.getPreciseWheelRotation()
            ));
        }
        // Plain vertical scroll with invertZoom=false passes through for OSRS native zoom

        return e;
    }

    /**
     * Rotates the camera horizontally (yaw).
     * Camera yaw ranges 0-2047 in OSRS.
     */
    private void rotateCameraYaw(double delta) {
        int currentYaw = client.getCameraYaw();
        int newYaw = (currentYaw + (int)(delta * ROTATE_SENSITIVITY)) % 2048;
        if (newYaw < 0) newYaw += 2048;
        client.setCameraYawTarget(newYaw);
    }

    /**
     * Tilts the camera vertically (pitch).
     * Pitch in OSRS ranges roughly 128-512 (lower = more overhead, higher = more horizontal).
     */
    private void rotateCameraPitch(double delta) {
        int currentPitch = client.getCameraPitch();
        int newPitch = currentPitch + (int)(delta * ROTATE_SENSITIVITY);
        newPitch = Math.max(128, Math.min(512, newPitch));
        client.setCameraPitchTarget(newPitch);
    }

    // ---- MouseListener required methods (pass-through) ----

    @Override
    public MouseEvent mouseClicked(MouseEvent e) { return e; }

    @Override
    public MouseEvent mousePressed(MouseEvent e) { return e; }

    @Override
    public MouseEvent mouseReleased(MouseEvent e) { return e; }

    @Override
    public MouseEvent mouseEntered(MouseEvent e) { return e; }

    @Override
    public MouseEvent mouseExited(MouseEvent e) { return e; }

    @Override
    public MouseEvent mouseDragged(MouseEvent e) { return e; }

    @Override
    public MouseEvent mouseMoved(MouseEvent e) { return e; }
}
