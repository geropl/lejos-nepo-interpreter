/**
 * Sensor abstraction interface
 * 
 * Abstracts sensor operations for different robot platforms and sensor types.
 */
public interface ISensor {
    
    /**
     * Get sensor type
     */
    String getType();
    
    /**
     * Check if touch sensor is pressed (for touch sensors)
     */
    boolean isPressed();
    
    /**
     * Get distance reading (for ultrasonic sensors)
     */
    double getDistance();
    
    /**
     * Get generic numeric value from sensor
     */
    double getValue();
}
