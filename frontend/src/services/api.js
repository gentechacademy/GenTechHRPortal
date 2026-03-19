import axios from 'axios';

// Use relative URL in development to leverage proxy (avoids CORS and is faster)
// In production, use the full API URL
const API_URL = process.env.NODE_ENV === 'development' 
  ? '/api' 
  : 'https://gentechhrportal.onrender.com/api';

const api = axios.create({
  baseURL: API_URL,
  timeout: 30000, // 30 second timeout
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    // Don't add token for auth endpoints (login, forgot-password, etc.)
    const isAuthEndpoint = config.url && (
      config.url.includes('/auth/login') ||
      config.url.includes('/auth/forgot-password') ||
      config.url.includes('/auth/verify-otp') ||
      config.url.includes('/auth/reset-password') ||
      config.url.includes('/auth/setup')
    );
    if (token && !isAuthEndpoint) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    return Promise.reject(error);
  }
);

// Auth APIs
export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  setup: () => api.post('/auth/setup'),
};

// Super Admin APIs
export const superAdminAPI = {
  getAllAdmins: () => api.get('/superadmin/admins'),
  createAdmin: (data) => api.post('/superadmin/admins', data),
  updateAdmin: (id, data) => api.put(`/superadmin/admins/${id}`, data),
  deleteAdmin: (id) => api.delete(`/superadmin/admins/${id}`),
  getAllCompanies: () => api.get('/superadmin/companies'),
  createCompany: (data) => api.post('/superadmin/companies', data),
  updateCompany: (id, data) => api.put(`/superadmin/companies/${id}`, data),
  deleteCompany: (id) => api.delete(`/superadmin/companies/${id}`),
  mapAdminToCompany: (adminId, companyId) =>
    api.put(`/superadmin/admins/${adminId}/company/${companyId}`),
};

// Admin APIs
export const adminAPI = {
  getAllEmployees: () => api.get('/admin/employees'),
  getEmployeesByCompany: (companyId) => api.get(`/admin/employees/company/${companyId}`),
  createEmployee: (data) => api.post('/admin/employees', data),
  updateEmployee: (id, data) => api.put(`/admin/employees/${id}`, data),
  deleteEmployee: (id) => api.delete(`/admin/employees/${id}`),
  getMyCompany: () => api.get('/admin/my-company'),
  getAllCompanies: () => api.get('/admin/companies'),
};

// Employee APIs
export const employeeAPI = {
  getMyProfile: () => api.get('/employee/profile'),
  updateProfilePicture: (profilePictureUrl) => api.put('/employee/profile/picture', { profilePictureUrl }),
  getMyProjects: () => api.get('/employee/my-projects'),
};

// Attendance APIs
export const attendanceAPI = {
  checkIn: (notes) => api.post('/attendance/checkin', { notes }),
  checkOut: (notes) => api.post('/attendance/checkout', { notes }),
  submitAttendance: (data) => api.post('/attendance/submit', data),
  getMyAttendance: () => api.get('/attendance/my-attendance'),
  getMyAttendanceByRange: (startDate, endDate) =>
    api.get(`/attendance/my-attendance/range?startDate=${startDate}&endDate=${endDate}`),
  getTodayAttendance: () => api.get('/attendance/today'),
  submitWeeklyAttendance: (data) => api.post('/attendance/weekly', data),
  updateAttendance: (id, data) => api.put(`/attendance/${id}`, data),
  getCurrentWeekAttendance: () => api.get('/attendance/weekly/current'),
  getWeeklyAttendance: (weekStartDate) => api.get(`/attendance/weekly?weekStartDate=${weekStartDate}`),
  canEditAttendance: (id) => api.get(`/attendance/${id}/can-edit`),
  getCompanyAttendance: (companyId) => api.get(`/attendance/company/${companyId}`),
  getPendingAttendanceByCompany: (companyId) => api.get(`/attendance/company/${companyId}/pending`),
  getAllPendingAttendance: () => api.get('/attendance/pending'),
  getEmployeeAttendance: (employeeId) => api.get(`/attendance/employee/${employeeId}`),
  approveAttendance: (data) => api.post('/attendance/approve', data),
};

