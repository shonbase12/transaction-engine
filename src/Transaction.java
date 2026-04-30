import java.time.Instant;
import java.util.Currency;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a financial transaction with a unique ID, amount, type, timestamp, accountId, currency, description, and state.
 */
public final class Transaction {
    private static final Logger logger = LoggerFactory.getLogger(Transaction.class);

    // Configurable supported currencies
    private static final Set<String> SUPPORTED_CURRENCIES = new HashSet<>();
    static {
        SUPPORTED_CURRENCIES.add("USD");
        SUPPORTED_CURRENCIES.add("EUR");
        SUPPORTED_CURRENCIES.add("GBP");
        SUPPORTED_CURRENCIES.add("JPY");
        SUPPORTED_CURRENCIES.add("CHF");
        // Add more supported currencies as needed
    }

    public enum TransactionType {
        CREDIT, DEBIT
    }

    public enum TransactionState {
        PENDING, COMPLETED, CANCELLED
    }

    private final String id;
    private final double amount;
    private final TransactionType type; // Use enum for type safety
    private final Instant timestamp; // Use Instant for better time management
    private final String accountId;
    private final Currency currency;
    private final String description;
    private TransactionState state; // mutable state with controlled transitions

    /**
     * Constructs a Transaction instance with the given parameters and current timestamp.
     * 
     * @param id          the unique identifier for the transaction
     * @param amount      the amount of the transaction, must be zero or positive
     * @param type        the type of transaction (CREDIT or DEBIT)
     * @param accountId   the account ID associated with the transaction
     * @param currency    the currency of the transaction
     * @param description the description of the transaction (nullable, max 255 chars)
     */
    public Transaction(String id, double amount, TransactionType type, String accountId, String currencyCode, String description) {
        this(id, amount, type, accountId, currencyCode, description, Instant.now());
    }

    /**
     * Constructs a Transaction instance with the given parameters and explicit timestamp.
     * 
     * @param id          the unique identifier for the transaction
     * @param amount      the amount of the transaction, must be zero or positive
     * @param type        the type of transaction (CREDIT or DEBIT)
     * @param accountId   the account ID associated with the transaction
     * @param currency    the currency of the transaction
     * @param description the description of the transaction (nullable, max 255 chars)
     * @param timestamp   the timestamp of the transaction, must not be null
     */
    public Transaction(String id, double amount, TransactionType type, String accountId, String currencyCode, String description, Instant timestamp) {
        validateTransaction(id, amount, type, accountId, currencyCode, description, timestamp);
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.accountId = accountId;
        this.currency = Currency.getInstance(currencyCode);
        this.description = sanitizeDescription(description);
        this.timestamp = timestamp;
        this.state = TransactionState.PENDING; // initial state
        logger.info("Transaction created: {}", this);
    }

    private void validateTransaction(String id, double amount, TransactionType type, String accountId, String currencyCode, String description, Instant timestamp) {
        if (id == null || id.isEmpty()) {
            logger.error("Transaction creation failed: ID cannot be null or empty.");
            throw new IllegalArgumentException("Transaction ID cannot be null or empty.");
        }
        if (amount < 0) {
            logger.error("Transaction creation failed: Amount cannot be negative.");
            throw new IllegalArgumentException("Amount cannot be negative.");
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
        if (!SUPPORTED_CURRENCIES.contains(currencyCode)) {
            logger.error("Transaction creation failed: Unsupported currency code: {}", currencyCode);
            throw new IllegalArgumentException("Unsupported currency code: " + currencyCode);
        }
        // Validate currency code by attempting to get Currency instance
        try {
            Currency.getInstance(currencyCode);
        } catch (IllegalArgumentException e) {
            logger.error("Transaction creation failed: Invalid currency code.", e);
            throw new IllegalArgumentException("Invalid currency code.");
        }
        if (description != null && description.length() > 255) {
            logger.error("Transaction creation failed: Description too long.");
            throw new IllegalArgumentException("Description cannot exceed 255 characters.");
        }
        if (timestamp == null) {
            logger.error("Transaction creation failed: Timestamp cannot be null.");
            throw new IllegalArgumentException("Timestamp cannot be null.");
        }
    }

    private String sanitizeDescription(String description) {
        if (description == null) {
            return null;
        }
        // Example sanitation: trim and replace control chars
        return description.trim().replaceAll("[\x00-\x1F\x7F]", "");
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

    public TransactionState getState() {
        return state;
    }

    /**
     * Transition the transaction state to COMPLETED.
     * Allowed only if current state is PENDING.
     */
    public void complete() {
        if (state != TransactionState.PENDING) {
            String msg = "Cannot complete transaction from state: " + state;
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
        state = TransactionState.COMPLETED;
        logger.info("Transaction {} marked as COMPLETED.", id);
    }

    /**
     * Transition the transaction state to CANCELLED.
     * Allowed only if current state is PENDING.
     */
    public void cancel() {
        if (state != TransactionState.PENDING) {
            String msg = "Cannot cancel transaction from state: " + state;
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
        state = TransactionState.CANCELLED;
        logger.info("Transaction {} marked as CANCELLED.", id);
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
                ", state=" + state +
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
                Objects.equals(description, that.description) &&
                state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, type, timestamp, accountId, currency, description, state);
    }
}
