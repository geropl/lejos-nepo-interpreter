import java.util.*;

/**
 * MockDisplay - Simulates NXT LCD display for testing
 * 
 * Tracks display content, cursor position, and all display operations
 * for validation in tests.
 */
public class MockDisplay {
    
    // Display dimensions (NXT LCD is 100x64 pixels, 16x8 characters)
    public static final int DISPLAY_WIDTH = 16;
    public static final int DISPLAY_HEIGHT = 8;
    
    // Display state
    private String[][] displayBuffer;  // Character buffer [line][column]
    private boolean isCleared;         // Whether display was recently cleared
    private int cursorX;              // Current cursor X position
    private int cursorY;              // Current cursor Y position
    
    // Operation tracking
    private Vector operationHistory;   // History of display operations
    private long lastOperationTime;    // Timestamp of last operation
    private int operationCount;        // Total number of operations
    private MockHardware hardware;     // Reference to main hardware for event logging
    
    /**
     * Initialize mock display
     */
    public MockDisplay() {
        displayBuffer = new String[DISPLAY_HEIGHT][DISPLAY_WIDTH];
        operationHistory = new Vector();
        reset();
    }
    
    /**
     * Set hardware reference for event logging
     */
    public void setHardware(MockHardware hardware) {
        this.hardware = hardware;
    }

    /**
     * Reset display to initial state
     */
    public void reset() {
        // Clear display buffer
        for (int line = 0; line < DISPLAY_HEIGHT; line++) {
            for (int col = 0; col < DISPLAY_WIDTH; col++) {
                displayBuffer[line][col] = " ";
            }
        }
        
        isCleared = true;
        cursorX = 0;
        cursorY = 0;
        lastOperationTime = System.currentTimeMillis();
        operationCount = 0;
        operationHistory.clear();
        
        logOperation("RESET", "Display reset to initial state");
    }
    
    /**
     * Clear display (simulates LCD.clear())
     */
    public void clear() {
        for (int line = 0; line < DISPLAY_HEIGHT; line++) {
            for (int col = 0; col < DISPLAY_WIDTH; col++) {
                displayBuffer[line][col] = " ";
            }
        }
        
        isCleared = true;
        cursorX = 0;
        cursorY = 0;
        
        logOperation("CLEAR", "Display cleared");
    }
    
    /**
     * Draw string at specified position (simulates LCD.drawString())
     */
    public void drawString(String text, int x, int y) {
        if (text == null) text = "";
        
        // Validate coordinates
        if (y < 0 || y >= DISPLAY_HEIGHT) {
            logOperation("ERROR", "Invalid Y coordinate: " + y);
            return;
        }
        
        if (x < 0) {
            logOperation("ERROR", "Invalid X coordinate: " + x);
            return;
        }
        
        // Clear the line first (NXT behavior)
        for (int col = 0; col < DISPLAY_WIDTH; col++) {
            displayBuffer[y][col] = " ";
        }
        
        // Draw the text
        int textLength = Math.min(text.length(), DISPLAY_WIDTH - x);
        for (int i = 0; i < textLength; i++) {
            if (x + i < DISPLAY_WIDTH) {
                displayBuffer[y][x + i] = String.valueOf(text.charAt(i));
            }
        }
        
        cursorX = x + textLength;
        cursorY = y;
        isCleared = false;
        
        logOperation("DRAW_STRING", "Drew '" + text + "' at (" + x + "," + y + ")");
    }
    
    /**
     * Draw integer at specified position
     */
    public void drawInt(int value, int x, int y) {
        drawString(String.valueOf(value), x, y);
    }
    
    /**
     * Draw integer with specified width
     */
    public void drawInt(int value, int width, int x, int y) {
        String text = String.valueOf(value);
        
        // Pad with spaces if needed
        while (text.length() < width) {
            text = " " + text;
        }
        
        drawString(text, x, y);
    }
    
    /**
     * Refresh display (simulates LCD.refresh())
     */
    public void refresh() {
        // In real NXT, this updates the physical display
        // In mock, we just log the operation
        logOperation("REFRESH", "Display refreshed");
    }
    
    /**
     * Get display content as string array (one string per line)
     */
    public String[] getDisplayLines() {
        String[] lines = new String[DISPLAY_HEIGHT];
        for (int line = 0; line < DISPLAY_HEIGHT; line++) {
            StringBuffer lineBuffer = new StringBuffer();
            for (int col = 0; col < DISPLAY_WIDTH; col++) {
                lineBuffer.append(displayBuffer[line][col]);
            }
            lines[line] = lineBuffer.toString();
        }
        return lines;
    }
    
    /**
     * Get display content as single string
     */
    public String getContent() {
        StringBuffer content = new StringBuffer();
        String[] lines = getDisplayLines();
        for (int i = 0; i < lines.length; i++) {
            content.append(lines[i]);
            if (i < lines.length - 1) {
                content.append("\n");
            }
        }
        return content.toString();
    }
    
    /**
     * Get specific line content
     */
    public String getLine(int lineNumber) {
        if (lineNumber < 0 || lineNumber >= DISPLAY_HEIGHT) {
            return "";
        }
        return getDisplayLines()[lineNumber];
    }
    
