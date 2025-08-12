import java.util.*;

/**
 * TestReport - Comprehensive test execution report
 * 
 * Contains summary statistics and detailed results from test suite execution.
 */
public class TestReport {
    
    private String suiteName;
    private Vector results;
    private int totalTests;
    private int passedTests;
    private int failedTests;
    private long executionTime;
    private long timestamp;
    
    /**
     * Create test report
     */
    public TestReport(String suiteName, Vector results, int totalTests, 
                     int passedTests, int failedTests, long executionTime) {
        this.suiteName = suiteName;
        this.results = results != null ? results : new Vector();
        this.totalTests = totalTests;
        this.passedTests = passedTests;
        this.failedTests = failedTests;
        this.executionTime = executionTime;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters
    public String getSuiteName() { return suiteName; }
    public Vector getResults() { return results; }
    public int getTotalTests() { return totalTests; }
    public int getPassedTests() { return passedTests; }
    public int getFailedTests() { return failedTests; }
    public long getExecutionTime() { return executionTime; }
    public long getTimestamp() { return timestamp; }
    
    /**
     * Get pass rate as percentage
     */
    public double getPassRate() {
        return totalTests > 0 ? (double) passedTests / totalTests * 100.0 : 0.0;
    }
    
    /**
     * Get failed test results
     */
    public Vector getFailedResults() {
        Vector failed = new Vector();
        for (int i = 0; i < results.size(); i++) {
            TestResult result = (TestResult) results.elementAt(i);
            if (!result.passed()) {
                failed.addElement(result);
            }
        }
        return failed;
    }
    
    /**
     * Get passed test results
     */
    public Vector getPassedResults() {
        Vector passed = new Vector();
        for (int i = 0; i < results.size(); i++) {
            TestResult result = (TestResult) results.elementAt(i);
            if (result.passed()) {
                passed.addElement(result);
            }
        }
        return passed;
    }
    
    public String toString() {
        return "TestReport{suite='" + suiteName + "', total=" + totalTests + 
               ", passed=" + passedTests + ", failed=" + failedTests + 
               ", time=" + executionTime + "ms}";
    }
}
