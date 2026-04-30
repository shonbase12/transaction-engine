import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

    @Test
    public void testZeroAmount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("12346", 0.0, Transaction.TransactionType.DEBIT, "acc2", "USD", "Withdrawal");
        });
        assertEquals("Amount must be positive.", exception.getMessage());
    }

    @Test
    public void testLargeAmount() {
        Transaction transaction = new Transaction("12347", 1e10, Transaction.TransactionType.CREDIT, "acc3", "USD", "Large deposit");
        assertEquals(1e10, transaction.getAmount());
    }

    @Test
    public void testSmallestPositiveAmount() {
        Transaction transaction = new Transaction("12357", 0.0001, Transaction.TransactionType.CREDIT, "acc12", "USD", "Small amount");
        assertEquals(0.0001, transaction.getAmount());
    }

    @Test
    public void testInvalidCurrencyCodeFormat() {
        String[] invalidCurrencies = {"US", "usd", "US1", "US$", "123", "U", "USDA"};
        for (String currency : invalidCurrencies) {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Transaction("12358", 100.0, Transaction.TransactionType.CREDIT, "acc13", currency, "Invalid currency code");
            });
            assertEquals("Currency code must be a 3-letter uppercase string.", exception.getMessage());
        }
    }

    @Test
    public void testNullAccountId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("12348", 100.0, Transaction.TransactionType.CREDIT, null, "USD", "Deposit");
        });
        assertEquals("Account ID cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testEmptyAccountId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("12349", 100.0, Transaction.TransactionType.CREDIT, "", "USD", "Deposit");
        });
        assertEquals("Account ID cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testNullCurrency() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("12350", 100.0, Transaction.TransactionType.CREDIT, "acc5", null, "Deposit");
        });
        assertEquals("Currency cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testEmptyCurrency() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("12351", 100.0, Transaction.TransactionType.CREDIT, "acc6", "", "Deposit");
        });
        assertEquals("Currency cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testNullDescription() {
        Transaction transaction = new Transaction("12352", 100.0, Transaction.TransactionType.CREDIT, "acc7", "USD", null);
        assertNull(transaction.getDescription());
    }

    @Test
    public void testEmptyDescription() {
        Transaction transaction = new Transaction("12353", 100.0, Transaction.TransactionType.CREDIT, "acc8", "USD", "");
        assertEquals("", transaction.getDescription());
    }

    @Test
    public void testTimestampImmutability() throws InterruptedException {
        Transaction transaction = new Transaction("12354", 100.0, Transaction.TransactionType.CREDIT, "acc9", "USD", "Deposit");
        long originalTimestamp = transaction.getTimestamp().getTime();
        Thread.sleep(10);
        assertEquals(originalTimestamp, transaction.getTimestamp().getTime());
    }

    @Test
    public void testTransactionTypeVariations() {
        for (Transaction.TransactionType type : Transaction.TransactionType.values()) {
            Transaction transaction = new Transaction("12355", 100.0, type, "acc10", "USD", "Test");
            assertEquals(type, transaction.getType());
        }
    }

    @Test
    public void testTimestampCloseToCreation() {
        long beforeCreation = System.currentTimeMillis();
        Transaction transaction = new Transaction("12356", 100.0, Transaction.TransactionType.CREDIT, "acc11", "USD", "Deposit");
        long afterCreation = System.currentTimeMillis();
        assertTrue(transaction.getTimestamp().getTime() >= beforeCreation && transaction.getTimestamp().getTime() <= afterCreation);
    }

    @Test
    public void testConcurrentTransactionCreation() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            executor.submit(() -> {
                Transaction transaction = new Transaction(
                    "concurrent-" + id,
                    100.0 + id,
                    Transaction.TransactionType.CREDIT,
                    "acc" + id,
                    "USD",
                    "Concurrent deposit " + id
                );
                assertEquals("concurrent-" + id, transaction.getId());
                assertEquals(100.0 + id, transaction.getAmount());
                assertEquals(Transaction.TransactionType.CREDIT, transaction.getType());
                assertEquals("acc" + id, transaction.getAccountId());
                assertEquals("USD", transaction.getCurrency());
                assertEquals("Concurrent deposit " + id, transaction.getDescription());
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(5, TimeUnit.SECONDS);
        assertTrue(finished, "Executor did not terminate in the expected time");
    }
}
