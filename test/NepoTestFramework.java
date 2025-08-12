import java.util.*;
import java.io.*;

/**
 * NepoTestFramework - Main test execution engine for NEPO block testing
 * 
 * This framework loads test cases, executes NEPO programs using MockHardware,
 * validates results, and generates comprehensive test reports.
 */
public class NepoTestFramework {
    
    // Test execution components
    private MockHardware mockHardware;
    private TestDataManager dataManager;
    private Vector testResults;
    
    // Configuration
    private boolean verboseOutput;
    private long defaultTimeoutMs;
    private String testDataDirectory;
    
    // Statistics
    private int totalTests;
    private int passedTests;
    private int failedTests;
    private int skippedTests;
    private long totalExecutionTime;
    
    /**
     * Initialize test framework
     */
    public NepoTestFramework() {
        this.mockHardware = new MockHardware();
        this.dataManager = new TestDataManager();
        this.testResults = new Vector();
        this.verboseOutput = false;
        this.defaultTimeoutMs = 5000; // 5 second default timeout
        this.testDataDirectory = "test-data";
        resetStatistics();
    }
    
    /**
     * Execute a single test case
     */
    public TestResult executeTest(BlockTestCase testCase) {
        if (testCase == null) {
            return new TestResult("NULL_TEST", false, "Test case is null", 0, null);
        }
        
        logVerbose("Executing test: " + testCase.getTestName());
        long startTime = System.currentTimeMillis();
        
        try {
            // Reset hardware to clean state
            mockHardware.reset();
            
            // Apply initial hardware state if specified
            if (testCase.getInitialState() != null) {
                applyInitialState(testCase.getInitialState());
            }
            
            // Execute the NEPO program
            Object executionResult = executeNepoProgram(testCase);
            
            // Capture final hardware state
            MockHardware.HardwareState finalState = mockHardware.getCurrentState();
            
            // Validate results
            ValidationResult validation = validateTestResult(testCase, executionResult, finalState);
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Create test result
            TestResult result = new TestResult(
                testCase.getTestName(),
                validation.passed(),
                validation.getMessage(),
                executionTime,
                finalState
            );
            
            // Add execution details
            result.setExecutionResult(executionResult);
            result.setEventHistory(mockHardware.getEventHistory());
            result.setExecutionStats(mockHardware.getStats());
            
            logVerbose("Test " + testCase.getTestName() + ": " + 
                      (validation.passed() ? "PASSED" : "FAILED") + 
                      " (" + executionTime + "ms)");
            
            return result;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logVerbose("Test " + testCase.getTestName() + " threw exception: " + e.getMessage());
            
            return new TestResult(
                testCase.getTestName(),
                false,
                "Exception during execution: " + e.getMessage(),
                executionTime,
                mockHardware.getCurrentState()
            );
        }
    }
    
    /**
     * Execute NEPO program from test case
     */
    private Object executeNepoProgram(BlockTestCase testCase) throws Exception {
        String nepoXML = testCase.getNepoXML();
        if (nepoXML == null || nepoXML.trim().length() == 0) {
            throw new Exception("No NEPO XML program provided");
        }
        
        // Parse XML program
        SimpleXMLParser.XMLElement program = SimpleXMLParser.parseXML(nepoXML);
        if (program == null) {
            throw new Exception("Failed to parse NEPO XML program");
        }
        
        // Create block executor with mock hardware
        MockNepoBlockExecutor executor = new MockNepoBlockExecutor(mockHardware);
        
        // Find start block
        SimpleXMLParser.XMLElement startBlock = findStartBlock(program);
        if (startBlock == null) {
            throw new Exception("No start block found in NEPO program");
        }
        
        // Execute program with timeout
        return executeWithTimeout(executor, startBlock, testCase.getTimeoutMs());
    }
    
    /**
     * Find start block in NEPO program
     */
    private SimpleXMLParser.XMLElement findStartBlock(SimpleXMLParser.XMLElement program) {
        // Look for robControls_start block
        return findBlockByType(program, "robControls_start");
    }
    
    /**
     * Find block by type recursively
     */
    private SimpleXMLParser.XMLElement findBlockByType(SimpleXMLParser.XMLElement element, String blockType) {
        if (element == null) return null;
        
        // Check if this element is the block we're looking for
        if ("block".equals(element.tagName) && blockType.equals(element.getAttribute("type"))) {
            return element;
        }
        
        // Search children recursively
        for (int i = 0; i < element.children.size(); i++) {
            SimpleXMLParser.XMLElement child = (SimpleXMLParser.XMLElement) element.children.elementAt(i);
            SimpleXMLParser.XMLElement found = findBlockByType(child, blockType);
            if (found != null) {
                return found;
            }
        }
        
        return null;
    }
    
