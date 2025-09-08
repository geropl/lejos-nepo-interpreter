import java.util.*;

/**
 * Table-driven unit test for ShallowXMLElement parsing
 * 
 * Tests that ShallowXMLElement correctly parses XML content by comparing
 * the output of getChildren() methods to expected tree structures.
 */
public class TestXMLElement {
    
    /**
     * Test case structure for XML element testing
     */
    private static class TestCase {
        String name;
        String content;
        ExpectedElement expected;
        
        TestCase(String name, String content, ExpectedElement expected) {
            this.name = name;
            this.content = content;
            this.expected = expected;
        }
    }
    
    /**
     * Expected element structure for comparison
     */
    private static class ExpectedElement {
        String tagName;
        Map<String, String> attributes;
        List<ExpectedElement> children;
        
        ExpectedElement(String tagName) {
            this.tagName = tagName;
            this.attributes = new HashMap<String, String>();
            this.children = new ArrayList<ExpectedElement>();
        }
        
        ExpectedElement attr(String name, String value) {
            attributes.put(name, value);
            return this;
        }
        
        ExpectedElement child(ExpectedElement child) {
            children.add(child);
            return this;
        }
        
        ExpectedElement children(ExpectedElement... children) {
            for (ExpectedElement child : children) {
                this.children.add(child);
            }
            return this;
        }
    }
    
    /**
     * Test cases for XML element parsing
     */
    private static final TestCase[] TEST_CASES = {
        new TestCase(
            "Simple element with no children",
            "<root></root>",
            new ExpectedElement("root")
        ),
        
        new TestCase(
            "Element with attributes",
            "<block type=\"test\" id=\"123\"></block>",
            new ExpectedElement("block")
                .attr("type", "test")
                .attr("id", "123")
        ),
        
        new TestCase(
            "Element with single child",
            "<root><child/></root>",
            new ExpectedElement("root")
                .child(new ExpectedElement("child"))
        ),
        
        new TestCase(
            "Element with multiple children",
            "<root><child1/><child2/></root>",
            new ExpectedElement("root")
                .children(
                    new ExpectedElement("child1"),
                    new ExpectedElement("child2")
                )
        ),
        
        new TestCase(
            "Block with statement and value - THE FAILING CASE",
            "<block type=\"test\"><statement name=\"DO\"><block type=\"inner\"/></statement><value name=\"PARAM\"><block type=\"math_number\"/></value></block>",
            new ExpectedElement("block")
                .attr("type", "test")
                .children(
                    new ExpectedElement("statement")
                        .attr("name", "DO")
                        .child(new ExpectedElement("block").attr("type", "inner")),
                    new ExpectedElement("value")
                        .attr("name", "PARAM")
                        .child(new ExpectedElement("block").attr("type", "math_number"))
                )
        ),
        
        new TestCase(
            "Nested blocks with same tag name",
            "<block type=\"outer\"><block type=\"inner\"/></block>",
            new ExpectedElement("block")
                .attr("type", "outer")
                .child(new ExpectedElement("block").attr("type", "inner"))
        ),
        
        new TestCase(
            "Config structure",
            "<config><block_set><instance><block type=\"robBrick_EV3-Brick\"><field name=\"WHEEL_DIAMETER\">5.6</field></block></instance></block_set></config>",
            new ExpectedElement("config")
                .child(new ExpectedElement("block_set")
                    .child(new ExpectedElement("instance")
                        .child(new ExpectedElement("block")
                            .attr("type", "robBrick_EV3-Brick")
                            .child(new ExpectedElement("field")
                                .attr("name", "WHEEL_DIAMETER")))))
        ),
        
        new TestCase(
            "Self-closing tags mixed with regular tags",
            "<root><self-closing/><regular><inner/></regular></root>",
            new ExpectedElement("root")
                .children(
                    new ExpectedElement("self-closing"),
                    new ExpectedElement("regular")
                        .child(new ExpectedElement("inner"))
                )
        ),
        
        new TestCase(
            "Complex NEPO structure",
            "<block type=\"robControls_loopForever\"><statement name=\"DO\"><block type=\"robActions_motorDiff_on\"><field name=\"DIRECTION\">FOREWARD</field><value name=\"POWER\"><block type=\"math_number\"><field name=\"NUM\">30</field></block></value></block></statement></block>",
            new ExpectedElement("block")
                .attr("type", "robControls_loopForever")
                .child(new ExpectedElement("statement")
                    .attr("name", "DO")
                    .child(new ExpectedElement("block")
                        .attr("type", "robActions_motorDiff_on")
                        .children(
                            new ExpectedElement("field")
                                .attr("name", "DIRECTION"),
                            new ExpectedElement("value")
                                .attr("name", "POWER")
                                .child(new ExpectedElement("block")
                                    .attr("type", "math_number")
                                    .child(new ExpectedElement("field")
                                        .attr("name", "NUM"))))))
        )
    };
    
