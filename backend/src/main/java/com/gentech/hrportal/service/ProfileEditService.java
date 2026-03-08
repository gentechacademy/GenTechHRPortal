package com.gentech.hrportal.service;

import com.gentech.hrportal.entity.EmployeeProfile;
import com.gentech.hrportal.entity.ProfileEditRequest;
import com.gentech.hrportal.entity.ProfileEditRequest.RequestStatus;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.EmployeeProfileRepository;
import com.gentech.hrportal.repository.ProfileEditRequestRepository;
import com.gentech.hrportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProfileEditService {
    
    @Autowired
    private ProfileEditRequestRepository profileEditRequestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;
    
    @Autowired
    private EmployeeService employeeService;
    
    /**
     * Create an edit request for phone or address
     * @param employeeId the employee ID
     * @param fieldName the field name ("phoneNumber" or "address")
     * @param newValue the new value
     * @return the created edit request
     */
    @Transactional
    public ProfileEditRequest requestProfileEdit(Long employeeId, String fieldName, String newValue) {
        // Validate field name
        if (!fieldName.equals("phoneNumber") && !fieldName.equals("address")) {
            throw new RuntimeException("Invalid field name. Only 'phoneNumber' and 'address' can be edited.");
        }
        
        // Get employee and their profile
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        EmployeeProfile profile = employeeProfileRepository.findByUserId(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee profile not found"));
        
        // Get old value
        String oldValue;
        if (fieldName.equals("phoneNumber")) {
            oldValue = profile.getPhoneNumber();
        } else {
            oldValue = profile.getAddress();
        }
        
        // Check if there's already a pending request for this field
        List<ProfileEditRequest> existingRequests = profileEditRequestRepository.findByEmployeeId(employeeId);
        for (ProfileEditRequest request : existingRequests) {
            if (request.getFieldName().equals(fieldName) && request.getStatus() == RequestStatus.PENDING) {
                throw new RuntimeException("There is already a pending edit request for this field.");
            }
        }
        
        // Create edit request
        ProfileEditRequest request = new ProfileEditRequest();
        request.setEmployee(employee);
        request.setCompany(employee.getCompany());
        request.setFieldName(fieldName);
        request.setOldValue(oldValue);
        request.setNewValue(newValue);
        request.setStatus(RequestStatus.PENDING);
        request.setRequestedBy(employee);
        
        ProfileEditRequest savedRequest = profileEditRequestRepository.save(request);
        
        // Send email notification to admin (placeholder - would be implemented with email service)
        sendAdminNotification(employee, fieldName, newValue);
        
        return savedRequest;
    }
    
    /**
     * Approve a profile edit request
     * @param requestId the request ID
     * @param approverId the approver ID
     * @return the updated request
     */
    @Transactional
    public ProfileEditRequest approveProfileEdit(Long requestId, Long approverId) {
        ProfileEditRequest request = profileEditRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Edit request not found"));
        
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request has already been processed.");
        }
        
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        // Update the actual employee profile
        EmployeeProfile profile = employeeProfileRepository.findByUserId(request.getEmployee().getId())
            .orElseThrow(() -> new RuntimeException("Employee profile not found"));
        
        if (request.getFieldName().equals("phoneNumber")) {
            profile.setPhoneNumber(request.getNewValue());
        } else if (request.getFieldName().equals("address")) {
            profile.setAddress(request.getNewValue());
        }
        
        employeeProfileRepository.save(profile);
        
        // Update request status
        request.setStatus(RequestStatus.APPROVED);
        request.setApprovedBy(approver);
        request.setApprovalDate(LocalDateTime.now());
        
        ProfileEditRequest savedRequest = profileEditRequestRepository.save(request);
        
        // Send notification to employee (placeholder - would be implemented with email service)
        sendEmployeeNotification(request.getEmployee(), request.getFieldName(), true, null);
        
        return savedRequest;
    }
    
    /**
     * Reject a profile edit request
     * @param requestId the request ID
     * @param approverId the approver ID
     * @param reason the rejection reason
     * @return the updated request
     */
    @Transactional
    public ProfileEditRequest rejectProfileEdit(Long requestId, Long approverId, String reason) {
        ProfileEditRequest request = profileEditRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Edit request not found"));
        
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Request has already been processed.");
        }
        
        User approver = userRepository.findById(approverId)
            .orElseThrow(() -> new RuntimeException("Approver not found"));
        
        // Update request status
        request.setStatus(RequestStatus.REJECTED);
        request.setApprovedBy(approver);
        request.setApprovalDate(LocalDateTime.now());
        request.setRejectionReason(reason);
        
        ProfileEditRequest savedRequest = profileEditRequestRepository.save(request);
        
        // Send notification to employee (placeholder - would be implemented with email service)
        sendEmployeeNotification(request.getEmployee(), request.getFieldName(), false, reason);
        
        return savedRequest;
    }
    
    /**
     * Get all edit requests for an employee
     * @param employeeId the employee ID
     * @return list of edit requests
     */
    public List<ProfileEditRequest> getEmployeeEditRequests(Long employeeId) {
        return profileEditRequestRepository.findByEmployeeId(employeeId);
    }
    
    /**
     * Get all edit requests for a company
     * @param companyId the company ID
     * @return list of edit requests
     */
    public List<ProfileEditRequest> getCompanyEditRequests(Long companyId) {
        return profileEditRequestRepository.findByCompanyId(companyId);
    }
    
    /**
     * Get all pending edit requests for a company
     * @param companyId the company ID
     * @return list of pending edit requests
     */
    public List<ProfileEditRequest> getPendingEditRequests(Long companyId) {
        return profileEditRequestRepository.findByCompanyIdAndStatus(companyId, RequestStatus.PENDING);
    }
    
    /**
     * Get a specific edit request by ID
     * @param requestId the request ID
     * @return the edit request
     */
    public ProfileEditRequest getEditRequestById(Long requestId) {
        return profileEditRequestRepository.findById(requestId)
            .orElseThrow(() -> new RuntimeException("Edit request not found"));
    }
    
    // Placeholder methods for email notifications
    private void sendAdminNotification(User employee, String fieldName, String newValue) {
        // TODO: Implement email notification to admin
        // This would typically use an EmailService to send notification
        System.out.println("Admin notification: Employee " + employee.getFullName() + 
            " requested to change " + fieldName + " to " + newValue);
    }
    
    private void sendEmployeeNotification(User employee, String fieldName, boolean approved, String reason) {
        // TODO: Implement email notification to employee
        // This would typically use an EmailService to send notification
        if (approved) {
            System.out.println("Employee notification: Your request to change " + fieldName + " has been approved.");
        } else {
            System.out.println("Employee notification: Your request to change " + fieldName + 
                " has been rejected. Reason: " + reason);
        }
    }
}
