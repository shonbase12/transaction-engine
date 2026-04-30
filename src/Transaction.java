import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a financial transaction with a unique ID, amount, type, timestamp, accountId, currency, description, and state.
 *
 * <p>State transitions are controlled and explicit:
 * <ul>
 *   <li>Initial state: PENDING</li>
 *   <li>Allowed transitions:
 *     <ul>
 *       <li>PENDING -> COMPLETED</li>
 *       <li>PENDING -> CANCELLED</li>
 *     </ul>
 *   </li>
 *   <li>No transitions allowed from COMPLETED or CANCELLED to other states.</li>
 * </ul>
 * </p>
 *
 * <p>The transaction amount must be zero or positive, and the currency code must be supported.
 * Description is sanitized and limited to 255 characters.
 * </p>
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
    private final BigDecimal amount;
    private final TransactionType type; // Use enum for type safety
    private final Instant timestamp; // Use Instant for better time management
    private final String accountId;
    private final Currency currency;
    private final String description;
    private final TransactionState state; // immutable state

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
    public Transaction(String id, BigDecimal amount, TransactionType type, String accountId, String currencyCode, String description) {
        this(id, amount, type, accountId, currencyCode, description, Instant.now(), TransactionState.PENDING);
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
     * @param state       the state of the transaction
     */
    private Transaction(String id, BigDecimal amount, TransactionType type, String accountId, String currencyCode, String description, Instant timestamp, TransactionState state) {
        validateTransaction(id, amount, type, accountId, currencyCode, description, timestamp);
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.accountId = accountId;
        this.currency = Currency.getInstance(currencyCode);
        this.description = sanitizeDescription(description);
        this.timestamp = timestamp;
        this.state = state;
        logger.info("Transaction created: {}", this);
    }

    private void validateTransaction(String id, BigDecimal amount, TransactionType type, String accountId, String currencyCode, String description, Instant timestamp) {
        if (id == null || id.isEmpty()) {
            logger.error("Transaction creation failed: ID cannot be null or empty.");
            throw new IllegalArgumentException("Transaction ID cannot be null or empty.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            logger.error("Transaction creation failed: Amount cannot be null or negative.");
            throw new IllegalArgumentException("Amount cannot be null or negative.");
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
        return description.trim().replaceAll("[\x00-\x1F\x7F]", "");
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
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
     * Return a new Transaction instance with state COMPLETED.
     * Allowed only if current state is PENDING.
     * @return new Transaction instance with COMPLETED state
     */
    public Transaction complete() {
        if (state != TransactionState.PENDING) {
            String msg = "Cannot complete transaction from state: " + state;
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
        logger.info("Transaction {} marked as COMPLETED.", id);
        return new Transaction(id, amount, type, accountId, currency.getCurrencyCode(), description, timestamp, TransactionState.COMPLETED);
    }

    /**
     * Return a new Transaction instance with state CANCELLED.
     * Allowed only if current state is PENDING.
     * @return new Transaction instance with CANCELLED state
     */
    public Transaction cancel() {
        if (state != TransactionState.PENDING) {
            String msg = "Cannot cancel transaction from state: " + state;
            logger.error(msg);
            throw new IllegalStateException(msg);
        }
        logger.info("Transaction {} marked as CANCELLED.", id);
        return new Transaction(id, amount, type, accountId, currency.getCurrencyCode(), description, timestamp, TransactionState.CANCELLED);
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
        return amount.equals(that.amount) &&
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
