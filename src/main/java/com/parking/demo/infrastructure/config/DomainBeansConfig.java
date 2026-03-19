package com.parking.demo.infrastructure.config;

import com.parking.demo.application.service.ParkingService;
import com.parking.demo.domain.ports.in.ManageParkingUseCase;
import com.parking.demo.domain.ports.out.GarageRepositoryPort;
import com.parking.demo.domain.ports.out.TicketRepositoryPort;
import com.parking.demo.domain.strategy.DynamicPricingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainBeansConfig {

    @Bean
    public DynamicPricingStrategy dynamicPricingStrategy() {
        return new DynamicPricingStrategy();
    }

    @Bean
    public ManageParkingUseCase manageParkingUseCase(
            TicketRepositoryPort ticketRepositoryPort,
            GarageRepositoryPort garageRepositoryPort,
            DynamicPricingStrategy pricingStrategy) {

        return new ParkingService(ticketRepositoryPort, garageRepositoryPort, pricingStrategy);
    }
}
