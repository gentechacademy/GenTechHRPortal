package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.CreateEmployeeRequest;
import com.gentech.hrportal.dto.EmployeeFullDetailsResponse;
import com.gentech.hrportal.dto.EmployeeProfileResponse;
import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.entity.Company;
import com.gentech.hrportal.repository.AttendanceRepository;
import com.gentech.hrportal.repository.UserRepository;
import com.gentech.hrportal.service.AdminService;
import com.gentech.hrportal.service.EmployeeSearchService;
import com.gentech.hrportal.service.SuperAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private SuperAdminService superAdminService;

    @Autowired
    private EmployeeSearchService employeeSearchService;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeProfileResponse>> getAllEmployees() {
        return ResponseEntity.ok(adminService.getAllEmployees());
    }

    @GetMapping("/employees/company/{companyId}")
    public ResponseEntity<List<EmployeeProfileResponse>> getEmployeesByCompany(@PathVariable Long companyId) {
        return ResponseEntity.ok(adminService.getEmployeesByCompany(companyId));
    }

    @PostMapping("/employees")
    public ResponseEntity<?> createEmployee(@RequestBody CreateEmployeeRequest request) {
        try {
            EmployeeProfileResponse employee = adminService.createEmployee(request);
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminService.getEmployeeById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        try {
            adminService.deleteEmployee(id);
            return ResponseEntity.ok(new MessageResponse("Employee deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody CreateEmployeeRequest request) {
        try {
            EmployeeProfileResponse employee = adminService.updateEmployee(id, request);
            return ResponseEntity.ok(employee);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/my-company")
    public ResponseEntity<?> getMyCompany() {
        try {
            Company company = adminService.getAdminCompany();
            return ResponseEntity.ok(company);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/companies")
    public ResponseEntity<List<Company>> getAllCompanies() {
        return ResponseEntity.ok(superAdminService.getAllCompanies());
    }

    @DeleteMapping("/attendance/reset/{username}")
    public ResponseEntity<?> resetAttendanceForEmployee(@PathVariable String username) {
        try {
            adminService.resetAttendanceForEmployee(username);
            return ResponseEntity.ok(new MessageResponse("All attendance records cleared for " + username));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // Employee Search Endpoints

    @GetMapping("/employees/search")
    public ResponseEntity<?> searchEmployees(@RequestParam String term) {
        try {
            List<EmployeeProfileResponse> employees = employeeSearchService.searchEmployee(term);
            return ResponseEntity.ok(employees);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/employees/{id}/full-details")
    public ResponseEntity<?> getEmployeeFullDetails(@PathVariable Long id) {
        try {
            EmployeeFullDetailsResponse employeeDetails = employeeSearchService.getEmployeeFullDetails(id);
            return ResponseEntity.ok(employeeDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/employees/{id}/work-history")
    public ResponseEntity<?> getEmployeeWorkHistory(@PathVariable Long id) {
        try {
            Map<String, Object> workHistory = employeeSearchService.getEmployeeWorkHistory(id);
            return ResponseEntity.ok(workHistory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/employees/{id}/attendance-summary")
    public ResponseEntity<?> getEmployeeAttendanceSummary(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        try {
            EmployeeFullDetailsResponse.AttendanceSummary summary = 
                    employeeSearchService.getEmployeeAttendanceSummary(id, from, to);
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/employees/{id}/leave-summary")
    public ResponseEntity<?> getEmployeeLeaveSummary(@PathVariable Long id) {
        try {
            EmployeeFullDetailsResponse.LeaveSummary summary = employeeSearchService.getEmployeeLeaveSummary(id);
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/employees/{id}/project-history")
    public ResponseEntity<?> getEmployeeProjectHistory(@PathVariable Long id) {
        try {
            var projectHistory = employeeSearchService.getEmployeeProjectHistory(id);
            return ResponseEntity.ok(projectHistory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
