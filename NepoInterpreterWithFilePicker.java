import lejos.nxt.*;
import lejos.util.Delay;

/**
 * Enhanced NEPO XML Interpreter with File Picker
 * 
 * This version includes a user-friendly file picker that allows users to
 * select NEPO XML programs directly on the NXT brick, making the system
 * more user-friendly for standalone operation.
 * 
 * Features:
 * - Interactive file picker for XML program selection
 * - Fallback to command line arguments if provided
 * - Error handling and user feedback
 * - Integration with existing NEPO interpreter functionality
 */
public class NepoInterpreterWithFilePicker {
    
    public static void main(String[] args) {
        // Display startup message
        LCD.drawString("NEPO Interpreter", 0, 0);
        LCD.drawString("with File Picker", 0, 1);
        LCD.drawString("v2.0", 0, 2);
        LCD.refresh();
        Delay.msDelay(2000);
        
        String xmlFile = null;
        
        try {
            // Check if filename provided via command line
            if (args.length > 0) {
                xmlFile = args[0];
                LCD.clear();
                LCD.drawString("Using file:", 0, 0);
                LCD.drawString(xmlFile, 0, 1);
                LCD.drawString("Press ENTER", 0, 3);
                LCD.drawString("or ESC to pick", 0, 4);
                LCD.refresh();
                
                int button = Button.waitForAnyPress();
                if (button == Button.ID_ESCAPE) {
                    // User wants to pick a different file
                    xmlFile = null;
                }
            }
            
            // If no file specified or user chose to pick, show file picker
            if (xmlFile == null) {
                LCD.clear();
                LCD.drawString("Loading files...", 0, 2);
                LCD.refresh();
                Delay.msDelay(500);
                
                xmlFile = FilePicker.selectFile();
                
                if (xmlFile == null) {
                    LCD.clear();
                    LCD.drawString("No file selected", 0, 2);
                    LCD.drawString("Exiting...", 0, 3);
                    LCD.refresh();
                    Delay.msDelay(2000);
                    return;
                }
            }
            
            // Display selected file and confirm
            LCD.clear();
            LCD.drawString("Selected:", 0, 0);
            LCD.drawString(xmlFile, 0, 1);
            LCD.drawString("", 0, 2);
            LCD.drawString("ENTER: Run", 0, 3);
            LCD.drawString("ESC: Cancel", 0, 4);
            LCD.refresh();
            
            int button = Button.waitForAnyPress();
            if (button == Button.ID_ESCAPE) {
                LCD.clear();
                LCD.drawString("Cancelled", 0, 2);
                LCD.refresh();
                Delay.msDelay(1000);
                return;
            }
            
            // Load and execute the program
            LCD.clear();
            LCD.drawString("Loading:", 0, 0);
            LCD.drawString(xmlFile, 0, 1);
            LCD.refresh();
            Delay.msDelay(1000);
            
            // Parse the XML program using SimpleXMLParser
            SimpleXMLParser.XMLElement root = SimpleXMLParser.parseFile(xmlFile);
            if (root == null) {
                showError("Failed to parse", "XML file");
                return;
            }
            
            LCD.drawString("XML parsed OK", 0, 2);
            LCD.refresh();
            Delay.msDelay(1000);
            
            // Find the start block
            SimpleXMLParser.XMLElement startBlock = findStartBlock(root);
            if (startBlock == null) {
                showError("No start block", "found in program");
                return;
            }
            
            LCD.drawString("Start block found", 0, 3);
            LCD.refresh();
            Delay.msDelay(1000);
            
            // Create executor and run program
            NepoBlockExecutor executor = new NepoBlockExecutor();
            
            LCD.clear();
            LCD.drawString("Executing:", 0, 0);
            LCD.drawString(xmlFile, 0, 1);
            LCD.drawString("", 0, 2);
            LCD.drawString("ESC to stop", 0, 6);
            LCD.refresh();
            Delay.msDelay(1000);
            
            // Execute the program
            executor.executeBlock(startBlock);
            
        } catch (Exception e) {
            showError("Error:", e.getMessage());
        }
        
        // Program finished
        LCD.clear();
        LCD.drawString("Program finished", 0, 2);
        LCD.drawString("Press any key", 0, 4);
        LCD.refresh();
        Button.waitForAnyPress();
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
        
        // Alternative: search all blocks recursively
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
    
    /**
     * Display error message and wait for user input
     */
    private static void showError(String line1, String line2) {
        LCD.clear();
        LCD.drawString("ERROR:", 0, 1);
        LCD.drawString(line1, 0, 2);
        LCD.drawString(line2, 0, 3);
        LCD.drawString("", 0, 4);
        LCD.drawString("Press any key", 0, 6);
        LCD.refresh();
        Button.waitForAnyPress();
    }
}
