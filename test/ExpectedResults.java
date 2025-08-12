import java.util.*;

/**
 * ExpectedResults - Defines expected outcomes for test validation
 * 
 * Contains expected hardware states, display content, execution metrics,
 * and other validation criteria for test cases.
 */
public class ExpectedResults {
    
    // Expected hardware state
    private MockHardware.HardwareState expectedHardwareState;
    
    // Expected display content
    private String expectedDisplayContent;
    private Hashtable expectedDisplayLines; // line number -> expected content
    
    // Expected motor states
    private Hashtable expectedMotorStates; // port -> MotorExpectation
    
    // Expected sensor values
    private Hashtable expectedSensorValues; // port -> SensorExpectation
    
    // Expected sound operations
    private Vector expectedSounds; // List of SoundExpectation
    
    // Expected execution metrics
    private long maxExecutionTimeMs;
    private long minExecutionTimeMs;
    private int minEventCount;
    private int maxEventCount;
    private int expectedBlockExecutions;
    
    // Expected event patterns
    private Vector expectedEventTypes; // List of expected event types in order
    private boolean strictEventOrder;  // Whether event order must match exactly
    
    /**
     * Initialize with default expectations
     */
    public ExpectedResults() {
        expectedDisplayLines = new Hashtable();
        expectedMotorStates = new Hashtable();
        expectedSensorValues = new Hashtable();
        expectedSounds = new Vector();
        expectedEventTypes = new Vector();
        
        maxExecutionTimeMs = 0;
        minExecutionTimeMs = 0;
        minEventCount = 0;
        maxEventCount = 0;
        expectedBlockExecutions = 0;
        strictEventOrder = false;
    }
    
    // Getters
    public MockHardware.HardwareState getExpectedHardwareState() { return expectedHardwareState; }
    public String getExpectedDisplayContent() { return expectedDisplayContent; }
    public long getMaxExecutionTimeMs() { return maxExecutionTimeMs; }
    public long getMinExecutionTimeMs() { return minExecutionTimeMs; }
    public int getMinEventCount() { return minEventCount; }
    public int getMaxEventCount() { return maxEventCount; }
    public int getExpectedBlockExecutions() { return expectedBlockExecutions; }
    public boolean isStrictEventOrder() { return strictEventOrder; }
    
    // Setters
    public void setExpectedHardwareState(MockHardware.HardwareState state) { 
        this.expectedHardwareState = state; 
    }
    public void setExpectedDisplayContent(String content) { 
        this.expectedDisplayContent = content; 
    }
    public void setMaxExecutionTimeMs(long maxTimeMs) { 
        this.maxExecutionTimeMs = maxTimeMs; 
    }
    public void setMinExecutionTimeMs(long minTimeMs) { 
        this.minExecutionTimeMs = minTimeMs; 
    }
    public void setMinEventCount(int minEvents) { 
        this.minEventCount = minEvents; 
    }
    public void setMaxEventCount(int maxEvents) { 
        this.maxEventCount = maxEvents; 
    }
    public void setExpectedBlockExecutions(int executions) { 
        this.expectedBlockExecutions = executions; 
    }
    public void setStrictEventOrder(boolean strict) { 
        this.strictEventOrder = strict; 
    }
    
    /**
     * Set expected content for specific display line
     */
    public void setExpectedDisplayLine(int lineNumber, String expectedContent) {
        expectedDisplayLines.put(new Integer(lineNumber), expectedContent);
    }
    
    /**
     * Get expected content for display line
     */
    public String getExpectedDisplayLine(int lineNumber) {
        return (String) expectedDisplayLines.get(new Integer(lineNumber));
    }
    
    /**
     * Get all expected display lines
     */
    public Hashtable getExpectedDisplayLines() {
        return expectedDisplayLines;
    }
    
    /**
     * Set expected motor state
     */
    public void setExpectedMotorState(String port, int speed, double rotation, String direction) {
        MotorExpectation expectation = new MotorExpectation(port, speed, rotation, direction);
        expectedMotorStates.put(port, expectation);
    }
    
    /**
     * Get expected motor state
     */
    public MotorExpectation getExpectedMotorState(String port) {
        return (MotorExpectation) expectedMotorStates.get(port);
    }
    
    /**
     * Get all expected motor states
     */
    public Hashtable getExpectedMotorStates() {
        return expectedMotorStates;
    }
    
    /**
     * Set expected sensor value
     */
    public void setExpectedSensorValue(String port, String type, Object value) {
        SensorExpectation expectation = new SensorExpectation(port, type, value);
        expectedSensorValues.put(port, expectation);
    }
    
    /**
     * Get expected sensor value
     */
    public SensorExpectation getExpectedSensorValue(String port) {
        return (SensorExpectation) expectedSensorValues.get(port);
    }
    
    /**
     * Get all expected sensor values
     */
    public Hashtable getExpectedSensorValues() {
        return expectedSensorValues;
    }
    
    /**
     * Add expected sound operation
     */
    public void addExpectedSound(int frequency, int duration) {
        expectedSounds.addElement(new SoundExpectation(frequency, duration, ""));
    }
    
    /**
     * Add expected note operation
     */
    public void addExpectedNote(String note, int duration) {
        expectedSounds.addElement(new SoundExpectation(0, duration, note));
    }
    
