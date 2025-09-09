#!/bin/bash

# Build script for NEPO NXT Interpreter
# This script compiles all components needed for the NEPO interpreter
#
# Usage:
#   ./build.sh          - Production build
#   ./build.sh debug    - Debug build with remote console support

set -e  # Exit on any error

DEBUG_MODE=false
TARGET="target/default"
if [ "$1" = "debug" ]; then
    DEBUG_MODE=true
    TARGET="target/debug"
    echo "=========================================="
    echo "Building NEPO NXT Interpreter (DEBUG)"
    echo "=========================================="
else
    echo "=========================================="
    echo "Building NEPO NXT Interpreter"
    echo "=========================================="
fi

# Set Java 8 for leJOS NXJ compatibility
echo "Setting up Java environment..."
export JAVA_HOME="/usr/lib/jvm/java-8-openjdk-amd64"
export PATH="/usr/lib/jvm/java-8-openjdk-amd64/bin:$PATH"

# Verify leJOS installation
echo "Checking leJOS NXJ installation..."
if [ -z "$NXJ_HOME" ]; then
    export NXJ_HOME="/opt/lejos_nxj"
fi

if [ ! -d "$NXJ_HOME" ]; then
    echo "ERROR: leJOS NXJ not found at $NXJ_HOME"
    echo "Please ensure leJOS NXJ is properly installed."
    exit 1
fi

export PATH="$NXJ_HOME/bin:$PATH"

# Verify tools are available
echo "Verifying leJOS tools..."
if ! command -v nxjc &> /dev/null; then
    echo "ERROR: nxjc command not found"
    echo "PATH: $PATH"
    echo "NXJ_HOME: $NXJ_HOME"
    exit 1
fi

if ! command -v nxjlink &> /dev/null; then
    echo "ERROR: nxjlink command not found"
    exit 1
fi

echo "✓ leJOS NXJ tools found"
echo "✓ NXJ_HOME: $NXJ_HOME"

# Show Java version
echo "Java version:"
java -version

# Create build directories
echo ""
echo "Setting up build environment..."
rm -rf build $TARGET
mkdir -p build $TARGET

echo "✓ Build directory created"

# Check source files exist
echo ""
echo "Checking source files..."
REQUIRED_FILES=(
    "src/IString.java"
    "src/ShallowString.java"
    "src/IXMLElement.java"
    "src/ShallowXMLElement.java"
    "src/IXMLParser.java"
    "src/ShallowXMLParser.java"
    "src/IHardware.java"
    "src/ISensor.java"
    "src/IMotor.java"
    "src/NXTHardware.java"
    "src/RobotConfiguration.java"
    "src/ConfigurationBlockExecutor.java"
    "src/NepoBlockExecutor.java"
    "src/CrashLogger.java"
    "src/FilePicker.java"
    "src/AdvancedFilePicker.java"
    "src/DynamicNepoRunner.java"
)

for file in "${REQUIRED_FILES[@]}"; do
    if [ ! -f "$file" ]; then
        echo "ERROR: Required file not found: $file"
        exit 1
    fi
    echo "✓ $file"
done

echo ""
echo "Compiling core components in dependency order..."

# Compile interfaces first (in dependency order)
echo "  → IString.java"
nxjc -cp .:build -d build src/IString.java || { echo "ERROR: Failed to compile IString.java"; exit 1; }

echo "  → IXMLElement.java"
nxjc -cp .:build -d build src/IXMLElement.java || { echo "ERROR: Failed to compile IXMLElement.java"; exit 1; }

echo "  → IXMLParser.java"
nxjc -cp .:build -d build src/IXMLParser.java || { echo "ERROR: Failed to compile IXMLParser.java"; exit 1; }

echo "  → ISensor.java"
nxjc -cp .:build -d build src/ISensor.java || { echo "ERROR: Failed to compile ISensor.java"; exit 1; }

echo "  → IMotor.java"
nxjc -cp .:build -d build src/IMotor.java || { echo "ERROR: Failed to compile IMotor.java"; exit 1; }

echo "  → IHardware.java"
nxjc -cp .:build -d build src/IHardware.java || { echo "ERROR: Failed to compile IHardware.java"; exit 1; }

# Compile implementations
echo "  → ShallowString.java"
nxjc -cp .:build -d build src/ShallowString.java || { echo "ERROR: Failed to compile ShallowString.java"; exit 1; }

echo "  → ShallowXMLElement.java"
nxjc -cp .:build -d build src/ShallowXMLElement.java || { echo "ERROR: Failed to compile ShallowXMLElement.java"; exit 1; }

