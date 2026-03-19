package com.parking.demo.infrastructure.adapters.in.web;

import com.parking.demo.domain.ports.out.TicketRepositoryPort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@RestController
@RequestMapping("/revenue")
public class RevenueController {

    private final TicketRepositoryPort ticketRepositoryPort;

    public RevenueController(TicketRepositoryPort ticketRepositoryPort) {
        this.ticketRepositoryPort = ticketRepositoryPort;
    }

    @GetMapping
    public ResponseEntity<RevenueResponse> getRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String sector) {

        BigDecimal amount = ticketRepositoryPort.calculateRevenueBySectorAndDate(sector, date);
        // Proteção contra nulos caso não haja faturamento no dia
        BigDecimal safeAmount = amount != null ? amount : BigDecimal.ZERO;

        return ResponseEntity.ok(new RevenueResponse(safeAmount, "BRL", Instant.now()));
    }

}
