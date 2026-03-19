package com.gentech.hrportal.dto;

public class ExitApprovalRequest {
    
    private Long exitId;
    private String approvalStatus; // APPROVED or REJECTED
    private String comments;
    
    public ExitApprovalRequest() {}
    
    public ExitApprovalRequest(Long exitId, String approvalStatus, String comments) {
        this.exitId = exitId;
        this.approvalStatus = approvalStatus;
        this.comments = comments;
    }
    
    // Getters and Setters
    public Long getExitId() { return exitId; }
    public void setExitId(Long exitId) { this.exitId = exitId; }
    
    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
}
