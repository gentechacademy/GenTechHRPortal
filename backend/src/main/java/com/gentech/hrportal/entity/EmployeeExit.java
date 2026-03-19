package com.gentech.hrportal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "employee_exits")
public class EmployeeExit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;
    
    @Column(name = "exit_date")
    private LocalDate exitDate;
    
    @Column(name = "last_working_date")
    private LocalDate lastWorkingDate;
    
    @Column(name = "notice_period_days")
    private Integer noticePeriodDays;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "exit_status", nullable = false)
    private ExitStatus exitStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "manager_approval_status")
    private ApprovalStatus managerApprovalStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "admin_approval_status")
    private ApprovalStatus adminApprovalStatus;
    
    @Column(name = "manager_comments", columnDefinition = "TEXT")
    private String managerComments;
    
    @Column(name = "admin_comments", columnDefinition = "TEXT")
    private String adminComments;
    
    @ManyToOne
    @JoinColumn(name = "manager_approved_by")
    private User managerApprovedBy;
    
    @ManyToOne
    @JoinColumn(name = "admin_approved_by")
    private User adminApprovedBy;
    
    @Column(name = "manager_approval_date")
    private LocalDateTime managerApprovalDate;
    
    @Column(name = "admin_approval_date")
    private LocalDateTime adminApprovalDate;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public EmployeeExit() {
        this.exitStatus = ExitStatus.ACTIVE;
        this.managerApprovalStatus = ApprovalStatus.PENDING;
        this.adminApprovalStatus = ApprovalStatus.PENDING;
    }
    
    public EmployeeExit(Long id, User employee, Company company, LocalDate exitDate, LocalDate lastWorkingDate,
                        Integer noticePeriodDays, String reason, ExitStatus exitStatus,
                        ApprovalStatus managerApprovalStatus, ApprovalStatus adminApprovalStatus,
                        String managerComments, String adminComments, User managerApprovedBy, User adminApprovedBy,
                        LocalDateTime managerApprovalDate, LocalDateTime adminApprovalDate,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.employee = employee;
        this.company = company;
        this.exitDate = exitDate;
        this.lastWorkingDate = lastWorkingDate;
        this.noticePeriodDays = noticePeriodDays;
        this.reason = reason;
        this.exitStatus = exitStatus;
        this.managerApprovalStatus = managerApprovalStatus;
        this.adminApprovalStatus = adminApprovalStatus;
        this.managerComments = managerComments;
        this.adminComments = adminComments;
        this.managerApprovedBy = managerApprovedBy;
        this.adminApprovedBy = adminApprovedBy;
        this.managerApprovalDate = managerApprovalDate;
        this.adminApprovalDate = adminApprovalDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (exitStatus == null) {
            exitStatus = ExitStatus.ACTIVE;
        }
        if (managerApprovalStatus == null) {
            managerApprovalStatus = ApprovalStatus.PENDING;
        }
        if (adminApprovalStatus == null) {
            adminApprovalStatus = ApprovalStatus.PENDING;
        }
        if (exitDate == null) {
            exitDate = LocalDate.now();
        }
        // Calculate notice period days if not set
        if (noticePeriodDays == null && exitDate != null && lastWorkingDate != null) {
            noticePeriodDays = (int) ChronoUnit.DAYS.between(exitDate, lastWorkingDate);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
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
    
    public User getManagerApprovedBy() { return managerApprovedBy; }
    public void setManagerApprovedBy(User managerApprovedBy) { this.managerApprovedBy = managerApprovedBy; }
    
    public User getAdminApprovedBy() { return adminApprovedBy; }
    public void setAdminApprovedBy(User adminApprovedBy) { this.adminApprovedBy = adminApprovedBy; }
    
    public LocalDateTime getManagerApprovalDate() { return managerApprovalDate; }
    public void setManagerApprovalDate(LocalDateTime managerApprovalDate) { this.managerApprovalDate = managerApprovalDate; }
    
    public LocalDateTime getAdminApprovalDate() { return adminApprovalDate; }
    public void setAdminApprovalDate(LocalDateTime adminApprovalDate) { this.adminApprovalDate = adminApprovalDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long id;
        private User employee;
        private Company company;
        private LocalDate exitDate;
        private LocalDate lastWorkingDate;
        private Integer noticePeriodDays;
        private String reason;
        private ExitStatus exitStatus;
        private ApprovalStatus managerApprovalStatus;
        private ApprovalStatus adminApprovalStatus;
        private String managerComments;
        private String adminComments;
        private User managerApprovedBy;
        private User adminApprovedBy;
        private LocalDateTime managerApprovalDate;
        private LocalDateTime adminApprovalDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(Long id) { this.id = id; return this; }
        public Builder employee(User employee) { this.employee = employee; return this; }
        public Builder company(Company company) { this.company = company; return this; }
        public Builder exitDate(LocalDate exitDate) { this.exitDate = exitDate; return this; }
        public Builder lastWorkingDate(LocalDate lastWorkingDate) { this.lastWorkingDate = lastWorkingDate; return this; }
        public Builder noticePeriodDays(Integer noticePeriodDays) { this.noticePeriodDays = noticePeriodDays; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder exitStatus(ExitStatus exitStatus) { this.exitStatus = exitStatus; return this; }
        public Builder managerApprovalStatus(ApprovalStatus managerApprovalStatus) { this.managerApprovalStatus = managerApprovalStatus; return this; }
        public Builder adminApprovalStatus(ApprovalStatus adminApprovalStatus) { this.adminApprovalStatus = adminApprovalStatus; return this; }
        public Builder managerComments(String managerComments) { this.managerComments = managerComments; return this; }
        public Builder adminComments(String adminComments) { this.adminComments = adminComments; return this; }
        public Builder managerApprovedBy(User managerApprovedBy) { this.managerApprovedBy = managerApprovedBy; return this; }
        public Builder adminApprovedBy(User adminApprovedBy) { this.adminApprovedBy = adminApprovedBy; return this; }
        public Builder managerApprovalDate(LocalDateTime managerApprovalDate) { this.managerApprovalDate = managerApprovalDate; return this; }
        public Builder adminApprovalDate(LocalDateTime adminApprovalDate) { this.adminApprovalDate = adminApprovalDate; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public EmployeeExit build() {
            return new EmployeeExit(id, employee, company, exitDate, lastWorkingDate, noticePeriodDays, reason,
                exitStatus, managerApprovalStatus, adminApprovalStatus, managerComments, adminComments,
                managerApprovedBy, adminApprovedBy, managerApprovalDate, adminApprovalDate, createdAt, updatedAt);
        }
    }
    
    public enum ExitStatus {
        ACTIVE,
        RESIGNED,
        TERMINATED
    }
    
    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
