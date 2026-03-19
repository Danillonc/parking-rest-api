package com.parking.demo.infrastructure.adapters.out.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "garages")
public class GarageEntity {
    @Id
    private String sector;
    private BigDecimal basePrice;
    private Integer maxCapacity;

    // Getters, Setters, Construtores
}
