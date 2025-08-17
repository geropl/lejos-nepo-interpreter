# NEPO Interpreter - NXT Deployment Guide

**Version:** 1.0  
**Target Platform:** LEGO Mindstorms NXT  
**Framework:** leJOS NXT

---

## Prerequisites

### 1. Hardware Requirements
- **LEGO Mindstorms NXT brick** (NXT 1.0 or 2.0)
- **USB cable** for NXT connection
- **SD card** (optional, for storing multiple programs)
- **NXT sensors and motors** as needed for your programs

### 2. Software Requirements
- **Java JDK 1.4-1.8** (leJOS NXT compatibility)
- **leJOS NXT** firmware and development tools
- **USB drivers** for NXT brick

---

## Step 1: Install leJOS NXT

### Download and Install leJOS NXT
1. **Download leJOS NXT** from: http://www.lejos.org/nxt.php
2. **Install leJOS NXT** following the official installation guide
3. **Flash NXT firmware** with leJOS firmware (replaces LEGO firmware)

### Verify Installation
```bash
# Check leJOS installation
nxj -version

# Check NXT connection
nxj -r HelloWorld
```

### Set Environment Variables
```bash
# Add to your .bashrc or .profile
export NXJ_HOME=/path/to/lejos_nxt
export PATH=$PATH:$NXJ_HOME/bin
```

---

## Step 2: Build the NEPO Interpreter

### Project Structure
```
nepo-interpreter/
├── src/
│   ├── NepoInterpreterMain.java      # Main entry point
│   ├── NepoBlockExecutor.java        # Block execution engine
│   ├── SimpleXMLParser.java          # XML parser
│   ├── RobotConfiguration.java       # Hardware configuration
│   └── ConfigurationBlockExecutor.java # Config block handler
├── programs/                         # NEPO XML programs
│   ├── test_program.xml
│   └── examples/
└── build/                           # Compiled classes
```

### Build Commands

#### Option 1: Manual Build (Recommended)
```bash
# Navigate to project directory
cd nepo-interpreter

# Create build directory
mkdir -p build

# Compile all Java files
nxjc -cp src -d build src/*.java

# Create JAR file
cd build
jar cf NepoInterpreter.jar *.class

# Upload to NXT
nxj -cp . -upload NepoInterpreter.jar NepoInterpreterMain
```

#### Option 2: Build Script
Create `build.sh`:
```bash
#!/bin/bash
echo "Building NEPO Interpreter for NXT..."

# Clean previous build
rm -rf build
mkdir build

# Compile
echo "Compiling Java sources..."
nxjc -cp src -d build src/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

# Create JAR
echo "Creating JAR file..."
cd build
jar cf NepoInterpreter.jar *.class
cd ..

echo "Build complete! JAR file: build/NepoInterpreter.jar"
echo "Ready for upload to NXT."
```

Make executable and run:
```bash
chmod +x build.sh
./build.sh
```

---

## Step 3: Deploy to NXT

### Upload the Interpreter
```bash
# Upload the main program
nxj -cp build -upload build/NepoInterpreter.jar NepoInterpreterMain

# Alternative: Upload and run immediately
nxj -cp build -r NepoInterpreterMain
```

### Upload NEPO XML Programs
```bash
# Upload XML program files
nxj -upload programs/test_program.xml
nxj -upload programs/examples/motor_test.xml
nxj -upload programs/examples/sensor_test.xml
```

---

## Step 4: Select and Run NEPO Programs

### Method 1: Default Program
The interpreter looks for `test_program.xml` by default:
```bash
# Upload your XML program as test_program.xml
nxj -upload your_program.xml test_program.xml

# Run the interpreter (will load test_program.xml)
# Use NXT menu: Files -> NepoInterpreterMain
```

### Method 2: Command Line Arguments
```bash
# Run with specific XML file
nxj -cp build -r NepoInterpreterMain your_program.xml
```

### Method 3: NXT File Browser
1. **Upload multiple XML files** to NXT
2. **Modify NepoInterpreterMain.java** to show file selection menu
3. **Use NXT buttons** to select program

#### Enhanced Main with File Selection
```java
public static void main(String[] args) {
    LCD.clear();
    LCD.drawString("NEPO Interpreter", 0, 0);
    LCD.drawString("Select program:", 0, 2);
    
    String[] programs = {"test_program.xml", "motor_test.xml", "sensor_test.xml"};
    int selected = 0;
    
    while (true) {
        // Display menu
        for (int i = 0; i < programs.length; i++) {
            String marker = (i == selected) ? ">" : " ";
            LCD.drawString(marker + programs[i], 0, 3 + i);
        }
        LCD.refresh();
        
        // Handle button input
        int button = Button.waitForAnyPress();
        if (button == Button.ID_UP && selected > 0) {
            selected--;
        } else if (button == Button.ID_DOWN && selected < programs.length - 1) {
            selected++;
        } else if (button == Button.ID_ENTER) {
            runProgram(programs[selected]);
            break;
        } else if (button == Button.ID_ESCAPE) {
            return;
        }
        LCD.clear();
        LCD.drawString("NEPO Interpreter", 0, 0);
        LCD.drawString("Select program:", 0, 2);
    }
}
```

---

## Step 5: Create NEPO XML Programs

### Method 1: Export from OpenRoberta Lab
1. **Visit** https://lab.open-roberta.org
2. **Select NXT robot**
3. **Create your program** using visual blocks
4. **Export XML** (Menu -> Export -> XML)
5. **Save as** `your_program.xml`

