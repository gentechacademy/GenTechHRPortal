package com.gentech.hrportal.service;

import com.gentech.hrportal.dto.ResignationApprovalDto;
import com.gentech.hrportal.dto.ResignationRequestDto;
import com.gentech.hrportal.entity.Notification;
import com.gentech.hrportal.entity.ResignationRequest;
import com.gentech.hrportal.entity.ResignationRequest.ResignationStatus;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.ResignationRepository;
import com.gentech.hrportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
public class ResignationService {
    
    @Autowired
    private ResignationRepository resignationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private NotificationService notificationService;
    
    private static final int DEFAULT_NOTICE_PERIOD_DAYS = 30;
    
    @Transactional
    public ResignationRequest submitResignation(Long employeeId, ResignationRequestDto dto) {
        // Validate employee
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        if (employee.getCompany() == null) {
            throw new RuntimeException("Employee does not belong to any company");
        }
        
        // Validate proposed last working day
        if (dto.getProposedLastWorkingDay() == null) {
            throw new RuntimeException("Proposed last working day is required");
        }
        
        LocalDate requestDate = LocalDate.now();
        
        if (dto.getProposedLastWorkingDay().isBefore(requestDate)) {
            throw new RuntimeException("Proposed last working day cannot be in the past");
        }
        
        // Check for existing pending/active resignation requests
        List<ResignationStatus> activeStatuses = Arrays.asList(
            ResignationStatus.PENDING_MANAGER,
            ResignationStatus.MANAGER_APPROVED,
            ResignationStatus.PENDING_ADMIN
        );
        
        boolean hasActiveRequest = resignationRepository.existsByEmployeeIdAndStatusIn(employeeId, activeStatuses);
        if (hasActiveRequest) {
            throw new RuntimeException("You already have an active resignation request");
        }
        
        // Calculate notice period days
        int noticePeriodDays = (int) ChronoUnit.DAYS.between(requestDate, dto.getProposedLastWorkingDay());
        
        ResignationRequest resignation = ResignationRequest.builder()
            .employee(employee)
            .company(employee.getCompany())
            .requestDate(requestDate)
            .proposedLastWorkingDay(dto.getProposedLastWorkingDay())
            .noticePeriodDays(noticePeriodDays)
            .reason(dto.getReason())
            .status(ResignationStatus.PENDING_MANAGER)
            .build();
        
        ResignationRequest savedResignation = resignationRepository.save(resignation);
        
        // Send notification to manager
        User manager = findManagerForEmployee(employee);
        if (manager != null) {
            emailService.sendResignationNotification(savedResignation, manager);
        }
        
        return savedResignation;
    }
    
    public List<ResignationRequest> getMyResignationRequests(Long employeeId) {
        return resignationRepository.findByEmployeeId(employeeId);
    }
    
    public List<ResignationRequest> getPendingForManager(Long managerId) {
        // Get manager's company
        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("Manager not found"));
        
        if (manager.getCompany() == null) {
            return List.of();
        }
        
