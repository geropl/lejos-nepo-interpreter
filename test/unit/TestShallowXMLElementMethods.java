import java.util.*;

/**
 * Table-driven test for ShallowXMLElement helper methods
 * 
 * Tests that helper methods like getChildren(), getAllChildren(), getChild()
 * work correctly on parsed XML documents by comparing their results.
 */
public class TestShallowXMLElementMethods {
    
    /**
     * Test case structure for method testing
     */
    private static class TestCase {
        String name;
        String inputXML;
        String targetPath; // Path to element to test (e.g., "config/block_set/instance")
        
        TestCase(String name, String inputXML, String targetPath) {
            this.name = name;
            this.inputXML = inputXML;
            this.targetPath = targetPath;
        }
    }
    
    /**
     * Test cases for XML element method testing
     */
    private static final TestCase[] TEST_CASES = {
        new TestCase(
            "Simple nested structure",
            "<root><child1><grandchild/></child1><child2/></root>",
            "root"
        ),
        
        new TestCase(
            "Block with statement and value",
            "<block type=\"test\">" +
            "<statement name=\"DO\"><block type=\"inner\"/></statement>" +
            "<value name=\"PARAM\"><block type=\"math_number\"/></value>" +
            "</block>",
            "block"
        ),
        
        new TestCase(
            "Config structure from programm1.xml",
            "<config>" +
            "<block_set xmlns=\"http://de.fhg.iais.roberta.blockly\" robottype=\"nxt\">" +
            "<instance x=\"470\" y=\"50\">" +
            "<block type=\"robBrick_EV3-Brick\" id=\"1\">" +
            "<field name=\"WHEEL_DIAMETER\">5.6</field>" +
            "<field name=\"TRACK_WIDTH\">11</field>" +
            "<value name=\"MA\">" +
            "<block type=\"robBrick_motor_big\" id=\"6\">" +
            "<field name=\"MOTOR_REGULATION\">TRUE</field>" +
            "</block>" +
            "</value>" +
            "</block>" +
            "</instance>" +
            "</block_set>" +
            "</config>",
            "config"
        ),
        
        new TestCase(
            "Instance element specifically",
            "<config>" +
            "<block_set xmlns=\"http://de.fhg.iais.roberta.blockly\" robottype=\"nxt\">" +
            "<instance x=\"470\" y=\"50\">" +
            "<block type=\"robBrick_EV3-Brick\" id=\"1\">" +
            "<field name=\"WHEEL_DIAMETER\">5.6</field>" +
            "<field name=\"TRACK_WIDTH\">11</field>" +
            "</block>" +
            "</instance>" +
            "</block_set>" +
            "</config>",
            "config/block_set/instance"
        ),
        
        new TestCase(
            "Multiple blocks in instance",
            "<instance x=\"53\" y=\"62\">" +
            "<block type=\"robControls_start\" id=\"1\"/>" +
            "<block type=\"robControls_loopForever\" id=\"2\">" +
            "<statement name=\"DO\">" +
            "<block type=\"robActions_motorDiff_on\" id=\"3\"/>" +
            "</statement>" +
            "</block>" +
            "</instance>",
            "instance"
        ),
        
        new TestCase(
            "Block with multiple values",
            "<block type=\"robControls_wait\" id=\"test\">" +
            "<value name=\"WAIT0\">" +
            "<block type=\"logic_operation\" id=\"op\">" +
            "<field name=\"OP\">OR</field>" +
            "<value name=\"A\">" +
            "<block type=\"logic_compare\" id=\"cmp\"/>" +
            "</value>" +
            "<value name=\"B\">" +
            "<block type=\"robSensors_touch_getSample\" id=\"touch\"/>" +
            "</value>" +
            "</block>" +
            "</value>" +
            "</block>",
            "block"
        )
    };
    