echo "  → ShallowXMLParser.java"
nxjc -cp .:build -d build src/ShallowXMLParser.java || { echo "ERROR: Failed to compile ShallowXMLParser.java"; exit 1; }

echo "  → RobotConfiguration.java"
nxjc -cp .:build -d build src/RobotConfiguration.java || { echo "ERROR: Failed to compile RobotConfiguration.java"; exit 1; }

echo "  → ConfigurationBlockExecutor.java"
nxjc -cp .:build -d build src/ConfigurationBlockExecutor.java || { echo "ERROR: Failed to compile ConfigurationBlockExecutor.java"; exit 1; }

echo "  → NXTHardware.java"
nxjc -cp .:build -d build src/NXTHardware.java || { echo "ERROR: Failed to compile NXTHardware.java"; exit 1; }

echo "  → NepoBlockExecutor.java"
nxjc -cp .:build -d build src/NepoBlockExecutor.java || { echo "ERROR: Failed to compile NepoBlockExecutor.java"; exit 1; }

echo "  → CrashLogger.java"
nxjc -cp .:build -d build src/CrashLogger.java || { echo "ERROR: Failed to compile CrashLogger.java"; exit 1; }

echo ""
echo "Compiling file picker components..."

echo "  → FilePicker.java"
nxjc -cp .:build -d build src/FilePicker.java || { echo "ERROR: Failed to compile FilePicker.java"; exit 1; }

echo "  → AdvancedFilePicker.java"
nxjc -cp .:build -d build src/AdvancedFilePicker.java || { echo "ERROR: Failed to compile AdvancedFilePicker.java"; exit 1; }


echo ""
echo "Compiling main program..."

echo "  → DynamicNepoRunner.java"
nxjc -cp .:build -d build src/DynamicNepoRunner.java || { echo "ERROR: Failed to compile DynamicNepoRunner.java"; exit 1; }

echo ""
echo "Creating NXT executable files..."

if [ "$DEBUG_MODE" = true ]; then
    echo "🐛 Creating DEBUG build with remote console support..."
    
    # Create dynamic version with debug info
    echo "  → Creating NepoInterpreter.nxj (DEBUG)"
    nxjlink -cp build -o $TARGET/NepoInterpreter.nxj -od $TARGET/NepoInterpreter.nxd -g -gr DynamicNepoRunner || { echo "ERROR: Failed to create NepoInterpreter.nxj"; exit 1; }
    
    echo "✓ Debug info file created:"
    echo "  → $TARGET/NepoInterpreter.nxd"
else
    echo "🏭 Creating PRODUCTION build..."
    
    # Create dynamic version with file picker
    echo "  → Creating NepoInterpreter.nxj"
    nxjlink -cp build -o $TARGET/NepoInterpreter.nxj DynamicNepoRunner || { echo "ERROR: Failed to create NepoInterpreter.nxj"; exit 1; }
fi

echo ""
echo "=========================================="
echo "BUILD SUCCESSFUL!"
echo "=========================================="
echo ""
echo "Generated files:"
echo "  $TARGET/NepoInterpreter.nxj - NEPO interpreter with dynamic file selection and crash logging"

if [ "$DEBUG_MODE" = true ]; then
    echo "  $TARGET/NepoInterpreter.nxd - Debug info for remote console"
fi

echo ""
echo "To upload to NXT:"
echo "  nxjupload $TARGET/NepoInterpreter.nxj"
echo ""

if [ "$DEBUG_MODE" = true ]; then
    echo "🐛 DEBUG MODE INSTRUCTIONS:"
    echo "1. Upload the .nxj file to NXT"
    echo "2. Upload XML programs (any size - no artificial limits)"
    echo "3. Start remote console for debugging:"
    echo "   nxjconsole -di $TARGET/NepoInterpreter.nxd"
    echo "4. Run the program on NXT: Files → NepoInterpreter"
    echo "5. View detailed debugging info on PC console"
    echo ""
    echo "🔍 Remote console will show:"
    echo "  - Proper exception class names"
    echo "  - Method names with line numbers"
    echo "  - Real-time program output"
    echo "  - OutOfMemoryError details if memory limits exceeded"
    echo ""
fi

if [ -d "sample_programs" ]; then
    echo "To upload sample programs:"
    echo "  nxjupload sample_programs/*.xml"
    echo ""
fi
echo "Note: NXT must be connected via USB and turned on for upload."
echo "=========================================="
