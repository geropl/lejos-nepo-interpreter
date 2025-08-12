import java.util.*;

/**
 * MockSound - Simulates NXT sound system for testing
 * 
 * Tracks tone generation, note playback, and sound operations
 * for validation in tests.
 */
public class MockSound {
    
    // Sound operation tracking
    private Vector soundHistory;       // History of sound operations
    private boolean isPlaying;         // Whether sound is currently playing
    private long lastOperationTime;    // Timestamp of last operation
    private int operationCount;        // Total number of sound operations
    
    // Current sound state
    private int currentFrequency;      // Current tone frequency (Hz)
    private int currentDuration;       // Current tone duration (ms)
    private String currentNote;        // Current musical note
    private long soundStartTime;       // When current sound started
    
    // Sound capabilities
    private boolean soundEnabled;      // Whether sound is enabled
    private int volume;               // Sound volume (0-100)
    
    /**
     * Initialize mock sound system
     */
    public MockSound() {
        soundHistory = new Vector();
        reset();
    }
    
    /**
     * Reset sound system to initial state
     */
    public void reset() {
        soundHistory.clear();
        isPlaying = false;
        lastOperationTime = System.currentTimeMillis();
        operationCount = 0;
        
        currentFrequency = 0;
        currentDuration = 0;
        currentNote = "";
        soundStartTime = 0;
        
        soundEnabled = true;
        volume = 100;
        
        logSound("RESET", "Sound system reset to initial state");
    }
    
    /**
     * Play tone with specified frequency and duration (simulates Sound.playTone())
     */
    public void playTone(int frequency, int duration) {
        if (!soundEnabled) {
            logSound("DISABLED", "Attempted to play tone while sound disabled");
            return;
        }
        
        // Validate parameters
        if (frequency < 0 || frequency > 20000) {
            logSound("ERROR", "Invalid frequency: " + frequency + " Hz");
            return;
        }
        
        if (duration < 0) {
            logSound("ERROR", "Invalid duration: " + duration + " ms");
            return;
        }
        
        currentFrequency = frequency;
        currentDuration = duration;
        currentNote = "";
        isPlaying = true;
        soundStartTime = System.currentTimeMillis();
        
        // Simulate sound playback
        simulateSoundPlayback(duration);
        
        logSound("PLAY_TONE", "Played tone " + frequency + "Hz for " + duration + "ms");
    }
    
    /**
     * Play musical note (simulates Sound.playNote())
     */
    public void playNote(String note, int duration) {
        if (!soundEnabled) {
            logSound("DISABLED", "Attempted to play note while sound disabled");
            return;
        }
        
        if (note == null || note.length() == 0) {
            logSound("ERROR", "Invalid note: " + note);
            return;
        }
        
        if (duration < 0) {
            logSound("ERROR", "Invalid duration: " + duration + " ms");
            return;
        }
        
        // Convert note to frequency (simplified mapping)
        int frequency = noteToFrequency(note);
        
        currentFrequency = frequency;
        currentDuration = duration;
        currentNote = note;
        isPlaying = true;
        soundStartTime = System.currentTimeMillis();
        
        // Simulate sound playback
        simulateSoundPlayback(duration);
        
        logSound("PLAY_NOTE", "Played note " + note + " (" + frequency + "Hz) for " + duration + "ms");
    }
    
    /**
     * Play beep sound (simulates Sound.beep())
     */
    public void beep() {
        playTone(1000, 200); // Standard beep: 1kHz for 200ms
        logSound("BEEP", "Played standard beep");
    }
    
    /**
     * Play two beeps (simulates Sound.twoBeeps())
     */
    public void twoBeeps() {
        playTone(1000, 150);
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        playTone(1000, 150);
        logSound("TWO_BEEPS", "Played two beeps");
    }
    
    /**
     * Play buzz sound (simulates Sound.buzz())
     */
    public void buzz() {
        playTone(100, 500); // Low frequency buzz
        logSound("BUZZ", "Played buzz sound");
    }
    
    /**
     * Set sound volume
     */
    public void setVolume(int volume) {
        this.volume = Math.max(0, Math.min(100, volume));
        logSound("SET_VOLUME", "Volume set to " + this.volume + "%");
    }
    
