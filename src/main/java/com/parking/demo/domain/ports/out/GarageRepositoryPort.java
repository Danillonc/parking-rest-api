package com.parking.demo.domain.ports.out;

import com.parking.demo.domain.model.Garage;
import com.parking.demo.domain.model.Spot;

import java.util.List;
import java.util.Optional;

public interface GarageRepositoryPort {

    Garage getSectorInfo(String sector);

    // Busca uma vaga pelas coordenadas geográficas
    Optional<Spot> findAvailableSpotByLocation(double lat, double lng);

    // Marca a vaga como ocupada (Usado no evento PARKED)
    void markSpotAsOccupied(Long spotId);

    // Marca a vaga como livre (Usado no evento EXIT)
    void markSpotAsAvailable(Long spotId);

    // Sincroniza os dados do simulador no startup da aplicação
    void syncGarageData(List<Garage> garages, List<Spot> spots);
}
