import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.Delay;
import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 * NEPO XML Interpreter for leJOS NXT
 * 
 * This interpreter reads NEPO XML programs created by Open Roberta Lab
 * and executes them directly on leJOS NXT firmware.
 * 
 * Architecture:
 * 1. XML Parser - Parses NEPO XML into DOM structure
 * 2. Block Registry - Maps NEPO block types to Java implementations
 * 3. Execution Engine - Traverses and executes blocks sequentially
 * 4. Hardware Abstraction - Maps NEPO hardware references to leJOS APIs
 */
public class NepoInterpreter {
    
    // Hardware mappings
    private static final Map<String, NXTRegulatedMotor> MOTORS = new HashMap<>();
    private static final Map<String, SensorPort> SENSOR_PORTS = new HashMap<>();
    
    // Block execution registry
    private static final Map<String, BlockExecutor> BLOCK_EXECUTORS = new HashMap<>();
    
    // Program state
    private Document xmlDocument;
    private Map<String, Object> variables = new HashMap<>();
    private boolean running = true;
    
    static {
        // Initialize motor mappings
        MOTORS.put("A", Motor.A);
        MOTORS.put("B", Motor.B);
        MOTORS.put("C", Motor.C);
        
        // Initialize sensor port mappings
        SENSOR_PORTS.put("1", SensorPort.S1);
        SENSOR_PORTS.put("2", SensorPort.S2);
        SENSOR_PORTS.put("3", SensorPort.S3);
        SENSOR_PORTS.put("4", SensorPort.S4);
        
        // Register block executors
        registerBlockExecutors();
    }
    
    public static void main(String[] args) {
        NepoInterpreter interpreter = new NepoInterpreter();
        
        try {
            // Load NEPO XML program from file
            String xmlFile = "program.xml";
            if (args.length > 0) {
                xmlFile = args[0];
            }
            
            interpreter.loadProgram(xmlFile);
            interpreter.execute();
            
        } catch (Exception e) {
            LCD.drawString("Error: " + e.getMessage(), 0, 0);
            LCD.refresh();
            Button.waitForAnyPress();
        }
    }
    
    /**
     * Load NEPO XML program from file
     */
    public void loadProgram(String filename) throws Exception {
        File xmlFile = new File(filename);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        xmlDocument = dBuilder.parse(xmlFile);
        xmlDocument.getDocumentElement().normalize();
    }
    
    /**
     * Execute the loaded NEPO program
     */
    public void execute() throws Exception {
        // Find the start block
        NodeList startBlocks = xmlDocument.getElementsByTagName("block");
        Element startBlock = null;
        
        for (int i = 0; i < startBlocks.getLength(); i++) {
            Element block = (Element) startBlocks.item(i);
            if ("robControls_start".equals(block.getAttribute("type"))) {
                startBlock = block;
                break;
            }
        }
        
        if (startBlock == null) {
            throw new Exception("No start block found");
        }
        
        // Execute the program starting from the start block
        executeBlock(startBlock);
    }
    
    /**
     * Execute a single block and its connected blocks
     */
    private void executeBlock(Element block) throws Exception {
        if (!running) return;
        
        String blockType = block.getAttribute("type");
        BlockExecutor executor = BLOCK_EXECUTORS.get(blockType);
        
        if (executor != null) {
            executor.execute(block, this);
        } else {
            LCD.drawString("Unknown block: " + blockType, 0, 1);
            LCD.refresh();
        }
        
        // Execute next block in sequence
        Element nextBlock = getNextBlock(block);
        if (nextBlock != null) {
            executeBlock(nextBlock);
        }
    }
    
    /**
     * Get the next block in the execution sequence
     */
    private Element getNextBlock(Element currentBlock) {
        NodeList nextElements = currentBlock.getElementsByTagName("next");
        if (nextElements.getLength() > 0) {
            Element nextElement = (Element) nextElements.item(0);
            NodeList blockElements = nextElement.getElementsByTagName("block");
            if (blockElements.getLength() > 0) {
                return (Element) blockElements.item(0);
            }
        }
        return null;
    }
    
    /**
     * Get statement blocks (for control structures)
     */
    public Element getStatementBlock(Element parentBlock, String statementName) {
        NodeList statements = parentBlock.getElementsByTagName("statement");
        for (int i = 0; i < statements.getLength(); i++) {
            Element statement = (Element) statements.item(i);
            if (statementName.equals(statement.getAttribute("name"))) {
                NodeList blocks = statement.getElementsByTagName("block");
                if (blocks.getLength() > 0) {
                    return (Element) blocks.item(0);
                }
            }
        }
        return null;
    }
    
    /**
     * Get value from a value block
     */
    public Object getValue(Element parentBlock, String valueName) {
        NodeList values = parentBlock.getElementsByTagName("value");
        for (int i = 0; i < values.getLength(); i++) {
            Element value = (Element) values.item(i);
            if (valueName.equals(value.getAttribute("name"))) {
                NodeList blocks = value.getElementsByTagName("block");
                if (blocks.getLength() > 0) {
                    Element valueBlock = (Element) blocks.item(0);
                    return evaluateValueBlock(valueBlock);
                }
            }
        }
        return null;
    }
    
