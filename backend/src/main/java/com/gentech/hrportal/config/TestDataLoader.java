package com.gentech.hrportal.config;

import com.gentech.hrportal.entity.*;
import com.gentech.hrportal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class TestDataLoader implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HRPolicyRepository policyRepository;
    
    @Autowired
    private PolicyAcknowledgmentRepository acknowledgmentRepository;
    
    @Autowired
    private BGVRequestRepository bgvRequestRepository;
    
    @Autowired
    private BGVDocumentRepository bgvDocumentRepository;
    
    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("🔄 Loading test data...");
        
        try {
            createSamplePolicies();
            createSampleBGVData();
            createSamplePolicyAssignments();
            System.out.println("✅ Test data loaded successfully!");
        } catch (Exception e) {
            System.out.println("⚠️  Test data loading error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createSamplePolicies() {
        if (policyRepository.count() > 0) {
            System.out.println("   ℹ Policies already exist");
            return;
        }
        
        List<HRPolicy> policies = Arrays.asList(
            createPolicy("Code of Integrity", "INTEGRITY_001", "Ethical standards and integrity guidelines for all employees", HRPolicy.PolicyType.POLICY, HRPolicy.PolicyCategory.INTEGRITY),
            createPolicy("POSH Policy", "POSH_001", "Prevention of Sexual Harassment at workplace as per POSH Act 2013", HRPolicy.PolicyType.POLICY, HRPolicy.PolicyCategory.POSH),
            createPolicy("Data Protection Policy", "DATA_PROT_001", "Handling and protection of company and personal data", HRPolicy.PolicyType.POLICY, HRPolicy.PolicyCategory.DATA_PROTECTION),
            createPolicy("Confidentiality Agreement", "CONF_001", "Agreement to maintain confidentiality of company information", HRPolicy.PolicyType.POLICY, HRPolicy.PolicyCategory.CONFIDENTIALITY),
            createPolicy("Code of Conduct", "CONDUCT_001", "Professional behavior guidelines in the workplace", HRPolicy.PolicyType.POLICY, HRPolicy.PolicyCategory.CODE_OF_CONDUCT),
            createPolicy("PF Nomination Form", "PF_001", "Provident Fund nomination form for employees", HRPolicy.PolicyType.FORM, HRPolicy.PolicyCategory.PF_NOMINATION),
            createPolicy("Gratuity Nomination Form", "GRATUITY_001", "Gratuity nomination form for employees", HRPolicy.PolicyType.FORM, HRPolicy.PolicyCategory.GRATUITY),
            createPolicy("ESI Declaration Form", "ESI_001", "Employee State Insurance declaration form", HRPolicy.PolicyType.FORM, HRPolicy.PolicyCategory.ESI_DECLARATION)
        );
        
        policies.forEach(policyRepository::save);
        System.out.println("   ✅ Created " + policies.size() + " HR Policies/Forms");
    }
    
    private void createSampleBGVData() {
        // Get employees
        Optional<User> dev1 = userRepository.findByUsername("developer1");
        Optional<User> dev2 = userRepository.findByUsername("developer2");
        Optional<User> admin = userRepository.findByUsername("admin");
        
        if (!dev1.isPresent() || !admin.isPresent()) {
            System.out.println("   ℹ Required users not found for BGV data");
            return;
        }
        
        // Check if BGV already exists for developer1
        Optional<BGVRequest> existing = bgvRequestRepository.findByEmployeeIdAndStatusNot(
            dev1.get().getId(), BGVRequest.BGVStatus.APPROVED);
        
        if (existing.isPresent()) {
            System.out.println("   ℹ BGV already exists for developer1");
        } else {
            // Create BGV for developer1 (Fresher)
            BGVRequest bgv1 = new BGVRequest();
            bgv1.setEmployee(dev1.get());
            bgv1.setCompany(dev1.get().getCompany());
            bgv1.setInitiatedBy(admin.get());
            bgv1.setStatus(BGVRequest.BGVStatus.PENDING);
            bgv1.setEmployeeType(BGVRequest.EmployeeType.FRESHER);
            bgv1.setRemarks("BGV initiated for new hire - Fresher");
            bgv1.setInitiatedAt(LocalDateTime.now().minusDays(5));
            
            BGVRequest savedBGV1 = bgvRequestRepository.save(bgv1);
            createBGVDocuments(savedBGV1, true);
            System.out.println("   ✅ Created BGV with documents for developer1 (Fresher)");
        }
        
        // Create BGV for developer2 (Experienced)
        if (dev2.isPresent()) {
            Optional<BGVRequest> existing2 = bgvRequestRepository.findByEmployeeIdAndStatusNot(
                dev2.get().getId(), BGVRequest.BGVStatus.APPROVED);
            
            if (!existing2.isPresent()) {
                BGVRequest bgv2 = new BGVRequest();
                bgv2.setEmployee(dev2.get());
                bgv2.setCompany(dev2.get().getCompany());
                bgv2.setInitiatedBy(admin.get());
                bgv2.setStatus(BGVRequest.BGVStatus.UNDER_REVIEW);
                bgv2.setEmployeeType(BGVRequest.EmployeeType.EXPERIENCED);
                bgv2.setRemarks("BGV for experienced hire - Documents submitted");
                bgv2.setInitiatedAt(LocalDateTime.now().minusDays(10));
                bgv2.setSubmittedAt(LocalDateTime.now().minusDays(3));
                
                BGVRequest savedBGV2 = bgvRequestRepository.save(bgv2);
                createBGVDocuments(savedBGV2, false);
                
                // Upload some documents for this one
                uploadSampleDocuments(savedBGV2);
                
                System.out.println("   ✅ Created BGV for developer2 (Experienced) - Under Verification");
            }
        }
    }
    
    private void createBGVDocuments(BGVRequest bgv, boolean isFresher) {
        List<BGVDocument.DocumentType> docTypes;
        
        if (isFresher) {
            docTypes = Arrays.asList(
                BGVDocument.DocumentType.AADHAR_CARD,
                BGVDocument.DocumentType.PAN_CARD,
                BGVDocument.DocumentType.DRIVING_LICENSE,
                BGVDocument.DocumentType.EDUCATION_MARKSHEET,
                BGVDocument.DocumentType.EDUCATION_CERTIFICATE,
                BGVDocument.DocumentType.ACKNOWLEDGEMENT_FORM
            );
        } else {
            docTypes = Arrays.asList(
                BGVDocument.DocumentType.AADHAR_CARD,
                BGVDocument.DocumentType.PAN_CARD,
                BGVDocument.DocumentType.DRIVING_LICENSE,
                BGVDocument.DocumentType.EDUCATION_MARKSHEET,
                BGVDocument.DocumentType.EDUCATION_CERTIFICATE,
                BGVDocument.DocumentType.PREVIOUS_EMPLOYMENT_OFFER,
                BGVDocument.DocumentType.PREVIOUS_EMPLOYMENT_PAYSLIPS,
                BGVDocument.DocumentType.PREVIOUS_EMPLOYMENT_RELIEVING,
                BGVDocument.DocumentType.PREVIOUS_EMPLOYMENT_EXPERIENCE,
                BGVDocument.DocumentType.SELF_DECLARATION_FORM
            );
        }
        
        for (BGVDocument.DocumentType type : docTypes) {
            BGVDocument doc = new BGVDocument();
            doc.setBgvRequest(bgv);
            doc.setDocumentType(type);
            doc.setDocumentName(getDocumentDisplayName(type));
            doc.setStatus(BGVDocument.DocumentStatus.PENDING);
            bgvDocumentRepository.save(doc);
        }
    }
    
    private void uploadSampleDocuments(BGVRequest bgv) {
        // Simulate some documents being uploaded
        List<BGVDocument> documents = bgvDocumentRepository.findByBgvRequestId(bgv.getId());
        
        int count = 0;
        for (BGVDocument doc : documents) {
            if (count < 6) { // Upload first 6 documents
                doc.setStatus(BGVDocument.DocumentStatus.UPLOADED);
                doc.setFileUrl("/uploads/bgv-documents/sample_" + doc.getDocumentType().name().toLowerCase() + ".pdf");
                doc.setFileName(doc.getDocumentName().replace(" ", "_") + ".pdf");
                doc.setUploadedAt(LocalDateTime.now().minusDays(2));
                bgvDocumentRepository.save(doc);
                count++;
            }
        }
    }
    
    private void createSamplePolicyAssignments() {
        Optional<User> dev1 = userRepository.findByUsername("developer1");
        Optional<User> dev2 = userRepository.findByUsername("developer2");
        
        if (!dev1.isPresent()) {
            System.out.println("   ℹ Developer1 not found for policy assignments");
            return;
        }
        
        List<HRPolicy> policies = policyRepository.findByActiveTrue();
        if (policies.isEmpty()) {
            return;
        }
        
        int assigned = 0;
        // Assign first 3 policies to developer1 as PENDING
        for (int i = 0; i < Math.min(3, policies.size()); i++) {
            HRPolicy policy = policies.get(i);
            
            Optional<PolicyAcknowledgment> existing = acknowledgmentRepository
                .findByPolicyIdAndEmployeeId(policy.getId(), dev1.get().getId());
            
            if (!existing.isPresent()) {
                PolicyAcknowledgment ack = new PolicyAcknowledgment();
                ack.setPolicy(policy);
                ack.setEmployee(dev1.get());
                ack.setCompany(dev1.get().getCompany());
                ack.setStatus(PolicyAcknowledgment.AcknowledgmentStatus.PENDING);
                ack.setAssignedAt(LocalDateTime.now().minusDays(i));
                acknowledgmentRepository.save(ack);
                assigned++;
            }
        }
        
        // Assign 2 policies to developer2 as ACKNOWLEDGED
        if (dev2.isPresent()) {
            for (int i = 3; i < Math.min(5, policies.size()); i++) {
                HRPolicy policy = policies.get(i);
                
                Optional<PolicyAcknowledgment> existing = acknowledgmentRepository
                    .findByPolicyIdAndEmployeeId(policy.getId(), dev2.get().getId());
                
                if (!existing.isPresent()) {
                    PolicyAcknowledgment ack = new PolicyAcknowledgment();
                    ack.setPolicy(policy);
                    ack.setEmployee(dev2.get());
                    ack.setCompany(dev2.get().getCompany());
                    ack.setStatus(PolicyAcknowledgment.AcknowledgmentStatus.ACKNOWLEDGED);
                    ack.setAssignedAt(LocalDateTime.now().minusDays(5));
                    ack.setAcknowledgedAt(LocalDateTime.now().minusDays(2));
                    ack.setSignature("Digital Signature");
                    acknowledgmentRepository.save(ack);
                    assigned++;
                }
            }
        }
        
        System.out.println("   ✅ Assigned " + assigned + " policies to employees");
    }
    
    private HRPolicy createPolicy(String name, String code, String desc, HRPolicy.PolicyType type, HRPolicy.PolicyCategory cat) {
        HRPolicy p = new HRPolicy();
        p.setPolicyName(name);
        p.setPolicyCode(code);
        p.setDescription(desc);
        p.setPolicyType(type);
        p.setCategory(cat);
        p.setFileUrl("/uploads/hr-policies/" + code.toLowerCase() + ".pdf");
        p.setFileName(name.replace(" ", "_") + ".pdf");
        p.setActive(true);
        return p;
    }
    
    private String getDocumentDisplayName(BGVDocument.DocumentType type) {
        switch (type) {
            case AADHAR_CARD: return "Aadhar Card";
            case PAN_CARD: return "PAN Card";
            case DRIVING_LICENSE: return "Driving License";
            case EDUCATION_MARKSHEET: return "Education Marksheet";
            case EDUCATION_CERTIFICATE: return "Education Certificate";
            case PREVIOUS_EMPLOYMENT_OFFER: return "Previous Employment Offer Letter";
            case PREVIOUS_EMPLOYMENT_PAYSLIPS: return "Previous Employment Payslips";
            case PREVIOUS_EMPLOYMENT_RELIEVING: return "Previous Employment Relieving Letter";
            case PREVIOUS_EMPLOYMENT_EXPERIENCE: return "Previous Employment Experience Certificate";
            case SELF_DECLARATION_FORM: return "Self Declaration Form";
            case ACKNOWLEDGEMENT_FORM: return "Acknowledgement Form";
            default: return type.name().replace("_", " ");
        }
    }
}
