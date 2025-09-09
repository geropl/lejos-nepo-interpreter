import java.io.*;
import java.util.*;

/**
 * Integration test framework for NEPO XML programs
 * 
 * Features:
 * - Table-driven test cases with program files and scenarios
 * - Runs XML programs with MockHardware
 * - Generates golden files if missing (test fails)
 * - Compares output with existing golden files
 * - Golden files named as {program}_{testcase}.golden
 */
public class TestIntegrationFramework {
    
    /**
     * Represents a single test case with program file and optional scenario.
     */
    public static class TestCase {
        public final String name;
        public final String programFile;
        public final DynamicTestScenario scenario;
        public final int maxIterations;
        
        public TestCase(String name, String programFile) {
            this(name, programFile, new DynamicTestScenario());
        }
        
        public TestCase(String name, String programFile, int maxIterations) {
            this(name, programFile, maxIterations, new DynamicTestScenario());
        }
        
        public TestCase(String name, String programFile, DynamicTestScenario scenario) {
            this(name, programFile, 10, scenario);
        }
        
        public TestCase(String name, String programFile, int maxIterations, DynamicTestScenario scenario) {
            this.name = name;
            this.programFile = programFile;
            this.maxIterations = maxIterations;
            this.scenario = scenario;
        }

        public String getProgramName() {
            String fileName = this.programFile;
            int lastDot = fileName.lastIndexOf('.');
            return lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
        }
        
        /**
         * Generate golden file name from test case.
         * Format: {program_name}_{test_case_name}.golden
         */
        public String getGoldenFileName() {
            String programName = this.getProgramName();
            return programName + "_" + this.name + GOLDEN_EXTENSION;
        }
    }
    
    // Static array of all test cases (like TestXMLParser pattern)
    private static final TestCase[] TEST_CASES = {
        // Basic tests (no scenarios)
        new TestCase("basic", "programm1.xml", 10),

        // Basic tests (no scenarios)
        new TestCase("hits_dark", "programm1.xml", 20, new DynamicTestScenario().atIteration(3).setLightSensor(4, 40).atIteration(4).setLightSensor(4, 80)),
        
        // Scenario-based tests using the same program with different conditions
        new TestCase("collision", "dynamic_test.xml", 
            new DynamicTestScenario()
                .atIteration(5).setTouchSensor(1, true)),
            
        new TestCase("obstacle_avoidance", "dynamic_test.xml",
            new DynamicTestScenario()
                .atIteration(5).setTouchSensor(1, true)),
            
        new TestCase("light_following", "dynamic_test.xml",
            new DynamicTestScenario()
                .atIteration(3).setLightSensor(4, 60.0)
                .atIteration(6).setLightSensor(4, 80.0)),
            
        // Custom complex scenario
        new TestCase("complex_navigation", "dynamic_test.xml",
            new DynamicTestScenario()
                .atIteration(2).setTouchSensor(1, true)
                .atIteration(4).setLightSensor(4, 25.0)
                .atIteration(6).setTouchSensor(1, false)),
                
        // Multiple tests for same program
        new TestCase("normal_run", "dynamic_test.xml"),
        new TestCase("early_collision", "dynamic_test.xml",
            new DynamicTestScenario()
                .atIteration(3).setTouchSensor(1, true))
    };
    
    private static final String CASES_DIR = "test/integration/cases";
    private static final String GOLDEN_EXTENSION = ".golden";
    
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;
    private static int generatedGoldenFiles = 0;

