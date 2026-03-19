package com.parking.demo.domain.ports.out;

import com.parking.demo.infrastructure.adapters.in.web.WebhookPayload;

public interface WebhookEventPublisherPort {
    void publish(WebhookPayload payload);
}
