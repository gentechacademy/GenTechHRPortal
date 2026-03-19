package com.gentech.hrportal.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Comprehensive DTO with all employee details for admin view
 */
public class EmployeeFullDetailsResponse {
    
    // Basic Employee Info
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String role;
    
    // Employee Profile
    private String employeeCode;
    private String department;
    private String designation;
    private LocalDate dateOfJoining;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String address;
    private String emergencyContact;
    private Double salary;
    private String profilePictureUrl;
    
    // Company Info
    private Long companyId;
    private String companyName;
    
    // Employment Status
    private Boolean isActive;
    private LocalDate lastWorkingDate;
    private String employmentStatus;
    
    // Attendance Summary
    private AttendanceSummary attendanceSummary;
    
    // Leave Summary
    private LeaveSummary leaveSummary;
    
    // Project Details
    private List<ProjectDetail> currentProjects;
    private List<ProjectDetail> pastProjects;
    
    // Salary Slip Summary
    private SalarySlipSummary salarySlipSummary;
    
    // Exit Details
    private ExitDetails exitDetails;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public EmployeeFullDetailsResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    
    public LocalDate getDateOfJoining() { return dateOfJoining; }
    public void setDateOfJoining(LocalDate dateOfJoining) { this.dateOfJoining = dateOfJoining; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public Double getSalary() { return salary; }
    public void setSalary(Double salary) { this.salary = salary; }
    
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDate getLastWorkingDate() { return lastWorkingDate; }
    public void setLastWorkingDate(LocalDate lastWorkingDate) { this.lastWorkingDate = lastWorkingDate; }
    
    public String getEmploymentStatus() { return employmentStatus; }
    public void setEmploymentStatus(String employmentStatus) { this.employmentStatus = employmentStatus; }
    
    public AttendanceSummary getAttendanceSummary() { return attendanceSummary; }
    public void setAttendanceSummary(AttendanceSummary attendanceSummary) { this.attendanceSummary = attendanceSummary; }
    
    public LeaveSummary getLeaveSummary() { return leaveSummary; }
    public void setLeaveSummary(LeaveSummary leaveSummary) { this.leaveSummary = leaveSummary; }
    
    public List<ProjectDetail> getCurrentProjects() { return currentProjects; }
    public void setCurrentProjects(List<ProjectDetail> currentProjects) { this.currentProjects = currentProjects; }
    
    public List<ProjectDetail> getPastProjects() { return pastProjects; }
    public void setPastProjects(List<ProjectDetail> pastProjects) { this.pastProjects = pastProjects; }
    
    public SalarySlipSummary getSalarySlipSummary() { return salarySlipSummary; }
    public void setSalarySlipSummary(SalarySlipSummary salarySlipSummary) { this.salarySlipSummary = salarySlipSummary; }
    
    public ExitDetails getExitDetails() { return exitDetails; }
    public void setExitDetails(ExitDetails exitDetails) { this.exitDetails = exitDetails; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    /**
     * Inner class for Attendance Summary
     */
    public static class AttendanceSummary {
        private Integer totalDays;
        private Integer presentDays;
        private Integer absentDays;
        private Integer leaveDays;
        private Integer workFromHomeDays;
        private Integer halfDays;
        private Double attendancePercentage;
        
        public AttendanceSummary() {}
        
        public Integer getTotalDays() { return totalDays; }
        public void setTotalDays(Integer totalDays) { this.totalDays = totalDays; }
        
        public Integer getPresentDays() { return presentDays; }
        public void setPresentDays(Integer presentDays) { this.presentDays = presentDays; }
        
        public Integer getAbsentDays() { return absentDays; }
        public void setAbsentDays(Integer absentDays) { this.absentDays = absentDays; }
        
        public Integer getLeaveDays() { return leaveDays; }
        public void setLeaveDays(Integer leaveDays) { this.leaveDays = leaveDays; }
        
        public Integer getWorkFromHomeDays() { return workFromHomeDays; }
        public void setWorkFromHomeDays(Integer workFromHomeDays) { this.workFromHomeDays = workFromHomeDays; }
        
        public Integer getHalfDays() { return halfDays; }
        public void setHalfDays(Integer halfDays) { this.halfDays = halfDays; }
        
        public Double getAttendancePercentage() { return attendancePercentage; }
        public void setAttendancePercentage(Double attendancePercentage) { this.attendancePercentage = attendancePercentage; }
    }
    
    /**
     * Inner class for Leave Summary
     */
    public static class LeaveSummary {
        private Integer totalLeaves;
        private Integer approvedLeaves;
        private Integer pendingLeaves;
        private Integer rejectedLeaves;
        private Integer sickLeaves;
        private Integer privilegeLeaves;
        private Integer casualLeaves;
        private Integer earnedLeaves;
        
        public LeaveSummary() {}
        
        public Integer getTotalLeaves() { return totalLeaves; }
        public void setTotalLeaves(Integer totalLeaves) { this.totalLeaves = totalLeaves; }
        
        public Integer getApprovedLeaves() { return approvedLeaves; }
        public void setApprovedLeaves(Integer approvedLeaves) { this.approvedLeaves = approvedLeaves; }
        
        public Integer getPendingLeaves() { return pendingLeaves; }
        public void setPendingLeaves(Integer pendingLeaves) { this.pendingLeaves = pendingLeaves; }
        
        public Integer getRejectedLeaves() { return rejectedLeaves; }
        public void setRejectedLeaves(Integer rejectedLeaves) { this.rejectedLeaves = rejectedLeaves; }
        
        public Integer getSickLeaves() { return sickLeaves; }
        public void setSickLeaves(Integer sickLeaves) { this.sickLeaves = sickLeaves; }
        
        public Integer getPrivilegeLeaves() { return privilegeLeaves; }
        public void setPrivilegeLeaves(Integer privilegeLeaves) { this.privilegeLeaves = privilegeLeaves; }
        
        public Integer getCasualLeaves() { return casualLeaves; }
        public void setCasualLeaves(Integer casualLeaves) { this.casualLeaves = casualLeaves; }
        
        public Integer getEarnedLeaves() { return earnedLeaves; }
        public void setEarnedLeaves(Integer earnedLeaves) { this.earnedLeaves = earnedLeaves; }
    }
    
    /**
     * Inner class for Project Detail
     */
    public static class ProjectDetail {
        private Long projectId;
        private String projectName;
        private String description;
        private String roleInProject;
        private String status;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer allocationPercentage;
        private String managerName;
        
        public ProjectDetail() {}
        
        public Long getProjectId() { return projectId; }
        public void setProjectId(Long projectId) { this.projectId = projectId; }
        
        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getRoleInProject() { return roleInProject; }
        public void setRoleInProject(String roleInProject) { this.roleInProject = roleInProject; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public Integer getAllocationPercentage() { return allocationPercentage; }
        public void setAllocationPercentage(Integer allocationPercentage) { this.allocationPercentage = allocationPercentage; }
        
        public String getManagerName() { return managerName; }
        public void setManagerName(String managerName) { this.managerName = managerName; }
    }
    
    /**
     * Inner class for Salary Slip Summary
     */
    public static class SalarySlipSummary {
        private Integer totalSlipsGenerated;
        private Integer totalSlipsDownloaded;
        private Double lastGrossSalary;
        private Double lastNetSalary;
        private Integer lastPaidMonth;
        private Integer lastPaidYear;
        private LocalDateTime lastGeneratedDate;
        private List<SalarySlipBasicInfo> recentSlips;
        
        public SalarySlipSummary() {}
        
        public Integer getTotalSlipsGenerated() { return totalSlipsGenerated; }
        public void setTotalSlipsGenerated(Integer totalSlipsGenerated) { this.totalSlipsGenerated = totalSlipsGenerated; }
        
        public Integer getTotalSlipsDownloaded() { return totalSlipsDownloaded; }
        public void setTotalSlipsDownloaded(Integer totalSlipsDownloaded) { this.totalSlipsDownloaded = totalSlipsDownloaded; }
        
        public Double getLastGrossSalary() { return lastGrossSalary; }
        public void setLastGrossSalary(Double lastGrossSalary) { this.lastGrossSalary = lastGrossSalary; }
        
        public Double getLastNetSalary() { return lastNetSalary; }
        public void setLastNetSalary(Double lastNetSalary) { this.lastNetSalary = lastNetSalary; }
        
        public Integer getLastPaidMonth() { return lastPaidMonth; }
        public void setLastPaidMonth(Integer lastPaidMonth) { this.lastPaidMonth = lastPaidMonth; }
        
        public Integer getLastPaidYear() { return lastPaidYear; }
        public void setLastPaidYear(Integer lastPaidYear) { this.lastPaidYear = lastPaidYear; }
        
        public LocalDateTime getLastGeneratedDate() { return lastGeneratedDate; }
        public void setLastGeneratedDate(LocalDateTime lastGeneratedDate) { this.lastGeneratedDate = lastGeneratedDate; }
        
        public List<SalarySlipBasicInfo> getRecentSlips() { return recentSlips; }
        public void setRecentSlips(List<SalarySlipBasicInfo> recentSlips) { this.recentSlips = recentSlips; }
    }
    
    /**
     * Inner class for Salary Slip Basic Info
     */
    public static class SalarySlipBasicInfo {
        private Long slipId;
        private Integer month;
        private Integer year;
        private Double grossSalary;
        private Double netSalary;
        private String status;
        private LocalDateTime generatedDate;
        
        public SalarySlipBasicInfo() {}
        
        public Long getSlipId() { return slipId; }
        public void setSlipId(Long slipId) { this.slipId = slipId; }
        
        public Integer getMonth() { return month; }
        public void setMonth(Integer month) { this.month = month; }
        
        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        
        public Double getGrossSalary() { return grossSalary; }
        public void setGrossSalary(Double grossSalary) { this.grossSalary = grossSalary; }
        
        public Double getNetSalary() { return netSalary; }
        public void setNetSalary(Double netSalary) { this.netSalary = netSalary; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getGeneratedDate() { return generatedDate; }
        public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }
    }
    
    /**
     * Inner class for Exit Details
     */
    public static class ExitDetails {
        private Long resignationId;
        private LocalDate requestDate;
        private LocalDate proposedLastWorkingDay;
        private LocalDate actualLastWorkingDay;
        private Integer noticePeriodDays;
        private String reason;
        private String status;
        private String managerRemarks;
        private String adminRemarks;
        private String managerApprovedBy;
        private String adminApprovedBy;
        private LocalDateTime managerApprovalDate;
        private LocalDateTime adminApprovalDate;
        
        public ExitDetails() {}
        
        public Long getResignationId() { return resignationId; }
        public void setResignationId(Long resignationId) { this.resignationId = resignationId; }
        
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
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getManagerRemarks() { return managerRemarks; }
        public void setManagerRemarks(String managerRemarks) { this.managerRemarks = managerRemarks; }
        
        public String getAdminRemarks() { return adminRemarks; }
        public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }
        
        public String getManagerApprovedBy() { return managerApprovedBy; }
        public void setManagerApprovedBy(String managerApprovedBy) { this.managerApprovedBy = managerApprovedBy; }
        
        public String getAdminApprovedBy() { return adminApprovedBy; }
        public void setAdminApprovedBy(String adminApprovedBy) { this.adminApprovedBy = adminApprovedBy; }
        
        public LocalDateTime getManagerApprovalDate() { return managerApprovalDate; }
        public void setManagerApprovalDate(LocalDateTime managerApprovalDate) { this.managerApprovalDate = managerApprovalDate; }
        
        public LocalDateTime getAdminApprovalDate() { return adminApprovalDate; }
        public void setAdminApprovalDate(LocalDateTime adminApprovalDate) { this.adminApprovalDate = adminApprovalDate; }
    }
    
    /**
     * Inner class for Work History Entry
     */
    public static class WorkHistoryEntry {
        private String type;
        private LocalDate startDate;
        private LocalDate endDate;
        private String description;
        private String status;
        
        public WorkHistoryEntry() {}
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private EmployeeFullDetailsResponse response = new EmployeeFullDetailsResponse();
        
        public Builder id(Long id) {
            response.setId(id);
            return this;
        }
        
        public Builder username(String username) {
            response.setUsername(username);
            return this;
        }
        
        public Builder fullName(String fullName) {
            response.setFullName(fullName);
            return this;
        }
        
        public Builder email(String email) {
            response.setEmail(email);
            return this;
        }
        
        public Builder role(String role) {
            response.setRole(role);
            return this;
        }
        
        public Builder employeeCode(String employeeCode) {
            response.setEmployeeCode(employeeCode);
            return this;
        }
        
        public Builder department(String department) {
            response.setDepartment(department);
            return this;
        }
        
        public Builder designation(String designation) {
            response.setDesignation(designation);
            return this;
        }
        
        public Builder dateOfJoining(LocalDate dateOfJoining) {
            response.setDateOfJoining(dateOfJoining);
            return this;
        }
        
        public Builder dateOfBirth(LocalDate dateOfBirth) {
            response.setDateOfBirth(dateOfBirth);
            return this;
        }
        
        public Builder phoneNumber(String phoneNumber) {
            response.setPhoneNumber(phoneNumber);
            return this;
        }
        
        public Builder address(String address) {
            response.setAddress(address);
            return this;
        }
        
        public Builder emergencyContact(String emergencyContact) {
            response.setEmergencyContact(emergencyContact);
            return this;
        }
        
        public Builder salary(Double salary) {
            response.setSalary(salary);
            return this;
        }
        
        public Builder profilePictureUrl(String profilePictureUrl) {
            response.setProfilePictureUrl(profilePictureUrl);
            return this;
        }
        
        public Builder companyId(Long companyId) {
            response.setCompanyId(companyId);
            return this;
        }
        
        public Builder companyName(String companyName) {
            response.setCompanyName(companyName);
            return this;
        }
        
        public Builder isActive(Boolean isActive) {
            response.setIsActive(isActive);
            return this;
        }
        
        public Builder lastWorkingDate(LocalDate lastWorkingDate) {
            response.setLastWorkingDate(lastWorkingDate);
            return this;
        }
        
        public Builder employmentStatus(String employmentStatus) {
            response.setEmploymentStatus(employmentStatus);
            return this;
        }
        
        public Builder attendanceSummary(AttendanceSummary attendanceSummary) {
            response.setAttendanceSummary(attendanceSummary);
            return this;
        }
        
        public Builder leaveSummary(LeaveSummary leaveSummary) {
            response.setLeaveSummary(leaveSummary);
            return this;
        }
        
        public Builder currentProjects(List<ProjectDetail> currentProjects) {
            response.setCurrentProjects(currentProjects);
            return this;
        }
        
        public Builder pastProjects(List<ProjectDetail> pastProjects) {
            response.setPastProjects(pastProjects);
            return this;
        }
        
        public Builder salarySlipSummary(SalarySlipSummary salarySlipSummary) {
            response.setSalarySlipSummary(salarySlipSummary);
            return this;
        }
        
        public Builder exitDetails(ExitDetails exitDetails) {
            response.setExitDetails(exitDetails);
            return this;
        }
        
        public Builder createdAt(LocalDateTime createdAt) {
            response.setCreatedAt(createdAt);
            return this;
        }
        
        public Builder updatedAt(LocalDateTime updatedAt) {
            response.setUpdatedAt(updatedAt);
            return this;
        }
        
        public EmployeeFullDetailsResponse build() {
            return response;
        }
    }
}
