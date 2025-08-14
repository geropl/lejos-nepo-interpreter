# Dynamic File Selection for NEPO NXT Interpreter

This document describes the dynamic file selection capabilities added to the NEPO NXT interpreter, allowing users to browse and select XML programs directly on the NXT device.

## Overview

The dynamic file selection system provides two file picker implementations:

1. **Simple File Picker** (`FilePicker.java`) - Basic file selection using leJOS TextMenu
2. **Advanced File Picker** (`AdvancedFilePicker.java`) - Enhanced interface with file information and navigation

## Features

### Simple File Picker
- Clean, menu-based interface using leJOS TextMenu
- File filtering by extension (.xml)
- File size display
- Automatic sorting
- Cancel option

### Advanced File Picker
- Interactive navigation with arrow keys
- File preview and detailed information
- Pagination for large file lists
- Directory support (future enhancement)
- File size formatting (B, K, M)
- Multiple selection modes

## Build Options

The dynamic file selection system supports both production and debug builds:

### Production Build
```bash
./build_dynamic.sh
```
- Optimized for performance and memory usage
- Numeric exception codes in crash logs
- Smaller binary size

### Debug Build
```bash
./build_dynamic.sh debug
```
- Full debugging support with remote console
- Proper exception class names and line numbers
- Debug info files (`.nxd`) for post-mortem analysis
- Real-time debugging via PC console

## Usage

### Basic Usage
```java
// Select any XML file
String filename = FilePicker.pickXMLFile();

// Select with custom title
String filename = FilePicker.pickFile(".xml", "Select Program");

// Advanced picker with file info
String filename = FilePicker.pickFileAdvanced(".xml", "Select Program", true);
```

### Advanced Usage
```java
// Use advanced picker
String filename = AdvancedFilePicker.selectXmlFile();

// Custom extensions
String[] extensions = {".xml", ".txt"};
String filename = AdvancedFilePicker.selectFileAdvanced("Select File", extensions);
```

## Main Programs

### 1. NepoInterpreterMain (Enhanced)
The original interpreter now includes dynamic file selection:
- Command line argument support (backward compatible)
- Dynamic file picker when no argument provided
- Restart option after program completion

### 2. DynamicNepoRunner (New)
Comprehensive dynamic program runner:
- Welcome screen
- Choice between simple and advanced picker
- Program execution with error handling
- Continuous operation (run multiple programs)
- Graceful exit handling

## File Structure

```
src/
├── FilePicker.java              # Simple file picker
├── AdvancedFilePicker.java      # Advanced file picker (root level)
├── NepoInterpreterMain.java     # Enhanced main interpreter
├── DynamicNepoRunner.java       # Dynamic runner (root level)
├── SimpleXMLParser.java         # XML parsing
├── NepoBlockExecutor.java       # Block execution
├── RobotConfiguration.java      # Hardware configuration
└── ConfigurationBlockExecutor.java # Configuration blocks

sample_programs/
├── simple_move.xml              # Basic motor movement
├── sound_test.xml               # Sound/tone generation
└── sensor_demo.xml              # Touch sensor demo
```

## Building and Deployment

### Build Process

**Production Build:**
```bash
./build_dynamic.sh
```

**Debug Build:**
```bash
./build_dynamic.sh debug
```

This creates:
- `NepoSimple.nxj` - Basic interpreter with file picker
- `NepoDynamic.nxj` - Full dynamic runner
- `NepoSimple.nxd` - Debug info (debug build only)
- `NepoDynamic.nxd` - Debug info (debug build only)

### Upload to NXT

**Production Deployment:**
```bash
# Upload programs
nxjupload NepoSimple.nxj
nxjupload NepoDynamic.nxj

# Upload sample programs
nxjupload sample_programs/*.xml
```

**Debug Deployment:**
```bash
# Upload programs
nxjupload NepoSimple.nxj
nxjupload NepoDynamic.nxj

# Upload sample programs
nxjupload sample_programs/*.xml

# Start remote console for debugging
nxjconsole -di NepoSimple.nxd    # For NepoSimple debugging
nxjconsole -di NepoDynamic.nxd   # For NepoDynamic debugging
```

