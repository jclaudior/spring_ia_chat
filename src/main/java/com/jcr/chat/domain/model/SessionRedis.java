package com.jcr.chat.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "session", timeToLive = 300) // 300s = 5 min
public class SessionRedis implements Serializable {

    @Id
    private UUID sessionId;

    private UUID userId;
}
