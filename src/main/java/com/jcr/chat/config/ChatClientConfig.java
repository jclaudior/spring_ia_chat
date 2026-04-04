package com.jcr.chat.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(
            ChatModel chatModel,
            SyncMcpToolCallbackProvider toolCallbackProvider
    ) {
        return ChatClient
                .builder(chatModel)
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }
}

