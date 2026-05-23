import { AdminLayout } from '../admin/AdminLayout';
import { Calendar, BookOpen, MapPin, User, Check, X, CheckCircle } from 'lucide-react';
import { useState, useEffect, useCallback } from 'react';
import api from '../../api';

export function AdminReservations() {
  const [activeTab, setActiveTab] = useState('pending');
  const [reservations, setReservations] = useState([]);

  const fetchReservations = useCallback(async () => {
    try {
      const response = await api.get(`/reservations?status=${activeTab.toUpperCase()}`);
      setReservations(response.data);
    } catch (error) {
      console.error(error);
    }
  }, [activeTab]);

  useEffect(() => {
    fetchReservations();
  }, [fetchReservations]);

  const handleUpdateStatus = async (id, newStatus) => {
    try {
      await api.put(`/reservations/${id}/status`, { status: newStatus });
      fetchReservations();
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Reservation Management</h2>
          <p className="text-gray-600 mt-1">Review, approve, and track student workspace usage states</p>
        </div>

        <div className="bg-white rounded-xl border border-gray-200 shadow-sm">
          <div className="p-6 border-b border-gray-200">
            <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
              <div className="flex gap-2">
                <button
                  onClick={() => setActiveTab('pending')}
                  className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                    activeTab === 'pending' ? 'bg-[#7F1D1D] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Pending Action
                </button>
                <button
                  onClick={() => setActiveTab('active')}
                  className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                    activeTab === 'active' ? 'bg-[#7F1D1D] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Approved / Active
                </button>
                <button
                  onClick={() => setActiveTab('completed')}
                  className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                    activeTab === 'completed' ? 'bg-[#7F1D1D] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Completed
                </button>
                <button
                  onClick={() => setActiveTab('rejected')}
                  className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                    activeTab === 'rejected' ? 'bg-[#7F1D1D] text-white' : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                  }`}
                >
                  Rejected
                </button>
              </div>
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full">
              <img src="" alt="" />
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Student</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Resource</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Date/Time</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Status</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {reservations.map((reservation) => (
                  <tr key={reservation.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center">
                          <User className="w-5 h-5 text-gray-600" />
                        </div>
                        <div>
                          <p className="font-medium text-gray-900">{reservation.studentName}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        {reservation.resourceType === 'BOOTH' ? (
                          <MapPin className="w-4 h-4 text-blue-600" />
                        ) : (
                          <BookOpen className="w-4 h-4 text-green-600" />
                        )}
                        <span className="font-medium text-gray-900">{reservation.resourceName}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 text-gray-600 text-sm">{reservation.reservationDate}</td>
                    <td className="px-6 py-4">
                      <span className={`px-2 py-1 rounded text-xs font-medium ${
                        reservation.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' :
                        reservation.status === 'ACTIVE' ? 'bg-green-100 text-green-700' :
                        reservation.status === 'REJECTED' ? 'bg-red-100 text-red-700' :
                        'bg-gray-100 text-gray-700'
                      }`}>
                        {reservation.status}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        {reservation.status === 'PENDING' && (
                          <>
                            <button 
                              onClick={() => handleUpdateStatus(reservation.id, 'ACTIVE')}
                              className="inline-flex items-center gap-1 bg-green-600 text-white px-3 py-1.5 rounded text-xs font-medium hover:bg-green-700 transition-colors"
                            >
                              <Check className="w-3.5 h-3.5" /> Approve
                            </button>
                            <button 
                              onClick={() => handleUpdateStatus(reservation.id, 'REJECTED')}
                              className="inline-flex items-center gap-1 bg-red-600 text-white px-3 py-1.5 rounded text-xs font-medium hover:bg-red-700 transition-colors"
                            >
                              <X className="w-3.5 h-3.5" /> Reject
                            </button>
                          </>
                        )}
                        {reservation.status === 'ACTIVE' && (
                          <button 
                            onClick={() => handleUpdateStatus(reservation.id, 'COMPLETED')}
                            className="inline-flex items-center gap-1 bg-[#7F1D1D] text-white px-3 py-1.5 rounded text-xs font-medium hover:bg-[#601616] transition-colors"
                          >
                            <CheckCircle className="w-3.5 h-3.5" /> Mark Complete
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {reservations.length === 0 && (
            <div className="text-center py-12">
              <Calendar className="w-16 h-16 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-600">No reservations found in this category</p>
            </div>
          )}
        </div>
      </div>
    </AdminLayout>
  );
}