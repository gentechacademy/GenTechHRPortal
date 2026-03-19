package com.gentech.hrportal.service;

import com.gentech.hrportal.dto.DocumentUploadRequest;
import com.gentech.hrportal.entity.EmployeeDocument;
import com.gentech.hrportal.entity.EmployeeDocument.DocumentStatus;
import com.gentech.hrportal.entity.EmployeeDocument.DocumentType;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.EmployeeDocumentRepository;
import com.gentech.hrportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeDocumentService {
    
    @Autowired
    private EmployeeDocumentRepository documentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Transactional
    public EmployeeDocument uploadDocument(Long employeeId, MultipartFile file, String documentTypeStr, String documentName) {
        // Validate employee
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        
        // Validate document type
        DocumentType documentType;
        try {
            documentType = DocumentType.valueOf(documentTypeStr);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid document type: " + documentTypeStr);
        }
        
        // Validate request
        if (documentName == null || documentName.trim().isEmpty()) {
            throw new RuntimeException("Document name is required");
        }
        
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is required");
        }
        
        // Check if there's an existing pending document of the same type
        List<EmployeeDocument> existingDocs = documentRepository.findByEmployeeIdAndDocumentType(employeeId, documentType);
        for (EmployeeDocument existingDoc : existingDocs) {
            if (existingDoc.getStatus() == DocumentStatus.PENDING) {
                throw new RuntimeException("You already have a pending document of this type. Please wait for approval or delete the existing one.");
            }
        }
        
        // Store file
        String subDirectory = "documents/employee_" + employeeId;
        String fileUrl = fileStorageService.storeFile(file, subDirectory);
        
        // Create document record
        EmployeeDocument document = new EmployeeDocument();
        document.setEmployee(employee);
        document.setDocumentType(documentType);
        document.setDocumentName(documentName);
        document.setDocumentUrl(fileUrl);
        document.setStatus(DocumentStatus.PENDING);
        
        return documentRepository.save(document);
    }
    
    @Transactional
    public EmployeeDocument uploadDocument(Long employeeId, DocumentUploadRequest request, MultipartFile file) {
        return uploadDocument(employeeId, file, request.getDocumentType().name(), request.getDocumentName());
    }
    
    public List<EmployeeDocument> getEmployeeDocuments(Long employeeId) {
        return documentRepository.findByEmployeeId(employeeId);
    }
    
    public List<EmployeeDocument> getDocumentsByStatus(String statusStr) {
        try {
            DocumentStatus status = DocumentStatus.valueOf(statusStr);
            return documentRepository.findByStatus(status);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + statusStr);
        }
    }
    
    public List<EmployeeDocument> getAllPendingDocuments() {
        return documentRepository.findByStatus(DocumentStatus.PENDING);
    }
    
    public List<EmployeeDocument> getAllDocuments() {
        return documentRepository.findAll();
    }
    
    @Transactional
    public EmployeeDocument approveDocument(Long documentId, Long adminId, String comments) {
        EmployeeDocument document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));
        
        if (document.getStatus() != DocumentStatus.PENDING) {
            throw new RuntimeException("Document is not in pending status. Current status: " + document.getStatus());
        }
        
        User approver = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));
        
        document.setStatus(DocumentStatus.APPROVED);
        document.setApprovedBy(approver);
        document.setApprovedAt(LocalDateTime.now());
        document.setAdminComments(comments);
        
        return documentRepository.save(document);
    }
    
    @Transactional
    public EmployeeDocument rejectDocument(Long documentId, Long adminId, String comments) {
        EmployeeDocument document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));
        
        if (document.getStatus() != DocumentStatus.PENDING) {
            throw new RuntimeException("Document is not in pending status. Current status: " + document.getStatus());
        }
        
        User approver = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found with id: " + adminId));
        
        document.setStatus(DocumentStatus.REJECTED);
        document.setApprovedBy(approver);
        document.setApprovedAt(LocalDateTime.now());
        document.setAdminComments(comments);
        
        return documentRepository.save(document);
    }
    
    @Transactional
    public void deleteDocument(Long documentId) {
        EmployeeDocument document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));
        
        // Only allow deletion of pending documents
        if (document.getStatus() != DocumentStatus.PENDING) {
            throw new RuntimeException("Only pending documents can be deleted. Current status: " + document.getStatus());
        }
        
        documentRepository.delete(document);
    }
    
    @Transactional
    public EmployeeDocument requestDocumentEdit(Long documentId, String reason) {
        EmployeeDocument document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));
        
        // Only approved documents can have edit requests
        if (document.getStatus() != DocumentStatus.APPROVED) {
            throw new RuntimeException("Only approved documents can request edits. Current status: " + document.getStatus());
        }
        
        // Create a new pending version with the reason as comments
        EmployeeDocument newVersion = new EmployeeDocument();
        newVersion.setEmployee(document.getEmployee());
        newVersion.setDocumentType(document.getDocumentType());
        newVersion.setDocumentName(document.getDocumentName());
        newVersion.setDocumentUrl(document.getDocumentUrl());
        newVersion.setStatus(DocumentStatus.PENDING);
        newVersion.setAdminComments("Edit requested. Reason: " + reason);
        
        return documentRepository.save(newVersion);
    }
    
    public boolean isDocumentEditable(Long documentId) {
        EmployeeDocument document = documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));
        
        return document.getStatus() == DocumentStatus.PENDING;
    }
    
    public EmployeeDocument getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));
    }
    
    public boolean hasApprovedDocument(Long employeeId, DocumentType documentType) {
        return documentRepository.existsByEmployeeIdAndDocumentTypeAndStatus(employeeId, documentType, DocumentStatus.APPROVED);
    }
    
    public EmployeeDocument getApprovedDocument(Long employeeId, DocumentType documentType) {
        return documentRepository.findByEmployeeIdAndDocumentTypeAndStatus(employeeId, documentType, DocumentStatus.APPROVED)
            .orElse(null);
    }
}
