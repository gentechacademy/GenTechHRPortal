package com.gentech.hrportal.config;

import com.gentech.hrportal.entity.Company;
import com.gentech.hrportal.entity.EmployeeProfile;
import com.gentech.hrportal.entity.SalarySlip;
import com.gentech.hrportal.entity.User;
import com.gentech.hrportal.repository.CompanyRepository;
import com.gentech.hrportal.repository.EmployeeProfileRepository;
import com.gentech.hrportal.repository.SalarySlipRepository;
import com.gentech.hrportal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class TestDataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SalarySlipRepository salarySlipRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Create GenTech Solutions company
        Company genTech = companyRepository.findByName("GenTech Solutions").orElse(null);
        if (genTech == null) {
            genTech = new Company();
            genTech.setName("GenTech Solutions");
            genTech.setAddress("123 Tech Street, Silicon Valley, CA 94025");
            genTech.setPhone("+1-555-123-4567");
            genTech.setEmail("info@gentech.com");
            genTech = companyRepository.save(genTech);
        }

        // Create FaceBook company
        Company faceBook = companyRepository.findByName("FaceBook").orElse(null);
        if (faceBook == null) {
            faceBook = new Company();
            faceBook.setName("FaceBook");
            faceBook.setAddress("FB Street, Menlo Park, CA 94025");
            faceBook.setPhone("+1-555-FACEBOOK");
            faceBook.setEmail("info@facebook.com");
            faceBook = companyRepository.save(faceBook);
        }

        // Create test admin for GenTech
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@gentech.com");
            admin.setFullName("Test Administrator");
            admin.setRole(User.Role.ADMIN);
            admin.setCompany(genTech);
            userRepository.save(admin);
        }

        // Create Ranjeet (admin1) for FaceBook
        if (!userRepository.existsByUsername("admin1")) {
            User ranjeet = new User();
            ranjeet.setUsername("admin1");
            ranjeet.setPassword(passwordEncoder.encode("admin123"));
            ranjeet.setEmail("shivaranjeetm@abx.com");
            ranjeet.setFullName("Ranjeet");
            ranjeet.setRole(User.Role.ADMIN);
            ranjeet.setCompany(faceBook);
            userRepository.save(ranjeet);
        }

        // Create test employees for GenTech
        createTestEmployee("developer1", "developer123", "developer1@gentech.com", "John Developer", 
                User.Role.DEVELOPER, genTech, "DEV001", "Engineering", "Software Developer");
        
        // Create 3 additional Developers
        createTestEmployeeWithPhoto("developer2", "developer123", "developer2@gentech.com", "Sarah Developer",
                User.Role.DEVELOPER, genTech, "DEV002", "Engineering", "Frontend Developer",
                "/uploads/profile-pictures/21820968-66dc-4dec-9ab4-cb7a2cc77c80.jpg", "+1-555-100-2002");
        
        createTestEmployeeWithPhoto("developer3", "developer123", "developer3@gentech.com", "Mike Developer",
                User.Role.DEVELOPER, genTech, "DEV003", "Engineering", "Backend Developer",
                "/uploads/profile-pictures/3840fd8c-ee76-48cb-826b-b67e84c96ad2.jpg", "+1-555-100-3003");
        
        createTestEmployeeWithPhoto("developer4", "developer123", "developer4@gentech.com", "Emily Developer",
                User.Role.DEVELOPER, genTech, "DEV004", "Engineering", "Full Stack Developer",
                "/uploads/profile-pictures/3a15be8d-f0f9-4032-a7b5-4f2d1f5cd4b3.jpg", "+1-555-100-4004");
        
        createTestEmployee("hr1", "hr123", "hr1@gentech.com", "Jane HR", 
                User.Role.HR, genTech, "HR001", "Human Resources", "HR Executive");
        
        createTestEmployee("manager1", "manager123", "manager1@gentech.com", "Bob Manager", 
                User.Role.MANAGER, genTech, "MGR001", "Engineering", "Engineering Manager");
        
        createTestEmployee("hrmanager1", "hrmanager123", "hrmanager1@gentech.com", "Alice HR Manager", 
                User.Role.HR_MANAGER, genTech, "HRM001", "Human Resources", "HR Manager");
        
        createTestEmployee("engineer1", "engineer123", "engineer1@gentech.com", "Tom Engineer", 
                User.Role.SOFTWARE_ENGINEER, genTech, "SE001", "Engineering", "Senior Software Engineer");
        
        // Create 3 additional Software Engineers
        createTestEmployeeWithPhoto("engineer2", "engineer123", "engineer2@gentech.com", "Lisa Engineer",
                User.Role.SOFTWARE_ENGINEER, genTech, "SE002", "Engineering", "Software Engineer",
                "/uploads/profile-pictures/42d54052-56a1-4e46-953c-d266ba3d3b5c.jpg", "+1-555-200-2002");
        
        createTestEmployeeWithPhoto("engineer3", "engineer123", "engineer3@gentech.com", "David Engineer",
                User.Role.SOFTWARE_ENGINEER, genTech, "SE003", "Engineering", "Senior Software Engineer",
                "/uploads/profile-pictures/43e47c21-bd45-4e5f-8686-ee25bc1dcfa9.jpg", "+1-555-200-3003");
        
        createTestEmployeeWithPhoto("engineer4", "engineer123", "engineer4@gentech.com", "Anna Engineer",
                User.Role.SOFTWARE_ENGINEER, genTech, "SE004", "Engineering", "Software Engineer",
                "/uploads/profile-pictures/46b7f22f-026d-4300-ba1a-f60f79c98239.jpg", "+1-555-200-4004");
        
        createTestEmployee("gm1", "gm123", "gm1@gentech.com", "General Manager", 
                User.Role.GENERAL_MANAGER, genTech, "GM001", "Management", "General Manager");
        
        // Create emp12 for testing forgot password
        createTestEmployee("emp12", "emp123", "shivaranjeetm@gmail.com", "Test Employee 12", 
                User.Role.DEVELOPER, genTech, "EMP012", "Engineering", "Test Developer");
        
        // Fix: Ensure all GenTech employees have company assigned (for existing data)
        fixEmployeeCompanyAssignments(genTech);

        // Create salary slips for test employees
        createTestSalarySlips();

        System.out.println("✅ Test data initialized successfully!");
        System.out.println("📋 Available test accounts:");
        System.out.println("   Super Admin: superadmin / superadmin123");
        System.out.println("   Admin (GenTech): admin / admin123");
        System.out.println("   Admin (FaceBook): admin1 / admin123");
        System.out.println("   Developer: developer1, developer2, developer3, developer4 / developer123");
        System.out.println("   HR: hr1 / hr123");
        System.out.println("   Manager: manager1 / manager123");
        System.out.println("   HR Manager: hrmanager1 / hrmanager123");
        System.out.println("   Software Engineer: engineer1, engineer2, engineer3, engineer4 / engineer123");
        System.out.println("   General Manager: gm1 / gm123");
    }

    private void createTestSalarySlips() {
        // Get employees and create salary slips for them
        String[] usernames = {"developer1", "developer2", "developer3", "developer4",
                "hr1", "manager1", "hrmanager1", "engineer1", "engineer2", "engineer3", "engineer4", "gm1"};
        int currentYear = LocalDate.now().getYear();
        int currentMonth = LocalDate.now().getMonthValue();
        
        for (String username : usernames) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user == null || user.getCompany() == null) continue;
            
            // Create salary slips for last 3 months
            for (int i = 0; i < 3; i++) {
                int month = currentMonth - i;
                int year = currentYear;
                if (month <= 0) {
                    month += 12;
                    year -= 1;
                }
                
                // Skip if already exists
                if (salarySlipRepository.existsByEmployeeIdAndMonthAndYear(user.getId(), month, year)) {
                    continue;
                }
                
                SalarySlip slip = new SalarySlip();
                slip.setEmployee(user);
                slip.setCompany(user.getCompany());
                slip.setMonth(month);
                slip.setYear(year);
                
                // Set earnings based on role
                double basicSalary = 50000.0;
                if (username.equals("gm1")) basicSalary = 100000.0;
                else if (username.equals("manager1") || username.equals("hrmanager1")) basicSalary = 75000.0;
                else if (username.equals("engineer1")) basicSalary = 60000.0;
                
                slip.setBasicSalary(basicSalary);
                slip.setHra(basicSalary * 0.4);
                slip.setDa(basicSalary * 0.2);
                slip.setSpecialAllowance(basicSalary * 0.15);
                slip.setConveyance(5000.0);
                slip.setMedical(5000.0);
                slip.setOtherAllowances(3000.0);
                
                // Set deductions
                slip.setPf(basicSalary * 0.12);
                slip.setProfessionalTax(200.0);
                slip.setIncomeTax(basicSalary * 0.1);
                slip.setOtherDeductions(0.0);
                
                // Calculate totals
                double grossSalary = basicSalary + slip.getHra() + slip.getDa() + slip.getSpecialAllowance() 
                        + slip.getConveyance() + slip.getMedical() + slip.getOtherAllowances();
                double totalDeductions = slip.getPf() + slip.getProfessionalTax() + slip.getIncomeTax() + slip.getOtherDeductions();
                double netSalary = grossSalary - totalDeductions;
                
                slip.setGrossSalary(grossSalary);
                slip.setTotalDeductions(totalDeductions);
                slip.setNetSalary(netSalary);
                slip.setStatus(SalarySlip.SalarySlipStatus.GENERATED);
                slip.setGeneratedDate(LocalDateTime.now());
                
                salarySlipRepository.save(slip);
                System.out.println("   💰 Created salary slip for " + username + " - " + month + "/" + year);
            }
        }
        System.out.println("✅ Test salary slips created successfully!");
    }

    private void createTestEmployee(String username, String password, String email, String fullName, 
                                     User.Role role, Company company, String empCode, String department, String designation) {
        if (!userRepository.existsByUsername(username)) {
            // Create User
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setFullName(fullName);
            user.setRole(role);
            user.setCompany(company);
            User savedUser = userRepository.save(user);

            // Create Employee Profile
            EmployeeProfile profile = new EmployeeProfile();
            profile.setUser(savedUser);
            profile.setCompany(company);
            profile.setEmployeeCode(empCode);
            profile.setDepartment(department);
            profile.setDesignation(designation);
            profile.setDateOfJoining(LocalDate.now().minusMonths(6));
            profile.setDateOfBirth(LocalDate.of(1990, 1, 15));
            profile.setPhoneNumber("+1-555-0000");
            profile.setAddress("123 Employee St, City, Country");
            profile.setEmergencyContact("+1-555-9999");
            profile.setSalary(75000.0);
            employeeProfileRepository.save(profile);
        }
    }

    private void createTestEmployeeWithPhoto(String username, String password, String email, String fullName,
                                              User.Role role, Company company, String empCode, String department, 
                                              String designation, String profilePictureUrl, String phoneNumber) {
        if (!userRepository.existsByUsername(username)) {
            // Create User
            User user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(password));
            user.setEmail(email);
            user.setFullName(fullName);
            user.setRole(role);
            user.setCompany(company);
            User savedUser = userRepository.save(user);

            // Create Employee Profile with photo
            EmployeeProfile profile = new EmployeeProfile();
            profile.setUser(savedUser);
            profile.setCompany(company);
            profile.setEmployeeCode(empCode);
            profile.setDepartment(department);
            profile.setDesignation(designation);
            profile.setDateOfJoining(LocalDate.now().minusMonths(6));
            profile.setDateOfBirth(LocalDate.of(1990, 1, 15));
            profile.setPhoneNumber(phoneNumber);
            profile.setAddress("123 Employee St, City, Country");
            profile.setEmergencyContact("+1-555-9999");
            profile.setSalary(75000.0);
            profile.setProfilePictureUrl(profilePictureUrl);
            employeeProfileRepository.save(profile);
        }
    }
    
    private void fixEmployeeCompanyAssignments(Company company) {
        System.out.println("🔧 Running fixEmployeeCompanyAssignments...");
        // Fix users who don't have a company assigned or don't have employee profile
        String[][] employees = {
            {"developer1", "developer123", "developer1@gentech.com", "John Developer", "DEVELOPER", "DEV001", "Engineering", "Software Developer", "50000"},
            {"developer2", "developer123", "developer2@gentech.com", "Sarah Developer", "DEVELOPER", "DEV002", "Engineering", "Frontend Developer", "55000"},
            {"developer3", "developer123", "developer3@gentech.com", "Mike Developer", "DEVELOPER", "DEV003", "Engineering", "Backend Developer", "55000"},
            {"developer4", "developer123", "developer4@gentech.com", "Emily Developer", "DEVELOPER", "DEV004", "Engineering", "Full Stack Developer", "60000"},
            {"hr1", "hr123", "hr1@gentech.com", "Jane HR", "HR", "HR001", "Human Resources", "HR Executive", "50000"},
            {"manager1", "manager123", "manager1@gentech.com", "Bob Manager", "MANAGER", "MGR001", "Engineering", "Engineering Manager", "75000"},
            {"hrmanager1", "hrmanager123", "hrmanager1@gentech.com", "Alice HR Manager", "HR_MANAGER", "HRM001", "Human Resources", "HR Manager", "75000"},
            {"engineer1", "engineer123", "engineer1@gentech.com", "Tom Engineer", "SOFTWARE_ENGINEER", "SE001", "Engineering", "Senior Software Engineer", "60000"},
            {"engineer2", "engineer123", "engineer2@gentech.com", "Lisa Engineer", "SOFTWARE_ENGINEER", "SE002", "Engineering", "Software Engineer", "55000"},
            {"engineer3", "engineer123", "engineer3@gentech.com", "David Engineer", "SOFTWARE_ENGINEER", "SE003", "Engineering", "Senior Software Engineer", "65000"},
            {"engineer4", "engineer123", "engineer4@gentech.com", "Anna Engineer", "SOFTWARE_ENGINEER", "SE004", "Engineering", "Software Engineer", "55000"},
            {"gm1", "gm123", "gm1@gentech.com", "General Manager", "GENERAL_MANAGER", "GM001", "Management", "General Manager", "100000"}
        };
        
        for (String[] emp : employees) {
            String username = emp[0];
            User user = userRepository.findByUsername(username).orElse(null);
            
            if (user != null) {
                // Fix company if null
                if (user.getCompany() == null) {
                    user.setCompany(company);
                    userRepository.save(user);
                    System.out.println("   🔧 Fixed company for: " + username);
                }
                
                // Create employee profile if doesn't exist
                if (!employeeProfileRepository.findByUserId(user.getId()).isPresent()) {
                    EmployeeProfile profile = new EmployeeProfile();
                    profile.setUser(user);
                    profile.setCompany(company);
                    profile.setEmployeeCode(emp[5]);
                    profile.setDepartment(emp[6]);
                    profile.setDesignation(emp[7]);
                    profile.setDateOfJoining(LocalDate.now().minusMonths(6));
                    profile.setDateOfBirth(LocalDate.of(1990, 1, 15));
                    profile.setPhoneNumber("+1-555-0000");
                    profile.setAddress("123 Employee St, City, Country");
                    profile.setEmergencyContact("+1-555-9999");
                    profile.setSalary(Double.parseDouble(emp[8]));
                    employeeProfileRepository.save(profile);
                    System.out.println("   🔧 Created profile for: " + username);
                }
            }
        }
    }
}
