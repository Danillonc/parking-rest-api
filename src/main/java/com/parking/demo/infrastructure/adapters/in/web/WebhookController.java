package com.parking.demo.infrastructure.adapters.in.web;

import com.parking.demo.domain.ports.out.WebhookEventPublisherPort;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final WebhookEventPublisherPort eventPublisher;

    public WebhookController(WebhookEventPublisherPort eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostMapping
    public ResponseEntity<Void> handleEvent(@Valid @RequestBody WebhookPayload payload) {
        eventPublisher.publish(payload);
        return ResponseEntity.accepted().build();
    }

}
