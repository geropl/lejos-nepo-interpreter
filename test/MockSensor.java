import java.util.*;

/**
 * MockSensor - Simulates NXT sensor behavior for all sensor types
 * 
 * Supports touch, ultrasonic, light, sound, and gyro sensors with
 * configurable values and realistic behavior simulation.
 */
public class MockSensor {
    
    // Sensor identification
    private String port;
    private String sensorType;     // "touch", "ultrasonic", "light", "sound", "gyro"
    
    // Current sensor state
    private Object currentValue;   // Current sensor reading
    private boolean isActive;      // Whether sensor is active/connected
    private long lastReadTime;     // Timestamp of last reading
    private int readCount;         // Number of times sensor was read
    
    // Sensor-specific state
    private boolean touchPressed;  // Touch sensor state
    private int ultrasonicDistance; // Ultrasonic distance in cm
    private int lightLevel;        // Light level (0-100)
    private int soundLevel;        // Sound level (0-100)
    private int gyroAngle;         // Gyro angle in degrees
    
    // History and simulation
    private Vector readHistory;    // History of sensor readings
    private Random random;         // For realistic value variation
    
    /**
     * Create mock sensor for specified port
     */
    public MockSensor(String port) {
        this.port = port;
        this.readHistory = new Vector();
        this.random = new Random();
        reset();
    }
    
    /**
     * Reset sensor to initial state
     */
    public void reset() {
        sensorType = "none";
        currentValue = null;
        isActive = false;
        lastReadTime = System.currentTimeMillis();
        readCount = 0;
        
        // Reset sensor-specific values
        touchPressed = false;
        ultrasonicDistance = 255; // Max distance (no object detected)
        lightLevel = 50;          // Mid-range light
        soundLevel = 0;           // Quiet
        gyroAngle = 0;            // Neutral position
        
        readHistory.clear();
        logReading("RESET", "Sensor reset to initial state");
    }
    
    /**
     * Configure sensor as touch sensor
     */
    public void configureTouchSensor() {
        sensorType = "touch";
        isActive = true;
        currentValue = new Boolean(touchPressed);
        logReading("CONFIGURE", "Configured as touch sensor");
    }
    
    /**
     * Configure sensor as ultrasonic sensor
     */
    public void configureUltrasonicSensor() {
        sensorType = "ultrasonic";
        isActive = true;
        currentValue = new Integer(ultrasonicDistance);
        logReading("CONFIGURE", "Configured as ultrasonic sensor");
    }
    
    /**
     * Configure sensor as light sensor
     */
    public void configureLightSensor() {
        sensorType = "light";
        isActive = true;
        currentValue = new Integer(lightLevel);
        logReading("CONFIGURE", "Configured as light sensor");
    }
    
    /**
     * Configure sensor as sound sensor
     */
    public void configureSoundSensor() {
        sensorType = "sound";
        isActive = true;
        currentValue = new Integer(soundLevel);
        logReading("CONFIGURE", "Configured as sound sensor");
    }
    
    /**
     * Configure sensor as gyro sensor
     */
    public void configureGyroSensor() {
        sensorType = "gyro";
        isActive = true;
        currentValue = new Integer(gyroAngle);
        logReading("CONFIGURE", "Configured as gyro sensor");
    }
    
    /**
     * Set touch sensor state
     */
    public void setTouchPressed(boolean pressed) {
        if (!"touch".equals(sensorType)) {
            configureTouchSensor();
        }
        touchPressed = pressed;
        currentValue = new Boolean(pressed);
        logReading("SET_VALUE", "Touch pressed: " + pressed);
    }
    
    /**
     * Set ultrasonic distance reading
     */
    public void setUltrasonicDistance(int distance) {
        if (!"ultrasonic".equals(sensorType)) {
            configureUltrasonicSensor();
        }
        ultrasonicDistance = Math.max(0, Math.min(255, distance)); // Valid range
        currentValue = new Integer(ultrasonicDistance);
        logReading("SET_VALUE", "Ultrasonic distance: " + ultrasonicDistance + "cm");
    }
    
    /**
     * Set light sensor level
     */
    public void setLightLevel(int level) {
        if (!"light".equals(sensorType)) {
            configureLightSensor();
        }
        lightLevel = Math.max(0, Math.min(100, level)); // Valid range
        currentValue = new Integer(lightLevel);
        logReading("SET_VALUE", "Light level: " + lightLevel + "%");
    }
    
    /**
     * Set sound sensor level
     */
    public void setSoundLevel(int level) {
        if (!"sound".equals(sensorType)) {
            configureSoundSensor();
        }
        soundLevel = Math.max(0, Math.min(100, level)); // Valid range
        currentValue = new Integer(soundLevel);
        logReading("SET_VALUE", "Sound level: " + soundLevel + "%");
    }
    
    /**
     * Set gyro sensor angle
     */
    public void setGyroAngle(int angle) {
        if (!"gyro".equals(sensorType)) {
            configureGyroSensor();
        }
        gyroAngle = angle; // Allow full range for gyro
        currentValue = new Integer(gyroAngle);
        logReading("SET_VALUE", "Gyro angle: " + gyroAngle + " degrees");
    }
    
    /**
     * Read current sensor value (simulates sensor reading)
     */
    public Object readValue() {
        if (!isActive) {
            logReading("ERROR", "Attempted to read inactive sensor");
            return null;
        }
        
        lastReadTime = System.currentTimeMillis();
        readCount++;
        
        // Add slight variation to simulate real sensor behavior
        Object readValue = addRealisticVariation(currentValue);
        
        logReading("READ", "Value: " + readValue);
        return readValue;
    }
    
