import java.util.*;

/**
 * NEPO Block Executor - Enhanced version with XML parsing
 * 
 * This class handles the execution of individual NEPO blocks
 * parsed from XML programs created in Open Roberta Lab.
 */
public class NepoBlockExecutor {
    
    // Hardware references
    private static Hashtable variables = new Hashtable();
    private boolean running = true;
    private RobotConfiguration robotConfig;
    private ConfigurationBlockExecutor configExecutor;
    private IHardware hardware;

    /**
     * Constructor with hardware dependency injection
     */
    public NepoBlockExecutor(IHardware hardware) {
        this.hardware = hardware;
        configExecutor = new ConfigurationBlockExecutor();
        robotConfig = configExecutor.createDefaultConfiguration(); // Default config
    }
    
    /**
     * Default constructor for backward compatibility
     */
    public NepoBlockExecutor() {
        this(new NXTHardware());
    }

    /**
     * Set robot configuration from XML config section
     */
    public void setConfiguration(IXMLElement configElement) {
        robotConfig = configExecutor.parseConfiguration(configElement);
    }
    
    /**
     * Get current robot configuration
     */
    public RobotConfiguration getConfiguration() {
        return robotConfig;
    }
    
    /**
     * Execute a block based on its type and parameters
     */
    public void executeBlock(IXMLElement block) {
        if (!running) return;
        
        IString blockTypeAttr = block.getAttribute("type");
        if (blockTypeAttr == null) return;
        
        String blockType = blockTypeAttr.toString();
        System.out.println("Executing block: " + blockType);

        try {
            if ("robControls_start".equals(blockType)) {
                executeStartBlock(block);
            } else if ("robActions_display_text".equals(blockType)) {
                executeDisplayTextBlock(block);
            } else if ("robActions_motor_on".equals(blockType)) {
                executeMotorOnBlock(block);
            } else if ("robActions_motor_stop".equals(blockType)) {
                executeMotorStopBlock(block);
            } else if ("robControls_wait_time".equals(blockType)) {
                executeWaitTimeBlock(block);
            } else if ("robControls_if".equals(blockType)) {
                executeIfBlock(block);
            } else if ("robControls_ifElse".equals(blockType)) {
                executeIfElseBlock(block);
            } else if ("robControls_repeat_times".equals(blockType)) {
                executeRepeatTimesBlock(block);
            } else if ("robSensors_touch_isPressed".equals(blockType)) {
                // This is a sensor value block, handled in getValue
            } else if ("robSensors_ultrasonic_distance".equals(blockType)) {
                // This is a sensor value block, handled in getValue
            } else if ("robActions_play_tone".equals(blockType)) {
                executePlayToneBlock(block);
            } else if ("robControls_loopForever".equals(blockType)) {
                executeLoopForeverBlock(block);
            } else if ("robActions_motorDiff_on".equals(blockType)) {
                executeMotorDiffOnBlock(block);
            } else if ("robControls_wait".equals(blockType)) {
                executeWaitBlock(block);
            } else if ("robSensors_light_getSample".equals(blockType)) {
                // This is a sensor value block, handled in getValue
            } else if ("robSensors_touch_getSample".equals(blockType)) {
                // This is a sensor value block, handled in getValue
            } else if ("robActions_motorDiff_turn_for".equals(blockType)) {
                executeMotorDiffTurnForBlock(block);
            } else {
                hardware.displayText("Unknown: " + blockType, 0, 7);
                hardware.refreshDisplay();
                hardware.delay(1000);
            }

            // Execute next block in sequence
            IXMLElement nextBlock = getNextBlock(block);
            if (nextBlock != null) {
                System.out.println("Found next block, executing...");
                executeBlock(nextBlock);
            } else {
                System.out.println("No next block found");
            }

        } catch (Exception e) {
            hardware.clearDisplay();
            hardware.displayText("Error in block:", 0, 0);
            hardware.displayText(blockType, 0, 1);
            hardware.displayText(e.getMessage(), 0, 2);
            hardware.refreshDisplay();
            hardware.waitForButtonPress();
        }
    }
    
