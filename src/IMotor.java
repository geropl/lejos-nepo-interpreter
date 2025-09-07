/**
 * Motor abstraction interface
 * 
 * Abstracts motor control operations for different robot platforms.
 */
public interface IMotor {
    
    /**
     * Set motor speed in degrees per second
     */
    void setSpeed(int speed);
    
    /**
     * Rotate motor by specified degrees
     */
    void rotate(int degrees);
    
    /**
     * Start motor forward
     */
    void forward();
    
    /**
     * Start motor backward
     */
    void backward();
    
    /**
     * Stop motor with brake
     */
    void stop();
    
    /**
     * Stop motor immediately with brake
     */
    void stop(boolean immediateReturn);
    
    /**
     * Float motor (coast to stop)
     */
    void flt(boolean immediateReturn);
    
    /**
     * Get current motor speed
     */
    int getSpeed();
    
    /**
     * Get motor encoder count (tacho count)
     */
    int getTachoCount();
}
