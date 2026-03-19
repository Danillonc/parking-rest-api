package com.parking.demo.infrastructure.adapters.out.db;

import com.parking.demo.domain.model.Ticket;
import com.parking.demo.domain.ports.out.TicketRepositoryPort;
import com.parking.demo.infrastructure.adapters.out.db.entity.TicketEntity;
import com.parking.demo.infrastructure.adapters.out.db.repository.TicketJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TicketDatabaseAdapter implements TicketRepositoryPort {

    private final TicketJpaRepository jpaRepository;

    @Override
    public Ticket save(Ticket ticket) {
        TicketEntity entity = toEntity(ticket);
        TicketEntity savedEntity = jpaRepository.save(entity);

        return toDomain(savedEntity);
    }

    @Override
    public Optional<Ticket> findActiveByLicensePlate(String licensePlate) {
        return jpaRepository.findActiveByLicensePlate(licensePlate)
                .map(this::toDomain);
    }

    @Override
    public int countActiveBySector(String sector) {
        return jpaRepository.countActiveTicketsBySector(sector);
    }

    @Override
    public BigDecimal calculateRevenueBySectorAndDate(String sector, LocalDate date) {
        BigDecimal sum = jpaRepository.sumRevenueBySectorAndDate(sector, date);
        return sum != null ? sum : BigDecimal.ZERO;
    }

    private TicketEntity toEntity(Ticket domain) {
        TicketEntity entity = new TicketEntity();
        entity.setLicensePlate(domain.licensePlate());
        entity.setSector(domain.sector());
        entity.setEntryTime(domain.entryTime());
        entity.setExitTime(domain.exitTime());
        entity.setDynamicMultiplier(domain.dynamicMultiplier());
        entity.setTotalAmount(domain.totalAmount());
        entity.setStatus(TicketEntity.Status.valueOf(domain.status().name()));
        return entity;
    }

    private Ticket toDomain(TicketEntity entity) {
        return new Ticket(
                entity.getLicensePlate(),
                entity.getSector(),
                entity.getEntryTime(),
                entity.getExitTime(),
                entity.getDynamicMultiplier(),
                entity.getTotalAmount(),
                Ticket.Status.valueOf(entity.getStatus().name()),
                null
        );
    }
}
