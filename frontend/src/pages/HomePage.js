import React from 'react';
import { Link } from 'react-router-dom';

const HomePage = () => {
  return (
    <div style={styles.container}>
      <div style={styles.hero}>
        <h1 style={styles.title}>Welcome to GenTech HR Portal</h1>
        <p style={styles.subtitle}>
          A comprehensive HR management system for managing companies, admins, and employees.
        </p>
        <Link to="/login" style={styles.loginBtn}>
          Login to Portal
        </Link>
        
        <div style={styles.features}>
          <div style={styles.featureCard}>
            <h3>Super Admin</h3>
            <p>Create and manage companies and administrators</p>
          </div>
          <div style={styles.featureCard}>
            <h3>Admin</h3>
            <p>Manage employees and their profiles</p>
          </div>
          <div style={styles.featureCard}>
            <h3>Employee</h3>
            <p>View your profile and company information</p>
          </div>
        </div>
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
  hero: {
    textAlign: 'center',
    color: 'white',
    maxWidth: '800px',
  },
  title: {
    fontSize: '48px',
    marginBottom: '20px',
    textShadow: '2px 2px 4px rgba(0,0,0,0.3)',
  },
  subtitle: {
    fontSize: '20px',
    marginBottom: '40px',
    opacity: 0.9,
  },
  loginBtn: {
    display: 'inline-block',
    backgroundColor: '#fff',
    color: '#667eea',
    padding: '15px 40px',
    borderRadius: '30px',
    textDecoration: 'none',
    fontSize: '18px',
    fontWeight: 'bold',
    boxShadow: '0 4px 15px rgba(0,0,0,0.2)',
    transition: 'transform 0.3s',
  },
  features: {
    display: 'flex',
    gap: '20px',
    marginTop: '60px',
    flexWrap: 'wrap',
    justifyContent: 'center',
  },
  featureCard: {
    backgroundColor: 'rgba(255,255,255,0.1)',
    backdropFilter: 'blur(10px)',
    padding: '25px',
    borderRadius: '15px',
    width: '220px',
    border: '1px solid rgba(255,255,255,0.2)',
  },
};

export default HomePage;
