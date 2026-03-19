package com.gentech.hrportal.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for employee search request
 * searchTerm can be employee ID (employeeCode) or name
 */
public class EmployeeSearchRequest {
    
    @NotBlank(message = "Search term is required")
    private String searchTerm;
    
    public EmployeeSearchRequest() {}
    
    public EmployeeSearchRequest(String searchTerm) {
        this.searchTerm = searchTerm;
    }
    
    public String getSearchTerm() {
        return searchTerm;
    }
    
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }
}
