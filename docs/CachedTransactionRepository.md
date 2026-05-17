# CachedTransactionRepository

## Overview
The `CachedTransactionRepository` class extends the `TransactionRepository` interface to add caching capabilities for transaction management.

## Methods
- `getTransactionById(String id)`: Retrieves a transaction by its ID, utilizing the cache for improved performance.
- `clearCache()`: Clears the cached transactions.

## Usage
This class is useful for applications that require fast access to transaction data.