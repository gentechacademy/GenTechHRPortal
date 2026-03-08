import React, { useState, useEffect } from 'react';
import { Routes, Route, Link, useLocation } from 'react-router-dom';
import Navbar from '../components/Navbar';
import { superAdminAPI, bonusAPI } from '../services/api';
import { toast } from 'react-toastify';

const SuperAdminDashboard = () => {
  return (
    <div>
      <Navbar title="Super Admin Dashboard" />
      <div style={styles.container}>
        <Sidebar />
        <div style={styles.content}>
          <Routes>
            <Route path="/" element={<DashboardHome />} />
            <Route path="/admins" element={<ManageAdmins />} />
            <Route path="/companies" element={<ManageCompanies />} />
            <Route path="/bonus-approvals" element={<BonusApprovals />} />
          </Routes>
        </div>
      </div>
    </div>
  );
};

const Sidebar = () => {
  const location = useLocation();
  
  const menuItems = [
    { path: '/superadmin', label: 'Dashboard', icon: '📊' },
    { path: '/superadmin/admins', label: 'Manage Admins', icon: '👤' },
    { path: '/superadmin/companies', label: 'Manage Companies', icon: '🏢' },
    { path: '/superadmin/bonus-approvals', label: 'Bonus Approvals', icon: '🎁' },
  ];

  return (
    <div style={styles.sidebar}>
      {menuItems.map((item) => (
        <Link
          key={item.path}
          to={item.path}
          style={{
            ...styles.menuItem,
            backgroundColor: location.pathname === item.path ? '#3498db' : 'transparent',
            color: location.pathname === item.path ? 'white' : '#333',
          }}
        >
          <span>{item.icon}</span> {item.label}
        </Link>
      ))}
    </div>
  );
};

const DashboardHome = () => {
  const [stats, setStats] = useState({ admins: 0, companies: 0 });

  useEffect(() => {
    loadStats();
  }, []);

  const loadStats = async () => {
    try {
      const [adminsRes, companiesRes] = await Promise.all([
        superAdminAPI.getAllAdmins(),
        superAdminAPI.getAllCompanies(),
      ]);
      setStats({
        admins: adminsRes.data.length,
        companies: companiesRes.data.length,
      });
    } catch (error) {
      toast.error('Failed to load statistics');
    }
  };

  return (
    <div>
      <h2 style={styles.pageTitle}>Dashboard Overview</h2>
      <div style={styles.statsGrid}>
        <div style={{...styles.statCard, backgroundColor: '#3498db'}}>
          <h3 style={styles.statNumber}>{stats.admins}</h3>
          <p>Total Admins</p>
        </div>
        <div style={{...styles.statCard, backgroundColor: '#27ae60'}}>
          <h3 style={styles.statNumber}>{stats.companies}</h3>
          <p>Total Companies</p>
        </div>
      </div>
    </div>
  );
};

