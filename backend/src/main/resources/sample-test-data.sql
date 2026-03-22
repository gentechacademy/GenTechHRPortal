-- =============================================================================
-- GenTech HR Portal - Comprehensive Sample Test Data
-- Run this script to populate your database with test data for all features
-- =============================================================================

-- =============================================================================
-- 1. COMPANIES
-- =============================================================================
INSERT INTO companies (name, logo_url, address, phone, email, created_at) VALUES
('GenTech Solutions Pvt Ltd', 'https://example.com/logos/gentech.png', '123 Tech Park, Bangalore, Karnataka 560001', '+91-80-12345678', 'info@gentech.com', NOW()),
('InnovateSoft Technologies', 'https://example.com/logos/innovate.png', '456 Innovation Hub, Hyderabad, Telangana 500032', '+91-40-87654321', 'contact@innovatesoft.com', NOW()),
('CloudFirst Systems', 'https://example.com/logos/cloudfirst.png', '789 Cloud Avenue, Pune, Maharashtra 411001', '+91-20-11223344', 'hello@cloudfirst.io', NOW())
ON CONFLICT (name) DO NOTHING;

-- =============================================================================
-- 2. USERS (Passwords are BCrypt encoded for 'password123')
-- Note: BCrypt hash for 'password123' - $2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq
-- =============================================================================

-- Super Admin
INSERT INTO users (username, password, email, full_name, role, company_id, created_at, updated_at) VALUES
('superadmin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'superadmin@gentech.com', 'Super Administrator', 'SUPER_ADMIN', NULL, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- Company 1 Admins and Employees
INSERT INTO users (username, password, email, full_name, role, company_id, created_at, updated_at) VALUES
-- Admins
('admin1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'admin@gentech.com', 'Rahul Sharma', 'ADMIN', 1, NOW(), NOW()),
('hr1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'hr@gentech.com', 'Priya Patel', 'HR', 1, NOW(), NOW()),
('hrmgr1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'hrmgr@gentech.com', 'Anita Desai', 'HR_MANAGER', 1, NOW(), NOW()),

-- Managers
('manager1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'manager1@gentech.com', 'Vikram Singh', 'MANAGER', 1, NOW(), NOW()),
('manager2', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'manager2@gentech.com', 'Deepa Gupta', 'MANAGER', 1, NOW(), NOW()),
('gmanager1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'gm@gentech.com', 'Rajesh Kumar', 'GENERAL_MANAGER', 1, NOW(), NOW()),

-- Employees
('dev1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'dev1@gentech.com', 'Amit Kumar', 'SOFTWARE_ENGINEER', 1, NOW(), NOW()),
('dev2', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'dev2@gentech.com', 'Sneha Reddy', 'SOFTWARE_ENGINEER', 1, NOW(), NOW()),
('dev3', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'dev3@gentech.com', 'Rohan Mehta', 'DEVELOPER', 1, NOW(), NOW()),
('dev4', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'dev4@gentech.com', 'Neha Sharma', 'DEVELOPER', 1, NOW(), NOW()),
('dev5', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'dev5@gentech.com', 'Suresh Iyer', 'SOFTWARE_ENGINEER', 1, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- Company 2 Users
INSERT INTO users (username, password, email, full_name, role, company_id, created_at, updated_at) VALUES
('admin2', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'admin@innovatesoft.com', 'Sanjay Verma', 'ADMIN', 2, NOW(), NOW()),
('mgr2', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'mgr@innovatesoft.com', 'Kavita Rao', 'MANAGER', 2, NOW(), NOW()),
('emp2_1', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'emp1@innovatesoft.com', 'Arun Nair', 'DEVELOPER', 2, NOW(), NOW()),
('emp2_2', '$2a$10$N9qo8uLOickgx2ZMRZoMy.MqrqQzBZN0UfGNEsKYGs/6t8VjX7fXq', 'emp2@innovatesoft.com', 'Lakshmi N', 'SOFTWARE_ENGINEER', 2, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- =============================================================================
-- 3. EMPLOYEE PROFILES
-- =============================================================================
INSERT INTO employee_profiles (user_id, company_id, employee_code, department, designation, date_of_joining, date_of_birth, phone_number, address, emergency_contact, salary, profile_picture_url, created_at, updated_at) VALUES
-- Company 1 Profiles
((SELECT id FROM users WHERE username='admin1'), 1, 'GT001', 'Administration', 'System Administrator', '2020-01-15', '1985-03-20', '+91-9876543210', 'Bangalore, Karnataka', '+91-9876543201', 120000.00, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='hr1'), 1, 'GT002', 'Human Resources', 'HR Executive', '2020-03-10', '1988-07-15', '+91-9876543211', 'Bangalore, Karnataka', '+91-9876543202', 80000.00, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='hrmgr1'), 1, 'GT003', 'Human Resources', 'HR Manager', '2019-06-01', '1982-11-25', '+91-9876543212', 'Bangalore, Karnataka', '+91-9876543203', 150000.00, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='manager1'), 1, 'GT004', 'Engineering', 'Engineering Manager', '2018-04-20', '1980-05-10', '+91-9876543213', 'Bangalore, Karnataka', '+91-9876543204', 200000.00, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='manager2'), 1, 'GT005', 'Engineering', 'Team Lead', '2019-08-15', '1986-09-18', '+91-9876543214', 'Bangalore, Karnataka', '+91-9876543205', 180000.00, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='gmanager1'), 1, 'GT006', 'Management', 'General Manager', '2017-01-10', '1978-12-05', '+91-9876543215', 'Bangalore, Karnataka', '+91-9876543206', 300000.00, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='dev1'), 1, 'GT007', 'Engineering', 'Software Engineer', '2021-06-01', '1995-04-12', '+91-9876543216', 'Bangalore, Karnataka', '+91-9876543207', 75000.00, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='dev2'), 1, 'GT008', 'Engineering', 'Software Engineer', '2022-01-10', '1996-08-22', '+91-9876543217', 'Bangalore, Karnataka', '+91-9876543208', 70000.00, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='dev3'), 1, 'GT009', 'Engineering', 'Junior Developer', '2022-07-15', '1998-02-14', '+91-9876543218', 'Bangalore, Karnataka', '+91-9876543209', 50000.00, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='dev4'), 1, 'GT010', 'Engineering', 'Junior Developer', '2023-01-20', '1999-06-30', '+91-9876543219', 'Bangalore, Karnataka', '+91-9876543210', 48000.00, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='dev5'), 1, 'GT011', 'Engineering', 'Senior Developer', '2020-09-01', '1992-10-08', '+91-9876543220', 'Bangalore, Karnataka', '+91-9876543211', 95000.00, NULL, NOW(), NOW())
ON CONFLICT (employee_code) DO NOTHING;