    /**
     * Execute start block - find and execute statement blocks
     */
    private void executeStartBlock(IXMLElement block) {
        System.out.println("Looking for statement block 'ST'");
        IXMLElement statement = getStatementBlock(block, "ST");
        if (statement != null) {
            System.out.println("Found statement block, executing...");
            executeBlock(statement);
        } else {
            System.out.println("No statement block found");
        }
    }

    /**
     * Execute display text block
     */
    private void executeDisplayTextBlock(IXMLElement block) {
        Object textValue = getValue(block, "OUT");
        if (textValue != null) {
            hardware.clearDisplay();
            hardware.displayText(textValue.toString(), 0, 0);
            hardware.refreshDisplay();
        }
    }

    /**
     * Execute motor on block
     */
    private void executeMotorOnBlock(IXMLElement block) {
        String motorPort = getFieldValue(block, "MOTORPORT");
        String rotationType = getFieldValue(block, "MOTORROTATION");
        
        Object powerValue = getValue(block, "POWER");
        Object rotationValue = getValue(block, "VALUE");
        
        if (motorPort != null && powerValue instanceof Double) {
            IMotor motor = getMotor(motorPort);
            if (motor != null) {
                double power = ((Double) powerValue).doubleValue();
                int speed = (int) (Math.abs(power) * 7.2); // Convert percentage to degrees/second
                motor.setSpeed(speed);
                
                if ("ROTATIONS".equals(rotationType) && rotationValue instanceof Double) {
                    double rotations = ((Double) rotationValue).doubleValue();
                    int degrees = (int) (rotations * 360);
                    if (power >= 0) {
                        motor.rotate(degrees);
                    } else {
                        motor.rotate(-degrees);
                    }
                } else {
                    // Continuous rotation
                    if (power >= 0) {
                        motor.forward();
                    } else {
                        motor.backward();
                    }
                }
            }
        }
    }

    /**
     * Execute motor stop block
     */
    private void executeMotorStopBlock(IXMLElement block) {
        String motorPort = getFieldValue(block, "MOTORPORT");
        if (motorPort != null) {
            IMotor motor = getMotor(motorPort);
            if (motor != null) {
                motor.stop();
            }
        }
    }

    /**
     * Execute wait time block
     */
    private void executeWaitTimeBlock(IXMLElement block) {
        Object waitValue = getValue(block, "WAIT");
        if (waitValue instanceof Double) {
            int milliseconds = ((Double) waitValue).intValue();
            hardware.delay(milliseconds);
        }
    }

    /**
     * Execute if block
     */
    private void executeIfBlock(IXMLElement block) {
        Object conditionValue = getValue(block, "IF0");
        if (conditionValue instanceof Boolean && ((Boolean) conditionValue).booleanValue()) {
            IXMLElement doBlock = getStatementBlock(block, "DO0");
            if (doBlock != null) {
                executeBlock(doBlock);
            }
        }
    }
    
    /**
     * Execute if-else block
     */
    private void executeIfElseBlock(IXMLElement block) {
        Object conditionValue = getValue(block, "IF0");
        if (conditionValue instanceof Boolean && ((Boolean) conditionValue).booleanValue()) {
            // Execute IF branch
            IXMLElement doBlock = getStatementBlock(block, "DO0");
            if (doBlock != null) {
                executeBlock(doBlock);
            }
        } else {
            // Execute ELSE branch
            IXMLElement elseBlock = getStatementBlock(block, "ELSE");
            if (elseBlock != null) {
                executeBlock(elseBlock);
            }
        }
    }

    /**
     * Execute repeat times block
     */
    private void executeRepeatTimesBlock(IXMLElement block) {
        Object timesValue = getValue(block, "TIMES");
        if (timesValue instanceof Double) {
            int times = ((Double) timesValue).intValue();
            IXMLElement doBlock = getStatementBlock(block, "DO");

            for (int i = 0; i < times && running; i++) {
                if (doBlock != null) {
                    executeBlock(doBlock);
                }
            }
        }
    }
    
