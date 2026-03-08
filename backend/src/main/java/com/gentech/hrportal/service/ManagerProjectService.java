package com.gentech.hrportal.service;

import com.gentech.hrportal.dto.*;
import com.gentech.hrportal.entity.*;
import com.gentech.hrportal.repository.*;
import com.gentech.hrportal.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ManagerProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;

    @Autowired
    private EmailService emailService;

    // Get current logged-in manager
    private User getCurrentManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Manager not found"));
    }

    // Get all projects for current manager
    public List<ProjectResponse> getMyProjects() {
        User manager = getCurrentManager();
        List<Project> projects = projectRepository.findByManagerId(manager.getId());
        return projects.stream().map(this::mapToProjectResponse).collect(Collectors.toList());
    }

    // Get projects by status for current manager
    public List<ProjectResponse> getMyProjectsByStatus(Project.ProjectStatus status) {
        User manager = getCurrentManager();
        List<Project> projects = projectRepository.findByManagerIdAndStatus(manager.getId(), status);
        return projects.stream().map(this::mapToProjectResponse).collect(Collectors.toList());
    }

    // Get single project with team details
    public ProjectResponse getProjectById(Long projectId) {
        User manager = getCurrentManager();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Verify manager owns this project
        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("You don't have access to this project");
        }

        return mapToProjectResponseWithTeam(project);
    }

    // Create new project
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request) {
        User manager = getCurrentManager();

        if (manager.getCompany() == null) {
            throw new RuntimeException("Manager is not assigned to any company");
        }

        // Check if project name already exists in the company
        if (projectRepository.existsByNameAndCompanyId(request.getName(), manager.getCompany().getId())) {
            throw new RuntimeException("Project with this name already exists in your company");
        }

        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .manager(manager)
                .company(manager.getCompany())
                .status(Project.ProjectStatus.ACTIVE)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        Project savedProject = projectRepository.save(project);
        return mapToProjectResponse(savedProject);
    }

    // Update project
    @Transactional
    public ProjectResponse updateProject(Long projectId, CreateProjectRequest request) {
        User manager = getCurrentManager();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("You don't have access to this project");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        Project updatedProject = projectRepository.save(project);
        return mapToProjectResponse(updatedProject);
    }

    // Update project status
    @Transactional
    public ProjectResponse updateProjectStatus(Long projectId, Project.ProjectStatus status) {
        User manager = getCurrentManager();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("You don't have access to this project");
        }

        project.setStatus(status);
        Project updatedProject = projectRepository.save(project);
        return mapToProjectResponse(updatedProject);
    }

    // Delete project
    @Transactional
    public void deleteProject(Long projectId) {
        User manager = getCurrentManager();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("You don't have access to this project");
        }

        // Delete all team members first
        List<TeamMember> teamMembers = teamMemberRepository.findByProjectId(projectId);
        teamMemberRepository.deleteAll(teamMembers);

        projectRepository.delete(project);
    }

    // Add team member to project
    @Transactional
    public TeamMemberResponse addTeamMember(Long projectId, AddTeamMemberRequest request) {
        User manager = getCurrentManager();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("You don't have access to this project");
        }

        User employee = userRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Check if employee is already in the team
        if (teamMemberRepository.existsByProjectIdAndEmployeeId(projectId, request.getEmployeeId())) {
            throw new RuntimeException("Employee is already a member of this project");
        }

        // Verify employee belongs to the same company
        if (employee.getCompany() == null || !employee.getCompany().getId().equals(project.getCompany().getId())) {
            throw new RuntimeException("Employee does not belong to your company");
        }

        TeamMember teamMember = TeamMember.builder()
                .project(project)
                .employee(employee)
                .roleInProject(request.getRoleInProject())
                .allocationPercentage(request.getAllocationPercentage())
                .status(TeamMember.AllocationStatus.ACTIVE)
                .build();

        TeamMember savedMember = teamMemberRepository.save(teamMember);

        // Send email notification to employee
        try {
            emailService.sendTeamAssignmentEmail(employee, project, request.getRoleInProject());
        } catch (Exception e) {
            System.err.println("Failed to send team assignment email: " + e.getMessage());
        }

        return mapToTeamMemberResponse(savedMember);
    }

    // Remove team member from project
    @Transactional
    public void removeTeamMember(Long projectId, Long employeeId) {
        User manager = getCurrentManager();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("You don't have access to this project");
        }

        teamMemberRepository.deleteByProjectIdAndEmployeeId(projectId, employeeId);
    }

    // Update team member role/allocation
    @Transactional
    public TeamMemberResponse updateTeamMember(Long projectId, Long employeeId, AddTeamMemberRequest request) {
        User manager = getCurrentManager();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("You don't have access to this project");
        }

        TeamMember teamMember = teamMemberRepository.findByProjectIdAndEmployeeId(projectId, employeeId)
                .orElseThrow(() -> new RuntimeException("Team member not found"));

        teamMember.setRoleInProject(request.getRoleInProject());
        teamMember.setAllocationPercentage(request.getAllocationPercentage());

        TeamMember updatedMember = teamMemberRepository.save(teamMember);
        return mapToTeamMemberResponse(updatedMember);
    }

    // Get available employees for team (employees in manager's company not in this project)
    public List<EmployeeProfileResponse> getAvailableEmployees(Long projectId) {
        User manager = getCurrentManager();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("You don't have access to this project");
        }

        // Get all employees in the company
        List<EmployeeProfile> allEmployees = employeeProfileRepository.findByCompanyId(project.getCompany().getId());

        // Get current team members
        List<TeamMember> currentTeam = teamMemberRepository.findByProjectId(projectId);
        List<Long> currentMemberIds = currentTeam.stream()
                .map(tm -> tm.getEmployee().getId())
                .collect(Collectors.toList());

        // Filter out current team members and the manager
        return allEmployees.stream()
                .filter(ep -> !currentMemberIds.contains(ep.getUser().getId()))
                .filter(ep -> !ep.getUser().getId().equals(manager.getId())) // Exclude manager
                .map(this::mapToEmployeeProfileResponse)
                .collect(Collectors.toList());
    }

    // Helper methods
    private ProjectResponse mapToProjectResponse(Project project) {
        int teamSize = teamMemberRepository.findByProjectId(project.getId()).size();

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .managerId(project.getManager().getId())
                .managerName(project.getManager().getFullName())
                .companyId(project.getCompany().getId())
                .companyName(project.getCompany().getName())
                .status(project.getStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .createdAt(project.getCreatedAt())
                .teamSize(teamSize)
                .build();
    }

    private ProjectResponse mapToProjectResponseWithTeam(Project project) {
        List<TeamMember> teamMembers = teamMemberRepository.findByProjectId(project.getId());
        List<TeamMemberResponse> teamResponses = teamMembers.stream()
                .map(this::mapToTeamMemberResponse)
                .collect(Collectors.toList());

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .managerId(project.getManager().getId())
                .managerName(project.getManager().getFullName())
                .companyId(project.getCompany().getId())
                .companyName(project.getCompany().getName())
                .status(project.getStatus())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .createdAt(project.getCreatedAt())
                .teamSize(teamMembers.size())
                .teamMembers(teamResponses)
                .build();
    }

    private TeamMemberResponse mapToTeamMemberResponse(TeamMember teamMember) {
        User employee = teamMember.getEmployee();

        return TeamMemberResponse.builder()
                .id(teamMember.getId())
                .projectId(teamMember.getProject().getId())
                .employeeId(employee.getId())
                .employeeName(employee.getFullName())
                .employeeEmail(employee.getEmail())
                .employeeRole(employee.getRole().name())
                .roleInProject(teamMember.getRoleInProject())
                .status(teamMember.getStatus())
                .allocationPercentage(teamMember.getAllocationPercentage())
                .joinedDate(teamMember.getJoinedDate())
                .createdAt(teamMember.getCreatedAt())
                .build();
    }

    private EmployeeProfileResponse mapToEmployeeProfileResponse(EmployeeProfile profile) {
        User user = profile.getUser();
        Company company = profile.getCompany();

        return EmployeeProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .companyId(company != null ? company.getId() : null)
                .companyName(company != null ? company.getName() : null)
                .employeeCode(profile.getEmployeeCode())
                .department(profile.getDepartment())
                .designation(profile.getDesignation())
                .phoneNumber(profile.getPhoneNumber())
                .build();
    }
}
