package com.jcr.chat.infrastructure.adapter.in.web;

import com.jcr.chat.application.port.in.ConversationUserCase;
import com.jcr.chat.domain.model.dto.ConversationRequestDTO;
import com.jcr.chat.domain.model.dto.ConversationResponseDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;


@Log4j2
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private ConversationUserCase conversationUserCase;

    @PostMapping("/{conversationId}/interaction")
    public ResponseEntity<ConversationResponseDTO> createConversation(
            @PathVariable UUID conversationId,
            @RequestBody ConversationRequestDTO conversationRequestDTO) {

        ConversationResponseDTO response = conversationUserCase.addInteraction(conversationId, conversationRequestDTO);

        URI location = URI.create("/conversation/" + response.getId());
        return ResponseEntity.created(location).body(response);

    }

}

