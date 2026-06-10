Decisões Arquiteturais
Interpretação do problema

O sistema foi desenvolvido para manter o estoque consistente diante de eventos recebidos de marketplaces, considerando cenários como duplicidade, eventos fora de ordem e recomposição automática.

Fonte da verdade

A tabela Stock representa o estado atual do estoque.

A tabela StockEvent representa o histórico imutável de eventos recebidos.

A tabela OrderLifecycle representa o ciclo de vida dos pedidos.

Idempotência

Todo evento possui eventId único.

Antes do processamento é realizada verificação:

existsByEventId(...)

Eventos já processados são ignorados.

Duplicidade lógica

Pedidos cancelados não podem devolver estoque duas vezes.

Marketplace não pode recompor estoque duas vezes.

Estados específicos do pedido são utilizados para impedir reprocessamentos.

Eventos fora de ordem

Quando um cancelamento chega antes da criação do pedido:

status = PENDING

O evento permanece registrado para investigação posterior.

Concorrência

Foi utilizada estratégia de Optimistic Locking através do campo:

@Version
private Long version;

Evita sobrescrita silenciosa em atualizações simultâneas.

Múltiplas instâncias

A solução utiliza PostgreSQL como ponto central de consistência.

A unicidade de eventId impede duplicidade entre instâncias.

Kafka

Kafka não foi utilizado.

Para manter o escopo compatível com o prazo do desafio, optou-se por processamento síncrono via REST e persistência relacional.

A arquitetura foi organizada de forma que a substituição futura por Kafka seja simples.

Trade-offs

Foi priorizada clareza da regra de negócio em detrimento de mecanismos avançados de mensageria.

A solução busca simplicidade operacional mantendo rastreabilidade completa.

Simplificações
Sem fila assíncrona real
Sem retry automático de pendências
Sem scheduler para reprocessamento
Evolução para produção

Possíveis evoluções:

Kafka
Dead Letter Queue
Retry automático
Observabilidade distribuída
Testcontainers
Métricas de negócio
Dashboard operacional