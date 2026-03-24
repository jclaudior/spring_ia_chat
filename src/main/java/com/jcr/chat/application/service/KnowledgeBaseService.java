package com.jcr.chat.application.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class KnowledgeBaseService {

    private final VectorStore vectorStore;

    @Autowired
    public KnowledgeBaseService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    /**
     * Busca documentos relevantes na base de conhecimento usando RAG
     * @param query - Pergunta do usuário
     * @return Lista de documentos relevantes
     */
    public List<Document> search(String query) {
        try {
            log.info("Searching knowledge base for query: {}", query);
            List<Document> results = vectorStore.similaritySearch(query);
            log.info("Found {} relevant documents", results.size());
            return results;
        } catch (Exception e) {
            log.error("Error searching knowledge base: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Busca com limite de resultados
     * @param query - Pergunta do usuário
     * @param limit - Número máximo de documentos
     * @return Lista de documentos relevantes
     */
    public List<Document> search(String query, int limit) {
        try {
            log.info("Searching knowledge base for query: {} with limit: {}", query, limit);
            List<Document> results = vectorStore.similaritySearch(query);
            return results.stream().limit(limit).toList();
        } catch (Exception e) {
            log.error("Error searching knowledge base: {}", e.getMessage(), e);
            return List.of();
        }
    }
}

