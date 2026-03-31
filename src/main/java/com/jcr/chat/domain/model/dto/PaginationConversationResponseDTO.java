package com.jcr.chat.domain.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginationConversationResponseDTO {
    @JsonProperty("pageable")
    PaginationDTO pageable;
    @JsonProperty("content")
    List<ConversationResponseDTO> content;
}