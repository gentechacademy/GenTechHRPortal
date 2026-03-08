package com.gentech.hrportal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_members")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;

    @Column(name = "role_in_project")
    private String roleInProject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AllocationStatus status = AllocationStatus.ACTIVE;

    @Column(name = "allocation_percentage")
    private Integer allocationPercentage = 100;

    @Column(name = "joined_date")
    private LocalDate joinedDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public TeamMember() {}

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (joinedDate == null) {
            joinedDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Project getProject() { return project; }
    public void setProject(Project project) { this.project = project; }

    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }

    public String getRoleInProject() { return roleInProject; }
    public void setRoleInProject(String roleInProject) { this.roleInProject = roleInProject; }

    public AllocationStatus getStatus() { return status; }
    public void setStatus(AllocationStatus status) { this.status = status; }

    public Integer getAllocationPercentage() { return allocationPercentage; }
    public void setAllocationPercentage(Integer allocationPercentage) { this.allocationPercentage = allocationPercentage; }

    public LocalDate getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public enum AllocationStatus {
        ACTIVE,
        COMPLETED,
        ON_BENCH,
        TRANSFERRED
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Project project;
        private User employee;
        private String roleInProject;
        private AllocationStatus status = AllocationStatus.ACTIVE;
        private Integer allocationPercentage = 100;
        private LocalDate joinedDate;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder project(Project project) { this.project = project; return this; }
        public Builder employee(User employee) { this.employee = employee; return this; }
        public Builder roleInProject(String roleInProject) { this.roleInProject = roleInProject; return this; }
        public Builder status(AllocationStatus status) { this.status = status; return this; }
        public Builder allocationPercentage(Integer allocationPercentage) { this.allocationPercentage = allocationPercentage; return this; }
        public Builder joinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; return this; }

        public TeamMember build() {
            TeamMember member = new TeamMember();
            member.setId(id);
            member.setProject(project);
            member.setEmployee(employee);
            member.setRoleInProject(roleInProject);
            member.setStatus(status);
            member.setAllocationPercentage(allocationPercentage);
            member.setJoinedDate(joinedDate);
            return member;
        }
    }
}
