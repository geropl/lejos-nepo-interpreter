/**
 * MockHardwareTest - Basic test runner to validate MockHardware functionality
 * 
 * This test validates that the mock hardware components work correctly
 * before using them for NEPO block testing.
 */
public class MockHardwareTest {
    
    private MockHardware hardware;
    private int testsPassed;
    private int testsFailed;
    
    public static void main(String[] args) {
        MockHardwareTest test = new MockHardwareTest();
        test.runAllTests();
    }
    
    /**
     * Run all mock hardware tests
     */
    public void runAllTests() {
        System.out.println("=== MockHardware Validation Tests ===");
        System.out.println();
        
        testsPassed = 0;
        testsFailed = 0;
        
        // Initialize hardware
        hardware = new MockHardware();
        
        // Run test suites
        testMotorSimulation();
        testSensorSimulation();
        testDisplaySimulation();
        testSoundSimulation();
        testTimerSimulation();
        testEventTracking();
        testStateValidation();
        
        // Print results
        System.out.println();
        System.out.println("=== Test Results ===");
        System.out.println("Tests Passed: " + testsPassed);
        System.out.println("Tests Failed: " + testsFailed);
        System.out.println("Total Tests: " + (testsPassed + testsFailed));
        
        if (testsFailed == 0) {
            System.out.println("✅ All tests PASSED! MockHardware is ready for use.");
        } else {
            System.out.println("❌ Some tests FAILED. Check implementation.");
        }
    }
    
    /**
     * Test motor simulation functionality
     */
    private void testMotorSimulation() {
        System.out.println("--- Testing Motor Simulation ---");
        
        // Test motor retrieval
        MockMotor motorA = hardware.getMotor("A");
        MockMotor motorB = hardware.getMotor("B");
        MockMotor motorC = hardware.getMotor("C");
        
        assertTrue("Motor A should not be null", motorA != null);
        assertTrue("Motor B should not be null", motorB != null);
        assertTrue("Motor C should not be null", motorC != null);
        
        // Test invalid motor port
        MockMotor invalidMotor = hardware.getMotor("X");
        assertTrue("Invalid motor should be null", invalidMotor == null);
        
        // Test motor operations
        motorB.setSpeed(360);
        assertEquals("Motor speed should be 360", 360, motorB.getSpeed());
        
        motorB.forward();
        assertTrue("Motor should be moving", motorB.isMoving());
        
        motorB.stop();
        assertFalse("Motor should not be moving after stop", motorB.isMoving());
        
        // Test rotation
        int initialRotation = motorB.getTachoCount();
        motorB.rotate(360);
        int finalRotation = motorB.getTachoCount();
        assertTrue("Motor should have rotated", finalRotation != initialRotation);
        
        System.out.println("Motor simulation tests completed.");
    }
    
    /**
     * Test sensor simulation functionality
     */
    private void testSensorSimulation() {
        System.out.println("--- Testing Sensor Simulation ---");
        
        // Test sensor retrieval
        MockSensor sensor1 = hardware.getSensor("1");
        MockSensor sensor2 = hardware.getSensor("2");
        
        assertTrue("Sensor 1 should not be null", sensor1 != null);
        assertTrue("Sensor 2 should not be null", sensor2 != null);
        
        // Test touch sensor
        sensor1.configureTouchSensor();
        assertEquals("Sensor should be touch type", "touch", sensor1.getSensorType());
        
        sensor1.setTouchPressed(true);
        assertTrue("Touch sensor should be pressed", sensor1.isPressed());
        
        sensor1.setTouchPressed(false);
        assertFalse("Touch sensor should not be pressed", sensor1.isPressed());
        
        // Test ultrasonic sensor
        sensor2.configureUltrasonicSensor();
        assertEquals("Sensor should be ultrasonic type", "ultrasonic", sensor2.getSensorType());
        
        sensor2.setUltrasonicDistance(50);
        assertEquals("Ultrasonic distance should be 50", 50, sensor2.getDistance());
        
        // Test light sensor
        sensor1.configureLightSensor();
        sensor1.setLightLevel(75);
        assertEquals("Light level should be 75", 75, sensor1.getLightLevel());
        
        System.out.println("Sensor simulation tests completed.");
    }
    
    /**
     * Test display simulation functionality
     */
    private void testDisplaySimulation() {
        System.out.println("--- Testing Display Simulation ---");
        
        MockDisplay display = hardware.getDisplay();
        assertTrue("Display should not be null", display != null);
        
        // Test display clear
        display.clear();
        assertTrue("Display should be cleared", display.isCleared());
        
        // Test text drawing
        display.drawString("Hello", 0, 0);
        assertTrue("Display should contain 'Hello'", display.containsText("Hello"));
        assertEquals("Line 0 should contain 'Hello'", "Hello           ", display.getLine(0));
        
        // Test multiple lines
        display.drawString("World", 0, 1);
        assertTrue("Display should contain 'World'", display.containsText("World"));
        
        // Test display state
        MockDisplay.DisplayState state = display.getState();
        assertFalse("Display should not be cleared after drawing", state.isCleared());
        assertTrue("Operation count should be > 0", state.getOperationCount() > 0);

        System.out.println("Display simulation tests completed.");
    }
    
