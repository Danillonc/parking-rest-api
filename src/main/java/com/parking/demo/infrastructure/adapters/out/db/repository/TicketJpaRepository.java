package com.parking.demo.infrastructure.adapters.out.db.repository;

import com.parking.demo.infrastructure.adapters.out.db.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface TicketJpaRepository extends JpaRepository<TicketEntity, Long> {

    // Busca o ticket ativo de um carro (que não seja EXIT)
    @Query("SELECT t FROM TicketEntity t WHERE t.licensePlate = :licensePlate AND t.status IN ('ENTRY', 'PARKED')")
    Optional<TicketEntity> findActiveByLicensePlate(@Param("licensePlate") String licensePlate);

    // Conta os carros que estão atualmente ocupando espaço
    @Query("SELECT COUNT(t) FROM TicketEntity t WHERE t.sector = :sector AND t.status IN ('ENTRY', 'PARKED')")
    int countActiveTicketsBySector(@Param("sector") String sector);

    // Soma o faturamento do setor em uma data específica
    @Query("SELECT SUM(t.totalAmount) FROM TicketEntity t WHERE t.sector = :sector AND DATE(t.exitTime) = :date AND t.status = 'EXIT'")
    BigDecimal sumRevenueBySectorAndDate(@Param("sector") String sector, @Param("date") LocalDate date);
}
