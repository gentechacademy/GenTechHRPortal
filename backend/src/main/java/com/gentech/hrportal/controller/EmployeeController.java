package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.EmployeeProfileResponse;
import com.gentech.hrportal.dto.EmployeeProjectsResponse;
import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employee")
@PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SOFTWARE_ENGINEER', 'MANAGER', 'GENERAL_MANAGER', 'DEVELOPER', 'ADMIN', 'SUPER_ADMIN')")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/profile")
    public ResponseEntity<?> getMyProfile() {
        try {
            EmployeeProfileResponse profile = employeeService.getMyProfile();
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-projects")
    public ResponseEntity<?> getMyProjects() {
        try {
            EmployeeProjectsResponse response = employeeService.getMyProjectsAndManager();
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/profile/picture")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SOFTWARE_ENGINEER', 'MANAGER', 'GENERAL_MANAGER', 'DEVELOPER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateProfilePicture(@RequestBody UpdateProfilePictureRequest request) {
        try {
            EmployeeProfileResponse profile = employeeService.updateProfilePicture(request.getProfilePictureUrl());
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    public static class UpdateProfilePictureRequest {
        private String profilePictureUrl;

        public String getProfilePictureUrl() {
            return profilePictureUrl;
        }

        public void setProfilePictureUrl(String profilePictureUrl) {
            this.profilePictureUrl = profilePictureUrl;
        }
    }
}
