import java.util.*;

/**
 * NepoTestRunner - Main test runner for NEPO block testing
 * 
 * Provides command-line interface and automated test execution
 * for the complete NEPO test harness.
 */
public class NepoTestRunner {
    
    private NepoTestFramework framework;
    private TestDataManager dataManager;
    private boolean verboseOutput;
    
    public static void main(String[] args) {
        NepoTestRunner runner = new NepoTestRunner();
        runner.run(args);
    }
    
    /**
     * Initialize test runner
     */
    public NepoTestRunner() {
        framework = new NepoTestFramework();
        dataManager = new TestDataManager();
        verboseOutput = true;
    }
    
    /**
     * Run tests based on command line arguments
     */
    public void run(String[] args) {
        System.out.println("=== NEPO Test Harness ===");
        System.out.println("Comprehensive testing for NEPO block implementations");
        System.out.println();
        
        // Configure framework
        framework.setVerboseOutput(verboseOutput);
        framework.setDefaultTimeout(10000); // 10 second default timeout
        
        if (args.length == 0) {
            // Run all basic tests
            runBasicTestSuite();
        } else {
            // Process command line arguments
            processArguments(args);
        }
    }
    
    /**
     * Run basic test suite for existing blocks
     */
    private void runBasicTestSuite() {
        System.out.println("Running basic test suite for existing NEPO blocks...");
        System.out.println();
        
        // Generate test cases for existing blocks
        Vector testCases = dataManager.generateBasicTestCases();
        TestSuite basicSuite = new TestSuite("Basic Block Tests", testCases);
        
        // Execute test suite
        TestReport report = framework.runTestSuite(basicSuite);
        
        // Display results
        displayTestReport(report);
        
        // Generate detailed report files
        generateReports(report);
    }
    
