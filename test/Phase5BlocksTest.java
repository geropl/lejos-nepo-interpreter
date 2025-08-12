import lejos.nxt.*;
import java.util.*;

/**
 * Test suite for Phase 5 Advanced Features blocks
 */
public class Phase5BlocksTest {
    
    private NepoBlockExecutor executor;
    private SimpleXMLParser.XMLElement createTestBlock(String type) {
        SimpleXMLParser.XMLElement block = new SimpleXMLParser.XMLElement();
        block.setAttribute("type", type);
        return block;
    }
    
    public void setUp() {
        executor = new NepoBlockExecutor();
    }
    
    public void testTextJoin() {
        System.out.println("Testing text join...");
        
        SimpleXMLParser.XMLElement joinBlock = createTestBlock("text_join");
        
        SimpleXMLParser.XMLElement aBlock = createTestBlock("text");
        aBlock.setAttribute("TEXT", "Hello ");
        joinBlock.addChild("A", aBlock);
        
        SimpleXMLParser.XMLElement bBlock = createTestBlock("text");
        bBlock.setAttribute("TEXT", "World!");
        joinBlock.addChild("B", bBlock);
        
        Object result = executor.executeBlock(joinBlock);
        System.out.println("Text join result: '" + result + "'");
        
        // Test with numbers
        SimpleXMLParser.XMLElement joinBlock2 = createTestBlock("text_join");
        SimpleXMLParser.XMLElement numBlock1 = createTestBlock("math_number");
        numBlock1.setAttribute("NUM", "42");
        joinBlock2.addChild("A", numBlock1);
        
        SimpleXMLParser.XMLElement numBlock2 = createTestBlock("math_number");
        numBlock2.setAttribute("NUM", "24");
        joinBlock2.addChild("B", numBlock2);
        
        Object result2 = executor.executeBlock(joinBlock2);
        System.out.println("Number join result: '" + result2 + "'");
        
        System.out.println("Text join test completed");
    }
    
    public void testMathRandom() {
        System.out.println("Testing math random int...");
        
        SimpleXMLParser.XMLElement randomBlock = createTestBlock("math_random_int");
        
        SimpleXMLParser.XMLElement fromBlock = createTestBlock("math_number");
        fromBlock.setAttribute("NUM", "1");
        randomBlock.addChild("FROM", fromBlock);
        
        SimpleXMLParser.XMLElement toBlock = createTestBlock("math_number");
        toBlock.setAttribute("NUM", "10");
        randomBlock.addChild("TO", toBlock);
        
        // Test multiple random values
        System.out.println("Random integers (1-10):");
        for (int i = 0; i < 5; i++) {
            Object result = executor.executeBlock(randomBlock);
            System.out.println("  Random " + (i+1) + ": " + result);
        }
        
        // Test with reversed range
        SimpleXMLParser.XMLElement randomBlock2 = createTestBlock("math_random_int");
        SimpleXMLParser.XMLElement fromBlock2 = createTestBlock("math_number");
        fromBlock2.setAttribute("NUM", "100");
        randomBlock2.addChild("FROM", fromBlock2);
        
        SimpleXMLParser.XMLElement toBlock2 = createTestBlock("math_number");
        toBlock2.setAttribute("NUM", "90");
        randomBlock2.addChild("TO", toBlock2);
        
        Object result2 = executor.executeBlock(randomBlock2);
        System.out.println("Reversed range (100-90): " + result2);
        
        System.out.println("Math random test completed");
    }
    
    public void testDisplayClear() {
        System.out.println("Testing display clear...");
        
        // First display some text
        SimpleXMLParser.XMLElement displayBlock = createTestBlock("robActions_display_text");
        SimpleXMLParser.XMLElement textBlock = createTestBlock("text");
        textBlock.setAttribute("TEXT", "Test Message");
        displayBlock.addChild("OUT", textBlock);
        
        executor.executeBlock(displayBlock);
        System.out.println("Displayed text");
        
        // Then clear the display
        SimpleXMLParser.XMLElement clearBlock = createTestBlock("robActions_display_clear");
        executor.executeBlock(clearBlock);
        System.out.println("Display cleared");
        
        System.out.println("Display clear test completed");
    }
    
    public void testWaitUntil() {
        System.out.println("Testing wait until...");
        
        SimpleXMLParser.XMLElement waitBlock = createTestBlock("robControls_waitUntil");
        SimpleXMLParser.XMLElement conditionBlock = createTestBlock("logic_boolean");
        conditionBlock.setAttribute("BOOL", "true");
        waitBlock.addChild("CONDITION", conditionBlock);
        
        long startTime = System.currentTimeMillis();
        executor.executeBlock(waitBlock);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Wait until completed in " + (endTime - startTime) + "ms");
        
        System.out.println("Wait until test completed");
    }
    
