package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.LeaveApprovalRequest;
import com.gentech.hrportal.dto.LeaveRequest;
import com.gentech.hrportal.dto.LeaveResponse;
import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.entity.Leave;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.security.UserDetailsImpl;
import com.gentech.hrportal.service.LeaveService;
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
@RequestMapping("/api/leaves")
public class LeaveController {
    
    @Autowired
    private LeaveService leaveService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/apply")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> applyForLeave(@RequestBody LeaveRequest request) {
        try {
            Long employeeId = getCurrentUserId();
            
            User employee = new User();
            employee.setId(employeeId);
            
            Leave leave = new Leave();
            leave.setEmployee(employee);
            leave.setLeaveType(request.getLeaveType());
            leave.setStartDate(request.getStartDate());
            leave.setEndDate(request.getEndDate());
            leave.setReason(request.getReason());
            
            Leave savedLeave = leaveService.applyForLeave(leave);
            return ResponseEntity.ok(new LeaveResponse(savedLeave));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/my-leaves")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyLeaves() {
        try {
            Long employeeId = getCurrentUserId();
            List<Leave> leaves = leaveService.getEmployeeLeaves(employeeId);
            List<LeaveResponse> responses = leaves.stream()
                .map(LeaveResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getCompanyLeaves(@PathVariable Long companyId) {
        try {
            List<Leave> leaves = leaveService.getCompanyLeaves(companyId);
            List<LeaveResponse> responses = leaves.stream()
                .map(LeaveResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/company/{companyId}/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getPendingLeavesByCompany(@PathVariable Long companyId) {
        try {
            List<Leave> leaves = leaveService.getPendingLeavesByCompany(companyId);
            List<LeaveResponse> responses = leaves.stream()
                .map(LeaveResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getAllPendingLeaves() {
        try {
            List<Leave> leaves = leaveService.getAllPendingLeaves();
            List<LeaveResponse> responses = leaves.stream()
                .map(LeaveResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> approveLeave(@RequestBody LeaveApprovalRequest request) {
        try {
            Long approverId = getCurrentUserId();
            Leave leave;
            if (request.isApproved()) {
                leave = leaveService.approveLeave(request.getLeaveId(), approverId);
            } else {
                leave = leaveService.rejectLeave(request.getLeaveId(), approverId, request.getRejectionReason());
            }
            return ResponseEntity.ok(new LeaveResponse(leave));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getEmployeeLeaves(@PathVariable Long employeeId) {
        try {
            List<Leave> leaves = leaveService.getEmployeeLeaves(employeeId);
            List<LeaveResponse> responses = leaves.stream()
                .map(LeaveResponse::new)
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
