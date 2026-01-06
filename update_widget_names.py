#!/usr/bin/env python3
"""
Fetches the latest InterfaceID.java from RuneLite and generates
interfaces.txt and components.txt for the widget name lookup.
"""

import re
import urllib.request
import os

INTERFACE_ID_URL = "https://raw.githubusercontent.com/runelite/runelite/master/runelite-api/src/main/java/net/runelite/api/gameval/InterfaceID.java"
RESOURCES_DIR = os.path.join(os.path.dirname(__file__), "src/main/resources")


def fetch_interface_id():
    print(f"Fetching InterfaceID.java from {INTERFACE_ID_URL}...")
    with urllib.request.urlopen(INTERFACE_ID_URL) as response:
        return response.read().decode('utf-8')


def parse_interfaces(content):
    pattern = r'public static final int ([A-Z_0-9]+) = (\d+);'
    return re.findall(pattern, content)


def parse_components(content):
    class_pattern = r'public static final class (\w+)\s*\{([^}]+)\}'
    classes = re.findall(class_pattern, content, re.DOTALL)

    components = []
    for class_name, class_body in classes:
        comp_pattern = r'public static final int ([A-Z_0-9]+) = (0x[0-9a-f_]+);'
        comps = re.findall(comp_pattern, class_body, re.IGNORECASE)
        for comp_name, hex_val in comps:
            int_val = int(hex_val.replace('_', ''), 16)
            components.append((int_val, f"{class_name}.{comp_name}"))

    return components


def main():
    os.makedirs(RESOURCES_DIR, exist_ok=True)

    content = fetch_interface_id()

    interfaces = parse_interfaces(content)
    components = parse_components(content)

    interfaces_path = os.path.join(RESOURCES_DIR, "interfaces.txt")
    with open(interfaces_path, 'w') as f:
        for name, id_val in interfaces:
            f.write(f"{id_val},{name}\n")
    print(f"Wrote {len(interfaces)} interfaces to {interfaces_path}")

    components_path = os.path.join(RESOURCES_DIR, "components.txt")
    with open(components_path, 'w') as f:
        for int_val, name in components:
            f.write(f"{int_val},{name}\n")
    print(f"Wrote {len(components)} components to {components_path}")

    print("Done!")


if __name__ == "__main__":
    main()
