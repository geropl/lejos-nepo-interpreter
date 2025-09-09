import java.util.*;

/**
 * Test to verify XML tag balancing in ShallowXMLElement parsing
 * 
 * This test specifically examines the findMatchingClosingTag method
 * and parseChildElements to see if they handle nested blocks correctly.
 */
public class TestXMLTagBalancing {
    
    public static void main(String[] args) {
        System.out.println("=== XML Tag Balancing Test ===");
        System.out.println();
        
        try {
            // Test the specific case that's failing
            testComplexBlockStructure();
            
            // Test the findMatchingClosingTag method directly
            testFindMatchingClosingTag();
            
            // Test parseChildElements behavior
            testParseChildElements();
            
            System.out.println("🎉 All diagnostic tests completed successfully!");
            System.exit(0);
        } catch (Exception e) {
            System.err.println("❌ Test failed with exception: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Test the complex block structure that's failing
     */
    private static void testComplexBlockStructure() {
        System.out.println("Test: Complex block structure parsing");
        
        String xml = "<block type=\"test\">" +
                    "<statement name=\"DO\">" +
                    "<block type=\"inner\"/>" +
                    "</statement>" +
                    "<value name=\"PARAM\">" +
                    "<block type=\"math_number\"/>" +
                    "</value>" +
                    "</block>";
        
        try {
            ShallowXMLParser parser = new ShallowXMLParser();
            IXMLElement root = parser.parseXML(xml);
            
            System.out.println("Parsed successfully: " + (root != null));
            if (root != null) {
                System.out.println("Root tag: " + root.getTagName());
                System.out.println("Root type: " + root.getAttribute("type"));
                
                Vector allChildren = root.getAllChildren();
                System.out.println("getAllChildren() count: " + allChildren.size());
                
                for (int i = 0; i < allChildren.size(); i++) {
                    IXMLElement child = (IXMLElement) allChildren.elementAt(i);
                    System.out.println("  Child " + i + ": " + child.getTagName());
                    IString nameAttr = child.getAttribute("name");
                    if (nameAttr != null) {
                        System.out.println("    name=" + nameAttr.toString());
                    }
                }
                
                // Test specific child access
                IXMLElement statement = root.getChild("statement");
                IXMLElement value = root.getChild("value");
                System.out.println("getChild('statement'): " + (statement != null ? statement.getTagName() : "null"));
                System.out.println("getChild('value'): " + (value != null ? value.getTagName() : "null"));
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
    
    /**
     * Test the findMatchingClosingTag method directly
     */
    private static void testFindMatchingClosingTag() {
        System.out.println("Test: findMatchingClosingTag method");
        
        // Test case 1: Simple nested blocks
        String content1 = "<statement name=\"DO\"><block type=\"inner\"/></statement><value name=\"PARAM\"><block type=\"math_number\"/></value>";
        ShallowString shallowContent1 = new ShallowString(content1, 0, content1.length());
        
        int closePos1 = ShallowXMLElement.findMatchingClosingTag(shallowContent1, "statement", 18); // After <statement name="DO">
        System.out.println("Finding closing tag for 'statement':");
        System.out.println("  Content: " + content1);
        System.out.println("  Start pos: 18 (after opening tag)");
        System.out.println("  Found closing at: " + closePos1);
        if (closePos1 != -1) {
            System.out.println("  Closing tag: " + content1.substring(closePos1, closePos1 + 12));
        }
        
        // Test case 2: Nested blocks with same tag name
        String content2 = "<block type=\"outer\"><block type=\"inner\"/></block>";
        ShallowString shallowContent2 = new ShallowString(content2, 0, content2.length());
        
        int closePos2 = ShallowXMLElement.findMatchingClosingTag(shallowContent2, "block", 19); // After <block type="outer">
        System.out.println("\nFinding closing tag for nested 'block':");
        System.out.println("  Content: " + content2);
        System.out.println("  Start pos: 19 (after opening tag)");
        System.out.println("  Found closing at: " + closePos2);
        if (closePos2 != -1) {
            System.out.println("  Closing tag: " + content2.substring(closePos2, closePos2 + 8));
        }
        
        System.out.println();
    }
    
    /**
     * Test parseChildElements behavior by examining a ShallowXMLElement directly
     */
    private static void testParseChildElements() {
        System.out.println("Test: parseChildElements behavior");
        
        String innerContent = "<statement name=\"DO\"><block type=\"inner\"/></statement><value name=\"PARAM\"><block type=\"math_number\"/></value>";
        ShallowString shallowInner = new ShallowString(innerContent, 0, innerContent.length());
        
        // Create a ShallowXMLElement with this inner content
        String fullContent = "<block type=\"test\">" + innerContent + "</block>";
        ShallowString shallowFull = new ShallowString(fullContent, 0, fullContent.length());
        ShallowString shallowOpenTag = new ShallowString("<block type=\"test\">", 0, 18);
        
        ShallowXMLElement element = new ShallowXMLElement(shallowFull, shallowOpenTag, shallowInner);
        
        System.out.println("Created element with inner content: " + innerContent);
        System.out.println("Element tag: " + element.getTagName());
        System.out.println("Element type: " + element.getAttribute("type"));
        
        // Force parsing of children
        Vector allChildren = element.getAllChildren();
        System.out.println("getAllChildren() count: " + allChildren.size());
        
        for (int i = 0; i < allChildren.size(); i++) {
            IXMLElement child = (IXMLElement) allChildren.elementAt(i);
            System.out.println("  Child " + i + ": " + child.getTagName());
            IString nameAttr = child.getAttribute("name");
            IString typeAttr = child.getAttribute("type");
            if (nameAttr != null) {
                System.out.println("    name=" + nameAttr.toString());
            }
            if (typeAttr != null) {
                System.out.println("    type=" + typeAttr.toString());
            }
            
            // Check if this child has its own children
            Vector grandChildren = child.getAllChildren();
            System.out.println("    grandchildren: " + grandChildren.size());
            for (int j = 0; j < grandChildren.size(); j++) {
                IXMLElement grandChild = (IXMLElement) grandChildren.elementAt(j);
                System.out.println("      " + j + ": " + grandChild.getTagName());
                IString grandType = grandChild.getAttribute("type");
                if (grandType != null) {
                    System.out.println("        type=" + grandType.toString());
                }
            }
        }
        
        System.out.println();
    }
}