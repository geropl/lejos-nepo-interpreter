#!/bin/bash

# Deployment script for NEPO Interpreter

echo "NEPO Interpreter Deployment"
echo "==========================="

# Build first
echo "Building..."
./compile.sh

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

# Check if NXT is connected
echo "Checking NXT connection..."
if ! command -v nxjupload &> /dev/null; then
    echo "Error: nxjupload not found. Please install leJOS NXT."
    exit 1
fi

echo "Uploading interpreter..."
nxjupload build/NepoInterpreter.jar

if [ $? -ne 0 ]; then
    echo "Failed to upload interpreter!"
    exit 1
fi

echo "Uploading test program..."
nxjupload test_program.xml

if [ $? -ne 0 ]; then
    echo "Failed to upload test program!"
    exit 1
fi

echo ""
echo "Deployment successful!"
echo ""
echo "On your NXT:"
echo "1. Navigate to Files"
echo "2. Run 'NepoInterpreter'"
echo "3. The program will automatically load test_program.xml"
echo ""
echo "To use your own NEPO program:"
echo "1. Export XML from Open Roberta Lab"
echo "2. Rename to test_program.xml"
echo "3. Run: nxjupload test_program.xml"
