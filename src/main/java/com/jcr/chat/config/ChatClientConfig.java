package com.jcr.chat.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(
            ChatModel chatModel,
            SyncMcpToolCallbackProvider toolCallbackProvider
    ) {
        return ChatClient
                .builder(chatModel)
                .temperature(0.7)   
                .topK(50) 
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }
}

