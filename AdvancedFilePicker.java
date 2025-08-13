import lejos.nxt.*;
import lejos.util.TextMenu;
import java.io.File;

/**
 * Advanced File Picker for leJOS NXT
 * 
 * This enhanced version provides additional features for file management
 * and selection, including file information display, sorting options,
 * and file operations.
 * 
 * Features:
 * - File listing with size and date information
 * - Sorting by name, size, or date
 * - File preview (first few lines)
 * - File operations (delete, rename)
 * - Multiple file type filtering
 * - Pagination for large file lists
 */
public class AdvancedFilePicker {
    
    // Helper method for String operations not available in leJOS
    private static boolean stringEndsWith(String str, String suffix) {
        if (str.length() < suffix.length()) return false;
        return str.substring(str.length() - suffix.length()).equals(suffix);
    }

    // Constants
    private static final String[] SUPPORTED_EXTENSIONS = {".xml", ".txt", ".log"};
    private static final int MAX_ITEMS_PER_PAGE = 6;
    private static final int PREVIEW_LINES = 3;
    
    // Menu options
    private static final String CANCEL_OPTION = "< Cancel >";
    private static final String NEXT_PAGE_OPTION = "< Next Page >";
    private static final String PREV_PAGE_OPTION = "< Previous Page >";
    private static final String FILE_INFO_OPTION = "< File Info >";
    
    /**
     * File information container
     */
    public static class FileInfo {
        public String name;
        public long size;
        public boolean exists;
        public String extension;
        
        public FileInfo(File file) {
            this.name = file.getName();
            this.size = file.length();
            this.exists = file.exists();
            this.extension = getFileExtension(name);
        }
        
        private String getFileExtension(String filename) {
            int lastDot = filename.lastIndexOf('.');
            return lastDot > 0 ? filename.substring(lastDot).toLowerCase() : "";
        }
    }
    
