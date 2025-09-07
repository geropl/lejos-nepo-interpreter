import java.io.*;
import java.util.*;

/**
 * Integration test framework for NEPO XML programs
 * 
 * Features:
 * - Discovers test cases in test/integration/cases/
 * - Runs XML programs with MockHardware
 * - Generates golden files if missing (test fails)
 * - Compares output with existing golden files
 * - Follows Test... naming convention
 */
public class TestIntegrationFramework {
    
    private static final String CASES_DIR = "test/integration/cases";
    private static final String GOLDEN_EXTENSION = ".golden";
    private static final String XML_EXTENSION = ".xml";
    
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
     * Discover and run all test cases in the cases directory
     */
    private static void runAllTests() {
        File casesDir = new File(CASES_DIR);
        if (!casesDir.exists()) {
            System.err.println("Cases directory not found: " + CASES_DIR);
            System.err.println("Please create the directory and add test cases.");
            return;
        }
        
        // Find all XML files in the cases directory
        File[] xmlFiles = casesDir.listFiles((dir, name) -> name.endsWith(XML_EXTENSION));
        if (xmlFiles == null || xmlFiles.length == 0) {
            System.out.println("No XML test cases found in " + CASES_DIR);
            System.out.println("Add .xml files to create test cases.");
            return;
        }
        
        System.out.println("Found " + xmlFiles.length + " test case(s):");
        for (File xmlFile : xmlFiles) {
            String baseName = getBaseName(xmlFile.getName());
            System.out.println("  - " + baseName);
        }
        System.out.println();
        
        // Run each test case
        for (File xmlFile : xmlFiles) {
            runTestCase(xmlFile);
        }
    }
    
    /**
     * Run a single test case
     */
    private static void runTestCase(File xmlFile) {
        String baseName = getBaseName(xmlFile.getName());
        String goldenFileName = baseName + GOLDEN_EXTENSION;
        File goldenFile = new File(xmlFile.getParent(), goldenFileName);
        
        totalTests++;
        System.out.println("🧪 Running test case: " + baseName);
        
        try {
            // Execute the test and capture output
            String actualOutput = executeTest(xmlFile);
            
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
        }
        
        System.out.println();
    }
    
    /**
     * Execute a test case and return the captured MockHardware output
     */
    private static String executeTest(File xmlFile) throws Exception {
        // Read XML content
        String xmlContent = readFile(xmlFile.getAbsolutePath());
        
        // Parse XML
        ShallowXMLParser parser = new ShallowXMLParser();
        IXMLElement root = parser.parseXML(xmlContent);
        
        if (root == null) {
            throw new Exception("Failed to parse XML");
        }
        
        // Create mock hardware to capture interactions
        MockHardware mockHardware = new MockHardware();
        mockHardware.clearLog(); // Start with clean log
        
        // Create NEPO block executor with mock hardware
        NepoBlockExecutor executor = new NepoBlockExecutor(mockHardware);
        
        // Find and set configuration if present
        IXMLElement config = findElement(root, "config");
        if (config != null) {
            executor.setConfiguration(config);
        }
        
        // Find program section
        IXMLElement program = findElement(root, "program");
        if (program == null) {
            throw new Exception("No program section found in XML");
        }
        
        executor.executeBlock(program);
        
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
     * Find element by tag name
     */
    private static IXMLElement findElement(IXMLElement parent, String tagName) {
        if (tagName.equals(parent.getTagName())) {
            return parent;
        }
        
        Vector<IXMLElement> children = parent.getAllChildren();
        for (int i = 0; i < children.size(); i++) {
            IXMLElement found = findElement(children.elementAt(i), tagName);
            if (found != null) {
                return found;
            }
        }
        return null;
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
     * Get base name without extension
     */
    private static String getBaseName(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
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