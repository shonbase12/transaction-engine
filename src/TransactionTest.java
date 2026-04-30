import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;
import java.util.concurrent.*;
import java.io.*;

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
            new Transaction("12346", 0, Transaction.TransactionType.CREDIT, "acc1", "USD", "Zero amount");
        });
        assertEquals("Amount must be positive.", exception.getMessage());
    }

    @Test
    public void testLargeAmount() {
        double largeAmount = Double.MAX_VALUE;
        Transaction transaction = new Transaction("12347", largeAmount, Transaction.TransactionType.CREDIT, "acc1", "USD", "Large amount");
        assertEquals(largeAmount, transaction.getAmount());
    }

    @Test
    public void testInvalidCurrency() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("12348", 100.0, Transaction.TransactionType.CREDIT, "acc1", "INVALID", "Invalid currency");
        });
        assertEquals("Currency code is invalid.", exception.getMessage());
    }

    @Test
    public void testNullCurrency() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("12349", 100.0, Transaction.TransactionType.CREDIT, "acc1", null, "Null currency");
        });
        assertEquals("Currency cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testNullDescription() {
        Transaction transaction = new Transaction("12350", 100.0, Transaction.TransactionType.CREDIT, "acc1", "USD", null);
        assertNull(transaction.getDescription());
    }

    @Test
    public void testEmptyDescription() {
        Transaction transaction = new Transaction("12351", 100.0, Transaction.TransactionType.CREDIT, "acc1", "USD", "");
        assertEquals("", transaction.getDescription());
    }

    @Test
    public void testLongDescription() {
        String longDesc = new String(new char[1000]).replace('\0', 'x');
        Transaction transaction = new Transaction("12352", 100.0, Transaction.TransactionType.CREDIT, "acc1", "USD", longDesc);
        assertEquals(longDesc, transaction.getDescription());
    }

    @Test
    public void testTimestampSetOnCreation() {
        Transaction transaction = new Transaction("12353", 100.0, Transaction.TransactionType.CREDIT, "acc1", "USD", "Timestamp test");
        Instant before = Instant.now().minusSeconds(1);
        assertTrue(transaction.getTimestamp().isAfter(before));
    }

    @Test
    public void testAccountIdNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Transaction("12354", 100.0, Transaction.TransactionType.CREDIT, null, "USD", "Null accountId");
        });
        assertEquals("Account ID cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testEqualsAndHashCode() {
        Transaction t1 = new Transaction("12355", 100.0, Transaction.TransactionType.CREDIT, "acc1", "USD", "Desc");
        Transaction t2 = new Transaction("12355", 100.0, Transaction.TransactionType.CREDIT, "acc1", "USD", "Desc");
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        Transaction t1 = new Transaction("12356", 100.0, Transaction.TransactionType.CREDIT, "acc1", "USD", "Desc");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(t1);
        oos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Transaction t2 = (Transaction) ois.readObject();

        assertEquals(t1, t2);
    }

    @Test
    public void testConcurrentCreation() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        Callable<Transaction> task = () -> new Transaction("12357", 100.0, Transaction.TransactionType.CREDIT, "acc1", "USD", "Concurrent");

        Future<Transaction>[] futures = new Future[10];
        for (int i = 0; i < 10; i++) {
            futures[i] = executor.submit(task);
        }

        for (Future<Transaction> future : futures) {
            Transaction t = future.get();
            assertNotNull(t);
            assertEquals("12357", t.getId());
        }

        executor.shutdown();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionTest that = (TransactionTest) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
