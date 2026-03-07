import React, { useState } from 'react';
import axios from 'axios';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            // This matches your SSD API Contract [cite: 284]
            const response = await axios.post('http://localhost:8080/api/v1/auth/login', {
                email,
                password
            });
            alert("Login Successful! Token: " + response.data.accessToken);
        } catch (error) {
            alert("Invalid Credentials");
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-[#800000]">
            <div className="bg-white p-8 rounded-2xl shadow-xl w-96">
                <h2 className="text-2xl font-bold text-center text-[#800000] mb-6">LibTek Admin</h2>
                <form onSubmit={handleLogin}>
                    <div className="mb-4">
                        <label className="block text-sm font-bold mb-2">Email Address</label>
                        <input 
                            type="email" 
                            className="w-full p-2 border rounded" 
                            value={email} 
                            onChange={(e) => setEmail(e.target.value)} 
                            required 
                        />
                    </div>
                    <div className="mb-6">
                        <label className="block text-sm font-bold mb-2">Password</label>
                        <input 
                            type="password" 
                            className="w-full p-2 border rounded" 
                            value={password} 
                            onChange={(e) => setPassword(e.target.value)} 
                            required 
                        />
                    </div>
                    <button type="submit" className="w-full bg-[#800000] text-white py-2 rounded font-bold hover:bg-red-900">
                        Login
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Login;