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
public class ConversationResponseDTO implements Serializable {
    private UUID id;
    private UUID sessionId;
    private UUID userId;
    private String title;
    private List<InteractionDTO> interactions;
    private Long createdAt;

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

