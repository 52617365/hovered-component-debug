# Hovered Component Debug

Thanks to Doga for providing his version so I could GPT it to RuneLite.

A RuneLite plugin that displays widget/component information when hovering over UI elements in Old School RuneScape. Useful for developers and plugin creators who need to identify widget IDs, child indices, and other component properties.

## Features

- Shows widget group ID and child ID in `group:child` format
- Displays dynamic child index when applicable (e.g., `162:56[3]`)
- Shows text content, name, and actions of widgets
- Displays sprite ID and item ID when present
- Color-coded overlays to distinguish overlapping widgets
- Automatically filters out very large widgets (>500px)

## Installation

### Building from source

1. Clone this repository
2. Build the plugin:
   ```bash
   ./gradlew build
   ```
3. The compiled JAR will be in `build/libs/`

### Using with RuneLite

Copy the built JAR to your RuneLite external plugins folder, or load it via the RuneLite developer tools.

## Usage

1. Enable the plugin in RuneLite's plugin configuration
2. Hover over any UI element in the game
3. Widget information will be displayed in the top-left corner with matching colored borders around the hovered widgets

## Widget Info Display

The overlay shows information in this format:
```
group:child[index] [T: text] [N: name] [A: action] [S: spriteId] [I: itemId] [Type: type]
```

- **group:child** - The widget ID split into interface group and child component
- **[index]** - The dynamic child index (only shown for dynamic children)
- **[T: ...]** - Text content of the widget
- **[N: ...]** - Name property of the widget
- **[A: ...]** - First available action
- **[S: ...]** - Sprite ID if the widget displays a sprite
- **[I: ...]** - Item ID if the widget displays an item
- **[Type: ...]** - Widget type number

## Requirements

- Java 11+
- RuneLite client

## License

This project is open source. Feel free to use and modify as needed.
