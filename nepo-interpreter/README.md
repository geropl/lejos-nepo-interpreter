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
└── README.md                   # This file
```

## Prerequisites

1. **leJOS NXT** installed and configured
2. **Java Development Kit** (JDK 1.4 or compatible)
3. **Apache Ant** for building
4. **Open Roberta Lab** for creating programs

## Setup Instructions

### 1. Install leJOS NXT

Download and install leJOS NXT from: https://lejos.sourceforge.io/nxj.php

Make sure your NXT brick has leJOS firmware installed.

### 2. Configure Build Environment

Edit `build.xml` and set the correct path to your leJOS installation:

```xml
<property name="lejos.home" value="/path/to/your/lejos_nxj"/>
```

### 3. Create Your NEPO Program

1. Go to https://lab.open-roberta.org/
2. Select "NXT" as your robot
3. Create your program using the visual blocks
4. Export your program (Menu → Export → XML)
5. Save the XML file as `program.xml` in this directory

### 4. Build and Deploy

```bash
# Compile the interpreter
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

Priority areas for improvement:
1. More NEPO block implementations
2. Better error handling and debugging
3. Sensor support
4. Control flow structures (loops, conditionals)
5. Variable and expression handling

## License

This project is provided as-is for educational and experimental purposes.

## Related Projects

- [Open Roberta Lab](https://github.com/OpenRoberta/openroberta-lab) - Visual programming environment
- [leJOS](http://lejos.sourceforge.io/) - Java for LEGO Mindstorms
- [NEPO Documentation](https://jira.iais.fraunhofer.de/wiki/display/ORInfo) - NEPO programming language docs
