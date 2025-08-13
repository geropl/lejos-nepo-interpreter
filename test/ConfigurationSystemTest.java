import java.util.*;

/**
 * Test suite for robBrick_ configuration blocks
 */
public class ConfigurationSystemTest {
    
    private ConfigurationBlockExecutor configExecutor;
    private NepoBlockExecutor executor;
    
    public void setUp() {
        configExecutor = new ConfigurationBlockExecutor();
        executor = new NepoBlockExecutor();
    }
    
    public void testDefaultConfiguration() {
        System.out.println("Testing default configuration...");
        
        RobotConfiguration config = configExecutor.createDefaultConfiguration();
        
        // Check default values
        if (config.getWheelDiameter() != 5.6) {
            System.out.println("ERROR: Wrong default wheel diameter");
            return;
        }
        
        if (config.getTrackWidth() != 12.0) {
            System.out.println("ERROR: Wrong default track width");
            return;
        }
        
        // Check default motors
        if (!config.hasMotor("B") || !config.hasMotor("C")) {
            System.out.println("ERROR: Default motors not configured");
            return;
        }
        
        // Check default sensors
        if (!config.hasSensor("1") || !config.hasSensor("4")) {
            System.out.println("ERROR: Default sensors not configured");
            return;
        }
        
        System.out.println("Default configuration test passed");
    }
    
    public void testMainBrickConfiguration() {
        System.out.println("Testing main brick configuration...");
        
        // Create test XML for robBrick_EV3-Brick
        SimpleXMLParser.XMLElement brickBlock = new SimpleXMLParser.XMLElement("block");
        brickBlock.setAttribute("type", "robBrick_EV3-Brick");
        
        // Add wheel diameter field
        SimpleXMLParser.XMLElement wheelField = new SimpleXMLParser.XMLElement("field");
        wheelField.setAttribute("name", "WHEEL_DIAMETER");
        wheelField.textContent = "6.2";
        brickBlock.addChild(wheelField);
        
        // Add track width field
        SimpleXMLParser.XMLElement trackField = new SimpleXMLParser.XMLElement("field");
        trackField.setAttribute("name", "TRACK_WIDTH");
        trackField.textContent = "15.5";
        brickBlock.addChild(trackField);
        
        RobotConfiguration config = configExecutor.parseConfiguration(brickBlock);
        
        // Verify parsed values
        if (Math.abs(config.getWheelDiameter() - 6.2) > 0.01) {
            System.out.println("ERROR: Wheel diameter not parsed correctly");
            return;
        }
        
        if (Math.abs(config.getTrackWidth() - 15.5) > 0.01) {
            System.out.println("ERROR: Track width not parsed correctly");
            return;
        }
        
        System.out.println("Main brick configuration test passed");
    }
    
    public void testMotorConfiguration() {
        System.out.println("Testing motor configuration...");
        
        // Create test XML for robBrick_motor_big
        SimpleXMLParser.XMLElement motorBlock = new SimpleXMLParser.XMLElement("block");
        motorBlock.setAttribute("type", "robBrick_motor_big");
        
        // Add motor port field
        SimpleXMLParser.XMLElement portField = new SimpleXMLParser.XMLElement("field");
        portField.setAttribute("name", "MOTORPORT");
        portField.textContent = "A";
        motorBlock.addChild(portField);
        
        // Add regulation field
        SimpleXMLParser.XMLElement regField = new SimpleXMLParser.XMLElement("field");
        regField.setAttribute("name", "MOTOR_REGULATION");
        regField.textContent = "FALSE";
        motorBlock.addChild(regField);
        
        // Add reverse field
        SimpleXMLParser.XMLElement revField = new SimpleXMLParser.XMLElement("field");
        revField.setAttribute("name", "MOTOR_REVERSE");
        revField.textContent = "ON";
        motorBlock.addChild(revField);
        
        // Add drive direction field
        SimpleXMLParser.XMLElement driveField = new SimpleXMLParser.XMLElement("field");
        driveField.setAttribute("name", "MOTOR_DRIVE");
        driveField.textContent = "LEFT";
        motorBlock.addChild(driveField);
        
        RobotConfiguration config = configExecutor.parseConfiguration(motorBlock);
        
        // Verify motor configuration
        RobotConfiguration.MotorConfig motorConfig = config.getMotor("A");
        if (motorConfig == null) {
            System.out.println("ERROR: Motor A not configured");
            return;
        }
        
        if (motorConfig.regulation != false) {
            System.out.println("ERROR: Motor regulation not set correctly");
            return;
        }
        
        if (motorConfig.reverse != true) {
            System.out.println("ERROR: Motor reverse not set correctly");
            return;
        }
        
        if (!"LEFT".equals(motorConfig.driveDirection)) {
            System.out.println("ERROR: Motor drive direction not set correctly");
            return;
        }
        
        System.out.println("Motor configuration test passed");
    }
    
