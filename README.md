# GenTech HR Portal

A comprehensive HR Management System built with Spring Boot, React, and PostgreSQL.

## Features

### Super Admin
- Create and manage administrators
- Create and manage companies
- Map admins to companies

### Admin
- Create employees with various roles (HR, HR Manager, Software Engineer, Manager, General Manager, Developer)
- View all employees
- Manage employee profiles

### Employee
- View company name and logo
- View personal profile information

### Forgot Password (OTP-based)
- Reset password using username
- OTP sent to registered email address
- 6-digit OTP with 15-minute expiry
- Secure password reset flow

### Employee Welcome Email
- Auto-send welcome email when admin creates new employee
- Email includes login credentials (username, password)
- Employee ID and employment details
- Login portal URL for easy access

### Manager Project & Team Management
- Managers can create projects for their company
- Assign team members to projects with roles
- Set allocation percentage for team members
- View project status and team details
- Email notification sent to employees when added to project

## Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 3.2.0 |
| Frontend | React 18 |
| Database | PostgreSQL |
| Security | Spring Security + JWT |
| Build Tool | Maven |

## Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Maven 3.8+

## Project Structure

```
hr-portal/
├── backend/                 # Spring Boot Application
│   ├── src/main/java/com/gentech/hrportal/
│   │   ├── config/         # Security & App Config
│   │   ├── controller/     # REST Controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA Entities
│   │   ├── repository/     # Spring Data Repositories
│   │   ├── security/       # JWT & Security Components
│   │   └── service/        # Business Logic
│   └── src/main/resources/
│       └── application.properties
├── frontend/               # React Application
│   ├── src/
│   │   ├── components/     # Reusable Components
│   │   ├── context/        # React Context (Auth)
│   │   ├── pages/          # Page Components
│   │   └── services/       # API Services
│   └── public/
└── README.md
```

## Setup Instructions

### 1. Database Setup

```sql
-- Create database
CREATE DATABASE hrportal;

-- Connect to database
\c hrportal
```

Update `backend/src/main/resources/application.properties` with your PostgreSQL credentials:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hrportal
spring.datasource.username=postgres
spring.datasource.password=your_password
```

### 2. Email Configuration (for Forgot Password)

Update `backend/src/main/resources/application-dev.properties` with your email credentials:

```properties
# Gmail SMTP Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.protocol=smtp
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# OTP Configuration
app.otp.expiry.minutes=15
```

**Note:** For Gmail, you need to generate an App Password:
1. Enable 2-Factor Authentication on your Google account
2. Go to https://myaccount.google.com/apppasswords
3. Select "App" = "Mail", "Device" = "Other (Custom name)"
4. Copy the 16-character App Password

### 3. Backend Setup

```bash
# Navigate to backend directory
cd backend

# Build the application
mvn clean install

# Run the application
mvn spring-boot:run
```

The backend server will start on `http://localhost:8080`

Default Super Admin credentials:
- **Username:** superadmin
- **Password:** superadmin123

### 4. Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start the development server
npm start
```

The frontend will be available at `http://localhost:3000`

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Login with credentials |
| POST | `/api/auth/setup` | Create super admin |
| POST | `/api/auth/forgot-password` | Request OTP for password reset |
| POST | `/api/auth/verify-otp` | Verify the OTP |
| POST | `/api/auth/reset-password` | Reset password with OTP |

### Super Admin (Requires SUPER_ADMIN role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/superadmin/admins` | Get all admins |
| POST | `/api/superadmin/admins` | Create new admin |
| DELETE | `/api/superadmin/admins/{id}` | Delete admin |
| GET | `/api/superadmin/companies` | Get all companies |
| POST | `/api/superadmin/companies` | Create new company |
| DELETE | `/api/superadmin/companies/{id}` | Delete company |
| PUT | `/api/superadmin/admins/{adminId}/company/{companyId}` | Map admin to company |

### Admin (Requires ADMIN or SUPER_ADMIN role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/employees` | Get all employees |
| GET | `/api/admin/employees/company/{id}` | Get employees by company |
| POST | `/api/admin/employees` | Create new employee |
| DELETE | `/api/admin/employees/{id}` | Delete employee |
| GET | `/api/admin/my-company` | Get admin's company |

