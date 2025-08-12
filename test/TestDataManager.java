import java.util.*;
import java.io.*;

/**
 * TestDataManager - Manages test data, programs, and expected results
 * 
 * Handles loading test cases from files, organizing tests by category,
 * and managing test data persistence.
 */
public class TestDataManager {
    
    private String baseDirectory;
    private Hashtable testSuites;     // suite name -> TestSuite
    private Hashtable testCases;      // test name -> BlockTestCase
    
    /**
     * Initialize test data manager
     */
    public TestDataManager() {
        this("test-data");
    }
    
    /**
     * Initialize with specific base directory
     */
    public TestDataManager(String baseDirectory) {
        this.baseDirectory = baseDirectory;
        this.testSuites = new Hashtable();
        this.testCases = new Hashtable();
    }
    
    /**
     * Load test suite from directory
     */
    public TestSuite loadTestSuite(String directory) {
        String suitePath = baseDirectory + "/" + directory;
        File suiteDir = new File(suitePath);
        
        if (!suiteDir.exists() || !suiteDir.isDirectory()) {
            System.err.println("Test suite directory not found: " + suitePath);
            return new TestSuite(directory, new Vector());
        }
        
        Vector testCases = new Vector();
        
        // Load test cases from directory
        File[] files = suiteDir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isFile() && file.getName().endsWith(".xml")) {
                    BlockTestCase testCase = loadTestCaseFromXML(file);
                    if (testCase != null) {
                        testCases.addElement(testCase);
                    }
                }
            }
        }
        
        TestSuite suite = new TestSuite(directory, testCases);
        testSuites.put(directory, suite);
        
        return suite;
    }
    
    /**
     * Load test case from XML file
     */
    private BlockTestCase loadTestCaseFromXML(File xmlFile) {
        try {
            // Read XML content
            String xmlContent = readFileContent(xmlFile);
            if (xmlContent == null || xmlContent.trim().length() == 0) {
                return null;
            }
            
            // Parse XML to extract test case information
            SimpleXMLParser.XMLElement root = SimpleXMLParser.parseXML(xmlContent);
            if (root == null) {
                System.err.println("Failed to parse XML file: " + xmlFile.getName());
                return null;
            }
            
            // Extract test case metadata
            String testName = extractTestName(xmlFile.getName(), root);
            String description = extractDescription(root);
            String category = extractCategory(root);
            String blockType = extractBlockType(root);
            
            // Create test case
            BlockTestCase testCase = new BlockTestCase(testName, xmlContent);
            testCase.setDescription(description);
            testCase.setCategory(category);
            testCase.setBlockType(blockType);
            
            // Set additional metadata
            testCase.setCreatedDate(new java.util.Date(xmlFile.lastModified()).toString());
            
            return testCase;
            
        } catch (Exception e) {
            System.err.println("Error loading test case from " + xmlFile.getName() + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Read file content as string
     */
    private String readFileContent(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            fis.read(buffer);
            fis.close();
            return new String(buffer);
        } catch (Exception e) {
            System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Extract test name from filename and XML
     */
    private String extractTestName(String filename, SimpleXMLParser.XMLElement root) {
        // Try to get name from XML metadata
        SimpleXMLParser.XMLElement testCase = findElement(root, "testCase");
        if (testCase != null) {
            String name = testCase.getAttribute("name");
            if (name != null && name.length() > 0) {
                return name;
            }
        }
        
        // Fall back to filename without extension
        int dotIndex = filename.lastIndexOf('.');
        return dotIndex > 0 ? filename.substring(0, dotIndex) : filename;
    }
    
    /**
     * Extract description from XML
     */
    private String extractDescription(SimpleXMLParser.XMLElement root) {
        SimpleXMLParser.XMLElement desc = findElement(root, "description");
        return desc != null ? desc.textContent : "";
    }
    
    /**
     * Extract category from XML
     */
    private String extractCategory(SimpleXMLParser.XMLElement root) {
        SimpleXMLParser.XMLElement testCase = findElement(root, "testCase");
        if (testCase != null) {
            String category = testCase.getAttribute("category");
            if (category != null && category.length() > 0) {
                return category;
            }
        }
        return "unit"; // Default category
    }
    
    /**
     * Extract primary block type being tested
     */
    private String extractBlockType(SimpleXMLParser.XMLElement root) {
        // Look for the first block element with a type attribute
        SimpleXMLParser.XMLElement block = findBlockElement(root);
        if (block != null) {
            String type = block.getAttribute("type");
            if (type != null && type.length() > 0) {
                return type;
            }
        }
        return "";
    }
    
    /**
     * Find element by tag name recursively
     */
    private SimpleXMLParser.XMLElement findElement(SimpleXMLParser.XMLElement parent, String tagName) {
        if (parent == null) return null;
        
        if (tagName.equals(parent.tagName)) {
            return parent;
        }
        
        for (int i = 0; i < parent.children.size(); i++) {
            SimpleXMLParser.XMLElement child = (SimpleXMLParser.XMLElement) parent.children.elementAt(i);
            SimpleXMLParser.XMLElement found = findElement(child, tagName);
            if (found != null) {
                return found;
            }
        }
        
        return null;
    }
    
    /**
     * Find first block element
     */
    private SimpleXMLParser.XMLElement findBlockElement(SimpleXMLParser.XMLElement parent) {
        return findElement(parent, "block");
    }
    
    /**
     * Create test suite programmatically
     */
    public TestSuite createTestSuite(String name, Vector testCases) {
        TestSuite suite = new TestSuite(name, testCases);
        testSuites.put(name, suite);
        return suite;
    }
    
    /**
     * Add test case to manager
     */
    public void addTestCase(BlockTestCase testCase) {
        if (testCase != null && testCase.getTestName() != null) {
            testCases.put(testCase.getTestName(), testCase);
        }
    }
    
    /**
     * Get test case by name
     */
    public BlockTestCase getTestCase(String testName) {
        return (BlockTestCase) testCases.get(testName);
    }
    
    /**
     * Get all test cases
     */
    public Vector getAllTestCases() {
        Vector allCases = new Vector();
        Enumeration values = testCases.elements();
        while (values.hasMoreElements()) {
            allCases.addElement(values.nextElement());
        }
        return allCases;
    }
    
    /**
     * Get test cases by category
     */
    public Vector getTestCasesByCategory(String category) {
        Vector filtered = new Vector();
        Enumeration values = testCases.elements();
        while (values.hasMoreElements()) {
            BlockTestCase testCase = (BlockTestCase) values.nextElement();
            if (category.equals(testCase.getCategory())) {
                filtered.addElement(testCase);
            }
        }
        return filtered;
    }
    
    /**
     * Get test cases by block type
     */
    public Vector getTestCasesByBlockType(String blockType) {
        Vector filtered = new Vector();
        Enumeration values = testCases.elements();
        while (values.hasMoreElements()) {
            BlockTestCase testCase = (BlockTestCase) values.nextElement();
            if (blockType.equals(testCase.getBlockType())) {
                filtered.addElement(testCase);
            }
        }
        return filtered;
    }
    
    /**
     * Get test cases by priority
     */
    public Vector getTestCasesByPriority(BlockTestCase.Priority priority) {
        Vector filtered = new Vector();
        Enumeration values = testCases.elements();
        while (values.hasMoreElements()) {
            BlockTestCase testCase = (BlockTestCase) values.nextElement();
            if (priority.equals(testCase.getPriority())) {
                filtered.addElement(testCase);
            }
        }
        return filtered;
    }
    
    /**
     * Save test case to XML file
     */
    public boolean saveTestCase(BlockTestCase testCase, String directory) {
        if (testCase == null || testCase.getTestName() == null) {
            return false;
        }
        
        try {
            String dirPath = baseDirectory + "/" + directory;
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            String filename = testCase.getTestName() + ".xml";
            File file = new File(dir, filename);
            
            FileWriter writer = new FileWriter(file);
            
            // Write test case metadata as XML comments
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<!-- Test Case: " + testCase.getTestName() + " -->\n");
            writer.write("<!-- Description: " + testCase.getDescription() + " -->\n");
            writer.write("<!-- Category: " + testCase.getCategory() + " -->\n");
            writer.write("<!-- Block Type: " + testCase.getBlockType() + " -->\n");
            writer.write("<!-- Priority: " + testCase.getPriority() + " -->\n");
            writer.write("\n");
            
            // Write the NEPO XML program
            writer.write(testCase.getNepoXML());
            
            writer.close();
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error saving test case " + testCase.getTestName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Generate test cases for existing blocks
     */
    public Vector generateBasicTestCases() {
        Vector testCases = new Vector();
        
        // Generate test cases for Phase 1 blocks
        testCases.addElement(createDisplayTextTest());
        testCases.addElement(createMotorOnTest());
        testCases.addElement(createMotorStopTest());
        testCases.addElement(createWaitTimeTest());
        testCases.addElement(createTouchSensorTest());
        testCases.addElement(createPlayToneTest());
        testCases.addElement(createRepeatTimesTest());
        testCases.addElement(createIfBlockTest());
        
        return testCases;
    }
    
    /**
     * Create display text test case
     */
    private BlockTestCase createDisplayTextTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robActions_display_text\" id=\"2\">\n" +
                    "          <value name=\"OUT\">\n" +
                    "            <block type=\"text\" id=\"3\">\n" +
                    "              <field name=\"TEXT\">Hello Test!</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("display_text_basic", xml)
            .description("Test basic display text functionality")
            .category("unit")
            .blockType("robActions_display_text")
            .expectDisplayLine(0, "Hello Test!     ")
            .expectMaxTime(1000);
    }
    
    /**
     * Create motor on test case
     */
    private BlockTestCase createMotorOnTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robActions_motor_on\" id=\"2\">\n" +
                    "          <field name=\"MOTORPORT\">B</field>\n" +
                    "          <field name=\"MOTORROTATION\">ROTATIONS</field>\n" +
                    "          <value name=\"POWER\">\n" +
                    "            <block type=\"math_number\" id=\"3\">\n" +
                    "              <field name=\"NUM\">50</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <value name=\"VALUE\">\n" +
                    "            <block type=\"math_number\" id=\"4\">\n" +
                    "              <field name=\"NUM\">1</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("motor_on_basic", xml)
            .description("Test basic motor on functionality")
            .category("unit")
            .blockType("robActions_motor_on")
            .expectMotor("B", 360, 360, "STOPPED")
            .expectMaxTime(2000);
    }
    
    /**
     * Create motor stop test case
     */
    private BlockTestCase createMotorStopTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robActions_motor_stop\" id=\"2\">\n" +
                    "          <field name=\"MOTORPORT\">A</field>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("motor_stop_basic", xml)
            .description("Test basic motor stop functionality")
            .category("unit")
            .blockType("robActions_motor_stop")
            .expectMotor("A", 0, 0, "STOPPED")
            .expectMaxTime(500);
    }
    
    /**
     * Create wait time test case
     */
    private BlockTestCase createWaitTimeTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robControls_wait_time\" id=\"2\">\n" +
                    "          <value name=\"WAIT\">\n" +
                    "            <block type=\"math_number\" id=\"3\">\n" +
                    "              <field name=\"NUM\">500</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("wait_time_basic", xml)
            .description("Test basic wait time functionality")
            .category("unit")
            .blockType("robControls_wait_time")
            .expectMinEvents(2)
            .timeout(2000);
    }
    
    /**
     * Create touch sensor test case
     */
    private BlockTestCase createTouchSensorTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robControls_if\" id=\"2\">\n" +
                    "          <value name=\"IF0\">\n" +
                    "            <block type=\"robSensors_touch_isPressed\" id=\"3\">\n" +
                    "              <field name=\"SENSORPORT\">1</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <statement name=\"DO0\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"4\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"5\">\n" +
                    "                  <field name=\"TEXT\">Pressed!</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("touch_sensor_basic", xml)
            .description("Test touch sensor with if block")
            .category("integration")
            .blockType("robSensors_touch_isPressed")
            .withSensor("1", "touch", new Boolean(true))
            .expectDisplayLine(0, "Pressed!        ")
            .expectMaxTime(1000);
    }
    
    /**
     * Create play tone test case
     */
    private BlockTestCase createPlayToneTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robActions_play_tone\" id=\"2\">\n" +
                    "          <value name=\"FREQUENCY\">\n" +
                    "            <block type=\"math_number\" id=\"3\">\n" +
                    "              <field name=\"NUM\">440</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <value name=\"DURATION\">\n" +
                    "            <block type=\"math_number\" id=\"4\">\n" +
                    "              <field name=\"NUM\">500</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("play_tone_basic", xml)
            .description("Test basic play tone functionality")
            .category("unit")
            .blockType("robActions_play_tone")
            .expectMinEvents(2)
            .expectMaxTime(1000);
    }
    
    /**
     * Create repeat times test case
     */
    private BlockTestCase createRepeatTimesTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robControls_repeat_times\" id=\"2\">\n" +
                    "          <value name=\"TIMES\">\n" +
                    "            <block type=\"math_number\" id=\"3\">\n" +
                    "              <field name=\"NUM\">3</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <statement name=\"DO\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"4\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"5\">\n" +
                    "                  <field name=\"TEXT\">Loop!</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("repeat_times_basic", xml)
            .description("Test basic repeat times functionality")
            .category("unit")
            .blockType("robControls_repeat_times")
            .expectDisplayLine(0, "Loop!           ")
            .expectMinEvents(5)
            .expectMaxTime(2000);
    }
    
    /**
     * Create if block test case
     */
    private BlockTestCase createIfBlockTest() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<blockSet robottype=\"nxt\">\n" +
                    "  <instance x=\"384\" y=\"50\">\n" +
                    "    <block type=\"robControls_start\" id=\"1\">\n" +
                    "      <statement name=\"ST\">\n" +
                    "        <block type=\"robControls_if\" id=\"2\">\n" +
                    "          <value name=\"IF0\">\n" +
                    "            <block type=\"logic_boolean\" id=\"3\">\n" +
                    "              <field name=\"BOOL\">TRUE</field>\n" +
                    "            </block>\n" +
                    "          </value>\n" +
                    "          <statement name=\"DO0\">\n" +
                    "            <block type=\"robActions_display_text\" id=\"4\">\n" +
                    "              <value name=\"OUT\">\n" +
                    "                <block type=\"text\" id=\"5\">\n" +
                    "                  <field name=\"TEXT\">True!</field>\n" +
                    "                </block>\n" +
                    "              </value>\n" +
                    "            </block>\n" +
                    "          </statement>\n" +
                    "        </block>\n" +
                    "      </statement>\n" +
                    "    </block>\n" +
                    "  </instance>\n" +
                    "</blockSet>";
        
        return new BlockTestCase("if_block_basic", xml)
            .description("Test basic if block functionality")
            .category("unit")
            .blockType("robControls_if")
            .expectDisplayLine(0, "True!           ")
            .expectMaxTime(1000);
    }
    
    /**
     * Get base directory
     */
    public String getBaseDirectory() {
        return baseDirectory;
    }
    
    /**
     * Set base directory
     */
    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }
}
