package com.gentech.hrportal.repository;

import com.gentech.hrportal.entity.HRPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HRPolicyRepository extends JpaRepository<HRPolicy, Long> {
    
    List<HRPolicy> findByActiveTrue();
    
    List<HRPolicy> findByPolicyType(HRPolicy.PolicyType policyType);
    
    List<HRPolicy> findByCategory(HRPolicy.PolicyCategory category);
    
    List<HRPolicy> findByPolicyTypeAndActiveTrue(HRPolicy.PolicyType policyType);
}