-- =============================================================================
-- 4. ATTENDANCE RECORDS
-- =============================================================================
INSERT INTO attendance (employee_id, company_id, attendance_date, check_in_time, check_out_time, working_hours, status, approval_status, approved_by, approval_date, notes, created_at, updated_at) VALUES
-- dev1 attendance
((SELECT id FROM users WHERE username='dev1'), 1, CURRENT_DATE, '09:00:00', '18:00:00', 8.0, 'PRESENT', 'APPROVED', (SELECT id FROM users WHERE username='manager1'), NOW(), 'Regular working day', NOW(), NOW()),
((SELECT id FROM users WHERE username='dev1'), 1, CURRENT_DATE - 1, '09:15:00', '18:30:00', 8.25, 'PRESENT', 'APPROVED', (SELECT id FROM users WHERE username='manager1'), NOW(), 'Late by 15 mins', NOW(), NOW()),
((SELECT id FROM users WHERE username='dev1'), 1, CURRENT_DATE - 2, '09:00:00', '17:00:00', 7.0, 'HALF_DAY', 'APPROVED', (SELECT id FROM users WHERE username='manager1'), NOW(), 'Half day for personal work', NOW(), NOW()),
((SELECT id FROM users WHERE username='dev1'), 1, CURRENT_DATE - 3, '10:00:00', '19:00:00', 8.0, 'WORK_FROM_HOME', 'APPROVED', (SELECT id FROM users WHERE username='manager1'), NOW(), 'WFH approved', NOW(), NOW()),
((SELECT id FROM users WHERE username='dev1'), 1, CURRENT_DATE - 5, NULL, NULL, 0.0, 'ON_LEAVE', 'APPROVED', (SELECT id FROM users WHERE username='manager1'), NOW(), 'Sick leave', NOW(), NOW()),

-- dev2 attendance
((SELECT id FROM users WHERE username='dev2'), 1, CURRENT_DATE, '09:30:00', '18:30:00', 8.0, 'PRESENT', 'PENDING', NULL, NULL, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='dev2'), 1, CURRENT_DATE - 1, '09:00:00', '18:00:00', 8.0, 'PRESENT', 'APPROVED', (SELECT id FROM users WHERE username='manager1'), NOW(), 'Regular day', NOW(), NOW()),
((SELECT id FROM users WHERE username='dev2'), 1, CURRENT_DATE - 2, NULL, NULL, 0.0, 'ON_LEAVE', 'PENDING', NULL, NULL, 'Casual leave', NOW(), NOW()),

-- dev3 attendance (pending approval)
((SELECT id FROM users WHERE username='dev3'), 1, CURRENT_DATE, '08:45:00', NULL, NULL, 'PRESENT', 'PENDING', NULL, NULL, 'Still checked in', NOW(), NOW()),
((SELECT id FROM users WHERE username='dev3'), 1, CURRENT_DATE - 1, '09:00:00', '18:00:00', 8.0, 'PRESENT', 'PENDING', NULL, NULL, NULL, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 5. LEAVE REQUESTS
-- =============================================================================
INSERT INTO leaves (employee_id, company_id, leave_type, start_date, end_date, number_of_days, reason, status, applied_by, approved_by, approval_date, rejection_reason, created_at, updated_at) VALUES
-- Pending leaves
((SELECT id FROM users WHERE username='dev1'), 1, 'PL', CURRENT_DATE + 7, CURRENT_DATE + 10, 4, 'Family vacation planned', 'PENDING', (SELECT id FROM users WHERE username='dev1'), NULL, NULL, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='dev2'), 1, 'CL', CURRENT_DATE + 3, CURRENT_DATE + 3, 1, 'Personal work', 'PENDING', (SELECT id FROM users WHERE username='dev2'), NULL, NULL, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='dev3'), 1, 'SL', CURRENT_DATE + 1, CURRENT_DATE + 2, 2, 'Fever and cold', 'PENDING', (SELECT id FROM users WHERE username='dev3'), NULL, NULL, NULL, NOW(), NOW()),

