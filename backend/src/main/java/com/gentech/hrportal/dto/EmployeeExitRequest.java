package com.gentech.hrportal.dto;

import java.time.LocalDate;

public class EmployeeExitRequest {
    
    private String reason;
    private Integer noticePeriodDays;
    private LocalDate proposedLastWorkingDate;
    
    public EmployeeExitRequest() {}
    
    public EmployeeExitRequest(String reason, Integer noticePeriodDays, LocalDate proposedLastWorkingDate) {
        this.reason = reason;
        this.noticePeriodDays = noticePeriodDays;
        this.proposedLastWorkingDate = proposedLastWorkingDate;
    }
    
    // Getters and Setters
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public Integer getNoticePeriodDays() { return noticePeriodDays; }
    public void setNoticePeriodDays(Integer noticePeriodDays) { this.noticePeriodDays = noticePeriodDays; }
    
    public LocalDate getProposedLastWorkingDate() { return proposedLastWorkingDate; }
    public void setProposedLastWorkingDate(LocalDate proposedLastWorkingDate) { this.proposedLastWorkingDate = proposedLastWorkingDate; }
}
