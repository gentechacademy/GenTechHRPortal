package com.gentech.hrportal.dto;

import com.gentech.hrportal.entity.ProfileEditRequest;
import com.gentech.hrportal.entity.ProfileEditRequest.RequestStatus;

import java.time.LocalDateTime;

public class ProfileEditResponse {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long companyId;
    private String companyName;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private RequestStatus status;
    private Long requestedById;
    private String requestedByName;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvalDate;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ProfileEditResponse() {}
    
    public ProfileEditResponse(ProfileEditRequest request) {
        this.id = request.getId();
        this.employeeId = request.getEmployee().getId();
        this.employeeName = request.getEmployee().getFullName();
        this.companyId = request.getCompany().getId();
        this.companyName = request.getCompany().getName();
        this.fieldName = request.getFieldName();
        this.oldValue = request.getOldValue();
        this.newValue = request.getNewValue();
        this.status = request.getStatus();
        this.rejectionReason = request.getRejectionReason();
        this.createdAt = request.getCreatedAt();
        this.updatedAt = request.getUpdatedAt();
        
        if (request.getRequestedBy() != null) {
            this.requestedById = request.getRequestedBy().getId();
            this.requestedByName = request.getRequestedBy().getFullName();
        }
        
        if (request.getApprovedBy() != null) {
            this.approvedById = request.getApprovedBy().getId();
            this.approvedByName = request.getApprovedBy().getFullName();
        }
        
        this.approvalDate = request.getApprovalDate();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getEmployeeName() {
        return employeeName;
    }
    
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    
    public Long getCompanyId() {
        return companyId;
    }
    
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getOldValue() {
        return oldValue;
    }
    
    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }
    
    public String getNewValue() {
        return newValue;
    }
    
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }
    
    public RequestStatus getStatus() {
        return status;
    }
    
    public void setStatus(RequestStatus status) {
        this.status = status;
    }
    
    public Long getRequestedById() {
        return requestedById;
    }
    
    public void setRequestedById(Long requestedById) {
        this.requestedById = requestedById;
    }
    
    public String getRequestedByName() {
        return requestedByName;
    }
    
    public void setRequestedByName(String requestedByName) {
        this.requestedByName = requestedByName;
    }
    
    public Long getApprovedById() {
        return approvedById;
    }
    
    public void setApprovedById(Long approvedById) {
        this.approvedById = approvedById;
    }
    
    public String getApprovedByName() {
        return approvedByName;
    }
    
    public void setApprovedByName(String approvedByName) {
        this.approvedByName = approvedByName;
    }
    
    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public String getRejectionReason() {
        return rejectionReason;
    }
    
    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
