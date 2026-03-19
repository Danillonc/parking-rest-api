package com.parking.demo.infrastructure.adapters.out.client.dto;

public record SpotConfigDTO(
        Long id,
        String sector,
        Double lat,
        Double lng
) {}
