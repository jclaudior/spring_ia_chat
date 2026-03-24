package com.jcr.chat.application.service;

import com.jcr.chat.application.port.in.ConversationUserCase;
import com.jcr.chat.domain.model.ConversationMongo;
import com.jcr.chat.domain.model.dto.ConversationRequestDTO;
import com.jcr.chat.domain.model.dto.ConversationResponseDTO;
import com.jcr.chat.domain.model.dto.SessionResponseDTO;
import com.jcr.chat.domain.model.mapper.ConversationMapper;
import com.jcr.chat.infrastructure.adapter.out.persistence.ConversationMongoRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ConversationService implements ConversationUserCase {
    @Autowired
    private SessionService sessionService;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private KnowledgeBaseService knowledgeBaseService;

    @Autowired
    private ConversationMongoRepository conversationRepository;

    @Autowired
    private ConversationMapper mapper;

    @Override
    public ConversationResponseDTO createConversation(UUID sessionId, ConversationRequestDTO conversationRequestDTO) {
        SessionResponseDTO sessionResponse = sessionService.findById(sessionId);

        String context = searchContext(conversationRequestDTO.getUserMessage());

        String agentResponse = chatClient.prompt()
                .messages(List.of(
                        new SystemMessage("You are an intelligent assistant that answers based on provided context and conversation history.\n" +
                                "                        If the answer is not found in the context, say that you could not find the information.\n" +
                                "                        Be helpful, concise and professional."),
                        new SystemMessage("CONTEXT:\n" + context),
                        new UserMessage(conversationRequestDTO.getUserMessage())
                ))
                .call()
                .content();

        ConversationMongo conversationMongo = ConversationMongo.builder()
                .id(UUID.randomUUID().toString())
                .userId(sessionResponse.getUserId().toString())
                .sessionId(sessionResponse.getSessionId().toString())
                .interactions(List.of(
                        ConversationMongo.Interaction.builder()
                                .author("User")
                                .message(conversationRequestDTO.getUserMessage())
                                .build(),
                        ConversationMongo.Interaction.builder()
                                .author("Agent")
                                .message(agentResponse)
                                .build()
                ))
                .build();
        conversationMongo = conversationRepository.save(conversationMongo);
        return mapper.toDTO(conversationMongo);
    }

    private String searchContext(String question) {
        List<Document> documents = knowledgeBaseService.search(question, 3);

        if (documents.isEmpty()) {
            return "No relevant documents found in knowledge base.";
        }

        return documents.stream()
                .map(doc -> {
                    String content = doc.getText();
                    String metadata = doc.getMetadata().entrySet().stream()
                            .map(e -> e.getKey() + ": " + e.getValue())
                            .collect(Collectors.joining(", "));
                    return "Document: " + metadata + "\nContent: " + content;
                })
                .collect(Collectors.joining("\n\n---\n\n"));
    }
}
