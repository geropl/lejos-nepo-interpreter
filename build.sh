#!/bin/bash

# NEPO Interpreter Build Script for leJOS NXT
# This script compiles and packages the NEPO interpreter for deployment

echo "🚀 NEPO Interpreter Build Script"
echo "================================="

# Check if leJOS NXT is installed
if ! command -v nxjc &> /dev/null; then
    echo "❌ ERROR: nxjc not found!"
    echo "Please install leJOS NXT and add it to your PATH"
    echo "Download from: http://www.lejos.org/nxt.php"
    exit 1
fi

echo "✅ leJOS NXT found: $(nxjc -version 2>&1 | head -1)"

# Check if source files exist
if [ ! -d "src" ]; then
    echo "❌ ERROR: src directory not found!"
    echo "Please run this script from the project root directory"
    exit 1
fi

echo "✅ Source directory found"

# Count source files
SRC_COUNT=$(find src -name "*.java" | wc -l)
echo "📁 Found $SRC_COUNT Java source files"

# Clean previous build
echo "🧹 Cleaning previous build..."
rm -rf build
mkdir -p build

# Compile Java sources
echo "🔨 Compiling Java sources..."
nxjc -cp src -d build src/*.java

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    echo "Please check your Java source files for errors"
    exit 1
fi

echo "✅ Compilation successful"

# Count compiled classes
CLASS_COUNT=$(find build -name "*.class" | wc -l)
echo "📦 Generated $CLASS_COUNT class files"

# Create JAR file
echo "📦 Creating JAR file..."
cd build
jar cf NepoInterpreter.jar *.class

if [ $? -ne 0 ]; then
    echo "❌ JAR creation failed!"
    exit 1
fi

cd ..
echo "✅ JAR file created: build/NepoInterpreter.jar"

# Check JAR contents
JAR_SIZE=$(du -h build/NepoInterpreter.jar | cut -f1)
echo "📊 JAR file size: $JAR_SIZE"

# Verify main class exists
if [ ! -f "build/NepoInterpreterMain.class" ]; then
    echo "❌ ERROR: NepoInterpreterMain.class not found!"
    echo "Main class is required for NXT execution"
    exit 1
fi

echo "✅ Main class verified: NepoInterpreterMain"

# List all compiled classes
echo "📋 Compiled classes:"
ls -1 build/*.class | sed 's/build\///g' | sed 's/\.class//g' | sort

echo ""
echo "🎉 Build completed successfully!"
echo "📁 Output: build/NepoInterpreter.jar"
echo ""
echo "Next steps:"
echo "1. Connect your NXT via USB"
echo "2. Upload interpreter: nxj -cp build -upload build/NepoInterpreter.jar NepoInterpreterMain"
echo "3. Upload XML program: nxj -upload your_program.xml test_program.xml"
echo "4. Run on NXT: Files → NepoInterpreterMain"
echo ""
echo "🚀 Ready for deployment!"
