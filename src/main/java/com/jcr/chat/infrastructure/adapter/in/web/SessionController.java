package com.jcr.chat.infrastructure.adapter.in.web;

import com.jcr.chat.application.port.in.ConversationUserCase;
import com.jcr.chat.application.port.in.SessionUseCase;
import com.jcr.chat.domain.model.dto.ConversationRequestDTO;
import com.jcr.chat.domain.model.dto.ConversationResponseDTO;
import com.jcr.chat.domain.model.dto.SessionRequestDTO;
import com.jcr.chat.domain.model.dto.SessionResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

import static reactor.netty.http.HttpConnectionLiveness.log;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private SessionUseCase sessionUseCase;

    @Autowired
    private ConversationUserCase conversationUserCase;

    @PostMapping
    public ResponseEntity<SessionResponseDTO> save (@RequestBody SessionRequestDTO sessionRequestDTO){
        SessionResponseDTO sessionResponseDTO = sessionUseCase.createSession(sessionRequestDTO);
        URI location = URI.create("/sessions/" + sessionResponseDTO.getSessionId());
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


    @PostMapping("/{sessionId}/conversations")
    public ResponseEntity<ConversationResponseDTO> createConversation(
            @PathVariable UUID sessionId,
            @RequestBody ConversationRequestDTO conversationRequestDTO) {

        log.info("Creating conversation for session: {}", sessionId);

        ConversationResponseDTO response = conversationUserCase.createConversation(sessionId, conversationRequestDTO);

        URI location = URI.create("/conversations/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }
}
