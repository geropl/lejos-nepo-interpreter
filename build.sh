#!/bin/bash

# NEPO Interpreter Build Script for leJOS NXT
# This script compiles and packages the NEPO interpreter for deployment
#
# Usage:
#   ./build.sh          - Production build
#   ./build.sh debug    - Debug build with remote console support

DEBUG_MODE=false
if [ "$1" = "debug" ]; then
    DEBUG_MODE=true
    echo "🐛 NEPO Interpreter DEBUG Build Script"
    echo "======================================"
else
    echo "🚀 NEPO Interpreter Build Script"
    echo "================================="
fi

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

# Link NXJ binary
echo "🔗 Linking NXJ binary..."
cd build

if [ "$DEBUG_MODE" = true ]; then
    echo "🐛 Creating DEBUG build with remote console support..."
    nxjlink -o NepoInterpreter.nxj -od NepoInterpreter.nxd -g -gr -cp . NepoInterpreterMain
    
    if [ $? -ne 0 ]; then
        echo "❌ Debug linking failed!"
        exit 1
    fi
    
    echo "✅ Debug build created with debug info: build/NepoInterpreter.nxd"
    echo "🔍 Use 'nxjconsole -di build/NepoInterpreter.nxd' for remote debugging"
else
    echo "🏭 Creating PRODUCTION build..."
    nxjlink -o NepoInterpreter.nxj -cp . NepoInterpreterMain
    
    if [ $? -ne 0 ]; then
        echo "❌ Production linking failed!"
        exit 1
    fi
    
    echo "✅ Production build created"
fi

cd ..
echo "✅ NXJ binary created: build/NepoInterpreter.nxj"

# Check NXJ binary
NXJ_SIZE=$(du -h build/NepoInterpreter.nxj | cut -f1)
echo "📊 NXJ binary size: $NXJ_SIZE"

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
echo "📁 Output: build/NepoInterpreter.nxj"

if [ "$DEBUG_MODE" = true ]; then
    echo "🐛 Debug info: build/NepoInterpreter.nxd"
    echo ""
    echo "Debug deployment steps:"
    echo "1. Connect your NXT via USB"
    echo "2. Upload binary: nxjupload build/NepoInterpreter.nxj"
    echo "3. Upload XML program: nxjupload test_program.xml"
    echo "4. Start remote console: nxjconsole -di build/NepoInterpreter.nxd"
    echo "5. Run program on NXT: Files → NepoInterpreter"
    echo ""
    echo "🔍 Remote console will show proper exception names and line numbers!"
else
    echo ""
    echo "Production deployment steps:"
    echo "1. Connect your NXT via USB"
    echo "2. Upload binary: nxjupload build/NepoInterpreter.nxj"
    echo "3. Upload XML program: nxjupload test_program.xml"
    echo "4. Run on NXT: Files → NepoInterpreter"
fi

echo ""
echo "🚀 Ready for deployment!"
