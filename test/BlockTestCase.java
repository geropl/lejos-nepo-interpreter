import java.util.*;

/**
 * BlockTestCase - Defines structure for individual NEPO block test cases
 * 
 * Contains test metadata, NEPO XML program, expected results, and validation rules
 * for comprehensive block testing.
 */
public class BlockTestCase {
    
    // Test identification
    private String testName;
    private String description;
    private String category;        // "unit", "integration", "hardware", "performance"
    private Priority priority;      // HIGH, MEDIUM, LOW
    private String blockType;       // Primary block being tested
    
    // Test program and setup
    private String nepoXML;         // NEPO XML program to execute
    private MockHardware.HardwareState initialState;  // Initial hardware state
    private long timeoutMs;         // Execution timeout (0 = use default)
    private boolean requiresHardware; // Whether real hardware is needed
    
    // Expected results and validation
    private ExpectedResults expectedResults;
    private ValidationRules validationRules;
    
    // Test metadata
    private String author;
    private String createdDate;
    private String lastModified;
    private Vector tags;            // Additional tags for categorization
    
    /**
     * Create basic test case
     */
    public BlockTestCase(String testName, String nepoXML) {
        this.testName = testName;
        this.nepoXML = nepoXML;
        this.description = "";
        this.category = "unit";
        this.priority = Priority.MEDIUM;
        this.blockType = "";
        this.initialState = null;
        this.timeoutMs = 0;
        this.requiresHardware = false;
        this.expectedResults = new ExpectedResults();
        this.validationRules = null;
        this.author = "";
        this.createdDate = "";
        this.lastModified = "";
        this.tags = new Vector();
    }
    
    /**
     * Create detailed test case
     */
    public BlockTestCase(String testName, String description, String category, 
                        Priority priority, String blockType, String nepoXML) {
        this(testName, nepoXML);
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.blockType = blockType;
    }
    
