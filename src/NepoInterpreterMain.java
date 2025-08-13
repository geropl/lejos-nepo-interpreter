import lejos.nxt.*;
import lejos.util.Delay;

/**
 * Main class for NEPO XML Interpreter with Dynamic File Selection
 * 
 * This version includes XML parsing capabilities and can execute
 * real NEPO XML programs created in Open Roberta Lab with dynamic file picking.
 */
public class NepoInterpreterMain {
    
    public static void main(String[] args) {
        LCD.clear();
        LCD.drawString("NEPO Interpreter", 0, 0);
        LCD.drawString("v2.0 Dynamic", 0, 1);
        LCD.refresh();
        Delay.msDelay(1500);
        
        try {
            String xmlFile = null;
            
            // Check if filename provided as argument
            if (args.length > 0) {
                xmlFile = args[0];
                LCD.clear();
                LCD.drawString("Using argument:", 0, 0);
                LCD.drawString(xmlFile, 0, 1);
                LCD.refresh();
                Delay.msDelay(1000);
            } else {
                // Show dynamic file picker
                xmlFile = FilePicker.pickFileAdvanced(".xml", "Select NEPO Program", true);
                
                if (xmlFile == null) {
                    LCD.clear();
                    LCD.drawString("No file selected", 0, 0);
                    LCD.drawString("Using default", 0, 1);
                    LCD.refresh();
                    Delay.msDelay(1000);
                    xmlFile = "test_program.xml";
                }
            }
            
            LCD.clear();
            LCD.drawString("Loading:", 0, 0);
            LCD.drawString(xmlFile, 0, 1);
            LCD.drawString("Please wait...", 0, 2);
            LCD.refresh();
            Delay.msDelay(1000);

            // Parse the XML program
            SimpleXMLParser.XMLElement root = SimpleXMLParser.parseFile(xmlFile);
            if (root == null) {
                LCD.clear();
                LCD.drawString("Failed to parse", 0, 0);
                LCD.drawString("XML file", 0, 1);
                LCD.refresh();
                Button.waitForAnyPress();
                return;
            }
            
            LCD.drawString("XML parsed OK", 0, 3);
            LCD.refresh();
            Delay.msDelay(1000);
            
            // Find the start block
            SimpleXMLParser.XMLElement startBlock = findStartBlock(root);
            if (startBlock == null) {
                LCD.clear();
                LCD.drawString("No start block", 0, 0);
                LCD.drawString("found", 0, 1);
                LCD.refresh();
                Button.waitForAnyPress();
                return;
            }
            
            LCD.drawString("Start block found", 0, 4);
            LCD.refresh();
            Delay.msDelay(1000);
            
            // Create executor and run program
            NepoBlockExecutor executor = new NepoBlockExecutor();
            
            LCD.clear();
            LCD.drawString("Executing...", 0, 0);
            LCD.refresh();
            Delay.msDelay(500);
            
            executor.executeBlock(startBlock);
            
        } catch (Exception e) {
            LCD.clear();
            LCD.drawString("Error:", 0, 0);
            LCD.drawString(e.getMessage(), 0, 1);
            LCD.drawString("Press any key", 0, 6);
            LCD.refresh();
            Button.waitForAnyPress();
        }
        
        // Program completion with restart option
        LCD.clear();
        LCD.drawString("Program Complete", 0, 0);
        LCD.drawString("", 0, 1);
        LCD.drawString("Press ENTER to", 0, 3);
        LCD.drawString("run another", 0, 4);
        LCD.drawString("Press ESCAPE", 0, 5);
        LCD.drawString("to exit", 0, 6);
        LCD.refresh();
        
        int button = Button.waitForAnyPress();
        if (button == Button.ID_ENTER) {
            // Restart the program for another file selection
            main(new String[0]);
        }
    }
    
    /**
     * Find the start block in the XML structure
     */
    private static SimpleXMLParser.XMLElement findStartBlock(SimpleXMLParser.XMLElement root) {
        // Look for blockSet -> instance -> block with type="robControls_start"
        SimpleXMLParser.XMLElement instance = root.getChild("instance");
        if (instance != null) {
            SimpleXMLParser.XMLElement block = instance.getChild("block");
            if (block != null && "robControls_start".equals(block.getAttribute("type"))) {
                return block;
            }
        }
        
        // Alternative: search all blocks
        return findBlockByType(root, "robControls_start");
    }
    
    /**
     * Recursively find a block by type
     */
    private static SimpleXMLParser.XMLElement findBlockByType(SimpleXMLParser.XMLElement element, String blockType) {
        if ("block".equals(element.tagName) && blockType.equals(element.getAttribute("type"))) {
            return element;
        }
        
        for (int i = 0; i < element.children.size(); i++) {
            SimpleXMLParser.XMLElement child = (SimpleXMLParser.XMLElement) element.children.elementAt(i);
            SimpleXMLParser.XMLElement result = findBlockByType(child, blockType);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }
}
