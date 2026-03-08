package com.gentech.hrportal.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "salary_slips")
public class SalarySlip {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private User employee;
    
    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Column(nullable = false)
    private Integer month;
    
    @Column(nullable = false)
    private Integer year;
    
    @Column(name = "basic_salary", nullable = false)
    private Double basicSalary;
    
    @Column(name = "hra")
    private Double hra;
    
    @Column(name = "da")
    private Double da;
    
    @Column(name = "special_allowance")
    private Double specialAllowance;
    
    @Column(name = "conveyance")
    private Double conveyance;
    
    @Column(name = "medical")
    private Double medical;
    
    @Column(name = "other_allowances")
    private Double otherAllowances;
    
    @Column(name = "bonus")
    private Double bonus;
    
    @Column(name = "gross_salary", nullable = false)
    private Double grossSalary;
    
    @Column(name = "pf")
    private Double pf;
    
    @Column(name = "professional_tax")
    private Double professionalTax;
    
    @Column(name = "income_tax")
    private Double incomeTax;
    
    @Column(name = "other_deductions")
    private Double otherDeductions;
    
    @Column(name = "total_deductions", nullable = false)
    private Double totalDeductions;
    
    @Column(name = "net_salary", nullable = false)
    private Double netSalary;
    
    @Column(name = "generated_date")
    private LocalDateTime generatedDate;
    
    @ManyToOne
    @JoinColumn(name = "generated_by")
    private User generatedBy;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalarySlipStatus status;
    
    @Column(name = "pdf_url")
    private String pdfUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public SalarySlip() {
        this.status = SalarySlipStatus.GENERATED;
    }
    
