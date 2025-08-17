# NEPO XML Interpreter for leJOS NXT

This project implements a runtime interpreter that can execute NEPO XML programs (created with Open Roberta Lab) directly on leJOS NXT firmware.

## Architecture Overview

The interpreter consists of several key components:

1. **XML Parser**: Parses NEPO XML files into a DOM structure
2. **Block Registry**: Maps NEPO block types to Java execution implementations
3. **Execution Engine**: Traverses and executes blocks sequentially
4. **Hardware Abstraction**: Maps NEPO hardware references to leJOS APIs

## How It Works

1. Create your program visually in Open Roberta Lab
2. Export/download the program as XML
3. Transfer the XML file to your NXT brick
4. Run the NEPO Interpreter on the NXT
5. The interpreter reads and executes your visual program

## Advantages of This Approach

- **Visual Programming**: Use Open Roberta Lab's intuitive drag-and-drop interface
- **leJOS Power**: Leverage leJOS's advanced features and performance
- **No Code Generation**: Direct interpretation eliminates compilation step
- **Extensible**: Easy to add support for new NEPO blocks
- **Educational**: Great for learning both visual programming and Java

## Project Structure

```
nepo-interpreter/
├── src/                        # All Java source files
│   ├── DynamicNepoRunner.java      # Main entry point with file picker
│   ├── ShallowXMLParser.java       # Memory-optimized XML parser
│   ├── ShallowString.java          # Memory-efficient string class
│   ├── ShallowXMLElement.java      # Lazy XML element materialization
│   ├── ShallowXMLParser.java       # Memory-optimized XML parser
│   ├── IXMLParser.java             # XML parser interface
│   ├── IXMLElement.java            # XML element interface
│   ├── IString.java                # Memory-efficient string interface
│   ├── NepoBlockExecutor.java      # NEPO block execution engine
│   ├── FilePicker.java             # Dynamic file selection
│   ├── CrashLogger.java            # Error handling and logging
│   └── ...                         # Other core classes
├── test/                       # Test framework and test cases
│   ├── src/                    # Test source files
│   ├── test-data/              # Test XML programs
│   └── build/                  # Compiled test classes
├── docs/                       # Documentation and guides
│   ├── QUICK_START_GUIDE.md    # Getting started guide
│   ├── DEPLOYMENT_GUIDE.md     # Deployment instructions
│   ├── DYNAMIC_FILE_SELECTION.md  # File picker documentation
│   ├── LEJOS_SETUP.md          # leJOS installation guide
│   └── project-management/     # Project tracking documents
├── build/                      # Compiled class files
├── target/                     # Generated NXJ/NXD files
├── examples/                   # Sample NEPO XML programs
├── sample_programs/            # Additional test programs
├── build.xml                   # Ant build script
├── build.sh                    # Shell build script
├── run_tests.sh                # Test runner
└── README.md                   # This file
```

## Build Options

The project supports both production and debug builds:

### Production Build
```bash
# Using shell script
./build.sh

# Using Ant
ant deploy
```

### Debug Build (with Remote Console)
```bash
# Using shell script  
./build.sh debug

# Using Ant
ant debug
```

**Debug builds include:**
- Full stack traces with proper class and method names
- Line number information for exceptions
- Remote console support for real-time debugging
- Debug info file (`.nxd`) for post-mortem analysis

**To use remote debugging:**
1. Build with debug option
2. Upload the program to NXT
3. Start remote console: `nxjconsole -di build/NepoInterpreter.nxd`
4. Run the program on NXT
5. View detailed debugging info on PC console

## Quick Start

### Dev Container (Recommended)
This project includes a complete dev container with leJOS NXJ pre-installed:

1. Open in GitHub Codespaces or VS Code with Dev Containers extension
2. Container will automatically install leJOS NXJ tools
3. Build and test:
   ```bash
   ./build.sh                   # Production build
   ./build.sh debug             # Debug build with remote console
   ./run_tests.sh               # Run all tests
   ```

### Manual Setup
See [docs/LEJOS_SETUP.md](docs/LEJOS_SETUP.md) for detailed installation instructions.

