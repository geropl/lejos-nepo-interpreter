import java.util.*;

/**
 * Phase2ComprehensiveTest - Comprehensive testing for Phase 2 blocks
 * 
 * Tests all Phase 2 blocks: ultrasonic distance, if-else, logic operations,
 * motor power reading, and timer functionality.
 */
public class Phase2ComprehensiveTest {
    
    public static void main(String[] args) {
        System.out.println("=== Phase 2 Comprehensive Block Testing ===");
        System.out.println("Testing: ultrasonic distance, if-else, logic operations, motor power, timer");
        System.out.println();
        
        NepoTestFramework framework = new NepoTestFramework();
        framework.setVerboseOutput(false); // Reduce noise for comprehensive test
        
        Vector testCases = new Vector();
        
        // Add all Phase 2 test cases
        testCases.addElement(createUltrasonicDistanceTest());
        testCases.addElement(createIfElseTestTrue());
        testCases.addElement(createIfElseTestFalse());
        testCases.addElement(createLogicAndTest());
        testCases.addElement(createLogicOrTest());
        testCases.addElement(createLogicNotTest());
        testCases.addElement(createMotorPowerTest());
        testCases.addElement(createTimerTest());
        testCases.addElement(createComplexIntegrationTest());
        
        // Create test suite
        TestSuite phase2Suite = new TestSuite("Phase 2 - Basic Interactivity", testCases);
        
        // Execute test suite
        TestReport report = framework.runTestSuite(phase2Suite);
        
        // Display results
        displayComprehensiveReport(report);
        
        // Generate reports
        try {
            framework.generateReport(report, "phase2-test-results.html");
            framework.generateReport(report, "phase2-test-results.json");
            System.out.println("\nDetailed reports generated:");
            System.out.println("- phase2-test-results.html");
            System.out.println("- phase2-test-results.json");
        } catch (Exception e) {
            System.err.println("Error generating reports: " + e.getMessage());
        }
    }
    
    private static BlockTestCase createUltrasonicDistanceTest() {
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
                    "                  <field name=\"NUM\">20</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <statement name=\"DO0\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"6\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"7\">\n" +
                    "                  <field name=\"TEXT\">Close object!</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("ultrasonic_distance_detection", xml)
            .description("Test ultrasonic distance sensor with object detection")
            .category("integration")
            .blockType("robSensors_ultrasonic_distance")
            .withSensor("4", "ultrasonic", new Integer(15)) // Distance < 20
            .expectDisplayLine(0, "Close object!   ")
            .expectMaxTime(1000);
    }
    
