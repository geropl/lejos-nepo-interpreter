#!/bin/bash

# Simple compilation script for NEPO Interpreter
# Requires leJOS NXT to be installed and in PATH

echo "NEPO Interpreter Build Script"
echo "============================="

# Check if leJOS tools are available
if ! command -v nxjc &> /dev/null; then
    echo "Error: nxjc not found. Please install leJOS NXT and add to PATH."
    exit 1
fi

# Create build directory
mkdir -p build/classes

echo "Compiling Java sources..."

# Compile all Java files
cd src
nxjc -d ../build/classes *.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

cd ..

echo "Creating JAR file..."

# Create JAR
cd build/classes
nxjjar -o ../NepoInterpreter.jar NepoInterpreterMain *.class

if [ $? -ne 0 ]; then
    echo "JAR creation failed!"
    exit 1
fi

cd ../..

echo "Build successful!"
echo "JAR file: build/NepoInterpreter.jar"
echo ""
echo "To upload to NXT:"
echo "  nxjupload build/NepoInterpreter.jar"
echo "  nxjupload test_program.xml"
echo ""
echo "Or run: ./deploy.sh"
