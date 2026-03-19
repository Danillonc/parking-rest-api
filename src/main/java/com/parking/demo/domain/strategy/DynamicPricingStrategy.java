package com.parking.demo.domain.strategy;

import java.math.BigDecimal;

public class DynamicPricingStrategy {
    public BigDecimal calculateMultiplier(int currentOccupancy, int maxCapacity) {
        double occupancyRate = (double) currentOccupancy / maxCapacity;

        if (occupancyRate >= 1.0) throw new ParkingFullException("Sector is full");
        if (occupancyRate < 0.25) return BigDecimal.valueOf(0.90);
        if (occupancyRate < 0.50) return BigDecimal.ONE;
        if (occupancyRate < 0.75) return BigDecimal.valueOf(1.10);
        return BigDecimal.valueOf(1.25);
    }
}