const ManageAdmins = () => {
  const [admins, setAdmins] = useState([]);
  const [companies, setCompanies] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    email: '',
    fullName: '',
    companyId: '',
  });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [adminsRes, companiesRes] = await Promise.all([
        superAdminAPI.getAllAdmins(),
        superAdminAPI.getAllCompanies(),
      ]);
      setAdmins(adminsRes.data);
      setCompanies(companiesRes.data);
    } catch (error) {
      toast.error('Failed to load data');
    }
  };

  const resetForm = () => {
    setFormData({ username: '', password: '', email: '', fullName: '', companyId: '' });
    setEditingId(null);
    setShowForm(false);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await superAdminAPI.updateAdmin(editingId, {
          ...formData,
          role: 'ADMIN',
        });
        toast.success('Admin updated successfully');
      } else {
        await superAdminAPI.createAdmin({
          ...formData,
          role: 'ADMIN',
        });
        toast.success('Admin created successfully');
      }
      resetForm();
      loadData();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to save admin');
    }
  };

  const handleEdit = (admin) => {
    setFormData({
      username: admin.username,
      password: '',
      email: admin.email,
      fullName: admin.fullName,
      companyId: admin.company?.id || '',
    });
    setEditingId(admin.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this admin?')) return;
    try {
      await superAdminAPI.deleteAdmin(id);
      toast.success('Admin deleted successfully');
      loadData();
    } catch (error) {
      toast.error('Failed to delete admin');
    }
  };

  return (
    <div>
      <div style={styles.header}>
        <h2 style={styles.pageTitle}>Manage Admins</h2>
        <button style={styles.addBtn} onClick={() => showForm ? resetForm() : setShowForm(true)}>
          {showForm ? 'Cancel' : '+ Add Admin'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} style={styles.formContainer}>
          <h3 style={styles.sectionTitle}>{editingId ? '✏️ Edit Admin' : '👤 Admin Details'}</h3>
          <div style={styles.formGrid}>
            <div style={styles.formField}>
              <label style={styles.label}>Username *</label>
              <input
                type="text"
                placeholder="Enter username"
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                style={styles.input}
                required
              />
            </div>
            <div style={styles.formField}>
              <label style={styles.label}>Password {editingId ? '(optional)' : '*'}</label>
              <input
                type="password"
                placeholder={editingId ? "Leave blank to keep current" : "Enter password"}
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                style={styles.input}
                required={!editingId}
              />
            </div>
            <div style={styles.formField}>
              <label style={styles.label}>Email *</label>
              <input
                type="email"
                placeholder="Enter email address"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                style={styles.input}
                required
              />
            </div>
            <div style={styles.formField}>
              <label style={styles.label}>Full Name *</label>
              <input
                type="text"
                placeholder="Enter full name"
                value={formData.fullName}
                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                style={styles.input}
                required
              />
            </div>
            <div style={styles.formField}>
              <label style={styles.label}>Company *</label>
              <select
                value={formData.companyId}
                onChange={(e) => setFormData({ ...formData, companyId: e.target.value })}
                style={styles.input}
                required
              >
                <option value="">Select Company</option>
                {companies.map((c) => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
            </div>
          </div>
          <button type="submit" style={styles.submitBtn}>
            {editingId ? 'Update Admin' : 'Create Admin'}
          </button>
        </form>
      )}

      <div style={styles.tableContainer}>
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>ID</th>
              <th style={styles.th}>Username</th>
              <th style={styles.th}>Full Name</th>
              <th style={styles.th}>Email</th>
              <th style={styles.th}>Company</th>
              <th style={styles.th}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {admins.map((admin) => (
              <tr key={admin.id}>
                <td style={styles.td}>{admin.id}</td>
                <td style={styles.td}>{admin.username}</td>
                <td style={styles.td}>{admin.fullName}</td>
                <td style={styles.td}>{admin.email}</td>
                <td style={styles.td}>{admin.company?.name || '-'}</td>
                <td style={styles.td}>
                  <button style={styles.editBtn} onClick={() => handleEdit(admin)}>Edit</button>
                  <button style={styles.deleteBtn} onClick={() => handleDelete(admin.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

const ManageCompanies = () => {
  const [companies, setCompanies] = useState([]);
  const [showForm, setShowForm] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    logoUrl: '',
    address: '',
    phone: '',
    email: '',
  });
  const [phoneError, setPhoneError] = useState('');

  useEffect(() => {
    loadCompanies();
  }, []);

  const loadCompanies = async () => {
    try {
      const res = await superAdminAPI.getAllCompanies();
      setCompanies(res.data);
    } catch (error) {
      toast.error('Failed to load companies');
    }
  };

  const resetForm = () => {
    setFormData({ name: '', logoUrl: '', address: '', phone: '', email: '' });
    setEditingId(null);
    setPhoneError('');
    setShowForm(false);
  };

  const validatePhone = (phone) => {
    if (!phone || phone.trim() === '') return true;
    // Allow + for country code, and -, spaces, () as separators
    // Must start with optional +, then contain 10-15 digits total
    const digitsOnly = phone.replace(/\D/g, '');
    const validFormat = /^[+]?[\d\-\(\)\s.]+$/.test(phone.trim());
    return validFormat && digitsOnly.length >= 10 && digitsOnly.length <= 15;
  };

  const handlePhoneChange = (e) => {
    const value = e.target.value;
    setFormData({ ...formData, phone: value });
    if (!validatePhone(value)) {
      setPhoneError('Phone number must be 10-15 digits (e.g., +1-555-123-4567)');
    } else {
      setPhoneError('');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!validatePhone(formData.phone)) {
      toast.error('Phone number must be 10-15 digits (e.g., +1-555-123-4567)');
      return;
    }

    try {
      if (editingId) {
        await superAdminAPI.updateCompany(editingId, formData);
        toast.success('Company updated successfully');
      } else {
        await superAdminAPI.createCompany(formData);
        toast.success('Company created successfully');
      }
      resetForm();
      loadCompanies();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to save company');
    }
  };

  const handleEdit = (company) => {
    setFormData({
      name: company.name,
      logoUrl: company.logoUrl || '',
      address: company.address || '',
      phone: company.phone || '',
      email: company.email || '',
    });
    setEditingId(company.id);
    setShowForm(true);
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this company?')) return;
    try {
      await superAdminAPI.deleteCompany(id);
      toast.success('Company deleted successfully');
      loadCompanies();
    } catch (error) {
      toast.error('Failed to delete company');
    }
  };

  return (
    <div>
      <div style={styles.header}>
        <h2 style={styles.pageTitle}>Manage Companies</h2>
        <button style={styles.addBtn} onClick={() => showForm ? resetForm() : setShowForm(true)}>
          {showForm ? 'Cancel' : '+ Add Company'}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} style={styles.formContainer}>
          <h3 style={styles.sectionTitle}>{editingId ? '✏️ Edit Company' : '🏢 Company Details'}</h3>
          <div style={styles.formGrid}>
            <div style={styles.formField}>
              <label style={styles.label}>Company Name *</label>
              <input
                type="text"
                placeholder="Enter company name"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                style={styles.input}
                required
              />
            </div>
            <div style={styles.formField}>
              <label style={styles.label}>Logo URL</label>
              <input
                type="text"
                placeholder="Enter logo URL (optional)"
                value={formData.logoUrl}
                onChange={(e) => setFormData({ ...formData, logoUrl: e.target.value })}
                style={styles.input}
              />
            </div>
            <div style={styles.formField}>
              <label style={styles.label}>Address</label>
              <input
                type="text"
                placeholder="Enter company address"
                value={formData.address}
                onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                style={styles.input}
              />
            </div>
            <div style={styles.formField}>
              <label style={styles.label}>Phone (10-15 digits)</label>
              <input
                type="text"
                placeholder="e.g., +1-555-123-4567"
                value={formData.phone}
                onChange={handlePhoneChange}
                style={{...styles.input, borderColor: phoneError ? '#e74c3c' : undefined}}
              />
              {phoneError && <span style={styles.errorText}>{phoneError}</span>}
            </div>
            <div style={styles.formField}>
              <label style={styles.label}>Email</label>
              <input
                type="email"
                placeholder="Enter company email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                style={styles.input}
              />
            </div>
          </div>
          <button type="submit" style={styles.submitBtn}>
            {editingId ? 'Update Company' : 'Create Company'}
          </button>
        </form>
      )}

      <div style={styles.tableContainer}>
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>ID</th>
              <th style={styles.th}>Name</th>
              <th style={styles.th}>Logo</th>
              <th style={styles.th}>Address</th>
              <th style={styles.th}>Phone</th>
              <th style={styles.th}>Email</th>
              <th style={styles.th}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {companies.map((company) => (
              <tr key={company.id}>
                <td style={styles.td}>{company.id}</td>
                <td style={styles.td}>{company.name}</td>
                <td style={styles.td}>
                  {company.logoUrl ? (
                    <img 
                      src={company.logoUrl} 
                      alt="Logo" 
                      style={styles.logo}
                      onError={(e) => {
                        e.target.style.display = 'none';
                        e.target.nextSibling.style.display = 'flex';
                      }}
                    />
                  ) : null}
                  <div style={{
                    ...styles.logoPlaceholder,
                    display: company.logoUrl ? 'none' : 'flex'
                  }}>
                    {company.name.charAt(0).toUpperCase()}
                  </div>
                </td>
                <td style={styles.td}>{company.address || '-'}</td>
                <td style={styles.td}>{company.phone || '-'}</td>
                <td style={styles.td}>{company.email || '-'}</td>
                <td style={styles.td}>
                  <button style={styles.editBtn} onClick={() => handleEdit(company)}>Edit</button>
                  <button style={styles.deleteBtn} onClick={() => handleDelete(company.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

// Bonus Approvals Component
const BonusApprovals = () => {
  const [bonusRequests, setBonusRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [processingId, setProcessingId] = useState(null);
  const [filterStatus, setFilterStatus] = useState('PENDING');
  const [showRejectModal, setShowRejectModal] = useState(false);
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [rejectionReason, setRejectionReason] = useState('');

  const months = [
    { value: 1, label: 'January' },
    { value: 2, label: 'February' },
    { value: 3, label: 'March' },
    { value: 4, label: 'April' },
    { value: 5, label: 'May' },
    { value: 6, label: 'June' },
    { value: 7, label: 'July' },
    { value: 8, label: 'August' },
    { value: 9, label: 'September' },
    { value: 10, label: 'October' },
    { value: 11, label: 'November' },
    { value: 12, label: 'December' },
  ];

  useEffect(() => {
    loadBonusRequests();
  }, []);

  const loadBonusRequests = async () => {
    try {
      const res = await bonusAPI.getAllBonusRequests();
      setBonusRequests(res.data || []);
    } catch (error) {
      toast.error('Failed to load bonus requests');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (requestId) => {
    setProcessingId(requestId);
    try {
      await bonusAPI.approveBonusRequest({
        requestId,
        approved: true,
      });
      toast.success('Bonus request approved!');
      loadBonusRequests();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to approve bonus request');
    } finally {
      setProcessingId(null);
    }
  };

  const handleRejectClick = (request) => {
    setSelectedRequest(request);
    setShowRejectModal(true);
  };

  const handleReject = async () => {
    if (!rejectionReason.trim()) {
      toast.error('Please provide a rejection reason');
      return;
    }

    setProcessingId(selectedRequest.id);
    try {
      await bonusAPI.approveBonusRequest({
        requestId: selectedRequest.id,
        approved: false,
        rejectionReason,
      });
      toast.success('Bonus request rejected');
      setShowRejectModal(false);
      setRejectionReason('');
      setSelectedRequest(null);
      loadBonusRequests();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to reject bonus request');
    } finally {
      setProcessingId(null);
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'APPROVED': return '#27ae60';
      case 'PENDING': return '#f39c12';
      case 'REJECTED': return '#e74c3c';
      default: return '#7f8c8d';
    }
  };

  const filteredRequests = bonusRequests.filter(record =>
    filterStatus === 'ALL' || record.status === filterStatus
  );

  const pendingCount = bonusRequests.filter(r => r.status === 'PENDING').length;

  if (loading) {
    return <div style={styles.loading}>Loading bonus requests...</div>;
  }

  return (
    <div>
      <div style={styles.header}>
        <h2 style={styles.pageTitle}>🎁 Bonus Approvals</h2>
        {pendingCount > 0 && (
          <span style={styles.badge}>
            {pendingCount} Pending
          </span>
        )}
      </div>

      {/* Filter Tabs */}
      <div style={styles.filterTabs}>
        {['ALL', 'PENDING', 'APPROVED', 'REJECTED'].map((status) => (
          <button
            key={status}
            onClick={() => setFilterStatus(status)}
            style={{
              ...styles.filterTab,
              backgroundColor: filterStatus === status ? '#3498db' : 'white',
              color: filterStatus === status ? 'white' : '#333',
            }}
          >
            {status}
          </button>
        ))}
      </div>

      {/* Bonus Requests Table */}
      <div style={styles.tableContainer}>
        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>Employee</th>
              <th style={styles.th}>Company</th>
              <th style={styles.th}>Amount</th>
              <th style={styles.th}>Period</th>
              <th style={styles.th}>Reason</th>
              <th style={styles.th}>Requested By</th>
              <th style={styles.th}>Status</th>
              <th style={styles.th}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredRequests.length === 0 ? (
              <tr>
                <td colSpan="8" style={styles.noDataCell}>
                  No bonus requests found
                </td>
              </tr>
            ) : (
              filteredRequests.map((request) => (
                <tr key={request.id} style={styles.tr}>
                  <td style={styles.td}>
                    <strong>{request.employeeName}</strong>
                  </td>
                  <td style={styles.td}>
                    {request.companyName}
                  </td>
                  <td style={styles.td}>
                    <strong style={{ color: '#27ae60' }}>
                      ₹{request.amount?.toLocaleString()}
                    </strong>
                  </td>
                  <td style={styles.td}>
                    {months.find(m => m.value === request.month)?.label} {request.year}
                  </td>
                  <td style={styles.td} title={request.reason}>
                    {request.reason?.length > 30 
                      ? request.reason.substring(0, 30) + '...' 
                      : request.reason}
                  </td>
                  <td style={styles.td}>
                    {request.requestedByName || 'Admin'}
                  </td>
                  <td style={styles.td}>
                    <span style={{
                      ...styles.statusBadge,
                      backgroundColor: getStatusColor(request.status),
                    }}>
                      {request.status}
                    </span>
                  </td>
                  <td style={styles.td}>
                    {request.status === 'PENDING' && (
                      <div style={styles.actionButtons}>
                        <button
                          onClick={() => handleApprove(request.id)}
                          disabled={processingId === request.id}
                          style={{
                            ...styles.approveBtn,
                            opacity: processingId === request.id ? 0.5 : 1,
                          }}
                          title="Approve"
                        >
                          {processingId === request.id ? '...' : '✓'}
                        </button>
                        <button
                          onClick={() => handleRejectClick(request)}
                          disabled={processingId === request.id}
                          style={{
                            ...styles.rejectBtn,
                            opacity: processingId === request.id ? 0.5 : 1,
                          }}
                          title="Reject"
                        >
                          ✕
                        </button>
                      </div>
                    )}
                    {request.status === 'APPROVED' && (
                      <span style={{ color: '#27ae60', fontSize: '12px' }}>
                        By: {request.approvedByName}
                      </span>
                    )}
                    {request.status === 'REJECTED' && (
                      <span style={{ color: '#e74c3c', fontSize: '12px' }} title={request.rejectionReason}>
                        {request.rejectionReason?.substring(0, 20) || 'No reason'}...
                      </span>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Rejection Modal */}
      {showRejectModal && (
        <div style={styles.modalOverlay}>
          <div style={styles.modal}>
            <h3 style={styles.modalTitle}>Reject Bonus Request</h3>
            <p>Employee: <strong>{selectedRequest?.employeeName}</strong></p>
            <p>Amount: <strong>₹{selectedRequest?.amount?.toLocaleString()}</strong></p>
            <textarea
              placeholder="Enter rejection reason..."
              value={rejectionReason}
              onChange={(e) => setRejectionReason(e.target.value)}
              style={styles.modalTextarea}
            />
            <div style={styles.modalButtons}>
              <button onClick={() => setShowRejectModal(false)} style={styles.modalCancelBtn}>
                Cancel
              </button>
              <button onClick={handleReject} style={styles.modalRejectBtn}>
                Reject
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

const styles = {
  container: {
    display: 'flex',
    minHeight: 'calc(100vh - 70px)',
  },
  sidebar: {
    width: '250px',
    backgroundColor: '#f8f9fa',
    padding: '20px',
    borderRight: '1px solid #dee2e6',
  },
  menuItem: {
    display: 'block',
    padding: '12px 15px',
    borderRadius: '5px',
    textDecoration: 'none',
    marginBottom: '5px',
    transition: 'all 0.3s',
  },
  content: {
    flex: 1,
    padding: '30px',
    backgroundColor: '#f5f6fa',
  },
  pageTitle: {
    marginBottom: '20px',
    color: '#2c3e50',
  },
  statsGrid: {
    display: 'flex',
    gap: '20px',
  },
  statCard: {
    padding: '30px',
    borderRadius: '10px',
    color: 'white',
    minWidth: '200px',
    textAlign: 'center',
  },
  statNumber: {
    fontSize: '36px',
    margin: '0 0 10px 0',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '20px',
  },
  addBtn: {
    backgroundColor: '#27ae60',
    color: 'white',
    border: 'none',
    padding: '10px 20px',
    borderRadius: '5px',
    cursor: 'pointer',
  },
  formContainer: {
    backgroundColor: 'white',
    padding: '30px',
    borderRadius: '12px',
    marginBottom: '25px',
    boxShadow: '0 4px 15px rgba(0,0,0,0.08)',
  },
  sectionTitle: {
    color: '#3498db',
    marginBottom: '20px',
    fontSize: '18px',
    fontWeight: '600',
    display: 'flex',
    alignItems: 'center',
    gap: '8px',
  },
  formGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))',
    gap: '20px',
  },
  formField: {
    display: 'flex',
    flexDirection: 'column',
    gap: '6px',
  },
  label: {
    fontSize: '13px',
    fontWeight: '600',
    color: '#555',
    textTransform: 'uppercase',
    letterSpacing: '0.3px',
  },
  input: {
    padding: '12px 14px',
    border: '2px solid #e0e0e0',
    borderRadius: '8px',
    fontSize: '14px',
    outline: 'none',
    backgroundColor: '#fafafa',
  },
  errorText: {
    color: '#e74c3c',
    fontSize: '12px',
  },
  submitBtn: {
    backgroundColor: '#3498db',
    color: 'white',
    border: 'none',
    padding: '14px 30px',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '16px',
    fontWeight: '600',
    width: '100%',
    marginTop: '20px',
    boxShadow: '0 4px 10px rgba(52, 152, 219, 0.3)',
  },
  tableContainer: {
    backgroundColor: 'white',
    borderRadius: '12px',
    boxShadow: '0 4px 15px rgba(0,0,0,0.08)',
    overflow: 'hidden',
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse',
  },
  th: {
    backgroundColor: '#34495e',
    color: 'white',
    padding: '12px',
    textAlign: 'left',
  },
  td: {
    padding: '12px',
    borderBottom: '1px solid #eee',
  },
  editBtn: {
    backgroundColor: '#f39c12',
    color: 'white',
    border: 'none',
    padding: '5px 10px',
    borderRadius: '3px',
    cursor: 'pointer',
    marginRight: '5px',
  },
  deleteBtn: {
    backgroundColor: '#e74c3c',
    color: 'white',
    border: 'none',
    padding: '5px 10px',
    borderRadius: '3px',
    cursor: 'pointer',
  },
  logo: {
    width: '50px',
    height: '50px',
    objectFit: 'contain',
    borderRadius: '5px',
  },
  logoPlaceholder: {
    width: '50px',
    height: '50px',
    borderRadius: '5px',
    backgroundColor: '#3498db',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '24px',
    color: 'white',
    fontWeight: 'bold',
  },
  badge: {
    backgroundColor: '#e74c3c',
    color: 'white',
    padding: '5px 15px',
    borderRadius: '20px',
    fontSize: '14px',
    fontWeight: '600',
  },
  filterTabs: {
    display: 'flex',
    gap: '10px',
    marginBottom: '20px',
  },
  filterTab: {
    padding: '10px 20px',
    border: '1px solid #ddd',
    borderRadius: '5px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500',
  },
  statusBadge: {
    padding: '6px 12px',
    borderRadius: '20px',
    fontSize: '12px',
    fontWeight: '600',
    color: 'white',
    textTransform: 'uppercase',
  },
  noDataCell: {
    textAlign: 'center',
    padding: '30px',
    color: '#7f8c8d',
  },
  actionButtons: {
    display: 'flex',
    gap: '5px',
  },
  approveBtn: {
    backgroundColor: '#27ae60',
    color: 'white',
    border: 'none',
    padding: '6px 12px',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
  },
  rejectBtn: {
    backgroundColor: '#e74c3c',
    color: 'white',
    border: 'none',
    padding: '6px 12px',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
  },
  loading: {
    textAlign: 'center',
    padding: '50px',
    fontSize: '18px',
    color: '#666',
  },
  modalOverlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0,0,0,0.5)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 1000,
  },
  modal: {
    backgroundColor: 'white',
    padding: '30px',
    borderRadius: '10px',
    width: '400px',
    maxWidth: '90%',
  },
  modalTitle: {
    marginBottom: '15px',
    color: '#2c3e50',
  },
  modalTextarea: {
    width: '100%',
    padding: '10px',
    border: '1px solid #ddd',
    borderRadius: '5px',
    fontSize: '14px',
    minHeight: '100px',
    resize: 'vertical',
    marginTop: '15px',
  },
  modalButtons: {
    display: 'flex',
    gap: '10px',
    marginTop: '20px',
  },
  modalCancelBtn: {
    flex: 1,
    padding: '10px',
    border: '1px solid #ddd',
    borderRadius: '5px',
    backgroundColor: 'white',
    cursor: 'pointer',
  },
  modalRejectBtn: {
    flex: 1,
    padding: '10px',
    border: 'none',
    borderRadius: '5px',
    backgroundColor: '#e74c3c',
    color: 'white',
    cursor: 'pointer',
  },
};

export default SuperAdminDashboard;
