package com.jcr.chat.domain.model.mapper;

import com.jcr.chat.domain.model.SessionRedis;
import com.jcr.chat.domain.model.dto.SessionRequestDTO;
import com.jcr.chat.domain.model.dto.SessionResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SessionMapper {
    SessionRedis toEntity(SessionRequestDTO sessionRequestDTO);

    SessionResponseDTO toDTO(SessionRedis sessionRedis);
}
