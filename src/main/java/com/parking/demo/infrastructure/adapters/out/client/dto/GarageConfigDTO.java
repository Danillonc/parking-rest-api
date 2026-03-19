package com.parking.demo.infrastructure.adapters.out.client.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record GarageConfigDTO(
        String sector,
        @JsonAlias({"basePrice", "base_price"})
        BigDecimal basePrice,
        @JsonProperty("max_capacity") Integer maxCapacity
) {}
