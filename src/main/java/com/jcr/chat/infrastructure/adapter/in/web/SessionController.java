package com.jcr.chat.infrastructure.adapter.in.web;

import com.jcr.chat.application.port.in.ConversationUserCase;
import com.jcr.chat.application.port.in.SessionUseCase;
import com.jcr.chat.domain.model.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

import static reactor.netty.http.HttpConnectionLiveness.log;

@RestController
@RequestMapping("/session")
public class SessionController {

    @Autowired
    private SessionUseCase sessionUseCase;

    @Autowired
    private ConversationUserCase conversationUserCase;

    @PostMapping
    public ResponseEntity<SessionResponseDTO> save (@RequestBody SessionRequestDTO sessionRequestDTO){
        SessionResponseDTO sessionResponseDTO = sessionUseCase.createSession(sessionRequestDTO);
        URI location = URI.create("/session/" + sessionResponseDTO.getSessionId());
        return ResponseEntity.created(location).body(sessionResponseDTO);
    }


    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponseDTO> findById (@PathVariable UUID sessionId){
        SessionResponseDTO sessionResponseDTO = sessionUseCase.findById(sessionId);
        return ResponseEntity.ok(sessionResponseDTO);
    }

    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> delete(@PathVariable UUID sessionId) {
        sessionUseCase.delete(sessionId);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /conversation/{sessionId}/conversation
     * Cria uma nova conversa com base em uma sessão e primeira mensagem do usuário
     */
    @PostMapping("/{sessionId}/conversation")
    public ResponseEntity<ConversationResponseDTO> createConversation(
            @PathVariable UUID sessionId,
            @RequestBody ConversationRequestDTO conversationRequestDTO) {

        log.info("Creating conversation for session: {}", sessionId);

        ConversationResponseDTO response = conversationUserCase.createConversation(sessionId, conversationRequestDTO);

        URI location = URI.create("/conversation/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }
}
