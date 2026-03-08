package com.gentech.hrportal.dto;

import com.gentech.hrportal.entity.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private Long managerId;
    private String managerName;
    private Long companyId;
    private String companyName;
    private Project.ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private int teamSize;
    private List<TeamMemberResponse> teamMembers;

    public ProjectResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public Project.ProjectStatus getStatus() { return status; }
    public void setStatus(Project.ProjectStatus status) { this.status = status; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getTeamSize() { return teamSize; }
    public void setTeamSize(int teamSize) { this.teamSize = teamSize; }

    public List<TeamMemberResponse> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<TeamMemberResponse> teamMembers) { this.teamMembers = teamMembers; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private Long managerId;
        private String managerName;
        private Long companyId;
        private String companyName;
        private Project.ProjectStatus status;
        private LocalDate startDate;
        private LocalDate endDate;
        private LocalDateTime createdAt;
        private int teamSize;
        private List<TeamMemberResponse> teamMembers;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder managerId(Long managerId) { this.managerId = managerId; return this; }
        public Builder managerName(String managerName) { this.managerName = managerName; return this; }
        public Builder companyId(Long companyId) { this.companyId = companyId; return this; }
        public Builder companyName(String companyName) { this.companyName = companyName; return this; }
        public Builder status(Project.ProjectStatus status) { this.status = status; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder endDate(LocalDate endDate) { this.endDate = endDate; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder teamSize(int teamSize) { this.teamSize = teamSize; return this; }
        public Builder teamMembers(List<TeamMemberResponse> teamMembers) { this.teamMembers = teamMembers; return this; }

        public ProjectResponse build() {
            ProjectResponse response = new ProjectResponse();
            response.setId(id);
            response.setName(name);
            response.setDescription(description);
            response.setManagerId(managerId);
            response.setManagerName(managerName);
            response.setCompanyId(companyId);
            response.setCompanyName(companyName);
            response.setStatus(status);
            response.setStartDate(startDate);
            response.setEndDate(endDate);
            response.setCreatedAt(createdAt);
            response.setTeamSize(teamSize);
            response.setTeamMembers(teamMembers);
            return response;
        }
    }
}
