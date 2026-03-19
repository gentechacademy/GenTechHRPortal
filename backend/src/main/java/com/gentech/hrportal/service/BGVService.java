package com.gentech.hrportal.service;

import com.gentech.hrportal.dto.BGVInitiateRequest;
import com.gentech.hrportal.dto.BGVVerifyRequest;
import com.gentech.hrportal.entity.*;
import com.gentech.hrportal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class BGVService {
    
    @Autowired
    private BGVRequestRepository bgvRequestRepository;
    
    @Autowired
    private BGVDocumentRepository bgvDocumentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CompanyRepository companyRepository;
    
    private static final List<BGVDocument.DocumentType> FRESHER_DOCUMENTS = Arrays.asList(
        BGVDocument.DocumentType.AADHAR_CARD, BGVDocument.DocumentType.PAN_CARD,
        BGVDocument.DocumentType.DRIVING_LICENSE, BGVDocument.DocumentType.EDUCATION_MARKSHEET,
        BGVDocument.DocumentType.EDUCATION_CERTIFICATE, BGVDocument.DocumentType.ACKNOWLEDGEMENT_FORM
    );
    
    private static final List<BGVDocument.DocumentType> EXPERIENCED_DOCUMENTS = Arrays.asList(
        BGVDocument.DocumentType.AADHAR_CARD, BGVDocument.DocumentType.PAN_CARD,
        BGVDocument.DocumentType.DRIVING_LICENSE, BGVDocument.DocumentType.EDUCATION_MARKSHEET,
        BGVDocument.DocumentType.EDUCATION_CERTIFICATE, BGVDocument.DocumentType.PREVIOUS_EMPLOYMENT_OFFER,
        BGVDocument.DocumentType.PREVIOUS_EMPLOYMENT_PAYSLIPS, BGVDocument.DocumentType.PREVIOUS_EMPLOYMENT_RELIEVING,
        BGVDocument.DocumentType.PREVIOUS_EMPLOYMENT_EXPERIENCE, BGVDocument.DocumentType.SELF_DECLARATION_FORM
    );
    
    @Transactional
    public BGVRequest initiateBGV(Long adminId, BGVInitiateRequest request) {
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        User employee = userRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        if (employee.getCompany() == null) {
            throw new RuntimeException("Employee does not belong to any company");
        }
        
        Optional<BGVRequest> existingRequest = bgvRequestRepository
            .findByEmployeeIdAndStatusNot(employee.getId(), BGVRequest.BGVStatus.APPROVED);
        
        if (existingRequest.isPresent()) {
            throw new RuntimeException("An active BGV request already exists for this employee");
        }
        
        BGVRequest.EmployeeType empType = BGVRequest.EmployeeType.valueOf(request.getEmployeeType());
        
        BGVRequest bgvRequest = new BGVRequest();
        bgvRequest.setEmployee(employee);
        bgvRequest.setCompany(employee.getCompany());
        bgvRequest.setInitiatedBy(admin);
        bgvRequest.setStatus(BGVRequest.BGVStatus.PENDING);
        bgvRequest.setEmployeeType(empType);
        bgvRequest.setRemarks(request.getRemarks());
        
        BGVRequest savedRequest = bgvRequestRepository.save(bgvRequest);
        
        List<BGVDocument.DocumentType> requiredDocs = 
            empType == BGVRequest.EmployeeType.FRESHER ? FRESHER_DOCUMENTS : EXPERIENCED_DOCUMENTS;
        
        for (BGVDocument.DocumentType docType : requiredDocs) {
            BGVDocument document = new BGVDocument();
            document.setBgvRequest(savedRequest);
            document.setDocumentType(docType);
            document.setDocumentName(getDocumentDisplayName(docType));
            document.setStatus(BGVDocument.DocumentStatus.PENDING);
            bgvDocumentRepository.save(document);
        }
        
        return savedRequest;
    }
    
    public List<BGVRequest> getEmployeeBGVRequests(Long employeeId) {
        return bgvRequestRepository.findByEmployeeId(employeeId);
    }
    
    @Transactional(readOnly = true)
    public Optional<BGVRequest> getActiveBGVRequest(Long employeeId) {
        Optional<BGVRequest> request = bgvRequestRepository.findByEmployeeIdAndStatusNot(employeeId, BGVRequest.BGVStatus.APPROVED);
        // Force load lazy collections
        request.ifPresent(r -> {
            if (r.getDocuments() != null) {
                r.getDocuments().size();
            }
        });
        return request;
    }
    
    public List<BGVRequest> getCompanyBGVRequests(Long companyId) {
        return bgvRequestRepository.findByCompanyId(companyId);
    }
    
    public List<BGVRequest> getPendingBGVRequests(Long companyId) {
        return bgvRequestRepository.findByCompanyIdAndStatus(companyId, BGVRequest.BGVStatus.SUBMITTED);
    }
    
    public List<BGVRequest> getAllBGVRequests() {
        return bgvRequestRepository.findAll();
    }
    
    public List<BGVRequest> getPendingBGVRequests() {
        return bgvRequestRepository.findByStatus(BGVRequest.BGVStatus.SUBMITTED);
    }
    
    public List<BGVDocument> getBGVDocuments(Long bgvRequestId) {
        return bgvDocumentRepository.findByBgvRequestId(bgvRequestId);
    }
    
    @Transactional
    public BGVDocument uploadDocument(Long bgvRequestId, BGVDocument.DocumentType docType, String fileUrl, String fileName) {
        BGVRequest request = bgvRequestRepository.findById(bgvRequestId)
            .orElseThrow(() -> new RuntimeException("BGV Request not found"));
        
        List<BGVDocument> docs = bgvDocumentRepository.findByBgvRequestId(bgvRequestId);
        BGVDocument document = docs.stream()
            .filter(d -> d.getDocumentType() == docType)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Document type not required for this BGV"));
        
        document.setFileUrl(fileUrl);
        document.setFileName(fileName);
        document.setStatus(BGVDocument.DocumentStatus.UPLOADED);
        
        if (request.getStatus() == BGVRequest.BGVStatus.PENDING) {
            request.setStatus(BGVRequest.BGVStatus.IN_PROGRESS);
            bgvRequestRepository.save(request);
        }
        
        return bgvDocumentRepository.save(document);
    }
    
    @Transactional
    public BGVRequest submitForVerification(Long employeeId, Long bgvRequestId) {
        BGVRequest request = bgvRequestRepository.findById(bgvRequestId)
            .orElseThrow(() -> new RuntimeException("BGV Request not found"));
        
        if (!request.getEmployee().getId().equals(employeeId)) {
            throw new RuntimeException("Unauthorized");
        }
        
        List<BGVDocument> docs = bgvDocumentRepository.findByBgvRequestId(bgvRequestId);
        boolean allUploaded = docs.stream().allMatch(d -> d.getStatus() == BGVDocument.DocumentStatus.UPLOADED || 
                                                          d.getStatus() == BGVDocument.DocumentStatus.APPROVED);
        
        if (!allUploaded) {
            throw new RuntimeException("All required documents must be uploaded before submission");
        }
        
        request.setStatus(BGVRequest.BGVStatus.SUBMITTED);
        return bgvRequestRepository.save(request);
    }
    
    @Transactional
    public BGVDocument verifyDocument(Long adminId, BGVVerifyRequest request) {
        BGVDocument document = bgvDocumentRepository.findById(request.getDocumentId())
            .orElseThrow(() -> new RuntimeException("Document not found"));
        
        document.setStatus(request.isApproved() ? BGVDocument.DocumentStatus.APPROVED : BGVDocument.DocumentStatus.REJECTED);
        document.setRemarks(request.getRemarks());
        
        BGVRequest bgvRequest = document.getBgvRequest();
        List<BGVDocument> allDocs = bgvDocumentRepository.findByBgvRequestId(bgvRequest.getId());
        
        boolean allApproved = allDocs.stream().allMatch(d -> d.getStatus() == BGVDocument.DocumentStatus.APPROVED);
        boolean anyRejected = allDocs.stream().anyMatch(d -> d.getStatus() == BGVDocument.DocumentStatus.REJECTED);
        
        if (allApproved) {
            bgvRequest.setStatus(BGVRequest.BGVStatus.APPROVED);
        } else if (anyRejected) {
            bgvRequest.setStatus(BGVRequest.BGVStatus.PARTIAL_APPROVED);
        } else {
            bgvRequest.setStatus(BGVRequest.BGVStatus.UNDER_REVIEW);
        }
        
        bgvRequestRepository.save(bgvRequest);
        return bgvDocumentRepository.save(document);
    }
    
    @Transactional
    public BGVRequest completeVerification(Long adminId, Long bgvRequestId, boolean approved, String remarks) {
        BGVRequest request = bgvRequestRepository.findById(bgvRequestId)
            .orElseThrow(() -> new RuntimeException("BGV Request not found"));
        
        User admin = userRepository.findById(adminId)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        request.setStatus(approved ? BGVRequest.BGVStatus.APPROVED : BGVRequest.BGVStatus.REJECTED);
        request.setVerifiedBy(admin);
        request.setRemarks(remarks);
        
        return bgvRequestRepository.save(request);
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
            default: return type.name();
        }
    }
}
