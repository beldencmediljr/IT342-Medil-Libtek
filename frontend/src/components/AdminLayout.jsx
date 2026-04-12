import { Link, useLocation, useNavigate } from 'react-router-dom';
import { LayoutDashboard, BookOpen, Calendar, LogOut, ShieldCheck, DollarSign } from 'lucide-react';

export function AdminLayout({ children }) {
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/admin/login');
  };

  const navLinks = [
    { path: '/admin/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
    { path: '/admin/resources', icon: BookOpen, label: 'Resources' },
    { path: '/admin/reservations', icon: Calendar, label: 'Reservations' },
    { path: '/admin/verifications', icon: ShieldCheck, label: 'Verifications' },
    { path: '/admin/fines', icon: DollarSign, label: 'Fines' },
  ];

  return (
    <div className="min-h-screen bg-gray-50 flex">
      <div className="w-64 bg-[#7F1D1D] fixed h-full shadow-xl flex flex-col">
        <div className="p-6 border-b border-white/10">
          <h1 className="text-2xl font-bold text-white">LibTek Admin</h1>
        </div>
        <nav className="p-4 space-y-2 flex-1">
          {navLinks.map((link) => (
            <Link 
              key={link.path}
              to={link.path} 
              className={`flex items-center gap-3 px-4 py-3 rounded-lg font-medium transition-colors ${
                location.pathname === link.path 
                  ? 'bg-white text-[#7F1D1D] shadow-md' 
                  : 'text-white/80 hover:bg-white/10 hover:text-white'
              }`}
            >
              <link.icon className="w-5 h-5" />
              {link.label}
            </Link>
          ))}
        </nav>
        <div className="p-4 border-t border-white/10">
          <button 
            onClick={handleLogout}
            className="flex items-center gap-3 px-4 py-3 w-full text-left rounded-lg font-medium text-white/80 hover:bg-[#601616] hover:text-white transition-colors"
          >
            <LogOut className="w-5 h-5" />
            Logout
          </button>
        </div>
      </div>
      <div className="ml-64 flex-1 p-8">
        {children}
      </div>
    </div>
  );
}