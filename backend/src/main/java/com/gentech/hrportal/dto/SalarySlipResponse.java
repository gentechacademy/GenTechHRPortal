package com.gentech.hrportal.dto;

import com.gentech.hrportal.entity.SalarySlip;
import com.gentech.hrportal.entity.SalarySlip.SalarySlipStatus;

import java.time.LocalDateTime;

public class SalarySlipResponse {
    
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeCode;
    private Long companyId;
    private String companyName;
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
    private Long generatedById;
    private String generatedByName;
    private SalarySlipStatus status;
    private String pdfUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public SalarySlipResponse() {}
    
    public SalarySlipResponse(SalarySlip salarySlip) {
        this.id = salarySlip.getId();
        this.month = salarySlip.getMonth();
        this.year = salarySlip.getYear();
        this.basicSalary = salarySlip.getBasicSalary();
        this.hra = salarySlip.getHra();
        this.da = salarySlip.getDa();
        this.specialAllowance = salarySlip.getSpecialAllowance();
        this.conveyance = salarySlip.getConveyance();
        this.medical = salarySlip.getMedical();
        this.otherAllowances = salarySlip.getOtherAllowances();
        this.bonus = salarySlip.getBonus();
        this.grossSalary = salarySlip.getGrossSalary();
        this.pf = salarySlip.getPf();
        this.professionalTax = salarySlip.getProfessionalTax();
        this.incomeTax = salarySlip.getIncomeTax();
        this.otherDeductions = salarySlip.getOtherDeductions();
        this.totalDeductions = salarySlip.getTotalDeductions();
        this.netSalary = salarySlip.getNetSalary();
        this.generatedDate = salarySlip.getGeneratedDate();
        this.status = salarySlip.getStatus();
        this.pdfUrl = salarySlip.getPdfUrl();
        this.createdAt = salarySlip.getCreatedAt();
        this.updatedAt = salarySlip.getUpdatedAt();
        
        if (salarySlip.getEmployee() != null) {
            this.employeeId = salarySlip.getEmployee().getId();
            this.employeeName = salarySlip.getEmployee().getFullName();
            // Note: employeeCode would need to be fetched from EmployeeProfile if available
            this.employeeCode = "EMP-" + salarySlip.getEmployee().getId(); // Default code
        }
        
        if (salarySlip.getCompany() != null) {
            this.companyId = salarySlip.getCompany().getId();
            this.companyName = salarySlip.getCompany().getName();
        }
        
        if (salarySlip.getGeneratedBy() != null) {
            this.generatedById = salarySlip.getGeneratedBy().getId();
            this.generatedByName = salarySlip.getGeneratedBy().getFullName();
        }
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    
    public String getEmployeeCode() { return employeeCode; }
    public void setEmployeeCode(String employeeCode) { this.employeeCode = employeeCode; }
    
    public Long getCompanyId() { return companyId; }
    public void setCompanyId(Long companyId) { this.companyId = companyId; }
    
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    
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
    
    public Long getGeneratedById() { return generatedById; }
    public void setGeneratedById(Long generatedById) { this.generatedById = generatedById; }
    
    public String getGeneratedByName() { return generatedByName; }
    public void setGeneratedByName(String generatedByName) { this.generatedByName = generatedByName; }
    
    public SalarySlipStatus getStatus() { return status; }
    public void setStatus(SalarySlipStatus status) { this.status = status; }
    
    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
