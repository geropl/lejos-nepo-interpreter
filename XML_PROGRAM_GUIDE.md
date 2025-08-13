# NEPO XML Program Selection and Loading Guide

**Target:** LEGO Mindstorms NXT with NEPO Interpreter  
**Purpose:** How to create, select, and load NEPO XML programs

---

## Overview

The NEPO Interpreter can load and execute XML programs created in OpenRoberta Lab. This guide covers all methods for selecting and loading these programs on your NXT.

---

## Method 1: Default Program Loading (Simplest)

### How It Works
The interpreter automatically looks for `test_program.xml` on the NXT when started without arguments.

### Steps
1. **Create/Export your NEPO program** from OpenRoberta Lab
2. **Save as any filename** (e.g., `my_robot_program.xml`)
3. **Upload to NXT as test_program.xml**:
   ```bash
   nxj -upload my_robot_program.xml test_program.xml
   ```
4. **Run interpreter** from NXT Files menu
5. **Program loads automatically**

### Pros/Cons
- ✅ **Simple** - No file selection needed
- ✅ **Fast** - Immediate program loading
- ❌ **Limited** - Only one program at a time
- ❌ **Overwrite** - Must replace file for new program

---

## Method 2: Command Line Program Selection

### How It Works
Specify the XML filename as a command line argument when running the interpreter.

### Steps
1. **Upload multiple XML programs** to NXT:
   ```bash
   nxj -upload program1.xml
   nxj -upload program2.xml
   nxj -upload program3.xml
   ```

2. **Run with specific program**:
   ```bash
   # Run program1.xml
   nxj -cp build -r NepoInterpreterMain program1.xml
   
   # Run program2.xml
   nxj -cp build -r NepoInterpreterMain program2.xml
   ```

### Pros/Cons
- ✅ **Multiple programs** - Store many programs on NXT
- ✅ **Flexible** - Choose any program
- ❌ **Requires PC** - Must run from computer
- ❌ **USB connection** - Can't run standalone on NXT

---

## Method 3: Enhanced File Selection Menu (Recommended)

### Implementation
Modify `NepoInterpreterMain.java` to include a file selection menu:

```java
public class NepoInterpreterMain {
    
    // List of available programs
    private static String[] availablePrograms = {
        "motor_test.xml",
        "sensor_test.xml", 
        "line_follow.xml",
        "obstacle_avoid.xml",
        "dance_routine.xml"
    };
    
    public static void main(String[] args) {
        LCD.clear();
        LCD.drawString("NEPO Interpreter", 0, 0);
        LCD.drawString("v1.0", 0, 1);
        LCD.refresh();
        Delay.msDelay(1000);
        
        String selectedProgram;
        
        if (args.length > 0) {
            // Command line argument provided
            selectedProgram = args[0];
        } else {
            // Show selection menu
            selectedProgram = showProgramMenu();
            if (selectedProgram == null) {
                return; // User cancelled
            }
        }
        
        runProgram(selectedProgram);
    }
    
    private static String showProgramMenu() {
        int selectedIndex = 0;
        int maxVisible = 5; // Max programs visible on screen
        int startIndex = 0;
        
        while (true) {
            LCD.clear();
            LCD.drawString("Select Program:", 0, 0);
            LCD.drawString("UP/DOWN: Navigate", 0, 7);
            LCD.drawString("ENTER: Select", 0, 6);
            LCD.drawString("ESCAPE: Cancel", 0, 5);
            
            // Display visible programs
            for (int i = 0; i < maxVisible && (startIndex + i) < availablePrograms.length; i++) {
                int programIndex = startIndex + i;
                String marker = (programIndex == selectedIndex) ? ">" : " ";
                String programName = availablePrograms[programIndex];
                
                // Truncate long names
                if (programName.length() > 14) {
                    programName = programName.substring(0, 11) + "...";
                }
                
                LCD.drawString(marker + programName, 0, 2 + i);
            }
            
            LCD.refresh();
            
            // Handle button input
            int button = Button.waitForAnyPress();
            
            if (button == Button.ID_UP) {
                if (selectedIndex > 0) {
                    selectedIndex--;
                    if (selectedIndex < startIndex) {
                        startIndex = selectedIndex;
                    }
                }
            } else if (button == Button.ID_DOWN) {
                if (selectedIndex < availablePrograms.length - 1) {
                    selectedIndex++;
                    if (selectedIndex >= startIndex + maxVisible) {
                        startIndex = selectedIndex - maxVisible + 1;
                    }
                }
            } else if (button == Button.ID_ENTER) {
                return availablePrograms[selectedIndex];
            } else if (button == Button.ID_ESCAPE) {
                return null; // Cancel
            }
        }
    }
    
    private static void runProgram(String xmlFile) {
        LCD.clear();
        LCD.drawString("Loading:", 0, 0);
        LCD.drawString(xmlFile, 0, 1);
        LCD.refresh();
        
        try {
            // Parse and execute the XML program
            SimpleXMLParser.XMLElement root = SimpleXMLParser.parseFile(xmlFile);
            if (root == null) {
                showError("Failed to parse XML file");
                return;
            }
            
            // Find configuration section
            SimpleXMLParser.XMLElement configElement = findConfigSection(root);
            
            // Find program section
            SimpleXMLParser.XMLElement startBlock = findStartBlock(root);
            if (startBlock == null) {
                showError("No start block found");
                return;
            }
            
            // Create executor and set configuration
            NepoBlockExecutor executor = new NepoBlockExecutor();
            if (configElement != null) {
                executor.setConfiguration(configElement);
            }
            
            LCD.clear();
            LCD.drawString("Executing...", 0, 0);
            LCD.drawString(xmlFile, 0, 1);
            LCD.refresh();
            
            // Execute the program
            executor.executeBlock(startBlock);
            
            LCD.clear();
            LCD.drawString("Program finished", 0, 0);
            LCD.drawString("Press any key", 0, 6);
            LCD.refresh();
            Button.waitForAnyPress();
            
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }
    
    private static void showError(String message) {
        LCD.clear();
        LCD.drawString("ERROR:", 0, 0);
        LCD.drawString(message, 0, 2);
        LCD.drawString("Press any key", 0, 6);
        LCD.refresh();
        Button.waitForAnyPress();
    }
    
    // ... rest of existing methods
}
```