    /**
     * Execute block with timeout
     */
    private Object executeWithTimeout(MockNepoBlockExecutor executor, 
                                    SimpleXMLParser.XMLElement startBlock, 
                                    long timeoutMs) throws Exception {
        
        final Object[] result = new Object[1];
        final Exception[] exception = new Exception[1];
        
        // Create execution thread
        Thread executionThread = new Thread(new Runnable() {
            public void run() {
                try {
                    executor.executeBlock(startBlock);
                    result[0] = "COMPLETED";
                } catch (Exception e) {
                    exception[0] = e;
                }
            }
        });
        
        // Start execution
        executionThread.start();
        
        // Wait for completion or timeout
        try {
            executionThread.join(timeoutMs > 0 ? timeoutMs : defaultTimeoutMs);
        } catch (InterruptedException e) {
            executionThread.interrupt();
            throw new Exception("Test execution interrupted");
        }
        
        // Check if thread is still running (timeout)
        if (executionThread.isAlive()) {
            executionThread.interrupt();
            throw new Exception("Test execution timed out after " + timeoutMs + "ms");
        }
        
        // Check for exceptions
        if (exception[0] != null) {
            throw exception[0];
        }
        
        return result[0];
    }
    
    /**
     * Apply initial hardware state for test
     */
    private void applyInitialState(MockHardware.HardwareState initialState) {
        // Apply motor states
        Hashtable motorStates = initialState.getMotorStates();
        if (motorStates != null) {
            Enumeration motorKeys = motorStates.keys();
            while (motorKeys.hasMoreElements()) {
                String port = (String) motorKeys.nextElement();
                MockMotor.MotorState state = (MockMotor.MotorState) motorStates.get(port);
                MockMotor motor = mockHardware.getMotor(port);
                if (motor != null && state != null) {
                    motor.setSpeed(state.getSpeed());
                    // Additional state application as needed
                }
            }
        }
        
        // Apply sensor states
        Hashtable sensorStates = initialState.getSensorStates();
        if (sensorStates != null) {
            Enumeration sensorKeys = sensorStates.keys();
            while (sensorKeys.hasMoreElements()) {
                String port = (String) sensorKeys.nextElement();
                MockSensor.SensorState state = (MockSensor.SensorState) sensorStates.get(port);
                MockSensor sensor = mockHardware.getSensor(port);
                if (sensor != null && state != null) {
                    // Configure sensor based on state
                    String sensorType = state.getSensorType();
                    if ("touch".equals(sensorType)) {
                        sensor.configureTouchSensor();
                        if (state.getCurrentValue() instanceof Boolean) {
                            sensor.setTouchPressed(((Boolean) state.getCurrentValue()).booleanValue());
                        }
                    } else if ("ultrasonic".equals(sensorType)) {
                        sensor.configureUltrasonicSensor();
                        if (state.getCurrentValue() instanceof Integer) {
                            sensor.setUltrasonicDistance(((Integer) state.getCurrentValue()).intValue());
                        }
                    }
                    // Add other sensor types as needed
                }
            }
        }
    }
    
    /**
     * Validate test result against expected outcomes
     */
    private ValidationResult validateTestResult(BlockTestCase testCase, 
                                              Object executionResult, 
                                              MockHardware.HardwareState finalState) {
        
        ExpectedResults expected = testCase.getExpectedResults();
        if (expected == null) {
            return new ValidationResult(true, "No validation rules specified");
        }
        
        Vector validationErrors = new Vector();
        
        // Validate hardware state
        if (expected.getExpectedHardwareState() != null) {
            MockHardware.ValidationResult hwValidation = 
                finalState.compare(expected.getExpectedHardwareState());
            if (!hwValidation.passed()) {
                validationErrors.addElement("Hardware state: " + hwValidation.getMessage());
            }
        }
        
        // Validate display content
        if (expected.getExpectedDisplayContent() != null) {
            String actualDisplay = mockHardware.getDisplay().getContent();
            if (!expected.getExpectedDisplayContent().equals(actualDisplay)) {
                validationErrors.addElement("Display content mismatch. Expected: '" + 
                    expected.getExpectedDisplayContent() + "', Actual: '" + actualDisplay + "'");
            }
        }
        
        // Validate execution time
        if (expected.getMaxExecutionTimeMs() > 0) {
            MockHardware.ExecutionStats stats = mockHardware.getStats();
            if (stats.getTotalTimeMs() > expected.getMaxExecutionTimeMs()) {
                validationErrors.addElement("Execution time exceeded limit. Expected: <" + 
                    expected.getMaxExecutionTimeMs() + "ms, Actual: " + stats.getTotalTimeMs() + "ms");
            }
        }
        
        // Validate event count
        if (expected.getMinEventCount() > 0) {
            Vector events = mockHardware.getEventHistory();
            if (events.size() < expected.getMinEventCount()) {
                validationErrors.addElement("Insufficient events. Expected: >=" + 
                    expected.getMinEventCount() + ", Actual: " + events.size());
            }
        }
        
        // Custom validation rules
        ValidationRules customRules = testCase.getValidationRules();
        if (customRules != null) {
            ValidationResult customResult = customRules.validate(executionResult, finalState);
            if (!customResult.passed()) {
                validationErrors.addElement("Custom validation: " + customResult.getMessage());
            }
        }
        
        // Compile results
        if (validationErrors.size() == 0) {
            return new ValidationResult(true, "All validations passed");
        } else {
            StringBuffer message = new StringBuffer("Validation failures: ");
            for (int i = 0; i < validationErrors.size(); i++) {
                if (i > 0) message.append("; ");
                message.append(validationErrors.elementAt(i));
            }
            return new ValidationResult(false, message.toString());
        }
    }
    
