import lejos.nxt.*;
import lejos.util.Delay;

/**
 * NXT hardware implementation of IHardware interface
 * 
 * Provides concrete implementation for NXT robot hardware.
 */
public class NXTHardware implements IHardware {
    
    @Override
    public void clearDisplay() {
        LCD.clear();
    }
    
    @Override
    public void displayText(String text, int x, int y) {
        LCD.drawString(text, x, y);
    }
    
    @Override
    public void refreshDisplay() {
        LCD.refresh();
    }
    
    @Override
    public IMotor getMotor(String port) {
        NXTRegulatedMotor nxtMotor = null;
        if ("A".equals(port)) nxtMotor = Motor.A;
        else if ("B".equals(port)) nxtMotor = Motor.B;
        else if ("C".equals(port)) nxtMotor = Motor.C;
        
        return nxtMotor != null ? new NXTMotorAdapter(nxtMotor) : null;
    }
    
    @Override
    public ISensor getSensor(String port, String type) {
        SensorPort sensorPort = getSensorPort(port);
        if (sensorPort == null) return null;
        
        if ("TOUCH".equals(type)) {
            return new NXTTouchSensorAdapter(new TouchSensor(sensorPort));
        } else if ("ULTRASONIC".equals(type)) {
            return new NXTUltrasonicSensorAdapter(new UltrasonicSensor(sensorPort));
        }
        
        return null;
    }
    
    @Override
    public void playTone(int frequency, int duration) {
        Sound.playTone(frequency, duration);
    }
    
    @Override
    public void waitForButtonPress() {
        Button.waitForAnyPress();
    }
    
    @Override
    public int getButtonPress() {
        return Button.waitForAnyPress();
    }
    
    @Override
    public void delay(int milliseconds) {
        Delay.msDelay(milliseconds);
    }
    
    private SensorPort getSensorPort(String port) {
        if ("1".equals(port)) return SensorPort.S1;
        if ("2".equals(port)) return SensorPort.S2;
        if ("3".equals(port)) return SensorPort.S3;
        if ("4".equals(port)) return SensorPort.S4;
        return null;
    }
    
    /**
     * Adapter class for NXT motors
     */
    private static class NXTMotorAdapter implements IMotor {
        private final NXTRegulatedMotor motor;
        
        public NXTMotorAdapter(NXTRegulatedMotor motor) {
            this.motor = motor;
        }
        
        @Override
        public void setSpeed(int speed) {
            motor.setSpeed(speed);
        }
        
        @Override
        public void rotate(int degrees) {
            motor.rotate(degrees);
        }
        
        @Override
        public void forward() {
            motor.forward();
        }
        
        @Override
        public void backward() {
            motor.backward();
        }
        
        @Override
        public void stop() {
            motor.stop();
        }
        
        @Override
        public void stop(boolean immediateReturn) {
            motor.stop(immediateReturn);
        }
        
        @Override
        public void flt(boolean immediateReturn) {
            motor.flt(immediateReturn);
        }
        
        @Override
        public int getSpeed() {
            return motor.getSpeed();
        }
        
        @Override
        public int getTachoCount() {
            return motor.getTachoCount();
        }
    }
    
    /**
     * Adapter class for NXT touch sensors
     */
    private static class NXTTouchSensorAdapter implements ISensor {
        private final TouchSensor sensor;
        
        public NXTTouchSensorAdapter(TouchSensor sensor) {
            this.sensor = sensor;
        }
        
        @Override
        public String getType() {
            return "TOUCH";
        }
        
        @Override
        public boolean isPressed() {
            return sensor.isPressed();
        }
        
        @Override
        public double getDistance() {
            return 0; // Not applicable for touch sensor
        }
        
        @Override
        public double getValue() {
            return isPressed() ? 1.0 : 0.0;
        }
    }
    
    /**
     * Adapter class for NXT ultrasonic sensors
     */
    private static class NXTUltrasonicSensorAdapter implements ISensor {
        private final UltrasonicSensor sensor;
        
        public NXTUltrasonicSensorAdapter(UltrasonicSensor sensor) {
            this.sensor = sensor;
        }
        
        @Override
        public String getType() {
            return "ULTRASONIC";
        }
        
        @Override
        public boolean isPressed() {
            return false; // Not applicable for ultrasonic sensor
        }
        
        @Override
        public double getDistance() {
            return sensor.getDistance();
        }
        
        @Override
        public double getValue() {
            return getDistance();
        }
    }
}
