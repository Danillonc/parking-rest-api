package com.parking.demo.infrastructure.adapters.in.web;

import java.math.BigDecimal;
import java.time.Instant;

public record RevenueResponse(BigDecimal amount, String currency, Instant timestamp)
{}