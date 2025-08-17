#!/bin/bash

# Build script for Dynamic NEPO Program Runner
# This script compiles all components needed for dynamic file selection
#
# Usage:
#   ./build_dynamic.sh          - Production build
#   ./build_dynamic.sh debug    - Debug build with remote console support

set -e  # Exit on any error

DEBUG_MODE=false
if [ "$1" = "debug" ]; then
    DEBUG_MODE=true
    echo "=========================================="
    echo "Building Dynamic NEPO Program Runner (DEBUG)"
    echo "=========================================="
else
    echo "=========================================="
    echo "Building Dynamic NEPO Program Runner"
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
rm -rf build target
mkdir -p build target

echo "✓ Build directory created"

# Check source files exist
echo ""
echo "Checking source files..."
REQUIRED_FILES=(
    "src/SimpleXMLParser.java"
    "src/ShallowString.java"
    "src/ShallowXMLElement.java"
    "src/ShallowXMLParser.java"
    "src/RobotConfiguration.java"
    "src/NepoBlockExecutor.java"
    "src/ConfigurationBlockExecutor.java"
    "src/CrashLogger.java"
    "src/FilePicker.java"
    "src/AdvancedFilePicker.java"
    "src/NepoInterpreterMain.java"
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
echo "Compiling utility classes first..."

echo "  → CrashLogger.java"
nxjc -cp . -d build src/CrashLogger.java || { echo "ERROR: Failed to compile CrashLogger.java"; exit 1; }

echo ""
echo "Compiling core components..."

# Compile in dependency order with error checking
echo "  → SimpleXMLParser.java"
nxjc -cp .:build -d build src/SimpleXMLParser.java || { echo "ERROR: Failed to compile SimpleXMLParser.java"; exit 1; }

# Compile shallow XML components (depends on SimpleXMLParser)
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

echo "  → NepoBlockExecutor.java"
nxjc -cp .:build -d build src/NepoBlockExecutor.java || { echo "ERROR: Failed to compile NepoBlockExecutor.java"; exit 1; }

echo ""
echo "Compiling file picker components..."

echo "  → FilePicker.java"
nxjc -cp .:build -d build src/FilePicker.java || { echo "ERROR: Failed to compile FilePicker.java"; exit 1; }

echo "  → AdvancedFilePicker.java"
nxjc -cp .:build -d build src/AdvancedFilePicker.java || { echo "ERROR: Failed to compile AdvancedFilePicker.java"; exit 1; }


echo ""
echo "Compiling main programs..."

echo "  → NepoInterpreterMain.java"
nxjc -cp .:build -d build src/NepoInterpreterMain.java || { echo "ERROR: Failed to compile NepoInterpreterMain.java"; exit 1; }

echo "  → DynamicNepoRunner.java"
nxjc -cp .:build -d build src/DynamicNepoRunner.java || { echo "ERROR: Failed to compile DynamicNepoRunner.java"; exit 1; }

echo ""
echo "Creating NXT executable files..."

if [ "$DEBUG_MODE" = true ]; then
    echo "🐛 Creating DEBUG builds with remote console support..."
    
    # Create simple version with debug info
    echo "  → Creating NepoSimple.nxj (DEBUG)"
    nxjlink -cp build -o target/NepoSimple.nxj -od target/NepoSimple.nxd -g -gr NepoInterpreterMain || { echo "ERROR: Failed to create NepoSimple.nxj"; exit 1; }
    
    # Create dynamic version with debug info
    echo "  → Creating NepoDynamic.nxj (DEBUG)"
    nxjlink -cp build -o target/NepoDynamic.nxj -od target/NepoDynamic.nxd -g -gr DynamicNepoRunner || { echo "ERROR: Failed to create NepoDynamic.nxj"; exit 1; }
    
    echo "✓ Debug info files created:"
    echo "  → target/NepoSimple.nxd"
    echo "  → target/NepoDynamic.nxd"
else
    echo "🏭 Creating PRODUCTION builds..."
    
    # Create simple version
    echo "  → Creating NepoSimple.nxj"
    nxjlink -cp build -o target/NepoSimple.nxj NepoInterpreterMain || { echo "ERROR: Failed to create NepoSimple.nxj"; exit 1; }
    
    # Create dynamic version with file picker
    echo "  → Creating NepoDynamic.nxj"
    nxjlink -cp build -o target/NepoDynamic.nxj DynamicNepoRunner || { echo "ERROR: Failed to create NepoDynamic.nxj"; exit 1; }
fi

echo ""
echo "Verifying generated files..."
if [ -f "target/NepoSimple.nxj" ]; then
    echo "✓ target/NepoSimple.nxj ($(du -h target/NepoSimple.nxj | cut -f1))"
else
    echo "✗ target/NepoSimple.nxj not created"
    exit 1
fi

if [ -f "target/NepoDynamic.nxj" ]; then
    echo "✓ target/NepoDynamic.nxj ($(du -h target/NepoDynamic.nxj | cut -f1))"
else
    echo "✗ target/NepoDynamic.nxj not created"
    exit 1
fi

# Check for sample programs
echo ""
echo "Checking sample programs..."
if [ -d "sample_programs" ]; then
    SAMPLE_COUNT=$(find sample_programs -name "*.xml" | wc -l)
    echo "✓ Found $SAMPLE_COUNT sample XML programs"
    find sample_programs -name "*.xml" -exec echo "  → {}" \;
else
    echo "⚠ No sample_programs directory found"
fi

echo ""
echo "=========================================="
echo "BUILD SUCCESSFUL!"
echo "=========================================="
echo ""
echo "Generated files:"
echo "  target/NepoSimple.nxj  - Basic NEPO interpreter with file picker"
echo "  target/NepoDynamic.nxj - Full dynamic program runner with crash logging"

if [ "$DEBUG_MODE" = true ]; then
    echo "  target/NepoSimple.nxd  - Debug info for NepoSimple"
    echo "  target/NepoDynamic.nxd - Debug info for NepoDynamic"
fi

echo ""
echo "To upload to NXT:"
echo "  nxjupload target/NepoSimple.nxj"
echo "  nxjupload target/NepoDynamic.nxj"
echo ""

if [ "$DEBUG_MODE" = true ]; then
    echo "🐛 DEBUG MODE INSTRUCTIONS:"
    echo "1. Upload the .nxj files to NXT"
    echo "2. Upload XML programs (any size - no artificial limits)"
    echo "3. Start remote console for debugging:"
    echo "   nxjconsole -di target/NepoSimple.nxd    # For NepoSimple debugging"
    echo "   nxjconsole -di target/NepoDynamic.nxd   # For NepoDynamic debugging"
    echo "4. Run the program on NXT"
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