    /**
     * Run a test suite
     */
    public TestReport runTestSuite(TestSuite suite) {
        if (suite == null) {
            return new TestReport("NULL_SUITE", new Vector(), 0, 0, 0, 0);
        }
        
        logVerbose("Running test suite: " + suite.getName());
        long suiteStartTime = System.currentTimeMillis();
        
        resetStatistics();
        testResults.clear();
        
        Vector testCases = suite.getTestCases();
        totalTests = testCases.size();
        
        // Execute each test case
        for (int i = 0; i < testCases.size(); i++) {
            BlockTestCase testCase = (BlockTestCase) testCases.elementAt(i);
            
            try {
                TestResult result = executeTest(testCase);
                testResults.addElement(result);
                
                if (result.passed()) {
                    passedTests++;
                } else {
                    failedTests++;
                }
                
            } catch (Exception e) {
                TestResult errorResult = new TestResult(
                    testCase.getTestName(),
                    false,
                    "Test framework error: " + e.getMessage(),
                    0,
                    null
                );
                testResults.addElement(errorResult);
                failedTests++;
            }
        }
        
        long suiteExecutionTime = System.currentTimeMillis() - suiteStartTime;
        totalExecutionTime = suiteExecutionTime;
        
        logVerbose("Test suite completed: " + passedTests + "/" + totalTests + " passed");
        
        return new TestReport(suite.getName(), testResults, totalTests, 
                            passedTests, failedTests, suiteExecutionTime);
    }
    
    /**
     * Load test suite from directory
     */
    public TestSuite loadTestSuite(String directory) {
        return dataManager.loadTestSuite(directory);
    }
    
    /**
     * Generate test report
     */
    public void generateReport(TestReport report, String outputFile) {
        try {
            if (outputFile.endsWith(".html")) {
                generateHtmlReport(report, outputFile);
            } else if (outputFile.endsWith(".json")) {
                generateJsonReport(report, outputFile);
            } else {
                generateTextReport(report, outputFile);
            }
        } catch (Exception e) {
            System.err.println("Failed to generate report: " + e.getMessage());
        }
    }
    
