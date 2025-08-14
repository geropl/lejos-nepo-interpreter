#!/bin/bash

# Build script for Dynamic NEPO Program Runner
# This script compiles all components needed for dynamic file selection

set -e  # Exit on any error

echo "=========================================="
echo "Building Dynamic NEPO Program Runner"
echo "=========================================="

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

# Create build directory
echo ""
echo "Setting up build environment..."
rm -rf build
mkdir -p build

echo "✓ Build directory created"

# Check source files exist
echo ""
echo "Checking source files..."
REQUIRED_FILES=(
    "src/SimpleXMLParser.java"
    "src/RobotConfiguration.java"
    "src/NepoBlockExecutor.java"
    "src/ConfigurationBlockExecutor.java"
    "src/CrashLogger.java"
    "src/FilePicker.java"
    "AdvancedFilePicker.java"
    "src/NepoInterpreterMain.java"
    "DynamicNepoRunner.java"
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
nxjc -cp .:build -d build AdvancedFilePicker.java || { echo "ERROR: Failed to compile AdvancedFilePicker.java"; exit 1; }


echo ""
echo "Compiling main programs..."

echo "  → NepoInterpreterMain.java"
nxjc -cp .:build -d build src/NepoInterpreterMain.java || { echo "ERROR: Failed to compile NepoInterpreterMain.java"; exit 1; }

echo "  → DynamicNepoRunner.java"
nxjc -cp .:build -d build DynamicNepoRunner.java || { echo "ERROR: Failed to compile DynamicNepoRunner.java"; exit 1; }

echo ""
echo "Creating NXT executable files..."

# Create simple version
echo "  → Creating NepoSimple.nxj"
nxjlink -cp build -o NepoSimple.nxj NepoInterpreterMain || { echo "ERROR: Failed to create NepoSimple.nxj"; exit 1; }

# Create dynamic version with file picker
echo "  → Creating NepoDynamic.nxj"
nxjlink -cp build -o NepoDynamic.nxj DynamicNepoRunner || { echo "ERROR: Failed to create NepoDynamic.nxj"; exit 1; }

echo ""
echo "Verifying generated files..."
if [ -f "NepoSimple.nxj" ]; then
    echo "✓ NepoSimple.nxj ($(du -h NepoSimple.nxj | cut -f1))"
else
    echo "✗ NepoSimple.nxj not created"
    exit 1
fi

if [ -f "NepoDynamic.nxj" ]; then
    echo "✓ NepoDynamic.nxj ($(du -h NepoDynamic.nxj | cut -f1))"
else
    echo "✗ NepoDynamic.nxj not created"
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
echo "  NepoSimple.nxj  - Basic NEPO interpreter with file picker"
echo "  NepoDynamic.nxj - Full dynamic program runner with crash logging"
echo ""
echo "To upload to NXT:"
echo "  nxjupload NepoSimple.nxj"
echo "  nxjupload NepoDynamic.nxj"
echo ""
if [ -d "sample_programs" ]; then
    echo "To upload sample programs:"
    echo "  nxjupload sample_programs/*.xml"
    echo ""
fi
echo "Note: NXT must be connected via USB and turned on for upload."
echo "=========================================="
