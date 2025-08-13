import java.util.*;

/**
 * Robot Configuration - Handles robBrick_ configuration blocks
 * 
 * This class manages the hardware configuration defined by robBrick_ blocks
 * in the OpenRoberta Lab XML format.
 */
public class RobotConfiguration {
    
    // Robot physical parameters
    private double wheelDiameter = 5.6; // Default wheel diameter in cm
    private double trackWidth = 12.0;   // Default track width in cm
    
    // Hardware mappings
    private Hashtable motors = new Hashtable();     // port -> MotorConfig
    private Hashtable sensors = new Hashtable();   // port -> SensorConfig
    
    /**
     * Motor configuration
     */
    public static class MotorConfig {
        public String port;
        public boolean regulation = true;
        public boolean reverse = false;
        public String driveDirection = "NONE"; // LEFT, RIGHT, NONE
        
        public MotorConfig(String port) {
            this.port = port;
        }
    }
    
    /**
     * Sensor configuration
     */
    public static class SensorConfig {
        public String port;
        public String type; // touch, ultrasonic, light, sound, gyro, color
        
        public SensorConfig(String port, String type) {
            this.port = port;
            this.type = type;
        }
    }
    
    // Getters and setters
    public double getWheelDiameter() { return wheelDiameter; }
    public void setWheelDiameter(double diameter) { this.wheelDiameter = diameter; }
    
    public double getTrackWidth() { return trackWidth; }
    public void setTrackWidth(double width) { this.trackWidth = width; }
    
    public void addMotor(String port, MotorConfig config) {
        motors.put(port, config);
    }
    
    public MotorConfig getMotor(String port) {
        return (MotorConfig) motors.get(port);
    }
    
    public void addSensor(String port, SensorConfig config) {
        sensors.put(port, config);
    }
    
    public SensorConfig getSensor(String port) {
        return (SensorConfig) sensors.get(port);
    }
    
    public Enumeration getMotorPorts() {
        return motors.keys();
    }
    
    public Enumeration getSensorPorts() {
        return sensors.keys();
    }
    
    /**
     * Check if a motor is configured on the given port
     */
    public boolean hasMotor(String port) {
        return motors.containsKey(port);
    }
    
    /**
     * Check if a sensor is configured on the given port
     */
    public boolean hasSensor(String port) {
        return sensors.containsKey(port);
    }
    
    /**
     * Get sensor type for a given port
     */
    public String getSensorType(String port) {
        SensorConfig config = getSensor(port);
        return config != null ? config.type : null;
    }
    
    /**
     * Validate the configuration
     */
    public boolean isValid() {
        // Check for port conflicts
        Enumeration motorPorts = motors.keys();
        while (motorPorts.hasMoreElements()) {
            String port = (String) motorPorts.nextElement();
            if (sensors.containsKey(port)) {
                return false; // Port conflict
            }
        }
        return true;
    }
    
    /**
     * Get configuration summary for debugging
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("RobotConfiguration:\n");
        sb.append("  Wheel Diameter: ").append(wheelDiameter).append(" cm\n");
        sb.append("  Track Width: ").append(trackWidth).append(" cm\n");
        
        sb.append("  Motors:\n");
        Enumeration motorPorts = motors.keys();
        while (motorPorts.hasMoreElements()) {
            String port = (String) motorPorts.nextElement();
            MotorConfig config = (MotorConfig) motors.get(port);
            sb.append("    Port ").append(port).append(": regulation=").append(config.regulation)
              .append(", reverse=").append(config.reverse)
              .append(", drive=").append(config.driveDirection).append("\n");
        }
        
        sb.append("  Sensors:\n");
        Enumeration sensorPorts = sensors.keys();
        while (sensorPorts.hasMoreElements()) {
            String port = (String) sensorPorts.nextElement();
            SensorConfig config = (SensorConfig) sensors.get(port);
            sb.append("    Port ").append(port).append(": type=").append(config.type).append("\n");
        }
        
        return sb.toString();
    }
}
