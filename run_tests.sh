#!/bin/bash

# Test Runner for NEPO NXT Interpreter
# Runs the comprehensive test suite for NEPO block implementations

set -e  # Exit on any error

echo "=========================================="
echo "NEPO NXT Interpreter Test Suite"
echo "=========================================="

# Change to test directory
cd test

# Create test build directory
mkdir -p build

# Check if test classes are compiled
if [ ! -f "build/NepoTestRunner.class" ]; then
    echo "⚠ Test classes not found. Compiling tests..."
    
    # First compile the core interfaces and implementations from src
    echo "Compiling core XML classes from src..."
    javac -cp ../src -d build ../src/IXMLParser.java ../src/IXMLElement.java ../src/IString.java ../src/ShallowXMLParser.java ../src/ShallowXMLElement.java ../src/ShallowString.java 2>/dev/null || {
        echo "❌ Failed to compile core XML classes from src"
        exit 1
    }
    
    # Compile test-specific classes
    echo "Compiling test XML helper classes..."
    javac -cp ../src:build -d build TestXMLElement.java 2>/dev/null || {
        echo "❌ Failed to compile test XML helper classes"
        exit 1
    }
    
    # Then compile test framework classes
    echo "Compiling test framework..."
    javac -cp ../src:build:. -d build BlockTestCase.java TestDataManager.java NepoTestFramework.java NepoTestRunner.java TestResult.java TestReport.java TestSuite.java ExpectedResults.java ValidationRules.java MockHardware.java MockDisplay.java MockMotor.java MockSensor.java MockSound.java MockTimer.java MockNepoBlockExecutor.java MockHardwareTest.java 2>/dev/null || {
        echo "ℹ Note: Some test files require leJOS environment for full compilation"
        echo "ℹ Continuing with available compiled tests..."
    }
    
    # Try to compile remaining test files (may fail due to leJOS dependencies)
    echo "Compiling additional test files..."
    javac -cp ../src:build:. -d build *.java 2>/dev/null || {
        echo "ℹ Note: Some test files require leJOS environment and were skipped"
    }
fi

echo ""
echo "Running all tests..."
echo ""

# Run the main test suite
java -cp ../src:build:. NepoTestRunner "$@"

echo ""
echo "=========================================="
echo "Test execution complete!"
echo ""
echo "📊 Reports generated:"
echo "  • test/test-results.html  - Detailed HTML report"
echo "  • test/test-results.json  - Machine-readable results"
echo "  • test/test-results.txt   - Simple text summary"
echo ""
echo "💡 Run with options:"
echo "  ./run_tests.sh --help     - Show all options"
echo "  ./run_tests.sh -v         - Verbose output"
echo "  ./run_tests.sh -c unit    - Run unit tests only"
echo "=========================================="
