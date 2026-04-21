// Transaction.java

import java.time.Instant;

public class Transaction {
    public enum TransactionType {
        CREDIT, DEBIT
    }

    private String id;
    private double amount;
    private TransactionType type; // Use enum for type safety
    private Instant timestamp; // Use Instant for better time management

    public Transaction(String id, double amount, TransactionType type) {
        validateTransaction(id, amount, type);
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.timestamp = Instant.now(); // Set the current time
    }

    private void validateTransaction(String id, double amount, TransactionType type) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null.");
        }
    }

    public String getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", timestamp=" + timestamp +
                '}';
    }
}