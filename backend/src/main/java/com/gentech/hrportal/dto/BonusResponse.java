package com.gentech.hrportal.dto;

import com.gentech.hrportal.entity.BonusRequest;

import java.time.LocalDateTime;

public class BonusResponse {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long companyId;
    private String companyName;
    private Double amount;
    private String reason;
    private Integer month;
    private Integer year;
    private String status;
    private Long requestedById;
    private String requestedByName;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime requestedDate;
    private LocalDateTime approvalDate;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public BonusResponse() {}
    
    public BonusResponse(BonusRequest request) {
        this.id = request.getId();
        this.employeeId = request.getEmployee() != null ? request.getEmployee().getId() : null;
        this.employeeName = request.getEmployee() != null ? request.getEmployee().getFullName() : null;
        this.companyId = request.getCompany() != null ? request.getCompany().getId() : null;
        this.companyName = request.getCompany() != null ? request.getCompany().getName() : null;
        this.amount = request.getAmount();
        this.reason = request.getReason();
        this.month = request.getMonth();
        this.year = request.getYear();
        this.status = request.getStatus() != null ? request.getStatus().name() : null;
        this.requestedById = request.getRequestedBy() != null ? request.getRequestedBy().getId() : null;
        this.requestedByName = request.getRequestedBy() != null ? request.getRequestedBy().getFullName() : null;
        this.approvedById = request.getApprovedBy() != null ? request.getApprovedBy().getId() : null;
        this.approvedByName = request.getApprovedBy() != null ? request.getApprovedBy().getFullName() : null;
        this.requestedDate = request.getRequestedDate();
        this.approvalDate = request.getApprovalDate();
        this.rejectionReason = request.getRejectionReason();
        this.createdAt = request.getCreatedAt();
        this.updatedAt = request.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getRequestedById() { return requestedById; }
    public void setRequestedById(Long requestedById) { this.requestedById = requestedById; }
    
    public String getRequestedByName() { return requestedByName; }
    public void setRequestedByName(String requestedByName) { this.requestedByName = requestedByName; }
    
    public Long getApprovedById() { return approvedById; }
    public void setApprovedById(Long approvedById) { this.approvedById = approvedById; }
    
    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
    
    public LocalDateTime getRequestedDate() { return requestedDate; }
    public void setRequestedDate(LocalDateTime requestedDate) { this.requestedDate = requestedDate; }
    
    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
