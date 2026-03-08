package com.gentech.hrportal.dto;

import java.time.LocalDate;
import java.util.List;

public class EmployeeProjectsResponse {

    private List<ProjectInfo> projects;
    private ManagerInfo reportingManager;

    public EmployeeProjectsResponse() {}

    public List<ProjectInfo> getProjects() { return projects; }
    public void setProjects(List<ProjectInfo> projects) { this.projects = projects; }

    public ManagerInfo getReportingManager() { return reportingManager; }
    public void setReportingManager(ManagerInfo reportingManager) { this.reportingManager = reportingManager; }

    public static class ProjectInfo {
        private Long projectId;
        private String projectName;
        private String description;
        private String roleInProject;
        private Integer allocationPercentage;
        private String projectStatus;
        private LocalDate projectStartDate;
        private LocalDate projectEndDate;
        private LocalDate joinedDate;
        private String managerName;
        private String managerEmail;

        // Getters and Setters
        public Long getProjectId() { return projectId; }
        public void setProjectId(Long projectId) { this.projectId = projectId; }

        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getRoleInProject() { return roleInProject; }
        public void setRoleInProject(String roleInProject) { this.roleInProject = roleInProject; }

        public Integer getAllocationPercentage() { return allocationPercentage; }
        public void setAllocationPercentage(Integer allocationPercentage) { this.allocationPercentage = allocationPercentage; }

        public String getProjectStatus() { return projectStatus; }
        public void setProjectStatus(String projectStatus) { this.projectStatus = projectStatus; }

        public LocalDate getProjectStartDate() { return projectStartDate; }
        public void setProjectStartDate(LocalDate projectStartDate) { this.projectStartDate = projectStartDate; }

        public LocalDate getProjectEndDate() { return projectEndDate; }
        public void setProjectEndDate(LocalDate projectEndDate) { this.projectEndDate = projectEndDate; }

        public LocalDate getJoinedDate() { return joinedDate; }
        public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }

        public String getManagerName() { return managerName; }
        public void setManagerName(String managerName) { this.managerName = managerName; }

        public String getManagerEmail() { return managerEmail; }
        public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }
    }

    public static class ManagerInfo {
        private Long managerId;
        private String managerName;
        private String managerEmail;
        private String managerRole;
        private String department;
        private String designation;

        // Getters and Setters
        public Long getManagerId() { return managerId; }
        public void setManagerId(Long managerId) { this.managerId = managerId; }

        public String getManagerName() { return managerName; }
        public void setManagerName(String managerName) { this.managerName = managerName; }

        public String getManagerEmail() { return managerEmail; }
        public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }

        public String getManagerRole() { return managerRole; }
        public void setManagerRole(String managerRole) { this.managerRole = managerRole; }

        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }

        public String getDesignation() { return designation; }
        public void setDesignation(String designation) { this.designation = designation; }
    }
}
