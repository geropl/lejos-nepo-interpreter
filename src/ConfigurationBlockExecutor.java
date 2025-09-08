import java.util.*;

/**
 * Configuration Block Executor - Handles robBrick_ blocks
 * 
 * This class processes robBrick_ configuration blocks to build
 * the robot hardware configuration.
 */
public class ConfigurationBlockExecutor {
    
    /**
     * Parse configuration from XML config element and build RobotConfiguration.
     * 
     * @param configRoot Must be a config element containing block_set/instance/robBrick_EV3-Brick
     * @return Complete robot configuration
     * @throws IllegalArgumentException if configRoot is not a config element
     * @throws ConfigurationException if required structure is missing
     */
    public RobotConfiguration parseConfiguration(IXMLElement configRoot) throws ConfigurationException {
        if (configRoot == null) {
            return createDefaultConfiguration();
        }
        
        // Validate input is config element
        if (!"config".equals(configRoot.getTagName())) {
            throw new IllegalArgumentException("Expected config element, got: " + configRoot.getTagName());
        }
        
        // Navigate to main brick block: config → robBrick_EV3-Brick
        IXMLElement brickBlock = configRoot.findElement("robBrick_EV3-Brick");
        if (brickBlock == null) {
            throw new ConfigurationException("No robBrick_EV3-Brick block found in config section");
        }
        
        // Parse the main brick block
        return parseMainBrickBlock(brickBlock);
    }
    

    

    

    

    

    
    /**
     * Parse the main robBrick_EV3-Brick block for wheel/track settings and ports.
     * 
     * @param brickBlock The robBrick_EV3-Brick block element
     * @return Complete robot configuration
     */
    private RobotConfiguration parseMainBrickBlock(IXMLElement brickBlock) {
        RobotConfiguration config = new RobotConfiguration();
        
        // Parse wheel diameter
        String wheelDiameter = getFieldValue(brickBlock, "WHEEL_DIAMETER");
        if (wheelDiameter != null) {
            try {
                config.setWheelDiameter(Double.parseDouble(wheelDiameter));
            } catch (NumberFormatException e) {
                // Use default value - already set in RobotConfiguration
            }
        }
        
        // Parse track width
        String trackWidth = getFieldValue(brickBlock, "TRACK_WIDTH");
        if (trackWidth != null) {
            try {
                config.setTrackWidth(Double.parseDouble(trackWidth));
            } catch (NumberFormatException e) {
                // Use default value - already set in RobotConfiguration
            }
        }
        
        // Process value elements for motors and sensors
        Vector<IXMLElement> values = brickBlock.getChildren("value");
        for (int i = 0; i < values.size(); i++) {
            IXMLElement value = values.elementAt(i);
            parsePortConfiguration(value, config);
        }
        
        return config;
    }
    
    /**
     * Parse a port configuration (motor or sensor) from a value element.
     * 
     * @param valueElement The value element (e.g., name="MA", name="S1")
     * @param config Configuration to add the port to
     */
    private void parsePortConfiguration(IXMLElement valueElement, RobotConfiguration config) {
        IString nameAttr = valueElement.getAttribute("name");
        if (nameAttr == null) return;
        
        String portName = nameAttr.toString();
        String portId = extractPortId(portName);
        
        IXMLElement block = valueElement.getChild("block");
        if (block == null) return;
        
        IString blockTypeAttr = block.getAttribute("type");
        if (blockTypeAttr == null) return;
        
        String blockType = blockTypeAttr.toString();
        
        if ("robBrick_motor_big".equals(blockType) || "robBrick_motor_medium".equals(blockType)) {
            RobotConfiguration.MotorConfig motorConfig = parseMotorBlock(block, portId);
            config.addMotor(portId, motorConfig);
        } else if (blockType.startsWith("robBrick_")) {
            String sensorType = extractSensorType(blockType);
            RobotConfiguration.SensorConfig sensorConfig = parseSensorBlock(block, portId, sensorType);
            config.addSensor(portId, sensorConfig);
        }
    }
    