    public static void main(String[] args) {
        System.out.println("=== ShallowXMLElement Table-Driven Test ===");
        System.out.println();
        
        int passed = 0;
        int failed = 0;
        
        for (TestCase testCase : TEST_CASES) {
            System.out.println("Test: " + testCase.name);
            
            try {
                // Parse the XML content
                ShallowXMLParser parser = new ShallowXMLParser();
                IXMLElement actual = parser.parseXML(testCase.content);
                
                if (actual == null) {
                    System.out.println("❌ FAIL: Parser returned null");
                    failed++;
                    continue;
                }
                
                // Compare with expected structure
                boolean matches = compareElements(actual, testCase.expected, "");
                
                if (matches) {
                    System.out.println("✓ PASS");
                    passed++;
                } else {
                    System.out.println("❌ FAIL: Structure mismatch");
                    failed++;
                }
                
            } catch (Exception e) {
                System.out.println("❌ FAIL: Exception - " + e.getMessage());
                failed++;
            }
            
            System.out.println();
        }
        
        System.out.println("=== Test Results ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
        
        if (failed > 0) {
            System.out.println("❌ " + failed + " test(s) failed");
            System.exit(1);
        } else {
            System.out.println("🎉 All tests passed!");
            System.exit(0);
        }
    }
    
    /**
     * Compare actual IXMLElement with expected structure
     */
    private static boolean compareElements(IXMLElement actual, ExpectedElement expected, String path) {
        String currentPath = path.isEmpty() ? expected.tagName : path + "/" + expected.tagName;
        
        // Check tag name
        if (!expected.tagName.equals(actual.getTagName())) {
            System.out.println("  Tag name mismatch at " + currentPath + ": expected '" + expected.tagName + "', got '" + actual.getTagName() + "'");
            return false;
        }
        
        // Check attributes
        for (Map.Entry<String, String> expectedAttr : expected.attributes.entrySet()) {
            IString actualValue = actual.getAttribute(expectedAttr.getKey());
            if (actualValue == null) {
                System.out.println("  Missing attribute at " + currentPath + ": " + expectedAttr.getKey());
                return false;
            }
            if (!expectedAttr.getValue().equals(actualValue.toString())) {
                System.out.println("  Attribute value mismatch at " + currentPath + "." + expectedAttr.getKey() + ": expected '" + expectedAttr.getValue() + "', got '" + actualValue.toString() + "'");
                return false;
            }
        }
        
        // Check children count
        Vector<IXMLElement> actualChildren = actual.getAllChildren();
        if (expected.children.size() != actualChildren.size()) {
            System.out.println("  Children count mismatch at " + currentPath + ": expected " + expected.children.size() + ", got " + actualChildren.size());
            
            // Show what children we actually got
            System.out.println("    Expected children:");
            for (int i = 0; i < expected.children.size(); i++) {
                System.out.println("      " + i + ": " + expected.children.get(i).tagName);
            }
            System.out.println("    Actual children:");
            for (int i = 0; i < actualChildren.size(); i++) {
                IXMLElement child = actualChildren.elementAt(i);
                IString type = child.getAttribute("type");
                String typeInfo = type != null ? " (type=" + type.toString() + ")" : "";
                System.out.println("      " + i + ": " + child.getTagName() + typeInfo);
            }
            
            return false;
        }
        
        // Check each child recursively
        for (int i = 0; i < expected.children.size(); i++) {
            ExpectedElement expectedChild = expected.children.get(i);
            IXMLElement actualChild = actualChildren.elementAt(i);
            
            if (!compareElements(actualChild, expectedChild, currentPath)) {
                return false;
            }
        }
        
        return true;
    }
}