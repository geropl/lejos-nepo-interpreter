# Simple Debugging Guide for NEPO NXT Interpreter

This guide explains how to debug crashes when running the NEPO interpreter on real NXT hardware.

## How It Works

When your program crashes **anywhere** (startup, file selection, XML parsing, program execution), you get:

1. **Useful error message on NXT screen** - Shows context, exception type, and error message
2. **Complete stack trace in crash.log file** - Full details for analysis on PC

The entire main method is wrapped in error handling, so crashes during:
- Program startup and initialization
- File picker operation and file selection  
- XML parsing and validation
- Block execution and program running
- All other operations

...are automatically caught and logged.

## What You See on NXT Screen

Instead of cryptic error messages, you get clear information:

```
ERROR #1
Main Execution
NullPointerException
Object is null

Logged to
crash.log
Press any key
```

This tells you:
- **ERROR #1** - This is the first crash
- **Main Execution** - Where the crash happened (context)
- **NullPointerException** - What type of error occurred
- **Object is null** - The specific error message
- **Logged to crash.log** - Complete details saved to file

## Getting the Complete Stack Trace

The crash.log file contains everything you need for debugging:

```
=====================================
CRASH #1 - Mon Jan 15 14:30:22 2024
Context: Main Execution
=====================================
Exception: java.lang.NullPointerException
Message: Object is null

Stack Trace:
java.lang.NullPointerException
    at NepoBlockExecutor.executeBlock(NepoBlockExecutor.java:45)
    at NepoInterpreterMain.main(NepoInterpreterMain.java:67)

System Information:
  Total Memory: 65536 bytes
  Free Memory: 32768 bytes
  Used Memory: 32768 bytes
  Java Version: 1.7.0
  Java Vendor: leJOS
=====================================
```

## How to Get the Log File

### Option 1: nxjcontrol (Recommended)
1. Run `nxjcontrol` on your PC
2. Connect to your NXT
3. Use the file browser to download `crash.log`
4. Open the file in any text editor

### Option 2: Command Line
```bash
nxjdownload crash.log
```

## Common Debugging Scenarios

### Scenario 1: Program Won't Start
**Screen shows**: Error during initialization
**Check**: Look for file system or memory issues in crash.log

### Scenario 2: Crash When Selecting XML File  
**Screen shows**: Error in file picker or XML parsing
**Check**: Verify your XML file is valid NEPO format

### Scenario 3: Crash During Program Execution
**Screen shows**: Error in block execution
**Check**: Stack trace shows which block type caused the issue

### Scenario 4: Memory Problems
**Screen shows**: OutOfMemoryError or similar
**Check**: System Information section in crash.log for memory usage

## Reading Stack Traces

The stack trace shows exactly where the error occurred:

```
at NepoBlockExecutor.executeBlock(NepoBlockExecutor.java:45)
at NepoInterpreterMain.main(NepoInterpreterMain.java:67)
```

This means:
- Error happened in `NepoBlockExecutor.executeBlock()` at line 45
- Which was called from `NepoInterpreterMain.main()` at line 67

## Tips for Effective Debugging

1. **Always check the screen message first** - It gives you the key information
2. **Download crash.log after each crash** - Don't let multiple crashes accumulate
3. **Look at the stack trace** - The top line usually shows the real problem
4. **Check system information** - Memory issues are common on NXT
5. **Test with simple XML files first** - Isolate complex program issues

## Example Debugging Session

```bash
# 1. Upload and test your program
nxjupload NepoDynamic.nxj
nxjupload my_program.xml

# 2. Run program on NXT - it crashes
# NXT screen shows: "ERROR #1, XML Parsing, NullPointerException"

# 3. Download the crash log
nxjcontrol  # Use GUI to get crash.log

# 4. Open crash.log and see:
# "at SimpleXMLParser.parseFile(SimpleXMLParser.java:123)"

# 5. Fix the null check in XML parser at line 123
# 6. Rebuild and test again
./build_dynamic.sh
nxjupload NepoDynamic.nxj
```

## Summary

The simplified debugging system gives you:

✅ **Clear error messages** on NXT screen with context and error type  
✅ **Complete stack traces** in crash.log file with line numbers  
✅ **System information** including memory usage  
✅ **Easy file transfer** using nxjcontrol GUI  
✅ **No complex tools** - just useful error messages and complete logs  

This makes debugging NXT programs much more straightforward than dealing with cryptic system errors.
