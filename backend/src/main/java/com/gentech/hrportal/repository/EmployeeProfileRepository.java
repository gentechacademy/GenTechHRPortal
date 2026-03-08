package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.EmployeeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface EmployeeProfileRepository extends JpaRepository<EmployeeProfile, Long> {
    Optional<EmployeeProfile> findByUserId(Long userId);
    List<EmployeeProfile> findByCompanyId(Long companyId);
    boolean existsByEmployeeCode(String employeeCode);
}
