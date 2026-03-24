package com.jcr.chat.domain.model.mapper;

import com.jcr.chat.domain.model.ConversationMongo;
import com.jcr.chat.domain.model.dto.ConversationResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConversationMapper {
    ConversationResponseDTO toDTO(ConversationMongo conversationMongo);
}
