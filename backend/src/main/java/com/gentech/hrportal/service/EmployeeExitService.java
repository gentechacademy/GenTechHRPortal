package com.gentech.hrportal.service;

import com.gentech.hrportal.dto.EmployeeExitRequest;
import com.gentech.hrportal.entity.EmployeeExit;
import com.gentech.hrportal.entity.EmployeeExit.ApprovalStatus;
import com.gentech.hrportal.entity.EmployeeExit.ExitStatus;
import com.gentech.hrportal.entity.Notification;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.EmployeeExitRepository;
import com.gentech.hrportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeExitService {
    
    @Autowired
    private EmployeeExitRepository employeeExitRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Transactional
    public EmployeeExit createResignationRequest(Long employeeId, EmployeeExitRequest request) {
        // Validate employee
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        if (employee.getCompany() == null) {
            throw new RuntimeException("Employee does not belong to any company");
        }
        
        // Validate proposed last working date
        if (request.getProposedLastWorkingDate() == null) {
            throw new RuntimeException("Proposed last working date is required");
        }
        
        LocalDate exitDate = LocalDate.now();
        
        if (request.getProposedLastWorkingDate().isBefore(exitDate)) {
            throw new RuntimeException("Proposed last working date cannot be in the past");
        }
        
        // Check for existing active exit record
        boolean hasActiveExit = employeeExitRepository.existsByEmployeeIdAndExitStatus(employeeId, ExitStatus.RESIGNED);
        if (hasActiveExit) {
            throw new RuntimeException("Employee already has an active resignation request");
        }
        
        // Calculate notice period days
        int noticePeriodDays = request.getNoticePeriodDays() != null ? 
            request.getNoticePeriodDays() : 
            (int) java.time.temporal.ChronoUnit.DAYS.between(exitDate, request.getProposedLastWorkingDate());
        
        EmployeeExit employeeExit = EmployeeExit.builder()
            .employee(employee)
            .company(employee.getCompany())
            .exitDate(exitDate)
            .lastWorkingDate(request.getProposedLastWorkingDate())
            .noticePeriodDays(noticePeriodDays)
            .reason(request.getReason())
            .exitStatus(ExitStatus.RESIGNED)
            .managerApprovalStatus(ApprovalStatus.PENDING)
            .adminApprovalStatus(ApprovalStatus.PENDING)
            .build();
        
        EmployeeExit savedExit = employeeExitRepository.save(employeeExit);
        
        // Send notification to manager
        User manager = findManagerForEmployee(employee);
        if (manager != null) {
            emailService.sendResignationNotification(savedExit, manager);
        }
        
        return savedExit;
    }
    
    public EmployeeExit getEmployeeExitDetails(Long employeeId) {
        return employeeExitRepository.findFirstByEmployeeIdOrderByCreatedAtDesc(employeeId)
            .orElseThrow(() -> new RuntimeException("No exit details found for employee"));
    }
    
    public List<EmployeeExit> getAllExitEmployees() {
        return employeeExitRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public List<EmployeeExit> getActiveEmployees() {
        return employeeExitRepository.findByExitStatus(ExitStatus.ACTIVE);
    }
    
    public List<EmployeeExit> getExitedEmployees() {
        return employeeExitRepository.findByExitStatus(ExitStatus.RESIGNED);
    }
    
    public List<EmployeeExit> getTerminatedEmployees() {
        return employeeExitRepository.findByExitStatus(ExitStatus.TERMINATED);
    }
    
    @Transactional
    public EmployeeExit approveByManager(Long exitId, Long managerId, String comments) {
        EmployeeExit exit = employeeExitRepository.findById(exitId)
            .orElseThrow(() -> new RuntimeException("Exit record not found"));
        
        if (exit.getManagerApprovalStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Exit request is not pending manager approval");
        }
        
        // Prevent self-approval
        if (exit.getEmployee().getId().equals(managerId)) {
            throw new RuntimeException("You cannot approve your own exit request");
        }
        
        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("Manager not found"));
        
        exit.setManagerApprovalStatus(ApprovalStatus.APPROVED);
        exit.setManagerApprovedBy(manager);
        exit.setManagerApprovalDate(LocalDateTime.now());
        exit.setManagerComments(comments);
        
        EmployeeExit savedExit = employeeExitRepository.save(exit);
        
        // Notify admin about manager approval
        User admin = findAdminForCompany(exit.getCompany().getId());
        if (admin != null) {
            emailService.sendResignationManagerApprovalNotification(savedExit, admin);
        }
        
        // Notify employee
        emailService.sendResignationStatusUpdateNotification(savedExit);
        
        return savedExit;
    }
    
    @Transactional
    public EmployeeExit approveByAdmin(Long exitId, Long adminId, String comments) {
        EmployeeExit exit = employeeExitRepository.findById(exitId)
            .orElseThrow(() -> new RuntimeException("Exit record not found"));
        
        if (exit.getManagerApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new RuntimeException("Exit request must be approved by manager first");
        }
        
        if (exit.getAdminApprovalStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Exit request is not pending admin approval");
        }
        
        // Prevent self-approval
        if (exit.getEmployee().getId().equals(adminId)) {
            throw new RuntimeException("You cannot approve your own exit request");
        }
        
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        exit.setAdminApprovalStatus(ApprovalStatus.APPROVED);
        exit.setAdminApprovedBy(admin);
        exit.setAdminApprovalDate(LocalDateTime.now());
        exit.setAdminComments(comments);
        
        EmployeeExit savedExit = employeeExitRepository.save(exit);
        
        // Notify employee via email
        emailService.sendResignationStatusUpdateNotification(savedExit);
        
        // Create in-app notification for employee
        notificationService.createNotification(
                savedExit.getEmployee(),
                "Exit Request Approved",
                "Your exit request has been approved by the admin. Your last working day is " + 
                        savedExit.getLastWorkingDate() + ". Please complete your exit formalities.",
                Notification.NotificationType.RESIGNATION_APPROVED,
                savedExit.getId(),
                "EMPLOYEE_EXIT"
        );
        
        return savedExit;
    }
    
    @Transactional
    public EmployeeExit rejectByManager(Long exitId, Long managerId, String comments) {
        EmployeeExit exit = employeeExitRepository.findById(exitId)
            .orElseThrow(() -> new RuntimeException("Exit record not found"));
        
        if (exit.getManagerApprovalStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Exit request is not pending manager approval");
        }
        
        // Prevent self-rejection
        if (exit.getEmployee().getId().equals(managerId)) {
            throw new RuntimeException("You cannot reject your own exit request");
        }
        
        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("Manager not found"));
        
        exit.setManagerApprovalStatus(ApprovalStatus.REJECTED);
        exit.setManagerApprovedBy(manager);
        exit.setManagerApprovalDate(LocalDateTime.now());
        exit.setManagerComments(comments);
        
        EmployeeExit savedExit = employeeExitRepository.save(exit);
        
        // Notify employee about rejection
        emailService.sendResignationStatusUpdateNotification(savedExit);
        
        return savedExit;
    }
    
    @Transactional
    public EmployeeExit rejectByAdmin(Long exitId, Long adminId, String comments) {
        EmployeeExit exit = employeeExitRepository.findById(exitId)
            .orElseThrow(() -> new RuntimeException("Exit record not found"));
        
        if (exit.getManagerApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new RuntimeException("Exit request must be approved by manager first");
        }
        
        if (exit.getAdminApprovalStatus() != ApprovalStatus.PENDING) {
            throw new RuntimeException("Exit request is not pending admin approval");
        }
        
        // Prevent self-rejection
        if (exit.getEmployee().getId().equals(adminId)) {
            throw new RuntimeException("You cannot reject your own exit request");
        }
        
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        exit.setAdminApprovalStatus(ApprovalStatus.REJECTED);
        exit.setAdminApprovedBy(admin);
        exit.setAdminApprovalDate(LocalDateTime.now());
        exit.setAdminComments(comments);
        
        EmployeeExit savedExit = employeeExitRepository.save(exit);
        
        // Notify employee via email
        emailService.sendResignationStatusUpdateNotification(savedExit);
        
        // Create in-app notification for employee
        notificationService.createNotification(
                savedExit.getEmployee(),
                "Exit Request Rejected",
                "Your exit request has been rejected by the admin. Please contact HR for more information.",
                Notification.NotificationType.RESIGNATION_REJECTED,
                savedExit.getId(),
                "EMPLOYEE_EXIT"
        );
        
        return savedExit;
    }
    
    public List<EmployeeExit> getPendingForManager(Long managerId) {
        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("Manager not found"));
        
        if (manager.getCompany() == null) {
            return List.of();
        }
        
        return employeeExitRepository.findByManagerApprovalStatusAndEmployee_Company_Id(
            ApprovalStatus.PENDING, 
            manager.getCompany().getId()
        ).stream()
            .filter(e -> !e.getEmployee().getId().equals(managerId))
            .collect(Collectors.toList());
    }
    
    public List<EmployeeExit> getPendingForAdmin(Long companyId) {
        return employeeExitRepository.findByManagerApprovalStatusAndEmployee_Company_Id(
            ApprovalStatus.APPROVED, 
            companyId
        ).stream()
            .filter(e -> e.getAdminApprovalStatus() == ApprovalStatus.PENDING)
            .collect(Collectors.toList());
    }
    
    public EmployeeExit getExitById(Long id) {
        return employeeExitRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Exit record not found"));
    }
    
    public List<EmployeeExit> getCompanyExitEmployees(Long companyId) {
        return employeeExitRepository.findByEmployee_Company_Id(companyId);
    }
    
    /**
     * Find manager for an employee
     */
    private User findManagerForEmployee(User employee) {
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
