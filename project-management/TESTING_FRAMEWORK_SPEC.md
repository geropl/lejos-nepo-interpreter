# Testing Framework Specification

## Overview

The NEPO Interpreter Testing Framework provides comprehensive testing capabilities for validating block implementations without requiring physical NXT hardware. The framework consists of mock hardware simulation, test execution engine, and result validation components.

## Architecture

```
NepoTestFramework
├── MockHardware          # Virtual NXT hardware simulation
├── TestExecutor          # Test case execution engine  
├── ResultValidator       # Expected vs actual comparison
├── TestDataManager       # Test case and data management
└── ReportGenerator       # Test result reporting
```

## Core Components

### 1. MockHardware.java

**Purpose:** Simulate NXT hardware components for testing without physical devices.

#### Requirements
- **Motors:** Simulate 3 regulated motors (A, B, C)
  - Track speed, rotation, direction
  - Simulate rotation completion timing
  - Handle concurrent motor operations
  - Record motor state history

- **Sensors:** Simulate all sensor types
  - Touch sensors (4 ports) - pressed/released states
  - Ultrasonic sensor - configurable distance readings
  - Light sensor - configurable light level readings
  - Sound sensor - configurable sound level readings
  - Gyro sensor - configurable angle readings (optional)

- **Display:** Simulate LCD operations
  - Track text content and position
  - Handle clear operations
  - Capture display state history

- **Sound:** Simulate sound generation
  - Record tone frequency and duration
  - Track musical note playback
  - Handle concurrent sound operations

- **Timer:** Simulate system timer
  - Millisecond precision timing
  - Reset functionality
  - Multiple timer support

#### Interface Design
```java
public class MockHardware {
    // Motor simulation
    public MockMotor getMotor(String port);
    public void setMotorState(String port, int speed, int rotation);
    
    // Sensor simulation  
    public MockSensor getSensor(String port, String type);
    public void setSensorValue(String port, Object value);
    
    // Display simulation
    public void displayText(String text, int x, int y);
    public void clearDisplay();
    public String getDisplayContent();
    
    // Sound simulation
    public void playTone(int frequency, int duration);
    public void playNote(String note, int duration);
    
    // State tracking
    public List<HardwareEvent> getEventHistory();
    public void reset();
}
```

### 2. NepoTestFramework.java

**Purpose:** Main test execution engine that loads test cases, executes NEPO programs, and validates results.

#### Requirements
- **Test Case Loading:** Load XML test programs and expected results
- **Program Execution:** Execute NEPO programs using NepoBlockExecutor
- **Result Capture:** Capture all hardware interactions and program outputs
- **Validation:** Compare actual results with expected outcomes
- **Reporting:** Generate detailed test reports with pass/fail status

#### Interface Design
```java
public class NepoTestFramework {
    public TestResult executeTest(TestCase testCase);
    public TestSuite loadTestSuite(String directory);
    public TestReport runTestSuite(TestSuite suite);
    public void generateReport(TestReport report, String outputFile);
}
```

### 3. BlockTestCase.java

**Purpose:** Define structure for individual test cases.

#### Requirements
- **Test Metadata:** Name, description, category, priority
- **Input Data:** NEPO XML program, initial hardware state
- **Expected Results:** Expected hardware states, display content, execution trace
- **Validation Rules:** Custom validation logic for complex scenarios

#### Structure
```java
public class BlockTestCase {
    String testName;
    String description;
    String category;        // "unit", "integration", "hardware"
    Priority priority;      // HIGH, MEDIUM, LOW
    
    String nepoXML;         // Test program
    MockHardware initialState;
    ExpectedResults expected;
    ValidationRules validation;
    
    long timeoutMs;         // Execution timeout
    boolean requiresHardware; // Real hardware needed
}
```

### 4. TestDataManager.java

**Purpose:** Manage test data, programs, and expected results.

#### Requirements
- **Test Organization:** Organize tests by category, phase, block type
- **Data Loading:** Load test programs from XML files
- **Result Management:** Store and retrieve expected results
- **Test Generation:** Generate test cases programmatically

#### Directory Structure
```
test-data/
├── unit-tests/
│   ├── control-flow/
│   ├── sensors/
│   ├── motors/
│   └── values/
├── integration-tests/
│   ├── simple-programs/
│   ├── complex-programs/
│   └── error-scenarios/
├── expected-results/
│   ├── hardware-states/
│   ├── display-outputs/
│   └── execution-traces/
└── mock-scenarios/
    ├── sensor-data/
    ├── motor-responses/
    └── timing-scenarios/
```

## Test Categories

### 1. Unit Tests
**Purpose:** Test individual blocks in isolation.

#### Requirements
- Test each block type independently
- Validate parameter handling
- Test error conditions
- Verify hardware interactions
- Check return values for value blocks

#### Example Test Cases
```java
// Motor On Block Test
TestCase motorOnTest = new TestCase()
    .name("robActions_motor_on_basic")
    .xml("<block type='robActions_motor_on'>...</block>")
    .expectMotor("B").speed(360).rotations(1)
    .timeout(5000);

// Touch Sensor Test  
TestCase touchTest = new TestCase()
    .name("robSensors_touch_isPressed")
    .xml("<block type='robSensors_touch_isPressed'>...</block>")
    .setSensor("1", "touch", true)
    .expectReturn(Boolean.TRUE);
```

### 2. Integration Tests
**Purpose:** Test block combinations and program flow.

#### Requirements
- Test block sequences
- Validate control flow (if, loops)
- Test variable interactions
- Verify complex program execution
- Check performance under load

