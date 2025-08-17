import java.util.Vector;
import lejos.nxt.*;
import lejos.util.Delay;

/**
 * Dynamic NEPO Program Runner
 * 
 * This is the main entry point that demonstrates dynamic file selection
 * capabilities on the NXT. Users can browse and select XML programs
 * directly from the NXT interface.
 */
public class DynamicNepoRunner {
    
    public static void main(String[] args) {
        try {
            showWelcomeScreen();
            
            while (true) {
                String selectedFile = selectProgram();
                
                if (selectedFile == null) {
                    showExitScreen();
                    break;
                }
                
                executeProgram(selectedFile);
                
                if (!askForAnother()) {
                    showExitScreen();
                    break;
                }
            }
            
        } catch (Exception e) {
            CrashLogger.handleException(e);
        }
    }

    /**
     * Show welcome screen
     */
    private static void showWelcomeScreen() {
        LCD.clear();
        LCD.drawString("NEPO Dynamic", 0, 0);
        LCD.drawString("Program Runner", 0, 1);
        LCD.drawString("v2.0", 0, 2);
        LCD.drawString("", 0, 3);
        LCD.drawString("Press any key", 0, 5);
        LCD.drawString("to continue", 0, 6);
        LCD.refresh();
        Button.waitForAnyPress();
    }
    
    /**
     * Let user select a program using file picker
     */
    private static String selectProgram() {
        LCD.clear();
        LCD.drawString("Select Picker:", 0, 0);
        LCD.drawString("", 0, 1);
        LCD.drawString("ENTER: Simple", 0, 2);
        LCD.drawString("LEFT:  Advanced", 0, 3);
        LCD.drawString("ESCAPE: Exit", 0, 4);
        LCD.refresh();
        
        int button = Button.waitForAnyPress();
        
        if (button == Button.ID_ENTER) {
            return FilePicker.pickXMLFile();
        } else if (button == Button.ID_LEFT) {
            return AdvancedFilePicker.selectXmlFile();
        } else {
            return null; // Exit or any other button
        }
    }

    /**
     * Execute the selected program
     */
    private static void executeProgram(String filename) {
        LCD.clear();
        LCD.drawString("Executing:", 0, 0);
        LCD.drawString(filename, 0, 1);
        LCD.drawString("", 0, 2);
        LCD.drawString("Loading...", 0, 3);
        LCD.refresh();
        
        // Parse the XML program using ShallowXMLParser
        IXMLElement root = new ShallowXMLParser().parseFile(filename);
        if (root == null) {
            showError("Failed to parse XML file");
            return;
        }
        
        LCD.drawString("XML parsed OK", 0, 4);
        LCD.refresh();
        Delay.msDelay(1000);
        
        // Find the start block
        IXMLElement startBlock = findStartBlock(root);
        if (startBlock == null) {
            showError("No start block found in program");
            return;
        }

        LCD.drawString("Start block found", 0, 5);
        LCD.refresh();
        Delay.msDelay(1000);
        
        // Create executor and run program
        NepoBlockExecutor executor = new NepoBlockExecutor();
        
        LCD.clear();
        LCD.drawString("Running:", 0, 0);
        LCD.drawString(filename, 0, 1);
        LCD.drawString("", 0, 2);
        LCD.drawString("Press ESCAPE", 0, 6);
        LCD.drawString("to stop", 0, 7);
        LCD.refresh();
        
        executor.executeBlock(startBlock);
        
        LCD.clear();
        LCD.drawString("Program", 0, 2);
        LCD.drawString("completed", 0, 3);
        LCD.drawString("successfully", 0, 4);
        LCD.refresh();
        Delay.msDelay(2000);
    }
    
    /**
     * Find the start block in the XML structure
     */
    private static IXMLElement findStartBlock(IXMLElement root) {
        // Look for blockSet -> instance -> block with type="robControls_start"
        IXMLElement instance = root.getChild("instance");
        if (instance != null) {
            IXMLElement block = instance.getChild("block");
            if (block != null) {
                IString typeAttr = block.getAttribute("type");
                if (typeAttr != null && "robControls_start".equals(typeAttr.toString())) {
                    return block;
                }
            }
        }
        
        // Alternative: search all blocks recursively
        return findBlockByType(root, "robControls_start");
    }
    
    /**
     * Recursively find a block by type
     */
    private static IXMLElement findBlockByType(IXMLElement element, String blockType) {
        if ("block".equals(element.getTagName())) {
            IString typeAttr = element.getAttribute("type");
            if (typeAttr != null && blockType.equals(typeAttr.toString())) {
                return element;
            }
        }
        
        Vector<IXMLElement> children = element.getAllChildren();
        for (int i = 0; i < children.size(); i++) {
            IXMLElement child = children.elementAt(i);
            IXMLElement result = findBlockByType(child, blockType);
            if (result != null) {
                return result;
            }
        }
        
        return null;
    }

    /**
     * Ask if user wants to run another program
     */
    private static boolean askForAnother() {
        LCD.clear();
        LCD.drawString("Run another", 0, 1);
        LCD.drawString("program?", 0, 2);
        LCD.drawString("", 0, 3);
        LCD.drawString("ENTER: Yes", 0, 4);
        LCD.drawString("ESCAPE: No", 0, 5);
        LCD.refresh();
        
        int button = Button.waitForAnyPress();
        return (button == Button.ID_ENTER);
    }
    
    /**
     * Show error message
     */
    private static void showError(String message) {
        LCD.clear();
        LCD.drawString("ERROR:", 0, 0);
        LCD.drawString("", 0, 1);
        
        // Split long messages
        if (message.length() <= 16) {
            LCD.drawString(message, 0, 2);
        } else {
            LCD.drawString(message.substring(0, 16), 0, 2);
            if (message.length() > 16) {
                LCD.drawString(message.substring(16), 0, 3);
            }
        }
        
        LCD.drawString("", 0, 5);
        LCD.drawString("Press any key", 0, 6);
        LCD.refresh();
        Button.waitForAnyPress();
    }
    
    /**
     * Show exit screen
     */
    private static void showExitScreen() {
        LCD.clear();
        LCD.drawString("NEPO Dynamic", 0, 1);
        LCD.drawString("Runner", 0, 2);
        LCD.drawString("", 0, 3);
        LCD.drawString("Goodbye!", 0, 4);
        LCD.refresh();
        Delay.msDelay(2000);
    }
}
