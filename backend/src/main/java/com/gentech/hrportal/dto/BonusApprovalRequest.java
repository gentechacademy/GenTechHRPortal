package com.gentech.hrportal.dto;

public class BonusApprovalRequest {
    
    private Long requestId;
    private boolean approved;
    private String rejectionReason;
    
    public BonusApprovalRequest() {}
    
    public BonusApprovalRequest(Long requestId, boolean approved, String rejectionReason) {
        this.requestId = requestId;
        this.approved = approved;
        this.rejectionReason = rejectionReason;
    }
    
    // Getters and Setters
    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
