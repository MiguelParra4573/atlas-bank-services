package com.fintechledger.ledger.domain;

import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Asiento de doble entrada: conjunto de apuntes de una misma transacción cuyos
 * débitos y créditos suman lo mismo por cada moneda.
 */
public record Journal(UUID transactionId, List<LedgerEntry> entries) {

    public Journal {
        Objects.requireNonNull(transactionId, "transactionId");
        Objects.requireNonNull(entries, "entries");
        entries = List.copyOf(entries);
        if (entries.size() < 2) {
            throw new IllegalArgumentException("a journal needs at least two entries");
        }
        if (entries.stream().anyMatch(e -> !e.transactionId().equals(transactionId))) {
            throw new IllegalArgumentException("all entries must belong to transaction " + transactionId);
        }
        requireBalanced(entries);
    }

    private static void requireBalanced(List<LedgerEntry> entries) {
        Map<Currency, Money> net = new HashMap<>();
        for (LedgerEntry entry : entries) {
            Currency currency = entry.amount().currency();
            Money signed = entry.direction() == EntryDirection.DEBIT
                    ? entry.amount()
                    : Money.zero(currency).minus(entry.amount());
            net.merge(currency, signed, Money::plus);
        }
        net.forEach((currency, total) -> {
            if (total.amount().signum() != 0) {
                throw new IllegalArgumentException(
                        "journal is not balanced in %s (net %s)".formatted(currency, total.amount()));
            }
        });
    }
}
