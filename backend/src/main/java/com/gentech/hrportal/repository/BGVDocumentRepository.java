package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.BGVDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BGVDocumentRepository extends JpaRepository<BGVDocument, Long> {
    
    List<BGVDocument> findByBgvRequestId(Long bgvRequestId);
    
    List<BGVDocument> findByBgvRequestIdAndStatus(Long bgvRequestId, BGVDocument.DocumentStatus status);
}