### Debug Mode Benefits

When using debug builds:
- **Real-time debugging** - See program output on PC console
- **Proper exception names** - Instead of "class 16", see "NullPointerException"
- **Line numbers** - Exact source code locations for errors
- **Method names** - Full stack traces with readable method names
- **Remote monitoring** - Monitor program execution from PC

## User Interface

### Simple Picker Interface
```
Select Program
--------------
simple_move.xml 245B
sound_test.xml  312B
sensor_demo.xml 456B
< Cancel >
```

### Advanced Picker Interface
```
Select NEPO Program    (1/2)
-------------------------
>simple_move.xml      245B
 sound_test.xml       312B
 sensor_demo.xml      456B
 test_program.xml     1K
 complex_prog.xml     2K
 another_test.xml     890B

UP/DN ENT L:Info
```

### Navigation Controls

#### Simple Picker
- **UP/DOWN**: Navigate menu
- **ENTER**: Select file
- **ESCAPE**: Cancel

#### Advanced Picker
- **UP/DOWN**: Navigate file list
- **ENTER**: Preview and select file
- **LEFT**: Show detailed file information
- **RIGHT**: Quick select (no preview)
- **ESCAPE**: Cancel selection

## Error Handling

The system includes comprehensive error handling:

1. **No Files Found**: Clear message when no XML files exist
2. **File Access Errors**: Graceful handling of file system issues
3. **Parse Errors**: Clear feedback for invalid XML files
4. **Runtime Errors**: Exception handling during program execution

## Sample Programs

### simple_move.xml
Basic motor movement program:
- Starts motor B at 50% power
- Waits 2 seconds
- Stops motor B

### sound_test.xml
Audio demonstration:
- Plays 440Hz tone for 1 second
- Waits 0.5 seconds
- Plays 880Hz tone for 1 second

### sensor_demo.xml
Touch sensor interaction:
- Loops 5 times
- Checks touch sensor on port S1
- Plays beep when pressed
- 100ms delay between checks

## Technical Implementation

### File System Access
Uses leJOS `File` class for:
- Directory listing (`File.listFiles()`)
- File information (`length()`, `exists()`)
- File filtering by extension

### Memory Management
- Efficient Vector usage for file lists
- String optimization for display
- Minimal object creation in loops

### User Interface
- leJOS `TextMenu` for simple picker
- Custom LCD drawing for advanced picker
- Button handling with proper debouncing

## Troubleshooting

### Common Issues

1. **No files shown**
   - Ensure XML files are uploaded to NXT
   - Check file extensions (.xml)
   - Verify files are in root directory

2. **File picker crashes**
   - Check available memory
   - Ensure leJOS firmware is up to date
   - Verify file system integrity

3. **Programs won't execute**
   - Validate XML syntax
   - Check for required start block
   - Verify block types are supported

### Debug Mode
Enable debug output by modifying the main programs to show additional information during execution.

## Future Enhancements

Potential improvements:
1. Directory navigation support
2. File operations (delete, rename)
3. Program favorites/bookmarks
4. File preview (first few blocks)
5. Sorting options (name, size, date)
6. Search/filter functionality

## Compatibility

- **leJOS NXJ**: 0.9.1 or later
- **Java**: J2ME/CDC compatible
- **NXT Firmware**: 1.29 or later
- **Memory**: Minimum 32KB free flash

## API Reference

### FilePicker Class
```java
public static String pickFile(String extension, String title)
public static String pickXMLFile()
public static String pickFileAdvanced(String extension, String title, boolean showInfo)
```

### AdvancedFilePicker Class
```java
public static String selectFileAdvanced(String title, String[] extensions)
public static String selectXmlFile()
public static void showFileInfo(String filename)
```

### DynamicNepoRunner Class
```java
public static void main(String[] args)  // Main entry point
```

This dynamic file selection system transforms the NEPO NXT interpreter from a static program loader into an interactive development environment, enabling rapid testing and deployment of multiple programs directly on the NXT device.
