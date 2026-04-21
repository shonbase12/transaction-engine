package com.novapay.transactions;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class CachedTransactionRepository implements TransactionRepository {
    private final TransactionRepository delegate;
    private final Cache<String, Transaction> cache;

    public CachedTransactionRepository(TransactionRepository delegate) {
        this.delegate = delegate;
        this.cache = Caffeine.newBuilder()
            .expireAfterAccess(10, TimeUnit.MINUTES) // Dynamic expiry can be implemented
            .maximumSize(1000)
            .build();
    }

    @Override
    public void save(Transaction tx) {
        delegate.save(tx);
        cache.put(tx.getId(), tx);
    }

    @Override
    public void saveAll(List<Transaction> transactions) {
        delegate.saveAll(transactions);
        for (Transaction tx : transactions) {
            cache.put(tx.getId(), tx);
        }
    }

    @Override
    public Optional<Transaction> findById(String txId) {
        Transaction cachedTx = cache.getIfPresent(txId);
        if (cachedTx != null) {
            return Optional.of(cachedTx);
        }
        Optional<Transaction> transaction = delegate.findById(txId);
        transaction.ifPresent(tx -> cache.put(txId, tx));
        return transaction;
    }

    @Override
    public void updateState(String txId, TransactionState state) {
        delegate.updateState(txId, state);
        Optional<Transaction> transaction = delegate.findById(txId);
        transaction.ifPresent(tx -> {
            tx.setState(state);
            cache.put(txId, tx);
        });
    }

    // Implement indexing methods based on the database being used
    public void createIndex(String fieldName) {
        // Logic to create index on the specified field (implementation depends on the database)
    }
}