    /**
     * Evaluate a value block and return its result
     */
    private Object evaluateValueBlock(Element block) {
        String blockType = block.getAttribute("type");
        
        switch (blockType) {
            case "math_number":
                NodeList numFields = block.getElementsByTagName("field");
                for (int i = 0; i < numFields.getLength(); i++) {
                    Element field = (Element) numFields.item(i);
                    if ("NUM".equals(field.getAttribute("name"))) {
                        return Double.parseDouble(field.getTextContent());
                    }
                }
                break;
                
            case "text":
                NodeList textFields = block.getElementsByTagName("field");
                for (int i = 0; i < textFields.getLength(); i++) {
                    Element field = (Element) textFields.item(i);
                    if ("TEXT".equals(field.getAttribute("name"))) {
                        return field.getTextContent();
                    }
                }
                break;
        }
        
        return null;
    }
    
    /**
     * Get field value from a block
     */
    public String getFieldValue(Element block, String fieldName) {
        NodeList fields = block.getElementsByTagName("field");
        for (int i = 0; i < fields.getLength(); i++) {
            Element field = (Element) fields.item(i);
            if (fieldName.equals(field.getAttribute("name"))) {
                return field.getTextContent();
            }
        }
        return null;
    }
    
    // Getters for hardware access
    public NXTRegulatedMotor getMotor(String port) {
        return MOTORS.get(port);
    }
    
    public SensorPort getSensorPort(String port) {
        return SENSOR_PORTS.get(port);
    }
    
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }
    
    public Object getVariable(String name) {
        return variables.get(name);
    }
    
    public void stop() {
        running = false;
    }
    
    /**
     * Register all block executors
     */
    private static void registerBlockExecutors() {
        // Control blocks
        BLOCK_EXECUTORS.put("robControls_start", new StartBlockExecutor());
        BLOCK_EXECUTORS.put("robControls_wait_time", new WaitTimeBlockExecutor());
        
        // Action blocks
        BLOCK_EXECUTORS.put("robActions_display_text", new DisplayTextBlockExecutor());
        BLOCK_EXECUTORS.put("robActions_motor_on", new MotorOnBlockExecutor());
        BLOCK_EXECUTORS.put("robActions_motor_stop", new MotorStopBlockExecutor());
        
        // Add more block executors as needed...
    }
    
    /**
     * Interface for block executors
     */
    interface BlockExecutor {
        void execute(Element block, NepoInterpreter interpreter) throws Exception;
    }
    
    /**
     * Start block executor - just continues to statement blocks
     */
    static class StartBlockExecutor implements BlockExecutor {
        public void execute(Element block, NepoInterpreter interpreter) throws Exception {
            Element statementBlock = interpreter.getStatementBlock(block, "ST");
            if (statementBlock != null) {
                interpreter.executeBlock(statementBlock);
            }
        }
    }
    
    /**
     * Wait time block executor
     */
    static class WaitTimeBlockExecutor implements BlockExecutor {
        public void execute(Element block, NepoInterpreter interpreter) throws Exception {
            Object waitValue = interpreter.getValue(block, "WAIT");
            if (waitValue instanceof Double) {
                int milliseconds = ((Double) waitValue).intValue();
                Delay.msDelay(milliseconds);
            }
        }
    }
    
    /**
     * Display text block executor
     */
    static class DisplayTextBlockExecutor implements BlockExecutor {
        public void execute(Element block, NepoInterpreter interpreter) throws Exception {
            Object textValue = interpreter.getValue(block, "OUT");
            if (textValue instanceof String) {
                LCD.clear();
                LCD.drawString((String) textValue, 0, 0);
                LCD.refresh();
            }
        }
    }
    
    /**
     * Motor on block executor
     */
    static class MotorOnBlockExecutor implements BlockExecutor {
        public void execute(Element block, NepoInterpreter interpreter) throws Exception {
            String motorPort = interpreter.getFieldValue(block, "MOTORPORT");
            String rotationType = interpreter.getFieldValue(block, "MOTORROTATION");
            
            Object powerValue = interpreter.getValue(block, "POWER");
            Object rotationValue = interpreter.getValue(block, "VALUE");
            
            if (motorPort != null && powerValue instanceof Double) {
                NXTRegulatedMotor motor = interpreter.getMotor(motorPort);
                if (motor != null) {
                    int power = ((Double) powerValue).intValue();
                    motor.setSpeed(Math.abs(power) * 7.2); // Convert percentage to degrees/second
                    
                    if ("ROTATIONS".equals(rotationType) && rotationValue instanceof Double) {
                        int degrees = (int) (((Double) rotationValue) * 360);
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
    }
    
    /**
     * Motor stop block executor
     */
    static class MotorStopBlockExecutor implements BlockExecutor {
        public void execute(Element block, NepoInterpreter interpreter) throws Exception {
            String motorPort = interpreter.getFieldValue(block, "MOTORPORT");
            if (motorPort != null) {
                NXTRegulatedMotor motor = interpreter.getMotor(motorPort);
                if (motor != null) {
                    motor.stop();
                }
            }
        }
    }
}