    /**
     * Generate HTML report
     */
    private void generateHtmlReport(TestReport report, String outputFile) throws Exception {
        FileWriter writer = new FileWriter(outputFile);
        
        writer.write("<html><head><title>NEPO Test Report</title>");
        writer.write("<style>");
        writer.write("body { font-family: Arial, sans-serif; margin: 20px; }");
        writer.write(".passed { color: green; }");
        writer.write(".failed { color: red; }");
        writer.write("table { border-collapse: collapse; width: 100%; }");
        writer.write("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        writer.write("th { background-color: #f2f2f2; }");
        writer.write("</style></head><body>");
        
        writer.write("<h1>NEPO Test Report</h1>");
        writer.write("<h2>Summary</h2>");
        writer.write("<p>Suite: " + report.getSuiteName() + "</p>");
        writer.write("<p>Total Tests: " + report.getTotalTests() + "</p>");
        writer.write("<p class='passed'>Passed: " + report.getPassedTests() + "</p>");
        writer.write("<p class='failed'>Failed: " + report.getFailedTests() + "</p>");
        writer.write("<p>Execution Time: " + report.getExecutionTime() + "ms</p>");
        
        writer.write("<h2>Test Results</h2>");
        writer.write("<table>");
        writer.write("<tr><th>Test Name</th><th>Status</th><th>Time (ms)</th><th>Message</th></tr>");
        
        Vector results = report.getResults();
        for (int i = 0; i < results.size(); i++) {
            TestResult result = (TestResult) results.elementAt(i);
            writer.write("<tr>");
            writer.write("<td>" + result.getTestName() + "</td>");
            writer.write("<td class='" + (result.passed() ? "passed" : "failed") + "'>" + 
                        (result.passed() ? "PASSED" : "FAILED") + "</td>");
            writer.write("<td>" + result.getExecutionTime() + "</td>");
            writer.write("<td>" + result.getMessage() + "</td>");
            writer.write("</tr>");
        }
        
        writer.write("</table>");
        writer.write("</body></html>");
        writer.close();
    }
    
    /**
     * Generate JSON report
     */
    private void generateJsonReport(TestReport report, String outputFile) throws Exception {
        FileWriter writer = new FileWriter(outputFile);
        
        writer.write("{\n");
        writer.write("  \"suiteName\": \"" + report.getSuiteName() + "\",\n");
        writer.write("  \"totalTests\": " + report.getTotalTests() + ",\n");
        writer.write("  \"passedTests\": " + report.getPassedTests() + ",\n");
        writer.write("  \"failedTests\": " + report.getFailedTests() + ",\n");
        writer.write("  \"executionTime\": " + report.getExecutionTime() + ",\n");
        writer.write("  \"results\": [\n");
        
        Vector results = report.getResults();
        for (int i = 0; i < results.size(); i++) {
            TestResult result = (TestResult) results.elementAt(i);
            writer.write("    {\n");
            writer.write("      \"testName\": \"" + result.getTestName() + "\",\n");
            writer.write("      \"passed\": " + result.passed() + ",\n");
            writer.write("      \"executionTime\": " + result.getExecutionTime() + ",\n");
            writer.write("      \"message\": \"" + result.getMessage() + "\"\n");
            writer.write("    }");
            if (i < results.size() - 1) writer.write(",");
            writer.write("\n");
        }
        
        writer.write("  ]\n");
        writer.write("}\n");
        writer.close();
    }
    
    /**
     * Generate text report
     */
    private void generateTextReport(TestReport report, String outputFile) throws Exception {
        FileWriter writer = new FileWriter(outputFile);
        
        writer.write("NEPO Test Report\n");
        writer.write("================\n\n");
        writer.write("Suite: " + report.getSuiteName() + "\n");
        writer.write("Total Tests: " + report.getTotalTests() + "\n");
        writer.write("Passed: " + report.getPassedTests() + "\n");
        writer.write("Failed: " + report.getFailedTests() + "\n");
        writer.write("Execution Time: " + report.getExecutionTime() + "ms\n\n");
        
        writer.write("Test Results:\n");
        writer.write("-------------\n");
        
        Vector results = report.getResults();
        for (int i = 0; i < results.size(); i++) {
            TestResult result = (TestResult) results.elementAt(i);
            writer.write(result.getTestName() + ": " + 
                        (result.passed() ? "PASSED" : "FAILED") + 
                        " (" + result.getExecutionTime() + "ms)\n");
            if (!result.passed()) {
                writer.write("  " + result.getMessage() + "\n");
            }
        }
        
        writer.close();
    }
    
    /**
     * Set verbose output
     */
    public void setVerboseOutput(boolean verbose) {
        this.verboseOutput = verbose;
    }
    
    /**
     * Set default timeout
     */
    public void setDefaultTimeout(long timeoutMs) {
        this.defaultTimeoutMs = timeoutMs;
    }
    
    /**
     * Reset statistics
     */
    private void resetStatistics() {
        totalTests = 0;
        passedTests = 0;
        failedTests = 0;
        skippedTests = 0;
        totalExecutionTime = 0;
    }
    
    /**
     * Log verbose message
     */
    private void logVerbose(String message) {
        if (verboseOutput) {
            System.out.println("[NepoTestFramework] " + message);
        }
    }
    
    /**
     * Get test statistics
     */
    public TestStatistics getStatistics() {
        return new TestStatistics(totalTests, passedTests, failedTests, 
                                skippedTests, totalExecutionTime);
    }
    
    /**
     * Validation result helper class
     */
    public static class ValidationResult {
        private boolean passed;
        private String message;
        
        public ValidationResult(boolean passed, String message) {
            this.passed = passed;
            this.message = message;
        }
        
        public boolean passed() { return passed; }
        public String getMessage() { return message; }
    }
    
    /**
     * Test statistics
     */
    public static class TestStatistics {
        private int totalTests;
        private int passedTests;
        private int failedTests;
        private int skippedTests;
        private long totalExecutionTime;
        
        public TestStatistics(int total, int passed, int failed, int skipped, long executionTime) {
            this.totalTests = total;
            this.passedTests = passed;
            this.failedTests = failed;
            this.skippedTests = skipped;
            this.totalExecutionTime = executionTime;
        }
        
        public int getTotalTests() { return totalTests; }
        public int getPassedTests() { return passedTests; }
        public int getFailedTests() { return failedTests; }
        public int getSkippedTests() { return skippedTests; }
        public long getTotalExecutionTime() { return totalExecutionTime; }
        public double getPassRate() { return totalTests > 0 ? (double) passedTests / totalTests : 0.0; }
    }
}
