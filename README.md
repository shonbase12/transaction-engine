# transaction-engine

Stateful transaction processor for NovaPay. Owns the transaction state machine
and persistence layer.

## States
`PENDING -> PROCESSING -> COMPLETED | FAILED | REFUNDED`
