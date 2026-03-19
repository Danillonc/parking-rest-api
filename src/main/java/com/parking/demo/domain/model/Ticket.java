package com.parking.demo.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Ticket(
        String licensePlate,
        String sector,
        LocalDateTime entryTime,
        LocalDateTime exitTime,
        BigDecimal dynamicMultiplier,
        BigDecimal totalAmount,
        Status status,
        Long spotId
) {
    public enum Status { ENTRY, PARKED, EXIT }

    public Ticket(String licensePlate, String sector, LocalDateTime entryTime, BigDecimal dynamicMultiplier, Status status) {
        this(licensePlate, sector, entryTime, null, dynamicMultiplier, null, status, null);
    }
}
