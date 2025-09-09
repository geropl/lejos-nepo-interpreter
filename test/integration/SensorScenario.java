/**
 * Interface for defining dynamic sensor behavior during test execution.
 * Scenarios can be triggered by iteration count or other conditions.
 */
public interface SensorScenario {
    
    /**
     * Check if this scenario should be applied given the current execution context.
     * 
     * @param context Current execution state
     * @return true if scenario should be applied now
     */
    boolean shouldApply(ExecutionContext context);
    
    /**
     * Apply sensor changes to the mock hardware.
     * Called when shouldApply returns true.
     * 
     * @param hardware Mock hardware to modify
     */
    void applySensorChanges(MockHardware hardware);
    
    /**
     * Get a description of this scenario for logging.
     */
    String getDescription();
    
    /**
     * Check if this scenario has already been applied.
     * Used to prevent multiple applications of one-time scenarios.
     */
    boolean isApplied();
    
    /**
     * Mark this scenario as applied.
     */
    void markApplied();
}