import java.util.*;

/**
 * Table-driven test for ShallowXMLParser
 * 
 * Tests XML parsing capabilities by comparing input XML strings
 * to expected output representations.
 */
public class TestXMLParser {
    
    /**
     * Test case structure for table-driven testing
     */
    private static class TestCase {
        String name;
        String inputXML;
        String expectedOutput;
        boolean shouldPass;
        
        TestCase(String name, String inputXML, String expectedOutput, boolean shouldPass) {
            this.name = name;
            this.inputXML = inputXML;
            this.expectedOutput = expectedOutput;
            this.shouldPass = shouldPass;
        }
        
        TestCase(String name, String inputXML, String expectedOutput) {
            this(name, inputXML, expectedOutput, true);
        }
    }
    
    /**
     * Test cases for XML parsing
     */
    private static final TestCase[] TEST_CASES = {
        // Basic element tests
        new TestCase(
            "Simple element",
            "<root></root>",
            "<root/>"
        ),
        
        new TestCase(
            "Element with text content",
            "<root>Hello World</root>",
            "<root>Hello World</root>"
        ),
        
        new TestCase(
            "Element with single attribute",
            "<root id=\"123\"></root>",
            "<root id=\"123\"/>"
        ),
        
        new TestCase(
            "Element with multiple attributes",
            "<root id=\"123\" name=\"test\"></root>",
            "<root id=\"123\" name=\"test\"/>"
        ),

        // Nested element tests
        new TestCase(
            "Simple nested elements",
            "<root><child></child></root>",
            "<root>\n  <child/>\n</root>"
        ),
        
        new TestCase(
            "Multiple children",
            "<root><child1></child1><child2></child2></root>",
            "<root>\n  <child1/>\n  <child2/>\n</root>"
        ),
        
        new TestCase(
            "Deeply nested elements",
            "<root><level1><level2><level3></level3></level2></level1></root>",
            "<root>\n  <level1>\n    <level2>\n      <level3/>\n    </level2>\n  </level1>\n</root>"
        ),
        
        // Mixed content tests
        new TestCase(
            "Element with attributes and children",
            "<root id=\"123\"><child name=\"test\"></child></root>",
            "<root id=\"123\">\n  <child name=\"test\"/>\n</root>"
        ),
        
        new TestCase(
            "Element with text and attributes",
            "<root id=\"123\">Hello</root>",
            "<root id=\"123\">Hello</root>"
        ),

        // Complex structure tests
        new TestCase(
            "Block with statement structure",
            "<block type=\"test\"><statement name=\"DO\"><block type=\"inner\"></block></statement></block>",
            "<block type=\"test\">\n  <statement name=\"DO\">\n    <block type=\"inner\"/>\n  </statement>\n</block>"
        ),
        
        new TestCase(
            "Block with value structure",
            "<block type=\"test\"><value name=\"PARAM\"><block type=\"math_number\"><field name=\"NUM\">42</field></block></value></block>",
            "<block type=\"test\">\n  <value name=\"PARAM\">\n    <block type=\"math_number\">\n      <field name=\"NUM\">42</field>\n    </block>\n  </value>\n</block>"
        ),
        
        // Self-closing elements
        new TestCase(
            "Self-closing element",
            "<root/>",
            "<root/>"
        ),
        
        new TestCase(
            "Self-closing with attributes",
            "<root id=\"123\" name=\"test\"/>",
            "<root id=\"123\" name=\"test\"/>"
        ),

        // Additional attribute test cases
        new TestCase(
            "Element with boolean attribute",
            "<block intask=\"true\"></block>",
            "<block intask=\"true\"/>"
        ),
        
        new TestCase(
            "Element with special characters in attribute",
            "<block id=\"G-=VG53Z13LHY]}-9T(|\"></block>",
            "<block id=\"G-=VG53Z13LHY]}-9T(|\"/>"
        ),
        
        new TestCase(
            "Element with numeric attribute",
            "<field name=\"NUM\">42</field>",
            "<field name=\"NUM\">42</field>"
        ),

        // Specific test cases for programm1.xml structure
        new TestCase(
            "Loop forever with statement",
            "<block type=\"robControls_loopForever\"><statement name=\"DO\"><block type=\"robActions_motorDiff_on\"></block></statement></block>",
            "<block type=\"robControls_loopForever\">\n  <statement name=\"DO\">\n    <block type=\"robActions_motorDiff_on\"/>\n  </statement>\n</block>"
        ),
        
        new TestCase(
            "Block with field only",
            "<block type=\"test\"><field name=\"DIRECTION\">FOREWARD</field></block>",
            "<block type=\"test\">\n  <field name=\"DIRECTION\">FOREWARD</field>\n</block>"
        ),
        
        new TestCase(
            "Block with value containing block",
            "<block type=\"test\"><value name=\"POWER\"><block type=\"math_number\"></block></value></block>",
            "<block type=\"test\">\n  <value name=\"POWER\">\n    <block type=\"math_number\"/>\n  </value>\n</block>"
        ),
        
        new TestCase(
            "Minimal statement structure",
            "<statement name=\"DO\"><block type=\"inner\"></block></statement>",
            "<statement name=\"DO\">\n  <block type=\"inner\"/>\n</statement>"
        ),
        
        new TestCase(
            "Minimal value structure", 
            "<value name=\"PARAM\"><block type=\"inner\"></block></value>",
            "<value name=\"PARAM\">\n  <block type=\"inner\"/>\n</value>"
        ),

        // Complex structure test cases (from programm1.xml)
        new TestCase(
            "Real programm1.xml fragment",
            "<block type=\"robControls_loopForever\" id=\"A}3-R{yifQT|F/+ufe))\" intask=\"true\">" +
            "<statement name=\"DO\">" +
            "<block type=\"robActions_motorDiff_on\" id=\"A6j?7eTaolmJMkIR,;pB\" intask=\"true\">" +
            "<field name=\"DIRECTION\">FOREWARD</field>" +
            "<value name=\"POWER\">" +
            "<block type=\"math_number\" id=\"]~_%k|BA{Zk%+P@I;c90\" intask=\"true\">" +
            "<field name=\"NUM\">30</field>" +
            "</block>" +
            "</value>" +
            "</block>" +
            "</statement>" +
            "</block>",
            "<block id=\"A}3-R{yifQT|F/+ufe))\" intask=\"true\" type=\"robControls_loopForever\">\n" +
            "  <statement name=\"DO\">\n" +
            "    <block id=\"A6j?7eTaolmJMkIR,;pB\" intask=\"true\" type=\"robActions_motorDiff_on\">\n" +
            "      <field name=\"DIRECTION\">FOREWARD</field>\n" +
            "      <value name=\"POWER\">\n" +
            "        <block id=\"]~_%k|BA{Zk%+P@I;c90\" intask=\"true\" type=\"math_number\">\n" +
            "          <field name=\"NUM\">30</field>\n" +
            "        </block>\n" +
            "      </value>\n" +
            "    </block>\n" +
            "  </statement>\n" +
            "</block>"
        ),
        
        new TestCase(
            "Export wrapper structure",
            "<export xmlns=\"http://de.fhg.iais.roberta.blockly\">" +
            "<program>" +
            "<block_set>" +
            "<instance>" +
            "<block type=\"test\"></block>" +
            "</instance>" +
            "</block_set>" +
            "</program>" +
            "</export>",
            "<export xmlns=\"http://de.fhg.iais.roberta.blockly\">\n" +
            "  <program>\n" +
            "    <block_set>\n" +
            "      <instance>\n" +
            "        <block type=\"test\"/>\n" +
            "      </instance>\n" +
            "    </block_set>\n" +
            "  </program>\n" +
            "</export>"
        ),

        // Error cases
        new TestCase(
            "Malformed XML - unclosed tag",
            "<root><child></root>",
            "ERROR: Malformed XML",
            false
        ),
        
        new TestCase(
            "Empty input",
            "",
            "ERROR: Empty input",
            false
        )
    };
    
