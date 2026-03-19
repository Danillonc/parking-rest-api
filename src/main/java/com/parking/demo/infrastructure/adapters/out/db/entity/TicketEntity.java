package com.parking.demo.infrastructure.adapters.out.db.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tickets")
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String licensePlate;

    private String sector;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;

    @Column(precision = 5, scale = 2)
    private BigDecimal dynamicMultiplier;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private Status status; // ENTRY, PARKED, EXIT

    public enum Status { ENTRY, PARKED, EXIT }

    // Getters, Setters, Construtores
}
