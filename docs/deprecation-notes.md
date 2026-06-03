## Deprecation Notes for Transaction State Management

Breaking changes introduced in PR #28:
- Transaction state transitions now enforce strict rules (PENDING → COMPLETED/CANCELED)
- Attempting invalid transitions throws IllegalStateException

Migration steps:
1. Update all transaction handling code to check/catch state transition exceptions
2. Review existing transaction initialization logic to ensure PENDING state is explicit
3. Update logging/serialization code to handle new state enum values