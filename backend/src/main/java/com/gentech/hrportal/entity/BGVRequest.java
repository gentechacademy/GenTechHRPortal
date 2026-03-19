package com.gentech.hrportal.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bgv_requests")
public class BGVRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by")
    private User initiatedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BGVStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeType employeeType;
    
    private String remarks;
    
    private LocalDateTime initiatedAt;
    
    private LocalDateTime submittedAt;
    
    private LocalDateTime verifiedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;
    
    @OneToMany(mappedBy = "bgvRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BGVDocument> documents = new ArrayList<>();
    
    public enum BGVStatus {
        PENDING, IN_PROGRESS, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED, PARTIAL_APPROVED
    }
    
    public enum EmployeeType {
        FRESHER, EXPERIENCED
    }
    
    public BGVRequest() {}
    
    @PrePersist
    protected void onCreate() {
        initiatedAt = LocalDateTime.now();
        if (status == null) {
            status = BGVStatus.PENDING;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public User getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(User initiatedBy) { this.initiatedBy = initiatedBy; }
    
    public BGVStatus getStatus() { return status; }
    public void setStatus(BGVStatus status) { this.status = status; }
    
    public EmployeeType getEmployeeType() { return employeeType; }
    public void setEmployeeType(EmployeeType employeeType) { this.employeeType = employeeType; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    
    public LocalDateTime getInitiatedAt() { return initiatedAt; }
    public void setInitiatedAt(LocalDateTime initiatedAt) { this.initiatedAt = initiatedAt; }
    
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    
    public User getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(User verifiedBy) { this.verifiedBy = verifiedBy; }
    
    public List<BGVDocument> getDocuments() { return documents; }
    public void setDocuments(List<BGVDocument> documents) { this.documents = documents; }
}
