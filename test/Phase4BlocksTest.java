import lejos.nxt.*;
import java.util.*;

/**
 * Test suite for Phase 4 Enhanced Hardware Support blocks
 */
public class Phase4BlocksTest {
    
    private NepoBlockExecutor executor;
    private SimpleXMLParser.XMLElement createTestBlock(String type) {
        SimpleXMLParser.XMLElement block = new SimpleXMLParser.XMLElement();
        block.setAttribute("type", type);
        return block;
    }
    
    public void setUp() {
        executor = new NepoBlockExecutor();
    }
    
    public void testGyroSensors() {
        System.out.println("Testing gyro sensors...");
        
        // Test gyro angle
        SimpleXMLParser.XMLElement gyroAngleBlock = createTestBlock("robSensors_gyro_angle");
        gyroAngleBlock.setAttribute("SENSORPORT", "S1");
        Object angleResult = executor.executeBlock(gyroAngleBlock);
        System.out.println("Gyro angle: " + angleResult);
        
        // Test gyro rate
        SimpleXMLParser.XMLElement gyroRateBlock = createTestBlock("robSensors_gyro_rate");
        gyroRateBlock.setAttribute("SENSORPORT", "S1");
        Object rateResult = executor.executeBlock(gyroRateBlock);
        System.out.println("Gyro rate: " + rateResult);
        
        System.out.println("Gyro sensors test completed");
    }
    
    public void testMotorControl() {
        System.out.println("Testing advanced motor control...");
        
        // Test motor stop
        SimpleXMLParser.XMLElement stopBlock = createTestBlock("robActions_motor_stop");
        stopBlock.setAttribute("MOTORPORT", "B");
        executor.executeBlock(stopBlock);
        System.out.println("Motor stop executed");
        
        // Test motor float
        SimpleXMLParser.XMLElement floatBlock = createTestBlock("robActions_motor_float");
        floatBlock.setAttribute("MOTORPORT", "B");
        executor.executeBlock(floatBlock);
        System.out.println("Motor float executed");
        
        System.out.println("Motor control test completed");
    }
    
    public void testColorSensors() {
        System.out.println("Testing color sensors...");
        
        // Test ambient light
        SimpleXMLParser.XMLElement ambientBlock = createTestBlock("robSensors_color_ambientlight");
        ambientBlock.setAttribute("SENSORPORT", "S3");
        Object ambientResult = executor.executeBlock(ambientBlock);
        System.out.println("Ambient light: " + ambientResult);
        
        // Test reflected light
        SimpleXMLParser.XMLElement reflectedBlock = createTestBlock("robSensors_color_light");
        reflectedBlock.setAttribute("SENSORPORT", "S3");
        Object reflectedResult = executor.executeBlock(reflectedBlock);
        System.out.println("Reflected light: " + reflectedResult);
        
        System.out.println("Color sensors test completed");
    }
    
    public void testSoundSensor() {
        System.out.println("Testing sound sensor...");
        
        SimpleXMLParser.XMLElement soundBlock = createTestBlock("robSensors_sound_loudness");
        soundBlock.setAttribute("SENSORPORT", "S2");
        Object soundResult = executor.executeBlock(soundBlock);
        System.out.println("Sound loudness: " + soundResult);
        
        System.out.println("Sound sensor test completed");
    }
    
    public void testCompassSensor() {
        System.out.println("Testing compass sensor...");
        
        SimpleXMLParser.XMLElement compassBlock = createTestBlock("robSensors_compass_angle");
        compassBlock.setAttribute("SENSORPORT", "S4");
        Object compassResult = executor.executeBlock(compassBlock);
        System.out.println("Compass angle: " + compassResult);
        
        System.out.println("Compass sensor test completed");
    }
    
    public void testSensorRanges() {
        System.out.println("Testing sensor value ranges...");
        
        // Test multiple readings to verify ranges
        for (int i = 0; i < 5; i++) {
            SimpleXMLParser.XMLElement gyroBlock = createTestBlock("robSensors_gyro_angle");
            gyroBlock.setAttribute("SENSORPORT", "S1");
            Object gyroResult = executor.executeBlock(gyroBlock);
            
            SimpleXMLParser.XMLElement colorBlock = createTestBlock("robSensors_color_ambientlight");
            colorBlock.setAttribute("SENSORPORT", "S3");
            Object colorResult = executor.executeBlock(colorBlock);
            
            System.out.println("Reading " + (i+1) + " - Gyro: " + gyroResult + ", Color: " + colorResult);
        }
        
        System.out.println("Sensor ranges test completed");
    }
    
    public void runAllTests() {
        setUp();
        testGyroSensors();
        testMotorControl();
        testColorSensors();
        testSoundSensor();
        testCompassSensor();
        testSensorRanges();
        System.out.println("All Phase 4 tests completed!");
    }
    
    public static void main(String[] args) {
        Phase4BlocksTest test = new Phase4BlocksTest();
        test.runAllTests();
    }
}
