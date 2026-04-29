import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {
    @Test
    public void testTransactionCreationValid() {
        Transaction transaction = new Transaction("12345", 100.0, Transaction.TransactionType.CREDIT, "acc1", "USD", "Deposit");
        assertEquals("12345", transaction.getId());
        assertEquals(100.0, transaction.getAmount());
        assertEquals(Transaction.TransactionType.CREDIT, transaction.getType());
        assertNotNull(transaction.getTimestamp());
        assertEquals("acc1", transaction.getAccountId());
        assertEquals("USD", transaction.getCurrency());
        assertEquals("Deposit", transaction.getDescription());
    }

    @Test
    public void testInvalidTransactionId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction(null, 100.0, Transaction.TransactionType.CREDIT, "acc1", "USD", "Deposit");
        });
        assertEquals("Transaction ID cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testInvalidAmount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("12345", -50.0, Transaction.TransactionType.CREDIT, "acc1", "USD", "Deposit");
        });
        assertEquals("Amount must be positive.", exception.getMessage());
    }

    @Test
    public void testInvalidType() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("12345", 100.0, null, "acc1", "USD", "Deposit");
        });
        assertEquals("Transaction type cannot be null.", exception.getMessage());
    }
}