package com.gentech.hrportal.dto;

import com.gentech.hrportal.entity.TeamMember;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TeamMemberResponse {

    private Long id;
    private Long projectId;
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private String employeeRole;
    private String roleInProject;
    private TeamMember.AllocationStatus status;
    private Integer allocationPercentage;
    private LocalDate joinedDate;
    private LocalDateTime createdAt;

    public TeamMemberResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }

    public String getEmployeeRole() { return employeeRole; }
    public void setEmployeeRole(String employeeRole) { this.employeeRole = employeeRole; }

    public String getRoleInProject() { return roleInProject; }
    public void setRoleInProject(String roleInProject) { this.roleInProject = roleInProject; }

    public TeamMember.AllocationStatus getStatus() { return status; }
    public void setStatus(TeamMember.AllocationStatus status) { this.status = status; }

    public Integer getAllocationPercentage() { return allocationPercentage; }
    public void setAllocationPercentage(Integer allocationPercentage) { this.allocationPercentage = allocationPercentage; }

    public LocalDate getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long projectId;
        private Long employeeId;
        private String employeeName;
        private String employeeEmail;
        private String employeeRole;
        private String roleInProject;
        private TeamMember.AllocationStatus status;
        private Integer allocationPercentage;
        private LocalDate joinedDate;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder projectId(Long projectId) { this.projectId = projectId; return this; }
        public Builder employeeId(Long employeeId) { this.employeeId = employeeId; return this; }
        public Builder employeeName(String employeeName) { this.employeeName = employeeName; return this; }
        public Builder employeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; return this; }
        public Builder employeeRole(String employeeRole) { this.employeeRole = employeeRole; return this; }
        public Builder roleInProject(String roleInProject) { this.roleInProject = roleInProject; return this; }
        public Builder status(TeamMember.AllocationStatus status) { this.status = status; return this; }
        public Builder allocationPercentage(Integer allocationPercentage) { this.allocationPercentage = allocationPercentage; return this; }
        public Builder joinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public TeamMemberResponse build() {
            TeamMemberResponse response = new TeamMemberResponse();
            response.setId(id);
            response.setProjectId(projectId);
            response.setEmployeeId(employeeId);
            response.setEmployeeName(employeeName);
            response.setEmployeeEmail(employeeEmail);
            response.setEmployeeRole(employeeRole);
            response.setRoleInProject(roleInProject);
            response.setStatus(status);
            response.setAllocationPercentage(allocationPercentage);
            response.setJoinedDate(joinedDate);
            response.setCreatedAt(createdAt);
            return response;
        }
    }
}