    public void testMotorSetSpeed() {
        System.out.println("Testing motor set speed...");
        
        SimpleXMLParser.XMLElement setSpeedBlock = createTestBlock("robActions_motor_setSpeed");
        setSpeedBlock.setAttribute("MOTORPORT", "B");
        
        SimpleXMLParser.XMLElement speedBlock = createTestBlock("math_number");
        speedBlock.setAttribute("NUM", "360");
        setSpeedBlock.addChild("SPEED", speedBlock);
        
        executor.executeBlock(setSpeedBlock);
        System.out.println("Motor B speed set to 360");
        
        // Test with different motor and speed
        SimpleXMLParser.XMLElement setSpeedBlock2 = createTestBlock("robActions_motor_setSpeed");
        setSpeedBlock2.setAttribute("MOTORPORT", "C");
        
        SimpleXMLParser.XMLElement speedBlock2 = createTestBlock("math_number");
        speedBlock2.setAttribute("NUM", "180");
        setSpeedBlock2.addChild("SPEED", speedBlock2);
        
        executor.executeBlock(setSpeedBlock2);
        System.out.println("Motor C speed set to 180");
        
        System.out.println("Motor set speed test completed");
    }
    
    public void testEncoderRotation() {
        System.out.println("Testing encoder rotation...");
        
        SimpleXMLParser.XMLElement encoderBlock = createTestBlock("robSensors_encoder_rotation");
        encoderBlock.setAttribute("MOTORPORT", "B");
        
        Object result = executor.executeBlock(encoderBlock);
        System.out.println("Motor B encoder rotation: " + result);
        
        // Test different motor
        SimpleXMLParser.XMLElement encoderBlock2 = createTestBlock("robSensors_encoder_rotation");
        encoderBlock2.setAttribute("MOTORPORT", "C");
        
        Object result2 = executor.executeBlock(encoderBlock2);
        System.out.println("Motor C encoder rotation: " + result2);
        
        System.out.println("Encoder rotation test completed");
    }
    
    public void testLEDControl() {
        System.out.println("Testing LED control...");
        
        // Test LED on
        SimpleXMLParser.XMLElement ledOnBlock = createTestBlock("robActions_led_on");
        ledOnBlock.setAttribute("COLOR", "orange");
        
        executor.executeBlock(ledOnBlock);
        System.out.println("LED turned on (orange)");
        
        // Test LED off
        SimpleXMLParser.XMLElement ledOffBlock = createTestBlock("robActions_led_off");
        
        executor.executeBlock(ledOffBlock);
        System.out.println("LED turned off");
        
        System.out.println("LED control test completed");
    }
    
    public void testComplexIntegration() {
        System.out.println("Testing complex Phase 5 integration...");
        
        // Create a complex scenario using multiple Phase 5 blocks
        
        // 1. Generate random number
        SimpleXMLParser.XMLElement randomBlock = createTestBlock("math_random_int");
        SimpleXMLParser.XMLElement fromBlock = createTestBlock("math_number");
        fromBlock.setAttribute("NUM", "1");
        randomBlock.addChild("FROM", fromBlock);
        SimpleXMLParser.XMLElement toBlock = createTestBlock("math_number");
        toBlock.setAttribute("NUM", "5");
        randomBlock.addChild("TO", toBlock);
        
        Object randomResult = executor.executeBlock(randomBlock);
        System.out.println("Generated random number: " + randomResult);
        
        // 2. Join text with the random number
        SimpleXMLParser.XMLElement joinBlock = createTestBlock("text_join");
        SimpleXMLParser.XMLElement textBlock = createTestBlock("text");
        textBlock.setAttribute("TEXT", "Random: ");
        joinBlock.addChild("A", textBlock);
        joinBlock.addChild("B", randomBlock);
        
        Object joinResult = executor.executeBlock(joinBlock);
        System.out.println("Joined text: '" + joinResult + "'");
        
        // 3. Display the result
        SimpleXMLParser.XMLElement displayBlock = createTestBlock("robActions_display_text");
        displayBlock.addChild("OUT", joinBlock);
        
        executor.executeBlock(displayBlock);
        System.out.println("Displayed joined text");
        
        System.out.println("Complex integration test completed");
    }
    
    public void runAllTests() {
        setUp();
        testTextJoin();
        testMathRandom();
        testDisplayClear();
        testWaitUntil();
        testMotorSetSpeed();
        testEncoderRotation();
        testLEDControl();
        testComplexIntegration();
        System.out.println("All Phase 5 tests completed!");
    }
    
    public static void main(String[] args) {
        Phase5BlocksTest test = new Phase5BlocksTest();
        test.runAllTests();
    }
}
