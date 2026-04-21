package com.novapay.transactions;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository {
    void save(Transaction tx);
    void saveAll(List<Transaction> transactions);
    Optional<Transaction> findById(String txId);
    void updateState(String txId, TransactionState state);
}