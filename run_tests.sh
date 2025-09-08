#!/bin/bash

# Test Runner for NEPO NXT Interpreter
# Runs all tests following the Test... naming convention

set -e  # Exit on any error

echo "=========================================="
echo "NEPO NXT Interpreter Test Suite"
echo "=========================================="
echo ""

# Function to show usage
show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  -h, --help     Show this help message"
    echo "  -v, --verbose  Verbose output"
    echo "  -u, --unit     Run unit tests only"
    echo "  -i, --integration  Run integration tests only"
    echo "  --clean        Clean build artifacts before running"
    echo ""
    echo "Test Organization:"
    echo "  • All test classes are named Test..."
    echo "  • Unit tests are in test/unit/"
    echo "  • Integration tests are in test/integration/"
    echo "  • Test build artifacts go to test/build/"
    echo ""
}

# Parse command line arguments
VERBOSE=false
UNIT_ONLY=false
INTEGRATION_ONLY=false
CLEAN=false

while [[ $# -gt 0 ]]; do
    case $1 in
        -h|--help)
            show_usage
            exit 0
            ;;
        -v|--verbose)
            VERBOSE=true
            shift
            ;;
        -u|--unit)
            UNIT_ONLY=true
            shift
            ;;
        -i|--integration)
            INTEGRATION_ONLY=true
            shift
            ;;
        --clean)
            CLEAN=true
            shift
            ;;
        *)
            echo "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Clean build artifacts if requested
if [ "$CLEAN" = true ]; then
    echo "🧹 Cleaning build artifacts..."
    rm -rf test/build/*
    echo ""
fi

# Create test build directory
mkdir -p test/build

# Compile core source files (only XML-related files that don't depend on leJOS)
echo "📦 Compiling core source files..."
CORE_FILES="src/IString.java src/ShallowString.java src/IXMLElement.java src/ShallowXMLElement.java src/IXMLParser.java src/ShallowXMLParser.java"

if [ "$VERBOSE" = true ]; then
    javac -cp src -d test/build $CORE_FILES
else
    javac -cp src -d test/build $CORE_FILES 2>/dev/null || {
        echo "❌ Failed to compile core source files"
        exit 1
    }
fi
echo "✅ Core source files compiled"
echo ""

# Function to compile and run tests in a directory
run_tests_in_dir() {
    local test_dir=$1
    local test_type=$2
    
    if [ ! -d "$test_dir" ]; then
        echo "⚠️  No $test_type directory found: $test_dir"
        return 0
    fi
    
    # Special handling for integration tests
    if [ "$test_type" = "Integration" ]; then
        local test_files=$(find "$test_dir" -name "TestIntegrationFramework.java" 2>/dev/null)
        if [ -z "$test_files" ]; then
            echo "⚠️  No TestIntegrationFramework.java found in $test_dir"
            return 0
        fi
        
        echo "📋 Running $test_type tests..."
        echo ""
        
        local passed=0
        local failed=0
        local total=0
        
        # Compile all Java files in integration directory
        local all_java_files=$(find "$test_dir" -name "*.java" 2>/dev/null)
        echo "🧪 Compiling integration test framework and scenarios..."
        
        if [ "$VERBOSE" = true ]; then
            javac -cp "src:test:test/build" -d test/build $all_java_files
        else
            javac -cp "src:test:test/build" -d test/build $all_java_files 2>/dev/null || {
                echo "❌ Failed to compile integration test framework"
                failed=$((failed + 1))
                total=$((total + 1))
                echo "📊 $test_type Results: $passed passed, $failed failed, $total total"
                echo ""
                return $failed
            }
        fi
        
        # Run only TestIntegrationFramework
        echo "   Running TestIntegrationFramework..."
        if [ "$VERBOSE" = true ]; then
            java -cp "test/build:src:test" "TestIntegrationFramework"
            local exit_code=$?
        else
            java -cp "test/build:src:test" "TestIntegrationFramework" >/dev/null 2>&1
            local exit_code=$?
        fi
        
        if [ $exit_code -eq 0 ]; then
            echo "   ✅ TestIntegrationFramework PASSED"
            passed=$((passed + 1))
        else
            echo "   ❌ TestIntegrationFramework FAILED"
            failed=$((failed + 1))
        fi
        total=$((total + 1))
        echo ""
        
        echo "📊 $test_type Results: $passed passed, $failed failed, $total total"
        echo ""
        
        return $failed
    else
        # Standard unit test handling
        local test_files=$(find "$test_dir" -name "Test*.java" 2>/dev/null)
        if [ -z "$test_files" ]; then
            echo "⚠️  No Test*.java files found in $test_dir"
            return 0
        fi
        
        echo "📋 Running $test_type tests..."
        echo ""
        
        local passed=0
        local failed=0
        local total=0
        
        for test_file in $test_files; do
            local class_name=$(basename "$test_file" .java)
            local relative_path=${test_file#test/}
            
            echo "🧪 Compiling and running $class_name..."
            
            # Compile the test file
            if [ "$VERBOSE" = true ]; then
                javac -cp "src:test:test/build" -d test/build "$test_file"
            else
                javac -cp "src:test:test/build" -d test/build "$test_file" 2>/dev/null || {
                    echo "❌ Failed to compile $class_name"
                    failed=$((failed + 1))
                    total=$((total + 1))
                    continue
                }
            fi
            
            # Run the test
            echo "   Running $class_name..."
            if [ "$VERBOSE" = true ]; then
                java -cp "src:test:test/build" "$class_name"
                local exit_code=$?
            else
                java -cp "src:test:test/build" "$class_name" >/dev/null 2>&1
                local exit_code=$?
            fi
            
            if [ $exit_code -eq 0 ]; then
                echo "   ✅ $class_name PASSED"
                passed=$((passed + 1))
            else
                echo "   ❌ $class_name FAILED"
                failed=$((failed + 1))
            fi
            total=$((total + 1))
            echo ""
        done
    fi
    
    echo "📊 $test_type Results: $passed passed, $failed failed, $total total"
    echo ""
    
    return $failed
}

# Run tests based on options
total_failures=0

if [ "$INTEGRATION_ONLY" = false ]; then
    run_tests_in_dir "test/unit" "Unit"
    unit_failures=$?
    total_failures=$((total_failures + unit_failures))
fi

if [ "$UNIT_ONLY" = false ]; then
    run_tests_in_dir "test/integration" "Integration"
    integration_failures=$?
    total_failures=$((total_failures + integration_failures))
fi

# Final summary
echo "=========================================="
if [ $total_failures -eq 0 ]; then
    echo "🎉 All tests PASSED!"
    echo "=========================================="
    exit 0
else
    echo "❌ $total_failures test(s) FAILED"
    echo "=========================================="
    exit 1
fi
