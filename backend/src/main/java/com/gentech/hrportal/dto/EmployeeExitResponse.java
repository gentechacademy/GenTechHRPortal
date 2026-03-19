package com.gentech.hrportal.dto;

import com.gentech.hrportal.entity.EmployeeExit;
import com.gentech.hrportal.entity.EmployeeExit.ApprovalStatus;
import com.gentech.hrportal.entity.EmployeeExit.ExitStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeExitResponse {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private String employeeUsername;
    private Long companyId;
    private String companyName;
    private LocalDate exitDate;
    private LocalDate lastWorkingDate;
    private Integer noticePeriodDays;
    private String reason;
    private ExitStatus exitStatus;
    private ApprovalStatus managerApprovalStatus;
    private ApprovalStatus adminApprovalStatus;
    private String managerComments;
    private String adminComments;
    private Long managerApprovedById;
    private String managerApprovedByName;
    private Long adminApprovedById;
    private String adminApprovedByName;
    private LocalDateTime managerApprovalDate;
    private LocalDateTime adminApprovalDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public EmployeeExitResponse() {}
    
    public EmployeeExitResponse(EmployeeExit exit) {
        this.id = exit.getId();
        if (exit.getEmployee() != null) {
            this.employeeId = exit.getEmployee().getId();
            this.employeeName = exit.getEmployee().getFullName();
            this.employeeEmail = exit.getEmployee().getEmail();
            this.employeeUsername = exit.getEmployee().getUsername();
        }
        if (exit.getCompany() != null) {
            this.companyId = exit.getCompany().getId();
            this.companyName = exit.getCompany().getName();
        }
        this.exitDate = exit.getExitDate();
        this.lastWorkingDate = exit.getLastWorkingDate();
        this.noticePeriodDays = exit.getNoticePeriodDays();
        this.reason = exit.getReason();
        this.exitStatus = exit.getExitStatus();
        this.managerApprovalStatus = exit.getManagerApprovalStatus();
        this.adminApprovalStatus = exit.getAdminApprovalStatus();
        this.managerComments = exit.getManagerComments();
        this.adminComments = exit.getAdminComments();
        if (exit.getManagerApprovedBy() != null) {
            this.managerApprovedById = exit.getManagerApprovedBy().getId();
            this.managerApprovedByName = exit.getManagerApprovedBy().getFullName();
        }
        if (exit.getAdminApprovedBy() != null) {
            this.adminApprovedById = exit.getAdminApprovedBy().getId();
            this.adminApprovedByName = exit.getAdminApprovedBy().getFullName();
        }
        this.managerApprovalDate = exit.getManagerApprovalDate();
        this.adminApprovalDate = exit.getAdminApprovalDate();
        this.createdAt = exit.getCreatedAt();
        this.updatedAt = exit.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }
    
    public String getEmployeeUsername() { return employeeUsername; }
    public void setEmployeeUsername(String employeeUsername) { this.employeeUsername = employeeUsername; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public LocalDate getExitDate() { return exitDate; }
    public void setExitDate(LocalDate exitDate) { this.exitDate = exitDate; }
    
    public LocalDate getLastWorkingDate() { return lastWorkingDate; }
    public void setLastWorkingDate(LocalDate lastWorkingDate) { this.lastWorkingDate = lastWorkingDate; }
    
    public Integer getNoticePeriodDays() { return noticePeriodDays; }
    public void setNoticePeriodDays(Integer noticePeriodDays) { this.noticePeriodDays = noticePeriodDays; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public ExitStatus getExitStatus() { return exitStatus; }
    public void setExitStatus(ExitStatus exitStatus) { this.exitStatus = exitStatus; }
    
    public ApprovalStatus getManagerApprovalStatus() { return managerApprovalStatus; }
    public void setManagerApprovalStatus(ApprovalStatus managerApprovalStatus) { this.managerApprovalStatus = managerApprovalStatus; }
    
    public ApprovalStatus getAdminApprovalStatus() { return adminApprovalStatus; }
    public void setAdminApprovalStatus(ApprovalStatus adminApprovalStatus) { this.adminApprovalStatus = adminApprovalStatus; }
    
    public String getManagerComments() { return managerComments; }
    public void setManagerComments(String managerComments) { this.managerComments = managerComments; }
    
    public String getAdminComments() { return adminComments; }
    public void setAdminComments(String adminComments) { this.adminComments = adminComments; }
    
    public Long getManagerApprovedById() { return managerApprovedById; }
    public void setManagerApprovedById(Long managerApprovedById) { this.managerApprovedById = managerApprovedById; }
    
    public String getManagerApprovedByName() { return managerApprovedByName; }
    public void setManagerApprovedByName(String managerApprovedByName) { this.managerApprovedByName = managerApprovedByName; }
    
    public Long getAdminApprovedById() { return adminApprovedById; }
    public void setAdminApprovedById(Long adminApprovedById) { this.adminApprovedById = adminApprovedById; }
    
    public String getAdminApprovedByName() { return adminApprovedByName; }
    public void setAdminApprovedByName(String adminApprovedByName) { this.adminApprovedByName = adminApprovedByName; }
    
    public LocalDateTime getManagerApprovalDate() { return managerApprovalDate; }
    public void setManagerApprovalDate(LocalDateTime managerApprovalDate) { this.managerApprovalDate = managerApprovalDate; }
    
    public LocalDateTime getAdminApprovalDate() { return adminApprovalDate; }
    public void setAdminApprovalDate(LocalDateTime adminApprovalDate) { this.adminApprovalDate = adminApprovalDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
