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
        LCD.drawString("Select Program:", 0, 0);
        LCD.drawString("", 0, 1);
        LCD.drawString("1. Simple Picker", 0, 2);
        LCD.drawString("2. Advanced Picker", 0, 3);
        LCD.drawString("3. Exit", 0, 4);
        LCD.drawString("", 0, 5);
        LCD.drawString("Use UP/DOWN/ENTER", 0, 6);
        LCD.refresh();
        
        int choice = 1;
        while (true) {
            // Highlight current choice
            LCD.drawString(choice == 1 ? ">" : " ", 0, 2);
            LCD.drawString(choice == 2 ? ">" : " ", 0, 3);
            LCD.drawString(choice == 3 ? ">" : " ", 0, 4);
            LCD.refresh();
            
            int button = Button.waitForAnyPress();
            
            switch (button) {
                case Button.ID_UP:
                    choice = (choice > 1) ? choice - 1 : 3;
                    break;
                case Button.ID_DOWN:
                    choice = (choice < 3) ? choice + 1 : 1;
                    break;
                case Button.ID_ENTER:
                    switch (choice) {
                        case 1:
                            return FilePicker.pickXMLFile();
                        case 2:
                            return AdvancedFilePicker.selectXmlFile();
                        case 3:
                            return null;
                    }
                    break;
                case Button.ID_ESCAPE:
                    return null;
            }
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
        
        try {
            // Parse the XML program
            SimpleXMLParser.XMLElement root = SimpleXMLParser.parseFile(filename);
            if (root == null) {
                showError("Failed to parse XML file");
                return;
            }
            
            LCD.drawString("XML parsed OK", 0, 4);
            LCD.refresh();
            Delay.msDelay(1000);
            
            // Find the start block
            SimpleXMLParser.XMLElement startBlock = findStartBlock(root);
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
            
        } catch (Exception e) {
            showError("Runtime error: " + e.getMessage());
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
