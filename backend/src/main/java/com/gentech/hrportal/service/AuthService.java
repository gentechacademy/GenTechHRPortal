package com.gentech.hrportal.service;

import com.gentech.hrportal.dto.*;
import com.gentech.hrportal.entity.PasswordResetToken;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.PasswordResetTokenRepository;
import com.gentech.hrportal.repository.UserRepository;
import com.gentech.hrportal.security.JwtUtils;
import com.gentech.hrportal.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Value("${app.otp.expiry.minutes:15}")
    private int otpExpiryMinutes;

    public LoginResponse authenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return LoginResponse.builder()
                .token(jwt)
                .type("Bearer")
                .username(userDetails.getUsername())
                .role(userDetails.getRole())
                .fullName(userDetails.getFullName())
                .userId(userDetails.getId())
                .build();
    }

    public void createSuperAdmin() {
        if (!userRepository.existsByUsername("superadmin")) {
            User superAdmin = User.builder()
                    .username("superadmin")
                    .password(encoder.encode("superadmin123"))
                    .email("superadmin@gentech.com")
                    .fullName("Super Administrator")
                    .role(User.Role.SUPER_ADMIN)
                    .build();
            userRepository.save(superAdmin);
        }
    }

    @Transactional
    public void initiatePasswordReset(ForgotPasswordRequest request) {
        System.out.println("🔍 Password reset requested for username: " + request.getUsername());
        
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            System.out.println("✅ User found: " + user.getUsername() + ", Email: " + user.getEmail());
            
            // Delete any existing tokens for this user (using deleteByUser if available)
            Optional<PasswordResetToken> existingToken = tokenRepository.findByUser(user);
            if (existingToken.isPresent()) {
                tokenRepository.delete(existingToken.get());
                tokenRepository.flush(); // Force immediate deletion
                System.out.println("🗑️ Deleted existing token for user: " + user.getUsername());
            }
            
            // Generate unique 6-digit OTP with timestamp to ensure uniqueness
            String otp = generateUniqueOtp();
            System.out.println("🔢 Generated OTP: " + otp + " for user: " + user.getUsername());
            
            // Create new token
            LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(otpExpiryMinutes);
            PasswordResetToken token = new PasswordResetToken(otp, user, expiryDate);
            tokenRepository.save(token);
            System.out.println("💾 OTP token saved to database");
            
            // Send OTP email (fetched from user's registered email)
            System.out.println("📧 Sending OTP email to: " + user.getEmail());
            emailService.sendPasswordResetOtp(user.getEmail(), user.getFullName(), otp, otpExpiryMinutes);
            System.out.println("📧 OTP email sending initiated");
        } else {
            System.out.println("❌ User not found: " + request.getUsername());
        }
        // Always return success to prevent username enumeration
    }

    public boolean verifyOtp(VerifyOtpRequest request) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(request.getOtp());
        
        if (tokenOpt.isEmpty()) {
            return false;
        }
        
        PasswordResetToken token = tokenOpt.get();
        
        // Verify the token belongs to the correct user (by username)
        if (!token.getUser().getUsername().equals(request.getUsername())) {
            return false;
        }
        
        if (token.isExpired() || token.isUsed()) {
            return false;
        }
        
        return true;
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(request.getOtp());
        
        if (tokenOpt.isEmpty()) {
            throw new BadCredentialsException("Invalid OTP");
        }
        
        PasswordResetToken token = tokenOpt.get();
        
        // Verify the token belongs to the correct user (by username)
        if (!token.getUser().getUsername().equals(request.getUsername())) {
            throw new BadCredentialsException("Invalid OTP");
        }
        
        if (token.isExpired()) {
            throw new BadCredentialsException("OTP has expired. Please request a new one.");
        }
        
        if (token.isUsed()) {
            throw new BadCredentialsException("OTP has already been used. Please request a new one.");
        }
        
        // Update password
        User user = token.getUser();
        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        // Mark token as used
        token.setUsed(true);
        tokenRepository.save(token);
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit number
        return String.valueOf(otp);
    }

    private String generateUniqueOtp() {
        String otp;
        do {
            otp = generateOtp();
        } while (tokenRepository.findByToken(otp).isPresent()); // Ensure uniqueness
        return otp;
    }
}
