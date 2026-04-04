package com.jcr.chat.infrastructure.adapter.out.persistence;

import com.jcr.chat.domain.model.ConversationRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRedisRepository extends CrudRepository<ConversationRedis, UUID> {
    List<ConversationRedis> findBySessionId(UUID sessionId);
}

