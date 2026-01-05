package com.example.hoveredcomponentdebug;

import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

import javax.inject.Inject;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HoveredComponentDebugOverlay extends Overlay {

    private static final Color[] LAYER_COLORS = new Color[]{
        Color.RED, Color.GREEN, Color.CYAN,
        Color.ORANGE, Color.YELLOW, Color.MAGENTA,
        Color.PINK, Color.WHITE, Color.LIGHT_GRAY,
        Color.BLUE, Color.BLACK, Color.DARK_GRAY
    };

    // Blacklisted interface groups (e.g., deadman overlay)
    private static final int[] BLACKLISTED_GROUPS = {
        // Add any interface groups you want to skip
    };

    private final Client client;

    @Inject
    public HoveredComponentDebugOverlay(Client client) {
        this.client = client;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ALWAYS_ON_TOP);
        setPriority(OverlayPriority.HIGHEST);
    }

    @Override
    public Dimension render(Graphics2D g) {
        int mx = client.getMouseCanvasPosition().getX();
        int my = client.getMouseCanvasPosition().getY();

        int layer = 1;
        List<WidgetInfo> hoveredWidgets = new ArrayList<>();

        Widget[] widgetRoots = client.getWidgetRoots();
        if (widgetRoots == null) {
            return null;
        }

        for (Widget widget : widgetRoots) {
            if (widget == null) {
                continue;
            }

            int groupId = widget.getId() >> 16;
            if (isBlacklisted(groupId)) {
                continue;
            }

            layer = renderWidgetRecursively(g, widget, mx, my, layer);
        }

        return null;
    }

    private int renderWidgetRecursively(Graphics2D g, Widget widget, int mx, int my, int layer) {
        if (widget == null) {
            return layer;
        }

        // Render this widget
        layer = renderWidget(g, widget, mx, my, layer);

        // Check nested children
        Widget[] nestedChildren = widget.getNestedChildren();
        if (nestedChildren != null) {
            for (Widget nested : nestedChildren) {
                layer = renderWidgetRecursively(g, nested, mx, my, layer);
            }
        }

        // Check dynamic children
        Widget[] dynamicChildren = widget.getDynamicChildren();
        if (dynamicChildren != null) {
            for (Widget dynamic : dynamicChildren) {
                layer = renderWidgetRecursively(g, dynamic, mx, my, layer);
            }
        }

        // Check static children
        Widget[] staticChildren = widget.getStaticChildren();
        if (staticChildren != null) {
            for (Widget staticChild : staticChildren) {
                layer = renderWidgetRecursively(g, staticChild, mx, my, layer);
            }
        }

        return layer;
    }

    private int renderWidget(Graphics2D g, Widget widget, int mx, int my, int layer) {
        if (widget == null || widget.isHidden()) {
            return layer;
        }

        Rectangle bounds = widget.getBounds();
        if (bounds == null) {
            return layer;
        }

        // Skip very large widgets and check if mouse is inside
        if (bounds.width < 500 && bounds.height < 500 && bounds.contains(mx, my)) {
            Color color = LAYER_COLORS[layer % LAYER_COLORS.length];

            // Draw bordered rectangle around widget
            drawBorderedRectangle(g, bounds, color);

            // Draw widget info text
            String info = formatWidgetInfo(widget);
            drawBoldedString(g, info, 20, layer * 18 + 15, color);

            layer++;
        }

        return layer;
    }

    private String formatWidgetInfo(Widget widget) {
        StringBuilder builder = new StringBuilder();

        // Widget ID in format group:child (or group.child)
        int id = widget.getId();
        int groupId = id >> 16;
        int childId = id & 0xFFFF;
        builder.append(groupId).append(":").append(childId);

        // Add index if it's a dynamic child
        int index = widget.getIndex();
        if (index != -1) {
            builder.append("[").append(index).append("]");
        }

        // Text content
        String text = widget.getText();
        if (text != null && !text.trim().isEmpty()) {
            String trimmed = text.trim();
            if (trimmed.length() > 30) {
                trimmed = trimmed.substring(0, 30) + "...";
            }
            builder.append(" [T: ").append(trimmed).append("]");
        }

        // Name/action
        String name = widget.getName();
        if (name != null && !name.trim().isEmpty()) {
            builder.append(" [N: ").append(name.trim()).append("]");
        }

        // Tooltip
        String[] actions = widget.getActions();
        if (actions != null) {
            for (String action : actions) {
                if (action != null && !action.trim().isEmpty()) {
                    builder.append(" [A: ").append(action.trim()).append("]");
                    break; // Just show first action
                }
            }
        }

        // Sprite/Material ID
        int spriteId = widget.getSpriteId();
        if (spriteId != -1) {
            builder.append(" [S: ").append(spriteId).append("]");
        }

        // Item ID if applicable
        int itemId = widget.getItemId();
        if (itemId != -1) {
            builder.append(" [I: ").append(itemId).append("]");
        }

        // Type
        int type = widget.getType();
        builder.append(" [Type: ").append(type).append("]");

        return builder.toString();
    }

    private void drawBorderedRectangle(Graphics2D g, Rectangle bounds, Color color) {
        // Draw black border
        g.setColor(Color.BLACK);
        g.drawRect(bounds.x - 1, bounds.y - 1, bounds.width + 2, bounds.height + 2);

        // Draw colored rectangle
        g.setColor(color);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    private void drawBoldedString(Graphics2D g, String text, int x, int y, Color color) {
        // Draw black outline for readability
        g.setColor(Color.BLACK);
        g.drawString(text, x - 1, y);
        g.drawString(text, x + 1, y);
        g.drawString(text, x, y - 1);
        g.drawString(text, x, y + 1);

        // Draw colored text
        g.setColor(color);
        g.drawString(text, x, y);
    }

    private boolean isBlacklisted(int groupId) {
        for (int blacklisted : BLACKLISTED_GROUPS) {
            if (blacklisted == groupId) {
                return true;
            }
        }
        return false;
    }
}
