package com.gentech.hrportal.service;

import com.gentech.hrportal.entity.*;
import com.gentech.hrportal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class HRPolicyService {
    
    @Autowired
    private HRPolicyRepository policyRepository;
    
    @Autowired
    private PolicyAcknowledgmentRepository acknowledgmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public HRPolicy createPolicy(HRPolicy policy) {
        return policyRepository.save(policy);
    }
    
    public List<HRPolicy> getAllPolicies() {
        return policyRepository.findAll();
    }
    
    public List<HRPolicy> getAllActivePolicies() {
        return policyRepository.findByActiveTrue();
    }
    
    public List<HRPolicy> getPoliciesByType(HRPolicy.PolicyType type) {
        return policyRepository.findByPolicyTypeAndActiveTrue(type);
    }
    
    @Transactional
    public PolicyAcknowledgment assignPolicyToEmployee(Long policyId, Long employeeId) {
        HRPolicy policy = policyRepository.findById(policyId)
            .orElseThrow(() -> new RuntimeException("Policy not found"));
        
        User employee = userRepository.findById(employeeId)
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        Optional<PolicyAcknowledgment> existing = acknowledgmentRepository
            .findByPolicyIdAndEmployeeId(policyId, employeeId);
        
        if (existing.isPresent()) {
            throw new RuntimeException("Policy already assigned to this employee");
        }
        
        PolicyAcknowledgment acknowledgment = new PolicyAcknowledgment();
        acknowledgment.setPolicy(policy);
        acknowledgment.setEmployee(employee);
        acknowledgment.setCompany(employee.getCompany());
        acknowledgment.setStatus(PolicyAcknowledgment.AcknowledgmentStatus.PENDING);
        
        return acknowledgmentRepository.save(acknowledgment);
    }
    
    @Transactional(readOnly = true)
    public List<PolicyAcknowledgment> getEmployeePolicies(Long employeeId) {
        return acknowledgmentRepository.findByEmployeeId(employeeId);
    }
    
    @Transactional(readOnly = true)
    public List<PolicyAcknowledgment> getPendingPolicies(Long employeeId) {
        List<PolicyAcknowledgment> list = acknowledgmentRepository.findByEmployeeIdAndStatus(employeeId, PolicyAcknowledgment.AcknowledgmentStatus.PENDING);
        // Force load lazy collections
        list.forEach(a -> {
            if (a.getPolicy() != null) {
                a.getPolicy().getPolicyName();
            }
        });
        return list;
    }
    
    @Transactional(readOnly = true)
    public List<PolicyAcknowledgment> getAcknowledgedPolicies(Long employeeId) {
        List<PolicyAcknowledgment> list = acknowledgmentRepository.findByEmployeeIdAndStatus(employeeId, PolicyAcknowledgment.AcknowledgmentStatus.ACKNOWLEDGED);
        // Force load lazy collections
        list.forEach(a -> {
            if (a.getPolicy() != null) {
                a.getPolicy().getPolicyName();
            }
        });
        return list;
    }
    
    @Transactional
    public PolicyAcknowledgment acknowledgePolicy(Long acknowledgmentId, String signature, String ipAddress) {
        PolicyAcknowledgment acknowledgment = acknowledgmentRepository.findById(acknowledgmentId)
            .orElseThrow(() -> new RuntimeException("Acknowledgment not found"));
        
        acknowledgment.setStatus(PolicyAcknowledgment.AcknowledgmentStatus.ACKNOWLEDGED);
        acknowledgment.setSignature(signature);
        acknowledgment.setIpAddress(ipAddress);
        
        return acknowledgmentRepository.save(acknowledgment);
    }
}
