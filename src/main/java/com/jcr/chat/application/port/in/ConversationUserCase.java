package com.jcr.chat.application.port.in;

import com.jcr.chat.domain.model.ConversationMongo;
import com.jcr.chat.domain.model.dto.ConversationRequestDTO;
import com.jcr.chat.domain.model.dto.ConversationResponseDTO;
import com.jcr.chat.domain.model.dto.PaginationConversationResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ConversationUserCase {
    ConversationResponseDTO createConversation(UUID sessionId, ConversationRequestDTO conversationRequestDTO);

    ConversationResponseDTO addInteraction(UUID conversationId, ConversationRequestDTO conversationRequestDTO);

    PaginationConversationResponseDTO listByUserId(UUID userId, int page, int limit);
}
