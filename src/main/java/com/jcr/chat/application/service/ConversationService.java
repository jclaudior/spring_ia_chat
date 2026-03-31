package com.jcr.chat.application.service;

import com.jcr.chat.application.port.in.ConversationUserCase;
import com.jcr.chat.domain.model.ConversationMongo;
import com.jcr.chat.domain.model.dto.*;
import com.jcr.chat.domain.model.mapper.ConversationMapper;
import com.jcr.chat.infrastructure.adapter.out.persistence.ConversationMongoRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.document.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConversationService implements ConversationUserCase {

    private static final String SYSTEM_PROMPT = """
        You are an intelligent assistant.

        Rules:
        - Always prioritize information from CONTEXT.
        - If the answer is not in the context, say clearly you don't know.
        - Do not hallucinate.
        - Keep answers concise and direct.
        - Use conversation history to maintain continuity.

        Output:
        - Answer the user question
        - Optionally infer user intent (short)
        """;

    private static final int HISTORY_LIMIT = 10;
    private static final int CONTEXT_LIMIT = 3;

    private final SessionService sessionService;
    private final ChatClient chatClient;
    private final KnowledgeBaseService knowledgeBaseService;
    private final ConversationMongoRepository repository;
    private final ConversationMapper mapper;

    public ConversationService(SessionService sessionService,
                               ChatClient chatClient,
                               KnowledgeBaseService knowledgeBaseService,
                               ConversationMongoRepository repository,
                               ConversationMapper mapper) {
        this.sessionService = sessionService;
        this.chatClient = chatClient;
        this.knowledgeBaseService = knowledgeBaseService;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public ConversationResponseDTO createConversation(UUID sessionId, ConversationRequestDTO request) {
        SessionResponseDTO session = getAndUpdateSession(sessionId);

        String context = searchContext(request.getUserMessage());
        String response = generateResponse(buildMessages(null, context, request.getUserMessage()));

        ConversationMongo conversation = buildNewConversation(session, request.getUserMessage(), response);

        return mapper.toDTO(repository.save(conversation));
    }

    @Override
    public ConversationResponseDTO addInteraction(UUID conversationId, ConversationRequestDTO request) {
        ConversationMongo conversation = findConversation(conversationId);

        getAndUpdateSession(UUID.fromString(conversation.getSessionId()));

        String context = searchContext(request.getUserMessage());

        List<Message> messages = buildMessages(conversation, context, request.getUserMessage());
        String response = generateResponse(messages);

        updateConversation(conversation, request.getUserMessage(), response);

        return mapper.toDTO(repository.save(conversation));
    }

    @Override
    public PaginationConversationResponseDTO listByUserId(UUID userId, int page, int limit) {

        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "updatedAt"));

        Page<ConversationMongo> pageResult =
                repository.findByUserId(userId.toString(), pageable);

        List<ConversationResponseDTO> content = pageResult.getContent()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        PaginationDTO pagination = PaginationDTO.builder()
                .limit(limit)
                .offset(page * limit)
                .pageNumber(page)
                .pageElements(pageResult.getNumberOfElements())
                .totalPages(pageResult.getTotalPages())
                .totalElements(pageResult.getTotalElements())
                .build();

        return PaginationConversationResponseDTO.builder()
                .content(content)
                .pageable(pagination)
                .build();
    }

    @Override
    public ConversationResponseDTO getById(UUID conversationId) {
        ConversationMongo conversation = repository.findById(conversationId.toString())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        return mapper.toDTO(conversation);
    }

    // =========================
    // Core Methods
    // =========================

    private List<Message> buildMessages(ConversationMongo conversation, String context, String userMessage) {
        List<Message> messages = new ArrayList<>();

        messages.add(new SystemMessage(SYSTEM_PROMPT));
        messages.add(new SystemMessage("CONTEXT:\n" + context));

        if (conversation != null) {
            getLastInteractions(conversation.getInteractions())
                    .forEach(interaction -> messages.add(mapToMessage(interaction)));
        }

        messages.add(new UserMessage(userMessage));

        return messages;
    }

    private String generateResponse(List<Message> messages) {
        return chatClient.prompt()
                .messages(messages)
                .call()
                .content();
    }

    private void updateConversation(ConversationMongo conversation, String userMessage, String response) {
        conversation.setUpdatedAt(Instant.now());

        conversation.getInteractions().addAll(List.of(
                createInteraction("user", userMessage),
                createInteraction("assistant", response)
        ));
    }

    private ConversationMongo buildNewConversation(SessionResponseDTO session,
                                                   String userMessage,
                                                   String response) {
        return ConversationMongo.builder()
                .id(UUID.randomUUID().toString())
                .title(generateTitleFallback(userMessage))
                .userId(session.getUserId().toString())
                .sessionId(session.getSessionId().toString())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .interactions(new ArrayList<>(List.of(
                        createInteraction("user", userMessage),
                        createInteraction("assistant", response)
                )))
                .build();
    }

    // =========================
    // Context / RAG
    // =========================

    private String searchContext(String question) {
        List<Document> documents = knowledgeBaseService.search(question, CONTEXT_LIMIT);

        if (documents.isEmpty()) {
            return "No relevant documents found in knowledge base.";
        }

        return documents.stream()
                .map(doc -> """
                        ### DOCUMENT
                        Metadata: %s
                        Content:
                        %s
                        """.formatted(doc.getMetadata(), doc.getText()))
                .collect(Collectors.joining("\n\n"));
    }

    // =========================
    // Helpers
    // =========================

    private SessionResponseDTO getAndUpdateSession(UUID sessionId) {
        SessionResponseDTO session = sessionService.findById(sessionId);
        sessionService.createSession(SessionRequestDTO.builder()
                .userId(session.getUserId())
                .build());
        return session;
    }

    private ConversationMongo findConversation(UUID conversationId) {
        return repository.findById(conversationId.toString())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    private List<ConversationMongo.Interaction> getLastInteractions(List<ConversationMongo.Interaction> interactions) {
        int fromIndex = Math.max(0, interactions.size() - HISTORY_LIMIT);
        return interactions.subList(fromIndex, interactions.size());
    }

    private Message mapToMessage(ConversationMongo.Interaction interaction) {
        return "user".equalsIgnoreCase(interaction.getAuthor())
                ? new UserMessage(interaction.getMessage())
                : new AssistantMessage(interaction.getMessage());
    }

    private ConversationMongo.Interaction createInteraction(String author, String message) {
        return ConversationMongo.Interaction.builder()
                .author(author)
                .message(message)
                .build();
    }

    private String generateTitleFallback(String message) {
        return message.length() > 40 ? message.substring(0, 40) + "..." : message;
    }
}