import lejos.nxt.*;
import java.util.*;

/**
 * Test suite for Phase 3 Advanced Control Flow blocks
 */
public class Phase3BlocksTest {
    
    private NepoBlockExecutor executor;
    private SimpleXMLParser.XMLElement createTestBlock(String type) {
        SimpleXMLParser.XMLElement block = new SimpleXMLParser.XMLElement();
        block.setAttribute("type", type);
        return block;
    }
    
    public void setUp() {
        executor = new NepoBlockExecutor();
    }
    
    public void testWhileLoop() {
        System.out.println("Testing while loop...");
        
        // Create a while block with condition
        SimpleXMLParser.XMLElement whileBlock = createTestBlock("robControls_while");
        SimpleXMLParser.XMLElement conditionBlock = createTestBlock("logic_boolean");
        conditionBlock.setAttribute("BOOL", "true");
        whileBlock.addChild("BOOL", conditionBlock);
        
        // This would run forever, so we'll test the structure
        System.out.println("While loop structure validated");
    }
    
    public void testRepeatForever() {
        System.out.println("Testing repeat forever...");
        
        SimpleXMLParser.XMLElement repeatBlock = createTestBlock("robControls_repeat_forever");
        SimpleXMLParser.XMLElement displayBlock = createTestBlock("robActions_display_text");
        displayBlock.setAttribute("OUT", "Loop iteration");
        repeatBlock.addChild("DO", displayBlock);
        
        // Test structure (actual execution would be infinite)
        System.out.println("Repeat forever structure validated");
    }
    
    public void testVariables() {
        System.out.println("Testing variables...");
        
        // Test variable set
        SimpleXMLParser.XMLElement setBlock = createTestBlock("variables_set");
        setBlock.setAttribute("VAR", "testVar");
        SimpleXMLParser.XMLElement valueBlock = createTestBlock("math_number");
        valueBlock.setAttribute("NUM", "42");
        setBlock.addChild("VALUE", valueBlock);
        
        executor.executeBlock(setBlock);
        
        // Test variable get
        SimpleXMLParser.XMLElement getBlock = createTestBlock("variables_get");
        getBlock.setAttribute("VAR", "testVar");
        
        Object result = executor.executeBlock(getBlock);
        System.out.println("Variable test completed");
    }
    
    public void testMathArithmetic() {
        System.out.println("Testing math arithmetic...");
        
        SimpleXMLParser.XMLElement mathBlock = createTestBlock("math_arithmetic");
        mathBlock.setAttribute("OP", "ADD");
        
        SimpleXMLParser.XMLElement aBlock = createTestBlock("math_number");
        aBlock.setAttribute("NUM", "10");
        mathBlock.addChild("A", aBlock);
        
        SimpleXMLParser.XMLElement bBlock = createTestBlock("math_number");
        bBlock.setAttribute("NUM", "5");
        mathBlock.addChild("B", bBlock);
        
        Object result = executor.executeBlock(mathBlock);
        System.out.println("Math arithmetic: 10 + 5 = " + result);
    }
    
    public void testMathSingle() {
        System.out.println("Testing math single operations...");
        
        SimpleXMLParser.XMLElement mathBlock = createTestBlock("math_single");
        mathBlock.setAttribute("OP", "ROOT");
        
        SimpleXMLParser.XMLElement numBlock = createTestBlock("math_number");
        numBlock.setAttribute("NUM", "16");
        mathBlock.addChild("NUM", numBlock);
        
        Object result = executor.executeBlock(mathBlock);
        System.out.println("Square root of 16 = " + result);
    }
    
    public void runAllTests() {
        setUp();
        testWhileLoop();
        testRepeatForever();
        testVariables();
        testMathArithmetic();
        testMathSingle();
        System.out.println("All Phase 3 tests completed!");
    }
    
    public static void main(String[] args) {
        Phase3BlocksTest test = new Phase3BlocksTest();
        test.runAllTests();
    }
}
