import java.util.*;

/**
 * Builder class for creating dynamic test scenarios with fluent API.
 * Provides convenient methods for configuring sensor changes at specific iterations.
 * 
 * Example usage:
 *   DynamicTestScenario scenario = new DynamicTestScenario()
 *       .atIteration(5).setTouchSensor(1, true)
 *       .atIteration(10).setLightSensor(4, 25.0);
 */
public class DynamicTestScenario {
    private List<SensorScenario> scenarios;
    private IterationBasedScenario currentScenario;
    
    /**
     * Create a new dynamic test scenario builder.
     */
    public DynamicTestScenario() {
        scenarios = new ArrayList<SensorScenario>();
        currentScenario = null;
    }
    
    /**
     * Start configuring a scenario for the specified iteration.
     * 
     * @param iteration Target iteration (0-based)
     * @return this builder for method chaining
     */
    public DynamicTestScenario atIteration(int iteration) {
        // Finalize previous scenario if exists
        if (currentScenario != null) {
            scenarios.add(currentScenario);
        }
        
        // Check if scenario already exists for this iteration
        for (SensorScenario existing : scenarios) {
            if (existing instanceof IterationBasedScenario) {
                IterationBasedScenario iterScenario = (IterationBasedScenario) existing;
                if (iterScenario.getTargetIteration() == iteration) {
                    currentScenario = iterScenario;
                    return this;
                }
            }
        }
        
        // Create new scenario for this iteration
        currentScenario = new IterationBasedScenario(iteration);
        return this;
    }
    
    /**
     * Set touch sensor value for the current iteration scenario.
     * 
     * @param port Sensor port number
     * @param pressed Whether sensor should be pressed
     * @return this builder for method chaining
     */
    public DynamicTestScenario setTouchSensor(int port, boolean pressed) {
        if (currentScenario == null) {
            throw new IllegalStateException("Must call atIteration() before setting sensor values");
        }
        currentScenario.setTouchSensor(port, pressed);
        return this;
    }
    
    /**
     * Set light sensor value for the current iteration scenario.
     * 
     * @param port Sensor port number
     * @param value Light sensor reading (0-100)
     * @return this builder for method chaining
     */
    public DynamicTestScenario setLightSensor(int port, double value) {
        if (currentScenario == null) {
            throw new IllegalStateException("Must call atIteration() before setting sensor values");
        }
        currentScenario.setLightSensor(port, value);
        return this;
    }
    
    /**
     * Set distance sensor value for the current iteration scenario.
     * 
     * @param port Sensor port number
     * @param distance Distance reading in cm
     * @return this builder for method chaining
     */
    public DynamicTestScenario setDistanceSensor(int port, double distance) {
        if (currentScenario == null) {
            throw new IllegalStateException("Must call atIteration() before setting sensor values");
        }
        currentScenario.setDistanceSensor(port, distance);
        return this;
    }
    
    /**
     * Add a custom sensor scenario.
     * 
     * @param scenario Custom scenario implementation
     * @return this builder for method chaining
     */
    public DynamicTestScenario addScenario(SensorScenario scenario) {
        scenarios.add(scenario);
        return this;
    }
    
    /**
     * Build and return the list of configured scenarios.
     * 
     * @return List of sensor scenarios
     */
    public List<SensorScenario> build() {
        // Finalize current scenario if exists
        if (currentScenario != null) {
            scenarios.add(currentScenario);
            currentScenario = null;
        }
        
        // Sort scenarios by iteration for predictable execution
        Collections.sort(scenarios, new Comparator<SensorScenario>() {
            public int compare(SensorScenario a, SensorScenario b) {
                if (a instanceof IterationBasedScenario && b instanceof IterationBasedScenario) {
                    IterationBasedScenario iterA = (IterationBasedScenario) a;
                    IterationBasedScenario iterB = (IterationBasedScenario) b;
                    return Integer.compare(iterA.getTargetIteration(), iterB.getTargetIteration());
                }
                return 0;
            }
        });
        
        return new ArrayList<SensorScenario>(scenarios);
    }
    
    /**
     * Get the number of configured scenarios.
     */
    public int getScenarioCount() {
        int count = scenarios.size();
        if (currentScenario != null) count++;
        return count;
    }
    
    /**
     * Check if any scenarios are configured.
     */
    public boolean hasScenarios() {
        return getScenarioCount() > 0;
    }
    
    /**
     * Clear all configured scenarios.
     */
    public void clear() {
        scenarios.clear();
        currentScenario = null;
    }
}