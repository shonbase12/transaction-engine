package com.novapay.transactions;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public class TransactionRepositoryImpl implements TransactionRepository {
    private Map<String, Transaction> transactionStore = new HashMap<>(); // Simulated in-memory store for caching

    @Override
    public void save(Transaction tx) {
        transactionStore.put(tx.getId(), tx);
    }

    @Override
    public void saveAll(List<Transaction> transactions) {
        for (Transaction tx : transactions) {
            save(tx);
        }
    }

    @Override
    public Optional<Transaction> findById(String txId) {
        return Optional.ofNullable(transactionStore.get(txId));
    }

    @Override
    public Optional<List<Transaction>> findByIds(List<String> txIds) {
        List<Transaction> results = new ArrayList<>();
        for (String txId : txIds) {
            transactionStore.get(txId).ifPresent(results::add);
        }
        return Optional.of(results);
    }

    @Override
    public void updateState(String txId, TransactionState state) {
        Transaction tx = transactionStore.get(txId);
        if (tx != null) {
            tx.setState(state);
        }
    }
}