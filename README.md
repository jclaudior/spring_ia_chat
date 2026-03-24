# 🤖 Chat Application - Sistema de Conversas com RAG e OpenAI

> Aplicação para gerenciar conversas com inteligência artificial, utilizando busca semântica em base de conhecimento (RAG) com pgVector.

## ✨ Características Principais

- 🔄 **Conversas Multi-turno** - Mantém histórico completo de mensagens
- 🧠 **RAG (Retrieval-Augmented Generation)** - Busca semântica em base de conhecimento
- 🤖 **Integração OpenAI** - Usa GPT-4o mini para gerar respostas
- 🎯 **Detecção de Intenções** - Identifica intenções dos usuários automaticamente
- ⚡ **Cache Redis** - Armazenamento rápido de sessões e conversas
- 🔐 **Sessões Seguras** - Com expiração automática (TTL)
- 📊 **REST API** - Endpoints bem documentados

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────────────┐
│           REST API Controllers                  │
│  (SessionController, ConversationController)    │
└─────────────────────────────────────────────────┘
                      ↓
┌─────────────────────────────────────────────────┐
│           Business Logic Services               │
│ (SessionService, ConversationService, ChatService) 
└─────────────────────────────────────────────────┘
                      ↓
┌──────────────────────┬──────────────────────────┐
│   Redis Cache        │    OpenAI + RAG          │
│ (Sessions, Convos)   │ (ChatClient, KnowledgeBase)
└──────────────────────┴──────────────────────────┘
                      ↓
┌──────────────────────┬──────────────────────────┐
│  PostgreSQL Redis    │  PostgreSQL pgVector     │
│  (Session Storage)   │  (Knowledge Base Search) │
└──────────────────────┴──────────────────────────┘
```

## 📋 Pré-requisitos

- **Java 17+**
- **Maven 3.9.12+**
- **Redis** rodando em `localhost:6379`
- **PostgreSQL** com pgVector rodando em `localhost:5432`
- **Chave de API OpenAI** (veja configuração abaixo)

## 🚀 Como Iniciar

### 1. Clonar o Repositório
```bash
git clone <repo-url>
cd chat
```

### 2. Configurar Variáveis de Ambiente
```bash
export OPENAI_API_KEY="sk-your-api-key-here"
```

Ou adicione no `application.yaml`:
```yaml
spring:
  ai:
    openai:
      api-key: sk-your-api-key-here
```

### 3. Iniciar Redis
```bash
redis-server
```

### 4. Iniciar PostgreSQL com pgVector
```bash
# Docker
docker run -d \
  --name postgres-pgvector \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  pgvector/pgvector:latest
```

### 5. Compilar e Rodar a Aplicação
```bash
./mvnw clean compile
./mvnw spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080/agent_conversation/v1`

## 📚 Endpoints Principais

### Sessões
- `POST /session` - Criar nova sessão
- `GET /session/{sessionId}` - Obter sessão
- `DELETE /session/{sessionId}` - Deletar sessão

### Conversas
- `POST /conversation/{sessionId}/conversation` - Criar conversa
- `POST /conversation/{conversationId}/interaction` - Adicionar mensagem
- `GET /conversation/{conversationId}` - Obter conversa
- `GET /conversation/session/{sessionId}` - Listar conversas da sessão
- `DELETE /conversation/{conversationId}` - Deletar conversa

Ver arquivo `EXEMPLOS_REQUISICOES.md` para detalhes completos com curl/Postman.

## ⚙️ Configuração

### application.yaml
```yaml
spring:
  application:
    name: chat
  data:
    redis:
      host: localhost
      port: 6379
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini
      embedding:
        options:
          model: text-embedding-3-small
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 1536

server:
  servlet:
    context-path: /agent_conversation/v1
```

## 🧠 Fluxo de Processamento

### Criar Conversa
```
1. POST /conversation/{sessionId}/conversation
   └─> ConversationController.createConversation()
       ├─> SessionService.findById() - Validar sessão
       ├─> ChatService.processMessage()
       │   ├─> KnowledgeBaseService.search() - Buscar contexto no pgVector
       │   ├─> Formatar contexto + histórico
       │   └─> OpenAI ChatClient - Gerar resposta
       ├─> ChatService.extractIntentions() - Detectar intenções
       ├─> ConversationRedisRepository.save() - Salvar no Redis (24h TTL)
       └─> Return ConversationResponseDTO
```

### Adicionar Interação
```
1. POST /conversation/{conversationId}/interaction
   └─> ConversationController.addInteraction()
       ├─> ConversationRedisRepository.findById() - Buscar conversa
       ├─> ChatService.processMessage() - Processar com histórico completo
       ├─> Adicionar mensagens ao histórico
       ├─> Atualizar TTL da conversa (24h)
       ├─> ConversationRedisRepository.save() - Salvar no Redis
       └─> Return ConversationResponseDTO
