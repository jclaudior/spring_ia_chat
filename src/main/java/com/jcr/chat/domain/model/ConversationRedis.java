package com.jcr.chat.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Conversation")
public class ConversationRedis implements Serializable {
    @Id
    private UUID conversationId;
    private UUID sessionId;
    private List<Message> messages;
    private Long createdAt;
    
    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message implements Serializable {
        private String role; // "user" ou "assistant"
        private String content;
        private List<Intention> intentions;
        private Long timestamp;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Intention implements Serializable {
        private String intention;
        private Double confidence;
    }
}