#### Example Scenarios
- Motor movement followed by sensor reading
- Conditional execution based on sensor input
- Loop with sensor termination condition
- Variable manipulation across blocks

### 3. Hardware Tests
**Purpose:** Validate hardware simulation accuracy.

#### Requirements
- Test mock hardware behavior
- Validate timing accuracy
- Test concurrent operations
- Verify state persistence
- Check hardware limits

### 4. Error Handling Tests
**Purpose:** Test error conditions and recovery.

#### Requirements
- Invalid XML programs
- Missing parameters
- Hardware failures
- Timeout conditions
- Memory constraints

## Test Execution Engine

### Execution Flow
1. **Load Test Case:** Parse XML, setup mock hardware
2. **Initialize Environment:** Reset hardware state, clear variables
3. **Execute Program:** Run NEPO program with timeout
4. **Capture Results:** Record all hardware interactions
5. **Validate Results:** Compare with expected outcomes
6. **Generate Report:** Create detailed test report

### Performance Requirements
- **Execution Speed:** <1 second per unit test
- **Memory Usage:** <10MB for test framework
- **Concurrency:** Support parallel test execution
- **Scalability:** Handle 1000+ test cases

## Validation Framework

### Result Validation Types

#### 1. Hardware State Validation
```java
// Motor state validation
validator.expectMotor("B")
    .speed(360)
    .rotations(1)
    .direction(FORWARD)
    .completed(true);

// Sensor state validation  
validator.expectSensor("1")
    .type("touch")
    .value(true)
    .readCount(1);
```

#### 2. Display Content Validation
```java
// Display validation
validator.expectDisplay()
    .line(0, "Hello Roberta!")
    .line(1, "")
    .cleared(false);
```

#### 3. Execution Trace Validation
```java
// Execution order validation
validator.expectExecutionOrder()
    .block("robActions_display_text")
    .block("robActions_motor_on")
    .block("robControls_wait_time");
```

#### 4. Performance Validation
```java
// Performance validation
validator.expectPerformance()
    .executionTime().lessThan(100, MILLISECONDS)
    .memoryUsage().lessThan(1, MEGABYTES)
    .blockCount().equals(5);
```

## Test Data Formats

### Test Case XML Format
```xml
<testCase name="motor_on_basic" category="unit" priority="high">
    <description>Test basic motor on functionality</description>
    <nepoProgram>
        <blockSet robottype="nxt">
            <!-- NEPO program blocks -->
        </blockSet>
    </nepoProgram>
    <initialState>
        <motor port="B" speed="0" rotation="0"/>
        <sensor port="1" type="touch" value="false"/>
    </initialState>
    <expectedResults>
        <motor port="B" speed="360" rotations="1" direction="forward"/>
        <executionTime max="100"/>
    </expectedResults>
</testCase>
```

### Expected Results Format
```json
{
    "testName": "motor_on_basic",
    "expectedHardware": {
        "motors": {
            "B": {
                "speed": 360,
                "rotations": 1,
                "direction": "forward",
                "completed": true
            }
        },
        "sensors": {},
        "display": {
            "lines": ["", "", "", "", "", "", "", ""],
            "cleared": false
        },
        "sound": []
    },
    "expectedPerformance": {
        "executionTimeMs": 100,
        "memoryUsageMB": 1,
        "blockExecutions": 3
    }
}
```

## Reporting Framework

### Test Report Structure
```java
public class TestReport {
    String suiteName;
    Date executionDate;
    int totalTests;
    int passedTests;
    int failedTests;
    int skippedTests;
    
    List<TestResult> results;
    PerformanceMetrics performance;
    CoverageReport coverage;
}
```

### Report Formats
- **Console Output:** Real-time test progress
- **HTML Report:** Detailed web-based report
- **JSON Report:** Machine-readable results
- **JUnit XML:** CI/CD integration format

## Implementation Plan

### Phase 1: Foundation (Week 1)
- [ ] MockHardware basic implementation
- [ ] NepoTestFramework core structure
- [ ] BlockTestCase definition
- [ ] Basic test execution

### Phase 2: Core Features (Week 2)
- [ ] Complete MockHardware simulation
- [ ] Result validation framework
- [ ] Test data management
- [ ] Report generation

### Phase 3: Advanced Features (Week 3)
- [ ] Performance testing
- [ ] Parallel execution
- [ ] CI/CD integration
- [ ] Advanced validation rules

## Usage Examples

### Basic Test Creation
```java
// Create a simple test case
TestCase test = new TestCase()
    .name("display_text_test")
    .category("unit")
    .xml(loadXML("display_text_basic.xml"))
    .expectDisplay().line(0, "Hello Roberta!")
    .timeout(1000);

// Execute test
TestResult result = framework.executeTest(test);
assert result.passed();
```

### Test Suite Execution
```java
// Load and run test suite
TestSuite suite = framework.loadTestSuite("test-data/unit-tests/");
TestReport report = framework.runTestSuite(suite);
framework.generateReport(report, "test-results.html");
```

### Custom Validation
```java
// Custom validation logic
TestCase customTest = new TestCase()
    .name("complex_scenario")
    .xml(complexProgram)
    .customValidation((actual, expected) -> {
        // Custom validation logic
        return actual.getMotorRotations("B") > 0.5;
    });
```

---

**Document Status:** Draft  
**Last Updated:** 2025-01-12  
**Next Review:** 2025-01-19  
**Implementation Priority:** Critical Path
