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
├── src/
│   └── NepoInterpreter.java     # Main interpreter class
├── build.xml                    # Ant build script
├── sample_nepo.xml             # Example NEPO program
├── program.xml                 # Your NEPO program goes here
├── NepoBlockTypes.md           # Documentation of supported blocks
├── project-management/         # Project tracking and planning documents
│   ├── DEVELOPMENT_PLAN.md     # Master project plan and status
│   ├── BLOCK_TRACKER.json      # Detailed block implementation tracking
│   ├── MILESTONES.md           # Time-based milestone tracking
│   ├── BLOCK_IMPLEMENTATION_CHECKLIST.md  # Implementation guidelines
│   ├── TESTING_FRAMEWORK_SPEC.md          # Test framework specification
│   └── PROJECT_TRACKING_GUIDE.md          # How to use tracking system
└── README.md                   # This file
```

## Quick Start

### Dev Container (Recommended)
This project includes a complete dev container with leJOS NXJ pre-installed:

1. Open in GitHub Codespaces or VS Code with Dev Containers extension
2. Container will automatically install leJOS NXJ tools
3. Build and test immediately:
   ```bash
   ./test_environment.sh  # Verify installation
   ./build_dynamic.sh     # Build programs
   ```

### Manual Setup
See [LEJOS_SETUP.md](LEJOS_SETUP.md) for detailed installation instructions.

## Prerequisites

1. **leJOS NXJ 0.9.1beta-3** - Java platform for LEGO NXT
2. **Java 8 JDK** - Required for leJOS NXJ compatibility  
3. **Open Roberta Lab** - For creating visual programs
4. **USB connection** - For uploading to NXT (optional for development)

## New Features

### Dynamic File Selection
The interpreter now includes dynamic file selection capabilities:

- **Interactive File Browser** - Select XML programs directly on NXT
- **Two Picker Modes** - Simple menu or advanced navigation
- **File Information** - Display file sizes and details
- **Multiple Programs** - Run different programs without recompiling
- **Sample Programs** - Included XML examples for testing

See [DYNAMIC_FILE_SELECTION.md](DYNAMIC_FILE_SELECTION.md) for complete documentation.

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
# Test environment
./test_environment.sh

# Build all components
./build_dynamic.sh

# Upload to NXT
nxjupload NepoDynamic.nxj
nxjupload sample_programs/*.xml
ant compile

# Build JAR file
ant jar

# Upload interpreter and program to NXT
ant deploy
```

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
