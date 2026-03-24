package com.jcr.chat.application.port.in;

import com.jcr.chat.domain.model.dto.SessionRequestDTO;
import com.jcr.chat.domain.model.dto.SessionResponseDTO;

import java.util.Optional;
import java.util.UUID;

public interface SessionUseCase {
    SessionResponseDTO createSession(SessionRequestDTO sessionRequestDTO);

    SessionResponseDTO findById(UUID sessionId);

    Optional<SessionResponseDTO> findByUserId(UUID userId);

    void delete (UUID sessionId);
}
