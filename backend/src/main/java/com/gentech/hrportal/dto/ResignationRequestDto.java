package com.gentech.hrportal.dto;

import java.time.LocalDate;

public class ResignationRequestDto {
    
    private LocalDate proposedLastWorkingDay;
    private String reason;
    
    public ResignationRequestDto() {}
    
    public ResignationRequestDto(LocalDate proposedLastWorkingDay, String reason) {
        this.proposedLastWorkingDay = proposedLastWorkingDay;
        this.reason = reason;
    }
    
    // Getters and Setters
    public LocalDate getProposedLastWorkingDay() { return proposedLastWorkingDay; }
    public void setProposedLastWorkingDay(LocalDate proposedLastWorkingDay) { this.proposedLastWorkingDay = proposedLastWorkingDay; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
