package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.dto.ResignationApprovalDto;
import com.gentech.hrportal.dto.ResignationRequestDto;
import com.gentech.hrportal.entity.ResignationRequest;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.security.UserDetailsImpl;
import com.gentech.hrportal.service.ResignationService;
import com.gentech.hrportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/resignation")
public class ResignationController {
    
    @Autowired
    private ResignationService resignationService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Submit a new resignation request
     * POST /api/resignation/submit
     */
    @PostMapping("/submit")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> submitResignation(@RequestBody ResignationRequestDto request) {
        try {
            Long employeeId = getCurrentUserId();
            ResignationRequest resignation = resignationService.submitResignation(employeeId, request);
            return ResponseEntity.ok(convertToMap(resignation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Get my resignation requests
     * GET /api/resignation/my-requests
     */
    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getMyResignationRequests() {
        try {
            Long employeeId = getCurrentUserId();
            List<ResignationRequest> requests = resignationService.getMyResignationRequests(employeeId);
            List<Map<String, Object>> responses = requests.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Get pending resignation requests for manager approval
     * GET /api/resignation/pending/manager
     */
    @GetMapping("/pending/manager")
    @PreAuthorize("hasRole('MANAGER') or hasRole('HR_MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getPendingForManager() {
        try {
            Long managerId = getCurrentUserId();
            List<ResignationRequest> requests = resignationService.getPendingForManager(managerId);
            List<Map<String, Object>> responses = requests.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Get pending resignation requests for admin approval
     * GET /api/resignation/pending/admin/{companyId}
     */
    @GetMapping("/pending/admin/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getPendingForAdmin(@PathVariable Long companyId) {
        try {
            List<ResignationRequest> requests = resignationService.getPendingForAdmin(companyId);
            List<Map<String, Object>> responses = requests.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Approve or reject resignation request by manager
     * POST /api/resignation/approve/manager
     */
    @PostMapping("/approve/manager")
    @PreAuthorize("hasRole('MANAGER') or hasRole('HR_MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> approveByManager(@RequestBody ResignationApprovalDto request) {
        try {
            Long managerId = getCurrentUserId();
            ResignationRequest resignation = resignationService.approveByManager(
                request.getResignationId(), 
                managerId, 
                request
            );
            return ResponseEntity.ok(convertToMap(resignation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Approve or reject resignation request by admin
     * POST /api/resignation/approve/admin
     */
    @PostMapping("/approve/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> approveByAdmin(@RequestBody ResignationApprovalDto request) {
        try {
            Long adminId = getCurrentUserId();
            ResignationRequest resignation = resignationService.approveByAdmin(
                request.getResignationId(), 
                adminId, 
                request
            );
            return ResponseEntity.ok(convertToMap(resignation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Get a specific resignation request by ID
     * GET /api/resignation/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getResignationById(@PathVariable Long id) {
        try {
            ResignationRequest resignation = resignationService.getResignationById(id);
            return ResponseEntity.ok(convertToMap(resignation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
    
    private Map<String, Object> convertToMap(ResignationRequest resignation) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", resignation.getId());
        
        // Employee info
        Map<String, Object> employeeMap = new HashMap<>();
        if (resignation.getEmployee() != null) {
            employeeMap.put("id", resignation.getEmployee().getId());
            employeeMap.put("fullName", resignation.getEmployee().getFullName());
            employeeMap.put("email", resignation.getEmployee().getEmail());
            employeeMap.put("username", resignation.getEmployee().getUsername());
        }
        map.put("employee", employeeMap);
        
        // Company info
        Map<String, Object> companyMap = new HashMap<>();
        if (resignation.getCompany() != null) {
            companyMap.put("id", resignation.getCompany().getId());
            companyMap.put("name", resignation.getCompany().getName());
        }
        map.put("company", companyMap);
        
        map.put("requestDate", resignation.getRequestDate());
        map.put("proposedLastWorkingDay", resignation.getProposedLastWorkingDay());
        map.put("actualLastWorkingDay", resignation.getActualLastWorkingDay());
        map.put("noticePeriodDays", resignation.getNoticePeriodDays());
        map.put("reason", resignation.getReason());
        map.put("status", resignation.getStatus());
        map.put("managerRemarks", resignation.getManagerRemarks());
        map.put("adminRemarks", resignation.getAdminRemarks());
        
        // Manager approval info
        Map<String, Object> managerApprovedByMap = new HashMap<>();
        if (resignation.getManagerApprovedBy() != null) {
            managerApprovedByMap.put("id", resignation.getManagerApprovedBy().getId());
            managerApprovedByMap.put("fullName", resignation.getManagerApprovedBy().getFullName());
        }
        map.put("managerApprovedBy", managerApprovedByMap);
        
        // Admin approval info
        Map<String, Object> adminApprovedByMap = new HashMap<>();
        if (resignation.getAdminApprovedBy() != null) {
            adminApprovedByMap.put("id", resignation.getAdminApprovedBy().getId());
            adminApprovedByMap.put("fullName", resignation.getAdminApprovedBy().getFullName());
        }
        map.put("adminApprovedBy", adminApprovedByMap);
        
        map.put("managerApprovalDate", resignation.getManagerApprovalDate());
        map.put("adminApprovalDate", resignation.getAdminApprovalDate());
        map.put("createdAt", resignation.getCreatedAt());
        map.put("updatedAt", resignation.getUpdatedAt());
        
        return map;
    }
}
