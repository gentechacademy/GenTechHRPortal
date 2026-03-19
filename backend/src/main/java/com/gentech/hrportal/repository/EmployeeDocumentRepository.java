package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.EmployeeDocument;
import com.gentech.hrportal.entity.EmployeeDocument.DocumentStatus;
import com.gentech.hrportal.entity.EmployeeDocument.DocumentType;
import com.gentech.hrportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, Long> {
    
    List<EmployeeDocument> findByEmployee(User employee);
    
    List<EmployeeDocument> findByEmployeeId(Long employeeId);
    
    List<EmployeeDocument> findByDocumentType(DocumentType documentType);
    
    List<EmployeeDocument> findByStatus(DocumentStatus status);
    
    List<EmployeeDocument> findByEmployeeIdAndStatus(Long employeeId, DocumentStatus status);
    
    List<EmployeeDocument> findByEmployeeIdAndDocumentType(Long employeeId, DocumentType documentType);
    
    List<EmployeeDocument> findByEmployee_Company_IdAndStatus(Long companyId, DocumentStatus status);
    
    Optional<EmployeeDocument> findByEmployeeIdAndDocumentTypeAndStatus(Long employeeId, DocumentType documentType, DocumentStatus status);
    
    List<EmployeeDocument> findByEmployee_Company_Id(Long companyId);
    
    boolean existsByEmployeeIdAndDocumentTypeAndStatus(Long employeeId, DocumentType documentType, DocumentStatus status);
}
