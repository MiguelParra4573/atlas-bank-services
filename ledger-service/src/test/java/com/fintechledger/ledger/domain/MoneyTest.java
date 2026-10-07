package com.fintechledger.ledger.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void normalizesScaleToCurrencyFractionDigits() {
        assertThat(Money.of("10", "USD").amount()).hasToString("10.00");
    }

    @Test
    void equalAmountsWithDifferentInputScaleAreEqual() {
        assertThat(Money.of("10", "USD")).isEqualTo(Money.of("10.00", "USD"));
    }

    @Test
    void rejectsMoreDecimalsThanCurrencyAllows() {
        assertThatThrownBy(() -> Money.of("10.001", "USD"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addsAndSubtractsSameCurrency() {
        Money ten = Money.of("10.00", "USD");
        Money three = Money.of("3.50", "USD");

        assertThat(ten.plus(three)).isEqualTo(Money.of("13.50", "USD"));
        assertThat(ten.minus(three)).isEqualTo(Money.of("6.50", "USD"));
    }

    @Test
    void rejectsArithmeticAcrossCurrencies() {
        assertThatThrownBy(() -> Money.of("1", "USD").plus(Money.of("1", "EUR")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("currency mismatch");
    }
}
