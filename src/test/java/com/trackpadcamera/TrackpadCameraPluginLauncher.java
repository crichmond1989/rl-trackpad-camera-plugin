package com.trackpadcamera;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TrackpadCameraPluginLauncher {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(TrackpadCameraPlugin.class);
        RuneLite.main(args);
    }
}