    /**
     * Check if display is cleared
     */
    public boolean isCleared() {
        return isCleared;
    }
    
    /**
     * Get cursor position
     */
    public int getCursorX() {
        return cursorX;
    }
    
    public int getCursorY() {
        return cursorY;
    }
    
    /**
     * Get operation count
     */
    public int getOperationCount() {
        return operationCount;
    }
    
    /**
     * Get operation history
     */
    public Vector getOperationHistory() {
        return new Vector(operationHistory); // Return copy
    }
    
    /**
     * Get current display state
     */
    public DisplayState getState() {
        return new DisplayState(getDisplayLines(), isCleared, cursorX, cursorY, 
                              operationCount, lastOperationTime);
    }
    
    /**
     * Check if display contains specific text
     */
    public boolean containsText(String text) {
        String content = getContent();
        return content.indexOf(text) != -1;
    }
    
    /**
     * Check if specific line contains text
     */
    public boolean lineContainsText(int lineNumber, String text) {
        String line = getLine(lineNumber);
        return line.indexOf(text) != -1;
    }
    
    /**
     * Get display as visual representation (for debugging)
     */
    public String getVisualRepresentation() {
        StringBuffer visual = new StringBuffer();
        visual.append("+").append(repeatChar('-', DISPLAY_WIDTH)).append("+\n");
        
        String[] lines = getDisplayLines();
        for (int i = 0; i < lines.length; i++) {
            visual.append("|").append(lines[i]).append("|\n");
        }
        
        visual.append("+").append(repeatChar('-', DISPLAY_WIDTH)).append("+");
        return visual.toString();
    }
    
    /**
     * Helper method to repeat character
     */
    private String repeatChar(char c, int count) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < count; i++) {
            buffer.append(c);
        }
        return buffer.toString();
    }
    
    /**
     * Log display operation
     */
    private void logOperation(String operation, String details) {
        lastOperationTime = System.currentTimeMillis();
        operationCount++;
        
        DisplayOperation op = new DisplayOperation(operation, details, 
                                                 lastOperationTime, getState());
        operationHistory.addElement(op);
        
        // Also log to main hardware event system
        if (hardware != null) {
            hardware.logEvent("DISPLAY", "Display " + operation.toLowerCase() + ": " + details);
        }
    }

    /**
     * Display state snapshot
     */
    public static class DisplayState {
        private String[] lines;
        private boolean cleared;
        private int cursorX;
        private int cursorY;
        private int operationCount;
        private long timestamp;
        
        public DisplayState(String[] lines, boolean cleared, int cursorX, int cursorY,
                          int operationCount, long timestamp) {
            this.lines = new String[lines.length];
            System.arraycopy(lines, 0, this.lines, 0, lines.length);
            this.cleared = cleared;
            this.cursorX = cursorX;
            this.cursorY = cursorY;
            this.operationCount = operationCount;
            this.timestamp = timestamp;
        }
        
        // Getters
        public String[] getLines() { return lines; }
        public boolean isCleared() { return cleared; }
        public int getCursorX() { return cursorX; }
        public int getCursorY() { return cursorY; }
        public int getOperationCount() { return operationCount; }
        public long getTimestamp() { return timestamp; }
        
        public String getLine(int lineNumber) {
            if (lineNumber >= 0 && lineNumber < lines.length) {
                return lines[lineNumber];
            }
            return "";
        }
        
        public String getContent() {
            StringBuffer content = new StringBuffer();
            for (int i = 0; i < lines.length; i++) {
                content.append(lines[i]);
                if (i < lines.length - 1) {
                    content.append("\n");
                }
            }
            return content.toString();
        }
        
        public String toString() {
            return "DisplayState{cleared=" + cleared + ", cursor=(" + cursorX + "," + cursorY + 
                   "), operations=" + operationCount + "}";
        }
        
        /**
         * Compare with expected state
         */
        public boolean matches(DisplayState expected) {
            if (cleared != expected.cleared) return false;
            if (cursorX != expected.cursorX) return false;
            if (cursorY != expected.cursorY) return false;
            
            // Compare line content
            if (lines.length != expected.lines.length) return false;
            for (int i = 0; i < lines.length; i++) {
                if (!lines[i].equals(expected.lines[i])) return false;
            }
            
            return true;
        }
    }
    
    /**
     * Display operation record
     */
    public static class DisplayOperation {
        private String operation;
        private String details;
        private long timestamp;
        private DisplayState stateBefore;
        
        public DisplayOperation(String operation, String details, long timestamp,
                              DisplayState state) {
            this.operation = operation;
            this.details = details;
            this.timestamp = timestamp;
            this.stateBefore = state;
        }
        
        public String getOperation() { return operation; }
        public String getDetails() { return details; }
        public long getTimestamp() { return timestamp; }
        public DisplayState getStateBefore() { return stateBefore; }
        
        public String toString() {
            return "[" + timestamp + "] " + operation + ": " + details;
        }
    }
}
