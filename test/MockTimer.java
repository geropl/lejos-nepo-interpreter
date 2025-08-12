import java.util.*;

/**
 * MockTimer - Simulates NXT timer functionality for testing
 * 
 * Provides timer operations with millisecond precision and
 * tracks timer usage for test validation.
 */
public class MockTimer {
    
    // Timer state
    private long startTime;           // When timer was started/reset
    private boolean isRunning;        // Whether timer is running
    private long pausedTime;          // Accumulated time when paused
    private boolean isPaused;         // Whether timer is paused
    
    // Operation tracking
    private Vector operationHistory;  // History of timer operations
    private int operationCount;       // Total number of operations
    
    /**
     * Initialize mock timer
     */
    public MockTimer() {
        operationHistory = new Vector();
        reset();
    }
    
    /**
     * Reset timer to initial state
     */
    public void reset() {
        startTime = System.currentTimeMillis();
        isRunning = true;
        pausedTime = 0;
        isPaused = false;
        operationCount = 0;
        operationHistory.clear();
        
        logOperation("RESET", "Timer reset to 0");
    }
    
    /**
     * Get current timer value in milliseconds
     */
    public long getValue() {
        long currentValue;
        
        if (isPaused) {
            currentValue = pausedTime;
        } else if (isRunning) {
            currentValue = pausedTime + (System.currentTimeMillis() - startTime);
        } else {
            currentValue = 0;
        }
        
        // Don't log getValue operations to avoid circular dependency
        return currentValue;
    }

    /**
     * Get timer value in seconds
     */
    public double getValueInSeconds() {
        return getValue() / 1000.0;
    }
    
    /**
     * Start timer (if stopped)
     */
    public void start() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
            isPaused = false;
            logOperation("START", "Timer started");
        } else {
            logOperation("START", "Timer already running");
        }
    }
    
    /**
     * Stop timer
     */
    public void stop() {
        if (isRunning) {
            if (!isPaused) {
                pausedTime += System.currentTimeMillis() - startTime;
            }
            isRunning = false;
            isPaused = false;
            logOperation("STOP", "Timer stopped at " + pausedTime + "ms");
        } else {
            logOperation("STOP", "Timer already stopped");
        }
    }
    
    /**
     * Pause timer (keeps accumulated time)
     */
    public void pause() {
        if (isRunning && !isPaused) {
            pausedTime += System.currentTimeMillis() - startTime;
            isPaused = true;
            logOperation("PAUSE", "Timer paused at " + pausedTime + "ms");
        } else {
            logOperation("PAUSE", "Timer not running or already paused");
        }
    }
    
    /**
     * Resume timer from pause
     */
    public void resume() {
        if (isRunning && isPaused) {
            startTime = System.currentTimeMillis();
            isPaused = false;
            logOperation("RESUME", "Timer resumed from " + pausedTime + "ms");
        } else {
            logOperation("RESUME", "Timer not paused");
        }
    }
    
    /**
     * Check if timer is running
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Check if timer is paused
     */
    public boolean isPaused() {
        return isPaused;
    }
    
    /**
     * Get operation count
     */
    public int getOperationCount() {
        return operationCount;
    }
    
    /**
     * Get operation history
     */
    public Vector getOperationHistory() {
        return new Vector(operationHistory); // Return copy
    }
    
    /**
     * Get current timer state
     */
    public TimerState getState() {
        return new TimerState(getValue(), isRunning, isPaused, operationCount);
    }
    
    /**
     * Wait until timer reaches specified value
     */
    public void waitUntil(long targetMs) {
        logOperation("WAIT_UNTIL", "Waiting until " + targetMs + "ms");
        
        while (getValue() < targetMs) {
            try {
                Thread.sleep(10); // Check every 10ms
            } catch (InterruptedException e) {
                break;
            }
        }
        
        logOperation("WAIT_COMPLETE", "Reached " + getValue() + "ms");
    }
    
    /**
     * Wait for specified duration from current time
     */
    public void waitFor(long durationMs) {
        long targetTime = getValue() + durationMs;
        waitUntil(targetTime);
    }
    
    /**
     * Simulate time passage (for testing)
     */
    public void simulateTimePassage(long milliseconds) {
        if (isRunning && !isPaused) {
            // Advance the start time backwards to simulate time passage
            startTime -= milliseconds;
            logOperation("SIMULATE", "Simulated " + milliseconds + "ms passage");
        }
    }
    
    /**
     * Get elapsed time since last reset
     */
    public long getElapsedTime() {
        return getValue();
    }
    
    /**
     * Get elapsed time since specific timestamp
     */
    public long getElapsedTimeSince(long timestamp) {
        return System.currentTimeMillis() - timestamp;
    }
    
    /**
     * Create timestamp for later comparison
     */
    public long createTimestamp() {
        long timestamp = System.currentTimeMillis();
        logOperation("TIMESTAMP", "Created timestamp: " + timestamp);
        return timestamp;
    }
    
    /**
     * Format time value as human-readable string
     */
    public String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long ms = milliseconds % 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        return String.valueOf(minutes) + ":" + 
               (seconds < 10 ? "0" : "") + seconds + "." +
               (ms < 100 ? "0" : "") + (ms < 10 ? "0" : "") + ms;
    }
    
    /**
     * Get current time formatted
     */
    public String getFormattedTime() {
        return formatTime(getValue());
    }
    
    /**
     * Log timer operation
     */
    private void logOperation(String operation, String details) {
        long timestamp = System.currentTimeMillis();
        operationCount++;
        
        // Get current value without logging to avoid circular dependency
        long currentValue;
        if (isPaused) {
            currentValue = pausedTime;
        } else if (isRunning) {
            currentValue = pausedTime + (timestamp - startTime);
        } else {
            currentValue = 0;
        }
        
        TimerOperation op = new TimerOperation(operation, details, timestamp, currentValue);
        operationHistory.addElement(op);
    }

    /**
     * Timer state snapshot
     */
    public static class TimerState {
        private long value;
        private boolean running;
        private boolean paused;
        private int operationCount;
        
        public TimerState(long value, boolean running, boolean paused, int operationCount) {
            this.value = value;
            this.running = running;
            this.paused = paused;
            this.operationCount = operationCount;
        }
        
        // Getters
        public long getValue() { return value; }
        public boolean isRunning() { return running; }
        public boolean isPaused() { return paused; }
        public int getOperationCount() { return operationCount; }
        
        public String toString() {
            return "TimerState{value=" + value + "ms, running=" + running + 
                   ", paused=" + paused + ", ops=" + operationCount + "}";
        }
        
        /**
         * Compare with expected state (with tolerance for timing)
         */
        public boolean matches(TimerState expected, long tolerance) {
            if (running != expected.running) return false;
            if (paused != expected.paused) return false;
            
            // Check value within tolerance
            long valueDiff = Math.abs(value - expected.value);
            return valueDiff <= tolerance;
        }
    }
    
    /**
     * Timer operation record
     */
    public static class TimerOperation {
        private String operation;
        private String details;
        private long timestamp;
        private long timerValue;
        
        public TimerOperation(String operation, String details, long timestamp, long timerValue) {
            this.operation = operation;
            this.details = details;
            this.timestamp = timestamp;
            this.timerValue = timerValue;
        }
        
        public String getOperation() { return operation; }
        public String getDetails() { return details; }
        public long getTimestamp() { return timestamp; }
        public long getTimerValue() { return timerValue; }
        
        public String toString() {
            return "[" + timestamp + "] " + operation + " (timer=" + timerValue + "ms): " + details;
        }
    }
}
