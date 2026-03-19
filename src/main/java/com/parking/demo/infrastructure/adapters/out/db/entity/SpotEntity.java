package com.parking.demo.infrastructure.adapters.out.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "spots")
public class SpotEntity {
    @Id
    private Long id;
    private String sector;
    private Double lat;
    private Double lng;
    private Boolean occupied = false;
}