    /**
     * Execute play tone block
     */
    private void executePlayToneBlock(IXMLElement block) {
        Object frequencyValue = getValue(block, "FREQUENCY");
        Object durationValue = getValue(block, "DURATION");
        
        if (frequencyValue instanceof Double && durationValue instanceof Double) {
            int frequency = ((Double) frequencyValue).intValue();
            int duration = ((Double) durationValue).intValue();
            hardware.playTone(frequency, duration);
            hardware.delay(duration);
        }
    }
    
    /**
     * Execute loop forever block
     */
    private void executeLoopForeverBlock(IXMLElement block) {
        System.out.println("Executing loop forever block");
        System.out.println("Block structure:");
        printBlockStructure(block, 0);
        
        IXMLElement doBlock = getStatementBlock(block, "DO");
        int iterations = 0;
        
        System.out.println("Do block found: " + (doBlock != null));

        while (running) {
            if (doBlock != null) {
                System.out.println("Loop iteration " + iterations);
                executeBlock(doBlock);
            }
            iterations++;
        }
        System.out.println("Loop completed after " + iterations + " iterations");
    }
    
    /**
     * Debug method to print block structure
     */
    private void printBlockStructure(IXMLElement element, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + element.getTagName());
        
        Vector<IXMLElement> children = element.getAllChildren();
        for (int i = 0; i < children.size(); i++) {
            printBlockStructure(children.elementAt(i), depth + 1);
        }
    }

    /**
     * Execute motor differential on block
     */
    private void executeMotorDiffOnBlock(IXMLElement block) {
        String direction = getFieldValue(block, "DIRECTION");
        Object powerValue = getValue(block, "POWER");
        
        if (powerValue instanceof Double) {
            double power = ((Double) powerValue).doubleValue();
            int speed = (int) (Math.abs(power) * 7.2);
            
            // Control both motors for differential drive
            IMotor motorA = getMotor("A");
            IMotor motorC = getMotor("C");
            
            if (motorA != null && motorC != null) {
                motorA.setSpeed(speed);
                motorC.setSpeed(speed);
                
                if ("FOREWARD".equals(direction)) {
                    motorA.forward();
                    motorC.forward();
                } else if ("BACKWARD".equals(direction)) {
                    motorA.backward();
                    motorC.backward();
                }
            }
        }
    }
    
    /**
     * Execute wait block (wait for condition)
     */
    private void executeWaitBlock(IXMLElement block) {
        System.out.println("Executing wait block");
        Object conditionValue = getValue(block, "WAIT0");
        System.out.println("Initial condition value: " + conditionValue);
        int maxWaitTime = 1000; // Reduced to 1 second for testing
        long startTime = System.currentTimeMillis();
        
        while (System.currentTimeMillis() - startTime < maxWaitTime && running) {
            conditionValue = getValue(block, "WAIT0");
            if (conditionValue instanceof Boolean && ((Boolean) conditionValue).booleanValue()) {
                System.out.println("Wait condition met");
                break;
            }
            try {
                Thread.sleep(50); // Check every 50ms
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("Wait block completed");
    }

    /**
     * Execute motor differential turn for block
     */
    private void executeMotorDiffTurnForBlock(IXMLElement block) {
        String direction = getFieldValue(block, "DIRECTION");
        Object powerValue = getValue(block, "POWER");
        Object degreeValue = getValue(block, "DEGREE");
        
        if (powerValue instanceof Double && degreeValue instanceof Double) {
            double power = ((Double) powerValue).doubleValue();
            double degrees = ((Double) degreeValue).doubleValue();
            int speed = (int) (Math.abs(power) * 7.2);
            
            IMotor motorA = getMotor("A");
            IMotor motorC = getMotor("C");
            
            if (motorA != null && motorC != null) {
                motorA.setSpeed(speed);
                motorC.setSpeed(speed);
                
                if ("RIGHT".equals(direction)) {
                    // Turn right: left motor forward, right motor backward
                    motorA.rotate((int) degrees);
                    motorC.rotate(-(int) degrees);
                } else if ("LEFT".equals(direction)) {
                    // Turn left: right motor forward, left motor backward
                    motorA.rotate(-(int) degrees);
                    motorC.rotate((int) degrees);
                }
            }
        }
    }

    /**
     * Get value from a value block
     */
    private Object getValue(IXMLElement parentBlock, String valueName) {
        Vector values = parentBlock.getChildren("value");
        for (int i = 0; i < values.size(); i++) {
            IXMLElement value = (IXMLElement) values.elementAt(i);
            IString nameAttr = value.getAttribute("name");
            if (nameAttr != null && valueName.equals(nameAttr.toString())) {
                IXMLElement valueBlock = value.getChild("block");
                if (valueBlock != null) {
                    return evaluateValueBlock(valueBlock);
                }
            }
        }
        return null;
    }
    
    /**
     * Evaluate a value block and return its result
     */
    private Object evaluateValueBlock(IXMLElement block) {
        IString blockTypeAttr = block.getAttribute("type");
        if (blockTypeAttr == null) return null;
        
        String blockType = blockTypeAttr.toString();

        if ("math_number".equals(blockType)) {
            String numText = getFieldValue(block, "NUM");
            if (numText != null) {
                try {
                    return new Double(Double.parseDouble(numText));
                } catch (NumberFormatException e) {
                    return new Double(0);
                }
            }
        } else if ("text".equals(blockType)) {
            String textValue = getFieldValue(block, "TEXT");
            return textValue != null ? textValue : "";
        } else if ("logic_boolean".equals(blockType)) {
            String boolText = getFieldValue(block, "BOOL");
            return new Boolean("TRUE".equals(boolText));
        } else if ("robSensors_touch_isPressed".equals(blockType)) {
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                ISensor sensor = hardware.getSensor(sensorPort, "TOUCH");
                if (sensor != null) {
                    return new Boolean(sensor.isPressed());
                }
            }
        } else if ("robSensors_ultrasonic_distance".equals(blockType)) {
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                ISensor sensor = hardware.getSensor(sensorPort, "ULTRASONIC");
                if (sensor != null) {
                    return new Double(sensor.getDistance());
                }
            }
        } else if ("robSensors_light_getSample".equals(blockType)) {
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                ISensor sensor = hardware.getSensor(sensorPort, "LIGHT");
                if (sensor != null) {
                    return new Double(sensor.getValue());
                }
            }
        } else if ("robSensors_touch_getSample".equals(blockType)) {
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                ISensor sensor = hardware.getSensor(sensorPort, "TOUCH");
                if (sensor != null) {
                    return new Boolean(sensor.isPressed());
                }
            }
        } else if ("robActions_motor_getPower".equals(blockType)) {
            String motorPort = getFieldValue(block, "MOTORPORT");
            if (motorPort != null) {
                IMotor motor = getMotor(motorPort);
                if (motor != null) {
                    // Return current motor speed as power percentage (approximate)
                    int speed = motor.getSpeed();
                    int power = speed / 7; // Rough conversion from degrees/sec to percentage
                    return new Double(power);
                }
            }
        } else if ("robSensors_timer_get".equals(blockType)) {
            // Return elapsed time since program start in milliseconds
            // For simplicity, we'll use system time
            return new Double(System.currentTimeMillis() % 1000000); // Keep it reasonable
        } else if ("robControls_while".equals(blockType)) {
            // While loop implementation
            Object condition = getValue(block, "BOOL");
            if (condition instanceof Boolean) {
                while (((Boolean) condition).booleanValue()) {
                    // Execute statements in the loop
                    IXMLElement statements = getStatementBlock(block, "DO");
                    if (statements != null) {
                        executeBlock(statements);
                    }
                    // Re-evaluate condition
                    condition = getValue(block, "BOOL");
                    if (!(condition instanceof Boolean)) break;
                }
            }
            return null;
        } else if ("robControls_repeat_forever".equals(blockType)) {
            // Infinite loop implementation (with safety limit)
            int maxIterations = 10000; // Safety limit
            int iterations = 0;
            while (iterations < maxIterations) {
                IXMLElement statements = getStatementBlock(block, "DO");
                if (statements != null) {
                    executeBlock(statements);
                }
                iterations++;
            }
            return null;
        } else if ("variables_get".equals(blockType)) {
            // Get variable value
            String varName = getFieldValue(block, "VAR");
            if (varName != null && variables.get(varName) != null) {
                return variables.get(varName);
            }
            return new Double(0); // Default value
        } else if ("variables_set".equals(blockType)) {
            // Set variable value
            String varName = getFieldValue(block, "VAR");
            Object value = getValue(block, "VALUE");
            if (varName != null && value != null) {
                variables.put(varName, value);
            }
            return null;
        } else if ("math_arithmetic".equals(blockType)) {
            // Arithmetic operations
            String operation = getFieldValue(block, "OP");
            Object aValue = getValue(block, "A");
            Object bValue = getValue(block, "B");
            
            if (aValue instanceof Double && bValue instanceof Double && operation != null) {
                double a = ((Double) aValue).doubleValue();
                double b = ((Double) bValue).doubleValue();
                
                switch (operation) {
                    case "ADD": return new Double(a + b);
                    case "MINUS": return new Double(a - b);
                    case "MULTIPLY": return new Double(a * b);
                    case "DIVIDE": return b != 0 ? new Double(a / b) : new Double(0);
                    case "POWER": return new Double(Math.pow(a, b));
                }
            }
            return new Double(0);
        } else if ("math_single".equals(blockType)) {
            // Single value math operations
            String operation = getFieldValue(block, "OP");
            Object numValue = getValue(block, "NUM");
            
            if (numValue instanceof Double && operation != null) {
                double num = ((Double) numValue).doubleValue();
                
                switch (operation) {
                    case "ROOT": return new Double(Math.sqrt(num));
                    case "ABS": return new Double(Math.abs(num));
                    case "NEG": return new Double(-num);
                    case "LN": return new Double(Math.log(num));
                    case "LOG10": return new Double(Math.log(num) / Math.log(10));
                    case "EXP": return new Double(Math.exp(num));
                    case "POW10": return new Double(Math.pow(10, num));
                    case "SIN": return new Double(Math.sin(Math.toRadians(num)));
                    case "COS": return new Double(Math.cos(Math.toRadians(num)));
                    case "TAN": return new Double(Math.tan(Math.toRadians(num)));
                    case "ASIN": return new Double(Math.toDegrees(Math.asin(num)));
                    case "ACOS": return new Double(Math.toDegrees(Math.acos(num)));
                    case "ATAN": return new Double(Math.toDegrees(Math.atan(num)));
                }
            }
            return new Double(0);
        } else if ("robSensors_gyro_angle".equals(blockType)) {
            // Gyro sensor angle reading
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                // Mock gyro angle reading (0-360 degrees)
                // In real implementation, would read from actual gyro sensor
                return new Double(Math.random() * 360);
            }
            return new Double(0);
        } else if ("robSensors_gyro_rate".equals(blockType)) {
            // Gyro sensor rate reading (degrees per second)
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                // Mock gyro rate reading (-1000 to 1000 degrees/sec)
                return new Double((Math.random() - 0.5) * 2000);
            }
            return new Double(0);
        } else if ("robActions_motor_stop".equals(blockType)) {
            // Stop motor immediately
            String motorPort = getFieldValue(block, "MOTORPORT");
            if (motorPort != null) {
                IMotor motor = getMotor(motorPort);
                if (motor != null) {
                    motor.stop(true); // Immediate stop with brake
                }
            }
            return null;
        } else if ("robActions_motor_float".equals(blockType)) {
            // Float motor (coast to stop)
            String motorPort = getFieldValue(block, "MOTORPORT");
            if (motorPort != null) {
                IMotor motor = getMotor(motorPort);
                if (motor != null) {
                    motor.flt(true); // Float/coast
                }
            }
            return null;
        } else if ("robSensors_color_ambientlight".equals(blockType)) {
            // Color sensor ambient light reading
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                // Mock ambient light reading (0-100)
                return new Double(Math.random() * 100);
            }
            return new Double(0);
        } else if ("robSensors_color_light".equals(blockType)) {
            // Color sensor reflected light reading
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                // Mock reflected light reading (0-100)
                return new Double(Math.random() * 100);
            }
            return new Double(0);
        } else if ("robSensors_sound_loudness".equals(blockType)) {
            // Sound sensor loudness reading
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                // Mock sound level reading (0-100)
                return new Double(Math.random() * 100);
            }
            return new Double(0);
        } else if ("robSensors_compass_angle".equals(blockType)) {
            // Compass sensor angle reading
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                // Mock compass angle reading (0-360 degrees)
                return new Double(Math.random() * 360);
            }
            return new Double(0);
        } else if ("text_join".equals(blockType)) {
            // String concatenation
            Object aValue = getValue(block, "A");
            Object bValue = getValue(block, "B");
            
            String aStr = (aValue != null) ? aValue.toString() : "";
            String bStr = (bValue != null) ? bValue.toString() : "";
            
            return aStr + bStr;
        } else if ("math_random_int".equals(blockType)) {
            // Random integer generation
            Object fromValue = getValue(block, "FROM");
            Object toValue = getValue(block, "TO");
            
            int from = 1; // Default range
            int to = 100;
            
            if (fromValue instanceof Double) {
                from = ((Double) fromValue).intValue();
            }
            if (toValue instanceof Double) {
                to = ((Double) toValue).intValue();
            }
            
            if (from > to) {
                int temp = from;
                from = to;
                to = temp;
            }
            
            int range = to - from + 1;
            int randomValue = from + (int)(Math.random() * range);
            return new Double(randomValue);
        } else if ("robActions_display_clear".equals(blockType)) {
            // Clear LCD display
            hardware.clearDisplay();
            hardware.refreshDisplay();
            return null;
        } else if ("robControls_waitUntil".equals(blockType)) {
            // Wait until condition becomes true
            Object condition = getValue(block, "CONDITION");
            int maxWaitTime = 30000; // 30 second safety limit
            long startTime = System.currentTimeMillis();
            
            while (System.currentTimeMillis() - startTime < maxWaitTime) {
                condition = getValue(block, "CONDITION");
                if (condition instanceof Boolean && ((Boolean) condition).booleanValue()) {
                    break;
                }
                try {
                    Thread.sleep(50); // Check every 50ms
                } catch (InterruptedException e) {
                    break;
                }
            }
            return null;
        } else if ("robActions_motor_setSpeed".equals(blockType)) {
            // Set motor speed without starting
            String motorPort = getFieldValue(block, "MOTORPORT");
            Object speedValue = getValue(block, "SPEED");
            
            if (motorPort != null && speedValue instanceof Double) {
                IMotor motor = getMotor(motorPort);
                if (motor != null) {
                    int speed = ((Double) speedValue).intValue();
                    speed = Math.max(0, Math.min(900, speed)); // Clamp to valid range
                    motor.setSpeed(speed);
                }
            }
            return null;
        } else if ("robSensors_encoder_rotation".equals(blockType)) {
            // Get motor encoder rotation value
            String motorPort = getFieldValue(block, "MOTORPORT");
            if (motorPort != null) {
                IMotor motor = getMotor(motorPort);
                if (motor != null) {
                    int rotation = motor.getTachoCount();
                    return new Double(rotation);
                }
            }
            return new Double(0);
        } else if ("robActions_led_on".equals(blockType)) {
            // Turn on LED (NXT has status LED)
            String color = getFieldValue(block, "COLOR");
            // NXT only has orange LED, but we'll simulate different colors
            // In real implementation, this would control the status LED
            // For now, we'll just track the state
            return null;
        } else if ("robActions_led_off".equals(blockType)) {
            // Turn off LED
            // In real implementation, this would turn off the status LED
            return null;
        } else if ("logic_compare".equals(blockType)) {
            String operation = getFieldValue(block, "OP");
            Object aValue = getValue(block, "A");
            Object bValue = getValue(block, "B");
            
            if (aValue instanceof Double && bValue instanceof Double && operation != null) {
                double a = ((Double) aValue).doubleValue();
                double b = ((Double) bValue).doubleValue();
                return new Boolean(executeComparison(operation, a, b));
            }
        } else if ("logic_operation".equals(blockType)) {
            String operation = getFieldValue(block, "OP");
            if (operation != null) {
                if ("NOT".equals(operation)) {
                    // Unary NOT operation
                    Object aValue = getValue(block, "A");
                    if (aValue instanceof Boolean) {
                        return new Boolean(!((Boolean) aValue).booleanValue());
                    }
                } else {
                    // Binary operations (AND, OR)
                    Object aValue = getValue(block, "A");
                    Object bValue = getValue(block, "B");
                    
                    if (aValue instanceof Boolean && bValue instanceof Boolean) {
                        boolean a = ((Boolean) aValue).booleanValue();
                        boolean b = ((Boolean) bValue).booleanValue();
                        return new Boolean(executeLogicalOperation(operation, a, b));
                    }
                }
            }
        }

        return null;
    }
    
    /**
     * Execute logical operations (AND, OR, NOT)
     */
    private boolean executeLogicalOperation(String operation, boolean a, boolean b) {
        if ("AND".equals(operation)) {
            return a && b;
        } else if ("OR".equals(operation)) {
            return a || b;
        }
        return false; // Default case
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
     * Get statement block by name
     */
    private IXMLElement getStatementBlock(IXMLElement parentBlock, String statementName) {
        System.out.println("Looking for statement: " + statementName);
        Vector<IXMLElement> statements = parentBlock.getChildren("statement");
        System.out.println("Found " + statements.size() + " statement elements");
        for (int i = 0; i < statements.size(); i++) {
            IXMLElement statement = statements.elementAt(i);
            IString nameAttr = statement.getAttribute("name");
            System.out.println("Statement " + i + " name: " + (nameAttr != null ? nameAttr.toString() : "null"));
            if (nameAttr != null && statementName.equals(nameAttr.toString())) {
                IXMLElement blockChild = statement.getChild("block");
                System.out.println("Found matching statement, has block child: " + (blockChild != null));
                return blockChild;
            }
        }
        return null;
    }

    /**
     * Get next block in sequence
     */
    private IXMLElement getNextBlock(IXMLElement currentBlock) {
        IXMLElement nextElement = currentBlock.getChild("next");
        if (nextElement != null) {
            return nextElement.getChild("block");
        }
        return null;
    }
    
    /**
     * Get motor by port letter
     */
    private IMotor getMotor(String port) {
        // Check if motor is configured
        if (robotConfig != null && !robotConfig.hasMotor(port)) {
            return null; // Motor not configured
        }
        
        return hardware.getMotor(port);
    }

    /**
     * Check if a sensor of the expected type is configured on the port
     */
    private boolean isSensorConfigured(String port, String expectedType) {
        if (robotConfig == null) return true; // Default behavior
        
        String configuredType = robotConfig.getSensorType(port);
        return expectedType.equals(configuredType);
    }
    
    /**
     * Get motor configuration for advanced motor control
     */
    private RobotConfiguration.MotorConfig getMotorConfig(String port) {
        if (robotConfig == null) return null;
        return robotConfig.getMotor(port);
    }

    /**
     * Execute comparison operation
     */
    private boolean executeComparison(String operation, double a, double b) {
        if ("EQ".equals(operation)) return a == b;
        if ("NEQ".equals(operation)) return a != b;
        if ("LT".equals(operation)) return a < b;
        if ("LTE".equals(operation)) return a <= b;
        if ("GT".equals(operation)) return a > b;
        if ("GTE".equals(operation)) return a >= b;
        return false;
    }
    
    /**
     * Stop execution
     */
    public void stop() {
        running = false;
    }
}
