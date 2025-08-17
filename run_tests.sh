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
    
    # Compile test classes (basic compilation for mock environment)
    javac -cp . -d build *.java src/*.java 2>/dev/null || {
        echo "ℹ Note: Some test files require leJOS environment for full compilation"
        echo "ℹ Running available compiled tests..."
    }
fi

echo ""
echo "Running all tests..."
echo ""

# Run the main test suite
java -cp .:build NepoTestRunner "$@"

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
