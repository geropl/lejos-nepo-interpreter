/**
 * MockMotor - Simulates NXT regulated motor behavior
 * 
 * Tracks motor state including speed, rotation, direction and provides
 * realistic simulation of motor operations for testing.
 */
public class MockMotor {
    
    // Motor identification
    private String port;
    
    // Current motor state
    private int speed;              // Current speed (0-720 degrees/second)
    private double totalRotation;   // Total rotation in degrees
    private boolean isMoving;       // Whether motor is currently moving
    private String direction;       // "FORWARD", "BACKWARD", "STOPPED"
    private boolean isRegulated;    // Whether motor is speed-regulated
    
    // Operation tracking
    private long lastOperationTime; // Timestamp of last operation
    private int powerSetting;       // Last power setting (-100 to 100)
    private String rotationType;    // "ROTATIONS", "DEGREES", "UNLIMITED"
    private double targetRotation;  // Target rotation for current operation
    private double startRotation;   // Rotation at start of current operation
    
    // History tracking
    private java.util.Vector operationHistory;
    private MockHardware hardware; // Reference to main hardware for event logging
    
    /**
     * Create mock motor for specified port
     */
    public MockMotor(String port) {
        this.port = port;
        this.operationHistory = new java.util.Vector();
        reset();
    }
    
    /**
     * Set hardware reference for event logging
     */
    public void setHardware(MockHardware hardware) {
        this.hardware = hardware;
    }

    /**
     * Reset motor to initial state
     */
    public void reset() {
        speed = 0;
        totalRotation = 0.0;
        isMoving = false;
        direction = "STOPPED";
        isRegulated = true;
        lastOperationTime = System.currentTimeMillis();
        powerSetting = 0;
        rotationType = "UNLIMITED";
        targetRotation = 0.0;
        startRotation = 0.0;
        operationHistory.clear();
        
        logOperation("RESET", "Motor reset to initial state");
    }
    
    /**
     * Set motor speed (simulates setSpeed())
     */
    public void setSpeed(int speed) {
        this.speed = Math.max(0, Math.min(720, speed)); // Clamp to valid range
        logOperation("SET_SPEED", "Speed set to " + this.speed + " deg/sec");
    }
    
    /**
     * Start motor forward (simulates forward())
     */
    public void forward() {
        isMoving = true;
        direction = "FORWARD";
        rotationType = "UNLIMITED";
        lastOperationTime = System.currentTimeMillis();
        logOperation("FORWARD", "Motor started forward at speed " + speed);
    }
    
    /**
     * Start motor backward (simulates backward())
     */
    public void backward() {
        isMoving = true;
        direction = "BACKWARD";
        rotationType = "UNLIMITED";
        lastOperationTime = System.currentTimeMillis();
        logOperation("BACKWARD", "Motor started backward at speed " + speed);
    }
    
    /**
     * Stop motor (simulates stop())
     */
    public void stop() {
        updateRotation(); // Update rotation before stopping
        isMoving = false;
        direction = "STOPPED";
        speed = 0;
        logOperation("STOP", "Motor stopped at rotation " + totalRotation);
    }
    
    /**
     * Rotate motor by specified degrees (simulates rotate())
     */
    public void rotate(int degrees) {
        updateRotation(); // Update current position
        
        startRotation = totalRotation;
        targetRotation = totalRotation + degrees;
        rotationType = "DEGREES";
        isMoving = true;
        direction = degrees >= 0 ? "FORWARD" : "BACKWARD";
        lastOperationTime = System.currentTimeMillis();
        
        // Simulate rotation completion
        simulateRotation(Math.abs(degrees));
        
        logOperation("ROTATE", "Rotated " + degrees + " degrees to " + totalRotation);
    }
    
    /**
     * Rotate motor by specified rotations (simulates rotateTo())
     */
    public void rotateBy(double rotations) {
        int degrees = (int) (rotations * 360);
        rotate(degrees);
    }
    
    /**
     * Check if motor is moving (simulates isMoving())
     */
    public boolean isMoving() {
        updateRotation(); // Update state before checking
        return isMoving;
    }
    
    /**
     * Get current rotation count (simulates getTachoCount())
     */
    public int getTachoCount() {
        updateRotation();
        return (int) totalRotation;
    }
    
    /**
     * Get current speed (simulates getSpeed())
     */
    public int getSpeed() {
        return speed;
    }
    
    /**
     * Get motor port
     */
    public String getPort() {
        return port;
    }
    
    /**
     * Get current motor state for validation
     */
    public MotorState getState() {
        updateRotation();
        return new MotorState(port, speed, totalRotation, isMoving, direction, 
                            powerSetting, rotationType, targetRotation);
    }
    
    /**
     * Get operation history
     */
    public java.util.Vector getOperationHistory() {
        return new java.util.Vector(operationHistory); // Return copy
    }
    
