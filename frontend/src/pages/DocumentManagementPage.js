import React, { useState, useEffect } from 'react';
import { adminDocumentAPI } from '../services/api';
import { toast } from 'react-toastify';

const DocumentManagementPage = () => {
  const [activeTab, setActiveTab] = useState('pending');
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedDoc, setSelectedDoc] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [actionType, setActionType] = useState(null);
  const [comments, setComments] = useState('');

  const tabs = [
    { id: 'pending', label: 'Pending' },
    { id: 'approved', label: 'Approved' },
    { id: 'rejected', label: 'Rejected' },
    { id: 'all', label: 'All Documents' },
  ];

  useEffect(() => {
    fetchDocuments();
  }, [activeTab]);

  const fetchDocuments = async () => {
    setLoading(true);
    try {
      let response;
      if (activeTab === 'pending') {
        response = await adminDocumentAPI.getPendingDocuments();
      } else if (activeTab === 'all') {
        response = await adminDocumentAPI.getAllDocuments();
      } else {
        response = await adminDocumentAPI.getDocumentsByStatus(activeTab.toUpperCase());
      }
      setDocuments(response.data || []);
    } catch (error) {
      toast.error('Failed to fetch documents');
    } finally {
      setLoading(false);
    }
  };

  const handleAction = (doc, action) => {
    setSelectedDoc(doc);
    setActionType(action);
    setShowModal(true);
  };

  const submitAction = async () => {
    try {
      if (actionType === 'approve') {
        await adminDocumentAPI.approveDocument(selectedDoc.id, comments);
        toast.success('Document approved successfully');
      } else {
        await adminDocumentAPI.rejectDocument(selectedDoc.id, comments);
        toast.success('Document rejected successfully');
      }
      setShowModal(false);
      setComments('');
      fetchDocuments();
    } catch (error) {
      toast.error('Action failed');
    }
  };

  const handleViewDocument = (docUrl) => {
    if (!docUrl) {
      toast.error('File not available');
      return;
    }
    const token = localStorage.getItem('token');
    const baseUrl = process.env.REACT_APP_API_URL || 'http://localhost:8081';
    const downloadUrl = `${baseUrl}/api/files/download?path=${encodeURIComponent(docUrl)}&token=${token}`;
    window.open(downloadUrl, '_blank');
  };

  const getStatusBadge = (status) => {
    const styles = {
      PENDING: { background: '#fff3cd', color: '#856404' },
      APPROVED: { background: '#d4edda', color: '#155724' },
      REJECTED: { background: '#f8d7da', color: '#721c24' },
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

  const documentTypes = {
    JOINING_LETTER: 'Joining Letter',
    RESIGNATION_LETTER: 'Resignation Letter',
    ID_PROOF: 'ID Proof',
    ADDRESS_PROOF: 'Address Proof',
    EDUCATION_CERTIFICATE: 'Education Certificate',
    EXPERIENCE_CERTIFICATE: 'Experience Certificate',
    OTHER: 'Other',
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.heading}>Document Management</h2>

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

      {/* Documents Table */}
      {loading ? (
        <div style={styles.loading}>Loading...</div>
      ) : (
        <div style={styles.tableContainer}>
          <table style={styles.table}>
            <thead>
              <tr style={styles.headerRow}>
                <th style={styles.th}>Employee</th>
                <th style={styles.th}>Document Name</th>
                <th style={styles.th}>Type</th>
                <th style={styles.th}>Upload Date</th>
                <th style={styles.th}>Status</th>
                <th style={styles.th}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {documents.length === 0 ? (
                <tr>
                  <td colSpan="6" style={styles.noData}>No documents found</td>
                </tr>
              ) : (
                documents.map((doc) => (
                  <tr key={doc.id} style={styles.row}>
                    <td style={styles.td}>
                      <div style={styles.employee}>
                        <strong>{doc.employeeName}</strong>
                        <span style={styles.empEmail}>{doc.employeeEmail}</span>
                      </div>
                    </td>
                    <td style={styles.td}>{doc.documentName}</td>
                    <td style={styles.td}>
                      {documentTypes[doc.documentType] || doc.documentType}
                    </td>
                    <td style={styles.td}>
                      {new Date(doc.uploadDate).toLocaleDateString()}
                    </td>
                    <td style={styles.td}>{getStatusBadge(doc.status)}</td>
                    <td style={styles.td}>
                      <div style={styles.actions}>
                        <button onClick={() => handleViewDocument(doc.documentUrl)} style={styles.viewLink}>👁 View</button>
                        {doc.status === 'PENDING' && (
                          <>
                            <button
                              onClick={() => handleAction(doc, 'approve')}
                              style={styles.approveButton}
                            >
                              Approve
                            </button>
                            <button
                              onClick={() => handleAction(doc, 'reject')}
                              style={styles.rejectButton}
                            >
                              Reject
                            </button>
                          </>
                        )}
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* Action Modal */}
      {showModal && (
        <div style={styles.modalOverlay}>
          <div style={styles.modal}>
            <h3 style={styles.modalTitle}>
              {actionType === 'approve' ? 'Approve Document' : 'Reject Document'}
            </h3>
            <p style={styles.modalText}>
              <strong>Document:</strong> {selectedDoc?.documentName}<br />
              <strong>Employee:</strong> {selectedDoc?.employeeName}
            </p>
            <div style={styles.formGroup}>
              <label style={styles.label}>
                {actionType === 'approve' ? 'Comments (Optional)' : 'Rejection Reason *'}
              </label>
              <textarea
                value={comments}
                onChange={(e) => setComments(e.target.value)}
                required={actionType === 'reject'}
                rows="4"
                style={styles.textarea}
                placeholder={actionType === 'approve' ? 'Add any comments...' : 'Provide reason for rejection...'}
              />
            </div>
            <div style={styles.modalButtons}>
              <button
                onClick={() => setShowModal(false)}
                style={styles.cancelButton}
              >
                Cancel
              </button>
              <button
                onClick={submitAction}
                style={actionType === 'approve' ? styles.approveConfirmBtn : styles.rejectConfirmBtn}
                disabled={actionType === 'reject' && !comments.trim()}
              >
                {actionType === 'approve' ? 'Approve' : 'Reject'}
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
  loading: {
    padding: '40px',
    textAlign: 'center',
    color: '#666',
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
  employee: {
    display: 'flex',
    flexDirection: 'column',
  },
  empEmail: {
    fontSize: '12px',
    color: '#999',
  },
  actions: {
    display: 'flex',
    gap: '8px',
  },
  viewLink: {
    color: '#667eea',
    textDecoration: 'none',
    fontSize: '13px',
    padding: '6px 12px',
    background: 'none',
    border: 'none',
    cursor: 'pointer',
    fontWeight: '500',
  },
  approveButton: {
    padding: '6px 12px',
    background: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '12px',
  },
  rejectButton: {
    padding: '6px 12px',
    background: '#dc3545',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '12px',
  },
  modalOverlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    background: 'rgba(0,0,0,0.5)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 1000,
  },
  modal: {
    background: 'white',
    borderRadius: '8px',
    padding: '24px',
    width: '100%',
    maxWidth: '500px',
  },
  modalTitle: {
    fontSize: '20px',
    marginBottom: '16px',
    color: '#333',
  },
  modalText: {
    fontSize: '14px',
    color: '#666',
    marginBottom: '20px',
    lineHeight: '1.6',
  },
  formGroup: {
    marginBottom: '20px',
  },
  label: {
    display: 'block',
    marginBottom: '8px',
    fontSize: '14px',
    fontWeight: '600',
    color: '#555',
  },
  textarea: {
    width: '100%',
    padding: '12px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '14px',
    resize: 'vertical',
  },
  modalButtons: {
    display: 'flex',
    gap: '12px',
    justifyContent: 'flex-end',
  },
  cancelButton: {
    padding: '10px 20px',
    border: '1px solid #ddd',
    background: 'white',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
  },
  approveConfirmBtn: {
    padding: '10px 20px',
    background: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '600',
  },
  rejectConfirmBtn: {
    padding: '10px 20px',
    background: '#dc3545',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '600',
  },
};

export default DocumentManagementPage;

