package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.EmployeeExit;
import com.gentech.hrportal.entity.EmployeeExit.ExitStatus;
import com.gentech.hrportal.entity.EmployeeExit.ApprovalStatus;
import com.gentech.hrportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeExitRepository extends JpaRepository<EmployeeExit, Long> {
    
    List<EmployeeExit> findByEmployee(User employee);
    
    List<EmployeeExit> findByEmployeeId(Long employeeId);
    
    Optional<EmployeeExit> findFirstByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
    
    List<EmployeeExit> findByExitStatus(ExitStatus status);
    
    List<EmployeeExit> findByManagerApprovalStatus(ApprovalStatus status);
    
    List<EmployeeExit> findByAdminApprovalStatus(ApprovalStatus status);
    
    List<EmployeeExit> findAllByOrderByCreatedAtDesc();
    
    List<EmployeeExit> findByEmployee_Company_Id(Long companyId);
    
    List<EmployeeExit> findByExitStatusAndEmployee_Company_Id(ExitStatus status, Long companyId);
    
    boolean existsByEmployeeIdAndExitStatus(Long employeeId, ExitStatus exitStatus);
    
    List<EmployeeExit> findByManagerApprovalStatusAndEmployee_Company_Id(ApprovalStatus status, Long companyId);
}
