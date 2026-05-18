import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';

// --- UPDATED VERTICAL SLICE IMPORTS ---
import { AdminLogin } from './features/auth/AdminLogin';
import { AdminDashboard } from './features/admin/AdminDashboard';
import { AdminResources } from './features/resources/AdminResources';
import { AdminReservations } from './features/reservations/AdminReservations';
import { AdminVerification } from './features/verification/AdminVerification';
import { AdminFines } from './features/fines/AdminFines';
// --------------------------------------

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