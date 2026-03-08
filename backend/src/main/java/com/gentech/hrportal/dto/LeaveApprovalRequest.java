package com.gentech.hrportal.dto;

public class LeaveApprovalRequest {
    
    private Long leaveId;
    private Long approverId;
    private String rejectionReason;
    private boolean approved;
    
    public LeaveApprovalRequest() {}
    
    public Long getLeaveId() { return leaveId; }
    public void setLeaveId(Long leaveId) { this.leaveId = leaveId; }
    
    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }
}