    public static void main(String[] args) {
        System.out.println("=== ShallowXMLParser Table-Driven Tests ===\n");
        
        int passed = 0;
        int failed = 0;
        
        for (TestCase testCase : TEST_CASES) {
            System.out.println("Test: " + testCase.name);
            System.out.println("Input: " + testCase.inputXML);
            System.out.println("Expected: " + testCase.expectedOutput);
            
            try {
                String actualOutput = parseAndFormat(testCase.inputXML);
                System.out.println("Actual: " + actualOutput);
                
                boolean testPassed = actualOutput.equals(testCase.expectedOutput);
                
                if (testCase.shouldPass) {
                    if (testPassed) {
                        System.out.println("✓ PASS\n");
                        passed++;
                    } else {
                        System.out.println("✗ FAIL - Output mismatch\n");
                        failed++;
                    }
                } else {
                    // This test should fail
                    if (testPassed) {
                        System.out.println("✗ FAIL - Expected error but got success\n");
                        failed++;
                    } else {
                        System.out.println("✓ PASS - Expected error occurred\n");
                        passed++;
                    }
                }
                
            } catch (Exception e) {
                String errorOutput = "ERROR: " + e.getMessage();
                System.out.println("Actual: " + errorOutput);
                
                if (!testCase.shouldPass && errorOutput.equals(testCase.expectedOutput)) {
                    System.out.println("✓ PASS - Expected error\n");
                    passed++;
                } else {
                    System.out.println("✗ FAIL - Unexpected error: " + e.getMessage() + "\n");
                    failed++;
                }
            }
        }
        
        System.out.println("=== Test Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
        
        if (failed == 0) {
            System.out.println("🎉 All tests passed!");
        } else {
            System.out.println("❌ " + failed + " test(s) failed");
        }
    }
    
    /**
     * Parse XML and format the result for comparison
     */
    private static String parseAndFormat(String xmlInput) throws Exception {
        if (xmlInput == null || xmlInput.trim().isEmpty()) {
            throw new Exception("Empty input");
        }
        
        ShallowXMLParser parser = new ShallowXMLParser();
        IXMLElement root = parser.parseXML(xmlInput);
        
        if (root == null) {
            throw new Exception("Parser returned null");
        }
        
        return formatElement(root, 0);
    }
    
    /**
     * Format an XML element as regular XML for comparison
     */
    private static String formatElement(IXMLElement element, int depth) {
        StringBuilder result = new StringBuilder();
        String indent = "  ".repeat(depth);
        
        // Start tag
        result.append(indent).append("<").append(element.getTagName());
        
        // Add attributes in consistent order (sorted by key)
        Map<String, IString> attributes = element.getAttributes();
        List<String> sortedKeys = new ArrayList<>(attributes.keySet());
        Collections.sort(sortedKeys);
        
        for (String key : sortedKeys) {
            IString value = attributes.get(key);
            result.append(" ").append(key).append("=\"").append(value.toString()).append("\"");
        }
        
        // Check if element has children or text content
        Vector<IXMLElement> children = element.getAllChildren();
        String textContent = element.getTextContent();
        boolean hasTextContent = textContent != null && !textContent.trim().isEmpty();
        boolean hasChildren = children.size() > 0;
        boolean hasContent = hasChildren || hasTextContent;

        if (!hasContent) {
            // Self-closing tag
            result.append("/>");
        } else {
            // Close opening tag
            result.append(">");
            
            // Add text content if present and no children
            if (!hasChildren && hasTextContent) {
                result.append(textContent.trim());
            }
            
            // Add children
            if (hasChildren) {
                for (int i = 0; i < children.size(); i++) {
                    result.append("\n");
                    result.append(formatElement(children.elementAt(i), depth + 1));
                }
                result.append("\n").append(indent);
            }
            
            // Closing tag
            result.append("</").append(element.getTagName()).append(">");
        }

        return result.toString();
    }
}
