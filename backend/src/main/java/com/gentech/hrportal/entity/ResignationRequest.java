package com.gentech.hrportal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "resignation_requests")
public class ResignationRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Column(name = "request_date", nullable = false)
    private LocalDate requestDate;
    
    @Column(name = "proposed_last_working_day", nullable = false)
    private LocalDate proposedLastWorkingDay;
    
    @Column(name = "actual_last_working_day")
    private LocalDate actualLastWorkingDay;
    
    @Column(name = "notice_period_days", nullable = false)
    private Integer noticePeriodDays;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ResignationStatus status;
    
    @Column(name = "manager_remarks", columnDefinition = "TEXT")
    private String managerRemarks;
    
    @Column(name = "admin_remarks", columnDefinition = "TEXT")
    private String adminRemarks;
    
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
    
    public ResignationRequest() {
        this.status = ResignationStatus.PENDING_MANAGER;
    }
    
    public ResignationRequest(Long id, User employee, Company company, LocalDate requestDate,
                              LocalDate proposedLastWorkingDay, LocalDate actualLastWorkingDay,
                              Integer noticePeriodDays, String reason, ResignationStatus status,
                              String managerRemarks, String adminRemarks, User managerApprovedBy,
                              User adminApprovedBy, LocalDateTime managerApprovalDate,
                              LocalDateTime adminApprovalDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.employee = employee;
        this.company = company;
        this.requestDate = requestDate;
        this.proposedLastWorkingDay = proposedLastWorkingDay;
        this.actualLastWorkingDay = actualLastWorkingDay;
        this.noticePeriodDays = noticePeriodDays;
        this.reason = reason;
        this.status = status;
        this.managerRemarks = managerRemarks;
        this.adminRemarks = adminRemarks;
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
        if (status == null) {
            status = ResignationStatus.PENDING_MANAGER;
        }
        if (requestDate == null) {
            requestDate = LocalDate.now();
        }
        // Calculate notice period days if not set
        if (noticePeriodDays == null && requestDate != null && proposedLastWorkingDay != null) {
            noticePeriodDays = (int) ChronoUnit.DAYS.between(requestDate, proposedLastWorkingDay);
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
    
    public LocalDate getRequestDate() { return requestDate; }
    public void setRequestDate(LocalDate requestDate) { this.requestDate = requestDate; }
    
    public LocalDate getProposedLastWorkingDay() { return proposedLastWorkingDay; }
    public void setProposedLastWorkingDay(LocalDate proposedLastWorkingDay) { this.proposedLastWorkingDay = proposedLastWorkingDay; }
    
    public LocalDate getActualLastWorkingDay() { return actualLastWorkingDay; }
    public void setActualLastWorkingDay(LocalDate actualLastWorkingDay) { this.actualLastWorkingDay = actualLastWorkingDay; }
    
    public Integer getNoticePeriodDays() { return noticePeriodDays; }
    public void setNoticePeriodDays(Integer noticePeriodDays) { this.noticePeriodDays = noticePeriodDays; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public ResignationStatus getStatus() { return status; }
    public void setStatus(ResignationStatus status) { this.status = status; }
    
    public String getManagerRemarks() { return managerRemarks; }
    public void setManagerRemarks(String managerRemarks) { this.managerRemarks = managerRemarks; }
    
    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }
    
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
        private LocalDate requestDate;
        private LocalDate proposedLastWorkingDay;
        private LocalDate actualLastWorkingDay;
        private Integer noticePeriodDays;
        private String reason;
        private ResignationStatus status;
        private String managerRemarks;
        private String adminRemarks;
        private User managerApprovedBy;
        private User adminApprovedBy;
        private LocalDateTime managerApprovalDate;
        private LocalDateTime adminApprovalDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(Long id) { this.id = id; return this; }
        public Builder employee(User employee) { this.employee = employee; return this; }
        public Builder company(Company company) { this.company = company; return this; }
        public Builder requestDate(LocalDate requestDate) { this.requestDate = requestDate; return this; }
        public Builder proposedLastWorkingDay(LocalDate proposedLastWorkingDay) { this.proposedLastWorkingDay = proposedLastWorkingDay; return this; }
        public Builder actualLastWorkingDay(LocalDate actualLastWorkingDay) { this.actualLastWorkingDay = actualLastWorkingDay; return this; }
        public Builder noticePeriodDays(Integer noticePeriodDays) { this.noticePeriodDays = noticePeriodDays; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder status(ResignationStatus status) { this.status = status; return this; }
        public Builder managerRemarks(String managerRemarks) { this.managerRemarks = managerRemarks; return this; }
        public Builder adminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; return this; }
        public Builder managerApprovedBy(User managerApprovedBy) { this.managerApprovedBy = managerApprovedBy; return this; }
        public Builder adminApprovedBy(User adminApprovedBy) { this.adminApprovedBy = adminApprovedBy; return this; }
        public Builder managerApprovalDate(LocalDateTime managerApprovalDate) { this.managerApprovalDate = managerApprovalDate; return this; }
        public Builder adminApprovalDate(LocalDateTime adminApprovalDate) { this.adminApprovalDate = adminApprovalDate; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public ResignationRequest build() {
            return new ResignationRequest(id, employee, company, requestDate, proposedLastWorkingDay,
                actualLastWorkingDay, noticePeriodDays, reason, status, managerRemarks, adminRemarks,
                managerApprovedBy, adminApprovedBy, managerApprovalDate, adminApprovalDate, createdAt, updatedAt);
        }
    }
    
    public enum ResignationStatus {
        PENDING_MANAGER,    // Waiting for manager approval
        MANAGER_APPROVED,   // Manager approved, waiting for admin
        PENDING_ADMIN,      // Waiting for admin approval (alternative name)
        APPROVED,           // Fully approved by admin
        REJECTED            // Rejected by either manager or admin
    }
}
