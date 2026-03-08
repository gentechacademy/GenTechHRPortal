package com.gentech.hrportal.service;

import com.gentech.hrportal.entity.EmployeeProfile;
import com.gentech.hrportal.entity.Leave;
import com.gentech.hrportal.entity.Project;
import com.gentech.hrportal.entity.SalarySlip;
import com.gentech.hrportal.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:hrportal@gentech.com}")
    private String fromEmail;
    
    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;
    
    @Value("${app.company.name:GenTech Solutions}")
    private String companyName;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    
    /**
     * Send email notification when leave is applied for > 3 days
     */
    public void sendLeaveApplicationNotification(Leave leave, User manager, User admin) {
        String subject = String.format("Leave Application - %s - %d days", 
            leave.getEmployee().getFullName(), leave.getNumberOfDays());
        
        String body = buildLeaveApplicationEmailBody(leave);
        
        // Send to manager if available
        if (manager != null && manager.getEmail() != null) {
            sendEmail(manager.getEmail(), subject, body);
        }
        
        // Send to admin if available
        if (admin != null && admin.getEmail() != null) {
            sendEmail(admin.getEmail(), subject, body);
        }
    }
    
    /**
     * Send email notification to employee when leave is approved
     */
    public void sendLeaveApprovalNotification(Leave leave) {
        String subject = String.format("Leave Approved - %s to %s", 
            leave.getStartDate().format(DATE_FORMATTER),
            leave.getEndDate().format(DATE_FORMATTER));
        
        String body = buildLeaveApprovalEmailBody(leave);
        
        sendEmail(leave.getEmployee().getEmail(), subject, body);
    }
    
    /**
     * Send email notification to employee when leave is rejected
     */
    public void sendLeaveRejectionNotification(Leave leave) {
        String subject = String.format("Leave Rejected - %s to %s", 
            leave.getStartDate().format(DATE_FORMATTER),
            leave.getEndDate().format(DATE_FORMATTER));
        
        String body = buildLeaveRejectionEmailBody(leave);
        
        sendEmail(leave.getEmployee().getEmail(), subject, body);
    }
    
    /**
     * Send email notification when profile edit is requested
     */
    public void sendProfileEditRequestNotification(Object request, User admin) {
        String subject = "Profile Edit Request - Action Required";
        String body = buildProfileEditRequestEmailBody(request);
        
        if (admin != null && admin.getEmail() != null) {
            sendEmail(admin.getEmail(), subject, body);
        }
    }
    
    /**
     * Generic method to send email
     */
    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
        } catch (Exception e) {
            // Log error but don't throw - email failure shouldn't break the flow
            System.err.println("❌ Failed to send email to " + to + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String buildLeaveApplicationEmailBody(Leave leave) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear Manager/Admin,\n\n");
        sb.append("A new leave application has been submitted that requires your attention.\n\n");
        sb.append("Employee Details:\n");
        sb.append("- Name: ").append(leave.getEmployee().getFullName()).append("\n");
        sb.append("- Employee ID: ").append(leave.getEmployee().getId()).append("\n");
        sb.append("- Company: ").append(leave.getCompany().getName()).append("\n\n");
        sb.append("Leave Details:\n");
        sb.append("- Type: ").append(leave.getLeaveType()).append("\n");
        sb.append("- Start Date: ").append(leave.getStartDate().format(DATE_FORMATTER)).append("\n");
        sb.append("- End Date: ").append(leave.getEndDate().format(DATE_FORMATTER)).append("\n");
        sb.append("- Number of Days: ").append(leave.getNumberOfDays()).append("\n");
        sb.append("- Reason: ").append(leave.getReason() != null ? leave.getReason() : "N/A").append("\n\n");
        sb.append("Please review and take appropriate action.\n\n");
        sb.append("Best regards,\n");
        sb.append("HR Portal Team");
        return sb.toString();
    }
    
    private String buildLeaveApprovalEmailBody(Leave leave) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(leave.getEmployee().getFullName()).append(",\n\n");
        sb.append("Your leave application has been APPROVED.\n\n");
        sb.append("Leave Details:\n");
        sb.append("- Type: ").append(leave.getLeaveType()).append("\n");
        sb.append("- Start Date: ").append(leave.getStartDate().format(DATE_FORMATTER)).append("\n");
        sb.append("- End Date: ").append(leave.getEndDate().format(DATE_FORMATTER)).append("\n");
        sb.append("- Number of Days: ").append(leave.getNumberOfDays()).append("\n");
        if (leave.getApprovedBy() != null) {
            sb.append("- Approved By: ").append(leave.getApprovedBy().getFullName()).append("\n");
        }
        sb.append("- Approved On: ").append(leave.getApprovalDate().format(DATE_FORMATTER)).append("\n\n");
        sb.append("Best regards,\n");
        sb.append("HR Portal Team");
        return sb.toString();
    }
    
    private String buildLeaveRejectionEmailBody(Leave leave) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(leave.getEmployee().getFullName()).append(",\n\n");
        sb.append("We regret to inform you that your leave application has been REJECTED.\n\n");
        sb.append("Leave Details:\n");
        sb.append("- Type: ").append(leave.getLeaveType()).append("\n");
        sb.append("- Start Date: ").append(leave.getStartDate().format(DATE_FORMATTER)).append("\n");
        sb.append("- End Date: ").append(leave.getEndDate().format(DATE_FORMATTER)).append("\n");
        sb.append("- Number of Days: ").append(leave.getNumberOfDays()).append("\n\n");
        sb.append("Reason for Rejection:\n");
        sb.append(leave.getRejectionReason() != null ? leave.getRejectionReason() : "No reason provided").append("\n\n");
        if (leave.getApprovedBy() != null) {
            sb.append("- Rejected By: ").append(leave.getApprovedBy().getFullName()).append("\n");
        }
        sb.append("\nBest regards,\n");
        sb.append("HR Portal Team");
        return sb.toString();
    }
    
    private String buildProfileEditRequestEmailBody(Object request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear Admin,\n\n");
        sb.append("A profile edit request has been submitted and requires your review.\n\n");
        sb.append("Please login to the HR Portal to review and approve/reject the request.\n\n");
        sb.append("Best regards,\n");
        sb.append("HR Portal Team");
        return sb.toString();
    }
    
    /**
     * Send salary slip email with PDF attachment
     * @param salarySlip the salary slip entity
     * @param pdfBytes the PDF file as byte array
     */
    public void sendSalarySlipEmail(SalarySlip salarySlip, byte[] pdfBytes) {
        try {
            String monthName = getMonthName(salarySlip.getMonth());
            String subject = String.format("Salary Slip for %s %d", 
                monthName, salarySlip.getYear());
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(salarySlip.getEmployee().getEmail());
            helper.setSubject(subject);
            helper.setText(buildSalarySlipEmailBody(salarySlip), false);
            
            // Attach PDF
            String filename = String.format("Salary-Slip-%s-%s-%d.pdf", 
                salarySlip.getEmployee().getFullName().replace(" ", "-"),
                monthName,
                salarySlip.getYear());
            
            helper.addAttachment(filename, () -> new java.io.ByteArrayInputStream(pdfBytes));
            
            mailSender.send(message);
            
        } catch (MessagingException e) {
            System.err.println("Failed to send salary slip email: " + e.getMessage());
        }
    }
    
    private String getMonthName(int month) {
        String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
        return months[month - 1];
    }
    
    private String buildSalarySlipEmailBody(SalarySlip salarySlip) {
        String monthName = getMonthName(salarySlip.getMonth());
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(salarySlip.getEmployee().getFullName()).append(",\n\n");
        sb.append("Your salary slip for ").append(monthName)
          .append(" ").append(salarySlip.getYear()).append(" is now available.\n\n");
        
        sb.append("PAYSLIP SUMMARY:\n");
        sb.append("================\n");
        sb.append(String.format("%-25s: Rs. %,.2f%n", "Gross Salary", salarySlip.getGrossSalary()));
        sb.append(String.format("%-25s: Rs. %,.2f%n", "Total Deductions", salarySlip.getTotalDeductions()));
        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-25s: Rs. %,.2f%n", "NET PAY", salarySlip.getNetSalary()));
        sb.append("\n");
        
        sb.append("Please find your detailed salary slip attached as a PDF.\n\n");
        sb.append("For any queries regarding your salary, please contact the HR department.\n\n");
        sb.append("Best regards,\n");
        sb.append("HR Portal Team\n");
        sb.append(companyName);
        return sb.toString();
    }

    /**
     * Send password reset OTP email
     * @param toEmail recipient email address
     * @param fullName recipient's full name
     * @param otp the 6-digit OTP
     * @param expiryMinutes OTP expiry time in minutes
     */
    public void sendPasswordResetOtp(String toEmail, String fullName, String otp, int expiryMinutes) {
        String subject = "Password Reset OTP - " + companyName;
        String body = buildPasswordResetOtpEmailBody(fullName, otp, expiryMinutes);
        sendEmail(toEmail, subject, body);
    }

    private String buildPasswordResetOtpEmailBody(String fullName, String otp, int expiryMinutes) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(fullName).append(",\n\n");
        sb.append("You have requested to reset your password for the HR Portal.\n\n");
        sb.append("Your One-Time Password (OTP) for password reset is:\n\n");
        sb.append("========================================\n");
        sb.append("              ").append(otp).append("\n");
        sb.append("========================================\n\n");
        sb.append("This OTP will expire in ").append(expiryMinutes).append(" minutes.\n\n");
        sb.append("If you did not request this password reset, please ignore this email or contact your administrator immediately.\n\n");
        sb.append("For security reasons, please do not share this OTP with anyone.\n\n");
        sb.append("Best regards,\n");
        sb.append("HR Portal Team\n");
        sb.append(companyName);
        return sb.toString();
    }

    /**
     * Send welcome email to new employee with login credentials
     * @param user the new employee user
     * @param plainPassword the plain text password (auto-generated or provided)
     * @param profile the employee profile
     */
    public void sendWelcomeEmail(User user, String plainPassword, EmployeeProfile profile) {
        String subject = "Welcome to " + companyName + " - Your Account Details";
        String body = buildWelcomeEmailBody(user, plainPassword, profile);
        sendEmail(user.getEmail(), subject, body);
    }

    private String buildWelcomeEmailBody(User user, String plainPassword, EmployeeProfile profile) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(user.getFullName()).append(",\n\n");
        sb.append("Welcome to ").append(companyName).append("!\n\n");
        sb.append("Your employee account has been successfully created. Below are your login credentials and employment details:\n\n");
        
        sb.append("========================================\n");
        sb.append("      LOGIN CREDENTIALS\n");
        sb.append("========================================\n");
        sb.append("Portal URL: ").append(frontendUrl).append("\n");
        sb.append("Username: ").append(user.getUsername()).append("\n");
        sb.append("Password: ").append(plainPassword).append("\n");
        sb.append("Role: ").append(user.getRole()).append("\n\n");
        
        sb.append("========================================\n");
        sb.append("      EMPLOYMENT DETAILS\n");
        sb.append("========================================\n");
        if (profile.getEmployeeCode() != null && !profile.getEmployeeCode().isEmpty()) {
            sb.append("Employee ID: ").append(profile.getEmployeeCode()).append("\n");
        }
        sb.append("Full Name: ").append(user.getFullName()).append("\n");
        sb.append("Email: ").append(user.getEmail()).append("\n");
        if (profile.getDepartment() != null && !profile.getDepartment().isEmpty()) {
            sb.append("Department: ").append(profile.getDepartment()).append("\n");
        }
        if (profile.getDesignation() != null && !profile.getDesignation().isEmpty()) {
            sb.append("Designation: ").append(profile.getDesignation()).append("\n");
        }
        if (profile.getDateOfJoining() != null) {
            sb.append("Date of Joining: ").append(profile.getDateOfJoining()).append("\n");
        }
        if (user.getCompany() != null) {
            sb.append("Company: ").append(user.getCompany().getName()).append("\n");
        }
        sb.append("\n");
        
        sb.append("========================================\n");
        sb.append("      IMPORTANT SECURITY NOTICE\n");
        sb.append("========================================\n");
        sb.append("1. Please login and change your password immediately.\n");
        sb.append("2. Do not share your login credentials with anyone.\n");
        sb.append("3. If you did not request this account, please contact HR immediately.\n\n");
        
        sb.append("For any assistance, please contact the HR department.\n\n");
        sb.append("Best regards,\n");
        sb.append("HR Portal Team\n");
        sb.append(companyName);
        return sb.toString();
    }

    /**
     * Send team assignment email to employee
     * @param employee the assigned employee
     * @param project the project assigned to
     * @param roleInProject the role in the project
     */
    public void sendTeamAssignmentEmail(User employee, Project project, String roleInProject) {
        String subject = "Project Assignment - " + project.getName() + " - " + companyName;
        String body = buildTeamAssignmentEmailBody(employee, project, roleInProject);
        sendEmail(employee.getEmail(), subject, body);
    }

    private String buildTeamAssignmentEmailBody(User employee, Project project, String roleInProject) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ").append(employee.getFullName()).append(",\n\n");
        sb.append("You have been assigned to a new project!\n\n");
        
        sb.append("========================================\n");
        sb.append("      PROJECT DETAILS\n");
        sb.append("========================================\n");
        sb.append("Project Name: ").append(project.getName()).append("\n");
        if (project.getDescription() != null && !project.getDescription().isEmpty()) {
            sb.append("Description: ").append(project.getDescription()).append("\n");
        }
        if (roleInProject != null && !roleInProject.isEmpty()) {
            sb.append("Your Role: ").append(roleInProject).append("\n");
        }
        sb.append("Project Manager: ").append(project.getManager().getFullName()).append("\n");
        if (project.getStartDate() != null) {
            sb.append("Start Date: ").append(project.getStartDate()).append("\n");
        }
        if (project.getEndDate() != null) {
            sb.append("End Date: ").append(project.getEndDate()).append("\n");
        }
        sb.append("Status: ").append(project.getStatus()).append("\n\n");
        
        sb.append("Please login to the HR Portal to view more details about this project.\n\n");
        sb.append("Portal URL: ").append(frontendUrl).append("\n\n");
        
        sb.append("Best regards,\n");
        sb.append("HR Portal Team\n");
        sb.append(companyName);
        return sb.toString();
    }
}
