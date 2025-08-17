# NEPO Interpreter - Troubleshooting Guide

**Common issues and solutions for NXT deployment**

---

## Build Issues

### "nxjc command not found"

**Problem:** leJOS NXT tools are not in your PATH

**Solutions:**
```bash
# Check if leJOS is installed
ls /opt/lejos_nxt/bin/  # or wherever you installed it

# Add to PATH temporarily
export PATH=$PATH:/opt/lejos_nxt/bin

# Add to PATH permanently (Linux/Mac)
echo 'export PATH=$PATH:/opt/lejos_nxt/bin' >> ~/.bashrc
source ~/.bashrc

# Windows: Add to System PATH via Control Panel
```

**Verify fix:**
```bash
nxjc -version
nxj -version
```

### "Compilation failed" / Java errors

**Problem:** Source code compilation errors

**Check these:**
```bash
# Verify all source files exist
ls src/*.java

# Check Java syntax
nxjc -cp src src/NepoInterpreterMain.java

# Look for specific errors
nxjc -cp src -d build src/*.java 2>&1 | grep -i error
```

**Common fixes:**
- **Missing imports:** Add required import statements
- **Syntax errors:** Check brackets, semicolons, quotes
- **Java version:** Ensure Java 1.4-1.8 compatibility
- **File encoding:** Ensure UTF-8 encoding

### "Cannot create JAR file"

**Problem:** jar command fails

**Solutions:**
```bash
# Check if jar command exists
which jar

# Manual JAR creation
cd build
jar cf NepoInterpreter.jar *.class

# Alternative: Use zip
zip -r NepoInterpreter.jar *.class
```

---

## NXT Connection Issues

### "NXT not found" / "Cannot connect to NXT"

**Problem:** NXT is not detected by leJOS tools

**Check these:**
1. **USB Connection:**
   ```bash
   # Check USB devices (Linux)
   lsusb | grep -i lego
   
   # Check device manager (Windows)
   # Look for "LEGO NXT" or "Unknown Device"
   ```

2. **NXT Power:**
   - Ensure NXT is powered on
   - Check battery level
   - Try fresh batteries

3. **leJOS Firmware:**
   ```bash
   # Check if leJOS firmware is installed
   # NXT should show leJOS menu, not LEGO firmware
   
   # Reflash if needed
   nxjflash
   ```

4. **USB Drivers:**
   - **Windows:** Install leJOS USB drivers
   - **Linux:** Check udev rules
   - **Mac:** Usually works without additional drivers

**Test connection:**
```bash
# Simple connection test
nxj -r HelloWorld

# If this works, connection is OK
```

### "Permission denied" (Linux)

**Problem:** User doesn't have USB device permissions

**Solutions:**
```bash
# Add user to dialout group
sudo usermod -a -G dialout $USER

# Create udev rule for NXT
sudo nano /etc/udev/rules.d/70-lego-nxt.rules

# Add this line:
SUBSYSTEM=="usb", ATTR{idVendor}=="0694", ATTR{idProduct}=="0002", MODE="0666", GROUP="dialout"

# Reload udev rules
sudo udevadm control --reload-rules
sudo udevadm trigger

# Log out and back in for group changes
```

---

## Upload Issues

### "Upload failed" / "Cannot upload to NXT"

**Problem:** File upload to NXT fails

**Check these:**
1. **NXT Memory:**
   ```bash
   # Check available memory on NXT
   # Use NXT menu: System -> Memory
   
   # Delete old files if needed
   # Use NXT menu: Files -> Delete
   ```

2. **File Size:**
   ```bash
   # Check JAR file size
   ls -lh build/NepoInterpreter.jar
   
   # NXT has ~64KB limit for programs
   # If too large, optimize code
   ```

3. **Connection Stability:**
   - Try different USB cable
   - Use different USB port
   - Restart NXT and try again

**Manual upload test:**
```bash
# Try uploading a simple file
echo "test" > test.txt
nxj -upload test.txt

# If this works, connection is OK
```

