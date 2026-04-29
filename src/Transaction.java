import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a financial transaction with a unique ID, amount, type, and timestamp.
 */
public class Transaction {
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    public enum TransactionType {
        CREDIT, DEBIT
    }

    private String id;
    private double amount;
    private TransactionType type; // Use enum for type safety
    private Instant timestamp; // Use Instant for better time management

    /**
     * Constructs a Transaction instance with the given parameters.
     * 
     * @param id     the unique identifier for the transaction
     * @param amount the amount of the transaction, must be positive
     * @param type   the type of transaction (CREDIT or DEBIT)
     */
    public Transaction(String id, double amount, TransactionType type) {
        validateTransaction(id, amount, type);
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.timestamp = Instant.now(); // Set the current time
        logger.info("Transaction created: {}", this);
    }

    private void validateTransaction(String id, double amount, TransactionType type) {
        if (id == null || id.isEmpty()) {
            logger.error("Transaction creation failed: ID cannot be null or empty.");
            throw new IllegalArgumentException("Transaction ID cannot be null or empty.");
        }
        if (amount <= 0) {
            logger.error("Transaction creation failed: Amount must be positive.");
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (type == null) {
            logger.error("Transaction creation failed: Type cannot be null.");
            throw new IllegalArgumentException("Transaction type cannot be null.");
        }
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

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", timestamp=" + timestamp +
                '}';
    }
}