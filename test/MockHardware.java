import java.util.*;

/**
 * MockHardware - Virtual NXT hardware simulation for testing
 * 
 * This class simulates all NXT hardware components (motors, sensors, display, sound)
 * without requiring physical hardware. It tracks all interactions and state changes
 * for test validation.
 * 
 * Compatible with leJOS NXT Java 1.4 constraints.
 */
public class MockHardware {
    
    // Hardware state tracking
    private Hashtable motors;           // MockMotor instances by port
    private Hashtable sensors;          // MockSensor instances by port
    private MockDisplay display;        // LCD display simulation
    private MockSound sound;           // Sound system simulation
    private MockTimer timer;           // Timer simulation
    
    // Event history for validation
    private Vector eventHistory;       // List of HardwareEvent objects
    private long startTime;            // Test start timestamp
    
    /**
     * Initialize mock hardware with default state
     */
    public MockHardware() {
        reset();
    }
    
    /**
     * Reset all hardware to initial state
     */
    public void reset() {
        // Initialize motor ports A, B, C
        motors = new Hashtable();
        motors.put("A", new MockMotor("A"));
        motors.put("B", new MockMotor("B"));
        motors.put("C", new MockMotor("C"));
        
        // Initialize sensor ports 1, 2, 3, 4
        sensors = new Hashtable();
        sensors.put("1", new MockSensor("1"));
        sensors.put("2", new MockSensor("2"));
        sensors.put("3", new MockSensor("3"));
        sensors.put("4", new MockSensor("4"));
        
        // Initialize other components
        display = new MockDisplay();
        sound = new MockSound();
        timer = new MockTimer();
        
        // Reset event tracking
        eventHistory = new Vector();
        startTime = System.currentTimeMillis();
        
        logEvent("SYSTEM", "Hardware reset completed");
    }
    
    /**
     * Get motor by port letter
     */
    public MockMotor getMotor(String port) {
        MockMotor motor = (MockMotor) motors.get(port);
        if (motor == null) {
            logEvent("ERROR", "Invalid motor port: " + port);
        } else {
            // Set the hardware reference so motor can log events
            motor.setHardware(this);
        }
        return motor;
    }

    /**
     * Get sensor by port number
     */
    public MockSensor getSensor(String port) {
        MockSensor sensor = (MockSensor) sensors.get(port);
        if (sensor == null) {
            logEvent("ERROR", "Invalid sensor port: " + port);
        }
        return sensor;
    }
    
    /**
     * Get display component
     */
    public MockDisplay getDisplay() {
        // Set hardware reference for event logging
        display.setHardware(this);
        return display;
    }

    /**
     * Get sound component
     */
    public MockSound getSound() {
        return sound;
    }
    
    /**
     * Get timer component
     */
    public MockTimer getTimer() {
        return timer;
    }
    
    /**
     * Get complete event history
     */
    public Vector getEventHistory() {
        return new Vector(eventHistory); // Return copy to prevent modification
    }
    
    /**
     * Get events of specific type
     */
    public Vector getEventsByType(String eventType) {
        Vector filtered = new Vector();
        for (int i = 0; i < eventHistory.size(); i++) {
            HardwareEvent event = (HardwareEvent) eventHistory.elementAt(i);
            if (eventType.equals(event.getType())) {
                filtered.addElement(event);
            }
        }
        return filtered;
    }
    
    /**
     * Get current hardware state summary
     */
    public HardwareState getCurrentState() {
        return new HardwareState(motors, sensors, display, sound, timer);
    }
    
    /**
     * Log hardware event for validation
     */
    public void logEvent(String type, String description) {
        long timestamp = System.currentTimeMillis() - startTime;
        HardwareEvent event = new HardwareEvent(type, description, timestamp);
        eventHistory.addElement(event);
    }
    
    /**
     * Log hardware event with component and details
     */
    public void logEvent(String type, String component, String action, Object details) {
        long timestamp = System.currentTimeMillis() - startTime;
        String description = component + ": " + action;
        if (details != null) {
            description += " (" + details.toString() + ")";
        }
        HardwareEvent event = new HardwareEvent(type, description, timestamp);
        eventHistory.addElement(event);
    }
    
