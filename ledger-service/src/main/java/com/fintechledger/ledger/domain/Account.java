package com.fintechledger.ledger.domain;

import java.util.Collection;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;

/**
 * Cuenta de cliente (pasivo, naturaleza acreedora): los créditos aumentan el
 * balance y los débitos lo disminuyen. El balance es una proyección calculada
 * por replay de apuntes, nunca un valor que se actualice directamente.
 */
public record Account(UUID id, Currency currency) {

    public Account {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(currency, "currency");
    }

    public Money balance(Collection<LedgerEntry> entries) {
        Money balance = Money.zero(currency);
        for (LedgerEntry entry : entries) {
            if (!entry.accountId().equals(id)) {
                continue;
            }
            balance = switch (entry.direction()) {
                case CREDIT -> balance.plus(entry.amount());
                case DEBIT -> balance.minus(entry.amount());
            };
        }
        return balance;
    }
}
