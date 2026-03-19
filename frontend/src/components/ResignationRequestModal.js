import React, { useState } from 'react';
import { resignationAPI } from '../services/api';
import { toast } from 'react-toastify';

const ResignationRequestModal = ({ isOpen, onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    reason: '',
    noticePeriodDays: 30,
    proposedLastWorkingDay: '',
  });
  const [loading, setLoading] = useState(false);
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [submittedData, setSubmittedData] = useState(null);

  if (!isOpen) return null;

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await resignationAPI.submitRequest(formData);
      setSubmittedData({
        ...formData,
        ...response.data
      });
      toast.success('Resignation request submitted successfully');
      setShowConfirmation(true);
      if (onSuccess) onSuccess();
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to submit resignation request');
    } finally {
      setLoading(false);
    }
  };

  const handleCloseConfirmation = () => {
    setShowConfirmation(false);
    onClose();
  };

  const calculateNoticePeriod = () => {
    if (!formData.proposedLastWorkingDay) return null;
    const today = new Date();
    const lwd = new Date(formData.proposedLastWorkingDay);
    const diffTime = Math.abs(lwd - today);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  };

  return (
    <div style={styles.overlay}>
      <div style={styles.modal}>
        {!showConfirmation ? (
          <>
            <h2 style={styles.heading}>Submit Resignation Request</h2>
            <form onSubmit={handleSubmit}>
              <div style={styles.formGroup}>
                <label style={styles.label}>Reason for Resignation *</label>
                <textarea
                  name="reason"
                  value={formData.reason}
                  onChange={handleChange}
                  required
                  rows="4"
                  style={styles.textarea}
                  placeholder="Please provide your reason for resignation..."
                />
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>Notice Period (Days) *</label>
                <input
                  type="number"
                  name="noticePeriodDays"
                  value={formData.noticePeriodDays}
                  onChange={handleChange}
                  required
                  min="1"
                  style={styles.input}
                />
              </div>

              <div style={styles.formGroup}>
                <label style={styles.label}>Proposed Last Working Date *</label>
                <input
                  type="date"
                  name="proposedLastWorkingDay"
                  value={formData.proposedLastWorkingDay}
                  onChange={handleChange}
                  required
                  min={new Date().toISOString().split('T')[0]}
                  style={styles.input}
                />
                {formData.proposedLastWorkingDay && (
                  <span style={styles.noticeInfo}>
                    Notice period: {calculateNoticePeriod()} days from today
                  </span>
                )}
              </div>

              <div style={styles.buttons}>
                <button
                  type="button"
                  onClick={onClose}
                  style={styles.cancelButton}
                  disabled={loading}
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  style={styles.submitButton}
                  disabled={loading}
                >
                  {loading ? 'Submitting...' : 'Submit Request'}
                </button>
              </div>
            </form>
          </>
        ) : (
          <div style={styles.confirmation}>
            <div style={styles.confirmationIcon}>✓</div>
            <h3 style={styles.confirmationTitle}>Resignation Submitted!</h3>
            <p style={styles.confirmationText}>
              Your resignation request has been submitted successfully and is pending approval.
            </p>
            
            <div style={styles.confirmationDetails}>
              <div style={styles.detailRow}>
                <span>Notice Period:</span>
                <strong>{submittedData?.noticePeriodDays} days</strong>
              </div>
              <div style={styles.detailRow}>
                <span>Proposed LWD:</span>
                <strong>{submittedData?.proposedLastWorkingDay}</strong>
              </div>
            </div>

            <p style={styles.noticeText}>
              Your request will be reviewed by your Manager and Admin. You will be notified once approved.
            </p>

            <button
              onClick={handleCloseConfirmation}
              style={styles.closeButton}
            >
              Go to Dashboard
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

const styles = {
  overlay: {
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
    padding: '32px',
    width: '100%',
    maxWidth: '500px',
    maxHeight: '90vh',
    overflow: 'auto',
  },
  heading: {
    fontSize: '20px',
    marginBottom: '24px',
    color: '#333',
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
    fontFamily: 'inherit',
  },
  input: {
    width: '100%',
    padding: '12px',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '14px',
  },
  noticeInfo: {
    display: 'block',
    marginTop: '8px',
    fontSize: '12px',
    color: '#667eea',
  },
  buttons: {
    display: 'flex',
    gap: '12px',
    justifyContent: 'flex-end',
    marginTop: '24px',
  },
  cancelButton: {
    padding: '12px 24px',
    border: '1px solid #ddd',
    background: 'white',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
  },
  submitButton: {
    padding: '12px 24px',
    border: 'none',
    background: '#dc3545',
    color: 'white',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '600',
  },
  confirmation: {
    textAlign: 'center',
    padding: '20px',
  },
  confirmationIcon: {
    width: '60px',
    height: '60px',
    background: '#28a745',
    color: 'white',
    borderRadius: '50%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '32px',
    margin: '0 auto 20px',
  },
  confirmationTitle: {
    fontSize: '24px',
    color: '#28a745',
    marginBottom: '12px',
  },
  confirmationText: {
    fontSize: '14px',
    color: '#666',
    marginBottom: '20px',
  },
  confirmationDetails: {
    background: '#f8f9fa',
    padding: '16px',
    borderRadius: '8px',
    marginBottom: '20px',
  },
  detailRow: {
    display: 'flex',
    justifyContent: 'space-between',
    padding: '8px 0',
    borderBottom: '1px solid #e0e0e0',
  },
  noticeText: {
    fontSize: '13px',
    color: '#999',
    marginBottom: '24px',
  },
  closeButton: {
    padding: '12px 32px',
    border: 'none',
    background: '#667eea',
    color: 'white',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '600',
  },
};

export default ResignationRequestModal;
