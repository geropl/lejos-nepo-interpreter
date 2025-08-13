# leJOS NXJ Setup for NEPO NXT Interpreter

This document explains how to set up the leJOS NXJ development environment for building and deploying the NEPO NXT Interpreter with dynamic file selection.

## Overview

The development environment includes:
- **leJOS NXJ 0.9.1beta-3** - Java development platform for LEGO NXT
- **Java 8 JDK** - Required for leJOS NXJ compatibility
- **Build tools** - Scripts for compiling and linking NXT programs
- **USB support** - For uploading programs to NXT (when available)

## Dev Container Setup

The project includes a complete dev container configuration that automatically installs leJOS NXJ.

### Files
- `.devcontainer/Dockerfile` - Custom image with leJOS NXJ installation
- `.devcontainer/devcontainer.json` - Container configuration with environment variables

### Automatic Installation
When you open this project in a dev container, it will:
1. Install Java 8 JDK
2. Download leJOS NXJ 0.9.1beta-3 from SourceForge
3. Set up environment variables (`NXJ_HOME`, `PATH`)
4. Create symbolic links for easy access to tools
5. Configure USB permissions for NXT access

## Manual Installation (Alternative)

If you need to install leJOS NXJ manually:

### Prerequisites
```bash
# Install Java 8 JDK
sudo apt-get update
sudo apt-get install openjdk-8-jdk

# Install USB libraries
sudo apt-get install libusb-0.1-4 libusb-dev
```

### Download and Install leJOS NXJ
```bash
# Create installation directory
sudo mkdir -p /opt/lejos_nxj

# Download leJOS NXJ
cd /tmp
wget https://sourceforge.net/projects/nxt.lejos.p/files/0.9.1beta-3/leJOS_NXJ_0.9.1beta-3.tar.gz/download -O leJOS_NXJ_0.9.1beta-3.tar.gz

# Extract and install
tar -xzf leJOS_NXJ_0.9.1beta-3.tar.gz
sudo mv leJOS_NXJ_0.9.1beta-3/* /opt/lejos_nxj/
sudo chmod +x /opt/lejos_nxj/bin/*

# Set up environment
export NXJ_HOME=/opt/lejos_nxj
export PATH="$NXJ_HOME/bin:$PATH"
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

### Add to Shell Profile
```bash
# Add to ~/.bashrc or ~/.profile
echo 'export NXJ_HOME=/opt/lejos_nxj' >> ~/.bashrc
echo 'export PATH="$NXJ_HOME/bin:$PATH"' >> ~/.bashrc
echo 'export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

## Environment Verification

You can verify your installation by running the build script:

```bash
./build_dynamic.sh
```

If the environment is properly configured, you should see:
```
==========================================
Building Dynamic NEPO Program Runner
==========================================
Setting up Java environment...
Checking leJOS NXJ installation...
Verifying leJOS tools...
✓ leJOS NXJ tools found
✓ NXJ_HOME: /opt/lejos_nxj
...
BUILD SUCCESSFUL!
==========================================
```

## leJOS NXJ Tools

### Core Commands

#### `nxjc` - NXT Java Compiler
Compiles Java source files for the NXT platform.
```bash
# Basic compilation
nxjc MyProgram.java

# With classpath
nxjc -cp .:lib -d build src/MyProgram.java

# Multiple files
nxjc -cp . -d build src/*.java
```

#### `nxjlink` - NXT Linker
Links compiled classes into NXT executable (.nxj) files.
```bash
# Create executable
nxjlink -cp build -o MyProgram.nxj MyProgram

# With specific main class
nxjlink -cp build -o MyProgram.nxj com.example.MyProgram
```

#### `nxjupload` - NXT Upload Tool
Uploads programs and files to the NXT brick.
```bash
# Upload program
nxjupload MyProgram.nxj

# Upload data file
nxjupload data.xml

# Upload to specific NXT (if multiple connected)
nxjupload -n MyNXT MyProgram.nxj
```

#### `nxj` - Direct Run Tool
Compiles, links, and uploads in one command.
```bash
# Compile and run immediately
nxj MyProgram.java

# With options
nxj -cp lib MyProgram.java
```

## Building the NEPO Interpreter

### Quick Build
```bash
# Make build script executable
chmod +x build_dynamic.sh

# Build all components
./build_dynamic.sh
```

