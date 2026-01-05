package com.example.hoveredcomponentdebug;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
    name = "Hovered Component Debug",
    description = "Shows widget/component information when hovering over UI elements",
    tags = {"debug", "widget", "interface", "component", "developer"}
)
public class HoveredComponentDebugPlugin extends Plugin {

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private HoveredComponentDebugOverlay overlay;

    @Override
    protected void startUp() {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() {
        overlayManager.remove(overlay);
    }
}
