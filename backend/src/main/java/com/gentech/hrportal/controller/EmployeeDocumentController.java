package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.DocumentApprovalRequest;
import com.gentech.hrportal.dto.DocumentResponse;
import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.entity.EmployeeDocument;
import com.gentech.hrportal.security.UserDetailsImpl;
import com.gentech.hrportal.service.EmployeeDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class EmployeeDocumentController {
    
    @Autowired
    private EmployeeDocumentService documentService;
    
    // ==================== Employee Endpoints ====================
    
    @PostMapping("/employee/documents/upload")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("documentType") String documentType,
            @RequestParam("documentName") String documentName,
            @RequestParam("file") MultipartFile file) {
        try {
            Long employeeId = getCurrentUserId();
            EmployeeDocument document = documentService.uploadDocument(employeeId, file, documentType, documentName);
            return ResponseEntity.ok(new DocumentResponse(document));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid document type: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/employee/documents/my-documents")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyDocuments() {
        try {
            Long employeeId = getCurrentUserId();
            List<EmployeeDocument> documents = documentService.getEmployeeDocuments(employeeId);
            List<DocumentResponse> responses = documents.stream()
                .map(DocumentResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/employee/documents/{id}")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok(new MessageResponse("Document deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/employee/documents/{id}/request-edit")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> requestDocumentEdit(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            if (reason == null || reason.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Reason is required for edit request"));
            }
            EmployeeDocument document = documentService.requestDocumentEdit(id, reason);
            return ResponseEntity.ok(new DocumentResponse(document));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // ==================== Admin Endpoints ====================
    
    @GetMapping("/admin/documents/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getAllDocuments() {
        try {
            List<EmployeeDocument> documents = documentService.getAllDocuments();
            List<DocumentResponse> responses = documents.stream()
                .map(DocumentResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/admin/documents/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getPendingDocuments() {
        try {
            List<EmployeeDocument> documents = documentService.getAllPendingDocuments();
            List<DocumentResponse> responses = documents.stream()
                .map(DocumentResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/admin/documents/by-status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getDocumentsByStatus(@PathVariable String status) {
        try {
            List<EmployeeDocument> documents = documentService.getDocumentsByStatus(status);
            List<DocumentResponse> responses = documents.stream()
                .map(DocumentResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/admin/documents/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> approveDocument(@PathVariable Long id, @RequestBody(required = false) DocumentApprovalRequest request) {
        try {
            Long adminId = getCurrentUserId();
            String comments = (request != null && request.getComments() != null) ? request.getComments() : null;
            EmployeeDocument document = documentService.approveDocument(id, adminId, comments);
            return ResponseEntity.ok(new DocumentResponse(document));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/admin/documents/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> rejectDocument(@PathVariable Long id, @RequestBody(required = false) DocumentApprovalRequest request) {
        try {
            Long adminId = getCurrentUserId();
            String comments = (request != null && request.getComments() != null) ? request.getComments() : null;
            EmployeeDocument document = documentService.rejectDocument(id, adminId, comments);
            return ResponseEntity.ok(new DocumentResponse(document));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // ==================== Utility Methods ====================
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}
