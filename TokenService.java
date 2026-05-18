// Updated error handling in TokenService.java

public class TokenService {
    // Existing methods...

    public void handleError(Exception e) {
        // Enhanced error handling logic
        if (e instanceof SpecificException) {
            // Handle specific exception
        } else {
            // General error handling
        }
        logError(e);
    }
}