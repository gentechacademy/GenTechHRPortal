import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { employeeSearchAPI } from '../services/api';
import { toast } from 'react-toastify';

const EmployeeDetailsPage = () => {
  const { id } = useParams();
  const [employee, setEmployee] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeSection, setActiveSection] = useState('basic');

  useEffect(() => {
    fetchEmployeeDetails();
  }, [id]);

  const fetchEmployeeDetails = async () => {
    setLoading(true);
    try {
      const response = await employeeSearchAPI.getFullDetails(id);
      setEmployee(response.data);
    } catch (error) {
      toast.error('Failed to fetch employee details');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <div style={styles.loading}>Loading...</div>;
  if (!employee) return <div style={styles.error}>Employee not found</div>;

  const sections = [
    { id: 'basic', label: 'Basic Info' },
    { id: 'employment', label: 'Employment Status' },
    { id: 'attendance', label: 'Attendance' },
    { id: 'leaves', label: 'Leaves' },
    { id: 'projects', label: 'Projects' },
    { id: 'salary', label: 'Salary' },
  ];

  const renderBasicInfo = () => (
    <div style={styles.section}>
      <h3 style={styles.sectionTitle}>Basic Information</h3>
      <div style={styles.grid}>
        <InfoItem label="Full Name" value={employee.fullName} />
        <InfoItem label="Employee Code" value={employee.employeeCode} />
        <InfoItem label="Email" value={employee.email} />
        <InfoItem label="Phone" value={employee.phoneNumber} />
        <InfoItem label="Department" value={employee.department} />
        <InfoItem label="Designation" value={employee.designation} />
        <InfoItem label="Date of Joining" value={employee.dateOfJoining} />
        <InfoItem label="Date of Birth" value={employee.dateOfBirth} />
        <InfoItem label="Address" value={employee.address} />
        <InfoItem label="Emergency Contact" value={employee.emergencyContact} />
      </div>
    </div>
  );

  const renderEmploymentStatus = () => (
    <div style={styles.section}>
      <h3 style={styles.sectionTitle}>Employment Status</h3>
      <div style={styles.grid}>
        <InfoItem 
          label="Current Status" 
          value={employee.isActive ? 'Active' : (employee.exitDetails?.employmentStatus || 'Inactive')} 
        />
        <InfoItem label="Last Working Day" value={employee.lastWorkingDate || '-'} />
        {employee.exitDetails && (
          <>
            <InfoItem label="Exit Reason" value={employee.exitDetails.reason} />
            <InfoItem label="Notice Period" value={`${employee.exitDetails.noticePeriodDays} days`} />
            <InfoItem label="Resignation Date" value={employee.exitDetails.exitDate} />
            <InfoItem label="Manager Approval" value={employee.exitDetails.managerApprovalStatus} />
            <InfoItem label="Admin Approval" value={employee.exitDetails.adminApprovalStatus} />
          </>
        )}
      </div>
    </div>
  );

  const renderAttendance = () => (
    <div style={styles.section}>
      <h3 style={styles.sectionTitle}>Attendance Summary</h3>
      {employee.attendanceSummary ? (
        <div style={styles.grid}>
          <InfoItem label="Total Days" value={employee.attendanceSummary.totalDays} />
          <InfoItem label="Present Days" value={employee.attendanceSummary.presentDays} />
          <InfoItem label="Absent Days" value={employee.attendanceSummary.absentDays} />
          <InfoItem label="Leave Days" value={employee.attendanceSummary.leaveDays} />
          <InfoItem label="Work From Home" value={employee.attendanceSummary.workFromHomeDays} />
          <InfoItem label="Attendance %" value={`${employee.attendanceSummary.attendancePercentage?.toFixed(2)}%`} />
        </div>
      ) : (
        <p style={styles.noData}>No attendance data available</p>
      )}
    </div>
  );

  const renderLeaves = () => (
    <div style={styles.section}>
      <h3 style={styles.sectionTitle}>Leave Summary</h3>
      {employee.leaveSummary ? (
        <div style={styles.grid}>
          <InfoItem label="Total Leaves" value={employee.leaveSummary.totalLeaves} />
          <InfoItem label="Approved" value={employee.leaveSummary.approvedLeaves} />
          <InfoItem label="Pending" value={employee.leaveSummary.pendingLeaves} />
          <InfoItem label="Rejected" value={employee.leaveSummary.rejectedLeaves} />
          <InfoItem label="Sick Leaves" value={employee.leaveSummary.sickLeaves} />
          <InfoItem label="Privilege Leaves" value={employee.leaveSummary.privilegeLeaves} />
        </div>
      ) : (
        <p style={styles.noData}>No leave data available</p>
      )}
    </div>
  );

  const renderProjects = () => (
    <div style={styles.section}>
      <h3 style={styles.sectionTitle}>Project History</h3>
      {employee.projects && employee.projects.length > 0 ? (
        <div style={styles.list}>
          {employee.projects.map((project, idx) => (
            <div key={idx} style={styles.card}>
              <h4 style={styles.cardTitle}>{project.projectName}</h4>
              <p style={styles.cardDesc}>{project.description}</p>
              <div style={styles.cardMeta}>
                <span>Role: {project.roleInProject}</span>
                <span>Status: {project.status}</span>
                <span>Allocation: {project.allocationPercentage}%</span>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <p style={styles.noData}>No project history available</p>
      )}
    </div>
  );

  const renderSalary = () => (
    <div style={styles.section}>
      <h3 style={styles.sectionTitle}>Salary Information</h3>
      <div style={styles.grid}>
        <InfoItem label="Current Salary" value={employee.salary ? `₹${employee.salary.toLocaleString()}` : '-'} />
        {employee.salarySlipSummary && (
          <>
            <InfoItem label="Total Slips" value={employee.salarySlipSummary.totalSlips} />
            <InfoItem label="Last Salary" value={employee.salarySlipSummary.lastSalary || '-'} />
          </>
        )}
      </div>
    </div>
  );

  const renderContent = () => {
    switch (activeSection) {
      case 'basic': return renderBasicInfo();
      case 'employment': return renderEmploymentStatus();
      case 'attendance': return renderAttendance();
      case 'leaves': return renderLeaves();
      case 'projects': return renderProjects();
      case 'salary': return renderSalary();
      default: return renderBasicInfo();
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.heading}>Employee Details</h2>
      
      <div style={styles.layout}>
        {/* Sidebar */}
        <div style={styles.sidebar}>
          {sections.map(section => (
            <button
              key={section.id}
              onClick={() => setActiveSection(section.id)}
              style={{
                ...styles.navButton,
                ...(activeSection === section.id ? styles.activeNav : {})
              }}
            >
              {section.label}
            </button>
          ))}
        </div>

        {/* Content */}
        <div style={styles.content}>
          {renderContent()}
        </div>
      </div>
    </div>
  );
};

const InfoItem = ({ label, value }) => (
  <div style={styles.infoItem}>
    <span style={styles.infoLabel}>{label}</span>
    <span style={styles.infoValue}>{value || '-'}</span>
  </div>
);

const styles = {
  container: {
    padding: '20px',
    maxWidth: '1200px',
    margin: '0 auto',
  },
  heading: {
    fontSize: '24px',
    marginBottom: '20px',
    color: '#333',
  },
  layout: {
    display: 'flex',
    gap: '20px',
  },
  sidebar: {
    width: '200px',
    flexShrink: 0,
  },
  navButton: {
    display: 'block',
    width: '100%',
    padding: '12px 16px',
    marginBottom: '8px',
    border: 'none',
    background: 'white',
    textAlign: 'left',
    cursor: 'pointer',
    borderRadius: '4px',
    fontSize: '14px',
    color: '#666',
    transition: 'all 0.2s',
  },
  activeNav: {
    background: '#667eea',
    color: 'white',
  },
  content: {
    flex: 1,
    background: 'white',
    borderRadius: '8px',
    padding: '24px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  section: {
    animation: 'fadeIn 0.3s ease',
  },
  sectionTitle: {
    fontSize: '18px',
    marginBottom: '20px',
    color: '#333',
    borderBottom: '2px solid #667eea',
    paddingBottom: '8px',
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
    gap: '16px',
  },
  infoItem: {
    display: 'flex',
    flexDirection: 'column',
    padding: '12px',
    background: '#f8f9fa',
    borderRadius: '4px',
  },
  infoLabel: {
    fontSize: '12px',
    color: '#666',
    marginBottom: '4px',
  },
  infoValue: {
    fontSize: '14px',
    fontWeight: '600',
    color: '#333',
  },
  noData: {
    color: '#999',
    fontStyle: 'italic',
  },
  list: {
    display: 'flex',
    flexDirection: 'column',
    gap: '12px',
  },
  card: {
    padding: '16px',
    background: '#f8f9fa',
    borderRadius: '4px',
    border: '1px solid #e0e0e0',
  },
  cardTitle: {
    fontSize: '16px',
    fontWeight: '600',
    marginBottom: '8px',
    color: '#333',
  },
  cardDesc: {
    fontSize: '14px',
    color: '#666',
    marginBottom: '12px',
  },
  cardMeta: {
    display: 'flex',
    gap: '16px',
    fontSize: '12px',
    color: '#999',
  },
  loading: {
    padding: '40px',
    textAlign: 'center',
    color: '#666',
  },
  error: {
    padding: '40px',
    textAlign: 'center',
    color: '#dc3545',
  },
};

export default EmployeeDetailsPage;
