package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.dto.ProfileEditApprovalRequest;
import com.gentech.hrportal.dto.ProfileEditRequestDto;
import com.gentech.hrportal.dto.ProfileEditResponse;
import com.gentech.hrportal.entity.ProfileEditRequest;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.security.UserDetailsImpl;
import com.gentech.hrportal.service.ProfileEditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/profile-edit")
public class ProfileEditController {
    
    @Autowired
    private ProfileEditService profileEditService;
    
    /**
     * Request a profile edit
     * POST /api/profile-edit/request
     * Request body: { fieldName: "phoneNumber", newValue: "+1234567890" }
     */
    @PostMapping("/request")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> requestProfileEdit(@RequestBody ProfileEditRequestDto requestDto) {
        try {
            Long employeeId = getCurrentUserId();
            
            // Verify the employee is editing their own profile
            if (requestDto.getEmployeeId() != null && !requestDto.getEmployeeId().equals(employeeId)) {
                // Only admin can edit other profiles
                if (!isAdmin()) {
                    return ResponseEntity.badRequest().body(new MessageResponse("You can only request edits for your own profile"));
                }
                employeeId = requestDto.getEmployeeId();
            }
            
            ProfileEditRequest request = profileEditService.requestProfileEdit(
                employeeId, 
                requestDto.getFieldName(), 
                requestDto.getNewValue()
            );
            
            return ResponseEntity.ok(new ProfileEditResponse(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Get my edit requests
     * GET /api/profile-edit/my-requests
     */
    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyEditRequests() {
        try {
            Long employeeId = getCurrentUserId();
            List<ProfileEditRequest> requests = profileEditService.getEmployeeEditRequests(employeeId);
            List<ProfileEditResponse> responses = requests.stream()
                .map(ProfileEditResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Get company edit requests (admin)
     * GET /api/profile-edit/company/{companyId}
     */
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getCompanyEditRequests(@PathVariable Long companyId) {
        try {
            List<ProfileEditRequest> requests = profileEditService.getCompanyEditRequests(companyId);
            List<ProfileEditResponse> responses = requests.stream()
                .map(ProfileEditResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Get pending edit requests for a company (admin)
     * GET /api/profile-edit/company/{companyId}/pending
     */
    @GetMapping("/company/{companyId}/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getPendingEditRequests(@PathVariable Long companyId) {
        try {
            List<ProfileEditRequest> requests = profileEditService.getPendingEditRequests(companyId);
            List<ProfileEditResponse> responses = requests.stream()
                .map(ProfileEditResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Approve or reject a profile edit request
     * POST /api/profile-edit/approve
     * Request body: { requestId: 1, approved: true/false, rejectionReason: "" }
     */
    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> approveProfileEdit(@RequestBody ProfileEditApprovalRequest request) {
        try {
            Long approverId = getCurrentUserId();
            
            ProfileEditRequest editRequest;
            if (request.isApproved()) {
                editRequest = profileEditService.approveProfileEdit(request.getRequestId(), approverId);
            } else {
                editRequest = profileEditService.rejectProfileEdit(
                    request.getRequestId(), 
                    approverId, 
                    request.getRejectionReason()
                );
            }
            
            return ResponseEntity.ok(new ProfileEditResponse(editRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Get a specific edit request by ID
     * GET /api/profile-edit/{requestId}
     */
    @GetMapping("/{requestId}")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getEditRequestById(@PathVariable Long requestId) {
        try {
            Long currentUserId = getCurrentUserId();
            ProfileEditRequest request = profileEditService.getEditRequestById(requestId);
            
            // Check if user has permission to view this request
            if (!request.getEmployee().getId().equals(currentUserId) && !isAdminOrManager()) {
                return ResponseEntity.badRequest().body(new MessageResponse("You don't have permission to view this request"));
            }
            
            return ResponseEntity.ok(new ProfileEditResponse(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
    
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPER_ADMIN"));
    }
    
    private boolean isAdminOrManager() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") 
                || a.getAuthority().equals("ROLE_SUPER_ADMIN")
                || a.getAuthority().equals("ROLE_HR_MANAGER")
                || a.getAuthority().equals("ROLE_MANAGER")
                || a.getAuthority().equals("ROLE_GENERAL_MANAGER"));
    }
}
