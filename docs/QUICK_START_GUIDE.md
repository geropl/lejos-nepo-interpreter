# NEPO Interpreter - Quick Start Guide

**Get your NXT running NEPO programs in 15 minutes!**

---

## Prerequisites Checklist

Before starting, ensure you have:
- [ ] **LEGO Mindstorms NXT brick** (any version)
- [ ] **USB cable** for NXT connection
- [ ] **Computer** with Java installed
- [ ] **Internet connection** for downloads

---

## Step 1: Install leJOS NXT (5 minutes)

### Download leJOS
1. **Visit**: http://www.lejos.org/nxt.php
2. **Download**: leJOS NXT (latest version)
3. **Install**: Follow the installer for your OS

### Flash NXT Firmware
1. **Connect NXT** via USB cable
2. **Turn on NXT** and ensure it's detected
3. **Run**: `nxjflash` (or use leJOS NXT Flash utility)
4. **Wait**: Firmware flashing takes ~2 minutes
5. **Verify**: NXT should show leJOS menu after reboot

### Test Installation
```bash
# Check if leJOS is working
nxj -version

# Test NXT connection
nxj -r HelloWorld
```

If you see "Hello World" on your NXT screen, you're ready!

---

## Step 2: Build NEPO Interpreter (3 minutes)

### Quick Build
```bash
# Navigate to the project directory
cd nepo-interpreter

# Build everything
nxjc -cp src -d build src/*.java
cd build
jar cf NepoInterpreter.jar *.class
cd ..

# Upload to NXT
nxj -cp build -upload build/NepoInterpreter.jar NepoInterpreterMain
```

### Verify Build
Your NXT should now have "NepoInterpreterMain" in the Files menu.

---

## Step 3: Create Your First NEPO Program (5 minutes)

### Option A: Use OpenRoberta Lab (Recommended)
1. **Visit**: https://lab.open-roberta.org
2. **Select**: "NXT" robot
3. **Create simple program**:
   - Drag "start" block
   - Add "display text" block with "Hello NEPO!"
   - Connect blocks
4. **Export**: Menu → Export → XML
5. **Save as**: `hello_nepo.xml`

### Option B: Use Example Program
Create `hello_nepo.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <block type="robActions_display_text" id="2">
          <value name="OUT">
            <block type="text" id="3">
              <field name="TEXT">Hello NEPO!</field>
            </block>
          </value>
          <next>
            <block type="robControls_wait_time" id="4">
              <value name="TIME">
                <block type="math_number" id="5">
                  <field name="NUM">3000</field>
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

---

## Step 4: Run Your Program (2 minutes)

### Upload and Run
```bash
# Upload your program
nxj -upload hello_nepo.xml test_program.xml

# Run the interpreter
# On NXT: Files → NepoInterpreterMain → Enter
```

### What You Should See
1. **"NEPO Interpreter v1.0"** - Startup screen
2. **"Loading: test_program.xml"** - File loading
3. **"XML parsed OK"** - Successful parsing
4. **"Executing..."** - Program running
5. **"Hello NEPO!"** - Your program output!
6. **"Program finished"** - Completion message

---

## Step 5: Try More Examples

### Motor Test Program
Create `motor_test.xml`:
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

**Connect a motor to port B and run this program!**

### Sensor Test Program
Create `sensor_test.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <block type="robControls_repeat_times" id="2">
          <value name="TIMES">
            <block type="math_number" id="3">
              <field name="NUM">10</field>
            </block>
          </value>
          <statement name="DO">
            <block type="robActions_display_text" id="4">
              <value name="OUT">
                <block type="robSensors_touch_isPressed" id="5">
                  <field name="SENSORPORT">1</field>
                </block>
              </value>
              <next>
                <block type="robControls_wait_time" id="6">
                  <value name="TIME">
                    <block type="math_number" id="7">
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

**Connect a touch sensor to port 1 and try pressing it!**

---

## Common Issues & Solutions

### "nxjc command not found"
```bash
# Add leJOS to your PATH
export PATH=$PATH:/path/to/lejos_nxt/bin
# Or on Windows: Add to System PATH
```

### "NXT not found"
- **Check USB connection**
- **Ensure NXT is on**
- **Try different USB port**
- **Check leJOS firmware** is installed

### "Failed to parse XML"
- **Check XML syntax** - use online XML validator
- **Verify file was uploaded** correctly
- **Check file encoding** (should be UTF-8)

### "No start block found"
- **Every program needs** `robControls_start` block
- **Check spelling** exactly: "robControls_start"
- **Verify XML structure** matches examples

### "Unknown block type"
- **Check block name** against supported blocks
- **See documentation** for complete block list
- **Verify spelling** of block types

---

## Next Steps

### Explore More Blocks
Our interpreter supports 48 different block types:
- **Control blocks**: if/else, loops, wait
- **Motor blocks**: movement, speed control
- **Sensor blocks**: touch, ultrasonic, gyro, color
- **Logic blocks**: comparisons, boolean operations
- **Math blocks**: arithmetic, advanced functions
- **Text blocks**: string operations
- **Variable blocks**: data storage

### Create Complex Programs
Try building:
- **Line following robot**
- **Obstacle avoidance**
- **Remote control**
- **Maze solver**
- **Dancing robot**

### Use OpenRoberta Lab
- **Visual programming** - drag and drop blocks
- **Simulation** - test before deploying
- **Sharing** - export/import programs
- **Learning** - tutorials and examples

---

## Build Script for Easy Deployment

Create `deploy.sh` for one-command deployment:
```bash
#!/bin/bash
echo "🚀 NEPO Interpreter Deployment Script"

# Build
echo "📦 Building interpreter..."
rm -rf build
mkdir build
nxjc -cp src -d build src/*.java
cd build
jar cf NepoInterpreter.jar *.class
cd ..

# Upload interpreter
echo "📤 Uploading interpreter to NXT..."
nxj -cp build -upload build/NepoInterpreter.jar NepoInterpreterMain

# Upload example programs
echo "📤 Uploading example programs..."
nxj -upload examples/hello_nepo.xml test_program.xml
nxj -upload examples/motor_test.xml
nxj -upload examples/sensor_test.xml

echo "✅ Deployment complete!"
echo "🎯 Run 'NepoInterpreterMain' from NXT Files menu"
```

Make executable and use:
```bash
chmod +x deploy.sh
./deploy.sh
```

---

## Summary

### What You've Accomplished
- ✅ **Installed leJOS NXT** firmware
- ✅ **Built NEPO interpreter** from source
- ✅ **Created XML programs** for your robot
- ✅ **Deployed and ran** programs on real NXT hardware
- ✅ **Tested motors and sensors** with NEPO blocks

### What You Can Do Now
- **Create any NEPO program** in OpenRoberta Lab
- **Export and run** on your NXT
- **Use all 48 supported blocks** for complex behaviors
- **Build educational robotics** projects
- **Share programs** with other NXT users

### Key Commands to Remember
```bash
# Build interpreter
nxjc -cp src -d build src/*.java

# Upload interpreter
nxj -cp build -upload build/NepoInterpreter.jar NepoInterpreterMain

# Upload program
nxj -upload your_program.xml test_program.xml

# Run on NXT
# Files → NepoInterpreterMain → Enter
```

---

**🎉 Congratulations! Your NXT is now running OpenRoberta Lab NEPO programs! 🎉**

**Ready to build amazing robots with visual programming!** 🤖
