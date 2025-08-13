import lejos.nxt.*;
import lejos.util.TextMenu;
import java.io.File;
import java.util.Vector;

/**
 * File Picker for NXT - Dynamic program selection
 * 
 * This class provides a user-friendly file selection interface
 * that runs directly on the NXT, allowing users to browse and
 * select XML programs dynamically.
 */
public class FilePicker {
    
    // Helper method for String operations not available in leJOS
    private static boolean stringEndsWith(String str, String suffix) {
        if (str.length() < suffix.length()) return false;
        return str.substring(str.length() - suffix.length()).equals(suffix);
    }

    /**
     * Show file picker and return selected filename
     * @param fileExtension Filter files by extension (e.g., ".xml")
     * @param title Title to display in the picker
     * @return Selected filename, or null if cancelled
     */
    public static String pickFile(String fileExtension, String title) {
        LCD.clear();
        LCD.drawString("Scanning files...", 0, 0);
        LCD.refresh();
        
        // Get all files from NXT flash memory
        File currentDir = new File(".");
        File[] allFiles = currentDir.listFiles();
        
        if (allFiles == null || allFiles.length == 0) {
            LCD.clear();
            LCD.drawString("No files found", 0, 0);
            LCD.drawString("Press any key", 0, 6);
            LCD.refresh();
            Button.waitForAnyPress();
            return null;
        }
        
        // Filter files by extension
        Vector filteredFiles = new Vector();
        for (int i = 0; i < allFiles.length; i++) {
            String filename = allFiles[i].getName();
            if (fileExtension == null || stringEndsWith(filename.toLowerCase(), fileExtension.toLowerCase())) {
                filteredFiles.addElement(filename);
            }
        }
        
        if (filteredFiles.size() == 0) {
            LCD.clear();
            LCD.drawString("No " + fileExtension + " files", 0, 0);
            LCD.drawString("found", 0, 1);
            LCD.drawString("Press any key", 0, 6);
            LCD.refresh();
            Button.waitForAnyPress();
            return null;
        }
        
        // Convert to array for TextMenu
        String[] fileNames = new String[filteredFiles.size()];
        for (int i = 0; i < filteredFiles.size(); i++) {
            fileNames[i] = (String) filteredFiles.elementAt(i);
        }

        // Show file selection menu
        LCD.clear();
        TextMenu menu = new TextMenu(fileNames, 1, title != null ? title : "Select File");
        
        int selection = menu.select();
        
        if (selection < 0) {
            return null; // User cancelled (pressed ESCAPE)
        }
        
        return fileNames[selection];
    }
    
    /**
     * Pick XML file specifically
     * @return Selected XML filename, or null if cancelled
     */
    public static String pickXMLFile() {
        return pickFile(".xml", "Select Program");
    }
    
    /**
     * Advanced file picker with file information
     * @param fileExtension Filter files by extension
     * @param title Title to display
     * @param showInfo Whether to show file size information
     * @return Selected filename, or null if cancelled
     */
    public static String pickFileAdvanced(String fileExtension, String title, boolean showInfo) {
        LCD.clear();
        LCD.drawString("Scanning files...", 0, 0);
        LCD.refresh();
        
        File currentDir = new File(".");
        File[] allFiles = currentDir.listFiles();
        
        if (allFiles == null || allFiles.length == 0) {
            showError("No files found");
            return null;
        }
        
        // Filter and collect file info
        Vector filteredFiles = new Vector();
        Vector fileSizes = new Vector();
        
        for (int i = 0; i < allFiles.length; i++) {
            String filename = allFiles[i].getName();
            if (fileExtension == null || stringEndsWith(filename.toLowerCase(), fileExtension.toLowerCase())) {
                filteredFiles.addElement(filename);
                fileSizes.addElement(new Long(allFiles[i].length()));
            }
        }
        
        if (filteredFiles.size() == 0) {
            showError("No " + fileExtension + " files found");
            return null;
        }
        
        // Create display names with file info
        String[] displayNames = new String[filteredFiles.size()];
        String[] fileNames = new String[filteredFiles.size()];
        
        for (int i = 0; i < filteredFiles.size(); i++) {
            fileNames[i] = (String) filteredFiles.elementAt(i);
            
            if (showInfo) {
                long size = ((Long) fileSizes.elementAt(i)).longValue();
                String sizeStr = formatFileSize(size);
                
                // Truncate filename if needed to fit size info
                String displayName = fileNames[i];
                if (displayName.length() > 10) {
                    displayName = displayName.substring(0, 7) + "...";
                }
                displayNames[i] = displayName + " " + sizeStr;
            } else {
                displayNames[i] = fileNames[i];
            }
        }
        
        // Show selection menu
        LCD.clear();
        TextMenu menu = new TextMenu(displayNames, 1, title != null ? title : "Select File");
        
        int selection = menu.select();
        
        if (selection < 0) {
            return null; // User cancelled
        }
        
        return fileNames[selection];
    }
    
    /**
     * Format file size for display
     */
    private static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        } else {
            return (bytes / 1024) + "K";
        }
    }
    
    /**
     * Show error message
     */
    private static void showError(String message) {
        LCD.clear();
        LCD.drawString("ERROR:", 0, 0);
        LCD.drawString(message, 0, 2);
        LCD.drawString("Press any key", 0, 6);
        LCD.refresh();
        Button.waitForAnyPress();
    }
    
    /**
     * Test the file picker
     */
    public static void main(String[] args) {
        LCD.clear();
        LCD.drawString("File Picker Test", 0, 0);
        LCD.refresh();
        
        String selectedFile = pickXMLFile();
        
        LCD.clear();
        if (selectedFile != null) {
            LCD.drawString("Selected:", 0, 0);
            LCD.drawString(selectedFile, 0, 1);
        } else {
            LCD.drawString("No file selected", 0, 0);
        }
        LCD.drawString("Press any key", 0, 6);
        LCD.refresh();
        Button.waitForAnyPress();
    }
}
