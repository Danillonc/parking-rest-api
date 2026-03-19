package com.parking.demo.infrastructure.adapters.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.demo.domain.ports.out.WebhookEventPublisherPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WebhookController.class)
class WebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WebhookEventPublisherPort eventPublisher;

    @Test
    @DisplayName("Deve retornar 202 Accepted quando o payload for válido")
    void shouldReturnAcceptedWhenPayloadIsValid() throws Exception {
        WebhookPayload validPayload = new WebhookPayload(
                "ZUL0001", LocalDateTime.now(), null, null, null, "A", "ENTRY"
        );

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validPayload)))
                .andExpect(status().isAccepted());

        verify(eventPublisher).publish(any(WebhookPayload.class));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando license_plate estiver vazio (Testando o @Valid)")
    void shouldReturnBadRequestWhenLicensePlateIsMissing() throws Exception {
        WebhookPayload invalidPayload = new WebhookPayload(
                null, LocalDateTime.now(), null, null, null, "A", "ENTRY"
        );

        mockMvc.perform(post("/webhook")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPayload)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.invalid_params[0]").value("license_plate is required"));
    }
}
