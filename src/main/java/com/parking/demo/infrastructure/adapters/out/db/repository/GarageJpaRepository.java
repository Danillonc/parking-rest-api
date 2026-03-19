package com.parking.demo.infrastructure.adapters.out.db.repository;

import com.parking.demo.infrastructure.adapters.out.db.entity.GarageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GarageJpaRepository extends JpaRepository<GarageEntity, String> {
}
