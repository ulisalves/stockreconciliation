# 🚀 Gubee Stock Reconciliation

Sistema responsável pela reconciliação de estoque entre plataforma interna e marketplaces.

---

## 📋 Objetivo

O projeto simula um cenário real de integração com marketplaces, tratando problemas comuns como:

- Idempotência
- Eventos duplicados
- Eventos fora de ordem
- Recomposição automática de estoque
- Rastreabilidade completa
- Consistência de estoque
- Concorrência

---

## 🛠 Tecnologias Utilizadas

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge)
![Swagger](https://img.shields.io/badge/OpenAPI-Swagger-85EA2D?style=for-the-badge)
![Actuator](https://img.shields.io/badge/Spring_Actuator-6DB33F?style=for-the-badge)
![JUnit](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge)

---

## 🏗 Arquitetura

O projeto foi organizado seguindo princípios de Arquitetura Hexagonal (Ports & Adapters), separando:

- Domínio
- Aplicação
- Adaptadores de Entrada
- Adaptadores de Saída
- Persistência

Objetivo:

- Baixo acoplamento
- Facilidade de manutenção
- Facilidade de evolução futura (Kafka, filas, etc.)

---

## ▶ Como Executar

### 1. Subir banco de dados

```bash
  docker compose up -d
Banco disponível em: localhost:5434

```
### 2. Executar aplicação
```bash
  mvn spring-boot:run
```
### 📚 Documentação da API

```Swagger:
  http://localhost:8080/swagger-ui.html

```
### ❤️ Health Check

```Spring Actuator:
  http://localhost:8080/actuator

```
### 🔌 Endpoints

```
  POST /events (Processa eventos de estoque)

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
  Consulta estoque atual.

GET /stocks/{accountId}/{sku}/history   
    Consulta histórico completo de eventos.

GET /events?status=PENDING
    Consulta eventos pendentes

```
📦 Eventos Suportados

| Evento                     | Descrição                            |
| -------------------------- | ------------------------------------ |
| STOCK_ADJUSTED             | Ajuste manual de estoque             |
| ORDER_CREATED              | Criação de pedido                    |
| ORDER_CANCELLED            | Cancelamento de pedido               |
| STOCK_SYNC_SENT            | Sincronização enviada ao marketplace |
| MARKETPLACE_STOCK_RESTORED | Recomposição automática              |


### 🧪 Testes

    mvn test
    
    Cobertura dos cenários:
    Pedido criado
    Pedido cancelado
    Evento duplicado
    Cancelamento duplicado
    Evento pendente
    Recomposição única de estoque

### ⚠ Limitações Conhecidas
    Sem reprocessamento automático de eventos pendentes
    Sem mensageria Kafka
    Sem retry automático
    Sem concorrência distribuída entre múltiplos nós

### 📸 Evidências
    /docs/evidences

    Incluindo:
    Swagger
    Actuator
    Fluxo completo via Bruno
    Consultas SQL
    Cenários do desafio

### 👨‍💻 Autor
    Ulisses Alves de Melo

    Desafio Técnico — Desenvolvedor Java Pleno
