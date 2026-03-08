package com.gentech.hrportal.dto;

public class LoginResponse {
    private String token;
    private String type;
    private String username;
    private String role;
    private String fullName;
    private Long userId;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, String type, String username, String role, String fullName, Long userId) {
        this.token = token;
        this.type = type;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
        this.userId = userId;
    }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String token;
        private String type;
        private String username;
        private String role;
        private String fullName;
        private Long userId;
        
        public Builder token(String token) { this.token = token; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        
        public LoginResponse build() {
            return new LoginResponse(token, type, username, role, fullName, userId);
        }
    }
}
