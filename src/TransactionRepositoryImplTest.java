import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionRepositoryImplTest {
    private TransactionRepositoryImpl repository;

    @BeforeEach
    public void setUp() {
        repository = new TransactionRepositoryImpl();
    }

    @Test
    public void testConcurrentSaves() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            executor.submit(() -> {
                Transaction tx = new Transaction(
                        "tx" + id,
                        100.0 + id,
                        Transaction.TransactionType.CREDIT,
                        "acc" + id,
                        "USD",
                        "Concurrent save " + id
                );
                repository.save(tx);
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);
        assertTrue(finished, "Executor did not terminate in the expected time");

        for (int i = 0; i < threadCount; i++) {
            Optional<Transaction> txOpt = repository.findById("tx" + i);
            assertTrue(txOpt.isPresent(), "Transaction should be present: tx" + i);
        }
    }

    @Test
    public void testConcurrentUpdates() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Pre-populate repository with transactions
        for (int i = 0; i < threadCount; i++) {
            Transaction tx = new Transaction(
                    "tx" + i,
                    100.0 + i,
                    Transaction.TransactionType.CREDIT,
                    "acc" + i,
                    "USD",
                    "Initial"
            );
            repository.save(tx);
        }

        // Concurrently update state
        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            executor.submit(() -> {
                repository.updateState("tx" + id, TransactionState.COMPLETED);
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);
        assertTrue(finished, "Executor did not terminate in the expected time");

        // Verify updates
        for (int i = 0; i < threadCount; i++) {
            Optional<Transaction> txOpt = repository.findById("tx" + i);
            assertTrue(txOpt.isPresent());
            assertEquals(TransactionState.COMPLETED, txOpt.get().getState());
        }
    }

    @Test
    public void testConcurrentFinds() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Pre-populate repository
        for (int i = 0; i < threadCount; i++) {
            Transaction tx = new Transaction(
                    "tx" + i,
                    100.0 + i,
                    Transaction.TransactionType.CREDIT,
                    "acc" + i,
                    "USD",
                    "Initial"
            );
            repository.save(tx);
        }

        List<Boolean> results = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            executor.submit(() -> {
                Optional<Transaction> txOpt = repository.findById("tx" + id);
                synchronized (results) {
                    results.add(txOpt.isPresent());
                }
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);
        assertTrue(finished, "Executor did not terminate in the expected time");

        assertEquals(threadCount, results.size());
        for (Boolean present : results) {
            assertTrue(present);
        }
    }

    @Test
    public void testConcurrentMixedOperations() throws InterruptedException {
        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        // Concurrently save and update
        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            executor.submit(() -> {
                Transaction tx = new Transaction(
                        "tx" + id,
                        100.0 + id,
                        Transaction.TransactionType.CREDIT,
                        "acc" + id,
                        "USD",
                        "Mixed operation " + id
                );
                repository.save(tx);
            });
            executor.submit(() -> {
                repository.updateState("tx" + id, TransactionState.COMPLETED);
            });
        }

        executor.shutdown();
        boolean finished = executor.awaitTermination(10, TimeUnit.SECONDS);
        assertTrue(finished, "Executor did not terminate in the expected time");

        // Verify transactions
        for (int i = 0; i < threadCount; i++) {
            Optional<Transaction> txOpt = repository.findById("tx" + i);
            assertTrue(txOpt.isPresent());
            assertEquals(TransactionState.COMPLETED, txOpt.get().getState());
        }
    }
}
