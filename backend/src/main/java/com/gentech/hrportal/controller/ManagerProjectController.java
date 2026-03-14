package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.*;
import com.gentech.hrportal.entity.Project;
import com.gentech.hrportal.service.ManagerProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasAnyRole('MANAGER', 'GENERAL_MANAGER', 'ADMIN', 'SUPER_ADMIN')")
public class ManagerProjectController {

    @Autowired
    private ManagerProjectService managerProjectService;

    // Get all projects for current manager
    @GetMapping("/projects")
    public ResponseEntity<List<ProjectResponse>> getMyProjects() {
        return ResponseEntity.ok(managerProjectService.getMyProjects());
    }

    // Get projects by status
    @GetMapping("/projects/status/{status}")
    public ResponseEntity<List<ProjectResponse>> getMyProjectsByStatus(@PathVariable Project.ProjectStatus status) {
        return ResponseEntity.ok(managerProjectService.getMyProjectsByStatus(status));
    }

    // Get single project with team details
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable Long projectId) {
        try {
            return ResponseEntity.ok(managerProjectService.getProjectById(projectId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Create new project
    @PostMapping("/projects")
    public ResponseEntity<?> createProject(@Valid @RequestBody CreateProjectRequest request) {
        try {
            ProjectResponse project = managerProjectService.createProject(request);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Update project
    @PutMapping("/projects/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable Long projectId, @Valid @RequestBody CreateProjectRequest request) {
        try {
            ProjectResponse project = managerProjectService.updateProject(projectId, request);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Update project status
    @PatchMapping("/projects/{projectId}/status")
    public ResponseEntity<?> updateProjectStatus(@PathVariable Long projectId, @RequestParam Project.ProjectStatus status) {
        try {
            ProjectResponse project = managerProjectService.updateProjectStatus(projectId, status);
            return ResponseEntity.ok(project);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Delete project
    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable Long projectId) {
        try {
            managerProjectService.deleteProject(projectId);
            return ResponseEntity.ok(new MessageResponse("Project deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Add team member to project
    @PostMapping("/projects/{projectId}/team")
    public ResponseEntity<?> addTeamMember(@PathVariable Long projectId, @Valid @RequestBody AddTeamMemberRequest request) {
        try {
            TeamMemberResponse member = managerProjectService.addTeamMember(projectId, request);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Remove team member from project
    @DeleteMapping("/projects/{projectId}/team/{employeeId}")
    public ResponseEntity<?> removeTeamMember(@PathVariable Long projectId, @PathVariable Long employeeId) {
        try {
            managerProjectService.removeTeamMember(projectId, employeeId);
            return ResponseEntity.ok(new MessageResponse("Team member removed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Update team member
    @PutMapping("/projects/{projectId}/team/{employeeId}")
    public ResponseEntity<?> updateTeamMember(@PathVariable Long projectId, @PathVariable Long employeeId, @Valid @RequestBody AddTeamMemberRequest request) {
        try {
            TeamMemberResponse member = managerProjectService.updateTeamMember(projectId, employeeId, request);
            return ResponseEntity.ok(member);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Get available employees for team
    @GetMapping("/projects/{projectId}/available-employees")
    public ResponseEntity<?> getAvailableEmployees(@PathVariable Long projectId) {
        try {
            return ResponseEntity.ok(managerProjectService.getAvailableEmployees(projectId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