    public SalarySlip(Long id, User employee, Company company, Integer month, Integer year,
                      Double basicSalary, Double hra, Double da, Double specialAllowance,
                      Double conveyance, Double medical, Double otherAllowances, Double bonus,
                      Double grossSalary, Double pf, Double professionalTax, Double incomeTax,
                      Double otherDeductions, Double totalDeductions, Double netSalary,
                      LocalDateTime generatedDate, User generatedBy, SalarySlipStatus status,
                      String pdfUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.employee = employee;
        this.company = company;
        this.month = month;
        this.year = year;
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.da = da;
        this.specialAllowance = specialAllowance;
        this.conveyance = conveyance;
        this.medical = medical;
        this.otherAllowances = otherAllowances;
        this.bonus = bonus;
        this.grossSalary = grossSalary;
        this.pf = pf;
        this.professionalTax = professionalTax;
        this.incomeTax = incomeTax;
        this.otherDeductions = otherDeductions;
        this.totalDeductions = totalDeductions;
        this.netSalary = netSalary;
        this.generatedDate = generatedDate;
        this.generatedBy = generatedBy;
        this.status = status;
        this.pdfUrl = pdfUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = SalarySlipStatus.GENERATED;
        }
        if (generatedDate == null) {
            generatedDate = LocalDateTime.now();
        }
        calculateSalary();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateSalary();
    }
    
    private void calculateSalary() {
        // Calculate gross salary
        double hraValue = hra != null ? hra : 0.0;
        double daValue = da != null ? da : 0.0;
        double specialAllowanceValue = specialAllowance != null ? specialAllowance : 0.0;
        double conveyanceValue = conveyance != null ? conveyance : 0.0;
        double medicalValue = medical != null ? medical : 0.0;
        double otherAllowancesValue = otherAllowances != null ? otherAllowances : 0.0;
        double bonusValue = bonus != null ? bonus : 0.0;
        
        grossSalary = basicSalary + hraValue + daValue + specialAllowanceValue + 
                      conveyanceValue + medicalValue + otherAllowancesValue + bonusValue;
        
        // Calculate total deductions
        double pfValue = pf != null ? pf : 0.0;
        double professionalTaxValue = professionalTax != null ? professionalTax : 0.0;
        double incomeTaxValue = incomeTax != null ? incomeTax : 0.0;
        double otherDeductionsValue = otherDeductions != null ? otherDeductions : 0.0;
        
        totalDeductions = pfValue + professionalTaxValue + incomeTaxValue + otherDeductionsValue;
        
        // Calculate net salary
        netSalary = grossSalary - totalDeductions;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getEmployee() { return employee; }
    public void setEmployee(User employee) { this.employee = employee; }
    
    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }
    
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public Double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(Double basicSalary) { this.basicSalary = basicSalary; }
    
    public Double getHra() { return hra; }
    public void setHra(Double hra) { this.hra = hra; }
    
    public Double getDa() { return da; }
    public void setDa(Double da) { this.da = da; }
    
    public Double getSpecialAllowance() { return specialAllowance; }
    public void setSpecialAllowance(Double specialAllowance) { this.specialAllowance = specialAllowance; }
    
    public Double getConveyance() { return conveyance; }
    public void setConveyance(Double conveyance) { this.conveyance = conveyance; }
    
    public Double getMedical() { return medical; }
    public void setMedical(Double medical) { this.medical = medical; }
    
    public Double getOtherAllowances() { return otherAllowances; }
    public void setOtherAllowances(Double otherAllowances) { this.otherAllowances = otherAllowances; }
    
    public Double getBonus() { return bonus; }
    public void setBonus(Double bonus) { this.bonus = bonus; }
    
    public Double getGrossSalary() { return grossSalary; }
    public void setGrossSalary(Double grossSalary) { this.grossSalary = grossSalary; }
    
    public Double getPf() { return pf; }
    public void setPf(Double pf) { this.pf = pf; }
    
    public Double getProfessionalTax() { return professionalTax; }
    public void setProfessionalTax(Double professionalTax) { this.professionalTax = professionalTax; }
    
    public Double getIncomeTax() { return incomeTax; }
    public void setIncomeTax(Double incomeTax) { this.incomeTax = incomeTax; }
    
    public Double getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(Double otherDeductions) { this.otherDeductions = otherDeductions; }
    
    public Double getTotalDeductions() { return totalDeductions; }
    public void setTotalDeductions(Double totalDeductions) { this.totalDeductions = totalDeductions; }
    
    public Double getNetSalary() { return netSalary; }
    public void setNetSalary(Double netSalary) { this.netSalary = netSalary; }
    
    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }
    
    public User getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(User generatedBy) { this.generatedBy = generatedBy; }
    
    public SalarySlipStatus getStatus() { return status; }
    public void setStatus(SalarySlipStatus status) { this.status = status; }
    
    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    
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
        private Company company;
        private Integer month;
        private Integer year;
        private Double basicSalary;
        private Double hra;
        private Double da;
        private Double specialAllowance;
        private Double conveyance;
        private Double medical;
        private Double otherAllowances;
        private Double bonus;
        private Double grossSalary;
        private Double pf;
        private Double professionalTax;
        private Double incomeTax;
        private Double otherDeductions;
        private Double totalDeductions;
        private Double netSalary;
        private LocalDateTime generatedDate;
        private User generatedBy;
        private SalarySlipStatus status;
        private String pdfUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Builder id(Long id) { this.id = id; return this; }
        public Builder employee(User employee) { this.employee = employee; return this; }
        public Builder company(Company company) { this.company = company; return this; }
        public Builder month(Integer month) { this.month = month; return this; }
        public Builder year(Integer year) { this.year = year; return this; }
        public Builder basicSalary(Double basicSalary) { this.basicSalary = basicSalary; return this; }
        public Builder hra(Double hra) { this.hra = hra; return this; }
        public Builder da(Double da) { this.da = da; return this; }
        public Builder specialAllowance(Double specialAllowance) { this.specialAllowance = specialAllowance; return this; }
        public Builder conveyance(Double conveyance) { this.conveyance = conveyance; return this; }
        public Builder medical(Double medical) { this.medical = medical; return this; }
        public Builder otherAllowances(Double otherAllowances) { this.otherAllowances = otherAllowances; return this; }
        public Builder bonus(Double bonus) { this.bonus = bonus; return this; }
        public Builder grossSalary(Double grossSalary) { this.grossSalary = grossSalary; return this; }
        public Builder pf(Double pf) { this.pf = pf; return this; }
        public Builder professionalTax(Double professionalTax) { this.professionalTax = professionalTax; return this; }
        public Builder incomeTax(Double incomeTax) { this.incomeTax = incomeTax; return this; }
        public Builder otherDeductions(Double otherDeductions) { this.otherDeductions = otherDeductions; return this; }
        public Builder totalDeductions(Double totalDeductions) { this.totalDeductions = totalDeductions; return this; }
        public Builder netSalary(Double netSalary) { this.netSalary = netSalary; return this; }
        public Builder generatedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; return this; }
        public Builder generatedBy(User generatedBy) { this.generatedBy = generatedBy; return this; }
        public Builder status(SalarySlipStatus status) { this.status = status; return this; }
        public Builder pdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        
        public SalarySlip build() {
            return new SalarySlip(id, employee, company, month, year, basicSalary, hra, da, 
                specialAllowance, conveyance, medical, otherAllowances, bonus, grossSalary, pf, 
                professionalTax, incomeTax, otherDeductions, totalDeductions, netSalary, 
                generatedDate, generatedBy, status, pdfUrl, createdAt, updatedAt);
        }
    }
    
    public enum SalarySlipStatus {
        GENERATED,
        SENT,
        DOWNLOADED
    }
}