    /**
     * Set power setting (for tracking purposes)
     */
    public void setPowerSetting(int power) {
        this.powerSetting = Math.max(-100, Math.min(100, power));
        // Convert power to speed (rough approximation)
        setSpeed(Math.abs(powerSetting) * 7); // Max speed ~700 deg/sec
    }
    
    /**
     * Update rotation based on elapsed time (for continuous movement)
     */
    private void updateRotation() {
        if (!isMoving || speed == 0) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        long elapsedMs = currentTime - lastOperationTime;
        lastOperationTime = currentTime;
        
        // Calculate rotation based on speed and time
        double rotationDelta = (speed * elapsedMs) / 1000.0; // degrees
        
        if ("BACKWARD".equals(direction)) {
            rotationDelta = -rotationDelta;
        }
        
        totalRotation += rotationDelta;
        
        // Check if target rotation reached (for limited rotations)
        if ("DEGREES".equals(rotationType)) {
            boolean targetReached = false;
            if ("FORWARD".equals(direction) && totalRotation >= targetRotation) {
                totalRotation = targetRotation;
                targetReached = true;
            } else if ("BACKWARD".equals(direction) && totalRotation <= targetRotation) {
                totalRotation = targetRotation;
                targetReached = true;
            }
            
            if (targetReached) {
                isMoving = false;
                direction = "STOPPED";
            }
        }
    }
    
    /**
     * Simulate rotation completion (for blocking rotate() calls)
     */
    private void simulateRotation(int degrees) {
        if (speed == 0) {
            speed = 360; // Default speed if not set
        }
        
        // Calculate time needed for rotation
        long rotationTimeMs = (degrees * 1000L) / speed;
        
        // Simulate the rotation over time
        try {
            Thread.sleep(Math.min(rotationTimeMs, 100)); // Cap simulation time
        } catch (InterruptedException e) {
            // Ignore interruption
        }
        
        // Complete the rotation
        totalRotation = targetRotation;
        isMoving = false;
        direction = "STOPPED";
    }
    
    /**
     * Log motor operation
     */
    private void logOperation(String operation, String details) {
        long timestamp = System.currentTimeMillis();
        MotorOperation op = new MotorOperation(operation, details, timestamp, getState());
        operationHistory.addElement(op);
        
        // Also log to main hardware event system
        if (hardware != null) {
            hardware.logEvent("MOTOR", "Motor " + port + " " + operation.toLowerCase() + ": " + details);
        }
    }

    /**
     * Motor state snapshot
     */
    public static class MotorState {
        private String port;
        private int speed;
        private double rotation;
        private boolean moving;
        private String direction;
        private int powerSetting;
        private String rotationType;
        private double targetRotation;
        
        public MotorState(String port, int speed, double rotation, boolean moving,
                         String direction, int powerSetting, String rotationType, 
                         double targetRotation) {
            this.port = port;
            this.speed = speed;
            this.rotation = rotation;
            this.moving = moving;
            this.direction = direction;
            this.powerSetting = powerSetting;
            this.rotationType = rotationType;
            this.targetRotation = targetRotation;
        }
        
        // Getters
        public String getPort() { return port; }
        public int getSpeed() { return speed; }
        public double getRotation() { return rotation; }
        public boolean isMoving() { return moving; }
        public String getDirection() { return direction; }
        public int getPowerSetting() { return powerSetting; }
        public String getRotationType() { return rotationType; }
        public double getTargetRotation() { return targetRotation; }
        
        public String toString() {
            return "MotorState{port=" + port + ", speed=" + speed + ", rotation=" + rotation + 
                   ", moving=" + moving + ", direction=" + direction + ", power=" + powerSetting + "}";
        }
        
        /**
         * Compare with expected state
         */
        public boolean matches(MotorState expected, double rotationTolerance) {
            if (!port.equals(expected.port)) return false;
            if (speed != expected.speed) return false;
            if (moving != expected.moving) return false;
            if (!direction.equals(expected.direction)) return false;
            if (powerSetting != expected.powerSetting) return false;
            
            // Check rotation within tolerance
            double rotationDiff = Math.abs(rotation - expected.rotation);
            return rotationDiff <= rotationTolerance;
        }
    }
    
    /**
     * Motor operation record
     */
    public static class MotorOperation {
        private String operation;
        private String details;
        private long timestamp;
        private MotorState stateBefore;
        
        public MotorOperation(String operation, String details, long timestamp, MotorState state) {
            this.operation = operation;
            this.details = details;
            this.timestamp = timestamp;
            this.stateBefore = state;
        }
        
        public String getOperation() { return operation; }
        public String getDetails() { return details; }
        public long getTimestamp() { return timestamp; }
        public MotorState getStateBefore() { return stateBefore; }
        
        public String toString() {
            return "[" + timestamp + "] " + operation + ": " + details;
        }
    }
}
