package com.jcr.chat.infrastructure.adapter.in.web;

import com.jcr.chat.application.port.in.ConversationUserCase;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@Log4j2
@RestController
@RequestMapping("/conversation")
public class ConversationController {

    @Autowired
    private ConversationUserCase conversationUserCase;

}

