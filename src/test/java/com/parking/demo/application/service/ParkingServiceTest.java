package com.parking.demo.application.service;

import com.parking.demo.domain.model.Garage;
import com.parking.demo.domain.model.Ticket;
import com.parking.demo.domain.ports.out.GarageRepositoryPort;
import com.parking.demo.domain.ports.out.TicketRepositoryPort;
import com.parking.demo.domain.strategy.DynamicPricingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

    @Mock
    private TicketRepositoryPort ticketRepository;

    @Mock
    private GarageRepositoryPort garageRepository;

    @Mock
    private DynamicPricingStrategy pricingStrategy;

    private ParkingService parkingService;

    @Captor
    private ArgumentCaptor<Ticket> ticketCaptor;

    @BeforeEach
    void setUp() {
        parkingService = new ParkingService(ticketRepository, garageRepository, pricingStrategy);
    }

    @Test
    @DisplayName("Deve processar a entrada com sucesso e salvar o ticket correto")
    void shouldProcessEntrySuccessfully() {
        // Arrange (Given)
        String licensePlate = "ZUL0001";
        String sector = "A";
        LocalDateTime entryTime = LocalDateTime.now();

        Garage mockGarage = new Garage(sector, BigDecimal.valueOf(10.0), 100);

        given(garageRepository.getSectorInfo(sector)).willReturn(mockGarage);
        given(ticketRepository.countActiveBySector(sector)).willReturn(10); // 10 carros
        given(pricingStrategy.calculateMultiplier(10, 100)).willReturn(BigDecimal.valueOf(0.90));

        // Act (When)
        parkingService.processEntry(licensePlate, entryTime, sector);

        // Assert (Then)
        verify(ticketRepository).save(ticketCaptor.capture());
        Ticket savedTicket = ticketCaptor.getValue();

        assertThat(savedTicket.licensePlate()).isEqualTo(licensePlate);
        assertThat(savedTicket.sector()).isEqualTo(sector);
        assertThat(savedTicket.dynamicMultiplier()).isEqualByComparingTo(BigDecimal.valueOf(0.90));
        assertThat(savedTicket.status()).isEqualTo(Ticket.Status.ENTRY);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException se o setor for nulo no ENTRY (Guard Clause)")
    void shouldThrowExceptionWhenSectorIsNullOnEntry() {
        assertThatThrownBy(() -> parkingService.processEntry("ZUL0001", LocalDateTime.now(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("O setor é obrigatório");
    }
}