    /**
     * Enable/disable sound
     */
    public void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        if (!enabled && isPlaying) {
            stopSound();
        }
        logSound("SET_ENABLED", "Sound " + (enabled ? "enabled" : "disabled"));
    }
    
    /**
     * Stop current sound
     */
    public void stopSound() {
        if (isPlaying) {
            isPlaying = false;
            long playTime = System.currentTimeMillis() - soundStartTime;
            logSound("STOP", "Stopped sound after " + playTime + "ms");
        }
    }
    
    /**
     * Check if sound is currently playing
     */
    public boolean isPlaying() {
        // Update playing status based on elapsed time
        if (isPlaying && currentDuration > 0) {
            long elapsed = System.currentTimeMillis() - soundStartTime;
            if (elapsed >= currentDuration) {
                isPlaying = false;
            }
        }
        return isPlaying;
    }
    
    /**
     * Get current frequency
     */
    public int getCurrentFrequency() {
        return isPlaying ? currentFrequency : 0;
    }
    
    /**
     * Get current duration
     */
    public int getCurrentDuration() {
        return currentDuration;
    }
    
    /**
     * Get current note
     */
    public String getCurrentNote() {
        return currentNote;
    }
    
    /**
     * Get volume
     */
    public int getVolume() {
        return volume;
    }
    
    /**
     * Check if sound is enabled
     */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    /**
     * Get operation count
     */
    public int getOperationCount() {
        return operationCount;
    }
    
    /**
     * Get sound history
     */
    public Vector getHistory() {
        return new Vector(soundHistory); // Return copy
    }
    
    /**
     * Get sounds of specific type
     */
    public Vector getSoundsByType(String soundType) {
        Vector filtered = new Vector();
        for (int i = 0; i < soundHistory.size(); i++) {
            SoundOperation sound = (SoundOperation) soundHistory.elementAt(i);
            if (soundType.equals(sound.getOperation())) {
                filtered.addElement(sound);
            }
        }
        return filtered;
    }
    
    /**
     * Get current sound state
     */
    public SoundState getState() {
        return new SoundState(isPlaying, currentFrequency, currentDuration, 
                            currentNote, volume, soundEnabled, operationCount);
    }
    
    /**
     * Convert musical note to frequency (simplified)
     */
    private int noteToFrequency(String note) {
        // Simplified note to frequency mapping
        String upperNote = note.toUpperCase();
        
        if (upperNote.startsWith("C")) return 262;
        if (upperNote.startsWith("D")) return 294;
        if (upperNote.startsWith("E")) return 330;
        if (upperNote.startsWith("F")) return 349;
        if (upperNote.startsWith("G")) return 392;
        if (upperNote.startsWith("A")) return 440;
        if (upperNote.startsWith("B")) return 494;
        
        return 440; // Default to A4
    }
    
    /**
     * Simulate sound playback timing
     */
    private void simulateSoundPlayback(int duration) {
        // For testing, we don't actually wait the full duration
        // Just simulate a small delay to make it realistic
        int simulationDelay = Math.min(duration, 50); // Max 50ms delay
        
        try {
            Thread.sleep(simulationDelay);
        } catch (InterruptedException e) {
            // Ignore interruption
        }
    }
    
    /**
     * Log sound operation
     */
    private void logSound(String operation, String details) {
        lastOperationTime = System.currentTimeMillis();
        operationCount++;
        
        SoundOperation sound = new SoundOperation(operation, details, lastOperationTime,
                                                currentFrequency, currentDuration, currentNote);
        soundHistory.addElement(sound);
    }
    
    /**
     * Sound state snapshot
     */
    public static class SoundState {
        private boolean playing;
        private int frequency;
        private int duration;
        private String note;
        private int volume;
        private boolean enabled;
        private int operationCount;
        
        public SoundState(boolean playing, int frequency, int duration, String note,
                         int volume, boolean enabled, int operationCount) {
            this.playing = playing;
            this.frequency = frequency;
            this.duration = duration;
            this.note = note;
            this.volume = volume;
            this.enabled = enabled;
            this.operationCount = operationCount;
        }
        
        // Getters
        public boolean isPlaying() { return playing; }
        public int getFrequency() { return frequency; }
        public int getDuration() { return duration; }
        public String getNote() { return note; }
        public int getVolume() { return volume; }
        public boolean isEnabled() { return enabled; }
        public int getOperationCount() { return operationCount; }
        
        public String toString() {
            return "SoundState{playing=" + playing + ", freq=" + frequency + 
                   ", duration=" + duration + ", note='" + note + "', volume=" + volume + 
                   ", enabled=" + enabled + ", ops=" + operationCount + "}";
        }
        
        /**
         * Compare with expected state
         */
        public boolean matches(SoundState expected) {
            if (playing != expected.playing) return false;
            if (volume != expected.volume) return false;
            if (enabled != expected.enabled) return false;
            
            // Only check frequency/duration/note if playing
            if (playing) {
                if (frequency != expected.frequency) return false;
                if (duration != expected.duration) return false;
                if (!note.equals(expected.note)) return false;
            }
            
            return true;
        }
    }
    
    /**
     * Sound operation record
     */
    public static class SoundOperation {
        private String operation;
        private String details;
        private long timestamp;
        private int frequency;
        private int duration;
        private String note;
        
        public SoundOperation(String operation, String details, long timestamp,
                            int frequency, int duration, String note) {
            this.operation = operation;
            this.details = details;
            this.timestamp = timestamp;
            this.frequency = frequency;
            this.duration = duration;
            this.note = note;
        }
        
        public String getOperation() { return operation; }
        public String getDetails() { return details; }
        public long getTimestamp() { return timestamp; }
        public int getFrequency() { return frequency; }
        public int getDuration() { return duration; }
        public String getNote() { return note; }
        
        public String toString() {
            return "[" + timestamp + "] " + operation + ": " + details;
        }
    }
}
