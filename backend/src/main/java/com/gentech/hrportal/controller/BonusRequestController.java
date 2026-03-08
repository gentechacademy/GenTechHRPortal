package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.BonusApprovalRequest;
import com.gentech.hrportal.dto.BonusRequestDto;
import com.gentech.hrportal.dto.BonusResponse;
import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.entity.BonusRequest;
import com.gentech.hrportal.security.UserDetailsImpl;
import com.gentech.hrportal.service.BonusRequestService;
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
@RequestMapping("/api/bonus")
public class BonusRequestController {

    @Autowired
    private BonusRequestService bonusRequestService;

    /**
     * Create a new bonus request (Admin only)
     * POST /api/bonus/request
     */
    @PostMapping("/request")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> createBonusRequest(@RequestBody BonusRequestDto dto) {
        try {
            Long requestedById = getCurrentUserId();
            BonusRequest request = bonusRequestService.createBonusRequest(dto, requestedById);
            return ResponseEntity.ok(new BonusResponse(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get all bonus requests for a company (Admin view)
     * GET /api/bonus/company/{companyId}
     */
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getCompanyBonusRequests(@PathVariable Long companyId) {
        try {
            List<BonusRequest> requests = bonusRequestService.getCompanyBonusRequests(companyId);
            List<BonusResponse> responses = requests.stream()
                    .map(BonusResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get pending bonus requests for a company (Admin view)
     * GET /api/bonus/company/{companyId}/pending
     */
    @GetMapping("/company/{companyId}/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getPendingBonusRequests(@PathVariable Long companyId) {
        try {
            List<BonusRequest> requests = bonusRequestService.getCompanyBonusRequestsByStatus(
                    companyId, BonusRequest.BonusStatus.PENDING);
            List<BonusResponse> responses = requests.stream()
                    .map(BonusResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get all pending bonus requests (Super Admin view)
     * GET /api/bonus/pending
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllPendingBonusRequests() {
        try {
            List<BonusRequest> requests = bonusRequestService.getAllPendingRequests();
            List<BonusResponse> responses = requests.stream()
                    .map(BonusResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get all bonus requests (Super Admin view)
     * GET /api/bonus/all
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllBonusRequests() {
        try {
            List<BonusRequest> requests = bonusRequestService.getAllBonusRequests();
            List<BonusResponse> responses = requests.stream()
                    .map(BonusResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get my bonus requests (Admin view - requests created by me)
     * GET /api/bonus/my-requests
     */
    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getMyBonusRequests() {
        try {
            Long requestedById = getCurrentUserId();
            List<BonusRequest> requests = bonusRequestService.getMyBonusRequests(requestedById);
            List<BonusResponse> responses = requests.stream()
                    .map(BonusResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get my bonus requests as employee (for currently logged-in employee)
     * GET /api/bonus/my-bonuses
     */
    @GetMapping("/my-bonuses")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getMyBonusesAsEmployee() {
        try {
            Long employeeId = getCurrentUserId();
            List<BonusRequest> requests = bonusRequestService.getEmployeeBonusRequests(employeeId);
            List<BonusResponse> responses = requests.stream()
                    .map(BonusResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get employee bonus requests
     * GET /api/bonus/employee/{employeeId}
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getEmployeeBonusRequests(@PathVariable Long employeeId) {
        try {
            List<BonusRequest> requests = bonusRequestService.getEmployeeBonusRequests(employeeId);
            List<BonusResponse> responses = requests.stream()
                    .map(BonusResponse::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Approve or reject a bonus request (Super Admin only)
     * POST /api/bonus/approve
     */
    @PostMapping("/approve")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> approveBonusRequest(@RequestBody BonusApprovalRequest request) {
        try {
            Long approvedById = getCurrentUserId();
            
            BonusRequest bonusRequest;
            if (request.isApproved()) {
                bonusRequest = bonusRequestService.approveBonusRequest(request.getRequestId(), approvedById);
            } else {
                bonusRequest = bonusRequestService.rejectBonusRequest(
                        request.getRequestId(), 
                        approvedById, 
                        request.getRejectionReason()
                );
            }
            
            return ResponseEntity.ok(new BonusResponse(bonusRequest));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get a specific bonus request by ID
     * GET /api/bonus/{requestId}
     */
    @GetMapping("/{requestId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getBonusRequestById(@PathVariable Long requestId) {
        try {
            BonusRequest request = bonusRequestService.getBonusRequestById(requestId);
            return ResponseEntity.ok(new BonusResponse(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Delete a bonus request (only if not approved)
     * DELETE /api/bonus/{requestId}
     */
    @DeleteMapping("/{requestId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> deleteBonusRequest(@PathVariable Long requestId) {
        try {
            bonusRequestService.deleteBonusRequest(requestId);
            return ResponseEntity.ok(new MessageResponse("Bonus request deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get total approved bonus for an employee in a specific month/year
     * GET /api/bonus/employee/{employeeId}/total?month={month}&year={year}
     */
    @GetMapping("/employee/{employeeId}/total")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getTotalApprovedBonus(
            @PathVariable Long employeeId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        try {
            Double total = bonusRequestService.getTotalApprovedBonusForEmployeeMonth(employeeId, month, year);
            return ResponseEntity.ok(java.util.Map.of("totalBonus", total));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Check if there's a pending bonus request for an employee in a specific month/year
     * GET /api/bonus/employee/{employeeId}/pending?month={month}&year={year}
     */
    @GetMapping("/employee/{employeeId}/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> hasPendingBonusRequest(
            @PathVariable Long employeeId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        try {
            boolean hasPending = bonusRequestService.hasPendingBonusForEmployeeMonth(employeeId, month, year);
            return ResponseEntity.ok(java.util.Map.of("hasPendingBonus", hasPending));
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
