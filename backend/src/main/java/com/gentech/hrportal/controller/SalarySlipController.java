package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.dto.SalarySlipRequest;
import com.gentech.hrportal.dto.SalarySlipResponse;
import com.gentech.hrportal.entity.SalarySlip;
import com.gentech.hrportal.entity.SalarySlip.SalarySlipStatus;
import com.gentech.hrportal.security.UserDetailsImpl;
import com.gentech.hrportal.service.PdfGenerationService;
import com.gentech.hrportal.service.SalarySlipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/salary")
public class SalarySlipController {

    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    // ==================== EMPLOYEE ENDPOINTS ====================

    /**
     * Get my salary slips
     */
    @GetMapping("/my-slips")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getMySalarySlips() {
        try {
            Long employeeId = getCurrentUserId();
            List<SalarySlipResponse> slips = salarySlipService.getEmployeeSalarySlips(employeeId);
            return ResponseEntity.ok(slips);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get my salary slips for a specific year
     */
    @GetMapping("/my-slips/year/{year}")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getMySalarySlipsByYear(@PathVariable Integer year) {
        try {
            Long employeeId = getCurrentUserId();
            List<SalarySlipResponse> allSlips = salarySlipService.getEmployeeSalarySlips(employeeId);
            List<SalarySlipResponse> yearSlips = allSlips.stream()
                    .filter(slip -> slip.getYear().equals(year))
                    .sorted((a, b) -> b.getMonth().compareTo(a.getMonth()))
                    .toList();
            return ResponseEntity.ok(yearSlips);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get specific month salary slip
     */
    @GetMapping("/my-slip/{month}/{year}")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getMySalarySlipForMonth(@PathVariable Integer month, @PathVariable Integer year) {
        try {
            Long employeeId = getCurrentUserId();
            SalarySlipResponse slip = salarySlipService.getEmployeeSalarySlipForMonth(employeeId, month, year);
            return ResponseEntity.ok(slip);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Download salary slip as PDF
     */
    @GetMapping("/my-slip/{id}/download")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> downloadSalarySlip(@PathVariable Long id) {
        try {
            Long employeeId = getCurrentUserId();
            SalarySlipResponse slipResponse = salarySlipService.getSalarySlipById(id);

            // Verify the slip belongs to the current employee
            if (!slipResponse.getEmployeeId().equals(employeeId)) {
                return ResponseEntity.status(403)
                        .body(new MessageResponse("Access denied: This salary slip does not belong to you"));
            }

            // Update status to DOWNLOADED
            salarySlipService.updateSalarySlipStatus(id, SalarySlipStatus.DOWNLOADED);

            // Get the PDF bytes
            byte[] pdfBytes;
            if (slipResponse.getPdfUrl() != null && !slipResponse.getPdfUrl().isEmpty()) {
                pdfBytes = pdfGenerationService.getPdfBytesByPath(slipResponse.getPdfUrl());
            } else {
                // Generate PDF on-the-fly if not exists
                SalarySlip slip = salarySlipService.getSalarySlipEntityById(id);
                String pdfPath = pdfGenerationService.generateSalarySlipPdf(slip);
                pdfBytes = pdfGenerationService.getPdfBytesByPath(pdfPath);
            }

            String filename = String.format("salary-slip-%d-%d-%d.pdf",
                    slipResponse.getEmployeeId(), slipResponse.getYear(), slipResponse.getMonth());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    // ==================== ADMIN ENDPOINTS ====================

    /**
     * Generate new salary slip
     */
    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> generateSalarySlip(@RequestBody SalarySlipRequest request) {
        try {
            Long generatedById = getCurrentUserId();
            SalarySlipResponse slip = salarySlipService.generateSalarySlip(request, generatedById);
            return ResponseEntity.ok(slip);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Preview salary calculations before generating
     */
    @PostMapping("/calculate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> calculateSalaryComponents(@RequestBody SalarySlipRequest request) {
        try {
            Map<String, Double> calculations = salarySlipService.calculateSalaryComponents(request);
            return ResponseEntity.ok(calculations);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get company salary slips
     */
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getCompanySalarySlips(
            @PathVariable Long companyId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        try {
            List<SalarySlipResponse> slips = salarySlipService.getCompanySalarySlips(companyId, month, year);
            return ResponseEntity.ok(slips);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get all salary slips for a specific month
     */
    @GetMapping("/month/{month}/{year}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getAllSalarySlipsForMonth(@PathVariable Integer month, @PathVariable Integer year) {
        try {
            List<SalarySlipResponse> slips = salarySlipService.getAllSalarySlipsForMonth(month, year);
            return ResponseEntity.ok(slips);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Update salary slip status
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> updateSalarySlipStatus(
            @PathVariable Long id,
            @RequestParam SalarySlipStatus status) {
        try {
            SalarySlipResponse slip = salarySlipService.updateSalarySlipStatus(id, status);
            return ResponseEntity.ok(slip);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get specific salary slip by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getSalarySlipById(@PathVariable Long id) {
        try {
            SalarySlipResponse slip = salarySlipService.getSalarySlipById(id);
            return ResponseEntity.ok(slip);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Generate bulk salary slips with auto-calculated salary components
     * Salary components are calculated from each employee's profile salary
     */
    @PostMapping("/generate-bulk")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> generateBulkSalarySlips(@RequestBody Map<String, Object> request) {
        try {
            Long generatedById = getCurrentUserId();
            @SuppressWarnings("unchecked")
            List<Integer> employeeIds = (List<Integer>) request.get("employeeIds");
            Integer month = (Integer) request.get("month");
            Integer year = (Integer) request.get("year");

            int successCount = 0;
            int failCount = 0;

            for (Integer employeeId : employeeIds) {
                try {
                    // Get employee's profile salary and calculate components
                    SalarySlipRequest slipRequest = salarySlipService.createAutoCalculatedSlipRequest(
                            employeeId.longValue(), month, year);

                    salarySlipService.generateSalarySlip(slipRequest, generatedById);
                    successCount++;
                } catch (Exception e) {
                    failCount++;
                    System.err.println("Failed to generate slip for employee " + employeeId + ": " + e.getMessage());
                }
            }

            return ResponseEntity.ok(Map.of(
                    "message",
                    String.format("Generated %d salary slips successfully, %d failed", successCount, failCount),
                    "successCount", successCount,
                    "failCount", failCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Check if salary slip exists
     */
    @GetMapping("/exists")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> checkIfSalarySlipExists(
            @RequestParam Long employeeId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        try {
            boolean exists = salarySlipService.checkIfSalarySlipExists(employeeId, month, year);
            return ResponseEntity.ok(Map.of("exists", exists));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get employee salary slips (admin view)
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getEmployeeSalarySlips(@PathVariable Long employeeId) {
        try {
            List<SalarySlipResponse> slips = salarySlipService.getEmployeeSalarySlips(employeeId);
            return ResponseEntity.ok(slips);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Get all salary slips (admin view)
     */
    @GetMapping("/slips")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getAllSalarySlips() {
        try {
            List<SalarySlipResponse> slips = salarySlipService.getAllSalarySlips();
            return ResponseEntity.ok(slips);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Download salary slip PDF (admin view - by slip ID)
     */
    @GetMapping("/slips/{id}/download")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> downloadSalarySlipAdmin(@PathVariable Long id) {
        try {
            SalarySlipResponse slipResponse = salarySlipService.getSalarySlipById(id);

            // Update status to DOWNLOADED
            salarySlipService.updateSalarySlipStatus(id, SalarySlipStatus.DOWNLOADED);

            // Get the PDF bytes
            byte[] pdfBytes;
            if (slipResponse.getPdfUrl() != null && !slipResponse.getPdfUrl().isEmpty()) {
                pdfBytes = pdfGenerationService.getPdfBytesByPath(slipResponse.getPdfUrl());
            } else {
                SalarySlip slip = salarySlipService.getSalarySlipEntityById(id);
                String pdfPath = pdfGenerationService.generateSalarySlipPdf(slip);
                pdfBytes = pdfGenerationService.getPdfBytesByPath(pdfPath);
            }

            String filename = String.format("salary-slip-%d-%d-%d.pdf",
                    slipResponse.getEmployeeId(), slipResponse.getYear(), slipResponse.getMonth());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Send salary slip via email (admin)
     */
    @PostMapping("/slips/{id}/send-email")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> sendSalarySlipEmail(@PathVariable Long id) {
        try {
            salarySlipService.sendSalarySlipEmail(id);
            return ResponseEntity.ok(new MessageResponse("Salary slip sent via email"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    /**
     * Delete salary slip (admin)
     */
    @DeleteMapping("/slips/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> deleteSalarySlip(@PathVariable Long id) {
        try {
            salarySlipService.deleteSalarySlip(id);
            return ResponseEntity.ok(new MessageResponse("Salary slip deleted successfully"));
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
