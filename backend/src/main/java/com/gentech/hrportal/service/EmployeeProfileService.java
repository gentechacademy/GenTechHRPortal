package com.gentech.hrportal.service;

import com.gentech.hrportal.entity.EmployeeProfile;
import com.gentech.hrportal.repository.EmployeeProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeProfileService {
    
    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;
    
    /**
     * Get employee profile by user ID
     * @param userId the user ID
     * @return EmployeeProfile or null if not found
     */
    public EmployeeProfile getProfileByUserId(Long userId) {
        return employeeProfileRepository.findByUserId(userId).orElse(null);
    }
}
