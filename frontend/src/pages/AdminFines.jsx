import { AdminLayout } from '../components/AdminLayout';
import { Search, DollarSign, User, BookOpen, Check, AlertTriangle } from 'lucide-react';
import { useState, useEffect, useCallback } from 'react';
import api from '../api';

export function AdminFines() {
  const [selectedFine, setSelectedFine] = useState(null);
  const [fines, setFines] = useState([]);
  const [receiptNumber, setReceiptNumber] = useState('');
  const [notes, setNotes] = useState('');

  const fetchFines = useCallback(async () => {
    try {
      const response = await api.get('/fines');
      setFines(response.data);
    } catch (error) {
      console.error('Failed to fetch fines:', error);
    }
  }, []);

  useEffect(() => {
    fetchFines();
  }, [fetchFines]);

  const handleClearFine = async () => {
    try {
      await api.put(`/fines/${selectedFine.id}/clear`, { receiptNumber, notes });
      setSelectedFine(null);
      setReceiptNumber('');
      setNotes('');
      fetchFines();
    } catch (error) {
      console.error('Failed to clear fine:', error);
    }
  };

  const totalUnpaid = fines.filter(f => f.status === 'unpaid').reduce((sum, f) => sum + f.amount, 0);
  const totalCleared = fines.filter(f => f.status === 'cleared').reduce((sum, f) => sum + f.amount, 0);

  return (
    <AdminLayout>
      <div className="space-y-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">Fine Management</h2>
          <p className="text-gray-600 mt-1">Track and clear overdue fines</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
          <div className="bg-white p-4 rounded-xl border border-gray-200 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-2xl font-bold text-red-600">{fines.filter(f => f.status === 'unpaid').length}</p>
                <p className="text-sm text-gray-600">Unpaid Fines</p>
              </div>
              <div className="w-10 h-10 bg-red-100 rounded-lg flex items-center justify-center">
                <AlertTriangle className="w-5 h-5 text-red-600" />
              </div>
            </div>
          </div>
          
          <div className="bg-white p-4 rounded-xl border border-gray-200 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-2xl font-bold text-[#7F1D1D]">PHP {totalUnpaid}</p>
                <p className="text-sm text-gray-600">Total Unpaid Amount</p>
              </div>
              <div className="w-10 h-10 bg-[#7F1D1D]/10 rounded-lg flex items-center justify-center">
                <DollarSign className="w-5 h-5 text-[#7F1D1D]" />
              </div>
            </div>
          </div>

          <div className="bg-white p-4 rounded-xl border border-gray-200 shadow-sm">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-2xl font-bold text-green-600">PHP {totalCleared}</p>
                <p className="text-sm text-gray-600">Total Cleared Amount</p>
              </div>
              <div className="w-10 h-10 bg-green-100 rounded-lg flex items-center justify-center">
                <Check className="w-5 h-5 text-green-600" />
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
                className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D]"
              />
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Student</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Resource</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Days Overdue</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Fine Amount</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Status</th>
                  <th className="px-6 py-3 text-left text-sm font-semibold text-gray-900">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {fines.map((fine) => (
                  <tr key={fine.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-3">
                        <div className="w-10 h-10 bg-gray-200 rounded-full flex items-center justify-center">
                          <User className="w-5 h-5 text-gray-600" />
                        </div>
                        <div>
                          <p className="font-medium text-gray-900">{fine.studentName}</p>
                          <p className="text-xs text-gray-600">{fine.studentId}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <div className="flex items-center gap-2">
                        <BookOpen className="w-4 h-4 text-gray-600" />
                        <span className="text-gray-900">{fine.resourceName}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <span className="px-2 py-1 bg-red-100 text-red-700 rounded text-xs font-medium">
                        {fine.daysOverdue} days
                      </span>
                    </td>
                    <td className="px-6 py-4 font-bold text-gray-900">PHP {fine.amount}</td>
                    <td className="px-6 py-4">
                      <span className={`px-2 py-1 rounded text-xs font-medium ${
                        fine.status === 'unpaid' ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'
                      }`}>
                        {fine.status.toUpperCase()}
                      </span>
                    </td>
                    <td className="px-6 py-4">
                      {fine.status !== 'cleared' && (
                        <button 
                          onClick={() => setSelectedFine(fine)}
                          className="text-[#7F1D1D] hover:underline text-sm font-medium"
                        >
                          Clear Fine
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {selectedFine && (
          <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-xl max-w-md w-full">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-xl font-bold text-gray-900">Clear Fine</h3>
              </div>
              <div className="p-6 space-y-4">
                <div className="bg-gray-50 rounded-lg p-4 space-y-3">
                  <div>
                    <p className="text-xs text-gray-500">Student</p>
                    <p className="font-medium text-gray-900">{selectedFine.studentName}</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-500">Fine Amount</p>
                    <p className="text-2xl font-bold text-[#7F1D1D]">PHP {selectedFine.amount}</p>
                  </div>
                </div>

                <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                  <div className="flex gap-3">
                    <AlertTriangle className="w-5 h-5 text-yellow-600 flex-shrink-0 mt-0.5" />
                    <div>
                      <p className="text-sm font-medium text-yellow-900">Important</p>
                      <p className="text-sm text-yellow-800 mt-1">
                        Ensure the student has paid at the University Accounting Office and presented the official receipt before clearing this fine.
                      </p>
                    </div>
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-2">Receipt Number (Optional)</label>
                  <input
                    type="text"
                    value={receiptNumber}
                    onChange={(e) => setReceiptNumber(e.target.value)}
                    placeholder="Enter official receipt number"
                    className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D]"
                  />
                </div>
              </div>

              <div className="p-6 border-t border-gray-200 flex gap-3">
                <button 
                  onClick={() => setSelectedFine(null)}
                  className="flex-1 px-4 py-2 border-2 border-gray-300 text-gray-700 rounded-lg font-medium hover:bg-gray-50"
                >
                  Cancel
                </button>
                <button 
                  onClick={handleClearFine}
                  className="flex-1 px-4 py-2 bg-green-500 text-white rounded-lg font-medium hover:bg-green-600 flex items-center justify-center gap-2"
                >
                  <Check className="w-5 h-5" /> Clear Fine
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </AdminLayout>
  );
}