## Prerequisites

1. **leJOS NXJ 0.9.1beta-3** - Java platform for LEGO NXT
2. **Java 8 JDK** - Required for leJOS NXJ compatibility  
3. **Open Roberta Lab** - For creating visual programs
4. **USB connection** - For uploading to NXT (optional for development)

## Documentation

Comprehensive documentation is available in the `docs/` folder:

- **[Quick Start Guide](docs/QUICK_START_GUIDE.md)** - Get up and running quickly
- **[Deployment Guide](docs/DEPLOYMENT_GUIDE.md)** - Production deployment instructions
- **[leJOS Setup](docs/LEJOS_SETUP.md)** - Complete installation guide
- **[Dynamic File Selection](docs/DYNAMIC_FILE_SELECTION.md)** - File picker documentation
- **[Troubleshooting](docs/TROUBLESHOOTING.md)** - Common issues and solutions
- **[Debugging Guide](docs/DEBUGGING_GUIDE.md)** - Debug builds and remote console
- **[XML Program Guide](docs/XML_PROGRAM_GUIDE.md)** - Creating NEPO programs
- **[Block Types](docs/NepoBlockTypes.md)** - Supported NEPO blocks

## Testing

The project includes a comprehensive test framework:

```bash
./run_tests.sh                 # Run all tests
./run_tests.sh -v              # Verbose output
./run_tests.sh -c unit         # Unit tests only
```

Test results are generated in multiple formats:
- `test/test-results.html` - Detailed HTML report
- `test/test-results.json` - Machine-readable results
- `test/test-results.txt` - Simple text summary

## Memory Management for NXT

### NXT Memory Constraints
- **Total RAM**: 64KB (extremely limited!)
- **ShallowXML Optimization**: 76% memory reduction vs traditional parsing
- **No artificial limits**: Let the NXT handle memory naturally

### Memory Optimization Features
- **ShallowString approach** - Offset/length pointers instead of string copies
- **Lazy object initialization** - Collections created only when needed
- **Immediate cleanup** - Buffers released after use
- **Single buffer parsing** - Eliminates recursive string copying

### ShallowXML Benefits

| Approach | 8KB XML Memory Usage | Efficiency |
|----------|---------------------|------------|
| **Traditional** | ~70KB (crashes) | Recursive copying |
| **ShallowXML** | ~16KB (works!) | Single buffer + pointers |

**Memory Strategy:**
- ShallowString: 12 bytes vs full String copy (50-500+ bytes)
- Lazy materialization: Parse attributes/children only when accessed
- No hard limits: Try any file size, let OutOfMemoryError occur naturally if needed

## Build Options

Multiple build approaches are supported:

### Shell Script (Recommended)
```bash
./build.sh                      # Production build
./build.sh debug                # Debug build with remote console support
```

### Ant Build System
```bash
ant deploy                      # Production build and upload
ant debug                       # Debug build with remote console
ant console                     # Start remote console (requires debug build)
```

### Generated Files (in `target/` directory)
- **target/NepoInterpreter.nxj** - NEPO interpreter with dynamic file selection and crash logging
- **target/NepoInterpreter.nxd** - Debug info for remote console (debug builds only)

## Debugging and Error Handling

### Crash Logger
The interpreter includes a comprehensive crash logging system:

- **Screen Display** - Shows error context and type on NXT LCD
- **File Logging** - Complete crash details saved to `crash.log`
- **Memory Info** - System memory status at crash time
- **Stack Traces** - Full call stack information (numeric format)

### Debug vs Production Builds

| Feature | Production | Debug |
|---------|------------|-------|
| Exception Display | Numeric codes | Full class names |
| Stack Traces | Method numbers | Method names + line numbers |
| Remote Console | No | Yes |
| File Size | Smaller | Larger |
| Performance | Faster | Slower |

**For Development:** Use debug builds with remote console for detailed error information.

**For Deployment:** Use production builds for optimal performance and memory usage.

## New Features

### Dynamic File Selection
The interpreter now includes dynamic file selection capabilities:

