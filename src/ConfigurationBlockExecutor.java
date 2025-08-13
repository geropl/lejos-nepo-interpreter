import java.util.*;

/**
 * Configuration Block Executor - Handles robBrick_ blocks
 * 
 * This class processes robBrick_ configuration blocks to build
 * the robot hardware configuration.
 */
public class ConfigurationBlockExecutor {
    
    /**
     * Parse configuration from XML and build RobotConfiguration
     */
    public RobotConfiguration parseConfiguration(SimpleXMLParser.XMLElement configRoot) {
        RobotConfiguration config = new RobotConfiguration();
        
        if (configRoot == null) {
            // Return default configuration if no config section
            return createDefaultConfiguration();
        }
        
        // Process the root configuration block
        executeConfigBlock(configRoot, config);
        
        return config;
    }
    
    /**
     * Execute a configuration block
     */
    private void executeConfigBlock(SimpleXMLParser.XMLElement block, RobotConfiguration config) {
        String blockType = block.getAttribute("type");
        if (blockType == null) return;
        
        if ("robBrick_EV3-Brick".equals(blockType)) {
            executeMainBrickBlock(block, config);
        } else if ("robBrick_motor_big".equals(blockType)) {
            executeMotorBlock(block, config);
        } else if ("robBrick_motor_medium".equals(blockType)) {
            executeMotorBlock(block, config); // Same as big motor for NXT
        } else if ("robBrick_touch".equals(blockType)) {
            executeSensorBlock(block, config, "touch");
        } else if ("robBrick_ultrasonic".equals(blockType)) {
            executeSensorBlock(block, config, "ultrasonic");
        } else if ("robBrick_light".equals(blockType)) {
            executeSensorBlock(block, config, "light");
        } else if ("robBrick_sound".equals(blockType)) {
            executeSensorBlock(block, config, "sound");
        } else if ("robBrick_gyro".equals(blockType)) {
            executeSensorBlock(block, config, "gyro");
        } else if ("robBrick_color".equals(blockType)) {
            executeSensorBlock(block, config, "color");
        }
        
        // Process child blocks
        Vector children = block.children;
        for (int i = 0; i < children.size(); i++) {
            SimpleXMLParser.XMLElement child = (SimpleXMLParser.XMLElement) children.elementAt(i);
            if ("statement".equals(child.tagName) && "ST".equals(child.getAttribute("name"))) {
                // Process statement children
                Vector statementChildren = child.children;
                for (int j = 0; j < statementChildren.size(); j++) {
                    SimpleXMLParser.XMLElement statementChild = (SimpleXMLParser.XMLElement) statementChildren.elementAt(j);
                    if ("block".equals(statementChild.tagName)) {
                        executeConfigBlock(statementChild, config);
                    }
                }
            } else if ("block".equals(child.tagName)) {
                executeConfigBlock(child, config);
            }
        }
    }
    
    /**
     * Execute main brick configuration block
     */
    private void executeMainBrickBlock(SimpleXMLParser.XMLElement block, RobotConfiguration config) {
        // Parse wheel diameter
        String wheelDiameter = getFieldValue(block, "WHEEL_DIAMETER");
        if (wheelDiameter != null) {
            try {
                config.setWheelDiameter(Double.parseDouble(wheelDiameter));
            } catch (NumberFormatException e) {
                // Use default value
            }
        }
        
        // Parse track width
        String trackWidth = getFieldValue(block, "TRACK_WIDTH");
        if (trackWidth != null) {
            try {
                config.setTrackWidth(Double.parseDouble(trackWidth));
            } catch (NumberFormatException e) {
                // Use default value
            }
        }
    }
    
    /**
     * Execute motor configuration block
     */
    private void executeMotorBlock(SimpleXMLParser.XMLElement block, RobotConfiguration config) {
        String motorPort = getFieldValue(block, "MOTORPORT");
        if (motorPort != null) {
            RobotConfiguration.MotorConfig motorConfig = new RobotConfiguration.MotorConfig(motorPort);
            
            // Parse motor regulation
            String regulation = getFieldValue(block, "MOTOR_REGULATION");
            if ("FALSE".equals(regulation)) {
                motorConfig.regulation = false;
            }
            
            // Parse motor reverse
            String reverse = getFieldValue(block, "MOTOR_REVERSE");
            if ("ON".equals(reverse)) {
                motorConfig.reverse = true;
            }
            
            // Parse drive direction
            String driveDirection = getFieldValue(block, "MOTOR_DRIVE");
            if (driveDirection != null) {
                motorConfig.driveDirection = driveDirection;
            }
            
            config.addMotor(motorPort, motorConfig);
        }
    }
    
    /**
     * Execute sensor configuration block
     */
    private void executeSensorBlock(SimpleXMLParser.XMLElement block, RobotConfiguration config, String sensorType) {
        String sensorPort = getFieldValue(block, "SENSORPORT");
        if (sensorPort != null) {
            RobotConfiguration.SensorConfig sensorConfig = new RobotConfiguration.SensorConfig(sensorPort, sensorType);
            config.addSensor(sensorPort, sensorConfig);
        }
    }
    
    /**
     * Get field value from a block
     */
    private String getFieldValue(SimpleXMLParser.XMLElement block, String fieldName) {
        Vector fields = block.getChildren("field");
        for (int i = 0; i < fields.size(); i++) {
            SimpleXMLParser.XMLElement field = (SimpleXMLParser.XMLElement) fields.elementAt(i);
            if (fieldName.equals(field.getAttribute("name"))) {
                return field.textContent;
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