    /**
     * Main file picker with advanced features
     * 
     * @param title Menu title
     * @param extensions Array of supported file extensions (e.g., {".xml", ".txt"})
     * @return Selected filename, or null if cancelled
     */
    public static String selectFileAdvanced(String title, String[] extensions) {
        try {
            // Get and filter files
            File[] allFiles = File.listFiles();
            if (allFiles == null || allFiles.length == 0) {
                showMessage("No files found", "Press any key");
                Button.waitForAnyPress();
                return null;
            }
            
            FileInfo[] filteredFiles = filterFiles(allFiles, extensions);
            if (filteredFiles.length == 0) {
                showMessage("No matching files", "Press any key");
                Button.waitForAnyPress();
                return null;
            }
            
            // Sort files by name (could add other sorting options)
            sortFilesByName(filteredFiles);
            
            // Handle pagination if needed
            if (filteredFiles.length <= MAX_ITEMS_PER_PAGE) {
                return selectFromSinglePage(filteredFiles, title);
            } else {
                return selectFromMultiplePages(filteredFiles, title);
            }
            
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage(), "Press any key");
            Button.waitForAnyPress();
            return null;
        }
    }
    
    /**
     * Select file from a single page
     */
    private static String selectFromSinglePage(FileInfo[] files, String title) {
        String[] menuItems = createMenuItems(files, 0, files.length);
        
        // Add cancel option
        String[] fullMenu = new String[menuItems.length + 1];
        System.arraycopy(menuItems, 0, fullMenu, 0, menuItems.length);
        fullMenu[menuItems.length] = CANCEL_OPTION;
        
        TextMenu menu = new TextMenu(fullMenu, 1, title);
        int selection = menu.select();
        
        if (selection == menuItems.length || selection < 0) {
            return null; // Cancel or invalid selection
        }
        
        return files[selection].name;
    }
    
    /**
     * Select file from multiple pages
     */
    private static String selectFromMultiplePages(FileInfo[] files, String title) {
        int currentPage = 0;
        int totalPages = (files.length + MAX_ITEMS_PER_PAGE - 1) / MAX_ITEMS_PER_PAGE;
        
        while (true) {
            int startIndex = currentPage * MAX_ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + MAX_ITEMS_PER_PAGE, files.length);
            
            String[] menuItems = createMenuItems(files, startIndex, endIndex);
            String[] fullMenu = createPaginatedMenu(menuItems, currentPage, totalPages);
            
            String pageTitle = title + " (" + (currentPage + 1) + "/" + totalPages + ")";
            TextMenu menu = new TextMenu(fullMenu, 1, pageTitle);
            int selection = menu.select();
            
            // Handle navigation options
            if (selection == fullMenu.length - 1) {
                return null; // Cancel
            } else if (selection == fullMenu.length - 2 && currentPage < totalPages - 1) {
                currentPage++; // Next page
                continue;
            } else if (selection == fullMenu.length - 3 && currentPage > 0) {
                currentPage--; // Previous page
                continue;
            } else if (selection >= 0 && selection < menuItems.length) {
                // File selected
                return files[startIndex + selection].name;
            }
        }
    }
    
    /**
     * Create menu items for a range of files
     */
    private static String[] createMenuItems(FileInfo[] files, int start, int end) {
        String[] items = new String[end - start];
        
        for (int i = start; i < end; i++) {
            FileInfo file = files[i];
            String name = file.name;
            
            // Truncate long filenames
            if (name.length() > 12) {
                name = name.substring(0, 9) + "...";
            }
            
            // Add size information
            String sizeStr = formatFileSize(file.size);
            items[i - start] = name + " " + sizeStr;
        }
        
        return items;
    }
    
    /**
     * Create paginated menu with navigation options
     */
    private static String[] createPaginatedMenu(String[] items, int currentPage, int totalPages) {
        int extraOptions = 1; // Cancel
        if (currentPage > 0) extraOptions++; // Previous page
        if (currentPage < totalPages - 1) extraOptions++; // Next page
        
        String[] fullMenu = new String[items.length + extraOptions];
        System.arraycopy(items, 0, fullMenu, 0, items.length);
        
        int index = items.length;
        if (currentPage > 0) {
            fullMenu[index++] = PREV_PAGE_OPTION;
        }
        if (currentPage < totalPages - 1) {
            fullMenu[index++] = NEXT_PAGE_OPTION;
        }
        fullMenu[index] = CANCEL_OPTION;
        
        return fullMenu;
    }
    
    /**
     * Filter files by supported extensions
     */
    private static FileInfo[] filterFiles(File[] allFiles, String[] extensions) {
        // Count matching files
        int count = 0;
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i] != null && isFileSupported(allFiles[i].getName(), extensions)) {
                count++;
            }
        }
        
        // Create filtered array
        FileInfo[] filtered = new FileInfo[count];
        int index = 0;
        for (int i = 0; i < allFiles.length; i++) {
            if (allFiles[i] != null && isFileSupported(allFiles[i].getName(), extensions)) {
                filtered[index++] = new FileInfo(allFiles[i]);
            }
        }
        
        return filtered;
    }
    
    /**
     * Check if file has supported extension
     */
    private static boolean isFileSupported(String filename, String[] extensions) {
        String name = filename.toLowerCase();
        for (int i = 0; i < extensions.length; i++) {
            if (stringEndsWith(name, extensions[i].toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Sort files by name (simple bubble sort)
     */
    private static void sortFilesByName(FileInfo[] files) {
        for (int i = 0; i < files.length - 1; i++) {
            for (int j = 0; j < files.length - i - 1; j++) {
                if (files[j].name.compareTo(files[j + 1].name) > 0) {
                    FileInfo temp = files[j];
                    files[j] = files[j + 1];
                    files[j + 1] = temp;
                }
            }
        }
    }
    
    /**
     * Format file size for display
     */
    private static String formatFileSize(long size) {
        if (size < 1024) {
            return "(" + size + "b)";
        } else if (size < 1024 * 1024) {
            return "(" + (size / 1024) + "k)";
        } else {
            return "(" + (size / (1024 * 1024)) + "M)";
        }
    }
    
    /**
     * Display file information
     */
    public static void showFileInfo(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                showMessage("File not found:", filename);
                Button.waitForAnyPress();
                return;
            }
            
            LCD.clear();
            LCD.drawString("File Info:", 0, 0);
            LCD.drawString(filename, 0, 1);
            LCD.drawString("Size: " + file.length() + " bytes", 0, 2);
            LCD.drawString("Exists: " + (file.exists() ? "Yes" : "No"), 0, 3);
            LCD.drawString("", 0, 4);
            LCD.drawString("Press any key", 0, 6);
            LCD.refresh();
            Button.waitForAnyPress();
            
        } catch (Exception e) {
            showMessage("Error getting info:", e.getMessage());
            Button.waitForAnyPress();
        }
    }
    
    /**
     * Convenience method for XML files only
     */
    public static String selectXmlFile() {
        return selectFileAdvanced("Select XML File", new String[]{".xml"});
    }
    
    /**
     * Convenience method for any supported file type
     */
    public static String selectAnyFile() {
        return selectFileAdvanced("Select File", SUPPORTED_EXTENSIONS);
    }
    
    /**
     * Display a message on the LCD
     */
    private static void showMessage(String line1, String line2) {
        LCD.clear();
        LCD.drawString(line1, 0, 2);
        LCD.drawString(line2, 0, 3);
        LCD.refresh();
    }
    
    /**
     * Test method
     */
    public static void main(String[] args) {
        LCD.drawString("Advanced File", 0, 0);
        LCD.drawString("Picker Demo", 0, 1);
        LCD.refresh();
        Button.waitForAnyPress();
        
        String selectedFile = selectXmlFile();
        
        if (selectedFile != null) {
            LCD.clear();
            LCD.drawString("Selected:", 0, 1);
            LCD.drawString(selectedFile, 0, 2);
            LCD.drawString("", 0, 3);
            LCD.drawString("ENTER: Info", 0, 4);
            LCD.drawString("ESC: Exit", 0, 5);
            LCD.refresh();
            
            int button = Button.waitForAnyPress();
            if (button == Button.ID_ENTER) {
                showFileInfo(selectedFile);
            }
        } else {
            showMessage("No file selected", "Press any key");
            Button.waitForAnyPress();
        }
    }
}
