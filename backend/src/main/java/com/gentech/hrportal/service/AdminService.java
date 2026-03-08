package com.gentech.hrportal.service;

import com.gentech.hrportal.dto.CreateEmployeeRequest;
import com.gentech.hrportal.dto.EmployeeProfileResponse;
import com.gentech.hrportal.entity.Company;
import com.gentech.hrportal.entity.EmployeeProfile;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.AttendanceRepository;
import com.gentech.hrportal.repository.BonusRequestRepository;
import com.gentech.hrportal.repository.CompanyRepository;
import com.gentech.hrportal.repository.EmployeeProfileRepository;
import com.gentech.hrportal.repository.LeaveRepository;
import com.gentech.hrportal.repository.PasswordResetTokenRepository;
import com.gentech.hrportal.repository.ProfileEditRequestRepository;
import com.gentech.hrportal.repository.SalarySlipRepository;
import com.gentech.hrportal.repository.TeamMemberRepository;
import com.gentech.hrportal.repository.UserRepository;
import com.gentech.hrportal.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private SalarySlipRepository salarySlipRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private BonusRequestRepository bonusRequestRepository;

    @Autowired
    private ProfileEditRequestRepository profileEditRequestRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public List<EmployeeProfileResponse> getAllEmployees() {
        // Get the current admin's company
        Company adminCompany = getAdminCompany();
        if (adminCompany == null) {
            throw new RuntimeException("Admin is not assigned to any company");
        }

        // Return only employees from admin's company
        List<EmployeeProfile> profiles = employeeProfileRepository.findByCompanyId(adminCompany.getId());
        return profiles.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<EmployeeProfileResponse> getEmployeesByCompany(Long companyId) {
        List<EmployeeProfile> profiles = employeeProfileRepository.findByCompanyId(companyId);
        return profiles.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public EmployeeProfileResponse createEmployee(CreateEmployeeRequest request) {
        // Validate username and email
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Get company
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Generate default password if not provided
        String password = request.getPassword();
        if (password == null || password.trim().isEmpty()) {
            password = "Employee@123"; // Default password
        }

        // Create User
        User employee = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(password))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole())
                .company(company)
                .build();

        User savedUser = userRepository.save(employee);

        // Create Employee Profile
        EmployeeProfile profile = EmployeeProfile.builder()
                .user(savedUser)
                .company(company)
                .employeeCode(request.getEmployeeCode())
                .department(request.getDepartment())
                .designation(request.getDesignation())
                .dateOfJoining(request.getDateOfJoining())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .emergencyContact(request.getEmergencyContact())
                .salary(request.getSalary())
                .profilePictureUrl(request.getProfilePictureUrl())
                .build();

        EmployeeProfile savedProfile = employeeProfileRepository.save(profile);

        // Send welcome email with login credentials
        try {
            emailService.sendWelcomeEmail(savedUser, password, savedProfile);
            System.out.println("✅ Welcome email sent to: " + savedUser.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Failed to send welcome email: " + e.getMessage());
            // Don't throw exception - email failure shouldn't break employee creation
        }

        return mapToResponse(savedProfile);
    }

    public EmployeeProfileResponse getEmployeeById(Long id) {
        EmployeeProfile profile = employeeProfileRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return mapToResponse(profile);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        EmployeeProfile profile = employeeProfileRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        User user = profile.getUser();
        Long userId = user.getId();
        
        // Delete all related records first to avoid foreign key constraint violations
        // Order matters - delete child records before parent
        
        // 1. Delete attendance records where user is the employee or approver
        attendanceRepository.deleteByEmployeeId(userId);
        attendanceRepository.deleteAll(attendanceRepository.findByApprovedById(userId));
        
        // 2. Delete salary slips where user is the employee or generator
        salarySlipRepository.deleteAll(salarySlipRepository.findByEmployeeId(userId));
        salarySlipRepository.deleteAll(salarySlipRepository.findByGeneratedById(userId));
        
        // 3. Delete leave records where user is the employee, applied_by, or approved_by
        leaveRepository.deleteAll(leaveRepository.findByEmployeeId(userId));
        leaveRepository.deleteAll(leaveRepository.findByAppliedById(userId));
        leaveRepository.deleteAll(leaveRepository.findByApprovedById(userId));
        
        // 4. Delete bonus requests where user is the employee, requested_by, or approved_by
        bonusRequestRepository.deleteAll(bonusRequestRepository.findByEmployeeId(userId));
        bonusRequestRepository.deleteAll(bonusRequestRepository.findByRequestedById(userId));
        
        // 5. Delete profile edit requests where user is the employee, requested_by, or approved_by
        profileEditRequestRepository.deleteAll(profileEditRequestRepository.findByEmployeeId(userId));
        profileEditRequestRepository.deleteAll(profileEditRequestRepository.findByRequestedById(userId));
        profileEditRequestRepository.deleteAll(profileEditRequestRepository.findByApprovedById(userId));
        
        // 6. Delete team member records where user is the employee
        teamMemberRepository.deleteAll(teamMemberRepository.findByEmployeeId(userId));
        
        // 7. Delete password reset tokens for the user
        passwordResetTokenRepository.deleteByUser(user);
        
        // 8. Finally delete the employee profile and user
        employeeProfileRepository.delete(profile);
        userRepository.deleteById(userId);
    }

    @Transactional
    public EmployeeProfileResponse updateEmployee(Long id, CreateEmployeeRequest request) {
        EmployeeProfile profile = employeeProfileRepository.findByUserId(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        User user = profile.getUser();

        // Check username uniqueness if changed (excluding current user)
        if (userRepository.findByUsernameAndIdNot(request.getUsername().trim(), user.getId()).isPresent()) {
            throw new RuntimeException("Username '" + request.getUsername() + "' is already taken by another user");
        }

        // Check email uniqueness if changed (excluding current user)
        if (userRepository.findByEmailAndIdNot(request.getEmail().trim(), user.getId()).isPresent()) {
            throw new RuntimeException("Email '" + request.getEmail() + "' is already taken by another user");
        }

        // Phone number validation
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            String phoneDigits = request.getPhoneNumber().replaceAll("[^\\d]", "");
            if (phoneDigits.length() < 10 || phoneDigits.length() > 15) {
                throw new RuntimeException("Phone number must be between 10-15 digits");
            }
        }

        // Update User
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim());
        user.setFullName(request.getFullName().trim());
        user.setRole(request.getRole());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        userRepository.save(user);

        // Update Profile
        profile.setEmployeeCode(request.getEmployeeCode());
        profile.setDepartment(request.getDepartment());
        profile.setDesignation(request.getDesignation());
        profile.setDateOfJoining(request.getDateOfJoining());
        profile.setDateOfBirth(request.getDateOfBirth());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setAddress(request.getAddress());
        profile.setEmergencyContact(request.getEmergencyContact());
        profile.setSalary(request.getSalary());
        profile.setProfilePictureUrl(request.getProfilePictureUrl());

        EmployeeProfile savedProfile = employeeProfileRepository.save(profile);
        return mapToResponse(savedProfile);
    }

    private EmployeeProfileResponse mapToResponse(EmployeeProfile profile) {
        User user = profile.getUser();
        Company company = profile.getCompany();

        return EmployeeProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .companyId(company != null ? company.getId() : null)
                .companyName(company != null ? company.getName() : null)
                .companyLogoUrl(company != null ? company.getLogoUrl() : null)
                .employeeCode(profile.getEmployeeCode())
                .department(profile.getDepartment())
                .designation(profile.getDesignation())
                .dateOfJoining(profile.getDateOfJoining())
                .dateOfBirth(profile.getDateOfBirth())
                .phoneNumber(profile.getPhoneNumber())
                .address(profile.getAddress())
                .emergencyContact(profile.getEmergencyContact())
                .salary(profile.getSalary())
                .profilePictureUrl(profile.getProfilePictureUrl())
                .createdAt(profile.getCreatedAt())
                .build();
    }

    public Company getAdminCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User admin = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return admin.getCompany();
    }

    @Transactional
    public void resetAttendanceForEmployee(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        attendanceRepository.deleteByEmployeeId(user.getId());
    }
}