- **Interactive File Browser** - Select XML programs directly on NXT
- **Two Picker Modes** - Simple menu or advanced navigation
- **File Information** - Display file sizes and details
- **Multiple Programs** - Run different programs without recompiling
- **Sample Programs** - Included XML examples for testing

See [docs/DYNAMIC_FILE_SELECTION.md](docs/DYNAMIC_FILE_SELECTION.md) for complete documentation.

### ShallowXML Memory Optimization
Revolutionary memory-efficient XML parsing for NXT's 64KB RAM constraint:

- **76% Memory Reduction** - 8KB XML uses ~16KB memory (vs 70KB+ traditional)
- **ShallowString** - 12-byte pointers instead of full string copies
- **Lazy Materialization** - Parse attributes/children only when accessed
- **No Size Limits** - Handles any XML file size, fails gracefully on OutOfMemoryError
- **Single Buffer** - Eliminates recursive string copying during parsing

**Memory Comparison:**
| Approach | 8KB XML Memory Usage | Result |
|----------|---------------------|--------|
| **Traditional** | ~70KB | OutOfMemoryError crash |
| **ShallowXML** | ~16KB | Successful parsing |

## Setup Instructions

### 1. Development Environment

The project includes a complete dev container setup:
- Automatic leJOS NXJ installation
- Pre-configured Java 8 environment
- Build tools and scripts ready to use

See [LEJOS_SETUP.md](LEJOS_SETUP.md) for detailed setup instructions.

### 2. Create Your NEPO Program

1. Go to https://lab.open-roberta.org/
2. Select "NXT" as your robot
3. Create your program using the visual blocks
4. Export your program (Menu → Export → XML)
5. Save the XML file in the project directory

### 3. Build and Deploy

```bash
# Build all components
./build_dynamic.sh

# Upload to NXT
nxjupload NepoDynamic.nxj
nxjupload sample_programs/*.xml
```

### 4. Run Tests

```bash
# Run all tests
./run_tests.sh

# Run with options
./run_tests.sh --help     # Show all options
./run_tests.sh -v         # Verbose output
./run_tests.sh -c unit    # Run unit tests only
```

The test suite includes:
- **Unit Tests** - Individual block functionality
- **Integration Tests** - Block combinations and workflows  
- **Mock Hardware Tests** - Simulated NXT hardware interactions
- **XML Parser Tests** - NEPO XML parsing validation

Test reports are generated in `test/` directory as HTML, JSON, and text formats.

### 5. Debugging on NXT Hardware

When running on real NXT hardware, crashes are automatically logged:

```bash
# After crashes, download detailed logs
nxjcontrol  # Use GUI to transfer crash.log from NXT to PC
```

**Debugging Features:**
- **Useful Error Messages** - Context, exception type, and message on NXT screen
- **Complete Stack Traces** - Full details written to crash.log file
- **System Information** - Memory usage and Java version in logs
- **Easy File Transfer** - Use nxjcontrol GUI to get crash.log

**What you see on NXT screen:**
```
ERROR #1
Main Execution
NullPointerException
Object is null

Logged to
crash.log
Press any key
```

**What you get in crash.log:**
- Complete stack trace with line numbers
- Exception details and context
- System memory information
- Timestamp for each crash

### 5. Run on NXT

1. On your NXT, navigate to Files
2. Find and run "NepoInterpreter"
3. The interpreter will automatically load and execute `program.xml`

## Supported NEPO Blocks

### Currently Implemented (Phase 1)
- ✅ `robControls_start` - Program start
- ✅ `robActions_display_text` - Display text on LCD
- ✅ `robActions_motor_on` - Control motors
- ✅ `robActions_motor_stop` - Stop motors
- ✅ `robControls_wait_time` - Wait/delay
- ✅ `math_number` - Numeric values
- ✅ `text` - Text strings

### Planned (Phase 2)
- 🔄 `robSensors_touch_isPressed` - Touch sensor
- 🔄 `robSensors_ultrasonic_distance` - Ultrasonic sensor
- 🔄 `robControls_if` - Conditional execution
- 🔄 `logic_compare` - Comparisons
- 🔄 `logic_boolean` - Boolean values

