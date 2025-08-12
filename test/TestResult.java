import java.util.*;

/**
 * TestResult - Contains results from test case execution
 * 
 * Stores test outcome, execution details, and validation results.
 */
public class TestResult {
    
    private String testName;
    private boolean passed;
    private String message;
    private long executionTime;
    private MockHardware.HardwareState finalState;
    
    // Additional execution details
    private Object executionResult;
    private Vector eventHistory;
    private MockHardware.ExecutionStats executionStats;
    private String category;
    private long timestamp;
    
    /**
     * Create test result
     */
    public TestResult(String testName, boolean passed, String message, 
                     long executionTime, MockHardware.HardwareState finalState) {
        this.testName = testName;
        this.passed = passed;
        this.message = message;
        this.executionTime = executionTime;
        this.finalState = finalState;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters
    public String getTestName() { return testName; }
    public boolean passed() { return passed; }
    public String getMessage() { return message; }
    public long getExecutionTime() { return executionTime; }
    public MockHardware.HardwareState getFinalState() { return finalState; }
    public Object getExecutionResult() { return executionResult; }
    public Vector getEventHistory() { return eventHistory; }
    public MockHardware.ExecutionStats getExecutionStats() { return executionStats; }
    public String getCategory() { return category; }
    public long getTimestamp() { return timestamp; }
    
    // Setters
    public void setExecutionResult(Object result) { this.executionResult = result; }
    public void setEventHistory(Vector history) { this.eventHistory = history; }
    public void setExecutionStats(MockHardware.ExecutionStats stats) { this.executionStats = stats; }
    public void setCategory(String category) { this.category = category; }
    
    public String toString() {
        return "TestResult{name='" + testName + "', passed=" + passed + 
               ", time=" + executionTime + "ms}";
    }
}
