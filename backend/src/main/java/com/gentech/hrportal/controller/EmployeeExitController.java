package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.EmployeeExitRequest;
import com.gentech.hrportal.dto.EmployeeExitResponse;
import com.gentech.hrportal.dto.ExitApprovalRequest;
import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.entity.EmployeeExit;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.security.UserDetailsImpl;
import com.gentech.hrportal.service.EmployeeExitService;
import com.gentech.hrportal.service.UserService;
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
@RequestMapping("/api")
public class EmployeeExitController {
    
    @Autowired
    private EmployeeExitService employeeExitService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Employee submits resignation request
     * POST /api/employee/exit/request
     */
    @PostMapping("/employee/exit/request")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> submitResignationRequest(@RequestBody EmployeeExitRequest request) {
        try {
            Long employeeId = getCurrentUserId();
            EmployeeExit exit = employeeExitService.createResignationRequest(employeeId, request);
            return ResponseEntity.ok(new EmployeeExitResponse(exit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Employee views their exit details
     * GET /api/employee/exit/my-exit
     */
    @GetMapping("/employee/exit/my-exit")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getMyExitDetails() {
        try {
            Long employeeId = getCurrentUserId();
            EmployeeExit exit = employeeExitService.getEmployeeExitDetails(employeeId);
            return ResponseEntity.ok(new EmployeeExitResponse(exit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Admin views all exit employees
     * GET /api/admin/exit/all
     */
    @GetMapping("/admin/exit/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getAllExitEmployees() {
        try {
            List<EmployeeExit> exits = employeeExitService.getAllExitEmployees();
            List<EmployeeExitResponse> responses = exits.stream()
                .map(EmployeeExitResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Admin views active employees
     * GET /api/admin/exit/active
     */
    @GetMapping("/admin/exit/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getActiveEmployees() {
        try {
            List<EmployeeExit> exits = employeeExitService.getActiveEmployees();
            List<EmployeeExitResponse> responses = exits.stream()
                .map(EmployeeExitResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Admin views exited employees (resigned)
     * GET /api/admin/exit/exited
     */
    @GetMapping("/admin/exit/exited")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getExitedEmployees() {
        try {
            List<EmployeeExit> exits = employeeExitService.getExitedEmployees();
            List<EmployeeExitResponse> responses = exits.stream()
                .map(EmployeeExitResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Manager approves or rejects exit request
     * POST /api/exit/manager/approve
     */
    @PostMapping("/exit/manager/approve")
    @PreAuthorize("hasRole('MANAGER') or hasRole('HR_MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> managerApproveExit(@RequestBody ExitApprovalRequest request) {
        try {
            Long managerId = getCurrentUserId();
            EmployeeExit exit;
            if ("APPROVED".equalsIgnoreCase(request.getApprovalStatus())) {
                exit = employeeExitService.approveByManager(request.getExitId(), managerId, request.getComments());
            } else if ("REJECTED".equalsIgnoreCase(request.getApprovalStatus())) {
                exit = employeeExitService.rejectByManager(request.getExitId(), managerId, request.getComments());
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid approval status. Use APPROVED or REJECTED"));
            }
            return ResponseEntity.ok(new EmployeeExitResponse(exit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Admin approves or rejects exit request
     * POST /api/exit/admin/approve
     */
    @PostMapping("/exit/admin/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> adminApproveExit(@RequestBody ExitApprovalRequest request) {
        try {
            Long adminId = getCurrentUserId();
            EmployeeExit exit;
            if ("APPROVED".equalsIgnoreCase(request.getApprovalStatus())) {
                exit = employeeExitService.approveByAdmin(request.getExitId(), adminId, request.getComments());
            } else if ("REJECTED".equalsIgnoreCase(request.getApprovalStatus())) {
                exit = employeeExitService.rejectByAdmin(request.getExitId(), adminId, request.getComments());
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid approval status. Use APPROVED or REJECTED"));
            }
            return ResponseEntity.ok(new EmployeeExitResponse(exit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Manager views pending exit approvals
     * GET /api/manager/exit/pending
     */
    @GetMapping("/manager/exit/pending")
    @PreAuthorize("hasRole('MANAGER') or hasRole('HR_MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getPendingForManager() {
        try {
            Long managerId = getCurrentUserId();
            List<EmployeeExit> exits = employeeExitService.getPendingForManager(managerId);
            List<EmployeeExitResponse> responses = exits.stream()
                .map(EmployeeExitResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Admin views pending exit approvals
     * GET /api/admin/exit/pending
     */
    @GetMapping("/admin/exit/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getPendingForAdmin(@RequestParam(required = false) Long companyId) {
        try {
            Long adminId = getCurrentUserId();
            User admin = userService.getUserById(adminId);
            
            Long targetCompanyId = companyId;
            if (targetCompanyId == null && admin.getCompany() != null) {
                targetCompanyId = admin.getCompany().getId();
            }
            
            if (targetCompanyId == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("Company ID is required"));
            }
            
            List<EmployeeExit> exits = employeeExitService.getPendingForAdmin(targetCompanyId);
            List<EmployeeExitResponse> responses = exits.stream()
                .map(EmployeeExitResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Get exit details by ID
     * GET /api/exit/{id}
     */
    @GetMapping("/exit/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getExitById(@PathVariable Long id) {
        try {
            EmployeeExit exit = employeeExitService.getExitById(id);
            return ResponseEntity.ok(new EmployeeExitResponse(exit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Admin views terminated employees
     * GET /api/admin/exit/terminated
     */
    @GetMapping("/admin/exit/terminated")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getTerminatedEmployees() {
        try {
            List<EmployeeExit> exits = employeeExitService.getTerminatedEmployees();
            List<EmployeeExitResponse> responses = exits.stream()
                .map(EmployeeExitResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Admin views exit employees by company
     * GET /api/admin/exit/company/{companyId}
     */
    @GetMapping("/admin/exit/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getCompanyExitEmployees(@PathVariable Long companyId) {
        try {
            List<EmployeeExit> exits = employeeExitService.getCompanyExitEmployees(companyId);
            List<EmployeeExitResponse> responses = exits.stream()
                .map(EmployeeExitResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
}
