# HR Portal - Test Plan

## Test Accounts

| Role | Username | Password |
|------|----------|----------|
| Super Admin | superadmin | superadmin123 |
| Admin | admin | admin123 |
| Developer | developer1 | developer123 |
| HR | hr1 | hr123 |
| Manager | manager1 | manager123 |
| HR Manager | hrmanager1 | hrmanager123 |
| Software Engineer | engineer1 | engineer123 |
| General Manager | gm1 | gm123 |

---

## 1. Authentication Tests

### Test 1.1: Login with Valid Credentials
**Steps:**
1. Go to http://localhost:3000
2. Click "Login to Portal"
3. Enter username: `superadmin`, password: `superadmin123`
4. Click Login

**Expected Result:** Successfully logged in, redirected to Super Admin Dashboard

### Test 1.2: Login with Invalid Credentials
**Steps:**
1. Go to Login page
2. Enter username: `invalid`, password: `invalid123`
3. Click Login

**Expected Result:** Error message "Invalid credentials" displayed

### Test 1.3: Login as Employee
**Steps:**
1. Login with username: `developer1`, password: `developer123`

**Expected Result:** Redirected to Employee Dashboard

### Test 1.4: Logout
**Steps:**
1. Click "Logout" button from any dashboard

**Expected Result:** Redirected to Login page, session cleared

---

## 2. Super Admin Tests

### Test 2.1: Create Company
**Steps:**
1. Login as Super Admin
2. Click "Manage Companies"
3. Click "+ Add Company"
4. Fill all fields:
   - Company Name: `Test Corp`
   - Logo URL: (leave empty)
   - Address: `123 Test Street, NYC`
   - Phone: `+1-555-TEST`
   - Email: `test@testcorp.com`
5. Click "Create Company"

**Expected Result:** Company created successfully, appears in list

### Test 2.2: Create Company Without Name (Validation)
**Steps:**
1. Click "+ Add Company"
2. Leave Company Name empty
3. Click "Create Company"

**Expected Result:** Form validation prevents submission

### Test 2.3: Create Admin
**Steps:**
1. Click "Manage Admins"
2. Click "+ Add Admin"
3. Fill all fields:
   - Username: `newadmin`
   - Password: `newadmin123`
   - Email: `newadmin@testcorp.com`
   - Full Name: `New Admin User`
   - Company: Select `Test Corp`
4. Click "Create Admin"

**Expected Result:** Admin created successfully with company assigned

### Test 2.4: Create Admin Without Company (Validation)
**Steps:**
1. Click "+ Add Admin"
2. Fill all fields except Company
3. Click "Create Admin"

**Expected Result:** Form validation error "Company is required for admin"

### Test 2.5: Delete Admin
**Steps:**
1. Find admin in list
2. Click "Delete" button
3. Confirm deletion

**Expected Result:** Admin removed from list

### Test 2.6: Delete Company
**Steps:**
1. Go to Manage Companies
2. Click "Delete" on a company
3. Confirm deletion

**Expected Result:** Company removed from list

---

## 3. Admin Tests

### Test 3.1: View My Company
**Steps:**
1. Login as Admin (`admin` / `admin123`)
2. View Dashboard

**Expected Result:** Company card shows:
- GenTech Solutions
- Company logo placeholder (letter "G")
- Address, Phone, Email

### Test 3.2: Create Employee - All Fields
**Steps:**
1. Click "Manage Employees"
2. Click "+ Add Employee"
3. Fill ALL fields:

**Account Information:**
- Username: `testemployee`
- Password: `testemp123`
- Email: `testemp@gentech.com`
- Full Name: `Test Employee User`
- Role: `SOFTWARE_ENGINEER`
- Company: `GenTech Solutions`

**Employment Details:**
- Employee Code: `EMP001`
- Department: `Engineering`
- Designation: `Senior Developer`
- Date of Joining: Select any date
- Salary: `85000`

**Personal Information:**
- Date of Birth: Select any date (e.g., 1990-01-15)
- Phone Number: `+1-555-123-4567`
- Address: `123 Employee St, City, Country`
- Emergency Contact: `+1-555-999-0000`

4. Click "Create Employee"

**Expected Result:** Employee created, appears in list with all details

### Test 3.3: Create Employee - Required Fields Only
**Steps:**
1. Click "+ Add Employee"
2. Fill only required fields (marked with *):
   - Username, Password, Email, Full Name, Role, Company
3. Click "Create Employee"

**Expected Result:** Employee created successfully with optional fields empty

### Test 3.4: Verify DOB and DOJ Labels
**Steps:**
1. Click "+ Add Employee"
2. Look at Employment Details section
3. Look at Personal Information section