### Manual Build Steps
```bash
# Create build directory
mkdir -p build

# Compile core components
nxjc -cp . -d build src/SimpleXMLParser.java
nxjc -cp .:build -d build src/RobotConfiguration.java
nxjc -cp .:build -d build src/NepoBlockExecutor.java
nxjc -cp .:build -d build src/ConfigurationBlockExecutor.java

# Compile file pickers
nxjc -cp .:build -d build src/FilePicker.java
nxjc -cp .:build -d build AdvancedFilePicker.java

# Compile main programs
nxjc -cp .:build -d build src/NepoInterpreterMain.java
nxjc -cp .:build -d build DynamicNepoRunner.java

# Create executables
nxjlink -cp build -o NepoSimple.nxj src.NepoInterpreterMain
nxjlink -cp build -o NepoDynamic.nxj DynamicNepoRunner
```

## NXT Connection and Upload

### USB Connection
1. Connect NXT to computer via USB cable
2. Turn on NXT brick
3. Ensure NXT is in normal mode (not firmware update mode)

### Upload Programs
```bash
# Upload the dynamic program runner
nxjupload NepoDynamic.nxj

# Upload sample XML programs
nxjupload sample_programs/simple_move.xml
nxjupload sample_programs/sound_test.xml
nxjupload sample_programs/sensor_demo.xml
```

### Verify Upload
On the NXT:
1. Navigate to "Files" menu
2. You should see the uploaded .nxj and .xml files
3. Run "NepoDynamic" to start the dynamic program runner

## Troubleshooting

### Common Issues

#### "nxjc: command not found"
```bash
# Check environment variables
echo $NXJ_HOME
echo $PATH

# Verify installation
ls -la /opt/lejos_nxj/bin/

# Re-source environment
source ~/.bashrc
```

#### "Java version incompatible"
```bash
# Check Java version (must be Java 8)
java -version

# Set correct JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
```

#### "USB device not found"
```bash
# Check USB connection
lsusb | grep -i lego

# Check permissions
ls -la /dev/bus/usb/

# Add user to plugdev group
sudo usermod -a -G plugdev $USER
```

#### "Compilation errors"
```bash
# Check classpath
echo $CLASSPATH

# Verify source files exist
find . -name "*.java" -type f

# Clean build directory
rm -rf build && mkdir build
```

### Debug Mode

Enable verbose output for troubleshooting:
```bash
# Verbose compilation
nxjc -verbose -cp . -d build src/MyProgram.java

# Debug linking
nxjlink -verbose -cp build -o MyProgram.nxj MyProgram
```

## Development Workflow

### Typical Development Cycle
1. **Edit** Java source files
2. **Build** using `./build_dynamic.sh` (includes verification)
3. **Upload** .nxj files to NXT
4. **Test** programs on NXT
5. **Debug** and repeat

### File Organization
```
project/
├── src/                    # Java source files
├── sample_programs/        # XML test programs
├── build/                  # Compiled classes (generated)
├── *.nxj                  # NXT executables (generated)
└── build_dynamic.sh       # Build script
```

### Best Practices
1. **Use build script** for consistent compilation
2. **Keep source organized** in src/ directory
3. **Version control** .java files, not .class or .nxj
4. **Test on actual NXT** hardware when possible
5. **Check build output** for any warnings or errors

## Advanced Configuration

### Custom leJOS Installation
If you need a different leJOS version or location:

```bash
# Set custom paths
export NXJ_HOME=/path/to/custom/lejos
export PATH="$NXJ_HOME/bin:$PATH"

# Update build script
sed -i 's|/opt/lejos_nxj|/path/to/custom/lejos|g' build_dynamic.sh
```

### Multiple NXT Support
```bash
# List connected NXTs
nxjbrowse

# Upload to specific NXT
nxjupload -n "MyNXT" program.nxj

# Set default NXT
export NXJ_DEFAULT_NXT="MyNXT"
```

### Memory Optimization
For large programs, optimize memory usage:
```bash
# Link with memory optimization
nxjlink -cp build -o program.nxj -od MyProgram

# Check program size
ls -lh *.nxj
```

This setup provides a complete development environment for creating and deploying NEPO NXT programs with dynamic file selection capabilities.
