package com.gentech.hrportal.dto;

import jakarta.validation.constraints.NotBlank;

public class VerifyOtpRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "OTP is required")
    private String otp;
    
    public VerifyOtpRequest() {}
    
    public VerifyOtpRequest(String username, String otp) {
        this.username = username;
        this.otp = otp;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getOtp() {
        return otp;
    }
    
    public void setOtp(String otp) {
        this.otp = otp;
    }
}
