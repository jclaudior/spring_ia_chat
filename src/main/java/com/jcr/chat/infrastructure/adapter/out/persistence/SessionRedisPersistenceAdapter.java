package com.jcr.chat.infrastructure.adapter.out.persistence;

import com.jcr.chat.application.port.out.SessionRepositoryPort;
import com.jcr.chat.domain.model.SessionRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class SessionRedisPersistenceAdapter implements SessionRepositoryPort {

    @Autowired
    private SessionRedisRepository repository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public SessionRedis save(SessionRedis sessionRedis) {
        return repository.save(sessionRedis);
    }

    @Override
    public Optional<SessionRedis> findById(UUID sessionId) {
        return repository.findById(sessionId);
    }

    @Override
    public Optional<SessionRedis> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public void delete(UUID sessionId) {
        repository.deleteById(sessionId);
    }

    public void persist(UUID sessionId) {
        redisTemplate.persist("session:" + sessionId);
    }

    public void setTTL(UUID sessionId, long seconds) {
        redisTemplate.expire("session:" + sessionId, seconds, TimeUnit.SECONDS);
    }
}
