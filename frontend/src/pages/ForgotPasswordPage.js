import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import api from '../services/api';

const ForgotPasswordPage = () => {
  const navigate = useNavigate();
  const [step, setStep] = useState(1); // 1: Username, 2: OTP, 3: New Password
  const [loading, setLoading] = useState(false);
  const [username, setUsername] = useState('');
  const [otp, setOtp] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');


  const handleUsernameSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await api.post('/auth/forgot-password', { username });
      
      toast.success(response.data.message || 'OTP has been sent to your registered email!');
      setStep(2);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to send OTP. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleOtpSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await api.post('/auth/verify-otp', { username, otp });
      toast.success(response.data.message || 'OTP verified successfully!');
      setStep(3);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Invalid OTP. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    
    if (newPassword !== confirmPassword) {
      toast.error('Passwords do not match!');
      return;
    }

    if (newPassword.length < 6) {
      toast.error('Password must be at least 6 characters long!');
      return;
    }

    setLoading(true);

    try {
      const response = await api.post('/auth/reset-password', {
        username,
        otp,
        newPassword
      });
      toast.success(response.data.message || 'Password reset successfully!');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to reset password. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const renderStep1 = () => (
    <form onSubmit={handleUsernameSubmit} style={styles.form}>
      <div style={styles.inputGroup}>
        <label style={styles.label}>Username</label>
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          style={styles.input}
          required
          placeholder="Enter your username"
          autoComplete="username"
        />
      </div>
      <p style={styles.hint}>
        OTP will be sent to your registered email address
      </p>
      <button type="submit" style={styles.button} disabled={loading}>
        {loading ? 'Sending OTP...' : 'Send OTP'}
      </button>
    </form>
  );

  const renderStep2 = () => (
    <form onSubmit={handleOtpSubmit} style={styles.form}>
      <div style={styles.infoBox}>
        <p style={styles.infoText}>
          OTP has been sent to your registered email
        </p>
      </div>
      <div style={styles.inputGroup}>
        <label style={styles.label}>Enter OTP</label>
        <input
          type="text"
          value={otp}
          onChange={(e) => setOtp(e.target.value)}
          style={styles.otpInput}
          required
          placeholder="Enter 6-digit OTP"
          maxLength={6}
        />
      </div>
      <button type="submit" style={styles.button} disabled={loading}>
        {loading ? 'Verifying...' : 'Verify OTP'}
      </button>
      <button
        type="button"
        onClick={() => setStep(1)}
        style={styles.secondaryButton}
      >
        Change Username
      </button>
    </form>
  );

  const renderStep3 = () => (
    <form onSubmit={handleResetPassword} style={styles.form}>
      <div style={styles.inputGroup}>
        <label style={styles.label}>New Password</label>
        <input
          type="password"
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          style={styles.input}
          required
          placeholder="Enter new password"
          minLength={6}
        />
      </div>
      <div style={styles.inputGroup}>
        <label style={styles.label}>Confirm Password</label>
        <input
          type="password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          style={styles.input}
          required
          placeholder="Confirm new password"
          minLength={6}
        />
      </div>
      <button type="submit" style={styles.button} disabled={loading}>
        {loading ? 'Resetting...' : 'Reset Password'}
      </button>
    </form>
  );

  const getStepTitle = () => {
    switch (step) {
      case 1:
        return 'Forgot Password';
      case 2:
        return 'Verify OTP';
      case 3:
        return 'Reset Password';
      default:
        return 'Forgot Password';
    }
  };

  const getStepDescription = () => {
    switch (step) {
      case 1:
        return 'Enter your username to receive OTP on your registered email';
      case 2:
        return 'Enter the 6-digit OTP sent to your registered email';
      case 3:
        return 'Create a new password for your account';
      default:
        return '';
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.loginBox}>
        <h2 style={styles.heading}>{getStepTitle()}</h2>
        <p style={styles.description}>{getStepDescription()}</p>

        {/* Progress Steps */}
        <div style={styles.progressContainer}>
          <div style={styles.progressStep}>
            <div style={{ ...styles.stepCircle, ...(step >= 1 ? styles.stepActive : {}) }}>1</div>
            <span style={styles.stepText}>User</span>
          </div>
          <div style={styles.progressLine} />
          <div style={styles.progressStep}>
            <div style={{ ...styles.stepCircle, ...(step >= 2 ? styles.stepActive : {}) }}>2</div>
            <span style={styles.stepText}>OTP</span>
          </div>
          <div style={styles.progressLine} />
          <div style={styles.progressStep}>
            <div style={{ ...styles.stepCircle, ...(step >= 3 ? styles.stepActive : {}) }}>3</div>
            <span style={styles.stepText}>Reset</span>
          </div>
        </div>

        {step === 1 && renderStep1()}
        {step === 2 && renderStep2()}
        {step === 3 && renderStep3()}

        <Link to="/login" style={styles.backLink}>
          ← Back to Login
        </Link>
      </div>
    </div>
  );
};

const styles = {
  container: {
    minHeight: '100vh',
    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '20px',
  },
  loginBox: {
    backgroundColor: 'white',
    padding: '40px',
    borderRadius: '10px',
    boxShadow: '0 10px 40px rgba(0,0,0,0.2)',
    width: '100%',
    maxWidth: '450px',
  },
  heading: {
    textAlign: 'center',
    marginBottom: '10px',
    color: '#333',
  },
  description: {
    textAlign: 'center',
    marginBottom: '25px',
    color: '#666',
    fontSize: '14px',
  },
  progressContainer: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: '30px',
    padding: '0 20px',
  },
  progressStep: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  stepCircle: {
    width: '35px',
    height: '35px',
    borderRadius: '50%',
    backgroundColor: '#e0e0e0',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontWeight: 'bold',
    color: '#666',
    transition: 'all 0.3s ease',
  },
  stepActive: {
    backgroundColor: '#667eea',
    color: 'white',
  },
  stepText: {
    fontSize: '12px',
    marginTop: '5px',
    color: '#666',
  },
  progressLine: {
    width: '50px',
    height: '2px',
    backgroundColor: '#e0e0e0',
    margin: '0 10px',
    marginBottom: '20px',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '20px',
  },
  inputGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '5px',
  },
  label: {
    fontSize: '14px',
    fontWeight: '600',
    color: '#555',
  },
  input: {
    padding: '12px',
    border: '1px solid #ddd',
    borderRadius: '5px',
    fontSize: '16px',
    outline: 'none',
  },
  otpInput: {
    padding: '12px',
    border: '1px solid #ddd',
    borderRadius: '5px',
    fontSize: '20px',
    outline: 'none',
    textAlign: 'center',
    letterSpacing: '4px',
    fontWeight: 'bold',
  },
  button: {
    padding: '14px',
    backgroundColor: '#667eea',
    color: 'white',
    border: 'none',
    borderRadius: '5px',
    fontSize: '16px',
    fontWeight: '600',
    cursor: 'pointer',
    marginTop: '10px',
  },
  secondaryButton: {
    padding: '12px',
    backgroundColor: 'transparent',
    color: '#667eea',
    border: '1px solid #667eea',
    borderRadius: '5px',
    fontSize: '14px',
    cursor: 'pointer',
  },
  backLink: {
    display: 'block',
    textAlign: 'center',
    marginTop: '20px',
    color: '#667eea',
    textDecoration: 'none',
  },
  infoBox: {
    backgroundColor: '#f0f4ff',
    padding: '15px',
    borderRadius: '5px',
    borderLeft: '4px solid #667eea',
  },
  infoText: {
    margin: 0,
    fontSize: '14px',
    color: '#555',
    textAlign: 'center',
  },
  hint: {
    fontSize: '13px',
    color: '#888',
    margin: '-10px 0 0 0',
    fontStyle: 'italic',
  },
};

export default ForgotPasswordPage;
