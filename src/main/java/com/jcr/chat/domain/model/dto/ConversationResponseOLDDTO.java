package com.jcr.chat.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationResponseOLDDTO implements Serializable {
    private UUID conversationId;
    private UUID sessionId;
    private String content;
    private List<IntentionOLDDTO> intentions;
    private Long createdAt;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

