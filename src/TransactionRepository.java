package com.novapay.transactions;

import java.util.Optional;

public interface TransactionRepository {
    void save(Transaction tx);
    Optional<Transaction> findById(String txId);
    void updateState(String txId, TransactionState state);
}
