package com.novapay.transactions;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionRepositoryImpl implements TransactionRepository {
    private Map<String, Transaction> transactionStore = new ConcurrentHashMap<>(); // Use ConcurrentHashMap for thread safety

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
    public List<Transaction> findByIds(List<String> txIds) {
        List<Transaction> results = new ArrayList<>();
        for (String txId : txIds) {
            Transaction tx = transactionStore.get(txId);
            if (tx != null) {
                results.add(tx);
            }
        }
        return results; // Return a list instead of Optional
    }

    @Override
    public void updateState(String txId, TransactionState state) {
        Transaction tx = transactionStore.get(txId);
        if (tx != null) {
            tx.setState(state);
        }
    }
}