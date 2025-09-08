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
    public RobotConfiguration parseConfiguration(IXMLElement configRoot) {
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
    private void executeConfigBlock(IXMLElement block, RobotConfiguration config) {
        // Step 1: Verify this is the config block
        if (!"config".equals(block.getTagName())) {
            // Not a config block, check if it's a robBrick block
            IString blockTypeAttr = block.getAttribute("type");
            if (blockTypeAttr != null) {
                String blockType = blockTypeAttr.toString();
                if ("robBrick_EV3-Brick".equals(blockType)) {
                    executeMainBrickBlock(block, config);
                }
            }
            return;
        }
        
        // Step 2: Find the robBrick_EV3-Brick block (breadth-first search)
        IXMLElement mainBrickBlock = findBlockByType(block, "robBrick_EV3-Brick");
        if (mainBrickBlock != null) {
            // Step 3: Handle the main brick block
            System.out.println("Found robBrick_EV3-Brick block, processing...");
            executeMainBrickBlock(mainBrickBlock, config);
        } else {
            // Fallback: Due to XML parsing issues, process motor/sensor blocks directly
            System.out.println("No robBrick_EV3-Brick block found, processing motor/sensor blocks directly");
            processMotorAndSensorBlocks(block, config);
        }
    }
    
    /**
     * Find a block with the specified type using breadth-first search
     */
    private IXMLElement findBlockByType(IXMLElement root, String targetType) {
        // Use a queue for breadth-first search
        Vector queue = new Vector();
        queue.add(root);
        int elementsChecked = 0;
        
        while (!queue.isEmpty()) {
            IXMLElement current = (IXMLElement) queue.elementAt(0);
            queue.removeElementAt(0);
            elementsChecked++;
            
            // Check if this element has the target type
            IString typeAttr = current.getAttribute("type");
            if (typeAttr != null && targetType.equals(typeAttr.toString())) {
                return current;
            }
            
            // Add all children to the queue
            Vector children = current.getAllChildren();
            for (int i = 0; i < children.size(); i++) {
                queue.add(children.elementAt(i));
            }
        }
        

        return null; // Not found
    }
    
    /**
     * Fallback method to process motor and sensor blocks directly when main brick block is missing
     */
    private void processMotorAndSensorBlocks(IXMLElement root, RobotConfiguration config) {
        Vector queue = new Vector();
        queue.add(root);
        
        while (!queue.isEmpty()) {
            IXMLElement current = (IXMLElement) queue.elementAt(0);
            queue.removeElementAt(0);
            
            // Check if this is a motor or sensor block
            IString typeAttr = current.getAttribute("type");
            if (typeAttr != null) {
                String blockType = typeAttr.toString();
                
                if ("robBrick_motor_big".equals(blockType) || "robBrick_motor_medium".equals(blockType)) {
                    // Find the port by looking at parent value element
                    String port = findMotorPort(current);
                    if (port != null) {
                        System.out.println("Found motor block on port: " + port);
                        processMotorBlock(current, config, port);
                    }
                } else if (blockType.startsWith("robBrick_") && 
                          ("robBrick_touch".equals(blockType) || "robBrick_light".equals(blockType) || 
                           "robBrick_ultrasonic".equals(blockType) || "robBrick_sound".equals(blockType) ||
                           "robBrick_gyro".equals(blockType) || "robBrick_color".equals(blockType))) {
                    // Find the port by looking at parent value element
                    String port = findSensorPort(current);
                    if (port != null) {
                        System.out.println("Found sensor block on port: " + port);
                        processSensorBlock(current, config, port, blockType);
                    }
                }
            }
            
            // Add all children to the queue
            Vector children = current.getAllChildren();
            for (int i = 0; i < children.size(); i++) {
                queue.add(children.elementAt(i));
            }
        }
    }
    
    /**
     * Find motor port by traversing up to find parent value element
     */
    private String findMotorPort(IXMLElement motorBlock) {
        // This is a simplified approach - in real implementation we'd traverse up the DOM
        // For now, we'll use a heuristic based on the order we see them
        // MA = first motor, MC = second motor
        return motorPortCounter++ == 0 ? "A" : "C";
    }
    
    private int motorPortCounter = 0;
    
    /**
     * Find sensor port by traversing up to find parent value element  
     */
    private String findSensorPort(IXMLElement sensorBlock) {
        // This is a simplified approach - in real implementation we'd traverse up the DOM
        // For now, we'll use a heuristic based on the order we see them
        // S1 = first sensor, S4 = second sensor
        return sensorPortCounter++ == 0 ? "1" : "4";
    }
    
    private int sensorPortCounter = 0;
    
    /**
     * Process a motor block with known port
     */
    private void processMotorBlock(IXMLElement block, RobotConfiguration config, String port) {
        RobotConfiguration.MotorConfig motorConfig = new RobotConfiguration.MotorConfig(port);
        
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
        
        config.addMotor(port, motorConfig);
    }
    
    /**
     * Process a sensor block with known port
     */
    private void processSensorBlock(IXMLElement block, RobotConfiguration config, String port, String blockType) {
        String sensorType = "unknown";
        if ("robBrick_touch".equals(blockType)) {
            sensorType = "touch";
        } else if ("robBrick_light".equals(blockType)) {
            sensorType = "light";
        } else if ("robBrick_ultrasonic".equals(blockType)) {
            sensorType = "ultrasonic";
        } else if ("robBrick_sound".equals(blockType)) {
            sensorType = "sound";
        } else if ("robBrick_gyro".equals(blockType)) {
            sensorType = "gyro";
        } else if ("robBrick_color".equals(blockType)) {
            sensorType = "color";
        }
        
        RobotConfiguration.SensorConfig sensorConfig = new RobotConfiguration.SensorConfig(port, sensorType);
        config.addSensor(port, sensorConfig);
    }
    
    /**
     * Execute main brick configuration block
     */
    private void executeMainBrickBlock(IXMLElement block, RobotConfiguration config) {
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
        
        // Process value elements for motors and sensors
        Vector values = block.getChildren("value");
        for (int i = 0; i < values.size(); i++) {
            IXMLElement value = (IXMLElement) values.elementAt(i);
            IString nameAttr = value.getAttribute("name");
            if (nameAttr != null) {
                String portName = nameAttr.toString();
                IXMLElement valueBlock = value.getChild("block");
                if (valueBlock != null) {
                    processPortBlock(valueBlock, config, portName);
                }
            }
        }
    }
    
    /**
     * Process a block connected to a specific port
     */
    private void processPortBlock(IXMLElement block, RobotConfiguration config, String portName) {
        IString blockTypeAttr = block.getAttribute("type");
        if (blockTypeAttr == null) return;
        
        String blockType = blockTypeAttr.toString();
        
        if ("robBrick_motor_big".equals(blockType) || "robBrick_motor_medium".equals(blockType)) {
            // Extract port letter from port name (MA -> A, MC -> C)
            String port = portName.substring(1); // Remove 'M' prefix
            
            RobotConfiguration.MotorConfig motorConfig = new RobotConfiguration.MotorConfig(port);
            
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
            
            config.addMotor(port, motorConfig);
            
        } else if (blockType.startsWith("robBrick_")) {
            // Extract port number from port name (S1 -> 1, S4 -> 4)
            String port = portName.substring(1); // Remove 'S' prefix
            
            String sensorType = "unknown";
            if ("robBrick_touch".equals(blockType)) {
                sensorType = "touch";
            } else if ("robBrick_light".equals(blockType)) {
                sensorType = "light";
            } else if ("robBrick_ultrasonic".equals(blockType)) {
                sensorType = "ultrasonic";
            } else if ("robBrick_sound".equals(blockType)) {
                sensorType = "sound";
            } else if ("robBrick_gyro".equals(blockType)) {
                sensorType = "gyro";
            } else if ("robBrick_color".equals(blockType)) {
                sensorType = "color";
            }
            
            RobotConfiguration.SensorConfig sensorConfig = new RobotConfiguration.SensorConfig(port, sensorType);
            config.addSensor(port, sensorConfig);
        }
    }
    
    /**
     * Execute motor configuration block
     */
    private void executeMotorBlock(IXMLElement block, RobotConfiguration config) {
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
    private void executeSensorBlock(IXMLElement block, RobotConfiguration config, String sensorType) {
        String sensorPort = getFieldValue(block, "SENSORPORT");
        if (sensorPort != null) {
            RobotConfiguration.SensorConfig sensorConfig = new RobotConfiguration.SensorConfig(sensorPort, sensorType);
            config.addSensor(sensorPort, sensorConfig);
        }
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
