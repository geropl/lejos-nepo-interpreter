import lejos.nxt.*;
import lejos.util.TextMenu;
import java.io.File;

/**
 * File Picker for leJOS NXT
 * 
 * This class provides a user-friendly file picker interface that runs directly
 * on the NXT brick, allowing users to browse and select NEPO XML files from
 * the flash file system without hardcoding filenames.
 * 
 * Features:
 * - Lists all .xml files in the NXT flash memory
 * - Scrollable menu interface using NXT buttons
 * - Returns selected filename for use by NEPO interpreter
 * - Handles empty directories gracefully
 * - Provides cancel option (ESC button)
 */
public class FilePicker {
    
    private static final String XML_EXTENSION = ".xml";
    private static final String TITLE = "Select NEPO Program";
    private static final String NO_FILES_MSG = "No XML files found";
    private static final String CANCEL_OPTION = "< Cancel >";
    
    /**
     * Check if string ends with suffix (leJOS NXT compatible)
     */
    private static boolean stringEndsWith(String str, String suffix) {
        if (str == null || suffix == null || suffix.length() > str.length()) {
            return false;
        }
        return str.substring(str.length() - suffix.length()).equals(suffix);
    }
    
    /**
     * Display file picker and return selected filename
     * 
     * @return Selected filename, or null if cancelled or no files found
     */
    public static String selectFile() {
        try {
            // Get list of all files from flash memory
            File[] allFiles = File.listFiles();
            
            if (allFiles == null || allFiles.length == 0) {
                showMessage(NO_FILES_MSG, "Press any key");
                Button.waitForAnyPress();
                return null;
            }
            
            // Filter for XML files
            String[] xmlFiles = filterXmlFiles(allFiles);
            
            if (xmlFiles.length == 0) {
                showMessage(NO_FILES_MSG, "Press any key");
                Button.waitForAnyPress();
                return null;
            }
            
            // Add cancel option
            String[] menuItems = new String[xmlFiles.length + 1];
            System.arraycopy(xmlFiles, 0, menuItems, 0, xmlFiles.length);
            menuItems[xmlFiles.length] = CANCEL_OPTION;
            
            // Create and display menu
            TextMenu menu = new TextMenu(menuItems, 1, TITLE);
            int selection = menu.select();
            
            // Handle selection
            if (selection == xmlFiles.length) {
                // Cancel selected
                return null;
            } else if (selection >= 0 && selection < xmlFiles.length) {
                return xmlFiles[selection];
            } else {
                // Invalid selection
                return null;
            }
            
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "Press any key");
            Button.waitForAnyPress();
            return null;
        }
    }
    
    /**
     * Filter files to include only XML files
     * 
     * @param allFiles Array of all files
     * @return Array of XML filenames
     */
    private static String[] filterXmlFiles(File[] allFiles) {
        // Count XML files first
        int xmlCount = 0;
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i] != null && 
                stringEndsWith(allFiles[i].getName().toLowerCase(), XML_EXTENSION)) {
                xmlCount++;
            }
        }
        
        // Create array of XML filenames
        String[] xmlFiles = new String[xmlCount];
        int index = 0;
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i] != null && 
                stringEndsWith(allFiles[i].getName().toLowerCase(), XML_EXTENSION)) {
                xmlFiles[index++] = allFiles[i].getName();
            }
        }
        
        return xmlFiles;
    }
    
    /**
     * Display a message on the LCD
     * 
     * @param line1 First line of message
     * @param line2 Second line of message
     */
    private static void showMessage(String line1, String line2) {
        LCD.clear();
        LCD.drawString(line1, 0, 2);
        LCD.drawString(line2, 0, 4);
        LCD.refresh();
    }
    
    /**
     * Pick XML file specifically
     */
    public static String pickXMLFile() {
        return selectFile();
    }
    
    /**
     * Advanced file picker with extension filtering and title
     */
    public static String pickFileAdvanced(String extension, String title, boolean showInfo) {
        if (showInfo) {
            return selectFileWithInfo();
        } else {
            return selectFile();
        }
    }
    
    /**
     * Enhanced file picker with file information display
     * 
     * @return Selected filename, or null if cancelled
     */
    public static String selectFileWithInfo() {
        try {
            File[] allFiles = File.listFiles();
            
            if (allFiles == null || allFiles.length == 0) {
                showMessage(NO_FILES_MSG, "Press any key");
                Button.waitForAnyPress();
                return null;
            }
            
            // Filter for XML files and keep File objects for info
            File[] xmlFileObjects = filterXmlFileObjects(allFiles);
            
            if (xmlFileObjects.length == 0) {
                showMessage(NO_FILES_MSG, "Press any key");
                Button.waitForAnyPress();
                return null;
            }
            
            // Create menu items with file sizes
            String[] menuItems = createMenuItemsWithInfo(xmlFileObjects);
            
            // Add cancel option
            String[] fullMenuItems = new String[menuItems.length + 1];
            System.arraycopy(menuItems, 0, fullMenuItems, 0, menuItems.length);
            fullMenuItems[menuItems.length] = CANCEL_OPTION;
            
            // Create and display menu
            TextMenu menu = new TextMenu(fullMenuItems, 1, TITLE);
            int selection = menu.select();
            
            // Handle selection
            if (selection == menuItems.length) {
                // Cancel selected
                return null;
            } else if (selection >= 0 && selection < menuItems.length) {
                return xmlFileObjects[selection].getName();
            } else {
                return null;
            }
            
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "Press any key");
            Button.waitForAnyPress();
            return null;
        }
    }
    
    /**
     * Filter files to include only XML File objects
     */
    private static File[] filterXmlFileObjects(File[] allFiles) {
        // Count XML files first
        int xmlCount = 0;
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i] != null && 
                stringEndsWith(allFiles[i].getName().toLowerCase(), XML_EXTENSION)) {
                xmlCount++;
            }
        }
        
        // Create array of XML File objects
        File[] xmlFiles = new File[xmlCount];
        int index = 0;
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i] != null && 
                stringEndsWith(allFiles[i].getName().toLowerCase(), XML_EXTENSION)) {
                xmlFiles[index++] = allFiles[i];
            }
        }
        
        return xmlFiles;
    }
    
    /**
     * Create menu items with file information
     */
    private static String[] createMenuItemsWithInfo(File[] xmlFiles) {
        String[] menuItems = new String[xmlFiles.length];
        
        for (int i = 0; i < xmlFiles.length; i++) {
            String name = xmlFiles[i].getName();
            long size = xmlFiles[i].length();
            
            // Truncate filename if too long and add size info
            if (name.length() > 10) {
                name = name.substring(0, 7) + "...";
            }
            
            menuItems[i] = name + " (" + size + "b)";
        }
        
        return menuItems;
    }
    
}
