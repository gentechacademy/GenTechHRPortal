import React, { useState, useEffect } from 'react';
import { resignationAPI } from '../services/api';
import { toast } from 'react-toastify';
import ResignationRequestModal from '../components/ResignationRequestModal';

const MyExitStatusPage = () => {
  const [exitData, setExitData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showRequestModal, setShowRequestModal] = useState(false);

  useEffect(() => {
    fetchExitStatus();
  }, []);

  const fetchExitStatus = async () => {
    try {
      const response = await resignationAPI.getMyResignation();
      // API returns an array, get the most recent active one
      const resignations = Array.isArray(response.data) ? response.data : [response.data];
      const activeResignation = resignations.find(r => 
        r.status === 'PENDING_MANAGER' || 
        r.status === 'MANAGER_APPROVED' || 
        r.status === 'PENDING_ADMIN'
      );
      setExitData(activeResignation || null);
    } catch (error) {
      if (error.response?.status !== 404) {
        toast.error('Failed to fetch exit status');
      }
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    const styles = {
      PENDING: { background: '#fff3cd', color: '#856404' },
      PENDING_MANAGER: { background: '#fff3cd', color: '#856404' },
      MANAGER_APPROVED: { background: '#cce5ff', color: '#004085' },
      PENDING_ADMIN: { background: '#cce5ff', color: '#004085' },
      APPROVED: { background: '#d4edda', color: '#155724' },
      REJECTED: { background: '#f8d7da', color: '#721c24' },
    };
    const labels = {
      PENDING: 'PENDING',
      PENDING_MANAGER: 'PENDING',
      MANAGER_APPROVED: 'MANAGER APPROVED',
      PENDING_ADMIN: 'PENDING',
      APPROVED: 'APPROVED',
      REJECTED: 'REJECTED',
    };
    return (
      <span style={{
        padding: '6px 16px',
        borderRadius: '16px',
        fontSize: '13px',
        fontWeight: '600',
        ...styles[status]
      }}>
        {labels[status] || status}
      </span>
    );
  };

  const calculateDaysRemaining = (lastWorkingDate) => {
    if (!lastWorkingDate) return null;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const lwd = new Date(lastWorkingDate);
    lwd.setHours(0, 0, 0, 0);
    const diffTime = lwd - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays > 0 ? diffDays : 0;
  };

  if (loading) return <div style={styles.loading}>Loading...</div>;

  if (!exitData || exitData.length === 0) {
    return (
      <div style={styles.container}>
        <h2 style={styles.heading}>My Exit Status</h2>
        <div style={styles.emptyState}>
          <p>You don't have any active resignation request.</p>
          <button 
            style={styles.requestButton}
            onClick={() => setShowRequestModal(true)}
          >
            📄 Request Resignation
          </button>
        </div>
        <ResignationRequestModal 
          isOpen={showRequestModal}
          onClose={() => setShowRequestModal(false)}
          onSuccess={() => {
            setShowRequestModal(false);
            fetchExitStatus();
          }}
        />
      </div>
    );
  }

  const daysRemaining = calculateDaysRemaining(exitData.lastWorkingDate);

  return (
    <div style={styles.container}>
      <h2 style={styles.heading}>My Exit Status</h2>
      
      {/* Status Overview */}
      <div style={styles.overviewCard}>
        <div style={styles.statusRow}>
          <span style={styles.statusLabel}>Overall Status:</span>
          {getStatusBadge(exitData.status || 'PENDING')}
        </div>
        {daysRemaining !== null && (
          <div style={styles.noticeBanner}>
            <span style={styles.noticeIcon}>📅</span>
            <span>
              {daysRemaining > 0 
                ? `Your notice period ends in ${daysRemaining} days (${exitData.proposedLastWorkingDay || exitData.lastWorkingDate})`
                : `Your notice period has ended. Last working day was ${exitData.proposedLastWorkingDay || exitData.lastWorkingDate}`
              }
            </span>
          </div>
        )}
      </div>

      {/* Approval Workflow */}
      <div style={styles.workflowCard}>
        <h3 style={styles.sectionTitle}>Approval Workflow</h3>
        
        {/* Manager Approval */}
        <div style={styles.approvalStep}>
          <div style={styles.stepHeader}>
            <span style={styles.stepNumber}>1</span>
            <span style={styles.stepName}>Manager Approval</span>
            {getStatusBadge(
              exitData.status === 'PENDING_MANAGER' ? 'PENDING_MANAGER' :
              exitData.status === 'MANAGER_APPROVED' || exitData.status === 'PENDING_ADMIN' || exitData.status === 'APPROVED' ? 'MANAGER_APPROVED' :
              'PENDING'
            )}
          </div>
          {exitData.managerRemarks && (
            <div style={styles.comments}>
              <strong>Comments:</strong> {exitData.managerRemarks}
            </div>
          )}
          {exitData.managerApprovalDate && (
            <div style={styles.approvalDate}>
              Approved on: {new Date(exitData.managerApprovalDate).toLocaleDateString()}
            </div>
          )}
        </div>

        {/* Admin Approval */}
        <div style={styles.approvalStep}>
          <div style={styles.stepHeader}>
            <span style={styles.stepNumber}>2</span>
            <span style={styles.stepName}>Admin Approval</span>
            {getStatusBadge(
              exitData.status === 'PENDING_MANAGER' || exitData.status === 'MANAGER_APPROVED' ? 'PENDING' :
              exitData.status === 'APPROVED' ? 'APPROVED' :
              exitData.status || 'PENDING'
            )}
          </div>
          {exitData.adminRemarks && (
            <div style={styles.comments}>
              <strong>Comments:</strong> {exitData.adminRemarks}
            </div>
          )}
          {exitData.adminApprovalDate && (
            <div style={styles.approvalDate}>
              Approved on: {new Date(exitData.adminApprovalDate).toLocaleDateString()}
            </div>
          )}
        </div>
      </div>

      {/* Resignation Details */}
      <div style={styles.detailsCard}>
        <h3 style={styles.sectionTitle}>Resignation Details</h3>
        <div style={styles.detailsGrid}>
          <div style={styles.detailItem}>
            <span style={styles.detailLabel}>Reason</span>
            <span style={styles.detailValue}>{exitData.reason || '-'}</span>
          </div>
          <div style={styles.detailItem}>
            <span style={styles.detailLabel}>Notice Period</span>
            <span style={styles.detailValue}>{exitData.noticePeriodDays || '-'} days</span>
          </div>
          <div style={styles.detailItem}>
            <span style={styles.detailLabel}>Request Date</span>
            <span style={styles.detailValue}>
              {exitData.requestDate ? new Date(exitData.requestDate).toLocaleDateString() : 
               exitData.createdAt ? new Date(exitData.createdAt).toLocaleDateString() : '-'}
            </span>
          </div>
          <div style={styles.detailItem}>
            <span style={styles.detailLabel}>Proposed Last Working Day</span>
            <span style={styles.detailValue}>{exitData.proposedLastWorkingDay || 'Pending Approval'}</span>
          </div>
        </div>
      </div>

      {(exitData.status === 'PENDING_MANAGER' || exitData.status === 'MANAGER_APPROVED' || exitData.status === 'PENDING') && (
        <div style={styles.infoBox}>
          <p>Your resignation request is being processed. You will be notified once approved.</p>
        </div>
      )}
      
      <ResignationRequestModal 
        isOpen={showRequestModal}
        onClose={() => setShowRequestModal(false)}
        onSuccess={() => {
          setShowRequestModal(false);
          fetchExitStatus();
        }}
      />
    </div>
  );
};

const styles = {
  container: {
    padding: '20px',
    maxWidth: '800px',
    margin: '0 auto',
  },
  heading: {
    fontSize: '24px',
    marginBottom: '20px',
    color: '#333',
  },
  loading: {
    padding: '40px',
    textAlign: 'center',
  },
  emptyState: {
    background: '#f8f9fa',
    padding: '40px',
    borderRadius: '8px',
    textAlign: 'center',
    color: '#666',
  },
  hint: {
    fontSize: '13px',
    color: '#999',
    marginTop: '12px',
  },
  requestButton: {
    background: '#dc3545',
    color: 'white',
    border: 'none',
    padding: '14px 28px',
    borderRadius: '8px',
    fontSize: '16px',
    fontWeight: '600',
    cursor: 'pointer',
    marginTop: '20px',
    transition: 'all 0.3s',
  },
  overviewCard: {
    background: 'white',
    borderRadius: '8px',
    padding: '24px',
    marginBottom: '20px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  statusRow: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    marginBottom: '16px',
  },
  statusLabel: {
    fontSize: '16px',
    fontWeight: '600',
    color: '#333',
  },
  noticeBanner: {
    background: '#e3f2fd',
    padding: '16px',
    borderRadius: '8px',
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    fontSize: '14px',
    color: '#1565c0',
  },
  noticeIcon: {
    fontSize: '20px',
  },
  workflowCard: {
    background: 'white',
    borderRadius: '8px',
    padding: '24px',
    marginBottom: '20px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  sectionTitle: {
    fontSize: '18px',
    marginBottom: '20px',
    color: '#333',
  },
  approvalStep: {
    padding: '20px',
    border: '1px solid #e0e0e0',
    borderRadius: '8px',
    marginBottom: '16px',
  },
  stepHeader: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    marginBottom: '12px',
  },
  stepNumber: {
    width: '28px',
    height: '28px',
    background: '#667eea',
    color: 'white',
    borderRadius: '50%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '14px',
    fontWeight: '600',
  },
  stepName: {
    flex: 1,
    fontSize: '16px',
    fontWeight: '600',
    color: '#333',
  },
  comments: {
    padding: '12px',
    background: '#f8f9fa',
    borderRadius: '4px',
    fontSize: '14px',
    color: '#666',
    marginTop: '8px',
  },
  approvalDate: {
    fontSize: '12px',
    color: '#999',
    marginTop: '8px',
  },
  detailsCard: {
    background: 'white',
    borderRadius: '8px',
    padding: '24px',
    marginBottom: '20px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
  },
  detailsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
    gap: '16px',
  },
  detailItem: {
    display: 'flex',
    flexDirection: 'column',
  },
  detailLabel: {
    fontSize: '12px',
    color: '#999',
    marginBottom: '4px',
  },
  detailValue: {
    fontSize: '14px',
    fontWeight: '600',
    color: '#333',
  },
  infoBox: {
    background: '#fff3cd',
    padding: '16px',
    borderRadius: '8px',
    fontSize: '14px',
    color: '#856404',
  },
};

export default MyExitStatusPage;