```

## 🎯 Intenções Detectadas

O sistema detecta automaticamente as seguintes intenções:

| Intenção | Exemplos | Confiança |
|----------|----------|-----------|
| **HELP_REQUEST** | "me ajuda", "help" | 0.95 |
| **GRATITUDE** | "obrigado", "valeu", "thanks" | 0.90 |
| **PROBLEM_REPORT** | "problema", "erro", "bug" | 0.92 |
| **INFORMATION_QUERY** | "qual", "saber", "what", "quem" | 0.88 |
| **HOW_TO_REQUEST** | "como fazer", "como", "how to" | 0.85 |
| **GENERAL_QUERY** | (padrão) | 0.70 |

## 📦 Estrutura de Diretórios

```
src/main/java/com/jcr/chat/
├── Application.java
├── application/
│   ├── port/
│   │   └── in/
│   │       └── SessionUseCase.java
│   └── service/
│       ├── ChatService.java
│       ├── ConversationService.java
│       ├── KnowledgeBaseService.java
│       └── SessionService.java
├── config/
│   ├── ChatClientConfig.java
│   ├── RedisRepositoryConfig.java
│   └── RedisRepositoryConfig.java
├── domain/
│   └── model/
│       ├── ConversationRedis.java
│       ├── SessionRedis.java
│       ├── SessionNotFoundException.java
│       ├── dto/
│       │   ├── ConversationRequestDTO.java
│       │   ├── ConversationResponseDTO.java
│       │   ├── InteractionRequestDTO.java
│       │   ├── IntentionDTO.java
│       │   ├── SessionRequestDTO.java
│       │   └── SessionResponseDTO.java
│       └── mapper/
│           └── SessionMapper.java
└── infrastructure/
    ├── adapter/
    │   ├── in/
    │   │   └── web/
    │   │       ├── ConversationController.java
    │   │       └── SessionController.java
    │   └── out/
    │       └── persistence/
    │           ├── ConversationRedisRepository.java
    │           ├── SessionRedisPersistenceAdapter.java
    │           └── SessionRedisRepository.java
    └── exception/
        └── GlobalExceptionHandler.java
```

## 🔒 Tratamento de Erros

Todos os erros são tratados globalmente com HTTP status apropriados:

- **404 Not Found** - Sessão ou conversa não encontrada
- **400 Bad Request** - Dados inválidos
- **500 Internal Server Error** - Erro do servidor

Exemplo de erro:
```json
{
  "timestamp": "2026-03-22T14:21:51.669Z",
  "status": 404,
  "error": "Not Found",
  "message": "Session not found with id: invalid-uuid",
  "path": "/agent_conversation/v1/session/invalid-uuid"
}
```

## 💾 Armazenamento de Dados

### Redis
- **Sessions**: TTL 5 minutos
- **Conversations**: TTL 24 horas
- Chave: `Session:{sessionId}`, `Conversation:{conversationId}`

### PostgreSQL
- Conexão configurada em `application.yaml`
- pgVector para armazenar embeddings (dimensão: 1536)

## 🧪 Testando a API

### Com curl
```bash
# 1. Criar sessão
SESSION_ID=$(curl -X POST http://localhost:8080/agent_conversation/v1/session \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123"}' | jq -r '.sessionId')

# 2. Criar conversa
CONV_ID=$(curl -X POST http://localhost:8080/agent_conversation/v1/conversation/$SESSION_ID/conversation \
  -H "Content-Type: application/json" \
  -d '{"userMessage": "Qual é o preço?"}' | jq -r '.conversationId')

# 3. Adicionar interação
curl -X POST http://localhost:8080/agent_conversation/v1/conversation/$CONV_ID/interaction \
  -H "Content-Type: application/json" \
  -d '{"userMessage": "Tem em estoque?"}'
```

### Com Postman
Importe a coleção fornecida: `postman_collection.json`

## 📊 Dependências Principais

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
</dependency>
```

## 🔄 Fluxo de Conversas Multi-turno

1. **Primeira Mensagem**: Cria conversa, busca contexto, processa com OpenAI
2. **Mensagens Subsequentes**: Usa histórico completo + novo contexto RAG
3. **TTL Automático**: Conversa expira em 24h se não tiver atividade
4. **Intenções**: Detectadas em cada turno

## 🚀 Próximos Passos

- [ ] Adicionar persistência permanente em PostgreSQL
- [ ] Implementar processamento assíncrono com filas
- [ ] Adicionar feedback de usuário para intenções
- [ ] Criar dashboard de análise
- [ ] Implementar contexto customizável por domínio
- [ ] Adicionar suporte a múltiplos modelos de IA

## 📝 Logs

A aplicação usa **Log4j2** para logging. Exemplo:
```
[INFO] Creating conversation for session: a550e8400-e29b-41d4-a716-446655440001
[INFO] Processing user message: Qual é o preço?
[INFO] Retrieved context from knowledge base
[INFO] Extracted 1 intentions from user message
[INFO] Conversation created with id: b660e8400-e29b-41d4-a716-446655440002
```

## 🤝 Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

MIT License - veja arquivo LICENSE para detalhes

## ✉️ Suporte

Para dúvidas ou issues, abra um GitHub Issue ou entre em contato.

---

**Última atualização**: Março 2026
**Status**: ✅ Build Success
**Java**: 17+
**Spring Boot**: 4.0.2

