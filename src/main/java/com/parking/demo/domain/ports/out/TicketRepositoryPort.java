package com.parking.demo.domain.ports.out;

import com.parking.demo.domain.model.Ticket;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface TicketRepositoryPort {

    Ticket save(Ticket ticket);

    // Busca um carro que está na garagem (status ENTRY ou PARKED)
    Optional<Ticket> findActiveByLicensePlate(String licensePlate);

    // Conta quantos carros estão ocupando vagas em um setor específico
    int countActiveBySector(String sector);

    // Calcula a receita total de um setor em um dia específico
    BigDecimal calculateRevenueBySectorAndDate(String sector, LocalDate date);
}
