package com.novapay.transactions;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

public class TransactionRepositoryImpl implements TransactionRepository {
    private static final Logger logger = LoggerFactory.getLogger(TransactionRepositoryImpl.class);
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
        } else {
            logger.error("Failed to update state: Transaction with id {} not found.", txId);
            throw new IllegalArgumentException("Transaction with id " + txId + " not found.");
        }
    }
}
