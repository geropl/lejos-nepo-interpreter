import java.util.*;

/**
 * NEPO Block Executor - Minimal implementation for programm1.xml
 * 
 * Only implements blocks that actually exist in programm1.xml:
 * - robControls_start
 * - robControls_loopForever  
 * - robActions_motorDiff_on
 * - robControls_wait
 * - robActions_motorDiff_turn_for
 * - robSensors_light_getSample
 * - robSensors_touch_getSample
 * - logic_compare
 * - logic_operation
 * - math_number
 */
public class NepoBlockExecutor {
    private boolean running = true;
    
    /**
     * Get the running state for subclasses.
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Set the running state for subclasses.
     */
    protected void setRunning(boolean running) {
        this.running = running;
    }
    
    private RobotConfiguration robotConfig;
    private ConfigurationBlockExecutor configExecutor;
    private IHardware hardware;

    public NepoBlockExecutor(IHardware hardware) {
        this.hardware = hardware;
        configExecutor = new ConfigurationBlockExecutor();
        robotConfig = configExecutor.createDefaultConfiguration();
    }

    public void setConfiguration(IXMLElement configElement) throws ConfigurationException {
        robotConfig = configExecutor.parseConfiguration(configElement);
    }
    
    public RobotConfiguration getConfiguration() {
        return robotConfig;
    }
    
    /**
     * Run a complete NEPO program from XML root element.
     * Handles configuration parsing and program execution.
     * 
     * @param xmlRoot The root XML element containing config and program sections
     * @throws Exception if no program section is found
     */
    public void runProgram(IXMLElement xmlRoot) throws Exception {
        // Find and set configuration if present
        IXMLElement config = xmlRoot.findElement("config");
        if (config != null) {
            setConfiguration(config);
        }
        
        // Find program section
        IXMLElement program = xmlRoot.findElement("program");
        if (program == null) {
            throw new Exception("No program section found in XML");
        }
        
        executeProgram(program);
    }

    /**
     * Execute a complete program - finds instance and executes all blocks in sequence
     */
    private void executeProgram(IXMLElement program) {
        // Find instance
        IXMLElement instance = program.findElement("instance");
        if (instance == null) return;
        
        // Execute all blocks in the instance in order
        Vector blocks = instance.getChildren("block");
        for (int i = 0; i < blocks.size(); i++) {
            IXMLElement block = (IXMLElement) blocks.elementAt(i);
            executeBlock(block);
        }
    }
    
