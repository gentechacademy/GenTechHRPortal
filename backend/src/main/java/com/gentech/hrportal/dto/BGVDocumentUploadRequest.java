package com.gentech.hrportal.dto;

public class BGVDocumentUploadRequest {
    
    private Long bgvRequestId;
    private String documentType;
    
    public BGVDocumentUploadRequest() {}
    
    public Long getBgvRequestId() { return bgvRequestId; }
    public void setBgvRequestId(Long bgvRequestId) { this.bgvRequestId = bgvRequestId; }
    
    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }
}
