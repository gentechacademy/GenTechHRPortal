package com.gentech.hrportal.dto;

public class BGVInitiateRequest {
    
    private Long employeeId;
    private String employeeType; // FRESHER or EXPERIENCED
    private String remarks;
    
    public BGVInitiateRequest() {}
    
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public String getEmployeeType() { return employeeType; }
    public void setEmployeeType(String employeeType) { this.employeeType = employeeType; }
    
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
