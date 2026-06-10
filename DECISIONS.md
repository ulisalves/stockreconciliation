# 📘 Decisão de arquitetura

## 1. Interpretação do Problema

O desafio consiste em manter o estoque consistente a partir de eventos recebidos de marketplaces.

Os principais riscos identificados foram:

- Eventos duplicados
- Cancelamentos duplicados
- Eventos fora de ordem
- Recomposição indevida de estoque
- Atualizações concorrentes
- Necessidade de rastreabilidade

A solução foi construída priorizando consistência e auditabilidade.

---

## 2. Fonte da Verdade

A fonte da verdade é o histórico de eventos processados

Em complemente, a solução utiliza três entidades principais:

### Stock

Representa o estado atual do estoque.

### StockEvent

Representa o histórico imutável de eventos recebidos.

### OrderLifecycle

Representa o ciclo de vida do pedido.

Dessa forma:

- Stock = estado atual
- StockEvent = auditoria
- OrderLifecycle = contexto de negócio

---

## 3. Idempotência

Todo evento possui um identificador único:

```java
eventId
```

```
Antes do processamento é realizada validação:
existsByEventId(...)
```
```
Caso o evento já exista:
IGNORED
Isso impede processamento duplicado.
```

## 4. Duplicidade Lógica

    Foram tratados dois cenários principais:
    Cancelamento duplicado, onde Um pedido cancelado não pode recompor estoque mais de uma vez.

    Marketplace restore duplicado
    A recomposição automática do marketplace não pode ocorrer duas vezes.
    O controle é realizado através do estado do pedido.

## 5. Eventos Fora de Ordem

Quando um cancelamento é recebido antes da criação do pedido fica com status:

    PENDING

O evento permanece registrado para investigação ou reprocessamento futuro.
Nenhuma alteração de estoque é realizada.

## 6. Concorrência

    Foi utilizada estratégia de Optimistic Locking 
    (Bloqueio Otimista) que assumi que colisões de dados são raras

```
@Version
private Long version;

Essa abordagem evita sobrescrita silenciosa em atualizações simultâneas do mesmo SKU.
Foi escolhida por sua simplicidade e aderência ao contexto do desafio.

```
## 7. Múltiplas Instâncias

A consistência é garantida através de:

- PostgreSQL centralizado
- Chave única de eventId
- Controle transacional com @Transactional

Isso reduz o risco de processamento duplicado mesmo em múltiplas instâncias.

## 8. Kafka

Kafka não foi utilizado.
A decisão foi tomada para manter o escopo compatível com o prazo do desafio.
A aplicação utiliza processamento síncrono via REST.

A arquitetura foi organizada para permitir evolução futura para mensageria assíncrona sem impacto significativo no domínio.

## 9. Trade-offs Assumidos

Foi priorizado:

- Clareza da regra de negócio
- Simplicidade operacional
- Rastreabilidade completa

Em vez de:

- Infraestrutura distribuída
- Processamento assíncrono
- Alta complexidade operacional

## 10. Simplificações

Por restrição de tempo foram simplificados:

- Reprocessamento automático de pendências
- Retry automático
- Dead Letter Queue
- Kafka
- Scheduler de reconciliação

## 11. O que faria diferente em produção

Para produção, talvez uma evolução do projeto:

    Apache Kafka
    Dead Letter Queue (DLQ)
    Retry automático
    Scheduler de reprocessamento
    Observabilidade distribuída
    OpenTelemetry
    Testcontainers
    Métricas de negócio
    Dashboard operacional
    Cache distribuído