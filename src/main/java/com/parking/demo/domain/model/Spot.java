package com.parking.demo.domain.model;

public record Spot(
    Long id,
    String sector,
    Double lat,
    Double lng,
    Boolean occupied
) {
}
