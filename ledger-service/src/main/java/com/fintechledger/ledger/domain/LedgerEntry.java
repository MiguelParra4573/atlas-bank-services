package com.fintechledger.ledger.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Apunte inmutable del ledger. Nunca se modifica ni se borra: una reversa es un
 * apunte nuevo en dirección contraria.
 */
public record LedgerEntry(
        UUID id,
        UUID transactionId,
        UUID accountId,
        EntryDirection direction,
        Money amount,
        Instant createdAt) {

    public LedgerEntry {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(transactionId, "transactionId");
        Objects.requireNonNull(accountId, "accountId");
        Objects.requireNonNull(direction, "direction");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(createdAt, "createdAt");
        if (!amount.isPositive()) {
            throw new IllegalArgumentException("ledger entry amount must be positive");
        }
    }

    public static LedgerEntry debit(UUID transactionId, UUID accountId, Money amount, Instant at) {
        return new LedgerEntry(UUID.randomUUID(), transactionId, accountId, EntryDirection.DEBIT, amount, at);
    }

    public static LedgerEntry credit(UUID transactionId, UUID accountId, Money amount, Instant at) {
        return new LedgerEntry(UUID.randomUUID(), transactionId, accountId, EntryDirection.CREDIT, amount, at);
    }
}