    /**
     * Parse motor configuration from a motor block.
     * 
     * @param motorBlock The motor block element
     * @param port The port identifier (A, C, etc.)
     * @return Motor configuration
     */
    private RobotConfiguration.MotorConfig parseMotorBlock(IXMLElement motorBlock, String port) {
        RobotConfiguration.MotorConfig motorConfig = new RobotConfiguration.MotorConfig(port);
        
        // Parse motor regulation
        String regulation = getFieldValue(motorBlock, "MOTOR_REGULATION");
        if ("FALSE".equals(regulation)) {
            motorConfig.regulation = false;
        }
        
        // Parse motor reverse
        String reverse = getFieldValue(motorBlock, "MOTOR_REVERSE");
        if ("ON".equals(reverse)) {
            motorConfig.reverse = true;
        }
        
        // Parse drive direction
        String driveDirection = getFieldValue(motorBlock, "MOTOR_DRIVE");
        if (driveDirection != null) {
            motorConfig.driveDirection = driveDirection;
        }
        
        return motorConfig;
    }
    
    /**
     * Parse sensor configuration from a sensor block.
     * 
     * @param sensorBlock The sensor block element
     * @param port The port identifier (1, 4, etc.)
     * @param sensorType The sensor type (touch, light, etc.)
     * @return Sensor configuration
     */
    private RobotConfiguration.SensorConfig parseSensorBlock(IXMLElement sensorBlock, String port, String sensorType) {
        return new RobotConfiguration.SensorConfig(port, sensorType);
    }
    
    /**
     * Extract sensor type from robBrick block type.
     * 
     * @param blockType The block type (e.g., "robBrick_touch")
     * @return Sensor type (e.g., "touch")
     */
    private String extractSensorType(String blockType) {
        if ("robBrick_touch".equals(blockType)) {
            return "touch";
        } else if ("robBrick_light".equals(blockType)) {
            return "light";
        } else if ("robBrick_ultrasonic".equals(blockType)) {
            return "ultrasonic";
        } else if ("robBrick_sound".equals(blockType)) {
            return "sound";
        } else if ("robBrick_gyro".equals(blockType)) {
            return "gyro";
        } else if ("robBrick_color".equals(blockType)) {
            return "color";
        }
        return "unknown";
    }
    
    /**
     * Extract port identifier from value element name.
     * MA → A, MC → C, S1 → 1, S4 → 4
     * 
     * @param valueName The value element name attribute
     * @return Port identifier
     */
    private String extractPortId(String valueName) {
        if (valueName != null && valueName.length() > 1) {
            return valueName.substring(1); // Remove prefix (M or S)
        }
        return valueName;
    }
    

    
    /**
     * Get field value from a block
     */
    private String getFieldValue(IXMLElement block, String fieldName) {
        Vector<IXMLElement> fields = block.getChildren("field");
        for (int i = 0; i < fields.size(); i++) {
            IXMLElement field = fields.elementAt(i);
            IString nameAttr = field.getAttribute("name");
            if (nameAttr != null && fieldName.equals(nameAttr.toString())) {
                return field.getTextContent();
            }
        }
        return null;
    }

    /**
     * Create default configuration for NXT
     */
    public RobotConfiguration createDefaultConfiguration() {
        RobotConfiguration config = new RobotConfiguration();
        
        // Default NXT setup
        config.setWheelDiameter(5.6);
        config.setTrackWidth(12.0);
        
        // Default motors
        RobotConfiguration.MotorConfig motorB = new RobotConfiguration.MotorConfig("B");
        motorB.regulation = true;
        motorB.reverse = false;
        motorB.driveDirection = "RIGHT";
        config.addMotor("B", motorB);
        
        RobotConfiguration.MotorConfig motorC = new RobotConfiguration.MotorConfig("C");
        motorC.regulation = true;
        motorC.reverse = false;
        motorC.driveDirection = "LEFT";
        config.addMotor("C", motorC);
        
        // Default sensors
        config.addSensor("1", new RobotConfiguration.SensorConfig("1", "touch"));
        config.addSensor("4", new RobotConfiguration.SensorConfig("4", "ultrasonic"));
        
        return config;
    }
}
