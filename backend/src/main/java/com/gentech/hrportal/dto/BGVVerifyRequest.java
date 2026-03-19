package com.gentech.hrportal.dto;

public class BGVVerifyRequest {
    
    private Long documentId;
    private boolean approved;
    private String remarks;
    
    public BGVVerifyRequest() {}
    
    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }
    
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
