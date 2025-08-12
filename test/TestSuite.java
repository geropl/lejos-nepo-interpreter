import java.util.*;

/**
 * TestSuite - Collection of related test cases
 * 
 * Groups test cases together for organized execution and reporting.
 */
public class TestSuite {
    
    private String name;
    private String description;
    private Vector testCases;
    private String category;
    private long createdTime;
    
    /**
     * Create test suite
     */
    public TestSuite(String name, Vector testCases) {
        this.name = name;
        this.testCases = testCases != null ? testCases : new Vector();
        this.description = "";
        this.category = "";
        this.createdTime = System.currentTimeMillis();
    }
    
    /**
     * Create test suite with description
     */
    public TestSuite(String name, String description, Vector testCases) {
        this(name, testCases);
        this.description = description;
    }
    
    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Vector getTestCases() { return testCases; }
    public String getCategory() { return category; }
    public long getCreatedTime() { return createdTime; }
    
    // Setters
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category) { this.category = category; }
    
    /**
     * Add test case to suite
     */
    public void addTestCase(BlockTestCase testCase) {
        if (testCase != null) {
            testCases.addElement(testCase);
        }
    }
    
    /**
     * Remove test case from suite
     */
    public void removeTestCase(BlockTestCase testCase) {
        testCases.removeElement(testCase);
    }
    
    /**
     * Get test case count
     */
    public int getTestCaseCount() {
        return testCases.size();
    }
    
    /**
     * Get test cases by category
     */
    public Vector getTestCasesByCategory(String category) {
        Vector filtered = new Vector();
        for (int i = 0; i < testCases.size(); i++) {
            BlockTestCase testCase = (BlockTestCase) testCases.elementAt(i);
            if (category.equals(testCase.getCategory())) {
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
        for (int i = 0; i < testCases.size(); i++) {
            BlockTestCase testCase = (BlockTestCase) testCases.elementAt(i);
            if (priority.equals(testCase.getPriority())) {
                filtered.addElement(testCase);
            }
        }
        return filtered;
    }
    
    public String toString() {
        return "TestSuite{name='" + name + "', testCases=" + testCases.size() + "}";
    }
}
