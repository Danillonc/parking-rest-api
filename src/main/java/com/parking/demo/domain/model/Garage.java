package com.parking.demo.domain.model;

import java.math.BigDecimal;

public record Garage(
    String sector,
    BigDecimal basePrice,
    Integer maxCapacity
) {
}
