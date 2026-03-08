package com.gentech.hrportal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendance")
public class Attendance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;
    
    @Column(name = "check_in_time")
    private LocalTime checkInTime;
    
    @Column(name = "check_out_time")
    private LocalTime checkOutTime;
    
    @Column(name = "working_hours")
    private Double workingHours;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AttendanceStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus;
    
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    
    @Column(name = "approval_date")
    private LocalDateTime approvalDate;
    
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    // New fields for weekly attendance
    @Column(name = "week_start_date")
    private LocalDate weekStartDate;
    
    @Column(name = "week_end_date")
    private LocalDate weekEndDate;
    
    @Column(name = "is_weekly_entry")
    private Boolean isWeeklyEntry;
    
    @Column(name = "can_edit_until")
    private LocalDateTime canEditUntil;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public Attendance() {
        this.approvalStatus = ApprovalStatus.PENDING;
        this.status = AttendanceStatus.PRESENT;
        this.isWeeklyEntry = false;
    }
    
    public Attendance(Long id, User employee, Company company, LocalDate attendanceDate, 
                      LocalTime checkInTime, LocalTime checkOutTime, Double workingHours,
                      AttendanceStatus status, ApprovalStatus approvalStatus, User approvedBy,
                      LocalDateTime approvalDate, String rejectionReason, String notes,
                      LocalDate weekStartDate, LocalDate weekEndDate, Boolean isWeeklyEntry,
                      LocalDateTime canEditUntil, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.employee = employee;
        this.company = company;
        this.attendanceDate = attendanceDate;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.workingHours = workingHours;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.approvedBy = approvedBy;
        this.approvalDate = approvalDate;
        this.rejectionReason = rejectionReason;
        this.notes = notes;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
        this.isWeeklyEntry = isWeeklyEntry;
        this.canEditUntil = canEditUntil;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (approvalStatus == null) {
            approvalStatus = ApprovalStatus.PENDING;
        }
        if (status == null) {
            status = AttendanceStatus.PRESENT;
        }
        if (isWeeklyEntry == null) {
            isWeeklyEntry = false;
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
    
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }
    
    public LocalTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalTime checkInTime) { this.checkInTime = checkInTime; }
    
    public LocalTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalTime checkOutTime) { this.checkOutTime = checkOutTime; }
    
    public Double getWorkingHours() { return workingHours; }
    public void setWorkingHours(Double workingHours) { this.workingHours = workingHours; }
    
    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
    
    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }
    
    public User getApprovedBy() { return approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }
    
    public LocalDateTime getApprovalDate() { return approvalDate; }
    public void setApprovalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; }
    
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    // New getters and setters for weekly attendance
    public LocalDate getWeekStartDate() { return weekStartDate; }
    public void setWeekStartDate(LocalDate weekStartDate) { this.weekStartDate = weekStartDate; }
    
    public LocalDate getWeekEndDate() { return weekEndDate; }
    public void setWeekEndDate(LocalDate weekEndDate) { this.weekEndDate = weekEndDate; }
    
    public Boolean getIsWeeklyEntry() { return isWeeklyEntry; }
    public void setIsWeeklyEntry(Boolean isWeeklyEntry) { this.isWeeklyEntry = isWeeklyEntry; }
    
    public LocalDateTime getCanEditUntil() { return canEditUntil; }
    public void setCanEditUntil(LocalDateTime canEditUntil) { this.canEditUntil = canEditUntil; }
    
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
        private LocalDate attendanceDate;
        private LocalTime checkInTime;
        private LocalTime checkOutTime;
        private Double workingHours;
        private AttendanceStatus status;
        private ApprovalStatus approvalStatus;
        private User approvedBy;
        private LocalDateTime approvalDate;
        private String rejectionReason;
        private String notes;
        private LocalDate weekStartDate;
        private LocalDate weekEndDate;
        private Boolean isWeeklyEntry;
        private LocalDateTime canEditUntil;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(Long id) { this.id = id; return this; }
        public Builder employee(User employee) { this.employee = employee; return this; }
        public Builder company(Company company) { this.company = company; return this; }
        public Builder attendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; return this; }
        public Builder checkInTime(LocalTime checkInTime) { this.checkInTime = checkInTime; return this; }
        public Builder checkOutTime(LocalTime checkOutTime) { this.checkOutTime = checkOutTime; return this; }
        public Builder workingHours(Double workingHours) { this.workingHours = workingHours; return this; }
        public Builder status(AttendanceStatus status) { this.status = status; return this; }
        public Builder approvalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; return this; }
        public Builder approvedBy(User approvedBy) { this.approvedBy = approvedBy; return this; }
        public Builder approvalDate(LocalDateTime approvalDate) { this.approvalDate = approvalDate; return this; }
        public Builder rejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder weekStartDate(LocalDate weekStartDate) { this.weekStartDate = weekStartDate; return this; }
        public Builder weekEndDate(LocalDate weekEndDate) { this.weekEndDate = weekEndDate; return this; }
        public Builder isWeeklyEntry(Boolean isWeeklyEntry) { this.isWeeklyEntry = isWeeklyEntry; return this; }
        public Builder canEditUntil(LocalDateTime canEditUntil) { this.canEditUntil = canEditUntil; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public Attendance build() {
            return new Attendance(id, employee, company, attendanceDate, checkInTime, checkOutTime,
                workingHours, status, approvalStatus, approvedBy, approvalDate, rejectionReason, notes,
                weekStartDate, weekEndDate, isWeeklyEntry, canEditUntil, createdAt, updatedAt);
        }
    }
    
    public enum AttendanceStatus {
        PRESENT,
        ABSENT,
        HALF_DAY,
        WORK_FROM_HOME,
        ON_LEAVE,
        HOLIDAY,
        WEEKLY_OFF
    }
    
    public enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
