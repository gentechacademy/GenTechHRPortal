package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.CreateCompanyRequest;
import com.gentech.hrportal.dto.CreateUserRequest;
import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.entity.Company;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.service.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/superadmin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminController {

    @Autowired
    private SuperAdminService superAdminService;

    // Admin Management
    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAllAdmins() {
        return ResponseEntity.ok(superAdminService.getAllAdmins());
    }

    @PostMapping("/admins")
    public ResponseEntity<?> createAdmin(@RequestBody CreateUserRequest request) {
        try {
            User admin = superAdminService.createAdmin(request);
            return ResponseEntity.ok(admin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/admins/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        superAdminService.deleteAdmin(id);
        return ResponseEntity.ok(new MessageResponse("Admin deleted successfully"));
    }

    // Company Management
    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(superAdminService.getAllCompanies());
    }

    @PostMapping("/companies")
    public ResponseEntity<?> createCompany(@RequestBody CreateCompanyRequest request) {
        try {
            Company company = superAdminService.createCompany(request);
            return ResponseEntity.ok(company);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        superAdminService.deleteCompany(id);
        return ResponseEntity.ok(new MessageResponse("Company deleted successfully"));
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable Long id, @RequestBody CreateCompanyRequest request) {
        try {
            Company company = superAdminService.updateCompany(id, request);
            return ResponseEntity.ok(company);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/admins/{id}")
    public ResponseEntity<?> updateAdmin(@PathVariable Long id, @RequestBody CreateUserRequest request) {
        try {
            User admin = superAdminService.updateAdmin(id, request);
            return ResponseEntity.ok(admin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Map Admin to Company
    @PutMapping("/admins/{adminId}/company/{companyId}")
    public ResponseEntity<?> mapAdminToCompany(@PathVariable Long adminId, @PathVariable Long companyId) {
        try {
            User admin = superAdminService.mapAdminToCompany(adminId, companyId);
            return ResponseEntity.ok(admin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
