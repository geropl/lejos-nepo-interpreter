import java.util.*;

/**
 * Mock hardware implementation for testing
 * 
 * Provides a test implementation that records all hardware interactions
 * without requiring actual NXT hardware.
 */
public class MockHardware implements IHardware {
    
    private List<String> log = new ArrayList<>();
    private Map<String, MockMotor> motors = new HashMap<>();
    private Map<String, MockSensor> sensors = new HashMap<>();

    @Override
    public void clearDisplay() {
        log.add("clearDisplay()");
    }
    
    @Override
    public void displayText(String text, int x, int y) {
        log.add("displayText('" + text + "', " + x + ", " + y + ")");
    }
    
    @Override
    public void refreshDisplay() {
        log.add("refreshDisplay()");
    }

    @Override
    public IMotor getMotor(String port) {
        log.add("getMotor('" + port + "')");
        if (!motors.containsKey(port)) {
            motors.put(port, new MockMotor(port));
        }
        return motors.get(port);
    }
    
    @Override
    public ISensor getSensor(String port, String type) {
        log.add("getSensor('" + port + "', '" + type + "')");
        String key = port + "_" + type;
        if (!sensors.containsKey(key)) {
            sensors.put(key, new MockSensor(port, type));
        }
        return sensors.get(key);
    }

    @Override
    public void playTone(int frequency, int duration) {
        log.add("playTone(" + frequency + ", " + duration + ")");
    }

    @Override
    public void waitForButtonPress() {
        log.add("waitForButtonPress()");
        // Simulate immediate button press for testing
    }
    
    @Override
    public int getButtonPress() {
        log.add("getButtonPress() -> 1");
        return 1; // Simulate button press
    }
    
    @Override
    public void delay(int milliseconds) {
        log.add("delay(" + milliseconds + ")");
        // No actual delay in tests
    }

    // Test helper methods
    public List<String> getLog() { return new ArrayList<>(log); }
    
    public void clearLog() {
        log.clear();
    }

    /**
     * Mock motor implementation
     */
    private class MockMotor implements IMotor {
        private final String port;
        private int speed = 0;
        private int tachoCount = 0;
        
        public MockMotor(String port) {
            this.port = port;
        }
        
        @Override
        public void setSpeed(int speed) {
            this.speed = speed;
            log.add("Motor " + port + ".setSpeed(" + speed + ")");
        }
        
        @Override
        public void rotate(int degrees) {
            tachoCount += degrees;
            log.add("Motor " + port + ".rotate(" + degrees + ")");
        }
        
        @Override
        public void forward() {
            log.add("Motor " + port + ".forward()");
        }
        
        @Override
        public void backward() {
            log.add("Motor " + port + ".backward()");
        }
        
        @Override
        public void stop() {
            log.add("Motor " + port + ".stop()");
        }
        
        @Override
        public void stop(boolean immediateReturn) {
            log.add("Motor " + port + ".stop(" + immediateReturn + ")");
        }
        
        @Override
        public void flt(boolean immediateReturn) {
            log.add("Motor " + port + ".flt(" + immediateReturn + ")");
        }

        @Override
        public int getSpeed() {
            log.add("Motor " + port + ".getSpeed() -> " + speed);
            return speed;
        }
        
        @Override
        public int getTachoCount() {
            log.add("Motor " + port + ".getTachoCount() -> " + tachoCount);
            return tachoCount;
        }
    }
    
    /**
     * Mock sensor implementation
     */
    private class MockSensor implements ISensor {
        private final String port;
        private final String type;
        private boolean pressed = false;
        private double distance = 50.0;
        
        public MockSensor(String port, String type) {
            this.port = port;
            this.type = type;
        }
        
        @Override
        public String getType() {
            log.add("Sensor " + port + " (" + type + ").getType() -> '" + type + "'");
            return type;
        }

        @Override
        public boolean isPressed() {
            log.add("Sensor " + port + " (" + type + ").isPressed() -> " + pressed);
            return pressed;
        }
        
        @Override
        public double getDistance() {
            log.add("Sensor " + port + " (" + type + ").getDistance() -> " + distance);
            return distance;
        }

        @Override
        public double getValue() {
            double value;
            if ("TOUCH".equals(type)) {
                value = isPressed() ? 1.0 : 0.0;
            } else if ("ULTRASONIC".equals(type)) {
                value = getDistance();
            } else if ("LIGHT".equals(type)) {
                value = 75.0; // Mock light sensor value
            } else {
                value = 0.0;
            }
            log.add("Sensor " + port + " (" + type + ").getValue() -> " + value);
            return value;
        }

        // Test helper methods
        public void setPressed(boolean pressed) { this.pressed = pressed; }
        public void setDistance(double distance) { this.distance = distance; }
    }
}
