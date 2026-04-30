import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages state transitions for Transaction objects.
 * Encapsulates allowed states and transitions to enhance extensibility.
 */
public final class TransactionStateMachine {
    private static final Logger logger = LoggerFactory.getLogger(TransactionStateMachine.class);

    public enum TransactionState {
        PENDING, COMPLETED, CANCELLED
    }

    /**
     * Validates if a transition from currentState to newState is allowed.
     * @param currentState the current state of the transaction
     * @param newState the desired new state
     * @return true if the transition is allowed, false otherwise
     */
    public boolean isValidTransition(TransactionState currentState, TransactionState newState) {
        if (currentState == null || newState == null) {
            logger.error("State transition validation failed: States cannot be null.");
            return false;
        }
        switch (currentState) {
            case PENDING:
                return (newState == TransactionState.COMPLETED || newState == TransactionState.CANCELLED);
            case COMPLETED:
            case CANCELLED:
                return false; // terminal states
            default:
                logger.error("State transition validation failed: Unknown current state {}.", currentState);
                return false;
        }
    }
}
