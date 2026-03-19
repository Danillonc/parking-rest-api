package com.parking.demo.infrastructure.adapters.out.kafka;

import com.parking.demo.domain.ports.out.WebhookEventPublisherPort;
import com.parking.demo.infrastructure.adapters.in.web.WebhookPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaWebhookEventPublisherAdapter implements WebhookEventPublisherPort {

    private final KafkaTemplate<String, WebhookPayload> kafkaTemplate;
    private static final String TOPIC = "garage-events";

    @Override
    public void publish(WebhookPayload payload) {
        kafkaTemplate.send(TOPIC, payload.licensePlate(), payload);
    }

}
