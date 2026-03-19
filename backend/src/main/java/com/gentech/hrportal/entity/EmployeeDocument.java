package com.gentech.hrportal.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "employee_documents")
public class EmployeeDocument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;
    
    @Column(name = "document_name", nullable = false)
    private String documentName;
    
    @Column(name = "document_url", nullable = false)
    private String documentUrl;
    
    @Column(name = "upload_date")
    private LocalDate uploadDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DocumentStatus status;
    
    @Column(name = "admin_comments", columnDefinition = "TEXT")
    private String adminComments;
    
    @ManyToOne
    @JoinColumn(name = "approved_by")
    private User approvedBy;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public EmployeeDocument() {
        this.status = DocumentStatus.PENDING;
    }
    
    public EmployeeDocument(Long id, User employee, DocumentType documentType, String documentName, 
                           String documentUrl, LocalDate uploadDate, DocumentStatus status, String adminComments,
                           User approvedBy, LocalDateTime approvedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.employee = employee;
        this.documentType = documentType;
        this.documentName = documentName;
        this.documentUrl = documentUrl;
        this.uploadDate = uploadDate;
        this.status = status;
        this.adminComments = adminComments;
        this.approvedBy = approvedBy;
        this.approvedAt = approvedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        uploadDate = LocalDate.now();
        if (status == null) {
            status = DocumentStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }
    
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
    
    public User getApprovedBy() { return approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }
    
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long id;
        private User employee;
        private DocumentType documentType;
        private String documentName;
        private String documentUrl;
        private LocalDate uploadDate;
        private DocumentStatus status;
        private String adminComments;
        private User approvedBy;
        private LocalDateTime approvedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(Long id) { this.id = id; return this; }
        public Builder employee(User employee) { this.employee = employee; return this; }
        public Builder documentType(DocumentType documentType) { this.documentType = documentType; return this; }
        public Builder documentName(String documentName) { this.documentName = documentName; return this; }
        public Builder documentUrl(String documentUrl) { this.documentUrl = documentUrl; return this; }
        public Builder uploadDate(LocalDate uploadDate) { this.uploadDate = uploadDate; return this; }
        public Builder status(DocumentStatus status) { this.status = status; return this; }
        public Builder adminComments(String adminComments) { this.adminComments = adminComments; return this; }
        public Builder approvedBy(User approvedBy) { this.approvedBy = approvedBy; return this; }
        public Builder approvedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public EmployeeDocument build() {
            return new EmployeeDocument(id, employee, documentType, documentName, documentUrl, uploadDate, 
                status, adminComments, approvedBy, approvedAt, createdAt, updatedAt);
        }
    }
    
    public enum DocumentType {
        JOINING_LETTER,
        RESIGNATION_LETTER,
        ID_PROOF,
        ADDRESS_PROOF,
        EDUCATION_CERTIFICATE,
        EXPERIENCE_CERTIFICATE,
        OTHER
    }
    
    public enum DocumentStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
