package com.parking.demo.infrastructure.adapters.out.client.dto;

import java.util.List;

public record SimulatorResponseDTO(
        List<GarageConfigDTO> garage,
        List<SpotConfigDTO> spots
) {}
