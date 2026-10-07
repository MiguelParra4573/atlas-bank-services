package com.fintechledger.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AccountTest {

    private final Instant now = Instant.parse("2026-01-01T00:00:00Z");
    private final Account account = new Account(UUID.randomUUID(), Currency.getInstance("USD"));

    @Test
    void balanceIsReplayOfCreditsMinusDebits() {
        UUID tx1 = UUID.randomUUID();
        UUID tx2 = UUID.randomUUID();
        UUID counterparty = UUID.randomUUID();

        List<LedgerEntry> entries = List.of(
                LedgerEntry.credit(tx1, account.id(), Money.of("100.00", "USD"), now),
                LedgerEntry.debit(tx1, counterparty, Money.of("100.00", "USD"), now),
                LedgerEntry.debit(tx2, account.id(), Money.of("30.25", "USD"), now),
                LedgerEntry.credit(tx2, counterparty, Money.of("30.25", "USD"), now));

        assertThat(account.balance(entries)).isEqualTo(Money.of("69.75", "USD"));
    }

    @Test
    void emptyHistoryHasZeroBalance() {
        assertThat(account.balance(List.of())).isEqualTo(Money.zero(account.currency()));
    }
}
