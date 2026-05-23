import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Mail, Lock, Shield } from 'lucide-react';

export function AdminLogin() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (email === 'admin@libtek.edu' && password === 'admin123') {
      localStorage.setItem('token', 'dummy-admin-token');
      navigate('/admin/dashboard');
    } else {
      alert('Invalid credentials');
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-[#7F1D1D] to-[#991B1B] flex items-center justify-center p-4">
      <div className="w-full max-w-md">
        <div className="text-center mb-8 flex flex-col items-center justify-center">
          <img 
            src="/logo512.png" 
            alt="LibTek Official Logo" 
            className="w-24 h-24 object-contain mb-4 select-none rounded-full"
            onError={(e) => { e.target.style.display = 'none'; }}
          />
          <h1 className="text-white text-3xl font-bold">LibTek Admin Portal</h1>
        </div>

        <div className="bg-white rounded-2xl shadow-2xl p-8">
          <div className="text-center mb-6">
            <div className="inline-flex items-center justify-center w-16 h-16 bg-[#7F1D1D]/10 rounded-full mb-4">
              <Shield className="w-8 h-8 text-[#7F1D1D]" />
            </div>
            <h2 className="text-2xl font-bold text-gray-900 mb-2">ADMIN LOGIN</h2>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Admin Email</label>
              <div className="relative">
                <Mail className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                <input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D]"
                  required
                />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Password</label>
              <div className="relative">
                <Lock className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
                <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D]"
                  required
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full bg-[#7F1D1D] text-white py-3 rounded-lg font-medium hover:bg-[#991B1B] transition-colors flex items-center justify-center gap-2"
            >
              <Shield className="w-5 h-5" />
              Login
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}