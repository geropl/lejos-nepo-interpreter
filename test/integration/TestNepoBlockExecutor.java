import java.util.*;

/**
 * Test-specific NEPO Block Executor that supports dynamic sensor scenarios.
 * Extends NepoBlockExecutor with iteration tracking and scenario application.
 */
public class TestNepoBlockExecutor extends NepoBlockExecutor {
    private ExecutionContext context;
    private List<SensorScenario> scenarios;
    private MockHardware mockHardware;
    
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
     */
    @Override
    protected void onIteration(int iterationNumber) {
        // Update execution context
        context.incrementIteration();
        
        // Apply scenarios that should trigger at this iteration
        for (SensorScenario scenario : scenarios) {
            if (scenario.shouldApply(context)) {
                scenario.applySensorChanges(mockHardware);
                
                // Log scenario application
                List<String> log = mockHardware.getLog();
                log.add("[Iteration " + (iterationNumber + 1) + "] Scenario applied: " + 
                        scenario.getDescription());
            }
        }
    }
    
    /**
     * Hook to determine maximum iterations based on scenarios.
     * Calculates the maximum iteration needed plus a buffer.
     */
    @Override
    protected Integer getMaxIterations() {
        if (scenarios.isEmpty()) {
            return Integer.valueOf(10); // Default for tests without scenarios
        }
        
        // Find the highest iteration number in scenarios
        int maxScenarioIteration = 0;
        for (SensorScenario scenario : scenarios) {
            if (scenario instanceof IterationBasedScenario) {
                int target = ((IterationBasedScenario) scenario).getTargetIteration();
                maxScenarioIteration = Math.max(maxScenarioIteration, target);
            }
        }
        
        // Add buffer iterations after last scenario
        return Integer.valueOf(Math.max(10, maxScenarioIteration + 3));
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
     * Public method for testing iteration logic.
     * Calls the protected onIteration method.
     * 
     * @param iterationNumber Iteration number to simulate
     */
    public void simulateIteration(int iterationNumber) {
        onIteration(iterationNumber);
    }
}