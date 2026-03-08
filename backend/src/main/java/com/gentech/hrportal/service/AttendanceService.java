package com.gentech.hrportal.service;

import com.gentech.hrportal.entity.Attendance;
import com.gentech.hrportal.entity.Attendance.ApprovalStatus;
import com.gentech.hrportal.entity.Attendance.AttendanceStatus;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.AttendanceRepository;
import com.gentech.hrportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
public class AttendanceService {
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public Attendance checkIn(Long employeeId, String notes) {
        LocalDate today = LocalDate.now();
        
        // Check if already checked in today
        Attendance existingAttendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, today);
        if (existingAttendance != null && existingAttendance.getCheckInTime() != null) {
            throw new RuntimeException("Already checked in for today");
        }
        
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        Attendance attendance;
        if (existingAttendance != null) {
            attendance = existingAttendance;
        } else {
            attendance = new Attendance();
            attendance.setEmployee(employee);
            attendance.setCompany(employee.getCompany());
            attendance.setAttendanceDate(today);
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendance.setApprovalStatus(ApprovalStatus.PENDING);
        }
        
        attendance.setCheckInTime(LocalTime.now());
        if (notes != null && !notes.isEmpty()) {
            attendance.setNotes(notes);
        }
        
        return attendanceRepository.save(attendance);
    }
    
    @Transactional
    public Attendance checkOut(Long employeeId, String notes) {
        LocalDate today = LocalDate.now();
        
        Attendance attendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, today);
        if (attendance == null || attendance.getCheckInTime() == null) {
            throw new RuntimeException("No check-in found for today");
        }
        
        if (attendance.getCheckOutTime() != null) {
            throw new RuntimeException("Already checked out for today");
        }
        
        attendance.setCheckOutTime(LocalTime.now());
        
        // Calculate working hours
        LocalTime checkIn = attendance.getCheckInTime();
        LocalTime checkOut = attendance.getCheckOutTime();
        long minutes = ChronoUnit.MINUTES.between(checkIn, checkOut);
        double hours = minutes / 60.0;
        attendance.setWorkingHours(Math.round(hours * 100.0) / 100.0);
        
        if (notes != null && !notes.isEmpty()) {
            String existingNotes = attendance.getNotes();
            attendance.setNotes(existingNotes != null ? existingNotes + " | Checkout: " + notes : "Checkout: " + notes);
        }
        
        return attendanceRepository.save(attendance);
    }
    
    @Transactional
    public Attendance submitAttendance(Attendance attendance) {
        if (attendance.getEmployee() == null || attendance.getEmployee().getId() == null) {
            throw new RuntimeException("Employee is required");
        }
        
        User employee = userRepository.findById(attendance.getEmployee().getId())
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // Check if attendance already exists for this date
        Attendance existingAttendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(
            employee.getId(), attendance.getAttendanceDate());
        
        if (existingAttendance != null) {
            throw new RuntimeException("Attendance already exists for this date");
        }
        
        attendance.setEmployee(employee);
        attendance.setCompany(employee.getCompany());
        attendance.setApprovalStatus(ApprovalStatus.PENDING);
        
        // Calculate working hours if both times are provided
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            long minutes = ChronoUnit.MINUTES.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
            double hours = minutes / 60.0;
            attendance.setWorkingHours(Math.round(hours * 100.0) / 100.0);
        }
        
        return attendanceRepository.save(attendance);
    }
    
    @Transactional
    public Attendance approveAttendance(Long attendanceId, Long approverId, String notes) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));
        
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        attendance.setApprovalStatus(ApprovalStatus.APPROVED);
        attendance.setApprovedBy(approver);
        attendance.setApprovalDate(LocalDateTime.now());
        
        if (notes != null && !notes.isEmpty()) {
            attendance.setNotes(attendance.getNotes() + " | Approval: " + notes);
        }
        
        return attendanceRepository.save(attendance);
    }
    
    @Transactional
    public Attendance rejectAttendance(Long attendanceId, Long approverId, String reason) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));
        
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        attendance.setApprovalStatus(ApprovalStatus.REJECTED);
        attendance.setApprovedBy(approver);
        attendance.setApprovalDate(LocalDateTime.now());
        attendance.setRejectionReason(reason);
        
        return attendanceRepository.save(attendance);
    }
    
    // ==================== NEW WEEKLY ATTENDANCE METHODS ====================
    
    /**
     * Submit weekly attendance for an employee (7 days at once)
     * @param employeeId The employee ID
     * @param weeklyAttendance List of 7 attendance records (Monday to Sunday)
     * @return List of saved attendance records
     */
    @Transactional
    public List<Attendance> submitWeeklyAttendance(Long employeeId, List<Attendance> weeklyAttendance) {
        // Allow partial submissions (1-7 days) - for resubmitting corrected records
        if (weeklyAttendance == null || weeklyAttendance.isEmpty()) {
            throw new RuntimeException("Attendances list cannot be empty");
        }
        if (weeklyAttendance.size() > 7) {
            throw new RuntimeException("Weekly attendance cannot contain more than 7 days");
        }
        
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // Get the week start date (Monday) from the first attendance
        LocalDate firstDate = weeklyAttendance.get(0).getAttendanceDate();
        LocalDate weekStartDate = firstDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEndDate = weekStartDate.plusDays(6);
        
        // Calculate canEditUntil (next Wednesday at 11:59 PM)
        LocalDateTime canEditUntil = weekEndDate.plusDays(3).atTime(23, 59, 59);
        
        // Validate all dates are within the same week
        for (Attendance attendance : weeklyAttendance) {
            LocalDate date = attendance.getAttendanceDate();
            if (date.isBefore(weekStartDate) || date.isAfter(weekEndDate)) {
                throw new RuntimeException("All attendance dates must be within the same week (Monday to Sunday)");
            }
        }
        
        // Check for existing attendance records to prevent duplicates
        for (int i = 0; i < weeklyAttendance.size(); i++) {
            Attendance attendance = weeklyAttendance.get(i);
            LocalDate date = attendance.getAttendanceDate();
            
            // Check if attendance already exists for this date
            Attendance existingAttendance = attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, date);
            
            if (existingAttendance != null) {
                // If already approved, throw error - cannot modify approved records
                if (existingAttendance.getApprovalStatus() == ApprovalStatus.APPROVED) {
                    throw new RuntimeException("Attendance for " + date + " has already been approved and cannot be modified.");
                } else if (existingAttendance.getApprovalStatus() == ApprovalStatus.REJECTED) {
                    // Allow resubmission if rejected - update the existing record
                    attendance.setId(existingAttendance.getId());
                } else {
                    // PENDING status - update the existing record instead of creating duplicate
                    attendance.setId(existingAttendance.getId());
                }
            }
        }
        
        // Save all attendance records
        for (Attendance attendance : weeklyAttendance) {
            attendance.setEmployee(employee);
            attendance.setCompany(employee.getCompany());
            attendance.setWeekStartDate(weekStartDate);
            attendance.setWeekEndDate(weekEndDate);
            attendance.setIsWeeklyEntry(true);
            attendance.setCanEditUntil(canEditUntil);
            attendance.setApprovalStatus(ApprovalStatus.PENDING);
            // Clear rejection reason on resubmission
            attendance.setRejectionReason(null);
            
            // Calculate working hours if both times are provided
            if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
                long minutes = ChronoUnit.MINUTES.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
                double hours = minutes / 60.0;
                attendance.setWorkingHours(Math.round(hours * 100.0) / 100.0);
            }
            
            attendanceRepository.save(attendance);
        }
        
        return weeklyAttendance;
    }
    
    /**
     * Update an attendance record (only if before canEditUntil)
     * @param attendanceId The attendance ID to update
     * @param employeeId The employee ID (for verification)
     * @param updatedAttendance The updated attendance data
     * @return The updated attendance
     */
    @Transactional
    public Attendance updateAttendance(Long attendanceId, Long employeeId, Attendance updatedAttendance) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));
        
        // Verify the attendance belongs to the employee
        if (!attendance.getEmployee().getId().equals(employeeId)) {
            throw new RuntimeException("You can only update your own attendance");
        }
        
        // Check if current time is before canEditUntil
        if (!canEditAttendance(attendanceId)) {
            throw new RuntimeException("Attendance can no longer be edited. Edit deadline has passed.");
        }
        
        // Update fields
        if (updatedAttendance.getCheckInTime() != null) {
            attendance.setCheckInTime(updatedAttendance.getCheckInTime());
        }
        if (updatedAttendance.getCheckOutTime() != null) {
            attendance.setCheckOutTime(updatedAttendance.getCheckOutTime());
        }
        if (updatedAttendance.getStatus() != null) {
            attendance.setStatus(updatedAttendance.getStatus());
        }
        if (updatedAttendance.getNotes() != null) {
            attendance.setNotes(updatedAttendance.getNotes());
        }
        
        // Recalculate working hours if both times are provided
        if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() != null) {
            long minutes = ChronoUnit.MINUTES.between(attendance.getCheckInTime(), attendance.getCheckOutTime());
            double hours = minutes / 60.0;
            attendance.setWorkingHours(Math.round(hours * 100.0) / 100.0);
        }
        
        // Reset approval status to PENDING if it was already approved
        if (attendance.getApprovalStatus() == ApprovalStatus.APPROVED) {
            attendance.setApprovalStatus(ApprovalStatus.PENDING);
            attendance.setApprovedBy(null);
            attendance.setApprovalDate(null);
        }
        
        return attendanceRepository.save(attendance);
    }
    
    /**
     * Check if attendance can still be edited
     * @param attendanceId The attendance ID
     * @return true if can edit, false otherwise
     */
    public boolean canEditAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));
        
        // If canEditUntil is not set, allow editing
        if (attendance.getCanEditUntil() == null) {
            return true;
        }
        
        // Check if current time is before canEditUntil
        return LocalDateTime.now().isBefore(attendance.getCanEditUntil());
    }
    
    /**
     * Get all attendance for a specific week
     * @param employeeId The employee ID
     * @param weekStartDate The week start date (Monday)
     * @return List of attendance records for the week
     */
    public List<Attendance> getWeeklyAttendance(Long employeeId, LocalDate weekStartDate) {
        // Ensure weekStartDate is a Monday
        LocalDate monday = weekStartDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = monday.plusDays(6);
        
        return attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(employeeId, monday, sunday);
    }
    
    /**
     * Get attendance for current week (Monday to today)
     * @param employeeId The employee ID
     * @return List of attendance records for current week
     */
    public List<Attendance> getCurrentWeekAttendance(Long employeeId) {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        
        return attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(employeeId, monday, today);
    }
    
    // ==================== EXISTING METHODS ====================
    
    public List<Attendance> getEmployeeAttendance(Long employeeId) {
        return attendanceRepository.findByEmployeeId(employeeId);
    }
    
    public List<Attendance> getEmployeeAttendanceByDateRange(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByEmployeeIdAndAttendanceDateBetween(employeeId, startDate, endDate);
    }
    
    public List<Attendance> getCompanyAttendance(Long companyId) {
        return attendanceRepository.findByCompanyId(companyId);
    }
    
    public List<Attendance> getPendingAttendanceByCompany(Long companyId) {
        return attendanceRepository.findByCompanyIdAndApprovalStatus(companyId, ApprovalStatus.PENDING);
    }
    
    public List<Attendance> getAllPendingAttendance() {
        return attendanceRepository.findByApprovalStatus(ApprovalStatus.PENDING);
    }
    
    public Attendance getAttendanceById(Long id) {
        return attendanceRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));
    }
    
    public Attendance getTodayAttendance(Long employeeId) {
        return attendanceRepository.findByEmployeeIdAndAttendanceDate(employeeId, LocalDate.now());
    }
    
    public void deleteAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
            .orElseThrow(() -> new RuntimeException("Attendance not found"));
        attendanceRepository.delete(attendance);
    }
}
