package com.gentech.hrportal.service;

import com.gentech.hrportal.dto.CreateCompanyRequest;
import com.gentech.hrportal.dto.CreateUserRequest;
import com.gentech.hrportal.entity.Company;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.CompanyRepository;
import com.gentech.hrportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SuperAdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllAdmins() {
        return userRepository.findByRole(User.Role.ADMIN);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Transactional
    public User createAdmin(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (request.getCompanyId() == null) {
            throw new RuntimeException("Company is required for admin");
        }

        User admin = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(User.Role.ADMIN)
                .build();

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));
        admin.setCompany(company);

        return userRepository.save(admin);
    }

    @Transactional
    public Company createCompany(CreateCompanyRequest request) {
        if (companyRepository.existsByName(request.getName())) {
            throw new RuntimeException("Company name already exists");
        }

        // Phone number validation
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            String phoneDigits = request.getPhone().replaceAll("[^\\d]", "");
            if (phoneDigits.length() < 10 || phoneDigits.length() > 15) {
                throw new RuntimeException("Phone number must be between 10-15 digits");
            }
        }

        Company company = Company.builder()
                .name(request.getName())
                .logoUrl(request.getLogoUrl())
                .address(request.getAddress())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();

        return companyRepository.save(company);
    }

    @Transactional
    public User mapAdminToCompany(Long adminId, Long companyId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("User is not an admin");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        admin.setCompany(company);
        return userRepository.save(admin);
    }

    public void deleteAdmin(Long id) {
        userRepository.deleteById(id);
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    @Transactional
    public Company updateCompany(Long id, CreateCompanyRequest request) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Check if name is being changed and if new name already exists
        if (!company.getName().equals(request.getName()) && companyRepository.existsByName(request.getName())) {
            throw new RuntimeException("Company name already exists");
        }

        // Phone number validation
        if (request.getPhone() != null && !request.getPhone().isEmpty()) {
            String phoneDigits = request.getPhone().replaceAll("[^\\d]", "");
            if (phoneDigits.length() < 10 || phoneDigits.length() > 15) {
                throw new RuntimeException("Phone number must be between 10-15 digits");
            }
        }

        company.setName(request.getName());
        company.setLogoUrl(request.getLogoUrl());
        company.setAddress(request.getAddress());
        company.setPhone(request.getPhone());
        company.setEmail(request.getEmail());

        return companyRepository.save(company);
    }

    @Transactional
    public User updateAdmin(Long id, CreateUserRequest request) {
        User admin = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("User is not an admin");
        }

        // Check username uniqueness if changed (excluding current user)
        if (userRepository.findByUsernameAndIdNot(request.getUsername().trim(), admin.getId()).isPresent()) {
            throw new RuntimeException("Username '" + request.getUsername() + "' is already taken by another user");
        }

        // Check email uniqueness if changed (excluding current user)
        if (userRepository.findByEmailAndIdNot(request.getEmail().trim(), admin.getId()).isPresent()) {
            throw new RuntimeException("Email '" + request.getEmail() + "' is already taken by another user");
        }

        admin.setUsername(request.getUsername());
        admin.setEmail(request.getEmail());
        admin.setFullName(request.getFullName());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new RuntimeException("Company not found"));
            admin.setCompany(company);
        }

        return userRepository.save(admin);
    }
}
