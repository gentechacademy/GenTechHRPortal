package com.gentech.hrportal.controller;

import com.gentech.hrportal.dto.AttendanceApprovalRequest;
import com.gentech.hrportal.dto.AttendanceRequest;
import com.gentech.hrportal.dto.AttendanceResponse;
import com.gentech.hrportal.dto.MessageResponse;
import com.gentech.hrportal.dto.WeeklyAttendanceRequest;
import com.gentech.hrportal.entity.Attendance;
import com.gentech.hrportal.entity.Attendance.AttendanceStatus;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.security.UserDetailsImpl;
import com.gentech.hrportal.service.AttendanceService;
import com.gentech.hrportal.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    
    @Autowired
    private AttendanceService attendanceService;
    
    @Autowired
    private EmployeeService employeeService;
    
    @PostMapping("/checkin")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> checkIn(@RequestBody AttendanceRequest request) {
        try {
            // Check if today is a weekend
            LocalDate today = LocalDate.now();
            if (today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY) {
                return ResponseEntity.badRequest().body(new MessageResponse("Attendance cannot be marked on Saturday or Sunday."));
            }
            
            Long employeeId = getCurrentUserId();
            Attendance attendance = attendanceService.checkIn(employeeId, request.getNotes());
            return ResponseEntity.ok(new AttendanceResponse(attendance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/checkout")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> checkOut(@RequestBody AttendanceRequest request) {
        try {
            // Check if today is a weekend
            LocalDate today = LocalDate.now();
            if (today.getDayOfWeek() == DayOfWeek.SATURDAY || today.getDayOfWeek() == DayOfWeek.SUNDAY) {
                return ResponseEntity.badRequest().body(new MessageResponse("Attendance cannot be marked on Saturday or Sunday."));
            }
            
            Long employeeId = getCurrentUserId();
            Attendance attendance = attendanceService.checkOut(employeeId, request.getNotes());
            return ResponseEntity.ok(new AttendanceResponse(attendance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/submit")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> submitAttendance(@RequestBody AttendanceRequest request) {
        try {
            // Check if the attendance date is a weekend
            LocalDate attendanceDate = request.getAttendanceDate();
            if (attendanceDate != null && 
                (attendanceDate.getDayOfWeek() == DayOfWeek.SATURDAY || attendanceDate.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Attendance cannot be submitted for Saturday or Sunday."));
            }
            
            Long employeeId = getCurrentUserId();
            request.setEmployeeId(employeeId);
            
            User employee = new User();
            employee.setId(employeeId);
            
            Attendance attendance = new Attendance();
            attendance.setEmployee(employee);
            attendance.setAttendanceDate(request.getAttendanceDate());
            attendance.setCheckInTime(request.getCheckInTime());
            attendance.setCheckOutTime(request.getCheckOutTime());
            attendance.setStatus(request.getStatus());
            attendance.setNotes(request.getNotes());
            
            Attendance savedAttendance = attendanceService.submitAttendance(attendance);
            return ResponseEntity.ok(new AttendanceResponse(savedAttendance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // ==================== NEW WEEKLY ATTENDANCE ENDPOINTS ====================
    
    /**
     * Submit weekly attendance (array of 7 days)
     * Request body: { weekStartDate: "2024-01-01", attendances: [...] }
     */
    @PostMapping("/weekly")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> submitWeeklyAttendance(@RequestBody WeeklyAttendanceRequest request) {
        try {
            Long employeeId = getCurrentUserId();
            
            if (request.getAttendances() == null || request.getAttendances().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Attendances list cannot be empty"));
            }
            
            // Validate that no attendance is submitted for weekends (except HOLIDAY or WEEKLY_OFF status)
            for (AttendanceRequest attendanceRequest : request.getAttendances()) {
                LocalDate date = attendanceRequest.getAttendanceDate();
                AttendanceStatus status = attendanceRequest.getStatus();
                if (date != null && 
                    (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY)) {
                    // Only allow HOLIDAY or WEEKLY_OFF status for weekends
                    if (status != AttendanceStatus.HOLIDAY && status != AttendanceStatus.WEEKLY_OFF) {
                        return ResponseEntity.badRequest().body(new MessageResponse(
                            "Attendance cannot be marked for Saturday or Sunday. Only HOLIDAY or WEEKLY_OFF status is allowed for weekends."));
                    }
                }
            }
            
            List<Attendance> weeklyAttendance = request.getAttendances().stream()
                .map(attendanceRequest -> {
                    User employee = new User();
                    employee.setId(employeeId);
                    
                    Attendance attendance = new Attendance();
                    attendance.setEmployee(employee);
                    attendance.setAttendanceDate(attendanceRequest.getAttendanceDate());
                    attendance.setCheckInTime(attendanceRequest.getCheckInTime());
                    attendance.setCheckOutTime(attendanceRequest.getCheckOutTime());
                    attendance.setStatus(attendanceRequest.getStatus());
                    attendance.setNotes(attendanceRequest.getNotes());
                    return attendance;
                })
                .collect(Collectors.toList());
            
            List<Attendance> savedAttendances = attendanceService.submitWeeklyAttendance(employeeId, weeklyAttendance);
            List<AttendanceResponse> responses = savedAttendances.stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Update a specific attendance record
     * Check edit permission (before canEditUntil)
     */
    @PutMapping("/{attendanceId}")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateAttendance(@PathVariable Long attendanceId, @RequestBody AttendanceRequest request) {
        try {
            Long employeeId = getCurrentUserId();
            
            // Get the existing attendance to check the date
            Attendance existingAttendance = attendanceService.getAttendanceById(attendanceId);
            if (existingAttendance != null && existingAttendance.getAttendanceDate() != null) {
                LocalDate attendanceDate = existingAttendance.getAttendanceDate();
                // Block updates if the attendance date is a weekend
                if (attendanceDate.getDayOfWeek() == DayOfWeek.SATURDAY || attendanceDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    return ResponseEntity.badRequest().body(new MessageResponse(
                        "Attendance cannot be updated for Saturday or Sunday."));
                }
            }
            
            Attendance updatedAttendance = new Attendance();
            updatedAttendance.setCheckInTime(request.getCheckInTime());
            updatedAttendance.setCheckOutTime(request.getCheckOutTime());
            updatedAttendance.setStatus(request.getStatus());
            updatedAttendance.setNotes(request.getNotes());
            
            Attendance attendance = attendanceService.updateAttendance(attendanceId, employeeId, updatedAttendance);
            return ResponseEntity.ok(new AttendanceResponse(attendance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Get current week's attendance (Monday to today)
     */
    @GetMapping("/weekly/current")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentWeekAttendance() {
        try {
            Long employeeId = getCurrentUserId();
            List<Attendance> attendances = attendanceService.getCurrentWeekAttendance(employeeId);
            List<AttendanceResponse> responses = attendances.stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Get attendance for a specific week
     * Query param: weekStartDate=2024-01-01 (Monday)
     */
    @GetMapping("/weekly")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getWeeklyAttendance(@RequestParam String weekStartDate) {
        try {
            Long employeeId = getCurrentUserId();
            LocalDate startDate = LocalDate.parse(weekStartDate);
            List<Attendance> attendances = attendanceService.getWeeklyAttendance(employeeId, startDate);
            List<AttendanceResponse> responses = attendances.stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    /**
     * Check if attendance can be edited
     */
    @GetMapping("/{attendanceId}/can-edit")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> canEditAttendance(@PathVariable Long attendanceId) {
        try {
            boolean canEdit = attendanceService.canEditAttendance(attendanceId);
            return ResponseEntity.ok(new CanEditResponse(canEdit));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // ==================== EXISTING ENDPOINTS ====================
    
    @GetMapping("/my-attendance")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyAttendance() {
        try {
            Long employeeId = getCurrentUserId();
            List<Attendance> attendances = attendanceService.getEmployeeAttendance(employeeId);
            List<AttendanceResponse> responses = attendances.stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/my-attendance/range")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getMyAttendanceByRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            Long employeeId = getCurrentUserId();
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<Attendance> attendances = attendanceService.getEmployeeAttendanceByDateRange(employeeId, start, end);
            List<AttendanceResponse> responses = attendances.stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/today")
    @PreAuthorize("hasRole('DEVELOPER') or hasRole('SOFTWARE_ENGINEER') or hasRole('HR') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> getTodayAttendance() {
        try {
            Long employeeId = getCurrentUserId();
            Attendance attendance = attendanceService.getTodayAttendance(employeeId);
            if (attendance == null) {
                return ResponseEntity.ok(null);
            }
            return ResponseEntity.ok(new AttendanceResponse(attendance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    // Admin/Manager endpoints for approval
    
    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getCompanyAttendance(@PathVariable Long companyId) {
        try {
            List<Attendance> attendances = attendanceService.getCompanyAttendance(companyId);
            List<AttendanceResponse> responses = attendances.stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/company/{companyId}/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getPendingAttendanceByCompany(@PathVariable Long companyId) {
        try {
            List<Attendance> attendances = attendanceService.getPendingAttendanceByCompany(companyId);
            List<AttendanceResponse> responses = attendances.stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/pending")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER')")
    public ResponseEntity<?> getAllPendingAttendance() {
        try {
            List<Attendance> attendances = attendanceService.getAllPendingAttendance();
            List<AttendanceResponse> responses = attendances.stream()
                .map(AttendanceResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> approveAttendance(@RequestBody AttendanceApprovalRequest request) {
        try {
            Long approverId = getCurrentUserId();
            Attendance attendance;
            if (request.isApproved()) {
                attendance = attendanceService.approveAttendance(request.getAttendanceId(), approverId, request.getNotes());
            } else {
                attendance = attendanceService.rejectAttendance(request.getAttendanceId(), approverId, request.getRejectionReason());
            }
            return ResponseEntity.ok(new AttendanceResponse(attendance));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{attendanceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteAttendance(@PathVariable Long attendanceId) {
        try {
            attendanceService.deleteAttendance(attendanceId);
            return ResponseEntity.ok(new MessageResponse("Attendance record deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('HR_MANAGER') or hasRole('MANAGER') or hasRole('GENERAL_MANAGER')")
    public ResponseEntity<?> getEmployeeAttendance(@PathVariable Long employeeId) {
        try {
            List<Attendance> attendances = attendanceService.getEmployeeAttendance(employeeId);
            List<AttendanceResponse> responses = attendances.stream()
                .map(AttendanceResponse::new)
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
    
    // Inner class for can-edit response
    public static class CanEditResponse {
        private boolean canEdit;
        
        public CanEditResponse(boolean canEdit) {
            this.canEdit = canEdit;
        }
        
        public boolean isCanEdit() {
            return canEdit;
        }
        
        public void setCanEdit(boolean canEdit) {
            this.canEdit = canEdit;
        }
    }
}
