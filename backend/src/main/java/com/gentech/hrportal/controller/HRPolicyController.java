package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.entity.*;
import com.gentech.hrportal.security.UserDetailsImpl;
import com.gentech.hrportal.service.FileStorageService;
import com.gentech.hrportal.service.HRPolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/policies")
public class HRPolicyController {
    
    @Autowired
    private HRPolicyService policyService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    // Create new policy/form
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> createPolicy(
            @RequestParam("policyName") String policyName,
            @RequestParam("policyCode") String policyCode,
            @RequestParam("description") String description,
            @RequestParam("policyType") String policyType,
            @RequestParam("category") String category,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.storeFile(file, "hr-policies");
            
            HRPolicy policy = new HRPolicy();
            policy.setPolicyName(policyName);
            policy.setPolicyCode(policyCode);
            policy.setDescription(description);
            policy.setPolicyType(HRPolicy.PolicyType.valueOf(policyType));
            policy.setCategory(HRPolicy.PolicyCategory.valueOf(category));
            policy.setFileUrl(fileUrl);
            policy.setFileName(file.getOriginalFilename());
            
            HRPolicy savedPolicy = policyService.createPolicy(policy);
            return ResponseEntity.ok(convertPolicyToMap(savedPolicy));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Get all policies (admin)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getAllPolicies() {
        try {
            List<HRPolicy> policies = policyService.getAllPolicies();
            List<Map<String, Object>> response = policies.stream()
                .map(this::convertPolicyToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Get all active policies
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getAllActivePolicies() {
        try {
            List<HRPolicy> policies = policyService.getAllActivePolicies();
            List<Map<String, Object>> response = policies.stream()
                .map(this::convertPolicyToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Get policies by type (POLICY or FORM)
    @GetMapping("/type/{type}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getPoliciesByType(@PathVariable String type) {
        try {
            List<HRPolicy> policies = policyService.getPoliciesByType(HRPolicy.PolicyType.valueOf(type));
            List<Map<String, Object>> response = policies.stream()
                .map(this::convertPolicyToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Admin: Assign policy to employee
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    @Transactional
    public ResponseEntity<?> assignPolicyToEmployee(@RequestBody Map<String, Object> request) {
        try {
            Long policyId = Long.valueOf(request.get("policyId").toString());
            Long employeeId = Long.valueOf(request.get("employeeId").toString());
            
            PolicyAcknowledgment acknowledgment = policyService.assignPolicyToEmployee(policyId, employeeId);
            return ResponseEntity.ok(convertAcknowledgmentToMap(acknowledgment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Employee: Get my pending policies
    @GetMapping("/my-pending")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('HR_MANAGER')")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getMyPendingPolicies() {
        try {
            Long employeeId = getCurrentUserId();
            List<PolicyAcknowledgment> acknowledgments = policyService.getPendingPolicies(employeeId);
            List<Map<String, Object>> response = acknowledgments.stream()
                .map(this::convertAcknowledgmentToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Employee: Get all my policies
    @GetMapping("/my-all")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getMyAllPolicies() {
        try {
            Long employeeId = getCurrentUserId();
            List<PolicyAcknowledgment> acknowledgments = policyService.getEmployeePolicies(employeeId);
            List<Map<String, Object>> response = acknowledgments.stream()
                .map(this::convertAcknowledgmentToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Employee: Get my acknowledged policies
    @GetMapping("/my-acknowledged")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getMyAcknowledgedPolicies() {
        try {
            Long employeeId = getCurrentUserId();
            List<PolicyAcknowledgment> acknowledgments = policyService.getAcknowledgedPolicies(employeeId);
            List<Map<String, Object>> response = acknowledgments.stream()
                .map(this::convertAcknowledgmentToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Employee: Acknowledge policy
    @PostMapping("/acknowledge/{acknowledgmentId}")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> acknowledgePolicy(
            @PathVariable Long acknowledgmentId,
            @RequestBody(required = false) Map<String, String> request) {
        try {
            String signature = request != null ? request.get("signature") : null;
            String ipAddress = request != null ? request.get("ipAddress") : null;
            
            PolicyAcknowledgment acknowledgment = policyService.acknowledgePolicy(acknowledgmentId, signature, ipAddress);
            return ResponseEntity.ok(convertAcknowledgmentToMap(acknowledgment));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
    
    private Map<String, Object> convertPolicyToMap(HRPolicy policy) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", policy.getId());
        map.put("policyName", policy.getPolicyName());
        map.put("policyCode", policy.getPolicyCode());
        map.put("description", policy.getDescription());
        map.put("fileUrl", policy.getFileUrl());
        map.put("fileName", policy.getFileName());
        map.put("policyType", policy.getPolicyType());
        map.put("category", policy.getCategory());
        map.put("active", policy.isActive());
        map.put("createdAt", policy.getCreatedAt());
        return map;
    }
    
    private Map<String, Object> convertAcknowledgmentToMap(PolicyAcknowledgment ack) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", ack.getId());
        
        if (ack.getPolicy() != null) {
            map.put("policy", convertPolicyToMap(ack.getPolicy()));
        }
        
        map.put("status", ack.getStatus());
        map.put("assignedAt", ack.getAssignedAt());
        map.put("acknowledgedAt", ack.getAcknowledgedAt());
        map.put("signature", ack.getSignature());
        
        return map;
    }
}