### Setup Steps
1. **Modify the availablePrograms array** with your XML filenames
2. **Recompile and upload** the enhanced interpreter
3. **Upload your XML programs** to the NXT
4. **Run interpreter** - menu appears automatically

### Pros/Cons
- ✅ **User-friendly** - Visual menu on NXT screen
- ✅ **Standalone** - No PC required after upload
- ✅ **Multiple programs** - Easy switching
- ✅ **Professional** - Polished user experience
- ❌ **Code changes** - Requires modifying source
- ❌ **Hardcoded list** - Must update array for new programs

---

## Method 4: Dynamic File Discovery

### Implementation
Automatically discover XML files on the NXT:

```java
private static String[] discoverXMLFiles() {
    Vector xmlFiles = new Vector();
    
    try {
        // This is a simplified example - actual file discovery
        // would require leJOS file system APIs
        File directory = new File(".");
        String[] files = directory.list();
        
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".xml")) {
                xmlFiles.addElement(files[i]);
            }
        }
        
    } catch (Exception e) {
        // Fallback to default programs
        xmlFiles.addElement("test_program.xml");
    }
    
    String[] result = new String[xmlFiles.size()];
    xmlFiles.copyInto(result);
    return result;
}
```

### Pros/Cons
- ✅ **Automatic** - Finds all XML files
- ✅ **Dynamic** - No hardcoded lists
- ❌ **Complex** - Requires file system access
- ❌ **leJOS limitations** - File discovery may be limited

---

## Creating NEPO XML Programs

### From OpenRoberta Lab (Recommended)

1. **Visit OpenRoberta Lab**:
   - Go to https://lab.open-roberta.org
   - Select "NXT" as your robot

2. **Create Your Program**:
   - Drag and drop blocks to create your program
   - Test in the simulator if available
   - Ensure you have a "start" block

3. **Export XML**:
   - Click the menu (≡) button
   - Select "Export" → "Program" → "XML"
   - Save the file with a descriptive name

4. **Upload to NXT**:
   ```bash
   nxj -upload your_program.xml
   ```

### Manual XML Creation

#### Basic Template
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <!-- Your program blocks go here -->
      </statement>
    </block>
  </instance>
