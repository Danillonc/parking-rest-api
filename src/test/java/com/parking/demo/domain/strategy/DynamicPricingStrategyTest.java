package com.parking.demo.domain.strategy;

import com.parking.demo.domain.exception.ParkingFullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DynamicPricingStrategyTest {

    private DynamicPricingStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new DynamicPricingStrategy();
    }

    @Test
    @DisplayName("Deve aplicar 10% de desconto (0.90) quando lotação for menor que 25%")
    void shouldApplyDiscountWhenBelow25Percent() {
        BigDecimal multiplier = strategy.calculateMultiplier(20, 100); // 20%
        assertThat(multiplier).isEqualByComparingTo(BigDecimal.valueOf(0.90));
    }

    @Test
    @DisplayName("Deve manter o preço base (1.00) quando lotação for entre 25% e 49%")
    void shouldApplyBasePriceWhenBelow50Percent() {
        BigDecimal multiplier = strategy.calculateMultiplier(49, 100); // 49%
        assertThat(multiplier).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    @DisplayName("Deve aplicar 25% de aumento (1.25) quando lotação for entre 75% e 99%")
    void shouldApplyIncreaseWhenBelow100Percent() {
        BigDecimal multiplier = strategy.calculateMultiplier(90, 100); // 90%
        assertThat(multiplier).isEqualByComparingTo(BigDecimal.valueOf(1.25));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o setor estiver 100% lotado")
    void shouldThrowExceptionWhenSectorIsFull() {
        assertThatThrownBy(() -> strategy.calculateMultiplier(100, 100))
                .isInstanceOf(ParkingFullException.class)
                .hasMessage("Sector is full");
    }

}
