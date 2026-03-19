package com.gentech.hrportal.dto;

public class ResignationApprovalDto {
    
    private Long resignationId;
    private boolean approved;
    private String remarks;
    
    public ResignationApprovalDto() {}
    
    public ResignationApprovalDto(Long resignationId, boolean approved, String remarks) {
        this.resignationId = resignationId;
        this.approved = approved;
        this.remarks = remarks;
    }
    
    // Getters and Setters
    public Long getResignationId() { return resignationId; }
    public void setResignationId(Long resignationId) { this.resignationId = resignationId; }
    
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
