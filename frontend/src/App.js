import React, { useState } from 'react';

function App() {
  const [view, setView] = useState('login'); 

  // Form State Variables
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [fullName, setFullName] = useState('');
  const [studentId, setStudentId] = useState('');

  const maroon = "#800000";
  const gold = "#FFD700";

  // --- LOGIN LOGIC ---
  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8080/api/v1/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email: email, password: password })
      });
      
      if (response.ok) {
        alert("Login Successful! ✅ Take your screenshot now!");
      } else {
        alert("Invalid credentials ❌");
      }
    } catch (error) {
      alert("Cannot connect to backend! Is Spring Boot running?");
    }
  };

  // --- REGISTRATION LOGIC ---
  const handleRegister = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8080/api/v1/auth/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fullName: fullName, email: email, password: password })
      });
      
      if (response.ok) {
        alert("Registration Successful! ✅ Please log in.");
        setPassword(''); // Clear password for safety
        setView('login'); // Send them back to the login screen
      } else {
        alert("Registration failed ❌ Email might already exist.");
      }
    } catch (error) {
      alert("Cannot connect to backend! Is Spring Boot running?");
    }
  };

  // --- UI COMPONENTS ---
  const Branding = () => (
    <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
      <div style={{ backgroundColor: gold, width: '70px', height: '70px', borderRadius: '50%', border: `4px solid ${maroon}`, display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 10px', color: maroon, fontWeight: 'bold', fontSize: '11px', lineHeight: '1' }}>
        LIB<br/>TEK
      </div>
      <h1 style={{ color: 'white', fontSize: '26px', margin: '0', fontWeight: 'bold' }}>LibTek</h1>
      <p style={{ color: 'white', opacity: 0.8, fontSize: '12px' }}>Smart Library Management System</p>
    </div>
  );

  return (
    <div style={{ backgroundColor: maroon, minHeight: '100vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '1rem', fontFamily: 'sans-serif' }}>
      <Branding />

      {/* 1. LOGIN VIEW */}
      {view === 'login' && (
        <div style={{ width: '100%', maxWidth: '380px', backgroundColor: 'white', borderRadius: '24px', padding: '2rem', boxShadow: '0 10px 25px rgba(0,0,0,0.3)' }}>
          <h2 style={{ fontSize: '22px', fontWeight: 'bold', textAlign: 'center', marginBottom: '1.5rem' }}>Welcome Back!</h2>
          <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            
            <input 
              type="email" 
              placeholder="Email Address" 
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={{ padding: '12px', border: '1px solid #ddd', borderRadius: '12px' }} 
              required 
            />
            
            <input 
              type="password" 
              placeholder="Password" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              style={{ padding: '12px', border: '1px solid #ddd', borderRadius: '12px' }} 
              required 
            />
            
            <button type="submit" style={{ backgroundColor: maroon, color: 'white', padding: '14px', border: 'none', borderRadius: '12px', fontWeight: 'bold', cursor: 'pointer' }}>
              Login →
            </button>
          </form>
          
          <div style={{ margin: '1.5rem 0', textAlign: 'center', borderTop: '1px solid #eee', paddingTop: '1rem', color: '#999', fontSize: '12px' }}>Or continue with</div>
          
          <button type="button" style={{ width: '100%', backgroundColor: 'white', border: '1px solid #ddd', padding: '12px', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '10px', fontWeight: '600', cursor: 'pointer' }}>
            <img src="https://www.gstatic.com/images/branding/product/1x/gsa_512dp.png" width="18" alt=""/> Google
          </button>
          
          <p style={{ marginTop: '1.5rem', textAlign: 'center', fontSize: '14px' }}>
            Don't have an account? <span onClick={() => { setView('register'); setPassword(''); }} style={{ color: maroon, fontWeight: 'bold', cursor: 'pointer', textDecoration: 'underline' }}>Sign up now</span>
          </p>
        </div>
      )}

      {/* 2. REGISTRATION VIEW */}
      {view === 'register' && (
        <div style={{ width: '100%', maxWidth: '380px', backgroundColor: 'white', borderRadius: '24px', padding: '2rem', boxShadow: '0 10px 25px rgba(0,0,0,0.3)' }}>
          <h2 style={{ fontSize: '22px', fontWeight: 'bold', textAlign: 'center', marginBottom: '0.5rem' }}>Create Account</h2>
          <p style={{ color: '#666', fontSize: '13px', textAlign: 'center', marginBottom: '1.5rem' }}>Enter your details to register</p>
          <form onSubmit={handleRegister} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
            
            <input 
              type="text" 
              placeholder="Full Name" 
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              style={{ padding: '12px', border: '1px solid #ddd', borderRadius: '12px' }} 
              required 
            />
            
            <input 
              type="text" 
              placeholder="Student ID (e.g. 17-1139-815)" 
              value={studentId}
              onChange={(e) => setStudentId(e.target.value)}
              style={{ padding: '12px', border: '1px solid #ddd', borderRadius: '12px' }} 
              required 
            />
            
            <input 
              type="email" 
              placeholder="University Email" 
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={{ padding: '12px', border: '1px solid #ddd', borderRadius: '12px' }} 
              required 
            />
            
            <input 
              type="password" 
              placeholder="Password" 
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              style={{ padding: '12px', border: '1px solid #ddd', borderRadius: '12px' }} 
              required 
            />
            
            <button type="submit" style={{ backgroundColor: maroon, color: 'white', padding: '14px', border: 'none', borderRadius: '12px', fontWeight: 'bold', cursor: 'pointer' }}>
              Register Account
            </button>
          </form>
          
          <div style={{ margin: '1.5rem 0', textAlign: 'center', borderTop: '1px solid #eee', paddingTop: '1rem', color: '#999', fontSize: '12px' }}>Or sign up with</div>
          
          <button type="button" style={{ width: '100%', backgroundColor: 'white', border: '1px solid #ddd', padding: '12px', borderRadius: '12px', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '10px', fontWeight: '600', cursor: 'pointer' }}>
            <img src="https://www.gstatic.com/images/branding/product/1x/gsa_512dp.png" width="18" alt=""/> Google
          </button>
          
          <p style={{ marginTop: '1.5rem', textAlign: 'center', fontSize: '14px' }}>
            Already have an account? <span onClick={() => { setView('login'); setPassword(''); }} style={{ color: maroon, fontWeight: 'bold', cursor: 'pointer', textDecoration: 'underline' }}>Login</span>
          </p>
        </div>
      )}
    </div>
  );
}

export default App;