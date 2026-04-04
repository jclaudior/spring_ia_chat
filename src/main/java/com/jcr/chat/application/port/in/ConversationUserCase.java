package com.jcr.chat.application.port.in;

import com.jcr.chat.domain.model.dto.ConversationRequestDTO;
import com.jcr.chat.domain.model.dto.ConversationResponseDTO;
import com.jcr.chat.domain.model.dto.PaginationConversationResponseDTO;

import java.util.UUID;

public interface ConversationUserCase {
    ConversationResponseDTO createConversation(UUID sessionId, ConversationRequestDTO conversationRequestDTO);

    ConversationResponseDTO addInteraction(UUID conversationId, ConversationRequestDTO conversationRequestDTO);

    PaginationConversationResponseDTO listByUserId(UUID userId, int page, int limit);

    ConversationResponseDTO getById(UUID conversationId);
}
