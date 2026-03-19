import React, { useState, useEffect } from 'react';
import { employeeDocumentAPI, uploadAPI } from '../services/api';
import { toast } from 'react-toastify';

const DocumentUploadSection = () => {
  const [documents, setDocuments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [showUploadForm, setShowUploadForm] = useState(false);
  const [formData, setFormData] = useState({
    documentType: '',
    documentName: '',
    file: null,
  });

  const documentTypes = [
    { value: 'JOINING_LETTER', label: 'Joining Letter' },
    { value: 'RESIGNATION_LETTER', label: 'Resignation Letter' },
    { value: 'ID_PROOF', label: 'ID Proof' },
    { value: 'ADDRESS_PROOF', label: 'Address Proof' },
    { value: 'EDUCATION_CERTIFICATE', label: 'Education Certificate' },
    { value: 'EXPERIENCE_CERTIFICATE', label: 'Experience Certificate' },
    { value: 'OTHER', label: 'Other' },
  ];

  useEffect(() => {
    fetchDocuments();
  }, []);

  const fetchDocuments = async () => {
    try {
      const response = await employeeDocumentAPI.getMyDocuments();
      setDocuments(response.data || []);
    } catch (error) {
      toast.error('Failed to fetch documents');
    } finally {
      setLoading(false);
    }
  };

  const handleFileChange = (e) => {
    setFormData(prev => ({
      ...prev,
      file: e.target.files[0]
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!formData.file) {
      toast.error('Please select a file');
      return;
    }

    setUploading(true);
    try {
      const uploadFormData = new FormData();
      uploadFormData.append('file', formData.file);
      uploadFormData.append('documentType', formData.documentType);
      uploadFormData.append('documentName', formData.documentName);

      await employeeDocumentAPI.uploadDocument(uploadFormData);
      toast.success('Document uploaded successfully');
      setShowUploadForm(false);
      setFormData({ documentType: '', documentName: '', file: null });
      fetchDocuments();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to upload document');
    } finally {
      setUploading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this document?')) return;

    try {
      await employeeDocumentAPI.deleteDocument(id);
      toast.success('Document deleted successfully');
      fetchDocuments();
    } catch (error) {
      toast.error('Failed to delete document');
    }
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

  return (
    <div style={styles.container}>
      <div style={styles.header}>
        <h2 style={styles.heading}>My Documents</h2>
        <button
          onClick={() => setShowUploadForm(!showUploadForm)}
          style={styles.uploadButton}
        >
          {showUploadForm ? 'Cancel' : '+ Upload Document'}
        </button>
      </div>

      {showUploadForm && (
        <div style={styles.uploadForm}>
          <h3 style={styles.formTitle}>Upload New Document</h3>
          <form onSubmit={handleSubmit}>
            <div style={styles.formRow}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Document Type *</label>
                <select
                  value={formData.documentType}
                  onChange={(e) => setFormData({...formData, documentType: e.target.value})}
                  required
                  style={styles.select}
                >
                  <option value="">Select Type</option>
                  {documentTypes.map(type => (
                    <option key={type.value} value={type.value}>{type.label}</option>
                  ))}
                </select>
              </div>
              <div style={styles.formGroup}>
                <label style={styles.label}>Document Name *</label>
                <input
                  type="text"
                  value={formData.documentName}
                  onChange={(e) => setFormData({...formData, documentName: e.target.value})}
                  required
                  placeholder="e.g., Degree Certificate"
                  style={styles.input}
                />
              </div>
            </div>
            <div style={styles.formGroup}>
              <label style={styles.label}>Select File *</label>
              <input
                type="file"
                onChange={handleFileChange}
                required
                accept=".pdf,.jpg,.jpeg,.png,.doc,.docx"
                style={styles.fileInput}
              />
              <span style={styles.hint}>Supported: PDF, JPG, PNG, DOC, DOCX (Max 10MB)</span>
            </div>
            <div style={styles.formButtons}>
              <button type="submit" style={styles.submitButton} disabled={uploading}>
                {uploading ? 'Uploading...' : 'Upload'}
              </button>
            </div>
          </form>
        </div>
      )}

      {loading ? (
        <div style={styles.loading}>Loading...</div>
      ) : (
        <div style={styles.documentsList}>
          {documents.length === 0 ? (
            <div style={styles.emptyState}>
              <p>No documents uploaded yet.</p>
              <p style={styles.emptyHint}>Upload your documents for verification.</p>
            </div>
          ) : (
            <table style={styles.table}>
              <thead>
                <tr style={styles.headerRow}>
                  <th style={styles.th}>Document Name</th>
                  <th style={styles.th}>Type</th>
                  <th style={styles.th}>Upload Date</th>
                  <th style={styles.th}>Status</th>
                  <th style={styles.th}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {documents.map((doc) => (
                  <tr key={doc.id} style={styles.row}>
                    <td style={styles.td}>{doc.documentName}</td>
                    <td style={styles.td}>
                      {documentTypes.find(t => t.value === doc.documentType)?.label || doc.documentType}
                    </td>
                    <td style={styles.td}>
                      {new Date(doc.uploadDate).toLocaleDateString()}
                    </td>
                    <td style={styles.td}>{getStatusBadge(doc.status)}</td>
                    <td style={styles.td}>
                      <div style={styles.actions}>
                        <a
                          href={doc.documentUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          style={styles.viewLink}
                        >
                          View
                        </a>
                        {doc.status === 'PENDING' && (
                          <button
                            onClick={() => handleDelete(doc.id)}
                            style={styles.deleteButton}
                          >
                            Delete
                          </button>
                        )}
                        {doc.status === 'APPROVED' && (
                          <span style={styles.lockedBadge}>🔒 Locked</span>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
};

const styles = {
  container: {
    padding: '20px',
    maxWidth: '1000px',
    margin: '0 auto',
  },
  header: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: '24px',
  },
  heading: {
    fontSize: '24px',
    color: '#333',
  },
  uploadButton: {
    padding: '10px 20px',
    background: '#667eea',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '600',
  },
  uploadForm: {
    background: '#f8f9fa',
    padding: '24px',
    borderRadius: '8px',
    marginBottom: '24px',
  },
  formTitle: {
    fontSize: '16px',
    marginBottom: '16px',
    color: '#333',
  },
  formRow: {
    display: 'flex',
    gap: '16px',
    marginBottom: '16px',
  },
  formGroup: {
    flex: 1,
  },
  label: {
    display: 'block',
    marginBottom: '8px',
    fontSize: '13px',
    fontWeight: '600',
    color: '#555',
  },
  select: {
    width: '100%',
    padding: '10px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '14px',
  },
  input: {
    width: '100%',
    padding: '10px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '14px',
  },
  fileInput: {
    width: '100%',
    padding: '10px 0',
  },
  hint: {
    fontSize: '12px',
    color: '#999',
  },
  formButtons: {
    marginTop: '16px',
  },
  submitButton: {
    padding: '10px 24px',
    background: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '600',
  },
  loading: {
    padding: '40px',
    textAlign: 'center',
    color: '#666',
  },
  documentsList: {
    background: 'white',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    overflow: 'hidden',
  },
  emptyState: {
    padding: '60px',
    textAlign: 'center',
    color: '#999',
  },
  emptyHint: {
    fontSize: '13px',
    marginTop: '8px',
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
  actions: {
    display: 'flex',
    gap: '12px',
    alignItems: 'center',
  },
  viewLink: {
    color: '#667eea',
    textDecoration: 'none',
    fontSize: '13px',
    fontWeight: '500',
  },
  deleteButton: {
    padding: '4px 12px',
    background: '#dc3545',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '12px',
  },
  lockedBadge: {
    fontSize: '12px',
    color: '#999',
  },
};

export default DocumentUploadSection;
