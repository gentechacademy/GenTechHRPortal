import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { employeeSearchAPI } from '../services/api';
import { toast } from 'react-toastify';

const EmployeeSearch = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [results, setResults] = useState([]);
  const [showDropdown, setShowDropdown] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSearch = async (e) => {
    e.preventDefault();
    if (!searchTerm.trim()) return;

    setLoading(true);
    try {
      const response = await employeeSearchAPI.search(searchTerm);
      setResults(response.data || []);
      setShowDropdown(true);
    } catch (error) {
      toast.error('Search failed');
    } finally {
      setLoading(false);
    }
  };

  const handleSelect = (employeeId) => {
    setShowDropdown(false);
    setSearchTerm('');
    navigate(`/admin/employee/${employeeId}/details`);
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Escape') {
      setShowDropdown(false);
    }
  };

  return (
    <div style={styles.container}>
      <form onSubmit={handleSearch} style={styles.form}>
        <div style={styles.inputWrapper}>
          <input
            type="text"
            placeholder="Search by Employee ID or Name..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyDown={handleKeyDown}
            style={styles.input}
          />
          <button type="submit" style={styles.searchButton} disabled={loading}>
            {loading ? '...' : '🔍'}
          </button>
        </div>
      </form>

      {showDropdown && results.length > 0 && (
        <div style={styles.dropdown}>
          {results.map((emp) => (
            <div
              key={emp.id}
              onClick={() => handleSelect(emp.id)}
              style={styles.resultItem}
            >
              <div style={styles.empName}>{emp.fullName}</div>
              <div style={styles.empDetails}>
                Code: {emp.employeeCode || '-'} | {emp.designation || '-'} | {emp.department || '-'}
              </div>
            </div>
          ))}
        </div>
      )}

      {showDropdown && results.length === 0 && !loading && (
        <div style={styles.dropdown}>
          <div style={styles.noResults}>No employees found</div>
        </div>
      )}

      {showDropdown && (
        <div style={styles.overlay} onClick={() => setShowDropdown(false)} />
      )}
    </div>
  );
};

const styles = {
  container: {
    position: 'relative',
    width: '350px',
  },
  form: {
    display: 'flex',
  },
  inputWrapper: {
    display: 'flex',
    width: '100%',
    position: 'relative',
    zIndex: 1001,
  },
  input: {
    flex: 1,
    padding: '10px 15px',
    border: '1px solid #ddd',
    borderRadius: '4px 0 0 4px',
    fontSize: '14px',
    outline: 'none',
  },
  searchButton: {
    padding: '10px 15px',
    background: '#667eea',
    color: 'white',
    border: 'none',
    borderRadius: '0 4px 4px 0',
    cursor: 'pointer',
    fontSize: '14px',
  },
  dropdown: {
    position: 'absolute',
    top: '100%',
    left: 0,
    right: 0,
    background: 'white',
    border: '1px solid #ddd',
    borderRadius: '4px',
    marginTop: '4px',
    boxShadow: '0 4px 12px rgba(0,0,0,0.15)',
    zIndex: 1001,
    maxHeight: '300px',
    overflowY: 'auto',
  },
  resultItem: {
    padding: '12px 16px',
    cursor: 'pointer',
    borderBottom: '1px solid #f0f0f0',
    ':hover': {
      background: '#f8f9fa',
    },
  },
  empName: {
    fontWeight: '600',
    color: '#333',
    fontSize: '14px',
  },
  empDetails: {
    fontSize: '12px',
    color: '#666',
    marginTop: '4px',
  },
  noResults: {
    padding: '16px',
    textAlign: 'center',
    color: '#999',
  },
  overlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    zIndex: 1000,
  },
};

export default EmployeeSearch;