    public void testSensorConfiguration() {
        System.out.println("Testing sensor configuration...");
        
        // Test touch sensor
        SimpleXMLParser.XMLElement touchBlock = new SimpleXMLParser.XMLElement("block");
        touchBlock.setAttribute("type", "robBrick_touch");
        
        SimpleXMLParser.XMLElement touchPortField = new SimpleXMLParser.XMLElement("field");
        touchPortField.setAttribute("name", "SENSORPORT");
        touchPortField.textContent = "2";
        touchBlock.addChild(touchPortField);
        
        RobotConfiguration config = configExecutor.parseConfiguration(touchBlock);
        
        // Verify touch sensor configuration
        if (!config.hasSensor("2")) {
            System.out.println("ERROR: Touch sensor not configured on port 2");
            return;
        }
        
        if (!"touch".equals(config.getSensorType("2"))) {
            System.out.println("ERROR: Wrong sensor type for touch sensor");
            return;
        }
        
        // Test ultrasonic sensor
        SimpleXMLParser.XMLElement ultrasonicBlock = new SimpleXMLParser.XMLElement("block");
        ultrasonicBlock.setAttribute("type", "robBrick_ultrasonic");
        
        SimpleXMLParser.XMLElement ultrasonicPortField = new SimpleXMLParser.XMLElement("field");
        ultrasonicPortField.setAttribute("name", "SENSORPORT");
        ultrasonicPortField.textContent = "3";
        ultrasonicBlock.addChild(ultrasonicPortField);
        
        config = configExecutor.parseConfiguration(ultrasonicBlock);
        
        // Verify ultrasonic sensor configuration
        if (!config.hasSensor("3")) {
            System.out.println("ERROR: Ultrasonic sensor not configured on port 3");
            return;
        }
        
        if (!"ultrasonic".equals(config.getSensorType("3"))) {
            System.out.println("ERROR: Wrong sensor type for ultrasonic sensor");
            return;
        }
        
        System.out.println("Sensor configuration test passed");
    }
    
    public void testConfigurationAwareExecution() {
        System.out.println("Testing configuration-aware execution...");
        
        // Create a configuration with motor on port A
        RobotConfiguration config = new RobotConfiguration();
        RobotConfiguration.MotorConfig motorA = new RobotConfiguration.MotorConfig("A");
        config.addMotor("A", motorA);
        
        // Set configuration in executor
        executor.setConfiguration(null); // This would set the config
        
        // Test that motor methods respect configuration
        // (This would require more complex testing with actual hardware simulation)
        
        System.out.println("Configuration-aware execution test completed");
    }
    
    public void testComplexConfiguration() {
        System.out.println("Testing complex configuration with multiple components...");
        
        // Create a complex configuration XML structure
        SimpleXMLParser.XMLElement mainBlock = new SimpleXMLParser.XMLElement("block");
        mainBlock.setAttribute("type", "robBrick_EV3-Brick");
        
        // Add physical parameters
        SimpleXMLParser.XMLElement wheelField = new SimpleXMLParser.XMLElement("field");
        wheelField.setAttribute("name", "WHEEL_DIAMETER");
        wheelField.textContent = "5.6";
        mainBlock.addChild(wheelField);
        
        SimpleXMLParser.XMLElement trackField = new SimpleXMLParser.XMLElement("field");
        trackField.setAttribute("name", "TRACK_WIDTH");
        trackField.textContent = "12";
        mainBlock.addChild(trackField);
        
        // Add statement with motors and sensors
        SimpleXMLParser.XMLElement statement = new SimpleXMLParser.XMLElement("statement");
        statement.setAttribute("name", "ST");
        
        // Add motor B
        SimpleXMLParser.XMLElement motorB = new SimpleXMLParser.XMLElement("block");
        motorB.setAttribute("type", "robBrick_motor_big");
        
        SimpleXMLParser.XMLElement motorBPort = new SimpleXMLParser.XMLElement("field");
        motorBPort.setAttribute("name", "MOTORPORT");
        motorBPort.textContent = "B";
        motorB.addChild(motorBPort);
        
        SimpleXMLParser.XMLElement motorBReg = new SimpleXMLParser.XMLElement("field");
        motorBReg.setAttribute("name", "MOTOR_REGULATION");
        motorBReg.textContent = "TRUE";
        motorB.addChild(motorBReg);
        
        SimpleXMLParser.XMLElement motorBDrive = new SimpleXMLParser.XMLElement("field");
        motorBDrive.setAttribute("name", "MOTOR_DRIVE");
        motorBDrive.textContent = "RIGHT";
        motorB.addChild(motorBDrive);
        
        statement.addChild(motorB);
        
        // Add touch sensor
        SimpleXMLParser.XMLElement touchSensor = new SimpleXMLParser.XMLElement("block");
        touchSensor.setAttribute("type", "robBrick_touch");
        
        SimpleXMLParser.XMLElement touchPort = new SimpleXMLParser.XMLElement("field");
        touchPort.setAttribute("name", "SENSORPORT");
        touchPort.textContent = "1";
        touchSensor.addChild(touchPort);
        
        statement.addChild(touchSensor);
        mainBlock.addChild(statement);
        
        // Parse the complex configuration
        RobotConfiguration config = configExecutor.parseConfiguration(mainBlock);
        
        // Verify all components
        if (Math.abs(config.getWheelDiameter() - 5.6) > 0.01) {
            System.out.println("ERROR: Complex config wheel diameter wrong");
            return;
        }
        
        if (!config.hasMotor("B")) {
            System.out.println("ERROR: Complex config motor B missing");
            return;
        }
        
        if (!config.hasSensor("1")) {
            System.out.println("ERROR: Complex config touch sensor missing");
            return;
        }
        
        RobotConfiguration.MotorConfig motorConfig = config.getMotor("B");
        if (!"RIGHT".equals(motorConfig.driveDirection)) {
            System.out.println("ERROR: Complex config motor drive direction wrong");
            return;
        }
        
        System.out.println("Complex configuration test passed");
    }
    
    public void runAllTests() {
        setUp();
        testDefaultConfiguration();
        testMainBrickConfiguration();
        testMotorConfiguration();
        testSensorConfiguration();
        testConfigurationAwareExecution();
        testComplexConfiguration();
        System.out.println("All configuration system tests completed!");
    }
    
    public static void main(String[] args) {
        ConfigurationSystemTest test = new ConfigurationSystemTest();
        test.runAllTests();
    }
}
