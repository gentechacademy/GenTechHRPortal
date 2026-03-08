package com.gentech.hrportal.dto;

public class BonusRequestDto {
    
    private Long employeeId;
    private Double amount;
    private String reason;
    private Integer month;
    private Integer year;
    
    public BonusRequestDto() {}
    
    public BonusRequestDto(Long employeeId, Double amount, String reason, Integer month, Integer year) {
        this.employeeId = employeeId;
        this.amount = amount;
        this.reason = reason;
        this.month = month;
        this.year = year;
    }
    
    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}