### "File already exists"

**Problem:** File with same name exists on NXT

**Solutions:**
```bash
# Force overwrite
nxj -upload -f build/NepoInterpreter.jar NepoInterpreterMain

# Or delete from NXT first
# Use NXT menu: Files -> [filename] -> Delete
```

---

## Runtime Issues

### "Failed to parse XML file"

**Problem:** XML program cannot be loaded

**Check these:**
1. **File exists on NXT:**
   - Use NXT Files menu to verify
   - Check filename spelling exactly

2. **XML syntax:**
   ```bash
   # Validate XML syntax
   xmllint --noout your_program.xml
   
   # Check for common issues
   grep -i "robControls_start" your_program.xml
   ```

3. **File encoding:**
   ```bash
   # Check file encoding
   file your_program.xml
   
   # Should show: UTF-8 Unicode text
   ```

4. **XML structure:**
   - Must have `<blockSet robottype="nxt">`
   - Must have `robControls_start` block
   - Check for proper nesting

**Test with minimal XML:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<blockSet robottype="nxt">
  <instance x="384" y="50">
    <block type="robControls_start" id="1">
      <statement name="ST">
        <block type="robActions_display_text" id="2">
          <value name="OUT">
            <block type="text" id="3">
              <field name="TEXT">Test</field>
            </block>
          </value>
        </block>
      </statement>
    </block>
  </instance>
</blockSet>
```

### "No start block found"

**Problem:** XML doesn't contain proper start block

**Solutions:**
1. **Check block type:**
   ```bash
   grep "robControls_start" your_program.xml
   ```

2. **Verify XML structure:**
   - Must have `<instance>` element
   - Must have `<block type="robControls_start">`
   - Check for typos in block type

3. **Use OpenRoberta Lab:**
   - Always start with "start" block
   - Export XML properly

### "Unknown block type"

**Problem:** Program uses unsupported blocks

**Check supported blocks:**
```bash
# See what blocks are implemented
grep "equals(blockType)" src/NepoBlockExecutor.java
```

**Our supported blocks:**
- **Control:** robControls_start, robControls_if, robControls_while, etc.
- **Actions:** robActions_motor_on, robActions_display_text, etc.
- **Sensors:** robSensors_touch_isPressed, robSensors_ultrasonic_distance, etc.
- **Logic:** logic_boolean, logic_compare, logic_operation
- **Math:** math_number, math_arithmetic, math_single
- **Text:** text, text_join
- **Variables:** variables_get, variables_set
- **Config:** robBrick_EV3-Brick, robBrick_motor_big, etc.

### "Motor/Sensor not working"

**Problem:** Hardware doesn't respond

**Check these:**
1. **Physical connections:**
   - Verify cables are secure
   - Check port assignments (A,B,C for motors; 1,2,3,4 for sensors)
   - Test hardware with LEGO firmware

2. **Port configuration:**
   ```xml
   <!-- Motor on port B -->
   <field name="MOTORPORT">B</field>
   
   <!-- Sensor on port 1 -->
   <field name="SENSORPORT">1</field>
   ```

3. **Hardware compatibility:**
   - Use NXT-compatible sensors/motors
   - Check voltage levels
   - Verify sensor types match code

**Test hardware:**
```bash
# Use simple test programs
nxj -cp build -r NepoInterpreterMain examples/motor_test.xml
nxj -cp build -r NepoInterpreterMain examples/sensor_test.xml
```

---

## Memory Issues

### "Out of memory" / Program crashes

**Problem:** NXT runs out of memory

**Solutions:**
1. **Reduce program size:**
   - Simplify XML programs
   - Remove unused blocks
   - Use shorter text strings

2. **Optimize code:**
   - Remove debug output
   - Use efficient data structures
   - Minimize object creation

3. **Clear NXT memory:**
   - Delete old files from NXT
   - Use NXT menu: System -> Format (last resort)

**Check memory usage:**
```bash
# Check JAR file size
ls -lh build/NepoInterpreter.jar

