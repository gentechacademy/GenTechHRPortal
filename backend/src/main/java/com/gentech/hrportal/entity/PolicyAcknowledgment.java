package com.gentech.hrportal.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "policy_acknowledgments")
public class PolicyAcknowledgment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private HRPolicy policy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AcknowledgmentStatus status;
    
    private LocalDateTime assignedAt;
    
    private LocalDateTime acknowledgedAt;
    
    private String signature;
    
    private String ipAddress;
    
    private String remarks;
    
    public enum AcknowledgmentStatus { PENDING, ACKNOWLEDGED, OVERDUE }
    
    public PolicyAcknowledgment() {}
    
    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
        if (status == null) {
            status = AcknowledgmentStatus.PENDING;
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public HRPolicy getPolicy() { return policy; }
    public void setPolicy(HRPolicy policy) { this.policy = policy; }
    
    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public AcknowledgmentStatus getStatus() { return status; }
    public void setStatus(AcknowledgmentStatus status) { this.status = status; }
    
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
    
    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
    
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
