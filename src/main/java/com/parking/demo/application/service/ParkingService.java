package com.parking.demo.application.service;

import com.parking.demo.domain.exception.BusinessException;
import com.parking.demo.domain.model.Garage;
import com.parking.demo.domain.model.Spot;
import com.parking.demo.domain.model.Ticket;
import com.parking.demo.domain.ports.in.ManageParkingUseCase;
import com.parking.demo.domain.ports.out.GarageRepositoryPort;
import com.parking.demo.domain.ports.out.TicketRepositoryPort;
import com.parking.demo.domain.strategy.DynamicPricingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ParkingService implements ManageParkingUseCase {

    private final TicketRepositoryPort ticketRepository;
    private final GarageRepositoryPort garageRepository;
    private final DynamicPricingStrategy pricingStrategy;

    @Transactional
    @Override
    public void processEntry(String licensePlate, LocalDateTime entryTime, String sector) {
        if (sector == null || sector.isBlank()) {
            throw new IllegalArgumentException("O setor é obrigatório para registrar a entrada do veículo.");
        }

        ticketRepository.findActiveByLicensePlate(licensePlate).ifPresent(t -> {
            throw new BusinessException("Veículo com a placa " + licensePlate + " já possui um ticket ativo.");
        });

        Garage garage = garageRepository.getSectorInfo(sector);

        int currentOccupancy = ticketRepository.countActiveBySector(sector);
        BigDecimal multiplier = pricingStrategy.calculateMultiplier(currentOccupancy, garage.maxCapacity());

        Ticket ticket = new Ticket(null, licensePlate, sector, entryTime, multiplier, Ticket.Status.ENTRY);
        ticketRepository.save(ticket);
    }

    @Transactional
    @Override
    public void processParked(String licensePlate, double lat, double lng) {
        Ticket ticket = ticketRepository.findActiveByLicensePlate(licensePlate)
                .orElseThrow(() -> new BusinessException("Active ticket not found for plate: " + licensePlate));

        Spot spot = garageRepository.findAvailableSpotByLocation(lat, lng)
                .orElseThrow(() -> new BusinessException("Spot not found or already occupied at the given location"));

        Ticket parkedTicket = new Ticket(
                ticket.id(),
                ticket.licensePlate(),
                ticket.sector(),
                ticket.entryTime(),
                ticket.exitTime(),
                ticket.dynamicMultiplier(),
                ticket.totalAmount(),
                Ticket.Status.PARKED,
                spot.id()
        );
        ticketRepository.save(parkedTicket);

        garageRepository.markSpotAsOccupied(spot.id());
    }

    @Transactional
    @Override
    public void processExit(String licensePlate, LocalDateTime exitTime) {
        Ticket ticket = ticketRepository.findActiveByLicensePlate(licensePlate)
                .orElseThrow(() -> new BusinessException("Active ticket not found for plate: " + licensePlate));

        Garage garage = garageRepository.getSectorInfo(ticket.sector());

        long minutes = java.time.Duration.between(ticket.entryTime(), exitTime).toMinutes();
        BigDecimal totalAmount = BigDecimal.ZERO;

        if (minutes > 30) {
            long hours = (long) Math.ceil(minutes / 60.0);
            totalAmount = garage.basePrice()
                    .multiply(ticket.dynamicMultiplier())
                    .multiply(BigDecimal.valueOf(hours));
        }

        Ticket exitedTicket = new Ticket(
                ticket.id(),
                ticket.licensePlate(),
                ticket.sector(),
                ticket.entryTime(),
                exitTime,
                ticket.dynamicMultiplier(),
                totalAmount,
                Ticket.Status.EXIT,
                ticket.spotId()
        );

        ticketRepository.save(exitedTicket);

        if (ticket.spotId() != null) {
            garageRepository.markSpotAsAvailable(ticket.spotId());
        }
    }
}
