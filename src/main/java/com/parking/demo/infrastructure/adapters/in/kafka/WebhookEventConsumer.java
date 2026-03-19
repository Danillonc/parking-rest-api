package com.parking.demo.infrastructure.adapters.in.kafka;

import com.parking.demo.domain.exception.BusinessException;
import com.parking.demo.domain.ports.in.ManageParkingUseCase;
import com.parking.demo.infrastructure.adapters.in.web.WebhookPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class WebhookEventConsumer {

    private final ManageParkingUseCase parkingUseCase;

    @KafkaListener(topics = "garage-events", groupId = "garage-processor-group")
    public void consume(WebhookPayload payload) {
        try {
            switch (payload.eventType()) {
                case "ENTRY" -> parkingUseCase.processEntry(payload.licensePlate(), payload.entryTime(), payload.sector());
                case "PARKED" -> parkingUseCase.processParked(payload.licensePlate(), payload.lat(), payload.lng());
                case "EXIT" -> parkingUseCase.processExit(payload.licensePlate(), payload.exitTime());
                default -> log.warn("Tipo de evento desconhecido: {}", payload.eventType());
            }
        } catch (BusinessException e) {
            log.error("Erro de negócio para o veículo {}: {}", payload.licensePlate(), e.getMessage());
            // Em produção real, salvar isso numa tabela de auditoria ou mandar pra um tópico DLQ.

        } catch (Exception e) {
            // Erros de infraestrutura (ex: Banco caiu).
            // Lançamos a exceção. O Spring NÃO FAZ O COMMIT e tenta reprocessar depois (Retry).
            log.error("Falha de infraestrutura. O Spring fará o retry.", e);
            throw e;
        }
    }
}
