import lejos.nxt.*;
import lejos.util.Delay;
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
    
    /**
     * Execute a block based on its type and parameters
     */
    public void executeBlock(SimpleXMLParser.XMLElement block) {
        if (!running) return;
        
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
            } else if ("robControls_repeat_times".equals(blockType)) {
                executeRepeatTimesBlock(block);
            } else if ("robSensors_touch_isPressed".equals(blockType)) {
                // This is a sensor value block, handled in getValue
            } else if ("robSensors_ultrasonic_distance".equals(blockType)) {
                // This is a sensor value block, handled in getValue
            } else if ("robActions_play_tone".equals(blockType)) {
                executePlayToneBlock(block);
            } else {
                LCD.drawString("Unknown: " + blockType, 0, 7);
                LCD.refresh();
                Delay.msDelay(1000);
            }
            
            // Execute next block in sequence
            SimpleXMLParser.XMLElement nextBlock = getNextBlock(block);
            if (nextBlock != null) {
                executeBlock(nextBlock);
            }
            
        } catch (Exception e) {
            LCD.clear();
            LCD.drawString("Error in block:", 0, 0);
            LCD.drawString(blockType, 0, 1);
            LCD.drawString(e.getMessage(), 0, 2);
            LCD.refresh();
            Button.waitForAnyPress();
        }
    }
    
    /**
     * Execute start block - find and execute statement blocks
     */
    private void executeStartBlock(SimpleXMLParser.XMLElement block) {
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
            LCD.clear();
            LCD.drawString(textValue.toString(), 0, 0);
            LCD.refresh();
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
            NXTRegulatedMotor motor = getMotor(motorPort);
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
    private void executeMotorStopBlock(SimpleXMLParser.XMLElement block) {
        String motorPort = getFieldValue(block, "MOTORPORT");
        if (motorPort != null) {
            NXTRegulatedMotor motor = getMotor(motorPort);
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
            Delay.msDelay(milliseconds);
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
            Sound.playTone(frequency, duration);
            Delay.msDelay(duration);
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
                TouchSensor sensor = new TouchSensor(getSensorPort(sensorPort));
                return new Boolean(sensor.isPressed());
            }
        } else if ("robSensors_ultrasonic_distance".equals(blockType)) {
            String sensorPort = getFieldValue(block, "SENSORPORT");
            if (sensorPort != null) {
                UltrasonicSensor sensor = new UltrasonicSensor(getSensorPort(sensorPort));
                return new Double(sensor.getDistance());
            }
        } else if ("logic_compare".equals(blockType)) {
            String operation = getFieldValue(block, "OP");
            Object aValue = getValue(block, "A");
            Object bValue = getValue(block, "B");
            
            if (aValue instanceof Double && bValue instanceof Double && operation != null) {
                double a = ((Double) aValue).doubleValue();
                double b = ((Double) bValue).doubleValue();
                return new Boolean(executeComparison(operation, a, b));
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
     * Get motor by port letter
     */
    private NXTRegulatedMotor getMotor(String port) {
        if ("A".equals(port)) return Motor.A;
        if ("B".equals(port)) return Motor.B;
        if ("C".equals(port)) return Motor.C;
        return null;
    }
    
    /**
     * Get sensor port by number string
     */
    private SensorPort getSensorPort(String port) {
        if ("1".equals(port)) return SensorPort.S1;
        if ("2".equals(port)) return SensorPort.S2;
        if ("3".equals(port)) return SensorPort.S3;
        if ("4".equals(port)) return SensorPort.S4;
        return SensorPort.S1;
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