</blockSet>
```

#### With Configuration
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <config>
    <block type="robBrick_EV3-Brick" id="config1">
      <field name="WHEEL_DIAMETER">5.6</field>
      <field name="TRACK_WIDTH">12</field>
      <statement name="ST">
        <block type="robBrick_motor_big" id="config2">
          <field name="MOTORPORT">B</field>
          <field name="MOTOR_REGULATION">TRUE</field>
          <field name="MOTOR_DRIVE">RIGHT</field>
        </block>
      </statement>
    </block>
  </config>
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <!-- Your program blocks go here -->
      </statement>
    </block>
  </instance>
</blockSet>
```

---

## Example Programs

### 1. Simple Motor Test
**Filename:** `motor_test.xml`
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
                  <field name="NUM">3</field>
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

### 2. Touch Sensor Response
**Filename:** `touch_response.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <block type="robControls_repeat_forever" id="2">
          <statement name="DO">
            <block type="robControls_if" id="3">
              <value name="IF0">
                <block type="robSensors_touch_isPressed" id="4">
                  <field name="SENSORPORT">1</field>
                </block>
              </value>
              <statement name="DO0">
                <block type="robActions_display_text" id="5">
                  <value name="OUT">
                    <block type="text" id="6">
                      <field name="TEXT">Touched!</field>
                    </block>
                  </value>
                  <next>
                    <block type="robActions_play_tone" id="7">
                      <value name="FREQUENCY">
                        <block type="math_number" id="8">
                          <field name="NUM">440</field>
                        </block>
                      </value>
                      <value name="DURATION">
                        <block type="math_number" id="9">
                          <field name="NUM">200</field>
                        </block>
                      </value>
                    </block>
                  </next>
                </block>
              </statement>
            </block>
          </statement>
        </block>
      </statement>
    </block>
  </instance>
</blockSet>
```

---

## Troubleshooting XML Programs

### Common XML Issues

#### "Failed to parse XML file"
- **Check XML syntax** - Use XML validator
- **Verify file encoding** - Should be UTF-8
- **Check file upload** - Ensure file exists on NXT

#### "No start block found"
- **Must have robControls_start block**
- **Check block type spelling** exactly
- **Verify XML structure** matches expected format

#### "Unknown block type"
- **Check block names** against implemented blocks
- **Verify spelling** of block types
- **Ensure block is supported** in our implementation

### XML Validation
```bash
# Validate XML syntax
xmllint --noout your_program.xml

# Check for common issues
grep -i "robControls_start" your_program.xml
```

### Debug Mode
Add debug output to your XML programs:
```xml
<block type="robActions_display_text" id="debug1">
  <value name="OUT">
    <block type="text" id="debug2">
      <field name="TEXT">Debug: Step 1</field>
    </block>
  </value>
</block>
```

---

## Best Practices

### File Organization
```
NXT Files/
├── NepoInterpreterMain.nxj    # Main interpreter
├── programs/
│   ├── basic/
│   │   ├── motor_test.xml
│   │   └── sensor_test.xml
│   ├── intermediate/
│   │   ├── line_follow.xml
│   │   └── maze_solve.xml
│   └── advanced/
│       ├── dance_routine.xml
│       └── soccer_robot.xml
```

### Naming Conventions
- **Use descriptive names**: `line_following.xml` not `prog1.xml`
- **Include difficulty**: `basic_motor_test.xml`
- **Version numbers**: `maze_solver_v2.xml`
- **Keep names short**: NXT display is limited

### Program Structure
- **Always start with robControls_start**
- **Add display messages** for user feedback
- **Include error handling** where possible
- **Test incrementally** - start simple, add complexity

---

## Summary

### Quick Reference

| Method | Best For | Complexity |
|--------|----------|------------|
| **Default Loading** | Single program testing | Low |
| **Command Line** | Development/debugging | Medium |
| **File Menu** | Multiple programs, user-friendly | High |
| **Auto Discovery** | Dynamic program management | Very High |

### Recommended Workflow
1. **Start with default loading** for initial testing
2. **Implement file menu** for production use
3. **Create multiple example programs**
4. **Test thoroughly** on actual NXT hardware

**🎯 Your NXT can now load and run any NEPO XML program created in OpenRoberta Lab! 🎯**
