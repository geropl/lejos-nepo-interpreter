/**
 * Hardware abstraction interface for NEPO Block Executor
 * 
 * This interface abstracts all hardware interactions to enable testing
 * and support for different robot platforms.
 */
public interface IHardware {
    
    /**
     * Display operations
     */
    void clearDisplay();
    void displayText(String text, int x, int y);
    void refreshDisplay();
    
    /**
     * Motor operations
     */
    IMotor getMotor(String port);
    
    /**
     * Sensor operations
     */
    ISensor getSensor(String port, String type);
    
    /**
     * Sound operations
     */
    void playTone(int frequency, int duration);
    
    /**
     * Input operations
     */
    void waitForButtonPress();
    int getButtonPress();
    
    /**
     * Timing operations
     */
    void delay(int milliseconds);
}
