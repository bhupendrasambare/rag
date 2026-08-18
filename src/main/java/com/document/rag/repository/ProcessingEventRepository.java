package com.document.rag.repository;

import com.document.rag.models.ProcessingEvent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessingEventRepository
        extends JpaRepository<ProcessingEvent, UUID> {

    Optional<ProcessingEvent> findTopByDocumentIdOrderByCreatedAtDesc(UUID documentId);
}
