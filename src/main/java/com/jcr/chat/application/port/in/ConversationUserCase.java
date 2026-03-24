package com.jcr.chat.application.port.in;

import com.jcr.chat.domain.model.dto.ConversationRequestDTO;
import com.jcr.chat.domain.model.dto.ConversationResponseDTO;

import java.util.UUID;

public interface ConversationUserCase {
    ConversationResponseDTO createConversation(UUID sessionId, ConversationRequestDTO conversationRequestDTO);
}