# Typical sizes:
# < 20KB: Good
# 20-40KB: OK
# > 40KB: May have issues
```

---

## Performance Issues

### "Program runs slowly"

**Problem:** Execution is sluggish

**Solutions:**
1. **Reduce complexity:**
   - Simplify loops
   - Reduce sensor polling frequency
   - Minimize display updates

2. **Optimize XML:**
   - Remove unnecessary blocks
   - Use efficient algorithms
   - Avoid deep nesting

3. **Hardware factors:**
   - Check battery level
   - Ensure good connections
   - Reduce mechanical load

### "Display updates slowly"

**Problem:** Screen refresh is slow

**Solutions:**
```java
// In your programs, reduce display frequency
<block type="robControls_wait_time">
  <value name="TIME">
    <block type="math_number">
      <field name="NUM">200</field>  <!-- Increase delay -->
    </block>
  </value>
</block>
```

---

## Debugging Techniques

### Add Debug Output

**Add display blocks to track execution:**
```xml
<block type="robActions_display_text">
  <value name="OUT">
    <block type="text">
      <field name="TEXT">Debug: Step 1</field>
    </block>
  </value>
</block>
```

### Use Simple Test Programs

**Start with minimal programs and add complexity:**
1. Hello World (display only)
2. Simple motor test
3. Basic sensor test
4. Combine features gradually

### Check NXT System Info

**Use NXT menus:**
- **System → Memory:** Check available space
- **System → Battery:** Check power level
- **Files:** Verify uploaded files
- **Settings:** Check system configuration

### Enable Verbose Output

**Modify NepoInterpreterMain.java for debugging:**
```java
// Add debug output
LCD.drawString("Debug: " + message, 0, line);
LCD.refresh();
Delay.msDelay(1000);
```

---

## Getting Help

### Check Log Files

**Look for error messages:**
```bash
# Check build output
./build.sh 2>&1 | tee build.log

# Check upload output
nxj -upload program.xml 2>&1 | tee upload.log
```

### Test with Known Good Programs

**Use our example programs:**
```bash
# Test basic functionality
nxj -cp build -r NepoInterpreterMain examples/hello_world.xml

# Test hardware
nxj -cp build -r NepoInterpreterMain examples/motor_test.xml
```

### Verify Installation

**Complete system check:**
```bash
# Check leJOS installation
nxjc -version
nxj -version

# Check NXT connection
nxj -r HelloWorld

# Check build process
./build.sh

# Check example programs
ls examples/*.xml
```

---

## Emergency Recovery

### NXT Won't Respond

**If NXT becomes unresponsive:**
1. **Remove batteries** for 10 seconds
2. **Reinstall batteries**
3. **Reflash leJOS firmware:** `nxjflash`
4. **Test with simple program**

### Corrupted Files

**If files are corrupted:**
1. **Delete all files** from NXT
2. **Format NXT** (System → Format)
3. **Reflash firmware** if needed
4. **Rebuild and redeploy**

### Complete Reset

**Nuclear option - start fresh:**
```bash
# Clean everything
rm -rf build
rm examples/*.xml

# Rebuild from scratch
./build.sh
./deploy.sh

# Test with minimal program
```

---

## Prevention Tips

### Best Practices

1. **Always test incrementally**
2. **Keep backups of working programs**
3. **Use version control for your XML files**
4. **Test on actual hardware regularly**
5. **Keep NXT batteries charged**
6. **Use quality USB cables**

### Regular Maintenance

```bash
# Weekly checks
nxj -version                    # Verify leJOS works
./build.sh                      # Verify build works
nxj -r HelloWorld              # Verify NXT connection

# Clean up old files
rm -rf build
mkdir build
```

**🔧 Most issues can be resolved by checking connections, verifying file syntax, and ensuring proper leJOS installation! 🔧**
