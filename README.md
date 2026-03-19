# 🚗 Garage Simulator Backend API

Este projeto é uma API RESTful e um consumidor de eventos para gestão de um estacionamento inteligente, construído com foco em **alta volumetria, resiliência e isolamento de domínio**.

## 🏗️ Arquitetura e Decisões de Design

A aplicação foi desenhada utilizando a **Arquitetura Hexagonal (Ports & Adapters)** combinada com conceitos táticos de **Domain-Driven Design (DDD)** e **Event-Driven Architecture (EDA)**.

### 1. Isolamento de Domínio (O Core Puro)
O coração da aplicação (regras de negócio, cálculo de lotação e estratégia de preços dinâmicos) foi construído em **Puro Java**, sem qualquer dependência ou anotação do framework (Spring) ou de bibliotecas de infraestrutura (JPA/Hibernate, Kafka).
* **Ports (Portas):** Interfaces que definem os contratos de entrada (Use Cases) e saída (Repositories/Publishers).
* **Adapters (Adaptadores):** Implementações concretas na infraestrutura que conversam com o mundo externo (MySQL, Kafka, APIs de terceiros).
* **Vantagem:** O domínio é altamente testável via testes unitários que rodam em milissegundos, garantindo um ciclo de feedback rápido.

### 2. Ingestão Assíncrona (Mitigação de Gargalos)
Para evitar que picos de tráfego de veículos derrubem o banco de dados relacional, a API de Webhook atua no padrão **Fail-Fast & Async Processing**.
* O Controller apenas valida o formato do payload e a integridade da requisição (`@Valid`).
* Estando válido, o evento é publicado imediatamente no **Kafka** e a API retorna `202 Accepted`.
* **Vantagem (Shock Absorber):** O Kafka atua como um amortecedor. Se chegarem 10.000 requisições simultâneas, a borda HTTP responde em milissegundos e o banco de dados processa a fila no seu próprio ritmo (Backpressure), evitando indisponibilidade (Downtime).

### 3. Prevenção de Race Conditions e Concorrência Massiva
Calcular a lotação exata (`countActiveBySector`) no exato momento da entrada é um ponto crítico que frequentemente gera falhas de concorrência (*Race Conditions*) em bancos relacionais sob alta carga.
* **A Solução:** Ao publicar o evento no Kafka, utilizamos a **placa do veículo (`license_plate`) como Chave de Partição (Key)**.
* **Vantagem:** Isso garante a **ordenação estrita** dos eventos. Eventos de um mesmo carro (ENTRY -> PARKED -> EXIT) sempre cairão na mesma partição e serão processados sequencialmente. Além disso, o processamento enfileirado por setor evita que duas transações concorrentes leiam a mesma lotação de 99% simultaneamente e permitam uma sobrelotação (101%).

### 4. Padrão Strategy (OCP)
A lógica de preço dinâmico foi encapsulada na classe `DynamicPricingStrategy`. Caso no futuro surjam novas regras (ex: preço de feriado, preço VIP), basta adicionar uma nova estratégia sem alterar a classe principal de serviço, respeitando o Princípio Aberto/Fechado (Open/Closed Principle) do SOLID.

### 5. Tratamento de Erros e Resiliência
* **RFC 7807:** Todos os erros da API são padronizados usando `ProblemDetail`, oferecendo respostas claras e semânticas (ex: `422 Unprocessable Entity` para estacionamento cheio, `400 Bad Request` para erros de validação).
* **Anti-Corruption Layer:** Integração com a API do simulador via `RestClient` utiliza `@JsonAlias` nos DTOs para blindar nosso sistema contra mudanças repentinas de nomenclatura (ex: `basePrice` vs `base_price`) na API terceira.

---

## 🛠️ Tecnologias Utilizadas