    /**
     * Get expected sounds
     */
    public Vector getExpectedSounds() {
        return expectedSounds;
    }
    
    /**
     * Add expected event type
     */
    public void addExpectedEventType(String eventType) {
        expectedEventTypes.addElement(eventType);
    }
    
    /**
     * Get expected event types
     */
    public Vector getExpectedEventTypes() {
        return expectedEventTypes;
    }
    
    /**
     * Clone expected results
     */
    public ExpectedResults clone() {
        ExpectedResults copy = new ExpectedResults();
        copy.expectedHardwareState = expectedHardwareState; // Shallow copy
        copy.expectedDisplayContent = expectedDisplayContent;
        copy.expectedDisplayLines = new Hashtable(expectedDisplayLines);
        copy.expectedMotorStates = new Hashtable(expectedMotorStates);
        copy.expectedSensorValues = new Hashtable(expectedSensorValues);
        copy.expectedSounds = new Vector(expectedSounds);
        copy.expectedEventTypes = new Vector(expectedEventTypes);
        copy.maxExecutionTimeMs = maxExecutionTimeMs;
        copy.minExecutionTimeMs = minExecutionTimeMs;
        copy.minEventCount = minEventCount;
        copy.maxEventCount = maxEventCount;
        copy.expectedBlockExecutions = expectedBlockExecutions;
        copy.strictEventOrder = strictEventOrder;
        return copy;
    }
    
    /**
     * Motor expectation
     */
    public static class MotorExpectation {
        private String port;
        private int expectedSpeed;
        private double expectedRotation;
        private String expectedDirection;
        private double rotationTolerance;
        
        public MotorExpectation(String port, int speed, double rotation, String direction) {
            this.port = port;
            this.expectedSpeed = speed;
            this.expectedRotation = rotation;
            this.expectedDirection = direction;
            this.rotationTolerance = 5.0; // Default 5 degree tolerance
        }
        
        public String getPort() { return port; }
        public int getExpectedSpeed() { return expectedSpeed; }
        public double getExpectedRotation() { return expectedRotation; }
        public String getExpectedDirection() { return expectedDirection; }
        public double getRotationTolerance() { return rotationTolerance; }
        
        public void setRotationTolerance(double tolerance) { 
            this.rotationTolerance = tolerance; 
        }
        
        /**
         * Check if motor state matches expectation
         */
        public boolean matches(MockMotor.MotorState actualState) {
            if (!port.equals(actualState.getPort())) return false;
            if (expectedSpeed != actualState.getSpeed()) return false;
            if (!expectedDirection.equals(actualState.getDirection())) return false;
            
            double rotationDiff = Math.abs(expectedRotation - actualState.getRotation());
            return rotationDiff <= rotationTolerance;
        }
        
        public String toString() {
            return "MotorExpectation{port=" + port + ", speed=" + expectedSpeed + 
                   ", rotation=" + expectedRotation + ", direction=" + expectedDirection + "}";
        }
    }
    
    /**
     * Sensor expectation
     */
    public static class SensorExpectation {
        private String port;
        private String expectedType;
        private Object expectedValue;
        
        public SensorExpectation(String port, String type, Object value) {
            this.port = port;
            this.expectedType = type;
            this.expectedValue = value;
        }
        
        public String getPort() { return port; }
        public String getExpectedType() { return expectedType; }
        public Object getExpectedValue() { return expectedValue; }
        
        /**
         * Check if sensor state matches expectation
         */
        public boolean matches(MockSensor.SensorState actualState) {
            if (!port.equals(actualState.getPort())) return false;
            if (!expectedType.equals(actualState.getSensorType())) return false;
            
            if (expectedValue == null && actualState.getCurrentValue() == null) return true;
            if (expectedValue == null || actualState.getCurrentValue() == null) return false;
            
            return expectedValue.equals(actualState.getCurrentValue());
        }
        
        public String toString() {
            return "SensorExpectation{port=" + port + ", type=" + expectedType + 
                   ", value=" + expectedValue + "}";
        }
    }
    
    /**
     * Sound expectation
     */
    public static class SoundExpectation {
        private int expectedFrequency;
        private int expectedDuration;
        private String expectedNote;
        
        public SoundExpectation(int frequency, int duration, String note) {
            this.expectedFrequency = frequency;
            this.expectedDuration = duration;
            this.expectedNote = note;
        }
        
        public int getExpectedFrequency() { return expectedFrequency; }
        public int getExpectedDuration() { return expectedDuration; }
        public String getExpectedNote() { return expectedNote; }
        
        /**
         * Check if sound operation matches expectation
         */
        public boolean matches(MockSound.SoundOperation actualSound) {
            if (expectedNote != null && expectedNote.length() > 0) {
                // Note-based comparison
                return expectedNote.equals(actualSound.getNote()) && 
                       expectedDuration == actualSound.getDuration();
            } else {
                // Frequency-based comparison
                return expectedFrequency == actualSound.getFrequency() && 
                       expectedDuration == actualSound.getDuration();
            }
        }
        
        public String toString() {
            if (expectedNote != null && expectedNote.length() > 0) {
                return "SoundExpectation{note=" + expectedNote + ", duration=" + expectedDuration + "}";
            } else {
                return "SoundExpectation{frequency=" + expectedFrequency + ", duration=" + expectedDuration + "}";
            }
        }
    }
}