    // Getters
    public String getTestName() { return testName; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Priority getPriority() { return priority; }
    public String getBlockType() { return blockType; }
    public String getNepoXML() { return nepoXML; }
    public MockHardware.HardwareState getInitialState() { return initialState; }
    public long getTimeoutMs() { return timeoutMs; }
    public boolean requiresHardware() { return requiresHardware; }
    public ExpectedResults getExpectedResults() { return expectedResults; }
    public ValidationRules getValidationRules() { return validationRules; }
    public String getAuthor() { return author; }
    public String getCreatedDate() { return createdDate; }
    public String getLastModified() { return lastModified; }
    public Vector getTags() { return tags; }
    
    // Setters
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setBlockType(String blockType) { this.blockType = blockType; }
    public void setNepoXML(String nepoXML) { this.nepoXML = nepoXML; }
    public void setInitialState(MockHardware.HardwareState initialState) { this.initialState = initialState; }
    public void setTimeoutMs(long timeoutMs) { this.timeoutMs = timeoutMs; }
    public void setRequiresHardware(boolean requiresHardware) { this.requiresHardware = requiresHardware; }
    public void setExpectedResults(ExpectedResults expectedResults) { this.expectedResults = expectedResults; }
    public void setValidationRules(ValidationRules validationRules) { this.validationRules = validationRules; }
    public void setAuthor(String author) { this.author = author; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
    public void setLastModified(String lastModified) { this.lastModified = lastModified; }
    
    /**
     * Add tag for categorization
     */
    public void addTag(String tag) {
        if (tag != null && !tags.contains(tag)) {
            tags.addElement(tag);
        }
    }
    
    /**
     * Remove tag
     */
    public void removeTag(String tag) {
        tags.removeElement(tag);
    }
    
    /**
     * Check if test has specific tag
     */
    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }
    
    /**
     * Fluent interface for building test cases
     */
    public BlockTestCase description(String description) {
        setDescription(description);
        return this;
    }
    
    public BlockTestCase category(String category) {
        setCategory(category);
        return this;
    }
    
    public BlockTestCase priority(Priority priority) {
        setPriority(priority);
        return this;
    }
    
    public BlockTestCase blockType(String blockType) {
        setBlockType(blockType);
        return this;
    }
    
    public BlockTestCase timeout(long timeoutMs) {
        setTimeoutMs(timeoutMs);
        return this;
    }
    
    public BlockTestCase requiresHardware(boolean requires) {
        setRequiresHardware(requires);
        return this;
    }
    
    public BlockTestCase tag(String tag) {
        addTag(tag);
        return this;
    }
    
    public BlockTestCase author(String author) {
        setAuthor(author);
        return this;
    }
    
    /**
     * Expect specific display content
     */
    public BlockTestCase expectDisplay(String expectedContent) {
        expectedResults.setExpectedDisplayContent(expectedContent);
        return this;
    }
    
    /**
     * Expect display line content
     */
    public BlockTestCase expectDisplayLine(int line, String expectedContent) {
        expectedResults.setExpectedDisplayLine(line, expectedContent);
        return this;
    }
    
    /**
     * Expect motor state
     */
    public BlockTestCase expectMotor(String port, int speed, double rotation, String direction) {
        expectedResults.setExpectedMotorState(port, speed, rotation, direction);
        return this;
    }
    
    /**
     * Expect sensor value
     */
    public BlockTestCase expectSensor(String port, String type, Object value) {
        expectedResults.setExpectedSensorValue(port, type, value);
        return this;
    }
    
    /**
     * Expect maximum execution time
     */
    public BlockTestCase expectMaxTime(long maxTimeMs) {
        expectedResults.setMaxExecutionTimeMs(maxTimeMs);
        return this;
    }
    
    /**
     * Expect minimum event count
     */
    public BlockTestCase expectMinEvents(int minEvents) {
        expectedResults.setMinEventCount(minEvents);
        return this;
    }
    
    /**
     * Set initial motor state
     */
    public BlockTestCase withMotor(String port, int speed, double rotation) {
        if (initialState == null) {
            // Create minimal initial state
            initialState = createMinimalHardwareState();
        }
        // Set motor state in initial state
        // Note: This would require extending HardwareState to be mutable
        // For now, we'll use a builder pattern
        return this;
    }
    
    /**
     * Set initial sensor state
     */
    public BlockTestCase withSensor(String port, String type, Object value) {
        if (initialState == null) {
            initialState = createMinimalHardwareState();
        }
        // Set sensor state in initial state
        return this;
    }
    
    /**
     * Create minimal hardware state for initialization
     */
    private MockHardware.HardwareState createMinimalHardwareState() {
        // Create empty hardware state
        MockHardware mockHw = new MockHardware();
        return mockHw.getCurrentState();
    }
    
    /**
     * Validate test case configuration
     */
    public ValidationResult validate() {
        Vector errors = new Vector();
        
        // Check required fields
        if (testName == null || testName.trim().length() == 0) {
            errors.addElement("Test name is required");
        }
        
        if (nepoXML == null || nepoXML.trim().length() == 0) {
            errors.addElement("NEPO XML program is required");
        }
        
        if (category == null || category.trim().length() == 0) {
            errors.addElement("Category is required");
        }
        
        // Validate XML format
        if (nepoXML != null) {
            try {
                SimpleXMLParser.XMLElement parsed = SimpleXMLParser.parseXML(nepoXML);
                if (parsed == null) {
                    errors.addElement("Invalid NEPO XML format");
                }
            } catch (Exception e) {
                errors.addElement("XML parsing error: " + e.getMessage());
            }
        }
        
        // Validate timeout
        if (timeoutMs < 0) {
            errors.addElement("Timeout cannot be negative");
        }
        
        // Compile results
        if (errors.size() == 0) {
            return new ValidationResult(true, "Test case is valid");
        } else {
            StringBuffer message = new StringBuffer("Validation errors: ");
            for (int i = 0; i < errors.size(); i++) {
                if (i > 0) message.append("; ");
                message.append(errors.elementAt(i));
            }
            return new ValidationResult(false, message.toString());
        }
    }
    
    /**
     * Clone test case
     */
    public BlockTestCase clone() {
        BlockTestCase copy = new BlockTestCase(testName + "_copy", nepoXML);
        copy.description = description;
        copy.category = category;
        copy.priority = priority;
        copy.blockType = blockType;
        copy.initialState = initialState; // Shallow copy
        copy.timeoutMs = timeoutMs;
        copy.requiresHardware = requiresHardware;
        copy.expectedResults = expectedResults.clone();
        copy.validationRules = validationRules;
        copy.author = author;
        copy.createdDate = createdDate;
        copy.lastModified = lastModified;
        copy.tags = new Vector(tags);
        return copy;
    }
    
    /**
     * Convert to string representation
     */
    public String toString() {
        return "BlockTestCase{name='" + testName + "', category='" + category + 
               "', priority=" + priority + ", blockType='" + blockType + "'}";
    }
    
    /**
     * Test case priority enumeration
     */
    public static class Priority {
        public static final Priority HIGH = new Priority("HIGH", 3);
        public static final Priority MEDIUM = new Priority("MEDIUM", 2);
        public static final Priority LOW = new Priority("LOW", 1);
        
        private String name;
        private int level;
        
        private Priority(String name, int level) {
            this.name = name;
            this.level = level;
        }
        
        public String getName() { return name; }
        public int getLevel() { return level; }
        
        public String toString() { return name; }
        
        public boolean equals(Object obj) {
            if (obj instanceof Priority) {
                return level == ((Priority) obj).level;
            }
            return false;
        }
    }
    
    /**
     * Validation result for test case validation
     */
    public static class ValidationResult {
        private boolean valid;
        private String message;
        
        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
    }
}
