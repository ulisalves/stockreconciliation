gubee-stock-reconciliation

Sistema responsável pela reconciliação de estoque entre plataforma interna e marketplaces.

Tecnologias
Java 17
Spring Boot 3
PostgreSQL
Docker Compose
Swagger/OpenAPI
Spring Actuator
Lombok
Maven
Como executar
Banco
docker compose up -d

Banco disponível:

localhost:5434
Aplicação
mvn spring-boot:run
Swagger
http://localhost:8080/swagger-ui.html
Actuator
http://localhost:8080/actuator
Endpoints
POST /events

Processa eventos de estoque.

Exemplo:

{
"eventId": "EVT-001",
"type": "ORDER_CREATED",
"accountId": "ACCOUNT-1",
"sku": "TV001",
"orderId": "ORDER-001",
"quantity": 2,
"occurredAt": "2026-06-10T10:00:00Z"
}
GET /stocks/{accountId}/{sku}

Retorna estoque atual.

GET /stocks/{accountId}/{sku}/history

Retorna histórico de eventos.

GET /events?status=PENDING

Retorna inconsistências pendentes.

Eventos suportados
STOCK_ADJUSTED
ORDER_CREATED
ORDER_CANCELLED
STOCK_SYNC_SENT
MARKETPLACE_STOCK_RESTORED
Como executar testes
mvn test
Limitações conhecidas
Reprocessamento automático de eventos pendentes não implementado.
Kafka substituído por processamento síncrono para simplificação do desafio.
Concorrência distribuída não implementada.

## Evidências

As evidências de execução encontram-se na pasta:

/docs/evidences

Incluindo:

- Swagger
- Actuator
- Fluxo de eventos
- Consultas SQL
- Cenários do desafio