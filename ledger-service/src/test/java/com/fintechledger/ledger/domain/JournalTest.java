package com.fintechledger.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JournalTest {

    private final UUID tx = UUID.randomUUID();
    private final UUID from = UUID.randomUUID();
    private final UUID to = UUID.randomUUID();
    private final Instant now = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void acceptsBalancedJournal() {
        Money amount = Money.of("25.00", "USD");

        Journal journal = new Journal(tx, List.of(
                LedgerEntry.debit(tx, from, amount, now),
                LedgerEntry.credit(tx, to, amount, now)));

        assertThat(journal.entries()).hasSize(2);
    }

    @Test
    void rejectsUnbalancedJournal() {
        assertThatThrownBy(() -> new Journal(tx, List.of(
                LedgerEntry.debit(tx, from, Money.of("25.00", "USD"), now),
                LedgerEntry.credit(tx, to, Money.of("24.99", "USD"), now))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not balanced");
    }

    @Test
    void rejectsSingleEntry() {
        assertThatThrownBy(() -> new Journal(tx, List.of(
                LedgerEntry.debit(tx, from, Money.of("1.00", "USD"), now))))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsEntriesFromAnotherTransaction() {
        UUID other = UUID.randomUUID();
        Money amount = Money.of("5.00", "USD");

        assertThatThrownBy(() -> new Journal(tx, List.of(
                LedgerEntry.debit(tx, from, amount, now),
                LedgerEntry.credit(other, to, amount, now))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("belong to transaction");
    }

    @Test
    void mustBalanceEachCurrencySeparately() {
        assertThatThrownBy(() -> new Journal(tx, List.of(
                LedgerEntry.debit(tx, from, Money.of("10.00", "USD"), now),
                LedgerEntry.credit(tx, to, Money.of("10.00", "EUR"), now))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not balanced");
    }

    @Test
    void entryAmountMustBePositive() {
        assertThatThrownBy(() -> LedgerEntry.debit(tx, from, Money.of("0.00", "USD"), now))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