    /**
     * Execute a block based on its type and parameters
     * 
     * Can be called with:
     * - A "program" element: will find and execute the start block within it
     * - Any specific block type: will execute that block directly
     */
    public void executeBlock(IXMLElement block) {
        if (!running) return;
        
        // Handle program element - find and execute all blocks starting with start block
        if ("program".equals(block.getTagName())) {
            executeProgram(block);
            return;
        }
        
        IString blockTypeAttr = block.getAttribute("type");
        if (blockTypeAttr == null) {
            return;
        }
        String blockType = blockTypeAttr.toString();
        try {
            
            if ("robControls_start".equals(blockType)) {
                executeStartBlock(block);
            } else if ("robControls_loopForever".equals(blockType)) {
                executeLoopForeverBlock(block);
            } else if ("robActions_motorDiff_on".equals(blockType)) {
                executeMotorDiffOnBlock(block);
            } else if ("robControls_wait".equals(blockType)) {
                executeWaitBlock(block);
            } else if ("robActions_motorDiff_turn_for".equals(blockType)) {
                executeMotorDiffTurnForBlock(block);
            } else {
                System.out.println("Unknown block type: " + blockType);
            }

        } catch (Exception e) {
            System.err.println("Error executing block " + blockType + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Execute start block - triggers program execution
     */
    private void executeStartBlock(IXMLElement block) {
        // Start block just marks the beginning - actual execution continues with next blocks
    }

    /**
     * Execute loop forever block
     */
    protected void executeLoopForeverBlock(IXMLElement loop) {
        IXMLElement doStatement = loop.getChild("statement");
        if (!doStatement.getAttribute("name").equals("DO")) {
            throw new RuntimeException("only DO statements supported in loops");
        }
        Vector<IXMLElement> blocks = doStatement.getChildren("block");
        
        while (this.running) {
            for (int i = 0; i < blocks.size(); i++) {
                IXMLElement block = blocks.elementAt(i);
                executeBlock(block);
            }
            
            // Call hook after block execution
            this.onIteration();
        }
    }
    
    /**
     * Hook called after each loop iteration.
     * Subclasses can override to implement custom iteration logic.
     */
    protected void onIteration() {
        // Default: no-op
    }

    /**
     * Execute motor differential on block
     */
    private void executeMotorDiffOnBlock(IXMLElement block) {
        String direction = getFieldValue(block, "DIRECTION");
        Object powerValue = getValue(block, "POWER");
        
        if (powerValue instanceof Double) {
            double power = ((Double) powerValue).doubleValue();
            int speed = (int) (Math.abs(power) * 7.2); // Convert percentage to degrees/second
            
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
        Object conditionValue = getValue(block, "WAIT0");
        
        while (this.running) {
            conditionValue = getValue(block, "WAIT0");
            if (conditionValue instanceof Boolean && ((Boolean) conditionValue).booleanValue()) {
                break;
            }
            // try {
            //     Thread.sleep(50); // Check every 50ms
            // } catch (InterruptedException e) {
            //     break;
            // }

            this.onIteration();
        }
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
                    Double result = Double.valueOf(Double.parseDouble(numText));

                    return result;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format: " + numText);
                    return Double.valueOf(0);
                }
            }
            return Double.valueOf(0);
        } else if ("logic_compare".equals(blockType)) {
            String op = getFieldValue(block, "OP");
            Object valueA = getValue(block, "A");
            Object valueB = getValue(block, "B");
            
            if (op != null && valueA != null && valueB != null) {
                if (valueA instanceof Double && valueB instanceof Double) {
                    double a = ((Double) valueA).doubleValue();
                    double b = ((Double) valueB).doubleValue();
                    
                    if ("LT".equals(op)) {
                        return Boolean.valueOf(a < b);
                    } else if ("GT".equals(op)) {
                        return Boolean.valueOf(a > b);
                    } else if ("EQ".equals(op)) {
                        return Boolean.valueOf(a == b);
                    } else if ("NEQ".equals(op)) {
                        return Boolean.valueOf(a != b);
                    } else if ("LTE".equals(op)) {
                        return Boolean.valueOf(a <= b);
                    } else if ("GTE".equals(op)) {
                        return Boolean.valueOf(a >= b);
                    }
                }
            }
            return Boolean.valueOf(false);
        } else if ("logic_operation".equals(blockType)) {
            String op = getFieldValue(block, "OP");
            Object valueA = getValue(block, "A");
            Object valueB = getValue(block, "B");
            
            if (op != null && valueA != null && valueB != null) {
                if (valueA instanceof Boolean && valueB instanceof Boolean) {
                    boolean a = ((Boolean) valueA).booleanValue();
                    boolean b = ((Boolean) valueB).booleanValue();
                    
                    if ("AND".equals(op)) {
                        return Boolean.valueOf(a && b);
                    } else if ("OR".equals(op)) {
                        return Boolean.valueOf(a || b);
                    }
                }
            }
            return Boolean.valueOf(false);
        } else if ("robSensors_light_getSample".equals(blockType)) {
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                ISensor sensor = hardware.getSensor(sensorPort, "LIGHT");
                if (sensor != null) {
                    return Double.valueOf(sensor.getValue());
                }
            }
            return Double.valueOf(50); // Default value
        } else if ("robSensors_touch_getSample".equals(blockType)) {
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                ISensor sensor = hardware.getSensor(sensorPort, "TOUCH");
                if (sensor != null) {
                    return Boolean.valueOf(sensor.isPressed());
                }
            }
            return Boolean.valueOf(false); // Default value
        }
        
        return null;
    }

    /**
     * Get field value from block
     */
    private String getFieldValue(IXMLElement block, String fieldName) {
        Vector fields = block.getChildren("field");
        for (int i = 0; i < fields.size(); i++) {
            IXMLElement field = (IXMLElement) fields.elementAt(i);
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
    protected IXMLElement getStatementBlock(IXMLElement parentBlock, String statementName) {
        Vector statements = parentBlock.getChildren("statement");
        for (int i = 0; i < statements.size(); i++) {
            IXMLElement statement = (IXMLElement) statements.elementAt(i);
            IString nameAttr = statement.getAttribute("name");
            if (nameAttr != null && statementName.equals(nameAttr.toString())) {
                return statement.getChild("block");
            }
        }
        return null;
    }

    /**
     * Get motor by port
     */
    private IMotor getMotor(String port) {
        if (robotConfig != null && robotConfig.hasMotor(port)) {
            return hardware.getMotor(port);
        }
        return null;
    }
    
}