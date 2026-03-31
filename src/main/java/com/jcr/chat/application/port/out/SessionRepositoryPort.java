package com.jcr.chat.application.port.out;

import com.jcr.chat.domain.model.SessionRedis;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepositoryPort {
    SessionRedis save(SessionRedis sessionRedis);

    Optional<SessionRedis> findById(UUID sessionId);

    Optional<SessionRedis> findByUserId(UUID userId);

    void delete(UUID sessionId);

}
