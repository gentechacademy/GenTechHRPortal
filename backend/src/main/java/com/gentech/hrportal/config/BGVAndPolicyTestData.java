package com.gentech.hrportal.config;

import com.gentech.hrportal.entity.*;
import com.gentech.hrportal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class BGVAndPolicyTestData implements CommandLineRunner {
    
    @Autowired
    private HRPolicyRepository policyRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PolicyAcknowledgmentRepository acknowledgmentRepository;
    
    @Override
    public void run(String... args) {
        System.out.println("🔄 Checking HR Policy test data...");
        
        // Create HR Policies if none exist
        if (policyRepository.count() == 0) {
            createHRPolicies();
        }
        
        System.out.println("✅ BGV and HR Policy test data initialized!");
    }
    
    private void createHRPolicies() {
        System.out.println("📋 Creating HR Policies...");
        
        createPolicy("Code of Integrity", "INTEGRITY_001", 
            "This policy outlines the ethical standards and integrity expected from all employees.", 
            HRPolicy.PolicyType.POLICY, HRPolicy.PolicyCategory.INTEGRITY);
        createPolicy("Prevention of Sexual Harassment (POSH)", "POSH_001", 
            "Policy for prevention of sexual harassment at workplace as per the POSH Act, 2013.", 
            HRPolicy.PolicyType.POLICY, HRPolicy.PolicyCategory.POSH);
        createPolicy("Data Protection and Privacy", "DATA_PROT_001", 
            "Policy governing the handling and protection of company and personal data.", 
            HRPolicy.PolicyType.POLICY, HRPolicy.PolicyCategory.DATA_PROTECTION);
        createPolicy("Confidentiality Agreement", "CONF_001", 
            "Agreement to maintain confidentiality of company information and trade secrets.", 
            HRPolicy.PolicyType.POLICY, HRPolicy.PolicyCategory.CONFIDENTIALITY);
        createPolicy("Code of Conduct", "CONDUCT_001", 
            "Guidelines for professional behavior and conduct in the workplace.", 
            HRPolicy.PolicyType.POLICY, HRPolicy.PolicyCategory.CODE_OF_CONDUCT);
        createPolicy("PF Nomination Form", "PF_FORM_001", 
            "Provident Fund nomination form for employees to nominate beneficiaries.", 
            HRPolicy.PolicyType.FORM, HRPolicy.PolicyCategory.PF_NOMINATION);
        createPolicy("Gratuity Nomination Form", "GRAT_FORM_001", 
            "Gratuity nomination form for employees to nominate beneficiaries.", 
            HRPolicy.PolicyType.FORM, HRPolicy.PolicyCategory.GRATUITY);
        createPolicy("ESI Declaration Form", "ESI_FORM_001", 
            "Employee State Insurance declaration form.", 
            HRPolicy.PolicyType.FORM, HRPolicy.PolicyCategory.ESI_DECLARATION);
        
        System.out.println("✅ Created " + policyRepository.count() + " HR Policies");
    }
    
    private void createPolicy(String name, String code, String desc, HRPolicy.PolicyType type, HRPolicy.PolicyCategory category) {
        HRPolicy policy = new HRPolicy();
        policy.setPolicyName(name);
        policy.setPolicyCode(code);
        policy.setDescription(desc);
        policy.setPolicyType(type);
        policy.setCategory(category);
        policy.setFileUrl("/uploads/hr-policies/" + code.toLowerCase() + ".pdf");
        policy.setFileName(name.replace(" ", "_") + ".pdf");
        policy.setActive(true);
        policyRepository.save(policy);
    }
}