### Future (Phase 3+)
- ⏳ Loops and advanced control flow
- ⏳ Variables and expressions
- ⏳ More sensor types
- ⏳ Sound and advanced display features

## Example Programs

### Hello World
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <block type="robActions_display_text" id="2">
          <value name="OUT">
            <block type="text" id="3">
              <field name="TEXT">Hello Roberta!</field>
            </block>
          </value>
        </block>
      </statement>
    </block>
  </instance>
</blockSet>
```

### Motor Control
```xml
<!-- Move motor B forward for 1 rotation at 50% power -->
<block type="robActions_motor_on" id="4">
  <field name="MOTORPORT">B</field>
  <field name="MOTORROTATION">ROTATIONS</field>
  <value name="POWER">
    <block type="math_number" id="5">
      <field name="NUM">50</field>
    </block>
  </value>
  <value name="VALUE">
    <block type="math_number" id="6">
      <field name="NUM">1</field>
    </block>
  </value>
</block>
```

## Extending the Interpreter

To add support for new NEPO blocks:

1. **Identify the Block**: Look at the XML structure of your NEPO program
2. **Create Executor**: Implement a new `BlockExecutor` class
3. **Register Block**: Add it to the `BLOCK_EXECUTORS` map
4. **Test**: Create a simple program to test the new block

Example of adding a new block:

```java
// Add to registerBlockExecutors()
BLOCK_EXECUTORS.put("robActions_play_tone", new PlayToneBlockExecutor());

// Implement the executor
static class PlayToneBlockExecutor implements BlockExecutor {
    public void execute(Element block, NepoInterpreter interpreter) throws Exception {
        Object frequency = interpreter.getValue(block, "FREQUENCY");
        Object duration = interpreter.getValue(block, "DURATION");
        
        if (frequency instanceof Double && duration instanceof Double) {
            int freq = ((Double) frequency).intValue();
            int dur = ((Double) duration).intValue();
            Sound.playTone(freq, dur);
        }
    }
}
```

## Troubleshooting

### Common Issues

1. **"No start block found"**
   - Make sure your NEPO program has a start block
   - Check that the XML file is properly formatted

2. **"Unknown block: xyz"**
   - The block type is not yet implemented
   - Check the supported blocks list above

3. **Motor not moving**
   - Verify motor port mapping (A, B, C)
   - Check power values (should be -100 to 100)

4. **XML parsing errors**
   - Ensure the XML file is valid
   - Try re-exporting from Open Roberta Lab

### Debug Mode

To enable debug output, modify the interpreter to display block information:

```java
LCD.drawString("Executing: " + blockType, 0, 2);
LCD.refresh();
```

## Contributing

This is a proof-of-concept implementation. Contributions are welcome!

### Development Process
For detailed development information, see the project management documentation:
- **[Development Plan](project-management/DEVELOPMENT_PLAN.md)** - Overall project status and roadmap
- **[Implementation Guide](project-management/BLOCK_IMPLEMENTATION_CHECKLIST.md)** - Step-by-step implementation process
- **[Project Tracking](project-management/PROJECT_TRACKING_GUIDE.md)** - How to use the tracking system

### Current Development Status
- **Phase 1 Complete:** 13/13 core blocks implemented ✅
- **Phase 2 Planned:** Basic interactivity (5 blocks) 📋
- **Phase 3 Planned:** Advanced control flow (6 blocks) 📋
- **Total Target:** 35+ NEPO block types

Priority areas for improvement:
1. Test harness implementation (critical path)
2. Phase 2 sensor and logic blocks
3. Variable management system
4. Advanced control flow structures
5. Enhanced hardware support

## License

This project is provided as-is for educational and experimental purposes.

## Related Projects

- [Open Roberta Lab](https://github.com/OpenRoberta/openroberta-lab) - Visual programming environment
- [leJOS](http://lejos.sourceforge.io/) - Java for LEGO Mindstorms
- [NEPO Documentation](https://jira.iais.fraunhofer.de/wiki/display/ORInfo) - NEPO programming language docs
