import { AdminLayout } from '../components/AdminLayout';
import { Search, Check, X, Eye, FileText } from 'lucide-react';
import { useState, useEffect, useCallback } from 'react';
import api from '../api';

export function AdminVerification() {
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [verifications, setVerifications] = useState([]);
  const [rejectionReason, setRejectionReason] = useState('');

  const fetchVerifications = useCallback(async () => {
    try {
      const response = await api.get('/verifications');
      setVerifications(response.data);
    } catch (error) {
      console.error('Failed to fetch verifications:', error);
    }
  }, []);

  useEffect(() => {
    fetchVerifications();
  }, [fetchVerifications]);

  const handleStatusUpdate = async (id, status) => {
    try {
      await api.put(`/verifications/${id}/status`, { 
        status, 
        rejectionReason: status === 'rejected' ? rejectionReason : null 
      });
      setSelectedStudent(null);
      setRejectionReason('');
      fetchVerifications();
    } catch (error) {
      console.error('Failed to update status:', error);
    }
  };

  const pendingCount = verifications.filter(v => v.status === 'pending').length;

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Student ID Verification</h2>
          <p className="text-gray-600 mt-1">Review and approve student ID submissions</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="bg-white p-4 rounded-xl border border-gray-200 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-2xl font-bold text-yellow-600">{pendingCount}</p>
                <p className="text-sm text-gray-600">Pending Review</p>
              </div>
              <div className="w-10 h-10 bg-yellow-100 rounded-lg flex items-center justify-center">
                <FileText className="w-5 h-5 text-yellow-600" />
              </div>
            </div>
          </div>

          <div className="bg-white p-4 rounded-xl border border-gray-200 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-2xl font-bold text-green-600">
                  {verifications.filter(v => v.status === 'approved').length}
                </p>
                <p className="text-sm text-gray-600">Approved</p>
              </div>
              <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                <Check className="w-5 h-5 text-green-600" />
              </div>
            </div>
          </div>

          <div className="bg-white p-4 rounded-xl border border-gray-200 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-2xl font-bold text-red-600">
                  {verifications.filter(v => v.status === 'rejected').length}
                </p>
                <p className="text-sm text-gray-600">Rejected</p>
              </div>
              <div className="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center">
                <X className="w-5 h-5 text-red-600" />
              </div>
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl border border-gray-200 shadow-sm">
          <div className="p-6 border-b border-gray-200">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                placeholder="Search by student name or ID..."
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D] focus:border-transparent"
              />
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Student</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Student ID</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Email</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Status</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {verifications.map((v) => (
                  <tr key={v.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4 font-medium text-gray-900">{v.studentName}</td>
                    <td className="px-6 py-4 text-gray-600">{v.studentId}</td>
                    <td className="px-6 py-4 text-gray-600 text-sm">{v.email}</td>
                    <td className="px-6 py-4">
                      <span className={`px-2 py-1 rounded text-xs font-medium ${
                        v.status === 'pending' ? 'bg-yellow-100 text-yellow-700' :
                        v.status === 'approved' ? 'bg-green-100 text-green-700' :
                        'bg-red-100 text-red-700'
                      }`}>
                        {v.status.toUpperCase()}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      <button 
                        onClick={() => setSelectedStudent(v)}
                        className="flex items-center gap-2 text-[#7F1D1D] hover:underline text-sm font-medium"
                      >
                        <Eye className="w-4 h-4" />
                        Review
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {selectedStudent && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-xl max-w-3xl w-full max-h-[90vh] overflow-y-auto">
              <div className="p-6 border-b border-gray-200">
                <div className="flex items-center justify-between">
                  <h3 className="text-xl font-bold text-gray-900">Review ID Submission</h3>
                  <button onClick={() => setSelectedStudent(null)} className="p-2 hover:bg-gray-100 rounded-lg">
                    <X className="w-5 h-5 text-gray-600" />
                  </button>
                </div>
              </div>

              <div className="p-6 space-y-6">
                <div className="bg-gray-50 rounded-lg p-4">
                  <h4 className="font-bold text-gray-900 mb-3">Student Information</h4>
                  <div className="grid grid-cols-2 gap-4">
                    <div><p className="text-xs text-gray-500">Name</p><p className="font-medium text-gray-900">{selectedStudent.studentName}</p></div>
                    <div><p className="text-xs text-gray-500">Student ID</p><p className="font-medium text-gray-900">{selectedStudent.studentId}</p></div>
                    <div><p className="text-xs text-gray-500">Email</p><p className="font-medium text-gray-900">{selectedStudent.email}</p></div>
                  </div>
                </div>

                <div>
                  <h4 className="font-bold text-gray-900 mb-3">ID Image</h4>
                  <div className="border-2 border-gray-200 rounded-lg overflow-hidden bg-gray-100 min-h-[200px] flex items-center justify-center">
                     {/* In a real app, render actual image. Using placeholder for visual parity with design */}
                    <img src={selectedStudent.idImageUrl || 'https://via.placeholder.com/400x250?text=ID+Document'} alt="ID" className="max-w-full" />
                  </div>
                </div>

                {selectedStudent.status === 'pending' && (
                  <div className="space-y-4">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Rejection Reason (if rejecting)</label>
                      <input 
                        type="text" 
                        value={rejectionReason}
                        onChange={(e) => setRejectionReason(e.target.value)}
                        placeholder="e.g., Image is blurry"
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-[#7F1D1D]" 
                      />
                    </div>
                    <div className="flex gap-3">
                      <button 
                        onClick={() => handleStatusUpdate(selectedStudent.id, 'approved')}
                        className="flex-1 flex items-center justify-center gap-2 px-6 py-3 bg-green-500 text-white rounded-lg font-medium hover:bg-green-600 transition-colors"
                      >
                        <Check className="w-5 h-5" /> Approve
                      </button>
                      <button 
                        onClick={() => handleStatusUpdate(selectedStudent.id, 'rejected')}
                        className="flex-1 flex items-center justify-center gap-2 px-6 py-3 bg-red-500 text-white rounded-lg font-medium hover:bg-red-600 transition-colors"
                      >
                        <X className="w-5 h-5" /> Reject
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </AdminLayout>
  );
}