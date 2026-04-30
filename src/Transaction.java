import java.time.Instant;
import java.util.Currency;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a financial transaction with a unique ID, amount, type, timestamp, accountId, currency, and description.
 */
public final class Transaction {
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    public enum TransactionType {
        CREDIT, DEBIT
    }

    private final String id;
    private final double amount;
    private final TransactionType type; // Use enum for type safety
    private final Instant timestamp; // Use Instant for better time management
    private final String accountId;
    private final Currency currency;
    private final String description;

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
    public Transaction(String id, double amount, TransactionType type, String accountId, String currencyCode, String description) {
        validateTransaction(id, amount, type, accountId, currencyCode);
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.accountId = accountId;
        this.currency = Currency.getInstance(currencyCode);
        this.description = description;
        this.timestamp = Instant.now(); // Set the current time
        logger.info("Transaction created: {}", this);
    }

    private void validateTransaction(String id, double amount, TransactionType type, String accountId, String currencyCode) {
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
        if (currencyCode == null || currencyCode.isEmpty()) {
            logger.error("Transaction creation failed: Currency cannot be null or empty.");
            throw new IllegalArgumentException("Currency cannot be null or empty.");
        }
        // Validate currency code by attempting to get Currency instance
        try {
            Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            logger.error("Transaction creation failed: Invalid currency code.", e);
            throw new IllegalArgumentException("Invalid currency code.");
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

    public Currency getCurrency() {
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
                ", currency=" + currency +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(that.amount, amount) == 0 &&
                id.equals(that.id) &&
                type == that.type &&
                timestamp.equals(that.timestamp) &&
                accountId.equals(that.accountId) &&
                currency.equals(that.currency) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, type, timestamp, accountId, currency, description);
    }
}