// Leave APIs
export const leaveAPI = {
  applyForLeave: (data) => api.post('/leaves/apply', data),
  getMyLeaves: () => api.get('/leaves/my-leaves'),
  getCompanyLeaves: (companyId) => api.get(`/leaves/company/${companyId}`),
  getPendingLeaves: (companyId) => api.get(`/leaves/company/${companyId}/pending`),
  approveLeave: (data) => api.post('/leaves/approve', data),
};

// Profile Edit APIs
export const profileEditAPI = {
  requestEdit: (data) => api.post('/profile-edit/request', data),
  getMyRequests: () => api.get('/profile-edit/my-requests'),
  getCompanyRequests: (companyId) => api.get(`/profile-edit/company/${companyId}`),
  getPendingRequests: (companyId) => api.get(`/profile-edit/company/${companyId}/pending`),
  approveRequest: (data) => api.post('/profile-edit/approve', data),
};

// Bonus APIs
export const bonusAPI = {
  requestBonus: (data) => api.post('/bonus/request', data),
  getMyBonusRequests: () => api.get('/bonus/my-requests'),
  getCompanyBonusRequests: (companyId) => api.get(`/bonus/company/${companyId}`),
  getPendingBonusRequests: (companyId) => api.get(`/bonus/company/${companyId}/pending`),
  getAllPendingBonusRequests: () => api.get('/bonus/pending'),
  getAllBonusRequests: () => api.get('/bonus/all'),
  approveBonusRequest: (data) => api.post('/bonus/approve', data),
  getMyBonuses: () => api.get('/bonus/my-bonuses'),
  getEmployeeBonusRequests: (employeeId) => api.get(`/bonus/employee/${employeeId}`),
  getBonusRequestById: (requestId) => api.get(`/bonus/${requestId}`),
  deleteBonusRequest: (requestId) => api.delete(`/bonus/${requestId}`),
  getTotalApprovedBonus: (employeeId, month, year) => 
    api.get(`/bonus/employee/${employeeId}/total?month=${month}&year=${year}`),
  hasPendingBonus: (employeeId, month, year) => 
    api.get(`/bonus/employee/${employeeId}/pending?month=${month}&year=${year}`),
};

// Salary APIs
export const salaryAPI = {
  getMySalarySlips: () => api.get('/salary/my-slips'),
  getMySalarySlipsByYear: (year) => api.get(`/salary/my-slips/year/${year}`),
  getMySalarySlipForMonth: (month, year) => api.get(`/salary/my-slip/${month}/${year}`),
  downloadSalarySlip: (id) => api.get(`/salary/my-slip/${id}/download`, { responseType: 'blob' }),
  generateSlip: (data) => api.post('/salary/generate', data),
  generateBulkSlips: (data) => api.post('/salary/generate-bulk', data),
  getAllSlips: () => api.get('/salary/slips'),
  getSlipsByCompany: (companyId, month, year) => api.get(`/salary/company/${companyId}?month=${month}&year=${year}`),
  getSlipsByEmployee: (employeeId) => api.get(`/salary/slips/employee/${employeeId}`),
  getSlipById: (slipId) => api.get(`/salary/slips/${slipId}`),
  checkSlipExists: (employeeId, month, year) =>
    api.get(`/salary/exists?employeeId=${employeeId}&month=${month}&year=${year}`),
  downloadSlip: (slipId) => api.get(`/salary/slips/${slipId}/download`, { responseType: 'blob' }),
  sendSlipEmail: (slipId) => api.post(`/salary/slips/${slipId}/send-email`),
  deleteSlip: (slipId) => api.delete(`/salary/slips/${slipId}`),
  generateSalarySlip: (data) => api.post('/salary/generate', data),
  getCompanySalarySlips: (companyId, month, year) =>
    api.get(`/salary/company/${companyId}?month=${month}&year=${year}`),
  getAllSalarySlips: (month, year) => api.get(`/salary/month/${month}/${year}`),
  updateSlipStatus: (id, status) => api.put(`/salary/${id}/status`, { status }),
};

