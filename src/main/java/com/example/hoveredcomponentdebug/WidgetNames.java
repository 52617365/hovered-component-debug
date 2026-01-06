package com.example.hoveredcomponentdebug;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Static lookup maps for InterfaceID and ComponentID names.
 * Loads from text files generated from RuneLite's InterfaceID.java gameval constants.
 */
public final class WidgetNames {

    private static final Map<Integer, String> INTERFACE_NAMES = new HashMap<>();
    private static final Map<Integer, String> COMPONENT_NAMES = new HashMap<>();

    static {
        loadMappings();
    }

    private static void loadMappings() {
        // Load interfaces
        try (InputStream is = WidgetNames.class.getResourceAsStream("/interfaces.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    INTERFACE_NAMES.put(Integer.parseInt(parts[0]), parts[1]);
                }
            }
        } catch (Exception e) {
            // Ignore
        }

        // Load components
        try (InputStream is = WidgetNames.class.getResourceAsStream("/components.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", 2);
                if (parts.length == 2) {
                    COMPONENT_NAMES.put(Integer.parseInt(parts[0]), parts[1]);
                }
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    public static String getInterfaceName(int groupId) {
        return INTERFACE_NAMES.get(groupId);
    }

    public static String getComponentName(int componentId) {
        return COMPONENT_NAMES.get(componentId);
    }
}