    /**
     * Test sound simulation functionality
     */
    private void testSoundSimulation() {
        System.out.println("--- Testing Sound Simulation ---");
        
        MockSound sound = hardware.getSound();
        assertTrue("Sound should not be null", sound != null);
        
        // Test tone playback
        sound.playTone(440, 500);
        assertEquals("Current frequency should be 440", 440, sound.getCurrentFrequency());
        assertEquals("Current duration should be 500", 500, sound.getCurrentDuration());
        
        // Test note playback
        sound.playNote("A", 300);
        assertEquals("Current note should be 'A'", "A", sound.getCurrentNote());
        
        // Test volume
        sound.setVolume(75);
        assertEquals("Volume should be 75", 75, sound.getVolume());
        
        // Test sound history
        java.util.Vector history = sound.getHistory();
        assertTrue("Sound history should not be empty", history.size() > 0);
        
        System.out.println("Sound simulation tests completed.");
    }
    
    /**
     * Test timer simulation functionality
     */
    private void testTimerSimulation() {
        System.out.println("--- Testing Timer Simulation ---");
        
        MockTimer timer = hardware.getTimer();
        assertTrue("Timer should not be null", timer != null);
        
        // Test timer reset
        timer.reset();
        assertTrue("Timer should be running after reset", timer.isRunning());
        assertFalse("Timer should not be paused after reset", timer.isPaused());
        
        // Test timer value (should be small since just reset)
        long initialValue = timer.getValue();
        assertTrue("Timer value should be >= 0", initialValue >= 0);
        
        // Simulate time passage
        timer.simulateTimePassage(1000);
        long newValue = timer.getValue();
        assertTrue("Timer value should increase", newValue > initialValue);
        
        // Test pause/resume
        timer.pause();
        assertTrue("Timer should be paused", timer.isPaused());
        
        timer.resume();
        assertFalse("Timer should not be paused after resume", timer.isPaused());
        
        System.out.println("Timer simulation tests completed.");
    }
    
    /**
     * Test event tracking functionality
     */
    private void testEventTracking() {
        System.out.println("--- Testing Event Tracking ---");
        
        // Reset hardware to start fresh
        hardware.reset();
        
        // Perform some operations
        MockMotor motor = hardware.getMotor("A");
        motor.setSpeed(360);
        motor.forward();
        motor.stop();
        
        MockSensor sensor = hardware.getSensor("1");
        sensor.configureTouchSensor();
        sensor.setTouchPressed(true);
        
        MockDisplay display = hardware.getDisplay();
        display.drawString("Test", 0, 0);
        
        // Check event history
        java.util.Vector events = hardware.getEventHistory();
        assertTrue("Event history should not be empty", events.size() > 0);
        
        // Check for specific event types
        java.util.Vector motorEvents = hardware.getEventsByType("MOTOR");
        java.util.Vector displayEvents = hardware.getEventsByType("DISPLAY");
        
        // Debug: print all event types
        System.out.println("Debug - All events:");
        for (int i = 0; i < events.size(); i++) {
            MockHardware.HardwareEvent event = (MockHardware.HardwareEvent) events.elementAt(i);
            System.out.println("  " + event.getType() + ": " + event.getDescription());
        }
        
        assertTrue("Should have motor events (found " + motorEvents.size() + ")", motorEvents.size() > 0);
        assertTrue("Should have display events (found " + displayEvents.size() + ")", displayEvents.size() > 0);

        System.out.println("Event tracking tests completed.");
    }
    
    /**
     * Test state validation functionality
     */
    private void testStateValidation() {
        System.out.println("--- Testing State Validation ---");
        
        // Get current hardware state
        MockHardware.HardwareState state = hardware.getCurrentState();
        assertTrue("Hardware state should not be null", state != null);
        
        // Test state components
        java.util.Hashtable motorStates = state.getMotorStates();
        assertTrue("Motor states should not be null", motorStates != null);
        assertTrue("Should have motor states", motorStates.size() > 0);
        
        java.util.Hashtable sensorStates = state.getSensorStates();
        assertTrue("Sensor states should not be null", sensorStates != null);
        assertTrue("Should have sensor states", sensorStates.size() > 0);
        
        // Test execution stats
        MockHardware.ExecutionStats stats = hardware.getStats();
        assertTrue("Execution stats should not be null", stats != null);
        assertTrue("Should have total events", stats.getTotalEvents() > 0);
        
        System.out.println("State validation tests completed.");
    }
    
    /**
     * Assert that condition is true
     */
    private void assertTrue(String message, boolean condition) {
        if (condition) {
            testsPassed++;
            System.out.println("✅ " + message);
        } else {
            testsFailed++;
            System.out.println("❌ " + message);
        }
    }
    
    /**
     * Assert that condition is false
     */
    private void assertFalse(String message, boolean condition) {
        assertTrue(message, !condition);
    }
    
    /**
     * Assert that two integers are equal
     */
    private void assertEquals(String message, int expected, int actual) {
        if (expected == actual) {
            testsPassed++;
            System.out.println("✅ " + message + " (expected: " + expected + ", actual: " + actual + ")");
        } else {
            testsFailed++;
            System.out.println("❌ " + message + " (expected: " + expected + ", actual: " + actual + ")");
        }
    }
    
    /**
     * Assert that two strings are equal
     */
    private void assertEquals(String message, String expected, String actual) {
        if ((expected == null && actual == null) || 
            (expected != null && expected.equals(actual))) {
            testsPassed++;
            System.out.println("✅ " + message + " (expected: '" + expected + "', actual: '" + actual + "')");
        } else {
            testsFailed++;
            System.out.println("❌ " + message + " (expected: '" + expected + "', actual: '" + actual + "')");
        }
    }
}
