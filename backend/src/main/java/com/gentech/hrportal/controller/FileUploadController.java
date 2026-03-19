package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/upload")
@PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SOFTWARE_ENGINEER', 'MANAGER', 'GENERAL_MANAGER', 'DEVELOPER', 'ADMIN', 'SUPER_ADMIN')")
public class FileUploadController {

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/profile-picture")
    @PreAuthorize("hasAnyRole('HR', 'HR_MANAGER', 'SOFTWARE_ENGINEER', 'MANAGER', 'GENERAL_MANAGER', 'DEVELOPER', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.storeFile(file, "profile-pictures");
            return ResponseEntity.ok(new UploadResponse(fileUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Could not upload file: " + e.getMessage()));
        }
    }

    @PostMapping("/company-logo")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> uploadCompanyLogo(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = fileStorageService.storeFile(file, "company-logos");
            return ResponseEntity.ok(new UploadResponse(fileUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Could not upload file: " + e.getMessage()));
        }
    }

    @PostMapping("/document")
    @PreAuthorize("hasAnyRole('DEVELOPER', 'SOFTWARE_ENGINEER', 'HR', 'MANAGER', 'GENERAL_MANAGER', 'ADMIN')")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file, 
                                            @RequestParam("employeeId") Long employeeId) {
        try {
            String subDirectory = "documents/employee_" + employeeId;
            String fileUrl = fileStorageService.storeFile(file, subDirectory);
            return ResponseEntity.ok(new UploadResponse(fileUrl));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Could not upload file: " + e.getMessage()));
        }
    }

    public static class UploadResponse {
        private String fileUrl;

        public UploadResponse(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }
    }
}
