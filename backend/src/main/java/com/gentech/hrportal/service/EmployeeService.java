package com.gentech.hrportal.service;

import com.gentech.hrportal.dto.EmployeeProfileResponse;
import com.gentech.hrportal.dto.EmployeeProjectsResponse;
import com.gentech.hrportal.entity.EmployeeProfile;
import com.gentech.hrportal.entity.TeamMember;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.EmployeeProfileRepository;
import com.gentech.hrportal.repository.TeamMemberRepository;
import com.gentech.hrportal.repository.UserRepository;
import com.gentech.hrportal.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

        @Autowired
        private EmployeeProfileRepository employeeProfileRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private TeamMemberRepository teamMemberRepository;

        public EmployeeProfileResponse getMyProfile() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                User user = userRepository.findById(userDetails.getId())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                EmployeeProfile profile = employeeProfileRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new RuntimeException("Employee profile not found"));

                return mapToResponse(profile);
        }

        public EmployeeProfileResponse getEmployeeProfile(Long userId) {
                EmployeeProfile profile = employeeProfileRepository.findByUserId(userId)
                                .orElseThrow(() -> new RuntimeException("Employee profile not found"));
                return mapToResponse(profile);
        }

        public EmployeeProfileResponse updateProfilePicture(String profilePictureUrl) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                User user = userRepository.findById(userDetails.getId())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                EmployeeProfile profile = employeeProfileRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new RuntimeException("Employee profile not found"));

                profile.setProfilePictureUrl(profilePictureUrl);
                EmployeeProfile updatedProfile = employeeProfileRepository.save(profile);

                return mapToResponse(updatedProfile);
        }

        public EmployeeProjectsResponse getMyProjectsAndManager() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                User user = userRepository.findById(userDetails.getId())
                                .orElseThrow(() -> new RuntimeException("User not found"));

                EmployeeProfile profile = employeeProfileRepository.findByUserId(user.getId())
                                .orElseThrow(() -> new RuntimeException("Employee profile not found"));

                // Get all projects where this employee is a team member
                List<TeamMember> teamMemberships = teamMemberRepository.findByEmployeeId(user.getId());

                List<EmployeeProjectsResponse.ProjectInfo> projects = teamMemberships.stream()
                                .map(tm -> {
                                        EmployeeProjectsResponse.ProjectInfo projectInfo = new EmployeeProjectsResponse.ProjectInfo();
                                        projectInfo.setProjectId(tm.getProject().getId());
                                        projectInfo.setProjectName(tm.getProject().getName());
                                        projectInfo.setDescription(tm.getProject().getDescription());
                                        projectInfo.setRoleInProject(tm.getRoleInProject());
                                        projectInfo.setAllocationPercentage(tm.getAllocationPercentage());
                                        projectInfo.setProjectStatus(tm.getProject().getStatus().name());
                                        projectInfo.setProjectStartDate(tm.getProject().getStartDate());
                                        projectInfo.setProjectEndDate(tm.getProject().getEndDate());
                                        projectInfo.setJoinedDate(tm.getJoinedDate());
                                        projectInfo.setManagerName(tm.getProject().getManager().getFullName());
                                        projectInfo.setManagerEmail(tm.getProject().getManager().getEmail());
                                        return projectInfo;
                                })
                                .collect(Collectors.toList());

                EmployeeProjectsResponse response = new EmployeeProjectsResponse();
                response.setProjects(projects);

                // Get reporting manager info from department (if available)
                // For now, we'll get the first project's manager or leave it null
                if (!projects.isEmpty()) {
                        EmployeeProjectsResponse.ManagerInfo managerInfo = new EmployeeProjectsResponse.ManagerInfo();
                        TeamMember firstMembership = teamMemberships.get(0);
                        User manager = firstMembership.getProject().getManager();
                        managerInfo.setManagerId(manager.getId());
                        managerInfo.setManagerName(manager.getFullName());
                        managerInfo.setManagerEmail(manager.getEmail());
                        managerInfo.setManagerRole(manager.getRole().name());
                        
                        // Try to get manager's profile info
                        EmployeeProfile managerProfile = employeeProfileRepository.findByUserId(manager.getId())
                                        .orElse(null);
                        if (managerProfile != null) {
                                managerInfo.setDepartment(managerProfile.getDepartment());
                                managerInfo.setDesignation(managerProfile.getDesignation());
                        }
                        
                        response.setReportingManager(managerInfo);
                }

                return response;
        }

        private EmployeeProfileResponse mapToResponse(EmployeeProfile profile) {
                User user = profile.getUser();
                var company = profile.getCompany();

                return EmployeeProfileResponse.builder()
                                .id(user.getId())
                                .username(user.getUsername())
                                .email(user.getEmail())
                                .fullName(user.getFullName())
                                .role(user.getRole().name())
                                .companyId(company != null ? company.getId() : null)
                                .companyName(company != null ? company.getName() : null)
                                .companyLogoUrl(company != null ? company.getLogoUrl() : null)
                                .employeeCode(profile.getEmployeeCode())
                                .department(profile.getDepartment())
                                .designation(profile.getDesignation())
                                .dateOfJoining(profile.getDateOfJoining())
                                .dateOfBirth(profile.getDateOfBirth())
                                .phoneNumber(profile.getPhoneNumber())
                                .address(profile.getAddress())
                                .emergencyContact(profile.getEmergencyContact())
                                .salary(profile.getSalary())
                                .profilePictureUrl(profile.getProfilePictureUrl())
                                .createdAt(profile.getCreatedAt())
                                .build();
        }
}
