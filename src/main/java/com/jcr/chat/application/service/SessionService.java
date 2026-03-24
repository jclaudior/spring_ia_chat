package com.jcr.chat.application.service;

import com.jcr.chat.application.port.in.SessionUseCase;
import com.jcr.chat.domain.model.SessionNotFoundException;
import com.jcr.chat.domain.model.SessionRedis;
import com.jcr.chat.domain.model.dto.SessionRequestDTO;
import com.jcr.chat.domain.model.dto.SessionResponseDTO;
import com.jcr.chat.domain.model.mapper.SessionMapper;
import com.jcr.chat.infrastructure.adapter.out.persistence.SessionRedisPersistenceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService implements SessionUseCase {

    @Autowired
    private SessionRedisPersistenceAdapter redisPersistenceAdapter;

    @Autowired
    private SessionMapper mapper;

    @Override
    public SessionResponseDTO createSession(SessionRequestDTO sessionRequestDTO) {
        Optional<SessionRedis> existing = redisPersistenceAdapter.findByUserId(sessionRequestDTO.getUserId());
        if (existing.isPresent()) {
            redisPersistenceAdapter.setTTL(existing.get().getSessionId(), 300);
            return mapper.toDTO(existing.get());
        } else {
            SessionRedis session = mapper.toEntity(sessionRequestDTO);
            session.setSessionId(UUID.randomUUID());
            return mapper.toDTO(redisPersistenceAdapter.save(session));
        }
    }

    @Override
    public SessionResponseDTO findById(UUID sessionId) {
        Optional<SessionRedis> optional = redisPersistenceAdapter.findById(sessionId);
        if (optional.isEmpty()) {
            throw new SessionNotFoundException("Session not found with id: " + sessionId);
        }
        return mapper.toDTO(optional.get());
    }

    @Override
    public void delete(UUID sessionId) {
        if (redisPersistenceAdapter.findById(sessionId).isEmpty()) {
            throw new SessionNotFoundException("Session not found with id: " + sessionId);
        }
        redisPersistenceAdapter.delete(sessionId);
    }

    @Override
    public Optional<SessionResponseDTO> findByUserId(UUID userId) {
        return redisPersistenceAdapter.findByUserId(userId).map(mapper::toDTO);
    }
}