-- Approved leaves
((SELECT id FROM users WHERE username='dev4'), 1, 'EL', CURRENT_DATE - 15, CURRENT_DATE - 10, 6, 'Long vacation', 'APPROVED', (SELECT id FROM users WHERE username='dev4'), (SELECT id FROM users WHERE username='manager2'), NOW() - INTERVAL '20 days', NULL, NOW() - INTERVAL '21 days', NOW()),
((SELECT id FROM users WHERE username='dev5'), 1, 'CL', CURRENT_DATE - 5, CURRENT_DATE - 5, 1, 'Bank work', 'APPROVED', (SELECT id FROM users WHERE username='dev5'), (SELECT id FROM users WHERE username='manager1'), NOW() - INTERVAL '7 days', NULL, NOW() - INTERVAL '8 days', NOW()),

-- Rejected leaves
((SELECT id FROM users WHERE username='dev1'), 1, 'PL', CURRENT_DATE + 20, CURRENT_DATE + 25, 6, 'International trip', 'REJECTED', (SELECT id FROM users WHERE username='dev1'), (SELECT id FROM users WHERE username='manager1'), NOW() - INTERVAL '2 days', 'Project deadline conflict', NOW() - INTERVAL '5 days', NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 6. PROFILE EDIT REQUESTS
-- =============================================================================
INSERT INTO profile_edit_requests (employee_id, company_id, field_name, old_value, new_value, status, requested_by, approved_by, approval_date, rejection_reason, created_at, updated_at) VALUES
-- Pending requests
((SELECT id FROM users WHERE username='dev1'), 1, 'phoneNumber', '+91-9876543216', '+91-9999999999', 'PENDING', (SELECT id FROM users WHERE username='dev1'), NULL, NULL, NULL, NOW() - INTERVAL '2 days', NOW()),
((SELECT id FROM users WHERE username='dev2'), 1, 'address', 'Bangalore, Karnataka', 'Hyderabad, Telangana', 'PENDING', (SELECT id FROM users WHERE username='dev2'), NULL, NULL, NULL, NOW() - INTERVAL '1 day', NOW()),
((SELECT id FROM users WHERE username='dev3'), 1, 'emergencyContact', '+91-9876543209', '+91-8888888888', 'PENDING', (SELECT id FROM users WHERE username='dev3'), NULL, NULL, NULL, NOW(), NOW()),

-- Approved requests
((SELECT id FROM users WHERE username='dev4'), 1, 'phoneNumber', '+91-9876543219', '+91-7777777777', 'APPROVED', (SELECT id FROM users WHERE username='dev4'), (SELECT id FROM users WHERE username='hr1'), NOW() - INTERVAL '5 days', NULL, NOW() - INTERVAL '7 days', NOW()),
((SELECT id FROM users WHERE username='dev5'), 1, 'address', 'Bangalore, Karnataka', 'Chennai, Tamil Nadu', 'APPROVED', (SELECT id FROM users WHERE username='dev5'), (SELECT id FROM users WHERE username='hr1'), NOW() - INTERVAL '3 days', NULL, NOW() - INTERVAL '4 days', NOW()),

-- Rejected requests
((SELECT id FROM users WHERE username='dev1'), 1, 'salary', '75000', '100000', 'REJECTED', (SELECT id FROM users WHERE username='dev1'), (SELECT id FROM users WHERE username='hrmgr1'), NOW() - INTERVAL '10 days', 'Salary changes not allowed through profile edit', NOW() - INTERVAL '12 days', NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 7. BONUS REQUESTS
-- =============================================================================
INSERT INTO bonus_requests (employee_id, company_id, amount, reason, month, year, status, requested_by, approved_by, requested_date, approval_date, rejection_reason, created_at, updated_at) VALUES
-- Pending bonus requests
((SELECT id FROM users WHERE username='dev1'), 1, 15000.00, 'Outstanding performance in Q1 project delivery', 3, 2026, 'PENDING', (SELECT id FROM users WHERE username='manager1'), NULL, NOW() - INTERVAL '5 days', NULL, NULL, NOW() - INTERVAL '5 days', NOW()),
((SELECT id FROM users WHERE username='dev2'), 1, 10000.00, 'Excellence in client presentation', 3, 2026, 'PENDING', (SELECT id FROM users WHERE username='manager1'), NULL, NOW() - INTERVAL '3 days', NULL, NULL, NOW() - INTERVAL '3 days', NOW()),
((SELECT id FROM users WHERE username='dev3'), 1, 5000.00, 'Completing certification on time', 2, 2026, 'PENDING', (SELECT id FROM users WHERE username='manager2'), NULL, NOW() - INTERVAL '2 days', NULL, NULL, NOW() - INTERVAL '2 days', NOW()),

-- Approved bonus requests
((SELECT id FROM users WHERE username='dev4'), 1, 20000.00, 'Successful project completion ahead of schedule', 2, 2026, 'APPROVED', (SELECT id FROM users WHERE username='manager2'), (SELECT id FROM users WHERE username='hrmgr1'), NOW() - INTERVAL '15 days', NOW() - INTERVAL '10 days', NULL, NOW() - INTERVAL '15 days', NOW()),
((SELECT id FROM users WHERE username='dev5'), 1, 25000.00, 'Mentoring new team members', 1, 2026, 'APPROVED', (SELECT id FROM users WHERE username='manager1'), (SELECT id FROM users WHERE username='hrmgr1'), NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days', NULL, NOW() - INTERVAL '30 days', NOW()),

-- Rejected bonus requests
((SELECT id FROM users WHERE username='dev1'), 1, 50000.00, 'Requesting bonus for personal reasons', 2, 2026, 'REJECTED', (SELECT id FROM users WHERE username='dev1'), (SELECT id FROM users WHERE username='hrmgr1'), NOW() - INTERVAL '20 days', NOW() - INTERVAL '18 days', 'Insufficient justification for bonus', NOW() - INTERVAL '20 days', NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 8. SALARY SLIPS
-- =============================================================================
INSERT INTO salary_slips (employee_id, company_id, month, year, basic_salary, hra, da, special_allowance, conveyance, medical, other_allowances, bonus, gross_salary, pf, professional_tax, income_tax, other_deductions, total_deductions, net_salary, generated_date, generated_by, status, created_at, updated_at) VALUES
-- dev1 salary slips
((SELECT id FROM users WHERE username='dev1'), 1, 1, 2026, 40000.00, 16000.00, 8000.00, 5000.00, 2000.00, 1500.00, 2500.00, 0.00, 75000.00, 4800.00, 200.00, 2500.00, 0.00, 7500.00, 67500.00, NOW() - INTERVAL '45 days', (SELECT id FROM users WHERE username='admin1'), 'SENT', NOW() - INTERVAL '45 days', NOW()),
((SELECT id FROM users WHERE username='dev1'), 1, 2, 2026, 40000.00, 16000.00, 8000.00, 5000.00, 2000.00, 1500.00, 2500.00, 10000.00, 85000.00, 4800.00, 200.00, 3500.00, 0.00, 8500.00, 76500.00, NOW() - INTERVAL '15 days', (SELECT id FROM users WHERE username='admin1'), 'SENT', NOW() - INTERVAL '15 days', NOW()),
((SELECT id FROM users WHERE username='dev1'), 1, 3, 2026, 40000.00, 16000.00, 8000.00, 5000.00, 2000.00, 1500.00, 2500.00, 0.00, 75000.00, 4800.00, 200.00, 2500.00, 0.00, 7500.00, 67500.00, NOW(), (SELECT id FROM users WHERE username='admin1'), 'GENERATED', NOW(), NOW()),

-- dev2 salary slips
((SELECT id FROM users WHERE username='dev2'), 1, 3, 2026, 35000.00, 14000.00, 7000.00, 4500.00, 2000.00, 1500.00, 2000.00, 0.00, 66000.00, 4200.00, 200.00, 2000.00, 0.00, 6400.00, 59600.00, NOW(), (SELECT id FROM users WHERE username='admin1'), 'GENERATED', NOW(), NOW()),

-- dev5 salary slips (with approved bonus)
((SELECT id FROM users WHERE username='dev5'), 1, 1, 2026, 50000.00, 20000.00, 10000.00, 8000.00, 3000.00, 2000.00, 2000.00, 25000.00, 120000.00, 6000.00, 200.00, 8000.00, 0.00, 14200.00, 105800.00, NOW() - INTERVAL '30 days', (SELECT id FROM users WHERE username='admin1'), 'SENT', NOW() - INTERVAL '30 days', NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 9. RESIGNATION REQUESTS
-- =============================================================================
INSERT INTO resignation_requests (employee_id, company_id, request_date, proposed_last_working_day, actual_last_working_day, notice_period_days, reason, status, manager_remarks, admin_remarks, manager_approved_by, admin_approved_by, manager_approval_date, admin_approval_date, created_at, updated_at) VALUES
-- Pending manager approval
((SELECT id FROM users WHERE username='dev3'), 1, CURRENT_DATE - 5, CURRENT_DATE + 25, NULL, 30, 'Got a better opportunity with higher compensation', 'PENDING_MANAGER', NULL, NULL, NULL, NULL, NULL, NULL, NOW() - INTERVAL '5 days', NOW()),

-- Manager approved, pending admin
((SELECT id FROM users WHERE username='dev4'), 1, CURRENT_DATE - 10, CURRENT_DATE + 20, NULL, 30, 'Relocating to different city for family reasons', 'MANAGER_APPROVED', 'Employee has been valuable to the team. Recommend approval.', NULL, (SELECT id FROM users WHERE username='manager2'), NULL, NOW() - INTERVAL '7 days', NULL, NOW() - INTERVAL '10 days', NOW()),

-- Fully approved
((SELECT id FROM users WHERE username='dev2'), 1, CURRENT_DATE - 45, CURRENT_DATE - 15, CURRENT_DATE - 15, 30, 'Higher studies abroad', 'APPROVED', 'All knowledge transfer completed. Good employee.', 'Approved. All exit formalities completed.', (SELECT id FROM users WHERE username='manager1'), (SELECT id FROM users WHERE username='admin1'), NOW() - INTERVAL '40 days', NOW() - INTERVAL '15 days', NOW() - INTERVAL '45 days', NOW()),

-- Rejected
((SELECT id FROM users WHERE username='dev1'), 1, CURRENT_DATE - 20, CURRENT_DATE + 10, NULL, 30, 'Personal reasons', 'REJECTED', 'Critical project phase. Cannot release at this time.', NULL, (SELECT id FROM users WHERE username='manager1'), NULL, NOW() - INTERVAL '18 days', NULL, NOW() - INTERVAL '20 days', NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 10. HR POLICIES
-- =============================================================================
INSERT INTO hr_policies (policy_name, policy_code, description, file_url, file_name, policy_type, category, active, created_at, updated_at) VALUES
('Code of Conduct Policy', 'POL-COC-001', 'Guidelines for professional behavior and ethical standards expected from all employees.', 'https://example.com/policies/code-of-conduct.pdf', 'code-of-conduct.pdf', 'POLICY', 'CODE_OF_CONDUCT', true, NOW(), NOW()),
('POSH Policy', 'POL-POSH-001', 'Prevention of Sexual Harassment at Workplace Policy as per the Sexual Harassment of Women at Workplace Act, 2013.', 'https://example.com/policies/posh-policy.pdf', 'posh-policy.pdf', 'POLICY', 'POSH', true, NOW(), NOW()),
('Data Protection Policy', 'POL-DP-001', 'Guidelines for handling sensitive company and customer data in compliance with data privacy regulations.', 'https://example.com/policies/data-protection.pdf', 'data-protection.pdf', 'POLICY', 'DATA_PROTECTION', true, NOW(), NOW()),
('Integrity Policy', 'POL-INT-001', 'Employee integrity and anti-bribery policy for maintaining ethical business practices.', 'https://example.com/policies/integrity-policy.pdf', 'integrity-policy.pdf', 'POLICY', 'INTEGRITY', true, NOW(), NOW()),
('PF Nomination Form', 'FORM-PF-001', 'Provident Fund nomination form for employees to declare their nominees.', 'https://example.com/forms/pf-nomination.pdf', 'pf-nomination.pdf', 'FORM', 'PF_NOMINATION', true, NOW(), NOW()),
('Gratuity Nomination Form', 'FORM-GRAT-001', 'Gratuity nomination form for declaring beneficiaries.', 'https://example.com/forms/gratuity-nomination.pdf', 'gratuity-nomination.pdf', 'FORM', 'GRATUITY', true, NOW(), NOW()),
('ESI Declaration Form', 'FORM-ESI-001', 'Employee State Insurance declaration form for eligible employees.', 'https://example.com/forms/esi-declaration.pdf', 'esi-declaration.pdf', 'FORM', 'ESI_DECLARATION', true, NOW(), NOW()),
('Confidentiality Agreement', 'POL-CONF-001', 'Employee confidentiality and non-disclosure agreement.', 'https://example.com/policies/confidentiality.pdf', 'confidentiality.pdf', 'POLICY', 'CONFIDENTIALITY', true, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 11. POLICY ACKNOWLEDGMENTS
-- =============================================================================
INSERT INTO policy_acknowledgments (employee_id, policy_id, acknowledged, acknowledged_at, created_at, updated_at) VALUES
((SELECT id FROM users WHERE username='dev1'), 1, true, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', NOW()),
((SELECT id FROM users WHERE username='dev1'), 2, true, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days', NOW()),
((SELECT id FROM users WHERE username='dev1'), 3, false, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='dev2'), 1, true, NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days', NOW()),
((SELECT id FROM users WHERE username='dev2'), 2, false, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='dev3'), 1, false, NULL, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 12. BGV REQUESTS
-- =============================================================================
INSERT INTO bgv_requests (employee_id, company_id, initiated_by, status, employee_type, remarks, initiated_at, submitted_at, verified_at, verified_by) VALUES
-- Pending document upload
((SELECT id FROM users WHERE username='dev3'), 1, (SELECT id FROM users WHERE username='admin1'), 'PENDING', 'FRESHER', 'Fresh graduate - need to verify educational documents', NOW() - INTERVAL '20 days', NULL, NULL, NULL),

-- Documents submitted, under review
((SELECT id FROM users WHERE username='dev4'), 1, (SELECT id FROM users WHERE username='admin1'), 'UNDER_REVIEW', 'EXPERIENCED', '3 years experience - verifying previous employment', NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days', NULL, NULL),

-- Approved
((SELECT id FROM users WHERE username='dev1'), 1, (SELECT id FROM users WHERE username='admin1'), 'APPROVED', 'EXPERIENCED', 'All documents verified successfully', NOW() - INTERVAL '100 days', NOW() - INTERVAL '95 days', NOW() - INTERVAL '90 days', (SELECT id FROM users WHERE username='admin1')),
((SELECT id FROM users WHERE username='dev2'), 1, (SELECT id FROM users WHERE username='admin1'), 'APPROVED', 'FRESHER', 'Educational documents verified', NOW() - INTERVAL '90 days', NOW() - INTERVAL '88 days', NOW() - INTERVAL '85 days', (SELECT id FROM users WHERE username='admin1'))
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 13. BGV DOCUMENTS
-- =============================================================================
INSERT INTO bgv_documents (bgv_request_id, document_type, document_name, file_url, file_name, status, uploaded_at, verified_at, verified_by, rejection_reason, remarks, created_at, updated_at) VALUES
-- For dev3 (PENDING)
((SELECT id FROM bgv_requests WHERE employee_id=(SELECT id FROM users WHERE username='dev3')), 'EDUCATION_CERTIFICATE', 'B.Tech Certificate', 'https://example.com/documents/dev3-btech.pdf', 'dev3-btech.pdf', 'PENDING', NULL, NULL, NULL, NULL, 'Upload pending', NOW(), NOW()),
((SELECT id FROM bgv_requests WHERE employee_id=(SELECT id FROM users WHERE username='dev3')), 'ID_PROOF', 'Aadhaar Card', NULL, NULL, 'PENDING', NULL, NULL, NULL, NULL, 'Upload pending', NOW(), NOW()),

-- For dev4 (UNDER_REVIEW)
((SELECT id FROM bgv_requests WHERE employee_id=(SELECT id FROM users WHERE username='dev4')), 'EXPERIENCE_CERTIFICATE', 'Previous Company Experience Letter', 'https://example.com/documents/dev4-exp.pdf', 'dev4-exp.pdf', 'VERIFIED', NOW() - INTERVAL '25 days', NOW() - INTERVAL '20 days', (SELECT id FROM users WHERE username='admin1'), NULL, 'Verified successfully', NOW(), NOW()),
((SELECT id FROM bgv_requests WHERE employee_id=(SELECT id FROM users WHERE username='dev4')), 'EDUCATION_CERTIFICATE', 'Degree Certificate', 'https://example.com/documents/dev4-degree.pdf', 'dev4-degree.pdf', 'PENDING_REUPLOAD', NOW() - INTERVAL '25 days', NULL, NULL, 'Document blurry, need clear copy', 'Please reupload clear document', NOW(), NOW()),

-- For dev1 (APPROVED)
((SELECT id FROM bgv_requests WHERE employee_id=(SELECT id FROM users WHERE username='dev1')), 'EXPERIENCE_CERTIFICATE', 'Previous Experience', 'https://example.com/documents/dev1-exp.pdf', 'dev1-exp.pdf', 'VERIFIED', NOW() - INTERVAL '95 days', NOW() - INTERVAL '90 days', (SELECT id FROM users WHERE username='admin1'), NULL, 'All good', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 14. EMPLOYEE DOCUMENTS
-- =============================================================================
INSERT INTO employee_documents (employee_id, document_type, document_name, document_url, upload_date, status, admin_comments, approved_by, approved_at, created_at, updated_at) VALUES
-- dev1 documents
((SELECT id FROM users WHERE username='dev1'), 'JOINING_LETTER', 'Joining Letter - Amit Kumar', 'https://example.com/documents/dev1-joining.pdf', CURRENT_DATE - INTERVAL '100 days', 'APPROVED', 'Document verified', (SELECT id FROM users WHERE username='admin1'), NOW() - INTERVAL '95 days', NOW(), NOW()),
((SELECT id FROM users WHERE username='dev1'), 'ID_PROOF', 'PAN Card - Amit Kumar', 'https://example.com/documents/dev1-pan.pdf', CURRENT_DATE - INTERVAL '100 days', 'APPROVED', 'Verified', (SELECT id FROM users WHERE username='admin1'), NOW() - INTERVAL '95 days', NOW(), NOW()),
((SELECT id FROM users WHERE username='dev1'), 'EDUCATION_CERTIFICATE', 'B.Tech Certificate', 'https://example.com/documents/dev1-degree.pdf', CURRENT_DATE - INTERVAL '100 days', 'APPROVED', 'Verified from university', (SELECT id FROM users WHERE username='admin1'), NOW() - INTERVAL '93 days', NOW(), NOW()),

-- dev2 documents
((SELECT id FROM users WHERE username='dev2'), 'JOINING_LETTER', 'Joining Letter - Sneha Reddy', 'https://example.com/documents/dev2-joining.pdf', CURRENT_DATE - INTERVAL '90 days', 'APPROVED', 'Document verified', (SELECT id FROM users WHERE username='admin1'), NOW() - INTERVAL '88 days', NOW(), NOW()),
((SELECT id FROM users WHERE username='dev2'), 'ID_PROOF', 'Aadhaar Card - Sneha Reddy', 'https://example.com/documents/dev2-aadhaar.pdf', CURRENT_DATE - INTERVAL '90 days', 'PENDING', NULL, NULL, NULL, NOW(), NOW()),

-- dev3 documents (pending verification)
((SELECT id FROM users WHERE username='dev3'), 'JOINING_LETTER', 'Joining Letter - Rohan Mehta', 'https://example.com/documents/dev3-joining.pdf', CURRENT_DATE - INTERVAL '30 days', 'PENDING', NULL, NULL, NULL, NOW(), NOW()),
((SELECT id FROM users WHERE username='dev3'), 'EXPERIENCE_CERTIFICATE', 'Internship Certificate', 'https://example.com/documents/dev3-internship.pdf', CURRENT_DATE - INTERVAL '25 days', 'PENDING', NULL, NULL, NULL, NOW(), NOW()),

-- dev4 documents (rejected)
((SELECT id FROM users WHERE username='dev4'), 'ADDRESS_PROOF', 'Rental Agreement', 'https://example.com/documents/dev4-address.pdf', CURRENT_DATE - INTERVAL '20 days', 'REJECTED', 'Document expired. Please upload recent utility bill.', NULL, NOW() - INTERVAL '18 days', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 15. PROJECTS
-- =============================================================================
INSERT INTO projects (name, description, start_date, end_date, status, manager_id, company_id, created_at, updated_at) VALUES
('E-Commerce Platform', 'Building a full-stack e-commerce platform with payment integration', '2025-01-15', '2026-06-30', 'IN_PROGRESS', (SELECT id FROM users WHERE username='manager1'), 1, NOW(), NOW()),
('Mobile Banking App', 'Developing mobile banking application for a financial client', '2025-03-01', '2026-03-31', 'IN_PROGRESS', (SELECT id FROM users WHERE username='manager2'), 1, NOW(), NOW()),
('AI Chatbot Integration', 'Integrating AI-powered chatbot for customer support', '2025-06-01', '2025-12-31', 'COMPLETED', (SELECT id FROM users WHERE username='manager1'), 1, NOW(), NOW()),
('Cloud Migration', 'Migrating legacy systems to cloud infrastructure', '2025-08-01', '2026-08-31', 'IN_PROGRESS', (SELECT id FROM users WHERE username='gmanager1'), 1, NOW(), NOW()),
('Internal HR Tool', 'Developing internal HR management dashboard', '2026-01-01', '2026-12-31', 'IN_PROGRESS', (SELECT id FROM users WHERE username='manager2'), 1, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 16. TEAM MEMBERS (Project Assignments)
-- =============================================================================
INSERT INTO team_members (project_id, employee_id, role, allocation_percentage, joined_date, created_at, updated_at) VALUES
-- E-Commerce Platform
((SELECT id FROM projects WHERE name='E-Commerce Platform'), (SELECT id FROM users WHERE username='dev1'), 'Backend Developer', 100, '2025-01-15', NOW(), NOW()),
((SELECT id FROM projects WHERE name='E-Commerce Platform'), (SELECT id FROM users WHERE username='dev2'), 'Frontend Developer', 80, '2025-02-01', NOW(), NOW()),
((SELECT id FROM projects WHERE name='E-Commerce Platform'), (SELECT id FROM users WHERE username='dev5'), 'Tech Lead', 50, '2025-01-15', NOW(), NOW()),

-- Mobile Banking App
((SELECT id FROM projects WHERE name='Mobile Banking App'), (SELECT id FROM users WHERE username='dev2'), 'Mobile Developer', 20, '2025-03-01', NOW(), NOW()),
((SELECT id FROM projects WHERE name='Mobile Banking App'), (SELECT id FROM users WHERE username='dev3'), 'Junior Developer', 100, '2025-07-15', NOW(), NOW()),
((SELECT id FROM projects WHERE name='Mobile Banking App'), (SELECT id FROM users WHERE username='dev4'), 'QA Engineer', 100, '2025-03-01', NOW(), NOW()),

-- AI Chatbot (Completed)
((SELECT id FROM projects WHERE name='AI Chatbot Integration'), (SELECT id FROM users WHERE username='dev1'), 'Backend Developer', 100, '2025-06-01', NOW(), NOW()),
((SELECT id FROM projects WHERE name='AI Chatbot Integration'), (SELECT id FROM users WHERE username='dev5'), 'Tech Lead', 50, '2025-06-01', NOW(), NOW()),

-- Cloud Migration
((SELECT id FROM projects WHERE name='Cloud Migration'), (SELECT id FROM users WHERE username='dev5'), 'Cloud Architect', 50, '2025-08-01', NOW(), NOW()),

-- Internal HR Tool
((SELECT id FROM projects WHERE name='Internal HR Tool'), (SELECT id FROM users WHERE username='dev3'), 'Full Stack Developer', 100, '2026-01-01', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 17. NOTIFICATIONS
-- =============================================================================
INSERT INTO notifications (user_id, title, message, type, read, read_at, reference_id, reference_type, created_at) VALUES
-- Unread notifications for dev1
((SELECT id FROM users WHERE username='dev1'), 'Leave Request Update', 'Your leave request for 2026-04-01 to 2026-04-04 has been approved.', 'LEAVE', false, NULL, 1, 'LEAVE_REQUEST', NOW() - INTERVAL '1 day'),
((SELECT id FROM users WHERE username='dev1'), 'New Policy Assigned', 'You have been assigned a new policy: Code of Conduct Policy', 'POLICY', false, NULL, 1, 'POLICY', NOW() - INTERVAL '2 days'),
((SELECT id FROM users WHERE username='dev1'), 'Salary Slip Generated', 'Your salary slip for March 2026 has been generated.', 'SALARY', false, NULL, 1, 'SALARY_SLIP', NOW() - INTERVAL '3 days'),

-- Unread notifications for dev2
((SELECT id FROM users WHERE username='dev2'), 'Attendance Pending Approval', 'Your attendance for today is pending approval.', 'ATTENDANCE', false, NULL, 1, 'ATTENDANCE', NOW()),
((SELECT id FROM users WHERE username='dev2'), 'Bonus Request Update', 'Your bonus request has been submitted for review.', 'BONUS', false, NULL, 1, 'BONUS_REQUEST', NOW() - INTERVAL '2 days'),

-- Read notifications for dev3
((SELECT id FROM users WHERE username='dev3'), 'BGV Request Initiated', 'Background verification has been initiated for you.', 'BGV', true, NOW() - INTERVAL '19 days', 1, 'BGV_REQUEST', NOW() - INTERVAL '20 days'),
((SELECT id FROM users WHERE username='dev3'), 'Document Upload Reminder', 'Please upload your BGV documents.', 'BGV', true, NOW() - INTERVAL '18 days', 1, 'BGV_REQUEST', NOW() - INTERVAL '19 days'),

-- Notifications for managers
((SELECT id FROM users WHERE username='manager1'), 'Leave Approval Pending', 'You have 3 leave requests pending approval.', 'LEAVE', false, NULL, NULL, 'LEAVE_BATCH', NOW()),
((SELECT id FROM users WHERE username='manager1'), 'Resignation Request', 'You have 1 resignation request pending approval.', 'RESIGNATION', false, NULL, 1, 'RESIGNATION_REQUEST', NOW() - INTERVAL '5 days'),
((SELECT id FROM users WHERE username='manager1'), 'Attendance Approval Pending', 'You have 5 attendance records pending approval.', 'ATTENDANCE', false, NULL, NULL, 'ATTENDANCE_BATCH', NOW()),

-- Notifications for admin
((SELECT id FROM users WHERE username='admin1'), 'New Employee Document', 'New document uploaded by employee for verification.', 'DOCUMENT', false, NULL, 1, 'EMPLOYEE_DOCUMENT', NOW() - INTERVAL '1 day'),
((SELECT id FROM users WHERE username='admin1'), 'BGV Under Review', '1 BGV request is under review.', 'BGV', false, NULL, 1, 'BGV_REQUEST', NOW() - INTERVAL '2 days'),
((SELECT id FROM users WHERE username='admin1'), 'Resignation Pending Admin', '1 resignation request pending admin approval.', 'RESIGNATION', false, NULL, 1, 'RESIGNATION_REQUEST', NOW() - INTERVAL '3 days')
ON CONFLICT DO NOTHING;

-- =============================================================================
-- 18. EMPLOYEE EXIT RECORDS
-- =============================================================================
INSERT INTO employee_exits (employee_id, company_id, exit_date, exit_type, reason, manager_approval_date, admin_approval_date, final_settlement_date, exit_interview_completed, exit_interview_notes, created_at, updated_at) VALUES
-- For dev2 (APPROVED resignation)
((SELECT id FROM users WHERE username='dev2'), 1, CURRENT_DATE - 15, 'RESIGNATION', 'Higher studies abroad', CURRENT_DATE - 40, CURRENT_DATE - 15, CURRENT_DATE - 15, true, 'All knowledge transfer completed. Will be missed.', NOW() - INTERVAL '45 days', NOW()),

-- For exited employee (terminated example)
((SELECT id FROM users WHERE username='dev4'), 1, NULL, 'RESIGNATION', NULL, NULL, NULL, NULL, false, NULL, NOW(), NOW())
ON CONFLICT DO NOTHING;

-- =============================================================================
-- TEST DATA SUMMARY
-- =============================================================================
-- Users: 15+ (across 2 companies + superadmin)
-- Companies: 3
-- Employee Profiles: 11
-- Attendance Records: 10+
-- Leave Requests: 6
-- Profile Edit Requests: 6
-- Bonus Requests: 6
-- Salary Slips: 5+
-- Resignation Requests: 4
-- HR Policies: 8
-- Policy Acknowledgments: 6
-- BGV Requests: 4
-- BGV Documents: 6
-- Employee Documents: 9
-- Projects: 5
-- Team Members: 10+
-- Notifications: 15+
-- Employee Exits: 2