**Expected Result:** 
- "Date of Joining" label visible
- "Date of Birth" label visible
- Both have proper date pickers

### Test 3.5: Delete Employee
**Steps:**
1. Find employee in list
2. Click "Delete" button
3. Confirm deletion

**Expected Result:** Employee removed from list

### Test 3.6: View Employee List
**Steps:**
1. Go to Manage Employees page

**Expected Result:** Table displays:
- ID, Name, Username, Role (with badge), Company, Department, Designation
- Delete button for each

---

## 4. Employee Tests

### Test 4.1: View Profile
**Steps:**
1. Login as any employee (e.g., `developer1` / `developer123`)
2. View My Profile page

**Expected Result:** Profile shows:
- Company Information section with logo placeholder
- Personal Information section
- Employment Information section

### Test 4.2: Company Logo Display
**Steps:**
1. View Employee Dashboard
2. Look at Company Information card

**Expected Result:** 
- Shows letter "G" in purple circle (company initial)
- Company name: GenTech Solutions
- No broken image icons

### Test 4.3: Profile Fields Display
**Steps:**
1. View My Profile
2. Check all sections

**Expected Result:** All fields displayed:
- Full Name, Username, Email, Role
- Phone Number, Date of Birth, Address, Emergency Contact
- Employee Code, Department, Designation, Date of Joining, Salary

---

## 5. UI/UX Tests

### Test 5.1: Form Layout
**Steps:**
1. Navigate to any form (Create Admin, Create Employee, Create Company)

**Expected Result:**
- All fields have labels
- Required fields marked with *
- Proper section headers with icons
- Grid layout for better organization
- Consistent styling

### Test 5.2: Responsive Design
**Steps:**
1. Resize browser window
2. Check on different screen sizes

**Expected Result:** Layout adjusts gracefully

### Test 5.3: Navigation
**Steps:**
1. Click between different menu items
2. Use browser back/forward buttons

**Expected Result:** Smooth navigation, no unexpected logouts

---

## 6. Security Tests

### Test 6.1: Access Control - Employee cannot access Admin
**Steps:**
1. Login as employee
2. Try to access: http://localhost:3000/admin

**Expected Result:** Redirected to Employee Dashboard or Login

### Test 6.2: Access Control - Admin cannot access Super Admin
**Steps:**
1. Login as admin
2. Try to access: http://localhost:3000/superadmin

**Expected Result:** Redirected to Admin Dashboard or Login

### Test 6.3: Token Expiration
**Steps:**
1. Login
2. Wait for token to expire (or clear localStorage)
3. Try to access protected page

**Expected Result:** Redirected to Login page

---

## Test Results Log

| Test ID | Description | Status | Notes |
|---------|-------------|--------|-------|
| 1.1 | Login with Valid Credentials | ⬜ | |
| 1.2 | Login with Invalid Credentials | ⬜ | |
| 1.3 | Login as Employee | ⬜ | |
| 1.4 | Logout | ⬜ | |
| 2.1 | Create Company | ⬜ | |
| 2.2 | Create Company Without Name | ⬜ | |
| 2.3 | Create Admin | ⬜ | |
| 2.4 | Create Admin Without Company | ⬜ | |
| 2.5 | Delete Admin | ⬜ | |
| 2.6 | Delete Company | ⬜ | |
| 3.1 | View My Company | ⬜ | |
| 3.2 | Create Employee - All Fields | ⬜ | |
| 3.3 | Create Employee - Required Only | ⬜ | |
| 3.4 | Verify DOB and DOJ Labels | ⬜ | |
| 3.5 | Delete Employee | ⬜ | |
| 3.6 | View Employee List | ⬜ | |
| 4.1 | View Profile | ⬜ | |
| 4.2 | Company Logo Display | ⬜ | |
| 4.3 | Profile Fields Display | ⬜ | |
| 5.1 | Form Layout | ⬜ | |
| 5.2 | Responsive Design | ⬜ | |
| 5.3 | Navigation | ⬜ | |
| 6.1 | Employee Access Control | ⬜ | |
| 6.2 | Admin Access Control | ⬜ | |
| 6.3 | Token Expiration | ⬜ | |

---

## Quick Reference - Field Validation

### Required Fields (marked with *)

**Create Admin:**
- Username, Password, Email, Full Name, Company

**Create Employee:**
- Username, Password, Email, Full Name, Role, Company

**Create Company:**
- Company Name

### Optional Fields

**Create Employee:**
- Employee Code, Department, Designation, Date of Joining, Salary
- Date of Birth, Phone Number, Address, Emergency Contact

**Create Company:**
- Logo URL, Address, Phone, Email
