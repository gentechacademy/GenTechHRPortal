package com.gentech.hrportal.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bgv_documents")
public class BGVDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bgv_request_id", nullable = false)
    private BGVRequest bgvRequest;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;
    
    private String documentName;
    
    private String fileUrl;
    
    private String fileName;
    
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;
    
    private String remarks;
    
    private LocalDateTime uploadedAt;
    
    private LocalDateTime verifiedAt;
    
    public enum DocumentType {
        AADHAR_CARD, PAN_CARD, DRIVING_LICENSE, EDUCATION_MARKSHEET, EDUCATION_CERTIFICATE,
        PREVIOUS_EMPLOYMENT_OFFER, PREVIOUS_EMPLOYMENT_PAYSLIPS, PREVIOUS_EMPLOYMENT_RELIEVING,
        PREVIOUS_EMPLOYMENT_EXPERIENCE, SELF_DECLARATION_FORM, ACKNOWLEDGEMENT_FORM
    }
    
    public enum DocumentStatus {
        PENDING, UPLOADED, UNDER_REVIEW, APPROVED, REJECTED
    }
    
    public BGVDocument() {}
    
    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = DocumentStatus.PENDING;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BGVRequest getBgvRequest() { return bgvRequest; }
    public void setBgvRequest(BGVRequest bgvRequest) { this.bgvRequest = bgvRequest; }
    
    public DocumentType getDocumentType() { return documentType; }
    public void setDocumentType(DocumentType documentType) { this.documentType = documentType; }
    
    public String getDocumentName() { return documentName; }
    public void setDocumentName(String documentName) { this.documentName = documentName; }
    
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public DocumentStatus getStatus() { return status; }
    public void setStatus(DocumentStatus status) { this.status = status; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
    
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
}