        // Return pending manager approvals for the same company
        // Exclude the manager's own requests
        return resignationRepository.findByStatusAndEmployee_Company_Id(
            ResignationStatus.PENDING_MANAGER, 
            manager.getCompany().getId()
        ).stream()
            .filter(r -> !r.getEmployee().getId().equals(managerId))
            .toList();
    }
    
    public List<ResignationRequest> getPendingForAdmin(Long companyId) {
        // Get both manager-approved and pending-manager resignations
        List<ResignationRequest> managerApproved = resignationRepository.findByStatusAndEmployee_Company_Id(
            ResignationStatus.MANAGER_APPROVED, 
            companyId
        );
        List<ResignationRequest> pendingManager = resignationRepository.findByStatusAndEmployee_Company_Id(
            ResignationStatus.PENDING_MANAGER, 
            companyId
        );
        
        // Combine both lists
        List<ResignationRequest> allPending = new java.util.ArrayList<>();
        allPending.addAll(managerApproved);
        allPending.addAll(pendingManager);
        
        return allPending;
    }
    
    @Transactional
    public ResignationRequest approveByManager(Long resignationId, Long managerId, ResignationApprovalDto dto) {
        ResignationRequest resignation = resignationRepository.findById(resignationId)
            .orElseThrow(() -> new RuntimeException("Resignation request not found"));
        
        if (resignation.getStatus() != ResignationStatus.PENDING_MANAGER) {
            throw new RuntimeException("Resignation request is not pending manager approval");
        }
        
        // Prevent self-approval
        if (resignation.getEmployee().getId().equals(managerId)) {
            throw new RuntimeException("You cannot approve your own resignation request");
        }
        
        User manager = userRepository.findById(managerId)
            .orElseThrow(() -> new RuntimeException("Manager not found"));
        
        if (dto.isApproved()) {
            resignation.setStatus(ResignationStatus.MANAGER_APPROVED);
            resignation.setManagerApprovedBy(manager);
            resignation.setManagerApprovalDate(LocalDateTime.now());
            resignation.setManagerRemarks(dto.getRemarks());
            
            ResignationRequest savedResignation = resignationRepository.save(resignation);
            
            // Notify admin about manager approval
            User admin = findAdminForCompany(resignation.getCompany().getId());
            if (admin != null) {
                emailService.sendResignationManagerApprovalNotification(savedResignation, admin);
            }
            
            // Notify employee via email
            emailService.sendResignationStatusUpdateNotification(savedResignation);
            
            // Create in-app notification for employee
            notificationService.createNotification(
                    savedResignation.getEmployee(),
                    "Resignation Approved by Manager",
                    "Your resignation request has been approved by your manager and is now pending admin approval.",
                    Notification.NotificationType.RESIGNATION_APPROVED,
                    savedResignation.getId(),
                    "RESIGNATION"
            );
            
            return savedResignation;
        } else {
            resignation.setStatus(ResignationStatus.REJECTED);
            resignation.setManagerApprovedBy(manager);
            resignation.setManagerApprovalDate(LocalDateTime.now());
            resignation.setManagerRemarks(dto.getRemarks());
            
            ResignationRequest savedResignation = resignationRepository.save(resignation);
            
            // Notify employee via email
            emailService.sendResignationStatusUpdateNotification(savedResignation);
            
            // Create in-app notification for employee
            notificationService.createNotification(
                    savedResignation.getEmployee(),
                    "Resignation Rejected by Manager",
                    "Your resignation request has been rejected by your manager. Reason: " + 
                            (dto.getRemarks() != null ? dto.getRemarks() : "No reason provided"),
                    Notification.NotificationType.RESIGNATION_REJECTED,
                    savedResignation.getId(),
                    "RESIGNATION"
            );
            
            return savedResignation;
        }
    }
    
    @Transactional
       public ResignationRequest approveByAdmin(Long resignationId, Long adminId, ResignationApprovalDto dto) {
        ResignationRequest resignation = resignationRepository.findById(resignationId)
            .orElseThrow(() -> new RuntimeException("Resignation request not found"));
        
        if (resignation.getStatus() != ResignationStatus.MANAGER_APPROVED && 
            resignation.getStatus() != ResignationStatus.PENDING_MANAGER) {
            throw new RuntimeException("Resignation request is not pending approval");
        }
        
        // Prevent self-approval
        if (resignation.getEmployee().getId().equals(adminId)) {
            throw new RuntimeException("You cannot approve your own resignation request");
        }
        
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (dto.isApproved()) {
            resignation.setStatus(ResignationStatus.APPROVED);
            resignation.setAdminApprovedBy(admin);
            resignation.setAdminApprovalDate(LocalDateTime.now());
            resignation.setAdminRemarks(dto.getRemarks());
            resignation.setActualLastWorkingDay(resignation.getProposedLastWorkingDay());
            
            ResignationRequest savedResignation = resignationRepository.save(resignation);
            
            // Notify employee about final approval via email
            emailService.sendResignationStatusUpdateNotification(savedResignation);
            
            // Create in-app notification for employee
            notificationService.createNotification(
                    savedResignation.getEmployee(),
                    "Resignation Approved",
                    "Your resignation request has been approved by the admin. Your last working day is " + 
                            savedResignation.getActualLastWorkingDay() + ". Please complete your exit formalities.",
                    Notification.NotificationType.RESIGNATION_APPROVED,
                    savedResignation.getId(),
                    "RESIGNATION"
            );
            
            return savedResignation;
        } else {
            resignation.setStatus(ResignationStatus.REJECTED);
            resignation.setAdminApprovedBy(admin);
            resignation.setAdminApprovalDate(LocalDateTime.now());
            resignation.setAdminRemarks(dto.getRemarks());
            
            ResignationRequest savedResignation = resignationRepository.save(resignation);
            
            // Notify employee about rejection via email
            emailService.sendResignationStatusUpdateNotification(savedResignation);
            
            // Create in-app notification for employee
            notificationService.createNotification(
                    savedResignation.getEmployee(),
                    "Resignation Rejected",
                    "Your resignation request has been rejected by the admin. Reason: " + 
                            (dto.getRemarks() != null ? dto.getRemarks() : "No reason provided"),
                    Notification.NotificationType.RESIGNATION_REJECTED,
                    savedResignation.getId(),
                    "RESIGNATION"
            );
            
            return savedResignation;
        }
    }
    
    public ResignationRequest getResignationById(Long id) {
        return resignationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Resignation request not found"));
    }
    
    /**
     * Find manager for an employee - simplified version
     * In a real system, this would look at department hierarchy
     */
    private User findManagerForEmployee(User employee) {
        // Find a user with MANAGER role in the same company
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
