import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a financial transaction with a unique ID, amount, type, timestamp, accountId, currency, and description.
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
    private String accountId;
    private String currency;
    private String description;

    /**
     * Constructs a Transaction instance with the given parameters.
     * 
     * @param id          the unique identifier for the transaction
     * @param amount      the amount of the transaction, must be positive
     * @param type        the type of transaction (CREDIT or DEBIT)
     * @param accountId   the account ID associated with the transaction
     * @param currency    the currency of the transaction
     * @param description the description of the transaction (nullable)
     */
    public Transaction(String id, double amount, TransactionType type, String accountId, String currency, String description) {
        validateTransaction(id, amount, type, accountId, currency);
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.accountId = accountId;
        this.currency = currency;
        this.description = description;
        this.timestamp = Instant.now(); // Set the current time
        logger.info("Transaction created: {}", this);
    }

    private void validateTransaction(String id, double amount, TransactionType type, String accountId, String currency) {
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
        if (accountId == null || accountId.isEmpty()) {
            logger.error("Transaction creation failed: Account ID cannot be null or empty.");
            throw new IllegalArgumentException("Account ID cannot be null or empty.");
        }
        if (currency == null || currency.isEmpty()) {
            logger.error("Transaction creation failed: Currency cannot be null or empty.");
            throw new IllegalArgumentException("Currency cannot be null or empty.");
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

    public String getAccountId() {
        return accountId;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", timestamp=" + timestamp +
                ", accountId='" + accountId + '\'' +
                ", currency='" + currency + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
