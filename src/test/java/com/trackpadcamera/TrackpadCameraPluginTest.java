package com.trackpadcamera;

import net.runelite.api.Client;
import net.runelite.api.GameState;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.Field;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TrackpadCameraPluginTest {

    private TrackpadCameraPlugin plugin;
    private Client client;
    private TrackpadCameraConfig config;

    @Before
    public void setUp() throws Exception {
        plugin = new TrackpadCameraPlugin();
        client = mock(Client.class);
        config = mock(TrackpadCameraConfig.class);

        inject(plugin, "client", client);
        inject(plugin, "config", config);

        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getCameraYaw()).thenReturn(512);
        when(client.getCameraPitch()).thenReturn(350);

        when(config.enableRotation()).thenReturn(true);
        when(config.enableTilt()).thenReturn(true);
        when(config.invertRotation()).thenReturn(false);
        when(config.invertTilt()).thenReturn(false);
        when(config.invertZoom()).thenReturn(false);
        when(config.rotationSensitivity()).thenReturn(5);
        when(config.tiltSensitivity()).thenReturn(5);
    }

    // --- Helpers ---

    private static void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static MouseWheelEvent trackpadSwipe(double delta, int modifiers, boolean ctrl) {
        MouseWheelEvent event = mock(MouseWheelEvent.class);
        when(event.getPreciseWheelRotation()).thenReturn(delta);
        when(event.getWheelRotation()).thenReturn(0);
        when(event.getModifiersEx()).thenReturn(modifiers);
        when(event.isControlDown()).thenReturn(ctrl);
        return event;
    }

    private static MouseWheelEvent trackpadSwipeWithSource(double delta, int modifiers, boolean ctrl) {
        MouseWheelEvent event = trackpadSwipe(delta, modifiers, ctrl);
        when(event.getSource()).thenReturn(mock(Component.class));
        return event;
    }

    private static MouseWheelEvent scrollWheelClick() {
        MouseWheelEvent event = mock(MouseWheelEvent.class);
        when(event.getPreciseWheelRotation()).thenReturn(3.0);
        when(event.getWheelRotation()).thenReturn(3);
        when(event.getModifiersEx()).thenReturn(0);
        return event;
    }

    // --- Trackpad detection ---

    @Test
    public void scrollWheelEventPassesThrough() {
        MouseWheelEvent event = scrollWheelClick();
        plugin.mouseWheelMoved(event);
        verify(event, never()).consume();
        verify(client, never()).setCameraYawTarget(anyInt());
        verify(client, never()).setCameraPitchTarget(anyInt());
    }

    // --- Gesture routing ---

    @Test
    public void verticalSwipePassesThrough() {
        // Plain two-finger vertical scroll passes through for OSRS zoom
        MouseWheelEvent event = trackpadSwipe(0.5, 0, false);
        plugin.mouseWheelMoved(event);
        verify(event, never()).consume();
        verify(client, never()).setCameraPitchTarget(anyInt());
    }

    @Test
    public void horizontalSwipeUpdatesYaw() {
        MouseWheelEvent event = trackpadSwipe(0.5, InputEvent.SHIFT_DOWN_MASK, false);
        plugin.mouseWheelMoved(event);
        verify(client).setCameraYawTarget(anyInt());
        verify(event).consume();
    }

    @Test
    public void ctrlScrollUpdatesPitch() {
        // Ctrl + two-finger scroll tilts camera pitch
        MouseWheelEvent event = trackpadSwipe(0.5, 0, true);
        plugin.mouseWheelMoved(event);
        verify(client).setCameraPitchTarget(anyInt());
        verify(event).consume();
    }

    // --- Game state guard ---

    @Test
    public void noActionWhenNotLoggedIn() {
        when(client.getGameState()).thenReturn(GameState.LOGIN_SCREEN);
        MouseWheelEvent event = trackpadSwipe(0.5, 0, true);
        plugin.mouseWheelMoved(event);
        verify(client, never()).setCameraPitchTarget(anyInt());
        verify(event, never()).consume();
    }

    // --- Config toggles ---

    @Test
    public void disabledTiltSkipsPitchUpdate() {
        when(config.enableTilt()).thenReturn(false);
        MouseWheelEvent event = trackpadSwipe(0.5, 0, true);
        plugin.mouseWheelMoved(event);
        verify(client, never()).setCameraPitchTarget(anyInt());
    }

    // --- Zoom inversion ---

    @Test
    public void invertZoomConsumesVerticalScroll() {
        when(config.invertZoom()).thenReturn(true);
        // Source component required for dispatchEvent
        MouseWheelEvent event = trackpadSwipeWithSource(0.5, 0, false);
        plugin.mouseWheelMoved(event);
        verify(event).consume();
    }

    @Test
    public void noInvertZoomPassesThroughVerticalScroll() {
        // invertZoom=false (default) — vertical scroll must not be consumed
        MouseWheelEvent event = trackpadSwipe(0.5, 0, false);
        plugin.mouseWheelMoved(event);
        verify(event, never()).consume();
    }

    @Test
    public void disabledRotationSkipsYawUpdate() {
        when(config.enableRotation()).thenReturn(false);
        MouseWheelEvent event = trackpadSwipe(0.5, InputEvent.SHIFT_DOWN_MASK, false);
        plugin.mouseWheelMoved(event);
        verify(client, never()).setCameraYawTarget(anyInt());
    }

    // --- Value clamping ---

    @Test
    public void pitchClampedToMax() {
        when(client.getCameraPitch()).thenReturn(510);
        MouseWheelEvent event = trackpadSwipe(5.0, 0, true);
        plugin.mouseWheelMoved(event);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(client).setCameraPitchTarget(captor.capture());
        assertTrue("Pitch must not exceed 512", captor.getValue() <= 512);
    }

    @Test
    public void pitchClampedToMin() {
        when(client.getCameraPitch()).thenReturn(130);
        MouseWheelEvent event = trackpadSwipe(-5.0, 0, true);
        plugin.mouseWheelMoved(event);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(client).setCameraPitchTarget(captor.capture());
        assertTrue("Pitch must not go below 128", captor.getValue() >= 128);
    }

    @Test
    public void yawWrapsWithinRange() {
        when(client.getCameraYaw()).thenReturn(2040);
        when(config.rotationSensitivity()).thenReturn(20);
        MouseWheelEvent event = trackpadSwipe(1.0, InputEvent.SHIFT_DOWN_MASK, false);
        plugin.mouseWheelMoved(event);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(client).setCameraYawTarget(captor.capture());
        int yaw = captor.getValue();
        assertTrue("Yaw must be in range [0, 2048)", yaw >= 0 && yaw < 2048);
    }

    @Test
    public void disabledTiltDoesNotConsumeVerticalScroll() {
        // Even with tilt disabled, plain vertical scroll must still pass through
        when(config.enableTilt()).thenReturn(false);
        MouseWheelEvent event = trackpadSwipe(0.5, 0, false);
        plugin.mouseWheelMoved(event);
        verify(event, never()).consume();
    }

    // --- Inversion ---

    @Test
    public void invertRotationReversesYawDirection() {
        when(config.invertRotation()).thenReturn(true);
        when(client.getCameraYaw()).thenReturn(512);
        MouseWheelEvent forward = trackpadSwipe(1.0, InputEvent.SHIFT_DOWN_MASK, false);
        plugin.mouseWheelMoved(forward);

        when(config.invertRotation()).thenReturn(false);
        when(client.getCameraYaw()).thenReturn(512);
        plugin = new TrackpadCameraPlugin();
        try { inject(plugin, "client", client); inject(plugin, "config", config); } catch (Exception e) { throw new RuntimeException(e); }
        MouseWheelEvent normal = trackpadSwipe(1.0, InputEvent.SHIFT_DOWN_MASK, false);
        plugin.mouseWheelMoved(normal);

        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(client, org.mockito.Mockito.times(2)).setCameraYawTarget(captor.capture());
        int invertedYaw = captor.getAllValues().get(0);
        int normalYaw = captor.getAllValues().get(1);
        assertTrue("Inverted yaw should differ from normal yaw", invertedYaw != normalYaw);
    }

    @Test
    public void invertTiltReversesPitchDirection() {
        when(config.invertTilt()).thenReturn(true);
        when(client.getCameraPitch()).thenReturn(350);
        MouseWheelEvent invertedEvent = trackpadSwipe(1.0, 0, true);
        plugin.mouseWheelMoved(invertedEvent);
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        verify(client).setCameraPitchTarget(captor.capture());
        int invertedPitch = captor.getValue();
        assertTrue("Inverted tilt should decrease pitch for positive delta", invertedPitch < 350);
    }
}
