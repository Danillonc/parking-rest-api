package com.parking.demo.infrastructure.adapters.out.db.repository;

import com.parking.demo.infrastructure.adapters.out.db.entity.SpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpotJpaRepository extends JpaRepository<SpotEntity, Long> {
    Optional<SpotEntity> findByLatAndLng(Double lat, Double lng);
}
