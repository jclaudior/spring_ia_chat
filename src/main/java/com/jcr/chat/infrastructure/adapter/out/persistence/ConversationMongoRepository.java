package com.jcr.chat.infrastructure.adapter.out.persistence;

import com.jcr.chat.domain.model.ConversationMongo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationMongoRepository extends MongoRepository<ConversationMongo, String> {
    Optional<ConversationMongo> findById(String id);
    Page<ConversationMongo> findByUserId(String userId, Pageable pageable);
}