    public static void main(String[] args) {
        System.out.println("=== ShallowXMLElement Helper Methods Test ===");
        System.out.println();
        
        int passed = 0;
        int failed = 0;
        
        for (TestCase testCase : TEST_CASES) {
            System.out.println("Test: " + testCase.name);
            System.out.println("XML: " + testCase.inputXML);
            System.out.println("Target path: " + testCase.targetPath);
            System.out.println();
            
            try {
                ShallowXMLParser parser = new ShallowXMLParser();
                IXMLElement root = parser.parseXML(testCase.inputXML);
                
                // Navigate to target element
                IXMLElement target = navigateToPath(root, testCase.targetPath);
                if (target == null) {
                    System.out.println("❌ FAIL: Could not navigate to target path");
                    failed++;
                    continue;
                }
                
                // Test all helper methods on the target element
                testElementMethods(target, testCase.targetPath);
                
                System.out.println("✓ PASS");
                passed++;
                
            } catch (Exception e) {
                System.out.println("❌ FAIL: Exception - " + e.getMessage());
                e.printStackTrace();
                failed++;
            }
            
            System.out.println();
            System.out.println("----------------------------------------");
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
     * Navigate to a specific path in the XML tree
     */
    private static IXMLElement navigateToPath(IXMLElement root, String path) {
        if (path.equals(root.getTagName())) {
            return root;
        }
        
        String[] parts = path.split("/");
        IXMLElement current = root;
        
        for (String part : parts) {
            if (part.equals(current.getTagName())) {
                continue; // Skip if it's the current element
            }
            
            IXMLElement child = current.getChild(part);
            if (child == null) {
                System.out.println("Could not find child: " + part + " in " + current.getTagName());
                return null;
            }
            current = child;
        }
        
        return current;
    }
    
    /**
     * Test all helper methods on an element and print results
     */
    private static void testElementMethods(IXMLElement element, String elementPath) {
        System.out.println("Testing methods on element: " + elementPath + " (tag: " + element.getTagName() + ")");
        
        // Test getAttributes()
        Map attributes = element.getAttributes();
        System.out.println("  getAttributes(): " + attributes.size() + " attributes");
        for (Object key : attributes.keySet()) {
            Object value = attributes.get(key);
            System.out.println("    " + key + " = " + value);
        }
        
        // Test getAttribute() for common attributes
        String[] commonAttrs = {"type", "id", "name", "x", "y", "intask", "deletable"};
        for (String attr : commonAttrs) {
            IString value = element.getAttribute(attr);
            if (value != null) {
                System.out.println("  getAttribute('" + attr + "'): " + value.toString());
            }
        }
        
        // Test getAllChildren()
        Vector allChildren = element.getAllChildren();
        System.out.println("  getAllChildren(): " + allChildren.size() + " children");
        for (int i = 0; i < allChildren.size(); i++) {
            IXMLElement child = (IXMLElement) allChildren.elementAt(i);
            IString childType = child.getAttribute("type");
            String typeInfo = childType != null ? " (type=" + childType.toString() + ")" : "";
            System.out.println("    " + i + ": " + child.getTagName() + typeInfo);
        }
        
        // Test getChildren() for common child types
        String[] commonChildTypes = {"block", "field", "value", "statement", "instance", "block_set"};
        for (String childType : commonChildTypes) {
            Vector children = element.getChildren(childType);
            if (children.size() > 0) {
                System.out.println("  getChildren('" + childType + "'): " + children.size() + " children");
                for (int i = 0; i < children.size(); i++) {
                    IXMLElement child = (IXMLElement) children.elementAt(i);
                    IString type = child.getAttribute("type");
                    String typeInfo = type != null ? " (type=" + type.toString() + ")" : "";
                    System.out.println("    " + i + ": " + child.getTagName() + typeInfo);
                }
            }
        }
        
        // Test getChild() for common child types
        for (String childType : commonChildTypes) {
            IXMLElement child = element.getChild(childType);
            if (child != null) {
                IString type = child.getAttribute("type");
                String typeInfo = type != null ? " (type=" + type.toString() + ")" : "";
                System.out.println("  getChild('" + childType + "'): " + child.getTagName() + typeInfo);
            }
        }
        
        // Test getTextContent()
        String textContent = element.getTextContent();
        if (textContent != null && !textContent.trim().isEmpty()) {
            System.out.println("  getTextContent(): '" + textContent + "'");
        }
        
        // Test getMemoryFootprint()
        int memoryFootprint = element.getMemoryFootprint();
        System.out.println("  getMemoryFootprint(): " + memoryFootprint + " bytes");
    }
}