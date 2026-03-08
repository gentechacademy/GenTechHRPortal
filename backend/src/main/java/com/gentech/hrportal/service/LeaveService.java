package com.gentech.hrportal.service;

import com.gentech.hrportal.entity.Attendance;
import com.gentech.hrportal.entity.Attendance.AttendanceStatus;
import com.gentech.hrportal.entity.Leave;
import com.gentech.hrportal.entity.Leave.LeaveStatus;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.AttendanceRepository;
import com.gentech.hrportal.repository.LeaveRepository;
import com.gentech.hrportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class LeaveService {
    
    @Autowired
    private LeaveRepository leaveRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    private static final int LONG_LEAVE_THRESHOLD = 3; // Days
    
    @Transactional
    public Leave applyForLeave(Leave leave) {
        // Validate employee
        if (leave.getEmployee() == null || leave.getEmployee().getId() == null) {
            throw new RuntimeException("Employee is required");
        }
        
        User employee = userRepository.findById(leave.getEmployee().getId())
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // Validate dates
        if (leave.getStartDate() == null || leave.getEndDate() == null) {
            throw new RuntimeException("Start date and end date are required");
        }
        
        if (leave.getEndDate().isBefore(leave.getStartDate())) {
            throw new RuntimeException("End date cannot be before start date");
        }
        
        // Check for overlapping leaves
        if (checkLeaveOverlap(employee.getId(), leave.getStartDate(), leave.getEndDate())) {
            throw new RuntimeException("You already have a leave application for the requested date range");
        }
        
        // Calculate number of days
        int numberOfDays = (int) ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
        leave.setNumberOfDays(numberOfDays);
        
        // Set employee and company
        leave.setEmployee(employee);
        leave.setCompany(employee.getCompany());
        leave.setStatus(LeaveStatus.PENDING);
        
        // Set applied by (can be different from employee in case of admin applying on behalf)
        if (leave.getAppliedBy() == null) {
            leave.setAppliedBy(employee);
        }
        
        Leave savedLeave = leaveRepository.save(leave);
        
        // Send email notification if leave is > 3 days
        if (numberOfDays > LONG_LEAVE_THRESHOLD) {
            // Find manager and admin for notification
            User manager = findManagerForEmployee(employee);
            User admin = findAdminForCompany(employee.getCompany().getId());
            emailService.sendLeaveApplicationNotification(savedLeave, manager, admin);
        }
        
        return savedLeave;
    }
    
    @Transactional
    public Leave approveLeave(Long leaveId, Long approverId) {
        Leave leave = leaveRepository.findById(leaveId)
            .orElseThrow(() -> new RuntimeException("Leave not found"));
        
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave is not in pending status");
        }
        
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        leave.setStatus(LeaveStatus.APPROVED);
        leave.setApprovedBy(approver);
        leave.setApprovalDate(LocalDateTime.now());
        
        Leave approvedLeave = leaveRepository.save(leave);
        
        // Create attendance records for each day of the leave
        createAttendanceForLeave(approvedLeave);
        
        // Send approval notification to employee
        emailService.sendLeaveApprovalNotification(approvedLeave);
        
        return approvedLeave;
    }
    
    /**
     * Create attendance records with ON_LEAVE status for each day of approved leave
     */
    private void createAttendanceForLeave(Leave leave) {
        User employee = leave.getEmployee();
        LocalDate currentDate = leave.getStartDate();
        LocalDate endDate = leave.getEndDate();
        
        while (!currentDate.isAfter(endDate)) {
            // Check if attendance already exists for this date
            Attendance existingAttendance = attendanceRepository
                .findByEmployeeIdAndAttendanceDate(employee.getId(), currentDate);
            
            if (existingAttendance == null) {
                // Create new attendance record with ON_LEAVE status
                Attendance attendance = new Attendance();
                attendance.setEmployee(employee);
                attendance.setCompany(employee.getCompany());
                attendance.setAttendanceDate(currentDate);
                attendance.setStatus(AttendanceStatus.ON_LEAVE);
                attendance.setNotes("Leave: " + leave.getLeaveType() + " - " + leave.getReason());
                attendance.setApprovalStatus(Attendance.ApprovalStatus.APPROVED);
                attendanceRepository.save(attendance);
            } else {
                // Update existing attendance to ON_LEAVE
                existingAttendance.setStatus(AttendanceStatus.ON_LEAVE);
                existingAttendance.setNotes("Leave: " + leave.getLeaveType() + " - " + leave.getReason());
                existingAttendance.setApprovalStatus(Attendance.ApprovalStatus.APPROVED);
                attendanceRepository.save(existingAttendance);
            }
            
            currentDate = currentDate.plusDays(1);
        }
    }
    
    @Transactional
    public Leave rejectLeave(Long leaveId, Long approverId, String reason) {
        Leave leave = leaveRepository.findById(leaveId)
            .orElseThrow(() -> new RuntimeException("Leave not found"));
        
        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave is not in pending status");
        }
        
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        leave.setStatus(LeaveStatus.REJECTED);
        leave.setApprovedBy(approver);
        leave.setApprovalDate(LocalDateTime.now());
        leave.setRejectionReason(reason);
        
        Leave rejectedLeave = leaveRepository.save(leave);
        
        // Send rejection notification to employee
        emailService.sendLeaveRejectionNotification(rejectedLeave);
        
        return rejectedLeave;
    }
    
    public List<Leave> getEmployeeLeaves(Long employeeId) {
        return leaveRepository.findByEmployeeId(employeeId);
    }
    
    public List<Leave> getCompanyLeaves(Long companyId) {
        return leaveRepository.findByCompanyId(companyId);
    }
    
    public List<Leave> getPendingLeavesByCompany(Long companyId) {
        return leaveRepository.findByCompanyIdAndStatus(companyId, LeaveStatus.PENDING);
    }
    
    public List<Leave> getAllPendingLeaves() {
        return leaveRepository.findByStatus(LeaveStatus.PENDING);
    }
    
    public boolean checkLeaveOverlap(Long employeeId, LocalDate startDate, LocalDate endDate) {
        return leaveRepository.existsByEmployeeIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            employeeId, endDate, startDate);
    }
    
    public Leave getLeaveById(Long id) {
        return leaveRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Leave not found"));
    }
    
    /**
     * Find manager for an employee - simplified version
     * In a real system, this would look at department hierarchy
     */
    private User findManagerForEmployee(User employee) {
        // Find a user with MANAGER role in the same company
        // This is a simplified implementation
        return userRepository.findAll().stream()
            .filter(u -> u.getCompany() != null 
                && u.getCompany().getId().equals(employee.getCompany().getId())
                && (u.getRole() == User.Role.MANAGER || u.getRole() == User.Role.HR_MANAGER))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Find admin for a company
     */
    private User findAdminForCompany(Long companyId) {
        return userRepository.findAll().stream()
            .filter(u -> u.getCompany() != null 
                && u.getCompany().getId().equals(companyId)
                && u.getRole() == User.Role.ADMIN)
            .findFirst()
            .orElse(null);
    }
}