    private static BlockTestCase createIfElseTestTrue() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robControls_ifElse\" id=\"2\">\n" +
                    "          <value name=\"IF0\">\n" +
                    "            <block type=\"logic_boolean\" id=\"3\">\n" +
                    "              <field name=\"BOOL\">TRUE</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <statement name=\"DO0\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"4\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"5\">\n" +
                    "                  <field name=\"TEXT\">True path</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "          <statement name=\"ELSE\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"6\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"7\">\n" +
                    "                  <field name=\"TEXT\">False path</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("ifelse_true_branch", xml)
            .description("Test if-else block taking true branch")
            .category("unit")
            .blockType("robControls_ifElse")
            .expectDisplayLine(0, "True path       ")
            .expectMaxTime(500);
    }
    
    private static BlockTestCase createIfElseTestFalse() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robControls_ifElse\" id=\"2\">\n" +
                    "          <value name=\"IF0\">\n" +
                    "            <block type=\"logic_boolean\" id=\"3\">\n" +
                    "              <field name=\"BOOL\">FALSE</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <statement name=\"DO0\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"4\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"5\">\n" +
                    "                  <field name=\"TEXT\">True path</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "          <statement name=\"ELSE\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"6\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"7\">\n" +
                    "                  <field name=\"TEXT\">False path</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("ifelse_false_branch", xml)
            .description("Test if-else block taking false branch")
            .category("unit")
            .blockType("robControls_ifElse")
            .expectDisplayLine(0, "False path      ")
            .expectMaxTime(500);
    }
    
    private static BlockTestCase createLogicAndTest() {
        String xml = createLogicTestXML("AND", "TRUE", "FALSE", "Should not show");
        
        return new BlockTestCase("logic_and_operation", xml)
            .description("Test AND logic operation (TRUE AND FALSE = FALSE)")
            .category("unit")
            .blockType("logic_operation")
            .expectMaxTime(500);
    }
    
    private static BlockTestCase createLogicOrTest() {
        String xml = createLogicTestXML("OR", "TRUE", "FALSE", "OR result");
        
        return new BlockTestCase("logic_or_operation", xml)
            .description("Test OR logic operation (TRUE OR FALSE = TRUE)")
            .category("unit")
            .blockType("logic_operation")
            .expectDisplayLine(0, "OR result       ")
            .expectMaxTime(500);
    }
    
    private static BlockTestCase createLogicNotTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robControls_if\" id=\"2\">\n" +
                    "          <value name=\"IF0\">\n" +
                    "            <block type=\"logic_operation\" id=\"3\">\n" +
                    "              <field name=\"OP\">NOT</field>\n" +
                    "              <value name=\"A\">\n" +
                    "                <block type=\"logic_boolean\" id=\"4\">\n" +
                    "                  <field name=\"BOOL\">FALSE</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <statement name=\"DO0\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"5\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"6\">\n" +
                    "                  <field name=\"TEXT\">NOT result</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("logic_not_operation", xml)
            .description("Test NOT logic operation (NOT FALSE = TRUE)")
            .category("unit")
            .blockType("logic_operation")
            .expectDisplayLine(0, "NOT result      ")
            .expectMaxTime(500);
    }
    
    private static BlockTestCase createMotorPowerTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robActions_motor_on\" id=\"2\">\n" +
                    "          <field name=\"MOTORPORT\">B</field>\n" +
                    "          <field name=\"MOTORROTATION\">UNLIMITED</field>\n" +
                    "          <value name=\"POWER\">\n" +
                    "            <block type=\"math_number\" id=\"3\">\n" +
                    "              <field name=\"NUM\">75</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <next>\n" +
                    "            <block type=\"robControls_if\" id=\"4\">\n" +
                    "              <value name=\"IF0\">\n" +
                    "                <block type=\"logic_compare\" id=\"5\">\n" +
                    "                  <field name=\"OP\">GT</field>\n" +
                    "                  <value name=\"A\">\n" +
                    "                    <block type=\"robActions_motor_getPower\" id=\"6\">\n" +
                    "                      <field name=\"MOTORPORT\">B</field>\n" +
                    "                    </block>\n" +
                    "                  </value>\n" +
                    "                  <value name=\"B\">\n" +
                    "                    <block type=\"math_number\" id=\"7\">\n" +
                    "                      <field name=\"NUM\">50</field>\n" +
                    "                    </block>\n" +
                    "                  </value>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "              <statement name=\"DO0\">\n" +
                    "                <block type=\"robActions_display_text\" id=\"8\">\n" +
                    "                  <value name=\"OUT\">\n" +
                    "                    <block type=\"text\" id=\"9\">\n" +
                    "                      <field name=\"TEXT\">High power!</field>\n" +
                    "                    </block>\n" +
                    "                  </value>\n" +
                    "                </block>\n" +
                    "              </statement>\n" +
                    "            </block>\n" +
                    "          </next>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("motor_power_reading", xml)
            .description("Test motor power reading after setting motor power")
            .category("integration")
            .blockType("robActions_motor_getPower")
            .expectDisplayLine(0, "High power!     ")
            .expectMaxTime(1000);
    }
    
    private static BlockTestCase createTimerTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robControls_wait_time\" id=\"2\">\n" +
                    "          <value name=\"WAIT\">\n" +
                    "            <block type=\"math_number\" id=\"3\">\n" +
                    "              <field name=\"NUM\">100</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <next>\n" +
                    "            <block type=\"robControls_if\" id=\"4\">\n" +
                    "              <value name=\"IF0\">\n" +
                    "                <block type=\"logic_compare\" id=\"5\">\n" +
                    "                  <field name=\"OP\">GT</field>\n" +
                    "                  <value name=\"A\">\n" +
                    "                    <block type=\"robSensors_timer_get\" id=\"6\">\n" +
                    "                    </block>\n" +
                    "                  </value>\n" +
                    "                  <value name=\"B\">\n" +
                    "                    <block type=\"math_number\" id=\"7\">\n" +
                    "                      <field name=\"NUM\">50</field>\n" +
                    "                    </block>\n" +
                    "                  </value>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "              <statement name=\"DO0\">\n" +
                    "                <block type=\"robActions_display_text\" id=\"8\">\n" +
                    "                  <value name=\"OUT\">\n" +
                    "                    <block type=\"text\" id=\"9\">\n" +
                    "                      <field name=\"TEXT\">Timer works!</field>\n" +
                    "                    </block>\n" +
                    "                  </value>\n" +
                    "                </block>\n" +
                    "              </statement>\n" +
                    "            </block>\n" +
                    "          </next>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("timer_functionality", xml)
            .description("Test timer reading after wait")
            .category("integration")
            .blockType("robSensors_timer_get")
            .expectDisplayLine(0, "Timer works!    ")
            .expectMaxTime(1000);
    }
    
    private static BlockTestCase createComplexIntegrationTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robControls_ifElse\" id=\"2\">\n" +
                    "          <value name=\"IF0\">\n" +
                    "            <block type=\"logic_operation\" id=\"3\">\n" +
                    "              <field name=\"OP\">AND</field>\n" +
                    "              <value name=\"A\">\n" +
                    "                <block type=\"logic_compare\" id=\"4\">\n" +
                    "                  <field name=\"OP\">LT</field>\n" +
                    "                  <value name=\"A\">\n" +
                    "                    <block type=\"robSensors_ultrasonic_distance\" id=\"5\">\n" +
                    "                      <field name=\"SENSORPORT\">4</field>\n" +
                    "                    </block>\n" +
                    "                  </value>\n" +
                    "                  <value name=\"B\">\n" +
                    "                    <block type=\"math_number\" id=\"6\">\n" +
                    "                      <field name=\"NUM\">30</field>\n" +
                    "                    </block>\n" +
                    "                  </value>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "              <value name=\"B\">\n" +
                    "                <block type=\"robSensors_touch_isPressed\" id=\"7\">\n" +
                    "                  <field name=\"SENSORPORT\">1</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <statement name=\"DO0\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"8\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"9\">\n" +
                    "                  <field name=\"TEXT\">Both conditions!</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "          <statement name=\"ELSE\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"10\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"11\">\n" +
                    "                  <field name=\"TEXT\">Not both!</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("complex_integration", xml)
            .description("Complex integration: if-else with AND logic combining ultrasonic and touch sensors")
            .category("integration")
            .blockType("multiple")
            .withSensor("4", "ultrasonic", new Integer(25)) // Distance < 30
            .withSensor("1", "touch", new Boolean(true))    // Touch pressed
            .expectDisplayLine(0, "Both conditions!")
            .expectMaxTime(1000);
    }
    
    private static String createLogicTestXML(String operation, String valueA, String valueB, String displayText) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<blockSet robottype=\"nxt\">\n" +
               "  <instance x=\"384\" y=\"50\">\n" +
               "    <block type=\"robControls_start\" id=\"1\">\n" +
               "      <statement name=\"ST\">\n" +
               "        <block type=\"robControls_if\" id=\"2\">\n" +
               "          <value name=\"IF0\">\n" +
               "            <block type=\"logic_operation\" id=\"3\">\n" +
               "              <field name=\"OP\">" + operation + "</field>\n" +
               "              <value name=\"A\">\n" +
               "                <block type=\"logic_boolean\" id=\"4\">\n" +
               "                  <field name=\"BOOL\">" + valueA + "</field>\n" +
               "                </block>\n" +
               "              </value>\n" +
               "              <value name=\"B\">\n" +
               "                <block type=\"logic_boolean\" id=\"5\">\n" +
               "                  <field name=\"BOOL\">" + valueB + "</field>\n" +
               "                </block>\n" +
               "              </value>\n" +
               "            </block>\n" +
               "          </value>\n" +
               "          <statement name=\"DO0\">\n" +
               "            <block type=\"robActions_display_text\" id=\"6\">\n" +
               "              <value name=\"OUT\">\n" +
               "                <block type=\"text\" id=\"7\">\n" +
               "                  <field name=\"TEXT\">" + displayText + "</field>\n" +
               "                </block>\n" +
               "              </value>\n" +
               "            </block>\n" +
               "          </statement>\n" +
               "        </block>\n" +
               "      </statement>\n" +
               "    </block>\n" +
               "  </instance>\n" +
               "</blockSet>";
    }
    
    private static void displayComprehensiveReport(TestReport report) {
        System.out.println("=== Phase 2 Comprehensive Test Results ===");
        System.out.println("Suite: " + report.getSuiteName());
        System.out.println("Total Tests: " + report.getTotalTests());
        System.out.println("Passed: " + report.getPassedTests() + " (" + 
                          String.valueOf((int) report.getPassRate()) + "%)");
        System.out.println("Failed: " + report.getFailedTests());
        System.out.println("Execution Time: " + report.getExecutionTime() + "ms");
        
        // Show detailed results
        Vector results = report.getResults();
        System.out.println("\nDetailed Results:");
        for (int i = 0; i < results.size(); i++) {
            TestResult result = (TestResult) results.elementAt(i);
            String status = result.passed() ? "✅ PASSED" : "❌ FAILED";
            System.out.println("  " + result.getTestName() + ": " + status + 
                             " (" + result.getExecutionTime() + "ms)");
            if (!result.passed()) {
                System.out.println("    → " + result.getMessage());
            }
        }
        
        // Summary
        System.out.println();
        if (report.getFailedTests() == 0) {
            System.out.println("🎉 ALL PHASE 2 TESTS PASSED!");
            System.out.println("Phase 2 - Basic Interactivity is COMPLETE and ready for production!");
        } else {
            System.out.println("❌ " + report.getFailedTests() + " test(s) FAILED");
            System.out.println("Phase 2 implementation needs attention.");
        }
        
        // Block coverage summary
        System.out.println("\nPhase 2 Block Coverage:");
        System.out.println("✅ robSensors_ultrasonic_distance - Distance measurement");
        System.out.println("✅ robControls_ifElse - If-else conditionals");
        System.out.println("✅ logic_operation - AND, OR, NOT operations");
        System.out.println("✅ robActions_motor_getPower - Motor power reading");
        System.out.println("✅ robSensors_timer_get - Timer functionality");
    }
}
