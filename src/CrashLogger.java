import lejos.nxt.*;
import java.io.*;
import java.util.Date;

/**
 * Simple Crash Logger for NXT
 * 
 * Provides:
 * 1. Useful error messages on NXT screen (context, exception type, message)
 * 2. Complete stack traces written to crash.log file
 * 
 * Use nxjcontrol to easily transfer crash.log to PC for analysis.
 */
public class CrashLogger {
    
    private static final String CRASH_LOG_FILE = "crash.log";
    private static int crashCount = 0;
    
    /**
     * Handle exception with useful screen display and complete file logging
     */
    public static void handleException(Exception e) {
        crashCount++;
        
        // Write complete details to file
        boolean success = writeToFile(e);
        
        // Show useful info on screen
        showErrorOnScreen(e, success);
    }
    
    /**
     * Write complete crash details to file
     */
    private static boolean writeToFile(Exception e) {
        try (FileOutputStream fos = new FileOutputStream(new File(CRASH_LOG_FILE), true); PrintStream ps = new PrintStream(fos)) {
            // Write crash header
            ps.println("=====================================");
            ps.println("CRASH #" + crashCount + " - " + getCurrentTimeString());
            ps.println("=====================================");
            
            // Write exception details
            ps.println("Exception: " + e.getClass().toString());
            if (e.getMessage() != null) {
                ps.println("Message: " + e.getMessage());
            }
            ps.println();
            
            // Write full stack trace
            ps.println("Stack Trace:");
            e.printStackTrace(ps);
            ps.println();
            
            // Write system information
            writeSystemInfo(ps);
            
            ps.println("=====================================");
            ps.println();

            return true;
        } catch (IOException ioException) {
            // If file writing fails, we'll show this in the screen display
            return false;
        }
    }
    
    /**
     * Show useful error information on NXT screen
     */
    private static void showErrorOnScreen(Exception e, boolean writeCrashSuccess) {
        LCD.clear();
        
        // Line 0: ERROR indicator
        LCD.drawString("ERROR #" + crashCount, 0, 0);
        
        // Line 1: Exception type (simplified)
        String exceptionName = getSimpleClassName(e.getClass().toString());
        LCD.drawString(exceptionName, 0, 1);
        
        // Line 2: Error message (if available and short)
        String message = e.getMessage();
        if (message != null && message.length() > 0) {
            if (message.length() > 16) {
                message = message.substring(0, 13) + "...";
            }
            LCD.drawString(message, 0, 2);
        }

        // Line 3: Empty
        // Line 4: Empty
        
        // Line 5-6: Instructions
        if (writeCrashSuccess) {
            LCD.drawString("Logged to", 0, 5);
            LCD.drawString("crash.log", 0, 6);
        } else {
            LCD.drawString("Error while", 0, 5);
            LCD.drawString("writing crash.log", 0, 6);
        }
        
        // Line 7: Continue instruction
        LCD.drawString("Press any key", 0, 7);
        
        LCD.refresh();
        Button.waitForAnyPress();
    }
    
    /**
     * Extract simple class name from full class string
     */
    private static String getSimpleClassName(String fullClassName) {
        int lastDot = fullClassName.lastIndexOf('.');
        String simpleName = (lastDot >= 0) ? fullClassName.substring(lastDot + 1) : fullClassName;
        
        // Remove "class " prefix if present
        if (simpleName.startsWith("class ")) {
            simpleName = simpleName.substring(6);
        }
        
        // Truncate if too long
        if (simpleName.length() > 16) {
            simpleName = simpleName.substring(0, 13) + "...";
        }
        
        return simpleName;
    }
    
    /**
     * Write system information to log
     */
    private static void writeSystemInfo(PrintStream ps) {
        ps.println("System Information:");
        
        try {
            Runtime runtime = Runtime.getRuntime();
            ps.println("  Total Memory: " + runtime.totalMemory() + " bytes");
            ps.println("  Free Memory: " + runtime.freeMemory() + " bytes");
            ps.println("  Used Memory: " + (runtime.totalMemory() - runtime.freeMemory()) + " bytes");
        } catch (Exception e) {
            ps.println("  Memory info unavailable: " + e.getMessage());
        }
        
        try {
            ps.println("  Java Version: " + System.getProperty("java.version"));
            ps.println("  Java Vendor: " + System.getProperty("java.vendor"));
        } catch (Exception e) {
            ps.println("  Java info unavailable: " + e.getMessage());
        }
        
        ps.println();
    }
    
    /**
     * Get current time as string (simple format since Date formatting is limited)
     */
    private static String getCurrentTimeString() {
        try {
            return new Date().toString();
        } catch (Exception e) {
            return "Time unavailable";
        }
    }
}
