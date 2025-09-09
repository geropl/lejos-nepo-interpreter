import java.util.*;

/**
 * Test-specific NEPO Block Executor that supports dynamic sensor scenarios.
 * Extends NepoBlockExecutor with iteration tracking and scenario application.
 */
public class TestNepoBlockExecutor extends NepoBlockExecutor {
    private ExecutionContext context;
    private List<SensorScenario> scenarios;
    private MockHardware mockHardware;
    private int maxTotalIterations = 20; // Default configurable limit
    
    /**
     * Create a test executor with mock hardware.
     * 
     * @param hardware Mock hardware instance
     */
    public TestNepoBlockExecutor(MockHardware hardware) {
        super(hardware);
        this.mockHardware = hardware;
        this.context = new ExecutionContext();
        this.scenarios = new ArrayList<SensorScenario>();
    }
    
    /**
     * Create a test executor with mock hardware and scenarios.
     * 
     * @param hardware Mock hardware instance
     * @param scenarios List of sensor scenarios to apply
     */
    public TestNepoBlockExecutor(MockHardware hardware, List<SensorScenario> scenarios) {
        super(hardware);
        this.mockHardware = hardware;
        this.context = new ExecutionContext();
        this.scenarios = new ArrayList<SensorScenario>(scenarios);
    }
    
    /**
     * Hook called after each loop iteration.
     * Updates execution context and applies scenarios.
     * Sets running to false if total iteration limit is exceeded.
     */
    @Override
    public void onIteration() {
        if (!this.isRunning()) {
            // Might happen on closing upper loops etc.
            return;
        }

        // Update execution context
        context.incrementIteration();
        
        // Check if we've exceeded the total iteration limit
        int currentIteration = this.context.getCurrentIteration();
        if (currentIteration >= maxTotalIterations) {
            mockHardware.log("[ITERATION LIMIT] Test stopped after " + currentIteration + 
                   " iterations (limit: " + maxTotalIterations + ")");
            
            // Stop execution by setting running flag to false
            this.setRunning(false);
            return;
        }
        
        // Apply scenarios that should trigger at this iteration
        for (SensorScenario scenario : scenarios) {
            if (scenario.shouldApply(context)) {
                scenario.applySensorChanges(mockHardware);
                
                // Log scenario application
                mockHardware.log("[Iteration " + currentIteration + "] Scenario applied: " + 
                        scenario.getDescription());
            }
        }
    }
    
    /**
     * Add a scenario to be applied during execution.
     * 
     * @param scenario Scenario to add
     */
    public void addScenario(SensorScenario scenario) {
        scenarios.add(scenario);
    }
    
    /**
     * Set the list of scenarios to apply.
     * 
     * @param scenarios List of scenarios
     */
    public void setScenarios(List<SensorScenario> scenarios) {
        this.scenarios = new ArrayList<SensorScenario>(scenarios);
    }
    
    /**
     * Get the current execution context.
     * 
     * @return Execution context
     */
    public ExecutionContext getExecutionContext() {
        return context;
    }
    
    /**
     * Get the list of configured scenarios.
     * 
     * @return List of scenarios
     */
    public List<SensorScenario> getScenarios() {
        return new ArrayList<SensorScenario>(scenarios);
    }
    
    /**
     * Get the mock hardware instance.
     * 
     * @return Mock hardware
     */
    public MockHardware getMockHardware() {
        return mockHardware;
    }
    
    /**
     * Set the maximum total iterations allowed across all loops.
     * 
     * @param maxTotalIterations Maximum total iterations (must be positive)
     */
    public void setMaxTotalIterations(int maxTotalIterations) {
        if (maxTotalIterations <= 0) {
            throw new IllegalArgumentException("Maximum total iterations must be positive");
        }
        this.maxTotalIterations = maxTotalIterations;
    }
    
    /**
     * Get the current maximum total iterations limit.
     * 
     * @return Maximum total iterations
     */
    public int getMaxTotalIterations() {
        return maxTotalIterations;
    }
}