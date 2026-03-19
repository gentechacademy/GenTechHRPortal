package com.gentech.hrportal.dto;

import com.gentech.hrportal.entity.EmployeeDocument;
import com.gentech.hrportal.entity.EmployeeDocument.DocumentStatus;
import com.gentech.hrportal.entity.EmployeeDocument.DocumentType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DocumentResponse {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeEmail;
    private Long companyId;
    private String companyName;
    private DocumentType documentType;
    private String documentName;
    private String documentUrl;
    private LocalDate uploadDate;
    private DocumentStatus status;
    private String adminComments;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public DocumentResponse() {}
    
    public DocumentResponse(EmployeeDocument document) {
        this.id = document.getId();
        if (document.getEmployee() != null) {
            this.employeeId = document.getEmployee().getId();
            this.employeeName = document.getEmployee().getFullName();
            this.employeeEmail = document.getEmployee().getEmail();
            if (document.getEmployee().getCompany() != null) {
                this.companyId = document.getEmployee().getCompany().getId();
                this.companyName = document.getEmployee().getCompany().getName();
            }
        }
        this.documentType = document.getDocumentType();
        this.documentName = document.getDocumentName();
        this.documentUrl = document.getDocumentUrl();
        this.uploadDate = document.getUploadDate();
        this.status = document.getStatus();
        this.adminComments = document.getAdminComments();
        if (document.getApprovedBy() != null) {
            this.approvedById = document.getApprovedBy().getId();
            this.approvedByName = document.getApprovedBy().getFullName();
        }
        this.approvedAt = document.getApprovedAt();
        this.createdAt = document.getCreatedAt();
        this.updatedAt = document.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    
    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }
    
    public String getDocumentUrl() { return documentUrl; }
    public void setDocumentUrl(String documentUrl) { this.documentUrl = documentUrl; }
    
    public LocalDate getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDate uploadDate) { this.uploadDate = uploadDate; }
    
    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    
    public String getAdminComments() { return adminComments; }
    public void setAdminComments(String adminComments) { this.adminComments = adminComments; }
    
    public Long getApprovedById() { return approvedById; }
    public void setApprovedById(Long approvedById) { this.approvedById = approvedById; }
    
    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
