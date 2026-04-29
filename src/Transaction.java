import java.time.Instant;

public class Transaction {
    public enum TransactionType {
        CREDIT, DEBIT
    }

    public enum TransactionState {
        PENDING,
        COMPLETED,
        CANCELED
    }

    private String id;
    private double amount;
    private TransactionType type; 
    private Instant timestamp; 
    private TransactionState state; // New state attribute

    public Transaction(String id, double amount, TransactionType type) {
        validateTransaction(id, amount, type);
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.timestamp = Instant.now(); 
        this.state = TransactionState.PENDING; // Initialize state to PENDING
    }

    private void validateTransaction(String id, double amount, TransactionType type) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null.");
        }
    }

    public void completeTransaction() {
        if (state != TransactionState.PENDING) {
            throw new IllegalStateException("Only pending transactions can be completed.");
        }
        state = TransactionState.COMPLETED; // Transition to COMPLETED
    }

    public void cancelTransaction() {
        if (state != TransactionState.PENDING) {
            throw new IllegalStateException("Only pending transactions can be canceled.");
        }
        state = TransactionState.CANCELED; // Transition to CANCELED
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public TransactionState getState() {
        return state; // Getter for the state
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", state=" + state + // Include state in string representation
                '}';
    }
}