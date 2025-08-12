/**
 * UltrasonicDistanceTest - Test ultrasonic distance sensor functionality
 */
public class UltrasonicDistanceTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Ultrasonic Distance Block ===");
        
        // Create test case for ultrasonic distance reading
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robControls_if\" id=\"2\">\n" +
                    "          <value name=\"IF0\">\n" +
                    "            <block type=\"logic_compare\" id=\"3\">\n" +
                    "              <field name=\"OP\">LT</field>\n" +
                    "              <value name=\"A\">\n" +
                    "                <block type=\"robSensors_ultrasonic_distance\" id=\"4\">\n" +
                    "                  <field name=\"SENSORPORT\">4</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "              <value name=\"B\">\n" +
                    "                <block type=\"math_number\" id=\"5\">\n" +
                    "                  <field name=\"NUM\">30</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <statement name=\"DO0\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"6\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"7\">\n" +
                    "                  <field name=\"TEXT\">Object detected!</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        // Create test framework
        NepoTestFramework framework = new NepoTestFramework();
        framework.setVerboseOutput(true);
        
        // Create test case
        BlockTestCase testCase = new BlockTestCase("ultrasonic_distance_test", xml)
            .description("Test ultrasonic distance sensor with conditional")
            .category("integration")
            .blockType("robSensors_ultrasonic_distance")
            .withSensor("4", "ultrasonic", new Integer(25)) // Distance < 30
            .expectDisplayLine(0, "Object detected!")
            .expectMaxTime(1000);
        
        // Execute test
        TestResult result = framework.executeTest(testCase);
        
        // Display result
        System.out.println("Test: " + result.getTestName());
        System.out.println("Status: " + (result.passed() ? "PASSED" : "FAILED"));
        System.out.println("Time: " + result.getExecutionTime() + "ms");
        if (!result.passed()) {
            System.out.println("Message: " + result.getMessage());
        }
        
        // Test with distance > 30 (should not display text)
        BlockTestCase testCase2 = new BlockTestCase("ultrasonic_distance_no_object", xml)
            .description("Test ultrasonic distance sensor with no object")
            .category("integration")
            .blockType("robSensors_ultrasonic_distance")
            .withSensor("4", "ultrasonic", new Integer(50)) // Distance > 30
            .expectMaxTime(1000);
        
        TestResult result2 = framework.executeTest(testCase2);
        
        System.out.println("\nTest: " + result2.getTestName());
        System.out.println("Status: " + (result2.passed() ? "PASSED" : "FAILED"));
        System.out.println("Time: " + result2.getExecutionTime() + "ms");
        if (!result2.passed()) {
            System.out.println("Message: " + result2.getMessage());
        }
        
        System.out.println("\n" + (result.passed() && result2.passed() ? 
                          "✅ Ultrasonic distance tests PASSED!" : 
                          "❌ Some ultrasonic distance tests FAILED"));
    }
}