// File Upload APIs
export const uploadAPI = {
  uploadProfilePicture: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/upload/profile-picture', formData, {
      headers: { 'Content-Type': undefined }  // Let browser set correct multipart boundary
    });
  },
  uploadCompanyLogo: (file) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post('/upload/company-logo', formData, {
      headers: { 'Content-Type': undefined }  // Let browser set correct multipart boundary
    });
  },
  uploadDocument: (file, employeeId) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('employeeId', employeeId);
    return api.post('/upload/document', formData, {
      headers: { 'Content-Type': undefined }  // Let browser set correct multipart boundary
    });
  },
};

// Manager Project APIs
export const managerProjectAPI = {
  getMyProjects: () => api.get('/manager/projects'),
  getProjectsByStatus: (status) => api.get('/manager/projects/status/' + status),
  getProjectById: (projectId) => api.get('/manager/projects/' + projectId),
  createProject: (data) => api.post('/manager/projects', data),
  updateProject: (projectId, data) => api.put('/manager/projects/' + projectId, data),
  updateProjectStatus: (projectId, status) => api.patch('/manager/projects/' + projectId + '/status?status=' + status),
  deleteProject: (projectId) => api.delete('/manager/projects/' + projectId),
  addTeamMember: (projectId, data) => api.post('/manager/projects/' + projectId + '/team', data),
  removeTeamMember: (projectId, employeeId) => api.delete('/manager/projects/' + projectId + '/team/' + employeeId),
  updateTeamMember: (projectId, employeeId, data) => api.put('/manager/projects/' + projectId + '/team/' + employeeId, data),
  getAvailableEmployees: (projectId) => api.get('/manager/projects/' + projectId + '/available-employees'),
};

// ============================================
// NEW FEATURE APIs
// ============================================

// Employee Exit APIs
export const employeeExitAPI = {
  requestExit: (data) => api.post('/employee/exit/request', data),
  getMyExitDetails: () => api.get('/employee/exit/my-exit'),
};

// Admin Exit APIs  
export const adminExitAPI = {
  getAllExitEmployees: () => api.get('/admin/exit/all'),
  getActiveEmployees: () => api.get('/admin/exit/active'),
  getExitedEmployees: () => api.get('/admin/exit/exited'),
  getTerminatedEmployees: () => api.get('/admin/exit/terminated'),
  getPendingApprovals: () => api.get('/admin/exit/pending'),
};

// Manager Exit APIs
export const managerExitAPI = {
  getPendingExits: () => api.get('/manager/exit/pending'),
  approveExit: (data) => api.post('/exit/manager/approve', data),
  rejectExit: (data) => api.post('/exit/manager/reject', data),
};

// Exit Approval APIs
export const exitApprovalAPI = {
  managerApprove: (data) => api.post('/exit/manager/approve', data),
  adminApprove: (data) => api.post('/exit/admin/approve', data),
  getPendingForAdmin: () => api.get('/admin/exit/pending'),
};

// Employee Search APIs
export const employeeSearchAPI = {
  search: (term) => api.get(`/admin/employees/search?term=${encodeURIComponent(term)}`),
  getFullDetails: (id) => api.get(`/admin/employees/${id}/full-details`),
  getWorkHistory: (id) => api.get(`/admin/employees/${id}/work-history`),
  getAttendanceSummary: (id, from, to) => api.get(`/admin/employees/${id}/attendance-summary?from=${from}&to=${to}`),
  getLeaveSummary: (id) => api.get(`/admin/employees/${id}/leave-summary`),
  getProjectHistory: (id) => api.get(`/admin/employees/${id}/project-history`),
};

