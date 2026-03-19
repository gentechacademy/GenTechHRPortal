import React, { useState, useEffect } from 'react';
import { adminExitAPI } from '../services/api';
import { toast } from 'react-toastify';

const ExitEmployeesPage = () => {
  const [activeTab, setActiveTab] = useState('all');
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);

  const tabs = [
    { id: 'all', label: 'All Employees', api: adminExitAPI.getAllExitEmployees },
    { id: 'active', label: 'Active Employees', api: adminExitAPI.getActiveEmployees },
    { id: 'exited', label: 'Exited Employees', api: adminExitAPI.getExitedEmployees },
    { id: 'terminated', label: 'Terminated', api: adminExitAPI.getTerminatedEmployees },
  ];

  useEffect(() => {
    fetchEmployees();
  }, [activeTab]);

  const fetchEmployees = async () => {
    setLoading(true);
    try {
      const currentTab = tabs.find(t => t.id === activeTab);
      const response = await currentTab.api();
      setEmployees(response.data || []);
    } catch (error) {
      toast.error('Failed to fetch employee data');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    const styles = {
      ACTIVE: { background: '#d4edda', color: '#155724' },
      RESIGNED: { background: '#fff3cd', color: '#856404' },
      TERMINATED: { background: '#f8d7da', color: '#721c24' },
    };
    return (
      <span style={{
        padding: '4px 12px',
        borderRadius: '12px',
        fontSize: '12px',
        fontWeight: '600',
        ...styles[status]
      }}>
        {status}
      </span>
    );
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.heading}>Employee Exit Management</h2>
      
      {/* Tabs */}
      <div style={styles.tabs}>
        {tabs.map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            style={{
              ...styles.tab,
              ...(activeTab === tab.id ? styles.activeTab : {})
            }}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Content */}
      {loading ? (
        <div style={styles.loading}>Loading...</div>
      ) : (
        <div style={styles.tableContainer}>
          <table style={styles.table}>
            <thead>
              <tr style={styles.headerRow}>
                <th style={styles.th}>Employee Code</th>
                <th style={styles.th}>Name</th>
                <th style={styles.th}>Designation</th>
                <th style={styles.th}>Department</th>
                <th style={styles.th}>Status</th>
                <th style={styles.th}>Last Working Day</th>
                <th style={styles.th}>Exit Date</th>
              </tr>
            </thead>
            <tbody>
              {employees.length === 0 ? (
                <tr>
                  <td colSpan="7" style={styles.noData}>No employees found</td>
                </tr>
              ) : (
                employees.map((emp, index) => (
                  <tr key={index} style={styles.row}>
                    <td style={styles.td}>{emp.employeeCode || '-'}</td>
                    <td style={styles.td}>{emp.fullName}</td>
                    <td style={styles.td}>{emp.designation || '-'}</td>
                    <td style={styles.td}>{emp.department || '-'}</td>
                    <td style={styles.td}>{getStatusBadge(emp.exitStatus || 'ACTIVE')}</td>
                    <td style={styles.td}>{emp.lastWorkingDate || '-'}</td>
                    <td style={styles.td}>{emp.exitDate || '-'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

const styles = {
  container: {
    padding: '20px',
    maxWidth: '1400px',
    margin: '0 auto',
  },
  heading: {
    fontSize: '24px',
    marginBottom: '20px',
    color: '#333',
  },
  tabs: {
    display: 'flex',
    gap: '10px',
    marginBottom: '20px',
    borderBottom: '2px solid #e0e0e0',
  },
  tab: {
    padding: '12px 24px',
    border: 'none',
    background: 'none',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500',
    color: '#666',
    borderBottom: '2px solid transparent',
    marginBottom: '-2px',
  },
  activeTab: {
    color: '#667eea',
    borderBottom: '2px solid #667eea',
  },
  tableContainer: {
    background: 'white',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    overflow: 'hidden',
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse',
  },
  headerRow: {
    background: '#f8f9fa',
  },
  th: {
    padding: '16px',
    textAlign: 'left',
    fontWeight: '600',
    color: '#555',
    borderBottom: '1px solid #e0e0e0',
  },
  row: {
    borderBottom: '1px solid #e0e0e0',
    ':hover': {
      background: '#f8f9fa',
    },
  },
  td: {
    padding: '16px',
    color: '#333',
  },
  noData: {
    padding: '40px',
    textAlign: 'center',
    color: '#999',
  },
  loading: {
    padding: '40px',
    textAlign: 'center',
    color: '#666',
  },
};

export default ExitEmployeesPage;
