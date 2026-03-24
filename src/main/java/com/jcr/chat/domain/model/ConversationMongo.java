package com.jcr.chat.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "conversations")
public class ConversationMongo implements Serializable {
    @Id
    private String id; // MongoDB usa String para ID
    private String userId;
    private String sessionId;
    private List<Interaction> interactions;
    private Long createdAt;
    private Long updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Interaction implements Serializable {
        private String author;
        private String message;
    }
}

