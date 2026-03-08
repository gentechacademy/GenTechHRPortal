package com.gentech.hrportal.dto;

import jakarta.validation.constraints.NotNull;

public class AddTeamMemberRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private String roleInProject;

    private Integer allocationPercentage = 100;

    public AddTeamMemberRequest() {}

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getRoleInProject() { return roleInProject; }
    public void setRoleInProject(String roleInProject) { this.roleInProject = roleInProject; }

    public Integer getAllocationPercentage() { return allocationPercentage; }
    public void setAllocationPercentage(Integer allocationPercentage) { this.allocationPercentage = allocationPercentage; }
}
