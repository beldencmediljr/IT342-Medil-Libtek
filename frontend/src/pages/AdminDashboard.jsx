import { AdminLayout } from '../components/AdminLayout';
import { Users, BookOpen, Calendar, TrendingUp, AlertTriangle, CheckCircle, MapPin, ScanLine } from 'lucide-react';
import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

export function AdminDashboard() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [scanStudentId, setScanStudentId] = useState(''); // NEW: State for scanner simulator
  const [isScanning, setIsScanning] = useState(false);    // NEW: Loading state for scanner

  const [dashboardData, setDashboardData] = useState({
    activeReservations: 0,
    pendingVerificationsCount: 0,
    overdueItemsCount: 0,
    overdueFinesTotal: 0,
    currentOccupancy: 0,
    maxCapacity: 100,
    pendingVerifications: [],
    recentActivities: []
  });

  const fetchDashboardData = useCallback(async () => {
    try {
      setLoading(true);
      const response = await api.get('/dashboard/summary');
      setDashboardData(response.data);
    } catch (error) {
      console.error('Failed to load dashboard data:', error);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDashboardData();
  }, [fetchDashboardData]);

  // NEW: Handler for the Scanner Simulator
  const handleScan = async (e) => {
    e.preventDefault();
    if (!scanStudentId.trim()) return;

    setIsScanning(true);
    try {
      await api.post('/scanner/scan', { studentId: scanStudentId });
      setScanStudentId(''); // Clear the input after success
      fetchDashboardData(); // Refresh the dashboard to see the occupancy change instantly
    } catch (error) {
      console.error('Scan failed:', error);
      alert('Failed to process scan. Check backend connection.');
    } finally {
      setIsScanning(false);
    }
  };

  if (loading && !dashboardData.maxCapacity) {
    return (
      <AdminLayout>
        <div className="flex items-center justify-center h-full">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#7F1D1D]"></div>
        </div>
      </AdminLayout>
    );
  }

  const occupancyPercentage = dashboardData.maxCapacity > 0 
    ? ((dashboardData.currentOccupancy / dashboardData.maxCapacity) * 100).toFixed(0) 
    : 0;

  return (
    <AdminLayout>
      <div className="space-y-6">
        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-[#7F1D1D]/10 rounded-lg flex items-center justify-center">
                <Users className="w-6 h-6 text-[#7F1D1D]" />
              </div>
              <span className="flex items-center gap-1 text-xs text-gray-500">
                <span className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
                Live
              </span>
            </div>
            <h3 className="text-3xl font-bold text-gray-900">{dashboardData.currentOccupancy}/{dashboardData.maxCapacity}</h3>
            <p className="text-sm text-gray-600 mt-1">Current Occupancy</p>
            <div className="mt-3 flex items-center text-green-600 text-sm">
              <TrendingUp className="w-4 h-4 mr-1" />
              <span>{dashboardData.maxCapacity - dashboardData.currentOccupancy} spaces available</span>
            </div>
          </div>

          <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                <Calendar className="w-6 h-6 text-blue-600" />
              </div>
            </div>
            <h3 className="text-3xl font-bold text-gray-900">{dashboardData.activeReservations}</h3>
            <p className="text-sm text-gray-600 mt-1">Active Reservations</p>
            <div className="mt-3 flex items-center text-blue-600 text-sm">
              <span>View all bookings</span>
            </div>
          </div>

          <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
                <AlertTriangle className="w-6 h-6 text-yellow-600" />
              </div>
            </div>
            <h3 className="text-3xl font-bold text-gray-900">{dashboardData.pendingVerificationsCount}</h3>
            <p className="text-sm text-gray-600 mt-1">Pending Verifications</p>
            <div className="mt-3 flex items-center text-yellow-600 text-sm">
              <span>Requires review</span>
            </div>
          </div>

          <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm">
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 bg-red-100 rounded-lg flex items-center justify-center">
                <BookOpen className="w-6 h-6 text-red-600" />
              </div>
            </div>
            <h3 className="text-3xl font-bold text-gray-900">{dashboardData.overdueItemsCount}</h3>
            <p className="text-sm text-gray-600 mt-1">Overdue Items</p>
            <div className="mt-3 flex items-center text-red-600 text-sm">
              <span>PHP {dashboardData.overdueFinesTotal} in fines</span>
            </div>
          </div>
        </div>

        {/* Two Column Layout */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 space-y-6">
            
            {/* NEW: Scanner Simulator */}
            <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-6">
              <div className="flex items-center gap-2 mb-4">
                <ScanLine className="w-5 h-5 text-[#7F1D1D]" />
                <h3 className="text-lg font-bold text-gray-900">Scanner Simulator (Dev Mode)</h3>
              </div>
              <p className="text-sm text-gray-600 mb-4">
                Enter a Student ID to simulate a physical ID scan at the library entrance. Scanning an ID once checks the student <strong>IN</strong>. Scanning the same ID again checks them <strong>OUT</strong>.
              </p>
              <form onSubmit={handleScan} className="flex gap-3">
                <input
                  type="text"
                  value={scanStudentId}
                  onChange={(e) => setScanStudentId(e.target.value)}
                  placeholder="e.g., 2024-105"
                  className="flex-1 px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#7F1D1D]"
                  required
                />
                <button
                  type="submit"
                  disabled={isScanning}
                  className="px-6 py-2 bg-[#7F1D1D] text-white rounded-lg font-medium hover:bg-[#991B1B] transition-colors disabled:opacity-50"
                >
                  {isScanning ? 'Scanning...' : 'Simulate Scan'}
                </button>
              </form>
            </div>

            {/* Real-Time Occupancy Chart */}
            <div className="bg-white rounded-xl border border-gray-200 shadow-sm">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-lg font-bold text-gray-900">Real-Time Occupancy</h3>
                <p className="text-sm text-gray-600 mt-1">Live library capacity monitoring</p>
              </div>
              <div className="p-6">
                <div className="mb-6">
                  <div className="flex items-center justify-between mb-2">
                    <span className="text-sm font-medium text-gray-700">Current: {dashboardData.currentOccupancy} people</span>
                    <span className="text-sm text-gray-600">{occupancyPercentage}%</span>
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-6">
                    <div 
                      className="bg-[#7F1D1D] h-6 rounded-full transition-all duration-500 flex items-center justify-end pr-2"
                      style={{ width: `${occupancyPercentage}%` }}
                    >
                      <span className="text-white text-xs font-medium">{dashboardData.currentOccupancy}</span>
                    </div>
                  </div>
                </div>

                <div className="h-48 flex items-end justify-around gap-2">
                  {['9AM', '10AM', '11AM', '12PM', '1PM', '2PM', '3PM', 'Now'].map((hour, index) => {
                    const heights = [40, 55, 70, 85, 65, 75, 60, occupancyPercentage];
                    return (
                      <div key={hour} className="flex-1 flex flex-col items-center gap-2">
                        <div 
                          className={`w-full rounded-t-lg transition-all ${
                            index === 7 ? 'bg-[#7F1D1D] animate-pulse' : 'bg-gray-300'
                          }`}
                          style={{ height: `${heights[index]}%` }}
                        ></div>
                        <span className="text-xs font-medium text-gray-600">{hour}</span>
                      </div>
                    );
                  })}
                </div>
              </div>
            </div>
          </div>

          {/* Pending Verifications */}
          <div className="bg-white rounded-xl border border-gray-200 shadow-sm flex flex-col">
            <div className="p-6 border-b border-gray-200">
              <h3 className="text-lg font-bold text-gray-900">Pending Verifications</h3>
              <p className="text-sm text-gray-600 mt-1">Student ID approvals</p>
            </div>
            <div className="divide-y divide-gray-100 flex-1 overflow-y-auto">
              {dashboardData.pendingVerifications.length === 0 ? (
                <div className="p-6 text-center text-gray-500">No pending verifications</div>
              ) : (
                dashboardData.pendingVerifications.map((item) => (
                  <div key={item.id} className="p-4 hover:bg-gray-50 transition-colors">
                    <div className="flex items-start justify-between mb-2">
                      <div className="flex-1">
                        <p className="font-medium text-gray-900 text-sm">{item.studentName}</p>
                        <p className="text-xs text-gray-600">{item.studentId}</p>
                      </div>
                    </div>
                    <button 
                      onClick={() => navigate('/admin/verifications')}
                      className="w-full py-1.5 mt-2 bg-yellow-100 text-yellow-700 font-medium text-xs rounded hover:bg-yellow-200 transition-colors"
                    >
                      Review Document
                    </button>
                  </div>
                ))
              )}
            </div>
            <div className="p-4 border-t border-gray-200">
              <button onClick={() => navigate('/admin/verifications')} className="w-full text-sm text-[#7F1D1D] font-medium hover:underline">
                View All Verifications →
              </button>
            </div>
          </div>
        </div>

        {/* Recent Activity */}
        <div className="bg-white rounded-xl border border-gray-200 shadow-sm">
          <div className="p-6 border-b border-gray-200">
            <h3 className="text-lg font-bold text-gray-900">Recent Reservations</h3>
            <p className="text-sm text-gray-600 mt-1">Latest library bookings</p>
          </div>
          <div className="divide-y divide-gray-100">
            {dashboardData.recentActivities.length === 0 ? (
              <div className="p-6 text-center text-gray-500">No recent activity found</div>
            ) : (
              dashboardData.recentActivities.map((activity) => (
                <div key={activity.id} className="p-4 flex items-center gap-4 hover:bg-gray-50 transition-colors">
                  <div className={`w-10 h-10 rounded-lg flex items-center justify-center ${
                    activity.resourceType === 'BOOTH' ? 'bg-blue-100' : 'bg-green-100'
                  }`}>
                    {activity.resourceType === 'BOOTH' ? (
                      <MapPin className="w-5 h-5 text-blue-600" />
                    ) : (
                      <BookOpen className="w-5 h-5 text-green-600" />
                    )}
                  </div>
                  <div className="flex-1">
                    <p className="font-medium text-gray-900 text-sm">{activity.studentName}</p>
                    <p className="text-xs text-gray-600">Reserved {activity.resourceName}</p>
                  </div>
                  <span className="text-xs font-medium px-2 py-1 bg-gray-100 rounded text-gray-600">
                    {activity.status}
                  </span>
                </div>
              ))
            )}
          </div>
        </div>

        {/* Quick Actions */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <button onClick={() => navigate('/admin/resources')} className="p-6 bg-[#7F1D1D] text-white rounded-xl hover:bg-[#991B1B] transition-colors text-left shadow-sm">
            <BookOpen className="w-8 h-8 mb-3" />
            <h4 className="font-bold mb-1">Manage Resources</h4>
            <p className="text-sm text-white/80">Add or edit books and booths</p>
          </button>
          
          <button onClick={() => navigate('/admin/reservations')} className="p-6 bg-blue-600 text-white rounded-xl hover:bg-blue-700 transition-colors text-left shadow-sm">
            <Calendar className="w-8 h-8 mb-3" />
            <h4 className="font-bold mb-1">View Reservations</h4>
            <p className="text-sm text-white/80">Check current and upcoming bookings</p>
          </button>
          
          <button onClick={() => navigate('/admin/fines')} className="p-6 bg-green-600 text-white rounded-xl hover:bg-green-700 transition-colors text-left shadow-sm">
            <CheckCircle className="w-8 h-8 mb-3" />
            <h4 className="font-bold mb-1">Clear Fines</h4>
            <p className="text-sm text-white/80">Process student payments</p>
          </button>
        </div>
      </div>
    </AdminLayout>
  );
}