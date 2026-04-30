import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a complete journal entry consisting of multiple Transaction instances.
 * Ensures that total debits equal total credits according to double-entry accounting principles.
 */
public class JournalEntry {
    private static final Logger logger = LoggerFactory.getLogger(JournalEntry.class);

    private String journalEntryId;
    private List<Transaction> transactions;
    private Instant timestamp;

    /**
     * Constructs a JournalEntry instance with the given ID and list of transactions.
     * Validates that the entry is balanced (total debits equal total credits).
     *
     * @param journalEntryId unique identifier for the journal entry
     * @param transactions list of Transaction instances
     */
    public JournalEntry(String journalEntryId, List<Transaction> transactions) {
        this.journalEntryId = journalEntryId;
        this.transactions = new ArrayList<>(transactions);
        this.timestamp = Instant.now();

        validateJournalEntry();
        logger.info("JournalEntry created: {}", this);
    }

    private void validateJournalEntry() {
        if (journalEntryId == null || journalEntryId.isEmpty()) {
            logger.error("JournalEntry creation failed: ID cannot be null or empty.");
            throw new IllegalArgumentException("JournalEntry ID cannot be null or empty.");
        }
        if (transactions == null || transactions.isEmpty()) {
            logger.error("JournalEntry creation failed: Transactions list cannot be null or empty.");
            throw new IllegalArgumentException("Transactions list cannot be null or empty.");
        }

        double totalDebits = 0.0;
        double totalCredits = 0.0;

        for (Transaction t : transactions) {
            if (t.getType() == Transaction.TransactionType.DEBIT) {
                totalDebits += t.getAmount();
            } else if (t.getType() == Transaction.TransactionType.CREDIT) {
                totalCredits += t.getAmount();
            } else {
                logger.error("JournalEntry validation failed: Unknown transaction type {}.", t.getType());
                throw new IllegalArgumentException("Unknown transaction type.");
            }
        }

        if (Double.compare(totalDebits, totalCredits) != 0) {
            logger.error("JournalEntry validation failed: Debits ({}) do not equal credits ({}).", totalDebits, totalCredits);
            throw new IllegalArgumentException("Journal entry is not balanced: debits do not equal credits.");
        }

        logger.info("JournalEntry validation passed: debits equal credits.");
    }

    public String getJournalEntryId() {
        return journalEntryId;
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "JournalEntry{" +
                "journalEntryId='" + journalEntryId + '\'' +
                ", transactions=" + transactions +
                ", timestamp=" + timestamp +
                '}';
    }
}