// Employee Document APIs
export const employeeDocumentAPI = {
  uploadDocument: (formData) => api.post('/employee/documents/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  getMyDocuments: () => api.get('/employee/documents/my-documents'),
  deleteDocument: (id) => api.delete(`/employee/documents/${id}`),
  requestEdit: (id, reason) => api.post(`/employee/documents/${id}/request-edit`, { reason }),
};

// Admin Document APIs
export const adminDocumentAPI = {
  getAllDocuments: () => api.get('/admin/documents/all'),
  getPendingDocuments: () => api.get('/admin/documents/pending'),
  getDocumentsByStatus: (status) => api.get(`/admin/documents/by-status/${status}`),
  approveDocument: (id, comments) => api.post(`/admin/documents/${id}/approve`, { comments }),
  rejectDocument: (id, comments) => api.post(`/admin/documents/${id}/reject`, { comments }),
};

// Resignation APIs (Candidate Portal)
export const resignationAPI = {
  submitRequest: (data) => api.post('/resignation/submit', data),
  getMyResignation: () => api.get('/resignation/my-requests'),
  getPendingForManager: () => api.get('/resignation/pending/manager'),
  getPendingForAdmin: (companyId) => api.get(`/resignation/pending/admin/${companyId}`),
  managerApprove: (data) => api.post('/resignation/approve/manager', data),
  adminApprove: (data) => api.post('/resignation/approve/admin', data),
  cancelRequest: (id) => api.post(`/resignation/${id}/cancel`),
};

// BGV (Background Verification) APIs
export const bgvAPI = {
  // Admin
  initiateBGV: (data) => api.post('/bgv/initiate', data),
  getCompanyBGVRequests: () => api.get('/bgv/company-requests'),
  getPendingBGV: () => api.get('/bgv/pending'),
  getBGVDocuments: (bgvRequestId) => api.get(`/bgv/documents/${bgvRequestId}`),
  verifyDocument: (data) => api.post('/bgv/verify-document', data),
  completeVerification: (bgvRequestId, data) => api.post(`/bgv/complete/${bgvRequestId}`, data),
  
  // Employee
  getMyBGVStatus: () => api.get('/bgv/my-status'),
  getMyDocuments: (bgvRequestId) => api.get(`/bgv/my-documents/${bgvRequestId}`),
  uploadDocument: (formData) => api.post('/bgv/upload-document', formData, {
    headers: { 'Content-Type': undefined }
  }),
  submitForVerification: (bgvRequestId) => api.post(`/bgv/submit/${bgvRequestId}`),
};

// HR Policy APIs
export const hrPolicyAPI = {
  // Admin
  createPolicy: (data) => api.post('/policies', data),
  getAllPolicies: () => api.get('/policies'),
  getAllActivePolicies: () => api.get('/policies/active'),
  getPoliciesByType: (type) => api.get(`/policies/type/${type}`),
  assignPolicyToEmployee: (data) => api.post('/policies/assign', data),
  
  // Employee
  getMyPendingPolicies: () => api.get('/policies/my-pending'),
  getMyAcknowledgedPolicies: () => api.get('/policies/my-acknowledged'),
  getMyAllPolicies: () => api.get('/policies/my-all'),
  acknowledgePolicy: (acknowledgmentId) => api.post(`/policies/acknowledge/${acknowledgmentId}`, {})
};

// Notification APIs
export const notificationAPI = {
  getMyNotifications: () => api.get('/notifications'),
  getUnreadNotifications: () => api.get('/notifications/unread'),
  getUnreadCount: () => api.get('/notifications/count'),
  markAsRead: (id) => api.post(`/notifications/${id}/read`),
  markAllAsRead: () => api.post('/notifications/read-all'),
  deleteNotification: (id) => api.delete(`/notifications/${id}`),
};

export default api;