### Method 2: Example XML Programs

#### Simple Motor Test
Create `programs/motor_test.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <block type="robActions_display_text" id="2">
          <value name="OUT">
            <block type="text" id="3">
              <field name="TEXT">Motor Test</field>
            </block>
          </value>
          <next>
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
                  <field name="NUM">2</field>
                </block>
              </value>
            </block>
          </next>
        </block>
      </statement>
    </block>
  </instance>
</blockSet>
```

#### Sensor Test
Create `programs/sensor_test.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <block type="robControls_repeat_forever" id="2">
          <statement name="DO">
            <block type="robActions_display_text" id="3">
              <value name="OUT">
                <block type="robSensors_ultrasonic_distance" id="4">
                  <field name="SENSORPORT">4</field>
                </block>
              </value>
              <next>
                <block type="robControls_wait_time" id="5">
                  <value name="TIME">
                    <block type="math_number" id="6">
                      <field name="NUM">500</field>
                    </block>
                  </value>
                </block>
              </next>
            </block>
          </statement>
        </block>
      </statement>
    </block>
  </instance>
</blockSet>
```

---

## Step 6: Running Programs on NXT

### Using NXT Menu System
1. **Power on NXT**
2. **Navigate to Files**
3. **Select NepoInterpreterMain**
4. **Press Enter** to run
5. **Program will load** test_program.xml by default

### Program Execution Flow
```
1. NXT boots with leJOS firmware
2. User selects NepoInterpreterMain from Files menu
3. Interpreter starts and displays startup screen
4. Loads XML program (test_program.xml or specified file)
5. Parses XML and finds robControls_start block
6. Executes program blocks sequentially
7. Shows completion message when done
```

### Debugging on NXT
The interpreter provides feedback on the NXT screen:
- **"NEPO Interpreter v1.0"** - Startup
- **"Loading: filename.xml"** - File loading
- **"XML parsed OK"** - Successful parsing
- **"Start block found"** - Ready to execute
- **"Executing..."** - Program running
- **Error messages** - If something goes wrong

---

## Troubleshooting

### Common Build Issues

#### "nxjc command not found"
```bash
# Check leJOS installation
echo $NXJ_HOME
ls $NXJ_HOME/bin

# Add to PATH
export PATH=$PATH:$NXJ_HOME/bin
```

#### "Cannot find leJOS classes"
```bash
# Verify leJOS classpath
nxjc -version
nxjc -cp src -d build src/NepoInterpreterMain.java
```

#### "Compilation errors"
- **Check Java version** (must be compatible with leJOS NXT)
- **Verify all source files** are present
- **Check for syntax errors** in Java code

### Common Runtime Issues

#### "Failed to parse XML file"
- **Check XML file exists** on NXT
- **Verify XML syntax** is correct
- **Ensure file was uploaded** properly

#### "No start block found"
- **XML must contain** `robControls_start` block
- **Check XML structure** matches expected format
- **Verify block type** is exactly "robControls_start"

#### "Motor/Sensor not working"
- **Check hardware connections**
- **Verify port assignments** in XML match physical setup
- **Test with simple programs** first

### Memory Issues
- **NXT has limited memory** (~64KB for programs)
- **Keep XML programs small** for complex logic
- **Use efficient data structures**
- **Avoid large string operations**

---

## Advanced Deployment Options

### Multiple Program Management
Create a program launcher:
```java
// ProgramLauncher.java
public class ProgramLauncher {
    private static String[] programs = {
        "basic_movement.xml",
        "sensor_navigation.xml", 
        "line_following.xml",
        "obstacle_avoidance.xml"
    };
    
    public static void main(String[] args) {
        int selected = showMenu();
        if (selected >= 0) {
            NepoInterpreterMain.main(new String[]{programs[selected]});
        }
    }
}
```

### SD Card Deployment
```bash
# Upload to SD card for more storage
nxj -d /path/to/sd/card -upload program.xml
```

### Wireless Deployment
```bash
# Deploy via Bluetooth (if configured)
nxj -b -r NepoInterpreterMain program.xml
```

---

## Example Deployment Session

```bash
# Complete deployment example
cd nepo-interpreter

# 1. Build the interpreter
./build.sh

# 2. Connect NXT via USB
# (NXT should be on and connected)

# 3. Upload interpreter
nxj -cp build -upload build/NepoInterpreter.jar NepoInterpreterMain

# 4. Upload test program
nxj -upload programs/motor_test.xml test_program.xml

# 5. Run on NXT
# Use NXT menu: Files -> NepoInterpreterMain

# 6. Or run directly
nxj -cp build -r NepoInterpreterMain
```

---

## Summary

### Quick Start Checklist
- [ ] Install leJOS NXT and flash firmware
- [ ] Build NEPO interpreter with `nxjc`
- [ ] Upload interpreter to NXT
- [ ] Create or export NEPO XML program
- [ ] Upload XML program to NXT
- [ ] Run interpreter from NXT Files menu
- [ ] Select and execute your program

### Key Files for Deployment
- **NepoInterpreterMain.class** - Main executable
- **Supporting .class files** - All compiled Java classes
- **XML program files** - Your NEPO programs
- **build.sh** - Build automation script

**🚀 Your NEPO interpreter is now ready to run OpenRoberta Lab programs on real NXT hardware! 🚀**
