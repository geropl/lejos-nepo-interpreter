/**
 * ValidationRules - Interface for custom validation logic
 * 
 * Allows test cases to define custom validation rules beyond
 * standard hardware state and display content validation.
 */
public interface ValidationRules {
    
    /**
     * Validate test execution result and final hardware state
     * 
     * @param executionResult Result returned from block execution
     * @param finalState Final hardware state after execution
     * @return ValidationResult indicating pass/fail and message
     */
    NepoTestFramework.ValidationResult validate(Object executionResult, 
                                               MockHardware.HardwareState finalState);
}
