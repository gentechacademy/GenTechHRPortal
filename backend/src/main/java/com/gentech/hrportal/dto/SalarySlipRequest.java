package com.gentech.hrportal.dto;

public class SalarySlipRequest {
    
    private Long employeeId;
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
    private Double pf;
    private Double professionalTax;
    private Double incomeTax;
    private Double otherDeductions;
    
    public SalarySlipRequest() {}
    
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
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
    
    public Double getPf() { return pf; }
    public void setPf(Double pf) { this.pf = pf; }
    
    public Double getProfessionalTax() { return professionalTax; }
    public void setProfessionalTax(Double professionalTax) { this.professionalTax = professionalTax; }
    
    public Double getIncomeTax() { return incomeTax; }
    public void setIncomeTax(Double incomeTax) { this.incomeTax = incomeTax; }
    
    public Double getOtherDeductions() { return otherDeductions; }
    public void setOtherDeductions(Double otherDeductions) { this.otherDeductions = otherDeductions; }
}
