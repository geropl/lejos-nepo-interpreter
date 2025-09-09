/**
 * Exception thrown when configuration parsing fails due to invalid or missing XML structure.
 */
public class ConfigurationException extends Exception {
    
    public ConfigurationException(String message) {
        super(message);
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}