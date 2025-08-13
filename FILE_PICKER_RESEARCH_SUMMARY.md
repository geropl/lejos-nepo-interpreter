# leJOS NXT File System and GUI Research Summary

## Overview
This document summarizes the research findings on leJOS NXT API capabilities for file system access and GUI components, specifically for implementing a file picker that runs directly on the NXT brick.

## File System Access Capabilities

### File Class (java.io.File)
The leJOS NXT provides a `java.io.File` class with the following key capabilities:

#### Core File Operations
- **File listing**: `File.listFiles()` - Returns array of all files in flash memory
- **File existence**: `file.exists()` - Check if file exists
- **File properties**: `file.getName()`, `file.length()` - Get name and size
- **File creation**: `file.createNewFile()` - Create new file
- **File deletion**: `file.delete()` - Delete file

#### Key Limitations
- **No directory support**: NXT flash file system is flat (no subdirectories)
- **Limited concurrent access**: Only one file can be open at a time
- **Flash memory constraints**: Limited storage space and file count (MAX_FILES = 30)

#### File System Structure
```java
// Get all files from flash memory
File[] allFiles = File.listFiles();

// File properties
String name = file.getName();        // e.g., "program.xml"
long size = file.length();           // Size in bytes
boolean exists = file.exists();      // File existence check
```

### Directory Browsing
Since the NXT uses a flat file system:
- No subdirectories or folder navigation needed
- All files are in the root of flash memory
- File listing returns all files at once
- Filtering must be done programmatically by extension or name patterns

## GUI Components for Menu Selection

### TextMenu Class (lejos.util.TextMenu)
The primary GUI component for creating selectable menus:

#### Key Features
- **Scrollable lists**: Navigate with LEFT/RIGHT buttons
- **Selection**: ENTER button to select item
- **Customizable display**: Set title, position, and item list
- **Automatic wrapping**: Menu wraps around at top/bottom
- **Timeout support**: Optional timeout for automatic exit

#### Constructor Options
```java
// Basic menu
TextMenu menu = new TextMenu(String[] items);

// With custom position
TextMenu menu = new TextMenu(String[] items, int topRow);

// With title and position
TextMenu menu = new TextMenu(String[] items, int topRow, String title);
```

#### Selection Methods
```java
int selection = menu.select();                    // No timeout
int selection = menu.select(int selectedIndex);   // Start at specific index
int selection = menu.select(int selectedIndex, int timeout); // With timeout
```

### LCD Display (lejos.nxt.LCD)
Low-level display control for custom interfaces:

#### Display Properties
- **Resolution**: 100x64 pixels monochrome
- **Text display**: 16 characters × 8 lines
- **Character size**: 6×8 pixels per character
- **Graphics support**: Pixel-level drawing and BitBlt operations

#### Key Methods
```java
LCD.clear();                                    // Clear display
LCD.drawString(String text, int x, int y);     // Draw text at position
LCD.refresh();                                  // Update display
LCD.setPixel(int x, int y, int color);         // Set individual pixel
```

### Button Input (lejos.nxt.Button)
Button handling for user interaction:

#### Available Buttons
- `Button.ENTER` - Center orange button
- `Button.LEFT` - Left gray button  
- `Button.RIGHT` - Right gray button
- `Button.ESCAPE` - Dark gray button

#### Input Methods
```java
Button.waitForAnyPress();                       // Wait for any button
int buttonId = Button.waitForAnyPress(timeout); // With timeout
boolean isPressed = Button.ENTER.isDown();      // Check button state
Button.ENTER.waitForPress();                    // Wait for specific button
```

## File Picker Implementation Strategies

### Basic File Picker
```java
public static String selectFile() {
    // 1. Get all files from flash memory
    File[] allFiles = File.listFiles();
    
    // 2. Filter for desired file types (e.g., .xml)
    String[] xmlFiles = filterXmlFiles(allFiles);
    
    // 3. Create menu with file list + cancel option
    String[] menuItems = new String[xmlFiles.length + 1];
    System.arraycopy(xmlFiles, 0, menuItems, 0, xmlFiles.length);
    menuItems[xmlFiles.length] = "< Cancel >";
    
    // 4. Display menu and get selection
    TextMenu menu = new TextMenu(menuItems, 1, "Select File");
    int selection = menu.select();
    
    // 5. Return selected filename or null if cancelled
    return (selection >= 0 && selection < xmlFiles.length) ? 
           xmlFiles[selection] : null;
}
```

### Advanced Features
1. **File Information Display**: Show file size, date, type
2. **Pagination**: Handle large file lists with multiple pages
3. **Sorting**: Sort files by name, size, or date
4. **File Operations**: Delete, rename, or copy files
5. **Preview**: Show first few lines of text files
6. **Multiple Extensions**: Support various file types

### Integration with NEPO Interpreter
```java
public static void main(String[] args) {
    String xmlFile = null;
    
    // Check command line arguments first
    if (args.length > 0) {
        xmlFile = args[0];
    } else {
        // Use file picker for interactive selection
        xmlFile = FilePicker.selectFile();
    }
    
    if (xmlFile != null) {
        // Load and execute the selected program
        executeNepoProgram(xmlFile);
    }
}
```

## Practical Considerations

### Memory Constraints
- **Flash memory**: Limited space for files (~256KB total)
- **RAM usage**: Keep file lists and menu items small
- **File count**: Maximum 30 files in flash memory

### User Experience
- **Clear navigation**: Use consistent button mappings
- **Visual feedback**: Show current selection clearly
- **Error handling**: Graceful handling of empty directories or errors
- **Cancellation**: Always provide escape/cancel option

### Performance
- **File listing**: `File.listFiles()` is relatively fast
- **Menu rendering**: TextMenu handles display efficiently
- **Button response**: Immediate feedback for button presses

## Code Examples Provided

1. **FilePicker.java**: Basic file picker implementation
2. **AdvancedFilePicker.java**: Enhanced version with pagination and file info
3. **NepoInterpreterWithFilePicker.java**: Integration example with NEPO interpreter

## Conclusion

The leJOS NXT API provides sufficient capabilities for implementing a functional file picker:

### Strengths
- Simple file system access via `File.listFiles()`
- Ready-to-use `TextMenu` component for selection interfaces
- Complete button and display control
- Integration-friendly design

### Limitations
- Flat file system (no directories)
- Limited concurrent file access
- Memory constraints on file count and size
- Basic GUI components (no advanced widgets)

### Recommendation
The file picker approach is highly viable for making the NEPO interpreter more user-friendly. The `TextMenu` class provides an excellent foundation for file selection, and the file system API is sufficient for basic file management operations. This would significantly improve the standalone usability of the NXT-based NEPO interpreter system.
