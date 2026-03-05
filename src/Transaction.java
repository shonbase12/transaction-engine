package com.novapay.transactions;

import java.time.Instant;

public class Transaction {
    private String id;
    private String merchantId;
    private long amountCents;
    private String currency;
    private TransactionState state;
    private Instant createdAt;
    private Instant updatedAt;

    public TransactionState getState() { return state; }
    public void setState(TransactionState state) {
        this.state = state;
        this.updatedAt = Instant.now();
    }
}