    /**
     * Check if touch sensor is pressed
     */
    public boolean isPressed() {
        if (!"touch".equals(sensorType)) {
            return false;
        }
        Boolean value = (Boolean) readValue();
        return value != null ? value.booleanValue() : false;
    }
    
    /**
     * Get ultrasonic distance
     */
    public int getDistance() {
        if (!"ultrasonic".equals(sensorType)) {
            return 255; // Max distance if not ultrasonic
        }
        Integer value = (Integer) readValue();
        return value != null ? value.intValue() : 255;
    }
    
    /**
     * Get light level
     */
    public int getLightLevel() {
        if (!"light".equals(sensorType)) {
            return 0;
        }
        Integer value = (Integer) readValue();
        return value != null ? value.intValue() : 0;
    }
    
    /**
     * Get sound level
     */
    public int getSoundLevel() {
        if (!"sound".equals(sensorType)) {
            return 0;
        }
        Integer value = (Integer) readValue();
        return value != null ? value.intValue() : 0;
    }
    
    /**
     * Get gyro angle
     */
    public int getAngle() {
        if (!"gyro".equals(sensorType)) {
            return 0;
        }
        Integer value = (Integer) readValue();
        return value != null ? value.intValue() : 0;
    }
    
    /**
     * Get sensor port
     */
    public String getPort() {
        return port;
    }
    
    /**
     * Get sensor type
     */
    public String getSensorType() {
        return sensorType;
    }
    
    /**
     * Check if sensor is active
     */
    public boolean isActive() {
        return isActive;
    }
    
    /**
     * Get read count
     */
    public int getReadCount() {
        return readCount;
    }
    
    /**
     * Get current sensor state
     */
    public SensorState getState() {
        return new SensorState(port, sensorType, currentValue, isActive, 
                             readCount, lastReadTime);
    }
    
    /**
     * Get reading history
     */
    public Vector getReadHistory() {
        return new Vector(readHistory); // Return copy
    }
    
    /**
     * Add realistic variation to sensor readings
     */
    private Object addRealisticVariation(Object baseValue) {
        // For testing, disable variation to ensure predictable results
        // In production, this would add realistic sensor noise
        return baseValue;
    }

    /**
     * Log sensor reading
     */
    private void logReading(String operation, String details) {
        long timestamp = System.currentTimeMillis();
        SensorReading reading = new SensorReading(operation, details, timestamp, 
                                                currentValue, sensorType);
        readHistory.addElement(reading);
    }
    
    /**
     * Simulate sensor scenario (for testing)
     */
    public void simulateScenario(String scenarioName) {
        if ("touch_press_release".equals(scenarioName)) {
            configureTouchSensor();
            setTouchPressed(true);
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            setTouchPressed(false);
        } else if ("ultrasonic_approach".equals(scenarioName)) {
            configureUltrasonicSensor();
            for (int dist = 100; dist >= 10; dist -= 10) {
                setUltrasonicDistance(dist);
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }
        } else if ("light_fade".equals(scenarioName)) {
            configureLightSensor();
            for (int level = 100; level >= 0; level -= 10) {
                setLightLevel(level);
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }
        }
        logReading("SCENARIO", "Simulated scenario: " + scenarioName);
    }
    
    /**
     * Sensor state snapshot
     */
    public static class SensorState {
        private String port;
        private String sensorType;
        private Object currentValue;
        private boolean isActive;
        private int readCount;
        private long lastReadTime;
        
        public SensorState(String port, String sensorType, Object currentValue,
                          boolean isActive, int readCount, long lastReadTime) {
            this.port = port;
            this.sensorType = sensorType;
            this.currentValue = currentValue;
            this.isActive = isActive;
            this.readCount = readCount;
            this.lastReadTime = lastReadTime;
        }
        
        // Getters
        public String getPort() { return port; }
        public String getSensorType() { return sensorType; }
        public Object getCurrentValue() { return currentValue; }
        public boolean isActive() { return isActive; }
        public int getReadCount() { return readCount; }
        public long getLastReadTime() { return lastReadTime; }
        
        public String toString() {
            return "SensorState{port=" + port + ", type=" + sensorType + 
                   ", value=" + currentValue + ", active=" + isActive + 
                   ", reads=" + readCount + "}";
        }
        
        /**
         * Compare with expected state
         */
        public boolean matches(SensorState expected) {
            if (!port.equals(expected.port)) return false;
            if (!sensorType.equals(expected.sensorType)) return false;
            if (isActive != expected.isActive) return false;
            
            // Compare values with type-specific logic
            if (currentValue == null && expected.currentValue == null) return true;
            if (currentValue == null || expected.currentValue == null) return false;
            
            return currentValue.equals(expected.currentValue);
        }
    }
    
    /**
     * Sensor reading record
     */
    public static class SensorReading {
        private String operation;
        private String details;
        private long timestamp;
        private Object value;
        private String sensorType;
        
        public SensorReading(String operation, String details, long timestamp,
                           Object value, String sensorType) {
            this.operation = operation;
            this.details = details;
            this.timestamp = timestamp;
            this.value = value;
            this.sensorType = sensorType;
        }
        
        public String getOperation() { return operation; }
        public String getDetails() { return details; }
        public long getTimestamp() { return timestamp; }
        public Object getValue() { return value; }
        public String getSensorType() { return sensorType; }
        
        public String toString() {
            return "[" + timestamp + "] " + operation + " (" + sensorType + "): " + details;
        }
    }
}
