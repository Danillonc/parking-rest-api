package com.parking.demo.infrastructure.adapters.out.db;

import com.parking.demo.domain.exception.BusinessException;
import com.parking.demo.domain.exception.SpotNotFoundException;
import com.parking.demo.domain.model.Garage;
import com.parking.demo.domain.model.Spot;
import com.parking.demo.domain.ports.out.GarageRepositoryPort;
import com.parking.demo.infrastructure.adapters.out.db.entity.GarageEntity;
import com.parking.demo.infrastructure.adapters.out.db.entity.SpotEntity;
import com.parking.demo.infrastructure.adapters.out.db.repository.GarageJpaRepository;
import com.parking.demo.infrastructure.adapters.out.db.repository.SpotJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class GarageDatabaseAdapter implements GarageRepositoryPort {

    private final GarageJpaRepository garageJpaRepository;
    private final SpotJpaRepository spotJpaRepository;

    @Override
    public Garage getSectorInfo(String sector) {
        GarageEntity entity = garageJpaRepository.findById(sector)
                .orElseThrow(() -> new BusinessException("Sector not found: " + sector));

        return new Garage(entity.getSector(), entity.getBasePrice(), entity.getMaxCapacity());
    }

    @Override
    public Optional<Spot> findAvailableSpotByLocation(double lat, double lng) {
        return spotJpaRepository.findByLatAndLng(lat, lng)
                .filter(spot -> !spot.getOccupied())
                .map(entity -> new Spot(entity.getId(), entity.getSector(), entity.getLat(), entity.getLng(), entity.getOccupied()));
    }

    @Override
    public void markSpotAsOccupied(Long spotId) {
        SpotEntity spot = spotJpaRepository.findById(spotId)
                .orElseThrow(() -> new SpotNotFoundException("Spot not found with id: " + spotId));
        spot.setOccupied(true);
        spotJpaRepository.save(spot);
    }

    @Override
    public void markSpotAsAvailable(Long spotId) {
        SpotEntity spot = spotJpaRepository.findById(spotId)
                .orElseThrow(() -> new SpotNotFoundException("Spot not found with id: " + spotId));
        spot.setOccupied(false);
        spotJpaRepository.save(spot);
    }

    @Override
    public void syncGarageData(List<Garage> garages, List<Spot> spots) {

    }
}
