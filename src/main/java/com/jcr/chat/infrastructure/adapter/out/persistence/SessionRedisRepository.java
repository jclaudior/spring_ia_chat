package com.jcr.chat.infrastructure.adapter.out.persistence;

import com.jcr.chat.domain.model.SessionRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRedisRepository extends CrudRepository<SessionRedis, UUID> {
    Optional<SessionRedis> findByUserId(UUID userId);
}
