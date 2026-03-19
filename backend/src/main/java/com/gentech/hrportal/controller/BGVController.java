package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.*;
import com.gentech.hrportal.entity.*;
import com.gentech.hrportal.security.UserDetailsImpl;
import com.gentech.hrportal.service.*;
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
@RequestMapping("/api/bgv")
public class BGVController {
    
    @Autowired
    private BGVService bgvService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    // Admin: Initiate BGV for an employee
    @PostMapping("/initiate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    @Transactional
    public ResponseEntity<?> initiateBGV(@RequestBody BGVInitiateRequest request) {
        try {
            Long adminId = getCurrentUserId();
            BGVRequest bgvRequest = bgvService.initiateBGV(adminId, request);
            return ResponseEntity.ok(convertToMap(bgvRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Employee: Get my BGV status
    @GetMapping("/my-status")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('HR_MANAGER')")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getMyBGVStatus() {
        try {
            Long employeeId = getCurrentUserId();
            Optional<BGVRequest> request = bgvService.getActiveBGVRequest(employeeId);
            if (request.isPresent()) {
                return ResponseEntity.ok(convertToMap(request.get()));
            }
            return ResponseEntity.ok(new MessageResponse("No active BGV request"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Employee: Get my BGV documents
    @GetMapping("/my-documents/{bgvRequestId}")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getMyDocuments(@PathVariable Long bgvRequestId) {
        try {
            Long employeeId = getCurrentUserId();
            List<BGVDocument> documents = bgvService.getBGVDocuments(bgvRequestId);
            List<Map<String, Object>> response = documents.stream()
                .map(this::convertDocumentToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Employee: Upload document
    @PostMapping("/upload-document")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("bgvRequestId") Long bgvRequestId,
            @RequestParam("documentType") String documentType,
            @RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.storeFile(file, "bgv-documents");
            BGVDocument.DocumentType docType = BGVDocument.DocumentType.valueOf(documentType);
            BGVDocument document = bgvService.uploadDocument(bgvRequestId, docType, fileUrl, file.getOriginalFilename());
            return ResponseEntity.ok(convertDocumentToMap(document));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Employee: Submit for verification
    @PostMapping("/submit/{bgvRequestId}")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> submitForVerification(@PathVariable Long bgvRequestId) {
        try {
            Long employeeId = getCurrentUserId();
            BGVRequest request = bgvService.submitForVerification(employeeId, bgvRequestId);
            return ResponseEntity.ok(convertToMap(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Admin: Get all BGV requests for company
    @GetMapping("/company-requests")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getCompanyRequests() {
        try {
            Long adminId = getCurrentUserId();
            // Get company from admin's profile
            // This is simplified - ideally get company from admin
            List<BGVRequest> requests = bgvService.getAllBGVRequests();
            List<Map<String, Object>> response = requests.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Admin: Get pending BGV requests
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getPendingRequests() {
        try {
            List<BGVRequest> requests = bgvService.getPendingBGVRequests();
            List<Map<String, Object>> response = requests.stream()
                .map(this::convertToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Admin: Get documents for a BGV request
    @GetMapping("/documents/{bgvRequestId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getBGVDocuments(@PathVariable Long bgvRequestId) {
        try {
            List<BGVDocument> documents = bgvService.getBGVDocuments(bgvRequestId);
            List<Map<String, Object>> response = documents.stream()
                .map(this::convertDocumentToMap)
                .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Admin: Verify document
    @PostMapping("/verify-document")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> verifyDocument(@RequestBody BGVVerifyRequest request) {
        try {
            Long adminId = getCurrentUserId();
            BGVDocument document = bgvService.verifyDocument(adminId, request);
            return ResponseEntity.ok(convertDocumentToMap(document));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Admin: Complete BGV verification
    @PostMapping("/complete/{bgvRequestId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> completeVerification(
            @PathVariable Long bgvRequestId,
            @RequestParam boolean approved,
            @RequestParam(required = false) String remarks) {
        try {
            Long adminId = getCurrentUserId();
            BGVRequest request = bgvService.completeVerification(adminId, bgvRequestId, approved, remarks);
            return ResponseEntity.ok(convertToMap(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
    
    private Map<String, Object> convertToMap(BGVRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", request.getId());
        
        Map<String, Object> empMap = new HashMap<>();
        if (request.getEmployee() != null) {
            empMap.put("id", request.getEmployee().getId());
            empMap.put("fullName", request.getEmployee().getFullName());
            empMap.put("email", request.getEmployee().getEmail());
        }
        map.put("employee", empMap);
        
        map.put("status", request.getStatus());
        map.put("employeeType", request.getEmployeeType());
        map.put("remarks", request.getRemarks());
        map.put("initiatedAt", request.getInitiatedAt());
        map.put("submittedAt", request.getSubmittedAt());
        map.put("verifiedAt", request.getVerifiedAt());
        
        return map;
    }
    
    private Map<String, Object> convertDocumentToMap(BGVDocument doc) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", doc.getId());
        map.put("documentType", doc.getDocumentType());
        map.put("documentName", doc.getDocumentName());
        map.put("fileUrl", doc.getFileUrl());
        map.put("fileName", doc.getFileName());
        map.put("status", doc.getStatus());
        map.put("remarks", doc.getRemarks());
        map.put("uploadedAt", doc.getUploadedAt());
        map.put("verifiedAt", doc.getVerifiedAt());
        return map;
    }
}