    /**
     * Process command line arguments
     */
    private void processArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            
            if ("--help".equals(arg) || "-h".equals(arg)) {
                displayHelp();
                return;
            } else if ("--verbose".equals(arg) || "-v".equals(arg)) {
                verboseOutput = true;
                framework.setVerboseOutput(true);
            } else if ("--quiet".equals(arg) || "-q".equals(arg)) {
                verboseOutput = false;
                framework.setVerboseOutput(false);
            } else if ("--suite".equals(arg) || "-s".equals(arg)) {
                if (i + 1 < args.length) {
                    runTestSuite(args[++i]);
                } else {
                    System.err.println("Error: --suite requires a suite name");
                }
            } else if ("--test".equals(arg) || "-t".equals(arg)) {
                if (i + 1 < args.length) {
                    runSingleTest(args[++i]);
                } else {
                    System.err.println("Error: --test requires a test name");
                }
            } else if ("--category".equals(arg) || "-c".equals(arg)) {
                if (i + 1 < args.length) {
                    runTestsByCategory(args[++i]);
                } else {
                    System.err.println("Error: --category requires a category name");
                }
            } else if ("--block".equals(arg) || "-b".equals(arg)) {
                if (i + 1 < args.length) {
                    runTestsByBlockType(args[++i]);
                } else {
                    System.err.println("Error: --block requires a block type");
                }
            } else if ("--timeout".equals(arg)) {
                if (i + 1 < args.length) {
                    try {
                        long timeout = Long.parseLong(args[++i]);
                        framework.setDefaultTimeout(timeout);
                    } catch (NumberFormatException e) {
                        System.err.println("Error: Invalid timeout value");
                    }
                } else {
                    System.err.println("Error: --timeout requires a millisecond value");
                }
            } else if ("--generate".equals(arg) || "-g".equals(arg)) {
                generateSampleTests();
            } else if ("--validate".equals(arg)) {
                validateTestFramework();
            } else {
                System.err.println("Unknown argument: " + arg);
                displayHelp();
                return;
            }
        }
    }
    
    /**
     * Run specific test suite
     */
    private void runTestSuite(String suiteName) {
        System.out.println("Running test suite: " + suiteName);
        
        TestSuite suite = framework.loadTestSuite(suiteName);
        if (suite.getTestCaseCount() == 0) {
            System.err.println("No test cases found in suite: " + suiteName);
            return;
        }
        
        TestReport report = framework.runTestSuite(suite);
        displayTestReport(report);
        generateReports(report);
    }
    
    /**
     * Run single test case
     */
    private void runSingleTest(String testName) {
        System.out.println("Running single test: " + testName);
        
        BlockTestCase testCase = dataManager.getTestCase(testName);
        if (testCase == null) {
            System.err.println("Test case not found: " + testName);
            return;
        }
        
        TestResult result = framework.executeTest(testCase);
        displayTestResult(result);
    }
    
    /**
     * Run tests by category
     */
    private void runTestsByCategory(String category) {
        System.out.println("Running tests by category: " + category);
        
        Vector testCases = dataManager.getTestCasesByCategory(category);
        if (testCases.size() == 0) {
            System.err.println("No test cases found for category: " + category);
            return;
        }
        
        TestSuite suite = new TestSuite(category + " Tests", testCases);
        TestReport report = framework.runTestSuite(suite);
        displayTestReport(report);
    }
    
    /**
     * Run tests by block type
     */
    private void runTestsByBlockType(String blockType) {
        System.out.println("Running tests by block type: " + blockType);
        
        Vector testCases = dataManager.getTestCasesByBlockType(blockType);
        if (testCases.size() == 0) {
            System.err.println("No test cases found for block type: " + blockType);
            return;
        }
        
        TestSuite suite = new TestSuite(blockType + " Tests", testCases);
        TestReport report = framework.runTestSuite(suite);
        displayTestReport(report);
    }
    
    /**
     * Generate sample test cases
     */
    private void generateSampleTests() {
        System.out.println("Generating sample test cases...");
        
        Vector testCases = dataManager.generateBasicTestCases();
        
        // Save test cases to files
        for (int i = 0; i < testCases.size(); i++) {
            BlockTestCase testCase = (BlockTestCase) testCases.elementAt(i);
            boolean saved = dataManager.saveTestCase(testCase, "unit-tests");
            if (saved) {
                System.out.println("Generated: " + testCase.getTestName() + ".xml");
            } else {
                System.err.println("Failed to generate: " + testCase.getTestName());
            }
        }
        
        System.out.println("Generated " + testCases.size() + " test cases in test-data/unit-tests/");
    }
    
    /**
     * Validate test framework functionality
     */
    private void validateTestFramework() {
        System.out.println("Validating test framework...");
        
        // Run MockHardware validation
        MockHardwareTest hwTest = new MockHardwareTest();
        hwTest.runAllTests();
        
        System.out.println();
        System.out.println("Test framework validation complete.");
    }
    
    /**
     * Display test report summary
     */
    private void displayTestReport(TestReport report) {
        System.out.println();
        System.out.println("=== Test Report Summary ===");
        System.out.println("Suite: " + report.getSuiteName());
        System.out.println("Total Tests: " + report.getTotalTests());
        System.out.println("Passed: " + report.getPassedTests() + " (" + 
                          String.valueOf((int) report.getPassRate()) + "%)");
        System.out.println("Failed: " + report.getFailedTests());
        System.out.println("Execution Time: " + report.getExecutionTime() + "ms");
        
        // Show failed tests
        Vector failedResults = report.getFailedResults();
        if (failedResults.size() > 0) {
            System.out.println();
            System.out.println("Failed Tests:");
            for (int i = 0; i < failedResults.size(); i++) {
                TestResult result = (TestResult) failedResults.elementAt(i);
                System.out.println("  " + result.getTestName() + ": " + result.getMessage());
            }
        }
        
        System.out.println();
        if (report.getFailedTests() == 0) {
            System.out.println("✅ All tests PASSED!");
        } else {
            System.out.println("❌ " + report.getFailedTests() + " test(s) FAILED");
        }
    }
    
    /**
     * Display single test result
     */
    private void displayTestResult(TestResult result) {
        System.out.println();
        System.out.println("=== Test Result ===");
        System.out.println("Test: " + result.getTestName());
        System.out.println("Status: " + (result.passed() ? "PASSED" : "FAILED"));
        System.out.println("Time: " + result.getExecutionTime() + "ms");
        if (!result.passed()) {
            System.out.println("Message: " + result.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Generate detailed report files
     */
    private void generateReports(TestReport report) {
        try {
            // Generate HTML report
            framework.generateReport(report, "test-results.html");
            System.out.println("HTML report generated: test-results.html");
            
            // Generate JSON report
            framework.generateReport(report, "test-results.json");
            System.out.println("JSON report generated: test-results.json");
            
            // Generate text report
            framework.generateReport(report, "test-results.txt");
            System.out.println("Text report generated: test-results.txt");
            
        } catch (Exception e) {
            System.err.println("Error generating reports: " + e.getMessage());
        }
    }
    
    /**
     * Display help information
     */
    private void displayHelp() {
        System.out.println("NEPO Test Runner - Usage:");
        System.out.println();
        System.out.println("  java NepoTestRunner [options]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help              Show this help message");
        System.out.println("  -v, --verbose           Enable verbose output");
        System.out.println("  -q, --quiet             Disable verbose output");
        System.out.println("  -s, --suite <name>      Run specific test suite");
        System.out.println("  -t, --test <name>       Run specific test case");
        System.out.println("  -c, --category <cat>    Run tests by category");
        System.out.println("  -b, --block <type>      Run tests by block type");
        System.out.println("  --timeout <ms>          Set default timeout in milliseconds");
        System.out.println("  -g, --generate          Generate sample test cases");
        System.out.println("  --validate              Validate test framework");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java NepoTestRunner                    # Run basic test suite");
        System.out.println("  java NepoTestRunner -c unit            # Run unit tests");
        System.out.println("  java NepoTestRunner -b robActions_motor_on  # Test motor blocks");
        System.out.println("  java NepoTestRunner -g                 # Generate sample tests");
        System.out.println("  java NepoTestRunner --validate         # Validate framework");
    }
    
    /**
     * Get framework statistics
     */
    public NepoTestFramework.TestStatistics getStatistics() {
        return framework.getStatistics();
    }
}
