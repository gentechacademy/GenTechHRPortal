package com.gentech.hrportal.dto;

import com.gentech.hrportal.entity.EmployeeDocument.DocumentType;

public class DocumentUploadRequest {
    
    private DocumentType documentType;
    private String documentName;
    
    public DocumentUploadRequest() {}
    
    public DocumentUploadRequest(DocumentType documentType, String documentName) {
        this.documentType = documentType;
        this.documentName = documentName;
    }
    
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    
    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }
}
