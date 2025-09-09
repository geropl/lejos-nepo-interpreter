/**
 * Tracks execution state during test runs to enable dynamic sensor scenarios.
 * Provides iteration counting for scenario triggering.
 */
public class ExecutionContext {
    private int currentIteration = 0;
    
    /**
     * Increment the current iteration counter.
     * Called at the beginning of each loop iteration.
     */
    public void incrementIteration() {
        currentIteration++;
    }
    
    /**
     * Get the current iteration number.
     */
    public int getCurrentIteration() {
        return currentIteration;
    }
    
    /**
     * Get a string representation for logging.
     */
    public String toString() {
        return "ExecutionContext{iteration=" + currentIteration + "}";
    }
}