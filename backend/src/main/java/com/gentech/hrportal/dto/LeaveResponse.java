package com.gentech.hrportal.dto;

import com.gentech.hrportal.entity.Leave;
import com.gentech.hrportal.entity.Leave.LeaveStatus;
import com.gentech.hrportal.entity.Leave.LeaveType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveResponse {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private Long companyId;
    private String companyName;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer numberOfDays;
    private String reason;
    private LeaveStatus status;
    private Long appliedById;
    private String appliedByName;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvalDate;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public LeaveResponse() {}
    
    public LeaveResponse(Leave leave) {
        this.id = leave.getId();
        this.employeeId = leave.getEmployee().getId();
        this.employeeName = leave.getEmployee().getFullName();
        this.companyId = leave.getCompany().getId();
        this.companyName = leave.getCompany().getName();
        this.leaveType = leave.getLeaveType();
        this.startDate = leave.getStartDate();
        this.endDate = leave.getEndDate();
        this.numberOfDays = leave.getNumberOfDays();
        this.reason = leave.getReason();
        this.status = leave.getStatus();
        this.rejectionReason = leave.getRejectionReason();
        this.createdAt = leave.getCreatedAt();
        this.updatedAt = leave.getUpdatedAt();
        
        if (leave.getAppliedBy() != null) {
            this.appliedById = leave.getAppliedBy().getId();
            this.appliedByName = leave.getAppliedBy().getFullName();
        }
        
        if (leave.getApprovedBy() != null) {
            this.approvedById = leave.getApprovedBy().getId();
            this.approvedByName = leave.getApprovedBy().getFullName();
        }
        
        this.approvalDate = leave.getApprovalDate();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    
    public Integer getNumberOfDays() { return numberOfDays; }
    public void setNumberOfDays(Integer numberOfDays) { this.numberOfDays = numberOfDays; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }
    
    public Long getAppliedById() { return appliedById; }
    public void setAppliedById(Long appliedById) { this.appliedById = appliedById; }
    
    public String getAppliedByName() { return appliedByName; }
    public void setAppliedByName(String appliedByName) { this.appliedByName = appliedByName; }
    
    public Long getApprovedById() { return approvedById; }
    public void setApprovedById(Long approvedById) { this.approvedById = approvedById; }
    
    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
    
    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
