package com.gentech.hrportal.dto;

import com.gentech.hrportal.entity.EmployeeDocument.DocumentStatus;

public class DocumentApprovalRequest {
    
    private Long documentId;
    private DocumentStatus status;
    private String comments;
    
    public DocumentApprovalRequest() {}
    
    public DocumentApprovalRequest(Long documentId, DocumentStatus status, String comments) {
        this.documentId = documentId;
        this.status = status;
        this.comments = comments;
    }
    
    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }
    
    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