* **Java 21** (Records, Pattern Matching)
* **Spring Boot 3.4** (Web, Data JPA, Kafka, Validation)
* **MySQL 8** (Persistência relacional)
* **Apache Kafka (KRaft)** (Mensageria assíncrona)
* **Docker & Docker Compose** (Infraestrutura e empacotamento)
* **JUnit 5 & Mockito** (Testes unitários e fatiados)

---

## 🚀 Como Executar o Projeto Localmente

O projeto está configurado para fácil execução via Docker, simulando um ambiente real.

### Pré-requisitos
* Docker e Docker Compose instalados.
* Porta `8081` livre na máquina host (usada pela nossa API).
* Porta `8080` livre na máquina host (usada pelo Kafka UI).

### Passo 1: Subir a Infraestrutura
Na raiz do projeto, execute o comando abaixo para subir o Banco de Dados, o Kafka, a interface gráfica do Kafka (Kafka-UI) e a API do Simulador da Estapar.

```bash
docker compose up -d mysql kafka kafka-ui simulator-api


(Aguarde cerca de 15 segundos para que o MySQL aceite conexões).

Passo 2: Iniciar a Aplicação (Backend)
Você pode rodar a aplicação pela sua IDE ou via terminal.

Via IDE (IntelliJ / Eclipse):
Abra a classe DemoApplication.java e execute (Run/Play).

Via Terminal (Maven):
./mvnw spring-boot:run

⚠️ Ponto de Atenção no Startup:
Durante a inicialização, a aplicação fará uma chamada GET à API do Simulador para popular o banco de dados. Procure no seu console pela seguinte mensagem de sucesso:

Sincronização concluída com sucesso. X setores e Y vagas carregadas.

🧹 Troubleshooting: Limpeza do Banco de Dados
Caso precise resetar o estado da aplicação (limpar tickets e garagens) sem precisar reiniciar todo o Docker Compose, execute este comando no terminal para recriar o banco de dados limpo:



🧪 Documentação da API e Testes Funcionais (cURL)
Dispare os comandos abaixo no seu terminal (ou importe-os no Postman) para simular o ciclo de vida completo de um veículo.

1. Registrar Entrada (Webhook - ENTRY)
Valida a requisição, envia para o Kafka, calcula o preço dinâmico e salva o ticket.

curl --location 'http://localhost:8081/webhook' \
--header 'Content-Type: application/json' \
--data '{
  "license_plate": "ZUL0001",
  "entry_time": "2026-03-19T12:00:00.000Z",
  "sector": "A",
  "event_type": "ENTRY"
}'

2. Confirmar Estacionamento (Webhook - PARKED)
Atualiza o ticket existente com as coordenadas da vaga.

curl --location 'http://localhost:8081/webhook' \
--header 'Content-Type: application/json' \
--data '{
  "license_plate": "ZUL0001",
  "lat": -23.561684,
  "lng": -46.655981,
  "event_type": "PARKED"
}'

3. Registrar Saída (Webhook - EXIT)
Finaliza a estadia e consolida o totalAmount cobrado.

curl --location 'http://localhost:8081/webhook' \
--header 'Content-Type: application/json' \
--data '{
  "license_plate": "ZUL0001",
  "exit_time": "2026-03-19T14:30:00.000Z",
  "event_type": "EXIT"
}'

4. Consultar Faturamento Diário (REST API Síncrona)
Busca no banco de dados a soma de todos os tickets finalizados em um determinado setor e data.

curl --location 'http://localhost:8081/revenue?date=2026-03-19&sector=A'

5. Teste de Resiliência (Fail Fast - Erro 400)
Teste o envio de um payload sem a placa do veículo para validar a padronização de erros RFC 7807 antes mesmo de onerar o Kafka.

curl --location 'http://localhost:8081/webhook' \
--header 'Content-Type: application/json' \
--data '{
  "entry_time": "2026-03-19T12:00:00.000Z",
  "sector": "A",
  "event_type": "ENTRY"
}'

