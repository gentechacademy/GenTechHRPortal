package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.PolicyAcknowledgment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyAcknowledgmentRepository extends JpaRepository<PolicyAcknowledgment, Long> {
    
    List<PolicyAcknowledgment> findByEmployeeId(Long employeeId);
    
    List<PolicyAcknowledgment> findByEmployeeIdAndStatus(Long employeeId, PolicyAcknowledgment.AcknowledgmentStatus status);
    
    List<PolicyAcknowledgment> findByPolicyId(Long policyId);
    
    Optional<PolicyAcknowledgment> findByPolicyIdAndEmployeeId(Long policyId, Long employeeId);
    
    List<PolicyAcknowledgment> findByCompanyId(Long companyId);
}