    public static void main(String[] args) {
        System.out.println("=== NEPO Integration Test Framework ===");
        System.out.println("Discovering test cases in: " + CASES_DIR);
        System.out.println();
        
        try {
            runAllTests();
            printSummary();
            
            // Exit with appropriate code
            if (failedTests > 0 || generatedGoldenFiles > 0) {
                System.exit(1);
            } else {
                System.exit(0);
            }
        } catch (Exception e) {
            System.err.println("Test framework error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Run all registered test cases
     */
    private static void runAllTests() {
        System.out.println("Found " + TEST_CASES.length + " test case(s):");
        for (TestCase testCase : TEST_CASES) {
            String programName = testCase.getProgramName();
            String scenarioInfo = testCase.scenario.hasScenarios() ? 
                " (with " + testCase.scenario.getScenarioCount() + " scenario(s))" : "";
            System.out.println("  - " + testCase.name + " (" + programName + ")" + scenarioInfo);
        }
        System.out.println();
        
        // Run each test case
        for (TestCase testCase : TEST_CASES) {
            runTestCase(testCase);
        }
    }
    
    /**
     * Run a single test case
     */
    private static void runTestCase(TestCase testCase) {
        String goldenFileName = testCase.getGoldenFileName();
        File goldenFile = new File(CASES_DIR, goldenFileName);
        
        totalTests++;
        String programName = testCase.getProgramName();
        System.out.println("🧪 Running test case: " + testCase.name + " (" + programName + ")");
        
        try {
            // Execute the test and capture output
            String actualOutput = executeTest(testCase);
            if (!goldenFile.exists()) {
                // Generate golden file
                System.out.println("   📝 Generating golden file: " + goldenFileName);
                writeFile(goldenFile.getAbsolutePath(), actualOutput);
                generatedGoldenFiles++;
                failedTests++;
                System.out.println("   ❌ FAIL - Golden file generated, please review and re-run");
            } else {
                // Compare with existing golden file
                String expectedOutput = readFile(goldenFile.getAbsolutePath());
                
                if (actualOutput.equals(expectedOutput)) {
                    passedTests++;
                    System.out.println("   ✅ PASS");
                } else {
                    failedTests++;
                    System.out.println("   ❌ FAIL - Output mismatch");
                    System.out.println("   Expected length: " + expectedOutput.length());
                    System.out.println("   Actual length: " + actualOutput.length());
                    
                    // Show first difference for debugging
                    showFirstDifference(expectedOutput, actualOutput);
                }
            }
            
        } catch (Exception e) {
            failedTests++;
            System.out.println("   ❌ FAIL - Exception: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("   Caused by: " + e.getCause().getMessage());
            }
            // Print stack trace for debugging
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * Execute a test case and return the captured MockHardware output
     */
    private static String executeTest(TestCase testCase) throws Exception {
        // Read XML content
        File programFile = new File(CASES_DIR, testCase.programFile);
        String xmlContent = readFile(programFile.getAbsolutePath());
        
        // Parse XML
        ShallowXMLParser parser = new ShallowXMLParser();
        IXMLElement program = parser.parseXML(xmlContent);
        if (program == null) {
            throw new Exception("Failed to parse XML");
        }
        
        // Determine if this test has scenarios
        boolean hasScenarios = testCase.scenario.hasScenarios();
        List<SensorScenario> scenarios;
        if (hasScenarios) {
            System.out.println("   🔄 Applying " + testCase.scenario.getScenarioCount() + " scenario(s)");
            scenarios = testCase.scenario.build();
        } else {
            scenarios = new ArrayList<SensorScenario>();
        }
        // Parse configuration first to create properly configured MockHardware
        RobotConfiguration robotConfig = ConfigurationBlockExecutor.parseConfigFromProgram(program);
        MockHardware mockHardware = new MockHardware(robotConfig);
        TestNepoBlockExecutor executor = new TestNepoBlockExecutor(mockHardware, scenarios);
        executor.setMaxTotalIterations(testCase.maxIterations);

        // Run the complete program (handles config and program sections)
        executor.runProgram(program);
        
        // Get the hardware interaction log
        List<String> log = mockHardware.getLog();
        
        // Format the log as the golden file content
        StringBuilder output = new StringBuilder();
        
        if (log.isEmpty()) {
            output.append("(No hardware interactions)\n");
        } else {
            for (String logEntry : log) {
                output.append(logEntry).append("\n");
            }
        }
        
        return output.toString();
    }
    
    /**
     * Read file content
     */
    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    /**
     * Write content to file
     */
    private static void writeFile(String filePath, String content) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs();
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.print(content);
        }
    }
    
    /**
     * Show first difference between expected and actual output
     */
    private static void showFirstDifference(String expected, String actual) {
        int minLength = Math.min(expected.length(), actual.length());
        
        for (int i = 0; i < minLength; i++) {
            if (expected.charAt(i) != actual.charAt(i)) {
                System.out.println("   First difference at position " + i + ":");
                System.out.println("   Expected: '" + expected.charAt(i) + "' (0x" + 
                    Integer.toHexString(expected.charAt(i)) + ")");
                System.out.println("   Actual:   '" + actual.charAt(i) + "' (0x" + 
                    Integer.toHexString(actual.charAt(i)) + ")");
                
                // Show context around the difference
                int start = Math.max(0, i - 10);
                int end = Math.min(expected.length(), i + 10);
                System.out.println("   Context expected: \"" + 
                    expected.substring(start, end).replace("\n", "\\n") + "\"");
                
                end = Math.min(actual.length(), i + 10);
                System.out.println("   Context actual:   \"" + 
                    actual.substring(start, end).replace("\n", "\\n") + "\"");
                return;
            }
        }
        
        if (expected.length() != actual.length()) {
            System.out.println("   Difference: length mismatch");
            System.out.println("   Expected length: " + expected.length());
            System.out.println("   Actual length: " + actual.length());
        }
    }
    
    /**
     * Print test summary
     */
    private static void printSummary() {
        System.out.println("=== Integration Test Summary ===");
        System.out.println("Total tests: " + totalTests);
        System.out.println("Passed: " + passedTests);
        System.out.println("Failed: " + failedTests);
        
        if (generatedGoldenFiles > 0) {
            System.out.println("Generated golden files: " + generatedGoldenFiles);
            System.out.println();
            System.out.println("⚠️  Golden files were generated for new test cases.");
            System.out.println("   Please review the generated .golden files and re-run tests.");
        }
        
        if (failedTests == 0 && generatedGoldenFiles == 0) {
            System.out.println("🎉 All tests passed!");
        } else if (generatedGoldenFiles > 0) {
            System.out.println("📝 Review generated golden files and re-run");
        } else {
            System.out.println("❌ " + failedTests + " test(s) failed");
        }
    }
}