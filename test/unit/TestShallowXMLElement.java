/**
 * Test to verify the centralized utility methods in ShallowXMLElement work correctly
 */
public class TestShallowXMLElement {
    public static void main(String[] args) {
        System.out.println("=== Testing ShallowXMLElement Utility Methods ===\n");
        
        // Test extractTagName
        testExtractTagName();
        
        // Test findMatchingClosingTag
        testFindMatchingClosingTag();
        
        System.out.println("=== All ShallowXMLElement utility method tests completed ===");
    }
    
    private static void testExtractTagName() {
        System.out.println("Testing extractTagName:");
        
        // Test cases for tag name extraction
        String[][] testCases = {
            {"<root>", "root"},
            {"<block type=\"test\">", "block"},
            {"<field name=\"NUM\"/>", "field"},
            {"<export xmlns=\"http://example.com\">", "export"},
            {"<tag>", "tag"},
            {"<self-closing/>", "self-closing"},
            {"<tag attr=\"val\"/>", "tag"},
            {"<>", "unknown"},
            {"", "unknown"},
            {null, "unknown"}
        };
        
        for (String[] testCase : testCases) {
            String input = testCase[0];
            String expected = testCase[1];
            
            IString inputStr = (input != null) ? new ShallowString(input) : null;
            String actual = ShallowXMLElement.extractTagName(inputStr);
            
            boolean passed = expected.equals(actual);
            System.out.println("  Input: " + input + " -> Expected: " + expected + ", Actual: " + actual + " " + (passed ? "✓" : "✗"));
        }
        System.out.println();
    }
    
    private static void testFindMatchingClosingTag() {
        System.out.println("Testing findMatchingClosingTag:");
        
        // Test cases for finding matching closing tags
        Object[][] testCases = {
            {"<root><child></child></root>", "root", 6, 21},  // Simple case
            {"<block><statement><block></block></statement></block>", "block", 7, 45},  // Nested blocks
            {"<tag><inner><deep></deep></inner></tag>", "tag", 5, 33},  // Deep nesting
            {"<root><child></child><child2></child2></root>", "root", 6, 38},  // Multiple children
            {"<single/>", "single", 0, -1},  // Self-closing (no content to search)
            {"<unclosed><child></child>", "unclosed", 11, -1},  // No closing tag
            {"<same><same></same></same>", "same", 6, 19},  // Same tag names nested
            {"<outer><inner attr=\"val\"><deep></deep></inner></outer>", "outer", 7, 46},  // Complex nesting with attributes
        };
        
        for (Object[] testCase : testCases) {
            String content = (String) testCase[0];
            String tagName = (String) testCase[1];
            int startPos = (Integer) testCase[2];
            int expected = (Integer) testCase[3];
            
            IString contentStr = new ShallowString(content);
            int actual = ShallowXMLElement.findMatchingClosingTag(contentStr, tagName, startPos);
            
            boolean passed = (expected == actual);
            System.out.println("  Content: " + content);
            System.out.println("  Tag: " + tagName + ", Start: " + startPos + " -> Expected: " + expected + ", Actual: " + actual + " " + (passed ? "✓" : "✗"));
            System.out.println();
        }
    }
}