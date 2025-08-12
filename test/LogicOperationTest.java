/**
 * LogicOperationTest - Test logical operation blocks (AND, OR, NOT)
 */
public class LogicOperationTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Logic Operation Blocks ===");
        
        NepoTestFramework framework = new NepoTestFramework();
        framework.setVerboseOutput(true);
        
        // Test 1: AND operation (TRUE AND TRUE = TRUE)
        String xmlAnd = createLogicOperationXML("AND", "TRUE", "TRUE", "AND True");
        BlockTestCase testAnd = new BlockTestCase("logic_and_true", xmlAnd)
            .description("Test AND operation with true values")
            .category("unit")
            .blockType("logic_operation")
            .expectDisplayLine(0, "AND True        ")
            .expectMaxTime(1000);
        
        TestResult resultAnd = framework.executeTest(testAnd);
        displayResult("AND (TRUE AND TRUE)", resultAnd);
        
        // Test 2: AND operation (TRUE AND FALSE = FALSE)
        String xmlAndFalse = createLogicOperationXML("AND", "TRUE", "FALSE", "Should not show");
        BlockTestCase testAndFalse = new BlockTestCase("logic_and_false", xmlAndFalse)
            .description("Test AND operation with mixed values")
            .category("unit")
            .blockType("logic_operation")
            .expectMaxTime(1000);
        
        TestResult resultAndFalse = framework.executeTest(testAndFalse);
        displayResult("AND (TRUE AND FALSE)", resultAndFalse);
        
        // Test 3: OR operation (TRUE OR FALSE = TRUE)
        String xmlOr = createLogicOperationXML("OR", "TRUE", "FALSE", "OR True");
        BlockTestCase testOr = new BlockTestCase("logic_or_true", xmlOr)
            .description("Test OR operation with mixed values")
            .category("unit")
            .blockType("logic_operation")
            .expectDisplayLine(0, "OR True         ")
            .expectMaxTime(1000);
        
        TestResult resultOr = framework.executeTest(testOr);
        displayResult("OR (TRUE OR FALSE)", resultOr);
        
        // Test 4: OR operation (FALSE OR FALSE = FALSE)
        String xmlOrFalse = createLogicOperationXML("OR", "FALSE", "FALSE", "Should not show");
        BlockTestCase testOrFalse = new BlockTestCase("logic_or_false", xmlOrFalse)
            .description("Test OR operation with false values")
            .category("unit")
            .blockType("logic_operation")
            .expectMaxTime(1000);
        
        TestResult resultOrFalse = framework.executeTest(testOrFalse);
        displayResult("OR (FALSE OR FALSE)", resultOrFalse);
        
        // Test 5: NOT operation (NOT TRUE = FALSE)
        String xmlNot = createNotOperationXML("TRUE", "Should not show");
        BlockTestCase testNot = new BlockTestCase("logic_not_true", xmlNot)
            .description("Test NOT operation with true value")
            .category("unit")
            .blockType("logic_operation")
            .expectMaxTime(1000);
        
        TestResult resultNot = framework.executeTest(testNot);
        displayResult("NOT (NOT TRUE)", resultNot);
        
        // Test 6: NOT operation (NOT FALSE = TRUE)
        String xmlNotFalse = createNotOperationXML("FALSE", "NOT False");
        BlockTestCase testNotFalse = new BlockTestCase("logic_not_false", xmlNotFalse)
            .description("Test NOT operation with false value")
            .category("unit")
            .blockType("logic_operation")
            .expectDisplayLine(0, "NOT False       ")
            .expectMaxTime(1000);
        
        TestResult resultNotFalse = framework.executeTest(testNotFalse);
        displayResult("NOT (NOT FALSE)", resultNotFalse);
        
        // Summary
        boolean allPassed = resultAnd.passed() && resultAndFalse.passed() && 
                           resultOr.passed() && resultOrFalse.passed() && 
                           resultNot.passed() && resultNotFalse.passed();
        
        System.out.println("\n" + (allPassed ? 
                          "✅ All logic operation tests PASSED!" : 
                          "❌ Some logic operation tests FAILED"));
    }
    
    private static String createLogicOperationXML(String operation, String valueA, String valueB, String displayText) {
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
    
    private static String createNotOperationXML(String value, String displayText) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
               "                  <field name=\"BOOL\">" + value + "</field>\n" +
               "                </block>\n" +
               "              </value>\n" +
               "            </block>\n" +
               "          </value>\n" +
               "          <statement name=\"DO0\">\n" +
               "            <block type=\"robActions_display_text\" id=\"5\">\n" +
               "              <value name=\"OUT\">\n" +
               "                <block type=\"text\" id=\"6\">\n" +
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
    
    private static void displayResult(String testName, TestResult result) {
        System.out.println("\nTest: " + testName);
        System.out.println("Status: " + (result.passed() ? "PASSED" : "FAILED"));
        System.out.println("Time: " + result.getExecutionTime() + "ms");
        if (!result.passed()) {
            System.out.println("Message: " + result.getMessage());
        }
    }
}
