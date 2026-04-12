import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AdminDashboard } from './pages/AdminDashboard';
import { AdminResources } from './pages/AdminResources';
import { AdminReservations } from './pages/AdminReservations';
import { AdminVerification } from './pages/AdminVerification';
import { AdminFines } from './pages/AdminFines';
import { AdminLogin } from './pages/AdminLogin';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/admin/login" element={<AdminLogin />} />
        <Route path="/admin/dashboard" element={<AdminDashboard />} />
        <Route path="/admin/resources" element={<AdminResources />} />
        <Route path="/admin/reservations" element={<AdminReservations />} />
        <Route path="/admin/verifications" element={<AdminVerification />} />
        <Route path="/admin/fines" element={<AdminFines />} />
        <Route path="*" element={<Navigate to="/admin/dashboard" />} />
      </Routes>
    </BrowserRouter>
  );
}