### Manager Projects (Requires MANAGER, GENERAL_MANAGER, ADMIN, or SUPER_ADMIN role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/manager/projects` | Get all projects for current manager |
| GET | `/api/manager/projects/status/{status}` | Get projects by status |
| GET | `/api/manager/projects/{projectId}` | Get project details with team |
| POST | `/api/manager/projects` | Create new project |
| PUT | `/api/manager/projects/{projectId}` | Update project |
| PATCH | `/api/manager/projects/{projectId}/status` | Update project status |
| DELETE | `/api/manager/projects/{projectId}` | Delete project |
| POST | `/api/manager/projects/{projectId}/team` | Add team member |
| DELETE | `/api/manager/projects/{projectId}/team/{employeeId}` | Remove team member |
| PUT | `/api/manager/projects/{projectId}/team/{employeeId}` | Update team member |
| GET | `/api/manager/projects/{projectId}/available-employees` | Get available employees for team |

### Employee (Requires any employee role)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/employee/profile` | Get my profile |

## Employee Roles

- `HR`
- `HR_MANAGER`
- `SOFTWARE_ENGINEER`
- `MANAGER`
- `GENERAL_MANAGER`
- `DEVELOPER`

## Security Configuration

- JWT-based authentication with Bearer token
- Role-based access control (RBAC)
- CORS enabled for React frontend
- BCrypt password encoding

## Forgot Password Flow

The application provides a secure OTP-based password reset mechanism:

### Flow Steps

1. **Request OTP**
   - User enters their username on the Forgot Password page
   - System validates the username and fetches the registered email
   - A 6-digit OTP is generated and sent to the user's email
   - OTP expires after 15 minutes (configurable)

2. **Verify OTP**
   - User enters the OTP received in their email
   - System validates the OTP against the stored token
   - Verifies OTP is not expired and belongs to the correct user

3. **Reset Password**
   - User enters a new password (minimum 6 characters)
   - Password is encrypted and saved
   - OTP token is marked as used
   - User is redirected to login page

### Security Features

- OTPs are unique and randomly generated
- Each OTP can only be used once
- Old OTPs are automatically deleted when new ones are requested
- User email is never exposed in API responses
- Passwords are BCrypt encrypted before storage

## Employee Welcome Email

When an admin creates a new employee, a welcome email is automatically sent to the employee's email address.

### Email Contents

- **Login Credentials**: Username, password, and portal URL
- **Employment Details**: Employee ID, department, designation, date of joining
- **Security Notice**: Instructions to change password and keep credentials secure

### Sample Email

```
Subject: Welcome to GenTech Solutions - Your Account Details

Dear [Employee Name],

Welcome to GenTech Solutions!

Your employee account has been successfully created. Below are your login 
credentials and employment details:

========================================
      LOGIN CREDENTIALS
========================================
Portal URL: http://localhost:3000
Username: [username]
Password: [password]
Role: [role]

========================================
      EMPLOYMENT DETAILS
========================================
Employee ID: [EMP001]
Full Name: [Employee Name]
Email: [email@company.com]
Department: [Engineering]
Designation: [Software Developer]
Date of Joining: [YYYY-MM-DD]
Company: [GenTech Solutions]

========================================
      IMPORTANT SECURITY NOTICE
========================================
1. Please login and change your password immediately.
2. Do not share your login credentials with anyone.
3. If you did not request this account, please contact HR immediately.
```

## Screenshots

### Login Page
Professional login page with gradient background and Forgot Password link.

### Forgot Password Page
3-step password reset flow:
- **Step 1:** Enter username
- **Step 2:** Enter OTP sent to registered email
- **Step 3:** Set new password

### Super Admin Dashboard
- Statistics overview
- Admin management
- Company management
- Map admins to companies

### Admin Dashboard
- View assigned company
- Employee management
- Create employees with detailed profiles

### Employee Dashboard
- View company information with logo
- Personal profile details
- Employment information

## Development

### Running Tests

```bash
# Backend tests
cd backend
mvn test

# Frontend tests
cd frontend
npm test
```

### Building for Production

```bash
# Build backend
cd backend
mvn clean package

# Build frontend
cd frontend
npm run build
```

## License

MIT License

## Support

For any issues or questions, please create an issue in the repository.
