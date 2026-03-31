package com.jcr.chat.infrastructure.adapter.in.web;

import com.jcr.chat.application.port.in.ConversationUserCase;
import com.jcr.chat.domain.model.dto.ConversationRequestDTO;
import com.jcr.chat.domain.model.dto.ConversationResponseDTO;
import com.jcr.chat.domain.model.dto.PaginationConversationResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;


@Log4j2
@RestController
@RequestMapping("/conversations")
public class ConversationController {

    @Autowired
    private ConversationUserCase conversationUserCase;

    @GetMapping
    public ResponseEntity<PaginationConversationResponseDTO> listConversations(
            @RequestParam UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        PaginationConversationResponseDTO response =
                conversationUserCase.listByUserId(userId, page, limit);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversations/{id}")
    public ResponseEntity<ConversationResponseDTO> getConversationById(
            @PathVariable UUID id) {

        ConversationResponseDTO response = conversationUserCase.getById(id);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{conversationId}/interactions")
    public ResponseEntity<ConversationResponseDTO> createConversation(
            @PathVariable UUID conversationId,
            @RequestBody ConversationRequestDTO conversationRequestDTO) {

        ConversationResponseDTO response = conversationUserCase.addInteraction(conversationId, conversationRequestDTO);

        URI location = URI.create("/conversations/" + response.getId());
        return ResponseEntity.created(location).body(response);

    }

}

