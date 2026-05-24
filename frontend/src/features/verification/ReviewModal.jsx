import React from 'react';
import { X, Check, AlertTriangle } from 'lucide-react';

export function ReviewModal({ verification, onClose, onApprove, onReject }) {
  const [reason, setReason] = React.useState('');

  if (!verification) return null;

  // Formats data payload structures safely to prevent browser URL resolution errors
  const getDisplayImage = (base64String) => {
    if (!base64String) return null;
    if (base64String.length < 100) return base64String;
    if (base64String.startsWith('data:image') || base64String.startsWith('http')) return base64String;
    return `data:image/jpeg;base64,${base64String}`;
  };

  const isPending = (status) => {
    if (!status) return false;
    const s = status.toLowerCase();
    return s === 'pending' || s === 'pending review';
  };

  return (
    <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4 backdrop-blur-sm">
      <div className="bg-white rounded-xl shadow-2xl w-full max-w-2xl overflow-hidden animate-in fade-in zoom-in-95 duration-150">
        <div className="flex items-center justify-between p-4 border-b border-gray-200">
          <h3 className="text-lg font-bold text-gray-900">Review ID Submission</h3>
          <button onClick={onClose} className="text-gray-400 hover:text-gray-600 transition-colors">
            <X className="w-5 h-5" />
          </button>
        </div>

        <div className="p-6 space-y-6 max-h-[80vh] overflow-y-auto">
          <div className="bg-gray-50 rounded-lg p-4 grid grid-cols-2 gap-4">
            <div>
              <span className="block text-xs font-medium text-gray-500 uppercase">Student Name</span>
              <span className="text-sm font-semibold text-gray-900">{verification.studentName}</span>
            </div>
            <div>
              <span className="block text-xs font-medium text-gray-500 uppercase">Student ID</span>
              <span className="text-sm font-semibold text-gray-900">{verification.studentId}</span>
            </div>
            <div className="col-span-2">
              <span className="block text-xs font-medium text-gray-500 uppercase">Email Address</span>
              <span className="text-sm font-semibold text-gray-900">{verification.email}</span>
            </div>
          </div>

          <div>
            <span className="block text-xs font-medium text-gray-500 uppercase mb-2">ID Image</span>
            <div className="border border-gray-200 rounded-lg overflow-hidden bg-gray-100 flex items-center justify-center min-h-[250px] relative shadow-inner">
              {verification.idImageUrl ? (
                <img
                  src={getDisplayImage(verification.idImageUrl)}
                  alt="Student ID Preview"
                  className="max-w-full max-h-[400px] object-contain select-none rounded"
                  onError={(e) => {
                    e.target.style.display = 'none';
                  }}
                />
              ) : (
                <div className="text-center p-4 text-gray-400">
                  <AlertTriangle className="w-8 h-8 mx-auto mb-2" />
                  <p className="text-sm">No image data available</p>
                </div>
              )}
            </div>
          </div>

          <div className="space-y-2">
            <label className="block text-xs font-medium text-gray-500 uppercase">Rejection Reason</label>
            <input
              type="text"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="Enter reason if rejecting..."
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-[#7F1D1D]"
            />
          </div>
        </div>

        <div className="flex items-center justify-end gap-3 p-4 bg-gray-50 border-t border-gray-200">
          {isPending(verification.status) && (
            <>
              <button
                onClick={() => onApprove(verification.id)}
                className="px-4 py-2 bg-emerald-600 text-white rounded-lg text-sm font-medium hover:bg-emerald-700 transition-colors"
              >
                Approve
              </button>
              <button
                onClick={() => onReject(verification.id, reason)}
                disabled={!reason.trim()}
                className="px-4 py-2 bg-red-600 text-white rounded-lg text-sm font-medium hover:bg-red-700 transition-colors disabled:opacity-50"
              >
                Reject
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}