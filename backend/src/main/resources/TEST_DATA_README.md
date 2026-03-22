# GenTech HR Portal - Sample Test Data

This directory contains sample test data for the GenTech HR Portal application.

## File: `sample-test-data.sql`

This SQL script populates your database with comprehensive test data covering all features of the HR Portal.

### Test Accounts

All test accounts use the password: `password123`

#### Super Admin
| Username | Email | Role |
|----------|-------|------|
| superadmin | superadmin@gentech.com | SUPER_ADMIN |

#### Company 1 (GenTech Solutions Pvt Ltd) - Admin Users
| Username | Email | Role | Full Name |
|----------|-------|------|-----------|
| admin1 | admin@gentech.com | ADMIN | Rahul Sharma |
| hr1 | hr@gentech.com | HR | Priya Patel |
| hrmgr1 | hrmgr@gentech.com | HR_MANAGER | Anita Desai |

#### Company 1 - Managers
| Username | Email | Role | Full Name |
|----------|-------|------|-----------|
| manager1 | manager1@gentech.com | MANAGER | Vikram Singh |
| manager2 | manager2@gentech.com | MANAGER | Deepa Gupta |
| gmanager1 | gm@gentech.com | GENERAL_MANAGER | Rajesh Kumar |

#### Company 1 - Employees
| Username | Email | Role | Employee Code | Department |
|----------|-------|------|---------------|------------|
| dev1 | dev1@gentech.com | SOFTWARE_ENGINEER | GT007 | Engineering |
| dev2 | dev2@gentech.com | SOFTWARE_ENGINEER | GT008 | Engineering |
| dev3 | dev3@gentech.com | DEVELOPER | GT009 | Engineering |
| dev4 | dev4@gentech.com | DEVELOPER | GT010 | Engineering |
| dev5 | dev5@gentech.com | SOFTWARE_ENGINEER | GT011 | Engineering |

#### Company 2 (InnovateSoft Technologies)
| Username | Email | Role | Full Name |
|----------|-------|------|-----------|
| admin2 | admin@innovatesoft.com | ADMIN | Sanjay Verma |
| mgr2 | mgr@innovatesoft.com | MANAGER | Kavita Rao |
| emp2_1 | emp1@innovatesoft.com | DEVELOPER | Arun Nair |
| emp2_2 | emp2@innovatesoft.com | SOFTWARE_ENGINEER | Lakshmi N |

---

## How to Load Test Data

### Option 1: Using psql (PostgreSQL)

```bash
# Connect to your database
psql -h localhost -U your_username -d hr_portal_db

# Run the SQL script
\i sample-test-data.sql
```

### Option 2: Using pgAdmin

1. Open pgAdmin
2. Connect to your database
3. Right-click on the database → Query Tool
4. Open the `sample-test-data.sql` file
5. Click Execute/Refresh (F5)

### Option 3: Using Spring Boot (H2 or similar)

If using an embedded database, place the SQL file in:
```
src/main/resources/data.sql
```

Spring Boot will automatically execute it on startup (when `spring.sql.init.mode=always`).

---

## Test Data Coverage

### 1. Dashboard
- Sample dashboard widgets data is populated through various entities

### 2. Manage Employees
- 11 employee profiles with complete details
- Various departments and designations

### 3. Exit Employees
- 2 employee exit records
- Mix of resignation and termination scenarios

### 4. Document Management
- 9 employee documents across multiple statuses:
  - PENDING: Documents awaiting approval
  - APPROVED: Verified documents
  - REJECTED: Documents with issues

### 5. Attendance Approval
- 10+ attendance records with different statuses:
  - PENDING: Records awaiting manager approval
  - APPROVED: Verified attendance
  - Various statuses: PRESENT, HALF_DAY, WORK_FROM_HOME, ON_LEAVE

### 6. Leave Approvals
- 6 leave requests covering:
  - PENDING: 3 requests awaiting approval
  - APPROVED: 2 approved requests
  - REJECTED: 1 rejected request

### 7. Profile Edit Requests
- 6 edit requests:
  - PENDING: 3 requests
  - APPROVED: 2 requests
  - REJECTED: 1 request (salary change - not allowed)

### 8. Salary Management
- 5+ salary slips across different months
- Various statuses: GENERATED, SENT
- Includes bonus calculations

### 9. Bonus Management
- 6 bonus requests:
  - PENDING: 3 requests
  - APPROVED: 2 requests
  - REJECTED: 1 request

### 10. Resignation Approvals
- 4 resignation requests:
  - PENDING_MANAGER: 1 request
  - MANAGER_APPROVED (Pending Admin): 1 request
  - APPROVED: 1 completed exit
  - REJECTED: 1 rejected request

### 11. BGV Management
- 4 BGV requests with different statuses:
  - PENDING: Awaiting document upload
  - UNDER_REVIEW: Documents submitted, being verified
  - APPROVED: Verification completed

- 6 BGV documents with statuses:
  - PENDING: Awaiting upload
  - VERIFIED: Approved documents
  - PENDING_REUPLOAD: Rejected, needs re-upload

### 12. HR Policies
- 8 HR policies covering various categories:
  - CODE_OF_CONDUCT
  - POSH
  - DATA_PROTECTION
  - INTEGRITY
  - PF_NOMINATION
  - GRATUITY
  - ESI_DECLARATION
  - CONFIDENTIALITY

### 13. Projects & Team Management
- 5 projects (IN_PROGRESS, COMPLETED)
- 10+ team member assignments

### 14. Notifications
- 15+ notifications across users
- Mix of read and unread notifications

---

## Data Summary

| Entity | Count |
|--------|-------|
| Companies | 3 |
| Users | 15+ |
| Employee Profiles | 11 |
| Attendance Records | 10+ |
| Leave Requests | 6 |
| Profile Edit Requests | 6 |
| Bonus Requests | 6 |
| Salary Slips | 5+ |
| Resignation Requests | 4 |
| HR Policies | 8 |
| Policy Acknowledgments | 6 |
| BGV Requests | 4 |
| BGV Documents | 6 |
| Employee Documents | 9 |
| Projects | 5 |
| Team Members | 10+ |
| Notifications | 15+ |
| Employee Exits | 2 |

---

## Notes

1. **Password**: All users have the password `password123`
2. **Dates**: Most dates are relative to `CURRENT_DATE` for freshness
3. **Conflicts**: The script uses `ON CONFLICT DO NOTHING` to prevent duplicate entries
4. **Relations**: All foreign key relationships are properly maintained
5. **Status Flows**: Data represents realistic workflow states

---

## Troubleshooting

### Duplicate Key Errors
If you see duplicate key errors, it means some data already exists. The script uses `ON CONFLICT DO NOTHING` but some tables might not have unique constraints.

### Foreign Key Violations
Make sure to run the script in order (companies → users → profiles → others) or truncate all tables first.

### Reset Database
To start fresh:
```sql
-- Truncate all tables (PostgreSQL)
TRUNCATE TABLE notifications, team_members, projects, employee_documents, 
bgv_documents, bgv_requests, policy_acknowledgments, hr_policies,
resignation_requests, salary_slips, bonus_requests, profile_edit_requests,
leaves, attendance, employee_exits, employee_profiles, users, companies 
CASCADE;

-- Then run the test data script
\i sample-test-data.sql
```