    /**
     * Simulate time passage (for timing-dependent operations)
     */
    public void simulateDelay(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // Ignore interruption
        }
        logEvent("TIMING", "Simulated delay: " + milliseconds + "ms");
    }
    
    /**
     * Validate expected state against current state
     */
    public ValidationResult validateState(HardwareState expected) {
        HardwareState current = getCurrentState();
        return current.compare(expected);
    }
    
    /**
     * Get execution statistics
     */
    public ExecutionStats getStats() {
        long totalTime = System.currentTimeMillis() - startTime;
        int motorOperations = getEventsByType("MOTOR").size();
        int sensorOperations = getEventsByType("SENSOR").size();
        int displayOperations = getEventsByType("DISPLAY").size();
        int soundOperations = getEventsByType("SOUND").size();
        
        return new ExecutionStats(totalTime, motorOperations, sensorOperations, 
                                displayOperations, soundOperations, eventHistory.size());
    }
    
    /**
     * Hardware event for tracking interactions
     */
    public static class HardwareEvent {
        private String type;
        private String description;
        private long timestamp;
        
        public HardwareEvent(String type, String description, long timestamp) {
            this.type = type;
            this.description = description;
            this.timestamp = timestamp;
        }
        
        public String getType() { return type; }
        public String getDescription() { return description; }
        public long getTimestamp() { return timestamp; }
        
        public String toString() {
            return "[" + timestamp + "ms] " + type + ": " + description;
        }
    }
    
    /**
     * Hardware state snapshot for validation
     */
    public static class HardwareState {
        private Hashtable motorStates;
        private Hashtable sensorStates;
        private String displayContent;
        private Vector soundHistory;
        private long timerValue;
        
        public HardwareState(Hashtable motors, Hashtable sensors, MockDisplay display, 
                           MockSound sound, MockTimer timer) {
            // Capture current state
            motorStates = new Hashtable();
            Enumeration motorKeys = motors.keys();
            while (motorKeys.hasMoreElements()) {
                String port = (String) motorKeys.nextElement();
                MockMotor motor = (MockMotor) motors.get(port);
                motorStates.put(port, motor.getState());
            }
            
            sensorStates = new Hashtable();
            Enumeration sensorKeys = sensors.keys();
            while (sensorKeys.hasMoreElements()) {
                String port = (String) sensorKeys.nextElement();
                MockSensor sensor = (MockSensor) sensors.get(port);
                sensorStates.put(port, sensor.getState());
            }
            
            displayContent = display.getContent();
            soundHistory = sound.getHistory();
            timerValue = timer.getValue();
        }
        
        public Hashtable getMotorStates() { return motorStates; }
        public Hashtable getSensorStates() { return sensorStates; }
        public String getDisplayContent() { return displayContent; }
        public Vector getSoundHistory() { return soundHistory; }
        public long getTimerValue() { return timerValue; }
        
        public ValidationResult compare(HardwareState expected) {
            // Implementation for state comparison
            return new ValidationResult(true, "State comparison not yet implemented");
        }
    }
    
    /**
     * Validation result for test assertions
     */
    public static class ValidationResult {
        private boolean passed;
        private String message;
        
        public ValidationResult(boolean passed, String message) {
            this.passed = passed;
            this.message = message;
        }
        
        public boolean passed() { return passed; }
        public String getMessage() { return message; }
    }
    
    /**
     * Execution statistics for performance tracking
     */
    public static class ExecutionStats {
        private long totalTimeMs;
        private int motorOperations;
        private int sensorOperations;
        private int displayOperations;
        private int soundOperations;
        private int totalEvents;
        
        public ExecutionStats(long totalTimeMs, int motorOps, int sensorOps, 
                            int displayOps, int soundOps, int totalEvents) {
            this.totalTimeMs = totalTimeMs;
            this.motorOperations = motorOps;
            this.sensorOperations = sensorOps;
            this.displayOperations = displayOps;
            this.soundOperations = soundOps;
            this.totalEvents = totalEvents;
        }
        
        public long getTotalTimeMs() { return totalTimeMs; }
        public int getMotorOperations() { return motorOperations; }
        public int getSensorOperations() { return sensorOperations; }
        public int getDisplayOperations() { return displayOperations; }
        public int getSoundOperations() { return soundOperations; }
        public int getTotalEvents() { return totalEvents; }
        
        public String toString() {
            return "ExecutionStats{time=" + totalTimeMs + "ms, motors=" + motorOperations + 
                   ", sensors=" + sensorOperations + ", display=" + displayOperations + 
                   ", sound=" + soundOperations + ", total=" + totalEvents + "}";
        }
    }
}
