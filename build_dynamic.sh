#!/bin/bash

# Build script for Dynamic NEPO Program Runner
# This script compiles all components needed for dynamic file selection

echo "Building Dynamic NEPO Program Runner..."

# Set leJOS paths (adjust as needed)
export NXJ_HOME="/opt/lejos_nxj"
export PATH="$NXJ_HOME/bin:$PATH"

# Create build directory
mkdir -p build

echo "Compiling core components..."

# Compile in dependency order
nxjc -cp . -d build src/SimpleXMLParser.java
nxjc -cp .:build -d build src/RobotConfiguration.java
nxjc -cp .:build -d build src/NepoBlockExecutor.java
nxjc -cp .:build -d build src/ConfigurationBlockExecutor.java

echo "Compiling file picker components..."
nxjc -cp .:build -d build src/FilePicker.java
nxjc -cp .:build -d build AdvancedFilePicker.java

echo "Compiling main programs..."
nxjc -cp .:build -d build src/NepoInterpreterMain.java
nxjc -cp .:build -d build DynamicNepoRunner.java

echo "Creating JAR files..."

# Create simple version
nxjlink -cp build -o NepoSimple.nxj src.NepoInterpreterMain

# Create dynamic version with file picker
nxjlink -cp build -o NepoDynamic.nxj DynamicNepoRunner

echo "Build complete!"
echo ""
echo "Generated files:"
echo "  NepoSimple.nxj  - Basic NEPO interpreter"
echo "  NepoDynamic.nxj - Dynamic file selection version"
echo ""
echo "Upload to NXT with:"
echo "  nxjupload NepoSimple.nxj"
echo "  nxjupload NepoDynamic.nxj"
echo ""
echo "Copy sample programs:"
echo "  nxjupload sample_programs/*.xml"
