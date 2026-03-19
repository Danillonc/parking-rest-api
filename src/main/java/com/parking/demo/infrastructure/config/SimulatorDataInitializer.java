package com.parking.demo.infrastructure.config;

import com.parking.demo.domain.model.Garage;
import com.parking.demo.domain.model.Spot;
import com.parking.demo.domain.ports.out.GarageRepositoryPort;
import com.parking.demo.infrastructure.adapters.out.client.dto.SimulatorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Component
public class SimulatorDataInitializer implements ApplicationRunner {

    private final RestClient restClient;
    private final GarageRepositoryPort garageRepositoryPort;

    // Injetamos a URL via properties para não ficar "chumbada" (hardcoded) no código
    public SimulatorDataInitializer(
            RestClient.Builder restClientBuilder,
            GarageRepositoryPort garageRepositoryPort,
            @Value("${simulator.api.url:http://localhost:3000}") String simulatorUrl) {

        this.restClient = restClientBuilder.baseUrl(simulatorUrl).build();
        this.garageRepositoryPort = garageRepositoryPort;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("A iniciar a sincronização de dados da garagem com o simulador...");

        try {
            // 1. Faz a chamada HTTP GET /garage
            SimulatorResponseDTO response = restClient.get()
                    .uri("/garage")
                    .retrieve()
                    .body(SimulatorResponseDTO.class);

            if (response != null && response.garage() != null && response.spots() != null) {

                // 2. Converte os DTOs externos para os Modelos de Domínio Puros
                List<Garage> garagesDomain = response.garage().stream()
                        .map(dto -> new Garage(dto.sector(), dto.basePrice(), dto.maxCapacity()))
                        .toList();

                List<Spot> spotsDomain = response.spots().stream()
                        // Assumimos que na carga inicial todas as vagas estão livres (occupied = false)
                        .map(dto -> new Spot(dto.id(), dto.sector(), dto.lat(), dto.lng(), false))
                        .toList();

                // 3. Persiste na base de dados usando a nossa Porta de Saída
                garageRepositoryPort.syncGarageData(garagesDomain, spotsDomain);

                log.info("Sincronização concluída com sucesso. {} setores e {} vagas carregadas.",
                        garagesDomain.size(), spotsDomain.size());
            } else {
                log.warn("A resposta do simulador veio vazia ou num formato inválido.");
            }

        } catch (RestClientException e) {
            // Tratamento de resiliência: Se o simulador estiver em baixo, logamos o erro de forma clara.
            log.error("Falha ao contactar a API do simulador. A base de dados pode estar vazia. Erro: {}", e.getMessage());

        }
    }
}
