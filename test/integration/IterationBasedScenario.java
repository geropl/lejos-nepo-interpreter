import java.util.*;

/**
 * Sensor scenario triggered at a specific loop iteration.
 * Allows precise control over when sensor values change during program execution.
 */
public class IterationBasedScenario implements SensorScenario {
    private int targetIteration;
    private Map<String, Object> sensorValues;
    private boolean applied = false;
    private String description;
    
    /**
     * Create a scenario that triggers at the specified iteration.
     * 
     * @param targetIteration Iteration number when scenario should apply (0-based)
     */
    public IterationBasedScenario(int targetIteration) {
        this.targetIteration = targetIteration;
        this.sensorValues = new HashMap<String, Object>();
        this.description = "Iteration " + targetIteration + " scenario";
    }
    
    /**
     * Set a touch sensor value.
     * 
     * @param port Sensor port number
     * @param pressed Whether sensor should be pressed
     * @return this scenario for method chaining
     */
    public IterationBasedScenario setTouchSensor(int port, boolean pressed) {
        sensorValues.put("touch_" + port, Boolean.valueOf(pressed));
        updateDescription();
        return this;
    }
    
    /**
     * Set a light sensor value.
     * 
     * @param port Sensor port number  
     * @param value Light sensor reading (0-100)
     * @return this scenario for method chaining
     */
    public IterationBasedScenario setLightSensor(int port, double value) {
        sensorValues.put("light_" + port, Double.valueOf(value));
        updateDescription();
        return this;
    }
    
    /**
     * Set a distance sensor value.
     * 
     * @param port Sensor port number
     * @param distance Distance reading in cm
     * @return this scenario for method chaining
     */
    public IterationBasedScenario setDistanceSensor(int port, double distance) {
        sensorValues.put("distance_" + port, Double.valueOf(distance));
        updateDescription();
        return this;
    }
    
    @Override
    public boolean shouldApply(ExecutionContext context) {
        return !applied && context.getCurrentIteration() == targetIteration;
    }
    
    @Override
    public void applySensorChanges(MockHardware hardware) {
        for (Map.Entry<String, Object> entry : sensorValues.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (key.startsWith("touch_")) {
                String port = key.substring(6);
                boolean pressed = ((Boolean) value).booleanValue();
                hardware.setTouchSensorValue(port, pressed);
            } else if (key.startsWith("light_")) {
                String port = key.substring(6);
                double lightValue = ((Double) value).doubleValue();
                hardware.setLightSensorValue(port, lightValue);
            } else if (key.startsWith("distance_")) {
                String port = key.substring(9);
                double distance = ((Double) value).doubleValue();
                hardware.setDistanceSensorValue(port, distance);
            }
        }
        applied = true;
    }
    
    @Override
    public String getDescription() {
        return description;
    }
    
    @Override
    public boolean isApplied() {
        return applied;
    }
    
    @Override
    public void markApplied() {
        applied = true;
    }
    
    /**
     * Reset the applied state for reuse.
     */
    public void reset() {
        applied = false;
    }
    
    /**
     * Update description based on configured sensor values.
     */
    private void updateDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append("Iteration ").append(targetIteration).append(": ");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : sensorValues.entrySet()) {
            if (!first) desc.append(", ");
            desc.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        
        description = desc.toString();
    }
    
    /**
     * Get the target iteration for this scenario.
     */
    public int getTargetIteration() {
        return targetIteration;
    }
}