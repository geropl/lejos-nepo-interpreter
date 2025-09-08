/**
 * Tracks execution state during test runs to enable dynamic sensor scenarios.
 * Provides iteration counting and elapsed time tracking for precise scenario triggering.
 */
public class ExecutionContext {
    private int currentIteration = 0;
    private long startTime = System.currentTimeMillis();
    private boolean loopActive = false;
    
    /**
     * Increment the current iteration counter.
     * Called at the beginning of each loop iteration.
     */
    public void incrementIteration() {
        currentIteration++;
    }
    
    /**
     * Get the current iteration number (0-based).
     */
    public int getCurrentIteration() {
        return currentIteration;
    }
    
    /**
     * Get elapsed time since execution started in milliseconds.
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * Reset the execution context for a new test run.
     */
    public void reset() {
        currentIteration = 0;
        startTime = System.currentTimeMillis();
        loopActive = false;
    }
    
    /**
     * Mark that a loop is currently active.
     */
    public void setLoopActive(boolean active) {
        loopActive = active;
    }
    
    /**
     * Check if a loop is currently executing.
     */
    public boolean isLoopActive() {
        return loopActive;
    }
    
    /**
     * Get a string representation for logging.
     */
    public String toString() {
        return "ExecutionContext{iteration=" + currentIteration + 
               ", elapsed=" + getElapsedTime() + "ms, loopActive=" + loopActive + "}";
    }
}