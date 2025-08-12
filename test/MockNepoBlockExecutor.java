import java.util.*;

/**
 * MockNepoBlockExecutor - NEPO block executor that uses MockHardware
 * 
 * Extends the existing NepoBlockExecutor functionality to work with
 * MockHardware for testing purposes.
 */
public class MockNepoBlockExecutor {
    
    private MockHardware mockHardware;
    private boolean running;
    private Hashtable variables;
    
    /**
     * Create executor with mock hardware
     */
    public MockNepoBlockExecutor(MockHardware mockHardware) {
        this.mockHardware = mockHardware;
        this.running = true;
        this.variables = new Hashtable();
    }
    
    /**
     * Execute a block based on its type and parameters
     */
    public void executeBlock(SimpleXMLParser.XMLElement block) {
        if (!running || block == null) return;
        
        String blockType = block.getAttribute("type");
        if (blockType == null) return;
        
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
            } else if ("robActions_play_tone".equals(blockType)) {
                executePlayToneBlock(block);
            } else {
                mockHardware.logEvent("ERROR", "Unknown block type: " + blockType);
            }
            
            // Execute next block in sequence
            SimpleXMLParser.XMLElement nextBlock = getNextBlock(block);
            if (nextBlock != null) {
                executeBlock(nextBlock);
            }
            
        } catch (Exception e) {
            mockHardware.logEvent("ERROR", "Exception in block " + blockType + ": " + e.getMessage());
            throw new RuntimeException("Block execution failed: " + e.getMessage());
        }
    }
    
    /**
     * Execute start block - find and execute statement blocks
     */
    private void executeStartBlock(SimpleXMLParser.XMLElement block) {
        mockHardware.logEvent("CONTROL", "Program started");
        SimpleXMLParser.XMLElement statement = getStatementBlock(block, "ST");
        if (statement != null) {
            executeBlock(statement);
        }
    }
    
    /**
     * Execute display text block
     */
    private void executeDisplayTextBlock(SimpleXMLParser.XMLElement block) {
        Object textValue = getValue(block, "OUT");
        if (textValue != null) {
            MockDisplay display = mockHardware.getDisplay();
            display.clear();
            display.drawString(textValue.toString(), 0, 0);
            display.refresh();
        }
    }
    
    /**
     * Execute motor on block
     */
    private void executeMotorOnBlock(SimpleXMLParser.XMLElement block) {
        String motorPort = getFieldValue(block, "MOTORPORT");
        String rotationType = getFieldValue(block, "MOTORROTATION");
        
        Object powerValue = getValue(block, "POWER");
        Object rotationValue = getValue(block, "VALUE");
        
        if (motorPort != null && powerValue instanceof Double) {
            MockMotor motor = mockHardware.getMotor(motorPort);
            if (motor != null) {
                double power = ((Double) powerValue).doubleValue();
                motor.setPowerSetting((int) power);
                
                if ("ROTATIONS".equals(rotationType) && rotationValue instanceof Double) {
                    double rotations = ((Double) rotationValue).doubleValue();
                    motor.rotateBy(rotations);
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
    private void executeMotorStopBlock(SimpleXMLParser.XMLElement block) {
        String motorPort = getFieldValue(block, "MOTORPORT");
        if (motorPort != null) {
            MockMotor motor = mockHardware.getMotor(motorPort);
            if (motor != null) {
                motor.stop();
            }
        }
    }
    
    /**
     * Execute wait time block
     */
    private void executeWaitTimeBlock(SimpleXMLParser.XMLElement block) {
        Object waitValue = getValue(block, "WAIT");
        if (waitValue instanceof Double) {
            int milliseconds = ((Double) waitValue).intValue();
            mockHardware.simulateDelay(milliseconds);
        }
    }
    
    /**
     * Execute if block
     */
    private void executeIfBlock(SimpleXMLParser.XMLElement block) {
        Object conditionValue = getValue(block, "IF0");
        if (conditionValue instanceof Boolean && ((Boolean) conditionValue).booleanValue()) {
            SimpleXMLParser.XMLElement doBlock = getStatementBlock(block, "DO0");
            if (doBlock != null) {
                executeBlock(doBlock);
            }
        }
    }
    
    /**
     * Execute if-else block
     */
    private void executeIfElseBlock(SimpleXMLParser.XMLElement block) {
        Object conditionValue = getValue(block, "IF0");
        if (conditionValue instanceof Boolean && ((Boolean) conditionValue).booleanValue()) {
            // Execute IF branch
            SimpleXMLParser.XMLElement doBlock = getStatementBlock(block, "DO0");
            if (doBlock != null) {
                executeBlock(doBlock);
            }
        } else {
            // Execute ELSE branch
            SimpleXMLParser.XMLElement elseBlock = getStatementBlock(block, "ELSE");
            if (elseBlock != null) {
                executeBlock(elseBlock);
            }
        }
    }

    /**
     * Execute repeat times block
     */
    private void executeRepeatTimesBlock(SimpleXMLParser.XMLElement block) {
        Object timesValue = getValue(block, "TIMES");
        if (timesValue instanceof Double) {
            int times = ((Double) timesValue).intValue();
            SimpleXMLParser.XMLElement doBlock = getStatementBlock(block, "DO");
            
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
    private void executePlayToneBlock(SimpleXMLParser.XMLElement block) {
        Object frequencyValue = getValue(block, "FREQUENCY");
        Object durationValue = getValue(block, "DURATION");
        
        if (frequencyValue instanceof Double && durationValue instanceof Double) {
            int frequency = ((Double) frequencyValue).intValue();
            int duration = ((Double) durationValue).intValue();
            MockSound sound = mockHardware.getSound();
            sound.playTone(frequency, duration);
        }
    }
    
    /**
     * Get value from a value block
     */
    private Object getValue(SimpleXMLParser.XMLElement parentBlock, String valueName) {
        Vector values = parentBlock.getChildren("value");
        for (int i = 0; i < values.size(); i++) {
            SimpleXMLParser.XMLElement value = (SimpleXMLParser.XMLElement) values.elementAt(i);
            if (valueName.equals(value.getAttribute("name"))) {
                SimpleXMLParser.XMLElement valueBlock = value.getChild("block");
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
    private Object evaluateValueBlock(SimpleXMLParser.XMLElement block) {
        String blockType = block.getAttribute("type");
        
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
                MockSensor sensor = mockHardware.getSensor(sensorPort);
                if (sensor != null) {
                    return new Boolean(sensor.isPressed());
                }
            }
        } else if ("robSensors_ultrasonic_distance".equals(blockType)) {
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                MockSensor sensor = mockHardware.getSensor(sensorPort);
                if (sensor != null) {
                    return new Double(sensor.getDistance());
                }
            }
        } else if ("robActions_motor_getPower".equals(blockType)) {
            String motorPort = getFieldValue(block, "MOTORPORT");
            if (motorPort != null) {
                MockMotor motor = mockHardware.getMotor(motorPort);
                if (motor != null) {
                    MockMotor.MotorState state = motor.getState();
                    return new Double(state.getPowerSetting());
                }
            }
        } else if ("robSensors_timer_get".equals(blockType)) {
            // Return timer value from mock hardware
            MockTimer timer = mockHardware.getTimer();
            return new Double(timer.getValue());
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
     * Get statement block by name
     */
    private SimpleXMLParser.XMLElement getStatementBlock(SimpleXMLParser.XMLElement parentBlock, String statementName) {
        Vector statements = parentBlock.getChildren("statement");
        for (int i = 0; i < statements.size(); i++) {
            SimpleXMLParser.XMLElement statement = (SimpleXMLParser.XMLElement) statements.elementAt(i);
            if (statementName.equals(statement.getAttribute("name"))) {
                return statement.getChild("block");
            }
        }
        return null;
    }
    
    /**
     * Get next block in sequence
     */
    private SimpleXMLParser.XMLElement getNextBlock(SimpleXMLParser.XMLElement currentBlock) {
        SimpleXMLParser.XMLElement nextElement = currentBlock.getChild("next");
        if (nextElement != null) {
            return nextElement.getChild("block");
        }
        return null;
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
     * Execute logical operation
     */
    private boolean executeLogicalOperation(String operation, boolean a, boolean b) {
        if ("AND".equals(operation)) return a && b;
        if ("OR".equals(operation)) return a || b;
        return false;
    }

    /**
     * Stop execution
     */
    public void stop() {
        running = false;
    }
    
    /**
     * Check if executor is running
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Get mock hardware reference
     */
    public MockHardware getMockHardware() {
        return mockHardware;
    }
}
