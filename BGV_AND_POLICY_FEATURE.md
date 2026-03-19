# BGV (Background Verification) and HR Policies Feature

## Overview
This document describes the new BGV (Background Verification) and HR Policies management system.

---

## Part A: Background Verification (BGV) System

### Features

#### 1. Admin Portal - BGV Management
- **Initiate BGV**: Admin can start BGV process for any employee
- **Document Requirements**:
  - **For Experienced Employees**:
    - Aadhar Card
    - PAN Card
    - Driving License
    - Education Marksheet & Certificate
    - Previous Employment Offer Letter
    - Previous Employment Payslips
    - Previous Employment Relieving Letter
    - Previous Employment Experience Certificate
    - Self Declaration Form (signed)
  
  - **For Freshers**:
    - Aadhar Card
    - PAN Card
    - Driving License
    - Education Marksheet & Certificate
    - Acknowledgement Form (signed)

- **Verification Process**:
  - Admin reviews each uploaded document
  - Can Approve or Reject with remarks
  - Final approval/rejection of entire BGV

#### 2. Employee Portal - BGV Upload
- **Dashboard Notification**: Shows pending BGV requirements
- **Document Upload**: Upload each required document
- **Status Tracking**: See which documents are approved/pending/rejected
- **Submit for Verification**: After all documents uploaded
- **BGV Status**: "BGV Documents Received" after admin verification

### Backend APIs

#### BGV Endpoints:
```
POST   /api/bgv/initiate           - Admin initiates BGV
GET    /api/bgv/my-status          - Employee views their BGV status
GET    /api/bgv/my-documents/{id}  - Employee views required documents
POST   /api/bgv/upload-document    - Employee uploads a document
POST   /api/bgv/submit/{id}        - Employee submits for verification
GET    /api/bgv/company-requests   - Admin views all company BGVs
GET    /api/bgv/pending            - Admin views pending BGVs
POST   /api/bgv/verify-document    - Admin verifies a document
POST   /api/bgv/complete/{id}      - Admin completes BGV
```

---

## Part B: HR Policies System

### Features

#### 1. Admin Portal - HR Policy Management
- **Upload Policies**:
  - Integrity Policy
  - POSH (Prevention of Sexual Harassment)
  - Confidentiality Policy
  - Code of Conduct
  - Data Protection Policy
  - Any custom policy

- **Upload Forms**:
  - PF Nomination Form
  - Gratuity Form
  - ESI Declaration Form
  - Any custom form

- **Assign to Employees**:
  - Select specific employees
  - Send policy acknowledgment requests
  - Track who has signed/not signed

#### 2. Employee Portal - Policy Acknowledgment
- **Dashboard Priority View**: Shows pending policies to sign
- **Policy Signing**: Digital acknowledgment with e-signature
- **Confirmation Message**: "Policy signed successfully"
- **View History**: See all signed policies

### Backend APIs

#### HR Policy Endpoints:
```
POST   /api/policies                        - Admin creates policy
GET    /api/policies/active                 - Get all active policies
GET    /api/policies/type/{type}            - Get policies by type (POLICY/FORM)
POST   /api/policies/assign                 - Assign policy to employee
GET    /api/policies/my-pending             - Employee views pending policies
GET    /api/policies/my-all                 - Employee views all their policies
POST   /api/policies/acknowledge/{id}       - Employee acknowledges policy
GET    /api/policies/employee/{id}/status   - Admin checks employee policy status
```

---

## Database Schema

### New Tables Created:

1. **bgv_requests**
   - id, employee_id, company_id, initiated_by
   - status (PENDING, IN_PROGRESS, SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED)
   - employee_type (FRESHER, EXPERIENCED)
   - initiated_at, submitted_at, verified_at

2. **bgv_documents**
   - id, bgv_request_id, document_type
   - file_url, file_name, status
   - uploaded_at, verified_at

3. **hr_policies**
   - id, policy_name, policy_code, description
   - file_url, policy_type (POLICY/FORM)
   - category, active, created_at

4. **policy_acknowledgments**
   - id, policy_id, employee_id, company_id
   - status (PENDING, ACKNOWLEDGED, OVERDUE)
   - assigned_at, acknowledged_at, signature

---

## Implementation Status

### ✅ Completed (Backend):
1. BGVRequest and BGVDocument entities
2. HRPolicy and PolicyAcknowledgment entities
3. Repositories for all entities
4. BGVService with complete workflow
5. BGVController with all endpoints
6. HRPolicyService for policy management

### ⏳ Pending (Frontend):
1. Admin BGV Management Page
2. Employee BGV Upload Page
3. Admin HR Policy Management Page
4. Employee Policy Signing Page

---

## Next Steps

To complete the feature, you need to build the frontend pages:

### Frontend Pages Needed:

1. **AdminDashboard - BGV Section**
   - Add "BGV Management" menu item
   - Table to view all BGV requests
   - Buttons to initiate BGV for employees
   - Document verification interface

2. **EmployeeDashboard - BGV Section**
   - Add "My BGV" menu item
   - Show pending document list
   - Upload interface for each document
   - Submit button

3. **AdminDashboard - HR Policies Section**
   - Add "HR Policies" menu item
   - Upload policy/form interface
   - Assign policy to employees
   - View acknowledgment status

4. **EmployeeDashboard - Policies Section**
   - Add "My Policies" menu item
   - Show pending policies (priority)
   - Policy viewing and signing interface

---

## File Locations

### Backend Files Created:
- `entity/BGVRequest.java`
- `entity/BGVDocument.java`
- `entity/HRPolicy.java`
- `entity/PolicyAcknowledgment.java`
- `repository/BGVRequestRepository.java`
- `repository/BGVDocumentRepository.java`
- `repository/HRPolicyRepository.java`
- `repository/PolicyAcknowledgmentRepository.java`
- `service/BGVService.java`
- `service/HRPolicyService.java`
- `controller/BGVController.java`
- `dto/BGVInitiateRequest.java`
- `dto/BGVDocumentUploadRequest.java`
- `dto/BGVVerifyRequest.java`

---

## Testing the Backend

After rebuilding the backend, test with:

1. **Login as Admin** → Initiate BGV for an employee
2. **Login as Employee** → View BGV requirements, upload documents
3. **Login as Admin** → Verify documents, approve BGV
4. **Login as Admin** → Create HR Policy, assign to employee
5. **Login as Employee** → View pending policy, acknowledge it

---

## Notes

- The backend is fully implemented and ready
- Frontend pages need to be built to use these APIs
- File upload uses existing FileStorageService
- All endpoints are secured with proper role-based access
