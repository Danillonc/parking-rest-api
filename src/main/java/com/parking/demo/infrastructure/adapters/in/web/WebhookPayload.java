package com.parking.demo.infrastructure.adapters.in.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record WebhookPayload(
        @NotBlank(message = "license_plate is required")
        @JsonProperty("license_plate") String licensePlate,

        @JsonProperty("entry_time") LocalDateTime entryTime,
        @JsonProperty("exit_time") LocalDateTime exitTime,

        Double lat,
        Double lng,
        String sector,

        @NotBlank(message = "event_type is required")
        @JsonProperty("event_type") String eventType
) {}
