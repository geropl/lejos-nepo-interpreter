/**
 * IfElseBlockTest - Test if-else block functionality
 */
public class IfElseBlockTest {
    
    public static void main(String[] args) {
        System.out.println("=== Testing If-Else Block ===");
        
        // Test case 1: Condition is true (should execute IF branch)
        String xmlTrue = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
                        "                  <field name=\"TEXT\">IF branch</field>\n" +
                        "                </block>\n" +
                        "              </value>\n" +
                        "            </block>\n" +
                        "          </statement>\n" +
                        "          <statement name=\"ELSE\">\n" +
                        "            <block type=\"robActions_display_text\" id=\"6\">\n" +
                        "              <value name=\"OUT\">\n" +
                        "                <block type=\"text\" id=\"7\">\n" +
                        "                  <field name=\"TEXT\">ELSE branch</field>\n" +
                        "                </block>\n" +
                        "              </value>\n" +
                        "            </block>\n" +
                        "          </statement>\n" +
                        "        </block>\n" +
                        "      </statement>\n" +
                        "    </block>\n" +
                        "  </instance>\n" +
                        "</blockSet>";
        
        // Test case 2: Condition is false (should execute ELSE branch)
        String xmlFalse = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
                         "                  <field name=\"TEXT\">IF branch</field>\n" +
                         "                </block>\n" +
                         "              </value>\n" +
                         "            </block>\n" +
                         "          </statement>\n" +
                         "          <statement name=\"ELSE\">\n" +
                         "            <block type=\"robActions_display_text\" id=\"6\">\n" +
                         "              <value name=\"OUT\">\n" +
                         "                <block type=\"text\" id=\"7\">\n" +
                         "                  <field name=\"TEXT\">ELSE branch</field>\n" +
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
        
        // Test 1: TRUE condition (should show "IF branch")
        BlockTestCase testCase1 = new BlockTestCase("ifelse_true_condition", xmlTrue)
            .description("Test if-else block with true condition")
            .category("unit")
            .blockType("robControls_ifElse")
            .expectDisplayLine(0, "IF branch       ")
            .expectMaxTime(1000);
        
        TestResult result1 = framework.executeTest(testCase1);
        
        System.out.println("Test 1: " + result1.getTestName());
        System.out.println("Status: " + (result1.passed() ? "PASSED" : "FAILED"));
        System.out.println("Time: " + result1.getExecutionTime() + "ms");
        if (!result1.passed()) {
            System.out.println("Message: " + result1.getMessage());
        }
        
        // Test 2: FALSE condition (should show "ELSE branch")
        BlockTestCase testCase2 = new BlockTestCase("ifelse_false_condition", xmlFalse)
            .description("Test if-else block with false condition")
            .category("unit")
            .blockType("robControls_ifElse")
            .expectDisplayLine(0, "ELSE branch     ")
            .expectMaxTime(1000);
        
        TestResult result2 = framework.executeTest(testCase2);
        
        System.out.println("\nTest 2: " + result2.getTestName());
        System.out.println("Status: " + (result2.passed() ? "PASSED" : "FAILED"));
        System.out.println("Time: " + result2.getExecutionTime() + "ms");
        if (!result2.passed()) {
            System.out.println("Message: " + result2.getMessage());
        }
        
        System.out.println("\n" + (result1.passed() && result2.passed() ? 
                          "✅ If-Else block tests PASSED!" : 
                          "❌ Some If-Else block tests FAILED"));
    }
}
